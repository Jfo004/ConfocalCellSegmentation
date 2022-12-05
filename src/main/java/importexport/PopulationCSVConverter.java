/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package importexport;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;
import java.util.ArrayList;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageByte;
import mcib3d.image3d.ImageFloat;
import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageShort;

/**
 *
 * @author Jan-Lukas Foerde
 */
public class PopulationCSVConverter {
    private ImageHandler intensityIH;
    private Objects3DPopulation objectPopulation;
    private ArrayList<String[]> csvList = new ArrayList();
    
    public PopulationCSVConverter(ImageHandler intensityIH, Objects3DPopulation objectPopulation) {
        this.intensityIH = intensityIH;
        this.objectPopulation = objectPopulation;
    }
    
    public PopulationCSVConverter(ImagePlus intensityImp, Objects3DPopulation objectPopulation) {
        this.objectPopulation = objectPopulation;
        if (intensityImp.getProcessor() instanceof FloatProcessor) intensityIH = new ImageFloat(intensityImp);
        else if (intensityImp.getProcessor() instanceof ShortProcessor) intensityIH = new ImageShort(intensityImp);
        else if (intensityImp.getProcessor() instanceof ByteProcessor) intensityIH = new ImageByte(intensityImp);
        else {
            System.out.println(intensityImp.getTitle() + " processor type not found.");
            intensityIH = null;
        }
    }
    
    
    
    public ArrayList<String[]> getCSV() {
        if (intensityIH == null) {
            System.out.println("processor type not found.");
            return null;
        }
        if (!csvList.isEmpty()) return csvList;
        
        //Adding Header
        
        
        return csvList;
    }
    
}
