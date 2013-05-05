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
 * $Id: TLocal_plugin.java,v 1.37 2006/04/10 08:43:54 hunold Exp $
 */

package net.sf.jfilesync.plugins.net.items;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.gui.term.TTerminal;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.util.TMiscTool;
import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;

public class TLocal_plugin extends AbstractLocalPlugin {

  private String absolutePath = null;
  private TPathControlInterface pci;
  private boolean connected = false;
  private TConnectionData conData;

  static final int bufsize = 131072; // 128 K
  static byte[] buf = new byte[bufsize];

  private final static Logger LOGGER = Logger.getLogger(TLocal_plugin.class.getPackage().getName());

  /* -------------------------------------------------------------------------- */
  public TLocal_plugin() {
    pci = TPathControlFactory.getPathControlInstance(TMiscTool.getOSId());
    absolutePath = (new File(".")).getAbsolutePath();
    absolutePath = pci.trimPathString(absolutePath);
  }

  /* -------------------------------------------------------------------------- */
  /* Information about the plugin */
  /* -------------------------------------------------------------------------- */
  public boolean isProvided(final int feature) {
    boolean back = false;

    if (feature == PROVIDES_FILETRANSFER) {
      back = true;
    }

    return back;
  }

  /* -------------------------------------------------------------------------- */

  public String getName() {
    return "Local file system plugin";
  }

  public String getDescription() {
    return "local file system";
  }

  public String getVersionString() {
    return getMajorVersion() + "." + getMinorVersion();
  }

  public int getMajorVersion() {
    return 0;
  }

  public int getMinorVersion() {
    return 3;
  }

  public String getLicense() {
    return "GPL";
  }

  public void connect(TConnectionData connectData) throws PluginConnectException {
    this.conData = connectData;
    connected = true;
  }

  public TConnectionData getTConnectionData() {
    return conData;
  }

  public void disconnect() {
    connected = false;
  }

  public boolean isConnected() {
    return connected;
  }

  public boolean isLocalConnection() {
    return true;
  }

  public TPathControlInterface getPathControl() {
    return TPathControlFactory.getPathControlInstance("unix");
  }

  /* -------------------------------------------------------------------------- */

  public TFileData ls() throws IOException {
    return ls(absolutePath);
  }

  /* -------------------------------------------------------------------------- */

  public TFileData ls(String path) throws IOException {
    // create new file list data structure
    TFileData fileData = new TFileData();

    // get the directory root entry
    File dir = new File(path);

    if (!dir.isDirectory()) {
      // System.err.println("local filesystem : no such directory");
      fileData.addFileProperties(extractFileProperties(dir));
      return fileData;
    }
    File cwd = dir;

    // get a list of all exists files in this directory
    // long start = System.currentTimeMillis();
    File[] entries = cwd.listFiles();

    if (entries == null) {
      return fileData;
    }
    // long end = System.currentTimeMillis();
    // System.out.println("list time : " + (end-start));

    // start = System.currentTimeMillis();
    for (int i = 0; i < entries.length; i++) {
      // convert and add the data to our list
      TFileProperties tfile = extractFileProperties(entries[i]);
      fileData.addFileProperties(tfile);
    }
    // end = System.currentTimeMillis();
    // System.out.println("convert time : " + (end-start));

    return fileData;
  }

  protected TFileProperties extractFileProperties(final File file) throws IOException {
    TFileProperties fileProp = new TFileProperties();

    fileProp.setFileName(file.getName());
    fileProp.setAbsoluteFileName(file.getAbsolutePath());
    fileProp.setFileSize(new BigInteger(Long.toString(file.length())));
    // System.out.println("modtime(" + localFile.getName() + ") = "
    // + localFile.lastModified());
    fileProp.setFileModTime(file.lastModified());
    fileProp.setDirectoryFlag(file.isDirectory());

    fileProp.setHiddenFlag(file.isHidden());

    try {

      // System.out.println("abs : " + file.getAbsolutePath());
      // System.out.println("can : " + file.getCanonicalPath());

      if (file.getAbsolutePath().compareTo(file.getCanonicalPath()) != 0) {

        // System.out.println("abs : " + file.getAbsolutePath());
        // System.out.println("can : " + file.getCanonicalPath());

        /*
         * /a/b/c /a/b => dirname(/a/b) dirname( can( /a/b/c ) ) basename( can(
         * /a/b/c ) ) == c
         */

        // final String canPath = file.getCanonicalPath();
        final String parentDir = getPathControl().getPathLevelUp(file.getAbsolutePath());
        final File parentDirFile = new File(parentDir);
        final String parentDirCan = parentDirFile.getCanonicalPath();

        // System.out.println("File abs     : " + file.getAbsolutePath());
        // System.out.println("File can     : " + file.getCanonicalPath());
        // System.out.println("parentDir    : " + parentDir);
        // System.out.println("parentDirCan : " + parentDirCan);

        // System.out.println("parentDir    : " + parentDir);
        // System.out.println("parentDirCan : " + parentDirCan);

        if (parentDir.equals(parentDirCan)) {
          fileProp.setLinkFlag(true);
        } else {
          // case /a/@b/@c -> /u/v/c
          // System.out.println("basename can: " +
          // getPathControl().basename(file.getCanonicalPath()));
          // System.out.println("basename abs: " +
          // getPathControl().basename(file.getAbsolutePath()));

          String basenameCan = getPathControl().basename(file.getCanonicalPath());
          String basenameAbs = getPathControl().basename(file.getAbsolutePath());

          if (!basenameCan.equals(basenameAbs)) {
            fileProp.setLinkFlag(true);
          } else {
            // basename equal
            // can also be a link
            // absolute /a/b/c canonical /v/u/c
            String dirnameCan = getPathControl().getPathLevelUp(file.getCanonicalPath());
            // System.out.println("dirnameCan : " + dirnameCan);
            if (!dirnameCan.equals(parentDirCan)) {
              fileProp.setLinkFlag(true);
            }
          }
        }

      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return fileProp;
  }

  public void chdir(String path) throws IOException {
    String backup = absolutePath;
    // the only thing is changing the absolutePath
    if (path.equals("..")) {
      absolutePath = pci.getPathLevelUp(absolutePath);
    } else {
      if (pci.startsWithRoot(path)) {
        absolutePath = path;
      } else {
        absolutePath = pci.appendDirectory(absolutePath, path);
      }
    }

    // test if absolutePath valid
    File test = new File(absolutePath);
    if (!test.isDirectory()) {
      absolutePath = backup;
      throw new IOException("Path does not exist : " + test.getAbsoluteFile());
    }

    // System.out.println("local> cd " + absolutePath);
  }

  protected void setAbsolutePath(final String path) {
    absolutePath = path;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public String pwd() {
    return (new File(".")).getAbsolutePath();
  }

  public synchronized void abort() throws IOException {
    super.abort();
  }

  public void shellRequest(TTerminal shellArea) {
  }

  public void setPermissions(final String file, final int perm) throws IOException {
    // not working since java.io.File does
    // not support permissions
    // throw new IOException("permissions unsupported");
    LOGGER.warning("permission handling in local plugin unsupported");
  }

  /*
   * public int getPermissions(String file) throws IOException { 
   * not working since java.io.File does not support permissions throw new
   * IOException("permissions unsupported"); }
   */

  public boolean exists(String path) {
    File f = new File(path);
    return f.exists();
  }

  public void symlink(String dir, String link) throws IOException {
    // throw new IOException("symlink unsupported");
    LOGGER.warning("symlinks unsupported");
  }

  public boolean isLink(final String path) throws IOException {
    boolean ret = false;
    File f = new File(path);
    if (!f.getAbsolutePath().equals(f.getCanonicalPath())) {
      ret = true;
    }
    return ret;
  }

  public int getConnectionID() {
    return ConnectionPluginManager.LOCAL_PLUGIN;
  }

  public boolean hasConnectionOptions() {
    return false;
  }

  public PluginOptionPanel getConnectionOptionPanel() {
    return null;
  }

  public boolean requiresPassword() {
    return false;
  }

  public String getProtocolString() {
    return "local";
  }

  public boolean requiresPort() {
    return false;
  }

  public int getDefaultPort() {
    return -1;
  }

  public void setHidden(String path) throws IOException {
    // not supported here
    // implemented in subclasses (mainly for Windows)
  }

}
