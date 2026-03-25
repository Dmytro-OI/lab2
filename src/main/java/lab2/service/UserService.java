package lab2.service;

import lab2.exception.NotFoundException;
import lab2.model.User;
import lab2.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Користувача з ID " + id + " не знайдено"));
    }

    public User create(User user) {
        return userRepository.save(user);
    }
}
