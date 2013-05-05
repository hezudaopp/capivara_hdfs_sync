/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2008 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.service.unify;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.jfilesync.engine.BasicFile;
import net.sf.jfilesync.engine.worker.BasicFileTreeEvent;
import net.sf.jfilesync.engine.worker.events.FileProgressWorkerEvent;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.util.MessageDigestor;

public class UnifyEfficientDuplicateSearchWorker extends GWorker {

    private final static Logger LOGGER = Logger
            .getLogger(UnifyEfficientDuplicateSearchWorker.class.getName());

    private final UnifyFileTree fileTree;
    private final AbstractConnectionProxy con;

    private final Map<BigInteger, List<UnifyFile>> size2FilesMap = new HashMap<BigInteger, List<UnifyFile>>();
    private int fileNum;
    private int filesDone;

    private final MessageDigestor digestor = new MessageDigestor("MD5");
    private int SMALL_DIGEST_SIZE_BYTES = 1024;

    public UnifyEfficientDuplicateSearchWorker(
            final AbstractConnectionProxy con, final UnifyFileTree fileTree) {

        if (con == null) {
            throw new IllegalArgumentException("con must not be null");
        }
        if (fileTree == null) {
            throw new IllegalArgumentException("fileTree must not be null");
        }

        this.con = con;
        this.fileTree = fileTree;
    }

    @Override
    public GWorkerEvent construct() {
        return new BasicFileTreeEvent(this, fileTree);
    }

    @Override
    public void task() throws Exception {
        final List<BasicFile> fileList = fileTree.getFilesInTree();

        fileNum = fileTree.getNumberOfFiles();

        // break the list of files in chunks where each list contains files with
        // the same file size
        for (final BasicFile file : fileList) {
            if (isCancelled()) {
                break;
            }

            if (file.isDirectory()) {
                continue;
            }

            if (!(file instanceof UnifyFile)) {
                continue;
            }

            List<UnifyFile> sizeFileList = null;
            if (size2FilesMap.containsKey(file.getFileSize())) {
                sizeFileList = size2FilesMap.get(file.getFileSize());
                sizeFileList.add((UnifyFile) file);
            } else {
                sizeFileList = new ArrayList<UnifyFile>();
                sizeFileList.add((UnifyFile) file);
                size2FilesMap.put(file.getFileSize(), sizeFileList);
            }
        }

        findDuplicates(fileList);
    }

    private void findDuplicates(final List<BasicFile> fileList)
            throws IOException, NoSuchAlgorithmException {

        for (BasicFile file : fileList) {

            if (isCancelled()) {
                break;
            }

            if (file.isDirectory()) {
                continue;
            }

            if (!(file instanceof UnifyFile)) {
                continue;
            }

            final UnifyFile uFile1 = (UnifyFile) file;

            final int perc = filesDone * 100 / fileNum;

            getObserver().updateGUI(
                    new FileProgressWorkerEvent(this, file.getAbsolutePath(),
                            perc));

            boolean othersMarkedToKeep = false;

            for (UnifyFile uFile2 : size2FilesMap.get(file.getFileSize())) {
                if (isCancelled()) {
                    break;
                }
                if (file == uFile2) {
                    continue;
                }
                if (uFile2.isDirectory()) {
                    continue;
                }

                if (!uFile1.hasPreDigest()) {
                    computeSmallDigest(uFile1);
                }
                if (!uFile2.hasPreDigest()) {
                    computeSmallDigest(uFile2);
                }

                if (uFile1.hasSamePreDigest(uFile2.getPreDigest())) {
                    //System.out.println(file.getFileName() + " - " + file2.getFileName());

                    if (!uFile1.hasDigest()) {
                        computeDigest(uFile1);
                    }
                    if (!uFile2.hasDigest()) {
                        computeDigest(uFile2);
                    }

                    if (uFile1.isDuplicateOf(uFile2)) {
                        LOGGER.info(file.getAbsolutePath() + " same hash as "
                                + uFile2.getAbsolutePath());
                        uFile1.addDuplicate(uFile2);
                        if (uFile2.getKeepIt()) {
                            othersMarkedToKeep = true;
                        }
                    }
                }
            }

            if (!othersMarkedToKeep) {
                uFile1.setKeepIt(true);
            }

            filesDone++;
        }

        // delete digests
        for (BasicFile file : fileList) {
            if (file instanceof UnifyFile) {
                final UnifyFile uf = (UnifyFile) file;
                if (uf.hasDigest()) {
                    uf.setMessageDigest(null);
                }
            }
        }

    }

    private void computeDigest(UnifyFile file) throws IOException,
            NoSuchAlgorithmException {
        final MessageDigest digest = digestor.digest(con, file
                .getAbsolutePath());
        if (digest != null) {
            file.setMessageDigest(digest.digest());
        } else {
            LOGGER.severe("cannot digest file : " + file.getAbsolutePath());
        }
    }

    private void computeSmallDigest(UnifyFile file) throws IOException,
            NoSuchAlgorithmException {
        final MessageDigest digest = digestor.digest(con, file
                .getAbsolutePath(), SMALL_DIGEST_SIZE_BYTES);
        if (digest != null) {
            file.setMessagePreDigest(digest.digest());
        } else {
            LOGGER.severe("cannot digest file : " + file.getAbsolutePath());
        }
    }

}
