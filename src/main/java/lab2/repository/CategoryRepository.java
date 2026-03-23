package lab2.repository;

import org.springframework.stereotype.Repository;
import lab2.model.Category;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CategoryRepository {
    private final Map<Long, Category> categories = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public CategoryRepository() {
        save(new Category(null, "Електроніка"));
    }

    public List<Category> findAll() { return new ArrayList<>(categories.values()); }
    public Optional<Category> findById(Long id) { return Optional.ofNullable(categories.get(id)); }
    public boolean existsById(Long id) { return categories.containsKey(id); }
    public Category save(Category category) {
        if (category.getId() == null) category.setId(idGenerator.getAndIncrement());
        categories.put(category.getId(), category);
        return category;
    }
}