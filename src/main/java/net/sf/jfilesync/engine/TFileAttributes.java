/*
 * capivara - Java File Synchronization
 *
 * Created on 24-May-2005
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
 * $Id: TFileAttributes.java,v 1.9 2005/09/11 15:21:17 hunold Exp $
 */
package net.sf.jfilesync.engine;

public class TFileAttributes
  implements Cloneable
{

  private FilePermissions permissions = null;

  public TFileAttributes() {
  }
    
  public void setPermissions(final int perm) {
    if( permissions == null ) {
      permissions = new FilePermissions(perm);
    }
    else {
      permissions.setValue(perm);
    }
  }
  
  public int getPermissions() {
    if( permissions != null) {
      return permissions.getValue();
    }
    else {
      return 0;
    }
  }
  
  public String getPermissionString() {
    if( permissions != null )
      return permissions.toString();
    else 
      return "";
  }
  
  public Object clone() {
    TFileAttributes attr = new TFileAttributes();
    if( permissions != null ) { 
      attr.setPermissions(permissions.getValue());
    }
    return attr;
  }
  
}
