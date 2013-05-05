/*
 * capivara - Java File Synchronization
 *
 * Created on 12 Nov 2007
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
package net.sf.jfilesync.sync2.syncer;

import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.sync2.SyncAttributeComparator;
import net.sf.jfilesync.sync2.SyncMethod;
import net.sf.jfilesync.sync2.SyncOptions;

public class SizeSyncMethod implements SyncMethod {

  public String getMethodDescription() {
    return "sync by file size";
  }

  public int getMethodID() {
    return SyncMethod.METHOD_SIZE;
  }

  public String getMethodName() {
    return "SizeSync";
  }

  public SyncAttributeComparator getSyncAttributeComparator() {
    return new SizeSyncAttributeComparator();
  }

  public SyncOptions getSyncOptions() {
    return new SizeSyncOptions();
  }

  public boolean isApplicable(ConnectionPlugin plugin1, ConnectionPlugin plugin2) {
    return true;
  }

  public boolean requiresTimeStamps() {
    return false;
  }

  public void applySyncSyncSettings(SyncSettings settings) {
    // TODO Auto-generated method stub
  }

}
