/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2005 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: SyncComparator.java,v 1.32 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.develop.DevelopmentMode;
import net.sf.jfilesync.engine.worker.events.ProgressMessage;
import net.sf.jfilesync.engine.worker.events.SyncCompareProgressMessage;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.diffs.SyncDiffSubDir;
import net.sf.jfilesync.sync2.diffs.SyncDiffTree;
import net.sf.jfilesync.sync2.diffs.SyncDiffType;
import net.sf.jfilesync.sync2.diffs.SyncDiffUnique;
import net.sf.jfilesync.sync2.event.SyncDiffEvent;
import net.sf.jfilesync.sync2.syncer.AbstractSyncPathComparator;
import net.sf.jfilesync.sync2.syncer.ISyncFileSorter;
import net.sf.jfilesync.sync2.syncer.SyncSettings;
import net.sf.jfilesync.util.TPathControlInterface;

public class SyncComparator extends GWorker {

    private final AbstractConnectionProxy con1;
    private final AbstractConnectionProxy con2;
    private final SyncTree tree1, tree2;
    private final SyncMethod syncMethod;
    private final TPathControlInterface pathControl1, pathControl2;

    private final static int TAKE_LEFT = -1,
                                  TAKE_BOTH = 0,
                                 TAKE_RIGHT = 2;

  private int syncID;
    private final AbstractSyncPathComparator pathComparator;
    private final ISyncFileSorter sorter;
    private int filesDone = 0;
    private int files1, files2;
    private SyncDiffTree[] syncDiffTrees;
    private final SyncSettings settings;

    private final static Logger LOGGER = Logger.getLogger(SyncComparator.class
            .getName());

    public SyncComparator(final int syncID, final AbstractConnectionProxy con1,
            final SyncTree tree1, final AbstractConnectionProxy con2,
            final SyncTree tree2, final SyncMethod syncMethod) {

        if (tree1 == null) {
            throw new IllegalArgumentException("tree1 is null");
        }
        if (tree2 == null) {
            throw new IllegalArgumentException("tree2 is null");
        }

        this.syncID = syncID;
        this.con1 = con1;
        this.tree1 = tree1;
        this.con2 = con2;
        this.tree2 = tree2;
        this.syncMethod = syncMethod;

        settings = SyncSettingsStore.getInstance().getSyncSettings(syncID);
        if (settings == null) {
            throw new IllegalArgumentException(
                    "no settings found for syncID : " + syncID);
        }

        pathComparator = settings.getAbstractSyncPathComparator();
        sorter = settings.getISyncFileSorter();

        pathControl1 = tree1.getPathControl();
        pathControl2 = tree2.getPathControl();
        files1 = tree1.getNumberOfFiles();
        files2 = tree2.getNumberOfFiles();

        LOGGER.info("files in dir1 : " + files1 + " files in dir2 : " + files2);
    }

    public int getSyncID() {
        return syncID;
    }

    public void task() throws Exception {

        final boolean halt = validateData();
        if (halt) {
            cancel();
            return;
        }

        final SyncFile root1 = tree1.getRoot();
        final SyncFile root2 = tree2.getRoot();

        try {
            syncDiffTrees = comparePaths(root1, root2);
        } catch (IOException e) {
            if (isCancelled()) {
                throw new InterruptedException();
            } else {
                throw e;
            }
        }

        if (syncDiffTrees[0] == null) {
            syncDiffTrees[0] = SyncDiffTree.getDummyTree();
        }
        if (syncDiffTrees[1] == null) {
            syncDiffTrees[1] = SyncDiffTree.getDummyTree();
        }

    }

    private boolean validateData() {
        boolean halt = false;

        halt = validateTree(tree1);
        if (!halt) {
            halt = validateTree(tree2);
        }

        return halt;
    }

    private boolean validateTree(SyncTree tree12) {
        boolean halt = false;

        final ISyncDataValidator validator = settings.getISyncDataValidator();
        ISyncDataValidationError error = validator.validateTree(tree1);
        if (error != null) {
            final int choice = JOptionPane.showConfirmDialog(null, error
                    .getMessage(), LanguageBundle.getInstance().getMessage(
                    "label.continue"), JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.NO_OPTION) {
                halt = true;
            }
        }

        return halt;
    }

    public GWorkerEvent construct() {
        GWorkerEvent event = null;
        if (syncDiffTrees != null) {
            event = new SyncDiffEvent(this, syncDiffTrees[0], syncDiffTrees[1]);
        } else {
            event = new SyncDiffEvent(this, null, null);
        }
        return event;
    }

    protected SyncDiffTree[] comparePaths(final SyncFile file1,
            final SyncFile file2) throws SyncException, IOException {

        // all differences found in this dir
        final List<SyncDiff> diffs1 = new ArrayList<SyncDiff>();
        final List<SyncDiff> diffs2 = new ArrayList<SyncDiff>();

        // result trees
        SyncDiffTree[] resTree = new SyncDiffTree[2];
        SyncDiff leftTreeRoot = null;
        SyncDiff rightTreeRoot = null;

        final SyncFile[] child_ar1 = file1.getChildren();
        final SyncFile[] child_ar2 = file2.getChildren();

        final SyncFileQueue queue1 = new SyncFileQueue(sorter
                .sortByName(child_ar1));
        final SyncFileQueue queue2 = new SyncFileQueue(sorter
                .sortByName(child_ar2));

        // log.info(queue1.toString());
        // log.info(queue2.toString());

        int status = TAKE_BOTH;
        SyncFile leftFile = null;
        SyncFile rightFile = null;

        final GWorkerObserver observer = getObserver();

        while ((!queue1.isEmpty() || !queue2.isEmpty()) && !isCancelled()) {

            if (status == TAKE_BOTH) {
                if (queue1.isEmpty()) {
                    if (rightFile != null) {
                        setFileUnique(rightFile, diffs2);
                    }
                    setQueueUnique(queue2, diffs2);
                    break;
                } else if (queue2.isEmpty()) {
                    if (leftFile != null) {
                        setFileUnique(leftFile, diffs1);
                    }
                    setQueueUnique(queue1, diffs1);
                    break;
                } else {
                    leftFile = queue1.pop();
                    rightFile = queue2.pop();
                }
            } else if (status == TAKE_LEFT) {
                if (queue1.isEmpty()) {
                    if (rightFile != null) {
                        setFileUnique(rightFile, diffs2);
                    }
                    setQueueUnique(queue2, diffs2);
                    break;
                } else {
                    leftFile = queue1.pop();
                }
            } else if (status == TAKE_RIGHT) {
                if (queue2.isEmpty()) {
                    if (leftFile != null) {
                        setFileUnique(leftFile, diffs1);
                    }
                    setQueueUnique(queue1, diffs1);
                    break;
                } else {
                    rightFile = queue2.pop();
                }
            }

            if (observer != null) {
                SyncCompareProgressMessage msg = new SyncCompareProgressMessage(
                        this, leftFile.getFile().getAbsoluteFileName(),
                        rightFile.getFile().getAbsoluteFileName(),
                        getProgress());
                observer.updateGUI(msg);

                if (DevelopmentMode.SYNC_COMPARATOR_GUI_DEBUG) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }

            // normalize to netPath
            // e.g. for Windows substitute \ with /
            final String relPath1 = pathControl1.getNetPath(leftFile
                    .getRelativePath());
            final String relPath2 = pathControl2.getNetPath(rightFile
                    .getRelativePath());

            // int comp = relPath1.compareTo(relPath2);
            final int comp = pathComparator.compare(relPath1, relPath2);

            // log.info("cmp: " + relPath1 + " <-> " + relPath2);

            if (comp == 0) {
                status = TAKE_BOTH;
                if (leftFile.getFile().isDirectory()
                        && rightFile.getFile().isDirectory()) {

                    SyncDiffTree[] subTrees = comparePaths(leftFile, rightFile);

                    if (subTrees.length > 0 && subTrees[0] != null) {
                        if (resTree[0] == null) {
                            leftTreeRoot = new SyncDiffSubDir(file1);
                            leftTreeRoot.addChild(subTrees[0].getRoot());
                            resTree[0] = new SyncDiffTree(leftTreeRoot);
                        } else {
                            leftTreeRoot.addChild(subTrees[0].getRoot());
                        }
                    }

                    if (subTrees.length > 1 && subTrees[1] != null) {
                        if (resTree[1] == null) {
                            rightTreeRoot = new SyncDiffSubDir(file2);
                            rightTreeRoot.addChild(subTrees[1].getRoot());
                            resTree[1] = new SyncDiffTree(rightTreeRoot);
                        } else {
                            rightTreeRoot.addChild(subTrees[1].getRoot());
                        }
                    }

                } else if (!leftFile.getFile().isDirectory()
                        && !rightFile.getFile().isDirectory()) {

                    compareFiles(leftFile, diffs1, rightFile, diffs2);
                    filesDone += 2;

                } else {
                    // something weird
                    // leftFile/rightFile have the same name
                    // but one's a dir and the other is a file
                    // what now? we could simply pretend it never happened?
                    // but we WONT do that
                    diffs1.add(new SyncDiffType(leftFile));
                    diffs2.add(new SyncDiffType(rightFile));
                }

                leftFile = null;
                rightFile = null;
            } else {
                if (comp < 0) {
                    setFileUnique(leftFile, diffs1);
                    status = TAKE_LEFT;
                    if (queue1.isEmpty() && queue2.isEmpty()) {
                        setFileUnique(rightFile, diffs2);
                    }
                } else {
                    setFileUnique(rightFile, diffs2);
                    status = TAKE_RIGHT;
                    if (queue1.isEmpty() && queue2.isEmpty()) {
                        setFileUnique(leftFile, diffs1);
                    }
                }
            }
        }

        if (diffs1.size() > 0) {
            if (leftTreeRoot == null) {
                leftTreeRoot = new SyncDiffSubDir(file1);
                assert (resTree[0] == null);
                resTree[0] = new SyncDiffTree(leftTreeRoot);
            }
            Iterator<SyncDiff> it = diffs1.iterator();
            while (it.hasNext()) {
                leftTreeRoot.addChild((SyncDiff) it.next());
            }
        }

        if (diffs2.size() > 0) {
            if (rightTreeRoot == null) {
                rightTreeRoot = new SyncDiffSubDir(file2);
                assert (resTree[1] == null);
                resTree[1] = new SyncDiffTree(rightTreeRoot);
            }
            Iterator<SyncDiff> it = diffs2.iterator();
            while (it.hasNext()) {
                rightTreeRoot.addChild((SyncDiff) it.next());
            }
        }

        return resTree;
    }

    // ---------------------------------------------------------------------------
    private void setQueueUnique(SyncFileQueue queue, List<SyncDiff> diffList) {
        while (!queue.isEmpty()) {
            final SyncFile file = queue.pop();
            setFileUnique(file, diffList);
        }
    }

    private void setFileUnique(final SyncFile file,
            final List<SyncDiff> diffList) {
        assert (file != null);

        if (!file.isExcluded()) {
            SyncDiff diff = new SyncDiffUnique(file);
            diffList.add(diff);
            if (file.getFile().isDirectory()) {
                setSubDirsUnique(diff);
            }
        }

        if (!file.getFile().isDirectory()) {
            fileDone();
        }

    }

    private void setSubDirsUnique(SyncDiff node) {

        // double check to really work on dirs
        if (node.getSyncFile().getFile().isDirectory()) {

            final SyncFile[] childs = node.getSyncFile().getChildren();

            for (int i = 0; i < childs.length; i++) {
                final SyncFile child = childs[i];

                if (!child.isExcluded()) {
                    SyncDiff leaf = new SyncDiffUnique(child);
                    node.addChild(leaf);
                    if (child.getFile().isDirectory()) {
                        setSubDirsUnique(leaf);
                    }
                }

                if (!child.getFile().isDirectory()) {
                    fileDone();
                }
            }

        }

    }

    private void fileDone() {
        filesDone++;
        GWorkerObserver o = getObserver();
        if (o != null) {
            ProgressMessage msg = new ProgressMessage(this, getProgress());
            o.updateGUI(msg);
        }
    }

    // ---------------------------------------------------------------------------

    protected void compareFiles(final SyncFile file1,
            final List<SyncDiff> diffList1, final SyncFile file2,
            final List<SyncDiff> diffList2) throws IOException {

        // if files are excluded by regex return immediately
        if (file1.isExcluded() || file2.isExcluded()) {
            return;
        }

        SyncAttributeComparator sac = syncMethod.getSyncAttributeComparator();
        try {
            SyncDiff[] diffs = sac.compare(con1, file1, con2, file2);
            if (diffs[0] != null) {
                diffList1.add(diffs[0]);
            }
            if (diffs[1] != null) {
                diffList2.add(diffs[1]);
            }
        } catch (IOException e) {

            LOGGER.warning(e.getMessage());

            // if IO exception occurs
            // ask user to continue and to exclude both files

            String question = LanguageBundle.getInstance().getMessage(
                    "error.sync.compare_files");

            question = question.replaceFirst("%f", file1.getRelativePath());
            question = question.replaceFirst("%e", e.getMessage());

            int opt = JOptionPane
                    .showConfirmDialog(null, question, LanguageBundle
                            .getInstance().getMessage("error.io.general"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);

            if (opt == JOptionPane.YES_OPTION) {
                file1.setExcluded(true);
                file2.setExcluded(true);
            } else {
                cancel();
            }
        }
    }

    private int getProgress() {
        if ((files1 + files2) <= 0) {
            return 100;
        } else {
            return Math.min(filesDone * 100 / (files1 + files2), 100);
        }
    }

    class SyncFileQueue {
        final LinkedList<SyncFile> list = new LinkedList<SyncFile>();

        public SyncFileQueue() {
        }

        public SyncFileQueue(SyncFile[] files) {
            for (int i = 0; i < files.length; i++) {
                list.addLast(files[i]);
            }
        }

        public SyncFile pop() {
            if (list.isEmpty())
                return null;
            else {
                SyncFile f = (SyncFile) list.getFirst();
                list.removeFirst();
                return f;
            }
        }

        public void push(SyncFile file) {
            list.addLast(file);
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public int getSize() {
            return list.size();
        }

        public String toString() {
            final StringBuffer buf = new StringBuffer();
            //buf.append(this);
            buf.append("SyncQueue " + hashCode());
            buf.append("\n");
            for (SyncFile file : list) {
                buf.append(file.getRelativePath());
                buf.append("\n");
            }
            return buf.toString();
        }

    }

}
