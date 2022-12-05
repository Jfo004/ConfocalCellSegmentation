/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Containers;

import java.util.HashMap;

/**
 *
 * @author Jan-Lukas Foerde
 */
public class WellLayout {
    HashMap layout;
    String[] groups;
    String[][] wellMap;

    public WellLayout(HashMap layout, String[] groups, String[][] wellMap) {
        this.layout = layout;
        this.groups = groups;
        this.wellMap = wellMap;
    }

    public String[][] getWellMap() {
        return wellMap;
    }

    public void setWellMap(String[][] wellCoordinates) {
        this.wellMap = wellCoordinates;
    }
    

    public HashMap getLayout() {
        return layout;
    }

    public void setLayout(HashMap layout) {
        this.layout = layout;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }
    
    
}
