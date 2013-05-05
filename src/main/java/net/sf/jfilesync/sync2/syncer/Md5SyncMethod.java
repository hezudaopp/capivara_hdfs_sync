/*
 * capivara - Java File Synchronization
 *
 * Created on 06-Jul-2005
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
 * $Id: Md5SyncMethod.java,v 1.7 2006/03/04 12:51:56 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.sync2.SyncAttributeComparator;
import net.sf.jfilesync.sync2.SyncMethod;
import net.sf.jfilesync.sync2.SyncOptions;

public class Md5SyncMethod implements SyncMethod {

  public SyncAttributeComparator getSyncAttributeComparator() {
    return new Md5SyncAttributeComparator();
  }

  public SyncOptions getSyncOptions() {
    return new HashSyncOptions();
  }

  public int getMethodID() {
    return SyncMethod.METHOD_MD5;
  }

  public String getMethodDescription() {
    return "synchronize by comparing MD5 hash";
  }

  public String getMethodName() {
    return "MD5 sync";
  }

  public boolean isApplicable(ConnectionPlugin plugin1, ConnectionPlugin plugin2)
  {
    /*
    boolean ret = false;
    if( plugin1 != null && plugin2 != null ) {
      if( plugin1.getConnectionID() == ConnectionPluginManager.LOCAL_PLUGIN
       && plugin2.getConnectionID() == ConnectionPluginManager.LOCAL_PLUGIN )
      {
        ret = true;
      }
    }
    return ret;
    */
    return true;
  }

  public boolean requiresTimeStamps() {
    return false;
  }

  public void applySyncSyncSettings(SyncSettings settings) {
    // TODO Auto-generated method stub

  }

}
