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
            Stage stage = new Stage();
            stage.setTitle("Employee Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}