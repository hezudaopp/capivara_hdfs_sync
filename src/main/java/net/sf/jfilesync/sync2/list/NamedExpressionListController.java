/*
 * capivara - Java File Synchronization
 *
 * Created on 25-Dec-2005
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
 * $Id$
 */
package net.sf.jfilesync.sync2.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class NamedExpressionListController implements ListSelectionListener, ActionListener {

  private NExpListList listOfLists;
  private NamedExpressionListComponent gui;
  private List<ActionListener> listeners = new ArrayList<ActionListener>();

  final static Logger LOGGER = Logger.getLogger(NamedExpressionListController.class.getPackage().getName());

  public NamedExpressionListController(NExpListList listOfLists, NamedExpressionListComponent gui) {
    this.listOfLists = listOfLists;
    this.gui = gui;

    gui.setNExpListList(listOfLists);
    gui.setListSelectionListener(this);
    gui.addActionListener(this);
  }

  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }

    // now show other list
    String listName = gui.getSelectedExpressionList();
    if (listName != null && listOfLists != null) {
      NamedExpressionList l = listOfLists.getList(listName);
      if (l != null) {
        gui.showExpressionList(l.getList());
      }
    }
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals(NamedExpressionListComponent.ACTION_DELETE)) {
      fireActionEvent(e);
    }
  }

  public synchronized void addActionListener(ActionListener l) {
    listeners.add(l);
  }

  public synchronized void fireActionEvent(ActionEvent e) {
    for (ActionListener l : listeners) {
      l.actionPerformed(e);
    }
  }

}
