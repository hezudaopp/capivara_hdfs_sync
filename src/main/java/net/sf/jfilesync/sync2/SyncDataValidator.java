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

public class SyncDataValidator implements ISyncDataValidator {

  private boolean caseInsensitive;
  
  public SyncDataValidator() {
    
  }
  
  public void enableCaseInsensitive(final boolean enabled) {
    this.caseInsensitive = enabled;
  }
  
  public ISyncDataValidationError validateTree(final SyncTree tree) {
    ISyncDataValidationError error = null;
    
    if( caseInsensitive ) {
      error = checkForFilesWithSameName(tree);
    }
    
    return error;
  }

  private ISyncDataValidationError checkForFilesWithSameName(final SyncTree tree) {
    ISyncDataValidationError error = null;

    final SyncFile root = tree.getRoot();
    if( root != null ) {
      final List<SyncFile> files = getFilesWithSameNameInOneDir(root);
      if( files.size() > 0 ) {
        error = new SyncDataCaseInsensitiveValidationError();
        for(SyncFile f : files) {
          error.addAffectedFile(f);
        }
      }
    }
        
    return error;
  }

  private List<SyncFile> getFilesWithSameNameInOneDir(final SyncFile dir) {
    
    final List<SyncFile> dupNameFiles = new ArrayList<SyncFile>();
    
    SyncFile[] children = dir.getChildren();
    if( children != null ) {
      
      for(int i=0; i<children.length; i++) {
        
        final SyncFile f = children[i];
        boolean foundFileWithSameName = false;
        
        if( f.isExcluded() ) {
          continue;
        }
        
        for(int j=i+1; j<children.length; j++) {
          final SyncFile f2 = children[j];
                    
          final String fileName1 = f.getFile().getFileName().toLowerCase();
          final String fileName2 = f2.getFile().getFileName().toLowerCase();
          if( fileName1.compareTo(fileName2) == 0 ) {
            if( ! foundFileWithSameName ) {
              dupNameFiles.add(f);
              foundFileWithSameName = true;
            }
            dupNameFiles.add(f2);
          }
          
        }
        
      }      
    }
    
    for(final SyncFile child : dir.getChildren() ) {
      if( child.getFile().isDirectory() ) {
        dupNameFiles.addAll( getFilesWithSameNameInOneDir(child) );
      }
    }
        
    return dupNameFiles;
  }

}
