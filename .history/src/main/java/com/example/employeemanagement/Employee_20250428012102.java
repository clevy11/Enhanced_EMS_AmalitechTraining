package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Employee<T> implements Comparable<Employee<T>> {
    private static final Logger LOGGER = Logger.getLogger(Employee.class.getName());
    private static final Set<String> VALID_DEPARTMENTS = new HashSet<>(Arrays.asList(
        "HR", "IT", "Finance", "Marketing", "Operations", "Sales"
    ));
    
    private static int nextId = 1; // Static counter for auto-incrementing IDs
    
    private final T employeeId;
    private String name;
    private String department;
    private double salary;
    private double performanceRating;
    private int yearsOfExperience;
    private boolean isActive;

    public Employee(String name, String department, double salary) {
        this.employeeId = (T) Integer.valueOf(nextId++);
        this.name = name;
        this.department = department;
        this.salary = Math.round(salary * 100.0) / 100.0;
        this.performanceRating = 0.0;
        this.yearsOfExperience = 0;
        this.isActive = true;
        LOGGER.log(Level.INFO, "Created new employee with auto-generated ID: {0}", this.employeeId);
    }

    private void validateSalary(double salary) throws InvalidSalaryException {
        if (salary < 0) {
            LOGGER.log(Level.SEVERE, "Attempted to set invalid salary: {0}", salary);
            throw new InvalidSalaryException("Salary cannot be negative");
        }
        if (Double.isNaN(salary) || Double.isInfinite(salary)) {
            LOGGER.log(Level.SEVERE, "Attempted to set invalid salary: {0}", salary);
            throw new InvalidSalaryException("Salary must be a valid number");
        }
    }

    private void validateDepartment(String department) throws InvalidDepartmentException {
        if (department == null) {
            LOGGER.log(Level.SEVERE, "Attempted to set null department");
            throw new InvalidDepartmentException("Department cannot be null");
        }
        if (department.trim().isEmpty()) {
            LOGGER.log(Level.SEVERE, "Attempted to set empty department");
            throw new InvalidDepartmentException("Department cannot be empty");
        }
        if (!VALID_DEPARTMENTS.contains(department)) {
            LOGGER.log(Level.SEVERE, "Attempted to set invalid department: {0}", department);
            throw new InvalidDepartmentException("Invalid department. Valid departments are: " + VALID_DEPARTMENTS);
        }
    }

    private void validateName(String name) throws IllegalArgumentException {
        if (name == null) {
            LOGGER.log(Level.SEVERE, "Attempted to set null name");
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (name.trim().isEmpty()) {
            LOGGER.log(Level.SEVERE, "Attempted to set empty name");
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() < 2) {
            LOGGER.log(Level.SEVERE, "Attempted to set name that is too short: {0}", name);
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
    }

    // Getters and Setters with validation
    public T getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    public double getPerformanceRating() { return performanceRating; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public boolean isActive() { return isActive; }

    public void setName(String name) {
        validateName(name);
        this.name = name;
        LOGGER.log(Level.INFO, "Updated name for employee {0}: {1}", new Object[]{employeeId, name});
    }

    public void setDepartment(String department) throws InvalidDepartmentException {
        validateDepartment(department);
        this.department = department;
        LOGGER.log(Level.INFO, "Updated department for employee {0}: {1}", new Object[]{employeeId, department});
    }

    public void setSalary(double salary) throws InvalidSalaryException {
        validateSalary(salary);
        this.salary = Math.round(salary * 100.0) / 100.0;
        LOGGER.log(Level.INFO, "Updated salary for employee {0}: {1}", new Object[]{employeeId, this.salary});
    }

    public void setPerformanceRating(double performanceRating) throws IllegalArgumentException {
        if (performanceRating < 0 || performanceRating > 5) {
            LOGGER.log(Level.SEVERE, "Performance rating out of range (0-5): {0}", performanceRating);
            throw new IllegalArgumentException("Performance rating must be between 0 and 5");
        }
        this.performanceRating = Math.round(performanceRating * 10.0) / 10.0; // Round to 1 decimal place
        LOGGER.log(Level.INFO, "Updated performance rating for employee {0}: {1}", new Object[]{employeeId, this.performanceRating});
    }

    public void setYearsOfExperience(int yearsOfExperience) throws IllegalArgumentException {
        if (yearsOfExperience < 0) {
            LOGGER.log(Level.SEVERE, "Years of experience cannot be negative: {0}", yearsOfExperience);
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }
        this.yearsOfExperience = yearsOfExperience;
        LOGGER.log(Level.INFO, "Updated years of experience for employee {0}: {1}", new Object[]{employeeId, yearsOfExperience});
    }

    public void setActive(boolean active) {
        isActive = active;
        LOGGER.log(Level.INFO, "Updated active status for employee {0}: {1}", new Object[]{employeeId, active});
    }

    @Override
    public int compareTo(Employee<T> other) {
        if (other == null) {
            return 1; // This object is greater than null
        }
        return Integer.compare(other.yearsOfExperience, this.yearsOfExperience);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee<?> employee = (Employee<?>) obj;
        return employeeId.equals(employee.employeeId);
    }

    @Override
    public int hashCode() {
        return employeeId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Department: %s | Salary: $%.2f | " +
                           "Rating: %.1f | Experience: %d years | Status: %s",
                           employeeId, name, department, salary, performanceRating,
                           yearsOfExperience, isActive ? "Active" : "Inactive");
    }
} 