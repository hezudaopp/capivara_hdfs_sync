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
 * $Id: TFileRenameDialog.java,v 1.5 2006/08/09 22:18:40 hunold Exp $
 */

package net.sf.jfilesync.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.prop.LanguageBundle;

public class TFileRenameDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = 3802103708331302059L;
  
  private String filename;
  private JTextField fileField;
  private int back = CANCEL;

  public static final int OK = 0; 
  public static final int CANCEL = 1;

  public TFileRenameDialog(JFrame parent, String fname) {
    super(parent, LanguageBundle.getInstance().getMessage(
        "menu.option.rename"), true);
    
    if( fname == null ) {
      throw new NullPointerException("fname");
    }
    
    this.filename = fname;

    final JPanel mainPanel = new JPanel();
    fileField = new JTextField(fname, 30);
    fileField.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
          TFileRenameDialog.this.actionPerformed(new ActionEvent(this,
              ActionEvent.ACTION_PERFORMED, "ok"));
        } else if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
          TFileRenameDialog.this.actionPerformed(new ActionEvent(this,
              ActionEvent.ACTION_PERFORMED, "cancel"));
        }
      }
    });
    mainPanel.add(fileField);
    
    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    final JButton okButton = new JButton("OK");
    okButton.setActionCommand("ok");
    okButton.addActionListener(this);
    final JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(this);
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    getContentPane().add(mainPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    pack();
  }

  public int showMe() {
    this.setLocationRelativeTo(this.getParent());
    this.setVisible(true);
    return back;
  }

  public void actionPerformed(ActionEvent e) {
    String c = e.getActionCommand();
    if( c.equals("ok") ) {
      filename = fileField.getText();
      if (checkName()) {
        // close
        back = OK;
        TFileRenameDialog.this.setVisible(false);
      } else {
        // TODO display proper error message 
        TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL);
      }
    }
    else if( c.equals("cancel") ) {
      back = CANCEL;
      // just hide and dispose
      TFileRenameDialog.this.setVisible(false);
    }
  }

  public String getFileName() {
    return filename;
  }

  private boolean checkName() {
    if( filename.equals("") ) {
      return false;
    }
    return true;
  }
}
