package com.ventimetriquadriconsulting.restaurant.employee.repository;

import com.ventimetriquadriconsulting.restaurant.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT employee FROM Employee employee WHERE employee.restaurant.branchCode = ?1 ORDER BY employee.lastName ASC")
    Optional<List<Employee>> findByBranchCode(String branchCode);

    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByEmployeeId(Long employeeId);
}