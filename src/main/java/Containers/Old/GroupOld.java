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
public class GroupOld implements Serializable{
    private String groupName;
    private ArrayList<SubjectOld> fishList = new ArrayList();
    private ExperimentOld parent;
    
    GroupOld(String groupName) {
        this.groupName = groupName.toUpperCase();
    }
    GroupOld(String groupName, File fish, String fishName) {
        this(groupName);
        addFish(fish, fishName);
    }
    
    GroupOld(String groupName, File fish, String fishName, ExperimentOld parent) {
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

    public SubjectOld getFish(String fishName) {
        for (SubjectOld fish : fishList) {
            if (fish.hasName(fishName.toUpperCase())) return fish;
        }
        return null;
    }

    void addFish(File file, String fishName) {
        for (SubjectOld fish : fishList) {
            if (fish.hasName(fishName)) {
                fish.addFile(file);
                return;
            } 
        }
        fishList.add(new SubjectOld(fishName, file, this));
    }
    
    public int getFishCount() {
        return fishList.size();
    }

    public Iterable<SubjectOld> getFish() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ArrayList<SubjectOld> getFishList() {
        return fishList;
    }
    
    public ExperimentOld getParentExperiment() {
        return parent;
    }
}
