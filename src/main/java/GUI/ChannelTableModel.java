/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import java.awt.Color;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Jan-Lukas Foerde
 */
public class ChannelTableModel extends AbstractTableModel {

    private String[] columnNames;
    private Object[][] data;
    
    public void setData(Object[][] data) {this.data = data;}
    public void setColNames(String[] colNames){this.columnNames = colNames;}
       
    
    @Override
    public int getRowCount() {return data.length;}

    @Override
    public int getColumnCount() {return columnNames.length;}

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {return data[rowIndex][columnIndex];}
    
    @Override
    public Class getColumnClass(int column) {
        switch(column) {
            case 0:
                return Integer.class;
            case 1:
                return Boolean.class;
            case 2:
                return Boolean.class;
            case 3:
                return Boolean.class;
            case 4:
                return Color.class;
            default:
                return Object.class;
        }}
    
    @Override
    public String getColumnName(int column) {return columnNames[column];}
    
    @Override
    public boolean isCellEditable(int row, int column) {return column > 1;}
    
    @Override
    public void setValueAt(Object value, int row, int column) {
        //Only one channel can be used for cell segmentation and bright field
        if (column == 1 || column == 2) {
            for (int i = 0; i < data.length; i ++) {
                data[i][column] = false;
            }
            data[row][column] = true;
        }
        else {
        data[row][column] = value;
        }
        fireTableDataChanged();
    }
    
}
