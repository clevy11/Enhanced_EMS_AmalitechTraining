package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EmployeeDatabase<T> {
    private final Map<T, Employee<T>> employees;
    private final List<Employee<T>> employeeList;
    private static final Logger logger = Logger.getLogger(EmployeeDatabase.class.getName());
    private static final Set<String> VALID_DEPARTMENTS = Set.of(
        "HR", "Finance", "IT", "Marketing", "Sales", "Operations", "Research"
    );

    public EmployeeDatabase() {
        this.employees = new HashMap<>();
        this.employeeList = new ArrayList<>();
    }

    // CRUD Operations
    public void addEmployee(Employee<T> employee) throws InvalidSalaryException, InvalidDepartmentException {
        try {
            validateEmployee(employee);
            employees.put(employee.getEmployeeId(), employee);
            employeeList.add(employee);
            logger.log(Level.INFO, "Employee added successfully: " + employee.getEmployeeId());
        } catch (InvalidSalaryException | InvalidDepartmentException e) {
            logger.log(Level.SEVERE, "Failed to add employee: " + e.getMessage());
            throw e;
        }
    }

    public Employee<T> getEmployee(T employeeId) throws EmployeeNotFoundException {
        try {
            Employee<T> employee = employees.get(employeeId);
            if (employee == null) {
                throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found");
            }
            return employee;
        } catch (EmployeeNotFoundException e) {
            logger.log(Level.WARNING, "Employee not found: " + e.getMessage());
            throw e;
        }
    }

    public void removeEmployee(T employeeId) throws EmployeeNotFoundException {
        try {
            Employee<T> employee = employees.remove(employeeId);
            if (employee == null) {
                throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found");
            }
            employeeList.remove(employee);
            logger.log(Level.INFO, "Employee removed successfully: " + employeeId);
        } catch (EmployeeNotFoundException e) {
            logger.log(Level.WARNING, "Failed to remove employee: " + e.getMessage());
            throw e;
        }
    }

    public void updateEmployeeDetails(T employeeId, String field, Object newValue) 
            throws EmployeeNotFoundException, InvalidSalaryException, InvalidDepartmentException {
        try {
            Employee<T> employee = getEmployee(employeeId);
            
            switch (field.toLowerCase()) {
                case "name" -> {
                    if (newValue == null || ((String) newValue).trim().isEmpty()) {
                        throw new IllegalArgumentException("Name cannot be empty");
                    }
                    employee.setName((String) newValue);
                }
                case "department" -> {
                    if (!VALID_DEPARTMENTS.contains(((String) newValue).toUpperCase())) {
                        throw new InvalidDepartmentException("Invalid department: " + newValue);
                    }
                    employee.setDepartment((String) newValue);
                }
                case "salary" -> {
                    double salary = (Double) newValue;
                    if (salary < 0) {
                        throw new InvalidSalaryException("Salary cannot be negative");
                    }
                    employee.setSalary(salary);
                }
                case "performancerating" -> {
                    double rating = (Double) newValue;
                    if (rating < 0 || rating > 5) {
                        throw new IllegalArgumentException("Performance rating must be between 0 and 5");
                    }
                    employee.setPerformanceRating(rating);
                }
                case "yearsofexperience" -> {
                    int years = (Integer) newValue;
                    if (years < 0) {
                        throw new IllegalArgumentException("Years of experience cannot be negative");
                    }
                    employee.setYearsOfExperience(years);
                }
                case "isactive" -> employee.setActive((Boolean) newValue);
                default -> throw new IllegalArgumentException("Invalid field: " + field);
            }
            logger.log(Level.INFO, "Employee details updated successfully: " + employeeId);
        } catch (EmployeeNotFoundException | InvalidSalaryException | InvalidDepartmentException e) {
            logger.log(Level.SEVERE, "Failed to update employee: " + e.getMessage());
            throw e;
        }
    }

    private void validateEmployee(Employee<T> employee) throws InvalidSalaryException, InvalidDepartmentException {
        if (employee.getSalary() < 0) {
            throw new InvalidSalaryException("Salary cannot be negative");
        }
        if (!VALID_DEPARTMENTS.contains(employee.getDepartment().toUpperCase())) {
            throw new InvalidDepartmentException("Invalid department: " + employee.getDepartment());
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be empty");
        }
    }

    public List<Employee<T>> getAllEmployees() {
        return new ArrayList<>(employeeList);
    }

    // Search and Filter Operations
    public List<Employee<T>> getEmployeesByDepartment(String department) {
        try {
            if (!VALID_DEPARTMENTS.contains(department.toUpperCase())) {
                throw new InvalidDepartmentException("Invalid department: " + department);
            }
            return employeeList.stream()
                    .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                    .collect(Collectors.toList());
        } catch (InvalidDepartmentException e) {
            logger.log(Level.WARNING, "Invalid department search: " + e.getMessage());
            return Collections.emptyList();
        }
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