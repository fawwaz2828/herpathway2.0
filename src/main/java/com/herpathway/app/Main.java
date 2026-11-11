package com.herpathway.app;

import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("HerPathway");
        primaryStage.setMinWidth(Navigator.MIN_WIDTH);
        primaryStage.setMinHeight(Navigator.MIN_HEIGHT);

        Navigator.init(primaryStage);
        Navigator.goTo(View.LOGIN);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
