module com.example.employeemanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.logging;

    opens com.example.employeemanagement to javafx.fxml;
    exports com.example.employeemanagement;
    exports com.example.employeemanagement.Exception;
    opens com.example.employeemanagement.Exception to javafx.fxml;
}