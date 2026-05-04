package lab2.order.controller;

import jakarta.validation.Valid;
import lab2.order.model.Cart;
import lab2.order.model.CartItem;
import lab2.order.model.CheckoutRequest;
import lab2.order.model.CustomerOrder;
import lab2.order.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getOrCreateCart(userId);
    }

    @PostMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Cart addItem(@PathVariable Long userId, @Valid @RequestBody CartItem item) {
        return cartService.addItem(userId, item);
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public Cart removeItem(@PathVariable Long userId, @PathVariable Long itemId) {
        return cartService.removeItem(userId, itemId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
    }

    @PostMapping("/{userId}/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder checkout(@PathVariable Long userId,
                                  @RequestBody(required = false) CheckoutRequest request) {
        String promoCode = request == null ? null : request.getPromoCode();
        return cartService.checkout(userId, promoCode);
    }
}
