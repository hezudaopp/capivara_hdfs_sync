/*
 * capivara - Java File Synchronization
 *
 * Created on 01-Dec-2005
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.prop.LanguageBundle;


public class ExpressionMainPanel extends JPanel {

  private ExpressionListEditPanel includePanel = null;
  private ExpressionListEditPanel excludePanel = null;
  private NamedExpressionListComponent namedListPanel = null;

  private JButton includeAddButton = new JButton();
  private JButton includeSaveButton = new JButton();
  private JButton excludeAddButton = new JButton();
  private JButton excludeSaveButton = new JButton();
  
//  private ExpressionListController includeListController;
//  private ExpressionListController excludeListController;
  private NamedExpressionListController namedListController;
  
  private JDialog owner;
  private ExpressionMainModel model;
  
  public final static String ACTION_ADD_INCL   = "add_incl";
  public final static String ACTION_SAVE_INCL  = "save_incl";
  public final static String ACTION_ADD_EXCL   = "add_excl";
  public final static String ACTION_SAVE_EXCL  = "save_excl";

  public ExpressionMainPanel(JDialog owner, ExpressionMainModel model) {
    this.owner = owner;
    this.model = model;   
    initUI();
    loadDefaults();
  }
  
//  public void setExpressionMainModel(ExpressionMainModel model) {
//    this.model = model;
//  }
        
  private void loadDefaults() {

    final ExpressionList il = model.getIncludeList();
    if( il != null ) {
      setIncludeList(il);
    }

    final ExpressionList el = model.getExcludeList();
    if( el != null ) {
      setExcludeList(el);
    }
    
  }

  public void initUI() {
    
    includeAddButton.setToolTipText(LanguageBundle
        .getInstance().getMessage("filter.add_includelist"));
    includeAddButton.setIcon(TImageIconProvider.getInstance()
        .getImageIcon(TImageIconProvider.LEFT_ARROW, 22, 22));
    includeAddButton.setPreferredSize(new Dimension(48,29));
    
    includeSaveButton.setToolTipText(LanguageBundle
        .getInstance().getMessage("filter.save_includelist"));
    includeSaveButton.setIcon(TImageIconProvider.getInstance()
        .getImageIcon(TImageIconProvider.RIGHT_ARROW_SAVE, 33, 22));   
    includeSaveButton.setPreferredSize(new Dimension(48,29));
    
    excludeAddButton.setToolTipText(LanguageBundle
        .getInstance().getMessage("filter.add_excludelist"));
    excludeAddButton.setIcon(TImageIconProvider.getInstance()
        .getImageIcon(TImageIconProvider.RIGHT_ARROW, 22, 22));
    excludeAddButton.setPreferredSize(new Dimension(48,29));

    excludeSaveButton.setToolTipText(LanguageBundle
        .getInstance().getMessage("filter.save_excludelist"));
    excludeSaveButton.setIcon(TImageIconProvider.getInstance()
        .getImageIcon(TImageIconProvider.LEFT_ARROW_SAVE, 33, 22));
    excludeSaveButton.setPreferredSize(new Dimension(48,29));

    includeAddButton.setActionCommand(ACTION_ADD_INCL);
    includeSaveButton.setActionCommand(ACTION_SAVE_INCL);
    excludeAddButton.setActionCommand(ACTION_ADD_EXCL);
    excludeSaveButton.setActionCommand(ACTION_SAVE_EXCL);
    
    includePanel = new ExpressionListEditPanel(owner);
    includePanel.setPreferredSizeOfList(new Dimension(200,350));
    new ExpressionListController(model.getIncludeList(), includePanel);    

    excludePanel = new ExpressionListEditPanel(owner);
    excludePanel.setPreferredSizeOfList(new Dimension(200,350));
    new ExpressionListController(model.getExcludeList(), excludePanel);

    namedListPanel = new NamedExpressionListComponent();
    namedListController = new NamedExpressionListController(model.getNExpListList(), 
        namedListPanel);
        
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    JPanel buttonPanel1 = new JPanel();
    buttonPanel1.setLayout(new BoxLayout(buttonPanel1, BoxLayout.Y_AXIS));
    buttonPanel1.add(Box.createVerticalGlue());
    buttonPanel1.add(includeAddButton);
    buttonPanel1.add(includeSaveButton);
    buttonPanel1.add(Box.createVerticalGlue());

    JPanel inclButtonPanel = new JPanel();
    inclButtonPanel.setLayout(new BorderLayout());
    inclButtonPanel.add(includePanel, BorderLayout.CENTER);
    inclButtonPanel.add(buttonPanel1, BorderLayout.EAST);

    JPanel buttonPanel2 = new JPanel();
    buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.Y_AXIS));
    buttonPanel2.add(Box.createVerticalGlue());
    buttonPanel2.add(excludeAddButton);
    buttonPanel2.add(excludeSaveButton);
    buttonPanel2.add(Box.createVerticalGlue());

    JPanel exclButtonPanel = new JPanel();
    exclButtonPanel.setLayout(new BorderLayout());
    exclButtonPanel.add(excludePanel, BorderLayout.CENTER);
    exclButtonPanel.add(buttonPanel2, BorderLayout.EAST);
    
    JPanel inclExclPanel = new JPanel();
    inclExclPanel.setLayout(new BoxLayout(inclExclPanel, BoxLayout.Y_AXIS));
    inclExclPanel.add(inclButtonPanel);
    inclExclPanel.add(exclButtonPanel);
    
    includePanel.setBorder(BorderFactory
        .createTitledBorder(LanguageBundle
            .getInstance().getMessage("filter.includefilters")));
    excludePanel.setBorder(BorderFactory
        .createTitledBorder(LanguageBundle
            .getInstance().getMessage("filter.excludefilters")));
        
    add(includePanel);
    add(buttonPanel1);
    add(namedListPanel);
    add(buttonPanel2);
    add(excludePanel);
  }
  
  public void setIncludeList(final ExpressionList list) {
    includePanel.setExpressionList(list);
  }

  public void setExcludeList(final ExpressionList list) {
    excludePanel.setExpressionList(list);
  }
  
  public void updateSavedExpressions() {
    namedListPanel.clearList();
    namedListPanel.setNExpListList(model.getNExpListList());
  }
  
  public String getSelectedNamedList() {
    return namedListPanel.getSelectedExpressionList();
  }

  public void addActionListener(ActionListener l) {
    includeAddButton.addActionListener(l);
    includeSaveButton.addActionListener(l);
    excludeAddButton.addActionListener(l);
    excludeSaveButton.addActionListener(l);
    namedListController.addActionListener(l);
  }
   
}
