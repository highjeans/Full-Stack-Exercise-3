package com.vaas.fullstackexercise3;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddPeer {
    public TextField seedField;
    public Button seedConnectButton;
    @FXML
    private Label welcomeText;

    @FXML
    protected void handleConnectButtonAction() {
        System.out.println("Attempt to connect to " + seedField.getText());
        if (!Server.getServerObject().requestPeersFrom(seedField.getText())) {
            new Alert(Alert.AlertType.ERROR, "Failed to connect to " + seedField.getText()).showAndWait();
        }
        else {
            ((Stage)(seedField.getScene().getWindow())).close();
        }
    }
}