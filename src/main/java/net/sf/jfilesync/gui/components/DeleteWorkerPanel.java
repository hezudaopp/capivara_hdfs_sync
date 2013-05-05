/*
 * capivara - Java File Synchronization
 *
 * Created on 23-Jun-2005
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
 * $Id: DeleteWorkerPanel.java,v 1.8 2006/08/09 22:18:39 hunold Exp $
 */
package net.sf.jfilesync.gui.components;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.events.DeleteStatusMessage;
import net.sf.jfilesync.prop.LanguageBundle;

public class DeleteWorkerPanel extends WorkerPanel {

  private int fileNum = -1;
  private final boolean withProgress;

  private final DisplayFileNamePanel fileNamePanel = new DisplayFileNamePanel();
  private final JButton cancelButton = new JButton(LanguageBundle
      .getInstance().getMessage("label.cancel"));
  private ExtendedProgressPanel progressPanel;

  public DeleteWorkerPanel(final int fileNum) {
    super(true);
    this.fileNum = fileNum;
    this.withProgress = true;
    initUI();
  }

  public DeleteWorkerPanel() {
    super(true);
    withProgress = false;
    initUI();
  }

  protected void initUI() {

    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel label = new JLabel(LanguageBundle.getInstance()
        .getMessage("message.delete_data"));
    titlePanel.add(label);

    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fireCancelEvent(new TAbstractDialogEvent(this));
        cancelButton.setEnabled(false);
      }
    });

    JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPane.add(cancelButton);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(titlePanel);
    add(fileNamePanel);
    if( withProgress ) {
      progressPanel = new ExtendedProgressPanel(fileNum);
      add(progressPanel);
    }

    add(buttonPane);

    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEtchedBorder(),
        BorderFactory.createEmptyBorder(0,5,0,5)));
  }


  public void displayWorkerData(GWorkerEvent data) {
    if( data instanceof DeleteStatusMessage ) {
      final DeleteStatusMessage msg = (DeleteStatusMessage)data;
      fileNamePanel.setFileName(msg.getFileName());
      if( withProgress ) {
        progressPanel.setStep(msg.getProgress());
      }
    }
  }

}
