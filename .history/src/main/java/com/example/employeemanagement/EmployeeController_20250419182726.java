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

public class EmployeeController {
    private final EmployeeDatabase<Integer> employeeDatabase = new EmployeeDatabase<>();
    private final ObservableList<Employee<Integer>> employeeList = FXCollections.observableArrayList();

    @FXML private TextField employeeIdField;
    @FXML private TextField nameField;
    @FXML private TextField departmentField;
    @FXML private TextField salaryField;
    @FXML private TextField ratingField;
    @FXML private TextField experienceField;
    @FXML private TextField searchField;
    @FXML private TextField departmentFilterField;
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

    @FXML
    public void initialize() {
        setupTableColumns();
        setupValidation();
        refreshEmployeeList();
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
        departmentField.textProperty().addListener((obs, oldVal, newVal) -> validateDepartment(newVal));
        salaryField.textProperty().addListener((obs, oldVal, newVal) -> validateSalary(newVal));
        ratingField.textProperty().addListener((obs, oldVal, newVal) -> validateRating(newVal));
        experienceField.textProperty().addListener((obs, oldVal, newVal) -> validateExperience(newVal));
    }

    private void validateId(String value) {
        if (value.isEmpty()) {
            idErrorLabel.setText("ID is required");
        } else {
            try {
                Integer.parseInt(value);
                idErrorLabel.setText("");
            } catch (NumberFormatException e) {
                idErrorLabel.setText("ID must be a number");
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
        if (value.isEmpty()) {
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
        if (value.isEmpty()) {
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
        if (!isFormValid()) {
            showAlert("Validation Error", "Please fix the errors in the form");
            return;
        }

        try {
            Integer id = Integer.parseInt(employeeIdField.getText());
            String name = nameField.getText();
            String department = departmentField.getText();
            double salary = Double.parseDouble(salaryField.getText());
            double rating = Double.parseDouble(ratingField.getText());
            int experience = Integer.parseInt(experienceField.getText());

            Employee<Integer> employee = new Employee<>(id, name, department, salary, rating, experience);
            employeeDatabase.addEmployee(employee);
            refreshEmployeeList();
            clearFields();
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
        if (!isFormValid()) {
            showAlert("Validation Error", "Please fix the errors in the form");
            return;
        }

        try {
            Integer id = Integer.parseInt(employeeIdField.getText());
            String field = getSelectedField();
            Object newValue = getNewValue(field);

            if (field != null && newValue != null) {
                employeeDatabase.updateEmployeeDetails(id, field, newValue);
                refreshEmployeeList();
                clearFields();
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid values for the fields.");
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
        String department = departmentFilterField.getText();

        if (!searchTerm.isEmpty() && !department.isEmpty()) {
            employeeList.setAll(employeeDatabase.searchEmployeesByName(searchTerm)
                    .stream()
                    .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                    .toList());
        } else if (!searchTerm.isEmpty()) {
            employeeList.setAll(employeeDatabase.searchEmployeesByName(searchTerm));
        } else if (!department.isEmpty()) {
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

    private void refreshEmployeeList() {
        employeeList.setAll(employeeDatabase.getAllEmployees());
    }

    private void clearFields() {
        employeeIdField.clear();
        nameField.clear();
        departmentField.clear();
        salaryField.clear();
        ratingField.clear();
        experienceField.clear();
    }

    private String getSelectedField() {
        if (!nameField.getText().isEmpty()) return "name";
        if (!departmentField.getText().isEmpty()) return "department";
        if (!salaryField.getText().isEmpty()) return "salary";
        if (!ratingField.getText().isEmpty()) return "performancerating";
        if (!experienceField.getText().isEmpty()) return "yearsofexperience";
        return null;
    }

    private Object getNewValue(String field) {
        if (field == null) return null;
        return switch (field) {
            case "name" -> nameField.getText();
            case "department" -> departmentField.getText();
            case "salary" -> Double.parseDouble(salaryField.getText());
            case "performancerating" -> Double.parseDouble(ratingField.getText());
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
}