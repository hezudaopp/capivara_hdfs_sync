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
 * $Id: CollectFilesDialog.java,v 1.13 2006/08/09 22:18:40 hunold Exp $
 */
package net.sf.jfilesync.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.events.CollectStatusMessage;
import net.sf.jfilesync.gui.components.CollectFilesPanel;
import net.sf.jfilesync.gui.components.ConnectionDetailsComponent2;
import net.sf.jfilesync.prop.LanguageBundle;

public class CollectFilesDialog extends AbstractWorkerDialog {

  private static final long serialVersionUID = 3545521720187761713L;

  private final Component parent;
  private ConnectionDetails details;
  private JButton cancelButton = new JButton(LanguageBundle.getInstance()
      .getMessage("label.cancel"));

  private CollectFilesPanel statsPanel = new CollectFilesPanel();


  public CollectFilesDialog(final Component parent,
      final ConnectionDetails details) {
    this.parent = parent;
    this.details = details;
    init();
  }

  private void init() {
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
//    content.setPreferredSize(new Dimension(350,150));
    setContentPane(content);

    ConnectionDetailsComponent2 cdc =
      new ConnectionDetailsComponent2(details);

    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

    //cdc.setPreferredSize(new Dimension(380,40));
    statsPanel.setPreferredSize(new Dimension(380,110));
    buttonPane.setPreferredSize(new Dimension(380,40));

    content.add(cdc);
    content.add(statsPanel);
    content.add(buttonPane);

    pack();
  }


  public void displayWorkerData(GWorkerEvent e) {
    if( e instanceof CollectStatusMessage ) {
      CollectStatusMessage csm = (CollectStatusMessage)e;
      statsPanel.setMessage(csm);
    }
  }

  public void enableGUIElement(boolean enable) {
    if (enable) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          pack();
          setLocationRelativeTo(parent);
          setVisible(true);
        }
      });
    } else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setVisible(false);
          dispose();
        }
      });
    }
  }



}
