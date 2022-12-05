/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Containers;

/**
 *TEMP class for constants
 * @author janlu
 */
public class Constants {
    public static int ANALYSIS_FLATTENED = 1;
    public static int ANALYSIS_CROPPED = 2;
    public static int ANALYSIS_CELLSCOUNTED = 3;
    public static int ANALYSIS_MONTAGE = 4;
    public static int ANALYSIS_CELLSSEGMENTED = 5;
    
    public static int CHANNEL_COMP = -1;
    public static String[] analysisNames = {"None", "Flattened", "Cropped", "Counted", "Montage", "CellSegmentation"};
    
    public static int TASK_FLATTENING = 1;
    public static int TASK_CROPPING = 2;
    public static int TASK_CELLCOUNTING = 3;
    public static int TASK_MAKINGMONTAGE = 4;
    public static int TASK_CELLSEGMENTING = 5;
}
