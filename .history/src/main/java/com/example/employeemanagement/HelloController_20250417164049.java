package com.example.employeemanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private void goToEmployeeManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("employee-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((javafx.scene.Node) loader.getRoot()).getScene().getWindow();
            currentStage.setTitle("Employee Management");
            currentStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}