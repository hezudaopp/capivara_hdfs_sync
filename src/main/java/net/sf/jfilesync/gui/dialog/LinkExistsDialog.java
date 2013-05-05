/*
 * capivara - Java File Synchronization
 *
 * Created on 18-Aug-2005
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
 * $Id: LinkExistsDialog.java,v 1.3 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.gui.dialog;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.sf.jfilesync.prop.LanguageBundle;

public class LinkExistsDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final String fileName;

    public final static int OPTION_SKIP = 0;
    public final static int OPTION_DELETE = 1;
    public final static int OPTION_CANCEL = 2;

    private int userChoice = OPTION_CANCEL;

    private JButton skipButton = new JButton(LanguageBundle.getInstance()
            .getMessage("copy.option.skip"));
    private JButton cancelButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.cancel"));
    private JButton deleteButton = new JButton(LanguageBundle.getInstance()
            .getMessage("copy.option.delete_link"));

    public LinkExistsDialog(final Frame owner, final String fileName) {
        super(owner, true);
        setTitle(LanguageBundle.getInstance().getMessage(
                "dialog.options.link_exists.title"));
        this.fileName = fileName;
        initUI();
    }

    private void initUI() {

        String msg = LanguageBundle.getInstance().getMessage(
                "dialog.options.link_exists.message");
        // substitute label placeholdes %l and %d

        msg = msg.replaceFirst("%l", "'" + fileName + "'");

        JLabel msgLabel = new JLabel(msg);
        JPanel msgPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        msgPane.add(msgLabel);
        msgPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPane.add(deleteButton);
        buttonPane.add(skipButton);
        buttonPane.add(cancelButton);
        registerActionCommand(deleteButton, "delete");
        registerActionCommand(skipButton, "skip");
        registerActionCommand(cancelButton, "cancel");

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        msgPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPane.add(msgPane);
        contentPane.add(buttonPane);

        setContentPane(contentPane);

        pack();

        setLocationRelativeTo(getOwner());
    }

    private void registerActionCommand(JButton b, String command) {
        b.setActionCommand(command);
        b.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("delete")) {
            userChoice = OPTION_DELETE;
            setVisible(false);
        } else if (cmd.equals("cancel")) {
            userChoice = OPTION_CANCEL;
            setVisible(false);
        } else if (cmd.equals("skip")) {
            userChoice = OPTION_SKIP;
            setVisible(false);
        }
    }

    public int getChosenOption() {
        return userChoice;
    }

}
