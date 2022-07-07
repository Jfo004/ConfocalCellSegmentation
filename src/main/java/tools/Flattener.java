package tools;


import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.gui.WaitForUserDialog;
import ij.io.DirectoryChooser;
import ij.io.OpenDialog;
import ij.measure.Calibration;
import ij.plugin.ChannelSplitter;
import ij.plugin.ZProjector;
import ij.plugin.frame.ContrastAdjuster;
import ij.process.LUT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *Class to flatten image stacks.
 * @author janlu
 */
public class Flattener {
    int tileSize;
    boolean isDirectory;
    String targetLocation;
    String outputLocation;
    int zProjectionMethod;
    boolean nameChannels;
    boolean makeComposite;
    int calibrationImage;
    double[][] channelIntensities;
    double[][] compositeIntensities;
    boolean doIntensityCalibration;
    boolean userCalibration;
    private OLDIMSImporter iI;
    private int nFiles;
    private String[] pMethodS = new String[] {"Maximum", "Average", "Median", "Sum"};
    private int[] pMethodV = new int[] {ZProjector.MAX_METHOD, ZProjector.AVG_METHOD, ZProjector.MEDIAN_METHOD, ZProjector.SUM_METHOD};
    private boolean wasCanceled = false;
    
    
    // TODO implement cancle
    // TEST manual calibration
    // TODO make code a bit prettier
    // TODO implement choose files
    //TODO store standard directories in IJ
    // TODO automatic switch to slow algorithm for focuser?
    
    public Flattener() {
        GenericDialog optionsDialog = new GenericDialog("Flatten confocal stack");
        optionsDialog.addRadioButtonGroup("",new String[] {"Single file", "Directory"} , 1, 2, "Directory");
        optionsDialog.addChoice("Projection method (F)", pMethodS, "Maximum");
        optionsDialog.addNumericField("Tile size (-1 to disable)", 1024);
        optionsDialog.addRadioButtonGroup("Intensity calibration", new String[]{"None", "Manual", "Values"}, 1, 3, "None");
        optionsDialog.addCheckboxGroup(1, 2, new String[] {"Create channel composite", "Name Channels"}, new boolean[] {true, true});
        optionsDialog.showDialog();
        if (optionsDialog.wasCanceled()) {
            wasCanceled = true;
            return;
        }
        
        //creating importer
        boolean isDir = ("Directory".equals(optionsDialog.getNextRadioButton()));
        String dir;
        if (isDir) {
           DirectoryChooser dCInput = new DirectoryChooser("Select input directory");
           dir = dCInput.getDirectory();
        }
        else {
            OpenDialog oD = new OpenDialog("Select file");
            dir = oD.getPath();
        }
        if (dir == null) {
            wasCanceled = true; 
            return;
        }
        iI = new OLDIMSImporter(isDir, dir);
        iI.prepareImportImaris();
        this.nFiles = iI.getNumberOfImages();
        this.channelIntensities = new double[iI.nChannels()][2];
        this.compositeIntensities = new double[iI.nChannels()][2];
        DirectoryChooser dCOut = new DirectoryChooser("Select output directory");
        outputLocation = dCOut.getDirectory();
        
        //storing variables
        zProjectionMethod = pMethodV[optionsDialog.getNextChoiceIndex()];
        tileSize = (int) optionsDialog.getNextNumber();
        String tempString = optionsDialog.getNextRadioButton();
        makeComposite = optionsDialog.getNextBoolean();
        nameChannels = optionsDialog.getNextBoolean();
        
        //No intensity calibration
        if ("None".equals(tempString)) {
            doIntensityCalibration = false;
            userCalibration = false;
            flattenArray(true);
            return;
        }
                
        //Doing intensity calibration manual
        if ("Manual".equals(tempString)) {
            userCalibration = true;
            doIntensityCalibration = true;
            GenericDialog calibrationDialog = new GenericDialog("Select all positive image");
            calibrationDialog.addChoice("Calibration image:", iI.getFileNames(), iI.getFileNames()[0]);
            calibrationDialog.showDialog();
            calibrationImage = calibrationDialog.getNextChoiceIndex();
            doUserCalibration(true);
            flattenArray(true);
            return;
        }
        
        //Doing intensity calibration by values
        userCalibration = false;
        doIntensityCalibration = true;
        GenericDialog calibrationDialog = new GenericDialog("enter channel intensities");
        int nChannels = iI.nChannels();
        for (int i = 0; i < nChannels; i++) {
            calibrationDialog.addNumericField("Channel " + (i+1) + ": Min", 0);
            calibrationDialog.addToSameRow();
            calibrationDialog.addNumericField("Max", 0);
        }
        calibrationDialog.showDialog();
        if (calibrationDialog.wasCanceled()) {
            wasCanceled = true;
            return;
        }
        for (int i = 0; i < nChannels; i++) {
            channelIntensities[i][0] = calibrationDialog.getNextNumber();
            channelIntensities[i][1] = calibrationDialog.getNextNumber();
        }
        if (!makeComposite) {
            flattenArray(true);
            return;
        }
        GenericDialog calibrationDialogComp = new GenericDialog("enter composite intensities");
        for (int i = 0; i < nChannels; i++) {
            calibrationDialogComp.addNumericField("Channel " + (i+1) + ": Min", 0);
            calibrationDialogComp.addToSameRow();
            calibrationDialogComp.addNumericField("Max", 0);
        }
        calibrationDialogComp.showDialog();
        if (calibrationDialogComp.wasCanceled()) {
            wasCanceled = true;
            return;
        }
        for (int i = 0; i < nChannels; i++) {
            compositeIntensities[i][0] = calibrationDialogComp.getNextNumber();
            compositeIntensities[i][1] = calibrationDialogComp.getNextNumber();
        }
        flattenArray(true); 
    }
    
     /**
     * Construct flattener with pre-determined parameters. Intensity values for 
     * channels given.
     * @param targetLoc Location of directory / file
     * @param outLoc output directory
     * @param isDir True if location is a directory
     * @param pMethod projection method to be used by ZProjector
     * @param tileSize Tile size of mosaic
     * @param nameChannels True if channels should be named individually
     * @param makeComp True if a composite of all channels should be constructed
     * @param channelInt array of min/max intensities for each channel 
     * [channel index] [min = 0, max = 1]
     * @param compInt array of min/max intensities in composite
     */
    public Flattener(String targetLoc, String outLoc, boolean isDir, int pMethod, int tileSize, boolean nameChannels, boolean makeComp, double[][] channelInt, double[][] compInt) {
        this.targetLocation = targetLoc;
        this.outputLocation = outLoc;
        this.nameChannels = nameChannels;
        this.isDirectory = isDir;
        this.zProjectionMethod = pMethod;
        this.tileSize = tileSize;
        this.nameChannels = nameChannels;
        this.makeComposite = makeComp;
        this.channelIntensities = channelInt;
        this.compositeIntensities = compInt;
        this.userCalibration = false;
        this.doIntensityCalibration = true;
        this.nFiles = iI.getNumberOfImages();
        
    }
    
    /**
     * Construct flattener with pre-determined parameters. user performs intensity
     * calibration.
     * @param targetLoc Location of directory / file
     * @param outLoc output directory
     * @param isDir True if location is a directory
     * @param pMethod projection method to be used by ZProjector
     * @param tileSize Tile size of mosaic
     * @param nameChannels True if channels should be named individually
     * @param makeComp True if a composite of all channels should be constructed
     * @param calImg Index of calibration image
     */
    public Flattener(String targetLoc, String outLoc, boolean isDir, int pMethod, int tileSize, boolean nameChannels, boolean makeComp, int calImg)  {
        this.outputLocation = outLoc;
        this.zProjectionMethod = pMethod;
        this.tileSize = tileSize;
        this.nameChannels = nameChannels;
        this.makeComposite = makeComp;
        this.calibrationImage = calImg;
        this.doIntensityCalibration = true;
        this.userCalibration = true;
        this.iI = new OLDIMSImporter(isDir, targetLoc);
        this.iI.prepareImportImaris();
        this.nFiles = iI.getNumberOfImages();
        doUserCalibration(true);
    }
    
     /**
     * Construct flattener with pre-determined parameters. no intensity calibration
     * is performed
     * @param targetLoc Location of directory / file
     * @param outLoc output directory
     * @param isDir True if location is a directory
     * @param pMethod projection method to be used by ZProjector
     * @param tileSize Tile size of mosaic
     * @param nameChannels True if channels should be named individually
     * @param makeComp True if a composite of all channels should be constructed
     */
    public Flattener(String targetLoc, String outLoc, boolean isDir, int pMethod, int tileSize, boolean nameChannels, boolean makeComp) {
        this.outputLocation = outLoc;
        this.zProjectionMethod = pMethod;
        this.tileSize = tileSize;
        this.nameChannels = nameChannels;
        this.makeComposite = makeComp;
        this.doIntensityCalibration = false;
        this.userCalibration = false;
        this.iI = new OLDIMSImporter(isDir, targetLoc);
        this.iI.prepareImportImaris();
        this.nFiles = iI.getNumberOfImages();
    }
    
    /**
     * Flattens input images and stores results to disk.
     * @param giveFeedback 
     */    
    public void flattenArray(boolean giveFeedback) {
        
        if (nFiles < 1) {
            IJ.showMessage("No images found");
            return;
        }
        CompositeImage[] channelArray;
        
        for (int i = 0; i < nFiles; i++) {
            // skips image used in calibration
            if ((i == calibrationImage) && userCalibration) continue; 
            
            if(giveFeedback) IJ.showStatus("Importing image " + (i + 1) + " of " + nFiles);
            ImagePlus imp = iI.getImp(i);
            
            if(giveFeedback) IJ.showStatus("Flattening image " + (i + 1) + " of " + nFiles);
            channelArray = flattenHyperstack(imp, tileSize, zProjectionMethod, nameChannels, makeComposite);
            imp.changes = false;
            imp.close();
            
            if(giveFeedback) IJ.showStatus("Saving image " + (i + 1) + " of " + nFiles);
            for(int j = 0; j < channelArray.length; j++) {
                
                if(doIntensityCalibration) {
                    if (makeComposite && (j == (channelArray.length - 1))) {
                        for (int k = 0; k < channelArray[j].getNChannels(); k++) {
                            channelArray[j].setC(k+1);
                            channelArray[j].setDisplayRange(compositeIntensities[k][0],compositeIntensities[k][1]);
                        }
                    }
                    else {
                        channelArray[j].setDisplayRange(channelIntensities[j][0], channelIntensities[j][1]);
                    }
                }
                
                if(channelArray[j].getNChannels() > 1) {
                IJ.saveAsTiff(channelArray[j], outputLocation + channelArray[j].getTitle());
                }
                else {
                //Issue with loosing LUT in single channel comp on save
                LUT tempLUT = channelArray[j].getChannelLut();
                Calibration tempCal = channelArray[j].getCalibration();
                ImagePlus tempImg = new ImagePlus(channelArray[j].getTitle(), channelArray[j].getChannelProcessor());
                tempImg.setLut(tempLUT);
                tempImg.setCalibration(tempCal);
                IJ.saveAsTiff(tempImg, outputLocation + channelArray[j].getTitle());
                }
            }
            if(giveFeedback) IJ.showProgress(i, nFiles);
        }
        if(giveFeedback) IJ.showStatus("Done");
    }
    
    private void doUserCalibration(boolean giveFeedback) {
        //Do intensity calibration
        // save image 
            
        if(giveFeedback) IJ.showStatus("Importing image calibration image");
        ImagePlus imp = iI.getImp(calibrationImage);
        CompositeImage[] channelArray;

        if(giveFeedback) IJ.showStatus("Flattening calibration image");
        channelArray = flattenHyperstack(imp, tileSize, zProjectionMethod, nameChannels, makeComposite);
        imp.changes = false;
        imp.close();
        
        ContrastAdjuster cA = new ContrastAdjuster();
        cA.run("");
        WaitForUserDialog waitDialog = new WaitForUserDialog("Adjust channels");
        for(int i = 0; i < channelArray.length; i++) {
            //Show image, set intensity and store settings
            channelArray[i].show();
            waitDialog.show();
            channelArray[i].hide();
                     
            
            //Storing Intensity range
            if ((i == channelArray.length -1) && makeComposite && (channelArray[i].getNChannels() > 1)) {
                for (int j = 0; j < channelArray[i].getNChannels(); j++) {
                    channelArray[i].setC(j+1);
                    compositeIntensities[j][0] = channelArray[i].getDisplayRangeMin();
                    compositeIntensities[j][1] = channelArray[i].getDisplayRangeMax();
                }
            }
            else {
                 channelIntensities[i][0] = channelArray[i].getDisplayRangeMin();
                 channelIntensities[i][1] = channelArray[i].getDisplayRangeMax();
            }  
            
            //Saving channel
            if(channelArray[i].getNChannels() > 1) {
                IJ.saveAsTiff(channelArray[i], outputLocation + channelArray[i].getTitle());
            }
            else {
            //Issue with loosing LUT in single channel comp on save
            LUT tempLUT = channelArray[i].getChannelLut();
            Calibration tempCal = channelArray[i].getCalibration();
            //Properties tempProp = channelArray[i].getProperties(); FIX properties for single channel?
            ImagePlus tempImg = new ImagePlus(channelArray[i].getTitle(), channelArray[i].getChannelProcessor());
            tempImg.setLut(tempLUT);
            tempImg.setCalibration(tempCal);
            IJ.saveAsTiff(tempImg, outputLocation + channelArray[i].getTitle());
            }
        } 
        cA.setVisible(false);
        cA.close();
    }
    
    /**
     * Flattens hyperstack and returns an array of composite images
     * @param inImp the hyperstack to be flattened
     * @param tileSize tile size if image is mosaic of multiple images during 
     * aquisition. set to 0 if unknown / image is not a mosaic (disables clean-up
     * before BF-focusing
     * @param projectionMethod Projection method for Z-projection
     * @param nameChannels Change title of images to reflect channel
     * @param makeChannelComp add an aditional composite image of all channels
     * to the last place in the array.
     * @param deleteImp If true, the ImagePlus will be deleted after import to 
     * save memory.
     * @return 
     */
    public static CompositeImage[] flattenHyperstack(ImagePlus inImp, int tileSize, int projectionMethod, Boolean nameChannels, Boolean makeChannelComp) {
        
        CompositeImage[] compositeStack = new CompositeImage[(makeChannelComp) ? inImp.getNChannels() + 1 : inImp.getNChannels()];
        String inImpTitle = inImp.getTitle();
        int bfChannels = (int)inImp.getProperty("BFChannels");
        ImagePlus[] channelArray = ChannelSplitter.split(inImp);
        ArrayList<Integer> bfList = new ArrayList<Integer>();
        inImp.changes = false;
        inImp.close();
        
        //Flattening channel stacks
        for (int i = 0; i < channelArray.length; i++) {
            ImagePlus tempImp;
            // If the stack is a BF channel
            if ((((1 << i) & bfChannels) >> i) == 1 ) {
                bfList.add(i);
            }
            // if the stack is not a BF channel
            else {
                tempImp = doZProjection(channelArray[i], projectionMethod);
                channelArray[i].changes = false;
                channelArray[i].close();
                channelArray[i] = tempImp;
                if (nameChannels) {
                    channelArray[i].setTitle(inImpTitle + "_Channel " + i);
                }
                else {
                    channelArray[i].setTitle(inImpTitle);
                }
            }
            LUT tempLut = channelArray[i].getLuts()[0];
            compositeStack[i] = new CompositeImage(channelArray[i]);
            compositeStack[i].setChannelLut(tempLut);
            compositeStack[i].setProperty("BFChannels", 0);
        }
        
        //Delay memory intensive focusing as dealing with z-stacks first frees memory
        for (int i : bfList) {                
            channelArray[i] = focusBF(channelArray[i], tileSize);
            if (nameChannels) {
                channelArray[i].setTitle(inImpTitle + "_Channel " + i + "_BF");
            }
            else {
                channelArray[i].setTitle(inImpTitle);
            }
            LUT tempLut = channelArray[i].getLuts()[0];
            compositeStack[i] = new CompositeImage(channelArray[i]);
            compositeStack[i].setChannelLut(tempLut);
            compositeStack[i].setProperty("BFChannels", 1);
        }
        
        //Adding Channel comp if selected
        if (makeChannelComp) {
            int lastIdx = compositeStack.length - 1;
            String compTitle = inImpTitle + "_Comp";
            compositeStack[lastIdx] = makeChannelComp(Arrays.copyOfRange(channelArray, 0,lastIdx), compTitle);
            compositeStack[lastIdx].setProperty("BFChannels", bfChannels);
        }
        return compositeStack;
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
    
    /**
     * Merging single slice images to composite image
     * @param channelArray Array of channel images
     * @param compTitle Title of returned comp
     * @return composite image of channels
     */
    public static CompositeImage makeChannelComp(ImagePlus[] channelArray, String compTitle) {
        ImagePlus tempImp;
        CompositeImage channelComp;
        
        // merging images in single comp if there are more than one images
        if (channelArray.length > 1) {
            ImageStack channelCompStack = new ImageStack(channelArray[0].getWidth(), channelArray[0].getHeight());
            Calibration tempCal = channelArray[0].getCalibration().copy();
            for (ImagePlus channelImg : channelArray) {
                channelCompStack.addSlice(channelImg.getProcessor());
            }
            tempImp = new ImagePlus(compTitle, channelCompStack);
            channelComp = new CompositeImage(tempImp, CompositeImage.COMPOSITE);
            channelComp.setCalibration(tempCal);
            for (int j = 0; j < channelArray.length; j++) {
                channelComp.setChannelLut(channelArray[j].getLuts()[0], j+1);
            }                    
        }
        else {
            channelComp = new CompositeImage(channelArray[0]);
            channelComp.setTitle(compTitle);
            channelComp.setLut(channelArray[0].getLuts()[0]);
        }
        return channelComp;
    }
    
}
