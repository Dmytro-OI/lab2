package lab2.service;

import lab2.exception.NotFoundException;
import lab2.model.Category;
import lab2.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категорію з ID " + id + " не знайдено"));
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }
}
