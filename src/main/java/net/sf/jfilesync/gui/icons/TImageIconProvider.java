/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TImageIconProvider.java,v 1.11 2006/05/04 21:45:35 hunold Exp $
 */

package net.sf.jfilesync.gui.icons;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.swing.ImageIcon;

import net.sf.jfilesync.MainWin;



public class TImageIconProvider {

  private static final String iconPath = "gui/icons";
  private static TImageIconProvider iprovider;

  public static final int
    BUTTON_OPEN_FILE     =  0,
    BUTTON_CONNECTED     =  1,
    BUTTON_DISCONNECTED  =  2,
    BUTTON_CONNECTING    =  3,
    BUTTON_DIR_UP        =  4,
    BUTTON_MKDIR         =  5,
    BUTTON_DELETE        =  6,
    BUTTON_START_SHELL   =  7,
    BUTTON_RELOAD_DIR    =  8,
    ICON_ARROW_UP        =  9,
    ICON_ARROW_DOWN      = 10,
    FOLDER_IMAGE         = 11,
    OPTION_GUI           = 12,
    BUTTON_COMPARE       = 13,
    BUTTON_SYNC          = 14,
    OPTION_PLUGINS       = 15,
    OPTION_LOOKFEEL      = 16,
    OPTION_LANGUAGE      = 17,
    OPTION_KEYBOARD      = 18,
    OPTION_FILETABLE     = 19,
    OPTION_FILEEDIT      = 20,
    FRAME_ICON           = 21,
    FOLDER2_IMAGE        = 22,
    BUTTON_COPY          = 23,
    LANGUAGE_SPLASH      = 24,
    OPTION_CON_PLUGINS   = 25,
    CANCEL_WORKER_ICON   = 26,
    ERROR_CRITICAL_ICON  = 50,
    ADD_ICON             = 70,
    DEL_ICON             = 71,
    SAVE_ICON            = 72,
    LEFT_ARROW           = 73,
    RIGHT_ARROW          = 74,
    SHREDDER_ICON        = 75,
    EDIT_ICON            = 76,
    RIGHT_ARROW_SAVE     = 77,
    LEFT_ARROW_SAVE      = 78,
    BOOKMARK_ICON        = 79,
    SYNC_ACTION_COPY_RIGHT = 80,
    SYNC_ACTION_COPY_LEFT  = 81,
    SYNC_ACTION_DELETE     = 82,
    SYNC_DIRECTION_LEFT    = 83,
    SYNC_DIRECTION_RIGHT   = 84,
    SYNC_DIRECTION_BI      = 85,
    SYNC_DIRECTION_NOOP    = 86
    ;

  private final static HashMap<Integer, String> iconHash = new HashMap<Integer, String>();

  private TImageIconProvider() {

    insert(BUTTON_OPEN_FILE, "openFile.png");
    insert(BUTTON_CONNECTED, "connected.png");
    insert(BUTTON_DISCONNECTED, "disconnected.png");
    insert(BUTTON_CONNECTING, "connecting.png");
    insert(BUTTON_DIR_UP, "up.png");
    insert(BUTTON_MKDIR, "folder_new.png");
    insert(BUTTON_DELETE, "edittrash.png");
    insert(BUTTON_START_SHELL, "konsole.png");
    insert(BUTTON_RELOAD_DIR, "reload.png");
    insert(BUTTON_COPY, "sftp_small_gray.jpg");
    insert(BUTTON_COMPARE, "compare.png");
    insert(BUTTON_SYNC, "sync.png");

    insert(ICON_ARROW_UP, "1uparrow.png");
    insert(ICON_ARROW_DOWN, "1downarrow.png");

    insert(FOLDER_IMAGE, "folder_orange.png");
    insert(FOLDER2_IMAGE, "folder_win.png");

    insert(OPTION_GUI, "gui.png");
    insert(OPTION_PLUGINS, "software.png");
    insert(OPTION_LOOKFEEL, "style.png");
    insert(OPTION_LANGUAGE, "language.png");
    insert(OPTION_KEYBOARD, "keybinding.png");
    insert(OPTION_FILETABLE, "filetable.png");
    insert(OPTION_FILEEDIT, "fileedit.png");
    insert(OPTION_CON_PLUGINS, "apacheconf.png");
    insert(FRAME_ICON, "capi_icon8-16.png");
    insert(LANGUAGE_SPLASH, "splash_logo.jpg");
    insert(CANCEL_WORKER_ICON, "cancel_worker.png");

    insert(ERROR_CRITICAL_ICON, "messagebox_critical.png");

    insert(ADD_ICON, "add_button.png");
    insert(DEL_ICON, "del_button.png");
    insert(SAVE_ICON, "save_all.png");
    insert(LEFT_ARROW, "previous.png");
    insert(RIGHT_ARROW, "next.png");
    insert(SHREDDER_ICON, "editshred.png");
    insert(EDIT_ICON, "edit.png");
    insert(RIGHT_ARROW_SAVE, "right_arrow_save.png");
    insert(LEFT_ARROW_SAVE, "left_arrow_save.png");

    insert(BOOKMARK_ICON, "stock-star.png");

    insert(SYNC_ACTION_COPY_LEFT, "copy_left_arrow.png");
    insert(SYNC_ACTION_COPY_RIGHT, "copy_right_arrow.png");
    insert(SYNC_ACTION_DELETE, "delete_cross.png");
    insert(SYNC_DIRECTION_LEFT, "arrow_left.png");
    insert(SYNC_DIRECTION_RIGHT, "arrow_right.png");
    insert(SYNC_DIRECTION_BI, "arrow_bidirect.png");
    insert(SYNC_DIRECTION_NOOP, "question_mark.png");
  }

  public static TImageIconProvider getInstance() {
    if (iprovider == null) {
      iprovider = new TImageIconProvider();
    }
    return iprovider;
  }


  private void insert(int buttonID, String filename) {
    iconHash.put(new Integer(buttonID), filename);
  }

  public ImageIcon getImageIcon(int iconID) {
    String iconRes = iconPath;

    if (!iconHash.containsKey(new Integer(iconID))) {
      return null;
    }
    String iconFN = (String) iconHash.get(new Integer(iconID));
    iconRes += "/" + iconFN;
    ImageIcon icon = new ImageIcon(MainWin.class.getResource(iconRes));

    return icon;
  }

  public ImageIcon getImageIcon(final int iconID, final int width,
      final int height) {
    ImageIcon icon = getImageIcon(iconID);
    if (icon != null) {
      Image image = icon.getImage().getScaledInstance(width, height,
          Image.SCALE_SMOOTH);
      icon = new ImageIcon(image);
    }
    return icon;
  }


  public Image getImage(int iconID) {
    String iconRes = iconPath;

    if (!iconHash.containsKey(new Integer(iconID))) {
      return null;
    }
    String iconFN = (String) iconHash.get(new Integer(iconID));

    iconRes += "/" + iconFN;
    Image icon = Toolkit.getDefaultToolkit().createImage(
        MainWin.class.getResource(iconRes));
    return icon;
  }


}
