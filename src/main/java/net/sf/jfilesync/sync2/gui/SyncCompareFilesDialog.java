/*
 * capivara - Java File Synchronization
 *
 * Created on 26-Jun-2005
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
 * $Id: SyncCompareFilesDialog.java,v 1.5 2006/08/09 22:18:39 hunold Exp $
 */
package net.sf.jfilesync.sync2.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.events.ProgressMessage;
import net.sf.jfilesync.engine.worker.events.SyncCompareProgressMessage;
import net.sf.jfilesync.gui.dialog.AbstractWorkerDialog;
import net.sf.jfilesync.gui.swing.JProgressSetter;
import net.sf.jfilesync.gui.swing.JTextComponentSetter;
import net.sf.jfilesync.prop.LanguageBundle;

public class SyncCompareFilesDialog extends AbstractWorkerDialog
{

  private final JFrame parent;
  
  private final JTextField fileField1 = new JTextField(40); 
  private final JTextField fileField2 = new JTextField(40); 
  private JProgressBar progressBar = new JProgressBar(0,100);
  private JButton cancelButton = new JButton(
      LanguageBundle.getInstance().getMessage("label.cancel"));

  public SyncCompareFilesDialog(final JFrame parent) {
    super(parent, true);
    this.parent = parent;
    initUI();
  }
  
  private void initUI() {
    
    final String title = LanguageBundle.getInstance()
    .getMessage("message.compare_data");
    setTitle(title);

    JPanel msgPanel = new JPanel();
    msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
    
    JLabel lab1 = new JLabel(LanguageBundle.getInstance()
        .getMessage("sync.compare.left"));
    JLabel lab2 = new JLabel(LanguageBundle.getInstance()
        .getMessage("sync.compare.right"));

    final JPanel msg1Panel = createMsgPanel(fileField1, lab1);
    final JPanel msg2Panel = createMsgPanel(fileField2, lab2);
    msgPanel.add(msg1Panel);
    msgPanel.add(msg2Panel);
    msgPanel.add(progressBar);
    
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fireCancelEvent(new TAbstractDialogEvent(this));
        cancelButton.setEnabled(false);
      }
    });

    JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPane.add(cancelButton);
  
    setResizable(false);
    
    // overcome 1.4 compliance probs (setPreferredSize)
    JPanel content = new JPanel();
    //content.setPreferredSize(new Dimension(350,150));
    setContentPane(content);
  
    content.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEtchedBorder(),
        BorderFactory.createEmptyBorder(2, 5, 2, 5) ));
    
    content.setLayout(new BorderLayout());
    content.add(msgPanel, BorderLayout.CENTER);
    content.add(buttonPane, BorderLayout.SOUTH);
  }
  
  private JPanel createMsgPanel(
      final JTextField field,
      final JLabel label) {
    final JPanel msgPanel = new JPanel();
  
    BoxLayout layout = new BoxLayout(msgPanel, BoxLayout.X_AXIS);
    msgPanel.setLayout(layout);

    label.setPreferredSize(new Dimension(70, 20));
    
    field.setEditable(false);
    
    msgPanel.add(label);
    msgPanel.add(Box.createHorizontalStrut(10));
    msgPanel.add(field);
    
    msgPanel.setBorder(BorderFactory.createEtchedBorder());
    
    return msgPanel;
  }
  
  public void setMessage1(final String msg) {
    SwingUtilities.invokeLater(
        new JTextComponentSetter(fileField1, msg));
  }
  
  public void setMessage2(final String msg) {
    SwingUtilities.invokeLater(
        new JTextComponentSetter(fileField2, msg));
  }

  public void setProgress(final int progress) {
    SwingUtilities.invokeLater(
        new JProgressSetter(progressBar, progress));
  }
  
  
  public void displayWorkerData(GWorkerEvent data) {
    if( data instanceof SyncCompareProgressMessage ) {
      SyncCompareProgressMessage msg = (SyncCompareProgressMessage)data;
      setMessage1(msg.getFileName1());
      setMessage2(msg.getFileName2());
      setProgress(msg.getProgress());
    }
    else if( data instanceof ProgressMessage ) {
      ProgressMessage msg = (ProgressMessage)data;
      setProgress(msg.getValue());
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
