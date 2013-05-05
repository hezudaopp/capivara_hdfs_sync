/*
 * capivara - Java File Synchronization
 *
 * Created on Feb 28, 2006
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.ConnectionConfig;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.types.QuickConnectMessage;
import net.sf.jfilesync.prop.LanguageBundle;

public class QuickConnectPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger
            .getLogger(QuickConnectPopupMenu.class.getPackage().getName());

    // hashmap to store config string and to match against them when clicked
    private final HashMap<String, ConnectionConfig> string2config = new HashMap<String, ConnectionConfig>();
    // menuitem maps to config
    private final HashMap<JMenuItem, ConnectionConfig> menuitem2config = new HashMap<JMenuItem, ConnectionConfig>();
    private final int ccID;

    public QuickConnectPopupMenu(final int ccID) {
        this.ccID = ccID;
        loadConnectionStrings();
        pack();
    }

    private void loadConnectionStrings() {

        ConnectionConfig[] connectionAr = MainWin.config.getSavedConnections();

        if (connectionAr == null || connectionAr.length == 0) {
            addConnectionEntry(null, LanguageBundle.getInstance().getMessage(
                    "connect.quick.no_connections"), false);
        } else {
            for (int i = 0; i < connectionAr.length; i++) {
                String conStr = connectionAr[i].getFormattedString();
                if (conStr != null) {
                    if (!string2config.containsKey(conStr)) {
                        string2config.put(conStr, connectionAr[i]);
                        addConnectionEntry(connectionAr[i], conStr, true);
                    }
                }
            }
        }

    }

    private void addConnectionEntry(final ConnectionConfig config,
            final String conString, boolean clickable) {

        JMenuItem item = new JMenuItem();
        item.setText(conString);

        if (clickable && config != null) {
            menuitem2config.put(item, config);
            item.addMouseListener(new QuickConnectMouseAdapter());
        }

        add(item);
    }

    class QuickConnectMouseAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            checkMouseButton(e);
        }

        public void mouseReleased(MouseEvent e) {
            checkMouseButton(e);
        }

        private void checkMouseButton(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                return; // ignore right mouse button
            }
            boolean newTab = false;
            if (SwingUtilities.isMiddleMouseButton(e)) {
                newTab = true;
            }
            if (e.getSource() instanceof JMenuItem) {
                ConnectionConfig config = (ConnectionConfig) menuitem2config
                        .get(e.getSource());
                LOGGER.info("fire quick connect event, newTab : " + newTab);
                TEventMulticaster.getInstance().fireTEvent(this, -1,
                        new QuickConnectMessage(config, ccID, newTab));
            }
        }

    }

}
