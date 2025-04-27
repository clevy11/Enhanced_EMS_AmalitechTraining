package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

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
        assertThrows(InvalidSalaryException.class, () -> {
            try {
                database.addEmployee(invalidEmployee);
            } catch (InvalidDepartmentException e) {
                fail("Unexpected InvalidDepartmentException");
            }
        });
    }

    @Test
    void addEmployee_InvalidDepartment_ThrowsInvalidDepartmentException() {
        Employee<Integer> invalidEmployee = new Employee<>(2, "Jane Doe", "InvalidDept", 50000.0, 4.0, 3);
        assertThrows(InvalidDepartmentException.class, () -> {
            try {
                database.addEmployee(invalidEmployee);
            } catch (InvalidSalaryException e) {
                fail("Unexpected InvalidSalaryException");
            }
        });
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
        assertThrows(InvalidSalaryException.class, () -> {
            try {
                database.updateEmployeeDetails(1, "salary", -1000.0);
            } catch (EmployeeNotFoundException | InvalidDepartmentException e) {
                fail("Unexpected exception: " + e.getClass().getSimpleName());
            }
        });
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
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                database.updateEmployeeDetails(1, "invalidField", "value");
            } catch (EmployeeNotFoundException | InvalidSalaryException | InvalidDepartmentException e) {
                fail("Unexpected exception: " + e.getClass().getSimpleName());
            }
        });
    }

    @Test
    void getAllEmployees_ReturnsAllEmployees() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000.0, 4.0, 3);
        database.addEmployee(emp2);
        assertEquals(2, database.getAllEmployees().size());
    }

    @Test
    void searchEmployeesByName_ReturnsMatchingEmployees() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000.0, 4.0, 3);
        database.addEmployee(emp2);
        assertEquals(1, database.searchEmployeesByName("John").size());
        assertEquals(1, database.searchEmployeesByName("Smith").size());
    }

    @Test
    void getHighPerformingEmployees_ReturnsHighPerformers() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000.0, 4.0, 3);
        database.addEmployee(emp2);
        assertEquals(2, database.getHighPerformingEmployees(4.0).size());
        assertEquals(1, database.getHighPerformingEmployees(4.5).size());
    }

    @Test
    void getEmployeesInSalaryRange_ReturnsEmployeesInRange() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000.0, 4.0, 3);
        database.addEmployee(emp2);
        assertEquals(2, database.getEmployeesInSalaryRange(50000, 65000).size());
        assertEquals(1, database.getEmployeesInSalaryRange(55000, 65000).size());
    }

    @Test
    void sortByExperience_ReturnsSortedList() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000.0, 4.0, 3);
        database.addEmployee(emp2);
        List<Employee<Integer>> sorted = database.sortByExperience();
        assertEquals(validEmployee, sorted.get(0));
        assertEquals(emp2, sorted.get(1));
    }

    @Test
    void sortBySalary_ReturnsSortedList() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000.0, 4.0, 3);
        database.addEmployee(emp2);
        List<Employee<Integer>> sorted = database.sortBySalary();
        assertEquals(validEmployee, sorted.get(0));
        assertEquals(emp2, sorted.get(1));
    }

    @Test
    void sortByPerformance_ReturnsSortedList() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000.0, 4.0, 3);
        database.addEmployee(emp2);
        List<Employee<Integer>> sorted = database.sortByPerformance();
        assertEquals(validEmployee, sorted.get(0));
        assertEquals(emp2, sorted.get(1));
    }

    @Test
    void giveSalaryRaise_UpdatesSalaries() throws Exception {
        database.addEmployee(validEmployee);
        database.giveSalaryRaise(10, 4.0);
        assertEquals(55000.0, database.getEmployee(1).getSalary());
    }

    @Test
    void getTopPaidEmployees_ReturnsTopPaid() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "Marketing", 55000.0, 4.0, 3);
        database.addEmployee(emp2);
        assertEquals(1, database.getTopPaidEmployees(1).size());
        assertEquals(validEmployee, database.getTopPaidEmployees(1).get(0));
    }

    @Test
    void getAverageDepartmentSalary_ReturnsCorrectAverage() throws Exception {
        database.addEmployee(validEmployee);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Smith", "IT", 55000.0, 4.0, 3);
        database.addEmployee(emp2);
        assertEquals(52500.0, database.getAverageDepartmentSalary("IT"));
    }
}