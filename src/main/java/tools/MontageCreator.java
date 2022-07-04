/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import GUI.MainMenuGUI;
import experiments.Constants;
import experiments.Experiment;
import experiments.Fish;
import experiments.FishGroup;
import experiments.ImageAnalysis;
import experiments.Measurement;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.io.RoiDecoder;
import ij.plugin.Concatenator;
import ij.plugin.MontageMaker;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 *TODO clean up process
 * TODO block mainGUI
 * TODO update analysis name
 * @author janlu
 */
public class MontageCreator implements Runnable{
        Experiment experiment;
        MainMenuGUI parent;
        
    public MontageCreator(Experiment experiment, MainMenuGUI parent) {
        this.experiment = experiment;
        this.parent = parent;
    }
    public void run() {
        sendStartMessage();
        createAllMontages();
        createCellCSV();
        sendStopMessage("Creating montage");
    }//Single Channels
    private void createAllMontages() {
        for(FishGroup group : experiment.getGroups()) {
            for (Fish fish : group.getFishList()){
                ArrayList<ImageAnalysis> croppedAnalysisList = new ArrayList();
                ArrayList<ImageAnalysis> countedAnalysisList = new ArrayList();
                File[] fileList = new File[2];
                for (Measurement measurement : fish.getMeasurements()) {
                    ImageAnalysis tempCropped = null;
                    ImageAnalysis tempCounted = null;
                    boolean hasMontage = false;
                    for (ImageAnalysis analysis : measurement.getAnalyses()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_MONTAGE)) {
                            hasMontage = true;
                        }
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSCOUNTED)) tempCounted = analysis;
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CROPPED)) tempCropped = analysis;
                    }
                    if(!hasMontage) {
                        if (tempCropped != null) croppedAnalysisList.add(tempCropped);
                        if (tempCounted != null) countedAnalysisList.add(tempCounted);
                    }
                }
                croppedAnalysisList.sort(null);
                countedAnalysisList.sort(null);
                ImagePlus[] imageArray;
                
                if (!croppedAnalysisList.isEmpty()) {
                    imageArray = new ImagePlus[croppedAnalysisList.size()];
                    for (int i = 0; i < croppedAnalysisList.size(); i++) {
                        imageArray[i] = IJ.openImage(croppedAnalysisList.get(i).getAnalysisFiles()[0].getAbsolutePath());
                        imageArray[i].flattenStack();
                        ImageProcessor ip = imageArray[i].getProcessor();
                        Font font = new Font("SansSerif", Font.PLAIN, 40);
                        ip.setFont(font);
                        ip.setColor(new Color(255, 255, 255));
                        ip.drawString(croppedAnalysisList.get(i).getAnalysisTime().toString(), 40, 40);
                        if (i == 0) ip.drawString(fish.getName(), imageArray[i].getWidth()/2, 40);
                        if (croppedAnalysisList.get(i).isAnalysisType(Constants.ANALYSIS_CELLSCOUNTED)) {
                            Roi fishRoi = RoiDecoder.open(croppedAnalysisList.get(i).getRois()[0].getAbsolutePath());
                            Roi cellRoi = RoiDecoder.open(croppedAnalysisList.get(i).getRois()[1].getAbsolutePath());
                            ip.setColor(new Color(255, 0, 0));
                            ip.draw(fishRoi);
                            ip.setColor(new Color(0, 0, 255));
                            ip.draw(cellRoi);
                        }
                    }
                    ImagePlus imageStack = Concatenator.run(imageArray);
                    MontageMaker montageMaker = new MontageMaker();
                    ImagePlus montage = montageMaker.makeMontage2​(imageStack, 1, imageArray.length, 1, 1, imageArray.length, 1, 0, false);
                    String saveString = experiment.getConfocalDirectory().getAbsolutePath()
                    + "\\ProcessedFiles\\Montage"
                    + "\\" + group.getGroupName()
                    + "\\" + fish.getName() + "_CroppedMontage.tif";
                    File makeDir = new File(saveString);
                    makeDir = makeDir.getParentFile();
                    makeDir.mkdirs();
                    IJ.save(montage, saveString);
                    montage.changes = false;
                    montage.close();
                    imageStack.changes = false;
                    imageStack.close();
                    for (ImagePlus image : imageArray) {
                        image.changes = false;
                        image.close();
                    }
                    fileList[0] = makeDir;
                }
                
                if (!countedAnalysisList.isEmpty()) {
                    imageArray = new ImagePlus[countedAnalysisList.size()];
                    for (int i = 0; i < countedAnalysisList.size(); i++) {
                        imageArray[i] = IJ.openImage(countedAnalysisList.get(i).getAnalysisFiles()[0].getAbsolutePath());
                        imageArray[i].flattenStack();
                        ImageProcessor ip = imageArray[i].getProcessor();
                        Font font = new Font("SansSerif", Font.PLAIN, 40);
                        ip.setFont(font);
                        ip.setColor(new Color(255, 255, 255));
                        ip.drawString(countedAnalysisList.get(i).getAnalysisTime().toString(), 40, 40);
                        if (i == 0) ip.drawString(fish.getName(), imageArray[i].getWidth()/2, 40);
                        Roi fishRoi = RoiDecoder.open(countedAnalysisList.get(i).getRois()[0].getAbsolutePath());
                        Roi cellRoi = RoiDecoder.open(countedAnalysisList.get(i).getRois()[1].getAbsolutePath());
                        Overlay overlay = new Overlay(fishRoi);
                        overlay.add(fishRoi);
                        imageArray[i].setOverlay(overlay);
                        imageArray[i] = imageArray[i].flatten();
                        overlay.add(cellRoi);
                        imageArray[i].setOverlay(overlay);
                        imageArray[i] = imageArray[i].flatten();
                    }
                    
                    ImagePlus imageStack = Concatenator.run(imageArray);
                    MontageMaker montageMaker = new MontageMaker();
                    ImagePlus montage = montageMaker.makeMontage2​(imageStack, 1, imageArray.length, 1, 1, imageArray.length, 1, 0, false);
                    String saveString = experiment.getConfocalDirectory().getAbsolutePath()
                    + "\\ProcessedFiles\\Montage"
                    + "\\" + group.getGroupName()
                    + "\\" + fish.getName() + "_CountedMontage.tif";
                    File makeDir = new File(saveString);
                    makeDir = makeDir.getParentFile();
                    makeDir.mkdirs();
                    IJ.save(montage, saveString);
                    montage.changes = false;
                    montage.close();
                    imageStack.changes = false;
                    imageStack.close();
                    for (ImagePlus image : imageArray) {
                        image.changes = false;
                        image.close();
                    }
                    fileList[1] = makeDir;
                }
            }
        }
    }
    private void sendStopMessage(String message) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.finishTask(message);
            }
        });
    }
    private void sendSaveMessage() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.saveExperiment();
            }
        });
    }
    private void sendStartMessage() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setTask("Cell Counting", Constants.TASK_CELLCOUNTING);
            }
        });
    }
    private void createCellCSV() {
        ArrayList<ArrayList<String>> outList = new ArrayList();
        ArrayList<String[]> outLocationList = new ArrayList();
        for(FishGroup group : experiment.getGroups()) {
            for (Fish fish : group.getFishList()){
                ArrayList<ImageAnalysis> countedAnalysisList = new ArrayList();
                ArrayList<String> cellCounts = new ArrayList();
                ArrayList<String> cellLocationsTemp = new ArrayList();
                String[] cellLocations = new String[0];
                cellCounts.add(group.getGroupName());
                cellCounts.add(fish.getName());
                for (Measurement measurement : fish.getMeasurements()) {
                    for (ImageAnalysis analysis : measurement.getAnalyses()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSCOUNTED)) countedAnalysisList.add(analysis);
                    }
                }
                countedAnalysisList.sort(null);
                int largestCellCount = 0;
                ArrayList<Integer> imageWidth = new ArrayList();
                ArrayList<Integer> imageHeight = new ArrayList();
                for (ImageAnalysis analysis : countedAnalysisList) {
                    cellCounts.add(Integer.toString(analysis.getIntStorage()[0]));
                    if (analysis.getIntStorage()[0] > largestCellCount) largestCellCount = analysis.getIntStorage()[0];
                    ImagePlus imp = IJ.openImage(analysis.getAnalysisFiles()[0].getAbsolutePath());
                    imageWidth.add(imp.getWidth());
                    imageHeight.add(imp.getHeight());
                    imp.close();
                }
                for (int i = 0; i < largestCellCount; i++) {
                    cellLocationsTemp.add(group.getGroupName());
                    cellLocationsTemp.add(fish.getName());
                    for (int j = 0; j < countedAnalysisList.size(); j++) {
                        if (i >= countedAnalysisList.get(j).getIntStorage()[0]) {
                            cellLocationsTemp.add ("");
                            cellLocationsTemp.add("");
                        }
                        else {
                            Roi cellRoi = RoiDecoder.open(countedAnalysisList.get(j).getRois()[1].getAbsolutePath());
                            double relativeX = cellRoi.getPolygon().xpoints[i] / (double)imageWidth.get(j);
                            double relativeY =cellRoi.getPolygon().ypoints[i] / (double)imageHeight.get(j);
                            cellLocationsTemp.add(Double.toString(relativeX));
                            cellLocationsTemp.add(Double.toString(relativeY));
                        }
                    }
                    cellLocations =cellLocationsTemp.toArray(new String[0]);
                    cellLocationsTemp.clear();
                    outLocationList.add(cellLocations);
                }
                
                outList.add(cellCounts);
            }
        }
        try {
        FileWriter out = new FileWriter(experiment.getConfocalDirectory().getAbsoluteFile() + "\\ProcessedFiles\\cellCount.csv");
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.EXCEL);
        for (ArrayList<String> fishLine : outList) {
            printer.printRecord(fishLine);
        }
        printer.flush();
        printer.close();
        out.close();
        } catch (IOException ex) {
            Logger.getLogger(MontageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
        FileWriter outSecond = new FileWriter(experiment.getConfocalDirectory().getAbsoluteFile() + "\\ProcessedFiles\\cellLocations.csv");
        CSVPrinter printerSecond = new CSVPrinter(outSecond, CSVFormat.EXCEL);
        for (String[] fishLine : outLocationList) {
            printerSecond.printRecord(fishLine);
        }
        printerSecond.flush();
        printerSecond.close();
        outSecond.close();
        } catch (IOException ex) {
            Logger.getLogger(MontageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
