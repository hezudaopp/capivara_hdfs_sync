/*
 * capivara - Java File Synchronization
 *
 * Created on 26-Jun-2005
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
 * $Id: SyncCompareProgressMessage.java,v 1.3 2005/08/19 21:29:01 hunold Exp $
 */
package net.sf.jfilesync.engine.worker.events;

import net.sf.gnocchi.GWorkerEvent;

public class SyncCompareProgressMessage extends GWorkerEvent {

  private final String fileName1;
  private final String fileName2;
  private final int progress;
  
  public SyncCompareProgressMessage(Object source,
      final String fileName1,
      final String fileName2,
      final int progress)
  {
    super(source);
    this.fileName1 = fileName1;
    this.fileName2 = fileName2;
    this.progress = progress;
  }

  public String getFileName1() {
    return fileName1;
  }

  public String getFileName2() {
    return fileName2;
  }

  public int getProgress() {
    return progress;
  }

}
