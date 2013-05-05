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
 * $Id$
 */
package net.sf.jfilesync.gui.components;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.prop.LanguageBundle;

public class ConnectionDetailsComponent2 extends JComponent {

  private static final long serialVersionUID = 3617297839265494837L;
  private Color bg = getBackground();
  private Color detailsForeGround = Color.BLUE;

  public ConnectionDetailsComponent2(final ConnectionDetails details) {
    JPanel hostPane = new JPanel();
    JTextField hostField = new JTextField(30);
    hostField.setText(details.getUser() + "@" + details.getHost());

    final JLabel hostLabel = new JLabel(LanguageBundle.getInstance()
        .getMessage("connection.connection")
        + ": ");

    setupTextField(hostField);
    setupLabel(hostLabel);
    
    hostPane.setLayout(new BoxLayout(hostPane, BoxLayout.X_AXIS));
    hostPane.add(hostLabel);
    hostPane.add(hostField);
    
    JPanel pathPane = new JPanel();
    JTextField pathField = new JTextField(30);
    pathField.setText(details.getCurrentPath());
    JLabel pathLabel = new JLabel(LanguageBundle.getInstance().getMessage(
        "connection.path")
        + ": ");
    
    pathPane.setLayout(new BoxLayout(pathPane, BoxLayout.X_AXIS));
    pathPane.add(pathLabel);
    pathPane.add(pathField);

    setupTextField(pathField);
    setupLabel(pathLabel);
    
    BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
    setLayout(layout);
    add(hostPane);
    add(pathPane);    
  }
  
  protected void setupLabel(JLabel lab) {
    lab.setBackground(bg);
  }
  
  protected void setupTextField(JTextField tf) {
    tf.setEditable(false);
    tf.setBackground(bg);
    tf.setForeground(detailsForeGround);
  }
  
}
