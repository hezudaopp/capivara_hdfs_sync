/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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

import java.awt.Component;
import java.util.logging.Logger;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerListener;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.TAbstractGUIElementListener;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.sync2.event.SyncTreeEvent;
import net.sf.jfilesync.sync2.event.SyncTreesMessage;
import net.sf.jfilesync.sync2.gui.SyncTreeBuilderDialog;
import net.sf.jfilesync.sync2.syncer.SyncSettings;

public class ConcurrentSyncTreeBuilder extends GWorker implements
        GWorkerListener, TAbstractGUIElementListener {

    private static Logger LOGGER = Logger
            .getLogger(ConcurrentSyncTreeBuilder.class.getName());

    // observer for sub-workers
    private final GWorkerObserver observer = new GWorkerObserver();
    private GWorker worker1, worker2;
    private SyncTreeBuilderDialog statsDialog;

    private final Component parent;
    private final AbstractConnectionProxy con1, con2;
    private final String path1, path2;
    private SyncTree tree1, tree2;
    private final ConnectionDetails conDetails1, conDetails2;

    private boolean worker1Done = false;
    private boolean worker2Done = false;
    private final SyncSettings settings;

    public ConcurrentSyncTreeBuilder(final Component parent, final int syncID,
            final AbstractConnectionProxy con1, final String path1,
            final ConnectionDetails conDetails1,
            final AbstractConnectionProxy con2, final String path2,
            final ConnectionDetails conDetails2) {
        this.parent = parent;
        this.con1 = con1;
        this.con2 = con2;
        this.path1 = path1;
        this.conDetails1 = conDetails1;
        this.path2 = path2;
        this.conDetails2 = conDetails2;
        observer.addWorkerListener(this);

        settings = SyncSettingsStore.getInstance().getSyncSettings(syncID);
        if (settings == null) {
            throw new IllegalArgumentException("sync settings not found");
        }
    }

    public int getSyncID() {
        return settings.getSyncID();
    }

    public void task() throws Exception {

        statsDialog = new SyncTreeBuilderDialog(parent, conDetails1,
                conDetails2);
        statsDialog.addTAbstractGUIElementListener(this);
        statsDialog.enableGUIElement(true);

        final SyncTreeBuilder2 myWorker = new SyncTreeBuilder2(con1, path1);
        //final SyncTreeBuilder3 myWorker = new SyncTreeBuilder3(con1, path1);
        myWorker.setIncludeExpressionList(settings.getIncludeList());
        myWorker.setExcludeExpressionList(settings.getExcludeList());
        myWorker.setFatModeEnabled(settings.getLeftFatModeEnabled());
        myWorker.addObserver(observer);

        final SyncTreeBuilder2 myWorker2 = new SyncTreeBuilder2(con2, path2);
        //final SyncTreeBuilder3 myWorker2 = new SyncTreeBuilder3(con2, path2);
        myWorker2.setIncludeExpressionList(settings.getIncludeList());
        myWorker2.setExcludeExpressionList(settings.getExcludeList());
        myWorker2.setFatModeEnabled(settings.getRightFatModeEnabled());
        myWorker2.addObserver(observer);

        worker1 = myWorker;
        worker2 = myWorker2;

        observer.execute(worker1);
        observer.execute(worker2);

        while ((!worker1Done || !worker2Done) && !isInterrupted()
                && !isCancelled()) {
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
            }
        }
    }

    public GWorkerEvent construct() {
        return new SyncTreesMessage(this, tree1, tree2);
    }

    public synchronized void workerDone(GWorkerEvent e) {

        if (e.getSource() == worker1) {
            LOGGER.fine(e.getSource() + " done 1");
            tree1 = ((SyncTreeEvent) worker1.construct()).getTree();
            worker1Done = true;
        } else if (e.getSource() == worker2) {
            LOGGER.fine(e.getSource() + " done 2");
            tree2 = ((SyncTreeEvent) worker2.construct()).getTree();
            worker2Done = true;
        }

        if (worker1Done && worker2Done) {
            notifyAll();
            statsDialog.enableGUIElement(false);
        }

    }

    public synchronized void workerCancelled(GWorkerEvent e) {
        if (worker1.hasDied() || worker2.hasDied()) {
            if (worker1.hasDied()) {
                setDied(worker1.getException());
            } else {
                setDied(worker2.getException());
            }
            statsDialog.enableGUIElement(false);
            notifyAll();
        } else { 
            cancel();
            statsDialog.enableGUIElement(false);
            notifyAll();
        }
    }

    public synchronized void workerDied(GWorkerEvent e) {
        if (worker1.hasDied() || worker2.hasDied()) {
            if (worker1.hasDied()) {
                setDied(worker1.getException());
                observer.cancel(worker2);
            }
            if (worker2.hasDied()) {
                setDied(worker2.getException());
                observer.cancel(worker1);
            }
            statsDialog.enableGUIElement(false);
            notifyAll();
        }
    }

    public void updateModel(GWorkerEvent e) {
        if (statsDialog != null) {
            if (e.getSource() == worker1) {
                statsDialog.displayWorkerDataForConnection1(e);
            } else if (e.getSource() == worker2) {
                statsDialog.displayWorkerDataForConnection2(e);
            }
        }
    }

    public void cancelClicked(TAbstractDialogEvent e) {
        if (worker1 != null) {
            observer.cancel(worker1);
        }
        if (worker2 != null) {
            observer.cancel(worker2);
        }
    }

}
