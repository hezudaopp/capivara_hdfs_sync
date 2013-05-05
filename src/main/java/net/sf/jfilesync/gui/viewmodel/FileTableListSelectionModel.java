/*
 * capivara - Java File Synchronization
 *
 * Created on 25-Mar-2005
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
 * $Id: FileTableListSelectionModel.java,v 1.3 2005/08/19 21:29:02 hunold Exp $
 */
package net.sf.jfilesync.gui.viewmodel;

import javax.swing.DefaultListSelectionModel;

public class FileTableListSelectionModel 
  extends DefaultListSelectionModel 
{

  public void setSelectionInterval(int index0, int index1) {
    System.out.println("i0: " + index0 + " i1: " + index1);
    super.setSelectionInterval(index0, index1);
  }
}
