/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import GUI.CellAnalysisGUI;
import GUI.MainMenuGUI;
import Containers.Constants;
import Containers.Old.ExperimentOld;
import Containers.Old.SubjectOld;
import Containers.Old.GroupOld;
import Containers.Old.ImageAnalysisOls;
import Containers.Old.MeasurementOld;
import ij.gui.Plot;
import ij.gui.WaitForUserDialog;
import java.util.ArrayList;
import java.util.List;
import mcib3d.geom.Objects3DPopulation;

/**
 *
 * @author janlu
 */
public class CellAnalysator implements Runnable{

    private final CellAnalysisGUI parent;
    private final ExperimentOld experiment;
    private ArrayList<ImageAnalysisOls> analysisList = new ArrayList();
    private Histogram experimentVolumeHistogram;
    private int nBins = 100;
    private int minVolumeGraph = 10;
    private int maxVolumeGraph = 1000;
    private int minViableCellVolume;
    private int maxViableCellVolume;
    
    public CellAnalysator(ExperimentOld experiment,CellAnalysisGUI parent) {
        this.parent = parent;
        this.experiment = experiment;
    }
    
    public synchronized void run(){
        createMeasurementList();
        createExperimentVolumeHistogram();
    }

    private void createMeasurementList() {
        for (GroupOld fishGroup : experiment.getGroups()) {
            for (SubjectOld fish : fishGroup.getFishList()) {
                for (MeasurementOld measurement : fish.getMeasurements()) {
                    for (ImageAnalysisOls analysis : measurement.getAnalyses()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSSEGMENTED)) analysisList.add(analysis);
                    }
                }
            }
        }
    }

    private void createExperimentVolumeHistogram() {
        //experimentVolumeHistogram = new Histogram();
        for (ImageAnalysisOls analysis : analysisList) {
            Objects3DPopulation population = new Objects3DPopulation();
            population.loadObjects(analysis.getRois()[0].getAbsolutePath());
            List objects = population.getMeasuresGeometrical();
            for (Object object : objects) {
                //experimentVolumeHistogram.add(((Double[])object)[2]);
            }
        }
       // experimentVolumeHistogram.fitToRange();
       // experimentVolumeHistogram.calculateHistogram();
        //Plot plot = new Plot("Cell Volume Distribution", "Cell volume (Pix)", "count");
        //plot.addPoints(experimentVolumeHistogram.getHistogram(), experimentVolumeHistogram.getBinCenters(), Plot.BAR);
       // plot.show();
        //WaitForUserDialog dialog = new WaitForUserDialog("asdfa");
        //dialog.show();
    }
    public Histogram getHistogram() {
        return experimentVolumeHistogram;
    }

}
