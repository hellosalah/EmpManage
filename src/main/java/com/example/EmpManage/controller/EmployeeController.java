package com.example.EmpManage.controller;

import com.example.EmpManage.config.UserSession;
import com.example.EmpManage.model.Employee;
import com.example.EmpManage.model.Role;
import com.example.EmpManage.model.User;
import com.example.EmpManage.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        if (!hasPermission(Role.HR_PERSONNEL, Role.ADMINISTRATOR)) {
            throw new RuntimeException("Permission denied: Cannot create employee.");
        }
        return employeeService.createEmployee(employee);
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        Role userRole = getUserRole();
        String userDepartment = getUserDepartment(); // Assuming a method to get current user's department

        if (userRole == Role.MANAGER) {
            return employeeService.getAllEmployees().stream()
                    .filter(emp -> emp.getDepartment().equals(userDepartment))
                    .collect(Collectors.toList());
        } else if (userRole == Role.HR_PERSONNEL || userRole == Role.ADMINISTRATOR) {
            return employeeService.getAllEmployees();
        } else {
            throw new RuntimeException("Permission denied: Cannot view employees.");
        }
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
        Role userRole = getUserRole();
        String userDepartment = getUserDepartment();

        Employee existingEmployee = employeeService.getEmployeeById(id);
        if (userRole == Role.MANAGER && !existingEmployee.getDepartment().equals(userDepartment)) {
            throw new RuntimeException("Permission denied: Cannot update employees outside your department.");
        } else if (userRole == Role.HR_PERSONNEL || userRole == Role.ADMINISTRATOR || (userRole == Role.MANAGER && existingEmployee.getDepartment().equals(userDepartment))) {
            return employeeService.updateEmployee(id, updatedEmployee);
        } else {
            throw new RuntimeException("Permission denied: Cannot update employees.");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        if (!hasPermission(Role.HR_PERSONNEL, Role.ADMINISTRATOR)) {
            throw new RuntimeException("Permission denied: Cannot delete employee.");
        }
        employeeService.deleteEmployee(id);
    }

    private boolean hasPermission(Role... roles) {
        Role userRole = getUserRole();
        for (Role role : roles) {
            if (role == userRole) {
                return true;
            }
        }
        return false;
    }

    private Role getUserRole() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        return currentUser.getRole();
    }

    private String getUserDepartment() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        return currentUser.getEmployee().getDepartment();
    }
}
