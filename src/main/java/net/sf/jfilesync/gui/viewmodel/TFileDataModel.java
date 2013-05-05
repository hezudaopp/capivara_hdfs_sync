/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TFileDataModel.java,v 1.7 2006/06/09 20:58:45 hunold Exp $
 */

package net.sf.jfilesync.gui.viewmodel;

import java.util.logging.Logger;

import javax.swing.table.*;

import net.sf.jfilesync.engine.*;


public class TFileDataModel extends AbstractTableModel {

    private static final long serialVersionUID = 2517103019241632082L;
    private TFileData fileData;
    private TFileDataPresenter presenter;

    private int columns = -1;

    static Logger logger = Logger.getLogger(TFileDataModel.class.getPackage()
            .getName());

    public TFileDataModel(TFileData pFileData) {
        init(pFileData);
    }

    public int getRowCount() {
        // + dummy ".."
        return fileData.getNumberofFiles() + 1;
    }

    public int getColumnCount() {
        if (columns == -1) {
            columns = fileData.getNumberOfFilePropertiesToPublish();
        }
        return columns;
    }

    public void setColumnCount(int count) {
        columns = count;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex == 0) {
            // insert artifical ".."
            if (columnIndex == 0)
                return "..";
            else if (columnIndex == 1) {
                return "UP-DIR";
            } else
                return "";
        } else {
            rowIndex--; // ignore ".."

            switch (columnIndex) {
            case 0:
                // name
                // but the name is not actually returned
                return fileData.getFileProperties(rowIndex);
            case 1:
                // size
                return presenter.getSize(fileData.getFileProperties(rowIndex));
            case 2:
                // mod time
                return presenter.getModTime(fileData
                        .getFileProperties(rowIndex));
            case 3:
                return fileData.getFileProperties(rowIndex).getAttributes()
                        .getPermissionString();
            default:
                return "";
            }
        }
    }

    public final void updateView(TFileData fd) {
        init(fd);
    }

    private void init(TFileData fileData) {
        this.fileData = fileData;
        if (presenter == null) {
            presenter = new TFileDataPresenter();
        }
    }

    public String getColumnNameAt(int column) {
        return TFileProperties.getFilePropertyName(fileData
                .getPropertyAtColumn(column));
    }

    public int getColumnPropertyNameAt(int column) {
        int props[] = TFileProperties.getPublishableProperties();
        return props[column];
    }

    public TFileProperties getFilePropertiesAtRow(int row) {
        return fileData.getFileProperties(row - 1);
    }

    protected TFileData getFileData() {
        return fileData;
    }

    public void setFileData(TFileData fileData) {
        this.fileData = fileData;
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    /**
     *
     * @param fileName
     * @return row of file or -1 if file does not exist
     */
    public int getRowOfFile(String fileName) {
        int row = -1;
        if (fileData != null) {
            for (int i = 0; i < fileData.getNumberofFiles(); i++) {
                if (fileData.getFileProperties(i).getFileName()
                        .equals(fileName)) {
                    row = i + 1; // remember ".." dummy
                    break;
                }
            }
        }
        return row;
    }


    public void toggleRowIdForSync(int rowId) {
        if( rowId > 0 ) {
            getFilePropertiesAtRow(rowId).toggleSelectedForSync();
        }
    }

}
