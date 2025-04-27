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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeController.class.getName());
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
        "HR", "IT", "Finance", "Marketing", "Operations", "Sales"
    );
    
    @FXML private ComboBox<String> departmentAnalyticsField;
    @FXML private TextArea analyticsOutput;

    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button raiseSalaryButton;

    @FXML
    public void initialize() {
        try {
            setupTableColumns();
            setupComboBoxes();
            setupValidation();
            refreshEmployeeList();
            // Initially hide action buttons
            updateButton.setVisible(false);
            deleteButton.setVisible(false);
            raiseSalaryButton.setVisible(false);
            LOGGER.log(Level.INFO, "EmployeeController initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing EmployeeController", e);
            showAlert("Initialization Error", "Failed to initialize the application: " + e.getMessage(), Alert.AlertType.ERROR);
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
            employeeIdField.textProperty().addListener((obs, oldVal, newVal) -> validateId(newVal));
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

    private void validateId(String value) {
        try {
            if (value.isEmpty()) {
                idErrorLabel.setText("Employee ID is required");
                return;
            }
            Integer id = Integer.parseInt(value);
            if (id <= 0) {
                idErrorLabel.setText("ID must be a positive number");
            } else {
                idErrorLabel.setText("");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid ID format: {0}", value);
            idErrorLabel.setText("ID must be a valid number");
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
                LOGGER.log(Level.WARNING, "Form validation failed");
                showAlert("Validation Error", "Please fix the errors in the form", Alert.AlertType.ERROR);
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
            
            LOGGER.log(Level.INFO, "Added new employee: {0}", employee);
            refreshEmployeeList();
            clearFields();
            showAlert("Success", "Employee added successfully", Alert.AlertType.INFORMATION);
            
        } catch (InvalidSalaryException e) {
            LOGGER.log(Level.SEVERE, "Invalid salary", e);
            showAlert("Error", "Invalid salary: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (InvalidDepartmentException e) {
            LOGGER.log(Level.SEVERE, "Invalid department", e);
            showAlert("Error", "Invalid department: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid number format", e);
            showAlert("Error", "Please check the numeric fields", Alert.AlertType.ERROR);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding employee", e);
            showAlert("Error", "Failed to add employee: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdateEmployee() {
        Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Error", "Please select an employee to update", Alert.AlertType.ERROR);
            return;
        }

        try {
            String name = nameField.getText();
            String department = departmentField.getValue();
            double salary = Double.parseDouble(salaryField.getText());

            employeeDatabase.updateEmployeeDetails(selectedEmployee.getEmployeeId(), "name", name);
            employeeDatabase.updateEmployeeDetails(selectedEmployee.getEmployeeId(), "department", department);
            employeeDatabase.updateEmployeeDetails(selectedEmployee.getEmployeeId(), "salary", salary);

            refreshEmployeeList();
            clearFields();
            showAlert("Success", "Employee updated successfully", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteEmployee() {
        Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Error", "Please select an employee to delete", Alert.AlertType.ERROR);
            return;
        }

        try {
            employeeDatabase.removeEmployee(selectedEmployee.getEmployeeId());
            refreshEmployeeList();
            showAlert("Success", "Employee deleted successfully", Alert.AlertType.INFORMATION);
        } catch (EmployeeNotFoundException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRaiseSalary() {
        Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Error", "Please select an employee", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Salary Raise");
        dialog.setHeaderText("Enter raise percentage");
        dialog.setContentText("Percentage:");

        dialog.showAndWait().ifPresent(percentageStr -> {
            try {
                double percentage = Double.parseDouble(percentageStr);
                employeeDatabase.giveSalaryRaise(percentage, 0.0);
                refreshEmployeeList();
                showAlert("Success", "Salary raised successfully", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid percentage", Alert.AlertType.ERROR);
            } catch (InvalidSalaryException e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleTableSelection() {
        Employee<Integer> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            // Show action buttons
            updateButton.setVisible(true);
            deleteButton.setVisible(true);
            raiseSalaryButton.setVisible(true);
            
            // Fill the form with selected employee's data
            nameField.setText(selectedEmployee.getName());
            departmentField.setText(selectedEmployee.getDepartment());
            salaryField.setText(String.valueOf(selectedEmployee.getSalary()));
        } else {
            // Hide action buttons
            updateButton.setVisible(false);
            deleteButton.setVisible(false);
            raiseSalaryButton.setVisible(false);
            clearFields();
        }
    }

    private void refreshEmployeeList() {
        employeeList.setAll(employeeDatabase.getAllEmployees());
        LOGGER.log(Level.INFO, "Employee list refreshed. Total employees: {0}", employeeList.size());
    }

    private void clearFields() {
        employeeIdField.clear();
        nameField.clear();
        departmentField.setValue(null);
        salaryField.clear();
        ratingField.setValue(null);
        experienceField.clear();
        clearErrorLabels();
        LOGGER.log(Level.INFO, "Input fields cleared");
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

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
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
            showAlert("Error", "Failed to get top paid employees: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleShowAverageSalary() {
        try {
            String department = departmentAnalyticsField.getValue();
            if (department == null || department.isEmpty()) {
                LOGGER.log(Level.WARNING, "No department selected for average salary");
                showAlert("Error", "Please select a department", Alert.AlertType.ERROR);
                return;
            }

            double avgSalary = employeeDatabase.getAverageDepartmentSalary(department);
            analyticsOutput.setText(String.format("Average Salary in %s: $%.2f", department, avgSalary));
            LOGGER.log(Level.INFO, "Displayed average salary for department: {0}", department);
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Invalid department in average salary calculation", e);
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating average salary", e);
            showAlert("Error", "Failed to calculate average salary: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeDatabase.getAllEmployees());
        return "index";
    }

    @PostMapping
    public String addEmployee(@RequestParam String name, 
                            @RequestParam String department,
                            @RequestParam double salary) {
        try {
            Employee<Integer> employee = new Employee<>(name, department, salary);
            employeeDatabase.addEmployee(employee);
        } catch (InvalidSalaryException | InvalidDepartmentException e) {
            // Handle validation errors
        }
        return "redirect:/employees";
    }

    @GetMapping("/{id}/update")
    public String showUpdateForm(@PathVariable Integer id, Model model) {
        try {
            Employee<Integer> employee = employeeDatabase.getEmployee(id);
            model.addAttribute("employee", employee);
            return "update-employee";
        } catch (EmployeeNotFoundException e) {
            return "redirect:/employees";
        }
    }

    @PostMapping("/{id}/update")
    public String updateEmployee(@PathVariable Integer id,
                               @RequestParam String name,
                               @RequestParam String department,
                               @RequestParam double salary) {
        try {
            employeeDatabase.updateEmployeeDetails(id, "name", name);
            employeeDatabase.updateEmployeeDetails(id, "department", department);
            employeeDatabase.updateEmployeeDetails(id, "salary", salary);
        } catch (EmployeeNotFoundException | InvalidSalaryException | InvalidDepartmentException e) {
            // Handle validation errors
        }
        return "redirect:/employees";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteEmployee(@PathVariable Integer id) {
        try {
            employeeDatabase.removeEmployee(id);
            return "Employee deleted successfully";
        } catch (EmployeeNotFoundException e) {
            return "Employee not found";
        }
    }

    @PostMapping("/{id}/raise-salary")
    @ResponseBody
    public String giveSalaryRaise(@PathVariable Integer id,
                                @RequestBody SalaryRaiseRequest request) {
        try {
            employeeDatabase.giveSalaryRaise(request.getPercentage(), 0.0);
            return "Salary raised successfully";
        } catch (InvalidSalaryException e) {
            return "Invalid salary raise percentage";
        }
    }

    // Helper class for salary raise request
    private static class SalaryRaiseRequest {
        private double percentage;

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }
    }
}