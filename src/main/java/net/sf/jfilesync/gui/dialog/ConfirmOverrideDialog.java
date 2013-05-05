/*
 * capivara - Java File Synchronization
 *
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
 * $Id: ConfirmOverrideDialog.java,v 1.12 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.gui.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.sf.jfilesync.prop.LanguageBundle;

public class ConfirmOverrideDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = 3617574894770927927L;

  public static final int OPTION_OVERRIDE     = 0,
                          OPTION_OVERRIDE_ALL = 1,
                          OPTION_SKIP         = 2,
                          OPTION_SKIP_ALL     = 3,
                          OPTION_CANCEL       = 4;
  
  private int userChoice = OPTION_CANCEL;
  
  private String fileName;
  private boolean isDirectory;

  public ConfirmOverrideDialog(Frame owner, String fileName, boolean isDirectory) {
    super(owner, true);
    this.fileName = fileName;
    this.isDirectory = isDirectory;
    initUI();
  }

  public ConfirmOverrideDialog(String fileName, boolean isDirectory) {
    this(null, fileName, isDirectory);
  }
  
  private void initUI() {
    
    String fileExists = "";
    if (isDirectory) {
      setTitle(LanguageBundle.getInstance().getMessage(
          "copy.override.dir.title"));
      fileExists = LanguageBundle.getInstance().getMessage(
          "copy.question.direxists")
          + " \"" + fileName + "\"";
    } else {
      setTitle(LanguageBundle.getInstance().getMessage(
          "copy.override.file.title"));
      fileExists = LanguageBundle.getInstance().getMessage(
          "copy.question.fileexists")
          + " \"" + fileName + "\"";
    }
    
    String overrideText = LanguageBundle.getInstance().getMessage(
        "copy.question.override")
        + "?";
    
    JLabel lab1 = new JLabel(fileExists);
    lab1.setBorder(null);
    JLabel lab2 = new JLabel(overrideText);
    lab2.setBorder(null);
    
    JPanel msgPanel = new JPanel();
    msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
    msgPanel.add(Box.createVerticalStrut(10));
    msgPanel.add(lab1);
    msgPanel.add(Box.createVerticalStrut(20));
    msgPanel.add(lab2);
    //msgPanel.setBorder(BorderFactory.createEmptyBorder(10,20,0,0));

    JButton overrideB = new JButton(LanguageBundle.getInstance().getMessage(
        "copy.option.override"));
    overrideB.setActionCommand("override");

    JButton overrideAllB = new JButton(LanguageBundle.getInstance().getMessage(
        "copy.option.override_all"));
    overrideAllB.setActionCommand("override_all");

    JButton skipB = new JButton(LanguageBundle.getInstance().getMessage(
        "copy.option.skip"));
    skipB.setActionCommand("skip");

    JButton skipAllB = new JButton(LanguageBundle.getInstance().getMessage(
        "copy.option.skip_all"));
    skipAllB.setActionCommand("skip_all");

    JButton cancelB = new JButton(LanguageBundle.getInstance().getMessage(
        "label.cancel"));
    cancelB.setActionCommand("cancel");
    
    JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPane.add(overrideB);
    buttonPane.add(overrideAllB);
    buttonPane.add(skipB);
    buttonPane.add(skipAllB);
    buttonPane.add(cancelB);
    
    reconfButton(overrideB);
    reconfButton(overrideAllB);
    reconfButton(skipB);
    reconfButton(skipAllB);
    reconfButton(cancelB);
    
    JPanel leftDummy = new JPanel();
    leftDummy.setPreferredSize(new Dimension(20,20));
    
    getContentPane().add(leftDummy, BorderLayout.WEST);
    getContentPane().add(msgPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    
    setResizable(false);
    //since 1.5 setMinimumSize(new Dimension(400,150));
    pack();
    Window w = getOwner();
    if( w != null ) {
      setLocationRelativeTo(w);
    }
  }
  
  protected void reconfButton(JButton b) {
    //Font oF = b.getFont();
    //Font nF = new Font(oF.getName(), oF.getStyle(), 9);
    //b.setFont(nF);
    //b.setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
    b.addActionListener(this);
  }

  public void actionPerformed(ActionEvent e) {

    final String cmd = e.getActionCommand();
    if (cmd.equals("override")) {
      userChoice = OPTION_OVERRIDE;
    } else if (cmd.equals("override_all")) {
      userChoice = OPTION_OVERRIDE_ALL;
    } else if (cmd.equals("skip")) {
      userChoice = OPTION_SKIP;
    } else if (cmd.equals("skip_all")) {
      userChoice = OPTION_SKIP_ALL;
    } else if (cmd.equals("cancel")) {
      userChoice = OPTION_CANCEL;
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setVisible(false);
      }
    });

  }
  
  public int getUserChoice() {
    return userChoice;
  }

}
