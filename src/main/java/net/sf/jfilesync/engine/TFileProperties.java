/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
 * changed by: Raik Nagel <raik.nagel@uni-bayreuth.de>
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
 * $Id: TFileProperties.java,v 1.14 2006/08/09 22:18:40 hunold Exp $
 */

package net.sf.jfilesync.engine;

import java.math.BigInteger;

import net.sf.jfilesync.prop.LanguageBundle;

/**
 * This class is basically for storing properties of files. We need that to
 * build a layer on top of the file property classes of different protocol
 * implementations.
 */
public class TFileProperties implements Cloneable {

  public static final int TFILE_PROPERTY_NAME = 0, TFILE_PROPERTY_SIZE = 1,
      TFILE_PROPERTY_MOD_TIME = 2, TFILE_PROPERTY_ABSOLUTE_NAME = 3;

  // these are the properties that can be delegated to
  // GUI components

  private static final int propertyOrder[] = {
      TFileProperties.TFILE_PROPERTY_NAME, TFileProperties.TFILE_PROPERTY_SIZE,
      TFileProperties.TFILE_PROPERTY_MOD_TIME };

  private String fileName;
  private String absoluteFileName;

  private BigInteger fileSize;
  private long fileModTime;
  private boolean directoryFlag;
  private boolean linkFlag;
  private boolean hiddenFlag;
  private TFileAttributes attr = new TFileAttributes();

  private boolean selectedForSync;

  // how many of them can be used somewhere (GUI)
  private static final int NUM_OF_PUBLISHABLE_PROPERTIES = propertyOrder.length;

  public TFileProperties() {
  }

  /**
   * We don't want to publish all. GUI elements want to know this number at the
   * time of their creation.
   */
  public static int getNumberOfPublishableProperties() {
    return NUM_OF_PUBLISHABLE_PROPERTIES;
  }

  public final void setFileName(final String fName) {
    this.fileName = fName;
  }

  public final String getFileName() {
    return this.fileName;
  }

  public final void setAbsoluteFileName(final String name) {
    this.absoluteFileName = name;
  }

  public final String getAbsoluteFileName() {
    return this.absoluteFileName;
  }

  public final void setFileSize(final BigInteger fSize) {
    this.fileSize = fSize;
  }

  public final BigInteger getFileSize() {
    return fileSize;
  }

  public final void setFileModTime(final long fModTime) {
    this.fileModTime = fModTime;
  }

  public final long getFileModTime() {
    return fileModTime;
  }

  public final void setDirectoryFlag(final boolean flag) {
    this.directoryFlag = flag;
  }

  public boolean isDirectory() {
    return directoryFlag;
  }

  public void setLinkFlag(final boolean flag) {
    this.linkFlag = flag;
  }

  public boolean isLink() {
    return linkFlag;
  }

  public void setHiddenFlag(final boolean flag) {
    hiddenFlag = flag;
  }

  public boolean isHidden() {
    return hiddenFlag;
  }

  public static int[] getPublishableProperties() {
    return propertyOrder;
  }

  public static String getFilePropertyName(final int propertyId) {
    switch (propertyId) {
    case TFileProperties.TFILE_PROPERTY_NAME:
      return LanguageBundle.getInstance()
          .getMessage("component.filename.label");
    case TFileProperties.TFILE_PROPERTY_MOD_TIME:
      return LanguageBundle.getInstance().getMessage("component.filemod.label");
    case TFileProperties.TFILE_PROPERTY_SIZE:
      return LanguageBundle.getInstance()
          .getMessage("component.filesize.label");
    default:
      return "Unsupported file property";
    }
  }

  public Object clone() {
    final TFileProperties cloneProp = new TFileProperties();
    cloneProp.setFileName(fileName);
    cloneProp.setAbsoluteFileName(getAbsoluteFileName());
    cloneProp.setFileModTime(fileModTime);
    cloneProp.setFileSize(fileSize);
    cloneProp.setDirectoryFlag(directoryFlag);
    cloneProp.setLinkFlag(linkFlag);
    cloneProp.setHiddenFlag(hiddenFlag);
    if (attr != null) {
      cloneProp.setAttributes((TFileAttributes) attr.clone());
    }
    return cloneProp;
  }

  public TFileAttributes getAttributes() {
    return attr;
  }

  public void setAttributes(final TFileAttributes attrs) {
    this.attr = attrs;
  }

  public boolean hasPermissions() {
    boolean ret = false;
    if (attr != null && attr.getPermissions() != -1) {
      ret = true;
    }
    return ret;
  }

  public void toggleSelectedForSync() {
    selectedForSync = !selectedForSync;
  }

  public boolean isSelectedForSync() {
    return selectedForSync;
  }

}
