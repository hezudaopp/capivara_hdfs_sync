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
 * $Id$
 */
package net.sf.jfilesync.service.unify;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.gui.viewmodel.BasicFilePopupEvent;
import net.sf.jfilesync.gui.viewmodel.BasicFilePopupListener;
import net.sf.jfilesync.service.unify.action.UnifyDeleteOption;
import net.sf.jfilesync.service.unify.action.UnifyMoveOption;
import net.sf.jfilesync.service.unify.action.UnifyOption;

public class DupController implements DupExplorerListener,
        BasicFilePopupListener, ActionListener {

    public enum DupControllerEvent {
        OKAY, CANCELLED
    };

    private final UnifyFileTree fileTree;
    private final DupExplorer explorer;

    private UnifyActionDialog actionPreviewDialog;

    private DupControllerEvent userOption = DupControllerEvent.CANCELLED;

    private UnifyDeleteOption deleteOption;
    private UnifyMoveOption moveOption;
    private UnifyPathRequester pathRequester;

    private final static Logger LOGGER = Logger.getLogger(DupController.class
            .getName());

    public DupController(UnifyFileTree fileTree, DupExplorer explorer) {
        this.fileTree = fileTree;
        this.explorer = explorer;
        this.explorer.addDupExlorerListener(this);
        this.explorer.addBasicFileTreePopupListener(this);
        this.explorer.addBasicFileTablePopupListener(this);

        pathRequester = new UnifyPathRequester(this.explorer);
        pathRequester.addDupExlorerListener(this);

        deleteOption = new UnifyDeleteOption(this.fileTree);
        moveOption = new UnifyMoveOption(this.fileTree);

        this.explorer.addUnifyOption(deleteOption);
        this.explorer.addUnifyOption(moveOption);
    }

    public void actionPerformed(ActionEvent e) {

        final String command = e.getActionCommand();

        if (command.equals(UnifyActionDialog.ACTION_START)) {

            if (actionPreviewDialog == null) {
                LOGGER.warning("BUG: preview must not be null");
            } else {
                userOption = DupControllerEvent.OKAY;
                explorer.setVisible(false);
            }

        } else if (command.equals(UnifyActionDialog.ACTION_CANCEL)) {

            userOption = DupControllerEvent.CANCELLED;

            if (actionPreviewDialog != null) {
                actionPreviewDialog.setVisible(false);
            }

        } else {
            LOGGER.warning("unknown command: " + command);
        }
    }

    public void performDupExplorerEvent(DupExplorerEvent e) {

        String command = e.getCommand();

        if (e instanceof DupMovePathEvent) {
            if (e.getCommand().equals(DupMovePathEvent.COMMAND_OK)) {

                final String path = pathRequester.getSelectedPath();
                if (path == null || path.equals("")) {

                    JOptionPane.showMessageDialog(pathRequester,
                            "Invalid path! Select again!");

                } else if (path
                        .startsWith(fileTree.getRoot().getAbsolutePath())) {
                    
                    JOptionPane.showMessageDialog(pathRequester,
                            "Cannot move duplicated files into subdirectory of "
                                    + fileTree.getRoot().getAbsolutePath());

                } else {
                    
                    File targetPath = new File(path);
                    
                    if( ! targetPath.exists() ) {
                        JOptionPane.showMessageDialog(pathRequester,
                                "Target path does not exist: " + path);
                    } else if( ! targetPath.canWrite() ) {
                        JOptionPane.showMessageDialog(pathRequester,
                                "Target path not writable: " + path);
                    } else {
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            DupController.this.pathRequester.setVisible(false);
                        }
                    });
                        moveOption.setTargetPath(path);
                        actionPreviewDialog = new UnifyActionDialog(explorer,
                                moveOption, this);
                        actionPreviewDialog.setVisible(true);
                    }
                }

            } else if (e.getCommand().equals(DupMovePathEvent.COMMAND_CANCEL)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        DupController.this.pathRequester.setVisible(false);
                    }
                });
            } else {
                LOGGER.warning("unkown command " + e.getCommand());
            }

        } else if (command.equals("cancel")) {
            explorer.setVisible(false);
        } else if (command.equals("preview")) {

            final UnifyOption option = getSelectedOption();
            if (option == null) {
                LOGGER.warning("BUG: option must not be null");
            } else {

                if (option instanceof UnifyMoveOption) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            DupController.this.pathRequester.setVisible(true);
                        }
                    });
                } else {
                    // final UnifyActions actions = option.getUnifyActions();
                    actionPreviewDialog = new UnifyActionDialog(explorer,
                            option, this);
                    actionPreviewDialog.setVisible(true);
                }
            }

        } else {
            LOGGER.warning("unknown command: " + command);
        }

    }

    public DupControllerEvent getUserChoice() {
        return userOption;
    }

    public UnifyOption getSelectedOption() {
        final UnifyOption option = explorer.getSelectedOption();
        if (option == null) {
            LOGGER.warning("BUG: option must not be null");
        }
        return option;
    }

    public void popupAction(BasicFilePopupEvent e) {
        final String command = e.getCommand();

        if (command
                .equals(UnifyFileTreePopup.EVENT_KEEP_CHILDREN_DEL_DUPLICATES)) {
            final BasicFile currentFile = e.getSelectedFile();
            final UnifyFile file = (UnifyFile) currentFile;
            file.keepAllChildren(true);
            explorer.updateUI();
        } else if (command.equals(UnifyFileTreePopup.EVENT_KEEP_CHILDREN)) {
            final BasicFile currentFile = e.getSelectedFile();
            final UnifyFile file = (UnifyFile) currentFile;
            file.keepAllChildren(false);
            explorer.updateUI();
        } else if (command
                .equals(UnifyFileTablePopup.EVENT_KEEP_FILE_DEL_DUPLICATES)) {
            final List<UnifyFile> selectedFiles = explorer.getSelectedFiles();
            for (UnifyFile file : selectedFiles) {
                file.setKeepIt(true);
                for (UnifyFile duplicate : file.getDuplicates()) {
                    duplicate.setKeepIt(false);
                }
            }
            explorer.updateUI();
        } else if (command.equals(UnifyFileTablePopup.EVENT_KEEP_FILE)) {
            final List<UnifyFile> selectedFiles = explorer.getSelectedFiles();
            for (UnifyFile file : selectedFiles) {
                file.setKeepIt(true);
            }
            explorer.updateUI();
        }

    }

}
