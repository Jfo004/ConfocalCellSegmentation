package experiments;


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
@XmlRootElement
public class FishNew implements Serializable{
    private String name;
    private ArrayList<MeasurementNew> measurementList = new ArrayList();
    private FishGroupNew parent;
    
    public FishNew() {
        
    }
    public FishNew(String name, FishGroupNew parent) {
        this.name = name;
        this.parent = parent;
    }
    FishNew(String name, File confocalFile, FishGroupNew parent) {
        this.name = name.toUpperCase();
        this.measurementList.add(new MeasurementNew(confocalFile, this));
        this.parent = parent;
    }

    void addFile(File confocalFile) {
        measurementList.add(new MeasurementNew(confocalFile, this));
    }

    @XmlAttribute
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlTransient
    public FishGroupNew getParentFishGroup() {
        return parent;
    }
    public void setParentFishGroup(FishGroupNew parent) {
        this.parent = parent;
    }
    
    @XmlTransient
    public ArrayList<MeasurementNew> getMeasurements() {
        return measurementList;
    }
    public void setMeasurements(ArrayList<MeasurementNew> measurementList) {
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

    
    
    
    
}
