package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeDatabaseTest {
    private EmployeeDatabase<Integer> database;
    private Employee<Integer> testEmployee;
    private Integer testEmployeeId;

    @BeforeEach
    void setUp() throws InvalidSalaryException, InvalidDepartmentException {
        database = new EmployeeDatabase<>();
        testEmployee = new Employee<>("John Doe", "IT", 50000.0);
        testEmployeeId = database.addEmployee(testEmployee);
    }

    @Test
    void testAddEmployee() throws InvalidSalaryException, InvalidDepartmentException, EmployeeNotFoundException {
        Employee<Integer> newEmployee = new Employee<>("Jane Smith", "HR", 60000.0);
        Integer newEmployeeId = database.addEmployee(newEmployee);
        Employee<Integer> retrievedEmployee = database.getEmployee(newEmployeeId);
        assertEquals(newEmployee.getName(), retrievedEmployee.getName());
        assertEquals(newEmployee.getDepartment(), retrievedEmployee.getDepartment());
        assertEquals(newEmployee.getSalary(), retrievedEmployee.getSalary());
    }

    @Test
    void testAddDuplicateEmployee() throws InvalidSalaryException, InvalidDepartmentException {
        assertThrows(IllegalArgumentException.class, () -> {
            Employee<Integer> duplicate = new Employee<>("John Doe", "IT", 50000.0);
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
        database.removeEmployee(testEmployeeId);
        assertThrows(EmployeeNotFoundException.class, () -> {
            database.getEmployee(testEmployeeId);
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
        database.updateEmployeeDetails(testEmployeeId, "salary", 55000.0);
        assertEquals(55000.0, database.getEmployee(testEmployeeId).getSalary());
    }

    @Test
    void testUpdateWithInvalidSalary() {
        assertThrows(InvalidSalaryException.class, () -> {
            database.updateEmployeeDetails(testEmployeeId, "salary", -1000.0);
        });
    }

    @Test
    void testUpdateWithInvalidDepartment() {
        assertThrows(InvalidDepartmentException.class, () -> {
            database.updateEmployeeDetails(testEmployeeId, "department", "Invalid Department");
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
        // Set performance rating to 4.5 to qualify for the raise
        database.updateEmployeeDetails(testEmployeeId, "performancerating", 4.5);
        database.giveSalaryRaise(10.0, 4.0);
        assertEquals(55000.0, database.getEmployee(testEmployeeId).getSalary());
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