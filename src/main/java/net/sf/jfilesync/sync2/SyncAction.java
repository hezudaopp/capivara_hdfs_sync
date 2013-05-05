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
 * $Id: SyncAction.java,v 1.6 2006/05/04 21:45:35 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;


public abstract class SyncAction {

  private SyncFile file;
  private List<SyncAction> children = new ArrayList<SyncAction>();

  public SyncAction(final SyncFile file) {
    this.file = file;
  }

  public SyncFile getSyncFile() {
    return file;
  }

  public void addChild(final SyncAction action) {
    children.add(action);
  }

  public SyncAction[] getChildren() {
    return (SyncAction[]) children.toArray(new SyncAction[children.size()]);
  }

  public boolean hasChildren() {
    return !children.isEmpty();
  }

  public abstract String getActionName();

  public abstract void renderFileName(JLabel label);

  //public abstract void renderActionName(JLabel label);

  public abstract JComponent renderLeftAction();

  public abstract JComponent renderRightAction();
}
