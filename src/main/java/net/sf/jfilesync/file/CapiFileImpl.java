/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2009 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.file;

import java.util.ArrayList;
import java.util.List;

import net.sf.jfilesync.engine.TFileProperties;

/**
 * 
 * This class stores the common file properties like size, modification time,
 * permissions.
 * 
 * This class is supposed to be the replacement class for TFileProperties.
 * 
 * @author sascha
 * 
 */

public class CapiFileImpl implements CapiFile {

  private List<CapiFile> children;
  private final TFileProperties fileProps;

  public CapiFileImpl(TFileProperties properties) {
    if (properties == null) {
      throw new IllegalArgumentException("properties must not be null");
    }
    this.fileProps = properties;
  }

  public void addChild(final CapiFile file) {
    if (file == null) {
      throw new IllegalArgumentException("file is null");
    }
    if (children == null) {
      children = new ArrayList<CapiFile>();
    }
    children.add(file);
  }

  public boolean hasChildren() {
    return (children != null && children.size() > 0);
  }

}
