/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package importexport;

import Containers.Experiment.Experiment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author janlu
 */
public class ExperimentExporterXML {
    ExperimentExporterXML() {   
    }
    public static void exportExperiment(Experiment experiment) {
        experiment.createRelativeFilePath();
        try {
            JAXBContext contextObj = JAXBContext.newInstance(Experiment.class);
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File outputFile = new File(experiment.getConfocalDirectory(), experiment.getName() + ".xml");
            OutputStream outputStream = new FileOutputStream(outputFile);
            marshallerObj.marshal(experiment, outputStream);
        } catch (JAXBException ex) {
            Logger.getLogger(ExperimentExporterXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExperimentExporterXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
