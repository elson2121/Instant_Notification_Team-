module com.instantnotificationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.instantnotificationsystem to javafx.fxml;
    exports com.instantnotificationsystem;
    
    opens com.instantnotificationsystem.controller to javafx.fxml;
    exports com.instantnotificationsystem.controller;
    
    opens com.instantnotificationsystem.model to javafx.base;
    exports com.instantnotificationsystem.model;
}