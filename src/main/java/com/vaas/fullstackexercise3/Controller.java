package com.vaas.fullstackexercise3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
//    @FXML
//    private Label welcomeText;
//

    @FXML
    protected void enterPeerManually() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-peer.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Connect to a peer");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch(Exception e) {
            System.err.println("An error occurred opening the add-peer dialog window.");
        }
    }

}