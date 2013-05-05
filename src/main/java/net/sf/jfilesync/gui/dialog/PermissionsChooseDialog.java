/*
 * capivara - Java File Synchronization
 *
 * Created on 10-Jun-2005
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
 * $Id: PermissionsChooseDialog.java,v 1.7 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.gui.dialog;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.sf.jfilesync.gui.components.PermissionPanel;
import net.sf.jfilesync.prop.LanguageBundle;

public class PermissionsChooseDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    public static final int TYPE_FILES_ONLY = 0;
    public static final int TYPE_FILES_AND_DIRS = 1;

    public static final int OPTION_PROCEED = 0;
    public static final int OPTION_CANCEL = 1;

    private int user_option = OPTION_CANCEL;

    private final int type;
    private final PermissionPanel dirPanel = new PermissionPanel(
            PermissionPanel.TYPE_DIR, true);
    private final PermissionPanel filePanel = new PermissionPanel(
            PermissionPanel.TYPE_FILE, false);

    private JButton okButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.ok"));
    private JButton cancelButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.cancel"));

    public PermissionsChooseDialog(final Component parent, final int dialogType) {
        this.type = dialogType;

        setModal(true);
        setTitle(LanguageBundle.getInstance().getMessage(
                "dialog.change_perm.title"));

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        switch (type) {
        case TYPE_FILES_AND_DIRS:
            mainPane.add(createContextPanel(dirPanel, true));
            mainPane.add(createContextPanel(filePanel, false));
            break;
        case TYPE_FILES_ONLY:
            mainPane.add(createContextPanel(filePanel, false));
            break;
        }

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        mainPane.add(buttonPanel);

        setContentPane(mainPane);
        pack();
        setLocationRelativeTo(parent);
    }

    protected JPanel createContextPanel(final JPanel p, final boolean dir) {
        JPanel cp = new JPanel();

        String label = "";

        label = (dir) ? LanguageBundle.getInstance().getMessage(
                "dialog.options.perm.dir") : LanguageBundle.getInstance()
                .getMessage("dialog.options.perm.file");

        cp.add(p);
        cp.setBorder(BorderFactory.createTitledBorder(label));

        return cp;
    }

    public int getUserOption() {
        return user_option;
    }

    public int getFilePermissions() {
        return filePanel.getPermissions();
    }

    public int getDirPermissions() {
        return dirPanel.getPermissions();
    }

    public void setFilePermissions(final int permissions) {
        if (permissions != -1) {
            filePanel.setPermissions(permissions);
        }
    }

    public void setDirPermissions(final int permissions) {
        if (permissions != -1) {
            dirPanel.setPermissions(permissions);
        }
    }

    public boolean isRecursiveEnabled() {
        return dirPanel.recursive();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            user_option = OPTION_CANCEL;
            setVisible(false);
        } else if (e.getSource() == okButton) {
            user_option = OPTION_PROCEED;
            setVisible(false);
        }

    }

}
