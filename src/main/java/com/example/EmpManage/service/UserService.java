package com.example.EmpManage.service;

import com.example.EmpManage.config.UserSession;
import com.example.EmpManage.model.Role;
import com.example.EmpManage.model.User;
import com.example.EmpManage.repository.UserRepository;
import com.example.EmpManage.model.Employee;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Role authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (user.getPassword().equals(password)) {
            UserSession.getInstance().setCurrentUser(user);
            return user.getRole();
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
