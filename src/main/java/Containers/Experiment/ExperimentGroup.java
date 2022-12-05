package Containers.Experiment;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author janlu
 */
@XmlRootElement(name = "Group")
public class ExperimentGroup implements Serializable{
    private String groupName;
    private ArrayList<Subject> fishList = new ArrayList();
    private Experiment parent;
    
    public ExperimentGroup() {
        
    }
    public ExperimentGroup(String groupName) {
        this.groupName = groupName.toUpperCase();
    }
    ExperimentGroup(String groupName, File fish, String fishName) {
        this(groupName);
        addFish(fish, fishName);
    }
    ExperimentGroup(String groupName, File fish, String fishName, Experiment parent) {
        this(groupName, fish, fishName);
        this.parent = parent;
    }
    
    @XmlAttribute(name = "Group_ID")
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName.toUpperCase();
    }
    
    @XmlElement(name = "Subject")
    public ArrayList<Subject> getSubjectList() {
        return fishList;
    }
    public void setFishList(ArrayList<Subject> fishList) {
        this.fishList = fishList;
    }
    
    @XmlTransient
    public Experiment getParent() {
        return parent;
    }
    public void setParent(Experiment parent) {
        this.parent = parent;
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

    public Subject getFish(String fishName) {
        for (Subject fish : fishList) {
            if (fish.hasName(fishName.toUpperCase())) return fish;
        }
        return null;
    }

    void addFish(File file, String fishName) {
        for (Subject fish : fishList) {
            if (fish.hasName(fishName)) {
                fish.addFile(file);
                return;
            } 
        }
        fishList.add(new Subject(fishName, file, this));
    }
    
    public int getFishCount() {
        return fishList.size();
    }

    public Iterable<Subject> getFish() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void updateParents(Experiment parent) {
        this.parent = parent;
        for(Subject subject : fishList) {
            subject.updateParents(this);
        }
    }

    void updateFile(File experimentPath) {
        for(Subject subject : fishList) {
            subject.updateFile(experimentPath);
        }
    }

    void createRelativeFilePath() {
        for(Subject subject : fishList) {
            subject.createRelativeFilePath();
        }
    }


}
