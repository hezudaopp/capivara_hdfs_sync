/*
 * capivara - Java File Synchronization
 *
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
 * $Id: PrefixComboBox.java,v 1.6 2006/02/24 22:21:53 hunold Exp $
 */
package net.sf.jfilesync.gui.components;

import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 * this class implements the ComboBox with prefix extension
 * (C) hunoldinho
 * TODO does not work properly yet
 */

public class PrefixComboBox extends JComboBox implements KeyListener {

  private static final long serialVersionUID = 3546360621873640499L;

  private String[] listData;
  private String[] prefixData;
  private String prefix;
  private PrefixComboBoxListener listener;
  private JTextField editor = (JTextField)getEditor().getEditorComponent();

//  private final static Logger LOGGER = Logger.getLogger(PrefixComboBox.class
//      .getName());

  public PrefixComboBox(String[] listData) {
    super();
    this.listData = listData;
    editor.addKeyListener(this);
    initData();
    initUI();
  }

  private void initUI() {
    editor.setColumns(20);
    setEditable(true);
  }

  /**
   * Only one listener should be enough.
   */
  public void setPrefixComboBoxListener(PrefixComboBoxListener l) {
    this.listener = l;
  }

  /**
   * updating internal list of strings
   */
  public void setListData(String[] listData) {
    this.listData = listData;
    initData();
  }

  private void initData() {
    prefix = "";
    prefixData = new String[listData.length];
    System.arraycopy(listData, 0, prefixData, 0, listData.length);
  }

  public void newChar() {
    prefix = (String) getEditor().getItem();
    if (prefix.length() > 0) {
      showListIfAvailable();
    } else {
      setPopupVisible(false);
    }
  }

  private void showListIfAvailable() {
    final String[] datain = listData;
    final List<String> dataout = new ArrayList<String>();

    for (int i = 0; i < datain.length; i++) {
      if (datain[i].startsWith(prefix)) {
        dataout.add(datain[i]);
      }
    }

    if (dataout.size() > 0) {
      removeAllItems();
      addItem(prefix);	// Jawinton
      Iterator<String> it = dataout.iterator();
      while (it.hasNext()) {
        addItem(it.next());
      }
      setPopupVisible(true);
    } else {
      setPopupVisible(false);
    }
  }

  public void setEditorText(String text) {
    editor.setText(text);
  }

  public String getEditorText() {
    return editor.getText();
  }

  public void keyTyped(KeyEvent e) {}
  public void keyPressed(KeyEvent e) {}

  public void keyReleased(KeyEvent e) {
    int code = e.getKeyCode();
    switch (code) {
      case KeyEvent.VK_LEFT:
      case KeyEvent.VK_RIGHT:
      case KeyEvent.VK_UP:
      case KeyEvent.VK_DOWN:
      case KeyEvent.VK_HOME:
      case KeyEvent.VK_SHIFT:
        break;
      case KeyEvent.VK_ENTER:
        e.consume();
        notifyListener();
        break;
//      case KeyEvent.VK_BACK_SPACE:	// Jawinton
//        newChar();
//        break;
      default:
        newChar();
        editor.setCaretPosition(prefix.length()); // Jawinton
//        caret.setDot(prefix.length());
        break;
    }
  }

  private void notifyListener() {
    if( listener != null ) {
      listener.prefixItemChosen(new PrefixItemEvent(this, editor.getText()));
    }
  }

  public void addCustomFocusListener(FocusListener listener) {
    editor.addFocusListener(listener);
  }

  public JTextField getEditorTextField() {
    return editor;
  }

}
