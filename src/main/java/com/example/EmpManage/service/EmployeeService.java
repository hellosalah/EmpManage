package com.example.EmpManage.service;

import com.example.EmpManage.model.Employee;
import com.example.EmpManage.repository.EmployeeRepository;

import java.util.List;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setFirstName(updatedEmployee.getFirstName());
                    employee.setLastName(updatedEmployee.getLastName());
                    employee.setJobTitle(updatedEmployee.getJobTitle());
                    employee.setDepartment(updatedEmployee.getDepartment());
                    employee.setHireDate(updatedEmployee.getHireDate());
                    employee.setEmploymentStatus(updatedEmployee.getEmploymentStatus());
                    employee.setContactInformation(updatedEmployee.getContactInformation());
                    employee.setAddress(updatedEmployee.getAddress());
                    employee.setRole(updatedEmployee.getRole());
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
