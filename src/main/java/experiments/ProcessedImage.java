package experiments;


import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author janlu
 */
public class ProcessedImage implements Serializable {
    public static int BRIGHT_FIELD = 1;
    public static int CELLS = 2;
    public static int VASCULATURE = 4;
    public static int DRUG = 8;
    public static int DRUG_CARRIER = 16;
    public static int MACROPHAGES = 32;
    
    public static int Z_PROJECTION = 1;
    public static int FOCUSED = 2;
    public static int REGISTERED = 4;
    public static int CELLS_COUNTED = 8;
    public static int OVERLAY = 16;
    
    //array containing cell rois
    
    String imageName;
    int processType;
    int channelType;
    int confocalChannel;
    
    
    
    ProcessedImage(String name, int channelType, int processType, int confocalChannel) {
        imageName = name;
        this.processType = processType;
        this.channelType = channelType;
        this.confocalChannel = confocalChannel;
    }
    
}
