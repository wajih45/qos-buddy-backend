package com.esgitech.monitoring;

import com.esgitech.monitoring.entity.User;
import com.esgitech.monitoring.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // READ
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}