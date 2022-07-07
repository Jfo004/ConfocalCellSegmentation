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
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;
import ij.process.LUT;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList; 
import java.util.logging.Level;
import java.util.logging.Logger;
import loci.formats.FormatException;
import loci.formats.MetadataTools;
import loci.formats.in.ImarisHDFReader;
import loci.formats.meta.IMetadata;
import loci.plugins.util.ImageProcessorReader;
import ome.units.quantity.Length;
import ome.xml.model.primitives.Timestamp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *Class for importing single or multiple images, and giving imagePlus and
 *image information on request
 * @author janlu
 */
public class OLDIMSImporter{
    private Boolean wasCanceled = false;
    private Boolean isDirectory = false;
    private String targetLoc = "";
    private ArrayList<String> filesToOpen = new ArrayList<String>();
    private int nFiles = 0;
    private ArrayList<Integer> bfChannels = new ArrayList<Integer>();
    private LUT bfLUT;
    
    
    /**
     * Constructor sets BF LUT. No Import options given, setUserImportOptions
     * required.
     */
    public OLDIMSImporter() {
        // Creating BF LUT (Greys)
        ome.xml.model.primitives.Color tempColor = new ome.xml.model.primitives.Color(-1);
        Color tempColorJava = new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), tempColor.getAlpha());
        this.bfLUT = LUT.createLutFromColor(tempColorJava);
    }
    
    /**
     * Constructs importer with given import options and creates BF LUT.
     * @param isDir True if targets are multiple files in a directory, false if
     * the target is a single file.
     * @param targetLocation Location of directory or file.
     */
    public OLDIMSImporter(Boolean isDir, String targetLocation) {
        this.isDirectory = isDir;
        this.targetLoc = targetLocation;
        
        // Creating BF LUT (Greys)
        ome.xml.model.primitives.Color tempColor = new ome.xml.model.primitives.Color(-1);
        Color tempColorJava = new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), tempColor.getAlpha());
        this.bfLUT = LUT.createLutFromColor(tempColorJava);
    }
       
    /**
     * Method to create a user dialog requesting import parameters and storing 
     * them in instance variables. Sets wasCanceled to true if the user clicks 
     * cancel. 
     */
    public void setUserImportOptions(){
        String[] importTypeList = {"Single file", "Directory"};
        String defaultImportType = "Directory";
        
        //Create and display dialog
        GenericDialog importDialog = new GenericDialog("Import options");
        importDialog.addRadioButtonGroup(null, importTypeList, 1, 2, defaultImportType);
        importDialog.showDialog();
        
        // return to main menu if canceled
        if (importDialog.wasCanceled()){
            this.wasCanceled = true;
            return;
        }
        
        //Store user input
        this.isDirectory = ("Directory".equals(importDialog.getNextRadioButton()));  
        
        //get directory of file location depending on choice
        if (this.isDirectory){
            DirectoryChooser dc = new DirectoryChooser("Select directory:");
            this.targetLoc = dc.getDirectory();
        }
        else { 
            OpenDialog od = new OpenDialog("Select image file:");
            this.targetLoc = od.getPath();
        }
        
        // set was canceled if user did not choose a directory or file
        if (this.targetLoc == null) {
        wasCanceled = true;
        }      
    }
    
    /**
     * Method to prepare files for Imaris import. Includes checking of files for
     * readability, dividing into groups (Not implemented) and counting number of images
     */
    public void prepareImportImaris(){
        
        //Clearing all previous files
        if (!this.filesToOpen.isEmpty()){
            this.filesToOpen.clear();  
        }
        
        nFiles = 0;
        //Add files to list
        if(!this.isDirectory){       
            this.filesToOpen.add(targetLoc);
        }
        else{
            File dir = new File(this.targetLoc);
            File[] files = dir.listFiles();
            //Select all valid files
            try(ImarisHDFReader tester = new ImarisHDFReader()){
                for (int i = 0; i < files.length; i++) {
                    String tempId = files[i].getAbsolutePath();
                    if(!tester.isThisType(tempId, true)) {
                        continue;
                    }
                    filesToOpen.add(tempId);
                }
            } catch (IOException e) {
                IJ.error("Error checking files: " + e.getMessage());
            }   
        }
        
        this.nFiles = this.filesToOpen.size();
    }
    
    /**
     * Checks if there is a file in the file list at index
     * @param idx Index to be checked
     * @return Returns true if the file list has a none empty entry at index
     */
    public Boolean hasImageAt(int idx) {
        if (idx < 0 || idx >= this.filesToOpen.size()) {
            return false;
        }
        else if ("".equals(this.filesToOpen.get(idx))) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns number of images in file list
     * @return number of images
     */
    public int getNumberOfImages(){
        return this.nFiles;
    }
    
    /**
     * Gives the IMARIS 5.5 Image at index. Adds physical pixel size and LUT if present.
     * Importer has to be prepared first using prepareImporter().
     * @param idx Index of image to be retrieved
     * @return Image at idx as ImagePlus
     */
    public ImagePlus getImp(int idx){
        
        ImageProcessorReader reader = new ImageProcessorReader(new ImarisHDFReader());
        
        try {
            // Setup reader and get image information
            //Enable reader for metadata
            IMetadata meta = MetadataTools.createOMEXMLMetadata();
            reader.setMetadataStore(meta);
            reader.setId(filesToOpen.get(idx));
            
            //Get image information
            int bfChannelFlag = 0;
            int imageCount = reader.getImageCount();
            int imageWidth = reader.getSizeX();
            int imageHeight = reader.getSizeY();
            int imageChannels = reader.getSizeC();
            int imageSlices = reader.getSizeZ();
            int imageTime = reader.getSizeT();
            String dimensionOrder = reader.getDimensionOrder();
            
            //Getting LUT for each channel
            //Array to hold Luts for each channel
            LUT[] channelLUT = new LUT[imageChannels];
            if (!bfChannels.isEmpty()){
                bfChannels.clear();
            }
            for(int i = 0; i < imageChannels; i++) {
                //get color information from metadata (color class from bio formats)
                ome.xml.model.primitives.Color tempColor = meta.getChannelColor(0, i);
                //channels in brightfield have no color, so set to Greys
                if (tempColor == null) {
                    channelLUT[i] = bfLUT;
                    bfChannelFlag = bfChannelFlag | ((1 << (imageChannels-1)) >> i);
                    //Fix because channels are reversed on import
                    bfChannels.add(imageChannels -(i+1));                    
                }
                else{
                    // convert to java color
                    Color tempColorJava = new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), tempColor.getAlpha());
                    // create lut using javacolor
                    channelLUT[i] = LUT.createLutFromColor(tempColorJava);
                }
            }
            
            //import images to image stack
            ImageStack imStack = new ImageStack(imageWidth, imageHeight);
            //If image has multiple channels (images come sequential, but need XYCZT) 
            if (imageChannels > 1){
                int channelSize = imageCount / imageChannels;
                int[] channelStart = new int[channelSize - 1];
                for (int i = 0; i < channelStart.length; i++){
                    channelStart[i] = channelSize*(i);
                }
                for (int i = 0; i < channelSize; i++){
                    for(int j = 0; j < imageChannels; j++) {
                        ImageProcessor tempIp = reader.openProcessors(channelStart[j])[0];
                        imStack.addSlice(tempIp);
                        channelStart[j]++;
                    }                                 
                IJ.showStatus("Opening planes: " + i + " / " + channelSize);
                }
            }
            //If image has a single channel
            else{
                for (int i = 0; i < imageCount; i++){
                  ImageProcessor tempIp = reader.openProcessors(i)[0];
                   imStack.addSlice(tempIp);
                   IJ.showStatus("Opening planes: " + i + " / " + imageCount);
                   }
            }
            
            //Creating composite
            IJ.showStatus("Constructing image... ");
            //Getting image name
            String imageName = (String) meta.getImageName(0);
            imageName = imageName.split(".ims", 0)[0];
            
            ImagePlus imp = new ImagePlus(imageName, imStack);
            if (imageSlices > 1) {
                imp = HyperStackConverter.toHyperStack(imp, imageChannels, imageSlices, imageTime, dimensionOrder, "color");
            }
            CompositeImage composite = new CompositeImage(imp, CompositeImage.COLOR);
            
            //Setting LUT                
            for (int i = 0; i < imageChannels; i++) {
                
                //Imaris gives channels backwards?
                composite.setPosition(imageChannels - i, 1, 1); //1-index
                composite.setChannelLut(channelLUT[i]); //0-index
            }
            
            //Setting distance calibration
            Calibration cal = composite.getCalibration();
            Length sizeX = meta.getPixelsPhysicalSizeX(0);
            Length sizeY = meta.getPixelsPhysicalSizeY(0);
            Length sizeZ = meta.getPixelsPhysicalSizeZ(0);
            cal.setXUnit(sizeX.unit().getSymbol());
            cal.setYUnit(sizeY.unit().getSymbol());
            cal.setZUnit(sizeZ.unit().getSymbol());
            cal.pixelHeight = sizeY.value().doubleValue();
            cal.pixelWidth = sizeX.value().doubleValue();
            cal.pixelDepth = sizeZ.value().doubleValue();
            composite.setProperty("BFChannels", bfChannelFlag);
            
            reader.close();
            return composite;                  
                        
        } catch (FormatException e) {
            IJ.error("Format Exception on file " + idx + " (" + filesToOpen.get(idx) + ") " + e.getMessage());         
        } catch (IOException e) {
            IJ.error("IO Exception on file " + idx + " (" + filesToOpen.get(idx) + ") " + e.getMessage());         
        } 
        return null;
    }
    
    /**
     * Method to return a list of file names prepared for import
     * @return String array of filenames, null if no files prepared
     */
    public String[] getFileNames(){
        //returns null if there are no files
        if (nFiles == 0) {
            return null;
        }
        //getting list of file names
        String[] fileNames = new String[nFiles]; 
        for (int i = 0; i < nFiles; i++){
            File tempFile = new File(this.filesToOpen.get(i));
            fileNames[i] = tempFile.getName();          
        }
        return fileNames;
    }
    
    /**
     * Method to get the position of all BF channels of last image opened
     * @return ArrayList of Bright field channels. Null if no image was opened
     * or no BF channels Found. 
     */
    public ArrayList<Integer> getBfChannels() {
        return bfChannels;
    }
    
    /**
     * Returns the bright field LUT
     * @return LUT for BF
     */
    public LUT getBFLUT() {
        return bfLUT;
    }
    
    /**
     * Returns true if the user canceled the import
     * @return 
     */
    public Boolean wasCanceled() {
        return this.wasCanceled;
    }
    
    public int nChannels() {
        ImageProcessorReader reader = new ImageProcessorReader(new ImarisHDFReader());
        
        try {
            reader.setId(filesToOpen.get(0));
            return reader.getSizeC();
        }   
        catch (FormatException | IOException e) {
            IJ.error("Error checking files: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Returns the acquisition time of an image as seconds since java epoch.
     * @param idx index of image
     * @return 
     */
    public long getAquisitionTime(int idx) {
        ImarisHDFReader reader = new ImarisHDFReader();
        IMetadata meta = MetadataTools.createOMEXMLMetadata();
        reader.setMetadataStore(meta);
        try {
        reader.setId(filesToOpen.get(idx)); 
        return (meta.getImageAcquisitionDate(0).asInstant().getMillis()/1000);
        } catch (FormatException | IOException ex) {
            Logger.getLogger(OLDIMSImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
