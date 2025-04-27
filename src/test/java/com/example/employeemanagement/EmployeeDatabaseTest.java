package com.example.employeemanagement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class EmployeeDatabaseTest {

    @Test
    void addEmployee() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        db.addEmployee(emp);
        assertEquals(emp, db.getEmployee(1));
    }

    @Test
    void getEmployee() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        db.addEmployee(emp);
        assertEquals(emp, db.getEmployee(1));
    }

    @Test
    void removeEmployee() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        db.addEmployee(emp);
        db.removeEmployee(1);
        assertNull(db.getEmployee(1));
    }

    @Test
    void updateEmployeeDetails() {
        EmployeeDatabase<Integer> db = new EmployeeDatabase<>();
        Employee<Integer> emp = new Employee<>(1, "John Doe", "Engineering", 60000, 4.5, 5);
        db.addEmployee(emp);
        db.updateEmployeeDetails(1, "salary", 65000.0);
        assertEquals(65000.0, db.getEmployee(1).getSalary());
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