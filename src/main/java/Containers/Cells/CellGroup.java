/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Containers.Cells;

import java.util.ArrayList;
import tools.CellSubject;

/**
 *
 * @author janlu
 */
public class CellGroup {
    private ArrayList<CellSubject> cellFishList = new ArrayList();
    private String groupName;
    
    public CellGroup(String groupName) {
        this.groupName = groupName;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public ArrayList<CellSubject> getFishList() {
        return cellFishList;
    }
    
    public void addFish(CellSubject cellFish) {
        cellFishList.add(cellFish);
    }
    
    @Override
    public String toString() {
        return groupName;
    }
    
}
