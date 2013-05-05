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
package net.sf.jfilesync.service.unify;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import net.sf.jfilesync.gui.viewmodel.AbstractBasicFilePopup;
import net.sf.jfilesync.gui.viewmodel.BasicFilePopupEvent;
import net.sf.jfilesync.prop.LanguageBundle;

public class UnifyFileTablePopup extends AbstractBasicFilePopup implements ActionListener {

  public final static String EVENT_KEEP_FILE_DEL_DUPLICATES  = "keep_file_delete_duplicates";
  public final static String EVENT_KEEP_FILE = "keep_file";

  public UnifyFileTablePopup() {
    addMenuItem(EVENT_KEEP_FILE_DEL_DUPLICATES, LanguageBundle.getInstance()
        .getMessage("unify.file.keep_and_delete"));
    addMenuItem(EVENT_KEEP_FILE, LanguageBundle.getInstance().getMessage(
        "unify.file.keep"));
  }

  private void addMenuItem(final String event, final String label) {
    JMenuItem item = new JMenuItem(label);
    item.setActionCommand(event);
    item.addActionListener(this);
    add(item);
  }

  public void actionPerformed(ActionEvent e) {
    fireBasicFilePopupEvent(new BasicFilePopupEvent(this,
        getBasicFileContext(), e.getActionCommand()));
  }

}
