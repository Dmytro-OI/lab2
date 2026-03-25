package lab2.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItem {

    @NotNull(message = "ID товару обов'язковий")
    private Long productId;

    @NotNull(message = "Кількість обов'язкова")
    @Min(value = 1, message = "Кількість має бути не менше 1")
    private Integer quantity;

    public OrderItem() {
    }

    public OrderItem(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
