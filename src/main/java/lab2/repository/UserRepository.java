package lab2.repository;

import org.springframework.stereotype.Repository;
import lab2.model.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserRepository() {
        save(new User(1L, "admin", "dmytro@lpnu.ua"));
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
}