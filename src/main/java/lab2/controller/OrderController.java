package lab2.controller;

import jakarta.validation.Valid;
import lab2.model.CustomerOrder;
import lab2.model.OrderStatus;
import lab2.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<CustomerOrder> getAll() {
        return orderService.getAll();
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
