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
 * $Id: TStyleChooser.java,v 1.4 2005/12/30 21:59:06 hunold Exp $
 */

package net.sf.jfilesync.settings;

import java.util.logging.Logger;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.settings.styles.KdeStyle;
import net.sf.jfilesync.settings.styles.TStandardStyle;
import net.sf.jfilesync.settings.styles.WindowsStyle;

public class TStyleChooser {

  private final static Logger LOGGER = Logger.getLogger(TStyleChooser.class
      .getName());

  private static TStyleInterface style = null;

  public TStyleChooser() {
  }

  public static TStyleInterface getStyle() {
    if( style == null ) {
      style = readStyle();
    }
    return style;
  }

  private static TStyleInterface readStyle() {
    TStyleInterface style = new TStandardStyle();
    int styleId = -1;
    try {
      styleId = MainWin.config.getProgramSettings().getIntegerOption(
          TProgramSettings.OPTION_TABLE_STYLE);
      switch (styleId) {
      case ConfigDefinitions.STYLE_KDE:
        style = new KdeStyle();
        break;
      case ConfigDefinitions.STYLE_WINDOWS:
        style = new WindowsStyle();
        break;
      default:
      }
    } catch (SettingsTypeException e) {
      LOGGER.warning(e.getMessage());
    }
    return style;
  }

}
