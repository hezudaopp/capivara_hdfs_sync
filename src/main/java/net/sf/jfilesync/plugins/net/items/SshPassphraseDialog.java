/*
 * capivara - Java File Synchronization
 *
 * Created on 05-May-2005
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
 * $Id: SshPassphraseDialog.java,v 1.5 2006/08/09 22:18:39 hunold Exp $
 */
package net.sf.jfilesync.plugins.net.items;

import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import net.sf.jfilesync.prop.LanguageBundle;

public class SshPassphraseDialog extends JDialog implements KeyListener {

    private static final long serialVersionUID = 1L;
    private JPasswordField passwdF = new JPasswordField(15);

    public SshPassphraseDialog(JFrame owner) {
        super(owner, LanguageBundle.getInstance().getMessage(
                "sshplug.passphrase.title"), true);

        passwdF.addKeyListener(this);

        JPanel passphraseP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel passphraseL = new JLabel(LanguageBundle.getInstance()
                .getMessage("sshplug.passphrase.enter"));
        passphraseP.add(passphraseL);
        passphraseP.add(passwdF);

        getContentPane().add(passphraseP);
        pack();
        setLocationRelativeTo(owner);
    }

    public String getPassword() {
        return new String(passwdF.getPassword());
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ENTER) {
            setVisible(false);
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}
