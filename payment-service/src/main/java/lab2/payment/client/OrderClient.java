package lab2.payment.client;

import lab2.payment.exception.BadRequestException;
import lab2.payment.exception.NotFoundException;
import lab2.payment.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class OrderClient {

    private final RestTemplate restTemplate;
    private final String orderUrl;

    public OrderClient(RestTemplate restTemplate,
                       @Value("${order-service.url}") String orderUrl) {
        this.restTemplate = restTemplate;
        this.orderUrl = orderUrl;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getOrder(Long orderId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    orderUrl + "/api/orders/" + orderId, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Замовлення з ID " + orderId + " не знайдено");
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Сервіс замовлень недоступний");
        }
    }

    public void updateOrderStatus(Long orderId, String status) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>("\"" + status + "\"", headers);
            restTemplate.exchange(
                    orderUrl + "/api/orders/" + orderId + "/status",
                    org.springframework.http.HttpMethod.PATCH, request, Object.class);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BadRequestException("Некоректний статус або перехід для замовлення з ID " + orderId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Не вдалося оновити статус замовлення з ID " + orderId);
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Сервіс замовлень недоступний");
        }
    }
}
