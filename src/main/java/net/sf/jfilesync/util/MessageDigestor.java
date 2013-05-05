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
package net.sf.jfilesync.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;

public class MessageDigestor {

    private final String algorithm;

    public MessageDigestor(String algorithm) {
        this.algorithm = algorithm;
    }

    // public MessageDigest digest(final FileInputStream instream)
    // throws NoSuchAlgorithmException, IOException {
    //
    // MessageDigest digest = MessageDigest.getInstance(algorithm);
    // digest.reset();
    //
    // final int blockSize = 1024;
    // int read;
    // byte[] buf = new byte[blockSize];
    //
    // while ((read = instream.read(buf)) != -1) {
    // digest.update(buf, 0, read);
    // }
    //
    // return digest;
    // }

    public MessageDigest digest(final AbstractConnectionProxy con,
            final String absolutePath) throws NoSuchAlgorithmException,
            IOException {

        MessageDigest digest = null;

        if (con.isFile(absolutePath)) {
            digest = MessageDigest.getInstance(algorithm);
            digest.reset();

            FileInputStream instream = null;
            File tmpFile = null;

            if (con.isLocalConnection()) {
                instream = new FileInputStream(absolutePath);
            } else {
                tmpFile = File.createTempFile("tempfile", null);
                FileOutputStream tmpOutStream = new FileOutputStream(tmpFile);
                con.get(absolutePath, tmpOutStream);
                tmpOutStream.close();
                instream = new FileInputStream(tmpFile);
            }

            final int blockSize = 1024;
            int read;
            byte[] buf = new byte[blockSize];

            while ((read = instream.read(buf)) != -1) {
                digest.update(buf, 0, read);
            }

            if (tmpFile != null) {
                // delete tmp file
                tmpFile.delete();
            }

        }

        return digest;
    }

    public MessageDigest digest(final AbstractConnectionProxy con,
            final String absolutePath, final int bytes)
            throws NoSuchAlgorithmException, IOException {

        MessageDigest digest = null;

        if (con.isFile(absolutePath)) {
            digest = MessageDigest.getInstance(algorithm);
            digest.reset();

            FileInputStream instream = null;
            File tmpFile = null;

            if (con.isLocalConnection()) {
                instream = new FileInputStream(absolutePath);
            } else {
                tmpFile = File.createTempFile("tempfile", null);
                FileOutputStream tmpOutStream = new FileOutputStream(tmpFile);
                con.get(absolutePath, tmpOutStream);
                tmpOutStream.close();
                instream = new FileInputStream(tmpFile);
            }

            byte[] buf = new byte[bytes];
            int read;

            while ((read = instream.read(buf)) != -1) {
                digest.update(buf, 0, read);
            }

            if (tmpFile != null) {
                // delete tmp file
                tmpFile.delete();
            }

        }

        return digest;

    }

}
