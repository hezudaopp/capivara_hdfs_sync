/*
 * capivara - Java File Synchronization
 *
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
package net.sf.jfilesync.gui.components;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.worker.events.CollectStatusMessage;
import net.sf.jfilesync.prop.LanguageBundle;

public class FileCollectWorkerPanel extends WorkerPanel {

  private static final long serialVersionUID = 1L;
  private CollectFilesPanel statsPanel = new CollectFilesPanel();

  public FileCollectWorkerPanel() {
    super(true);
    initUI();
  }

  protected void initUI() {

    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel label = new JLabel(LanguageBundle.getInstance()
        .getMessage("dialog.collect.title"));
    titlePanel.add(label);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(titlePanel);
    add(statsPanel);

//    setBorder(BorderFactory.createCompoundBorder(
//        BorderFactory.createEtchedBorder(),
//        BorderFactory.createEmptyBorder(0,5,0,5)));

  }

  @Override
  public void displayWorkerData(GWorkerEvent e) {
    if( e instanceof CollectStatusMessage ) {
      CollectStatusMessage csm = (CollectStatusMessage)e;
      statsPanel.setMessage(csm);
    }
  }

}
