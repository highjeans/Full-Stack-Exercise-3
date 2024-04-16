module com.vaas.fullstackexercise3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.httpserver;


    opens com.vaas.fullstackexercise3 to javafx.fxml;
    exports com.vaas.fullstackexercise3;
}