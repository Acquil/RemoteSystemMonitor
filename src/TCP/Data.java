package TCP;


import javax.swing.*;
import java.io.Serializable;

public class Data implements Serializable {
    private static final long serialVersionUID = -123456L;
    private String osUser;
    private String osName;
    private String osVersion;
    private String osArchitecture;

    private long totalMemory;
    private long freeMemory;
    private Double cpuLoad;
    private ImageIcon screen;


    private String rxData;
    private String txData;

    public Data(){
        osUser = System.getProperty("user.name");
        osName = System.getProperty("os.name");
        osVersion = System.getProperty("os.version");
        osArchitecture = System.getProperty("os.arch");
    }

    public void setTotalMemory(long totalMemory){this.totalMemory = totalMemory;}
    public void setFreeMemory(long freeMemory){this.freeMemory = freeMemory;}
    public void setCpuLoad(Double load){this.cpuLoad = load;}
    public void setScreen(ImageIcon img){this.screen = img;}
    public ImageIcon getScreen(){return this.screen;}

    public String getOSName(){
        return this.osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getOsArchitecture() {
        return osArchitecture;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public Double getCpuLoad() {
        return cpuLoad;
    }

    public String getOsUser() {
        return osUser;
    }

    public String getRxData() {
        return rxData;
    }

    public void setRxData(String rxData) {
        this.rxData = rxData;
    }

    public String getTxData() {
        return txData;
    }

    public void setTxData(String txData) {
        this.txData = txData;
    }
}
