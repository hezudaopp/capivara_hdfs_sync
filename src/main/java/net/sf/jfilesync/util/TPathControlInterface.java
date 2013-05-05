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
 */

package net.sf.jfilesync.util;

public interface TPathControlInterface {

  public boolean isRoot(String path);

  public String getPathLevelUp(String path);

  public String trimPathString(String path);

  public String appendDirectory(String absolutePath, String path);

  public String getNetPath(String path);

  public String netAppendDirectory(String absolutePath, String path);

  public boolean startsWithRoot(String path);

  /**
   * returns the last object in a path like /a/b/c
   * The object <<c>> can be a filename or a name of a directory.
   * @param path
   * @return from /a/b/c(/) -> c
   */

  public String basename(String path);

  /**
   * returns b if the root dir is /a and path is /a/b/c and basename c
   *
   * @param rootdir
   * @param path
   * @param basename
   * @return basepath
   */
  public String basepath(String rootdir, String path, String basename);

  /**
   * root = /a  path = /a/b/c  returns b/c
   * there will be no file separator at the start of the string
   *
   * @param root
   * @param path
   * @return path without string that was declared to be root
   */
  public String plainpath(String root, String path);

  /**
   * the same functionality as plain path but for network protocols
   * like FTP, SFTP where the folder root is "/"
   *
   * @param root
   * @param path
   * @return path without string that was passed to be the root
   */

  public String netPlainpath(String root, String path);

  /**
   * checks if 2 paths are referring to the same file/directory
   * no matter what case the roots are in
   * @param path1
   * @param path2
   * @return returns true if path1 equals path2
   */
//  public boolean pathEquals(String path1, String path2);

  /**
   * returns the current root of a given path
   * @param path
   * @return the root of path or an empty string if path does not have a root
   */
  public String getRoot(String path);

  /**
   * returns a valid path for a given system, if the path exists is not checked
   */
  public String normalize(String path);
  
  public String getFileSeparator();
  //public String getFileSep();

  /**
   * 
   * @param currentPath
   * @return a path that can be printed on the current platform
   * e.g. on Windows we need to substitute \ with \\
   */
  
  public String getPrintablePath(String currentPath);
}
