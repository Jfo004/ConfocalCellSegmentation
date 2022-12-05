/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Containers.Experiment;

import Containers.Constants;
import ij.gui.Roi;
import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import importexport.InstantAdapter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author janlu
 */
@XmlRootElement(name = "Analysis")
public class ImageAnalysis implements Serializable, Comparable{
    
    int analysisType;
    File[] analysisFiles;
    String[] relativeAnalysisFiles;
    String analysisName;
    Measurement parent;
    Instant analysisTime;
    File[] rois;
    String[] relativeRois;
    int[] channelIDs;
    int[] intStorage;

    public ImageAnalysis(int analysisType, File[] fileArray, int[] channelIDs, Instant analysisTime, Measurement parent) {
        this.analysisType = analysisType;
        this.analysisFiles = fileArray;
        this.analysisTime = analysisTime;
        this.parent = parent;
        this.channelIDs = channelIDs;
        this.analysisName = Constants.analysisNames[analysisType] + "_" + parent.getFileName();
    }
    public ImageAnalysis() {
    }

    @XmlAttribute(name = "Analysis_type")
    public int getAnalysisType() {
        return analysisType;
    }
    public void setAnalysisType(int analysisType) {
        this.analysisType = analysisType;
    }

    @XmlTransient
    public File[] getAnalysisFiles() {
        return analysisFiles;
    }
    public void setAnalysisFiles(File[] analysisFiles) {
        this.analysisFiles = analysisFiles;
    }

    @XmlElement(name = "File")
    public String[] getRelativeAnalysisFiles() {
        return relativeAnalysisFiles;
    }
    public void setRelativeAnalysisFiles(String[] relativeAnalysisFiles) {
        this.relativeAnalysisFiles = relativeAnalysisFiles;
    }

    @XmlElement(name = "ROI")
    public String[] getRelativeRois() {
        return relativeRois;
    }
    public void setRelativeRois(String[] relativeRois) {
        this.relativeRois = relativeRois;
    }  
    
    @XmlAttribute(name = "Analysis_ID")
    public String getAnalysisName() {
        return analysisName;
    }
    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }

    @XmlTransient
    public Measurement getParent() {
        return parent;
    }
    public void setParent(Measurement parent) {
        this.parent = parent;
    }

    @XmlJavaTypeAdapter(InstantAdapter.class)
    @XmlAttribute(name = "Analysis_Time")
    public Instant getAnalysisTime() {
        return analysisTime;
    }
    public void setAnalysisTime(Instant analysisTime) {
        this.analysisTime = analysisTime;
    }

    @XmlElement(name = "Channel_ID")
    public int[] getChannelIDs() {
        return channelIDs;
    }
    public void setChannelIDs(int[] channelIDs) {
        this.channelIDs = channelIDs;
    }

    @XmlElement(name = "Int")
    public int[] getIntStorage() {
        return intStorage;
    }
    public void setIntStorage(int[] intStorage) {
        this.intStorage = intStorage;
    }
    
    @XmlTransient
    public File[] getRois() {
        return rois;
    }
    public void setRois(File[] rois) {
        this.rois = rois;
    }
    
    public boolean isAnalysisType(int analysisType) {
        return (this.analysisType == analysisType);
    }  

    @Override
    public int compareTo(Object secondAnalysis) {
        return this.analysisTime.compareTo(((ImageAnalysis)secondAnalysis).getAnalysisTime());
    }

    void updateFile(File experimentPath) {
        //TEMP
//        analysisFiles = new File[3];
//        analysisFiles[0] = new File(experimentPath, "ProcessedFiles\\Flattened\\" + parent.getParent().getParentFishGroup().getGroupName() + "\\" + parent.getParent().getName() + "\\" + parent.fileName + "\\" + parent.fileName + "_Channel 0_BF.tif");
//        analysisFiles[1] = new File(experimentPath, "ProcessedFiles\\Flattened\\" + parent.getParent().getParentFishGroup().getGroupName() + "\\" + parent.getParent().getName() + "\\" + parent.fileName + "\\" + parent.fileName + "_Channel 1.tif");
//        analysisFiles[2] = new File(experimentPath, "ProcessedFiles\\Flattened\\" + parent.getParent().getParentFishGroup().getGroupName() + "\\" + parent.getParent().getName() + "\\" + parent.fileName + "\\" + parent.fileName + "_Comp.tif");
       
        if(relativeAnalysisFiles != null ) {
            analysisFiles = new File[relativeAnalysisFiles.length];
            for (int i = 0; i < analysisFiles.length; i++) {
                analysisFiles[i] = new File(experimentPath, relativeAnalysisFiles[i]);
            }
        }
        
        if(relativeRois != null) {
            rois = new File[relativeRois.length];
            for (int i = 0; i < rois.length; i++) {
                rois[i] = new File(experimentPath, relativeRois[i]);
            }
        }
    }

    void createRelativeFilePath() {
        if (analysisFiles != null) {
            relativeAnalysisFiles = new String[analysisFiles.length];
            for (int i = 0; i < analysisFiles.length; i++) {
                relativeAnalysisFiles[i] = getRelativePath(parent.getParent().getParentFishGroup().getParent().getConfocalDirectory(), analysisFiles[i]);
            }
        }
        if (rois != null) {
            relativeRois = new String[rois.length];
            for (int i = 0; i < rois.length; i++) {
                relativeRois[i] = getRelativePath(parent.getParent().getParentFishGroup().getParent().getConfocalDirectory(), rois[i]);
            }
        }
        
        
    }
    private String getRelativePath(File mainDir, File subDir) {
        Path mainPath = Paths.get(mainDir.getAbsolutePath());
        Path subPath = Paths.get(subDir.getAbsolutePath());
        return mainPath.relativize(subPath).toString();
    }
    
    
    
    
    
}
