package TCP;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.*;
import javafx.fxml.FXML;
import javafx.util.Duration;

import javax.swing.*;

import static java.lang.Thread.sleep;

public class Controller {

    @FXML Label lblSetIP;
    @FXML Label lblSetPort;
    @FXML ListView<String> connectedClientList;
    @FXML ImageView ScreenShot;
    @FXML SplitPane mainWindow;
    @FXML SplitPane leftWindow;
    @FXML TabPane rightWindow;
    @FXML Label lblSysUser;
    @FXML Label lblSysOS;
    @FXML Label lblSysVersion;
    @FXML Label lblSysArch;
    @FXML Label lblSysTotalMemory;
    @FXML Label lblSysFreeMemory;
    @FXML Label lblSysCPU;
    @FXML ProgressBar pgRAM;
    @FXML Label lblUpload;
    @FXML Label lblDownload;

    private Integer port = 15001;
    private ObservableList<String> connectedClients = FXCollections.observableArrayList();
    private Map<String,Data> dataMap = new HashMap<>();
    private Map<String, javafx.scene.image.Image> screenMap = new HashMap<>();
    private Map<String, String> initialUploadMap = new HashMap<>();
    private Map<String, String> initialDownloadMap = new HashMap<>();
    private Map<String, Boolean> downloadCrossed = new HashMap<>();

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
                    ObjectInputStream ois;
                    ois = new ObjectInputStream(sk.getInputStream());

                    while (true) {

                        Data input = (Data)ois.readObject();
                        ImageIcon imgIcon = input.getScreen();
                        Image img = imgIcon.getImage();
                        BufferedImage bimg = Converter.toBufferedimage(img);

                        javafx.scene.image.Image imgScene = SwingFXUtils.toFXImage(bimg,null);
                        dataMap.put(hostName, input);
                        screenMap.put(hostName, imgScene);

                        initialDownloadMap.put(hostName,input.getRxData());
                        initialUploadMap.put(hostName,input.getTxData());
                        downloadCrossed.put(hostName,false);

                        if (hostName.equals(connectedClientList.getSelectionModel().getSelectedItems().get(0))){
                            ScreenShot.setImage(imgScene);
                        }
                        System.gc();

                    }
                } catch (Exception e) {
                        e.printStackTrace();
                        break;
                } finally {

                    try {
                        sk.close();
                    } catch (IOException e) {
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

            } catch (Exception e) {
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

        new Thread(connAccepter).start();
//        new Thread(backgroundUpdater).start();

        connectedClientList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if(!(newValue == null)) {

                System.out.println("Selected item: " + newValue);
                ScreenShot.setImage(screenMap.get(newValue));

                lblSysUser.setText(dataMap.get(newValue).getOsUser());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lblSysOS.setText(dataMap.get(newValue).getOSName());
                lblSysVersion.setText(dataMap.get(newValue).getOsVersion());
                lblSysArch.setText(dataMap.get(newValue).getOsArchitecture());

                lblSysFreeMemory.setText(String.valueOf(dataMap.get(newValue).getFreeMemory()));
                lblSysTotalMemory.setText(String.valueOf(dataMap.get(newValue).getTotalMemory()));
                lblSysCPU.setText(String.valueOf(dataMap.get(newValue).getCpuLoad()));
            }
        });

        Timeline bgUpdater = new Timeline(new KeyFrame(Duration.millis(1000),actionEvent -> {
            System.out.println("Background Task");
            String selectedValue = connectedClientList.getSelectionModel().getSelectedItems().get(0);
            if(selectedValue != null){
                Double free = Double.valueOf(dataMap.get(selectedValue).getFreeMemory())/1000000;
                Double total = Double.valueOf(dataMap.get(selectedValue).getTotalMemory())/1000000;

                lblSysFreeMemory.setText(String.valueOf(free) + " MB");
                lblSysTotalMemory.setText(String.valueOf(total) + " MB");
                lblSysCPU.setText(String.valueOf(dataMap.get(selectedValue).getCpuLoad()));
                pgRAM.setProgress(((total-free)/total));

//
//                Integer down = //Integer.valueOf(nowDownload) -
//                        Integer.valueOf(initialDownloadMap.get(selectedValue));
//                Integer up = //Integer.valueOf(nowUpload) -
//                        Integer.valueOf(initialUploadMap.get(selectedValue));

                lblUpload.setText(String.valueOf(dataMap.get(selectedValue).getTxData()));
                lblDownload.setText(String.valueOf(dataMap.get(selectedValue).getRxData()));

//                if(down > 1 && downloadCrossed.get(selectedValue).equals(false)){
//                    javafx.scene.control.PopupControl notificationPane = new PopupControl();
////                    notificationPane.se
//                    downloadCrossed.put(selectedValue,true);
////                    notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
//                }

            }
        }));
        bgUpdater.setCycleCount(Animation.INDEFINITE);
        bgUpdater.play();
    }

    @FXML
    private void run(){}

}
