package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Comparator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeSortingTest {
    private EmployeeDatabase<Integer> database;
    private Employee<Integer> emp1;
    private Employee<Integer> emp2;
    private Employee<Integer> emp3;

    @BeforeEach
    void setUp() throws InvalidSalaryException, InvalidDepartmentException {
        database = new EmployeeDatabase<>();
        emp1 = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
        emp2 = new Employee<>(2, "Jane Smith", "HR", 60000.0, 4.0, 8);
        emp3 = new Employee<>(3, "Bob Johnson", "Finance", 70000.0, 4.8, 3);

        database.addEmployee(emp1);
        database.addEmployee(emp2);
        database.addEmployee(emp3);
    }

    @Test
    void sortByExperience_ReturnsCorrectOrder() {
        List<Employee<Integer>> sorted = database.sortByExperience();
        assertEquals(emp2, sorted.get(0)); // Most experience
        assertEquals(emp1, sorted.get(1));
        assertEquals(emp3, sorted.get(2)); // Least experience
    }

    @Test
    void sortBySalary_ReturnsCorrectOrder() {
        List<Employee<Integer>> sorted = database.sortBySalary();
        assertEquals(emp3, sorted.get(0)); // Highest salary
        assertEquals(emp2, sorted.get(1));
        assertEquals(emp1, sorted.get(2)); // Lowest salary
    }

    @Test
    void sortByPerformance_ReturnsCorrectOrder() {
        List<Employee<Integer>> sorted = database.sortByPerformance();
        assertEquals(emp3, sorted.get(0)); // Highest rating
        assertEquals(emp1, sorted.get(1));
        assertEquals(emp2, sorted.get(2)); // Lowest rating
    }

    @Test
    void sortEmptyList_ReturnsEmptyList() {
        EmployeeDatabase<Integer> emptyDb = new EmployeeDatabase<>();
        assertTrue(emptyDb.sortByExperience().isEmpty());
        assertTrue(emptyDb.sortBySalary().isEmpty());
        assertTrue(emptyDb.sortByPerformance().isEmpty());
    }

    @Test
    void searchEmployeesByName_ValidSearch_ReturnsMatchingEmployees() {
        List<Employee<Integer>> results = database.searchEmployeesByName("John");
        assertEquals(2, results.size());
        assertTrue(results.contains(emp1));
        assertTrue(results.contains(emp3));
    }

    @Test
    void searchEmployeesByName_NoMatches_ReturnsEmptyList() {
        List<Employee<Integer>> results = database.searchEmployeesByName("Nonexistent");
        assertTrue(results.isEmpty());
    }

    @Test
    void searchEmployeesByName_NullSearchTerm_ReturnsEmptyList() {
        List<Employee<Integer>> results = database.searchEmployeesByName(null);
        assertTrue(results.isEmpty());
    }

    @Test
    void getEmployeesByDepartment_ValidDepartment_ReturnsCorrectEmployees() {
        List<Employee<Integer>> results = database.getEmployeesByDepartment("IT");
        assertEquals(1, results.size());
        assertEquals(emp1, results.get(0));
    }

    @Test
    void getEmployeesByDepartment_InvalidDepartment_ReturnsEmptyList() {
        List<Employee<Integer>> results = database.getEmployeesByDepartment("InvalidDept");
        assertTrue(results.isEmpty());
    }

    @Test
    void getEmployeesByDepartment_NullDepartment_ReturnsEmptyList() {
        List<Employee<Integer>> results = database.getEmployeesByDepartment(null);
        assertTrue(results.isEmpty());
    }

    @Test
    void getEmployeesInSalaryRange_ValidRange_ReturnsCorrectEmployees() {
        List<Employee<Integer>> results = database.getEmployeesInSalaryRange(55000.0, 65000.0);
        assertEquals(1, results.size());
        assertEquals(emp2, results.get(0));
    }

    @Test
    void getEmployeesInSalaryRange_InvalidRange_ReturnsEmptyList() {
        List<Employee<Integer>> results = database.getEmployeesInSalaryRange(80000.0, 90000.0);
        assertTrue(results.isEmpty());
    }

    @Test
    void getHighPerformingEmployees_ValidRating_ReturnsCorrectEmployees() {
        List<Employee<Integer>> results = database.getHighPerformingEmployees(4.5);
        assertEquals(1, results.size());
        assertEquals(emp3, results.get(0));
    }

    @Test
    void getHighPerformingEmployees_InvalidRating_ReturnsEmptyList() {
        List<Employee<Integer>> results = database.getHighPerformingEmployees(5.0);
        assertTrue(results.isEmpty());
    }
} 