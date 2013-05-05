/*
 * capivara - Java File Synchronization
 *
 * Created on 04-Apr-2006
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
package net.sf.jfilesync.gui.dialog;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

public class ListPopup extends JPopupMenu {

    private static final long serialVersionUID = 1L;

    private JScrollPane scrollPane;

    private final ListMenuPanel menuPanel = new ListMenuPanel();

    public ListPopup() {
        initUI();
    }

    public MenuElement[] getSubElements() {
        return new MenuElement[] { menuPanel };
    }

    private void initUI() {

        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(menuPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);
    }

    public void add(final JMenu menu) {
        if (menu != null) {
            menuPanel.add(menu);
        }
    }

    static class ListMenuPanel extends JPanel implements MenuElement {

        public ListMenuPanel() {
            super(null, false);
        }

        public void processMouseEvent(MouseEvent event, MenuElement[] path,
                MenuSelectionManager manager) {
        }

        public void processKeyEvent(KeyEvent event, MenuElement[] path,
                MenuSelectionManager manager) {
        }

        public void menuSelectionChanged(boolean isIncluded) {
        }

        public MenuElement[] getSubElements() {
            MenuElement[] result = new MenuElement[getComponentCount()];
            Arrays.asList(getComponents()).toArray(result);
            return result;
        }

        public Component getComponent() {
            return this;
        }

    }

}
