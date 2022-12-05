/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Containers.Cells;

import java.util.ArrayList;
import mcib3d.geom.Object3D;

/**
 *
 * @author janlu
 */
public class Cell {
    private int objectNumber;
    private double positionX;
    private double positionY;
    private double positionZ;
    private int volumePix;
    private double volumeUnit;
    private int surfacePix;
    private double surfaceUnit;
    private ArrayList<Double> minIntensity = new ArrayList();
    private ArrayList<Double> maxIntensity = new ArrayList();
    private ArrayList<Double> averageIntensity = new ArrayList();
    private ArrayList<Double> medianIntensity = new ArrayList();
    private ArrayList<Double> integratedIntensity = new ArrayList();
    private ArrayList<Integer> measurementChannels;

    public Cell(int volumePix, double volumeUnit, double averageIntensity, int cellNumber, double positionX, double positionY, double positionZ) {
        
    }

    public Cell(int objectNumber, double positionX, double positionY, double positionZ, int volumePix, double volumeUnit, int surfacePix, double surfaceUnit,ArrayList<Integer> measurementChannels, ArrayList<Double> minIntensity, ArrayList<Double> maxIntensity, ArrayList<Double> averageIntensity, ArrayList<Double> medianIntensity, ArrayList<Double> integratedIntensity) {
        this.objectNumber = objectNumber;
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.volumePix = volumePix;
        this.volumeUnit = volumeUnit;
        this.surfacePix = surfacePix;
        this.surfaceUnit = surfaceUnit;
        this.measurementChannels = measurementChannels;
        this.minIntensity = minIntensity;
        this.maxIntensity = maxIntensity;
        this.averageIntensity = averageIntensity;
        this.medianIntensity = medianIntensity;
        this.integratedIntensity = integratedIntensity;
    }

    public int getVolumePix() {
        return volumePix;
    }

    public void setVolumePix(int volumePix) {
        this.volumePix = volumePix;
    }

    public double getVolumeUnit() {
        return volumeUnit;
    }

    public void setVolumeUnit(double volumeUnit) {
        this.volumeUnit = volumeUnit;
    }

    public int getObjectNumber() {
        return objectNumber;
    }

    public void setObjectNumber(int objectNumber) {
        this.objectNumber = objectNumber;
    }

    public int getSurfacePix() {
        return surfacePix;
    }

    public void setSurfacePix(int surfacePix) {
        this.surfacePix = surfacePix;
    }

    public double getSurfaceUnit() {
        return surfaceUnit;
    }

    public void setSurfaceUnit(double surfaceUnit) {
        this.surfaceUnit = surfaceUnit;
    }

    public ArrayList<Integer> getMeasurementChannels() {
        return measurementChannels;
    }

    public void setMeasurementChannels(ArrayList<Integer> measurementChannels) {
        this.measurementChannels = measurementChannels;
    }

    
    
    public ArrayList<Double> getMinIntensity() {
        return minIntensity;
    }

    public void setMinIntensity(ArrayList<Double> minIntensity) {
        this.minIntensity = minIntensity;
    }

    public ArrayList<Double> getMaxIntensity() {
        return maxIntensity;
    }

    public void setMaxIntensity(ArrayList<Double> maxIntensity) {
        this.maxIntensity = maxIntensity;
    }

    public ArrayList<Double> getAverageIntensity() {
        return averageIntensity;
    }

    public void setAverageIntensity(ArrayList<Double> averageIntensity) {
        this.averageIntensity = averageIntensity;
    }

    public ArrayList<Double> getMedianIntensity() {
        return medianIntensity;
    }

    public void setMedianIntensity(ArrayList<Double> medianIntensity) {
        this.medianIntensity = medianIntensity;
    }

    public ArrayList<Double> getIntegratedIntensity() {
        return integratedIntensity;
    }

    public void setIntegratedIntensity(ArrayList<Double> integratedIntensity) {
        this.integratedIntensity = integratedIntensity;
    }

    

    public int getCellNumber() {
        return objectNumber;
    }

    public void setCellNumber(int cellNumber) {
        this.objectNumber = cellNumber;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public double getPositionZ() {
        return positionZ;
    }

    public void setPositionZ(double positionZ) {
        this.positionZ = positionZ;
    }   
    
}
