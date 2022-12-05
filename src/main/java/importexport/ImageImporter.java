/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package importexport;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.LUT;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import loci.formats.FormatException;
import loci.formats.IFormatHandler;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.in.ImarisHDFReader;
import loci.formats.meta.IMetadata;
import loci.plugins.util.ImageProcessorReader;
import ome.units.quantity.Length;
import ome.xml.model.primitives.Color;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Jan-Lukas Foerde
 */
public abstract class ImageImporter {
    
    protected ArrayList<String> filesToOpen = new ArrayList();
    

    public ImageImporter() {        
    }
    
    /**
     *Prepares importer for import.
     * @param file file or directory to be imported.
     */
    public void setImport(File file) {
        filesToOpen = createFileList(file);
    }

    
    /**
     * Returns channel of image as ImagePlus
     * @param imageIdx index of image
     * @param channelIdx index of channel (starting from 0)
     * @param frame index of frame
     * @return ImagePlus of channel
     */
    public ImagePlus getChannel(int imageIdx, int channelIdx, int frame) {
        try {
            IFormatReader tester = getFormatReader();
            tester.setId(filesToOpen.get(imageIdx));
            int width = tester.getSizeX();
            int height = tester.getSizeY();
            return getSubStack(0,0,width,height,channelIdx,imageIdx,frame);
        } catch (FormatException | IOException ex) {
            Logger.getLogger(ImageImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; 
    }
    
    /**
     * Returns sub-stack of an image
     * @param startX starting x-position in pixels
     * @param startY starting y-position in pixels
     * @param width sub-stack width in pixels
     * @param height sub-stack height in pixels
     * @param channelIdx channel of image to import
     * @param imageIdx index of image in file list
     * @param frameIdx index of frame
     * @return sub-stack of image as ImagePlus 
     */
    public ImagePlus getSubStack(int startX, int startY, int width, int height, int channelIdx, int imageIdx, int frameIdx){
        if( imageIdx >= filesToOpen.size() || imageIdx < 0) {
            return null;
        }
        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;
        ImageProcessorReader reader = new ImageProcessorReader(getFormatReader());
        IMetadata meta = MetadataTools.createOMEXMLMetadata();
        reader.setMetadataStore(meta);
        try {
            reader.setId(filesToOpen.get(imageIdx)); 
            if (channelIdx >= reader.getSizeC() || channelIdx < 0) {
                return null;
            }
            if ((startX + width) > reader.getSizeX()) width = reader.getSizeX() - startX;
            if ((startY + height) > reader.getSizeY()) height = reader.getSizeY() - startY;
            
            //Reading image data
            int sizeZ = reader.getSizeZ();
            ImageStack channelStack = new ImageStack();
            for(int i = 0;i < sizeZ; i++) {
                channelStack.addSlice(reader.openProcessors(reader.getIndex(i, channelIdx, frameIdx), startX, startY, width, height)[0]);                
            }
            ImagePlus channelImp = new ImagePlus(getFileName(imageIdx) + "_Channel:" + channelIdx + " _Frame:" + frameIdx, channelStack);
            
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
            return channelImp;
        } 
        catch (IOException | FormatException ex) {
            Logger.getLogger(IMSImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Returns the acquisition time of an image as seconds since java epoch.
     * @param idx index of image
     * @return
     */
    public Instant getAquisitionTime(int idx, int frame){
        IFormatReader reader = getFormatReader();
        IMetadata meta = MetadataTools.createOMEXMLMetadata();
        reader.setMetadataStore(meta);
        try {
        reader.setId(filesToOpen.get(idx));
        return (Instant.ofEpochMilli(meta.getImageAcquisitionDate(0).asInstant().getMillis()));
        } catch (FormatException | IOException ex) {
            Logger.getLogger(IMSImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * returns the number of channels for image at idx
     * @param idx index of image
     * @return number of channels
     */
    public int nChannels(int idx){
        ImageProcessorReader reader = new ImageProcessorReader(getFormatReader());
        
        try {
            reader.setId(filesToOpen.get(idx));
            return reader.getSizeC();
        }   
        catch (FormatException | IOException e) {
            IJ.error("Error checking files: " + e.getMessage());
            return 0;
        }
    }
    
    public int nChannels(File file){
        ImageProcessorReader reader = new ImageProcessorReader(getFormatReader());
        
        try {
            reader.setId(file.getAbsolutePath());
            return reader.getSizeC();
        }   
        catch (FormatException | IOException e) {
            IJ.error("Error checking files: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Returns the name of a file without extension
     * @param idx index of file
     * @return name of file
     */
    public String getFileName(int idx) {
        File tempFile = new File(filesToOpen.get(idx));
        return FilenameUtils.removeExtension(tempFile.getName());
    }
    
    /**
     * Returns the total number of acquisitions, including multiple frames in
     * a single file.
     * @return Returns the number of acquisitions.
     */
    public int getNumberOfImages() {
        ImageProcessorReader reader = new ImageProcessorReader(getFormatReader());
        int nImages = 0;
        for (int i = 0; i < filesToOpen.size(); i++) {
            try {
                reader.setId(filesToOpen.get(i));
                nImages += reader.getSizeT();
            }   
            catch (FormatException | IOException e) {
                IJ.error("Error checking files: " + e.getMessage());
                return 0;
            }
        }
        return nImages;
    } 

    /**
     * Returns number of images in file list
     * @return number of images
     */
    public int getNumberOfFiles() {
        return filesToOpen.size();
    }

    /**
     *Checks if "file" is a valid file for this importer. 
     * @param file File to check.
     * @return true if file is valid, false otherwise.
     */
    public boolean isValidFile(File file) {
                try(IFormatReader tester = getFormatReader()){
        return tester.isThisType(file.getAbsolutePath(), true);

        } catch (IOException ex) {
            Logger.getLogger(IMSImporter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Checks if there is a file at index
     * @param idx Index to be checked
     * @return true if file exists, false otherwise.
     */
    public Boolean hasImageAt(int idx) {
        if (idx < 0 || idx >= this.filesToOpen.size()) {
            return false;
        } else if ("".equals(this.filesToOpen.get(idx))) {
            return false;
        }
        return true;
    }

    /**
     * Method to return a list of file names available for import
     * @return String array of filenames, null if no files available
     */
    public String[] getFileNames() {
        //returns null if there are no files
        if (filesToOpen.isEmpty()) return null;
        
        //getting list of file names
        String[] fileNames = new String[filesToOpen.size()];
        for (int i = 0; i < filesToOpen.size(); i++) {
            File tempFile = new File(this.filesToOpen.get(i));
            fileNames[i] = tempFile.getName();
        }
        return fileNames;
    }

    protected ArrayList<String> createFileList(File inputFile) {
        ArrayList<String> tempList = new ArrayList();
        if (inputFile.isFile()) {
            if (isValidFile(inputFile)) {
                tempList.add(inputFile.getAbsolutePath());
                return tempList;
            }
        }
        for (File testFile : inputFile.listFiles()) {
            if (isValidFile(testFile)) {
                tempList.add(testFile.getAbsolutePath());
            }
        }
        return tempList;
    }
    
    /**
     * Returns the format specific format handler
     * @return Format handler
     */
    protected abstract IFormatReader getFormatReader();

}
