package lab2.repository;

import lab2.model.CustomerOrder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {
    private final Map<Long, CustomerOrder> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<CustomerOrder> findAll() {
        return new ArrayList<>(orders.values());
    }

    public Optional<CustomerOrder> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public CustomerOrder save(CustomerOrder order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }
}
