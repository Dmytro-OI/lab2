package lab2.payment.service;

import lab2.payment.client.OrderClient;
import lab2.payment.exception.BadRequestException;
import lab2.payment.exception.NotFoundException;
import lab2.payment.model.Payment;
import lab2.payment.model.PaymentStatus;
import lab2.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    public PaymentService(PaymentRepository paymentRepository, OrderClient orderClient) {
        this.paymentRepository = paymentRepository;
        this.orderClient = orderClient;
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
        Map<String, Object> order = orderClient.getOrder(payment.getOrderId());

        String status = (String) order.get("status");
        if (!"CREATED".equals(status)) {
            throw new BadRequestException("Оплатити можна лише замовлення зі статусом CREATED");
        }

        if (paymentRepository.findByOrderId(payment.getOrderId()).isPresent()) {
            throw new BadRequestException("Платіж для замовлення з ID " + payment.getOrderId() + " вже існує");
        }

        Double totalAmount = ((Number) order.get("totalAmount")).doubleValue();

        payment.setId(null);
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        orderClient.updateOrderStatus(payment.getOrderId(), "PAID");

        return paymentRepository.save(payment);
    }
}
