/*
 * capivara - Java File Synchronization
 *
 * Created on 03-Apr-2006
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
package net.sf.jfilesync.gui.components;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class BookmarkShowEvent extends EventObject {

  private final MouseEvent mouseEvent;
  
  public BookmarkShowEvent(Object source, MouseEvent e) {
    super(source);
    if( e == null ) {
      throw new NullPointerException("mouse event");
    }
    mouseEvent = e;
  }
  
  public Component getComponent() {
    return mouseEvent.getComponent();
  }
  
  public int getX() {
    return mouseEvent.getX();
  }

  public int getY() {
    return mouseEvent.getY();
  }

}
