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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.TAbstractGUIElementListener;
import net.sf.jfilesync.engine.worker.TAbstractWorkerGUIElement;

public abstract class WorkerPanel extends JPanel implements
    TAbstractWorkerGUIElement {

  private final List<TAbstractGUIElementListener> listeners = new ArrayList<TAbstractGUIElementListener>();

  public WorkerPanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }

  public abstract void displayWorkerData(GWorkerEvent data);

  public void enableGUIElement(boolean enable) {
    // not required for WorkerPanels
  }

  public void addTAbstractGUIElementListener(TAbstractGUIElementListener l) {
      listeners.add(l);
  }

  public void removeTAbstractGUIElementListener(TAbstractGUIElementListener l) {
      listeners.remove(l);
  }

  public void fireCancelEvent(TAbstractDialogEvent e) {
      for(TAbstractGUIElementListener l : listeners) {
          l.cancelClicked(e);
      }
  }

}
