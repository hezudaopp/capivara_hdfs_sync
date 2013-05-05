/*
 * capivara - Java File Synchronization
 *
 * Created on 06-Jul-2005
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
 * $Id: Sha1SyncAttributor.java,v 1.4 2005/08/19 21:29:01 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sf.jfilesync.sync2.SyncAttribute;

public class Sha1SyncAttributor extends HashSyncAttributor {

  public MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
    return MessageDigest.getInstance("SHA-1");
  }

  public int getSyncAttributeID() {
    return SyncAttribute.HASH_ATTRIBUTE;
  }
  
}
