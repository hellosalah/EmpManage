package com.example.EmpManage.service;

import com.example.EmpManage.repository.EmployeeRepository;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
}
