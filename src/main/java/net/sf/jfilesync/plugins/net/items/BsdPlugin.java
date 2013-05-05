/*
 * capivara - Java File Synchronization
 *
 * Created on Sep 8, 2010
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
 <license/>
 *
 * $Id$
 */
package net.sf.jfilesync.plugins.net.items;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.gui.term.TTerminal;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.plugins.net.items.bsd.BsdLsReaderProxy;
import net.sf.jfilesync.util.TMiscTool;
import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;

public class BsdPlugin extends AbstractLocalPlugin {

	private String currentPath = pwd();
	static Logger log = Logger
			.getLogger(BsdPlugin.class.getPackage().getName());
	private final TPathControlInterface pci = TPathControlFactory
			.getPathControlInstance(TMiscTool.OS_LINUX);

	private TConnectionData connectData;
	private boolean connected = false;

	private final static int MAJOR = 0;
	private final static int MINOR = 1;

	private final BsdLsReaderProxy lsReader = new BsdLsReaderProxy(pci);

	public BsdPlugin() {
	}

	@Override
	public void connect(final TConnectionData connectData)
			throws PluginConnectException {
		this.connectData = connectData;
		connected = true;
	}

	@Override
	public TConnectionData getTConnectionData() {
		return connectData;
	}

	@Override
	public boolean requiresPort() {
		return false;
	}

	@Override
	public int getDefaultPort() {
		return 0;
	}

	@Override
	public void setHidden(final String path) throws IOException {
		// do nothing
	}

	@Override
	public void disconnect() {
		connected = false;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public TPathControlInterface getPathControl() {
		return pci;
	}

	@Override
	public TFileData ls() throws IOException {
		return ls(currentPath);
	}

	@Override
	public TFileData ls(final String path) throws IOException {

		final String[] cmdAr = { "ls", "-lacT", path };
		final Process proc = Runtime.getRuntime().exec(cmdAr);
		final InputStream instream = proc.getInputStream();
		final InputStreamReader isr = new InputStreamReader(instream);
		final StringBuffer lsBuffer = new StringBuffer();
		int b;

		while ((b = isr.read()) != -1) {
			lsBuffer.append((char) b);
		}

		return lsReader.ls(path, lsBuffer.toString());
	}

	@Override
	public void chdir(final String path) throws IOException {
		final String backup = currentPath;
		// the only thing is changing the absolutePath
		if (path.equals("..")) {
			currentPath = pci.getPathLevelUp(currentPath);
		} else {
			if (pci.startsWithRoot(path)) {
				currentPath = path;
			} else {
				currentPath = pci.appendDirectory(currentPath, path);
			}
		}

		// test if absolutePath valid
		final File test = new File(currentPath);
		if (!test.isDirectory()) {
			currentPath = backup;
			throw new IOException("Path does not exist : "
					+ test.getAbsoluteFile());
		}
	}

	@Override
	public String getAbsolutePath() {
		return currentPath;
	}

	@Override
	public boolean exists(final String path) {
		final File f = new File(path);
		return f.exists();
	}

	@Override
	public String pwd() {
		return (new File(".")).getAbsolutePath();
	}

	@Override
	public void setPermissions(final String file, final int permissions)
			throws IOException {
		final String permStr = GnuPermissionParser
				.getPermissionString(permissions);

		final String command[] = new String[3];
		command[0] = "chmod";
		command[1] = permStr;
		command[2] = file;

		final Process proc = Runtime.getRuntime().exec(command);
		proc.getErrorStream().close();
		proc.getInputStream().close();
		proc.getOutputStream().close();
	}

	@Override
	public void symlink(final String dir, final String link) throws IOException {
		log.warning("symlinks unsupported");
	}

	@Override
	public boolean isLink(final String path) throws IOException {
		boolean ret = false;

		final TFileData data = ls(path);
		if (data == null) {
			throw new IOException("Cannot get file attributes of : " + path
					+ "\n ls returns null");
		} else if (data.getFileProperties().size() != 1) {
			throw new IOException("Cannot get file attributes of : " + path
					+ "\n ls returns zero files");
		} else {
			ret = data.getFileProperties(0).isLink();
		}

		return ret;
	}

	@Override
	public void shellRequest(final TTerminal shellArea) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getConnectionID() {
		return ConnectionPluginManager.BSD_PLUGIN;
	}

	@Override
	public boolean hasConnectionOptions() {
		return false;
	}

	@Override
	public PluginOptionPanel getConnectionOptionPanel() {
		return null;
	}

	@Override
	public String getProtocolString() {
		return "local";
	}

	@Override
	public boolean isProvided(final int feature) {
		boolean back = false;

		switch (feature) {
		case PROVIDES_FILETRANSFER:
		case PROVIDES_PERMISSION_HANDLING:
		case PROVIDES_SYMLINK_HANDLING:
			back = true;
			break;
		}
		return back;
	}

	@Override
	public String getName() {
		return "BSD plugin";
	}

	@Override
	public String getDescription() {
		return "BSD plugin (Mac OS X) (beta)";
	}

	@Override
	public int getMajorVersion() {
		return MAJOR;
	}

	@Override
	public int getMinorVersion() {
		return MINOR;
	}

	@Override
	public String getLicense() {
		return "GPL";
	}

}
