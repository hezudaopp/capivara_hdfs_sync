/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id$
 */
package net.sf.jfilesync.service.unify;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.util.TPathControlInterface;

public class UnifyFile extends BasicFile {

  private byte[] messageDigest;
  private byte[] messageSmallDigest;
  private List<UnifyFile> duplicates = new ArrayList<UnifyFile>();

  private boolean keepIt = false;

  //private final Logger LOGGER = Logger.getLogger(UnifyFile.class.getName());

  public UnifyFile(final TFileProperties file, final String rootPath,
      final TPathControlInterface pathControl) {
    super(file, rootPath, pathControl);
    if( isDirectory() ) {
      keepIt = true;
    }
  }

  public byte[] getMessageDigest() {
    return messageDigest;
  }

  public void setMessageDigest(final byte[] messageDigest) {
    this.messageDigest = messageDigest;
  }

  public boolean hasSameDigest(final byte[] digest) {
    return MessageDigest.isEqual(messageDigest, digest);
  }

  public boolean hasDigest() {
      return (messageDigest != null);
  }

  public void setMessagePreDigest(byte[] digest) {
      this.messageSmallDigest = digest;
  }

  public boolean hasSamePreDigest(final byte[] digest) {
      return MessageDigest.isEqual(messageSmallDigest, digest);
  }

  public boolean hasPreDigest() {
        return (messageSmallDigest != null);
  }

  public byte[] getPreDigest() {
      return messageSmallDigest;
  }

  /**
   * this method is required since only comparing the digest is not
   * enough to detect duplicates
   * if a file has a size of 0 then the digest is useless
   * in this case we compare the file names
   * @param otherFile
   * @return
   */

  public boolean isDuplicateOf(final UnifyFile otherFile) {
    boolean duplicate = false;

    if( getFileSize().equals(otherFile.getFileSize()) ) {
      if( getFileSize().equals(BigInteger.ZERO) ) {
        if( getFileName().equals(otherFile.getFileName())) {
          duplicate = true;
        }
      } else {
        duplicate = hasSameDigest(otherFile.getMessageDigest());
      }
    }

    return duplicate;
  }

  public void addDuplicate(final UnifyFile file) {
    duplicates.add(file);
  }

  public UnifyFile[] getDuplicates() {
    return duplicates.toArray(new UnifyFile[duplicates.size()]);
  }

  public void setKeepIt(boolean keepIt) {
    this.keepIt = keepIt;
  }

  public boolean getKeepIt() {
    return keepIt;
  }

  public boolean hasDuplicates() {
    return ( duplicates.size() > 0 );
  }

  public String getAbsoluteTargetFileName(final String targetPath) {
    return getPathControl()
        .appendDirectory(targetPath, getRelativePathToRoot());
  }

  public UnifyFile clone() {
    final UnifyFile fileClone = new UnifyFile(getTFileProperties(),
        getRootPath(), getPathControl());
    return fileClone;
  }

  /**
   * @return returns true if file is dir and contains a file with duplicates
   */
  public DuplicatesInfo getDuplicatesInfo() {
    /*
     * This can be done with a cache for info objects.
     * We also need a dirty flag which is set when keepIt/duplicates change.
     * TODO implements caching
     */
    DuplicatesInfo dupInfo = new DuplicatesInfo();

    if( isDirectory() ) {
      for( BasicFile child : getChildren() ) {
        UnifyFile uf = (UnifyFile)child;
        if( uf.hasDuplicates() ) {
          dupInfo.setWithDuplicates(true);
          if( uf.getKeepIt() == false ) {
            dupInfo.setWithDuplicatesToDelete(true);
            break;
          }
        } else {
          if( uf.isDirectory() ) {
            DuplicatesInfo subInfo = uf.getDuplicatesInfo();
            dupInfo.setWithDuplicates( dupInfo.isWithDuplicates() | subInfo.isWithDuplicates() );
            dupInfo.setWithDuplicatesToDelete( dupInfo.isWithDuplicatesToDelete() | subInfo.isWithDuplicatesToDelete() );
          }
        }
      }
    }

    return dupInfo;
  }

  public void keepAllChildren(boolean deleteDuplicates) {
    if( ! isDirectory() ) {
      return;
    }
    if (deleteDuplicates) {
      keepAllChildrenAndDeleteDuplicates(this);
    } else {
      for (BasicFile child : getChildren()) {
        UnifyFile uf = (UnifyFile) child;
        if (uf.isDirectory()) {
          uf.keepAllChildren(deleteDuplicates);
        } else {
          uf.setKeepIt(true);
        }
      }
    }
  }

  private void keepAllChildrenAndDeleteDuplicates(final BasicFile root) {
    if( ! isDirectory() ) {
      return;
    }

    for (BasicFile child : getChildren()) {
      UnifyFile uf = (UnifyFile) child;
      if (uf.isDirectory()) {
        uf.keepAllChildrenAndDeleteDuplicates(root);
      } else {
        uf.setKeepIt(true);
        for(UnifyFile duplicate : uf.getDuplicates() ) {
          if( root.hasChild(duplicate) ) {
            duplicate.setKeepIt(true);
          } else {
            duplicate.setKeepIt(false);
          }
        }
      }
    }
  }



}
