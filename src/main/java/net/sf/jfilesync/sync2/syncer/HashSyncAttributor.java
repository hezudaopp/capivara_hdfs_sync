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
 * $Id: HashSyncAttributor.java,v 1.8 2006/08/02 20:25:46 hunold Exp $
 */
package net.sf.jfilesync.sync2.syncer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.sync2.SyncAttribute;
import net.sf.jfilesync.sync2.SyncAttributor;
import net.sf.jfilesync.sync2.SyncFile;

public abstract class HashSyncAttributor
implements SyncAttributor {

  static Logger log = Logger.getLogger(HashSyncAttributor.class.getPackage().getName());
  private MessageDigest msgDigest = null;

  public HashSyncAttributor() {
    try {
      msgDigest = getMessageDigest();
    } catch (NoSuchAlgorithmException nae) {
      log.severe(nae.getMessage());
    }
  }

  public SyncAttribute getSyncAttribute(final AbstractConnectionProxy con,
      final SyncFile file) throws IOException {

    SyncAttribute ret = null;

    if (msgDigest == null) {
      throw new IOException("No MessageDigest found!");
    }

    if (!file.getFile().isDirectory()) {

      File f = null;

      if (con.isLocalConnection()) {
        f = new File(file.getFile().getAbsoluteFileName());
      } else {
        f = File.createTempFile("tempfile", null);
        FileOutputStream tmpOutStream = new FileOutputStream(f);
        //FileChannel channel = tmpOutStream.getChannel();
        con.get(file.getFile().getAbsoluteFileName(), tmpOutStream);
        tmpOutStream.close();
      }

      FileInputStream instream = new FileInputStream(f);

      msgDigest.reset();

      final int blockSize = 1024;
      int read;
      byte[] buf = new byte[blockSize];

      while ((read = instream.read(buf)) != -1) {
        msgDigest.update(buf, 0, read);
      }

      instream.close();

      byte[] hash = msgDigest.digest();

      ret = new HashSyncAttribute(hash);

      if (!con.isLocalConnection()) {
        // delete tmp file
        f.delete();
      }
    }
    return ret;
  }

  public abstract MessageDigest getMessageDigest()
      throws NoSuchAlgorithmException;

}
