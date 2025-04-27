package com.example.employeemanagement;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AddEmployeeController {
    private static final Logger LOGGER = Logger.getLogger(AddEmployeeController.class.getName());
    private EmployeeDatabase employeeDatabase;
    private Stage dialogStage;

    @FXML
    private TextField newNameField;
    @FXML
    private TextField newDepartmentField;
    @FXML
    private TextField newSalaryField;

    public void setEmployeeDatabase(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleSave() {
        try {
            String name = newNameField.getText().trim();
            String department = newDepartmentField.getText().trim();
            double salary = Double.parseDouble(newSalaryField.getText().trim());

            // Create new employee (ID will be auto-generated)
            Employee newEmployee = new Employee(name, department, salary, 0.0, 0);
            employeeDatabase.addEmployee(newEmployee);

            LOGGER.log(Level.INFO, "Added new employee: {0}", newEmployee);
            dialogStage.close();
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter a valid salary amount.");
            LOGGER.log(Level.SEVERE, "Invalid salary input", e);
        } catch (InvalidSalaryException e) {
            showError("Invalid Salary", e.getMessage());
            LOGGER.log(Level.SEVERE, "Invalid salary", e);
        } catch (InvalidDepartmentException e) {
            showError("Invalid Department", e.getMessage());
            LOGGER.log(Level.SEVERE, "Invalid department", e);
        } catch (Exception e) {
            showError("Error", "Failed to add employee: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Failed to add employee", e);
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 