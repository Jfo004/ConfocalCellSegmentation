package Containers.Old;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author janlu
 */
public class SubjectOld implements Serializable{
    private String name;
    private ArrayList<MeasurementOld> measurementList = new ArrayList();
    private GroupOld parent;
    
    SubjectOld(String name, File confocalFile, GroupOld parent) {
        this.name = name.toUpperCase();
        this.measurementList.add(new MeasurementOld(confocalFile, this));
        this.parent = parent;
    }

    void addFile(File confocalFile) {
        measurementList.add(new MeasurementOld(confocalFile, this));
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    public ArrayList<MeasurementOld> getMeasurements() {
        return measurementList;
    }
    
    public GroupOld getParentFishGroup() {
        return parent;
    }
    
    
    
}
