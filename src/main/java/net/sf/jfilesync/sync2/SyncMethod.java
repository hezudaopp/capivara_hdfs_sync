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
 * $Id: SyncMethod.java,v 1.10 2006/03/04 12:51:57 hunold Exp $
 */

package net.sf.jfilesync.sync2;

import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.sync2.syncer.SyncSettings;

public interface SyncMethod {

  public final static int METHOD_TIME = 0;
  public final static int METHOD_SHA1 = 1;
  public final static int METHOD_MD5  = 2;
  public final static int METHOD_SIZE = 3;

  //public SyncAttributor getSyncAttributor();
  public SyncAttributeComparator getSyncAttributeComparator();

  /**
   * returns the SyncOptions for this method
   * e.g., identical, newer_files, unique, etc.
   * @return
   */
  public SyncOptions getSyncOptions();
  public int getMethodID();
  public String getMethodDescription();
  public String getMethodName();
  public boolean isApplicable(ConnectionPlugin plugin1, ConnectionPlugin plugin2);
  public boolean requiresTimeStamps();


  public void applySyncSyncSettings(SyncSettings settings);

}
