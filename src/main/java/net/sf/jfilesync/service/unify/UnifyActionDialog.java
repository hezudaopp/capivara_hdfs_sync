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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.service.unify.action.UnifyAction;
import net.sf.jfilesync.service.unify.action.UnifyOption;

public class UnifyActionDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  public final static String ACTION_START  = "start_unify";
  public final static String ACTION_CANCEL = "start_cancel";

  private final UnifyOption unifyOption;
  private final ActionListener listener;

  public UnifyActionDialog(final Dialog owner, final UnifyOption unifyOption,
      final ActionListener listener) {

    super(owner, LanguageBundle.getInstance().getMessage(
        "unify.actions.preview"), true);

    if( unifyOption == null ) {
      throw new IllegalArgumentException("unifyOption must not be null");
    }
    if( listener == null ) {
      throw new IllegalArgumentException("listener must not be null");
    }
    this.unifyOption = unifyOption;
    this.listener = listener;
    initUI();
  }

  private void initUI() {
    final JList viewList = new JList();

    final List<UnifyAction> actionList = unifyOption.getUnifyActions()
        .getUnifyActionList();
    viewList.setListData(actionList.toArray(new Object[actionList.size()]));
    viewList.setCellRenderer(new UnifyActionListRenderer());

    final JScrollPane scrollPane = new JScrollPane(viewList);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(getParent());
  }

  private JPanel createButtonPanel() {
    final JPanel panel = new JPanel();

    final JButton startButton = new JButton(LanguageBundle.getInstance()
        .getMessage("label.start"));
    startButton.setActionCommand(ACTION_START);
    startButton.addActionListener(listener);

    final JButton cancelButton = new JButton(LanguageBundle.getInstance()
        .getMessage("label.cancel"));
    cancelButton.setActionCommand(ACTION_CANCEL);
    cancelButton.addActionListener(listener);

    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    panel.add(startButton);
    panel.add(cancelButton);

    return panel;
  }

  class UnifyActionListRenderer implements ListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {

      Component c = null;
      if( value instanceof UnifyAction ) {
        c = unifyOption.getUnifyActionRenderer().render((UnifyAction) value);
      } else {
        c = new JLabel("unkown item");
      }
      return c;
    }

  }

}
