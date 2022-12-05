/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Containers.Cells;

import java.util.ArrayList;

/**
 * 
 * @author Jan-Lukas Førde
 */
public class CellHolder {
    private ArrayList<CellGroup> cellGroups = new ArrayList();
    
    public CellHolder() {  
    }
    
    public void addGroup(CellGroup group) {
        cellGroups.add(group);
    }
    
    public ArrayList<CellGroup> getGroupList() {
        return cellGroups;
    }
    @Override
    public String toString() {
        return "Experiment";
    }
    
    
    
}
