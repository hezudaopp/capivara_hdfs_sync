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
 * $Id: SyncProgressMessage.java,v 1.3 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import net.sf.gnocchi.GWorkerEvent;

public class SyncProgressMessage extends GWorkerEvent {

  private static final long serialVersionUID = 3257007640021184822L;
  private int percentage;
  private String actionName, currentFile;
  
  public SyncProgressMessage(Object source, 
      int percentage, 
      String actionName, 
      String currentFile) {
    super(source);
    this.percentage = percentage;
    this.actionName = actionName;
    this.currentFile = currentFile;
  }

  public String getActionName() { return actionName; }
  public String getCurrentFileName() { return currentFile; }
  public int getPercentage() { return percentage; }
  
}
