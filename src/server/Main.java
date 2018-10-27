package server;

import javafx.application.Application;
import javafx.application.Platform;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.concurrent.Task;

public class Main extends Application {

    private Integer port = 15001;

    private Task connAccepter = new Task<Void>() {
        @Override
        public Void call() throws Exception {
            ServerSocket socket = null;
            try {
                socket = new ServerSocket(port);
                while(true){
                    Socket client = socket.accept();
                    System.out.println("New client connected :" + socket.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    };


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("RSM - Server");

//        Label lblPort = (Label)root.lookup("#lblSetPort");
//        lblPort.setText("15001");

        primaryStage.setScene(new Scene(root, 720, 520));

        new Thread(connAccepter).start();

        primaryStage.show();


        //To close threads on exit
        primaryStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });

    }





    public static void main(String[] args){

//        Runnable connAccepter = new AcceptIncomingConn(15001);
//        Thread incomingConn = new Thread(connAccepter);
//        incomingConn.setDaemon(true);
//        incomingConn.start();

        launch(args);


//        incomingConn.join();

    }
}
