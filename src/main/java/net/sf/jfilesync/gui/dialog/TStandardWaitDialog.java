
/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004  Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TStandardWaitDialog.java,v 1.6 2006/08/09 22:18:40 hunold Exp $
 */

package net.sf.jfilesync.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.TAbstractWorkerGUIElement;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.util.TWindowPositioner;


public class TStandardWaitDialog extends TAbstractStandardDialog implements
    ActionListener, TAbstractWorkerGUIElement {
  
  private static final long serialVersionUID = 1L;

//  private final int MAX_MSG_LENGTH = 50; 
  
  private JButton cancelButton = 
    new JButton(LanguageBundle.getInstance().getMessage("label.cancel"));
  
  public TStandardWaitDialog(Component parent, int type) {
    super(parent, type);
    init();
  }
  
  private void init() {
    String headings = LanguageBundle.getInstance().getMessage("message.please_wait");
    setName(headings);
    String msg = getMsgByType();
    
    JLabel label = new JLabel(msg);
    getContentPane().add(label, BorderLayout.CENTER);
    
    
    cancelButton.addActionListener(this);
    cancelButton.setActionCommand("cancel");
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(cancelButton);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    setUndecorated(true);
    setModal(false);
    setSize(new Dimension(180,50));
    pack();
    setLocation(TWindowPositioner.center(parent,this));
  }
  
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if( cmd.equals("cancel") ) {
      fireCancelEvent(new TAbstractDialogEvent(this));
      cancelButton.setEnabled(false);
    }
  }
  
  public void displayWorkerData(GWorkerEvent data) {
    // there should be no response from the worker
  }
  
  public void enableGUIElement(boolean enable) {
    if( ! enable ) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setVisible(false);
          //dispose();
        }
      });
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          pack();
          setVisible(true);
        }
      });
    }
  }
  

}
