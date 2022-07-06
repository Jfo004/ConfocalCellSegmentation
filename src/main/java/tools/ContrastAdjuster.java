/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

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
import ij.gui.WaitForUserDialog;

/**
 *TODO clean up process
 * TODO block mainGUI
 * TODO update analysis name
 * @author janlu
 */
public class ContrastAdjuster implements Runnable{
        double channelIntensityMax;
        double channelIntensityMin;
        double[] compositeIntensitiesMax;
        double[] compositeIntensitiesMin;
        ExperimentNew experiment;
        ImageAnalysisNew controllAnalysis;
        
    public ContrastAdjuster(ExperimentNew experiment, ImageAnalysisNew controllAnalysis) {
        this.experiment = experiment;
        this.controllAnalysis = controllAnalysis;

    }
    
    @Override
    public void run() {
        //Single Channels
        int compositeIndex = controllAnalysis.getChannelIDs().length -1;
        int channelCount = compositeIndex;
        for (int i = 0; i < channelCount; i++) {
            String imagePath = controllAnalysis.getAnalysisFiles()[i].getAbsolutePath();
            if (!imagePath.endsWith(".tif")) imagePath = imagePath.concat(".tif");
            
            ImagePlus controllImage = IJ.openImage(imagePath);
            controllImage.show();
            WaitForUserDialog dialog = new WaitForUserDialog("Adjust Channels");
            dialog.show();
            channelIntensityMax = controllImage.getDisplayRangeMax();
            channelIntensityMin = controllImage.getDisplayRangeMin();
            controllImage.close();
            for (FishGroupNew group : experiment.getGroups()) {
                for (FishNew fish : group.getFishList()) {
                    for (MeasurementNew measurement : fish.getMeasurements()) {
                        for (ImageAnalysisNew analysis : measurement.getAnalysisList()) {
                            if (!analysis.isAnalysisType(Constants.ANALYSIS_FLATTENED)) {
                                System.out.println("Skipped analysis: " + analysis.getAnalysisName());
                                continue;
                            }
                            if (i > analysis.getAnalysisFiles().length -1){
                                System.out.println("Out of index: " + i + " in " + analysis.getAnalysisName());
                            }
                            imagePath = analysis.getAnalysisFiles()[i].getAbsolutePath();
                            if (!imagePath.endsWith(".tif")) imagePath = imagePath.concat(".tif");
                            ImagePlus image = IJ.openImage(imagePath);
                            image.setDisplayRange(channelIntensityMin, channelIntensityMax);
                            IJ.saveAsTiff(image, analysis.getAnalysisFiles()[i].getAbsolutePath());
                            image.close();
                            System.out.println("Done with image: " + i + " in " + analysis.getAnalysisName());
                        }
                    }
                }
            }  
        }
        String imagePath = controllAnalysis.getAnalysisFiles()[compositeIndex].getAbsolutePath();
        if (!imagePath.endsWith(".tif")) imagePath = imagePath.concat(".tif");
        ImagePlus controllImage = IJ.openImage(imagePath);
        controllImage.show();
        WaitForUserDialog dialog = new WaitForUserDialog("Adjust Channels");
        dialog.show();
        compositeIntensitiesMax = new double[controllImage.getNChannels()];
        compositeIntensitiesMin = new double[controllImage.getNChannels()];
        for (int i = 1; i <= controllImage.getNChannels(); i++) {
            controllImage.setC(i);
            compositeIntensitiesMax[i-1] = controllImage.getDisplayRangeMax();
            compositeIntensitiesMin[i-1] = controllImage.getDisplayRangeMin();
        }
        controllImage.close();
        for (FishGroupNew group : experiment.getGroups()) {
            for (FishNew fish : group.getFishList()) {
                for (MeasurementNew measurement : fish.getMeasurements()) {
                    for (ImageAnalysisNew analysis : measurement.getAnalysisList()) {
                        if (!analysis.isAnalysisType(Constants.ANALYSIS_FLATTENED)) {
                            System.out.println("Skipped analysis(Comp): " + analysis.getAnalysisName());
                            continue;
                        }
                        if (compositeIndex > analysis.getAnalysisFiles().length -1){
                            System.out.println("Out of index(Comp): " + compositeIndex + " in " + analysis.getAnalysisName());
                        }
                        imagePath = analysis.getAnalysisFiles()[compositeIndex].getAbsolutePath();
                        if (!imagePath.endsWith(".tif")) imagePath = imagePath.concat(".tif");
                        ImagePlus image = IJ.openImage(imagePath);

                        for (int i = 1; i <= image.getNChannels(); i++) {
                            image.setC(i);
                            image.setDisplayRange(compositeIntensitiesMin[i-1], compositeIntensitiesMax[i-1]);
                        }

                        IJ.saveAsTiff(image, analysis.getAnalysisFiles()[compositeIndex].getAbsolutePath());
                        image.close();
                        System.out.println("Done with comp in " + analysis.getAnalysisName());
                    }
                }
            }
        }
    System.out.println("Done with contrast adjustment");  
    }
}
