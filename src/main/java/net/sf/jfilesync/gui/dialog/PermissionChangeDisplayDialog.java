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
 * $Id: PermissionChangeDisplayDialog.java,v 1.7 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.gui.dialog;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.events.DisplayFileNameMessage;
import net.sf.jfilesync.engine.worker.events.ProgressMessage;
import net.sf.jfilesync.gui.components.DisplayFileNamePanel;
import net.sf.jfilesync.gui.components.ProgressPanel;
import net.sf.jfilesync.prop.LanguageBundle;

public class PermissionChangeDisplayDialog extends AbstractWorkerDialog {

    private static final long serialVersionUID = 1L;
    private final Component parent;
    private DisplayFileNamePanel fnPanel = new DisplayFileNamePanel(40);
    private ProgressPanel progressPanel = new ProgressPanel();
    private JButton cancelButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.cancel"));

    public PermissionChangeDisplayDialog(Component parent) {
        this.parent = parent;
        setTitle(LanguageBundle.getInstance().getMessage(
                "dialog.change_perm.title"));
        setModal(true);
        setResizable(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireCancelEvent(new TAbstractDialogEvent(this));
                cancelButton.setEnabled(false);
            }
        });

        JPanel mainPane = new JPanel();
        BoxLayout layout = new BoxLayout(mainPane, BoxLayout.Y_AXIS);
        mainPane.setLayout(layout);

        mainPane.add(fnPanel);
        mainPane.add(progressPanel);
        mainPane.add(buttonPanel);
        setContentPane(mainPane);
        pack();
    }

    public void setProgress(final int progress) {
        progressPanel.setProgress(progress);
    }

    public void setFileName(final String fn) {
        fnPanel.setFileName(fn);
    }

    public void displayWorkerData(GWorkerEvent data) {

        if (data instanceof ProgressMessage) {
            setProgress(((ProgressMessage) data).getValue());
        } else if (data instanceof DisplayFileNameMessage) {
            String fn = ((DisplayFileNameMessage) data).getFileName();
            setFileName(fn);
        }

    }

    public synchronized void enableGUIElement(boolean enable) {
        if (!enable) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setVisible(false);
                    dispose();
                }
            });
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    pack();
                    setLocationRelativeTo(parent);
                    setVisible(true);
                }
            });
        }
    }

}
