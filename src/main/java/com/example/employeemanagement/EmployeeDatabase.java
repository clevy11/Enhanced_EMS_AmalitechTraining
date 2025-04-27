package com.example.employeemanagement;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDatabase<T> {
    private final Map<T, Employee<T>> employees;
    private final List<Employee<T>> employeeList;

    public EmployeeDatabase() {
        this.employees = new HashMap<>();
        this.employeeList = new ArrayList<>();
    }

    // CRUD Operations
    public void addEmployee(Employee<T> employee) {
        employees.put(employee.getEmployeeId(), employee);
        employeeList.add(employee);
    }

    public Employee<T> getEmployee(T employeeId) {
        return employees.get(employeeId);
    }

    public void removeEmployee(T employeeId) {
        Employee<T> employee = employees.remove(employeeId);
        if (employee != null) {
            employeeList.remove(employee);
        }
    }

    public void updateEmployeeDetails(T employeeId, String field, Object newValue) {
        Employee<T> employee = employees.get(employeeId);
        if (employee != null) {
            switch (field.toLowerCase()) {
                case "name" -> employee.setName((String) newValue);
                case "department" -> employee.setDepartment((String) newValue);
                case "salary" -> employee.setSalary((Double) newValue);
                case "performancerating" -> employee.setPerformanceRating((Double) newValue);
                case "yearsofexperience" -> employee.setYearsOfExperience((Integer) newValue);
                case "isactive" -> employee.setActive((Boolean) newValue);
            }
        }
    }

    public List<Employee<T>> getAllEmployees() {
        return new ArrayList<>(employeeList);
    }

    // Search and Filter Operations
    public List<Employee<T>> getEmployeesByDepartment(String department) {
        return employeeList.stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public List<Employee<T>> searchEmployeesByName(String searchTerm) {
        return employeeList.stream()
                .filter(e -> e.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Employee<T>> getHighPerformingEmployees(double minRating) {
        return employeeList.stream()
                .filter(e -> e.getPerformanceRating() >= minRating)
                .collect(Collectors.toList());
    }

    public List<Employee<T>> getEmployeesInSalaryRange(double minSalary, double maxSalary) {
        return employeeList.stream()
                .filter(e -> e.getSalary() >= minSalary && e.getSalary() <= maxSalary)
                .collect(Collectors.toList());
    }

    // Sorting Operations
    public List<Employee<T>> sortByExperience() {
        return employeeList.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Employee<T>> sortBySalary() {
        return employeeList.stream()
                .sorted((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()))
                .collect(Collectors.toList());
    }

    public List<Employee<T>> sortByPerformance() {
        return employeeList.stream()
                .sorted((e1, e2) -> Double.compare(e2.getPerformanceRating(), e1.getPerformanceRating()))
                .collect(Collectors.toList());
    }

    // Salary Management
    public void giveSalaryRaise(double percentage, double minRating) {
        employeeList.stream()
                .filter(e -> e.getPerformanceRating() >= minRating)
                .forEach(e -> e.setSalary(e.getSalary() * (1 + percentage/100)));
    }

    public List<Employee<T>> getTopPaidEmployees(int count) {
        return employeeList.stream()
                .sorted((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public double getAverageDepartmentSalary(String department) {
        return employeeList.stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }
} 