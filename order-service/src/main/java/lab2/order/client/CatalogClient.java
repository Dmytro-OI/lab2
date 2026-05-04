package lab2.order.client;

import lab2.order.exception.BadRequestException;
import lab2.order.exception.NotFoundException;
import lab2.order.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CatalogClient {

    private final RestTemplate restTemplate;
    private final String catalogUrl;

    public CatalogClient(RestTemplate restTemplate,
                         @Value("${catalog-service.url}") String catalogUrl) {
        this.restTemplate = restTemplate;
        this.catalogUrl = catalogUrl;
    }

    public boolean userExists(Long userId) {
        try {
            restTemplate.getForEntity(catalogUrl + "/api/users/" + userId, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Сервіс каталогу недоступний");
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getProduct(Long productId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    catalogUrl + "/api/products/" + productId, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Товар з ID " + productId + " не знайдено в каталозі");
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Сервіс каталогу недоступний");
        }
    }

    public void deductStock(Long productId, int quantity) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Integer>> request = new HttpEntity<>(Map.of("quantity", quantity), headers);
            restTemplate.exchange(
                    catalogUrl + "/api/products/" + productId + "/stock",
                    HttpMethod.PATCH, request, Object.class);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BadRequestException("Недостатньо товару на складі. productId=" + productId);
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Сервіс каталогу недоступний");
        }
    }
}
