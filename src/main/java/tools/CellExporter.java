/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import Containers.Cells.Cell;
import Containers.Cells.CellDay;
import Containers.Cells.CellGroup;
import Containers.Old.ExperimentOld;
import Containers.Experiment.Experiment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import util.opencsv.CSVWriter;

/**
 *
 * @author janlu
 */
public class CellExporter {
    ArrayList<CellGroup> cellGroups;
    Experiment experiment;
    
    public CellExporter(ArrayList<CellGroup> cellGroups, Experiment experiment) {
        this.cellGroups = cellGroups;
        this.experiment = experiment;
    }
    
    public void run() {
        File file = new File(experiment.getConfocalDirectory() + "\\SegmentedObjects.csv");
        try {
            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);
            ArrayList<Integer> measurementChannels = cellGroups.get(0).getFishList().get(0).getDayList().get(0).getCellAt(0).getMeasurementChannels();
            
            String[] header = new String[11 + (measurementChannels.size() * 5)];
            header[0] = "Group";
            header[1] = "Subject";
            header[2] = "Time";
            header[3] = "Object.Number";
            header[4] = "Pos.X";
            header[5] = "Pos.Y";
            header[6] = "Pos.Z";
            header[7] = "Volume.Pixels";
            header[8] = "Volume.Unit";
            header[9] = "Surface.Pixels";
            header[10] = "Surface.Unit";
            for (int i = 0; i < measurementChannels.size(); i ++) {
                int index = header.length - (measurementChannels.size()*5) + i*5;
                header[index] = "Channel." + measurementChannels.get(i) + ".Min.Intensity";
                header[index + 1] = "Channel." + measurementChannels.get(i) + ".Max.Intensity";
                header[index + 2] = "Channel." + measurementChannels.get(i) + ".Average.Intensity";
                header[index + 3] = "Channel." + measurementChannels.get(i) + ".Median.Intensity";
                header[index + 4] = "Channel." + measurementChannels.get(i) + ".Integrated.Intensity";
            }
            writer.writeNext(header);
            
            for (CellGroup group : cellGroups) {
                for (CellSubject subject : group.getFishList()) {
                    for (CellDay day : subject.getDayList()) {
                        for (Cell cell : day.getCellList()) {
                            String[] lineOut = new String[header.length];
                            lineOut[0] = group.getGroupName();
                            lineOut[1] = subject.getName();
                            lineOut[2] = day.toString();
                            lineOut[3] = Integer.toString(cell.getCellNumber());
                            lineOut[4] = Double.toString(cell.getPositionX());
                            lineOut[5] = Double.toString(cell.getPositionY());
                            lineOut[6] = Double.toString(cell.getPositionZ());
                            lineOut[7] = Integer.toString(cell.getVolumePix());
                            lineOut[8] = Double.toString(cell.getVolumeUnit());
                            lineOut[9] = Integer.toString(cell.getSurfacePix());
                            lineOut[10] = Double.toString(cell.getSurfaceUnit());
                            
                            for (int i = 0; i < measurementChannels.size(); i ++) {
                                int index = lineOut.length - (measurementChannels.size()*5) + i*5;
                                lineOut[index] = Double.toString(cell.getMinIntensity().get(i));
                                lineOut[index + 1] = Double.toString(cell.getMaxIntensity().get(i));
                                lineOut[index + 2] = Double.toString(cell.getAverageIntensity().get(i));
                                lineOut[index + 3] = Double.toString(cell.getMedianIntensity().get(i));
                                lineOut[index + 4] = Double.toString(cell.getIntegratedIntensity().get(i));
                            }
                             writer.writeNext(lineOut);
                        }
                    }
                }
            }
            writer.close();
        }
        catch (IOException e) {
            System.out.println("IOException");
        }
    }
}
