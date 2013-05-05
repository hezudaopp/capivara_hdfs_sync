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
 * $Id: AbstractWorkerDialog.java,v 1.5 2005/08/19 21:29:01 hunold Exp $
 */
package net.sf.jfilesync.gui.dialog;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JFrame;

import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.TAbstractGUIElementListener;
import net.sf.jfilesync.engine.worker.TAbstractWorkerGUIElement;


public abstract class AbstractWorkerDialog extends JDialog implements
    TAbstractWorkerGUIElement {

  private final java.util.List<TAbstractGUIElementListener> listeners = new ArrayList<TAbstractGUIElementListener>();

  public AbstractWorkerDialog() {
  }

  public AbstractWorkerDialog(Dialog owner, boolean modal) {
    super(owner, modal);
  }

  public AbstractWorkerDialog(JFrame owner, boolean modal) {
    super(owner, modal);
  }

  public synchronized void addTAbstractGUIElementListener(
      TAbstractGUIElementListener l) {
    listeners.add(l);
  }

  public synchronized void removeTAbstractGUIElementListener(
      TAbstractGUIElementListener l) {
    listeners.remove(l);
  }

  public synchronized void fireCancelEvent(TAbstractDialogEvent e) {
    Iterator<TAbstractGUIElementListener> lit = listeners.iterator();
    while (lit.hasNext()) {
      lit.next().cancelClicked(e);
    }
  }

}
