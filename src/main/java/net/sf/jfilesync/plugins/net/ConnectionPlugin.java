/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed by: Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: ConnectionPlugin.java,v 1.20 2006/08/06 20:49:28 hunold Exp $
 */

package net.sf.jfilesync.plugins.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.plugins.GeneralPlugin;
import net.sf.jfilesync.plugins.net.items.PluginOptionPanel;
import net.sf.jfilesync.util.TPathControlInterface;

public interface ConnectionPlugin extends GeneralPlugin {

  public void connect(TConnectionData connectData)
      throws PluginConnectException;

  public TConnectionData getTConnectionData();

  public void disconnect();

  public boolean isConnected();

  public boolean isLocalConnection();

  public TPathControlInterface getPathControl();

  public TFileData ls() throws IOException;

  public TFileData ls(String path) throws IOException;

  public void chdir(String path) throws IOException;

  public String getAbsolutePath();

  public boolean exists(String path);

  public void put(InputStream instream, String remoteFileName)
      throws IOException;

  public void get(String remoteFileName, OutputStream outstream)
      throws IOException;

  public void mkdir(String dir) throws IOException;

  public void mkdirs(String dir) throws IOException;

  public String pwd() throws IOException;

  public void remove(String file) throws IOException;

  public void rmdir(String dir) throws IOException;

  public void abort() throws IOException;

  public void rename(String oldpath, String newpath) throws IOException;

  public void setModificationTime(String file, long mtime) throws IOException;

  public void setPermissions(String file, int permissions) throws IOException;

  //public int getPermissions(String file) throws IOException;

  //public void symlink(String dir, String link) throws IOException;

  public boolean isLink(String path) throws IOException;

  public boolean isFile(String path) throws IOException;

  // public void shellRequest(TTerminal shellArea);

  /**
   * unique id of the connection plugin
   */
  public int getConnectionID();

  /**
   * if plugin provides additional options
   * it will return true
   */
  public boolean hasConnectionOptions();

  /**
   * returns a PluginOptionPanel with specific options
   * for this plugin or null if the plugin
   * does not provide options
   */
  public PluginOptionPanel getConnectionOptionPanel();

  /**
   * returns true whether the user needs to enter
   * a password considering the current plugin settings
   */
  public boolean requiresPassword();
  
  public boolean requiresUsername();	// Jawinton

  public String getProtocolString();

  public boolean requiresPort();

  public int getDefaultPort();

  public void setHidden(String path) throws IOException;


}
