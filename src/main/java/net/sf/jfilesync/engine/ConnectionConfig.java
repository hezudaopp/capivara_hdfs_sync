/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: ConnectionConfig.java,v 1.4 2006/03/01 08:41:02 hunold Exp $
 */

package net.sf.jfilesync.engine;

import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;

public class ConnectionConfig {
  private final String descriptor;
  private final int protocol;
  private final String host;
  private final String username;
  private final int port;
  private String password;

  public ConnectionConfig(final String descriptor, final int protocol, final String host, final int port,
      final String username) {
    this.descriptor = descriptor;
    this.protocol = protocol;
    this.host = host;
    this.port = port;
    this.username = username;
  }

  public String getDescription() {
    return descriptor;
  }

  public int getProtocol() {
    return protocol;
  }

  public String getHostName() {
    return host;
  }

  public String getUserName() {
    return username;
  }

  public int getPort() {
    return port;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  /**
   * 
   * @return a formatted String with the information of this config. Or null if
   *         an error occurs, e.g. the plugin is invalid.
   */

  public String getFormattedString() {
    String str = null;
    ConnectionPlugin plugin = ConnectionPluginManager.getConnectionModelInstance(protocol);
    if (plugin != null) {
      str = plugin.getProtocolString() + "://" + username + "@" + host;
      if (port != -1 && port != plugin.getDefaultPort()) {
        str += ":" + port;
      }
    }
    return str;
  }

}
