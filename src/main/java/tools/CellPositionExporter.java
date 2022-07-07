/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import experiments.Experiment;
import ij.IJ;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import loci.formats.FormatException;
import loci.formats.in.ImarisHDFReader;
import loci.formats.in.TiffReader;
import mcib3d.geom.Object3D;
import mcib3d.geom.Vector3D;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import util.opencsv.CSVWriter;

/**
 *
 * @author janlu
 */
public class CellPositionExporter {
    ArrayList<CellGroup> cellGroups;
    IntervalMarker[] markers;
    Experiment experiment;
    
    public CellPositionExporter(ArrayList<CellGroup> cellGroups, IntervalMarker[] markers, Experiment experiment) {
        this.cellGroups = cellGroups;
        this.markers = markers;
        this.experiment = experiment;
    }
    
    public void run() {
        File file = new File(experiment.getConfocalDirectory() + "\\SegmentedCellPosition.csv");
        try {
            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);
            String[] markerLabels = new String[markers.length];
            for (int i = 0; i < markers.length; i++) markerLabels[i] = (markers[i] == null) ? "All Cells" : markers[i].getLabel();
            writer.writeNext(markerLabels);
            ArrayList<String[]> outList = new ArrayList();
            for (CellGroup group : cellGroups) {
                for (CellSubject fish : group.getFishList()) {
                    for (CellDay day : fish.getDayList()) {
                        
                        for (int i = 0; i < markers.length; i++) {
                            //ArrayList<Object3D> objects = day.getCellPopulation().getObjectsWithinVolume(markers[i].getStartValue(), markers[i].getEndValue(), true);
                            ArrayList<Object3D> objects = null;
                            for (int j = 0; j < objects.size(); j++) {
                                String[] lineOut = new String[3 + (markers.length* 2)];
                                lineOut[0] = group.getGroupName();
                                lineOut[1] = fish.getName();
                                lineOut[2] = day.toString();
                                double imageXSize;
                                double imageYSize;
                                try(TiffReader tester = new TiffReader()){
                                    tester.setId(day.getAnalysisFile().getAbsolutePath());
                                    imageXSize = tester.getSizeX();
                                    imageYSize = tester.getSizeY();
                                } catch (IOException | FormatException e) {
                                    System.out.println("Error exporting file " + day.getAnalysisFile().getAbsolutePath());
                                    imageXSize = 1;
                                    imageYSize = 1;
                                    
                                }
                                
                                
                                
                                double relativeX = objects.get(j).getCenterX()/imageXSize;
                                double relativeY = objects.get(j).getCenterY()/imageYSize;
                                
                                
                                
                                lineOut[3 + (i*2)] = Double.toString(relativeX);
                                lineOut[4 + (i*2)] = Double.toString(relativeY);
                                for (int k = 3; k < lineOut.length; k++) {
                                    if (k != (i*2) + 3 && k != (i*2) + 4) {
                                    lineOut[k] = "";
                                    }
                                }
                                outList.add(lineOut);
                                
                            }
                            
                        }
                        
                    }
                    outList.add(new String[] {" "});
                    
                }
                
            }
            writer.writeAll(outList);
            writer.close();
        }
        catch (IOException e) {
            System.out.println("IOException");
        }
    }
    
    
}
