/*
 * capivara - Java File Synchronization
 *
 * Created on 21-Sep-2005
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
 * $Id: CapivaraCloseDialog.java,v 1.3 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.gui.dialog;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.sf.jfilesync.prop.LanguageBundle;

public class CapivaraCloseDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    public final static int OK_OPTION = 0;
    public final static int CANCEL_OPTION = 1;

    private int option = CANCEL_OPTION;
    private JCheckBox promptCheckBox = new JCheckBox(LanguageBundle
            .getInstance().getMessage("dialog.confirm.exit_always"));

    public CapivaraCloseDialog(JFrame owner) {
        super(owner, LanguageBundle.getInstance().getMessage("menu.exit"), true);
        initUI();
    }

    private void initUI() {

        JPanel msgPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel msgLabel = new JLabel(LanguageBundle.getInstance().getMessage(
                "dialog.confirm.exit"));
        msgPane.add(msgLabel);

        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkPanel.add(promptCheckBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton(LanguageBundle.getInstance().getMessage(
                "label.ok"));
        okButton.addActionListener(this);
        okButton.setActionCommand("ok");
        JButton cancelButton = new JButton(LanguageBundle.getInstance()
                .getMessage("label.cancel"));
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("cancel");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(msgPane);
        mainPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        mainPanel.add(checkPanel);
        mainPanel.add(buttonPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(getOwner());
    }

    public boolean isNeverPromptSelected() {
        return promptCheckBox.isSelected();
    }

    public int getUserOption() {
        return option;
    }

    public void actionPerformed(ActionEvent e) {
        final String cmd = e.getActionCommand();
        if (cmd.equals("ok")) {
            option = OK_OPTION;
            setVisible(false);
        } else if (cmd.equals("cancel")) {
            option = CANCEL_OPTION;
            setVisible(false);
        }
    }

}
