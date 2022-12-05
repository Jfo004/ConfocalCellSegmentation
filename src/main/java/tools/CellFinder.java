
package tools;

import GUI.MainMenuGUI;
import Containers.Constants;
import Containers.Old.ExperimentOld;
import Containers.Experiment.Experiment;
import Containers.Old.SubjectOld;
import Containers.Old.GroupOld;
import Containers.Experiment.ExperimentGroup;
import Containers.Experiment.Subject;
import Containers.Old.ImageAnalysisOls;
import Containers.Experiment.ImageAnalysis;
import Containers.Old.MeasurementOld;
import Containers.Experiment.Measurement;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.io.RoiEncoder;
import ij.plugin.frame.RoiManager;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import javax.swing.SwingUtilities;


/**
 *
 * @author Jan-Lukas Førde
 */
public class CellFinder implements Runnable{
    Experiment experiment;
    ArrayList<ImageAnalysis> analysisList = new ArrayList();
    MainMenuGUI parent;
    ArrayList<File> fileList = new ArrayList();
    public CellFinder(Experiment experiment, MainMenuGUI parent) {
        this.experiment = experiment;
        this.parent = parent;
    }
    
    public void run() {
        sendStartMessage();
        createAnalysisList();
        performCounting();
        sendStopMessage("Cell counting");
    }

    private void createAnalysisList() {
        for(ExperimentGroup group : experiment.getGroups()) {
            for (Subject fish : group.getSubjectList()){
                for (Measurement measurement : fish.getMeasurements()) {
                    ImageAnalysis tempAnalysis = null;
                    boolean hasCounted = false;
                    boolean hasCropped = false;
                    for (ImageAnalysis analysis : measurement.getAnalysisList()) {
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CROPPED)) {
                            hasCropped = true;
                            tempAnalysis = analysis;
                        }
                        if (analysis.isAnalysisType(Constants.ANALYSIS_CELLSCOUNTED)) hasCounted = true;
                    }
                    if(!hasCounted && hasCropped) analysisList.add(tempAnalysis);
                }
            }
        }
    }

    private void performCounting() {
        RoiManager rm = new RoiManager();
        int cellCount;
        for (int i = 0; i < analysisList.size(); i++) {
            rm.reset();
            Roi selection;
            ImagePlus image = IJ.openImage(analysisList.get(i).getAnalysisFiles()[analysisList.get(i).getAnalysisFiles().length -1 ].getAbsolutePath());
            image.show();
            WaitForUserDialog dialog = new WaitForUserDialog("Select Cells");
            dialog.show();
            image.hide();
            selection = image.getRoi();
            if (selection == null) {
                IJ.showMessage("No selection");
                i--;
                continue;
            }
            String saveString = analysisList.get(i).getParent().getConfocalFile().getParent() 
                    + "\\ProcessedFiles\\Counted"
                    + "\\" + analysisList.get(i).getParent().getParent().getParentFishGroup().getGroupName() 
                    + "\\" + analysisList.get(i).getParent().getParent().getName()
                    + "\\" + analysisList.get(i).getParent().getFileName() 
                    + "\\" + "Counted_" + image.getTitle();
            File makeDir = new File(saveString);
            makeDir = makeDir.getParentFile();
            makeDir.mkdirs();
            IJ.save(image, saveString);
            image.changes = false;
            image.close();
            ImageAnalysis countedAnalysis = new ImageAnalysis(Constants.ANALYSIS_CELLSCOUNTED, new File[]{new File(saveString)}, new int[] {-1}, Instant.now(), analysisList.get(i).getParent());
            countedAnalysis.setIntStorage(new int[] {selection.getPolygon().npoints});
            saveRoi(countedAnalysis,analysisList.get(i).getRois()[0], selection);
            analysisList.get(i).getParent().addAnalysis(countedAnalysis);
            System.out.println("Counted: " + selection.getPolygon().npoints + " Cells in: " + countedAnalysis.getAnalysisName());
            sendSaveMessage();
            if (Thread.interrupted()) {
                sendStopMessage("Counting - aborted");
                return;
            }
        }
    }
    
    private void sendStopMessage(String message) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.finishTask(message);
            }
        });
    }
    private void sendSaveMessage() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.saveExperiment();
            }
        });
    }
    private void sendStartMessage() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                parent.setTask("Cell Counting", Constants.TASK_CELLCOUNTING);
            }
        });
        
    }
    private void saveRoi(ImageAnalysis analysis, File fishSelection, Roi cellSelection) {
        File roiPath = new File(analysis.getParent().getConfocalFile().getParent() + "\\ROIs\\" + analysis.getAnalysisName() + "_CellROI.roi");
        roiPath.getParentFile().mkdirs();
        if (!RoiEncoder.save(cellSelection, roiPath.getAbsolutePath())) {
            System.out.println("First ROI write failed for: " + analysis.getAnalysisName());
            if (!RoiEncoder.save(cellSelection, roiPath.getAbsolutePath())) {
                System.out.println("Second ROI write failed for: " + analysis.getAnalysisName());
                return;
            }
        };
        analysis.setRois(new File[] {fishSelection, roiPath});
    }
    
}
