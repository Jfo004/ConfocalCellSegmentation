package experiments;


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
public class FishGroup implements Serializable{
    private String groupName;
    private ArrayList<Fish> fishList = new ArrayList();
    private Experiment parent;
    
    FishGroup(String groupName) {
        this.groupName = groupName.toUpperCase();
    }
    FishGroup(String groupName, File fish, String fishName) {
        this(groupName);
        addFish(fish, fishName);
    }
    
    FishGroup(String groupName, File fish, String fishName, Experiment parent) {
        this(groupName, fish, fishName);
        this.parent = parent;
    }
    

    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName.toUpperCase();
    }

    boolean hasName(String name) {
        return groupName.equalsIgnoreCase(name);
    }

    public String[] getFishNames() {
        String[] fishNameArray = new String[fishList.size()];
        for (int i = 0; i < fishList.size(); i++) {
            fishNameArray[i] = fishList.get(i).getName();
        }
        return fishNameArray;
    }

    public Fish getFish(String fishName) {
        for (Fish fish : fishList) {
            if (fish.hasName(fishName.toUpperCase())) return fish;
        }
        return null;
    }

    void addFish(File file, String fishName) {
        for (Fish fish : fishList) {
            if (fish.hasName(fishName)) {
                fish.addFile(file);
                return;
            } 
        }
        fishList.add(new Fish(fishName, file, this));
    }
    
    public int getFishCount() {
        return fishList.size();
    }

    public Iterable<Fish> getFish() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ArrayList<Fish> getFishList() {
        return fishList;
    }
    
    public Experiment getParentExperiment() {
        return parent;
    }
}
