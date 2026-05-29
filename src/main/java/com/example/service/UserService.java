package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.User;
import com.example.repository.UserRepository;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean isAdmin(Long id) {
        return userRepository.findById(id).get().getRole().equals("ROLE_ADMIN");
    }

    public void create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepository.save(user);
    }

    public User updateProfile(String currentEmail, User newData) {
        User currentUser = userRepository.findByEmail(currentEmail).get();
        currentUser.setName(newData.getName());
        currentUser.setEmail(newData.getEmail());
        currentUser.setPostalCode(newData.getPostalCode());
        currentUser.setAddress(newData.getAddress());
        currentUser.setUpdatedAt(LocalDateTime.now());

        String newPassword = newData.getPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(currentUser);
    }
}