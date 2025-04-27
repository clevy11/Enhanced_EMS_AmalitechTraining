package com.example.employeemanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

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

    @FXML
    public void initialize() {
        try {
            setupTableColumns();
            setupValidation();
            refreshTable();
            LOGGER.log(Level.INFO, "EmployeeController initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing EmployeeController", e);
            showAlert("Initialization Error", "Failed to initialize the application: " + e.getMessage());
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
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
        LOGGER.log(Level.WARNING, "Alert shown: {0} - {1}", new Object[]{title, content});
    }
}