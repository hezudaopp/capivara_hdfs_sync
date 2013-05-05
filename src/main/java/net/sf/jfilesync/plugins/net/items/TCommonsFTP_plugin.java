/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003, 2004 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TCommonsFTP_plugin.java,v 1.47 2006/08/09 22:18:39 hunold Exp $
 */

package net.sf.jfilesync.plugins.net.items;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.FilePermissions;
import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.engine.TFileAttributes;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.util.EncodingUtils;
import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class TCommonsFTP_plugin implements ConnectionPlugin {

	private FTPClient ftpClient;
	private TConnectionData conData;

	private final static Logger LOGGER = Logger
			.getLogger(TCommonsFTP_plugin.class.getName());

	// for aborting put/get
	private OutputStream outstream;
	private InputStream instream;
	private boolean abort = false;
	// private boolean connected = false;

	// private final CommonsFtpOptions optionPanel = new CommonsFtpOptions();

	private SimpleDateFormat modtimeFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public TCommonsFTP_plugin() {
		ftpClient = new FTPClient();
		if (EncodingUtils.getJVMEnconding().equals("UTF-8")) {
			ftpClient.setControlEncoding("UTF-8");
		}
	}

	public boolean isProvided(int feature) {
		boolean ret = false;

		switch (feature) {
		case PROVIDES_PERMISSION_HANDLING:
		case PROVIDES_FILETRANSFER:
			ret = true;
			break;
		}

		return ret;
	}

	public String getName() {
		return "Apache Commons Net FTP plugin";
	}

	public String getDescription() {
		return "FTP - Apache Commons Net";
	}

	public String getVersionString() {
		return getMajorVersion() + "." + getMinorVersion();
	}

	public int getMajorVersion() {
		return 0;
	}

	public int getMinorVersion() {
		return 3;
	}

	public String getLicense() {
		return "Apache License 2.0";
	}

	public TPathControlInterface getPathControl() {
		return TPathControlFactory.getPathControlInstance("net");
	}

	public void connect(TConnectionData connectData)
			throws PluginConnectException {

		this.conData = connectData;

		String username = conData.getUser();
		String hostname = conData.getHost();
		String passwd = conData.getPassword();
		int port = conData.getPort();

		LOGGER.info("trying to connect to :" + hostname);

		try {
			if (connectData.getEncoding() != null) {
				LOGGER.info("set ftp control encoding : "
						+ connectData.getEncoding());
				ftpClient.setControlEncoding(connectData.getEncoding());
			}
			if (port != -1 && port != getDefaultPort()) {
				ftpClient.connect(hostname, port);
			} else {
				ftpClient.connect(hostname);
			}
			ftpClient.enterLocalPassiveMode();
		} catch (IOException ioex) {
			ioex.printStackTrace();
			throw new PluginConnectException(
					TErrorHandling.ERROR_CONNECTION_FAILURE, ioex.getMessage());
		}

		int reply = ftpClient.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.disconnect();
				}
			} catch (IOException e) {
				throw new PluginConnectException(
						TErrorHandling.ERROR_CONNECTION_FAILURE, e.getMessage());
			}
		} else {
			boolean authOK = false;
			try {
				authOK = ftpClient.login(username, passwd);

				if (!authOK) {
					ftpClient.disconnect();

					throw new PluginConnectException(
							TErrorHandling.ERROR_CONNECTION_AUTHENTICATION,
							LanguageBundle.getInstance().getMessage(
									"plugin.connect.auth_failed"));
				}
			} catch (IOException ioex) {
				throw new PluginConnectException(
						TErrorHandling.ERROR_CONNECTION_AUTHENTICATION,
						ioex.getMessage());
			}

			// connected = true;
			LOGGER.info("ftp connect done");
		}

	}

	public TConnectionData getTConnectionData() {
		return conData;
	}

	public void disconnect() {
		try {
			// connected = false;
			ftpClient.logout();
			ftpClient.disconnect();
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		boolean isConnected = false;

		if (ftpClient.isConnected()) {
			isConnected = true;

			if (ftpClient != null) {
				try {
					ftpClient.pwd();
				} catch (IOException e) {
					isConnected = false;
				}
			}

		}
		return isConnected;
	}

	public boolean isLocalConnection() {
		return false;
	}

	public TFileData ls() throws IOException {
		return ls(ftpClient.printWorkingDirectory());
	}

	public TFileData ls(String path) throws IOException {
		TFileData fileData = new TFileData();
		FTPFile[] flist = null;

		if (path == null) {
			throw new NullPointerException("path is null");
		}

		if (path.equals(".")) {
			flist = ftpClient.listFiles();
		} else {
			// TODO connection root req.
			// need a root path for connection
			// user input or "/"
			chdir(path);
			flist = ftpClient.listFiles();
		}

		if (flist != null) {
			for (int i = 0; i < flist.length; i++) {
				if (flist[i] == null) {
					LOGGER.warning("Problem while listing directory. Possibly missing files");
					continue;
				} else if (flist[i].getName().compareTo("..") == 0
						|| flist[i].getName().compareTo(".") == 0) {
					continue; // filter . and ..
				} else {
					TFileProperties tfile = extractFileProperties(flist[i],
							flist);
					fileData.addFileProperties(tfile);
				}
			}
		}
		return fileData;
	}

	protected TFileProperties extractFileProperties(FTPFile file,
			FTPFile[] filesInDir) throws IOException {
		TFileProperties prop = new TFileProperties();

		prop.setFileName(file.getName());

		final String cwd = ftpClient.printWorkingDirectory();
		String fname = null;
		if (cwd.endsWith("/")) {
			fname = cwd + file.getName();
		} else {
			fname = cwd + "/" + file.getName();
		}
		prop.setAbsoluteFileName(fname);

		if (file.getName().startsWith(".")) {
			prop.setHiddenFlag(true);
		}

		// There is a little problem with ftp.getSize(), because it's sometimes
		// 0
		prop.setFileSize(new BigInteger(Long.toString(file.getSize())));
		// System.out.println(file.getName() + " , " + file.getTimestamp());
		prop.setFileModTime(file.getTimestamp().getTimeInMillis());
		// System.out.println("file: " + fname);
		// System.out.println("isDirectory: " + file.isDirectory());
		prop.setDirectoryFlag(file.isDirectory());
		prop.setLinkFlag(file.isSymbolicLink());

		int permissions = 0;

		permissions |= file.isDirectory() ? FilePermissions.S_IFDIR : 0;
		permissions |= file.isSymbolicLink() ? FilePermissions.S_IFLNK : 0;

		permissions |= file.hasPermission(FTPFile.USER_ACCESS,
				FTPFile.READ_PERMISSION) ? FilePermissions.S_IRUSR : 0;
		permissions |= file.hasPermission(FTPFile.USER_ACCESS,
				FTPFile.WRITE_PERMISSION) ? FilePermissions.S_IWUSR : 0;
		permissions |= file.hasPermission(FTPFile.USER_ACCESS,
				FTPFile.EXECUTE_PERMISSION) ? FilePermissions.S_IXUSR : 0;
		permissions |= file.hasPermission(FTPFile.GROUP_ACCESS,
				FTPFile.READ_PERMISSION) ? FilePermissions.S_IRGRP : 0;
		permissions |= file.hasPermission(FTPFile.GROUP_ACCESS,
				FTPFile.WRITE_PERMISSION) ? FilePermissions.S_IWGRP : 0;
		permissions |= file.hasPermission(FTPFile.GROUP_ACCESS,
				FTPFile.EXECUTE_PERMISSION) ? FilePermissions.S_IXGRP : 0;
		permissions |= file.hasPermission(FTPFile.WORLD_ACCESS,
				FTPFile.READ_PERMISSION) ? FilePermissions.S_IROTH : 0;
		permissions |= file.hasPermission(FTPFile.WORLD_ACCESS,
				FTPFile.WRITE_PERMISSION) ? FilePermissions.S_IWOTH : 0;
		permissions |= file.hasPermission(FTPFile.WORLD_ACCESS,
				FTPFile.EXECUTE_PERMISSION) ? FilePermissions.S_IXOTH : 0;

		final TFileAttributes attr = new TFileAttributes();
		attr.setPermissions(permissions);
		prop.setAttributes(attr);

		/*
		 * what needs to be done is implement caching of directories which have
		 * to be listed for link detection implement recursive link detection
		 * for links to links SaHu July 2006
		 */

		/*
		 * if( file.isSymbolicLink() ) { System.out.println("link target : " +
		 * file.getLink()); }
		 */

		// if( file.isSymbolicLink() ) {
		// // check if link points to dir
		// final String linkTarget = file.getLink();
		// final String linkTargetBaseName =
		// getPathControl().basename(linkTarget);
		// //System.out.println("link target basename: " + linkTargetBaseName);
		// if( linkTarget != null ) {
		// String linkContaingPath =
		// getPathControl().getPathLevelUp(linkTarget);
		// FTPFile[] targetFiles = null;
		// if( linkContaingPath.equals("") || linkContaingPath.equals(cwd) ) {
		// targetFiles = filesInDir;
		// } else {
		// //System.out.println("check dir : " + linkContaingPath);
		// targetFiles = ftpClient.listFiles(linkContaingPath);
		// }
		//
		//
		// if( targetFiles != null ) {
		// for(int i=0; i<targetFiles.length; i++) {
		// //System.out.println("> " + targetFiles[i].getName());
		// if( targetFiles[i].getName().equals(linkTargetBaseName) ) {
		// if( targetFiles[i].isDirectory() ) {
		// prop.setDirectoryFlag(true);
		// }
		// break;
		// }
		// }
		// }
		// }
		// }

		if (file.isSymbolicLink()) {
			final String linkTarget = file.getLink();
			boolean result = ftpClient.changeWorkingDirectory(linkTarget);
			if (result) {
				prop.setDirectoryFlag(true);
			}
			ftpClient.changeWorkingDirectory(cwd);
		}

		return prop;
	}

	public void chdir(final String path) throws IOException {
		if (path.equals(".")) {
			ftpClient.changeWorkingDirectory(ftpClient.printWorkingDirectory());
		} else {
			ftpClient.changeWorkingDirectory(path);
		}
	}

	public String getAbsolutePath() {
		String path = "";
		try {
			// System.err.println("pwd : " + ftpClient.printWorkingDirectory());
			ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
			String pwd = ftpClient.printWorkingDirectory();
			if (pwd != null) {
				path = pwd;
			}
		} catch (IOException e) {
			LOGGER.severe("FTP: could not get working dir: " + e.getMessage());
		}
		return path;
	}

	public boolean exists(String path) {
		boolean ret = false;
		try {
			boolean success = ftpClient.changeWorkingDirectory(path);
			if (success) {
				ret = true;
			}
			if (!ret) {
				String[] names = ftpClient.listNames(path);
				if (names != null && names.length > 0) {
					ret = true;
				}
			}
		} catch (IOException e) {
			LOGGER.warning(e.getMessage());
		}

		return ret;
	}

	public boolean isLink(String path) throws IOException {
		boolean ret = false;
		FTPFile[] file_list = ftpClient.listFiles(path);
		if (file_list != null && file_list.length == 1) {
			if (file_list[0].isSymbolicLink()) {
				ret = true;
			}
		}
		return ret;
	}

	public boolean isFile(String path) throws IOException {
		boolean ret = false;
		FTPFile[] file_list = ftpClient.listFiles(path);
		if (file_list != null && file_list.length == 1) {
			if (file_list[0].isFile()) {
				ret = true;
			}
		}
		return ret;
	}

	public void put(final InputStream instream, final String remoteFileName)
			throws IOException {
		synchronized (this) {
			abort = false;
			this.instream = instream;
		}
		try {
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.storeFile(remoteFileName, instream);
		} catch (IOException e) {
			if (!abort) {
				throw e;
			} else {
				checkConnectionState();
			}
		}
	}

	public void get(String remoteFileName, OutputStream outstream)
			throws IOException {
		synchronized (this) {
			abort = false;
			this.outstream = outstream;
		}
		try {
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(remoteFileName, outstream);
		} catch (IOException e) {
			if (!abort) {
				throw e;
			} else {
				checkConnectionState();
			}
		}
	}

	private void checkConnectionState() throws IOException {

		if (ftpClient == null) {
			throw new IOException(LanguageBundle.getInstance().getMessage(
					"plugin.connection.lost"));
		}

		if (!ftpClient.isConnected()) {
			throw new IOException(LanguageBundle.getInstance().getMessage(
					"plugin.connection.lost"));
		} else {
			LOGGER.info("complete pending command");
			try {
				ftpClient.completePendingCommand();
			} catch (FTPConnectionClosedException e) {
				// connected = false;
			}
		}

	}

	public void mkdir(String dir) throws IOException {
		ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
		ftpClient.makeDirectory(dir);
	}

	public void mkdirs(String dir) throws IOException {
		ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
		ftpClient.makeDirectory(dir);
	}

	public String pwd() {
		return getAbsolutePath();
	}

	public void remove(String fileName) throws IOException {
		ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
		ftpClient.deleteFile(fileName);
	}

	public void rmdir(String dir) throws IOException {
		ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
		boolean ok = ftpClient.removeDirectory(dir);
		if (!ok) {
			throw new IOException(LanguageBundle.getInstance().getMessage(
					"error.rmdir"));
		}
	}

	public void abort() throws IOException {
		// abort() does not work as expected
		// ftpClient.abort();

		abort = true;
		if (instream != null) {
			instream.close();
		}
		if (outstream != null) {
			outstream.close();
		}

	}

	public void rename(String oldpath, String newpath) throws IOException {
		ftpClient.rename(oldpath, newpath);
	}

	public void setModificationTime(final String file, final long mtime)
			throws IOException {
		Date mtimeDate = new Date(mtime);
		boolean commandOK = ftpClient.sendSiteCommand("MDTM "
				+ modtimeFormat.format(mtimeDate) + " " + file);
		if (!commandOK) {
			LOGGER.warning("Cannot set modtime on " + file);
		}
	}

	public void setPermissions(final String file, final int perm)
			throws IOException {
		// TODO implement

		final FilePermissions fP = new FilePermissions(perm);

		final String FTP_CMD = "chmod" + " " + fP.getOctalString() + " " + file;

		// LOGGER.info("sending command: " + FTP_CMD);

		// final int reply = ftpClient.sendCommand(FTP_CMD);
		final boolean ok = ftpClient.sendSiteCommand(FTP_CMD);

		if (!ok) {
			LOGGER.warning(ftpClient.getReplyString());
		}

		// if( FTPReply.isNegativePermanent(reply) ) {
		// //throw new IOException("CHMOD failed with reply code: " + reply);
		// LOGGER.warning("CHMOD failed with reply code: " + reply);
		// }
		//
		// if( !FTPReply.isPositiveCompletion(reply) ) {
		// LOGGER.warning("CHMOD not successful, reply code: " + reply);
		// }

	}

	public void symlink(String dir, String link) throws IOException {
		LOGGER.info("symlink unsupported");
	}

	public int getConnectionID() {
		return ConnectionPluginManager.JAKARTA_FTP_PLUGIN;
	}

	public boolean hasConnectionOptions() {
		// return true;
		return false;
	}

	public PluginOptionPanel getConnectionOptionPanel() {
		// return optionPanel;
		return null;
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
		return "ftp";
	}

	public boolean requiresPort() {
		return true;
	}

	public int getDefaultPort() {
		return 21;
	}

	public void setHidden(String path) throws IOException {
		// TODO Auto-generated method stub

	}

}
