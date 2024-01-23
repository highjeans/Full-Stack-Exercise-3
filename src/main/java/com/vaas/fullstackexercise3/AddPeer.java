package com.vaas.fullstackexercise3;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddPeer {
    public TextField seedField;
    public Button seedConnectButton;
    @FXML
    private Label welcomeText;

    @FXML
    protected void handleConnectButtonAction() {
        System.out.println("Attempt to connect to " + seedField.getText());
    }
}