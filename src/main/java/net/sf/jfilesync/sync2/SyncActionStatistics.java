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
 * $Id: SyncActionStatistics.java,v 1.2 2006/02/02 22:41:13 hunold Exp $
 */
package net.sf.jfilesync.sync2;

public class SyncActionStatistics {

  private final SyncAction[] actions;
  
  private int copyNum    = 0;
  private int deleteNum  = 0;
  private long copySizeKb= 0;
  
  public SyncActionStatistics(final SyncAction[] actions) {
    this.actions = actions;
    if( this.actions != null ) {
      makeStats();
    }
  }
  
  private void makeStats() {
    for(int i=0; i<actions.length; i++) {
      if( actions[i] != null ) {
        countAction(actions[i]);
      }
    }
  }
  
  private void countAction(final SyncAction action) {
    if( action instanceof SyncActionCopy ) {
      SyncActionCopy copyAction = (SyncActionCopy)action;
      copyNum++;
      copySizeKb += copyAction.getSyncFile().getFile()
        .getFileSize().longValue() / 1024l;
    }
    else if( action instanceof SyncActionDelete ) {
      deleteNum++;
    }
  }

  public long getKbToCopy() {
    return copySizeKb;
  }
  
  public int getNumberOfFilesToCopy() {
    return copyNum;
  }
  
  public int getNumberOfFilesToDelete() {
    return deleteNum;
  }
  
}
