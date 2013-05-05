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

package net.sf.jfilesync.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.TAbstractGUIElementListener;
import net.sf.jfilesync.gui.icons.TImageIconProvider;

public class TStatusLine extends JPanel implements ActionListener {

    private static final long serialVersionUID = 3258408439360204854L;

    private final Font textFont = new Font("Dialog", Font.PLAIN, 11);
    private final JTextField statusField = new JTextField(8);
    private final JTextField hostField = new JTextField();
    private final JButton cancelButton = new JButton(TImageIconProvider
            .getInstance().getImageIcon(TImageIconProvider.CANCEL_WORKER_ICON));

    private final java.util.List<TAbstractGUIElementListener> listeners = new ArrayList<TAbstractGUIElementListener>();

    public TStatusLine() {
        initUI();
    }

    protected void initUI() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        
        setLayout(layout);
        
        statusField.setFont(textFont);
        statusField.setRequestFocusEnabled(false);
        statusField.setEditable(false);
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        //constraints.weightx = 0.1;
        add(statusField, constraints);

        hostField.setFont(textFont);
        hostField.setRequestFocusEnabled(false);
        hostField.setEditable(false);
        hostField.setText("");

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        add(hostField, constraints);

        cancelButton.setMaximumSize(new Dimension(20, 20));
        cancelButton.setMinimumSize(new Dimension(20, 20));
        cancelButton.setPreferredSize(new Dimension(20, 20));
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(this);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.PAGE_END;
        add(cancelButton, constraints);
        
        //setBorder(BorderFactory.createEtchedBorder());

    }

    public void setText(String msgStr) {
        statusField.setText(msgStr);
    }

    public void setTextColor(Color c) {
        statusField.setForeground(c);
    }

    public void setHostTextColor(Color c) {
        hostField.setForeground(c);
    }

    public void setHostString(String hStr) {
        hostField.setText(hStr);
    }

    public void setCancelEnabled(boolean enabled) {
        cancelButton.setEnabled(enabled);
    }

    public synchronized void addTAbstractGUIElementListener(
            final TAbstractGUIElementListener l) {
        listeners.add(l);
    }

    public synchronized void fireTAbstractDialogEvent(
            final TAbstractDialogEvent e) {
        for (final TAbstractGUIElementListener l : listeners) {
            l.cancelClicked(e);
        }
    }

    public void actionPerformed(ActionEvent e) {
        final Object src = e.getSource();
        if (src.equals(cancelButton)) {
            fireTAbstractDialogEvent(new TAbstractDialogEvent(this));
        }
    }

}
