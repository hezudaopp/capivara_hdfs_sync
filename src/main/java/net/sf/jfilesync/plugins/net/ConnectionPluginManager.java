/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
 * changed by: Raik Nagel <raik.nagel@uni-bayreuth.de>
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
 * $Id: ConnectionPluginManager.java,v 1.9 2006/08/06 20:49:28 hunold Exp $
 */

package net.sf.jfilesync.plugins.net;

import java.util.logging.Logger;

import net.sf.jfilesync.plugins.net.items.*;
import net.sf.jfilesync.util.*;

public class ConnectionPluginManager {

  private final static Logger LOGGER = Logger
      .getLogger(ConnectionPluginManager.class.getPackage().getName());
  
  /*
   * NEVER change these number!
   * otherwise config file becomes corrupted
   */
  
  public static final int 
     LOCAL_PLUGIN           = 0, 
     J2SSH_SFTP_PLUGIN      = 1,
     JAKARTA_FTP_PLUGIN     = 3, 
     GNU_PLUGIN             = 5, 
     JSCH_SFTP_PLUGIN       = 6,
     J2SSH_SESSION_PLUGIN   = 7,
     BSD_PLUGIN             = 8,
  	 HDFS_PLUGIN			= 9; // Jawinton

  private final static int[] supportedProtocols = { 
//    LOCAL_PLUGIN,
//    J2SSH_SFTP_PLUGIN, 
//    JAKARTA_FTP_PLUGIN, 
//    GNU_PLUGIN, 
//    JSCH_SFTP_PLUGIN,
//    BSD_PLUGIN,
//    J2SSH_SESSION_PLUGIN
    HDFS_PLUGIN // Jawinton
  };

  public static String getPluginDescription(ConnectionPlugin plugin) {
    return plugin.getDescription();
  }

  public static int[] getSupportedPlugins() {
    return supportedProtocols;
  }

  public static int getProtocolByDescription(String name) {
    int prot = -1;
    for (int i = 0; i < supportedProtocols.length; i++) {
      if (name.equals(getConnectionModelInstance(supportedProtocols[i])
          .getDescription())) {
        prot = supportedProtocols[i];
        break;
      }
    }
    return prot;
  }

  public static ConnectionPlugin getConnectionModelInstance(int chosenProtocol) {
    ConnectionPlugin plugin = null;

    switch (chosenProtocol) {
    case ConnectionPluginManager.LOCAL_PLUGIN:
      plugin = getLocalPlugin();
      break;
    case ConnectionPluginManager.J2SSH_SFTP_PLUGIN:
      plugin = new SSHToolsPlugin();
      break;
    case ConnectionPluginManager.JAKARTA_FTP_PLUGIN:
      plugin = new TCommonsFTP_plugin();
      break;
    case ConnectionPluginManager.GNU_PLUGIN:
      plugin = new GnuPlugin();
      break;
    case ConnectionPluginManager.JSCH_SFTP_PLUGIN:
      plugin = new JSchPluginProxy();
      break;
    case ConnectionPluginManager.BSD_PLUGIN:
      plugin = new BsdPlugin();
      break;
    case ConnectionPluginManager.HDFS_PLUGIN:	// Jawinton
      plugin = new THdfs_plugin();
      break;
    default:
      LOGGER.severe("unknown Protocol ID " + chosenProtocol);
      plugin = null;
    }
    return plugin;
  }

  protected static TLocal_plugin getLocalPlugin() {
    return new TLocalPluginFactory().getLocalPlugin(TMiscTool.getOSId());
  }

}
