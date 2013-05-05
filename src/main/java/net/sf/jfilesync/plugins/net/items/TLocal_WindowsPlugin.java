/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2008 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.plugins.net.items;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;

public class TLocal_WindowsPlugin extends TLocal_plugin {

  private static TPathControlInterface pathControl = TPathControlFactory.getPathControlInstance("windows");

  private final static Logger LOGGER = Logger.getLogger(TLocal_WindowsPlugin.class.getName());

  public TLocal_WindowsPlugin() {
    super();
  }

  public boolean isProvided(final int feature) {
    boolean back = false;

    if (feature == PROVIDES_FILETRANSFER) {
      back = true;
    }

    return back;
  }

  public String getName() {
    return "Windows plugin";
  }

  public String getDescription() {
    return "local file system on Windows";
  }

  public String getVersionString() {
    return getMajorVersion() + "." + getMinorVersion();
  }

  public int pluginMainVersion() {
    return 0;
  }

  public int pluginLowVersion() {
    return 2;
  }

  protected TFileProperties extractFileProperties(final File file) throws IOException {
    TFileProperties fileProp = new TFileProperties();

    fileProp.setFileName(file.getName());
    fileProp.setAbsoluteFileName(file.getAbsolutePath());
    fileProp.setFileSize(new BigInteger(Long.toString(file.length())));

    long mtime = file.lastModified();

    fileProp.setFileModTime(mtime);

    fileProp.setDirectoryFlag(file.isDirectory());

    fileProp.setHiddenFlag(file.isHidden());

    // !TODO! what about links in Windows -> do any exist ?
    return fileProp;
  }

  public TPathControlInterface getPathControl() {
    return pathControl;
  }

  @Override
  public void put(InputStream instream, String remoteFileName) throws IOException {

    fixRemoteFileHiddenProblem(remoteFileName);
    super.put(instream, remoteFileName);

  }

  @Override
  public void put(String localFileName, String remoteFileName) throws IOException {

    fixRemoteFileHiddenProblem(remoteFileName);
    super.put(localFileName, remoteFileName);
  }

  public void setHidden(String path) throws IOException {
    final String command = "C:\\WINDOWS\\System32\\ATTRIB.EXE +H \"" + path + "\"";
    LOGGER.info(command);
    Runtime.getRuntime().exec(command);
  }

  private void fixRemoteFileHiddenProblem(String remoteFileName) {
    File remFile = new File(remoteFileName);
    if (remFile.canWrite() && remFile.isHidden()) {
      remFile.delete();
    }
  }

  public void chdir(String path) throws IOException {

    String absolutePath = getAbsolutePath();
    String backup = absolutePath;

    // the only thing is changing absolutePath
    if (path.equals("..")) {
      absolutePath = pathControl.getPathLevelUp(absolutePath);
    } else {
      // lotsa Windows roots -> take care!
      if (pathControl.startsWithRoot(path) || pathControl.startsWithRoot(pathControl.appendDirectory(path, ""))) {
        absolutePath = path;
      } else {
        absolutePath = pathControl.appendDirectory(absolutePath, path);
      }
    }

    // test if absolutePath valid
    File test = new File(absolutePath);
    if (!test.isDirectory()) {
      absolutePath = backup;
      throw new IOException("Path does not exist : " + test.getAbsoluteFile());
    }

    setAbsolutePath(absolutePath);
  }

}
