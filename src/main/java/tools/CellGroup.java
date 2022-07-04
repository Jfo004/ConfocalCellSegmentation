/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.ArrayList;

/**
 *
 * @author janlu
 */
public class CellGroup {
    private ArrayList<CellFish> cellFishList = new ArrayList();
    private String groupName;
    
    public CellGroup(String groupName) {
        this.groupName = groupName;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public ArrayList<CellFish> getFishList() {
        return cellFishList;
    }
    
    public void addFish(CellFish cellFish) {
        cellFishList.add(cellFish);
    }
    
    @Override
    public String toString() {
        return groupName;
    }
    
}
