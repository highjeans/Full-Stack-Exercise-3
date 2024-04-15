module com.vaas.fullstackexercise3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.httpcomponents.client5.httpclient5.fluent;
    requires org.apache.httpcomponents.core5.httpcore5;


    opens com.vaas.fullstackexercise3 to javafx.fxml;
    exports com.vaas.fullstackexercise3;
}