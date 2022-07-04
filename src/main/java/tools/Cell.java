/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import mcib3d.geom.Object3D;

/**
 *
 * @author janlu
 */
public class Cell {
    private int volumePix;
    private double volumeUnit;
    private double averageIntensity;
    private int cellNumber;
    private double positionX;
    private double positionY;
    private double positionZ;

    public Cell(int volumePix, double volumeUnit, double averageIntensity, int cellNumber, double positionX, double positionY, double positionZ) {
        this.volumePix = volumePix;
        this.volumeUnit = volumeUnit;
        this.averageIntensity = averageIntensity;
        this.cellNumber = cellNumber;
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
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

    public double getAverageIntensity() {
        return averageIntensity;
    }

    public void setAverageIntensity(double averageIntensity) {
        this.averageIntensity = averageIntensity;
    }

    public int getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(int cellNumber) {
        this.cellNumber = cellNumber;
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
