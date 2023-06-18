/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package importexport;

/**
 *
 * @author Jan-Lukas Foerde
 */
public class ImageImporterFactory {
    ImageImporterFactory() {
        
    }
    public static ImageImporter createImporter(FileType fileType) {
        ImageImporter imageImporter;
        switch(fileType) {
            case IMS:
                imageImporter = new IMSImporter();
                break;
            case TIFF:
                imageImporter = new TIFFImporter();
                break;
            case OIR:
                imageImporter = new OIRImporter();
                break;
            default:
                
                return null;
        }
        
        return imageImporter;
    }

    
}
