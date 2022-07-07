/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import GUI.CellAnalysisGUI;
import experiments.Constants;
import experiments.Experiment;
import experiments.ExperimentNew;
import experiments.Fish;
import experiments.FishGroup;
import experiments.FishGroupNew;
import experiments.FishNew;
import experiments.ImageAnalysis;
import experiments.ImageAnalysisNew;
import experiments.Measurement;
import experiments.MeasurementNew;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author janlu
 */
public class CellImporterCSV implements Runnable{
    ExperimentNew experiment;
    CellHolder cellHolder;
    CellAnalysisGUI parent;
    private int completed = 0;
    private int total = 0;
    private String currentFile;
    
    public CellImporterCSV(ExperimentNew experiment, CellAnalysisGUI parent){
        this.experiment = experiment;
        this.parent = parent;
    }
    public void run() {
        countAnalyses();
        createCellHolder();
        sendCellHolder();
    }

    private void createCellHolder() {
        this.cellHolder = new CellHolder();
        for (FishGroupNew fishGroup : experiment.getGroups()) {
            CellGroup cellGroup = new CellGroup(fishGroup.getGroupName());
            for (FishNew fish : fishGroup.getFishList()) {
                CellSubject cellFish = new CellSubject(fish.getName());
                for (MeasurementNew measurement : fish.getMeasurements()) {
                    for (ImageAnalysisNew analysis : measurement.getAnalysisList()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSSEGMENTED)) {
                            //send update
                            currentFile = "Fish: " + cellFish.toString() + " Time: " + analysis.getAnalysisTime().toString();
                            sendProgressUpdate();
                            
                            //Create new aquisition
                            CellDay cellDay = new CellDay(analysis.getAnalysisTime(), analysis.getAnalysisFiles()[0]);
                            
                            //Import Objects
                            try {
                                Scanner scanner = new Scanner(analysis.getRois()[0]);
                                Scanner lineScanner;
                                
                                //Skip header
                                scanner.nextLine();
                                
                                while(scanner.hasNextLine()) {
                                    lineScanner = new Scanner(scanner.nextLine());
                                    lineScanner.useDelimiter(",");
                                    int volumePix = Integer.parseInt(lineScanner.next().replaceAll("\"", ""));
                                    double volumeUnit = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    double averageIntensity = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    int objectNumber = Integer.parseInt(lineScanner.next().replaceAll("\"", ""));
                                    double positionX = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    double positionY = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    double positionZ = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    cellDay.addCell(new Cell(volumePix, volumeUnit, averageIntensity, objectNumber, positionX, positionY, positionZ));
                                }
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(CellImporterCSV.class.getName()).log(Level.SEVERE, null, ex);
                            }                                                 
                            cellFish.addDay(cellDay);
                            completed++;
                            break;
                        }    
                    }
                }
                cellGroup.addFish(cellFish);
            }
            cellHolder.addGroup(cellGroup);
            sendProgressUpdate();
        }
    }

    private void countAnalyses() {
        for (FishGroupNew fishGroup : experiment.getGroups()) {
            for (FishNew fish : fishGroup.getFishList()) {
                for (MeasurementNew measurement : fish.getMeasurements()) {
                    for (ImageAnalysisNew analysis : measurement.getAnalysisList()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSSEGMENTED)) {
                            total++;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void sendProgressUpdate() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setProgress(currentFile, completed, total);
            }
        });
    }

    private void sendCellHolder() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setCellHolder(cellHolder);
            }
        });
    }
}
