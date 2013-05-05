/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2009 Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
<license>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
</license>
 *
 */
package net.sf.jfilesync.sync2;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerListener;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileDataSorter;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.engine.worker.DataCollectWorker;
import net.sf.jfilesync.engine.worker.events.CollectStatusMessage;
import net.sf.jfilesync.engine.worker.events.TFileListEvent;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;
import net.sf.jfilesync.sync2.event.SyncTreeEvent;
import net.sf.jfilesync.sync2.list.ExpressionList;

public class SyncTreeBuilder3 extends GWorker implements GWorkerListener {

    private static final Logger LOGGER = Logger
            .getLogger(SyncTreeBuilder3.class.getName());

    // observes sub-workers
    private final GWorkerObserver observer = new GWorkerObserver();

    private SyncTree tree;
    private final AbstractConnectionProxy con;
    private final String path;
    
    private volatile int numFiles;
    private boolean opt_follow_symlinks = false;
    private boolean opt_fat_mode = false;
    private ExpressionList includeList;
    private ExpressionList excludeList;

    private final Queue<SyncFile> dirsToGo = new LinkedList<SyncFile>();
    private final Map<GWorker, SyncFile> worker2dir = new HashMap<GWorker, SyncFile>();
    
    private final static int MAX_WORKERS = 5;
    
    public SyncTreeBuilder3(AbstractConnectionProxy con, String path)
            throws IOException {
        this.con = con;
        this.path = path;
        init();
    }

    private void loadSettings() {
        try {
            opt_follow_symlinks = MainWin.config.getProgramSettings()
                    .getBooleanOption(
                            TProgramSettings.OPTION_FOLLOW_SYMLINKS_SYNC);
        } catch (SettingsTypeException ste) {
            LOGGER.severe(ste.getMessage());
        }
    }

    public void setIncludeExpressionList(ExpressionList list) {
        includeList = list;
    }

    public void setExcludeExpressionList(ExpressionList list) {
        excludeList = list;
    }

    public void setFatModeEnabled(boolean enabled) {
        opt_fat_mode = enabled;
    }

    private void init() throws IOException {
        loadSettings();

        SyncFile rootHandle = null;

        con.chdir(path);

        if (con.getPathControlInstance().isRoot(path)) {
            TFileProperties dummy = new TFileProperties();
            dummy.setFileName(path);
            dummy.setAbsoluteFileName(path);
            dummy.setDirectoryFlag(true);
            rootHandle = new SyncFile(dummy, "");
        } else {
            con.chdir("..");
            String topPath = con.getCurrentPath();
            TFileProperties[] fprops = con.ls(topPath).getFilePropertiesArray();
            for (int i = 0; i < fprops.length; i++) {
                if (fprops[i].getAbsoluteFileName().equals(path)) {
                    rootHandle = new SyncFile(fprops[i], "");
                    LOGGER.info("root handle found : "
                            + fprops[i].getAbsoluteFileName());
                    break;
                }
            }
        }

        if (rootHandle != null) {
            tree = new SyncTree(rootHandle, path, con.getPathControlInstance());
        } else {
            throw new IOException("Could not find sync root dir " + path);
        }

    }

    public void task() throws Exception {

        synchronized (observer) {

            SyncFile cDir = null;

            numFiles = 0;
            observer.addWorkerListener(this);

            dirsToGo.add(tree.getRoot());
            
            boolean done = false;

            while (! done) {

                LOGGER.fine("empty: " + dirsToGo.isEmpty() + " #workers:"
                        + observer.getNumberOfCurrentWorkers()
                        + " cancelled: " + isCancelled());

                if( isCancelled() ) {
                    done = true;
                    
                    observer.cancelWorkers();
                    
                } else if( dirsToGo.isEmpty() ) {
                    if( observer.getNumberOfCurrentWorkers() == 0 ) {
                        done = true;
                    } else {
                        LOGGER.fine("WAIT");
                        observer.wait();
                        LOGGER.fine("WAKE UP");
                    }
                } else {
                    
                    if( observer.getNumberOfCurrentWorkers() < MAX_WORKERS ) {
                        
                        LOGGER.fine("remove");
                        cDir = dirsToGo.remove();
        
                        getObserver().updateGUI(
                                new CollectStatusMessage(this, cDir.getFile()
                                        .getAbsoluteFileName(), numFiles));
        
                        DataCollectWorker worker = new DataCollectWorker(con, cDir
                                .getFile().getAbsoluteFileName(), false,
                                opt_follow_symlinks);
    
                        worker.addObserver(observer);
    
                        worker2dir.put(worker, cDir);
    
                        LOGGER.fine("excute");
                        observer.execute(worker);
                        
                    } else {
                        observer.wait();
                    }
                }
            }   
                
            if( ! isCancelled() ) {
                setViewStateOfTree();
            }
            
        }

        // long end = System.currentTimeMillis();
        LOGGER.fine("DONE");
    }

    public GWorkerEvent construct() {
        return new SyncTreeEvent(this, tree);
    }

    private void setViewStateOfTree() {
        if (tree != null) {
            // include or exclude pattern match?
            SyncFile root = tree.getRoot();
            // we don't want to exclude the root of the sync tree
            // this root is a dummy anyway
            // but the childs
            SyncFile[] childs = root.getChildren();
            for (int i = 0; childs != null && i < childs.length; i++) {
                setViewState(childs[i]);
            }
        }
    }

    private boolean setViewState(final SyncFile file) {
        boolean childIncluded = false;

        if (file.getFile().isDirectory()) {
            if (inExcludeList(file)) {
                file.setExcluded(true);
                excludeChilds(file);
            } else if (!inIncludeList(file)) {

                // if it's not explicity excluded
                // check childs
                // if one child is included then don't exclude this dir
                boolean includedChilds = false;
                SyncFile[] childs = file.getChildren();
                for (int i = 0; childs != null && i < childs.length; i++) {
                    includedChilds |= setViewState(childs[i]);
                }

                if (includedChilds) {
                    childIncluded = true;
                } else {
                    file.setExcluded(true);
                }
            } else {
                // simply continue to set view state of children
                SyncFile[] childs = file.getChildren();
                for (int i = 0; childs != null && i < childs.length; i++) {
                    setViewState(childs[i]);
                }
            }
        } else {
            // file types other than dir
            if (!inIncludeList(file) || inExcludeList(file)) {
                file.setExcluded(true);
            } else {
                childIncluded = true;
            }
        }

        return childIncluded;
    }

    private void excludeChilds(final SyncFile dir) {
        if (dir == null) {
            return;
        }
        if (dir.getFile().isDirectory()) {
            SyncFile[] childs = dir.getChildren();
            for (int i = 0; i < childs.length; i++) {
                if (childs[i] != null) {
                    childs[i].setExcluded(true);
                    excludeChilds(childs[i]);
                }
            }
        }
    }

    private boolean inIncludeList(final SyncFile file) {
        boolean ret = false;

        if (includeList == null || includeList.size() == 0) {
            ret = true;
        } else {
            for (int i = 0; i < includeList.size(); i++) {
                boolean matches = includeList.get(i).matches(
                        file.getRelativePath());
                if (matches) {
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }

    private boolean inExcludeList(final SyncFile file) {
        boolean ret = false;

        if (excludeList != null) {
            for (int i = 0; i < excludeList.size(); i++) {
                boolean matches = excludeList.get(i).matches(
                        file.getRelativePath());
                if (matches) {
                    ret = true;
                    break;
                }
            }
        }

        // log.info("file : " + file.getRelativePath()+ ", excluded : " + ret);

        return ret;
    }

    private long fatModTime(final long time) {
        // see http://www.beginningtoseethelight.org/fat16/
        long mtime = time;

        final long resolution = 2000L;
        if (mtime % resolution != 0) {
            mtime /= resolution;
            mtime *= resolution;
            mtime += resolution;
        }
        return mtime;
    }

    @Override
    public void updateModel(GWorkerEvent e) {
        
    }

    @Override
    public void workerCancelled(GWorkerEvent e) {
        cancel();
        synchronized (observer) {
            observer.notifyAll();
        }
    }

    @Override
    public void workerDied(GWorkerEvent e) {
        setDied(((GWorker) e.getSource()).getException());
        synchronized (observer) {
            observer.notifyAll();
        }
    }

    @Override
    public void workerDone(GWorkerEvent e) {

        synchronized (observer) {
            LOGGER.fine("worker done");
            
            final TFileListEvent res = (TFileListEvent) ((GWorker) e.getSource())
                    .construct();

            TFileData data = res.getFileData();

            numFiles += data.getNumberofFiles();

            final TFileDataSorter dataSorter = new TFileDataSorter(
                    TFileDataSorter.SORT_ASCENDING,
                    TFileProperties.TFILE_PROPERTY_NAME);

            dataSorter.setFileData(data);

            TFileProperties[] files = dataSorter.getFileDataCopy()
                    .getFilePropertiesArray();

            for (int i = 0; i < files.length; i++) {

                /*
                 * now that we have all file handles 1) get the relative path from
                 * root 2) add the requested attributes
                 */

                String relativePath = con.getPathControlInstance().plainpath(path,
                        files[i].getAbsoluteFileName());

                LOGGER.fine("file : " + files[i].getAbsoluteFileName()
                        + " rel path : " + relativePath);

                SyncFile cfile = new SyncFile(files[i], relativePath);
                if (opt_fat_mode) {
                    cfile.getFile().setFileModTime(
                            fatModTime(cfile.getFile().getFileModTime()));
                }

                worker2dir.get(((GWorker) e.getSource())).addChild(cfile);

                if (files[i].isDirectory()) {
                    // push front instead of push back
                    // to make it look nicer
                    // make it a DFS

                    // check if excluded
                    // if explicitly excluded -> don't go deeper
                    if (inExcludeList(cfile)) {
                        LOGGER.fine("exclude dir: " + cfile.getRelativePath());
                        cfile.setExcluded(true);
                    } else {
                        dirsToGo.add(cfile);
                    }

                }
            }
                        
            observer.notifyAll();
        }
        
        
    }

}
