/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import experiments.ExperimentNew;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Jan-Lukas Foerde
 */
public class ExperimentImporter {
    File experimentPath;
    ExperimentNew experiment;

    public ExperimentImporter(File experimentPath) {
        this.experimentPath = experimentPath;
    }
    
    public ExperimentNew importExperiment() {
        
        try {
            JAXBContext context = JAXBContext.newInstance(ExperimentNew.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            experiment = (ExperimentNew) unmarshaller.unmarshal(experimentPath);
            experiment.updateParent();
            experiment.updateFile(experimentPath.getParentFile());
        } catch (JAXBException ex) {
            Logger.getLogger(ExperimentImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return experiment;
    }
}
