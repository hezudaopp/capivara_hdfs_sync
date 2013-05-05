/*
 * capivara - Java File Synchronization
 *
 * Created on 29-May-2005
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
 */
package net.sf.jfilesync.engine;

public class FilePermissions {

  
  /**
   S_IFMT     0170000   bitmask for the file type bitfields
   S_IFSOCK   0140000   socket
   S_IFLNK    0120000   symbolic link
   S_IFREG    0100000   regular file
   S_IFBLK    0060000   block device
   S_IFDIR    0040000   directory
   S_IFCHR    0020000   character device
   S_IFIFO    0010000   fifo
   S_ISUID    0004000   set UID bit
   S_ISGID    0002000   set GID bit (see below)
   S_ISVTX    0001000   sticky bit (see below)
   S_IRWXU    00700     mask for file owner permissions
   S_IRUSR    00400     owner has read permission
   S_IWUSR    00200     owner has write permission
   S_IXUSR    00100     owner has execute permission
   S_IRWXG    00070     mask for group permissions
   S_IRGRP    00040     group has read permission
   S_IWGRP    00020     group has write permission
   S_IXGRP    00010     group has execute permission
   S_IRWXO    00007     mask for permissions for others (not in group)
   S_IROTH    00004     others have read permission
   S_IWOTH    00002     others have write permission
   S_IXOTH    00001     others have execute permission 
   */
  
  /*
   * POSIX-compliant octal values of file permissions 
   */
  
  public final static int S_IFSOCK = 0140000;
  public final static int S_IFLNK = 0120000;
  public final static int S_IFREG = 0100000;
  public final static int S_IFBLK = 0060000;
  public final static int S_IFDIR = 0040000;
  public final static int S_IFCHR = 0020000;
  public final static int S_IFIFO = 0010000;
  public final static int S_ISUID = 0004000;
  public final static int S_ISGID = 0002000;
  public final static int S_ISVTX = 0001000;
  public final static int S_IRUSR = 0400;
  public final static int S_IWUSR = 0200;
  public final static int S_IXUSR = 0100;
  public final static int S_IRGRP = 0040;
  public final static int S_IWGRP = 0020;
  public final static int S_IXGRP = 0010;
  public final static int S_IROTH = 0004;
  public final static int S_IWOTH = 0002;
  public final static int S_IXOTH = 0001;

  private int permissions = -1;

  public FilePermissions(int permissions) {
    this.permissions = permissions;
  }

  public int getValue() {
    return permissions;
  }

  public void setValue(int v) {
    permissions = v;
  }

  public String toString() {
    char[] ret_ar = new String("----------").toCharArray();

    if ((permissions & S_IFLNK) == S_IFLNK) {
      ret_ar[0] = 'l';
    } else {
      if ((permissions & S_IFDIR) == S_IFDIR) {
        ret_ar[0] = 'd';
      }
      if ((permissions & S_IFIFO) == S_IFIFO) {
        ret_ar[0] = 'p';
      }
      if ((permissions & S_IFSOCK) == S_IFSOCK) {
        ret_ar[0] = 's';
      }
      if ((permissions & S_IFCHR) == S_IFCHR) {
        ret_ar[0] = 'c';
      }
      if ((permissions & S_IFBLK) == S_IFBLK) {
        ret_ar[0] = 'b';
      }
    }

    if ((permissions & S_IRUSR) == S_IRUSR) {
      ret_ar[1] = 'r';
    }
    if ((permissions & S_IWUSR) == S_IWUSR) {
      ret_ar[2] = 'w';
    }

    if ((permissions & S_ISUID) == S_ISUID) {
      if ((permissions & S_IXUSR) == S_IXUSR) {
        ret_ar[3] = 's';
      } else {
        ret_ar[3] = 'S';
      }
    } else {
      if ((permissions & S_IXUSR) == S_IXUSR) {
        ret_ar[3] = 'x';
      }
    }

    if ((permissions & S_IRGRP) == S_IRGRP) {
      ret_ar[4] = 'r';
    }
    if ((permissions & S_IWGRP) == S_IWGRP) {
      ret_ar[5] = 'w';
    }

    if ((permissions & S_ISGID) == S_ISGID) {
      if ((permissions & S_IXGRP) == S_IXGRP) {
        ret_ar[6] = 's';
      } else {
        ret_ar[6] = 'S';
      }
    } else {
      if ((permissions & S_IXGRP) == S_IXGRP) {
        ret_ar[6] = 'x';
      }
    }

    if ((permissions & S_IROTH) == S_IROTH) {
      ret_ar[7] = 'r';
    }
    if ((permissions & S_IWOTH) == S_IWOTH) {
      ret_ar[8] = 'w';
    }

    if ((permissions & S_ISVTX) == S_ISVTX) {
      if ((permissions & S_IXOTH) == S_IXOTH) {
        ret_ar[9] = 't';
      } else {
        ret_ar[9] = 'T';
      }
    } else {
      if ((permissions & S_IXOTH) == S_IXOTH) {
        ret_ar[9] = 'x';
      }
    }

    return new String(ret_ar);
  }

  public String getOctalString() {

    int usr = 0;
    int grp = 0;
    int oth = 0;

    usr += ((permissions & S_IRUSR) == S_IRUSR) ? 4 : 0;
    usr += ((permissions & S_IWUSR) == S_IWUSR) ? 2 : 0;
    usr += ((permissions & S_IXUSR) == S_IXUSR) ? 1 : 0;

    grp += ((permissions & S_IRGRP) == S_IRGRP) ? 4 : 0;
    grp += ((permissions & S_IWGRP) == S_IWGRP) ? 2 : 0;
    grp += ((permissions & S_IXGRP) == S_IXGRP) ? 1 : 0;

    oth += ((permissions & S_IROTH) == S_IROTH) ? 4 : 0;
    oth += ((permissions & S_IWOTH) == S_IWOTH) ? 2 : 0;
    oth += ((permissions & S_IXOTH) == S_IXOTH) ? 1 : 0;

    return usr + "" + grp + "" + oth;
  }

}
