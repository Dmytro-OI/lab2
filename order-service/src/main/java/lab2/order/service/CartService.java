package lab2.order.service;

import lab2.order.client.CatalogClient;
import lab2.order.client.UserClient;
import lab2.order.exception.BadRequestException;
import lab2.order.exception.NotFoundException;
import lab2.order.model.*;
import lab2.order.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CatalogClient catalogClient;
    private final UserClient userClient;
    private final OrderService orderService;

    public CartService(CartRepository cartRepository, CatalogClient catalogClient,
                       UserClient userClient, OrderService orderService) {
        this.cartRepository = cartRepository;
        this.catalogClient = catalogClient;
        this.userClient = userClient;
        this.orderService = orderService;
    }

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        if (!userClient.userExists(userId)) {
            throw new NotFoundException("Користувача з ID " + userId + " не знайдено");
        }
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUserId(userId);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public Cart addItem(Long userId, CartItem newItem) {
        Cart cart = getOrCreateCart(userId);

        Map<String, Object> product = catalogClient.getProduct(newItem.getProductId());
        int stock = ((Number) product.get("stockQuantity")).intValue();

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(newItem.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            int newQty = existing.get().getQuantity() + newItem.getQuantity();
            if (stock < newQty) {
                throw new BadRequestException("Недостатньо товару на складі");
            }
            existing.get().setQuantity(newQty);
        } else {
            if (stock < newItem.getQuantity()) {
                throw new BadRequestException("Недостатньо товару на складі");
            }
            newItem.setId(null);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new NotFoundException("Позиції з ID " + itemId + " не знайдено в кошику");
        }
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        return cartRepository.save(cart);
    }

    @Transactional
    public CustomerOrder checkout(Long userId) {
        return checkout(userId, null);
    }

    @Transactional
    public CustomerOrder checkout(Long userId, String promoCode) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Кошик порожній");
        }

        CustomerOrder order = new CustomerOrder();
        order.setUserId(userId);
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProductId());
            oi.setQuantity(ci.getQuantity());
            orderItems.add(oi);
        }
        order.setItems(orderItems);
        order.setPromoCode(promoCode);

        CustomerOrder saved = orderService.create(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return saved;
    }
}
