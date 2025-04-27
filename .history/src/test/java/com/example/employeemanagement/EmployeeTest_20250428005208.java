package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTest {
    private Employee<Integer> employee;

    @BeforeEach
    void setUp() throws InvalidSalaryException, InvalidDepartmentException {
        employee = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
    }

    @Test
    void testValidEmployeeCreation() {
        assertNotNull(employee);
        assertEquals("John Doe", employee.getName());
        assertEquals("IT", employee.getDepartment());
        assertEquals(50000.0, employee.getSalary());
    }

    @Test
    void testInvalidSalary() {
        assertThrows(InvalidSalaryException.class, () -> {
            new Employee<>(2, "Jane Doe", "HR", -1000.0, 4.0, 3);
        });
    }

    @Test
    void testInvalidDepartment() {
        assertThrows(InvalidDepartmentException.class, () -> {
            new Employee<>(3, "Bob Smith", "Invalid Dept", 60000.0, 4.2, 7);
        });
    }

    @Test
    void testEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Employee<>(4, "", "IT", 55000.0, 4.0, 4);
        });
    }

    @Test
    void testNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Employee<>(5, null, "IT", 55000.0, 4.0, 4);
        });
    }

    @Test
    void testUpdateSalary() {
        assertThrows(InvalidSalaryException.class, () -> {
            employee.setSalary(-5000.0);
        });
    }

    @Test
    void testUpdateDepartment() {
        assertThrows(InvalidDepartmentException.class, () -> {
            employee.setDepartment("Invalid");
        });
    }

    @Test
    void testValidDepartmentUpdate() throws InvalidDepartmentException {
        employee.setDepartment("HR");
        assertEquals("HR", employee.getDepartment());
    }

    @Test
    void testValidSalaryUpdate() throws InvalidSalaryException {
        employee.setSalary(60000.0);
        assertEquals(60000.0, employee.getSalary());
    }

    @Test
    void testNegativeYearsOfExperience() {
        assertThrows(IllegalArgumentException.class, () -> {
            employee.setYearsOfExperience(-1);
        });
    }
} 