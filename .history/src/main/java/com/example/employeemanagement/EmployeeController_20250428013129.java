package com.example.employeemanagement;

import com.example.employeemanagement.Exception.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EmployeeController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeController.class.getName());
    private final EmployeeDatabase<Integer> employeeDatabase = new EmployeeDatabase<>();
    private final ObservableList<Employee<Integer>> employeeList = FXCollections.observableArrayList();

    @FXML private TextField nameField;
    @FXML private ComboBox<String> departmentField;
    @FXML private TextField salaryField;
    @FXML private ComboBox<Integer> ratingField;
    @FXML private TextField experienceField;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> departmentFilterField;
    @FXML private TableView<Employee<Integer>> employeeTable;
    @FXML private TableColumn<Employee<Integer>, Integer> idColumn;
    @FXML private TableColumn<Employee<Integer>, String> nameColumn;
    @FXML private TableColumn<Employee<Integer>, String> departmentColumn;
    @FXML private TableColumn<Employee<Integer>, Double> salaryColumn;
    @FXML private TableColumn<Employee<Integer>, Double> ratingColumn;
    @FXML private TableColumn<Employee<Integer>, Integer> experienceColumn;
    
    // Error labels
    @FXML private Label nameErrorLabel;
    @FXML private Label departmentErrorLabel;
    @FXML private Label salaryErrorLabel;
    @FXML private Label ratingErrorLabel;
    @FXML private Label experienceErrorLabel;

    // List of departments
    private final List<String> departments = Arrays.asList(
        "HR", "IT", "Finance", "Marketing", "Operations", "Sales"
    );
    
    @FXML private ComboBox<String> departmentAnalyticsField;
    @FXML private TextArea analyticsOutput;

    @FXML
    public void initialize() {
        try {
            setupTableColumns();
            setupComboBoxes();
            setupValidation();
            refreshEmployeeList();
            LOGGER.log(Level.INFO, "EmployeeController initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing EmployeeController", e);
            showAlert("Initialization Error", "Failed to initialize the application: " + e.getMessage());
        }
    }
    
    private void setupComboBoxes() {
        try {
            // Setup department ComboBox
            departmentField.setItems(FXCollections.observableArrayList(departments));
            
            // Setup rating ComboBox (1-5)
            ratingField.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
            
            // Setup department filter ComboBox
            departmentFilterField.setItems(FXCollections.observableArrayList(departments));
            departmentFilterField.getItems().add(0, "All Departments");
            departmentFilterField.setValue("All Departments");
            
            LOGGER.log(Level.INFO, "ComboBoxes setup completed");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up ComboBoxes", e);
            throw e;
        }
    }

    private void setupTableColumns() {
        try {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
            salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
            ratingColumn.setCellValueFactory(new PropertyValueFactory<>("performanceRating"));
            ratingColumn.setCellFactory(column -> new TableCell<Employee<Integer>, Double>() {
                @Override
                protected void updateItem(Double rating, boolean empty) {
                    super.updateItem(rating, empty);
                    if (empty || rating == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        int fullStars = rating.intValue();
                        boolean halfStar = (rating - fullStars) >= 0.5;
                        StringBuilder stars = new StringBuilder();
                        for (int i = 0; i < fullStars; i++) stars.append("★");
                        if (halfStar) stars.append("☆");
                        for (int i = fullStars + (halfStar ? 1 : 0); i < 5; i++) stars.append("☆");
                        setText(stars.toString() + " (" + String.format("%.1f", rating) + ")");
                    }
                }
            });
            experienceColumn.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));

            employeeTable.setItems(employeeList);
            LOGGER.log(Level.INFO, "Table columns setup completed");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up table columns", e);
            throw e;
        }
    }

    private void setupValidation() {
        try {
            nameField.textProperty().addListener((obs, oldVal, newVal) -> validateName(newVal));
            departmentField.valueProperty().addListener((obs, oldVal, newVal) -> validateDepartment(newVal));
            salaryField.textProperty().addListener((obs, oldVal, newVal) -> validateSalary(newVal));
            ratingField.valueProperty().addListener((obs, oldVal, newVal) -> validateRating(newVal != null ? newVal.toString() : ""));
            experienceField.textProperty().addListener((obs, oldVal, newVal) -> validateExperience(newVal));
            LOGGER.log(Level.INFO, "Validation setup completed");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up validation", e);
            throw e;
        }
    }

    private void validateName(String value) {
        if (value.isEmpty()) {
            nameErrorLabel.setText("Name is required");
            LOGGER.log(Level.WARNING, "Empty name provided");
        } else if (value.length() < 2) {
            nameErrorLabel.setText("Name must be at least 2 characters");
            LOGGER.log(Level.WARNING, "Name too short: {0}", value);
        } else {
            nameErrorLabel.setText("");
        }
    }

    private void validateDepartment(String value) {
        if (value == null || value.isEmpty()) {
            departmentErrorLabel.setText("Department is required");
            LOGGER.log(Level.WARNING, "Empty department provided");
        } else if (!departments.contains(value)) {
            departmentErrorLabel.setText("Invalid department");
            LOGGER.log(Level.WARNING, "Invalid department: {0}", value);
        } else {
            departmentErrorLabel.setText("");
        }
    }

    private void validateSalary(String value) {
        try {
            if (value.isEmpty()) {
                salaryErrorLabel.setText("Salary is required");
                return;
            }
            double salary = Double.parseDouble(value);
            if (salary <= 0) {
                salaryErrorLabel.setText("Salary must be positive");
                LOGGER.log(Level.WARNING, "Invalid salary amount: {0}", salary);
            } else {
                salaryErrorLabel.setText("");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid salary format: {0}", value);
            salaryErrorLabel.setText("Salary must be a number");
        }
    }

    private void validateRating(String value) {
        try {
            if (value == null || value.isEmpty()) {
                ratingErrorLabel.setText("Rating is required");
                return;
            }
            double rating = Double.parseDouble(value);
            if (rating < 0 || rating > 5) {
                ratingErrorLabel.setText("Rating must be between 0 and 5");
                LOGGER.log(Level.WARNING, "Invalid rating value: {0}", rating);
            } else {
                ratingErrorLabel.setText("");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid rating format: {0}", value);
            ratingErrorLabel.setText("Rating must be a number");
        }
    }

    private void validateExperience(String value) {
        try {
            if (value.isEmpty()) {
                experienceErrorLabel.setText("Experience is required");
                return;
            }
            int experience = Integer.parseInt(value);
            if (experience < 0) {
                experienceErrorLabel.setText("Experience must be positive");
                LOGGER.log(Level.WARNING, "Invalid experience value: {0}", experience);
            } else {
                experienceErrorLabel.setText("");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid experience format: {0}", value);
            experienceErrorLabel.setText("Experience must be a number");
        }
    }

    private boolean isFormValid() {
        return nameErrorLabel.getText().isEmpty() &&
               departmentErrorLabel.getText().isEmpty() &&
               salaryErrorLabel.getText().isEmpty() &&
               ratingErrorLabel.getText().isEmpty() &&
               experienceErrorLabel.getText().isEmpty();
    }

    @FXML
    private void handleAddEmployee() {
        try {
            if (!isFormValid()) {
                LOGGER.log(Level.WARNING, "Form validation failed");
                return;
            }

            String name = nameField.getText();
            String department = departmentField.getValue();
            double salary = Double.parseDouble(salaryField.getText());
            int rating = ratingField.getValue() != null ? ratingField.getValue() : 0;
            int experience = experienceField.getText().isEmpty() ? 0 : Integer.parseInt(experienceField.getText());

            Employee<Integer> employee = new Employee<>(name, department, salary);
            employee.setPerformanceRating(rating);
            employee.setYearsOfExperience(experience);

            employeeDatabase.addEmployee(employee);
            refreshEmployeeList();
            clearFields();
            clearErrorLabels();
            showSuccessAlert("Success", "Employee added successfully with ID: " + employee.getEmployeeId());
            LOGGER.log(Level.INFO, "Added new employee with auto-generated ID: {0}", employee.getEmployeeId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding employee", e);
            showAlert("Error", "Failed to add employee: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveEmployee() {
        try {
            Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                LOGGER.log(Level.WARNING, "No employee selected for removal");
                showAlert("Error", "Please select an employee to remove");
                return;
            }

            employeeDatabase.removeEmployee(selectedEmployee.getEmployeeId());
            LOGGER.log(Level.INFO, "Removed employee with ID: {0}", selectedEmployee.getEmployeeId());
            refreshEmployeeList();
            showSuccessAlert("Success", "Employee removed successfully");
            
        } catch (EmployeeNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Employee not found", e);
            showAlert("Error", "Employee not found: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing employee", e);
            showAlert("Error", "Failed to remove employee: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateEmployee() {
        try {
            Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                LOGGER.log(Level.WARNING, "No employee selected for update");
                showAlert("Error", "Please select an employee to update");
                return;
            }

            String field = getSelectedField();
            if (field == null) {
                LOGGER.log(Level.WARNING, "No field selected for update");
                showAlert("Error", "Please select a field to update");
                return;
            }

            Object newValue = getNewValue(field);
            if (newValue == null) {
                LOGGER.log(Level.WARNING, "Invalid value for field: {0}", field);
                showAlert("Error", "Please enter a valid value");
                return;
            }

            employeeDatabase.updateEmployeeDetails(selectedEmployee.getEmployeeId(), field, newValue);
            LOGGER.log(Level.INFO, "Updated employee {0}, field: {1}, value: {2}", 
                      new Object[]{selectedEmployee.getEmployeeId(), field, newValue});
            refreshEmployeeList();
            showSuccessAlert("Success", "Employee updated successfully");
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid input for update", e);
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating employee", e);
            showAlert("Error", "Failed to update employee: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String searchTerm = searchField.getText().trim();
            String department = departmentFilterField.getValue();
            
            List<Employee<Integer>> searchResults;
            if (department.equals("All Departments")) {
                searchResults = employeeDatabase.searchEmployeesByName(searchTerm);
            } else {
                searchResults = employeeDatabase.getEmployeesByDepartment(department).stream()
                    .filter(e -> e.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
            }
            
            employeeList.setAll(searchResults);
            LOGGER.log(Level.INFO, "Search completed. Found {0} results", searchResults.size());
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid search parameters", e);
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during search", e);
            showAlert("Error", "Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortByExperience() {
        try {
            List<Employee<Integer>> sorted = employeeDatabase.sortByExperience();
            employeeList.setAll(sorted);
            LOGGER.log(Level.INFO, "Sorted employees by experience");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sorting by experience", e);
            showAlert("Error", "Failed to sort employees: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortBySalary() {
        try {
            List<Employee<Integer>> sorted = employeeDatabase.sortBySalary();
            employeeList.setAll(sorted);
            LOGGER.log(Level.INFO, "Sorted employees by salary");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sorting by salary", e);
            showAlert("Error", "Failed to sort employees: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortByPerformance() {
        try {
            List<Employee<Integer>> sorted = employeeDatabase.sortByPerformance();
            employeeList.setAll(sorted);
            LOGGER.log(Level.INFO, "Sorted employees by performance");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sorting by performance", e);
            showAlert("Error", "Failed to sort employees: " + e.getMessage());
        }
    }

    @FXML
    private void handleRaiseSalary() {
        try {
            Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                LOGGER.log(Level.WARNING, "No employee selected for salary raise");
                showAlert("Error", "Please select an employee");
                return;
            }

            double currentRating = selectedEmployee.getPerformanceRating();
            if (currentRating < 0 || currentRating > 5) {
                LOGGER.log(Level.WARNING, "Invalid performance rating: {0}", currentRating);
                showAlert("Error", "Invalid performance rating");
                return;
            }

            double raisePercentage = calculateRaisePercentage(currentRating);
            if (raisePercentage < 0) {
                LOGGER.log(Level.WARNING, "Invalid raise percentage: {0}", raisePercentage);
                showAlert("Error", "Invalid raise percentage");
                return;
            }
            
            employeeDatabase.giveSalaryRaise(raisePercentage, currentRating);
            LOGGER.log(Level.INFO, "Applied {0}% salary raise to employee {1}", 
                      new Object[]{raisePercentage, selectedEmployee.getEmployeeId()});
            
            refreshEmployeeList();
            showSuccessAlert("Success", 
                String.format("Salary raised by %.1f%% based on performance rating", raisePercentage));
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid input for salary raise", e);
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error applying salary raise", e);
            showAlert("Error", "Failed to apply salary raise: " + e.getMessage());
        }
    }

    private double calculateRaisePercentage(double rating) {
        if (rating >= 4.5) return 10.0;
        if (rating >= 4.0) return 7.5;
        if (rating >= 3.5) return 5.0;
        if (rating >= 3.0) return 2.5;
        return 0.0;
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void refreshEmployeeList() {
        employeeList.setAll(employeeDatabase.getAllEmployees());
        LOGGER.log(Level.INFO, "Employee list refreshed. Total employees: {0}", employeeList.size());
    }

    private void clearFields() {
        nameField.clear();
        departmentField.setValue(null);
        salaryField.clear();
        ratingField.setValue(null);
        experienceField.clear();
    }

    private void clearErrorLabels() {
        nameErrorLabel.setText("");
        departmentErrorLabel.setText("");
        salaryErrorLabel.setText("");
        ratingErrorLabel.setText("");
        experienceErrorLabel.setText("");
    }

    private String getSelectedField() {
        // Get the selected field from the UI
        if (salaryField.isFocused()) return "salary";
        if (departmentField.isFocused()) return "department";
        if (ratingField.isFocused()) return "performanceRating";
        if (experienceField.isFocused()) return "yearsOfExperience";
        return null;
    }

    private Object getNewValue(String field) throws InvalidSalaryException, InvalidDepartmentException {
        try {
            switch (field.toLowerCase()) {
                case "salary" -> {
                    String salaryText = salaryField.getText().trim();
                    if (salaryText.isEmpty()) {
                        throw new IllegalArgumentException("Salary cannot be empty");
                    }
                    double value = Double.parseDouble(salaryText);
                    if (value < 0) {
                        throw new InvalidSalaryException("Salary cannot be negative");
                    }
                    return Math.round(value * 100.0) / 100.0; // Round to 2 decimal places
                }
                case "department" -> {
                    String dept = departmentField.getValue();
                    if (dept == null || dept.trim().isEmpty()) {
                        throw new IllegalArgumentException("Department cannot be empty");
                    }
                    if (!departments.contains(dept)) {
                        throw new InvalidDepartmentException("Invalid department: " + dept);
                    }
                    return dept;
                }
                case "performancerating" -> {
                    Integer rating = ratingField.getValue();
                    if (rating == null) {
                        throw new IllegalArgumentException("Rating cannot be empty");
                    }
                    if (rating < 0 || rating > 5) {
                        throw new IllegalArgumentException("Rating must be between 0 and 5");
                    }
                    return rating.doubleValue();
                }
                case "yearsofexperience" -> {
                    String expText = experienceField.getText().trim();
                    if (expText.isEmpty()) {
                        throw new IllegalArgumentException("Experience cannot be empty");
                    }
                    int value = Integer.parseInt(expText);
                    if (value < 0) {
                        throw new IllegalArgumentException("Experience cannot be negative");
                    }
                    return value;
                }
                default -> throw new IllegalArgumentException("Invalid field: " + field);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format for field: {0}", field);
            throw new IllegalArgumentException("Please enter a valid number");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
        LOGGER.log(Level.WARNING, "Alert shown: {0} - {1}", new Object[]{title, content});
    }

    @FXML
    private void handleShowTopPaid() {
        try {
            List<Employee<Integer>> topPaid = employeeDatabase.getTopPaidEmployees(5);
            StringBuilder result = new StringBuilder("Top 5 Paid Employees:\n\n");
            for (Employee<Integer> emp : topPaid) {
                result.append(String.format("%s - $%.2f\n", emp.getName(), emp.getSalary()));
            }
            analyticsOutput.setText(result.toString());
            LOGGER.log(Level.INFO, "Displayed top paid employees");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing top paid employees", e);
            showAlert("Error", "Failed to get top paid employees: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowAverageSalary() {
        try {
            String department = departmentAnalyticsField.getValue();
            if (department == null || department.isEmpty()) {
                LOGGER.log(Level.WARNING, "No department selected for average salary");
                showAlert("Error", "Please select a department");
                return;
            }

            double avgSalary = employeeDatabase.getAverageDepartmentSalary(department);
            analyticsOutput.setText(String.format("Average Salary in %s: $%.2f", department, avgSalary));
            LOGGER.log(Level.INFO, "Displayed average salary for department: {0}", department);
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid department in average salary calculation", e);
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating average salary", e);
            showAlert("Error", "Failed to calculate average salary: " + e.getMessage());
        }
    }
}