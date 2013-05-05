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
 * $Id: TConfigObserver.java,v 1.3 2006/01/01 00:42:00 hunold Exp $
 */

package net.sf.jfilesync.settings;

import java.io.File;
import java.io.IOException;

import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;

public class ConfigFileHandler {

  private final TPathControlInterface pathControl;
  private static ConfigFileHandler configHandler;

  private ConfigFileHandler() {
    pathControl = TPathControlFactory.getLocalPathControlInstance();
  }

  public static synchronized ConfigFileHandler getInstance() {
    if( configHandler == null ) {
      configHandler = new ConfigFileHandler();
    }
    return configHandler;
  }

  public boolean configDirExists() {
    boolean fine = false;
    File testFile = new File(getConfigDirLocation());
    if (testFile != null && testFile.exists() && testFile.isDirectory()) {
      fine = true;
    }
    return fine;
  }

  public boolean configFileExists() {
    boolean fine = false;
    File testFile = new File(getConfigFileLocation());
    if (testFile != null && testFile.exists()) {
      fine = true;
    }
    return fine;
  }

  public boolean createConfigDir() throws IOException {
    File configDir = new File(getConfigDirLocation());
    return configDir.mkdir();
  }

  public String getConfigDirLocation() {
    final String userDir = CapivaraRuntimeConfiguration.getInstance()
        .getUserConfigDir();
    String configDir = pathControl.appendDirectory(userDir,
        ConfigDefinitions.CONFIG_DIR);
    return configDir;
  }

  public String getConfigFileLocation() {
    String fileLoc = pathControl.appendDirectory(getConfigDirLocation(),
        ConfigDefinitions.CONFIG_FILE);
    return fileLoc;
  }

  public String getRegexFileLocation() {
    return pathControl.appendDirectory(getConfigDirLocation(),
        ConfigDefinitions.EXPRESSION_FILE);
  }

  public String getSyncProjectLocation() {
    return pathControl.appendDirectory(getConfigDirLocation(),
        ConfigDefinitions.SYNC_PROJECT_DIR);
  }

  public String getSyncProjectFileLocation(String fileName) {
    return pathControl.appendDirectory(getSyncProjectLocation(), fileName);
  }

  public String getUserThemeDirectory() {
    return pathControl.appendDirectory(getConfigDirLocation(),
        ConfigDefinitions.USER_THEME_DIR);
  }

}
