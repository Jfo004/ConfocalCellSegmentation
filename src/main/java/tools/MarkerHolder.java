/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import org.jfree.chart.plot.IntervalMarker;

/**
 *
 * @author janlu
 */
public class MarkerHolder {
    private IntervalMarker marker; 
    private String label;
    
    public MarkerHolder(String label, IntervalMarker marker){
        this.label = label;
        this.marker = marker;
    }
    
    @Override
    public String toString() {
        return label;
    }
    public IntervalMarker getMarker() {
        return marker;
    }
}
