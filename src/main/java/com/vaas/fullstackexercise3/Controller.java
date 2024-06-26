package com.vaas.fullstackexercise3;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class Controller {
    @FXML
    public TextArea currentMessages;

    @FXML
    public Label peersList;
    public TextField newMessage;

    public Controller() {
        Server.getServerObject().getKnownPeers().addListener((ListChangeListener<InetSocketAddress>) change -> {
            Platform.runLater(() -> {
                // Update peersList in the GUI every time the knownPeers list is updated in through the Server
                List<InetSocketAddress> new_peers = (List<InetSocketAddress>) change.getList();
                StringBuilder newPeersString = new StringBuilder();
                for (InetSocketAddress peer : new_peers) {
                    newPeersString.append(peer.getAddress().getHostAddress()).append(":").append(peer.getPort()).append("\n");
                }
                peersList.setText(newPeersString.toString());
            });
        });
        Server.getServerObject().getMessagesObject().addListener((ListChangeListener<String>) change -> {
            Platform.runLater(() -> {
                // Update currentMessage in the GUI every time the message list is updated
                List<String> messages = (List<String>) change.getList();
                StringBuilder newMessagesString = new StringBuilder();
                for (String message : messages) {
                    newMessagesString.append(message).append("\n");
                }
                currentMessages.setText(newMessagesString.substring(0, newMessagesString.length() - 1));
            });
        });
    }

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

    public void broadcastMessage(ActionEvent actionEvent) {
        Server.getServerObject().broadcast(false, newMessage.getText());
        Server.getServerObject().getMessagesObject().add(newMessage.getText());
        newMessage.setText("");
    }
}