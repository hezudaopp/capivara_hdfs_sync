/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.event.types;

import net.sf.jfilesync.event.TMessage;

public class JvmStatsMessage implements TMessage {

  private final String key;
  private final String value;
  
  public JvmStatsMessage(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  public ID getMessageType() {
    return ID.JVM_STATS_MESSAGE;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }


}
