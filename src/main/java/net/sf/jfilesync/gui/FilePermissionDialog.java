/*
 * capivara - Java File Synchronization
 *
 * Created on 30-May-2005
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
 * $Id: FilePermissionDialog.java,v 1.7 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.gui;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.gui.components.PermissionPanel;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;

public class FilePermissionDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    public final static int OPTION_OK = 0;
    public final static int OPTION_CANCEL = 1;
    private int option = OPTION_CANCEL;

    private final static String EVT_OK = "OK";
    private final static String EVT_CANCEL = "CANCEL";
    private final static String EVT_DEFAULT = "DEFAULT";

    private PermissionPanel dirPermPanel = new PermissionPanel(
            PermissionPanel.TYPE_DIR, false);
    private PermissionPanel filePermPanel = new PermissionPanel(
            PermissionPanel.TYPE_FILE, false);

    private final static Logger LOGGER = Logger
            .getLogger(FilePermissionDialog.class.getPackage().getName());

    public FilePermissionDialog(Dialog owner) {
        super(owner, LanguageBundle.getInstance().getMessage(
                "dialog.permissions.title"), true);
        initUI();
        setDefaultValues();
    }

    public FilePermissionDialog(Frame owner) {
        super(owner, LanguageBundle.getInstance().getMessage(
                "dialog.permissions.title"), true);
        initUI();
        setDefaultValues();
    }

    protected void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        dirPermPanel.setBorder(BorderFactory.createTitledBorder(LanguageBundle
                .getInstance().getMessage("label.perm.dir")));
        mainPanel.add(dirPermPanel);

        filePermPanel.setBorder(BorderFactory.createTitledBorder(LanguageBundle
                .getInstance().getMessage("label.perm.file")));
        mainPanel.add(filePermPanel);

        JPanel buttonP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okB = new JButton(LanguageBundle.getInstance().getMessage(
                "label.ok"));
        okB.addActionListener(this);
        okB.setActionCommand(EVT_OK);
        JButton cancelB = new JButton(LanguageBundle.getInstance().getMessage(
                "label.cancel"));
        cancelB.addActionListener(this);
        cancelB.setActionCommand(EVT_CANCEL);
        JButton defaultB = new JButton(LanguageBundle.getInstance().getMessage(
                "label.default"));
        defaultB.addActionListener(this);
        defaultB.setActionCommand(EVT_DEFAULT);

        buttonP.add(okB);
        buttonP.add(cancelB);
        buttonP.add(defaultB);

        mainPanel.add(buttonP);

        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(getParent());
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals(EVT_OK)) {
            option = OPTION_OK;
            setVisible(false);
        } else if (cmd.equals(EVT_CANCEL)) {
            option = OPTION_CANCEL;
            setVisible(false);
        } else if (cmd.equals(EVT_DEFAULT)) {
            setDefaultValues();
        }
    }

    protected void setDefaultValues() {
        try {
            int dirPerm = MainWin.config.getProgramSettings().getIntegerOption(
                    TProgramSettings.OPTION_PERMISSIONS_DIR);
            dirPermPanel.setPermissions(dirPerm);

            int filePerm = MainWin.config.getProgramSettings()
                    .getIntegerOption(TProgramSettings.OPTION_PERMISSIONS_FILE);
            filePermPanel.setPermissions(filePerm);

            dirPermPanel.repaint();
            filePermPanel.repaint();
        } catch (SettingsTypeException ste) {
            LOGGER.warning(ste.getMessage());
        }
    }

    public int getUserOption() {
        return option;
    }

    public int getDirectoryPermissions() {
        return dirPermPanel.getPermissions();
    }

    public boolean isRecursiveEnable() {
        return dirPermPanel.recursive();
    }

    public int getFilePermissions() {
        return filePermPanel.getPermissions();
    }

}
