package Containers.Old;


import Containers.Old.SubjectOld;
import Containers.Old.ImageAnalysisOls;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class MeasurementOld implements Serializable {
    String fileName;
    File confocalFile;
    Instant acquisitionTime;
    SubjectOld parent;
    ArrayList<ImageAnalysisOls> analysisList = new ArrayList();
    File fishLocation;
    
    MeasurementOld (File confocalFile, SubjectOld parent) {
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
            Logger.getLogger(MeasurementOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<ImageAnalysisOls> getAnalyses() {
        return analysisList;
    }
    
    public SubjectOld getParentFish() {
        return parent;
    }

    public void addAnalysis(int analysisType, File[] fileArray, int[] channelIDs) {
        analysisList.add(new ImageAnalysisOls(analysisType, fileArray, channelIDs, Instant.now(), this));
    }
    public void addAnalysis(ImageAnalysisOls analysis) {
        analysisList.add(analysis);
    }

    public File getFishROI() {
        return fishLocation;
    }

    public void setFishROI(File fishLocation) {
        this.fishLocation = fishLocation;
    }
    
    
    
}
