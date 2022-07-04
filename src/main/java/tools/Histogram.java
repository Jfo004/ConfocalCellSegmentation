/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;


import java.util.ArrayList;


/**
 *
 * @author janlu
 */
public class Histogram {
    private double cutoffMin;
    private double cutoffMax;
    private double binMin;
    private double binMax;
    private int nBins;
    private boolean useLog;
    private ArrayList<Double> inData;
    
    private double[] histogramData;
    private double[] binStart;
    private double[] binEnd;
    private double[] binCenter;
    
    public Histogram(double cutoffMin, double cutoffMax, double binMin, double binMax, int nBins, boolean useLog, ArrayList<Double> inData) {
        this.cutoffMin = cutoffMin;
        this.cutoffMax = cutoffMax;
        this.binMin = binMin;
        this.binMax = binMax;
        this.nBins = nBins;
        this.useLog = useLog;
        this.inData = inData;
        createHistogram();
    }

    private void createHistogram() {
        createBins();
        populateBins();
    }

    private void createBins() {
        double range;
        binStart = new double[nBins];
        binEnd = new double[nBins];
        binCenter = new double[nBins];
        
        if (useLog) {
            range = Math.log10(binMax) - Math.log10(binMin);
            if (binMin <= 0) binMin = Double.MIN_VALUE;
        }
        else {
            range = binMax-binMin;
        }
        double binSize = range/nBins;
        
        if (useLog) {
            for (int i = 0; i < nBins; i++) {
                binStart[i] = Math.pow(10, Math.log10(binMin) + (i*binSize));
                binEnd[i] = Math.pow(10, Math.log10(binMin) + ((i+1)*binSize));
                binCenter[i] = Math.pow(10, (Math.log10(binStart[i])+ Math.log10(binEnd[i]))/2);
                System.out.println("Bin Start " + binStart[i] + "Bin End " + binEnd[i] + "Bin Center " + binCenter[i]);
            }
        }
        else {
            for (int i = 0; i < nBins; i++) {
                binStart[i] = binMin + i*binSize;
                binEnd[i] = (binMin + (i+1)*binSize);
                binCenter[i] = (binStart[i] + binEnd[i])/2;
            }
        }
        
    }

    private void populateBins() {
        histogramData = new double[nBins];
        for (Double dataPoint : inData) {
            if (dataPoint < cutoffMin || dataPoint > cutoffMax) continue;
            if ( dataPoint < binMin) {
                histogramData[0] = histogramData[0] + 1;
                continue;
            }
            else if (dataPoint >= binMax) {
                histogramData[nBins-1] = histogramData[nBins-1] + 1;
                continue;
            }
            
            for (int i = 0; i < nBins; i++) {
                if (dataPoint >= binStart[i] && dataPoint < binEnd[i]) {
                    histogramData[i] = histogramData[i] + 1;
                    continue;
                }    
            }
        }
    }
    
    public double getBinValue(int bin) {
        if (bin >= nBins) bin = nBins-1;
        if (bin < 0) bin = 0;
        return histogramData[bin];
    }
    public double getBinStart(int bin) {
        if (bin >= nBins) bin = nBins-1;
        if (bin < 0) bin = 0;
        return binStart[bin];
    }
    public double getBinEnd(int bin) {
        if (bin >= nBins) bin = nBins-1;
        if (bin < 0) bin = 0;
        return binEnd[bin];
    }
    public double getBinCenter(int bin) {
        if (bin >= nBins) bin = nBins-1;
        if (bin < 0) bin = 0;
        return binCenter[bin];
    }
    
    
}
