package com.example.employeemanagement;

import javafx.beans.property.*;

public class Employee<T> implements Comparable<Employee<T>> {
    private T employeeId;
    private final StringProperty name;
    private final StringProperty department;
    private final DoubleProperty salary;
    private final DoubleProperty performanceRating;
    private final IntegerProperty yearsOfExperience;
    private final BooleanProperty isActive;

    public Employee(T employeeId, String name, String department, double salary, 
                   double performanceRating, int yearsOfExperience) {
        this.employeeId = employeeId;
        this.name = new SimpleStringProperty(name);
        this.department = new SimpleStringProperty(department);
        this.salary = new SimpleDoubleProperty(salary);
        this.performanceRating = new SimpleDoubleProperty(performanceRating);
        this.yearsOfExperience = new SimpleIntegerProperty(yearsOfExperience);
        this.isActive = new SimpleBooleanProperty(true);
    }

    // Getters and Setters
    public T getEmployeeId() { return employeeId; }
    public String getName() { return name.get(); }
    public String getDepartment() { return department.get(); }
    public double getSalary() { return salary.get(); }
    public double getPerformanceRating() { return performanceRating.get(); }
    public int getYearsOfExperience() { return yearsOfExperience.get(); }
    public boolean isActive() { return isActive.get(); }

    public void setName(String name) { this.name.set(name); }
    public void setDepartment(String department) { this.department.set(department); }
    public void setSalary(double salary) { this.salary.set(salary); }
    public void setPerformanceRating(double performanceRating) { 
        this.performanceRating.set(performanceRating); 
    }
    public void setYearsOfExperience(int yearsOfExperience) { 
        this.yearsOfExperience.set(yearsOfExperience); 
    }
    public void setActive(boolean active) { isActive.set(active); }

    // JavaFX Property Methods
    public StringProperty nameProperty() { return name; }
    public StringProperty departmentProperty() { return department; }
    public DoubleProperty salaryProperty() { return salary; }
    public DoubleProperty performanceRatingProperty() { return performanceRating; }
    public IntegerProperty yearsOfExperienceProperty() { return yearsOfExperience; }
    public BooleanProperty isActiveProperty() { return isActive; }

    @Override
    public int compareTo(Employee<T> other) {
        return Integer.compare(other.getYearsOfExperience(), this.getYearsOfExperience());
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Department: %s | Salary: $%.2f | " +
                           "Rating: %.1f | Experience: %d years | Status: %s",
                           employeeId, getName(), getDepartment(), getSalary(), 
                           getPerformanceRating(), getYearsOfExperience(), 
                           isActive() ? "Active" : "Inactive");
    }
} 