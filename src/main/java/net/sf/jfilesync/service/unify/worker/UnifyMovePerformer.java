/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id$
 */
package net.sf.jfilesync.service.unify.worker;

import java.util.ArrayList;
import java.util.List;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerListener;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.BasicFileCopyWorker;
import net.sf.jfilesync.engine.BasicFileTree;
import net.sf.jfilesync.engine.FileCopyListener;
import net.sf.jfilesync.engine.worker.BasicFileTreeDeleteWorker;
import net.sf.jfilesync.engine.worker.BasicFileTreeDeleteWorker.DELETE_TYPE;
import net.sf.jfilesync.engine.worker.events.DeleteStatusMessage;
import net.sf.jfilesync.engine.worker.events.FileProgressWorkerEvent;
import net.sf.jfilesync.engine.worker.events.TSimpleWorkerDoneEvent;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.service.unify.action.UnifyAction;
import net.sf.jfilesync.service.unify.action.UnifyMoveOption;

public class UnifyMovePerformer extends GWorker implements FileCopyListener,
        GWorkerListener {

    private final UnifyMoveOption option;
    private final AbstractConnectionProxy con;
    private final GWorkerObserver observer = new GWorkerObserver();

    private int actionNum;
    private int actionsDone;

    public UnifyMovePerformer(final AbstractConnectionProxy con,
            final UnifyMoveOption option) {
        this.con = con;
        this.option = option;
    }

    @Override
    public GWorkerEvent construct() {
        return new TSimpleWorkerDoneEvent(this);
    }

    @Override
    public void task() throws Exception {

        final List<BasicFile> filesToCopy = new ArrayList<BasicFile>();
        for (final UnifyAction action : option.getUnifyActions()
                .getUnifyActionList()) {
            filesToCopy.add(action.getUnifyFile());
        }

        final BasicFileTree copyTree = option.buildSourceCopyBasicFileTree();

        // each file has to be copied
        // only files have to be deleted
        actionNum = copyTree.getNumberOfFiles() + copyTree.getNumberOfNodes();
        actionsDone = 0;

        BasicFileCopyWorker copyWorker = new BasicFileCopyWorker(con, option
                .getSourcePath(), option.getTargetPath(), copyTree);
        copyWorker.addFileCopyListener(this);
        copyWorker.addObserver(observer);
        observer.executeAndWait(copyWorker);

        if (copyWorker.getWorkerState() == STATE_DONE && !isCancelled()) {
            BasicFileTreeDeleteWorker deleteWorker = new BasicFileTreeDeleteWorker(
                    con, copyTree, DELETE_TYPE.FILES_ONLY);
            deleteWorker.addObserver(observer);
            observer.addWorkerListener(this);
            observer.executeAndWait(deleteWorker);
            observer.removeWorkerListener(this);
        }

    }

    public void finishCopying(String filename) {
        actionsDone++;
    }

    public void startCopying(String filename) {
        getObserver().updateGUI(
                new FileProgressWorkerEvent(this, filename,
                        getProgressPercentage()));
    }

    private int getProgressPercentage() {
        int percentage = 0;
        if (actionNum > 0) {
            percentage = actionsDone * 100 / actionNum;
        }
        return percentage;
    }

    public void updateModel(GWorkerEvent e) {
        if (e instanceof DeleteStatusMessage) {
            actionsDone++;
            DeleteStatusMessage msg = (DeleteStatusMessage) e;
            getObserver().updateGUI(
                    new FileProgressWorkerEvent(this, msg.getFileName(),
                            getProgressPercentage()));
        }
    }

    public void workerCancelled(GWorkerEvent e) {
        cancel();
    }

    public void workerDied(GWorkerEvent e) {
        cancel();
    }

    public void workerDone(GWorkerEvent e) {

    }

}
