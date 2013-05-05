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
 * $Id: PrefixItemEvent.java,v 1.3 2005/08/19 21:29:01 hunold Exp $
 */
package net.sf.jfilesync.gui.components;

import java.util.EventObject;

public class PrefixItemEvent extends EventObject {

  private static final long serialVersionUID = 3258126977252012849L;

  private final String data;
  
  public PrefixItemEvent(Object source, String data) {
    super(source);
    this.data = data;
  }

  public String getStringData() {
    return data;
  }
  
}
