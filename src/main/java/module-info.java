module com.example.logapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.logapp to javafx.fxml;
    exports com.example.logapp;
}