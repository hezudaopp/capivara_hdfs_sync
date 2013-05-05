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
 * $Id: TimeSyncAttribute.java,v 1.5 2006/04/02 14:47:42 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import net.sf.jfilesync.sync2.SyncAttribute;
import net.sf.jfilesync.sync2.UnknownAttributeException;


public class TimeSyncAttribute extends SyncAttribute {

//  private final static Logger LOGGER = Logger.getLogger(TimeSyncAttribute.class
//      .getName());

  private final long timestamp;

  public TimeSyncAttribute(final long timestamp) {
    this.timestamp = timestamp;
  }

  public long getTimeStamp() {
    return timestamp;
  }

  public boolean equals(final SyncAttribute attr)
      throws UnknownAttributeException {
    if (attr instanceof TimeSyncAttribute) {
      if (timestamp != ((TimeSyncAttribute) attr).getTimeStamp()) {
        return false;
      }
    } else {
      throw new UnknownAttributeException("time attribute expected");
    }
    return true;
  }

  public int getType() {
    return SyncAttribute.TIME_ATTRIBUTE;
  }

}
