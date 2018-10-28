package TCP;


import javax.swing.*;
import java.io.Serializable;

public class Data implements Serializable {
    private static final long serialVersionUID = -123456L;
    private String osName;
    private String osVersion;
    private String osArchitecture;

    private long totalMemory;
    private long freeMemory;
    private Double cpuLoad;
    private ImageIcon screen;

//    public Data(String s1, String s2, String s3){
//        osName = s1;
//        osVersion = s2;
//        osArchitecture = s3;
//    }


    public Data(){
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
}
