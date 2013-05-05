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
 * $Id: GnuPermissionParser.java,v 1.4 2005/09/23 21:55:46 hunold Exp $
 */
package net.sf.jfilesync.plugins.net.items;

public class GnuPermissionParser {

  public static final int S_IFMT = 0xF000;
  public static final int S_IFSOCK = 0xC000;
  public static final int S_IFLNK = 0xA000;
  public static final int S_IFREG = 0x8000;
  public static final int S_IFBLK = 0x6000;
  public static final int S_IFDIR = 0x4000;
  public static final int S_IFCHR = 0x2000;
  public static final int S_IFIFO = 0x1000;
  public final static int S_ISUID = 0x800;
  public final static int S_ISGID = 0x400;
  public final static int S_IRUSR = 0x100;
  public final static int S_IWUSR = 0x80;
  public final static int S_IXUSR = 0x40;
  public final static int S_IRGRP = 0x20;
  public final static int S_IWGRP = 0x10;
  public final static int S_IXGRP = 0x08;
  public final static int S_IROTH = 0x04;
  public final static int S_IWOTH = 0x02;
  public final static int S_IXOTH = 0x01;

  /*
     b     Block special file.
     c     Character special file.
     d     Directory.
     l     Symbolic link.
     s     Socket link.
     p     FIFO.
     -     Regular file.
   */
  
  public static int parserPermissionString(final String perm) {
    int p = 0;
    
    //System.out.println("perm : " + perm);
    if( perm.charAt(0) == 'd' ) {
      p |= S_IFDIR;
    }
    else if( perm.charAt(0) == 'l' ) {
      p |= S_IFLNK;
    }
    else if( perm.charAt(0) == 'c' ) {
      p |= S_IFCHR;
    }
    else if( perm.charAt(0) == 'b' ) {
      p |= S_IFBLK;
    }
    else if( perm.charAt(0) == 's' ) {
      p |= S_IFSOCK;
    }
    else if( perm.charAt(0) == 'p' ) {
      p |= S_IFIFO;
    }
    
    
    p |= ((perm.charAt(1) == 'r') ? S_IRUSR : 0);
    p |= ((perm.charAt(2) == 'w') ? S_IWUSR : 0);
    p |= ((perm.charAt(3) == 'x') ? S_IXUSR : 0);
    p |= ((perm.charAt(4) == 'r') ? S_IRGRP : 0);
    p |= ((perm.charAt(5) == 'w') ? S_IWGRP : 0);
    p |= ((perm.charAt(6) == 'x') ? S_IXGRP : 0);
    p |= ((perm.charAt(7) == 'r') ? S_IROTH : 0);
    p |= ((perm.charAt(8) == 'w') ? S_IWOTH : 0);
    p |= ((perm.charAt(9) == 'x') ? S_IXOTH : 0);

    return p;
  }
  
  public static String getPermissionString(final int iperm) {
    char[] perm = { '0', '0', '0' };
    
    int uperm = 0;
    if( (iperm & S_IRUSR) > 0 ) {
      uperm += 4;
    }
    if( (iperm & S_IWUSR) > 0 ) {
      uperm += 2;
    }
    if( (iperm & S_IXUSR) > 0 ) {
      uperm += 1;
    }
    perm[0] = (char)(uperm + 48);
    
    int gperm = 0;
    if( (iperm & S_IRGRP) > 0 ) {
      gperm += 4;
    }
    if( (iperm & S_IWGRP) > 0 ) {
      gperm += 2;
    }
    if( (iperm & S_IXGRP) > 0 ) {
      gperm += 1;
    }
    perm[1] = (char)(gperm + 48);

    int operm = 0;
    if( (iperm & S_IROTH) > 0 ) {
      operm += 4;
    }
    if( (iperm & S_IWOTH) > 0 ) {
      operm += 2;
    }
    if( (iperm & S_IXOTH) > 0 ) {
      operm += 1;
    }
    perm[2] = (char)(operm + 48);

    return new String(perm);
  }
  
}
