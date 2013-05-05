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

import javax.swing.BoxLayout;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.worker.events.FileProgressWorkerEvent;

/**
 *
 * This panel shows
 * - the current file
 * and
 * - the current overall progress of the operation
 *
 * @author sascha
 *
 */

public class SimpleFileProgressWorkerPanel extends WorkerPanel {

  private static final long serialVersionUID = 1L;
  private final String title;
  private DisplayFileNamePanel namePanel = new DisplayFileNamePanel();
  private ProgressPanel progressPanel = new ProgressPanel();

  public SimpleFileProgressWorkerPanel(final String title) {
    super(true);
    this.title = title;
    initUI();
  }

  public void initUI() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    if( title != null ) {
      add(new SimpleTitlePanel(title));
    }
    add(namePanel);
    add(progressPanel);
  }

  @Override
  public void displayWorkerData(GWorkerEvent data) {
    if( data instanceof FileProgressWorkerEvent ) {
      FileProgressWorkerEvent e = (FileProgressWorkerEvent)data;
      namePanel.setFileName(e.getFileName());
      progressPanel.setProgress(e.getPercentage());
    }
  }

}
