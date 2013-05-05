/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed by: 2003, 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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

package net.sf.jfilesync.plugins.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.io.CapivaraFileInputStream;
import net.sf.jfilesync.io.CapivaraFileOutputStream;
import net.sf.jfilesync.util.TPathControlInterface;

public class ConnectionProxy implements AbstractConnectionProxy {

  private ConnectionPlugin client;
  private TConnectionData conData;

  public ConnectionProxy(TConnectionData conData) {
    if (conData == null) {
      throw new IllegalArgumentException("conData is null");
    } else {
      this.conData = conData;
    }
    client = conData.getPlugin();
  }

  public void connect() throws PluginConnectException {
    // disable all existing connections
    if (client.isConnected()) {
      client.disconnect();
    }
    // initiate the connection
    client.connect(conData);
  }

  public void disconnect() {
    if (client.isConnected()) {
      client.disconnect();
    }
  }

  public TFileData ls() throws IOException {
    return client.ls();
  }

  public TFileData ls(String path) throws IOException {
    return client.ls(path);
  }

  public boolean isConnected() {
    return client.isConnected();
  }

  public boolean isLocalConnection() {
    return client.isLocalConnection();
  }

  public void chdir(final String path) throws IOException {
    if (client != null && client.isConnected()) {
      client.chdir(path);
    }
  }

  public String getCurrentPath() {
    return client.getAbsolutePath();
  }

  public CapivaraFileInputStream getFileInputStream(final String fileName) throws IOException {
    CapivaraFileInputStream stream = new CapivaraFileInputStream(fileName);
    return stream;
  }

  public void put(final InputStream instream, final String remoteFileName) throws IOException {
    client.put(instream, remoteFileName);
  }

  public CapivaraFileOutputStream getFileOutputStream(final String fileName) throws IOException {
    CapivaraFileOutputStream stream = new CapivaraFileOutputStream(fileName);
    return stream;
  }

  public void get(String remoteFileName, OutputStream outstream) throws IOException {
    client.get(remoteFileName, outstream);
  }

  public String getEncodedFileName(final String fileName) throws IOException {
    return fileName;
  }

  public void mkdir(final String dirName) throws IOException {
    client.mkdir(dirName);
  }

  public void mkdirs(final String dirName) throws IOException {
    client.mkdir(dirName);
  }

  public void remove(final String fileName) throws IOException {
    client.remove(fileName);
  }

  public void rmdir(final String dirName) throws IOException {
    client.rmdir(dirName);
  }

  public void abort() throws IOException {
    client.abort();
  }

  public String pwd() throws IOException {
    return client.pwd();
  }

  public void rename(String oldAbsoluteFileName, String newName) throws IOException {
    client.rename(oldAbsoluteFileName, newName);
  }

  public void setModificationTime(final String fileName, long mtime) throws IOException {
    client.setModificationTime(fileName, mtime);
  }

  public void setPermissions(final String fileName, final int permissions) throws IOException {
    client.setPermissions(fileName, permissions);
  }

  public boolean exists(final String path) {
    return client.exists(path);
  }

  public boolean isLink(final String path) throws IOException {
    return client.isLink(path);
  }

  public boolean isFile(final String path) throws IOException {
    return client.isFile(path);
  }

  public ConnectionPlugin getPlugin() {
    return (ConnectionPlugin) client;
  }

  public TPathControlInterface getPathControlInstance() {
    return client.getPathControl();
  }

  public void setHidden(String path) throws IOException {
    client.setHidden(path);
  }

}
