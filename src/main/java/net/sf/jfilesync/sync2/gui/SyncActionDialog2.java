/*
 * capivara - Java File Synchronization
 *
 * Created on 04-May-2006
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
package net.sf.jfilesync.sync2.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.gui.components.ConnectionDetailsComponent2;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.SyncAction;
import net.sf.jfilesync.sync2.SyncActionModel2;
import net.sf.jfilesync.sync2.SyncActionStatistics;
import net.sf.jfilesync.sync2.SyncActionTable2;

public class SyncActionDialog2 extends JDialog implements ActionListener, ComponentListener {

  private static final long serialVersionUID = 4050196432021697844L;

  private final SyncActionTable2 actionTable;
  private final SyncActionStatistics stats1;
  private final SyncActionStatistics stats2;
  
  public static final int ACTION_SYNC   = 0;
  public static final int ACTION_CANCEL = 1;
  
  private int userChoice = ACTION_CANCEL; 
  
  private final JSplitPane topSplitPane = new JSplitPane();
  private final ConnectionDetails conDetailsLeft;
  private final ConnectionDetails conDetailsRight;
  
  public SyncActionDialog2(final JDialog owner, final SyncAction[] leftActions,
      final ConnectionDetails conDetailsLeft, final SyncAction[] rightActions,
      final ConnectionDetails conDetailsRight) {

    super(owner, LanguageBundle.getInstance().getMessage(
        "sync.dialog.actionlist.title"), true);

    if (leftActions == null) {
      throw new IllegalArgumentException("leftActions is null");
    }
    if (rightActions == null) {
      throw new IllegalArgumentException("rightActions is null");
    }
    
    actionTable = new SyncActionTable2(new SyncActionModel2(leftActions,
        rightActions));
    this.conDetailsLeft = conDetailsLeft;
    this.conDetailsRight = conDetailsRight;

    stats1 = new SyncActionStatistics(leftActions);
    stats2 = new SyncActionStatistics(rightActions);

    initUI();
  }

  private void initUI() {
    
    
    final JComponent c1 = new ConnectionDetailsComponent2(conDetailsLeft);
    final JPanel leftStatsPanel = createStatsPanel(stats1);
    final JPanel leftInfoPanel = new JPanel();
    leftInfoPanel.setLayout(new BoxLayout(leftInfoPanel, BoxLayout.Y_AXIS));
    leftInfoPanel.add(c1);
    leftInfoPanel.add(leftStatsPanel);
    
    final JComponent c2 = new ConnectionDetailsComponent2(conDetailsRight);
    final JPanel rightStatsPanel = createStatsPanel(stats2);
    final JPanel rightInfoPanel = new JPanel();
    rightInfoPanel.setLayout(new BoxLayout(rightInfoPanel, BoxLayout.Y_AXIS));
    rightInfoPanel.add(c2);
    rightInfoPanel.add(rightStatsPanel);
    
    topSplitPane.setLeftComponent(leftInfoPanel);
    topSplitPane.setRightComponent(rightInfoPanel);
    addComponentListener(this);
          
    final JPanel topPane = new JPanel(new BorderLayout());
    topPane.add(topSplitPane, BorderLayout.CENTER);
    
    final JScrollPane scrollPane = new JScrollPane(actionTable);
    scrollPane.setBorder(BorderFactory.createTitledBorder(LanguageBundle
        .getInstance().getMessage("sync.preview.action_title")));
    
    final JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    
    mainPanel.add(topPane, BorderLayout.NORTH);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
    
    setContentPane(mainPanel);    
    pack();
    topSplitPane.setDividerLocation(Component.CENTER_ALIGNMENT);
    setLocationRelativeTo(getOwner());
  }
    
  
  private JPanel createButtonPanel() {
    JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton syncButton = new JButton(LanguageBundle.getInstance()
        .getMessage("sync.dialog.label.sync"));
    syncButton.addActionListener(this);
    syncButton.setActionCommand("sync");

    JButton cancelButton = new JButton(LanguageBundle.getInstance()
        .getMessage("label.cancel"));
    cancelButton.addActionListener(this);
    cancelButton.setActionCommand("cancel");

    buttonPane.add(syncButton);
    buttonPane.add(cancelButton);
    return buttonPane;
  }
  
  private JPanel createStatsPanel(final SyncActionStatistics stats) {
    JPanel pane = new JPanel();
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

    pane.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createLoweredBevelBorder(), LanguageBundle.getInstance()
        .getMessage("sync.stats")));

    JLabel copyNumLabel = new JLabel(LanguageBundle.getInstance()
        .getMessage("sync.stats.files_copy"));

    JTextField copyField = new JTextField(15);
    copyField.setEditable(false);
    copyField.setText(stats.getNumberOfFilesToCopy() + "");

    JPanel copyPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    copyPane.add(copyNumLabel);
    copyPane.add(copyField);

    JLabel copyKbLabel = new JLabel(LanguageBundle.getInstance()
        .getMessage("sync.stats.kb_copy"));

    JTextField kbField = new JTextField(15);
    kbField.setEditable(false);
    kbField.setText(stats.getKbToCopy() + "");

    JPanel kbPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    kbPane.add(copyKbLabel);
    kbPane.add(kbField);

    JLabel deleteNumLabel = new JLabel(LanguageBundle.getInstance()
        .getMessage("sync.stats.files_delete"));

    JTextField deleteNumField = new JTextField(15);
    deleteNumField.setEditable(false);
    deleteNumField.setText(stats.getNumberOfFilesToDelete() + "");

    JPanel deletePane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    deletePane.add(deleteNumLabel);
    deletePane.add(deleteNumField);

    pane.add(copyPane);
    pane.add(kbPane);
    pane.add(deletePane);

    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

    pane.setMinimumSize(new Dimension(300, 300));

    return pane;
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("sync")) {
      userChoice = ACTION_SYNC;
      setVisible(false);
    } else if (cmd.equals("cancel")) {
      userChoice = ACTION_CANCEL;
      setVisible(false);
    }
  }
  
  public int getUserChoice() {
    return userChoice;
  }

  public void componentResized(final ComponentEvent e) {
    topSplitPane.setDividerLocation(Component.CENTER_ALIGNMENT);
  }

  public void componentMoved(ComponentEvent e) {
  }

  public void componentShown(ComponentEvent e) {
  }

  public void componentHidden(ComponentEvent e) {
  }
  
}
