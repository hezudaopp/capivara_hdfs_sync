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
package net.sf.jfilesync.service.unify;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.jfilesync.prop.LanguageBundle;

public class UnifyPathRequester extends AbstractDupDialog implements
        ActionListener {

    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger
            .getLogger(UnifyPathRequester.class.getName());

    private static final String COMMAND_OK = "path.ok";
    private static final String COMMAND_CANCEL = "path.cancel";
    private static final String COMMAND_SELECT = "path.select";

    private JButton okButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.ok"));
    private JButton cancelButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.cancel"));
    private JButton selectButton = new JButton(LanguageBundle.getInstance()
            .getMessage("unify.request.choose_dir"));

    private JTextField pathField = new JTextField(25);

    public UnifyPathRequester(JDialog owner) {
        super(owner, LanguageBundle.getInstance().getMessage("label.select"));
        setModal(true);
        initUI();
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel msgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        msgPanel.add(new JLabel(LanguageBundle.getInstance().getMessage(
                "unify.request.target_directory")));

        pathField.setEditable(false);

        JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pathPanel.add(pathField);
        pathPanel.add(selectButton);
        selectButton.setActionCommand(COMMAND_SELECT);
        selectButton.addActionListener(this);

        JPanel butPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        okButton.setActionCommand(COMMAND_OK);
        okButton.addActionListener(this);
        cancelButton.setActionCommand(COMMAND_CANCEL);
        cancelButton.addActionListener(this);

        butPanel.add(okButton);
        butPanel.add(cancelButton);

        mainPanel.add(msgPanel);
        mainPanel.add(pathPanel);
        mainPanel.add(butPanel);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {

        final String command = e.getActionCommand();
        if (command.equals(COMMAND_OK)) {
            fireDupExplorerEvent(new DupMovePathEvent(this,
                    DupMovePathEvent.COMMAND_OK));
        } else if (command.equals(COMMAND_CANCEL)) {
            fireDupExplorerEvent(new DupMovePathEvent(this,
                    DupMovePathEvent.COMMAND_CANCEL));
        } else if (command.equals(COMMAND_SELECT)) {

            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                pathField.setText(fileChooser.getSelectedFile()
                        .getAbsolutePath());
            }

        } else {
            LOGGER.warning("unknown action command : " + command);
        }

    }

    public String getSelectedPath() {
        return pathField.getText();
    }

}
