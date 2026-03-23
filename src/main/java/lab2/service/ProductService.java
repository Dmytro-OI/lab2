package lab2.service;

import org.springframework.stereotype.Service;
import lab2.model.Product;
import lab2.repository.ProductRepository;
import lab2.repository.CategoryRepository;
import lab2.repository.UserRepository;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар з ID " + id + " не знайдено"));
    }

    public Product create(Product product) {
        validateRelations(product);
        applyDiscount(product);
        return productRepository.save(product);
    }

    public Product update(Long id, Product updatedProduct) {
        Product existing = getById(id);

        validateRelations(updatedProduct);

        existing.setName(updatedProduct.getName());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        existing.setCategoryId(updatedProduct.getCategoryId());
        existing.setOwnerId(updatedProduct.getOwnerId());

        return productRepository.save(existing);
    }

    public void delete(Long id) {
        if (productRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Неможливо видалити: товар з ID " + id + " не знайдено");
        }
        productRepository.deleteById(id);
    }

    private void validateRelations(Product product) {
        if (product.getCategoryId() != null && !categoryRepository.existsById(product.getCategoryId())) {
            throw new RuntimeException("Категорії з ID " + product.getCategoryId() + " не існує");
        }
        if (product.getOwnerId() != null && !userRepository.existsById(product.getOwnerId())) {
            throw new RuntimeException("Користувача (власника) з ID " + product.getOwnerId() + " не існує");
        }
    }

    private void applyDiscount(Product product) {
        if (product.getPrice() != null && product.getPrice() > 5000) {
            product.setPrice(product.getPrice() * 0.9);
        }
    }
}