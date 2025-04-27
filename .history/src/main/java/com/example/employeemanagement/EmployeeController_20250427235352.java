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
    private final EmployeeDatabase<Integer> employeeDatabase = new EmployeeDatabase<>();
    private final ObservableList<Employee<Integer>> employeeList = FXCollections.observableArrayList();
    private static final Logger logger = Logger.getLogger(EmployeeController.class.getName());

    @FXML private TextField employeeIdField;
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
    @FXML private Label idErrorLabel;
    @FXML private Label nameErrorLabel;
    @FXML private Label departmentErrorLabel;
    @FXML private Label salaryErrorLabel;
    @FXML private Label ratingErrorLabel;
    @FXML private Label experienceErrorLabel;

    // List of departments
    private final List<String> departments = Arrays.asList(
        "HR", "IT", "Finance", "Marketing", "Operations", 
        "Sales", "Customer Service", "Research & Development", "Legal", "Administration"
    );
    
    @FXML private ComboBox<String> departmentAnalyticsField;
    @FXML private TextArea analyticsOutput;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupValidation();
        refreshEmployeeList();
    }
    
    private void setupComboBoxes() {
        // Setup department ComboBox
        departmentField.setItems(FXCollections.observableArrayList(departments));
        
        // Setup rating ComboBox (1-5)
        ratingField.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        
        // Setup department filter ComboBox
        departmentFilterField.setItems(FXCollections.observableArrayList(departments));
        departmentFilterField.getItems().add(0, "All Departments");
        departmentFilterField.setValue("All Departments");
    }

    private void setupTableColumns() {
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
    }

    private void setupValidation() {
        // Add listeners to validate input fields
        employeeIdField.textProperty().addListener((obs, oldVal, newVal) -> validateId(newVal));
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateName(newVal));
        departmentField.valueProperty().addListener((obs, oldVal, newVal) -> validateDepartment(newVal));
        salaryField.textProperty().addListener((obs, oldVal, newVal) -> validateSalary(newVal));
        ratingField.valueProperty().addListener((obs, oldVal, newVal) -> validateRating(newVal != null ? newVal.toString() : ""));
        experienceField.textProperty().addListener((obs, oldVal, newVal) -> validateExperience(newVal));
    }

    private void validateId(String value) {
        if (value.isEmpty()) {
            idErrorLabel.setText("Employee ID is required");
        } else {
            try {
                Integer id = Integer.parseInt(value);
                if (id <= 0) {
                    idErrorLabel.setText("ID must be a positive number");
                } else {
                    idErrorLabel.setText("");
                }
            } catch (NumberFormatException e) {
                idErrorLabel.setText("ID must be a valid number");
            }
        }
    }

    private void validateName(String value) {
        if (value.isEmpty()) {
            nameErrorLabel.setText("Name is required");
        } else if (value.length() < 2) {
            nameErrorLabel.setText("Name must be at least 2 characters");
        } else {
            nameErrorLabel.setText("");
        }
    }

    private void validateDepartment(String value) {
        if (value == null || value.isEmpty()) {
            departmentErrorLabel.setText("Department is required");
        } else {
            departmentErrorLabel.setText("");
        }
    }

    private void validateSalary(String value) {
        if (value.isEmpty()) {
            salaryErrorLabel.setText("Salary is required");
        } else {
            try {
                double salary = Double.parseDouble(value);
                if (salary <= 0) {
                    salaryErrorLabel.setText("Salary must be positive");
                } else {
                    salaryErrorLabel.setText("");
                }
            } catch (NumberFormatException e) {
                salaryErrorLabel.setText("Salary must be a number");
            }
        }
    }

    private void validateRating(String value) {
        if (value == null || value.isEmpty()) {
            ratingErrorLabel.setText("Rating is required");
        } else {
            try {
                double rating = Double.parseDouble(value);
                if (rating < 0 || rating > 5) {
                    ratingErrorLabel.setText("Rating must be between 0 and 5");
                } else {
                    ratingErrorLabel.setText("");
                }
            } catch (NumberFormatException e) {
                ratingErrorLabel.setText("Rating must be a number");
            }
        }
    }

    private void validateExperience(String value) {
        if (value.isEmpty()) {
            experienceErrorLabel.setText("Experience is required");
        } else {
            try {
                int experience = Integer.parseInt(value);
                if (experience < 0) {
                    experienceErrorLabel.setText("Experience must be positive");
                } else {
                    experienceErrorLabel.setText("");
                }
            } catch (NumberFormatException e) {
                experienceErrorLabel.setText("Experience must be a number");
            }
        }
    }

    private boolean isFormValid() {
        return idErrorLabel.getText().isEmpty() &&
               nameErrorLabel.getText().isEmpty() &&
               departmentErrorLabel.getText().isEmpty() &&
               salaryErrorLabel.getText().isEmpty() &&
               ratingErrorLabel.getText().isEmpty() &&
               experienceErrorLabel.getText().isEmpty();
    }

    @FXML
    private void handleAddEmployee() {
        try {
            // Validate all fields first
            validateId(employeeIdField.getText());
            validateName(nameField.getText());
            validateDepartment(departmentField.getValue());
            validateSalary(salaryField.getText());
            validateRating(ratingField.getValue() != null ? ratingField.getValue().toString() : "");
            validateExperience(experienceField.getText());
            
            if (!isFormValid()) {
                showAlert("Validation Error", "Please fix the errors in the form");
                return;
            }

            Integer id = Integer.parseInt(employeeIdField.getText());
            String name = nameField.getText();
            String department = departmentField.getValue();
            double salary = Double.parseDouble(salaryField.getText());
            double rating = ratingField.getValue() != null ? ratingField.getValue() : 0;
            int experience = Integer.parseInt(experienceField.getText());

            Employee<Integer> employee = new Employee<>(id, name, department, salary, rating, experience);
            employeeDatabase.addEmployee(employee);
            
            logger.log(Level.INFO, "Employee added successfully: " + employee);
            
            refreshEmployeeList();
            clearFields();
            showSuccessAlert("Success", "Employee added successfully");
            
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for ID, salary, and experience");
            logger.log(Level.WARNING, "Invalid number format: " + e.getMessage());
        } catch (InvalidSalaryException e) {
            salaryErrorLabel.setText(e.getMessage());
            logger.log(Level.WARNING, "Invalid salary: " + e.getMessage());
        } catch (InvalidDepartmentException e) {
            departmentErrorLabel.setText(e.getMessage());
            logger.log(Level.WARNING, "Invalid department: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
            logger.log(Level.SEVERE, "Unexpected error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveEmployee() {
        try {
            Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                showAlert("Selection Error", "Please select an employee to remove");
                return;
            }

            employeeDatabase.removeEmployee(selectedEmployee.getEmployeeId());
            logger.log(Level.INFO, "Employee removed successfully: " + selectedEmployee.getEmployeeId());
            
            refreshEmployeeList();
            showSuccessAlert("Success", "Employee removed successfully");
            
        } catch (EmployeeNotFoundException e) {
            showAlert("Error", e.getMessage());
            logger.log(Level.WARNING, "Employee not found: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
            logger.log(Level.SEVERE, "Unexpected error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateEmployee() {
        try {
            Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
            if (selectedEmployee == null) {
                showAlert("Selection Error", "Please select an employee to update");
                return;
            }

            String field = getSelectedField();
            if (field == null) {
                showAlert("Selection Error", "Please select a field to update");
                return;
            }

            Object newValue = getNewValue(field);
            if (newValue == null) {
                showAlert("Input Error", "Please enter a valid value");
                return;
            }

            employeeDatabase.updateEmployeeDetails(
                selectedEmployee.getEmployeeId(),
                field,
                newValue
            );
            
            logger.log(Level.INFO, "Employee updated successfully: " + selectedEmployee.getEmployeeId());
            
            refreshEmployeeList();
            showSuccessAlert("Success", "Employee updated successfully");
            
        } catch (EmployeeNotFoundException e) {
            showAlert("Error", e.getMessage());
            logger.log(Level.WARNING, "Employee not found: " + e.getMessage());
        } catch (InvalidSalaryException e) {
            showAlert("Error", e.getMessage());
            logger.log(Level.WARNING, "Invalid salary: " + e.getMessage());
        } catch (InvalidDepartmentException e) {
            showAlert("Error", e.getMessage());
            logger.log(Level.WARNING, "Invalid department: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
            logger.log(Level.SEVERE, "Unexpected error: " + e.getMessage());
        }
    }

    @FXML
    private void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) employeeTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                refreshEmployeeList();
                return;
            }

            List<Employee<Integer>> searchResults = employeeDatabase.searchEmployeesByName(searchTerm);
            employeeList.setAll(searchResults);
            
            if (searchResults.isEmpty()) {
                showAlert("Search Results", "No employees found matching the search criteria");
            }
            
        } catch (Exception e) {
            showAlert("Error", "An error occurred during search: " + e.getMessage());
            logger.log(Level.SEVERE, "Search error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortByExperience() {
        try {
            List<Employee<Integer>> sortedList = employeeDatabase.sortByExperience();
            employeeList.setAll(sortedList);
        } catch (Exception e) {
            showAlert("Error", "An error occurred during sorting: " + e.getMessage());
            logger.log(Level.SEVERE, "Sorting error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortBySalary() {
        try {
            List<Employee<Integer>> sortedList = employeeDatabase.sortBySalary();
            employeeList.setAll(sortedList);
        } catch (Exception e) {
            showAlert("Error", "An error occurred during sorting: " + e.getMessage());
            logger.log(Level.SEVERE, "Sorting error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortByPerformance() {
        try {
            List<Employee<Integer>> sortedList = employeeDatabase.sortByPerformance();
            employeeList.setAll(sortedList);
        } catch (Exception e) {
            showAlert("Error", "An error occurred during sorting: " + e.getMessage());
            logger.log(Level.SEVERE, "Sorting error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRaiseSalary() {
        try {
            String department = departmentAnalyticsField.getValue();
            if (department == null || department.isEmpty()) {
                showAlert("Input Error", "Please select a department");
                return;
            }

            double averageSalary = employeeDatabase.getAverageDepartmentSalary(department);
            analyticsOutput.setText(String.format("Average salary in %s: $%.2f", department, averageSalary));
            
        } catch (Exception e) {
            showAlert("Error", "An error occurred while calculating average salary: " + e.getMessage());
            logger.log(Level.SEVERE, "Salary calculation error: " + e.getMessage());
        }
    }

    private void refreshEmployeeList() {
        try {
            employeeList.setAll(employeeDatabase.getAllEmployees());
        } catch (Exception e) {
            showAlert("Error", "An error occurred while refreshing the employee list: " + e.getMessage());
            logger.log(Level.SEVERE, "Refresh error: " + e.getMessage());
        }
            Integer id = Integer.parseInt(employeeIdField.getText());
            Employee<Integer> employee = employeeDatabase.getEmployee(id);
            
            if (employee == null) {
                showAlert("Error", "No employee found with this ID");
                return;
            }

            // Calculate raise based on performance rating
            double currentRating = employee.getPerformanceRating();
            double raisePercentage = calculateRaisePercentage(currentRating);
            double currentSalary = employee.getSalary();
            double newSalary = currentSalary * (1 + raisePercentage/100);

            // Update the employee's salary
            employeeDatabase.updateEmployeeDetails(id, "salary", newSalary);
            refreshEmployeeList();
            
            // Console output for salary raise
            System.out.println("\n=== Salary Raise Applied ===");
            System.out.printf("Employee: %s\nPrevious Salary: $%.2f\nRaise Percentage: %.1f%%\nNew Salary: $%.2f\n",
                employee.getName(), currentSalary, raisePercentage, newSalary);
            System.out.println("===========================\n");
            
            // Show success message
            showSuccessAlert("Salary Raise Applied", 
                String.format("Employee %s received a %.1f%% raise.\nNew salary: $%.2f", 
                    employee.getName(), raisePercentage, newSalary));
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid employee ID.");
        }
    }

    private double calculateRaisePercentage(double rating) {
        // Performance-based raise calculation
        if (rating >= 4.5) return 10.0;  // Outstanding
        if (rating >= 4.0) return 8.0;   // Excellent
        if (rating >= 3.5) return 6.0;   // Very Good
        if (rating >= 3.0) return 4.0;   // Good
        if (rating >= 2.0) return 2.0;   // Satisfactory
        return 0.0;                      // Needs Improvement
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
    }

    private void clearFields() {
        employeeIdField.clear();
        nameField.clear();
        departmentField.setValue(null);
        salaryField.clear();
        ratingField.setValue(null);
        experienceField.clear();
        departmentAnalyticsField.setValue(null);
    }
    
    private void clearErrorLabels() {
        idErrorLabel.setText("");
        nameErrorLabel.setText("");
        departmentErrorLabel.setText("");
        salaryErrorLabel.setText("");
        ratingErrorLabel.setText("");
        experienceErrorLabel.setText("");
    }

    private String getSelectedField() {
        if (!nameField.getText().isEmpty()) return "name";
        if (departmentField.getValue() != null) return "department";
        if (!salaryField.getText().isEmpty()) return "salary";
        if (ratingField.getValue() != null) return "performancerating";
        if (!experienceField.getText().isEmpty()) return "yearsofexperience";
        return null;
    }

    private Object getNewValue(String field) {
        if (field == null) return null;
        return switch (field) {
            case "name" -> nameField.getText();
            case "department" -> departmentField.getValue();
            case "salary" -> Double.parseDouble(salaryField.getText());
            case "performancerating" -> ratingField.getValue();
            case "yearsofexperience" -> Integer.parseInt(experienceField.getText());
            default -> null;
        };
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleShowTopPaid() {
        List<Employee<Integer>> employees = employeeDatabase.getAllEmployees();
        employees.sort((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()));
        
        StringBuilder output = new StringBuilder("Top 5 Paid Employees:\n\n");
        for (int i = 0; i < Math.min(5, employees.size()); i++) {
            Employee<Integer> emp = employees.get(i);
            output.append(String.format("%d. %s %s - $%.2f\n", 
                i + 1, emp.getName(), emp.getDepartment(), emp.getSalary()));
        }
        
        // Console output for top paid employees
        System.out.println("\n=== Top 5 Paid Employees ===");
        System.out.println(output.toString());
        System.out.println("===========================\n");
        
        analyticsOutput.setText(output.toString());
    }

    @FXML
    private void handleShowAverageSalary() {
        String selectedDept = departmentAnalyticsField.getValue();
        List<Employee<Integer>> employees = employeeDatabase.getAllEmployees();
        
        if (selectedDept != null && !selectedDept.isEmpty()) {
            employees = employees.stream()
                .filter(e -> e.getDepartment().equals(selectedDept))
                .collect(Collectors.toList());
        }
        
        if (employees.isEmpty()) {
            String message = "No employees found" + 
                (selectedDept != null ? " in department: " + selectedDept : "");
            System.out.println("\n=== Average Salary ===");
            System.out.println(message);
            System.out.println("=====================\n");
            analyticsOutput.setText(message);
            return;
        }
        
        double average = employees.stream()
            .mapToDouble(Employee::getSalary)
            .average()
            .orElse(0.0);
        
        String output = String.format("Average Salary%s: $%.2f",
            selectedDept != null ? " in " + selectedDept : "",
            average);
        
        // Console output for average salary
        System.out.println("\n=== Average Salary ===");
        System.out.println(output);
        System.out.println("=====================\n");
        
        analyticsOutput.setText(output);
    }
}