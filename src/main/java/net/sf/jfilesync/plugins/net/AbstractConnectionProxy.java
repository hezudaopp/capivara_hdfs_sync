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
 * $Id: AbstractConnectionProxy.java,v 1.2 2006/08/29 19:58:19 hunold Exp $
 */
package net.sf.jfilesync.plugins.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.io.CapivaraFileInputStream;
import net.sf.jfilesync.io.CapivaraFileOutputStream;
import net.sf.jfilesync.util.TPathControlInterface;

public interface AbstractConnectionProxy {

  public abstract void connect() throws PluginConnectException;

  public abstract void disconnect();

  public abstract TFileData ls() throws IOException;

  public abstract TFileData ls(String path) throws IOException;

  public abstract boolean isConnected();

  public abstract boolean isLocalConnection();

  public abstract void chdir(final String path) throws IOException;

  public abstract String getCurrentPath();

  public abstract CapivaraFileInputStream getFileInputStream(final String fileName)
      throws IOException;

  public abstract void put(final InputStream instream,
      final String remoteFileName) throws IOException;

  public abstract CapivaraFileOutputStream getFileOutputStream(final String fileName)
      throws IOException;

  public abstract void get(String remoteFileName, OutputStream outstream)
      throws IOException;

  public abstract String getEncodedFileName(final String fileName)
      throws IOException;

  public abstract void mkdir(final String dirName) throws IOException;

  public abstract void mkdirs(final String dirName) throws IOException;

  public abstract void remove(final String fileName) throws IOException;

  public abstract void rmdir(final String dirName) throws IOException;

  public abstract void abort() throws IOException;

  public abstract String pwd() throws IOException;

  public abstract void rename(String oldAbsoluteFileName, String newName)
      throws IOException;

  public abstract void setModificationTime(final String fileName, long mtime)
      throws IOException;

  public abstract void setPermissions(final String fileName,
      final int permissions) throws IOException;

  public abstract boolean exists(final String path);

  public abstract boolean isLink(final String path) throws IOException;

  public abstract boolean isFile(final String path) throws IOException;

  public abstract ConnectionPlugin getPlugin();

  public abstract TPathControlInterface getPathControlInstance();

  public abstract void setHidden(String path) throws IOException;

}
