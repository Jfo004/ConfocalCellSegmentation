/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import Containers.Cells.CellDay;
import java.util.ArrayList;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;

/**
 *
 * @author janlu
 */
public class CellSubject {
    ArrayList<CellDay> dayList = new ArrayList();
    private String fishName;
    
    public CellSubject(String name) {
        this.fishName = name;
    }
    
    public String getName() {
        return fishName;
    }
    
    public void addDay(CellDay cellDay) {
        dayList.add(cellDay);
    }
    public ArrayList<CellDay> getDayList() {
        return dayList;
    }
    
    @Override
    public String toString() {
        return fishName;
    }
    
}
