package com.example.employeemanagement;

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
import java.util.stream.Collectors;

public class EmployeeController {
    private final EmployeeDatabase<Integer> employeeDatabase = new EmployeeDatabase<>();
    private final ObservableList<Employee<Integer>> employeeList = FXCollections.observableArrayList();

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

        try {
            Integer id = Integer.parseInt(employeeIdField.getText());
            
            // Check if employee with this ID already exists
            if (employeeDatabase.getEmployee(id) != null) {
                idErrorLabel.setText("Employee with this ID already exists");
                return;
            }
            
            String name = nameField.getText();
            String department = departmentField.getValue();
            double salary = Double.parseDouble(salaryField.getText());
            double rating = ratingField.getValue() != null ? ratingField.getValue() : 0;
            int experience = Integer.parseInt(experienceField.getText());

            Employee<Integer> employee = new Employee<>(id, name, department, salary, rating, experience);
            employeeDatabase.addEmployee(employee);
            refreshEmployeeList();
            clearFields();
            clearErrorLabels(); // Clear all error messages after successful addition
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for ID, salary, rating, and experience.");
        }
    }

    @FXML
    private void handleRemoveEmployee() {
        if (employeeIdField.getText().isEmpty()) {
            showAlert("Error", "Please enter an employee ID to remove");
            return;
        }

        try {
            Integer id = Integer.parseInt(employeeIdField.getText());
            employeeDatabase.removeEmployee(id);
            refreshEmployeeList();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid employee ID.");
        }
    }

    @FXML
    private void handleUpdateEmployee() {
        // Validate ID first
        validateId(employeeIdField.getText());
        
        if (idErrorLabel.getText().isEmpty()) {
            try {
                Integer id = Integer.parseInt(employeeIdField.getText());
                
                // Check if employee with this ID exists
                if (employeeDatabase.getEmployee(id) == null) {
                    idErrorLabel.setText("No employee found with this ID");
                    return;
                }
                
                // Validate only the fields that are being updated
                String field = getSelectedField();
                if (field == null) {
                    showAlert("Update Error", "Please enter a value in at least one field to update");
                    return;
                }
                
                switch (field) {
                    case "name" -> validateName(nameField.getText());
                    case "department" -> validateDepartment(departmentField.getValue());
                    case "salary" -> validateSalary(salaryField.getText());
                    case "performancerating" -> validateRating(ratingField.getValue() != null ? ratingField.getValue().toString() : "");
                    case "yearsofexperience" -> validateExperience(experienceField.getText());
                }
                
                if (!isFormValid()) {
                    showAlert("Validation Error", "Please fix the errors in the form");
                    return;
                }
                
                Object newValue = getNewValue(field);
                if (newValue != null) {
                    employeeDatabase.updateEmployeeDetails(id, field, newValue);
                    refreshEmployeeList();
                    clearFields();
                    clearErrorLabels();
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter valid values for the fields.");
            }
        } else {
            showAlert("Validation Error", "Please fix the errors in the ID field");
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
        String searchTerm = searchField.getText();
        String department = departmentFilterField.getValue();

        if (!searchTerm.isEmpty() && department != null && !department.equals("All Departments")) {
            employeeList.setAll(employeeDatabase.searchEmployeesByName(searchTerm)
                    .stream()
                    .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                    .toList());
        } else if (!searchTerm.isEmpty()) {
            employeeList.setAll(employeeDatabase.searchEmployeesByName(searchTerm));
        } else if (department != null && !department.equals("All Departments")) {
            employeeList.setAll(employeeDatabase.getEmployeesByDepartment(department));
        } else {
            refreshEmployeeList();
        }
    }

    @FXML
    private void handleSortByExperience() {
        employeeList.setAll(employeeDatabase.sortByExperience());
    }

    @FXML
    private void handleSortBySalary() {
        employeeList.setAll(employeeDatabase.sortBySalary());
    }

    @FXML
    private void handleSortByPerformance() {
        employeeList.setAll(employeeDatabase.sortByPerformance());
    }

    @FXML
    private void handleRaiseSalary() {
        if (employeeIdField.getText().isEmpty()) {
            showAlert("Error", "Please enter an employee ID to apply raise");
            return;
        }

        try {
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
            analyticsOutput.setText("No employees found" + 
                (selectedDept != null ? " in department: " + selectedDept : ""));
            return;
        }
        
        double average = employees.stream()
            .mapToDouble(Employee::getSalary)
            .average()
            .orElse(0.0);
        
        String output = String.format("Average Salary%s: $%.2f",
            selectedDept != null ? " in " + selectedDept : "",
            average);
        
        analyticsOutput.setText(output);
    }
}