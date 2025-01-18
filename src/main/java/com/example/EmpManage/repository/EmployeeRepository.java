package com.example.EmpManage.repository;

import com.example.EmpManage.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.firstName) LIKE %:keyword% OR " +
            "LOWER(e.lastName) LIKE %:keyword% OR " +
            "CAST(e.employeeId AS string) LIKE %:keyword% OR " +
            "LOWER(e.department) LIKE %:keyword% OR " +
            "LOWER(e.jobTitle) LIKE %:keyword%")
    List<Employee> findByKeyword(@Param("keyword") String keyword);
}
