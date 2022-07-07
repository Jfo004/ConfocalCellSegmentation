/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import ij.ImagePlus;
import java.io.File;
import java.util.ArrayList;
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
     * Returns image at index as ImagePlus.
     * Importer has to be prepared first using setImport().
     * @param idx Index of image to be retrieved
     * @return Image at idx as ImagePlus, null if no image found
     */
    public abstract ImagePlus getImage(int idx);
    
    /**
     * Returns channel of image as ImagePlus
     * @param imageIdx index of image
     * @param channelIdx index of channel (starting from 0)
     * @return ImagePlus of channel
     */
    abstract ImagePlus getChannel(int imageIdx, int channelIdx);
    
    /**
     * Returns sub-stack of an image
     * @param startX starting x-position in pixels
     * @param startY starting y-position in pixels
     * @param width sub-stack width in pixels
     * @param height sub-stack height in pixels
     * @param cellChannel channel of image to import
     * @param imageIdx index of image
     * @return sub-stack of image as ImagePlus 
     */
    abstract ImagePlus getSubStack(int startX, int startY, int width, int height, int cellChannel, int imageIdx);
    
    /**
     * Returns the acquisition time of an image as seconds since java epoch.
     * @param idx index of image
     * @return
     */
    public abstract long getAquisitionTime(int idx);
    
    /**
     * returns the number of channels for image at idx
     * @param idx index of image
     * @return number of channels
     */
    abstract int nChannels(int idx);
    
    /**
     * Returns the name of a file without extension
     * @param idx index of file
     * @return name of file
     */
    String getFileName(int idx) {
        File tempFile = new File(filesToOpen.get(idx));
        return FilenameUtils.removeExtension(tempFile.getName());
    }
    
    
    

    /**
     * Returns number of images in file list
     * @return number of images
     */
    public int getNumberOfImages() {
        return filesToOpen.size();
    }

    /**
     *Checks if "file" is a valid file for this importer. 
     * @param file File to check.
     * @return true if file is valid, false otherwise.
     */
    abstract public boolean isValidFile(File file);
    
    
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




    
}
