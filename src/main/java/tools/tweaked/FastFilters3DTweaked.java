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


import ij.ImageStack;
import mcib3d.geom.Object3D;
import mcib3d.geom.Object3DVoxels;
import mcib3d.geom.Voxel3DComparable;
import mcib3d.image3d.*;
import mcib3d.utils.Chrono;
import mcib3d.utils.Logger.AbstractLog;
import mcib3d.utils.Logger.IJStatus;
import mcib3d.utils.ThreadUtil;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import mcib3d.geom.Point3D;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 * *
 * /**
 * Copyright (C) 2008- 2012 Thomas Boudier and others
 * <p>
 * <p>
 * <p>
 * This file is part of mcib3d
 * <p>
 * mcib3d is free software; you can redistribute it and/or modify it under the
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
 * @author thomas
 */
public class FastFilters3DTweaked {

    static public final int MEAN = 0;
    static public final int MEDIAN = 1;
    static public final int MIN = 2;
    static public final int MAX = 3;
    static public final int MAXLOCAL = 4;
    static public final int TOPHAT = 5;
    static public final int OPENGRAY = 6;
    static public final int CLOSEGRAY = 7;
    static public final int VARIANCE = 8;
    static public final int SOBEL = 9;
    static public final int ADAPTIVE = 10;

    public static ImageStack filterIntImageStack(ImageStack stackorig, int filter, float vx, float vy, float vz, int nbcpus, boolean showstatus) {
        return filterIntImage(ImageInt.wrap(stackorig), filter, vx, vy, vz, nbcpus, showstatus).getImageStack();
    }

    public static ArrayList<Voxel3DComparable> getListMaxima(ImageHandler stackorig, float vx, float vy, float vz, double minThreshold, int nbcpus, boolean showstatus) {
        if ((stackorig instanceof ImageByte) || (stackorig instanceof ImageShort)) {
            return getListMaximaInt((ImageInt) stackorig, vx, vy, vz,minThreshold, nbcpus, showstatus);
        } else if (stackorig instanceof ImageFloat) {
            System.out.print("Float image not implemented");
            throw new UnsupportedOperationException("Not supported yet. Float image not implemented in getListMaxima");
        } else {
            return null;
        }
    }

    public static ArrayList<Voxel3DComparable> getListMaximaInt(ImageInt stackorig, float vx, float vy, float vz,double minThreshold, int nbcpus, boolean showstatus) {
        int nbToProcess = stackorig.sizeZ;
        // Timer
        final Chrono time = new Chrono(nbToProcess);
        time.start();
        final AbstractLog show = new IJStatus();
        // get stack info
        final float voisx = vx;
        final float voisy = vy;
        final float voisz = vz;

        final ImageIntTweaked ima = new ImageIntTweaked(stackorig.getImagePlus());
        // PARALLEL 
        final AtomicInteger ai = new AtomicInteger(0);
        final int n_cpus = nbcpus == 0 ? ThreadUtil.getNbCpus() : nbcpus;
        final int dec = (int) Math.ceil((double) ima.sizeZ / (double) n_cpus);
        Thread[] threads = ThreadUtil.createThreadArray(n_cpus);
        final ArrayList<Voxel3DComparable>[] listes = new ArrayList[n_cpus];
        for (int ithread = 0; ithread < threads.length; ithread++) {
            threads[ithread] = new Thread() {
                @Override
                public void run() {
                    for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                        listes[k] = ima.getListMaxima(voisx, voisy, voisz, dec * k, dec * (k + 1), minThreshold, time, show);
                    }
                }
            };
        }
        ThreadUtil.startAndJoin(threads);

        final ArrayList<Voxel3DComparable> liste = new ArrayList<Voxel3DComparable>();
        for (ArrayList<Voxel3DComparable> li : listes) {
            liste.addAll(li);
        }

        return liste;
    }

    public static ArrayList<Voxel3DComparable> getListMaximaFloat(ImageFloat stackorig, float vx, float vy, float vz,double minThreshold, int nbcpus, boolean showstatus) {
        int nbToProcess = stackorig.sizeZ;
        // Timer
        final Chrono time = new Chrono(nbToProcess);
        time.start();
        final AbstractLog show = new IJStatus();
        // get stack info
        final float voisx = vx;
        final float voisy = vy;
        final float voisz = vz;

        final ImageFloat ima = stackorig;
        // PARALLEL 
        final AtomicInteger ai = new AtomicInteger(0);
        final int n_cpus = nbcpus == 0 ? ThreadUtil.getNbCpus() : nbcpus;
        final int dec = (int) Math.ceil((double) ima.sizeZ / (double) n_cpus);
        Thread[] threads = ThreadUtil.createThreadArray(n_cpus);
        final ArrayList<Voxel3DComparable>[] listes = new ArrayList[n_cpus];
        for (int ithread = 0; ithread < threads.length; ithread++) {
            threads[ithread] = new Thread() {
                @Override
                public void run() {
                    for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                        listes[k] = ima.getListMaxima(voisx, voisy, voisz, dec * k, dec * (k + 1), time, show);
                    }
                }
            };
        }
        ThreadUtil.startAndJoin(threads);

        final ArrayList<Voxel3DComparable> liste = new ArrayList<Voxel3DComparable>();
        for (ArrayList<Voxel3DComparable> li : listes) {
            liste.addAll(li);
        }

        return liste;
    }

    public static ImageInt filterIntImage(ImageInt stackorig, int filter, float vx, float vy, float vz, int nbcpus, boolean showstatus) {
        return filterIntImage(stackorig, filter, vx, vy, vz, nbcpus, showstatus, new IJStatus());
    }

    public static ImageInt filterIntImage(ImageInt stackorig, int filter, float vx, float vy, float vz, int nbcpus, boolean showstatus, AbstractLog log) {
        int nbToProcess = stackorig.sizeZ;
        if ((filter == TOPHAT) || (filter == CLOSEGRAY) || (filter == OPENGRAY)) nbToProcess *= 2;
        // Timer
        final Chrono time = new Chrono(nbToProcess);
        time.start();
        final AbstractLog show = log;

        // get stack info
        final float voisx = vx;
        final float voisy = vy;
        final float voisz = vz;

        final ImageInt ima = stackorig;
        ImageInt res = (ImageInt) ima.createSameDimensions();
        if ((filter == MEAN) || (filter == MEDIAN) || (filter == MIN) || (filter == MAX) || (filter == MAXLOCAL) || (filter == TOPHAT) || (filter == VARIANCE) || (filter == CLOSEGRAY) || (filter == OPENGRAY)) {
            // PARALLEL 
            final ImageInt out = res;
            final AtomicInteger ai = new AtomicInteger(0);
            final int n_cpus = nbcpus == 0 ? ThreadUtil.getNbCpus() : nbcpus;

            int fi = filter;
            if ((fi == TOPHAT) || (fi == OPENGRAY)) {
                fi = MIN;
            }
            if (fi == CLOSEGRAY) {
                fi = MAX;
            }
            final int f = fi;
            final int dec = (int) Math.ceil((double) ima.sizeZ / (double) n_cpus);
            Thread[] threads = ThreadUtil.createThreadArray(n_cpus);
            show.log("Starting");
            for (int ithread = 0; ithread < threads.length; ithread++) {
                threads[ithread] = new Thread() {
                    @Override
                    public void run() {
                        for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                            ima.filterGeneric(out, voisx, voisy, voisz, dec * k, dec * (k + 1), f, time, show);
                        }
                    }
                };
            }
            ThreadUtil.startAndJoin(threads);
            show.log("Finished");

            // TOPHAT MAX
            if ((filter == TOPHAT) || (filter == OPENGRAY)) {
                final int f2 = MAX;
                final ImageInt res2 = (ImageInt) ima.createSameDimensions();
                ai.set(0);
                for (int ithread = 0; ithread < threads.length; ithread++) {
                    threads[ithread] = new Thread() {
                        @Override
                        public void run() {
                            for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                                out.filterGeneric(res2, voisx, voisy, voisz, dec * k, dec * (k + 1), f2, time, show);
                            }
                        }
                    };
                }
                ThreadUtil.startAndJoin(threads);

                // TOPHAT DIFFERENCE
                if (filter == TOPHAT) {
                    res = ima.substractImage(res2);
                } else {
                    res = res2;
                }
            }
            // CLOSING 2nd Step
            if (filter == CLOSEGRAY) {
                final int f2 = MIN;
                final ImageInt res2 = (ImageInt) ima.createSameDimensions();
                ai.set(0);
                for (int ithread = 0; ithread < threads.length; ithread++) {
                    threads[ithread] = new Thread() {
                        @Override
                        public void run() {
                            for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                                out.filterGeneric(res2, voisx, voisy, voisz, dec * k, dec * (k + 1), f2, time, show);
                            }
                        }
                    };
                }
                ThreadUtil.startAndJoin(threads);
                res = res2;
            }
        } else if (filter == SOBEL) {
            res = ima.sobelFilter();
        } else if (filter == ADAPTIVE) {
            //res = ima.adaptiveFilter(voisx, voisy, voisz, nbcpus);
            // PARALLEL
            final ImageInt out = res;
            final AtomicInteger ai = new AtomicInteger(0);
            final int n_cpus = nbcpus == 0 ? ThreadUtil.getNbCpus() : nbcpus;
            final int dec = (int) Math.ceil((double) ima.sizeZ / (double) n_cpus);
            Thread[] threads = ThreadUtil.createThreadArray(n_cpus);
            show.log("Starting");
            for (int ithread = 0; ithread < threads.length; ithread++) {
                threads[ithread] = new Thread() {
                    @Override
                    public void run() {
                        for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                            ima.adaptiveFilter(out, voisx, voisy, voisz, dec * k, dec * (k + 1), time, show);
                        }
                    }
                };
            }
            ThreadUtil.startAndJoin(threads);
            show.log("Finished");
        }
        // copy info from input image
        res.setScale(stackorig);
        res.setOffset(stackorig);

        return res;
    }

    public static ImageStack filterImageStack(ImageStack stackorig, int filter, float vx, float vy, float vz, int nbcpus, boolean showstatus) {
        int bit = stackorig.getBitDepth();
        if ((bit == 8) || (bit == 16)) {
            return filterIntImageStack(stackorig, filter, vx, vy, vz, nbcpus, showstatus);
        } else if (bit == 32) {
            return filterFloatImageStack(stackorig, filter, vx, vy, vz, nbcpus, showstatus);
        } else {
            return null;
        }
    }


    public static ImageHandler filterImage(ImageHandler stackorig, int filter, float vx, float vy, float vz, int nbcpus, boolean showstatus) {
        if ((stackorig instanceof ImageByte) || (stackorig instanceof ImageShort)) {
            return filterIntImage((ImageInt) stackorig, filter, vx, vy, vz, nbcpus, showstatus);
        } else if (stackorig instanceof ImageFloat) {
            return filterFloatImage((ImageFloat) stackorig, filter, vx, vy, vz, nbcpus, showstatus);
        } else {
            return null;
        }
    }

    public static ImageFloat filterFloatImage(ImageFloat stackorig, int filter, float vx, float vy, float vz, int nbcpus, boolean showstatus) {
        int nbToProcess = stackorig.sizeZ;
        if ((filter == TOPHAT) || (filter == CLOSEGRAY) || (filter == OPENGRAY)) nbToProcess *= 2;
        // Timer
        final Chrono time = new Chrono(nbToProcess);
        time.start();
        final AbstractLog show = new IJStatus();
        // get stack info
        final float voisx = vx;
        final float voisy = vy;
        final float voisz = vz;

        final ImageFloat ima = stackorig;
        ImageFloat res = (ImageFloat) ima.createSameDimensions();
        if ((filter == MEAN) || (filter == MEDIAN) || (filter == MIN) || (filter == MAX) || (filter == MAXLOCAL) || (filter == TOPHAT) || (filter == VARIANCE) || (filter == CLOSEGRAY) || (filter == OPENGRAY)) {
            // PARALLEL 
            final ImageFloat out = res;
            final AtomicInteger ai = new AtomicInteger(0);
            final int n_cpus = nbcpus == 0 ? ThreadUtil.getNbCpus() : nbcpus;

            int fi = filter;
            if ((fi == TOPHAT) || (fi == OPENGRAY)) {
                fi = MIN;
            }
            if (fi == CLOSEGRAY) {
                fi = MAX;
            }
            final int f = fi;
            final int dec = (int) Math.ceil((double) ima.sizeZ / (double) n_cpus);
            Thread[] threads = ThreadUtil.createThreadArray(n_cpus);
            for (int ithread = 0; ithread < threads.length; ithread++) {
                threads[ithread] = new Thread() {
                    @Override
                    public void run() {
                        for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                            ima.filterGeneric(out, voisx, voisy, voisz, dec * k, dec * (k + 1), f, time, show);
                        }
                    }
                };
            }
            ThreadUtil.startAndJoin(threads);

            // TOPHAT MAX
            if ((filter == TOPHAT) || (filter == OPENGRAY)) {
                final int f2 = MAX;
                final ImageFloat res2 = (ImageFloat) ima.createSameDimensions();
                ai.set(0);
                for (int ithread = 0; ithread < threads.length; ithread++) {
                    threads[ithread] = new Thread() {
                        @Override
                        public void run() {
                            for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                                out.filterGeneric(res2, voisx, voisy, voisz, dec * k, dec * (k + 1), f2, time, show);
                            }
                        }
                    };
                }
                ThreadUtil.startAndJoin(threads);

                // TOPHAT DIFFERENCE
                if (filter == TOPHAT) {
                    res = ima.subtractImage(res2);
                } else {
                    res = res2;
                }
            }
            // CLOSING 2nd Step
            if (filter == CLOSEGRAY) {
                final int f2 = MIN;
                final ImageFloat res2 = (ImageFloat) ima.createSameDimensions();
                ai.set(0);
                for (int ithread = 0; ithread < threads.length; ithread++) {
                    threads[ithread] = new Thread() {
                        @Override
                        public void run() {
                            for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                                out.filterGeneric(res2, voisx, voisy, voisz, dec * k, dec * (k + 1), f2, time, show);
                            }
                        }
                    };
                }
                ThreadUtil.startAndJoin(threads);
                res = res2;
            }
        } else if (filter == SOBEL) {
            res = ima.sobelFilter();
        } else if (filter == ADAPTIVE) {
            //res = ima.adaptiveFilter(voisx, voisy, voisz, nbcpus);
            // PARALLEL
            final ImageFloat out = res;
            final AtomicInteger ai = new AtomicInteger(0);
            final int n_cpus = nbcpus == 0 ? ThreadUtil.getNbCpus() : nbcpus;
            final int dec = (int) Math.ceil((double) ima.sizeZ / (double) n_cpus);
            Thread[] threads = ThreadUtil.createThreadArray(n_cpus);
            show.log("Starting");
            for (int ithread = 0; ithread < threads.length; ithread++) {
                threads[ithread] = new Thread() {
                    @Override
                    public void run() {
                        for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                            ima.adaptiveFilter(out, voisx, voisy, voisz, dec * k, dec * (k + 1), time, show);
                        }
                    }
                };
            }
            ThreadUtil.startAndJoin(threads);
            show.log("Finished");
        }
        // copy info from input image
        res.setScale(stackorig);
        res.setOffset(stackorig);

        return res;
    }

    public static ImageStack filterFloatImageStack(ImageStack stackorig, int filter, float vx, float vy, float vz, int nbcpus, boolean showstatus) {
        return filterFloatImage((ImageFloat) ImageFloat.wrap(stackorig), filter, vx, vy, vz, nbcpus, showstatus).getImageStack();

    }

    /**
     * Create a kernel neighorhood as an ellipsoid
     *
     * @param radx Radius x of the ellipsoid
     * @param rady Radius y of the ellipsoid
     * @param radz Radius z of the ellipsoid
     * @return The kernel as an array
     */
    public static int[] createKernelEllipsoid(float radx, float rady, float radz) {
        int vx = (int) Math.ceil(radx);
        int vy = (int) Math.ceil(rady);
        int vz = (int) Math.ceil(radz);
        int[] ker = new int[(2 * vx + 1) * (2 * vy + 1) * (2 * vz + 1)];
        double dist;

        double rx2 = radx * radx;
        double ry2 = rady * rady;
        double rz2 = radz * radz;

        if (rx2 != 0) {
            rx2 = 1.0 / rx2;
        } else {
            rx2 = 0;
        }
        if (ry2 != 0) {
            ry2 = 1.0 / ry2;
        } else {
            ry2 = 0;
        }
        if (rz2 != 0) {
            rz2 = 1.0 / rz2;
        } else {
            rz2 = 0;
        }

        int idx = 0;
        for (int k = -vz; k <= vz; k++) {
            for (int j = -vy; j <= vy; j++) {
                for (int i = -vx; i <= vx; i++) {
                    dist = ((double) (i * i)) * rx2 + ((double) (j * j)) * ry2 + ((double) (k * k)) * rz2;
                    if (dist <= 1.0) {
                        ker[idx] = 1;
                    } else {
                        ker[idx] = 0;
                    }
                    idx++;
                }
            }
        }

        return ker;
    }

    /**
     * Create a kernel neighorhood as an ellipsoid
     *
     * @param stepx Radius x of the ellipsoid
     * @param stepy Radius y of the ellipsoid
     * @param stepz Radius z of the ellipsoid
     * @return The kernel as an array
     */
    public static int[] createKernelBrick(int stepx, int stepy, int stepz) {
        int vx = stepx;
        int vy = stepy;
        int vz = stepz;
        int[] ker = new int[vx*vy*vz];

        int idx = 0;
        for (int k = 0; k < vz; k++) {
            for (int j = 0; j < vy; j++) {
                for (int i = 0; i < vx; i++) {
                    ker[idx] = 1;
                    idx++;
                }
            }
        }

        return ker;
    }


    public static int[] createKernelFromObject(Object3D obj) {
        int[] bb = obj.getBoundingBox();
        ImageHandler seg = obj.getLabelImage();
        int vx = bb[1] - bb[0] + 1;
        int vy = bb[3] - bb[2] + 1;
        int vz = bb[5] - bb[4] + 1;
        int[] ker = new int[vx * vy * vz];
        int idx = 0;
        for (int k = bb[4]; k <= bb[5]; k++) {
            for (int j = bb[2]; j <= bb[3]; j++) {
                for (int i = bb[0]; i <= bb[1]; i++) {
                    if (seg.getPixel(i, j, k) > 0) {
                        ker[idx] = 1;
                    } else {
                        ker[idx] = 0;
                    }
                    idx++;
                }
            }
        }

        return ker;
    }

    public static int getNbFromKernel(int[] ker) {
        int nb = 0;
        for (int aKer : ker) {
            if (aKer > 0) {
                nb++;
            }
        }
        return nb;
    }

    public static float[] getRadiiFromObject(Object3D obj) {
        int[] bb = obj.getBoundingBox();
        float vx = (float) Math.ceil(0.5 * (bb[1] - bb[0]));
        float vy = (float) Math.ceil(0.5 * (bb[3] - bb[2]));
        float vz = (float) Math.ceil(0.5 * (bb[5] - bb[4]));

        return new float[]{vx, vy, vz};
    }

    public static ImageHandler filterImage(ImageHandler stackorig, int filter, Object3DVoxels obj, int nbcpus, boolean showstatus) {
        if ((stackorig instanceof ImageByte) || (stackorig instanceof ImageShort)) {
            return filterIntImage((ImageInt) stackorig, filter, obj, nbcpus, showstatus);
        } else if (stackorig instanceof ImageFloat) {
            return filterFloatImage((ImageFloat) stackorig, filter, obj, nbcpus, showstatus);
        } else {
            return null;
        }
    }

    public static ImageInt filterIntImage(ImageInt stackorig, int filter, Object3DVoxels obj, int nbcpus, boolean showstatus) {
        int nbToProcess = stackorig.sizeZ;
        if ((filter == TOPHAT) || (filter == CLOSEGRAY) || (filter == OPENGRAY)) nbToProcess *= 2;
        // Timer
        final Chrono time = new Chrono(nbToProcess);
        time.start();
        final AbstractLog show = new IJStatus();

        final Object3DVoxels object = obj;
        //IJ.log("Using java filtering " + voisx + " " + voisy + " " + voisz + " " + filter + " " + nbcpus);
        final ImageInt ima = stackorig;
        ImageInt res = (ImageInt) ima.createSameDimensions();
        if ((filter == MEAN) || (filter == MEDIAN) || (filter == MIN) || (filter == MAX) || (filter == MAXLOCAL) || (filter == TOPHAT) || (filter == VARIANCE) || (filter == CLOSEGRAY) || (filter == OPENGRAY)) {
            // PARALLEL 
            final ImageInt out = res;
            final AtomicInteger ai = new AtomicInteger(0);
            final int n_cpus = nbcpus == 0 ? ThreadUtil.getNbCpus() : nbcpus;

            int fi = filter;
            if ((fi == TOPHAT) || (fi == OPENGRAY)) {
                fi = MIN;
            }
            if (fi == CLOSEGRAY) {
                fi = MAX;
            }
            final int f = fi;
            final int dec = (int) Math.ceil((double) ima.sizeZ / (double) n_cpus);
            Thread[] threads = ThreadUtil.createThreadArray(n_cpus);
            for (int ithread = 0; ithread < threads.length; ithread++) {
                threads[ithread] = new Thread() {
                    @Override
                    public void run() {
                        for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                            ima.filterGeneric(out, object, dec * k, dec * (k + 1), f, time, show);
                        }
                    }
                };
            }
            ThreadUtil.startAndJoin(threads);

            // TOPHAT MAX
            if ((filter == TOPHAT) || (filter == OPENGRAY)) {
                final int f2 = MAX;
                final ImageInt res2 = (ImageInt) ima.createSameDimensions();
                ai.set(0);
                for (int ithread = 0; ithread < threads.length; ithread++) {
                    threads[ithread] = new Thread() {
                        @Override
                        public void run() {
                            for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                                out.filterGeneric(res2, object, dec * k, dec * (k + 1), f2, time, show);
                            }
                        }
                    };
                }
                ThreadUtil.startAndJoin(threads);

                // TOPHAT DIFFERENCE
                if (filter == TOPHAT) {
                    res = ima.substractImage(res2);
                } else {
                    res = res2;
                }
            }
            // CLOSING 2nd Step
            if (filter == CLOSEGRAY) {
                final int f2 = MIN;
                final ImageInt res2 = (ImageInt) ima.createSameDimensions();
                ai.set(0);
                for (int ithread = 0; ithread < threads.length; ithread++) {
                    threads[ithread] = new Thread() {
                        @Override
                        public void run() {
                            for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                                out.filterGeneric(res2, object, dec * k, dec * (k + 1), f2, time, show);
                            }
                        }
                    };
                }
                ThreadUtil.startAndJoin(threads);
                res = res2;
            }
        } else if (filter == SOBEL) {
            res = ima.sobelFilter();
        } else if (filter == ADAPTIVE) {
            //res = ima.adaptiveFilter(voisx, voisy, voisz, nbcpus);
        }
        // copy info from input image
        res.setScale(stackorig);
        res.setOffset(stackorig);

        return res;
    }

    public static ImageFloat filterFloatImage(ImageFloat stackorig, int filter, Object3DVoxels obj, int nbcpus, boolean showstatus) {
        int nbToProcess = stackorig.sizeZ;
        if ((filter == TOPHAT) || (filter == CLOSEGRAY) || (filter == OPENGRAY)) nbToProcess *= 2;
        // Timer
        final Chrono time = new Chrono(nbToProcess);
        time.start();
        final AbstractLog show = new IJStatus();
        final Object3DVoxels object = obj;
        //IJ.log("Using java filtering " + voisx + " " + voisy + " " + voisz + " " + filter + " " + nbcpus);
        final ImageFloat ima = stackorig;
        ImageFloat res = (ImageFloat) ima.createSameDimensions();
        if ((filter == MEAN) || (filter == MEDIAN) || (filter == MIN) || (filter == MAX) || (filter == MAXLOCAL) || (filter == TOPHAT) || (filter == VARIANCE) || (filter == CLOSEGRAY) || (filter == OPENGRAY)) {
            // PARALLEL 
            final ImageFloat out = res;
            final AtomicInteger ai = new AtomicInteger(0);
            final int n_cpus = nbcpus == 0 ? ThreadUtil.getNbCpus() : nbcpus;

            int fi = filter;
            if ((fi == TOPHAT) || (fi == OPENGRAY)) {
                fi = MIN;
            }
            if (fi == CLOSEGRAY) {
                fi = MAX;
            }
            final int f = fi;
            final int dec = (int) Math.ceil((double) ima.sizeZ / (double) n_cpus);
            Thread[] threads = ThreadUtil.createThreadArray(n_cpus);
            for (int ithread = 0; ithread < threads.length; ithread++) {
                threads[ithread] = new Thread() {
                    @Override
                    public void run() {
                        for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                            ima.filterGeneric(out, object, dec * k, dec * (k + 1), f, time, show);
                        }
                    }
                };
            }
            ThreadUtil.startAndJoin(threads);

            // TOPHAT MAX
            if ((filter == TOPHAT) || (filter == OPENGRAY)) {
                final int f2 = MAX;
                final ImageFloat res2 = (ImageFloat) ima.createSameDimensions();
                ai.set(0);
                for (int ithread = 0; ithread < threads.length; ithread++) {
                    threads[ithread] = new Thread() {
                        @Override
                        public void run() {
                            for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                                out.filterGeneric(res2, object, dec * k, dec * (k + 1), f2, time, show);
                            }
                        }
                    };
                }
                ThreadUtil.startAndJoin(threads);

                // TOPHAT DIFFERENCE
                if (filter == TOPHAT) {
                    res = ima.subtractImage(res2);
                } else {
                    res = res2;
                }
            }
            // CLOSING 2nd Step
            if (filter == CLOSEGRAY) {
                final int f2 = MIN;
                final ImageFloat res2 = (ImageFloat) ima.createSameDimensions();
                ai.set(0);
                for (int ithread = 0; ithread < threads.length; ithread++) {
                    threads[ithread] = new Thread() {
                        @Override
                        public void run() {
                            for (int k = ai.getAndIncrement(); k < n_cpus; k = ai.getAndIncrement()) {
                                out.filterGeneric(res2, object, dec * k, dec * (k + 1), f2, time, show);
                            }
                        }
                    };
                }
                ThreadUtil.startAndJoin(threads);
                res = res2;
            }
        } else if (filter == SOBEL) {
            res = ima.sobelFilter();
        } else if (filter == ADAPTIVE) {
            //res = ima.adaptiveFilter(voisx, voisy, voisz, nbcpus);
        }
        // copy info from input image
        res.setScale(stackorig);
        res.setOffset(stackorig);

        return res;
    }
}

