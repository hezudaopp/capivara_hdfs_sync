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
 * $Id: ConfigDefinitions.java,v 1.109 2006/09/06 09:04:02 hunold Exp $
 */

package net.sf.jfilesync.settings;

import java.util.ArrayList;
import java.util.List;

public class ConfigDefinitions {
    
  public static String PROGRAM_NAME = "capivara";
  public static String PROGRAM_VERSION = "0.8.8";
  public static String COPYRIGHT_YEARS = "2003-2009";

  public static final String CONFIG_FILE = "config.xml";
  public static final String CONFIG_DIR = ".jfilesync";
  public static final String EXPRESSION_FILE = "capiregex.xml";
  public static final String SYNC_PROJECT_DIR = "projects";
  public static final String USER_THEME_DIR  = "themes";

  public static final String LICENSE_FILE = "COPYING";

  //public static String BUILD_VERSION =  "unreleased" ;
  public static String BUILD_DATE    =  "no date" ;

  public static final int STYLE_KDE     = 0;
  public static final int STYLE_WINDOWS = 1;
  public static final int DEFAULT_STYLE = STYLE_WINDOWS;

  public static final int WAIT_FOR_CONNECTION_TIMEOUT_S = 5;	// 30 to 5

  public static final String LINK_PREFIX = "@";

  public static final int EDITOR_MAX_FILE_SIZE_KB = 100;

  public static final int DEFAULT_PERMISSIONS_DIR  = 040700;
  public static final int DEFAULT_PERMISSIONS_FILE = 0100700;

  public static final int TAB_TITLE_LENGTH = 12;
  public static final int TAB_FONT_SIZE    = 12;

  public static final String SPLASH_IMAGE = "capivara_splash_550.png";

  public static final List<String> MAC_EXCLUDE_LIST =
    new ArrayList<String>();

  static {
    MAC_EXCLUDE_LIST.add(".*\\.DS_Store");
    MAC_EXCLUDE_LIST.add("(.*/\\._.*)|(^\\._.*)");
  }

}
