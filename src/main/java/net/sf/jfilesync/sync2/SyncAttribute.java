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
 * $Id: SyncAttribute.java,v 1.8 2005/08/19 21:29:02 hunold Exp $
 */

package net.sf.jfilesync.sync2;

public abstract class SyncAttribute {

  public final static int TIME_ATTRIBUTE  = 0;
  public final static int HASH_ATTRIBUTE  = 1;
   
  public SyncAttribute() {
  }
  
  public abstract boolean equals(final SyncAttribute attr) 
  	throws UnknownAttributeException;
  
  public abstract int getType();
  
}
