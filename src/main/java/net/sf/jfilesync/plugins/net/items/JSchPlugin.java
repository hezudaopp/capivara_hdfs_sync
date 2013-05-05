/*
 * capivara - Java File Synchronization
 *
 * Created on 03-Oct-2005
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.engine.TFileAttributes;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class JSchPlugin implements ConnectionPlugin {

	private static Logger LOGGER = Logger.getLogger(JSchPlugin.class
			.getPackage().getName());

	private JSch jsch = new JSch();
	private Session session;
	private ChannelSftp sftpChannel;
	private TPathControlInterface pathControl = TPathControlFactory
			.getPathControlInstance("net");

	private JSchSftpProgressMonitor monitor = new JSchSftpProgressMonitor();

	public JSchPlugin() {

	}

	public void connect(TConnectionData connectData)
			throws PluginConnectException {

		try {
			session = jsch.getSession(connectData.getUser(),
					connectData.getHost(), connectData.getPort());

			UserInfo ui = new MyUserInfo(connectData);
			session.setUserInfo(ui);

			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;

		} catch (JSchException e) {
			e.printStackTrace();
			throw new PluginConnectException(
					TErrorHandling.ERROR_CONNECTION_AUTHENTICATION,
					e.getMessage());
		}

	}

	public TConnectionData getTConnectionData() {
		return null;
	}

	public void disconnect() {
		sftpChannel.disconnect();
		session.disconnect();
	}

	public boolean isConnected() {
		boolean con = false;
		if (session != null && session.isConnected()) {
			con = true;
		}
		return con;
	}

	public boolean isLocalConnection() {
		return false;
	}

	public TPathControlInterface getPathControl() {
		return pathControl;
	}

	public TFileData ls() throws IOException {
		try {
			return ls(sftpChannel.pwd());
		} catch (SftpException e) {
			throw new IOException(e);
		}
	}

	public TFileData ls(String path) throws IOException {
		TFileData data = new TFileData();
		try {
			@SuppressWarnings("rawtypes")
			Vector v = sftpChannel.ls(path);
			for (int i = 0; i < v.size(); i++) {
				LsEntry entry = (LsEntry) v.get(i);
				if (entry.getFilename().equals(".")
						|| entry.getFilename().equals("..")) {
					continue;
				} else {
					data.addFileProperties(extractFileProperties(path, entry));
				}
			}
		} catch (SftpException e) {
			throw new IOException(e.getMessage());
		}
		return data;
	}

	protected TFileProperties extractFileProperties(final String path,
			final LsEntry file) {
		TFileProperties fileProp = new TFileProperties();

		fileProp.setFileName(file.getFilename());
		fileProp.setAbsoluteFileName(pathControl.appendDirectory(path,
				file.getFilename()));

		if (file.getFilename().startsWith(".")) {
			fileProp.setHiddenFlag(true);
		}

		SftpATTRS attr = file.getAttrs();
		fileProp.setFileModTime(attr.getMTime() * 1000L);
		fileProp.setFileSize(BigInteger.valueOf(attr.getSize()));
		fileProp.setDirectoryFlag(attr.isDir());
		fileProp.setLinkFlag(attr.isLink());
		TFileAttributes myAttr = new TFileAttributes();
		myAttr.setPermissions(file.getAttrs().getPermissions());
		fileProp.setAttributes(myAttr);

		return fileProp;
	}

	public void chdir(String path) throws IOException {
		try {
			LOGGER.info("change to " + path);
			sftpChannel.cd(path);
			LOGGER.info("changed to " + path);
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public String getAbsolutePath() {
		try {
			return sftpChannel.pwd();
		} catch (SftpException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean exists(String path) {
		boolean exists = false;
		try {
			sftpChannel.stat(path);
			exists = true;
		} catch (SftpException e) {
			// LOGGER.info("NOT EXISTS");
			// e.printStackTrace();
			// when file doesn't exists then exception gets thrown
		}
		return exists;
	}

	public void put(InputStream instream, String remoteFileName)
			throws IOException {

		// synchronized (this) {
		// inPut = true;
		// }

		try {
			monitor.reset();
			sftpChannel.put(instream, remoteFileName, monitor);
			// sftpChannel.put(instream, remoteFileName);
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

	}

	public void get(String remoteFileName, OutputStream outstream)
			throws IOException {

		// synchronized (this) {
		// inPut = false;
		// }

		try {
			monitor.reset();
			sftpChannel.get(remoteFileName, outstream, monitor);
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public void mkdir(String dir) throws IOException {
		try {
			sftpChannel.mkdir(dir);
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public void mkdirs(final String dir) throws IOException {
		String completeDir = dir;
		if (pathControl.isRoot(completeDir)) {
			return;
		} else {
			if (completeDir.endsWith("/")) {
				completeDir = completeDir
						.substring(0, completeDir.length() - 1);
			}
			StringTokenizer tok = new StringTokenizer(completeDir, "/");
			String currentDir = "";
			while (tok.hasMoreTokens()) {
				currentDir += "/" + tok.nextToken();
				if (!exists(currentDir)) {
					mkdir(currentDir);
				}
			}
		}
	}

	public String pwd() {
		try {
			return sftpChannel.pwd();
		} catch (SftpException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void remove(String file) throws IOException {
		try {
			sftpChannel.rm(file);
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public void rmdir(String dir) throws IOException {
		try {
			sftpChannel.rmdir(dir);
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public synchronized void abort() throws IOException {
		// abort = true;
		// if( instream != null ) {
		// instream.close();
		// }
		// if( outstream != null ) {
		// outstream.close();
		// }

		// try {
		// if( inPut ) {
		// sftpChannel.cancelPut();
		// } else {
		// sftpChannel.cancelGet();
		// }
		// } catch(SftpException e) {
		// e.printStackTrace();
		// }
		monitor.cancel();

	}

	public void rename(String oldpath, String newpath) throws IOException {
		try {
			sftpChannel.rename(oldpath, newpath);
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public void setModificationTime(String file, long mtime) throws IOException {
		try {
			sftpChannel.setMtime(file, (int) (mtime / 1000L));
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public void setPermissions(String file, int permissions) throws IOException {
		try {
			SftpATTRS attr = sftpChannel.stat(file);
			attr.setPERMISSIONS(permissions);
			sftpChannel.setStat(file, attr);
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public void symlink(String dir, String link) throws IOException {
	}

	public boolean isLink(String path) throws IOException {
		boolean islink = false;
		try {
			SftpATTRS attr = sftpChannel.stat(path);
			// System.out.println("permissions " +
			// Integer.toBinaryString(attr.getPermissions()));
			islink = attr.isLink();
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		return islink;
	}

	public boolean isFile(String path) throws IOException {
		boolean isfile = false;
		try {
			SftpATTRS attr = sftpChannel.stat(path);
			isfile = !attr.isDir();
		} catch (SftpException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		return isfile;
	}

	public int getConnectionID() {
		return ConnectionPluginManager.JSCH_SFTP_PLUGIN;
	}

	public boolean hasConnectionOptions() {
		return false;
	}

	public boolean requiresPassword() {
		return true;
	}

	// Jawinton
	@Override
	public boolean requiresUsername() {
		return true;
	}

	public String getProtocolString() {
		return "sftp";
	}

	public boolean requiresPort() {
		return true;
	}

	public int getDefaultPort() {
		return 22;
	}

	public boolean isProvided(int feature) {
		boolean back = false;

		switch (feature) {
		case PROVIDES_FILETRANSFER:
		case PROVIDES_PERMISSION_HANDLING:
			// not yet case PROVIDES_SYMLINK_HANDLING:
			back = true;
			break;
		}
		return back;
	}

	public String getName() {
		return "JSch plugin";
	}

	public String getDescription() {
		return "Java Secure Channel plugin (SFTP, experimental)";
	}

	public String getVersionString() {
		return getMajorVersion() + "." + getMinorVersion();
	}

	public int getMajorVersion() {
		return 0;
	}

	public int getMinorVersion() {
		return 2;
	}

	public String getLicense() {
		return "BSD style";
	}

	public PluginOptionPanel getConnectionOptionPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {

		private String passwd;
		// private JTextField passwordField = (JTextField) new
		// JPasswordField(20);
		private final TConnectionData data;

		public MyUserInfo(TConnectionData data) {
			this.data = data;
		}

		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			Object[] options = { "yes", "no" };
			int foo = JOptionPane.showOptionDialog(null, str, "Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			return foo == 0;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			// Object[] ob = { passwordField };
			// int result = JOptionPane.showConfirmDialog(null, ob, message,
			// JOptionPane.OK_CANCEL_OPTION);
			// if (result == JOptionPane.OK_OPTION) {
			// passwd = passwordField.getText();
			// return true;
			// } else {
			// return false;
			// }
			passwd = data.getPassword();
			return true;
		}

		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		public String[] promptKeyboardInteractive(String destination,
				String name, String instruction, String[] prompt, boolean[] echo) {
			return new String[] { data.getPassword() };
		}
	}

	static class JSchSftpProgressMonitor implements SftpProgressMonitor {

		private boolean cancelled;

		public synchronized void reset() {
			cancelled = false;
		}

		public synchronized void cancel() {
			cancelled = true;
		}

		public boolean count(long arg0) {
			return !cancelled; // true to continue
		}

		public void end() {
		}

		public void init(int arg0, String arg1, String arg2, long arg3) {

		}

	}

	public void setHidden(String path) throws IOException {
		// TODO Auto-generated method stub

	}

}
