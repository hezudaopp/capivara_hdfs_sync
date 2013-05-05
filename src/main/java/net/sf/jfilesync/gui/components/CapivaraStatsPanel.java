/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventListener;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.event.types.JvmStatsMessage;

public class CapivaraStatsPanel extends JPanel implements TEventListener {

  private final JLabel encodingKey = new JLabel("VM encoding");
  private final JTextField encodingVal = new JTextField(10);

  private final JLabel localeKey = new JLabel("locale");
  private final JTextField localeVal = new JTextField(2);

//  private final JLabel memoryLabel = new JLabel("memory");
//  private final JLabel memoryVal = new JLabel("");
  
  private long freeMem  = 0L;
  private long totalMem = 0L;
  
  public CapivaraStatsPanel() {
    initUI();
    TEventMulticaster.getInstance().addTEventListener(this,
        TMessage.ID.JVM_STATS_MESSAGE);
  }
  
  private void initUI() {
    
    final JPanel mainPanel = new JPanel();
    
    JPanel statsPanel = createStatsPanel();
    
//    final JPanel memPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//    
//    memPanel.add(memoryLabel);
//    memPanel.add(memoryVal);
    
    mainPanel.add(statsPanel);
//    mainPanel.add(memPanel);
    
//    SpringLayout layout = new SpringLayout();
//    mainPanel.setLayout(layout);
    mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
    
//    
//    layout.putConstraint(SpringLayout.WEST, statsPanel,
//        5,
//        SpringLayout.WEST, mainPanel);
//    layout.putConstraint(SpringLayout.NORTH, statsPanel,
//        5,
//        SpringLayout.NORTH, mainPanel);    
//    layout.putConstraint(SpringLayout.WEST, memPanel,
//        5,
//        SpringLayout.EAST, statsPanel);
//    layout.putConstraint(SpringLayout.NORTH, memPanel,
//        5,
//        SpringLayout.NORTH, mainPanel);
    
    setLayout(new GridLayout(1,1));
    //setBorder(BorderFactory.createRaisedBevelBorder());
    //setBorder(BorderFactory.createRaisedBevelBorder());
    add(mainPanel);
  }

  private JPanel createStatsPanel() {
    
    final JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    
    final Font f = encodingKey.getFont();
    final Font f2 = new Font(f.getFamily(), f.getStyle(), 10);

    encodingVal.setEditable(false);
    encodingVal.setHorizontalAlignment(JTextField.LEFT);
    
    localeVal.setEditable(false);
    localeVal.setHorizontalAlignment(JTextField.LEFT);

    encodingKey.setFont(f2);
    encodingVal.setFont(f2);
    localeKey.setFont(f2);
    localeVal.setFont(f2);
    
    statsPanel.add(encodingKey);
    statsPanel.add(encodingVal);
    statsPanel.add(localeKey);
    statsPanel.add(localeVal);

    return statsPanel;
  }

  public void setEncoding(final String encoding) {
    encodingVal.setText(encoding);
  }
  
  public void setLocale(String locale) {
    localeVal.setText(locale);
  }
  
  public void setFreeMemory(final long freeMemory) {
    this.freeMem = freeMemory;
    updateMemLabels();
  }
  
  public void setTotalMemory(final long totalMemory) {
    this.totalMem = totalMemory;
    updateMemLabels();
  }

  private void updateMemLabels() {
    long usedMem = totalMem - freeMem;
//    System.out.println("freeMem : " + freeMem);
//    System.out.println("totalMem : " + totalMem);
    if( usedMem > 0 && totalMem > 0) {
      final long usedMb  = usedMem / (1024*1024);
      final long totalMb = totalMem / (1024*1024);
      final StringBuffer memLab = new StringBuffer();
      memLab.append(Long.toString(usedMb));
      memLab.append("/");
      memLab.append(Long.toString(totalMb));
//      memoryVal.setText(memLab.toString());
    }
  }

  public void processEvent(TEvent e) {
    TMessage.ID mid = e.getMessage().getMessageType();
    switch(mid) {
    case JVM_STATS_MESSAGE:
      JvmStatsMessage message = (JvmStatsMessage)e.getMessage();
      handleJvmStatsMessage(message);
    }
  }

  private void handleJvmStatsMessage(JvmStatsMessage message) {
    if( message == null ) {
      return;
    }
    //System.out.println("invoke updater");
    SwingUtilities.invokeLater(new StatsUpdater(this, message));    
  }

  class StatsUpdater implements Runnable {

    private final CapivaraStatsPanel panel;
    private final JvmStatsMessage message;
    
    public StatsUpdater(CapivaraStatsPanel panel, JvmStatsMessage message) {
      this.panel = panel;
      this.message = message;
    }
    
    public void run() {
      if( message.getKey().equals("encoding") ) {
        panel.setEncoding(message.getValue());
      } else if( message.getKey().equals("locale") ) {
        panel.setLocale(message.getValue());
      } else if( message.getKey().equals("freemem") ) {
        panel.setFreeMemory(Long.parseLong(message.getValue()));
      } else if( message.getKey().equals("totalmem") ) {
        panel.setTotalMemory(Long.parseLong(message.getValue()));
      }
    }
    
  }
  
}
