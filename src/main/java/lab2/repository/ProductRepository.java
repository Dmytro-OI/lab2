package lab2.repository;

import org.springframework.stereotype.Repository;
import lab2.model.Product;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepository {
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Product> findAll() { return new ArrayList<>(products.values()); }
    public Optional<Product> findById(Long id) { return Optional.ofNullable(products.get(id)); }
    public Product save(Product product) {
        if (product.getId() == null) product.setId(idGenerator.getAndIncrement());
        products.put(product.getId(), product);
        return product;
    }

    public void deleteById(Long id) {
        products.remove(id);
    }
}