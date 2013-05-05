/*
 * capivara - Java File Synchronization
 *
 * Created on 10-Sep-2005
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
 * $Id: GnuPlugin.java,v 1.13 2006/08/05 15:28:17 hunold Exp $
 */
package net.sf.jfilesync.plugins.net.items;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileAttributes;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.gui.term.TTerminal;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.util.TMiscTool;
import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;


public class GnuPlugin extends AbstractLocalPlugin {

  private String currentPath = pwd();
  static Logger LOG = Logger.getLogger(GnuPlugin.class.getPackage().getName());
  private TPathControlInterface pci =TPathControlFactory
    .getPathControlInstance(TMiscTool.OS_LINUX);

  private TConnectionData connectData;
  private boolean connected = false;

  private final static int MAJOR = 0;
  private final static int MINOR = 2;

  public GnuPlugin() {
  }

  public void connect(TConnectionData connectData)
      throws PluginConnectException {
    this.connectData = connectData;
    connected = true;
  }

  public TConnectionData getTConnectionData() {
    return connectData;
  }

  public void disconnect() {
    connected = false;
  }

  public boolean isConnected() {
    return connected;
  }

  public TPathControlInterface getPathControl() {
    return pci;
  }

  public TFileData ls() throws IOException {
    return ls(currentPath);
  }

  public TFileData ls(final String path) throws IOException {
    TFileData fileData = new TFileData();

    //String[] cmdAr = { "ls", "-la", "--time-style=+%s", "-Q", "-1", path };

    String[] cmdAr = { "ls", "-la", "--time-style=+%s", "-Q", "-1", "-H", path };

//    String cmd = "";
//    for(String s : cmdAr) {
//      cmd += s + " ";
//    }
//    System.out.println(cmd );

    Process proc = Runtime.getRuntime().exec(cmdAr);
    InputStream instream = proc.getInputStream();
    InputStreamReader isr = new InputStreamReader(instream);
    BufferedReader br = new BufferedReader(isr);
    String line;
    while( (line = br.readLine()) != null ) {
      //System.out.println(line);
      if( line != null ) {
        if( line.startsWith("total") ) {
          continue;
        }
        else {
          TFileProperties file = parseLine(path, line);
          if( file != null ) {
            fileData.addFileProperties(file);
          }
        }
      }
    }

    try {
      proc.waitFor();
    } catch(InterruptedException e) {
      LOG.warning(e.getMessage());
    } finally {
      InputStream errStream = proc.getErrorStream();
      if( errStream != null ) {
        errStream.close();
      }
      OutputStream outStream = proc.getOutputStream();
      if( outStream != null ) {
        outStream.close();
      }
    }

    br.close();
    isr.close();
    instream.close();

    return fileData;
  }

  protected TFileProperties parseLine(final String path, final String line) {
    TFileProperties prop = new TFileProperties();

    StringTokenizer st = new StringTokenizer(line);

    // ls -la -1 -s --time-style=+%s -Q /dev

    try {
      String permStr = st.nextToken();

      if( permStr.charAt(0) == 'd' ) {
        prop.setDirectoryFlag(true);
      }

      if( permStr.charAt(0) == 'l' ) {
        prop.setLinkFlag(true);
// example:
// lrwxrwxrwx   1 sascha users   27 1137956026 "testlink" -> "/home/sascha/tmp/test/test2"
      }

      int perm = GnuPermissionParser.parserPermissionString(permStr);
      TFileAttributes attr = new TFileAttributes();
      attr.setPermissions(perm);
      prop.setAttributes(attr);

      st.nextToken(); // read hard links
      st.nextToken(); // read user
      st.nextToken(); // read group

      String size = st.nextToken();
      if( size.endsWith(",") ) {
        // char/block device
        st.nextToken(); // read next and put it in trash
        prop.setFileSize(new BigInteger("0"));
      }
      else {
        prop.setFileSize(new BigInteger(size));
      }

      String time = st.nextToken();
      long timeL = Long.parseLong(time);
      prop.setFileModTime(timeL*1000L);

      //  unquote
      st.nextToken("\""); // go to first quoting char
      String name = st.nextToken("\""); // get file name

      if( name.equals(".") || name.equals("..") ) {
        return null;
      }
      else {
        prop.setFileName(name);

        //String absName = pci.appendDirectory(currentPath, name);
        String absName = pci.appendDirectory(path, name);
        prop.setAbsoluteFileName(absName);
      }

      if( name.startsWith(".") ) {
        prop.setHiddenFlag(true);
      }

      // if it was a link check the target of the link
      // if it's a dir mark it as a dir
      if( prop.isLink() ) {
        // until next "
        String arrow = st.nextToken();
        if( arrow.equals(" -> ") ) {
          String target = st.nextToken("\"");

          TPathControlInterface pathControl = getPathControl();
          File f = null;
          if( pathControl.startsWithRoot(target) ) {
            f = new File(target);
          }
          else {
            final String fullPath = pathControl.appendDirectory(
                pathControl.getPathLevelUp(prop.getAbsoluteFileName()),
                target);
            f = new File(fullPath);
          }
          if( f != null && f.exists() && f.isDirectory() ) {
            prop.setDirectoryFlag(true);
          }
        }
      }

    }
    catch(NoSuchElementException e) {
      LOG.severe("Cannot parse file : " + line);
      prop = null;
    }

    return prop;
  }


  public void chdir(String path) throws IOException {
    String backup = currentPath;
    // the only thing is changing the absolutePath
    if( path.equals("..") ) {
      currentPath = pci.getPathLevelUp(currentPath);
    }
    else {
      if( pci.startsWithRoot(path) ) {
        currentPath = path;
      }
      else {
        currentPath = pci.appendDirectory(currentPath, path);
      }
    }

    // test if absolutePath valid
    File test = new File(currentPath);
    if( ! test.isDirectory() ) {
      currentPath = backup;
      throw new IOException("Path does not exist : " + test.getAbsoluteFile());
    }
  }

  public String getAbsolutePath() {
    return currentPath;
  }

  public boolean exists(String path) {
    File f = new File(path);
    return f.exists();
  }


  public void setPermissions(final String file, final int permissions)
      throws IOException {
    String permStr = GnuPermissionParser.getPermissionString(permissions);

    String command[] = new String[3];
    command[0] = "chmod";
    command[1] = permStr;
    command[2] = file;

    Process proc = Runtime.getRuntime().exec(command);
    proc.getErrorStream().close();
    proc.getInputStream().close();
    proc.getOutputStream().close();
  }


  public void setHidden(String path) throws IOException {
    // do nothing
  }

  public void symlink(String dir, String link) throws IOException {
    LOG.warning("symlinks unsupported by gnu plug-in");
  }

  public boolean isLink(String path) throws IOException {
    boolean ret = false;

    TFileData data = ls(path);
    if( data == null || data.getFileProperties().size() != 1 ) {
      throw new IOException("Cannot get file attributes of : " + path);
    }
    else {
      ret = data.getFileProperties(0).isLink();
    }

    return ret;
  }

  public void shellRequest(TTerminal shellArea) {
  }

  public int getConnectionID() {
    return ConnectionPluginManager.GNU_PLUGIN;
  }

  public boolean hasConnectionOptions() {
    return false;
  }

  public PluginOptionPanel getConnectionOptionPanel() {
    return null;
  }

  public String getProtocolString() {
    return "local";
  }

  public boolean isProvided(int feature) {
    boolean back = false;

    switch(feature) {
    case PROVIDES_FILETRANSFER:
    case PROVIDES_PERMISSION_HANDLING:
    case PROVIDES_SYMLINK_HANDLING:
      back = true;
      break;
    }
    return back;
  }

  public String getName() {
    return "GNU plugin";
  }

  public String getDescription() {
    return "GNU tools plugin";
  }

  public int getMajorVersion() {
    return MAJOR;
  }

  public int getMinorVersion() {
    return MINOR;
  }

  public String getLicense() {
    return "GPL";
  }

  public String pwd() {
    return (new File(".")).getAbsolutePath();
  }

  public boolean requiresPort() {
    return false;
  }

  public int getDefaultPort() {
    return -1;
  }

}
