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
 * $Id: TPathControlBase.java,v 1.10 2006/06/11 21:00:12 hunold Exp $
 */

package net.sf.jfilesync.util;

/**
 * This class provides the basic functions for working with directories
 * on different systems. All sub-classes should override system specific
 * methods.
 */

public abstract class TPathControlBase implements TPathControlInterface {

  protected String root_str = "/";
  private String fileSeparator;
  
  public TPathControlBase(String fileSep) {
    this.fileSeparator = fileSep;
  }
  
  public String getFileSeparator() {
    return fileSeparator;
  }

  public boolean isRoot(String path) {
    if (root_str.compareTo(path) == 0)
      return true;
    else
      return false;
  }

  public String getPathLevelUp(String path) {
    String upPath = null;

    if (path.equals(".")) {
      return path;
    }
    if (isRoot(path)) {
      return path;
    }

    upPath = path;
    if (path.endsWith(fileSeparator)) {
      upPath = upPath.substring(0, upPath.length() - fileSeparator.length());
    }

    upPath = upPath.substring(0, upPath.lastIndexOf(fileSeparator) + 1);
    if (!isRoot(upPath)) {
      upPath = trimPathString(upPath);
    }

    return upPath;
  }

  public String trimPathString(String path) {
    String tpath = path;

    if (isRoot(path)) {
      return tpath;
    }

    if (tpath.endsWith(".")) {
      tpath = tpath.substring(0, tpath.length() - 2);
    }

    if (isRoot(tpath)) {
      return tpath;
    }

    if (tpath.endsWith(fileSeparator)) {
      tpath = tpath.substring(0, tpath.lastIndexOf(fileSeparator));
    }

    return tpath;
  }

  public String appendDirectory(String absolutePath, String path) {
    StringBuffer sbuf = new StringBuffer(trimPathString(absolutePath));
    if (isRoot(sbuf.toString())) {
      sbuf.append(path);
    } else {
      sbuf.append(fileSeparator);
      sbuf.append(path);
    }
    return sbuf.toString();
  }

  public String netAppendDirectory(String absolutePath, String path) {
    StringBuffer buffer = new StringBuffer(absolutePath);
    if (absolutePath.endsWith(fileSeparator)) {
      buffer.append(path);
    } else {
      buffer.append(fileSeparator);
      buffer.append(path);
    }
    return buffer.toString();
  }

  public String getNetPath(String path) {
    return path;
  }

  public boolean startsWithRoot(String path) {
    if (path.startsWith(root_str)) {
      return true;
    } else {
      return false;
    }
  }

  public String basename(String path) {
    String basename = path;

    if (isRoot(basename)) {
      return basename;
    }

    if (basename.endsWith(fileSeparator)) {
      basename = basename.substring(0, basename.length()
          - fileSeparator.length());
    }

    basename = basename.substring(basename.lastIndexOf(fileSeparator) + 1,
        basename.length());

    return basename;
  }

  public String basepath(String rootdir, String path, String basename) {
    String basepath = path;

    if (isRoot(basepath))
      return basepath;

    if (rootdir.compareTo(path) == 0)
      return "";

    if (basepath.startsWith(rootdir)) {
      basepath = basepath.substring(rootdir.length(), basepath.length());
    }

    if (basepath.startsWith(fileSeparator)) {
      basepath = basepath.substring(fileSeparator.length(), basepath.length());
    }

    if (basepath.endsWith(basename))
      basepath = basepath.substring(0, basepath.length() - basename.length());
    else if (basepath.endsWith(basename + fileSeparator)) {
      basepath = basepath.substring(0, basepath.length() - basename.length()
          - fileSeparator.length());
    }

    if (!isRoot(basepath) && basepath.endsWith(fileSeparator))
      basepath = basepath.substring(0, basepath.length()
          - fileSeparator.length());

    return basepath;
  }

  public String plainpath(String root, String path) {
    String plain = path;

    if (isRoot(plain))
      return plain;

    if (root.compareTo(plain) == 0)
      return "";

    if (plain.startsWith(root)) {
      plain = plain.substring(root.length(), plain.length());
    }

    if (plain.startsWith(fileSeparator)) {
      plain = plain.substring(fileSeparator.length(), plain.length());
    }

    return plain;
  }

  public String netPlainpath(String root, String path) {
    String plain = path;

    if (plain.compareTo("/") == 0) // net root
      return plain;

    if (root.compareTo(plain) == 0)
      return "";

    if (plain.startsWith(root)) {
      plain = plain.substring(root.length(), plain.length());
    }

    if (plain.startsWith(fileSeparator)) {
      plain = plain.substring(fileSeparator.length(), plain.length());
    }

    return plain;
  }

//  public boolean pathEquals(String path1, String path2) {
//    if (path1.compareTo(path2) == 0)
//      return true;
//    else
//      return false;
//  }

  public String getRoot(String path) {
    if (path != null && startsWithRoot(path)) {
      return root_str;
    } else {
      return "";
    }
  }

  public String normalize(String path) {
    // !TD!
    // but usually not much to do here
    return path;
  }

//  public abstract String getFileSep();
    
}
