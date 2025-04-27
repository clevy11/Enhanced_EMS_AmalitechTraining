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
import java.util.Optional;

public class EmployeeController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeController.class.getName());
    private final EmployeeDatabase<Integer> database = new EmployeeDatabase<>();
    private final ObservableList<Employee<Integer>> employeeList = FXCollections.observableArrayList();

    @FXML private TextField nameField;
    @FXML private TextField departmentField;
    @FXML private TextField salaryField;
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
            refreshTable();
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
            departmentField.textProperty().addListener((obs, oldVal, newVal) -> validateDepartment(newVal));
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
        if (value == null || value.trim().isEmpty()) {
            showAlert("Validation Error", "Name cannot be empty");
        }
    }

    private void validateDepartment(String value) {
        if (value == null || value.trim().isEmpty()) {
            showAlert("Validation Error", "Department cannot be empty");
        }
    }

    private void validateSalary(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                showAlert("Validation Error", "Salary cannot be empty");
                return;
            }
            double salary = Double.parseDouble(value);
            if (salary < 0) {
                showAlert("Validation Error", "Salary cannot be negative");
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Salary must be a valid number");
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
    private void handleSave() {
        try {
            String name = nameField.getText();
            String department = departmentField.getText();
            double salary = Double.parseDouble(salaryField.getText());

            Employee<Integer> employee = new Employee<>(name, department, salary);
            database.addEmployee(employee);
            refreshTable();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid salary amount.");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            try {
                String name = nameField.getText();
                String department = departmentField.getText();
                double salary = Double.parseDouble(salaryField.getText());

                database.updateEmployeeDetails(selectedEmployee.getEmployeeId(), "name", name);
                database.updateEmployeeDetails(selectedEmployee.getEmployeeId(), "department", department);
                database.updateEmployeeDetails(selectedEmployee.getEmployeeId(), "salary", salary);

                refreshTable();
                clearFields();
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid salary amount.");
            } catch (Exception e) {
                showAlert("Error", e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select an employee to update.");
        }
    }

    @FXML
    private void handleDelete() {
        Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            try {
                database.removeEmployee(selectedEmployee.getEmployeeId());
                refreshTable();
                clearFields();
            } catch (Exception e) {
                showAlert("Error", e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select an employee to delete.");
        }
    }

    @FXML
    private void handleRaise() {
        Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            TextInputDialog dialog = new TextInputDialog("10");
            dialog.setTitle("Give Salary Raise");
            dialog.setHeaderText("Enter raise percentage");
            dialog.setContentText("Percentage:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(percentage -> {
                try {
                    double raisePercentage = Double.parseDouble(percentage);
                    database.giveSalaryRaise(raisePercentage, selectedEmployee.getPerformanceRating());
                    refreshTable();
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a valid percentage.");
                } catch (Exception e) {
                    showAlert("Error", e.getMessage());
                }
            });
        } else {
            showAlert("No Selection", "Please select an employee to give a raise.");
        }
    }

    private void refreshTable() {
        employeeList.clear();
        employeeList.addAll(database.getAllEmployees());
    }

    private void clearFields() {
        nameField.clear();
        departmentField.clear();
        salaryField.clear();
        clearErrorLabels();
        LOGGER.log(Level.INFO, "Input fields cleared");
    }

    private void clearErrorLabels() {
        nameErrorLabel.setText("");
        departmentErrorLabel.setText("");
        salaryErrorLabel.setText("");
        ratingErrorLabel.setText("");
        experienceErrorLabel.setText("");
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
            List<Employee<Integer>> topPaid = database.getTopPaidEmployees(5);
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

            double avgSalary = database.getAverageDepartmentSalary(department);
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