package lab2.order.service;

import lab2.order.client.CatalogClient;
import lab2.order.client.UserClient;
import lab2.order.exception.BadRequestException;
import lab2.order.exception.NotFoundException;
import lab2.order.model.CustomerOrder;
import lab2.order.model.OrderItem;
import lab2.order.model.OrderStatus;
import lab2.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private static final String PROMO_CODE = "Hrynchyshyn";
    private static final double PROMO_DISCOUNT_RATE = 0.10;

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final UserClient userClient;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient, UserClient userClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
        this.userClient = userClient;
    }

    public List<CustomerOrder> getAll(Long userId) {
        if (userId != null) {
            return orderRepository.findByUserId(userId);
        }
        return orderRepository.findAll();
    }

    public CustomerOrder getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Замовлення з ID " + id + " не знайдено"));
    }

    @Transactional
    public CustomerOrder create(CustomerOrder order) {
        if (!userClient.userExists(order.getUserId())) {
            throw new BadRequestException("Користувача з ID " + order.getUserId() + " не існує");
        }

        Map<Long, Map<String, Object>> productsById = new HashMap<>();
        Map<Long, Integer> requestedQuantityByProduct = new HashMap<>();

        for (OrderItem item : order.getItems()) {
            Map<String, Object> product = catalogClient.getProduct(item.getProductId());
            productsById.put(item.getProductId(), product);
            requestedQuantityByProduct.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : requestedQuantityByProduct.entrySet()) {
            Map<String, Object> product = productsById.get(entry.getKey());
            int stock = ((Number) product.get("stockQuantity")).intValue();
            if (stock < entry.getValue()) {
                throw new BadRequestException("Недостатньо товару на складі. productId=" + entry.getKey());
            }
        }

        double total = 0.0;
        for (Map.Entry<Long, Integer> entry : requestedQuantityByProduct.entrySet()) {
            Map<String, Object> product = productsById.get(entry.getKey());
            double price = ((Number) product.get("price")).doubleValue();
            int qty = entry.getValue();
            total += price * qty;
            catalogClient.deductStock(entry.getKey(), qty);
        }

        if (PROMO_CODE.equalsIgnoreCase(order.getPromoCode())) {
            total = total * (1 - PROMO_DISCOUNT_RATE);
        }

        order.setId(null);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        for (OrderItem item : order.getItems()) {
            item.setOrder(order);
            Map<String, Object> product = productsById.get(item.getProductId());
            item.setPrice(((Number) product.get("price")).doubleValue());
        }

        return orderRepository.save(order);
    }

    @Transactional
    public CustomerOrder updateStatus(Long id, OrderStatus newStatus) {
        CustomerOrder order = getById(id);
        OrderStatus current = order.getStatus();

        if (current == OrderStatus.CANCELLED || current == OrderStatus.SHIPPED) {
            throw new BadRequestException("Статус змінювати не можна для завершеного замовлення");
        }
        if (current == OrderStatus.CREATED && (newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED)) {
            order.setStatus(newStatus);
            return orderRepository.save(order);
        }
        if (current == OrderStatus.PAID && (newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED)) {
            order.setStatus(newStatus);
            return orderRepository.save(order);
        }
        throw new BadRequestException("Некоректний перехід статусу: " + current + " -> " + newStatus);
    }
}
