package importexport;



import loci.formats.IFormatReader;
import loci.formats.in.ImarisHDFReader;

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
public class IMSImporter extends ImageImporter {
    
    
    public IMSImporter() {
    }

    
    @Override
    protected IFormatReader getFormatReader() {
        return new ImarisHDFReader();
    }
}
