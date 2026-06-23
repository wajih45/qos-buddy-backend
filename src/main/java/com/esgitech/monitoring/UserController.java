package com.esgitech.monitoring;

import com.esgitech.monitoring.entity.User;
import com.esgitech.monitoring.repository.UserRepository;
import com.esgitech.monitoring.service.EmailService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final EmailService emailService;

    private final Map<String, String> resetCodes = new HashMap<>();

    public UserController(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        return userRepository.save(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User loginRequest) {
        User user = userRepository.findByEmailAndPassword(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Email not found");
        }

        String code = String.valueOf(100000 + new Random().nextInt(900000));

        resetCodes.put(email, code);

        System.out.println("RESET CODE = " + code);

        return code;
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String newPassword
    ) {
        String savedCode = resetCodes.get(email);

        if (savedCode == null || !savedCode.equals(code)) {
            throw new RuntimeException("Invalid reset code");
        }

        User user = userRepository.findByEmail(email);
        user.setPassword(newPassword);

        userRepository.save(user);
        resetCodes.remove(email);

        return "Password updated successfully";
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}