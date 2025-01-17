package com.example.EmpManage.controller;

import com.example.EmpManage.model.Role;
import com.example.EmpManage.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Role authenticate(@RequestParam String username, @RequestParam String password) {
        return userService.authenticate(username, password);
    }
}
