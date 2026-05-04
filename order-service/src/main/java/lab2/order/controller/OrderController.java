package lab2.order.controller;

import jakarta.validation.Valid;
import lab2.order.model.CustomerOrder;
import lab2.order.model.OrderStatus;
import lab2.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<CustomerOrder> getAll(@RequestParam(required = false) Long userId) {
        return orderService.getAll(userId);
    }

    @GetMapping("/{id}")
    public CustomerOrder getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder create(@Valid @RequestBody CustomerOrder order) {
        return orderService.create(order);
    }

    @PatchMapping("/{id}/status")
    public CustomerOrder updateStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        return orderService.updateStatus(id, status);
    }
}
