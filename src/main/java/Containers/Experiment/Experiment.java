package Containers.Experiment;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import importexport.FileType;
import importexport.ImageImporter;
import importexport.ImageImporterFactory;

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
public class Experiment implements Serializable{
    private String name = "None";
    private File confocalDirectory;
    private ArrayList<ExperimentGroup> groups = new ArrayList();
    private int groupCount = 0;
    private int fishCount = 0;
    private int imageCount = 0;
    private FileType fileType;
    private int bfChannel;
    private int segmentationChannel;
    private String[] channelColors;
    private Boolean[] analysisChannels;
    
    
/**
 * 
     * @param name
 * @param confocalDirectory
 * @param timeOfFertilization
 * @param timeOfInjection 
 */
    public Experiment() {
        
    }
//    public Experiment(String name, File confocalDirectory,FileType fileType, HashMap groupMap) {
//        this.name = name;
//        this.fileType = fileType;
//        this.confocalDirectory = confocalDirectory;
//        
//        for (File file : confocalDirectory.listFiles()){
//            boolean placedFile = false;
//            if (FilenameUtils.isExtension(file.getName(), "ims")){
//                String tempGroupName;
//                String tempFishName = (String)groupMap.get(file.getName().substring(0, file.getName().indexOf("_")).toUpperCase());
//                if (tempFishName == null) tempFishName = file.getName().substring(0, file.getName().indexOf("_")).toUpperCase();
//                if (tempFishName.contains("(")) tempGroupName = tempFishName.substring(0, tempFishName.indexOf("("));
//                else tempGroupName = "None";
//
//                for (ExperimentGroup checkGroup : groups) {
//                    if (checkGroup.hasName(tempGroupName)) {
//                        checkGroup.addFish(file, tempFishName);
//                        placedFile = true;
//                    }
//                }
//                if (!placedFile) {
//                    groups.add(new ExperimentGroup(tempGroupName, file, tempFishName, this));
//                }
//                imageCount++;
//            }
//        }
//        for (ExperimentGroup group : groups) {
//            fishCount += group.getFishCount();
//            groupCount = groups.size();
//        }
//    }

    public Experiment(String name, File confocalDirectory, FileType fileType, HashMap groupMap, int bfChannel, int segmentationChannel,Boolean[] analysisChannels, String[] channelColors) {
        this.name = name;
        this.fileType = fileType;
        this.confocalDirectory = confocalDirectory;
        this.bfChannel = bfChannel;
        this.segmentationChannel = segmentationChannel;
        this.analysisChannels = analysisChannels;
        this.channelColors = channelColors;
        
        for (File file : confocalDirectory.listFiles()){
            boolean placedFile = false;
            ImageImporter tester = ImageImporterFactory.createImporter(fileType);
            
            if (tester.isValidFile(file)){
                String tempGroupName;
                String tempFishName = (String)groupMap.get(file.getName().substring(0, file.getName().indexOf("_")).toUpperCase());
                if (tempFishName == null) tempFishName = file.getName().substring(0, file.getName().indexOf("_")).toUpperCase();
                if (tempFishName.contains("(")) tempGroupName = tempFishName.substring(0, tempFishName.indexOf("("));
                else tempGroupName = "None";

                for (ExperimentGroup checkGroup : groups) {
                    if (checkGroup.hasName(tempGroupName)) {
                        checkGroup.addFish(file, tempFishName);
                        placedFile = true;
                    }
                }
                if (!placedFile) {
                    groups.add(new ExperimentGroup(tempGroupName, file, tempFishName, this));
                }
                imageCount++;
            }
        }
        for (ExperimentGroup group : groups) {
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

    @XmlAttribute(name = "BF_channel")
    public int getBfChannel() {
        return bfChannel;
    }
    public void setBfChannel(int bfChannel) {
        this.bfChannel = bfChannel;
    }

    @XmlAttribute(name = "Segmentation_channel")
    public int getSegmentationChannel() {
        return segmentationChannel;
    }
    public void setSegmentationChannel(int segmentationChannel) {
        this.segmentationChannel = segmentationChannel;
    }

    @XmlElement(name = "Channel_color")
    public String[] getChannelColors() {
        return channelColors;
    }
    public void setChannelColors(String[] channelColors) {
        this.channelColors = channelColors;
    }

    @XmlElement(name = "Analysis_channel")
    public Boolean[] getAnalysisChannels() {
        return analysisChannels;
    }

    public void setAnalysisChannels(Boolean[] analysisChannels) {
        this.analysisChannels = analysisChannels;
    }
    
    
    
    @XmlTransient
    public File getConfocalDirectory() {
        return confocalDirectory;
    }
    public void setConfocalDirectory(File confocalDirectory) {
        this.confocalDirectory = confocalDirectory;
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
    public ArrayList<ExperimentGroup> getGroups() {
        return groups;
    }
    public void setGroups(ArrayList<ExperimentGroup> groups) {
        this.groups = groups;
    }

    public String[] getGroupNames() {
        String[] groupNameArray = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            groupNameArray[i] = groups.get(i).getGroupName();
        }
        return groupNameArray;
    }
    public ExperimentGroup getGroup(String groupName) {
        for (ExperimentGroup group : groups) {
            if (group.hasName(groupName)) return group;
        }
        return null;        
    }

    public void updateParent() {
        for (ExperimentGroup group : groups) {
            group.updateParents(this);
        }
    }

    public void updateFile(File experimentPath) {
        this.confocalDirectory = experimentPath;
        for (ExperimentGroup group : groups) {
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

    public void createRelativeFilePath() {
        for (ExperimentGroup group : groups) {
            group.createRelativeFilePath();
        }
    }



 
    
    
    
    
}
