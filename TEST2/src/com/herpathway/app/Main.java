package com.herpathway.app;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/com/herpathway/view/LoginView.fxml"));
        primaryStage.setTitle("Mentoring App");
        primaryStage.setScene(new Scene(root, 967, 677));
        primaryStage.show();
        primaryStage.setMinWidth(967);
        primaryStage.setMaxWidth(967);
        primaryStage.setMinHeight(677);
        primaryStage.setMaxHeight(677);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
