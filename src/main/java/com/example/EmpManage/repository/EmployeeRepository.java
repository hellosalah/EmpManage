package com.example.EmpManage.repository;

import com.example.EmpManage.model.Employee;
import com.example.EmpManage.model.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.firstName) LIKE %:keyword% OR " +
            "LOWER(e.lastName) LIKE %:keyword% OR " +
            "CAST(e.employeeId AS string) LIKE %:keyword% OR " +
            "LOWER(e.department) LIKE %:keyword% OR " +
            "LOWER(e.jobTitle) LIKE %:keyword%")
    List<Employee> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT e FROM Employee e WHERE " +
            "(:employmentStatus IS NULL OR e.employmentStatus = :employmentStatus) AND " +
            "(:department IS NULL OR :department = '' OR e.department = :department) AND " +
            "(:hireDate IS NULL OR DATE(e.hireDate) = DATE(:hireDate))")
    List<Employee> findEmployeesByFilters(
            @Param("employmentStatus") EmploymentStatus employmentStatus,
            @Param("department") String department,
            @Param("hireDate") LocalDate hireDate);
}
