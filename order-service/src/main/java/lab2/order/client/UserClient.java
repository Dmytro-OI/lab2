package lab2.order.client;

import lab2.order.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userUrl;

    public UserClient(RestTemplate restTemplate,
                      @Value("${user-service.url}") String userUrl) {
        this.restTemplate = restTemplate;
        this.userUrl = userUrl;
    }

    public boolean userExists(Long userId) {
        try {
            restTemplate.getForEntity(userUrl + "/api/users/" + userId, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Сервіс користувачів недоступний");
        }
    }
}
