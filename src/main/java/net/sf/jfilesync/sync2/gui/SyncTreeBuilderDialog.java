/*
 * capivara - Java File Synchronization
 *
 * Created on 07-Mar-2006
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id$
 */
package net.sf.jfilesync.sync2.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.events.CollectStatusMessage;
import net.sf.jfilesync.gui.components.CollectFilesPanel;
import net.sf.jfilesync.gui.components.ConnectionDetailsComponent2;
import net.sf.jfilesync.gui.dialog.AbstractWorkerDialog;
import net.sf.jfilesync.prop.LanguageBundle;

public class SyncTreeBuilderDialog extends AbstractWorkerDialog {

  private final Component parent;
  private final ConnectionDetails details1;
  private final ConnectionDetails details2;
  
  private JButton cancelButton = new JButton(LanguageBundle.getInstance()
      .getMessage("label.cancel"));

  private CollectFilesPanel statsPanel1 = new CollectFilesPanel();
  private CollectFilesPanel statsPanel2 = new CollectFilesPanel();
  
  public SyncTreeBuilderDialog(final Component parent,
      final ConnectionDetails details1, final ConnectionDetails details2) {
    this.parent = parent;
    this.details1 = details1;
    this.details2 = details2;
    initUI();
  }
  
  private void initUI() {
    
    setTitle(LanguageBundle.getInstance().getMessage("dialog.collect.title"));
    
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fireCancelEvent(new TAbstractDialogEvent(this));
        cancelButton.setEnabled(false);
      }
    });

    JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPane.add(cancelButton);

    setModal(true);
    setResizable(false);
    
    // overcome 1.4 compliance probs
    // setPreferredSize for JDialog just since 1.5
    JPanel content = new JPanel();
    setContentPane(content);
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    
    ConnectionDetailsComponent2 cdc1 = new ConnectionDetailsComponent2(details1);
    ConnectionDetailsComponent2 cdc2 = new ConnectionDetailsComponent2(details2);

    final JPanel con1Panel = createConnectionPanel(cdc1, statsPanel1);
    final JPanel con2Panel = createConnectionPanel(cdc2, statsPanel2);
    
    JPanel bothConPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    bothConPanel.add(con1Panel);
    bothConPanel.add(con2Panel);
        
    content.add(bothConPanel);
    content.add(buttonPane);
    
    pack();    
  }

  private JPanel createConnectionPanel(final JComponent detailsComponent, 
      final CollectFilesPanel statsPane) {
    JPanel pane = new JPanel();
    
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    pane.add(detailsComponent);
    pane.add(statsPane);
    
    return pane;
  }

  /**
   * instead we use displayWorkerDataForConnectionX
   */
  
  public void displayWorkerData(GWorkerEvent data) {
  }
  
  public void displayWorkerDataForConnection1(GWorkerEvent e) {
    if( e instanceof CollectStatusMessage ) {
      CollectStatusMessage csm = (CollectStatusMessage)e;
      statsPanel1.setMessage(csm);
    }    
  }
  
  public void displayWorkerDataForConnection2(GWorkerEvent e) {
    if( e instanceof CollectStatusMessage ) {
      CollectStatusMessage csm = (CollectStatusMessage)e;
      statsPanel2.setMessage(csm);
    }    
  }

  public void enableGUIElement(boolean enable) {
    if( enable ) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          pack();
          setLocationRelativeTo(parent);
          setVisible(true);
        }
      });
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setVisible(false);
          dispose();
        }
      });
    }    
  }

}
