package com.example.employeemanagement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {
    @Test
    void compareTo_EmployeeWithMoreExperience_ReturnsNegative() {
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Doe", "IT", 55000.0, 4.0, 8);
        
        assertTrue(emp1.compareTo(emp2) < 0);
    }

    @Test
    void compareTo_EmployeeWithLessExperience_ReturnsPositive() {
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 8);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Doe", "IT", 55000.0, 4.0, 5);
        
        assertTrue(emp1.compareTo(emp2) > 0);
    }

    @Test
    void compareTo_EmployeeWithSameExperience_ReturnsZero() {
        Employee<Integer> emp1 = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
        Employee<Integer> emp2 = new Employee<>(2, "Jane Doe", "IT", 55000.0, 4.0, 5);
        
        assertEquals(0, emp1.compareTo(emp2));
    }

    @Test
    void toString_ValidEmployee_ReturnsFormattedString() {
        Employee<Integer> emp = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
        String expected = "ID: 1 | Name: John Doe | Department: IT | Salary: $50000.00 | Rating: 4.5 | Experience: 5 years | Status: Active";
        
        assertEquals(expected, emp.toString());
    }

    @Test
    void constructor_NullName_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> 
            new Employee<>(1, null, "IT", 50000.0, 4.5, 5));
    }

    @Test
    void constructor_NullDepartment_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> 
            new Employee<>(1, "John Doe", null, 50000.0, 4.5, 5));
    }

    @Test
    void setSalary_NegativeValue_ThrowsIllegalArgumentException() {
        Employee<Integer> emp = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
        assertThrows(IllegalArgumentException.class, () -> emp.setSalary(-1000.0));
    }

    @Test
    void setPerformanceRating_OutOfRange_ThrowsIllegalArgumentException() {
        Employee<Integer> emp = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
        assertThrows(IllegalArgumentException.class, () -> emp.setPerformanceRating(6.0));
    }

    @Test
    void setYearsOfExperience_NegativeValue_ThrowsIllegalArgumentException() {
        Employee<Integer> emp = new Employee<>(1, "John Doe", "IT", 50000.0, 4.5, 5);
        assertThrows(IllegalArgumentException.class, () -> emp.setYearsOfExperience(-1));
    }
} 