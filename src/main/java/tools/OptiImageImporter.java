package tools;



import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.LUT;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.logging.Level;
import java.util.logging.Logger;
import loci.formats.FormatException;
import loci.formats.MetadataTools;
import loci.formats.in.ImarisHDFReader;
import loci.formats.meta.IMetadata;
import loci.plugins.util.ImageProcessorReader;
import ome.units.quantity.Length;
import ome.xml.model.primitives.Color;
import org.apache.commons.io.FilenameUtils;

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
public class OptiImageImporter {
    private Boolean isDirectory = false;
    private String targetLoc = "";
    private ArrayList<String> filesToOpen = new ArrayList<String>();
    private int nFiles = 0;
    
    /**
     * Constructs importer with given import options and creates BF LUT.
     * @param isDir True if targets are multiple files in a directory, false if
     * the target is a single file.
     * @param targetLocation Location of directory or file.
     */
    public OptiImageImporter(Boolean isDir, String targetLocation) {
        this.isDirectory = isDir;
        this.targetLoc = targetLocation;
        if(!this.isDirectory){       
            this.filesToOpen.add(targetLoc);
        }
        else{
            File dir = new File(this.targetLoc);
            File[] files = dir.listFiles();
            //Select all valid files
            try(ImarisHDFReader tester = new ImarisHDFReader()){
                for (File file : files) {
                    String tempId = file.getAbsolutePath();
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
    
    public OptiImageImporter(File file) {
        if (file.isFile()) {
            nFiles = 1;
            filesToOpen.add(file.getAbsolutePath());
            return;
        }
        try(ImarisHDFReader tester = new ImarisHDFReader()){
            for (File testFile : file.listFiles()) {
                if(!tester.isThisType(testFile.getAbsolutePath(), true)) {
                    continue;
                }
                filesToOpen.add(testFile.getAbsolutePath());
            }
        } catch (IOException e) {
            IJ.error("Error checking files: " + e.getMessage());
        }
        nFiles = filesToOpen.size();
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
     * DONT USE
     * Gives the IMARIS 5.5 Image at index. Adds physical pixel size and LUT if present.
     * Importer has to be prepared first using prepareImporter().
     * @param idx Index of image to be retrieved
     * @return Image at idx as ImagePlus
     */
    public ImagePlus getImage(int idx){
        
        ImageProcessorReader reader = new ImageProcessorReader(new ImarisHDFReader());
        
        try {
            reader.setId(filesToOpen.get(idx));
            int imageChannels = reader.getSizeC();
            for (int i = 0; i < imageChannels; i++) {
                
            }
            return null;                  
                        
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
            Logger.getLogger(OptiImageImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * returns the number of channels as int
     * @param idx index of image
     * @return 
     */
    int nChannels(int idx) {
        ImageProcessorReader reader = new ImageProcessorReader(new ImarisHDFReader());
        
        try {
            reader.setId(filesToOpen.get(idx));
            return reader.getSizeC();
        }   
        catch (FormatException | IOException e) {
            IJ.error("Error checking files: " + e.getMessage());
            return 0;
        }
    }

    ImagePlus getChannel(int imageIdx, int channelIdx) {
        //Setup and check of index
        if( imageIdx >= filesToOpen.size() || imageIdx < 0) {
            return null;
        }
        ImageProcessorReader reader = new ImageProcessorReader(new ImarisHDFReader());
        IMetadata meta = MetadataTools.createOMEXMLMetadata();
        reader.setMetadataStore(meta);
        try {
            reader.setId(filesToOpen.get(imageIdx)); 
            if (channelIdx >= reader.getSizeC() || channelIdx < 0) {
                return null;
            }
            
            //Reading image data
            int sizeZ = reader.getSizeZ();
            ImageStack channelStack = new ImageStack();
            for(int i = 0;i < sizeZ; i++) {
                channelStack.addSlice(reader.openProcessors(reader.getIndex(i, channelIdx, 0))[0]);
            }
            ImagePlus channelImp = new ImagePlus(getTitle(imageIdx) + "_Channel:" + channelIdx, channelStack);
            
            //Setting pixel size
            Calibration cal = channelImp.getCalibration();
            Length pSizeX = meta.getPixelsPhysicalSizeX(0);
            Length pSizeY = meta.getPixelsPhysicalSizeY(0);
            Length pSizeZ = meta.getPixelsPhysicalSizeZ(0);
            cal.setXUnit(pSizeX.unit().getSymbol());
            cal.setYUnit(pSizeY.unit().getSymbol());
            cal.setZUnit(pSizeZ.unit().getSymbol());
            cal.pixelHeight = pSizeY.value().doubleValue();
            cal.pixelWidth = pSizeX.value().doubleValue();
            cal.pixelDepth = pSizeZ.value().doubleValue();
            
            //Setting LUT
            channelImp.setProperty("isBF", false);
            Color tempColor = meta.getChannelColor(0, (reader.getSizeC() - 1) -channelIdx); //Lut inverted compared to channel in metadata???
            //BF channel on confocal gives no color
            if (tempColor == null) {
                channelImp.setProperty("isBF", true);
                tempColor = new Color(255, 255, 255, 255);
                }
            java.awt.Color tempColorJava = new java.awt.Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), tempColor.getAlpha());
            channelImp.setLut(LUT.createLutFromColor(tempColorJava));
            reader.close();
            return channelImp;
        } 
        catch (IOException | FormatException ex) {
            Logger.getLogger(OptiImageImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;   
    }

    
    /**
     * Returns the name of a file without extension
     * @param idx index of file
     * @return 
     */
    String getTitle(int idx) {
        File tempFile = new File(filesToOpen.get(idx));
        return FilenameUtils.removeExtension(tempFile.getName());
    }

    ImagePlus getSubImage(int startX, int startY, int width, int height, int cellChannel, int imageIdx) {
        if( imageIdx >= filesToOpen.size() || imageIdx < 0) {
            return null;
        }
        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;
        ImageProcessorReader reader = new ImageProcessorReader(new ImarisHDFReader());
        IMetadata meta = MetadataTools.createOMEXMLMetadata();
        reader.setMetadataStore(meta);
        try {
            reader.setId(filesToOpen.get(imageIdx)); 
            if (cellChannel >= reader.getSizeC() || cellChannel < 0) {
                return null;
            }
            if ((startX + width) > reader.getSizeX()) width = reader.getSizeX() - startX;
            if ((startY + height) > reader.getSizeY()) height = reader.getSizeY() - startY;
            
            //Reading image data
            int sizeZ = reader.getSizeZ();
            ImageStack channelStack = new ImageStack();
            for(int i = 0;i < sizeZ; i++) {
                channelStack.addSlice(reader.openProcessors(reader.getIndex(i, cellChannel, 0), startX, startY, width, height)[0]);                
            }
            ImagePlus channelImp = new ImagePlus(getTitle(imageIdx) + "_Channel:" + cellChannel, channelStack);
            
            //Setting pixel size
            Calibration cal = channelImp.getCalibration();
            Length pSizeX = meta.getPixelsPhysicalSizeX(0);
            Length pSizeY = meta.getPixelsPhysicalSizeY(0);
            Length pSizeZ = meta.getPixelsPhysicalSizeZ(0);
            cal.setXUnit(pSizeX.unit().getSymbol());
            cal.setYUnit(pSizeY.unit().getSymbol());
            cal.setZUnit(pSizeZ.unit().getSymbol());
            cal.pixelHeight = pSizeY.value().doubleValue();
            cal.pixelWidth = pSizeX.value().doubleValue();
            cal.pixelDepth = pSizeZ.value().doubleValue();
            
            //Setting LUT
            channelImp.setProperty("isBF", false);
            Color tempColor = meta.getChannelColor(0, (reader.getSizeC() - 1) -cellChannel); //Lut inverted compared to channel in metadata???
            //BF channel on confocal gives no color
            if (tempColor == null) {
                channelImp.setProperty("isBF", true);
                tempColor = new Color(255, 255, 255, 255);
                }
            java.awt.Color tempColorJava = new java.awt.Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), tempColor.getAlpha());
            channelImp.setLut(LUT.createLutFromColor(tempColorJava));
            reader.close();
            return channelImp;
        } 
        catch (IOException | FormatException ex) {
            Logger.getLogger(OptiImageImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;   
    }
}
