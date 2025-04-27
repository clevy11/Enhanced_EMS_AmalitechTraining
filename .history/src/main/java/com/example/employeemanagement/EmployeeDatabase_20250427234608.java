package com.example.employeemanagement;

import com.example.employeemanagement.Exception.EmployeeNotFoundException;
import com.example.employeemanagement.Exception.InvalidDepartmentException;
import com.example.employeemanagement.Exception.InvalidSalaryException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class EmployeeDatabase<T> {
    private static final Logger logger = Logger.getLogger(EmployeeDatabase.class.getName());
    private final List<Employee<T>> employees = new ArrayList<>();

    public void addEmployee(Employee<T> employee) {
        try {
            employees.add(employee);
            logger.info("Added employee: " + employee);
        } catch (Exception e) {
            logger.severe("Error adding employee: " + e.getMessage());
            throw e;
        }
    }

    public Employee<T> getEmployee(T id) throws EmployeeNotFoundException {
        try {
            return employees.stream()
                .filter(e -> e.getEmployeeId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found"));
        } catch (Exception e) {
            logger.severe("Error getting employee: " + e.getMessage());
            throw e;
        }
    }

    public Employee<T> getEmployeeByName(String name) throws EmployeeNotFoundException {
        try {
            return employees.stream()
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with name " + name + " not found"));
        } catch (Exception e) {
            logger.severe("Error getting employee by name: " + e.getMessage());
            throw e;
        }
    }

    public void removeEmployee(T id) throws EmployeeNotFoundException {
        try {
            Employee<T> employee = getEmployee(id);
            employee.setActive(false);
            logger.info("Removed employee: " + employee);
        } catch (Exception e) {
            logger.severe("Error removing employee: " + e.getMessage());
            throw e;
        }
    }

    public void updateEmployeeDetails(T id, String field, Object value) 
            throws EmployeeNotFoundException, InvalidSalaryException, InvalidDepartmentException {
        try {
            Employee<T> employee = getEmployee(id);
            updateEmployeeField(employee, field, value);
            logger.info("Updated employee " + id + ": " + field + " = " + value);
        } catch (Exception e) {
            logger.severe("Error updating employee: " + e.getMessage());
            throw e;
        }
    }

    private void updateEmployeeField(Employee<T> employee, String field, Object value) 
            throws InvalidSalaryException, InvalidDepartmentException {
        switch (field.toLowerCase()) {
            case "name" -> employee.setName((String) value);
            case "department" -> employee.setDepartment((String) value);
            case "salary" -> employee.setSalary((Double) value);
            case "performancerating" -> employee.setPerformanceRating((Double) value);
            case "yearsofexperience" -> employee.setYearsOfExperience((Integer) value);
            default -> throw new IllegalArgumentException("Invalid field: " + field);
        }
    }

    public List<Employee<T>> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    public List<Employee<T>> getEmployeesByDepartment(String department) {
        return employees.stream()
            .filter(e -> e.getDepartment().equalsIgnoreCase(department))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Employee<T>> searchEmployeesByName(String name) {
        return employees.stream()
            .filter(e -> e.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Employee<T>> sortByExperience() {
        return employees.stream()
            .sorted((e1, e2) -> Integer.compare(e2.getYearsOfExperience(), e1.getYearsOfExperience()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Employee<T>> sortBySalary() {
        return employees.stream()
            .sorted((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Employee<T>> sortByPerformance() {
        return employees.stream()
            .sorted((e1, e2) -> Double.compare(e2.getPerformanceRating(), e1.getPerformanceRating()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // Search and Filter Operations
    public List<Employee<T>> getHighPerformingEmployees(double minRating) {
        return employees.stream()
                .filter(e -> e.getPerformanceRating() >= minRating)
                .collect(Collectors.toList());
    }

    public List<Employee<T>> getEmployeesInSalaryRange(double minSalary, double maxSalary) {
        return employees.stream()
                .filter(e -> e.getSalary() >= minSalary && e.getSalary() <= maxSalary)
                .collect(Collectors.toList());
    }

    // Salary Management
    public void giveSalaryRaise(double percentage, double minRating) {
        employees.stream()
                .filter(e -> e.getPerformanceRating() >= minRating)
                .forEach(e -> e.setSalary(e.getSalary() * (1 + percentage/100)));
    }

    public List<Employee<T>> getTopPaidEmployees(int count) {
        return employees.stream()
                .sorted((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public double getAverageDepartmentSalary(String department) {
        return employees.stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }
} 