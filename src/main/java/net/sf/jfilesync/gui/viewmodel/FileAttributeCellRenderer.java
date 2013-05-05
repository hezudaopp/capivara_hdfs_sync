/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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

package net.sf.jfilesync.gui.viewmodel;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.jfilesync.settings.TStyleInterface;

/**
 * This subclass of DefaultTableCellRenderer is responsible for rendering all
 * file properties but file names.
 */

public class FileAttributeCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 539795343476311631L;

    private TStyleInterface style;
    private int horizontalAlignment = JLabel.LEFT;

    public FileAttributeCellRenderer(TStyleInterface style) {
        this.style = style;
    }

    public FileAttributeCellRenderer(TStyleInterface style,
            int horizontalAlignment) {
        this.style = style;
        this.horizontalAlignment = horizontalAlignment;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (!(value instanceof String)) {
            throw new IllegalArgumentException("value must be String");
        }

        JLabel label = new JLabel((String) value);

        label.setOpaque(true);
        label.setFont(style.getTableFont());
        label.setHorizontalAlignment(horizontalAlignment);
        label.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2));

        if (row % 2 == 0) {
            label.setBackground(style.getTableLineColorEven());
        } else {
            label.setBackground(style.getTableLineColorOdd());
        }
        label.setForeground(style.getTableForegroundColor());

        if (isSelected || table.isRowSelected(row)) {
            label.setForeground(style.getTableSelectionForegroundColor());
            label.setBackground(style.getTableSelectionBackGroundColor());
        }

        return label;
    }

    protected void setValue(Object value) {
        setText((value == null) ? "" : value.toString());
    }

}
