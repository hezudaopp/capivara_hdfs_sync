/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: UnsupportedProtocolException.java,v 1.3 2006/06/11 10:50:01 hunold Exp $
 */

package net.sf.jfilesync.plugins.net;

public class UnsupportedProtocolException extends Exception {

  private static final long serialVersionUID = 3257568395246514225L;
  private final int unsupportedProtocolID;

  public UnsupportedProtocolException(int protocolID) {
    unsupportedProtocolID = protocolID;
  }

  public String getMessage() {
    return new String("unsupported protocol ID : " + unsupportedProtocolID);
  }

}
