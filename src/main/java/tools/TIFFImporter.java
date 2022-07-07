/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import ij.ImagePlus;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import loci.formats.in.TiffReader;

/**
 *
 * @author Jan-Lukas Foerde
 */
public class TIFFImporter extends ImageImporter {


    @Override
    public ImagePlus getImage(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    ImagePlus getChannel(int imageIdx, int channelIdx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    ImagePlus getSubStack(int startX, int startY, int width, int height, int cellChannel, int imageIdx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public long getAquisitionTime(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    int nChannels(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isValidFile(File file) {
        try(TiffReader tester = new TiffReader()){
        return tester.isThisType(file.getAbsolutePath(), true);

        } catch (IOException ex) {
            Logger.getLogger(IMSImporter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
}
