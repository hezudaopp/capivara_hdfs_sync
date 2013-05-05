/*
 * capivara - Java File Synchronization
 *
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
 * $Id: CaseInsensitiveProxy.java,v 1.2 2006/08/29 19:58:19 hunold Exp $
 */
package net.sf.jfilesync.plugins.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.io.CapivaraFileInputStream;
import net.sf.jfilesync.io.CapivaraFileOutputStream;
import net.sf.jfilesync.util.TPathControlInterface;

public class CaseInsensitiveProxy implements AbstractConnectionProxy {

  private final AbstractConnectionProxy proxy;
  private final static Logger LOGGER = Logger.getLogger(CaseInsensitiveProxy.class.getName());

  public CaseInsensitiveProxy(AbstractConnectionProxy proxy) {
    if (proxy == null) {
      throw new IllegalArgumentException("proxy is null");
    }
    this.proxy = proxy;
  }

  public void abort() throws IOException {
    proxy.abort();
  }

  public void chdir(final String path) throws IOException {
    // final String ppath = findFile(path);
    // if( ppath != null ) {
    // proxy.chdir(ppath);
    // } else {
    proxy.chdir(path);
    // }
  }

  public void connect() throws PluginConnectException {
    proxy.connect();
  }

  public void disconnect() {
    proxy.disconnect();
  }

  public boolean exists(final String path) {
    boolean exists = false;
    final String ppath = findFile(path);
    if (ppath != null) {
      exists = true;
    }
    return exists;
  }

  public void get(String remoteFileName, OutputStream outstream) throws IOException {
    proxy.get(remoteFileName, outstream);
  }

  public String getCurrentPath() {
    return proxy.getCurrentPath();
  }

  public String getEncodedFileName(String fileName) throws IOException {
    return proxy.getEncodedFileName(fileName);
  }

  public CapivaraFileInputStream getFileInputStream(final String fileName) throws IOException {
    final String ppath = findFile(fileName);
    final CapivaraFileInputStream ret;
    if (ppath != null) {
      ret = proxy.getFileInputStream(ppath);
    } else {
      ret = proxy.getFileInputStream(fileName);
    }
    return ret;
  }

  public CapivaraFileOutputStream getFileOutputStream(final String fileName) throws IOException {
    final String ppath = findFile(fileName);
    final CapivaraFileOutputStream ret;
    if (ppath != null) {
      ret = proxy.getFileOutputStream(ppath);
    } else {
      ret = proxy.getFileOutputStream(fileName);
    }
    return ret;
  }

  public TPathControlInterface getPathControlInstance() {
    return proxy.getPathControlInstance();
  }

  public ConnectionPlugin getPlugin() {
    return proxy.getPlugin();
  }

  public boolean isConnected() {
    return proxy.isConnected();
  }

  public boolean isFile(final String path) throws IOException {
    final String ppath = findFile(path);
    if (ppath != null) {
      return proxy.isFile(ppath);
    } else {
      return proxy.isFile(path);
    }
  }

  public boolean isLink(String path) throws IOException {
    final String ppath = findFile(path);
    if (ppath != null) {
      return proxy.isLink(ppath);
    } else {
      return proxy.isLink(path);
    }
  }

  public boolean isLocalConnection() {
    return proxy.isLocalConnection();
  }

  public TFileData ls() throws IOException {
    return proxy.ls();
  }

  public TFileData ls(String path) throws IOException {
    return proxy.ls(path);
  }

  public void mkdir(String dirName) throws IOException {
    proxy.mkdir(dirName);
  }

  public void mkdirs(String dirName) throws IOException {
    proxy.mkdirs(dirName);
  }

  public void put(InputStream instream, String remoteFileName) throws IOException {

    final String otherCaseName = findFile(remoteFileName);
    if (otherCaseName != null) {
      LOGGER.info("put file (other case) : " + otherCaseName);
      proxy.put(instream, otherCaseName);
    } else {
      LOGGER.info("put file : " + remoteFileName);
      proxy.put(instream, remoteFileName);
    }
  }

  public String pwd() throws IOException {
    return proxy.pwd();
  }

  public void remove(String fileName) throws IOException {
    final String ppath = findFile(fileName);
    if (ppath != null) {
      proxy.remove(ppath);
    } else {
      proxy.remove(fileName);
    }
  }

  public void rename(String oldAbsoluteFileName, String newName) throws IOException {
    proxy.rename(oldAbsoluteFileName, newName);
  }

  public void rmdir(String dirName) throws IOException {
    final String ppath = findFile(dirName);
    if (ppath != null) {
      proxy.remove(ppath);
    } else {
      proxy.rmdir(dirName);
    }
  }

  public void setModificationTime(final String fileName, long mtime) throws IOException {

    final String ppath = findFile(fileName);
    if (ppath != null) {
      proxy.setModificationTime(ppath, mtime);
    } else {
      throw new IOException("No file " + fileName);
    }

  }

  public void setPermissions(String fileName, int permissions) throws IOException {

    final String ppath = findFile(fileName);
    if (ppath != null) {
      proxy.setPermissions(ppath, permissions);
    } else {
      throw new IOException("No file " + fileName);
    }

  }

  private String findFile(final String path) {
    String ret = null;

    // LOGGER.info("find file " + path);

    if (getPathControlInstance().isRoot(path)) {
      ret = path;
    } else {
      final String pathUp = getPathControlInstance().getPathLevelUp(path);
      try {
        // LOGGER.info("ls of path: " + pathUp);
        final TFileData pathContents = ls(pathUp);
        if (pathContents == null) {
          LOGGER.warning("No contents found in " + pathUp);
        } else {
          for (int i = 0; i < pathContents.getNumberofFiles(); i++) {
            final String fileName = pathContents.getFileProperties(i).getAbsoluteFileName();
            // LOGGER.info("Compare " + path + " and " + fileName);
            if (path.compareToIgnoreCase(fileName) == 0) {
              ret = fileName;
              break;
            }
          }
        }
      } catch (IOException e) {
        LOGGER.warning("Cannot list directory " + pathUp + ";" + e.getMessage());
        e.printStackTrace();
      }
    }

    return ret;
  }

  public void setHidden(String path) throws IOException {
    final String ppath = findFile(path);
    if (ppath != null) {
      proxy.setHidden(ppath);
    } else {
      throw new IOException("No file " + path);
    }
  }

}
