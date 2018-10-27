package server;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("RSM - Server");

        primaryStage.setScene(new Scene(root, 720, 520));
        primaryStage.setMaximized(true);
        primaryStage.show();

        //To close threads on exit
        primaryStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });

    }


    public static void main(String[] args){
        launch(args);
    }
}
