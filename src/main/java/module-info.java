module com.vaas.fullstackexercise3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.vaas.fullstackexercise3 to javafx.fxml;
    exports com.vaas.fullstackexercise3;
}