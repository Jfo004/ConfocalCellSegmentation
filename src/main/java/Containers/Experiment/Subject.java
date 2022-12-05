package Containers.Experiment;


import Containers.Experiment.ExperimentGroup;
import Containers.Experiment.Measurement;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author janlu
 */
@XmlRootElement(name = "Subject")
public class Subject implements Serializable{
    private String name;
    private ArrayList<Measurement> measurementList = new ArrayList();
    private ExperimentGroup parent;
    
    public Subject() {
        
    }
    public Subject(String name, ExperimentGroup parent) {
        this.name = name;
        this.parent = parent;
    }
    public Subject(String name, File confocalFile, ExperimentGroup parent) {
        this.name = name.toUpperCase();
        this.measurementList.add(new Measurement(confocalFile, this));
        this.parent = parent;
    }

    void addFile(File confocalFile) {
        measurementList.add(new Measurement(confocalFile, this));
    }

    @XmlAttribute(name = "Subject_ID")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlTransient
    public ExperimentGroup getParentFishGroup() {
        return parent;
    }
    public void setParentFishGroup(ExperimentGroup parent) {
        this.parent = parent;
    }
    
    @XmlElement(name = "Measurement")
    public ArrayList<Measurement> getMeasurements() {
        return measurementList;
    }
    public void setMeasurements(ArrayList<Measurement> measurementList) {
        this.measurementList = measurementList;
    }
    
    
    public boolean hasName(String name) {
        return this.name.equalsIgnoreCase(name);
    }

    public String[] getMeasurementNames() {
        String[] measurementNameArray = new String[measurementList.size()];
        for (int i = 0; i < measurementList.size(); i++) {
            measurementNameArray[i] = measurementList.get(i).getFileName();
        }
        return measurementNameArray;
    }

    void updateParents(ExperimentGroup parent) {
        this.parent = parent;
        for (Measurement measurement : measurementList) {
            measurement.updateParents(this);
        }
    }

    void updateFile(File experimentPath) {
        for (Measurement measurement : measurementList) {
            measurement.updateFile(experimentPath);
        }
    }

    void createRelativeFilePath() {
        for (Measurement measurement : measurementList) {
            measurement.createRelativeFilePath();
        }
    }

    
    
    
    
}
