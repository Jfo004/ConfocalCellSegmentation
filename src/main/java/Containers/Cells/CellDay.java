/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Containers.Cells;

import Containers.Old.MeasurementOld;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;

/**
 *
 * @author janlu
 */
public class CellDay {
    private ArrayList<Cell> cellList = new ArrayList();
    private File analysisFile;
    private Instant aquisitionTime;
    
    public CellDay(Instant aquisitionTime, File analysisFile) {
        this.aquisitionTime = aquisitionTime;
        this.analysisFile = analysisFile;
    }

    public ArrayList<Cell> getCellList() {
        return cellList;
    }
    
    public Cell getCellAt(int idx) {
        return cellList.get(idx);
    }

    public void addCell(Cell cell) {
        cellList.add(cell);
    }
    
    public void setCellList(ArrayList<Cell> cellList) {
        this.cellList = cellList;
    }
    
    public File getAnalysisFile() {
        return this.analysisFile;
    }
    
    @Override
    public String toString() {
        return aquisitionTime.toString();
    }
}
