package experiments;


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
import tools.InstantAdapter;
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
public class MeasurementNew implements Serializable {
    String fileName;
    File confocalFile;
    String relativeConfocalPath;
    Instant acquisitionTime;
    FishNew parent;
    ArrayList<ImageAnalysisNew> analysisList = new ArrayList();
    File fishLocation;
    String relativeFishLocationPath;
    
    public MeasurementNew() {
        
    }
    public MeasurementNew (File confocalFile, FishNew parent) {
        this.confocalFile = confocalFile;
        this.fileName = FilenameUtils.removeExtension(confocalFile.getName());
        setAcquisitionTime(confocalFile);
        this.parent = parent;
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
    public FishNew getParent() {
        return parent;
    }
    public void setParent(FishNew parent) {
        this.parent = parent;
    }

    @XmlElement(name = "Analysis")
    public ArrayList<ImageAnalysisNew> getAnalysisList() {
        return analysisList;
    }
    public void setAnalysisList(ArrayList<ImageAnalysisNew> analysisList) {
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
            Logger.getLogger(MeasurementNew.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Remove
    public void setAnalyses(ArrayList<ImageAnalysisNew> analysisList) {
        this.analysisList = analysisList;
    }
    
    

    public void addAnalysis(int analysisType, File[] fileArray, int[] channelIDs) {
        analysisList.add(new ImageAnalysisNew(analysisType, fileArray, channelIDs, Instant.now(), this));
    }
    public void addAnalysis(ImageAnalysisNew analysis) {
        analysisList.add(analysis);
    }

    

    //Remove
    public void setFishROI(File fishLocation) {
        this.fishLocation = fishLocation;
    }

    void updateParents(FishNew parent) {
        this.parent = parent;
        for(ImageAnalysisNew analysis : analysisList) {
            analysis.setParent(this);
        }
        
    }

    void updateFile(File experimentPath) {
        confocalFile = new File(experimentPath, relativeConfocalPath);
        fishLocation = new File(experimentPath, relativeFishLocationPath);
        
        for (ImageAnalysisNew analysis : analysisList) {
            analysis.updateFile(experimentPath);
        }
    }
    
    
    
}
