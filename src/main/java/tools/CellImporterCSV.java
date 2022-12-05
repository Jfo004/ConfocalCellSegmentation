/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import Containers.Cells.Cell;
import Containers.Cells.CellHolder;
import Containers.Cells.CellDay;
import Containers.Cells.CellGroup;
import GUI.CellAnalysisGUI;
import Containers.Constants;
import Containers.Experiment.Experiment;
import Containers.Experiment.ExperimentGroup;
import Containers.Experiment.Subject;
import Containers.Experiment.ImageAnalysis;
import Containers.Experiment.Measurement;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author janlu
 */
public class CellImporterCSV implements Runnable{
    Experiment experiment;
    CellHolder cellHolder;
    CellAnalysisGUI parent;
    private int completed = 0;
    private int total = 0;
    private String currentFile;
    
    public CellImporterCSV(Experiment experiment, CellAnalysisGUI parent){
        this.experiment = experiment;
        this.parent = parent;
    }
    public CellImporterCSV(Experiment experiment) {
        this(experiment, null);
    }
    @Override
    public void run() {
        countAnalyses();
        createCellHolder();
        sendCellHolder();
    }

    private void createCellHolder() {
        this.cellHolder = new CellHolder();
        for (ExperimentGroup fishGroup : experiment.getGroups()) {
            CellGroup cellGroup = new CellGroup(fishGroup.getGroupName());
            for (Subject fish : fishGroup.getSubjectList()) {
                CellSubject cellFish = new CellSubject(fish.getName());
                for (Measurement measurement : fish.getMeasurements()) {
                    for (ImageAnalysis analysis : measurement.getAnalysisList()) {
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
                                
                                // get measurement channels from header
                                String temp = scanner.nextLine();
                                lineScanner = new Scanner(temp);
                                lineScanner.useDelimiter(",");
                                System.out.println(lineScanner.delimiter());
                                // skip headers before fluorescence
                                System.out.println(temp);
                                for (int i = 0; i < 8 ; i++) System.out.println(lineScanner.next());
                                // get Channel id from first fluorescence header
                                ArrayList<Integer> measurementChannels = new ArrayList();
                                while(lineScanner.hasNext()) {
                                    String header = lineScanner.next().replaceAll("\"", "");
                                    String[] headerArray = header.split("\\.");
                                    
                                    measurementChannels.add(Integer.parseInt(headerArray[1]));
                                    //Skipping other measurements of same channel
                                    for (int i = 0; i < 4 ; i++) System.out.println(lineScanner.next());
                                }
                                
                                while(scanner.hasNextLine()) {
                                    lineScanner = new Scanner(scanner.nextLine());
                                    lineScanner.useDelimiter(",");
                                    int objectNumber = Integer.parseInt(lineScanner.next().replaceAll("\"", ""));
                                    double positionX = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    double positionY = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    double positionZ = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    int volumePix = Integer.parseInt(lineScanner.next().replaceAll("\"", ""));
                                    double volumeUnit = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    int surfacePix = Integer.parseInt(lineScanner.next().replaceAll("\"", "").split("\\.")[0]); //3D suite saves pixel count as float with a .0
                                    double surfaceUnit = Double.parseDouble(lineScanner.next().replaceAll("\"", ""));
                                    
                                    ArrayList<Double> minIntensity = new ArrayList();
                                    ArrayList<Double> maxIntensity = new ArrayList();
                                    ArrayList<Double> averageIntensity = new ArrayList();
                                    ArrayList<Double> medianIntensity = new ArrayList();
                                    ArrayList<Double> integratedIntensity = new ArrayList();
                                    for (Integer measurementChannel : measurementChannels) {
                                        minIntensity.add(Double.parseDouble(lineScanner.next().replaceAll("\"", "")));
                                        maxIntensity.add(Double.parseDouble(lineScanner.next().replaceAll("\"", "")));
                                        averageIntensity.add(Double.parseDouble(lineScanner.next().replaceAll("\"", "")));
                                        medianIntensity.add(Double.parseDouble(lineScanner.next().replaceAll("\"", "")));
                                        integratedIntensity.add(Double.parseDouble(lineScanner.next().replaceAll("\"", "")));
                                    }                                    
                                    
                                    cellDay.addCell(new Cell(objectNumber, positionX, positionY, positionZ, volumePix, volumeUnit, surfacePix, surfaceUnit, measurementChannels, minIntensity, maxIntensity, averageIntensity, medianIntensity, integratedIntensity ));
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
        for (ExperimentGroup fishGroup : experiment.getGroups()) {
            for (Subject fish : fishGroup.getSubjectList()) {
                for (Measurement measurement : fish.getMeasurements()) {
                    for (ImageAnalysis analysis : measurement.getAnalysisList()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSSEGMENTED)) {
                            total++;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public CellHolder getCellHolder(){
        countAnalyses();
        createCellHolder();
        return cellHolder;
    }

    private void sendProgressUpdate() {
        if (parent == null) return;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setProgress(currentFile, completed, total);
            }
        });
    }

    private void sendCellHolder() {
        if(parent == null) return;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setCellHolder(cellHolder);
            }
        });
    }
}
