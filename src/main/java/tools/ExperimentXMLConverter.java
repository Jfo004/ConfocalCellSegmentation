/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import GUI.MainMenuGUI;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author Jan-Lukas Foerde
 */
public class ExperimentXMLConverter {

    Experiment experimentOld;
    ExperimentNew experimentNew = new ExperimentNew();
    File newLocation;
    
    public ExperimentXMLConverter(Experiment experiment) {
        this.experimentOld = experiment;
    }
    
    public ExperimentXMLConverter() {
        experimentOld = importExperiment();
    }
    
    public ExperimentNew convert() {
        if(experimentOld == null) {
            System.out.println(" Old experiment not found");
            return null;
        }
        if (newLocation == null) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) throw new UnsupportedOperationException("Not supported yet.");
            newLocation = fileChooser.getSelectedFile();
        }
        
        experimentNew.setName(experimentOld.getName());
        experimentNew.setConfocalDirectory(newLocation.getParentFile());
        experimentNew.setTimeOfFertilization(experimentOld.getTimeOfFertilization());
        experimentNew.setTimeOfInjection(experimentOld.getTimeOfInjection());
        experimentNew.setGroupCount(experimentOld.getGroupCount());
        experimentNew.setFishCount(experimentOld.getFishCount());
        experimentNew.setImageCount(experimentOld.getImageCount());
        
        ArrayList<FishGroupNew> groupList = new ArrayList();
        for(FishGroup groupOld : experimentOld.getGroups()) {
            FishGroupNew groupNew = new FishGroupNew();
            groupNew.setGroupName(groupOld.getGroupName());
            groupNew.setParent(experimentNew);
            
            ArrayList<FishNew> fishList = new ArrayList();
            for(Fish fishOld : groupOld.getFishList()) {
                FishNew fishNew = new FishNew();
                fishNew.setName(fishOld.getName());
                fishNew.setParentFishGroup(groupNew);
                
                ArrayList<MeasurementNew> measurementList = new ArrayList();
                for(Measurement measurementOld : fishOld.getMeasurements()) {
                    MeasurementNew measurementNew = new MeasurementNew();
                    measurementNew.setFileName(measurementOld.getFileName());
                    measurementNew.setAcquisitionTime(measurementOld.getAcquisitionTime());
                    measurementNew.setParent(fishNew);
                    
                    measurementNew.setRelativeConfocalPath(getRelativePath(experimentOld.getConfocalDirectory(), measurementOld.getConfocalFile()));
                    measurementNew.setConfocalFile(measurementOld.getConfocalFile());
                    measurementNew.setFishLocation(measurementOld.getFishROI());
                    measurementNew.setRelativeFishLocationPath(getRelativePath(experimentOld.getConfocalDirectory(), measurementOld.getFishROI()));
                    
                    ArrayList<ImageAnalysisNew> analysisList = new ArrayList();
                    for(ImageAnalysis analysisOld : measurementOld.getAnalyses()) {
                        ImageAnalysisNew analysisNew = new ImageAnalysisNew();
                        analysisNew.setAnalysisType(analysisOld.getAnalysisType());
                        analysisNew.setAnalysisName(analysisOld.getAnalysisName());
                        analysisNew.setParent(measurementNew);
                        analysisNew.setAnalysisTime(analysisOld.getAnalysisTime());
                        analysisNew.setChannelIDs(analysisOld.getChannelIDs());
                        analysisNew.setIntStorage(analysisNew.getIntStorage());
                        
                        analysisNew.setAnalysisFiles(analysisOld.getAnalysisFiles());
                        analysisNew.setRelativeAnalysisFiles(createRelativeList(analysisOld.getAnalysisFiles()));
                        analysisNew.setRois(analysisOld.getRois());
                        analysisNew.setRelativeRois(createRelativeList(analysisOld.getRois()));
                        
                        analysisList.add(analysisNew);
                    }
                    measurementNew.setAnalyses(analysisList);
                    measurementList.add(measurementNew);
                }
                fishNew.setMeasurements(measurementList);
                fishList.add(fishNew);
            }
            groupNew.setFishList(fishList);
            groupList.add(groupNew);
        }
        experimentNew.setGroups(groupList);

        return experimentNew;
    }
    
    private String[] createRelativeList(File[] fileArray) {
        if(fileArray == null) return null;
        String[] stringArray = new String[fileArray.length];
        for(int i = 0; i < stringArray.length; i++) {
            stringArray[i] = getRelativePath(experimentOld.getConfocalDirectory(), fileArray[i]);
        }
        return stringArray;
    }
    
    private String getRelativePath(File mainDir, File subDir) {
        Path mainPath = Paths.get(mainDir.getAbsolutePath());
        Path subPath = Paths.get(subDir.getAbsolutePath());
        return mainPath.relativize(subPath).toString();
    }

    private Experiment importExperiment() {
        Experiment experimentImported;
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                newLocation = fileChooser.getSelectedFile();
                System.out.println("Importing: " + newLocation.getAbsolutePath());
                FileInputStream fileInStream = new FileInputStream(newLocation);
                ObjectInputStream objectInStream = new ObjectInputStream(fileInStream);
                experimentImported = (Experiment) objectInStream.readObject();
                objectInStream.close();
                fileInStream.close();
            } catch (IOException ex) {
                Logger.getLogger(ExperimentXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ExperimentXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            
            return experimentImported;
        }
        System.out.println("No file chosen");
        return null;
    }
    
}
