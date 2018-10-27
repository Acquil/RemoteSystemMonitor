package server;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Controller {

    @FXML
    Label lblSetIP;
    @FXML
    Label lblSetPort;

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
    }



}
