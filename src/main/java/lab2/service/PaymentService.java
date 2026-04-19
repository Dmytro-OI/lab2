package lab2.service;

import lab2.exception.BadRequestException;
import lab2.exception.NotFoundException;
import lab2.model.CustomerOrder;
import lab2.model.OrderStatus;
import lab2.model.Payment;
import lab2.model.PaymentStatus;
import lab2.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public PaymentService(PaymentRepository paymentRepository, OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Платіж з ID " + id + " не знайдено"));
    }

    @Transactional
    public Payment create(Payment payment) {
        CustomerOrder order = orderService.getById(payment.getOrderId());

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BadRequestException("Оплатити можна лише замовлення зі статусом CREATED");
        }

        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new BadRequestException("Платіж для замовлення з ID " + order.getId() + " вже існує");
        }

        payment.setId(null);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        orderService.updateStatus(order.getId(), OrderStatus.PAID);

        return paymentRepository.save(payment);
    }
}
