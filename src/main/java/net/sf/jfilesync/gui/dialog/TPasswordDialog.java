
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
 * $Id: TPasswordDialog.java,v 1.7 2006/08/09 22:18:40 hunold Exp $
 */

package net.sf.jfilesync.gui.dialog;

import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jfilesync.*;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;



public class TPasswordDialog extends JDialog
    implements ActionListener, ItemListener
{

  private static final long serialVersionUID = 7557637083627198903L;

  private final JFrame parent; //needed for dialogs
  private JPasswordField passwordField;
  private JTextField remarks ;
  private JButton okButton, detailsButton ;
  private JPanel detailsPanel ;

  //global property from MainWin.config.getProgramSettings().getAutoConfirmPassWD
  private JCheckBox askMeCheckBox;

  static Logger logger = Logger.getLogger(TPasswordDialog.class.getName());

  public TPasswordDialog(final JFrame parentFrame) {
    super(parentFrame, true);
    this.parent = parentFrame;

    passwordField = new JPasswordField(15);
    passwordField.setEchoChar('*');
    passwordField.setActionCommand("ok");
    passwordField.addActionListener(this);

    remarks = new JTextField(15) ;
    remarks.setEditable(false);

    JLabel label = new JLabel(LanguageBundle.getInstance().getMessage("dialog.pwd.pwd_question"));
    label.setLabelFor(passwordField);

//    JPanel textPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
    JPanel textPane = new JPanel() ;
    textPane.setLayout( new BoxLayout(textPane, BoxLayout.X_AXIS));
    textPane.add(Box.createHorizontalStrut(5));
    textPane.add(label);
    textPane.add(Box.createHorizontalStrut(5));
    textPane.add(passwordField);
    textPane.add(Box.createHorizontalStrut(5));
//    textPane.add(remarks) ;

    JPanel centerPane = new JPanel() ;
    centerPane.setLayout( new BoxLayout( centerPane, BoxLayout.PAGE_AXIS) );
    centerPane.add(textPane) ;
    centerPane.add( Box.createVerticalStrut(5)) ;

    // Don't ask CheckBox
    boolean askForConfirmation = true;
    try {
      askForConfirmation = MainWin.config.getProgramSettings()
      .getBooleanOption(TProgramSettings.OPTION_CONFIRM_PASSWORD);
    }
    catch(SettingsTypeException sex) {
      logger.log(Level.WARNING, sex.getMessage());
    }
    
    askMeCheckBox = new JCheckBox(
        LanguageBundle.getInstance().getMessage("dialog.pwd.autoconfirm"),
        askForConfirmation);
    askMeCheckBox.addItemListener(this);

    centerPane.add(askMeCheckBox) ;

    JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
//    JButton okButton = new JButton("OK");
    okButton = new JButton("OK");
    okButton.setActionCommand("ok");
    okButton.addActionListener(this);
    buttonPane.add(okButton);

    detailsButton = new JButton(LanguageBundle.getInstance().getMessage("dialog.pwd.more")) ;
    detailsButton.setActionCommand("details");
    detailsButton.addActionListener(this);
    buttonPane.add(detailsButton);


    JPanel mainPanel = new JPanel(new BorderLayout() ) ;
//    mainPanel.add(textPane, BorderLayout.CENTER);
    mainPanel.add( new JPanel(), BorderLayout.NORTH);
    mainPanel.add(centerPane, BorderLayout.CENTER);
    mainPanel.add(buttonPane, BorderLayout.SOUTH);

    detailsPanel = new JPanel(new BorderLayout() ) ;
    detailsPanel.setVisible(false) ;
    detailsPanel.add(new JLabel(LanguageBundle.getInstance().getMessage("dialog.pwd.client_msg")),
                     BorderLayout.BEFORE_FIRST_LINE ) ;
    detailsPanel.add(remarks) ;

    this.getContentPane().add(mainPanel, BorderLayout.CENTER) ;
    this.getContentPane().add(detailsPanel, BorderLayout.SOUTH) ;

    this.pack();
    this.setLocationRelativeTo(parent);
  }


  // ------------------------------------------------------------------------

    public void itemStateChanged(ItemEvent e)
    { 
      //  only one CheckBox!!!!
      try {
        MainWin.config.getProgramSettings()
        .setProgramOption(TProgramSettings.OPTION_CONFIRM_PASSWORD,
            new Boolean(askMeCheckBox.isSelected()).toString() );
      }
      catch(SettingsTypeException sex) {
        logger.log(Level.WARNING, sex.getMessage());
      }
    }

// ------------------------------------------------------------------------


  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals("ok"))
    {
      this.setVisible(false);
    } else
    if (cmd.equals("details"))
    {
      if (detailsPanel.isVisible())
      {
        detailsPanel.setVisible(false);
        detailsButton.setText(LanguageBundle.getInstance().getMessage("dialog.pwd.more"));
      }
      else
      {
        detailsPanel.setVisible(true);
        detailsButton.setText(LanguageBundle.getInstance().getMessage("dialog.pwd.less"));
      }

      this.pack();
    }
  }

// ------------------------------------------------------------------------

  public String getPassword() {
    return new String(passwordField.getPassword());
  }

  // setting up a default passwort
  public void setDefaultPassword(String pwd)
  {
     if (pwd != null)
     {
       if (pwd.length() > 0)
       {
         passwordField.setText(pwd);
         passwordField.setCaretPosition(pwd.length());
       }
     }
  }

  public void setRemark(String text)
  {
    remarks.setText(text);
  }

}

