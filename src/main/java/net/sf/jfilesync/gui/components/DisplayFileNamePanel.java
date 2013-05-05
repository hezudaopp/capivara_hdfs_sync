/*
 * capivara - Java File Synchronization
 *
 * Created on 10-Jun-2005
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
 * $Id: DisplayFileNamePanel.java,v 1.8 2006/04/02 14:47:42 hunold Exp $
 */
package net.sf.jfilesync.gui.components;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import net.sf.jfilesync.gui.swing.JTextComponentSetter;

public class DisplayFileNamePanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private int cols = 25;
  //private int maxLength = 40;
  private final JTextField fnField = new JTextField();

  public DisplayFileNamePanel() {
    super(true);
    initUI();
  }

  public DisplayFileNamePanel(final int cols) {
    super(true);
    this.cols = cols;
    initUI();
  }

  private void initUI() {
    fnField.setColumns(cols);
    fnField.setHorizontalAlignment(JTextField.LEADING);
    fnField.setEditable(false);

    SpringLayout layout = new SpringLayout();
    layout
        .putConstraint(SpringLayout.WEST, fnField, 0, SpringLayout.WEST, this);
    layout
        .putConstraint(SpringLayout.EAST, this, 0, SpringLayout.EAST, fnField);
    layout.putConstraint(SpringLayout.NORTH, fnField, 0, SpringLayout.NORTH,
        this);
    layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH,
        fnField);

    setLayout(layout);
    add(fnField);
  }

  public void setFileName(final String fileName) {
    SwingUtilities.invokeLater(new JTextComponentSetter(fnField, fileName));
  }

}
