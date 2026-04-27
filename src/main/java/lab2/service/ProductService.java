package lab2.service;

import lab2.exception.BadRequestException;
import lab2.exception.NotFoundException;
import lab2.model.Product;
import lab2.repository.CategoryRepository;
import lab2.repository.ProductRepository;
import lab2.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<Product> getAll(Long categoryId, String name, Pageable pageable) {
        if (categoryId != null && name != null) {
            return productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, name, pageable);
        }
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId, pageable);
        }
        if (name != null) {
            return productRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар з ID " + id + " не знайдено"));
    }

    @Transactional
    public Product create(Product product) {
        validateRelations(product);
        product.setId(null);
        return productRepository.save(product);
    }

    @Transactional
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

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Товар з ID " + id + " не знайдено");
        }
        productRepository.deleteById(id);
    }

    private void validateRelations(Product product) {
        if (!categoryRepository.existsById(product.getCategoryId())) {
            throw new BadRequestException("Категорії з ID " + product.getCategoryId() + " не існує");
        }
        if (!userRepository.existsById(product.getOwnerId())) {
            throw new BadRequestException("Користувача (власника) з ID " + product.getOwnerId() + " не існує");
        }
    }

}
