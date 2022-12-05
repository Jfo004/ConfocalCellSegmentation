/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import Containers.Cells.CellGroup;
import GUI.CellAnalysisGUI;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import org.jfree.chart.plot.Marker;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;

/**
 *
 * @author janlu
 */
public class CellCountDatasetGenerator implements Runnable{
    private CellAnalysisGUI parent;
    private XYIntervalSeriesCollection dataset;
    private Marker[] markers;
    private ArrayList<CellGroup> seriesList;
    
    public CellCountDatasetGenerator(CellAnalysisGUI parent, Marker[] markers, ArrayList<CellGroup> seriesList) {
        this.parent = parent;
        this.markers = markers;
        this.seriesList = seriesList;
    }
    
    @Override
    public void run() {
        generateValues();
        sendDataset();
    }

    private void generateValues() {
        for (CellGroup group : seriesList) {
            for (Marker marker : markers) {
                XYIntervalSeries series = new XYIntervalSeries(group.getGroupName() + " - " + marker.getLabel());
                
            }
        }
    }


    
    private void sendDataset() {
        SwingUtilities.invokeLater(() -> {
            parent.setCellCountDataset(dataset);
        });
    }


}
