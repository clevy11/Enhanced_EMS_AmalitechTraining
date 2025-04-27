package com.example.employeemanagement;

import java.util.logging.Logger;

public class Employee<T> implements Comparable<Employee<T>> {
    private static final Logger logger = Logger.getLogger(Employee.class.getName());
    private static int idCounter = 1;
    private T employeeId;
    private String name;
    private String department;
    private double salary;
    private double performanceRating;
    private int yearsOfExperience;
    private boolean isActive;

    public Employee(String name, String department, double salary, 
                   double performanceRating, int yearsOfExperience) 
                   throws InvalidSalaryException, InvalidDepartmentException {
        this.employeeId = (T) Integer.valueOf(idCounter++);
        validateInput(name, department, salary, performanceRating, yearsOfExperience);
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.performanceRating = performanceRating;
        this.yearsOfExperience = yearsOfExperience;
        this.isActive = true;
        logger.info("Created new employee with ID: " + this.employeeId);
    }

    private void validateInput(String name, String department, double salary, 
                             double performanceRating, int yearsOfExperience) 
                             throws InvalidSalaryException, InvalidDepartmentException {
        if (name == null || name.trim().isEmpty()) {
            logger.warning("Attempted to create employee with empty name");
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        if (department == null || department.trim().isEmpty()) {
            logger.warning("Attempted to create employee with empty department");
            throw new InvalidDepartmentException("Department cannot be empty");
        }
        
        if (salary < 0) {
            logger.warning("Attempted to create employee with negative salary: " + salary);
            throw new InvalidSalaryException("Salary cannot be negative");
        }
        
        if (performanceRating < 0 || performanceRating > 5) {
            logger.warning("Attempted to create employee with invalid performance rating: " + performanceRating);
            throw new IllegalArgumentException("Performance rating must be between 0 and 5");
        }
        
        if (yearsOfExperience < 0) {
            logger.warning("Attempted to create employee with negative years of experience: " + yearsOfExperience);
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }
    }

    // Getters and Setters
    public T getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    public double getPerformanceRating() { return performanceRating; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public boolean isActive() { return isActive; }

    public void setName(String name) { 
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setDepartment(String department) throws InvalidDepartmentException {
        if (department == null || department.trim().isEmpty()) {
            throw new InvalidDepartmentException("Department cannot be empty");
        }
        this.department = department;
    }

    public void setSalary(double salary) throws InvalidSalaryException {
        if (salary < 0) {
            throw new InvalidSalaryException("Salary cannot be negative");
        }
        this.salary = salary;
    }

    public void setPerformanceRating(double performanceRating) { 
        if (performanceRating < 0 || performanceRating > 5) {
            throw new IllegalArgumentException("Performance rating must be between 0 and 5");
        }
        this.performanceRating = performanceRating; 
    }

    public void setYearsOfExperience(int yearsOfExperience) { 
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }
        this.yearsOfExperience = yearsOfExperience; 
    }

    public void setActive(boolean active) { isActive = active; }

    @Override
    public int compareTo(Employee<T> other) {
        if (other == null) {
            return 1;
        }
        return Integer.compare(other.yearsOfExperience, this.yearsOfExperience);
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Department: %s | Salary: $%.2f | " +
                           "Rating: %.1f | Experience: %d years | Status: %s",
                           employeeId, name, department, salary, performanceRating,
                           yearsOfExperience, isActive ? "Active" : "Inactive");
    }
} 