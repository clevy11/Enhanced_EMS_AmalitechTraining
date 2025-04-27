package com.example.employeemanagement;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.logging.Logger;

public class EmployeeDetailsController {
    private static final Logger logger = Logger.getLogger(EmployeeDetailsController.class.getName());
    private Employee<Integer> employee;
    private Stage stage;

    @FXML private Label idLabel;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> departmentField;
    @FXML private TextField salaryField;
    @FXML private ComboBox<Integer> ratingField;
    @FXML private TextField experienceField;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button raiseSalaryButton;
    @FXML private Button closeButton;

    @FXML
    public void initialize() {
        // Setup rating options
        ratingField.getItems().addAll(1, 2, 3, 4, 5);
        
        // Setup department options
        departmentField.getItems().addAll(
            "HR", "IT", "Finance", "Marketing", "Operations", 
            "Sales", "Customer Service", "Research & Development", "Legal", "Administration"
        );
    }

    public void setEmployee(Employee<Integer> employee) {
        this.employee = employee;
        updateFields();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void updateFields() {
        if (employee != null) {
            idLabel.setText("ID: " + employee.getEmployeeId());
            nameField.setText(employee.getName());
            departmentField.setValue(employee.getDepartment());
            salaryField.setText(String.format("%.2f", employee.getSalary()));
            ratingField.setValue((int) employee.getPerformanceRating());
            experienceField.setText(String.valueOf(employee.getYearsOfExperience()));
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            String name = nameField.getText();
            String department = departmentField.getValue();
            double salary = Double.parseDouble(salaryField.getText());
            double rating = ratingField.getValue() != null ? ratingField.getValue() : 0;
            int experience = Integer.parseInt(experienceField.getText());

            employee.setName(name);
            employee.setDepartment(department);
            employee.setSalary(salary);
            employee.setPerformanceRating(rating);
            employee.setYearsOfExperience(experience);

            logger.info("Updated employee: " + employee);
            showSuccessAlert("Success", "Employee updated successfully");
            closeWindow();
        } catch (InvalidSalaryException e) {
            logger.warning("Invalid salary: " + e.getMessage());
            showAlert("Error", e.getMessage());
        } catch (InvalidDepartmentException e) {
            logger.warning("Invalid department: " + e.getMessage());
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            logger.severe("Error updating employee: " + e.getMessage());
            showAlert("Error", "Failed to update employee: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        try {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText(null);
            confirmDialog.setContentText("Are you sure you want to delete this employee?");
            
            if (confirmDialog.showAndWait().get() == ButtonType.OK) {
                employee.setActive(false);
                logger.info("Deleted employee: " + employee);
                showSuccessAlert("Success", "Employee deleted successfully");
                closeWindow();
            }
        } catch (Exception e) {
            logger.severe("Error deleting employee: " + e.getMessage());
            showAlert("Error", "Failed to delete employee: " + e.getMessage());
        }
    }

    @FXML
    private void handleRaiseSalary() {
        try {
            double currentSalary = employee.getSalary();
            double raisePercentage = calculateRaisePercentage(employee.getPerformanceRating());
            double newSalary = currentSalary * (1 + raisePercentage / 100);
            
            employee.setSalary(newSalary);
            salaryField.setText(String.format("%.2f", newSalary));
            
            logger.info("Raised salary for employee " + employee.getEmployeeId() + 
                       " from " + currentSalary + " to " + newSalary);
            showSuccessAlert("Success", "Salary raised by " + String.format("%.1f", raisePercentage) + "%");
        } catch (InvalidSalaryException e) {
            logger.warning("Invalid salary after raise: " + e.getMessage());
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            logger.severe("Error raising salary: " + e.getMessage());
            showAlert("Error", "Failed to raise salary: " + e.getMessage());
        }
    }

    private double calculateRaisePercentage(double rating) {
        if (rating >= 4.5) return 10.0;
        if (rating >= 4.0) return 7.5;
        if (rating >= 3.5) return 5.0;
        if (rating >= 3.0) return 2.5;
        return 0.0;
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 