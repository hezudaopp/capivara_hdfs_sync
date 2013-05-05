/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.sync2;

import java.util.ArrayList;
import java.util.List;

import net.sf.jfilesync.prop.LanguageBundle;

public class SyncDataCaseInsensitiveValidationError implements
    ISyncDataValidationError {

  private final List<SyncFile> affectedFileList = new ArrayList<SyncFile>();

  public void addAffectedFile(final SyncFile file) {
    this.affectedFileList.add(file);
  }

  public List<SyncFile> getAffectedFileList() {
    return affectedFileList;
  }

  public String getMessage() {
    final StringBuilder buf = new StringBuilder();
    final int MAX_LIST_LENGTH = 10; // just to limit list size
    
    buf.append("@todo@ : files with same name");
    buf.append("\n");
    
    int fileCount = 0;
    for(final SyncFile f : affectedFileList) {
      buf.append(f.getRelativePath());
      buf.append("\n");
      fileCount++;
      if( fileCount >= MAX_LIST_LENGTH ) {
        break;
      }
    }
    
    buf.append(LanguageBundle.getInstance().getMessage("label.continue"));
    
    return buf.toString();
  }

}
