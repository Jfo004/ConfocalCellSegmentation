package experiments;


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
@XmlRootElement
public class FishGroupNew implements Serializable{
    private String groupName;
    private ArrayList<FishNew> fishList = new ArrayList();
    private ExperimentNew parent;
    
    public FishGroupNew() {
        
    }
    public FishGroupNew(String groupName) {
        this.groupName = groupName.toUpperCase();
    }
    FishGroupNew(String groupName, File fish, String fishName) {
        this(groupName);
        addFish(fish, fishName);
    }
    FishGroupNew(String groupName, File fish, String fishName, ExperimentNew parent) {
        this(groupName, fish, fishName);
        this.parent = parent;
    }
    
    @XmlAttribute(name = "Name")
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName.toUpperCase();
    }
    
    @XmlElement
    public ArrayList<FishNew> getFishList() {
        return fishList;
    }
    public void setFishList(ArrayList<FishNew> fishList) {
        this.fishList = fishList;
    }
    
    @XmlTransient
    public ExperimentNew getParent() {
        return parent;
    }
    public void setParent(ExperimentNew parent) {
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

    public FishNew getFish(String fishName) {
        for (FishNew fish : fishList) {
            if (fish.hasName(fishName.toUpperCase())) return fish;
        }
        return null;
    }

    void addFish(File file, String fishName) {
        for (FishNew fish : fishList) {
            if (fish.hasName(fishName)) {
                fish.addFile(file);
                return;
            } 
        }
        fishList.add(new FishNew(fishName, file, this));
    }
    
    public int getFishCount() {
        return fishList.size();
    }

    public Iterable<FishNew> getFish() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
