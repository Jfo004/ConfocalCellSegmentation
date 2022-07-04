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
import javax.xml.bind.annotation.XmlRootElement;
import loci.formats.FormatException;
import loci.formats.in.ImarisHDFReader;
import org.apache.commons.io.FilenameUtils;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author janlu
 */
@XmlRootElement
public class MeasurementNew implements Serializable {
    String fileName;
    File confocalFile;
    Instant acquisitionTime;
    FishNew parent;
    ArrayList<ImageAnalysisNew> analysisList = new ArrayList();
    File fishLocation;
    
    public MeasurementNew() {
        
    }
    
    public MeasurementNew (File confocalFile, FishNew parent) {
        this.confocalFile = confocalFile;
        this.fileName = FilenameUtils.removeExtension(confocalFile.getName());
        setAcquisitionTime(confocalFile);
        this.parent = parent;
    }

    public String getFileName() {
        return fileName;
    }

    public File getConfocalFile() {
        return confocalFile;
    }

    public Instant getAcquisitionTime() {
        return acquisitionTime;
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

    public ArrayList<ImageAnalysisNew> getAnalyses() {
        return analysisList;
    }
    
    public void setAnalyses(ArrayList<ImageAnalysisNew> analysisList) {
        this.analysisList = analysisList;
    }
    
    public FishNew getParentFish() {
        return parent;
    }

    public void addAnalysis(int analysisType, File[] fileArray, int[] channelIDs) {
        analysisList.add(new ImageAnalysisNew(analysisType, fileArray, channelIDs, Instant.now(), this));
    }
    public void addAnalysis(ImageAnalysisNew analysis) {
        analysisList.add(analysis);
    }

    public File getFishROI() {
        return fishLocation;
    }

    public void setFishROI(File fishLocation) {
        this.fishLocation = fishLocation;
    }
    
    
    
}
