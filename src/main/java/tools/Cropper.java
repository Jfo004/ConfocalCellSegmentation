/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import GUI.MainMenuGUI;
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
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.io.RoiEncoder;
import ij.measure.ResultsTable;
import ij.plugin.RoiRotator;
import ij.plugin.filter.EDM;
import ij.plugin.filter.MaximumFinder;
import ij.plugin.filter.Transformer;
import ij.plugin.frame.RoiManager;
import ij.process.BinaryProcessor;
import ij.process.ImageProcessor;
import ij.plugin.RoiScaler;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import javax.swing.SwingUtilities;


/**
 *
 * @author janlu
 */
public class Cropper implements Runnable{
    ExperimentNew experiment;
    ArrayList<ImageAnalysisNew> analysisList = new ArrayList();
    MainMenuGUI parent;
    ArrayList<File> fileList = new ArrayList();
    public Cropper(ExperimentNew experiment, MainMenuGUI parent) {
        this.experiment = experiment;
        this.parent = parent;
    }
    
    public void run() {
        sendStartMessage();
        createAnalysisList();
        performCropping();
        sendStopMessage("Cropping");
    }

    private void createAnalysisList() {
        for(FishGroupNew group : experiment.getGroups()) {
            for (FishNew fish : group.getFishList()){
                for (MeasurementNew measurement : fish.getMeasurements()) {
                    ImageAnalysisNew tempAnalysis = null;
                    boolean hasFlattened = false;
                    boolean hasCropped = false;
                    for (ImageAnalysisNew analysis : measurement.getAnalysisList()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_FLATTENED)) {
                            hasFlattened = true;
                            tempAnalysis = analysis;
                        }
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CROPPED)) hasCropped = true;
                    }
                    if(hasFlattened && !hasCropped) analysisList.add(tempAnalysis);
                }
            }
        }
    }

    private void performCropping() {
        RoiManager rm = new RoiManager();
        for (int i = 0; i < analysisList.size(); i++) {
            rm.reset();
            Roi selection;
            double ferretAngle;
            int initialImageX;
            int initialImageY;
            int finalImageX;
            int finalImageY;
            double maximumPosX;
            double maximumPosY;
            String openingString = analysisList.get(i).getAnalysisFiles()[analysisList.get(i).getAnalysisFiles().length -1 ].getAbsolutePath();
            if (!openingString.endsWith(".tif")) openingString = openingString.concat(".tif");
            ImagePlus image = IJ.openImage(openingString);
            initialImageX = image.getWidth();
            initialImageY = image.getHeight();
            image.show();
            WaitForUserDialog dialog = new WaitForUserDialog("Select Fish");
            dialog.show();
            image.hide();
            selection = image.getRoi();
            if (selection == null) {
                IJ.showMessage("No selection");
                i--;
                continue;
            }
            saveRoi(analysisList.get(i).getParent(), selection);
            
            ferretAngle = selection.getFeretValues()[1];
            
            image.deleteRoi();
            
            IJ.run(image, "Rotate... ", "angle=" + ferretAngle + " grid=0 interpolation=Bilinear enlarge");
            rm.add(selection, 1);
            rm.select(0);
            rm.translate((image.getWidth() - initialImageX)/(double)2, (image.getHeight()- initialImageY)/(double)2);
            selection = rm.getRoi(0);
            selection = RoiRotator.rotate(selection, ferretAngle, image.getWidth()/(double)2, image.getHeight()/(double)2);
            image.setRoi(selection);
            image = image.crop();
            
            ImagePlus roiMask = new ImagePlus("roiMask", selection.getMask());
            EDM edm = new EDM();
            edm.toEDM(roiMask.getProcessor());
            MaximumFinder maximumFinder = new MaximumFinder();
            maximumFinder.findMaxima(roiMask.getProcessor(), 10, MaximumFinder.LIST, true);
            ResultsTable resultsTable = ResultsTable.getResultsTable("Results");
            
            if (resultsTable.size() >0) {
                maximumPosX = resultsTable.getValueAsDouble(0, 0);
                maximumPosY = resultsTable.getValueAsDouble(1, 0);
                System.out.println("Maximum found for analysis: " + analysisList.get(i).getAnalysisName() + " At X: " + maximumPosX + " Y: " + maximumPosY);
            }
            else{
                maximumPosX = 0;
                maximumPosY = 0;
                System.out.println("No maximum found for analysis: " + analysisList.get(i).getAnalysisName());
            }
            resultsTable.reset();
            
            if(maximumPosX > (image.getWidth()/2)) {
                for (int j = 1; j <= image.getNChannels(); j++) {
                    image.setC(j);
                    image.getProcessor().flipHorizontal();
                    selection = RoiScaler.scale(selection, -1, 1, true);
                    System.out.println("Flipped horizontally: " + analysisList.get(i).getAnalysisName());
                }
            }
            if(maximumPosY < (image.getHeight()/2)) {
                for (int j = 1; j <= image.getNChannels(); j++) {
                    image.setC(j);
                    image.getProcessor().flipVertical();
                    selection = RoiScaler.scale(selection, 1, -1, true);
                    System.out.println("Flipped vertically: " + analysisList.get(i).getAnalysisName());
                }
            }
            
            String saveString = analysisList.get(i).getParent().getConfocalFile().getParent() 
                    + "\\ProcessedFiles\\Cropped"
                    + "\\" + analysisList.get(i).getParent().getParent().getParentFishGroup().getGroupName() 
                    + "\\" + analysisList.get(i).getParent().getParent().getName()
                    + "\\" + analysisList.get(i).getParent().getFileName() 
                    + "\\" + image.getTitle() + "_CROPPED.tif";
            File makeDir = new File(saveString);
            makeDir = makeDir.getParentFile();
            makeDir.mkdirs();
            IJ.save(image, saveString);
            image.changes = false;
            image.close();
            ImageAnalysisNew croppedAnalysis = new ImageAnalysisNew(Constants.ANALYSIS_CROPPED, new File[]{new File(saveString)}, new int[] {-1}, Instant.now(), analysisList.get(i).getParent());
            saveRoi(croppedAnalysis, selection);
            analysisList.get(i).getParent().addAnalysis(croppedAnalysis);
            
            sendSaveMessage();
            if (Thread.interrupted()) {
                sendStopMessage("Cropping - aborted");
                rm.close();
                return;
            }
        }
        rm.close();
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
                parent.setTask("Cropping", Constants.TASK_CROPPING);
            }
        });
    }

    private void saveRoi(MeasurementNew measurement, Roi selection) {
        File roiPath = new File(measurement.getConfocalFile().getParent() + "\\ROIs\\" + measurement.getFileName() + "_FISHROI.roi");
        roiPath.getParentFile().mkdirs();
        if (!RoiEncoder.save(selection, roiPath.getAbsolutePath())) {
            System.out.println("First ROI write failed for: " + measurement.getFileName());
            if (!RoiEncoder.save(selection, roiPath.getAbsolutePath())) {
                System.out.println("Second ROI write failed for: " + measurement.getFileName());
                return;
            }
        };
        measurement.setFishROI(roiPath);
    }
    private void saveRoi(ImageAnalysisNew analysis, Roi selection) {
        File roiPath = new File(analysis.getParent().getConfocalFile().getParent() + "\\ROIs\\" + analysis.getAnalysisName() + "_FISHROI.roi");
        roiPath.getParentFile().mkdirs();
        if (!RoiEncoder.save(selection, roiPath.getAbsolutePath())) {
            System.out.println("First ROI write failed for: " + analysis.getAnalysisName());
            if (!RoiEncoder.save(selection, roiPath.getAbsolutePath())) {
                System.out.println("Second ROI write failed for: " + analysis.getAnalysisName());
                return;
            }
        };
        analysis.setRois(new File[] {roiPath});
    }
    
}
