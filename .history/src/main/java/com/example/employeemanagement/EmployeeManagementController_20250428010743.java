package com.example.employeemanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

public class EmployeeManagementController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeManagementController.class.getName());
    private final EmployeeDatabase database = new EmployeeDatabase();
    private Employee<UUID> selectedEmployee;

    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Employee<UUID>> employeeTable;
    
    @FXML
    private TableColumn<Employee<UUID>, UUID> idColumn;
    
    @FXML
    private TableColumn<Employee<UUID>, String> nameColumn;
    
    @FXML
    private TableColumn<Employee<UUID>, String> departmentColumn;
    
    @FXML
    private TableColumn<Employee<UUID>, Double> salaryColumn;
    
    @FXML
    private TableColumn<Employee<UUID>, Double> performanceColumn;
    
    @FXML
    private TableColumn<Employee<UUID>, Integer> experienceColumn;
    
    @FXML
    private Dialog<Employee<UUID>> employeeDetailsDialog;
    
    @FXML
    private Dialog<Employee<UUID>> addEmployeeDialog;

    @FXML
    public void initialize() {
        // Set up table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().employeeIdProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        departmentColumn.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        salaryColumn.setCellValueFactory(cellData -> cellData.getValue().salaryProperty());
        performanceColumn.setCellValueFactory(cellData -> cellData.getValue().performanceRatingProperty());
        experienceColumn.setCellValueFactory(cellData -> cellData.getValue().yearsOfExperienceProperty());

        // Set up table selection listener
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEmployee = newSelection;
                showEmployeeDetails(newSelection);
            }
        });

        // Load initial data
        refreshTable();
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            try {
                ObservableList<Employee<UUID>> searchResults = 
                    FXCollections.observableArrayList(database.searchEmployeesByName(searchTerm));
                employeeTable.setItems(searchResults);
            } catch (Exception e) {
                showError("Search Error", "Failed to search employees: " + e.getMessage());
            }
        } else {
            refreshTable();
        }
    }

    @FXML
    private void showAddEmployeeDialog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-employee-dialog.fxml"));
            DialogPane dialogPane = loader.load();
            AddEmployeeController controller = loader.getController();
            controller.setDatabase(database);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    refreshTable();
                }
            });
        } catch (Exception e) {
            showError("Error", "Failed to show add employee dialog: " + e.getMessage());
        }
    }

    private void showEmployeeDetails(Employee<UUID> employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("employee-details-dialog.fxml"));
            DialogPane dialogPane = loader.load();
            EmployeeDetailsController controller = loader.getController();
            controller.setEmployee(employee);
            controller.setDatabase(database);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    refreshTable();
                }
            });
        } catch (Exception e) {
            showError("Error", "Failed to show employee details: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortByExperience(ActionEvent event) {
        try {
            ObservableList<Employee<UUID>> sortedList = 
                FXCollections.observableArrayList(database.sortByExperience());
            employeeTable.setItems(sortedList);
        } catch (Exception e) {
            showError("Sort Error", "Failed to sort by experience: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortBySalary(ActionEvent event) {
        try {
            ObservableList<Employee<UUID>> sortedList = 
                FXCollections.observableArrayList(database.sortBySalary());
            employeeTable.setItems(sortedList);
        } catch (Exception e) {
            showError("Sort Error", "Failed to sort by salary: " + e.getMessage());
        }
    }

    @FXML
    private void handleSortByPerformance(ActionEvent event) {
        try {
            ObservableList<Employee<UUID>> sortedList = 
                FXCollections.observableArrayList(database.sortByPerformance());
            employeeTable.setItems(sortedList);
        } catch (Exception e) {
            showError("Sort Error", "Failed to sort by performance: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            ObservableList<Employee<UUID>> employeeList = 
                FXCollections.observableArrayList(database.getAllEmployees());
            employeeTable.setItems(employeeList);
        } catch (Exception e) {
            showError("Error", "Failed to refresh employee list: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 