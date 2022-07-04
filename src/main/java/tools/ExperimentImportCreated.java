/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import experiments.Constants;
import experiments.Experiment;
import experiments.Fish;
import experiments.FishGroup;
import experiments.Measurement;
import java.io.File;
import java.time.Instant;
import java.util.HashMap;

/**
 *
 * @author janlu
 */
public class ExperimentImportCreated {
    Experiment experiment;
    File confocalDir;
    String name;
    Instant timeOfFertilization;
    Instant timeOfInjection;
    HashMap groupMap = new HashMap();
    
    public ExperimentImportCreated (File confocalDir, String name, Instant timeOfFertilization, Instant timeOfInjection ) {
        this.confocalDir = confocalDir;
        this.name = name;
        this.timeOfFertilization = timeOfFertilization;
        this.timeOfInjection = timeOfInjection;
    }
    public Experiment createExperiment() {
        constructHashMap();
        if (groupMap.isEmpty()) return null;
        experiment = new Experiment(name, confocalDir, timeOfFertilization, timeOfInjection, groupMap);
        addAnalyses();
        return experiment;
    }

    private void addFlattened(File analysisDir) {
        File[] fileList = analysisDir.listFiles();
        
    }

    private void constructHashMap() {
        File flattenedDirectory = null;
        flattenedSearch:
        for (File testProcessedDir : confocalDir.listFiles()) {
            if ("ProcessedFiles".equals(testProcessedDir.getName())) {
                for (File testFlattenedDir : testProcessedDir.listFiles()) {
                    System.out.println("Checking: " + testFlattenedDir.getName());
                    if ("Flattened".equals(testFlattenedDir.getName())) {
                        flattenedDirectory = testFlattenedDir;
                        System.out.println("Flattened dir found");
                        continue flattenedSearch;
                    }
                }
            }
        }
        if (flattenedDirectory == null) {
            System.out.println("Flattened dir not found");
            return;
        }
        
        for (File groupDirs : flattenedDirectory.listFiles()) {
            for (File fishDir : groupDirs.listFiles()) {
               File firstMeasurement = fishDir.listFiles()[0];
               if(firstMeasurement == null) {
                   System.out.println("No measurement found for: " + fishDir.getName());
                   continue;
               }
               String wellID = firstMeasurement.getName().substring(0, firstMeasurement.getName().indexOf("_"));
               groupMap.put(wellID, fishDir.getName());
               System.out.println("Added wellID: " + wellID + " To " + fishDir.getName());
            }
        }
        
    }

    private void addAnalyses() {
        File flattenedDir = new File(confocalDir.getAbsoluteFile() + "\\ProcessedFiles\\Flattened");
        for (FishGroup group : experiment.getGroups()) {
            for (Fish fish : group.getFishList()) {
                for (Measurement measurement : fish.getMeasurements()) {
                    for (File groupDir : flattenedDir.listFiles()) {
                        for (File fishDir : groupDir.listFiles()) {
                            for (File measurementDir : fishDir.listFiles()) {
                                if (measurement.getFileName().equalsIgnoreCase(measurementDir.getName())) {
                                    measurement.addAnalysis(Constants.ANALYSIS_FLATTENED, measurementDir.listFiles(), new int[] {0,1,-1});
                                    System.out.println("Added analysis to :" + measurement.getFileName());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
