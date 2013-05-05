/*
 * capivara - Java File Synchronization
 *
 * Created on 26-Jul-2005
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
 * $Id: SyncPerformer2.java,v 1.11 2006/08/23 22:20:52 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.CopyProgressListener;
import net.sf.jfilesync.engine.FileCopyListener;
import net.sf.jfilesync.engine.FileTransfer2;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.plugins.GeneralPlugin;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.prop.LanguageBundle;

public class SyncPerformer2 extends GWorker implements FileCopyListener {

    private final AbstractConnectionProxy con1;
    private final AbstractConnectionProxy con2;
    private final SyncAction[] list1;
    private final SyncAction[] list2;
    private final String rootPath1;
    private final String rootPath2;
    private CopyProgressListener progressListener;
    private FileTransfer2 transfer;
    // just for retrieving the action name
    private SyncActionCopy dummy = new SyncActionCopy(null);
    // private boolean opt_follow_symlinks = false;
    int filesDone = 0;
    int filesTotal = 0;
    private boolean inCopy = false;

    // private final static Logger LOGGER =
    // Logger.getLogger(SyncPerformer2.class
    // .getPackage().getName());

    public SyncPerformer2(final AbstractConnectionProxy con1,
            final SyncAction[] list1, final String rootPath1,
            final AbstractConnectionProxy con2, final SyncAction[] list2,
            final String rootPath2) {
        this.con1 = con1;
        this.con2 = con2;
        this.list1 = list1;
        this.list2 = list2;
        this.rootPath1 = rootPath1;
        this.rootPath2 = rootPath2;
    }

    public void task() throws Exception {
        filesTotal = list1.length + list2.length;
        syncList(con1, rootPath1, list1, con2, rootPath2);
        syncList(con2, rootPath2, list2, con1, rootPath1);
    }

    public void setCopyProgressListener(CopyProgressListener listener) {
        progressListener = listener;
    }

    private void syncList(AbstractConnectionProxy source, String sourceRoot,
            SyncAction[] list, AbstractConnectionProxy target, String targetRoot)
            throws IOException {

        final List<SyncAction> copyList = new ArrayList<SyncAction>();
        final List<SyncAction> deleteList = new ArrayList<SyncAction>();

        for (int i = 0; i < list.length; i++) {
            if (list[i] instanceof SyncActionCopy) {
                copyList.add(list[i]);
            } else if (list[i] instanceof SyncActionDelete) {
                deleteList.add(list[i]);
            }
        }

        performCopyActions(source, sourceRoot, copyList, target, targetRoot);

        performDeleteActions(source, sourceRoot, deleteList, target, targetRoot);

    }

    private void performCopyActions(AbstractConnectionProxy source,
            String sourceRoot, List<SyncAction> copyList,
            AbstractConnectionProxy target, String targetRoot)
            throws IOException {

        // we need this to create an array of TFileProperties
        List<TFileProperties> fileList = new ArrayList<TFileProperties>();

        final Iterator<SyncAction> it = copyList.iterator();
        while (it.hasNext()) {
            SyncActionCopy action = (SyncActionCopy) it.next();
            fileList.add(action.getSyncFile().getFile());
        }

        if (fileList.size() > 0) {
            TFileProperties[] files = (TFileProperties[]) fileList
                    .toArray(new TFileProperties[fileList.size()]);

            transfer = new FileTransfer2(source, sourceRoot, target,
                    targetRoot, files);

            if (progressListener != null) {
                transfer.addCopyProgressListener(progressListener);
            }

            transfer.addFileCopyListener(this);

            setTransferOptions(transfer);

            synchronized (this) {
                inCopy = true;
            }

            transfer.startCopying();

            synchronized (this) {
                inCopy = false;
            }
        }

    }

    private void performDeleteActions(AbstractConnectionProxy source,
            String sourceRoot, List<SyncAction> deleteList,
            AbstractConnectionProxy target, String targetRoot)
            throws IOException {

        final Iterator<SyncAction> it = deleteList.iterator();
        while (it.hasNext()) {
            SyncActionDelete action = (SyncActionDelete) it.next();
            sendProgressMsg(action.getActionName(), action.getSyncFile()
                    .getFile().getAbsoluteFileName());
            try {
                source.remove(action.getSyncFile().getFile()
                        .getAbsoluteFileName());
            } catch (IOException e) {
                boolean cont = showErrorDialog(e.getMessage());
                if (!cont) {
                    break;
                }
            }
            filesDone++;
        }

    }

    private boolean showErrorDialog(final String errorMessage) {
        boolean cont = true;

        String[] options = new String[] {
                LanguageBundle.getInstance().getMessage("label.continue"),
                LanguageBundle.getInstance().getMessage("label.cancel") };

        final int option = JOptionPane.showOptionDialog(null, errorMessage,
                LanguageBundle.getInstance().getMessage("label.ioerror"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);

        if (option == JOptionPane.CLOSED_OPTION) {
            cont = false;
        } else if (option == 1) {
            cont = false;
        }

        return cont;
    }

    private void setTransferOptions(final FileTransfer2 transfer) {
        transfer.addOption(FileTransfer2.OPTION_PRESERVE_MTIME);
        transfer.setOverrideMode(FileTransfer2.MODE_OVERRIDE_ALL);

        if (con1.getPlugin().isProvided(
                GeneralPlugin.PROVIDES_PERMISSION_HANDLING)
                && con2.getPlugin().isProvided(
                        GeneralPlugin.PROVIDES_PERMISSION_HANDLING)) {
            transfer.addOption(FileTransfer2.OPTION_PRESERVE_PERMISSIONS);
        }
    }

    private void sendProgressMsg(String action, String fileName) {
        if (filesTotal <= 0) {
            return;
        }
        int progress = (filesTotal <= 0) ? 0 : filesDone * 100 / filesTotal;
        getObserver().updateGUI(
                new SyncProgressMessage(this, progress, action, fileName));
    }

    public GWorkerEvent construct() {
        // does not generate any result object
        // it just works
        return null;
    }

    public void startCopying(String filename) {
        sendProgressMsg(dummy.getActionName(), filename);
    }

    public void finishCopying(String filename) {
        filesDone++;
        sendProgressMsg(dummy.getActionName(), filename);
    }

    public synchronized void customCancel() {
        if (inCopy && transfer != null) {
            transfer.cancelTransfer();
        }
    }

}
