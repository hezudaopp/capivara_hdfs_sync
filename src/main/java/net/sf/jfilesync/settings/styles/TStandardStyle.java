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
 * $Id: TStandardStyle.java,v 1.4 2006/01/06 00:07:52 hunold Exp $
 */

package net.sf.jfilesync.settings.styles;

import java.awt.*;
import java.util.logging.Logger;

import javax.swing.*;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.settings.*;

public class TStandardStyle implements TStyleInterface
{

  private Font tableFont = new Font("SansSerif", Font.PLAIN, 12);
  private final static Logger LOGGER = Logger.getLogger(TStandardStyle.class.getName());
  
  
  public TStandardStyle() {
    
    try {
      final int tab_font_size = MainWin.config.getProgramSettings().getIntegerOption(
          TProgramSettings.OPTION_TAB_FONT_SIZE);
      tableFont = new Font(tableFont.getFamily(), tableFont.getStyle(), tab_font_size);
    } catch (SettingsTypeException ste) {
      LOGGER.severe(ste.getMessage());
    }
        
  }
  

  
  public Color getTableForegroundColor() {
    return SystemColor.textText;
  }

  public Color getTableBackgroundColor() {
    return SystemColor.text;
  }

  public Color getTableSelectionForegroundColor() {
    return SystemColor.textHighlightText;
  }

  public Color getTableSelectionBackGroundColor() {
    return SystemColor.textHighlight;
  }

  public Color getTableLineColorEven() {
    return SystemColor.white;
  }

  public Color getTableLineColorOdd() {
    return SystemColor.lightGray;
  }

  public Color getTableFileNameEven() {
    return SystemColor.white;
  }

  public Color getTableFileNameOdd() {
    return SystemColor.lightGray;
  }

  public ImageIcon getFolderImageIcon() {
    return TImageIconProvider.getInstance().getImageIcon(TImageIconProvider.FOLDER_IMAGE);
  }

  public boolean showTableGrid() {
    return true;
  }

  public Font getTableFont() {
    //return new Font("Helvetica", Font.PLAIN, 12);
    //return new Font("SansSerif", Font.PLAIN, 12);
    return tableFont;
  }

}
