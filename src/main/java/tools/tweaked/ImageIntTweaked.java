/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.tweaked;

/**
 *
 * @author janlu
 */



import ij.ImagePlus;
import ij.ImageStack;
import mcib3d.geom.*;


import mcib3d.image3d.processing.FastFilters3D;

import mcib3d.utils.ArrayUtil;
import mcib3d.utils.Chrono;
import mcib3d.utils.Logger.AbstractLog;


import java.util.ArrayList;
import java.util.Arrays;

import java.util.TreeMap;

import mcib3d.image3d.ImageByte;
import mcib3d.image3d.ImageFloat;

import mcib3d.image3d.ImageHandler;
import mcib3d.image3d.ImageInt;


/**
 * *
 * /**
 * Copyright (C) 2012 Jean Ollion
 * <p>
 * <p>
 * <p>
 * This file is part of tango
 * <p>
 * tango is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Jean Ollion
 * @author Thomas Boudier
 */
public class ImageIntTweaked extends ImageHandler {

    public ImageIntTweaked(ImagePlus img) {
        super(img);
    }

    public ImageIntTweaked(ImageStack img) {
        super(img);
    }

    public ImageIntTweaked(String title, int sizeX, int sizeY, int sizeZ) {
        super(title, sizeX, sizeY, sizeZ);
    }

    public ImageIntTweaked(ImageHandler im, boolean scaling) {
        super(im.getTitle(), im.sizeX, im.sizeY, im.sizeZ, im.offsetX, im.offsetY, im.offsetZ);
    }

    protected ImageIntTweaked(String title, int sizeX, int sizeY, int sizeZ, int offsetX, int offsetY, int offsetZ) {
        super(title, sizeX, sizeY, sizeZ, offsetX, offsetY, offsetZ);
    }

    public ArrayList<Voxel3DComparable> getListMaxima(float radx, float rady, float radz, int zmin, int zmax, double minThreshold) {
        return getListMaxima(radx, rady, radz, zmin, zmax, minThreshold, null, null);
    }

    public ArrayList<Voxel3DComparable> getListMaxima(float radx, float rady, float radz, int zmin,
                                                      int zmax, double minThreshold, Chrono timer, AbstractLog log) {
        ArrayList<Voxel3DComparable> res = new ArrayList<Voxel3DComparable>();
        int[] ker = FastFilters3D.createKernelEllipsoid(radx, rady, radz);
        int nb = FastFilters3D.getNbFromKernel(ker);
        if (zmin < 0) {
            zmin = 0;
        }
        if (zmax > this.sizeZ) {
            zmax = this.sizeZ;
        }
        int value;
        ArrayUtil tab;
        for (int k = zmin; k < zmax; k++) {
            for (int j = 0; j < sizeY; j++) {
                for (int i = 0; i < sizeX; i++) {
                    value = (int)this.getPixel(i, j, k);
                    if (value < minThreshold) continue;
                    tab = this.getNeighborhoodKernel(ker, nb, i, j, k, radx, rady, radz);
                    
                    if (tab.isMaximum(value)) {
                        res.add(new Voxel3DComparable(i, j, k, value, 1));
                    }
                }
            }
            if (timer != null) {
                String ti = timer.getFullInfo(1);
                if (ti != null) log.log("3D maxima : " + ti);
            }
        }

        return res;
    }

    @Override
    public double getSizeInMb() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getPixel(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getPixel(int i, int i1, int i2) {
        ImagePlus img = this.getImagePlus();
        ImageStack stack = img.getStack();
        return (float)stack.getVoxel(i, i1, i2);  
    }

    @Override
    public float getPixel(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getPixel(Point3D pd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getPixelInterpolated(Point3D pd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(Object3D od, float f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPixel(int i, float f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPixel(Point3D pd, float f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPixel(int i, int i1, int i2, float f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPixel(int i, int i1, float f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getArray1D() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getArray1D(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageHandler deleteSlices(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void trimSlices(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void erase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fill(double d, int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isOpened() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void getMinAndMax(ImageInt ii) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int[] getHisto(ImageInt ii) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int[] getHisto(ImageInt ii, int i, double d, double d1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void flushPixels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageByte thresholdRangeInclusive(float f, float f1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageByte thresholdRangeExclusive(float f, float f1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageByte threshold(float f, boolean bln, boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void thresholdCut(float f, boolean bln, boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageHandler cropRadius(int i, int i1, int i2, int i3, int i4, int i5, boolean bln, boolean bln1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageHandler crop3D(String string, int i, int i1, int i2, int i3, int i4, int i5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public ImageHandler[] crop3D(TreeMap<Float,int[]> sdf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



    @Override
    public ImageHandler resize(int i, int i1, int i2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageHandler resample(int i, int i1, int i2, int i3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageHandler resample(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ImageFloat normalize_(ImageInt ii, double d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageFloat normalize(double d, double d1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void invert(ImageInt ii) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void intersectMask(ImageInt ii) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void intersectMask2D(ImageInt ii, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getPixel(Point3DInt pdi) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImageHandler crop3DMask(String string, ImageInt ii, float f, int i, int i1, int i2, int i3, int i4, int i5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void intersectMask(ImageHandler ih) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

