package Containers.Experiment;


import ij.gui.Roi;
import ij.gui.ShapeRoi;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import loci.formats.FormatException;
import loci.formats.in.ImarisHDFReader;
import org.apache.commons.io.FilenameUtils;
import importexport.FileType;
import importexport.InstantAdapter;
import java.nio.file.Path;
import java.nio.file.Paths;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author janlu
 */
@XmlRootElement(name = "Measurement")
public class Measurement implements Serializable {
    String fileName;
    File confocalFile;
    int frame;
    String relativeConfocalPath;
    Instant acquisitionTime;
    Subject parent;
    ArrayList<ImageAnalysis> analysisList = new ArrayList();
    File fishLocation;
    String relativeFishLocationPath;
    
    
    public Measurement() {
        
    }
    public Measurement (File confocalFile, Subject parent) {
        this.confocalFile = confocalFile;
        this.fileName = FilenameUtils.removeExtension(confocalFile.getName());
        setAcquisitionTime(confocalFile);
        this.parent = parent;
    }

    public FileType getFileType() {
        return this.getParent().getParentFishGroup().getParent().getFileType();
    }    

    @XmlAttribute(name = "File_Name")
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @XmlTransient
    public File getConfocalFile() {
        return confocalFile;
    }
    public void setConfocalFile(File confocalFile) {
        this.confocalFile = confocalFile;
    }

    @XmlAttribute(name = "Frame")
    public int getFrame() {
        return frame;
    }
    public void setFrame(int frame) {
        this.frame = frame;
    }

    
    
    @XmlJavaTypeAdapter(InstantAdapter.class)
    @XmlAttribute(name = "Acquisition_time")
    public Instant getAcquisitionTime() {
        return acquisitionTime;
    }
    public void setAcquisitionTime(Instant acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }

    @XmlAttribute(name = "Relative_path")
    public String getRelativeConfocalPath() {
        return relativeConfocalPath;
    }
    public void setRelativeConfocalPath(String relativeConfocalPath) {
        this.relativeConfocalPath = relativeConfocalPath;
    }

    @XmlAttribute(name = "Roi_file")
    public String getRelativeFishLocationPath() {
        return relativeFishLocationPath;
    }
    public void setRelativeFishLocationPath(String relativeFishLocationPath) {
        this.relativeFishLocationPath = relativeFishLocationPath;
    }
    
    @XmlTransient
    public Subject getParent() {
        return parent;
    }
    public void setParent(Subject parent) {
        this.parent = parent;
    }

    @XmlElement(name = "Analysis")
    public ArrayList<ImageAnalysis> getAnalysisList() {
        return analysisList;
    }
    public void setAnalysisList(ArrayList<ImageAnalysis> analysisList) {
        this.analysisList = analysisList;
    }

    @XmlTransient
    public File getFishLocation() {
        return fishLocation;
    }
    public void setFishLocation(File fishLocation) {
        this.fishLocation = fishLocation;
    }
    
    
    private void setAcquisitionTime(File confocalFile) {
        ImarisHDFReader reader = new ImarisHDFReader();
        try {
            reader.setId(confocalFile.getAbsolutePath());
            String tempTime = (String)reader.getMetadataValue("DateAndTime");
            tempTime = tempTime.replace(" ", "T").concat("Z");
            this.acquisitionTime = Instant.parse(tempTime);
        } catch (FormatException | IOException ex) {
            Logger.getLogger(Measurement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Remove
    public void setAnalyses(ArrayList<ImageAnalysis> analysisList) {
        this.analysisList = analysisList;
    }
    
    

    public void addAnalysis(int analysisType, File[] fileArray, int[] channelIDs) {
        analysisList.add(new ImageAnalysis(analysisType, fileArray, channelIDs, Instant.now(), this));
    }
    public void addAnalysis(ImageAnalysis analysis) {
        analysisList.add(analysis);
    }

    

    //Remove
    public void setFishROI(File fishLocation) {
        this.fishLocation = fishLocation;
    }

    void updateParents(Subject parent) {
        this.parent = parent;
        for(ImageAnalysis analysis : analysisList) {
            analysis.setParent(this);
        }
        
    }

    void updateFile(File experimentPath) {
        //if(relativeConfocalPath == null ) confocalFile = new File(experimentPath, fileName + ".ims");
        confocalFile = new File(experimentPath, relativeConfocalPath);
        
        if(relativeFishLocationPath != null) fishLocation = new File(experimentPath, relativeFishLocationPath);
        for (ImageAnalysis analysis : analysisList) {
            analysis.updateFile(experimentPath);
        }
    }

    void createRelativeFilePath() {
        relativeConfocalPath = getRelativePath(parent.getParentFishGroup().getParent().getConfocalDirectory(), confocalFile);
        if (fishLocation != null) relativeFishLocationPath = getRelativePath(parent.getParentFishGroup().getParent().getConfocalDirectory(), fishLocation);
        for (ImageAnalysis analysis : analysisList) {
            analysis.createRelativeFilePath();
        }
    }
    
    private String getRelativePath(File mainDir, File subDir) {
        Path mainPath = Paths.get(mainDir.getAbsolutePath());
        Path subPath = Paths.get(subDir.getAbsolutePath());
        return mainPath.relativize(subPath).toString();
    }
    
    
    
}
