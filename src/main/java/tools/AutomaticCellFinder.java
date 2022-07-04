/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import GUI.MainMenuGUI;
import experiments.Constants;
import experiments.Experiment;
import experiments.Fish;
import experiments.FishGroup;
import experiments.ImageAnalysis;
import experiments.Measurement;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Plot;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.io.RoiDecoder;
import ij.measure.Calibration;
import ij.plugin.GaussianBlur3D;
import ij.plugin.RGBStackMerge;
import ij.plugin.RoiRotator;
import ij.plugin.ZProjector;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.PlugInFilterRunner;
import ij.plugin.filter.Rotator;
import ij.plugin.filter.Transformer;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.LUT;
import ij.process.ShortProcessor;
import ij.process.StackProcessor;
import ij.process.StackStatistics;
import java.awt.Color;
import java.io.File;
import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageByte;
import mcib3d.image3d.ImageFloat;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageInt;
import mcib3d.image3d.ImageShort;
import mcib3d.image3d.processing.MaximaFinder;
import mcib3d.image3d.regionGrowing.Watershed3D;
import mcib3d.utils.ArrayUtil;
import tools.tweaked.MaximaFinderTweaked;



/**
 *
 * @author janlu
 */
public class AutomaticCellFinder implements Runnable{
    private Experiment experiment;
    private MainMenuGUI parent;
    private ArrayList<Measurement> measurementList = new ArrayList();
    private int cellChannel;
    private double cellCutoffIntensity;
    
    
    public AutomaticCellFinder(Experiment experiment, MainMenuGUI parent, int cellChannel) {
        this.experiment = experiment;
        this.parent = parent;
        this.cellChannel = cellChannel;
    }
    
    @Override
    public void run() {
        sendStartMessage();
        createMeasurementList();
        segmentCells();
        sendStopMessage("Cell Segmenter");        
    }


    private void segmentCells() {
        int total = measurementList.size();
        int current = 0;
        for (Measurement measurement : measurementList) {
            sendUpdateMessage(measurement.getFileName(), current, total);
            current++;
            sendFileUpdateMessage("Importing", 0, 5);
            ImagePlus targetImageImp = importCroppedChannel(measurement);
            
            //Manual processing
            //sendFileUpdateMessage("Pre-processing", 1, 5);
            targetImageImp = preProcess(targetImageImp);
            ZProjector zp = new ZProjector();
            zp.setMethod(ZProjector.MAX_METHOD);
            zp.setImage(targetImageImp);
            zp.doProjection();
            ImagePlus projection = zp.getProjection();
            projection.show();
            targetImageImp.show();
            ArrayList<ImageAnalysis> analysisList = measurement.getAnalyses();
            ImagePlus croppedImage = new ImagePlus();
            for (ImageAnalysis analysis : analysisList) {
                if (analysis.isAnalysisType(Constants.ANALYSIS_CROPPED)) {
                    croppedImage = IJ.openImage(analysis.getAnalysisFiles()[0].getAbsolutePath());
                    croppedImage.show();
                    System.out.println("FOund BF");
                    break;
                }
            }
            //TODO implement rotation on import
            //user: 
            //find threshold and subtract
            //Mark outlisers and set 0 
            //flip image if needed
            
            this.cellCutoffIntensity = 1;
            
            WaitForUserDialog dialog = new WaitForUserDialog("Subtract, set 0 and rotate");
            dialog.show();
            projection.hide();
            waitForAdjustment();
            
            targetImageImp.hide();
            if (croppedImage != null) croppedImage.close();
            if (RoiManager.getInstance() != null) RoiManager.getInstance().reset();
            
            ImagePlus seedImageImp = targetImageImp.duplicate();
            GaussianBlur3D.blur(seedImageImp, 2, 2, 2);
            
            
            
            
            ImageHandler seedImageSource;
            if (targetImageImp.getProcessor() instanceof FloatProcessor) seedImageSource = new ImageFloat(seedImageImp);
            else if (targetImageImp.getProcessor() instanceof ShortProcessor) seedImageSource = new ImageShort(seedImageImp);
            else if (targetImageImp.getProcessor() instanceof ByteProcessor) seedImageSource = new ImageByte(seedImageImp);
            else {
                System.out.println(targetImageImp.getTitle() + " processor type not found.");
                continue;
            }
            ImageHandler targetImage;
            if (targetImageImp.getProcessor() instanceof FloatProcessor) targetImage = new ImageFloat(targetImageImp);
            else if (targetImageImp.getProcessor() instanceof ShortProcessor) targetImage = new ImageShort(targetImageImp);
            else if (targetImageImp.getProcessor() instanceof ByteProcessor) targetImage = new ImageByte(targetImageImp);
            else {
                System.out.println(targetImageImp.getTitle() + " processor type not found.");
                continue;
            }
            
            sendFileUpdateMessage("Creating Seed", 2, 5);
            ImageHandler seedImage = createSeedImage(seedImageSource);
            sendFileUpdateMessage("Performing Watershed", 3, 5);
            ImageHandler segmentedImage = createSegmentedImage(targetImage, seedImage);
            sendFileUpdateMessage("Creating Analysis", 4, 5);
            createAnalysis(segmentedImage, targetImage, measurement);
            sendSaveMessage();
            sendFileUpdateMessage("Finished", 5, 5);
            
            if (Thread.interrupted()) {
                sendStopMessage("Cell Segmenter - Aborted");
                return;
            }
        }
    }
    
    private void waitForAdjustment() {
        //send message to unlock and images
        try {
            wait();
        } catch (InterruptedException ex) {
            System.out.println("Wait interrupted");
            
        }
        
        //send message to lock
    }
    
    private void createMeasurementList() {
        for (FishGroup fishGroup : experiment.getGroups()) {
            for (Fish fish : fishGroup.getFishList()) {
                for (Measurement measurement : fish.getMeasurements()) {
                    boolean completed = false;
                    for (ImageAnalysis analysis : measurement.getAnalyses()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSSEGMENTED)){
                            completed = true;
                        } 
                    }
                    if (!completed) measurementList.add(measurement);
                }
            }
        }
    }
    private void sendStartMessage() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setTask("Cell Segmenter", Constants.TASK_CELLSEGMENTING);
            }
        });
    }
    private void sendUpdateMessage(String fileName, int progress, int total) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setProgress(fileName, progress, total);
            }
        });
    }
    private void sendFileUpdateMessage(String task, int progress, int total) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setFileProgress(task, progress, total);
            }
        });
    }
    private void sendStopMessage(String message) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.finishTask(message);
            }
        });
    }
    private void sendSaveMessage() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.saveExperiment();
            }
        });
    }

    private ImagePlus importCroppedChannel(Measurement measurement) {
        Roi fishRoi = RoiDecoder.open(measurement.getFishROI().getAbsolutePath());
        int startX = fishRoi.getBounds().x;
        int startY = fishRoi.getBounds().y;
        int width = fishRoi.getBounds().width;
        int height = fishRoi.getBounds().height;
        OptiImageImporter importer = new OptiImageImporter(measurement.getConfocalFile());
        ImagePlus image = importer.getSubImage(startX,startY,width,height,cellChannel, 0);
        int initialX = image.getWidth();
        int initialY = image.getHeight();
        image.show();
        IJ.run(image, "Rotate... ", "angle=" + fishRoi.getFeretValues()[1] + " grid=0 interpolation=Bilinear enlarge stack");
        image.hide();
        int moveX = (image.getWidth()-initialX)/2;
        int moveY = (image.getHeight()-initialY)/2;
        fishRoi.setLocation(moveX, moveY);
        fishRoi = RoiRotator.rotate(fishRoi, fishRoi.getFeretValues()[1]);
        image.setRoi(fishRoi, true);
        ImagePlus imageTemp = image.crop("stack");
        image.changes = false;
        image.close();
        image = imageTemp;
        image.setRoi(fishRoi, true);
        return image;
    }
/**
 * Returns two imageProcessors. excessive blur for seed-image. 
 * @param image
 * @return 
 */
    private ImagePlus preProcess(ImagePlus image) {
        //Gaussian blur to remove some noise/get smoother image
        Calibration tempCalibration = image.getCalibration();
        //GaussianBlur3D.blur(image, 2, 2, 2);
        //ZProjector zp = new ZProjector();
        //zp.setMethod(ZProjector.MAX_METHOD);
        //zp.setImage(image);
        //zp.doProjection();
        //ImagePlus projection = zp.getProjection();
        
        image.restoreRoi();
        Roi fishRoi = image.getRoi();
        //projection.setRoi(fishRoi);
        //ImageStatistics stats = projection.getAllStatistics();
        //double median = stats.median;
        //projection.getProcessor().subtract(median);
        //projection.getProcessor().setAutoThreshold("Percentile dark");
        //double cutoff = projection.getProcessor().getMinThreshold();
        
        //this.cellCutoffIntensity = cutoff + median;
        //System.out.println("Median: " + median);
        //System.out.println("Cutoff by threshold: " + this.cellCutoffIntensity);
        
        
        
        
        //minSeedInsensity/cellcutoffintensity
        //System.out.println("Start background&noise");
        
        //image.setRoi(image.getRoi().getInverse(image));
        //int histogramMax = (int)Math.pow(2, image.getBitDepth());
        //StackStatistics imageStat = new StackStatistics(image, histogramMax - 1, 1, histogramMax - 1);
        //long[] histogram = imageStat.getHistogram();
        //int backgroundMedian = getMedian(histogram);
        //int backgroundMAD = getMAD(backgroundMedian, histogram);
        //double minThreshold = median * 1.2;
        //System.out.println("Min threshold: " + minThreshold);
        
        //if (this.cellCutoffIntensity < minThreshold) {
        //    this.cellCutoffIntensity = minThreshold;
        //    System.out.println("Cutoff too small, now: " + this.cellCutoffIntensity);
        //}
            
        
        //Setting intensity outside fish-roi to 0
        image.deleteRoi();
        ImageStack stack = image.getStack();
        for (int i = 1; i <= stack.size(); i++) {
            stack.getProcessor(i).setColor(0);
            stack.getProcessor(i).fillOutside(fishRoi);
        }
        image.setStack(stack);
        
        // alternative cutoff
        //imageStat = new StackStatistics(image, histogramMax, 0, histogramMax - 1);
        //histogram = imageStat.getHistogram();
        //int[] intHistogram = new int[histogram.length];
        //for (int idx = 0; idx < histogram.length; idx++) {
        //    intHistogram[idx] = (int) histogram[idx];
        //}
        
        //int[] classCenters = ArrayUtil.kMeans_Histogram1D(intHistogram, 4, 1);
        //Arrays.sort(classCenters);
        //for (int idx = 0; idx < classCenters.length; idx++) {
        //    System.out.println("Group " + idx + " center at: " + classCenters[idx]);
        //}
        //for (int idx = 0; idx < classCenters.length -1; idx++ ) {
        //    this.cellCutoffIntensity = (classCenters[idx + 1] + classCenters[idx])/(double)2;
        //    if (this.cellCutoffIntensity > (backgroundMedian + backgroundMAD)*2) {
        //        System.out.println("Cell cutt-off at: " + this.cellCutoffIntensity);
        //        break;
        //    }
        //    
        //    System.out.println("Cell cutt-off too low at: " + this.cellCutoffIntensity);
        //    if (idx == classCenters.length -2) {
        //        this.cellCutoffIntensity = (backgroundMedian + backgroundMAD)*2;
        //        System.out.println("No cutoff high enough, now:  " + this.cellCutoffIntensity);
        //    }
        //}        
        image.setCalibration(tempCalibration);
        return image;
    }

    private ImageHandler createSegmentedImage(ImageHandler targetImage, ImageHandler seedImage) {
        Watershed3D watershed = new Watershed3D(targetImage, seedImage, this.cellCutoffIntensity, (int)this.cellCutoffIntensity);
        ImageHandler segmentedImage = watershed.getWatershedImage3D();
        segmentedImage.setScale(targetImage);
        return segmentedImage;
    }

    private ImageHandler createSeedImage(ImageHandler targetImage) {
        float radXY = 3;
        float radZ = 2;
        MaximaFinderTweaked maximaFinder = new MaximaFinderTweaked(targetImage, radXY, radZ, (float)this.cellCutoffIntensity);
        maximaFinder.setVerbose(false);
        return maximaFinder.getImagePeaks();
    }

    private int getMedian(long[] histogram) {
        long totalCount = 0;
        long tempSum = 0;
        for (long count : histogram) totalCount +=count;
        for (int bin = 0; bin < histogram.length; bin++) {
            tempSum += histogram[bin];
            if (tempSum >= (totalCount/2)) return bin;
        }
        return 0;
    }

    private int getMAD(double median, long[] histogram) {
        long[] difference = new long[histogram.length];
        for (int bin = 0; bin < histogram.length; bin++) {
            difference[(int)Math.abs((long)median - bin)] +=histogram[bin];
        }

        return getMedian(difference);
    }

    private void viewHistogram(ImageHandler segmentedImage) {
        Objects3DPopulation population = new Objects3DPopulation(segmentedImage);
        List objects = population.getMeasuresGeometrical();
        double[] volumeArray = new double[objects.size()];
        for (int idx = 0; idx < objects.size(); idx++) {
            volumeArray[idx] = ((Double[])objects.get(idx))[1];
        }
        
        Arrays.sort(volumeArray);
        double min = volumeArray[0];
        double max = 1000;
        int bins = 100;
        double binSize = (max-min)/bins;
        double[] histogram = new double[bins];
        int crop = 1000;
        for (double value : volumeArray) {
            if (value > crop) {
                histogram[histogram.length - 1]++;
                continue;
            }
            histogram[(int)Math.floor((value-min)/binSize)]++;
        }
        double[] histogramX = new double[bins];
        for (int idx = 0; idx < histogramX.length; idx++) {
            histogramX[idx] = idx*binSize;
        }
        
        Plot plot = new Plot("Distribution", "Size", "count");
        plot.addPoints(histogramX, histogram, Plot.BAR);
        plot.show();
        WaitForUserDialog dialog = new WaitForUserDialog("asdf");
        dialog.show();

    }

    private void createAnalysis(ImageHandler segmentedImage, ImageHandler targetImage, Measurement measurement) {
       String outputString = measurement.getConfocalFile().getParent()
               .concat("\\ProcessedFiles\\Segmented\\")
               .concat(measurement.getParentFish().getParentFishGroup().getGroupName())
               .concat("\\").concat(measurement.getParentFish().getName()
               .concat("\\").concat(measurement.getFileName()));
       File outputDir = new File(outputString);
       outputDir.mkdirs();
       File outputImage = new File(outputString.concat("\\controlImage.tif"));
       File outputCells = new File(outputString.concat("\\Objects.zip"));
       
       Objects3DPopulation population = new Objects3DPopulation(segmentedImage);
       population = new Objects3DPopulation(population.getObjectsWithinVolume(2, Double.MAX_VALUE, false));
       for (Double[] cellVolume :population.getMeasuresGeometrical()) System.out.println("Value: " + cellVolume[0] + " VolPix: " + cellVolume[1] + " volUnit: " + cellVolume[2]);
           
       population.saveObjects(outputCells.getAbsolutePath());
       ImagePlus controlImage = RGBStackMerge.mergeChannels(new ImagePlus[] {targetImage.getImagePlus(), segmentedImage.getImagePlus()}, true);
       controlImage.setC(1);
       controlImage.setLut(LUT.createLutFromColor(Color.green));
       controlImage.setC(2);
       controlImage.setDisplayRange(0, 1);
       controlImage.setLut(LUT.createLutFromColor(Color.red));
       IJ.saveAsTiff(controlImage, outputImage.getAbsolutePath());
       controlImage.close();
       segmentedImage.closeImagePlus();
       targetImage.closeImagePlus();
       ImageAnalysis analysis = new ImageAnalysis(Constants.ANALYSIS_CELLSSEGMENTED, new File[] {outputImage}, new int[]{-1}, measurement.getAcquisitionTime(), measurement);
       analysis.setRois(new File[] {outputCells});
       measurement.addAnalysis(analysis);
    }
}
