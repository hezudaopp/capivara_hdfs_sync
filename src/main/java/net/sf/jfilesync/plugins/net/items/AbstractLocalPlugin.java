/*
 * capivara - Java File Synchronization
 *
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
 */
package net.sf.jfilesync.plugins.net.items;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.gui.term.TTerminal;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.util.TPathControlInterface;

public abstract class AbstractLocalPlugin implements ConnectionPlugin {

	private boolean aborted = false;
	static Logger log = Logger.getLogger(AbstractLocalPlugin.class.getName());
	// static final int bufsize = 131072; // 128 K
	static final int bufsize = 524288; // 512 K
	static byte[] buf = new byte[bufsize];

	public boolean isLocalConnection() {
		return true;
	}

	public void put(InputStream instream, String remoteFileName)
			throws IOException {
		File targetFile = new File(remoteFileName);
		targetFile.createNewFile();
		FileOutputStream outstream = new FileOutputStream(targetFile);
		copyStream(instream, outstream);
	}

	public void put(String localFileName, String remoteFileName)
			throws IOException {
		File sourceFile = new File(localFileName);
		File targetFile = new File(remoteFileName);
		boolean wellCreated = targetFile.createNewFile();
		if (!wellCreated) {
			log.warning(targetFile + " already exists");
		}
		FileInputStream instream = new FileInputStream(sourceFile);
		FileOutputStream outstream = new FileOutputStream(targetFile);
		copyStream(instream, outstream);
	}

	public void get(String remoteFileName, String localFileName)
			throws IOException {
		put(remoteFileName, localFileName);
	}

	public void get(String remoteFileName, OutputStream outstream)
			throws IOException {
		FileInputStream instream = new FileInputStream(remoteFileName);
		copyStream(instream, outstream);
	}

	protected void copyStream(InputStream instream, OutputStream outstream)
			throws IOException {
		int bytesread;

		synchronized (this) {
			aborted = false;
		}

		try {

			// if ((instream instanceof FileInputStream)
			// && (outstream instanceof FileOutputStream)) {
			// FileChannel inChannel = ((FileInputStream) instream)
			// .getChannel();
			// FileChannel outChannel = ((FileOutputStream) outstream)
			// .getChannel();
			//
			// inChannel.transferTo(0, inChannel.size(), outChannel);
			//
			// inChannel.close();
			// outChannel.close();
			// } else {

			synchronized (buf) {
				while ((bytesread = instream.read(buf)) != -1 && !aborted) {
					outstream.write(buf, 0, bytesread);
				}
			}
			// }

		} catch (IOException e) {
			throw e;
		} finally {
			instream.close();
			outstream.close();
		}

	}

	public void mkdir(String dir) throws IOException {
		String completeDir = dir;
		if (!getPathControl().startsWithRoot(dir)) {
			completeDir = getPathControl().appendDirectory(getAbsolutePath(),
					dir);
		}
		File file = new File(completeDir);

		if (file.exists()) {
			throw new IOException("Directory already exists : " + completeDir);
		}
		if (!file.mkdirs()) {
			throw new IOException("Could not create directory : " + completeDir);
		}
	}

	public void mkdirs(String dir) throws IOException {
		String completeDir = dir;
		if (!getPathControl().startsWithRoot(dir)) {
			completeDir = getPathControl().appendDirectory(getAbsolutePath(),
					dir);
		}
		File file = new File(completeDir);
		if (file.exists()) {
			throw new IOException("Directory already exists : " + completeDir);
		}
		if (!file.mkdirs()) {
			throw new IOException("Could not create directory : " + completeDir);
		}
	}

	public void remove(String file) throws IOException {
		deleteFile(file);
	}

	public void rmdir(String dirName) throws IOException {
		deleteFile(dirName);
	}

	protected void deleteFile(final String path) throws IOException {
		File file = new File(path);

		// if( file.exists() ) {
		// boolean deleted = file.delete();
		// if( ! deleted ) {
		// throw new IOException("Could not delete " + file.getAbsolutePath());
		// }
		// }
		// else {
		// throw new IOException("File does not exist : " +
		// file.getAbsolutePath());
		// }

		boolean deleted = file.delete();
		if (!deleted) {
			throw new IOException("Could not delete " + file.getAbsolutePath());
		}
	}

	public void abort() throws IOException {
		aborted = true;
	}

	public void rename(String oldpath, String newpath) throws IOException {
		File file = new File(oldpath);
		if (!file.exists())
			throw new IOException("file does not exists: " + oldpath);

		File newfile = new File(newpath);

		boolean ok = file.renameTo(newfile);
		if (!ok) {
			throw new IOException("Could not rename file to : " + newpath);
		}
	}

	public void setModificationTime(final String fileName, final long mtime)
			throws IOException {
		File f = new File(fileName);
		if (!f.exists()) {
			throw new IOException("Cannot set modification time for "
					+ fileName + " cause it doesn't exist");
		}

		boolean modTimeSet = f.setLastModified(mtime);
		if (!modTimeSet) {
			throw new IOException("modification time not properly set on "
					+ fileName);
		}
	}

	public boolean isFile(final String path) throws IOException {
		File f = new File(path);
		return f.isFile();
	}

	public boolean requiresPassword() {
		return false;
	}

	// Jawinton
	@Override
	public boolean requiresUsername() {
		return true;
	}

	public String getVersionString() {
		return getMajorVersion() + "." + getMinorVersion();
	}

	public abstract void connect(TConnectionData connectData)
			throws PluginConnectException;

	public abstract void disconnect();

	public abstract boolean isConnected();

	public abstract TPathControlInterface getPathControl();

	public abstract TFileData ls() throws IOException;

	public abstract TFileData ls(String path) throws IOException;

	public abstract void chdir(String path) throws IOException;

	public abstract String getAbsolutePath();

	public abstract boolean exists(String path);

	public abstract String pwd() throws IOException;

	public abstract void setPermissions(String file, int permissions)
			throws IOException;

	public abstract void symlink(String dir, String link) throws IOException;

	public abstract boolean isLink(String path) throws IOException;

	public abstract void shellRequest(TTerminal shellArea);

	public abstract int getConnectionID();

	public abstract boolean hasConnectionOptions();

	public abstract PluginOptionPanel getConnectionOptionPanel();

	public abstract String getProtocolString();

	public abstract boolean isProvided(int feature);

	public abstract String getName();

	public abstract String getDescription();

	public abstract int getMajorVersion();

	public abstract int getMinorVersion();

	public abstract String getLicense();

}
