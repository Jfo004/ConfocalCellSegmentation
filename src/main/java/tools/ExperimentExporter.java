/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import experiments.ExperimentNew;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author janlu
 */
public class ExperimentExporter {
    ExperimentExporter() {   
    }
    public static void exportExperiment(ExperimentNew experiment) {
        try {
            JAXBContext contextObj = JAXBContext.newInstance(ExperimentNew.class);
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshallerObj.marshal(experiment, System.out);
        } catch (JAXBException ex) {
            Logger.getLogger(ExperimentExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
