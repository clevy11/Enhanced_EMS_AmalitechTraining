package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeDatabaseTest {
    private EmployeeDatabase<Integer> database;
    private Employee<Integer> testEmployee;

    @BeforeEach
    void setUp() throws InvalidSalaryException, InvalidDepartmentException {
        database = new EmployeeDatabase<>();
        testEmployee = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
        database.addEmployee(testEmployee);
    }

    @Test
    void testAddEmployee() throws InvalidSalaryException, InvalidDepartmentException, EmployeeNotFoundException {
        Employee<Integer> newEmployee = new Employee<>(2, "Jane Smith", "HR", 60000.0, 4.0, 3);
        database.addEmployee(newEmployee);
        assertEquals(newEmployee, database.getEmployee(2));
    }

    @Test
    void testAddDuplicateEmployee() throws InvalidSalaryException, InvalidDepartmentException {
        assertThrows(IllegalArgumentException.class, () -> {
            Employee<Integer> duplicate = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
            database.addEmployee(duplicate);
        });
    }

    @Test
    void testGetNonExistentEmployee() {
        assertThrows(EmployeeNotFoundException.class, () -> {
            database.getEmployee(999);
        });
    }

    @Test
    void testRemoveEmployee() throws EmployeeNotFoundException {
        database.removeEmployee(1);
        assertThrows(EmployeeNotFoundException.class, () -> {
            database.getEmployee(1);
        });
    }

    @Test
    void testRemoveNonExistentEmployee() {
        assertThrows(EmployeeNotFoundException.class, () -> {
            database.removeEmployee(999);
        });
    }

    @Test
    void testUpdateEmployeeDetails() throws EmployeeNotFoundException, InvalidSalaryException, InvalidDepartmentException {
        database.updateEmployeeDetails(1, "salary", 55000.0);
        assertEquals(55000.0, database.getEmployee(1).getSalary());
    }

    @Test
    void testUpdateWithInvalidSalary() {
        assertThrows(InvalidSalaryException.class, () -> {
            database.updateEmployeeDetails(1, "salary", -1000.0);
        });
    }

    @Test
    void testUpdateWithInvalidDepartment() {
        assertThrows(InvalidDepartmentException.class, () -> {
            database.updateEmployeeDetails(1, "department", "Invalid Department");
        });
    }

    @Test
    void testGetEmployeesByDepartment() throws InvalidDepartmentException {
        assertEquals(1, database.getEmployeesByDepartment("IT").size());
        assertEquals(0, database.getEmployeesByDepartment("HR").size());
    }

    @Test
    void testGetEmployeesByInvalidDepartment() {
        assertThrows(InvalidDepartmentException.class, () -> {
            database.getEmployeesByDepartment("");
        });
    }

    @Test
    void testSearchEmployeesByName() {
        assertEquals(1, database.searchEmployeesByName("John").size());
        assertEquals(0, database.searchEmployeesByName("Jane").size());
    }

    @Test
    void testSearchEmployeesWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            database.searchEmployeesByName("");
        });
    }

    @Test
    void testGetEmployeesInSalaryRange() throws InvalidSalaryException {
        assertEquals(1, database.getEmployeesInSalaryRange(40000.0, 60000.0).size());
        assertEquals(0, database.getEmployeesInSalaryRange(60001.0, 70000.0).size());
    }

    @Test
    void testGetEmployeesWithInvalidSalaryRange() {
        assertThrows(InvalidSalaryException.class, () -> {
            database.getEmployeesInSalaryRange(-1000.0, 50000.0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            database.getEmployeesInSalaryRange(60000.0, 50000.0);
        });
    }

    @Test
    void testGiveSalaryRaise() throws InvalidSalaryException, EmployeeNotFoundException {
        database.giveSalaryRaise(10.0, 4.0);
        assertEquals(55000.0, database.getEmployee(1).getSalary());
    }

    @Test
    void testGiveSalaryRaiseWithInvalidPercentage() {
        assertThrows(IllegalArgumentException.class, () -> {
            database.giveSalaryRaise(-10.0, 4.0);
        });
    }

    @Test
    void testGetAverageDepartmentSalary() throws InvalidDepartmentException {
        assertEquals(50000.0, database.getAverageDepartmentSalary("IT"));
        assertEquals(0.0, database.getAverageDepartmentSalary("HR"));
    }

    @Test
    void testGetAverageSalaryWithInvalidDepartment() {
        assertThrows(InvalidDepartmentException.class, () -> {
            database.getAverageDepartmentSalary("");
        });
    }
}