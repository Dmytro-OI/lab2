package lab2.model;

import jakarta.validation.constraints.*;

public class Product {
    private Long id;
    @NotBlank(message = "Назва товару обов'язкова")
    private String name;
    @Min(0)
    private Double price;
    @Min(0)
    private Integer stockQuantity;
    private Long categoryId;

    public Product() {}
    public Product(Long id, String name, Double price, Integer stockQuantity, Long categoryId) {
        this.id = id; this.name = name; this.price = price;
        this.stockQuantity = stockQuantity; this.categoryId = categoryId;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    private Long ownerId;

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}