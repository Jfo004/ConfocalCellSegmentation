/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments;

import ij.gui.Roi;
import java.io.File;
import java.io.Serializable;
import java.time.Instant;

/**
 *
 * @author janlu
 */
public class ImageAnalysisNew implements Serializable, Comparable{
    
    int analysisType;
    File[] analysisFiles;
    String analysisName;
    MeasurementNew parent;
    Instant analysisTime;
    File[] rois;
    int[] channelIDs;
    int[] intStorage;

    public ImageAnalysisNew(int analysisType, File[] fileArray, int[] channelIDs, Instant analysisTime, MeasurementNew parent) {
        this.analysisType = analysisType;
        this.analysisFiles = fileArray;
        this.analysisTime = analysisTime;
        this.parent = parent;
        this.channelIDs = channelIDs;
        this.analysisName = Constants.analysisNames[analysisType] + "_" + parent.getFileName();
    }

    public boolean isAnalysisType(int analysisType) {
        return (this.analysisType == analysisType);
    }

    public int getAnalysisType() {
        return analysisType;
    }

    public File[] getAnalysisFiles() {
        return analysisFiles;
    }

    public String getAnalysisName() {
        return analysisName;
    }

    public MeasurementNew getParentMeasurement() {
        return parent;
    }

    public Instant getAnalysisTime() {
        return analysisTime;
    }

    public int[] getChannelIDs() {
        return channelIDs;
    }

    public MeasurementNew getParent() {
        return parent;
    }

    public File[] getRois() {
        return rois;
    }

    public void setRois(File[] rois) {
        this.rois = rois;
    }

    public int[] getIntStorage() {
        return intStorage;
    }

    public void setIntStorage(int[] intStorage) {
        this.intStorage = intStorage;
    }

    @Override
    public int compareTo(Object secondAnalysis) {
        return this.analysisTime.compareTo(((ImageAnalysisNew)secondAnalysis).getAnalysisTime());
    }
    
    
    
    
}
