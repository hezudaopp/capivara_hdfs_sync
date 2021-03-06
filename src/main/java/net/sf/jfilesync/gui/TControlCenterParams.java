/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TControlCenterParams.java,v 1.2 2005/08/19 21:29:01 hunold Exp $
 */

package net.sf.jfilesync.gui;

import javax.swing.JFrame;

import net.sf.jfilesync.event.TEventMulticaster;

public class TControlCenterParams {

  private JFrame parent;
  private TEventMulticaster emc;
  private int ccId;

  public TControlCenterParams(JFrame parent, int ccId, TEventMulticaster emc) {
    this.parent = parent;
    this.ccId = ccId;
    this.emc  = emc;
  }

  public final JFrame getParent() { return parent; }
  public final int getCentrolCenterId() { return ccId; }
  public final TEventMulticaster getEventMulticaster() { return emc; }

}
