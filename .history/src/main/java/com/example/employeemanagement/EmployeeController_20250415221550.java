package com.example.employeemanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML
    public void initialize() {
        // Set up table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().getEmployeeId().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        departmentColumn.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        salaryColumn.setCellValueFactory(cellData -> cellData.getValue().salaryProperty().asObject());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().performanceRatingProperty().asObject());
        experienceColumn.setCellValueFactory(cellData -> cellData.getValue().yearsOfExperienceProperty().asObject());

        employeeTable.setItems(employeeList);
    }

    @FXML
    private void handleAddEmployee() {
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
        // In a real application, you would have a way to select which field to update
        // For simplicity, we'll just use the first non-empty field
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