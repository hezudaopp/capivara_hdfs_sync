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
 * $Id: CopyConfirmDialog.java,v 1.12 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.gui.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.gui.FilePermissionDialog;
import net.sf.jfilesync.prop.LanguageBundle;

public class CopyConfirmDialog extends JDialog 
  implements ActionListener
{

  private static final long serialVersionUID = 3256443620654790456L;
  private final Frame parent;
  private final ConnectionDetails details1;
  private final ConnectionDetails details2;
  
  public static final int OPTION_OK             = 0,
                          OPTION_CANCEL         = 1;
                            
  private int choice = OPTION_CANCEL;
  
  public static final int TYPE_DIRECTORIES           = 0,
                          TYPE_FILES                 = 1,
                          TYPE_FILES_AND_DIRECTORIES = 2;
  
  private JCheckBox preserveMtimeBox = new JCheckBox(
      LanguageBundle.getInstance().getMessage("label.preserve_mtime"), false);
  
  private JButton okB =
    new JButton(LanguageBundle.getInstance().getMessage("label.ok"));

  private JCheckBox preservePermBox = new JCheckBox(
      LanguageBundle.getInstance().getMessage("label.preserve_perm"), false);
  private JCheckBox permBox = new JCheckBox(
      LanguageBundle.getInstance().getMessage("label.use_perm"), false );
  private JButton permButton = new JButton(
      LanguageBundle.getInstance().getMessage("label.permissions") );
  private FilePermissionDialog permDiag = new FilePermissionDialog(this);
  
  private JCheckBox followSymLinkBox = new JCheckBox(
      LanguageBundle.getInstance().getMessage("copy.option.follow_symlinks"), false );
  
  public CopyConfirmDialog(final Frame parent, 
      final ConnectionDetails details1,
      final ConnectionDetails details2, 
      final int fileNum,
      final int type) {
    super();
    this.parent = parent;
    this.details1 = details1;
    this.details2 = details2;

    String sourceMsg = "";
    if( fileNum < 2 ) {
      if( type == TYPE_FILES ) {
        sourceMsg = LanguageBundle.getInstance().getMessage("copy.confirm.action.file");
      }
      else {
        sourceMsg = LanguageBundle.getInstance().getMessage("copy.confirm.action.dir");
      }
    }
    else {
      switch(type) {
        case TYPE_DIRECTORIES:
          sourceMsg = LanguageBundle.getInstance().getMessage("copy.confirm.action.dirs");
          break;
        case TYPE_FILES:
          sourceMsg = LanguageBundle.getInstance().getMessage("copy.confirm.action.files");
          break;
        case TYPE_FILES_AND_DIRECTORIES:
          sourceMsg = LanguageBundle.getInstance().getMessage("copy.confirm.action.dirsfiles");
          break;
      }
      sourceMsg = sourceMsg.replaceFirst("%d", new Integer(fileNum).toString());
    }
    
    JLabel msgField = new JLabel();
    //msgField.setEditable(false);
    //msgField.setFocusable(false);
    msgField.setHorizontalAlignment(JTextField.LEFT);
    msgField.setBorder(null);
    msgField.setText(sourceMsg + ":");
    
    JPanel msgP = new JPanel();
    msgP.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
    msgP.add(msgField);

    init(msgP);
  }
  
  public CopyConfirmDialog(final Frame parent, 
      final ConnectionDetails details1,
      final ConnectionDetails details2,
      final String fileName, 
      final boolean isDirectory) {
    super();
    this.parent = parent;
    this.details1 = details1;
    this.details2 = details2;

    String sourceMsg = "";
    if( isDirectory ) {
      sourceMsg = LanguageBundle.getInstance().getMessage("copy.confirm.action.dir");
    }
    else {
      sourceMsg = LanguageBundle.getInstance().getMessage("copy.confirm.action.file");
    }
    sourceMsg += " \"" + fileName + "\"";

    JLabel msgField = new JLabel();
    //msgField.setEditable(false);
    //msgField.setFocusable(false);
    msgField.setHorizontalAlignment(JTextField.LEFT);
    msgField.setBorder(null);
    msgField.setText(sourceMsg + ":");
    
    JPanel msgP = new JPanel();
    msgP.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
    msgP.add(msgField);

    init(msgP);
  }
  
  protected void init(JPanel customPanel) {
    setTitle(LanguageBundle.getInstance().getMessage("label.copy"));
    setModal(true);
    setResizable(false);
           
    JPanel optionPanel = createOptionPanel();
        
    // generic panels
    JPanel conPanel1 = createConnectionPanel(details1,
        LanguageBundle.getInstance().getMessage("copy.confirm.source"));
    JPanel conPanel2 = createConnectionPanel(details2,
        LanguageBundle.getInstance().getMessage("copy.confirm.target"));
    
    JPanel sourceP = createPathPanel(details1.getCurrentPath());
    JPanel toP = createToPanel();
    JPanel targetP = createPathPanel(details2.getCurrentPath());
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    addLeftToPanel(mainPanel, customPanel);
    mainPanel.add(Box.createVerticalStrut(3));
    addLeftToPanel(mainPanel, conPanel1);
    addLeftToPanel(mainPanel, sourceP);
    mainPanel.add(Box.createVerticalStrut(3));
    addLeftToPanel(mainPanel, toP);
    mainPanel.add(Box.createVerticalStrut(3));
    addLeftToPanel(mainPanel, conPanel2);
    addLeftToPanel(mainPanel, targetP);
    addLeftToPanel(mainPanel, optionPanel);
    
    mainPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(2,5,2,5),
        BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(5,5,5,5))
        ));
    
    JPanel buttonPanel = createButtonPanel();
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(parent);
    
    okB.requestFocus();
  }

  private void addLeftToPanel(JPanel p, JComponent c) {
    c.setAlignmentX(Component.LEFT_ALIGNMENT);
    p.add(c);
  }
  
  protected JPanel createButtonPanel() {
    JPanel bP = new JPanel(new FlowLayout(FlowLayout.CENTER));
    okB = new JButton(LanguageBundle.getInstance().getMessage("label.ok"));
    okB.setActionCommand("ok");
    okB.addActionListener(this);
    JButton cancelB = new JButton(LanguageBundle.getInstance().getMessage("label.cancel"));
    cancelB.setActionCommand("cancel");
    cancelB.addActionListener(this);
    
    bP.add(okB);
    bP.add(cancelB);
    return bP;
  }
  
  protected JPanel createConnectionPanel(
      final ConnectionDetails cd, final String text) 
  {
    JPanel cP = new JPanel();
    cP.setLayout(new BoxLayout(cP, BoxLayout.X_AXIS));
    
    JLabel lab1 = new JLabel(text + ": ");
        
    JTextField conField = new JTextField(25);
    conField.setText(cd.getUser() + "@" + cd.getHost());
    conField.setEditable(false);
    conField.setFocusable(false);
    conField.setBackground(lab1.getBackground());
    
    cP.add(lab1);
    cP.add(conField);
    return cP;
  }
  
  protected JPanel createPathPanel(String path) {
    JTextField tf = new JTextField(30);
    tf.setEditable(false);
    tf.setText(path);
    tf.setBackground(Color.WHITE);
    tf.setFocusable(false);
    
    JPanel pP = new JPanel();
    pP.setLayout(new BoxLayout(pP, BoxLayout.X_AXIS));
    pP.add(tf);
    return pP;
  }
  
  protected JPanel createToPanel() {
    JPanel tP = new JPanel();
    tP.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
    JLabel lab = new JLabel();
    lab.setText(LanguageBundle.getInstance().getMessage("copy.confirm.to") +":");
    lab.setBorder(null);
    tP.add(lab);
    return tP;
  }
  
  protected JPanel createOptionPanel() {
    JPanel oP = new JPanel();
    oP.setLayout(new BoxLayout(oP, BoxLayout.Y_AXIS));
    
    preservePermBox.setEnabled(false);

    preservePermBox.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        boolean selected = preservePermBox.isSelected();
        boolean enableTargetPerm = ! selected;
        permBox.setEnabled(enableTargetPerm);
        permButton.setEnabled(enableTargetPerm);
      }
    });
        
    permButton.setActionCommand("permissions");
    permButton.addActionListener(this);
    
    JPanel permPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
    permPanel.add(permBox);
    permPanel.add(permButton);
    permBox.setEnabled(false);
    permButton.setEnabled(false);
    
    followSymLinkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    preserveMtimeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    preservePermBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    permPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    oP.add(followSymLinkBox);
    oP.add(preserveMtimeBox);
    oP.add(preservePermBox);
    oP.add(permPanel);
    
    return oP;
  }
   
  public int getUserChoice() {
    return choice;
  }
  
  public boolean getPreserveModificationTime() {
    return preserveMtimeBox.isSelected();
  }
  
  public void setPreserveMtime(boolean enable) {
    preserveMtimeBox.setSelected(enable);
  }

  public void setEnablePreservePermissions(boolean enable) {
    preservePermBox.setEnabled(enable);
  }
  
  public void setEnableTargetPermissions(boolean enable) {
    permBox.setEnabled(enable);
    permButton.setEnabled(enable);
  }
  
  public boolean getPreservePermissions() {
    return preservePermBox.isSelected();
  }
  
  public void setPreservePermissions(boolean selected) {
    preservePermBox.setSelected(selected);
  }
  
  public boolean getUseCustomTargetPermissions() {
    return permBox.isSelected();
  }
  
  public boolean getFollowSymcLinks() {
    return followSymLinkBox.isSelected();
  }
  
  public void setFollowSymLinks(boolean selected) {
    followSymLinkBox.setSelected(selected);
  }
  
  public int getFilePermissions() {
    return permDiag.getFilePermissions();
  }
  
  public int getDirectoryPermissions() {
    return permDiag.getDirectoryPermissions();
  }
  
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if( cmd.equals("ok") ) {
      choice = OPTION_OK;
      setVisible(false);
    }
    else if( cmd.equals("cancel") ) {
      choice = OPTION_CANCEL;
      setVisible(false);
    }
    else if( cmd.equals("permissions") ) {
      permDiag.setVisible(true);
    }
  }
  
}
