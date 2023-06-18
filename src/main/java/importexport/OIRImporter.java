/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package importexport;

import loci.formats.IFormatReader;
import loci.formats.in.OIRReader;

/**
 *
 * @author Jan-Lukas Foerde
 */
public class OIRImporter extends ImageImporter {

    @Override
    protected IFormatReader getFormatReader() {
        return new OIRReader();
    }
    
    
}
