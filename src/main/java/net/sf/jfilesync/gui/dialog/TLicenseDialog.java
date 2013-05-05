/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TLicenseDialog.java,v 1.6 2006/08/29 19:58:19 hunold Exp $
 */

package net.sf.jfilesync.gui.dialog;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.ConfigDefinitions;

/**
 *  very simple dialog that shows the license that was applied to capivara
 *  @author sahu
 */

public class TLicenseDialog extends JDialog {

  private static final long serialVersionUID = -4775965322159324933L;

  public TLicenseDialog(JDialog owner) {
    super(owner, LanguageBundle.getInstance().getMessage("label.license"), true);
    String license = "";
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          getClass().getResourceAsStream(
              "/net/sf/jfilesync/" + ConfigDefinitions.LICENSE_FILE)));
      String line;
      while ((line = reader.readLine()) != null) {
        license = license.concat(line + "\n");
      }
    } catch (Exception ex) {
      license = LanguageBundle.getInstance()
          .getMessage("error.reading_license");
      System.out.println(ex.getMessage());
    }
    JTextArea textarea = new JTextArea(license);
    textarea.setEditable(false);
    textarea.setFont(new Font("Monospaced", Font.PLAIN, 11));

    JScrollPane scrollPane = new JScrollPane(textarea);
    scrollPane.setPreferredSize(new Dimension(512, 600));
    scrollPane.setMaximumSize(new Dimension(512, 600));

    JPanel buttonPane = new JPanel();
    JButton closeButton = new JButton(LanguageBundle.getInstance().getMessage(
        "label.close"));
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        TLicenseDialog.this.setVisible(false);
        TLicenseDialog.this.dispose();
      }
    });
    buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
    buttonPane.add(closeButton);

    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    this.getContentPane().add(buttonPane, BorderLayout.SOUTH);

    this.pack();
    this.setLocationRelativeTo(owner);
  }
}
