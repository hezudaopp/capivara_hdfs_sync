/*
 * capivara - Java File Synchronization
 *
 * Created on 27-Mar-2005
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
 */
package net.sf.jfilesync.gui.components;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventListener;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.event.types.ConnectionStateMessage;
import net.sf.jfilesync.event.types.ControlActivationEvent;
import net.sf.jfilesync.gui.TControlCenter;
import net.sf.jfilesync.gui.TControlCenterFactory;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;

public class TabbedBrowser extends JTabbedPane implements TEventListener, MouseListener, ActionListener, ChangeListener {

  private final JFrame parent;
  private final HashMap<Integer, TControlCenter> ccMap = new HashMap<Integer, TControlCenter>();
  private int tab_title_length = 15;	// Jawinton 12 to 15

  public static final String ACTION_ADD_TAB = "add_tab";
  public static final String ACTION_DEL_TAB = "del_tab";

  private static Logger LOGGER = Logger.getLogger(TabbedBrowser.class.getName());

  private int selectedTab = -1;

  public TabbedBrowser(final JFrame parent) {
    this.parent = parent;

    TEventMulticaster.getInstance().addTEventListener(this, TMessage.ID.CONNECTION_STATE_MESSAGE);

    loadSettings();

    setFocusable(false);
    addMouseListener(this);
    addChangeListener(this);
    addControlCenter(TControlCenterFactory.getInstance(parent).getNextControlCenter());
  }

  private void loadSettings() {
	  /* Jawinton */
//    try {
//      tab_title_length = MainWin.config.getProgramSettings().getIntegerOption(TProgramSettings.OPTION_TAB_TITLE_LENGTH);
//    } catch (SettingsTypeException ste) {
//      LOGGER.severe(ste.getMessage());
//    }
	  /* Jawinton */
  }

  public synchronized TControlCenter getFirstControlCenter() {
    TControlCenter cc = null;
    if (getTabCount() > 0) {
      cc = (TControlCenter) getComponentAt(0);
    }
    return cc;
  }

  public synchronized void addControlCenter(final TControlCenter cc) {
    if (cc == null) {
      throw new NullPointerException("cc is null");
    }
    final Integer ikey = new Integer(cc.getID());
    if (!ccMap.containsKey(ikey)) {
      ccMap.put(ikey, cc);
      String tabname = getTabNameOf(cc);
      addTab(tabname, cc);
      // TabInserter ti = new TabInserter(this, cc, tabname);
      // SwingUtilities.invokeLater(ti);
    } else {
      LOGGER.log(Level.SEVERE, "cannot add tab: already exists");
    }
  }

  public synchronized void removeControlCenter(final TControlCenter cc) {
    if (cc == null) {
      throw new NullPointerException("cc is null");
    }

    Integer ikey = new Integer(cc.getID());
    if (ccMap.containsKey(ikey)) {
      // double check the tab count
      int idx = indexOfComponent(cc);
      if (getTabCount() > 1 && idx < getTabCount()) {
        ccMap.remove(ikey);
        removeTabAt(idx);
        // TabDeleter td = new TabDeleter(this, idx);
        // SwingUtilities.invokeLater(td);
      }
    } else {
      LOGGER.log(Level.SEVERE, "ControlCenter unkown, ID : " + cc.getID());
    }

    LOGGER.info("tabCount after remove: " + getTabCount());
  }

  /**
   * 
   * @param ccID
   * @return reference to the ControlCenter with the passed ID or null if it
   *         does not exist
   */
  public synchronized TControlCenter getControlCenter(final int ccID) {
    return (TControlCenter) ccMap.get(new Integer(ccID));
  }

  public void showControlCenter(final int ccID) {
    TControlCenter cc = getControlCenter(ccID);
    if (cc != null) {
      setSelectedComponent(cc);
    }
  }

  protected String getTabNameOf(final TControlCenter cc) {
    String name = "";

    if (cc == null) {
      throw new NullPointerException("cc is null");
    }

    ConnectionDetails cd = cc.getConnectionDetails();
    if (cd != null) {
      name = cd.getHost();
    } else {
      name = getControlCenterDefaultName(cc);
    }
    return name;
  }

  private String getControlCenterDefaultName(final TControlCenter cc) {
    return LanguageBundle.getInstance().getMessage("connection.connection") + (cc.getID() + 1);
  }

  protected int getTabIndexOf(final TControlCenter cc) {
    int idx = -1;
    for (int i = 0; i < getTabCount(); i++) {
      if (getComponentAt(i).equals(cc)) {
        idx = i;
        break;
      }
    }
    return idx;
  }

  private void updateTabName(final TControlCenter cc, final String name) {

    int idx = getTabIndexOf(cc);
    if (idx >= 0) {
      setTitleAt(idx, name);
    }
  }

  public void processEvent(TEvent e) {

    if (e.getMessage() instanceof ConnectionStateMessage) {
      ConnectionStateMessage csm = (ConnectionStateMessage) e.getMessage();
      int ccid = csm.getControlCenterID();
      Integer ikey = new Integer(ccid);
      if (ccMap.containsKey(ikey)) {
        TControlCenter cc = (TControlCenter) ccMap.get(ikey);
        if (csm.isConnected()) {
          updateTabName(cc, getTabNameOf(cc));
        } else {
          updateTabName(cc, getControlCenterDefaultName(cc));
        }
      }
    }

  }

  public void setTitleAt(final int idx, final String name) {
    String pname;

    if (name == null) {
      throw new NullPointerException("name is null");
    }

    if (name.length() <= tab_title_length) {
      pname = name;
    } else {
      pname = name.substring(0, tab_title_length);
    }

    super.setTitleAt(idx, pname);
  }

  public synchronized boolean containsControlCenter(final TControlCenter cc) {
    if (cc == null) {
      throw new NullPointerException("cc is null");
    }
    Integer ikey = new Integer(cc.getID());
    return ccMap.containsKey(ikey);
  }

  public synchronized boolean containsControlCenterById(final int ccID) {
    Integer ikey = new Integer(ccID);
    return ccMap.containsKey(ikey);
  }

  public TControlCenter getFocusedControlCenter() {
    TControlCenter retval = null;
    retval = (TControlCenter) getSelectedComponent();
    return retval;
  }

  public void mousePressed(MouseEvent e) {
    selectedTab = -1;

    // logger.info("mouse pressed");
    if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
      // check if we clicked inside a tab
      // if so then show a popup menu
      // to add/delete/... tabs

      Point p = e.getPoint();
      LOGGER.info("tabcount : " + getTabCount());

      for (int i = 0; i < getTabCount(); i++) {
        Rectangle rect = getUI().getTabBounds(this, i);
        if (rect.contains(p)) {
          selectedTab = i;
          TabPopup popup = new TabPopup(this);
          popup.show(e.getComponent(), e.getX(), e.getY());
          break;
        }
      }
    }

  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void stateChanged(ChangeEvent e) {
    TControlCenter cc = (TControlCenter) getSelectedComponent();
    if (cc != null) {
      // important to update ui since a tab
      // does not get painted while hidden
      cc.updateUI();

      // notify others about the activation
      TControlCenterFactory.getControlCenterBus().fireControlCenterBusEvent(
          new ControlActivationEvent(this, cc.getID()));
    }
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals(ACTION_ADD_TAB)) {
      addControlCenter(TControlCenterFactory.getInstance(parent).getNextControlCenter());
    } else if (cmd.equals(ACTION_DEL_TAB)) {
      if (selectedTab != -1) {
        if (getTabCount() > 1) {
          TControlCenter cc = (TControlCenter) getComponent(selectedTab);
          removeControlCenter(cc);
        }
      }
    }
  }

  /**
   * popup menu shown when user clicks on any tab
   * 
   * @author sascha
   */
  class TabPopup extends JPopupMenu {

    public TabPopup(final ActionListener listener) {
      addMenuItem(new JMenuItem("add tab"), ACTION_ADD_TAB, listener);
      addMenuItem(new JMenuItem("delete tab"), ACTION_DEL_TAB, listener);
    }

    private void addMenuItem(final JMenuItem item, final String actionCommand, final ActionListener listener) {
      item.setActionCommand(actionCommand);
      item.addActionListener(listener);
      add(item);
    }
  }

  class TabInserter implements Runnable {
    private final JTabbedPane target;

    private final Component comp;

    private final String name;

    public TabInserter(final JTabbedPane target, final Component comp, final String name) {
      this.target = target;
      this.comp = comp;
      this.name = name;
    }

    public void run() {
      target.addTab(name, comp);
    }
  }

  class TabDeleter implements Runnable {

    private final JTabbedPane target;

    private final int index;

    public TabDeleter(final JTabbedPane target, final int index) {
      this.target = target;
      this.index = index;
    }

    public void run() {
      target.remove(index);
    }
  }

}
