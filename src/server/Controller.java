package server;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import javafx.fxml.FXML;

public class Controller {

    @FXML
    Label lblSetIP;
    @FXML
    Label lblSetPort;
    @FXML
    ListView<String> connectedClientList;
    @FXML
    ImageView ScreenShot;
    @FXML
    SplitPane mainWindow;
    @FXML
    SplitPane leftWindow;
    @FXML
    TabPane rightWindow;

    private Integer port = 15001;
    private ObservableList<String> connectedClients = FXCollections.observableArrayList();
    private Map<String,javafx.scene.image.Image> data = new HashMap<>();

    class handleClient implements Runnable{

        Socket sk;
        handleClient(Socket sk){ this.sk = sk;}

        @Override
        public void run() {
            String hostName = sk.getInetAddress().getHostName();
            connectedClients.add(hostName);
            connectedClientList.setItems(connectedClients);

            while(true){
                try {
                    ObjectInputStream dis;
                    Rectangle clientScreenDim = null;
                    dis = new ObjectInputStream(sk.getInputStream());
                    clientScreenDim = (Rectangle) dis.readObject();

                    while (true) {
                        ImageIcon imgIcon = (ImageIcon)dis.readObject();
                        Image img = imgIcon.getImage();

                        BufferedImage bimg = Converter.toBufferedimage(img);

                        javafx.scene.image.Image imgScene = SwingFXUtils.toFXImage(bimg,null);
                        data.put(hostName, imgScene);
//                        ScreenShot.setImage(imgScene);

                    }
                } catch (Exception e) {
                        e.printStackTrace();
                        break;
                } finally {

                    try {
                        sk.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        e.printStackTrace();
                    }
                    System.out.println("Finally!");
                    connectedClients.remove(hostName);
                    connectedClientList.setItems(connectedClients);
                }
            }
        }
    }

    private Task connAccepter = new Task<Void>() {
        @Override
        public Void call() throws Exception {
            ServerSocket socket = null;
            ExecutorService ex = Executors.newCachedThreadPool();
            try {
                socket = new ServerSocket(port);
                while(true){
                    Socket client = socket.accept();
                    ex.execute(new handleClient(client));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                ex.shutdownNow();
            }
            return null;
        }
    };

    @FXML
    private void initialize() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        lblSetIP.setText(address.getHostAddress());
        lblSetPort.setText("15001");

        mainWindow.setDividerPositions(0.25);
        leftWindow.maxWidthProperty().bind(mainWindow.widthProperty().multiply(0.25));
        ScreenShot.setPreserveRatio(true);
        ScreenShot.fitHeightProperty().bind(rightWindow.heightProperty());
        ScreenShot.fitWidthProperty().bind(rightWindow.widthProperty());

        connectedClientList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            System.out.println("Selected item: "+newValue);
            ScreenShot.setImage(data.get(newValue));
        });

        new Thread(connAccepter).start();

    }

    @FXML
    private void run(){
        System.out.println("hi");
    }

}
