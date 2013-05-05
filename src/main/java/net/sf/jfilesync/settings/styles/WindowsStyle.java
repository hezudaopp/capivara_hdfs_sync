/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: WindowsStyle.java,v 1.3 2006/01/06 00:07:52 hunold Exp $
 */

package net.sf.jfilesync.settings.styles;

import java.awt.*;

import javax.swing.*;

import net.sf.jfilesync.gui.icons.TImageIconProvider;

public class WindowsStyle extends TStandardStyle
{
  public WindowsStyle()
  {
  }

  public Color getTableLineColorEven() {
    return Color.white;
  }

  public Color getTableLineColorOdd() {
    return Color.white;
  }

  public Color getTableFileNameEven() {
    return new Color(0xf3efef);
  }

  public Color getTableFileNameOdd() {
    return new Color(0xf3efef);
  }

  public ImageIcon getFolderImageIcon() {
    return TImageIconProvider.getInstance().getImageIcon(TImageIconProvider.FOLDER2_IMAGE);
  }

  public boolean showTableGrid() {
    return false;
  }

}
