/*
 * capivara - Java File Synchronization
 *
 * Created on 11-Apr-2006
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sf.jfilesync.gui.components.BookmarkActionListener;
import net.sf.jfilesync.prop.LanguageBundle;

public class BookmarkPopupMenu extends JPopupMenu implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final String CMD_ENTER = "enter";
    private static final String CMD_DELETE = "delete";
    private final String[] data;
    private final List<BookmarkActionListener> listeners = new ArrayList<BookmarkActionListener>();

    // private final Dimension popupSize = new Dimension(200,150);

    public BookmarkPopupMenu(String[] data) {
        super();
        if (data == null) {
            throw new NullPointerException("data");
        }
        this.data = data;
        initUI();
    }

    private void initUI() {

        for (int i = 0; i < data.length; i++) {
            add(createCustomMenu(data[i]));
        }
        // setPreferredSize(popupSize);

    }

    private JMenu createCustomMenu(String bookmark) {
        JMenu menu = new JMenu(bookmark);

        // menu.setMenuLocation(popupSize.width, 0);

        JMenuItem item1 = new BookmarkItem(LanguageBundle.getInstance()
                .getMessage("bookmark.goto"), bookmark);
        item1.addActionListener(this);
        item1.setActionCommand(CMD_ENTER);

        JMenuItem item2 = new BookmarkItem(LanguageBundle.getInstance()
                .getMessage("bookmark.delete"), bookmark);
        item2.addActionListener(this);
        item2.setActionCommand(CMD_DELETE);

        menu.add(item1);
        menu.add(item2);
        return menu;
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof BookmarkItem) {
            BookmarkItem item = (BookmarkItem) e.getSource();
            String cmd = e.getActionCommand();
            if (cmd.equals(CMD_ENTER)) {
                fireBookmarkSelected(item.getBookmark());
            } else if (cmd.equals(CMD_DELETE)) {
                fireBookmarkDelete(item.getBookmark());
            } else {
                System.err.println("unkown command in BookmarkPopupMenu");
            }
        }

    }

    public synchronized void addBookmarkActionListener(
            BookmarkActionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public synchronized void fireBookmarkSelected(final String bookmark) {
        if (bookmark == null) {
            return;
        }
        for (Iterator<BookmarkActionListener> it = listeners.iterator(); it
                .hasNext();) {
            it.next().bookmarkSelected(bookmark);
        }
    }

    public synchronized void fireBookmarkDelete(final String bookmark) {
        if (bookmark == null) {
            return;
        }
        for (Iterator<BookmarkActionListener> it = listeners.iterator(); it
                .hasNext();) {
            it.next().bookmarkDelete(bookmark);
        }
    }

    static class BookmarkItem extends JMenuItem {

        private static final long serialVersionUID = 1L;
        private final String bookmark;

        public BookmarkItem(String text, String bookmark) {
            super(text);
            this.bookmark = bookmark;
        }

        public String getBookmark() {
            return bookmark;
        }
    }

}
