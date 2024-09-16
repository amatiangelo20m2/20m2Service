package com.ventimetriquadriconsulting.restaurant.employee.service;

import com.ventimetriquadriconsulting.restaurant.employee.entity.Employee;
import com.ventimetriquadriconsulting.restaurant.employee.entity.dto.EmployeeDTO;
import com.ventimetriquadriconsulting.restaurant.employee.repository.EmployeeRepository;
import com.ventimetriquadriconsulting.restaurant.exception.customexception.EmailAlreadyInUserException;
import com.ventimetriquadriconsulting.restaurant.restaurant.entity.dto.RestaurantDTO;
import com.ventimetriquadriconsulting.restaurant.restaurant.service.RestaurantService;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final RestaurantService restaurantService;

    public EmployeeDTO saveEmployee(String branchCode, EmployeeDTO employeeDTO) {

        Optional<Employee> byEmail = employeeRepository.findByEmail(employeeDTO.getEmail());
        if(byEmail.isPresent()){
            log.error("Email already in use {}", employeeDTO.getEmail());

            throw new EmailAlreadyInUserException("Email '" + employeeDTO.getEmail() + "' already in use");

        }else{
            RestaurantDTO restaurantDTO = restaurantService
                    .retrieveRestaurantConfiguration(branchCode);

            log.info("Create following employee {}" , employeeDTO);
            Employee employee = employeeDTO.toEntity();
            employee.setRestaurant(RestaurantDTO.toEntity(restaurantDTO));
            Employee savedEmployee = employeeRepository.save(employee);
            log.info("Saved data {}" , savedEmployee);

            return EmployeeDTO.fromEntity(savedEmployee);
        }

    }

    @Modifying
    @Transactional
    public EmployeeDTO updateEmployee(String branchCode,
                                      EmployeeDTO employeeDto) {

        log.info("Update employee with email {} for branch with code {}. New configuration: {}", employeeDto.getEmail(), branchCode, employeeDto);
        Employee existingEmployee = employeeRepository.findByEmployeeId(employeeDto.getEmployeeId()).orElseThrow(()
                -> new NotFoundException("Employee not found with id " + employeeDto.getEmployeeId()));

        existingEmployee.setFirstName(employeeDto.getFirstName());
        existingEmployee.setLastName(employeeDto.getLastName());
        existingEmployee.setEmail(employeeDto.getEmail());
        existingEmployee.setPhone(employeeDto.getPhone());
        existingEmployee.setGender(employeeDto.getGender());

        existingEmployee.setJobDescription(employeeDto.getJobDescription());
        existingEmployee.setContractType(employeeDto.getContractType());
        existingEmployee.setRemuneration(employeeDto.getRemuneration());
        existingEmployee.setDob(employeeDto.getDob());
        existingEmployee.setHireDate(employeeDto.getHireDate());

        existingEmployee.setStartDateInduction(employeeDto.getStartDateInduction());
        existingEmployee.setEndDate(employeeDto.getEndDate());
        existingEmployee.setCostReference(employeeDto.getCostReference());

        Employee savedEmployee = employeeRepository.save(existingEmployee);


        return EmployeeDTO.fromEntity(savedEmployee);
    }

    // Retrieve an employee by ID and return the corresponding DTO
    public Optional<EmployeeDTO> getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeDTO::fromEntity);
    }

    // Retrieve all employees and return them as a list of DTOs
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeeByBranchCode(String branchCode) {

        log.info("Retrieve employee by branchCode {}", branchCode );

        Optional<List<Employee>> employees = employeeRepository.findByBranchCode(branchCode);
        if(employees.isPresent()){
            return EmployeeDTO.fromEntityList(employees.get());
        }else{
            return Collections.emptyList();
        }
    }

    @Transactional
    @Modifying
    public void hideEmployee(Long employeeId) {
        log.info("Hide employee with id {}", employeeId );

        Optional<Employee> byEmployeeId = employeeRepository.findByEmployeeId(employeeId);
        byEmployeeId.ifPresent(employee -> employee.setVisible(!employee.isVisible()));
    }
}