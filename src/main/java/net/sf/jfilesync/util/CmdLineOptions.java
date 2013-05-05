/*
 * capivara - Java File Synchronization
 *
 * Created on 22-Apr-2005
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
 * $Id: CmdLineOptions.java,v 1.8 2006/02/10 09:12:16 hunold Exp $
 */
package net.sf.jfilesync.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jfilesync.plugins.net.ConnectionPluginManager;

public class CmdLineOptions {

  private final static Logger LOGGER = Logger.getLogger(CmdLineOptions.class.getName());

  private final String[] args;
  private boolean hideSplash = false;
  private boolean showHelp = false;

  private int leftProt, rightProt;
  private String leftUser, rightUser;
  private String leftHost, rightHost;
  private boolean hasLeft, hasRight;
  private String leftPath, rightPath;

  private String configDir;

  private static final String PLUGIN_LOCAL = "local";
  private static final String PLUGIN_GNU = "gnu";
  private static final String PLUGIN_FTP = "ftp";
  private static final String PLUGIN_SFTP = "sftp";
  private static final String PLUGIN_SFTPLS = "sftpls";

  public CmdLineOptions(final String[] args) {
    this.args = args;

    for (int i = 0; i < args.length; i++) {

      if (args[i].equals("--nosplash")) {
        hideSplash = true;
      } else if (args[i].equals("--help")) {
        showHelp = true;
      } else if (args[i].equals("--left") || args[i].equals("--right")) {
        parseTarget(i);
      } else if (args[i].equals("-c") || args[i].equals("--config")) {
        if (i + 1 < args.length) {
          configDir = args[i + 1];
          i++;
        } else {
          LOGGER.warning("no config directory");
          System.exit(-1);
        }
      } else if (args[i].startsWith("--")) {
        LOGGER.warning("unkown option " + args[i]);
      }

    }
  }

  protected void parseTarget(final int pos) {

    boolean left = true;
    if (args[pos].equals("--right")) {
      left = false;
    }

    if (pos + 1 < args.length && !args[pos + 1].startsWith("--")) {
      final String target = new String(args[pos + 1]);
      String pretarget;
      int colon_index;

      colon_index = target.indexOf(":");
      if (colon_index > 0) {
        pretarget = target.substring(0, colon_index);
      } else {
        pretarget = target;
      }

      if (pretarget.equals(PLUGIN_LOCAL) || pretarget.equals(PLUGIN_GNU)) {
        String path = "";

        if (colon_index > 0) {
          path = target.substring(colon_index + 1);
        }

        int pluginId = ConnectionPluginManager.LOCAL_PLUGIN;
        if (pretarget.equals(PLUGIN_GNU)) {
          pluginId = ConnectionPluginManager.GNU_PLUGIN;
        }

        if (left) {
          leftProt = pluginId;
          leftUser = TMiscTool.getUserName();
          leftHost = TMiscTool.getLocalHostName();
          leftPath = path;
          hasLeft = true;
        } else {
          rightProt = pluginId;
          rightUser = TMiscTool.getUserName();
          rightHost = TMiscTool.getLocalHostName();
          rightPath = path;
          hasRight = true;
        }
      } else {
        // match (ftp|sftp):username@hostname

        final String prot = target.substring(0, target.indexOf(":"));
        int protocolID = -1;
        if (prot.equals(PLUGIN_FTP)) {
          protocolID = ConnectionPluginManager.JAKARTA_FTP_PLUGIN;
        } else if (prot.equals(PLUGIN_SFTP)) {
          protocolID = ConnectionPluginManager.J2SSH_SFTP_PLUGIN;
        } else if (prot.equals(PLUGIN_SFTPLS)) {
          protocolID = ConnectionPluginManager.J2SSH_SESSION_PLUGIN;
        } else {
          LOGGER.warning("unsupported protocol " + prot);
          throw new IllegalArgumentException("unsupported protocol " + prot);
        }

        final String user = target.substring(target.indexOf(":") + 1, target.indexOf("@"));
        String host = target.substring(target.indexOf("@") + 1, target.length());
        String path = "";

        final int i = host.indexOf(":");
        if (i > 0) {
          path = host.substring(i + 1);
          host = host.substring(0, i);
        }

        LOGGER.info("prot :" + prot + ": user :" + user + ": host :" + host + ":");
        if (left) {
          leftProt = protocolID;
          leftUser = user;
          leftHost = host;
          leftPath = path;
          hasLeft = true;
        } else {
          rightProt = protocolID;
          rightUser = user;
          rightHost = host;
          rightPath = path;
          hasRight = true;
        }

      }
    } else {
      if (left) {
        LOGGER.log(Level.WARNING, "--left requires target");
      } else {
        LOGGER.log(Level.WARNING, "--right requires target");
      }
    }
  }

  public boolean hideSplashScreen() {
    return hideSplash;
  }

  public boolean hasLeftTarget() {
    return hasLeft;
  }

  public boolean hasRightTarget() {
    return hasRight;
  }

  public int getLeftProtocol() {
    return leftProt;
  }

  public int getRightProtocol() {
    return rightProt;
  }

  public String getLeftUser() {
    return leftUser;
  }

  public String getRightUser() {
    return rightUser;
  }

  public String getLeftHost() {
    return leftHost;
  }

  public String getRightHost() {
    return rightHost;
  }

  public String getLeftPath() {
    return leftPath;
  }

  public String getRightPath() {
    return rightPath;
  }

  public boolean showHelp() {
    return showHelp;
  }

  public boolean hasConfigDir() {
    return (configDir != null);
  }

  public String getConfigDir() {
    return configDir;
  }

  public String getUsage() {
    String usage = "";

    usage += " --help ";
    usage += " --nosplash";
    usage += " -c | --config [config directory]";
    usage += " --left target --right target";
    usage += "\n\n";

    usage += "--help \t\tprint this message \n";
    usage += "--nosplash \tdon't show splash screen \n";
    usage += "target \t\tprotocol[:user@host][:path] \n";

    usage += "supported protocol ids: ";
    usage += PLUGIN_LOCAL + " " + PLUGIN_GNU + " " + PLUGIN_FTP + " " + PLUGIN_SFTP;
    usage += "\n";

    return usage;
  }

}
