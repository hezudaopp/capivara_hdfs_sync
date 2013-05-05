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
 * $Id: TPathWindows.java,v 1.6 2006/06/11 21:00:12 hunold Exp $
 */

package net.sf.jfilesync.util;


public class TPathWindows extends TPathControlBase {

  private final String[] roots;

  public TPathWindows(final String[] roots, final String fileSep) {
    super(fileSep);
    this.roots = roots;
  }

  public boolean isRoot(final String path) {
    if (path.length() < 2) {
      return false;
    } else if (path.charAt(1) != ':') {
      return false;
    } else if (!Character.isLetter(path.charAt(0))) {
      return false;
    } else {
      // find out if current path matches any root
      for (int i = 0; i < roots.length; i++) {
        String normroot = roots[i].toUpperCase();
        if (roots[i].compareTo(path) == 0
            || roots[i].compareTo(path + getFileSeparator()) == 0
            || normroot.compareTo(path.toUpperCase()) == 0
            || normroot.compareTo(path.toUpperCase() + getFileSeparator()) == 0) {
          return true;
        }
      }
    }
    return false;
  }

  public String getPathLevelUp(String path) {
    String upPath = null;

    if (path.equals(".")) {
      return path;
    } else if (isRoot(path)) {
      return path;
    } else {
      upPath = path;
      if (path.endsWith(getFileSeparator()))
        upPath = upPath.substring(0, upPath.length() - getFileSeparator().length());

      upPath = upPath.substring(0, upPath.lastIndexOf(getFileSeparator()) + 1);
      if (!isRoot(upPath))
        upPath = trimPathString(upPath);
    }

    return upPath;
  }

  public String trimPathString(String path) {
    String tpath = path;

    if (isRoot(path))
      return tpath;

    if (tpath.endsWith(getFileSeparator()))
      tpath = tpath.substring(0, tpath.lastIndexOf(getFileSeparator()));

    return tpath;
  }

  public String appendDirectory(String absolutePath, String path) {
    StringBuffer sbuf = new StringBuffer(trimPathString(absolutePath));
    if (isRoot(sbuf.toString())) {
      sbuf.append(path);
    } else {
      sbuf.append(getFileSeparator());
      sbuf.append(path);
    }
    return sbuf.toString();
  }

  public String getNetPath(String path) {
    return path.replaceAll("\\\\", "/");
  }

  public boolean startsWithRoot(String path) {
    for (int i = 0; i < roots.length; i++) {
      if (path.toUpperCase().startsWith(roots[i].toUpperCase())) {
        return true;
      }
    }
    return false;
  }

  public boolean pathEquals(String path1, String path2) {
    boolean hasroot1 = startsWithRoot(path1);
    boolean hasroot2 = startsWithRoot(path2);

    if (hasroot1 && hasroot2) {
      String root1 = getRoot(path1);
      String root2 = getRoot(path2);

      if (root1.toUpperCase().compareTo(root2.toUpperCase()) != 0) {
        return false;
      }

      if (plainpath(root1, path1).compareTo(plainpath(root2, path2)) == 0) {
        return true;
      }
    } else if (!hasroot1 && !hasroot2) {
      if (path1.compareTo(path2) == 0) {
        return true;
      }
    }

    return false;
  }

  public String getRoot(String path) {
    String root = "";
    String tpath = path.toUpperCase();

    if (!startsWithRoot(tpath)) {
      return root;
    }

    for (int i = 0; i < roots.length; i++) {
      if (path.startsWith(roots[i].toLowerCase())) {
        return roots[i].toLowerCase();
      }
      if (path.startsWith(roots[i].toUpperCase())) {
        return roots[i].toUpperCase();
      }
    }
    return root;
  }

  public String normalize(String path) {
    if (path.endsWith(":")) {
      path = path + getFileSeparator();
    }
    return path;
  }

//  public String getFileSep() {
//    return System.getProperty("file.separator");
//  }

  public String getPrintablePath(String currentPath) {
    String printable = currentPath.replaceAll("\\\\", "\\\\\\\\");
    return printable;
  }

}
