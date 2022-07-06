package tools;


import GUI.MainMenuGUI;
import experiments.Constants;
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
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.plugin.ZProjector;
import java.io.File;
import java.util.ArrayList;
import java.util.stream.IntStream;
import javax.swing.SwingUtilities;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *Class to flatten image stacks.
 * @author janlu
 */
public class OptiFlattener implements Runnable{
    private int tileSize;
    private String outputDirectory;
    private int zProjectionMethod;
    private OptiImageImporter iI;
    private int nFiles;
    private ExperimentNew experiment;
    private MainMenuGUI parent;
    private ArrayList<MeasurementNew> measurementList = new ArrayList();
    private ArrayList<File> processedFiles = new ArrayList();
    private File outputDirectoryFile;
    private volatile boolean abort = false;
    int [] channelIDs;
    
    
     /**
     * Construct flattener with pre-determined parameters.
 is performed
     * @param targetLoc Location of directory / file
     * @param outputDirectory
     * @param zProjectionMethod
     * @param isDir True if location is a directory
     * @param tileSize Tile size of mosaic
     */
    public OptiFlattener(String targetLoc, String outputDirectory, boolean isDir, int zProjectionMethod, int tileSize) {
        this(new OptiImageImporter(isDir, targetLoc), outputDirectory, zProjectionMethod, tileSize);
    }
    
    
    /**
     * 
     * @param iI
     * @param outputDirectory
     * @param zProjectionMethod
     * @param tileSize 
     */
    public OptiFlattener(OptiImageImporter iI, String outputDirectory, int zProjectionMethod, int tileSize) {
        this.iI = iI;
        this.tileSize = tileSize;
        this.zProjectionMethod = zProjectionMethod;
        this.outputDirectory = outputDirectory;
        this.nFiles = iI.getNumberOfImages();
        flattenArray(true);
        
    }

    public OptiFlattener(ExperimentNew experiment, MainMenuGUI parent, int tileSize, int projectionMethod) {
        this.zProjectionMethod = projectionMethod;
        this.tileSize = tileSize;
        this.experiment = experiment;
        this.parent = parent;
    }
    
    @Override
    public void run() {
        sendStartMessage();
        flattenExperiment();
    }
    
    /**
     * Flattens input images and stores results to disk.
     * @param giveFeedback 
     */    
    private void flattenArray(boolean giveFeedback) {
        
        if (nFiles < 1) {
            IJ.showMessage("No images found");
            return;
        }
        
        for (int i = 0; i < nFiles; i++) {
                        
            int nChannels = iI.nChannels(i);
            ImagePlus[] channelArray = new ImagePlus[nChannels];
            String imageTitle = iI.getTitle(i);
            int totalFiles = (nChannels == 1) ? 1 : nChannels + 1;
            channelIDs = IntStream.range(0, totalFiles).toArray();
            for (int j = 0; j < nChannels; j++) {
                sendFileUpdateMessage(j, totalFiles);
                ImagePlus imp = iI.getChannel(i,j);
                
                
                //Processing channels
                if ((boolean)imp.getProperty("isBF")) {
                    channelArray[j] = focusBF(imp, tileSize);
                    channelArray[j].setTitle(imageTitle + "_Channel " + j + "_BF");
                }
                
                else {
                    channelArray[j] = doZProjection(imp, zProjectionMethod);
                    channelArray[j].setTitle(imageTitle + "_Channel " + j);
                }
                String saveString = outputDirectory + "\\" + channelArray[j].getTitle();
                if (!saveString.endsWith(".tif")) saveString = saveString.concat(".tif");
                IJ.saveAsTiff(channelArray[j], saveString);
                processedFiles.add(new File(saveString));
            }
            
            //Make and save comp if more that 1 channel
            if (nChannels > 1) {
                channelIDs[channelIDs.length - 1] = -1;
                ImageStack iStack = new ImageStack();
                
                for (ImagePlus channel : channelArray) {
                    iStack.addSlice(channel.getProcessor());
                }
                CompositeImage comp = new CompositeImage(new ImagePlus(imageTitle + "_Comp", iStack), CompositeImage.COMPOSITE);
                comp.setCalibration(channelArray[0].getCalibration());
                for (int j = 0; j < nChannels; j++) {
                    comp.setChannelLut(channelArray[j].getLuts()[0], j+1);
                }
                String saveString = outputDirectory + "\\" + comp.getTitle();
                if (!saveString.endsWith(".tif")) saveString = saveString.concat(".tif");
                IJ.saveAsTiff(comp, saveString);
                processedFiles.add(new File(saveString));
                sendFileUpdateMessage(totalFiles, totalFiles);
            }
        }
    }
    
    /**
     * Focuses ImagePlus using stack focusing.
     * @param BFImp BF image stack to be focused
     * @param tileSize Size of n*n tiles, 0 for no tiles
     * @return Focused BF image
     */
    public static ImagePlus focusBF(ImagePlus BFImp, int tileSize) {
        //Storing name and calibration
        String tempTitle = BFImp.getTitle();
        Calibration tempCal = BFImp.getCalibration();
        ImageStack BFStack = BFImp.getStack();
        
        
        if (tileSize > 1) {
            //Removing slices with empty areas
            int stackWidth = BFStack.getWidth();
            int stackHeight = BFStack.getHeight();
            int tilesX = (int) Math.round(stackWidth / ((double) tileSize));
            int tilesY = (int) Math.round(stackHeight / ((double) tileSize));
            int nSlices = BFStack.getSize();
            
            //From top slice
            Boolean noEmptyTiles;
            //starting at the center of the first tile
            int startPos = (int) tileSize/2;
            int checkX;
            int checkY;
            for (int i = nSlices; i > 0; i-- ){
                noEmptyTiles = true;
                scanLoop:
                for (int j = 0; j < tilesX; j++) {
                    for (int k = 0; k < tilesY; k++) {
                        checkX = startPos + (j*tileSize);
                        checkY = startPos + (k*tileSize);
                        
                        if (BFStack.getVoxel(checkX, checkY, i-1) == 0) {
                            BFStack.deleteLastSlice();
                            noEmptyTiles = false;
                            break scanLoop;
                        }

                    }

                }
                if(noEmptyTiles) {
                    break;
                }
            }
            //From first Slice
            noEmptyTiles = false;
            while(!noEmptyTiles) {
                noEmptyTiles = true;
                scanLoop:
                for (int j = 0; j < tilesX; j++) {
                    for (int k = 0; k < tilesY; k++) {
                        checkX = startPos + (j*tileSize);
                        checkY = startPos + (k*tileSize);
                        if (BFStack.getVoxel(checkX, checkY, 0) == 0) {
                            BFStack.deleteSlice(1);
                            noEmptyTiles = false;
                            break scanLoop;
                        }

                    }

                }
            }
            
            BFImp.setStack(BFStack);
            
        }
        
        //Focusing stack
        Stack_Focuser_ sf = new Stack_Focuser_();
        sf.focusBF(BFImp, 11);
        BFImp.setTitle(tempTitle);
        BFImp.setCalibration(tempCal);
        return BFImp;
    }
    
    /**
     * Returns a z-projection of an imagePlus using the given method. 
     * Methods found in ZProjector class
     * @param Imp ImagePlus to be projected
     * @param method Method from ZProjector class
     * @return 
     */
    public static ImagePlus doZProjection(ImagePlus Imp, int method) {
        //Storing title and calibration
        String tempTitle = Imp.getTitle();
        Calibration tempCal = Imp.getCalibration();
        
        //Doing projection
        ZProjector zp = new ZProjector();
        zp.setMethod(method);
        zp.setImage(Imp);
        zp.doProjection();
        Imp = zp.getProjection();
        Imp.setTitle(tempTitle);
        Imp.setCalibration(tempCal);
        return Imp;
    }
    
    /**
     * Returns a z-projection of an imagePlus using Max_Method
     * @param Imp ImagePlus to be projected
     * @return 
     */
    public static ImagePlus doZProjection(ImagePlus Imp){
        return doZProjection(Imp, ZProjector.MAX_METHOD);
    }

    private void flattenExperiment() {
        createMeasurementList();
        if (measurementList.isEmpty()) {
            sendStopMessage("Flatten - No measurements flattend");
            return;
        }
        String fileDirectoryString = measurementList.get(0).getConfocalFile().getParent() + "\\ProcessedFiles\\Flattened";
        outputDirectoryFile = new File(fileDirectoryString);
        
        for (MeasurementNew measurement : measurementList) {
            sendUpdateMessage(measurement.getConfocalFile().getName(), measurementList.indexOf(measurement), measurementList.size());
            flattenMeasurement(measurement);
            if (Thread.interrupted()) {
                sendStopMessage("Flatten - aborted");
                return;
            }
        }
        sendStopMessage("Flatten"); 
    }

    private void createMeasurementList() {
        for (FishGroupNew fishGroup : experiment.getGroups()) {
            for (FishNew fish : fishGroup.getFishList()) {
                for (MeasurementNew measurement : fish.getMeasurements()) {
                    boolean completed = false;
                    for (ImageAnalysisNew analysis : measurement.getAnalysisList()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_FLATTENED)){
                            completed = true;
                        } 
                    }
                    if (!completed) measurementList.add(measurement);
                }
            }
        }
    }

    private void flattenMeasurement(MeasurementNew measurement) { 
        iI = new OptiImageImporter(measurement.getConfocalFile());
        nFiles = 1;
        outputDirectory = outputDirectoryFile.getAbsolutePath() 
                + "\\" + measurement.getParent().getParentFishGroup().getGroupName() 
                + "\\" + measurement.getParent().getName()
                + "\\" + measurement.getFileName();
        
        File makeDir = new File(outputDirectory);
        System.out.println("OutputDir: " + makeDir.getAbsolutePath());
        makeDir.setWritable(true, false);
        System.out.println( "make file: " + makeDir.mkdirs());
        makeDir.setWritable(true, false);
        outputDirectory = makeDir.getAbsolutePath();
        processedFiles.clear();
        flattenArray(false);
        measurement.addAnalysis(Constants.ANALYSIS_FLATTENED, processedFiles.toArray(new File[0]), channelIDs);
        sendSaveMessage();
        try{
            Thread.sleep(1000);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
        
    }
    
    private void sendStartMessage() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setTask("Flatten", Constants.TASK_FLATTENING);
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
    private void sendFileUpdateMessage(int progress, int total) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setFileProgress(progress, total);
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
    
    
}
