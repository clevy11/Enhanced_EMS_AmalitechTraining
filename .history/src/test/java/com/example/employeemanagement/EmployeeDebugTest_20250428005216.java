package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class demonstrates debugging by introducing an intentional bug
 * in the salary calculation logic and then fixing it.
 */
public class EmployeeDebugTest {
    private EmployeeDatabase<Integer> database;

    @BeforeEach
    void setUp() throws InvalidSalaryException, InvalidDepartmentException {
        database = new EmployeeDatabase<>();
        // Add some test employees
        database.addEmployee(new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5));
        database.addEmployee(new Employee<>(2, "Jane Smith", "HR", 60000.0, 4.0, 3));
        database.addEmployee(new Employee<>(3, "Bob Wilson", "IT", 55000.0, 4.2, 4));
    }

    /**
     * This test demonstrates a bug in salary calculation where the raise percentage
     * is incorrectly applied, leading to wrong final salaries.
     */
    @Test
    void debugSalaryCalculationBug() throws InvalidSalaryException, EmployeeNotFoundException {
        // Bug: Salary raise calculation is incorrect
        // Current implementation: salary * (1 + percentage/100)
        // Bug demonstration: For a 10% raise, we expect 55000.0 but might get a different value
        
        // Give a 10% raise to employees with rating >= 4.0
        database.giveSalaryRaise(10.0, 4.0);
        
        // Get the updated salary for employee 1
        Employee<Integer> employee = database.getEmployee(1);
        double expectedSalary = 55000.0; // 50000 * 1.10
        double actualSalary = employee.getSalary();
        
        // This assertion helps in debugging by showing the exact values
        assertEquals(expectedSalary, actualSalary,
                String.format("Expected salary: %.2f, but got: %.2f", expectedSalary, actualSalary));
    }

    /**
     * This test demonstrates a bug in the department search functionality
     * where case sensitivity might cause incorrect results.
     */
    @Test
    void debugDepartmentSearchBug() throws InvalidDepartmentException {
        // Bug: Department search might be case sensitive
        // Test with different case variations
        int countIT = database.getEmployeesByDepartment("IT").size();
        int countIt = database.getEmployeesByDepartment("it").size();
        int countit = database.getEmployeesByDepartment("it").size();
        
        // All these should return the same count (2 IT employees)
        assertEquals(countIT, countIt, "Department search is case sensitive!");
        assertEquals(countIT, countit, "Department search is case sensitive!");
        assertEquals(2, countIT, "Incorrect number of IT employees found");
    }

    /**
     * This test demonstrates how to debug performance rating calculations
     * by logging intermediate values.
     */
    @Test
    void debugPerformanceRatingCalculation() throws EmployeeNotFoundException, 
            InvalidSalaryException, InvalidDepartmentException {
        Employee<Integer> employee = database.getEmployee(1);
        
        // Update performance rating and verify
        double newRating = 4.8;
        database.updateEmployeeDetails(1, "performancerating", newRating);
        
        assertEquals(newRating, employee.getPerformanceRating(),
                String.format("Performance rating update failed. Expected: %.1f, Got: %.1f",
                        newRating, employee.getPerformanceRating()));
    }

    /**
     * This test demonstrates debugging employee search functionality
     * with various edge cases.
     */
    @Test
    void debugEmployeeSearch() {
        // Test searching with partial names
        assertEquals(1, database.searchEmployeesByName("John").size(), "Failed to find 'John'");
        assertEquals(0, database.searchEmployeesByName("Johnny").size(), "Found non-existent 'Johnny'");
        
        // Test searching with case variations
        assertEquals(1, database.searchEmployeesByName("john").size(), "Case-sensitive search issue");
        assertEquals(1, database.searchEmployeesByName("JOHN").size(), "Case-sensitive search issue");
        
        // Test searching with spaces
        assertEquals(1, database.searchEmployeesByName("John Doe").size(), "Failed to find full name");
        assertEquals(1, database.searchEmployeesByName("Doe").size(), "Failed to find last name");
    }
} 