/*
 * capivara - Java File Synchronization
 *
 * Created on 27-Mar-2005
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
 * $Id: ControlCenterSwitchMessage.java,v 1.4 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.event.types;

import net.sf.jfilesync.event.TMessage;

/**
 * The event is raised when <tab> key is pressed.
 * (move cursor to other "side")
 * The event is raised by some table and consumed by MainWin.
 */
public class ControlCenterSwitchMessage implements TMessage {

  private final int ccID;
  
  public ControlCenterSwitchMessage(final int ccID) {
    this.ccID = ccID;
  }
  
  public int getOrigin() {
    return ccID;
  }

  public ID getMessageType() {
    return ID.CONTROLCENTER_CHANGE_MESSAGE;
  }
  
  
  
}
