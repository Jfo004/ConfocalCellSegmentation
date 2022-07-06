/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import experiments.Experiment;
import experiments.ExperimentNew;
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
    ExperimentNew experiment;
    
    public CellExporter(ArrayList<CellGroup> cellGroups, ExperimentNew experiment) {
        this.cellGroups = cellGroups;
        this.experiment = experiment;
    }
    
    public void run() {
        File file = new File(experiment.getConfocalDirectory() + "\\SegmentedCells.csv");
        try {
            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);
            String[] header = {"Group", "Fish", "Time", "Cell.ID", "Position.X", "Position.Y", "Position.Z", "Volume.Pixels", "Volume.Unit", "Average.Intensity"};
            writer.writeNext(header);
            
            for (CellGroup group : cellGroups) {
                for (CellFish fish : group.getFishList()) {
                    for (CellDay day : fish.getDayList()) {
                        for (Cell cell : day.getCellList()) {
                             String[] lineOut = {group.getGroupName(),
                                 fish.getName(),
                                 day.toString(),
                                 Integer.toString(cell.getCellNumber()),
                                 Double.toString(cell.getPositionX()),
                                 Double.toString(cell.getPositionY()),
                                 Double.toString(cell.getPositionZ()),
                                 Integer.toString(cell.getVolumePix()),
                                 Double.toString(cell.getVolumeUnit()),
                                 Double.toString(cell.getAverageIntensity())};
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
