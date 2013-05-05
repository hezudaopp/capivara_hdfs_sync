/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.service.unify;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class AbstractDupDialog extends JDialog {

  public AbstractDupDialog(JFrame owner, String title) {
    super(owner, title);
  }

  public AbstractDupDialog(JDialog owner, String title) {
    super(owner, title);
  }

  private final List<DupExplorerListener> listeners = new ArrayList<DupExplorerListener>();

  public void addDupExlorerListener(DupExplorerListener listener) {
    listeners.add(listener);
  }

  public void fireDupExplorerEvent(DupExplorerEvent e) {
    if( e == null ) {
      throw new IllegalArgumentException("e must not be null");
    }
    for (DupExplorerListener listener : listeners ) {
      listener.performDupExplorerEvent(e);
    }
  }

}
