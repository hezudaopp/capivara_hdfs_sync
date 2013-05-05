/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TMkdirDialog.java,v 1.4 2006/08/09 22:18:40 hunold Exp $
 */

package net.sf.jfilesync.gui;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

import net.sf.jfilesync.prop.LanguageBundle;


public class TMkdirDialog extends JDialog
implements PropertyChangeListener
{

  private static final long serialVersionUID = 3978704017108971573L;

  public static final int OK = 0, CANCEL = 1;

  private String
      btn1Label = LanguageBundle.getInstance().getMessage("label.clear"),
      btn2Label = LanguageBundle.getInstance().getMessage("label.ok"),
      btn3Label = LanguageBundle.getInstance().getMessage("label.cancel");
  private String message =
      LanguageBundle.getInstance().getMessage("dialog.mkdir.new_directory.message");
  private String chosenDir = "";
  private JTextField inputField;
  private JOptionPane optionPane;
  private int closingType = CANCEL;

  public TMkdirDialog(JFrame owner) {
    super(owner,
        LanguageBundle.getInstance().getMessage("dialog.mkdir.new_directory.label"),
        true);
    inputField = new JTextField(20);
    Object messages[] = {message, inputField};
    Object options[] = {btn1Label, btn2Label, btn3Label};
    optionPane = new JOptionPane(messages, JOptionPane.QUESTION_MESSAGE,
                                 JOptionPane.YES_NO_CANCEL_OPTION,
                                 null, options, options[1]);
    this.setContentPane(optionPane);
    optionPane.addPropertyChangeListener(this);

    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        closingType = CANCEL;
        TMkdirDialog.this.setVisible(false);
      }
    });
    this.pack();
  }


  public String getDirUserEntered() {
    return chosenDir;
  }

  public void propertyChange(PropertyChangeEvent e) {

    Object value = optionPane.getValue();
    if( value.equals(btn1Label) ) {
      // clear
      inputField.setText("");
    }
    else if( value.equals(btn2Label) ) {
      // ok
      chosenDir = inputField.getText();
      closingType = OK;
      this.setVisible(false);
    }
    else if( value.equals(btn3Label) ) {
      // cancel
      closingType = CANCEL;
      this.setVisible(false);
    }
  }

  public int getClosingType() {
    return closingType;
  }

}
