package com.example.employeemanagement;

import java.util.Objects;

public class Employee<T> implements Comparable<Employee<T>> {
    private T employeeId;
    private String name;
    private String department;
    private double salary;
    private double performanceRating;
    private int yearsOfExperience;
    private boolean isActive;

    public Employee(T employeeId, String name, String department, double salary, 
                   double performanceRating, int yearsOfExperience) {
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null").trim();
        if (this.name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.department = Objects.requireNonNull(department, "Department cannot be null").trim();
        if (this.department.isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        setSalary(salary);
        setPerformanceRating(performanceRating);
        setYearsOfExperience(yearsOfExperience);
        this.isActive = true;
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
        this.name = Objects.requireNonNull(name, "Name cannot be null").trim();
        if (this.name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    public void setDepartment(String department) { 
        this.department = Objects.requireNonNull(department, "Department cannot be null").trim();
        if (this.department.isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
    }

    public void setSalary(double salary) { 
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
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
        Objects.requireNonNull(other, "Cannot compare with null employee");
        return Integer.compare(other.yearsOfExperience, this.yearsOfExperience);
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Department: %s | Salary: $%.2f | " +
                           "Rating: %.1f | Experience: %d years | Status: %s",
                           employeeId, name, department, salary, performanceRating,
                           yearsOfExperience, isActive ? "Active" : "Inactive");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee<?> employee = (Employee<?>) o;
        return employeeId.equals(employee.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }
} 