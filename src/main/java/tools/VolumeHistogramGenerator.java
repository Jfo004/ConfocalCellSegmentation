/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import Containers.Cells.Cell;
import Containers.Cells.CellHolder;
import Containers.Cells.CellDay;
import Containers.Cells.CellGroup;
import GUI.CellAnalysisGUI;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;

/**
 *
 * @author janlu
 */
public class VolumeHistogramGenerator implements Runnable {
    CellAnalysisGUI parent;
    XYIntervalSeriesCollection dataset;
    private int nBins = 100;
    private double cutoffMin = 0;
    private double cutoffMax = Double.MAX_VALUE;
    private double histogramMin = 0;
    private double histogramMax = 1000;
    private boolean isLog = false;
    private boolean isCumulative;
    private ArrayList<Object> seriesList;
    private ArrayList<ArrayList<Double>> dataList = new ArrayList();
    private ArrayList<String> nameList = new ArrayList();
    double[] binStart;
    double[] binEnd;
    
    
    public VolumeHistogramGenerator(ArrayList<Object> seriesList, CellAnalysisGUI parent) {
        this.seriesList = seriesList;
        this.parent = parent;
    }
    
    @Override
    public synchronized void run() {
        generateDataList();
        generateBins();
        if(isCumulative) generateCumulativeDataset();
        else generateIndivitualDataset();
        
        sendDataset();
    }

    private void generateDataList() {
        dataList.clear();
        nameList.clear();
        for (Object object : seriesList) {
            if (object instanceof CellHolder) {
                dataList.add(getCellHolderData((CellHolder) object));
                nameList.add(((CellHolder) object).toString());
            }
            else if (object instanceof CellGroup) {
                dataList.add(getCellGroupData((CellGroup) object));
                nameList.add(((CellGroup) object).toString());
            }
            else if (object instanceof CellSubject) {
                dataList.add(getCellFishData((CellSubject) object));
                nameList.add(((CellSubject) object).toString());
            }
            else if (object instanceof CellDay) {
                dataList.add(getCellDayData((CellDay) object));
                nameList.add(((CellDay) object).toString());
            }
        }
    }
    
    private void generateIndivitualDataset() {
        dataset = new XYIntervalSeriesCollection();
        for(int i = 0; i < nameList.size(); i++) {
            XYIntervalSeries series = new XYIntervalSeries(nameList.get(i));
            Histogram histogram = new Histogram(cutoffMin, cutoffMax, histogramMin, histogramMax, nBins, isLog, dataList.get(i));
            for (int j = 0; j < nBins; j++) {
                series.add(histogram.getBinCenter(j), histogram.getBinStart(j), histogram.getBinEnd(j), histogram.getBinValue(j), histogram.getBinValue(j), histogram.getBinValue(j));
            }
            
            dataset.addSeries(series);
        }
    }

    private void sendDataset() {
        SwingUtilities.invokeLater(() -> {
            parent.setHistogramDataset(dataset);
        });
    }
    
    public synchronized void setCutoff(double min, double max) {
        cutoffMax = max;
        cutoffMin = min;
    }
    public synchronized void setHisogramRange(double min, double max) {
        histogramMax = max;
        histogramMin = min;
    }
    public synchronized void setNumberOfBins(int bins) {
        nBins = bins;
    }
    public synchronized void setSeries(ArrayList<Object> seriesList) {
        this.seriesList = new ArrayList();
        for (Object object : seriesList) {
            if (object instanceof CellHolder || object instanceof CellGroup || object instanceof CellSubject || object instanceof CellDay) this.seriesList.add(object);
            else System.out.println("Histogram generator dropped object");
        }
    } 

    private ArrayList<Double> getCellDayData(CellDay cellDay) {
        ArrayList<Cell> cellList = cellDay.getCellList();
        ArrayList<Double> volumeList = new ArrayList();
        for (Cell cell : cellList) {
            double volume = cell.getVolumeUnit();
            if (volume > cutoffMin && volume < cutoffMax) volumeList.add(volume);
        }
        return volumeList;
    }
    private ArrayList<Double> getCellFishData(CellSubject CellFish) {
        ArrayList<Double> volumeList = new ArrayList();
        for (CellDay cellDay : CellFish.getDayList()) {
            volumeList.addAll(getCellDayData(cellDay));
        }
        return volumeList;
    }
    private ArrayList<Double> getCellGroupData(CellGroup CellGroup) {
        ArrayList<Double> volumeList = new ArrayList();
        for (CellSubject cellFish : CellGroup.getFishList()) {
            volumeList.addAll(getCellFishData(cellFish));
        }
        return volumeList;
    }
    private ArrayList<Double> getCellHolderData(CellHolder CellHolder) {
        ArrayList<Double> volumeList = new ArrayList();
        for (CellGroup cellGroup : CellHolder.getGroupList()) {
            volumeList.addAll(getCellGroupData(cellGroup));
        }
        return volumeList;
    }    

    public void setLog(boolean isLog) {
        this.isLog = isLog;
    }
    
    public void setCumulative(boolean isCumulative) {
        this.isCumulative = isCumulative;
    }

    private void generateCumulativeDataset() {
        dataset = new XYIntervalSeriesCollection();
        ArrayList<Double> cumulativeData = new ArrayList();
        for(int i = 0; i < nameList.size(); i++) {
            cumulativeData.addAll(dataList.get(i));
        }
        XYIntervalSeries series = new XYIntervalSeries("Cumulative of " + nameList.size() + " measurements");
        Histogram histogram = new Histogram(cutoffMin, cutoffMax, histogramMin, histogramMax, nBins, isLog, cumulativeData);
        for (int j = 0; j < nBins; j++) {
            series.add(histogram.getBinCenter(j), histogram.getBinStart(j), histogram.getBinEnd(j), histogram.getBinValue(j), histogram.getBinValue(j), histogram.getBinValue(j));
        }
        dataset.addSeries(series);
    }

    private void generateBins() {
        binStart = new double[nBins];
        binEnd = new double[nBins];
        
        
        for (int i = 0; i < nBins; i++) {
            if(isLog) {
                
            }
            else {
                double range = histogramMax - histogramMin;
                
            }
        }
    }
    
}
