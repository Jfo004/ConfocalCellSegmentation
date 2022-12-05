package Containers.Old;


import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.io.FilenameUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author janlu
 */
public class ExperimentOld implements Serializable{
    private String name = "None";
    private boolean usesGroups = true;
    private File confocalDirectory;
    private ArrayList<GroupOld> groups = new ArrayList();
    private ArrayList<SubjectOld> fishList = new ArrayList();
    private Instant timeOfFertilization;
    private Instant timeOfInjection;
    private HashMap groupMap;
    private int groupCount = 0;
    private int fishCount = 0;
    private int imageCount = 0;
    
    
/**
 * 
     * @param name
 * @param confocalDirectory
 * @param timeOfFertilization
 * @param timeOfInjection 
 */
    public ExperimentOld(String name, File confocalDirectory, Instant timeOfFertilization, Instant timeOfInjection) {
        this(name, confocalDirectory, timeOfFertilization, timeOfInjection, null);    
    }
    public ExperimentOld(String name, File confocalDirectory, Instant timeOfFertilization, Instant timeOfInjection, HashMap groupMap) {
        this.name = name;
        this.confocalDirectory = confocalDirectory;
        this.timeOfFertilization = timeOfFertilization;
        this.timeOfInjection = timeOfInjection;
        
        //Make individual groups
        if (groupMap == null) {
            usesGroups = false;
            for (File file : confocalDirectory.listFiles()){
                boolean placedFile = false;
                if (FilenameUtils.isExtension(file.getName(), "ims")){
                    String tempFishName = file.getName().substring(0, file.getName().indexOf("_")).toUpperCase();
                    for (GroupOld checkGroup : groups) {
                        if (checkGroup.hasName(tempFishName)) {
                            checkGroup.addFish(file, tempFishName);
                            placedFile = true;
                        }
                    }
                    if (!placedFile) {
                        groups.add(new GroupOld(tempFishName, file, tempFishName));
                    }
                    imageCount++;
               }
            }
        }
        else {
            usesGroups = true;
            for (File file : confocalDirectory.listFiles()){
                boolean placedFile = false;
                if (FilenameUtils.isExtension(file.getName(), "ims")){
                    String tempGroupName;
                    String tempFishName = (String)groupMap.get(file.getName().substring(0, file.getName().indexOf("_")).toUpperCase());
                    if (tempFishName == null) tempFishName = file.getName().substring(0, file.getName().indexOf("_")).toUpperCase();
                    if (tempFishName.contains("(")) tempGroupName = tempFishName.substring(0, tempFishName.indexOf("("));
                    else tempGroupName = "None";
                    
                    for (GroupOld checkGroup : groups) {
                        if (checkGroup.hasName(tempGroupName)) {
                            checkGroup.addFish(file, tempFishName);
                            placedFile = true;
                        }
                    }
                    if (!placedFile) {
                        groups.add(new GroupOld(tempGroupName, file, tempFishName, this));
                    }
                    imageCount++;
                }
            }
        }
        if (usesGroups) {
            fishCount = 0;
            for (GroupOld group : groups) {
                fishCount += group.getFishCount();
                groupCount = groups.size();
            }
        }
        else {
            fishCount = fishList.size();
            groupCount = groups.size();
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public File getConfocalDirectory() {
        return confocalDirectory;
    }
    public void setConfocalDirectory(File confocalDirectory) {
        this.confocalDirectory = confocalDirectory;
    }
    
    public Instant getTimeOfFertilization() {
        return timeOfFertilization;
    }
    public void setTimeOfFertilization(Instant timeOfFertilization) {
        this.timeOfFertilization = timeOfFertilization;
    }
    
    public Instant getTimeOfInjection() {
        return timeOfInjection;
    }
    public void setTimeOfInjection(Instant timeOfInjection) {
        this.timeOfInjection = timeOfInjection;
    }
    
    public int getGroupCount() {
        return groupCount;
    }
    
    public int getFishCount() {
        return fishCount;
    }
    
    public int getImageCount() {
        return imageCount;
    }
    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public boolean isUsesGroups() {
        return usesGroups;
    }

    public String[] getGroupNames() {
        String[] groupNameArray = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            groupNameArray[i] = groups.get(i).getGroupName();
        }
        return groupNameArray;
    }
    public GroupOld getGroup(String groupName) {
        for (GroupOld group : groups) {
            if (group.hasName(groupName)) return group;
        }
        return null;        
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
            if (fish.hasName(fishName)) return fish;
        }
        return null; 
    }

    public ArrayList<GroupOld> getGroups() {
        return groups;
    }
 
    
    
    
    
}
