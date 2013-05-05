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
 * $Id: TFileData.java,v 1.4 2006/04/02 14:47:42 hunold Exp $
 */

package net.sf.jfilesync.engine;


import java.util.ArrayList;
import java.util.List;

/*
 * our file-list
 */

public class TFileData implements Cloneable {

    private final List<TFileProperties> fileProperties = new ArrayList<TFileProperties>();
    private int[] supportedProperties;
    private int lastSortedProperty = -1;
    private int lastSortDirection = -1;

    public TFileData() {
        supportedProperties = TFileProperties.getPublishableProperties();
    }

    public void addFileProperties(TFileProperties fileProp) {
        fileProperties.add(fileProp);
    }

    public int getNumberOfFilePropertiesToPublish() {
        return TFileProperties.getNumberOfPublishableProperties();
    }

    public int getNumberofFiles() {
        return fileProperties.size();
    }

    public long getSizeofFilesInKByte() {
        long size = 0;
        for (int i = 0; i < fileProperties.size(); i++) {
            TFileProperties file = (TFileProperties) fileProperties.get(i);
            size += (file.getFileSize().longValue() / 1024);
        }
        return size;
    }

    public TFileProperties getFileProperties(int pos) {
        // I know I know, exception/error handling

        // pos equals the row in the table
        return (TFileProperties) fileProperties.get(pos);
    }

    public int getPropertyAtColumn(int colIndex) {
        try {
            return supportedProperties[colIndex];
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    public Object clone() {
        TFileData cloneData = new TFileData();
        for (int i = 0; i < fileProperties.size(); i++)
            cloneData
                    .addFileProperties((TFileProperties) ((TFileProperties) fileProperties
                            .get(i)).clone());
        return cloneData;
    }

    public List<TFileProperties> getFileProperties() {
        return fileProperties;
    }

    public TFileProperties[] getFilePropertiesArray() {
        TFileProperties[] props = new TFileProperties[fileProperties.size()];
        for (int i = 0; i < fileProperties.size(); i++) {
            props[i] = (TFileProperties) fileProperties.get(i);
        }
        return props;
    }

    /**
     * returns the last sorted property or -1 if never sorted
     *
     * @see TFileDataSorter
     */
    public int getCurrentSortedProperty() {
        return lastSortedProperty;
    }

    /**
     * returns the last used direction to sort or -1 if never sorted
     *
     * @see TFileDataSorter
     */
    public int getLastSortDirection() {
        return lastSortDirection;
    }

    protected void setCurrentSortedProperty(int property, int direction) {
        lastSortedProperty = property;
        lastSortDirection = direction;
    }

}
