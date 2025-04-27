package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;

public class EmployeeDatabase<T> {
    private static final Logger LOGGER = Logger.getLogger(EmployeeDatabase.class.getName());
    private final Map<T, Employee<T>> employees;
    private final List<Employee<T>> employeeList;
    private int nextId = 1; // Auto-increment counter

    public EmployeeDatabase() {
        this.employees = new HashMap<>();
        this.employeeList = new ArrayList<>();
        LOGGER.log(Level.INFO, "Initialized new EmployeeDatabase");
    }

    // CRUD Operations
    public void addEmployee(Employee<T> employee) {
        if (employee == null) {
            LOGGER.log(Level.SEVERE, "Attempted to add null employee");
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        // Set auto-incremented ID
        employee.setEmployeeId((T) Integer.valueOf(nextId++));
        
        if (employees.containsKey(employee.getEmployeeId())) {
            LOGGER.log(Level.SEVERE, "Employee with ID {0} already exists", employee.getEmployeeId());
            throw new IllegalArgumentException("Employee with this ID already exists");
        }
        
        employees.put(employee.getEmployeeId(), employee);
        employeeList.add(employee);
        LOGGER.log(Level.INFO, "Added new employee with ID: {0}", employee.getEmployeeId());
    }

    public Employee<T> getEmployee(T employeeId) throws EmployeeNotFoundException {
        if (employeeId == null) {
            LOGGER.log(Level.SEVERE, "Null employee ID provided");
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        Employee<T> employee = employees.get(employeeId);
        if (employee == null) {
            LOGGER.log(Level.SEVERE, "Employee with ID {0} not found", employeeId);
            throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found");
        }
        LOGGER.log(Level.INFO, "Retrieved employee with ID: {0}", employeeId);
        return employee;
    }

    public void removeEmployee(T employeeId) throws EmployeeNotFoundException {
        if (employeeId == null) {
            LOGGER.log(Level.SEVERE, "Null employee ID provided");
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        Employee<T> employee = employees.remove(employeeId);
        if (employee == null) {
            LOGGER.log(Level.SEVERE, "Attempted to remove non-existent employee with ID: {0}", employeeId);
            throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found");
        }
        employeeList.remove(employee);
        LOGGER.log(Level.INFO, "Removed employee with ID: {0}", employeeId);
    }

    public void updateEmployeeDetails(T employeeId, String field, Object value) 
            throws EmployeeNotFoundException, InvalidSalaryException, InvalidDepartmentException {
        if (employeeId == null) {
            LOGGER.log(Level.SEVERE, "Null employee ID provided");
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        Employee<T> employee = getEmployee(employeeId);
        if (employee == null) {
            LOGGER.log(Level.SEVERE, "Employee not found: {0}", employeeId);
            throw new EmployeeNotFoundException("Employee not found");
        }

        try {
            switch (field.toLowerCase()) {
                case "salary" -> {
                    if (!(value instanceof Double)) {
                        throw new IllegalArgumentException("Salary must be a number");
                    }
                    double salary = (Double) value;
                    if (salary < 0) {
                        throw new InvalidSalaryException("Salary cannot be negative");
                    }
                    employee.setSalary(Math.round(salary * 100.0) / 100.0);
                }
                case "department" -> {
                    if (!(value instanceof String)) {
                        throw new IllegalArgumentException("Department must be a string");
                    }
                    String dept = (String) value;
                    if (dept == null || dept.trim().isEmpty()) {
                        throw new InvalidDepartmentException("Department cannot be null or empty");
                    }
                    employee.setDepartment(dept);
                }
                case "performancerating" -> {
                    if (!(value instanceof Double)) {
                        throw new IllegalArgumentException("Performance rating must be a number");
                    }
                    double rating = (Double) value;
                    if (rating < 0 || rating > 5) {
                        throw new IllegalArgumentException("Rating must be between 0 and 5");
                    }
                    employee.setPerformanceRating(rating);
                }
                case "yearsofexperience" -> {
                    if (!(value instanceof Integer)) {
                        throw new IllegalArgumentException("Years of experience must be an integer");
                    }
                    int years = (Integer) value;
                    if (years < 0) {
                        throw new IllegalArgumentException("Years of experience cannot be negative");
                    }
                    employee.setYearsOfExperience(years);
                }
                default -> throw new IllegalArgumentException("Invalid field: " + field);
            }
            LOGGER.log(Level.INFO, "Updated {0} for employee {1}", new Object[]{field, employeeId});
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid input for update", e);
            throw e;
        }
    }

    public List<Employee<T>> getAllEmployees() {
        LOGGER.log(Level.INFO, "Retrieved all employees. Count: {0}", employeeList.size());
        return new ArrayList<>(employeeList);
    }

    // Search and Filter Operations
    public List<Employee<T>> getEmployeesByDepartment(String department) throws InvalidDepartmentException {
        if (department == null || department.trim().isEmpty()) {
            LOGGER.log(Level.SEVERE, "Null or empty department provided");
            throw new InvalidDepartmentException("Department cannot be null or empty");
        }
        
        if (employeeList.isEmpty()) {
            LOGGER.log(Level.INFO, "Attempted to get employees from empty list for department: {0}", department);
            return new ArrayList<>();
        }
        
        try {
            List<Employee<T>> result = employeeList.stream()
                    .filter(Objects::nonNull)
                    .filter(e -> {
                        String dept = e.getDepartment();
                        return dept != null && dept.equalsIgnoreCase(department);
                    })
                    .collect(Collectors.toList());
                    
            LOGGER.log(Level.INFO, "Found {0} employees in department: {1}", 
                new Object[]{result.size(), department});
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting employees by department", e);
            throw new RuntimeException("Failed to get employees by department", e);
        }
    }

    public List<Employee<T>> searchEmployeesByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            LOGGER.log(Level.SEVERE, "Empty search term provided");
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }
        
        if (employeeList.isEmpty()) {
            LOGGER.log(Level.INFO, "Attempted to search in empty employee list");
            return new ArrayList<>();
        }
        
        try {
            List<Employee<T>> result = employeeList.stream()
                    .filter(Objects::nonNull)
                    .filter(e -> {
                        String name = e.getName();
                        return name != null && name.toLowerCase().contains(searchTerm.toLowerCase());
                    })
                    .collect(Collectors.toList());
                    
            LOGGER.log(Level.INFO, "Found {0} employees matching search term: {1}", 
                      new Object[]{result.size(), searchTerm});
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching employees by name", e);
            throw new RuntimeException("Failed to search employees by name", e);
        }
    }

    public List<Employee<T>> getHighPerformingEmployees(double minRating) {
        if (minRating < 0 || minRating > 5) {
            LOGGER.log(Level.SEVERE, "Invalid minimum rating: {0}", minRating);
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        List<Employee<T>> result = employeeList.stream()
                .filter(e -> e.getPerformanceRating() >= minRating)
                .collect(Collectors.toList());
        LOGGER.log(Level.INFO, "Found {0} high-performing employees (rating >= {1})", 
                  new Object[]{result.size(), minRating});
        return result;
    }

    public List<Employee<T>> getEmployeesInSalaryRange(double minSalary, double maxSalary) 
            throws InvalidSalaryException {
        if (minSalary < 0 || maxSalary < 0) {
            LOGGER.log(Level.SEVERE, "Negative salary range provided: min={0}, max={1}", 
                new Object[]{minSalary, maxSalary});
            throw new InvalidSalaryException("Salary range cannot be negative");
        }
        
        if (minSalary > maxSalary) {
            LOGGER.log(Level.SEVERE, "Invalid salary range: min={0}, max={1}", 
                new Object[]{minSalary, maxSalary});
            throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
        }
        
        return employees.values().stream()
            .filter(e -> e.getSalary() >= minSalary && e.getSalary() <= maxSalary)
            .collect(Collectors.toList());
    }

    // Sorting Operations
    public List<Employee<T>> sortByExperience() {
        if (employeeList.isEmpty()) {
            LOGGER.log(Level.INFO, "Attempted to sort empty employee list by experience");
            return new ArrayList<>();
        }
        
        try {
            List<Employee<T>> result = employeeList.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.nullsLast(Comparator.naturalOrder()))
                    .collect(Collectors.toList());
            LOGGER.log(Level.INFO, "Sorted {0} employees by experience", result.size());
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sorting employees by experience", e);
            throw new RuntimeException("Failed to sort employees by experience", e);
        }
    }

    public List<Employee<T>> sortBySalary() {
        if (employeeList.isEmpty()) {
            LOGGER.log(Level.INFO, "Attempted to sort empty employee list by salary");
            return new ArrayList<>();
        }
        
        try {
            List<Employee<T>> result = employeeList.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.nullsLast(
                        Comparator.comparing(
                            Employee::getSalary,
                            Comparator.nullsLast(Comparator.naturalOrder())
                        )
                    ))
                    .collect(Collectors.toList());
            LOGGER.log(Level.INFO, "Sorted {0} employees by salary", result.size());
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sorting employees by salary", e);
            throw new RuntimeException("Failed to sort employees by salary", e);
        }
    }

    public List<Employee<T>> sortByPerformance() {
        if (employeeList.isEmpty()) {
            LOGGER.log(Level.INFO, "Attempted to sort empty employee list by performance");
            return new ArrayList<>();
        }
        
        try {
            List<Employee<T>> result = employeeList.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.nullsLast(
                        Comparator.comparing(
                            Employee::getPerformanceRating,
                            Comparator.nullsLast(Comparator.naturalOrder())
                        )
                    ))
                    .collect(Collectors.toList());
            LOGGER.log(Level.INFO, "Sorted {0} employees by performance", result.size());
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sorting employees by performance", e);
            throw new RuntimeException("Failed to sort employees by performance", e);
        }
    }

    // Salary Management
    public void giveSalaryRaise(double percentage, double minRating) throws InvalidSalaryException {
        if (percentage < 0) {
            LOGGER.log(Level.SEVERE, "Negative percentage provided for salary raise: {0}", percentage);
            throw new IllegalArgumentException("Percentage cannot be negative");
        }
        
        if (minRating < 0 || minRating > 5) {
            LOGGER.log(Level.SEVERE, "Invalid minimum rating: {0}", minRating);
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        
        employees.values().stream()
            .filter(e -> e.getPerformanceRating() >= minRating)
            .forEach(e -> {
                try {
                    double newSalary = e.getSalary() * (1 + percentage/100);
                    e.setSalary(Math.round(newSalary * 100.0) / 100.0);
                } catch (InvalidSalaryException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to update salary for employee {0}", e.getEmployeeId());
                    throw new RuntimeException("Failed to update salary", ex);
                }
            });
            
        LOGGER.log(Level.INFO, "Applied {0}% salary raise to employees with rating >= {1}", 
            new Object[]{percentage, minRating});
    }

    public List<Employee<T>> getTopPaidEmployees(int count) {
        if (count <= 0) {
            LOGGER.log(Level.SEVERE, "Invalid count for top paid employees: {0}", count);
            throw new IllegalArgumentException("Count must be positive");
        }
        List<Employee<T>> result = employeeList.stream()
                .sorted((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()))
                .limit(count)
                .collect(Collectors.toList());
        LOGGER.log(Level.INFO, "Retrieved top {0} paid employees", count);
        return result;
    }

    public double getAverageDepartmentSalary(String department) throws InvalidDepartmentException {
        if (department == null || department.trim().isEmpty()) {
            LOGGER.log(Level.SEVERE, "Null or empty department provided");
            throw new InvalidDepartmentException("Department cannot be null or empty");
        }
        
        List<Employee<T>> deptEmployees = getEmployeesByDepartment(department);
        if (deptEmployees.isEmpty()) {
            LOGGER.log(Level.INFO, "No employees found in department: {0}", department);
            return 0.0;
        }
        
        double average = deptEmployees.stream()
            .mapToDouble(Employee::getSalary)
            .average()
            .orElse(0.0);
            
        LOGGER.log(Level.INFO, "Average salary for department {0}: {1}", 
            new Object[]{department, average});
        return Math.round(average * 100.0) / 100.0;
    }
} 