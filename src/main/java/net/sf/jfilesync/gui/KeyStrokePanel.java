/*
 * capivara - Java File Synchronization
 *
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
 * $Id: KeyStrokePanel.java,v 1.3 2006/08/29 19:58:19 hunold Exp $
 */
package net.sf.jfilesync.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import net.sf.jfilesync.prop.LanguageBundle;

public class KeyStrokePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private KeyStrokeTable keyStrokeTable;

    public KeyStrokePanel(KeyStrokeConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("keystroke config is null");
        }

        keyStrokeTable = new KeyStrokeTable();
        setKeyStrokeConfig(config);

        JScrollPane scrollpane = new JScrollPane(keyStrokeTable);

        setLayout(new BorderLayout());
        add(scrollpane, BorderLayout.CENTER);
    }

    public void setKeyStrokeConfig(KeyStrokeConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("keystroke config is null");
        }
        KeyStrokeModel model = new KeyStrokeModel(config);
        keyStrokeTable.setModel(model);

        keyStrokeTable.getColumnModel().getColumn(0).setCellRenderer(
                new KeyStrokeTableCellRenderer());
        keyStrokeTable.getColumnModel().getColumn(1).setCellRenderer(
                new KeyStrokeTableCellRenderer());
    }

    class KeyStrokeTable extends JTable {

        private static final long serialVersionUID = 1L;

        public KeyStrokeTable() {
            super();
        }

        public KeyStrokeTable(KeyStrokeModel tableModel) {
            super(tableModel);
        }
    }

    class KeyStrokeTableCellRenderer implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            final JLabel cellLabel = new JLabel((String) value);

            switch (column) {
            case 0:
                break;
            case 1:
                cellLabel.setForeground(Color.BLUE);
                break;
            default:
                break;
            }

            return cellLabel;
        }

    }

    class KeyStrokeModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;
        private KeyStrokeConfig config;

        public KeyStrokeModel(KeyStrokeConfig config) {
            this.config = config;
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return config.getNumberOfKeyStrokes();
        }

        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return LanguageBundle.getInstance().getMessage(
                        "dialog.options.keystroke_action");
            case 1:
                return LanguageBundle.getInstance().getMessage(
                        "dialog.options.keystroke_settings");
            default:
                return "unknown col";
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Object ret = null;

            if (columnIndex == 0) {
                final String actionName = config.getActionName(rowIndex);
                // lookup action name in locale
                ret = LanguageBundle.getInstance().getMessage(actionName);
            } else if (columnIndex == 1) {
                ret = config.getKeyStroke(rowIndex).toString();
            } else {
                ret = "no value in model";
            }

            return ret;
        }

    }

}
