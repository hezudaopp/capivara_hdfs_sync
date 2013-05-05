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
package net.sf.jfilesync.gui.viewmodel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import net.sf.jfilesync.service.unify.UnifyFile;

public class BasicFileTable extends JTable {

    private static final long serialVersionUID = -3187615156599859777L;

    private final BasicFileTableRenderer tableCellRender;

    private final BasicFileTableMouseListener tableMouseListener = new BasicFileTableMouseListener(
            null);

    public BasicFileTable(BasicFileModel basicFileModel,
            BasicFileTableRenderer tableCellRender) {
        super(basicFileModel);
        this.tableCellRender = tableCellRender;

        getTableHeader().setReorderingAllowed(false);

        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        // setCellSelectionEnabled(true);
        // setColumnSelectionInterval(0, getColumnCount()-1);

        setColumnRenderer();
        adjustColumnWidths();
        addMouseListener(tableMouseListener);
    }

    public void adjustColumnWidths() {
        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(
                    tableCellRender.getBasicFileTableColumModel()
                            .getColumnWidth(i));
        }
    }

    @Override
    public void setModel(TableModel model) {
        super.setModel(model);
        setColumnRenderer();
    }

    private void setColumnRenderer() {
        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            getColumnModel().getColumn(i).setCellRenderer(tableCellRender);
        }
    }

    public void setAbstractBasicFileTreePopup(AbstractBasicFilePopup popup) {
        tableMouseListener.setAbstractBasicFileTreePopup(popup);
    }

    class BasicFileTableMouseListener extends MouseAdapter {

        private AbstractBasicFilePopup popup;

        public BasicFileTableMouseListener(AbstractBasicFilePopup popup) {
            this.popup = popup;
        }

        public void setAbstractBasicFileTreePopup(AbstractBasicFilePopup popup) {
            this.popup = popup;
        }

        public void mousePressed(MouseEvent e) {
            final int row = BasicFileTable.this.rowAtPoint(e.getPoint());
            if (e.isPopupTrigger()) {
                // final BasicFile file =
                // (BasicFile)BasicFileTable.this.getModel().getValueAt(row, 0);
                BasicFileTable.this.addRowSelectionInterval(row, row);
                // popup.setBasicFileContext(file);
                showPopup(e);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopup(e);
            }
        }

        private void showPopup(MouseEvent e) {
            if (popup != null) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public List<UnifyFile> getSelectedFiles() {
        final List<UnifyFile> selectedFiles = new ArrayList<UnifyFile>();
        for (int row : getSelectedRows()) {
            final UnifyFile file = (UnifyFile) getModel().getValueAt(row, 0);
            selectedFiles.add(file);
        }
        return selectedFiles;
    }

}
