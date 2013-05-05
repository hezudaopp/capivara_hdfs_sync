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
 * $Id: EncodingProxy.java,v 1.2 2006/08/29 19:58:19 hunold Exp $
 */
package net.sf.jfilesync.plugins.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.io.CapivaraFileInputStream;
import net.sf.jfilesync.io.CapivaraFileOutputStream;
import net.sf.jfilesync.util.EncodingUtils;
import net.sf.jfilesync.util.TPathControlInterface;

public class EncodingProxy implements AbstractConnectionProxy {

  private final AbstractConnectionProxy proxy;
  private final String encoding;
  private final String hostEncoding = EncodingUtils.getJVMEnconding();
  private final static Logger LOGGER = Logger.getLogger(EncodingProxy.class.getName());

  public EncodingProxy(AbstractConnectionProxy proxy, String encoding) {
    if (proxy == null) {
      throw new IllegalArgumentException("proxy is null");
    }
    if (encoding == null) {
      throw new IllegalArgumentException("encoding is null");
    }
    this.proxy = proxy;
    this.encoding = encoding;
  }

  public TFileData ls() throws IOException {
    TFileData data = proxy.ls();
    data = decodeTFileData(data);
    return data;
  }

  public TFileData ls(final String path) throws IOException {
    final String ppath = EncodingUtils.encodeToTargetEncoding(path, encoding, hostEncoding);
    TFileData data = proxy.ls(ppath);
    data = decodeTFileData(data);
    return data;
  }

  protected TFileData decodeTFileData(final TFileData data) throws IOException {
    for (int i = 0; i < data.getNumberofFiles(); i++) {
      TFileProperties file = data.getFileProperties(i);
      file.setAbsoluteFileName(EncodingUtils.decodeToHostEncoding(file.getAbsoluteFileName(), encoding, hostEncoding));
      file.setFileName(EncodingUtils.decodeToHostEncoding(file.getFileName(), encoding, hostEncoding));
    }
    return data;
  }

  public String getEncodedFileName(final String fileName) throws IOException {
    final String fname = EncodingUtils.encodeToTargetEncoding(fileName, encoding, hostEncoding);
    return fname;
  }

  public void chdir(final String path) throws IOException {
    final String encodedPath = getEncodedFileName(path);
    proxy.chdir(encodedPath);
  }

  public String getCurrentPath() {
    String path = proxy.getCurrentPath();
    try {
      path = EncodingUtils.decodeToHostEncoding(path, encoding, hostEncoding);
    } catch (UnsupportedEncodingException e) {
      LOGGER.severe(e.getMessage());
    }
    return path;
  }

  public CapivaraFileInputStream getFileInputStream(final String fileName) throws IOException {
    final String pFileName = EncodingUtils.encodeToTargetEncoding(fileName, encoding, hostEncoding);
    CapivaraFileInputStream stream = new CapivaraFileInputStream(pFileName);
    return stream;
  }

  public void put(final InputStream instream, final String remoteFileName) throws IOException {
    final String fileName = EncodingUtils.encodeToTargetEncoding(remoteFileName, encoding, hostEncoding);
    proxy.put(instream, fileName);
  }

  public CapivaraFileOutputStream getFileOutputStream(final String fileName) throws IOException {
    final String pFileName = EncodingUtils.encodeToTargetEncoding(fileName, encoding, hostEncoding);
    CapivaraFileOutputStream stream = new CapivaraFileOutputStream(pFileName);
    return stream;
  }

  public void get(String remoteFileName, OutputStream outstream) throws IOException {
    proxy.get(remoteFileName, outstream);
  }

  public void mkdir(final String dirName) throws IOException {
    proxy.mkdir(getEncodedFileName(dirName));
  }

  public void mkdirs(final String dirName) throws IOException {
    proxy.mkdir(getEncodedFileName(dirName));
  }

  public void remove(final String fileName) throws IOException {
    proxy.remove(getEncodedFileName(fileName));
  }

  public void rmdir(final String dirName) throws IOException {
    proxy.rmdir(getEncodedFileName(dirName));
  }

  public void abort() throws IOException {
    proxy.abort();
  }

  public String pwd() throws IOException {
    String pwd = proxy.pwd();
    try {
      pwd = EncodingUtils.decodeToHostEncoding(pwd, encoding, hostEncoding);
    } catch (UnsupportedEncodingException e) {
      LOGGER.severe(e.getMessage());
    }
    return pwd;
  }

  public void rename(String oldAbsoluteFileName, String newName) throws IOException {
    proxy.rename(getEncodedFileName(oldAbsoluteFileName), getEncodedFileName(newName));
  }

  public void setModificationTime(final String fileName, long mtime) throws IOException {
    proxy.setModificationTime(getEncodedFileName(fileName), mtime);
  }

  public void setPermissions(final String fileName, final int permissions) throws IOException {
    proxy.setPermissions(getEncodedFileName(fileName), permissions);
  }

  public boolean exists(final String path) {
    boolean exists = false;
    try {
      exists = proxy.exists(getEncodedFileName(path));
    } catch (IOException e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
    }
    return exists;
  }

  public boolean isLink(final String path) throws IOException {
    return proxy.isLink(getEncodedFileName(path));
  }

  public boolean isFile(final String path) throws IOException {
    return proxy.isFile(getEncodedFileName(path));
  }

  public void connect() throws PluginConnectException {
    proxy.connect();
  }

  public void disconnect() {
    proxy.disconnect();
  }

  public boolean isConnected() {
    return proxy.isConnected();
  }

  public boolean isLocalConnection() {
    return proxy.isLocalConnection();
  }

  public ConnectionPlugin getPlugin() {
    return proxy.getPlugin();
  }

  public TPathControlInterface getPathControlInstance() {
    return proxy.getPathControlInstance();
  }

  public void setHidden(String path) throws IOException {
    proxy.setHidden(getEncodedFileName(path));
  }

}
