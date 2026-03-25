package lab2.service;

import lab2.exception.BadRequestException;
import lab2.exception.NotFoundException;
import lab2.model.CustomerOrder;
import lab2.model.OrderItem;
import lab2.model.OrderStatus;
import lab2.model.Product;
import lab2.repository.OrderRepository;
import lab2.repository.ProductRepository;
import lab2.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<CustomerOrder> getAll() {
        return orderRepository.findAll();
    }

    public CustomerOrder getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Замовлення з ID " + id + " не знайдено"));
    }

    public CustomerOrder create(CustomerOrder order) {
        if (!userRepository.existsById(order.getUserId())) {
            throw new BadRequestException("Користувача з ID " + order.getUserId() + " не існує");
        }

        Map<Long, Product> productsById = new HashMap<>();
        Map<Long, Integer> requestedQuantityByProduct = new HashMap<>();

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BadRequestException("Товар з ID " + item.getProductId() + " не існує"));
            productsById.put(product.getId(), product);
            requestedQuantityByProduct.merge(product.getId(), item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> requestEntry : requestedQuantityByProduct.entrySet()) {
            Product product = productsById.get(requestEntry.getKey());
            Integer requestedQuantity = requestEntry.getValue();
            if (product.getStockQuantity() < requestedQuantity) {
                throw new BadRequestException(
                        "Недостатньо товару на складі. productId=" + product.getId()
                );
            }
        }

        double total = 0.0;
        for (Map.Entry<Long, Integer> requestEntry : requestedQuantityByProduct.entrySet()) {
            Product product = productsById.get(requestEntry.getKey());
            Integer requestedQuantity = requestEntry.getValue();
            total += product.getPrice() * requestedQuantity;
            product.setStockQuantity(product.getStockQuantity() - requestedQuantity);
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

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
