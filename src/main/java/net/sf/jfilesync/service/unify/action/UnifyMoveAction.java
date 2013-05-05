/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.service.unify.action;

import java.awt.Component;

import javax.swing.JLabel;

import net.sf.jfilesync.service.unify.UnifyFile;


public class UnifyMoveAction extends UnifyAction {

  private final String targetPath;

  public UnifyMoveAction(UnifyFile file, String targetPath) {
    super(file);
    this.targetPath = targetPath;
  }

//  @Override
//  public Component getActionComponent() {
//    final JLabel label = new JLabel(getUnifyFile().getRelativePathToRoot()
//        + " -> " + getTargetFileName());
//    return label;
//  }

  public String getTargetFileName() {
    return getUnifyFile().getAbsoluteTargetFileName(targetPath);
  }

}
