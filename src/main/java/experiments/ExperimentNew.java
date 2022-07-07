package experiments;


import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.io.FilenameUtils;
import tools.FileType;
import tools.InstantAdapter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author janlu
 */
@XmlRootElement(name = "Experiment")
public class ExperimentNew implements Serializable{
    private String name = "None";
    private File confocalDirectory;
    private ArrayList<FishGroupNew> groups = new ArrayList();
    private Instant timeOfFertilization;
    private Instant timeOfInjection;
    private int groupCount = 0;
    private int fishCount = 0;
    private int imageCount = 0;
    private FileType fileType;
    
    
/**
 * 
     * @param name
 * @param confocalDirectory
 * @param timeOfFertilization
 * @param timeOfInjection 
 */
    public ExperimentNew() {
        
    }
    public ExperimentNew(String name, File confocalDirectory,FileType fileType, Instant timeOfFertilization, Instant timeOfInjection, HashMap groupMap) {
        this.name = name;
        this.fileType = fileType;
        this.confocalDirectory = confocalDirectory;
        this.timeOfFertilization = timeOfFertilization;
        this.timeOfInjection = timeOfInjection;
        
        for (File file : confocalDirectory.listFiles()){
            boolean placedFile = false;
            if (FilenameUtils.isExtension(file.getName(), "ims")){
                String tempGroupName;
                String tempFishName = (String)groupMap.get(file.getName().substring(0, file.getName().indexOf("_")).toUpperCase());
                if (tempFishName == null) tempFishName = file.getName().substring(0, file.getName().indexOf("_")).toUpperCase();
                if (tempFishName.contains("(")) tempGroupName = tempFishName.substring(0, tempFishName.indexOf("("));
                else tempGroupName = "None";

                for (FishGroupNew checkGroup : groups) {
                    if (checkGroup.hasName(tempGroupName)) {
                        checkGroup.addFish(file, tempFishName);
                        placedFile = true;
                    }
                }
                if (!placedFile) {
                    groups.add(new FishGroupNew(tempGroupName, file, tempFishName, this));
                }
                imageCount++;
            }
        }
        for (FishGroupNew group : groups) {
            fishCount += group.getFishCount();
            groupCount = groups.size();
        }
    }
    
    
    @XmlAttribute(name = "Name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlTransient
    public File getConfocalDirectory() {
        return confocalDirectory;
    }
    public void setConfocalDirectory(File confocalDirectory) {
        this.confocalDirectory = confocalDirectory;
    }
    
    @XmlJavaTypeAdapter(InstantAdapter.class)
    @XmlAttribute(name = "Time_fertilization")
    public Instant getTimeOfFertilization() {
        return timeOfFertilization;
    }
    public void setTimeOfFertilization(Instant timeOfFertilization) {
        this.timeOfFertilization = timeOfFertilization;
    }
    
    @XmlJavaTypeAdapter(InstantAdapter.class)
    @XmlAttribute(name = "Time_transplant")
    public Instant getTimeOfInjection() {
        return timeOfInjection;
    }
    public void setTimeOfInjection(Instant timeOfInjection) {
        this.timeOfInjection = timeOfInjection;
    }
    
    @XmlAttribute(name = "Group_count")
    public int getGroupCount() {
        return groupCount;
    }
    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }
    
    @XmlAttribute(name = "Subject_count")
    public int getFishCount() {
        return fishCount;
    }
    public void setFishCount(int fishCount) {
        this.fishCount = fishCount;
    }
    
    @XmlAttribute(name = "Image_count")
    public int getImageCount() {
        return imageCount;
    }
    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }    
    
    @XmlElement(name = "Group")
    public ArrayList<FishGroupNew> getGroups() {
        return groups;
    }
    public void setGroups(ArrayList<FishGroupNew> groups) {
        this.groups = groups;
    }

    public String[] getGroupNames() {
        String[] groupNameArray = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            groupNameArray[i] = groups.get(i).getGroupName();
        }
        return groupNameArray;
    }
    public FishGroupNew getGroup(String groupName) {
        for (FishGroupNew group : groups) {
            if (group.hasName(groupName)) return group;
        }
        return null;        
    }

    public void updateParent() {
        for (FishGroupNew group : groups) {
            group.updateParents(this);
        }
    }

    public void updateFile(File experimentPath) {
        this.confocalDirectory = experimentPath;
        for (FishGroupNew group : groups) {
            group.updateFile(experimentPath);
        }
    }

    @XmlAttribute(name = "File_Type")
    public FileType getFileType() {
        return fileType;
    }
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }



 
    
    
    
    
}
