/*
 * capivara - Java File Synchronization
 *
 * Created on 26-Jul-2005
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
 * $Id: SyncProgressDialog2.java,v 1.11 2006/08/09 22:18:39 hunold Exp $
 */
package net.sf.jfilesync.sync2.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.CopyProgressListener;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.gui.dialog.AbstractWorkerDialog;
import net.sf.jfilesync.gui.swing.JTextComponentSetter;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.SyncProgressMessage;
import net.sf.jfilesync.util.StatisticsFormatter;

public class SyncProgressDialog2
extends AbstractWorkerDialog
implements CopyProgressListener {

  private static final long serialVersionUID = 3256436993402352950L;

  private Component parent;
  private JProgressBar fileProgress = new JProgressBar(0, 100);
  private JProgressBar ovProgress = new JProgressBar(0, 100);
  private JTextField actionLabel = new JTextField(30);
  private JTextField fileLabel   = new JTextField(30);
  private JLabel rateField   = new JLabel("");
  private JButton cancelButton = new JButton(
      LanguageBundle.getInstance().getMessage("label.cancel"));

  public SyncProgressDialog2(JFrame parent) {
    this.parent = parent;
    init();
  }

  public SyncProgressDialog2(JDialog parent) {
    this.parent = parent;
    init();
  }


  private void init() {

    setTitle(LanguageBundle.getInstance().getMessage("sync.inprogress"));

    actionLabel.setHorizontalAlignment(SwingConstants.LEADING);
    actionLabel.setEditable(false);

    fileLabel.setHorizontalAlignment(SwingConstants.LEADING);
    fileLabel.setEditable(false);

    JLabel ovLabel = new JLabel(LanguageBundle.getInstance()
        .getMessage("dialog.transfer.overall_stats.label"));

    JLabel currentFileProgLabel = new JLabel(LanguageBundle.getInstance()
        .getMessage("dialog.transfer.current_file_progress"));

    JLabel currentActionLabel = new JLabel(LanguageBundle.getInstance()
        .getMessage("sync.perform.current_action"));

    JLabel currentFileLabel = new JLabel(LanguageBundle.getInstance()
        .getMessage("dialog.transfer.current_file"));

    JLabel curRateLabel = new JLabel(LanguageBundle.getInstance()
        .getMessage("dialog.transfer.current_transfer_rate.label")+ " : ");

    JPanel ratePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    ratePanel.add(curRateLabel);
    ratePanel.add(rateField);

    JPanel progressPanel = new JPanel();
    progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));

    progressPanel.add(makeLabelPanel(currentActionLabel));
    progressPanel.add(actionLabel);
    progressPanel.add(makeLabelPanel(currentFileLabel));
    progressPanel.add(fileLabel);
    progressPanel.add(makeLabelPanel(currentFileProgLabel));
    progressPanel.add(fileProgress);
    progressPanel.add(ratePanel);
    progressPanel.add(makeLabelPanel(ovLabel));
    progressPanel.add(ovProgress);
    progressPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fireCancelEvent(new TAbstractDialogEvent(this));
      }
    });

    JPanel mainPane = new JPanel(new BorderLayout());
    mainPane.add(progressPanel, BorderLayout.CENTER);
    mainPane.add(buttonPanel, BorderLayout.SOUTH);

    setContentPane(mainPane);
    setModal(true);
    setResizable(false);
    pack();
  }

  private JPanel makeLabelPanel(final JLabel l) {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
    p.add(l);
    return p;
  }

  public void displayWorkerData(GWorkerEvent data) {
    if( data instanceof SyncProgressMessage ) {
      SyncProgressMessage msg = (SyncProgressMessage)data;
      setActionName(msg.getActionName());
      setFileName(msg.getCurrentFileName());
      setProgress(msg.getPercentage());
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

  public void setProgress(int percentage) {
    if( percentage < 0 ) {
      percentage = 0;
    }
    else if( percentage > 100 ) {
      percentage = 100;
    }
    ovProgress.setValue(percentage);
  }

  public void setActionName(String action) {
    actionLabel.setText(action);
  }

  public void setFileName(String fileName) {
    SwingUtilities.invokeLater(new JTextComponentSetter(fileLabel, fileName));
  }

  public void setCurrentFileProgress(int progress) {
    fileProgress.setValue(progress);
  }

  public void setOverallProgress(int progress) {
    // nothing to do here - copy related
  }

  public void setAverageTransferRate(float rate) {
    // we can only display the current transfer rate here
    // but let's do it
    if( rate > 0.0f ) {
      String rateStr = StatisticsFormatter.formatTransferRate(rate);
      rateField.setText(rateStr);
    }
  }

  public void setSecondsElapsed(long secs) {
    // nothing to do here - copy related
  }

  public void setSecondsEstimated(long secs) {
    // nothing to do here - copy related
  }

  public void setExtendedOverallProgress(int filesDone, int fileNum, int progress) {
    // nothing to do here - copy related
  }

  public void setCurrentFileName(String fileName) {
    // nothing to do here - copy related
  }

  public void shutdown() {
    // nothing to do here - copy related
  }

  public void startup() {
    // nothing to do here - copy related
  }

  public boolean closeAfterCompletion() {
    return true;
  }

  public void setFinishedState() {
    // nothing to do here - copy related
  }

}
