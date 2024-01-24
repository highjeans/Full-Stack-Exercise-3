package com.vaas.fullstackexercise3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 640);
        stage.setTitle("VaasChat Client");
        stage.setScene(scene);
        stage.show();
        (new Thread(() -> {
            (new Server()).startServer(0);
        })).start();
    }

    public static void main(String[] args) {
        launch();
    }
}