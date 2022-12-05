/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import Containers.Cells.Cell;
import Containers.Cells.CellHolder;
import Containers.Cells.CellDay;
import Containers.Cells.CellGroup;
import GUI.CellAnalysisGUI;
import Containers.Constants;
import Containers.Old.ExperimentOld;
import Containers.Old.SubjectOld;
import Containers.Old.GroupOld;
import Containers.Old.ImageAnalysisOls;
import Containers.Old.MeasurementOld;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ChannelSplitter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;
import javax.swing.SwingUtilities;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageByte;
import mcib3d.image3d.ImageFloat;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageShort;

/**
 *
 * @author janlu
 */
public class CellImporter implements Runnable{
    ExperimentOld experiment;
    CellHolder cellHolder;
    CellAnalysisGUI parent;
    private int completed = 0;
    private int total = 0;
    private String currentFile;
    
    public CellImporter(ExperimentOld experiment, CellAnalysisGUI parent){
        this.experiment = experiment;
        this.parent = parent;
    }
    public void run() {
        countAnalyses();
        createCellHolder();
        sendCellHolder();
    }

    private void createCellHolder() {
        this.cellHolder = new CellHolder();
        for (GroupOld fishGroup : experiment.getGroups()) {
            CellGroup cellGroup = new CellGroup(fishGroup.getGroupName());
            for (SubjectOld fish : fishGroup.getFishList()) {
                CellSubject cellFish = new CellSubject(fish.getName());
                for (MeasurementOld measurement : fish.getMeasurements()) {
                    for (ImageAnalysisOls analysis : measurement.getAnalyses()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSSEGMENTED)) {
                            //send update
                            currentFile = "Fish: " + cellFish.toString() + " Time: " + analysis.getAnalysisTime().toString();
                            sendProgressUpdate();
                            
                            //Create new aquisition
                            CellDay cellDay = new CellDay(analysis.getAnalysisTime(), analysis.getAnalysisFiles()[0]);
                            
                            //read information for all objects
                            //Import objects
                            Objects3DPopulation cellPopulation = new Objects3DPopulation();
                            cellPopulation.loadObjects(analysis.getRois()[0].getAbsolutePath());
                            
                            //Import intensity image
                            ImagePlus intensityIMP = IJ.openImage(analysis.getAnalysisFiles()[0].getAbsolutePath());
                            intensityIMP = ChannelSplitter.split(intensityIMP)[0];
                            ImageHandler intensityImage;
                            if (intensityIMP.getProcessor() instanceof FloatProcessor) intensityImage = new ImageFloat(intensityIMP);
                            else if (intensityIMP.getProcessor() instanceof ShortProcessor) intensityImage = new ImageShort(intensityIMP);
                            else if (intensityIMP.getProcessor() instanceof ByteProcessor) intensityImage = new ImageByte(intensityIMP);
                            else {
                                System.out.println(intensityIMP.getTitle() + " processor type not found.");
                                continue;
                            }
                            
                            //Create and add each object
                            for (Object3D object: cellPopulation.getObjectsList()) {
                                int volumePix = object.getVolumePixels();
                                double volumeUnit = object.getVolumeUnit();
                                double averageIntensity = object.getPixMeanValue(intensityImage);
                                int objectNumber = object.getValue();
                                double positionX = object.getCenterX();
                                double positionY = object.getCenterY();
                                double positionZ = object.getCenterZ();
                                 
                                cellDay.addCell(new Cell(volumePix, volumeUnit, averageIntensity, objectNumber, positionX, positionY, positionZ));
                            }
                            
                            cellFish.addDay(cellDay);
                            completed++;
                            break;
                        }    
                    }
                }
                cellGroup.addFish(cellFish);
            }
            cellHolder.addGroup(cellGroup);
        }
    }

    private void countAnalyses() {
        for (GroupOld fishGroup : experiment.getGroups()) {
            for (SubjectOld fish : fishGroup.getFishList()) {
                for (MeasurementOld measurement : fish.getMeasurements()) {
                    for (ImageAnalysisOls analysis : measurement.getAnalyses()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSSEGMENTED)) {
                            total++;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void sendProgressUpdate() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setProgress(currentFile, completed, total);
            }
        });
    }

    private void sendCellHolder() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setCellHolder(cellHolder);
            }
        });
    }
}
