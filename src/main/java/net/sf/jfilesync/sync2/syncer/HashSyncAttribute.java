/*
 * capivara - Java File Synchronization
 *
 * Created on 28-Jun-2005
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
 * $Id: HashSyncAttribute.java,v 1.3 2005/08/19 21:29:01 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import net.sf.jfilesync.sync2.SyncAttribute;
import net.sf.jfilesync.sync2.UnknownAttributeException;

public class HashSyncAttribute extends SyncAttribute {

  private final byte[] hash;
  
  public HashSyncAttribute(final byte[] hash) {
    this.hash = hash;
  }
  
  public byte[] getDigest() {
    return hash;
  }
  
  public boolean equals(SyncAttribute attr) throws UnknownAttributeException {
    boolean ret = false;
        
    if( attr instanceof HashSyncAttribute ) {
      final byte[] hash2 = ((HashSyncAttribute)attr).getDigest();
      if( hash != null && hash2 != null ) {
        if( hash.length == hash2.length ) {
          boolean diff = false;
          for(int i=0; i<hash.length; i++) {
            if( hash[i] != hash2[i] ) {
              diff = true;
            }
          }
          if( ! diff ) {
            ret = true;
          }
        }
      }
      
    }
    else {
      throw new UnknownAttributeException("hash attribute expected");
    }
    
    return ret;
  }
  
  public String toString() {
    StringBuffer ret = new StringBuffer();

    for(int i=0; i<hash.length; i++) {
      System.out.print( hash[i] );
    }
    System.out.println();

    return ret.toString();
  }

  public int getType() {
    return HASH_ATTRIBUTE;
  }

}
