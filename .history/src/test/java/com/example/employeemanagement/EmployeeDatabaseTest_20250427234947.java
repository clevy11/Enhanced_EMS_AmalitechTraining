package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeDatabaseTest {
    private EmployeeDatabase<Integer> database;
    private Employee<Integer> validEmployee;

    @BeforeEach
    void setUp() {
        database = new EmployeeDatabase<>();
        validEmployee = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
    }

    @Test
    void addEmployee_ValidEmployee_Success() throws InvalidSalaryException, InvalidDepartmentException {
        database.addEmployee(validEmployee);
        assertEquals(1, database.getAllEmployees().size());
    }

    @Test
    void addEmployee_NegativeSalary_ThrowsInvalidSalaryException() {
        Employee<Integer> invalidEmployee = new Employee<>(2, "Jane Doe", "IT", -1000.0, 4.0, 3);
        assertThrows(InvalidSalaryException.class, () -> database.addEmployee(invalidEmployee));
    }

    @Test
    void addEmployee_InvalidDepartment_ThrowsInvalidDepartmentException() {
        Employee<Integer> invalidEmployee = new Employee<>(2, "Jane Doe", "InvalidDept", 50000.0, 4.0, 3);
        assertThrows(InvalidDepartmentException.class, () -> database.addEmployee(invalidEmployee));
    }

    @Test
    void getEmployee_ExistingEmployee_ReturnsEmployee() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> found = database.getEmployee(1);
        assertNotNull(found);
        assertEquals(validEmployee.getName(), found.getName());
    }

    @Test
    void getEmployee_NonExistingEmployee_ThrowsEmployeeNotFoundException() {
        assertThrows(EmployeeNotFoundException.class, () -> database.getEmployee(999));
    }

    @Test
    void removeEmployee_ExistingEmployee_Success() throws Exception {
        database.addEmployee(validEmployee);
        database.removeEmployee(1);
        assertThrows(EmployeeNotFoundException.class, () -> database.getEmployee(1));
    }

    @Test
    void updateEmployeeDetails_ValidUpdate_Success() throws Exception {
        database.addEmployee(validEmployee);
        database.updateEmployeeDetails(1, "salary", 60000.0);
        Employee<Integer> updated = database.getEmployee(1);
        assertEquals(60000.0, updated.getSalary());
    }

    @Test
    void updateEmployeeDetails_NegativeSalary_ThrowsInvalidSalaryException() throws Exception {
        database.addEmployee(validEmployee);
        assertThrows(InvalidSalaryException.class, 
            () -> database.updateEmployeeDetails(1, "salary", -1000.0));
    }

    @Test
    void getEmployeesByDepartment_ValidDepartment_ReturnsEmployees() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> anotherEmployee = new Employee<>(2, "Jane Doe", "IT", 55000.0, 4.0, 3);
        database.addEmployee(anotherEmployee);

        assertEquals(2, database.getEmployeesByDepartment("IT").size());
    }

    @Test
    void getEmployeesByDepartment_InvalidDepartment_ReturnsEmptyList() {
        assertEquals(0, database.getEmployeesByDepartment("InvalidDept").size());
    }

    @Test
    void updateEmployeeDetails_InvalidField_ThrowsIllegalArgumentException() throws Exception {
        database.addEmployee(validEmployee);
        assertThrows(IllegalArgumentException.class,
            () -> database.updateEmployeeDetails(1, "invalidField", "value"));
    }

    @Test
    void getAllEmployees() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(2, db.getAllEmployees().size());
    }

    @Test
    void getEmployeesByDepartment() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(1, db.getEmployeesByDepartment("Engineering").size());
    }

    @Test
    void searchEmployeesByName() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(1, db.searchEmployeesByName("John").size());
    }

    @Test
    void getHighPerformingEmployees() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(2, db.getHighPerformingEmployees(4.0).size());
    }

    @Test
    void getEmployeesInSalaryRange() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(2, db.getEmployeesInSalaryRange(50000, 65000).size());
    }

    @Test
    void sortByExperience() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(emp1, db.sortByExperience().get(0));
    }

    @Test
    void sortBySalary() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(emp1, db.sortBySalary().get(0));
    }

    @Test
    void sortByPerformance() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(emp1, db.sortByPerformance().get(0));
    }

    @Test
    void giveSalaryRaise() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        db.addEmployee(emp1);
        db.giveSalaryRaise(10, 4.0);
        assertEquals(66000.0, db.getEmployee(1).getSalary());
    }

    @Test
    void getTopPaidEmployees() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(1, db.getTopPaidEmployees(1).size());

    }

    @Test
    void getAverageDepartmentSalary() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Engineering", 55000, 4.0, 3);
        db.addEmployee(emp1);
        db.addEmployee(emp2);
        assertEquals(57500.0, db.getAverageDepartmentSalary("Engineering"));
    }
}