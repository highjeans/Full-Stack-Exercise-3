module com.vaas.fullstackexercise3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.httpserver;
    requires java.net.http;


    opens com.vaas.fullstackexercise3 to javafx.fxml;
    exports com.vaas.fullstackexercise3;
}