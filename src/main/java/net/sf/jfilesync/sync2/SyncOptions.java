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
 * $Id: SyncOptions.java,v 1.3 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.sync2;

import java.util.*;

/**
 * contains all available sync options
 *
 */
public abstract class SyncOptions {

  private List<SyncOption> options = new ArrayList<SyncOption>();

  public void addSyncOption(SyncOption opt) {
    options.add(opt);
  }

  public void removeSyncOption(SyncOption opt) {
    options.remove(opt);
  }

  /**
   * @param description
   * @return returns sync option that matches the description or null if
   * there is no matching option
   */

  public SyncOption getSyncOption(final String description) {
    SyncOption opt = null;

    if( description == null ) {
      return null;
    }

    for(final SyncOption o : options) {
      if (o.getDescription().equals(description)) {
        opt = o;
        break;
      }
    }
    return opt;
  }

  public SyncOption[] getSyncOptions() {
    return (SyncOption[]) options.toArray(new SyncOption[options.size()]);
  }

}
