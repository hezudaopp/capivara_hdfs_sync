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
 * $Id: TStandardMessageDialog.java,v 1.7 2006/08/09 22:18:40 hunold Exp $
 */

package net.sf.jfilesync.gui.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.events.TDisplayMessageEvent;
import net.sf.jfilesync.prop.LanguageBundle;


public class TStandardMessageDialog extends TAbstractStandardDialog {

  private static final long serialVersionUID = -2403722497264719L;

  private int maxLength = 50;
  private String msg = "";
  private JLabel msgLabel;
  private JButton cancelButton = 
    new JButton(LanguageBundle.getInstance().getMessage("label.cancel"));

  public TStandardMessageDialog(Component parent, int type) {
    super(parent, type);
    init();
  }
  
  public TStandardMessageDialog(Component parent, int type,	int maxlength) {
    super(parent, type);
    this.maxLength = maxlength;
    init();
  }
  
  private void init() {

    setTitle(getMsgByType());
    
    msgLabel = new JLabel("", JLabel.LEFT);
    msgLabel.setMaximumSize(new Dimension(300, 30));
    msgLabel.setMinimumSize(new Dimension(300, 30));

    JPanel msgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    msgPanel.add(msgLabel);

    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fireCancelEvent(new TAbstractDialogEvent(this));
        cancelButton.setEnabled(false);
      }
    });

    JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPane.add(cancelButton);

//    this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
  
    setModal(true);
    setResizable(false);
    
    // overcome 1.4 compliance probs
    // setPreferredSize for JDialog just since 1.5
    JPanel content = new JPanel();
    content.setPreferredSize(new Dimension(350,150));
    setContentPane(content);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(msgPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
  }
  
  public void setMessage(String msg) {
    this.msg = msg;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        msgLabel.setText(
            trimMessage(TStandardMessageDialog.this.msg, maxLength));    
      }
    });
  }
  
  public void displayWorkerData(GWorkerEvent e) {
    if( e instanceof TDisplayMessageEvent ) {
      String pmsg = ((TDisplayMessageEvent)e).getMessage();
      setMessage(pmsg);
    }
  }
  
  public void enableGUIElement(boolean enable) {
    if( ! enable ) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setVisible(false);
          dispose();
        }
      });
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          pack();
          setLocationRelativeTo(parent);
          setVisible(true);
        }
      });
    }
  }
  
}
