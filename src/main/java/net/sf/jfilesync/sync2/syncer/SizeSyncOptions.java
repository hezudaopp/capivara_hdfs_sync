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

import net.sf.jfilesync.sync2.SyncOptions;
import net.sf.jfilesync.sync2.syncer.HashSyncOption.Method;

public class SizeSyncOptions extends SyncOptions {
  
  /**
   * 
   * takes care when modifying this
   * it takes HashSyncOption.Method as argument
   * 
   */
  
  public SizeSyncOptions() {
    for(Method m : Method.values()) {
      addSyncOption(new SizeSyncOption(m));
    }
  }

}
