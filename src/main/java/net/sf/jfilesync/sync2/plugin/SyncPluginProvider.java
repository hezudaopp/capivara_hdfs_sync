/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2008 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 */
package net.sf.jfilesync.sync2.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.sync2.SyncFile;

public class SyncPluginProvider {

  private int lastConId;
  private HashMap<Integer, AbstractConnectionProxy> conHash = new HashMap<Integer, AbstractConnectionProxy>();

  public SyncPluginProvider() {

    lastConId = 0;

  }

  public synchronized int registerConnection(AbstractConnectionProxy con) {
    int conId = -1;

    if (conHash.containsValue(con)) {
      for (Integer cID : conHash.keySet()) {
        if (conHash.get(cID).equals(con)) {
          conId = cID;
          break;
        }
      }
    } else {
      conId = lastConId;
      lastConId++;
    }

    assert (conId != -1);

    conHash.put(conId, con);

    return conId;
  }
  
  public List<String> readLines(int conId, SyncFile file) throws IOException {

    List<String> lines = new ArrayList<String>();

    if (!conHash.containsKey(conId)) {
      throw new IOException("connection id invalid");
    }

    AbstractConnectionProxy con = conHash.get(conId);

    if (con == null || !con.isConnected()) {
      throw new IOException("connection not active: "
          + con.getPlugin().getProtocolString());
    }

    if (!file.getFile().isDirectory()) {

      File f = null;

      if (con.isLocalConnection()) {

        f = new File(file.getFile().getAbsoluteFileName());

      } else {

        f = File.createTempFile("tempfile", null);
        FileOutputStream tmpOutStream = new FileOutputStream(f);
        con.get(file.getFile().getAbsoluteFileName(), tmpOutStream);
        tmpOutStream.close();

      }

      assert (file != null);

      String line;
      BufferedReader reader = new BufferedReader(new FileReader(f));
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }

      if (!con.isLocalConnection()) {
        // delete tmp file
        f.delete();
      }

    } else {
      throw new IOException("file " + file.getFile().getFileName()
          + " is not a regular file");
    }

    return lines;
  }

}
