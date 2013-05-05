/*
 * capivara - Java File Synchronization
 *
 * Created on 03-Jun-2005
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

import net.sf.jfilesync.gui.TControlCenter;

// structure to give all methods access to
// source and target CC

public class CopySettings {

  private TControlCenter source;
  private TControlCenter target;
  private boolean preserve_mtime;
  private boolean preserve_perm;
  private boolean change_target_perm;
  // private boolean follow_symlinks;
  private int target_dir_perm = -1;
  private int target_file_perm = -1;

  public void setSource(TControlCenter source) {
    this.source = source;
  }

  public TControlCenter getSource() {
    return source;
  }

  public void setTarget(TControlCenter target) {
    this.target = target;
  }

  public TControlCenter getTarget() {
    return target;
  }

  public void setPreserveMtime(boolean enable) {
    preserve_mtime = enable;
  }

  public boolean getPreserveMtime() {
    return preserve_mtime;
  }

  public void setPreservePerm(boolean enable) {
    preserve_perm = enable;
  }

  public boolean getPreservePerm() {
    return preserve_perm;
  }

  public void setCustomPerm(boolean enable) {
    change_target_perm = enable;
  }

  public boolean getCustomPerm() {
    return change_target_perm;
  }

  public void setFilePerm(int perm) {
    target_file_perm = perm;
  }

  public int getFilePerm() {
    return target_file_perm;
  }

  public void setDirPerm(int perm) {
    target_dir_perm = perm;
  }

  public int getDirPerm() {
    return target_dir_perm;
  }

}
