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
 */

package net.sf.jfilesync.engine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;

/**
 * This class takes <code>TFileData</code> and sorts it according to a specified
 * property of <code>TFileProperties</code>. It uses Konquerer style sorting as
 * directories are always displayed first.
 * 
 * @author Sascha Hunold
 * @see TFileData
 * @see TFileProperties
 */

public class TFileDataSorter {

    public static final int SORT_ASCENDING = 0, SORT_DESCENDING = 1;

    private static final int LESS = -1, EQUAL = 0, GREATER = 1;

    private TFileData fileData, dataCopy;
    private int lastDirection;
    private int lastSortedProperty;

    private int filter = 0;
    public static final int FILTER_HIDDEN_FILES = 0x1;

    private final static Logger LOGGER = Logger.getLogger(TFileDataSorter.class
            .getPackage().getName());

    public TFileDataSorter(int direction, int sortProperty) {
        fileData = null;
        lastDirection = direction;
        lastSortedProperty = sortProperty;
    }

    public TFileDataSorter(TFileData fileData) {
        if (fileData == null) {
            throw new NullPointerException("fileData");
        }
        this.fileData = fileData;
        this.dataCopy = (TFileData) fileData.clone();
        lastDirection = SORT_ASCENDING;
        lastSortedProperty = TFileProperties.TFILE_PROPERTY_NAME;
    }

    public void setFileData(TFileData fileData) {
        this.fileData = fileData;
        if (this.fileData != null) {
            dataCopy = (TFileData) fileData.clone();
        }
    }

    public void sortFileDataBy(int pFileProperty) {
        sortFileDataBy(pFileProperty, TFileDataSorter.SORT_ASCENDING);
    }

    public void sortFileDataByLastPropertyUsed() {
        sortFileDataBy(lastSortedProperty, lastDirection);
    }

    public void sortFileDataInverse(int pFileProperty) {
        if (lastSortedProperty == pFileProperty) {
            lastSortedProperty = pFileProperty;
            if (lastDirection == SORT_ASCENDING) {
                lastDirection = SORT_DESCENDING;
            } else {
                lastDirection = SORT_ASCENDING;
            }
        } else {
            lastDirection = SORT_ASCENDING;
            lastSortedProperty = pFileProperty;
        }

        sortFileDataBy(lastSortedProperty, lastDirection);
    }

    public void sortFileDataBy(int fileProperty, int direction) {

        boolean wasSorted = false;

        if (dataCopy == null) {
            return;
        }

        List<TFileProperties> fileList = dataCopy.getFileProperties();
        switch (fileProperty) {
        case TFileProperties.TFILE_PROPERTY_NAME:
            Collections
                    .sort(fileList, new FileNameComparator(direction, false));
            wasSorted = true;
            break;
        case TFileProperties.TFILE_PROPERTY_SIZE:
            Collections.sort(fileList, new FileSizeComparator(direction));
            wasSorted = true;
            break;
        case TFileProperties.TFILE_PROPERTY_MOD_TIME:
            Collections.sort(fileList, new ModTimeComparator(direction));
            wasSorted = true;
            break;
        case TFileProperties.TFILE_PROPERTY_ABSOLUTE_NAME:
            Collections.sort(fileList, new FileNameComparator(direction, true));
            wasSorted = true;
            break;
        }

        if (wasSorted) {
            lastDirection = direction;
            lastSortedProperty = fileProperty;
            // let the data know about its state of sort
            dataCopy.setCurrentSortedProperty(fileProperty, direction);
        }

    }

    public TFileData getFileDataCopy() {
        // it's more efficient to filter before sorting
        // TODO
        return filterData(dataCopy);
    }

    public void addFilter(int pfilter) {
        filter |= pfilter;
    }

    public boolean isFilterSet(int pfilter) {
        return ((filter & pfilter) > 0);
    }

    public void removeFilter(int pfilter) {
        if (isFilterSet(pfilter)) {
            filter ^= pfilter;
        }
    }

    // later we'll have a stand-alone class TFileFilter
    private TFileData filterData(TFileData data) {

        if (filter == 0) { // no filter
            return data;
        }

        TFileProperties[] infiles = data.getFilePropertiesArray();
        TFileData filteredData = new TFileData();
        for (int i = 0; i < infiles.length; i++) {
            if (isFilterSet(FILTER_HIDDEN_FILES)) {
                if (!infiles[i].isHidden()) {
                    filteredData.addFileProperties(infiles[i]);
                }
            }
        }
        filteredData
                .setCurrentSortedProperty(lastSortedProperty, lastDirection);
        return filteredData;
    }

    /**
     * generic sorting class
     */

    public abstract class TAbstractComparator implements
            Comparator<TFileProperties> {

        private int direction;

        public TAbstractComparator(int direction) {
            this.direction = direction;
        }

        public int compare(TFileProperties prop1, TFileProperties prop2) {
            int compareValue = EQUAL;

            compareValue = getCompareValue(prop1, prop2);

            if (containsDirectory(prop1, prop2) == 0) {
                if (direction == TFileDataSorter.SORT_DESCENDING) {
                    compareValue = (compareValue == LESS) ? GREATER : LESS;
                }
            }

            return compareValue;
        }

        public abstract int getCompareValue(TFileProperties prop1,
                TFileProperties prop2);

        /**
         * checks whether prop1 or prop2 is a directory return values: LESS :
         * prop1 is directory GREATER: prop2 is directory EQUAL : both are
         * either dirs or files
         */
        protected int containsDirectory(TFileProperties prop1,
                TFileProperties prop2) {

            if (prop1.isDirectory() && !prop2.isDirectory()) {
                return LESS;
            }

            if (!prop1.isDirectory() && prop2.isDirectory()) {
                return GREATER;
            }

            return EQUAL;
        }

        /**
         * helper method for Sort classes
         * 
         * @param fname1
         * @param fname2
         * @return LESS, EQUAL, GREATER
         */
        protected int fileNameCompare(String fname1, String fname2) {
            int cv = fname1.compareTo(fname2);
            if (cv < 0) {
                return LESS;
            } else if (cv == 0) {
                return EQUAL;
            } else {
                return GREATER;
            }
        }

    }

    /*
     * compares file names
     */

    class FileNameComparator extends TAbstractComparator {

        private boolean absolute;
        private boolean caseInsensitive;

        public FileNameComparator(int direction, boolean absolute) {
            super(direction);
            this.absolute = absolute;

            try {
                caseInsensitive = MainWin.config
                        .getProgramSettings()
                        .getBooleanOption(
                                TProgramSettings.OPTION_FILE_TABLE_CASE_INSENSITIVE);
            } catch (SettingsTypeException e) {
                LOGGER.warning("cannot read option: "
                        + "OPTION_FILE_TABLE_CASE_INSENSITIVE");
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }

        }

        public int getCompareValue(TFileProperties prop1, TFileProperties prop2) {
            int compareValue = EQUAL;
            int dirCheck;
            String name1, name2;

            if (!absolute) {
                name1 = prop1.getFileName();
                name2 = prop2.getFileName();
            } else {
                name1 = prop1.getAbsoluteFileName();
                name2 = prop2.getAbsoluteFileName();
            }

            if( caseInsensitive ) {
                name1 = name1.toLowerCase();
                name2 = name2.toLowerCase();
            }
            
            if (name1.compareTo("..") == 0) {
                if (name2.compareTo(".") == 0) {
                    compareValue = GREATER;
                } else {
                    compareValue = LESS;
                }
            }

            if ((dirCheck = containsDirectory(prop1, prop2)) != 0) {
                return dirCheck;
            }

            if (compareValue != EQUAL) {
                return compareValue;
            } else {
                return fileNameCompare(name1, name2);
            }
        }
    }

    /*
     * compares the size of files
     */
    class FileSizeComparator extends TAbstractComparator {

        public FileSizeComparator(int direction) {
            super(direction);
        }

        public int getCompareValue(TFileProperties prop1, TFileProperties prop2) {
            int retval = 0;

            retval = containsDirectory(prop1, prop2);

            if (retval == 0) {

                retval = prop1.getFileSize().compareTo(prop2.getFileSize());

                if (retval == 0) {
                    // if still equal lets sort by name
                    retval = fileNameCompare(prop1.getFileName(), prop2
                            .getFileName());
                }
            }

            /*
             * if( prop1.isDirectory() && prop2.isDirectory() ) { return
             * fileNameCompare(prop1.getFileName(), prop2.getFileName()); }
             * else{ return prop1.getFileSize().compareTo(prop2.getFileSize());
             * }
             */

            return retval;
        }
    }

    /*
     * sorting modification time
     */
    class ModTimeComparator extends TAbstractComparator {

        public ModTimeComparator(int direction) {
            super(direction);
        }

        public int getCompareValue(TFileProperties prop1, TFileProperties prop2) {
            int compareValue = EQUAL, dirCheck;

            if ((dirCheck = containsDirectory(prop1, prop2)) != 0)
                return dirCheck;

            long modtime1 = prop1.getFileModTime();
            long modtime2 = prop2.getFileModTime();

            if (modtime1 < modtime2) {
                compareValue = LESS;
            } else if (modtime1 > modtime2) {
                compareValue = GREATER;
            } else {
                compareValue = fileNameCompare(prop1.getFileName(), prop2
                        .getFileName());
            }

            return compareValue;
        }
    }

}
