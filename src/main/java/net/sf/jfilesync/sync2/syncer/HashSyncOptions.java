/*
 * capivara - Java File Synchronization
 *
 * Created on 29-Jun-2005
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
 * $Id: HashSyncOptions.java,v 1.2 2005/08/19 21:29:01 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import net.sf.jfilesync.sync2.SyncOptions;
import net.sf.jfilesync.sync2.syncer.HashSyncOption.Method;

public class HashSyncOptions extends SyncOptions {

  public HashSyncOptions() {
//    addSyncOption(new HashSyncOption(HashSyncOption.LEFT_TO_RIGHT));
//    addSyncOption(new HashSyncOption(HashSyncOption.RIGHT_TO_LEFT));
//    addSyncOption(new HashSyncOption(HashSyncOption.UNIQUE_LEFT_RIGHT));
//    addSyncOption(new HashSyncOption(HashSyncOption.UNIQUE_RIGHT_LEFT));
    for(Method m : Method.values()) {
      addSyncOption(new HashSyncOption(m));
    }
  }

}
