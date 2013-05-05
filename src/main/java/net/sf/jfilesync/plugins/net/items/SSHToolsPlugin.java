/*
 * capivara - Java File Synchronization
 *
 * Copyright (c) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 * Copyright (c) Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TOpenSSH_plugin.java,v 1.54 2006/08/09 22:18:39 hunold Exp $
 */

package net.sf.jfilesync.plugins.net.items;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.engine.TFileAttributes;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.gui.dialog.TPasswordDialog;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;
import net.sf.jfilesync.sync2.projects.SyncProjectIOException;
import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sshtools.common.hosts.DialogKnownHostsKeyVerification;
import com.sshtools.j2ssh.FileTransferProgress;
import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.TransferCancelledException;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.KBIAuthenticationClient;
import com.sshtools.j2ssh.authentication.KBIPrompt;
import com.sshtools.j2ssh.authentication.KBIRequestHandler;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.configuration.ConfigurationException;
import com.sshtools.j2ssh.configuration.ConfigurationLoader;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.io.UnsignedInteger32;
import com.sshtools.j2ssh.sftp.FileAttributes;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.sftp.SftpSubsystemClient;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;

public class SSHToolsPlugin implements ConnectionPlugin {

	private SshClient sshClient;
	private SftpClient sftpClient;
	private SftpSubsystemClient sftpChannel;

	// status of connection
	private boolean CONNECTED = false;

	private TConnectionData connectData;

	private final static Logger LOGGER = Logger.getLogger(SSHToolsPlugin.class
			.getName());

	private SshOptionPanel optionPanel;
	private SshPassphraseDialog passphraseDialog;
	// password not required for key authentication
	private boolean passwordRequired = true;

	private SSHToolsTransferProgress progress = new SSHToolsTransferProgress();

	/*
	 * --------------------------------------------------------------------------
	 */
	public SSHToolsPlugin() {
		try {
			ConfigurationLoader.initialize(false);
		} catch (ConfigurationException e) {
			LOGGER.warning(e.getMessage());
		}
		sshClient = new SshClient();
	}

	protected SshClient getSshClient() {
		return sshClient;
	}

	public boolean isProvided(int feature) {
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

	public String getName() {
		return "SSHTools SFTP plugin";
	}

	public String getDescription() {
		return "SFTP - SSH2 by SSHTools";
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
		return "GPL";
	}

	public TPathControlInterface getPathControl() {
		return TPathControlFactory.getPathControlInstance("net");
	}

	public void connect(TConnectionData connectData)
			throws PluginConnectException {

		this.connectData = connectData;

		String username = connectData.getUser();
		String hostname = connectData.getHost();
		String passwd = connectData.getPassword();
		int port = connectData.getPort();

		try {
			final JFrame parentFrame = connectData.getParentComponent();

			boolean socksAuth = false;

			if (optionPanel != null) {
				socksAuth = optionPanel
						.isOptionEnabled(SshOptionPanel.OPT_SOCKS_PROXY);
			}

			if (socksAuth) {

				LOGGER.info("Using socks proxy");

				final SocksProxyData socksData = optionPanel
						.getSocksProxyData();
				final SshConnectionProperties props = new SshConnectionProperties();

				if (socksData == null) {
					throw new PluginConnectException(
							TErrorHandling.ERROR_CONNECTION_FAILURE,
							"Socks proxy data invalid");
				} else {
					fillSshConnectionProperties(props, connectData, socksData);
				}

				// port has been checked in fillSshConnectionProperties
				if (parentFrame != null) {
					sshClient.connect(props,
							new DialogKnownHostsKeyVerification(parentFrame));
				} else {
					sshClient.connect(props);
				}
			} else {
				// standard connect
				if (port != -1 && port != getDefaultPort()) {
					if (parentFrame != null) {
						sshClient
								.connect(hostname, port,
										new DialogKnownHostsKeyVerification(
												parentFrame));
					} else {
						sshClient.connect(hostname, port);
					}
				} else {
					if (parentFrame != null) {
						sshClient
								.connect(hostname,
										new DialogKnownHostsKeyVerification(
												parentFrame));
					} else {
						sshClient.connect(hostname);
					}
				}
			}

			int result = AuthenticationProtocolState.FAILED;
			boolean keyAuth = false;

			if (optionPanel != null) {
				keyAuth = optionPanel
						.isOptionEnabled(SshOptionPanel.OPT_KEY_AUTHENTICATION);
			}

			if (keyAuth) {

				PublicKeyAuthenticationClient pka = new PublicKeyAuthenticationClient();
				String fileName = optionPanel.getKeyFileName();
				LOGGER.info("key file : " + fileName);

				if (fileName == null || fileName.equals("")) {
					// try to locate a private key file
					String[] standardNames = new String[] { "id_rsa", "id_dsa" };
					String filesep = System.getProperty("file.separator");
					String baseName = System.getProperty("user.home") + filesep
							+ ".ssh";
					for (int i = 0; i < standardNames.length; i++) {
						String keyFileName = baseName + filesep
								+ standardNames[i];
						File keyFile = new File(keyFileName);
						if (keyFile.exists()) {
							fileName = keyFileName;
							break;
						}
					}
				}

				LOGGER.info("key file : " + fileName);

				SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(
						fileName));

				String passphrase = "";
				if (file.isPassphraseProtected()) {
					LOGGER.info("need passphrase");
					passphrase = getPassPhrase();
				}

				SshPrivateKey key = file.toPrivateKey(passphrase);

				pka.setUsername(username);
				pka.setKey(key);

				result = sshClient.authenticate(pka);
			} else {
				// FIRST authentication test - simple password
				PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
				pwd.setUsername(username);
				pwd.setPassword(passwd);

				result = sshClient.authenticate(pwd);

				// ----------------------------------------------------------
				// 2nd trial
				if (result == AuthenticationProtocolState.FAILED) {

					KBIAuthenticationClient kbi = new KBIAuthenticationClient();
					kbi.setUsername(username);

					// callback interface
					kbi.setKBIRequestHandler(new TCapivaraKBIRequestHandler());

					result = sshClient.authenticate(kbi);
				}
			}

			if (result == AuthenticationProtocolState.COMPLETE) {
				sftpClient = sshClient.openSftpClient();
				sftpChannel = sshClient.openSftpChannel();
				CONNECTED = true;

			} else {
				throw new PluginConnectException(
						TErrorHandling.ERROR_CONNECTION_AUTHENTICATION,
						LanguageBundle.getInstance().getMessage(
								"plugin.connect.auth_failed"));
			}

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw new PluginConnectException(
					TErrorHandling.ERROR_CONNECTION_FAILURE, e.getMessage());
		}
	}

	private void fillSshConnectionProperties(
			final SshConnectionProperties props, final TConnectionData conData,
			final SocksProxyData socksData) {

		props.setHost(conData.getHost());
		if (conData.getPort() != -1) {
			props.setPort(conData.getPort());
		}
		props.setUsername(conData.getUser());

		int type = socksData.getType();
		if (type == SocksProxyData.SOCKS4) {
			props.setTransportProvider(SshConnectionProperties.USE_SOCKS4_PROXY);
		} else if (type == SocksProxyData.SOCKS5) {
			props.setTransportProvider(SshConnectionProperties.USE_SOCKS5_PROXY);
		}

		props.setProxyHost(socksData.getHost());
		props.setProxyPort(socksData.getPort());
		props.setProxyUsername(socksData.getUser());
		props.setProxyPassword(socksData.getPassword());
	}

	public TConnectionData getTConnectionData() {
		return connectData;
	}

	public TFileData ls() throws IOException {
		return ls(sftpClient.pwd());
	}

	public TFileData ls(String path) throws IOException {
		final TFileData fileData = new TFileData();

		if (!isConnected()) {
			return fileData;
		}

		// get a list of files of remote directory
		@SuppressWarnings("unchecked")
		final List<SftpFile> remote_files = (List<SftpFile>) sftpClient
				.ls(path);

		for (final SftpFile currentFile : remote_files) {
			if (currentFile.getFilename().compareTo("..") == 0
					|| currentFile.getFilename().compareTo(".") == 0) {
				// filter . and .. out
				// ANYONE has doubts ??? tell me !
			} else {
				TFileProperties tfile = extractFileProperties(currentFile);
				fileData.addFileProperties(tfile);
			}
		}
		return fileData;
	}

	protected TFileProperties extractFileProperties(final SftpFile file) {
		TFileProperties fileProp = new TFileProperties();

		fileProp.setFileName(file.getFilename());

		if (file.getFilename().startsWith(".")) {
			fileProp.setHiddenFlag(true);
		}

		String absolutePath = file.getAbsolutePath();
		if (absolutePath.startsWith("//")) {
			// System.err.println(file.getAbsolutePath());
			absolutePath = absolutePath.replaceFirst("//", "/");
		}
		fileProp.setAbsoluteFileName(absolutePath);

		// this is a fix
		fileProp.setFileModTime(file.getAttributes().getModifiedTime()
				.longValue() * 1000L);
		fileProp.setFileSize(file.getAttributes().getSize().bigIntValue());

		// LOGGER.info("file : " + file.getFilename() + " dir : " +
		// file.isDirectory());

		if (file.getAttributes().isDirectory()) {
			fileProp.setDirectoryFlag(true);
		} else {
			fileProp.setDirectoryFlag(false);
		}

		TFileAttributes attr = new TFileAttributes();
		attr.setPermissions(file.getAttributes().getPermissions().intValue());

		/*
		 * logger.info( file.getFilename() + ": " +
		 * file.getAttributes().getPermissions() + " :" +
		 * attr.getPermissionString());
		 */

		fileProp.setAttributes(attr);

		fileProp.setLinkFlag(file.isLink());

		if (file.isLink()) {
			// check if it points to directory
			try {
				final String linkTarget = sftpChannel
						.getSymbolicLinkTarget(file.getAbsolutePath());
				final FileAttributes targetAttr = sftpChannel
						.getAttributes(linkTarget);
				if (targetAttr != null && targetAttr.isDirectory()) {
					fileProp.setDirectoryFlag(true);
				}
			} catch (IOException e) {
				LOGGER.warning("Cannot determine link target of "
						+ file.getAbsolutePath());
			}

		}

		return fileProp;
	}

	/*
	 * --------------------------------------------------------------------------
	 */

	public void disconnect() {
		try {
			sftpChannel.close();
			sftpClient.quit();
			sshClient.disconnect();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		/* if an error occurs - connection status is false, too */
		CONNECTED = false;
	}

	/* ------------------------------------------------------------------------- */

	public boolean isConnected() {
		boolean retval = false;
		if (sshClient != null && sshClient.isConnected()) {
			if (sftpClient != null && !sftpClient.isClosed()) {
				retval = sshClient.isAuthenticated();
			}
		}
		return retval;
	}

	public boolean isLocalConnection() {
		return false;
	}

	public void chdir(String path) throws IOException {
		sftpClient.cd(path);
	}

	public String getAbsolutePath() {
		if (sftpClient != null && CONNECTED) {
			return sftpClient.pwd();
		}
		return null;
	}

	public void put(final InputStream instream, final String remoteFileName)
			throws IOException {
		try {
			sftpClient.put(instream, remoteFileName, progress);
		} catch (TransferCancelledException e) {
			progress.reset();
		}
	}

	public void get(String remoteFileName, OutputStream outstream)
			throws IOException {
		try {
			sftpClient.get(remoteFileName, outstream, progress);
		} catch (TransferCancelledException e) {
			progress.reset();
		}

	}

	public void mkdir(String dir) throws IOException {
		sftpClient.mkdir(dir);
	}

	public void mkdirs(String dir) throws IOException {
		sftpClient.mkdirs(dir);
		/*
		 * workaround for missing IOException in SftpClient.java from j2ssh bug:
		 * if mkdirs fails j2ssh catch the exception quitely
		 */
		if (!exists(dir)) {
			throw new IOException("Could not create directory");
		}
	}

	public String pwd() {
		return sftpClient.pwd();
	}

	public void remove(String file) throws IOException {
		sftpClient.rm(file);
	}

	public void rmdir(String dir) throws IOException {
		sftpClient.rm(dir);
	}

	public synchronized void abort() throws IOException {
		progress.cancel();
	}

	public void rename(String oldpath, String newpath) throws IOException {
		sftpClient.rename(oldpath, newpath);
	}

	public void setModificationTime(String file, long mtime) throws IOException {
		FileAttributes attr = new FileAttributes();
		attr.setTimes(new UnsignedInteger32(mtime / 1000L),
				new UnsignedInteger32(mtime / 1000L));

		sftpChannel.setAttributes(file, attr);
	}

	public void setPermissions(final String file, final int perm)
			throws IOException {
		FileAttributes attr = new FileAttributes();
		attr.setPermissions(new UnsignedInteger32(perm));
		sftpChannel.setAttributes(file, attr);
	}

	public void setHidden(String path) throws IOException {
		// do nothing
	}

	public boolean exists(String path) {
		boolean exists = false;
		try {
			FileAttributes attr = sftpClient.stat(path);
			if (attr.isDirectory() || attr.isFile() || attr.isLink()) {
				exists = true;
			}
		} catch (IOException iex) {
			LOGGER.fine("file does not exist : " + path);
		}
		return exists;
	}

	public boolean isLink(String path) throws IOException {
		boolean ret = false;
		FileAttributes attr = sftpClient.stat(path);
		if (attr.isLink()) {
			ret = true;
		}
		return ret;
	}

	public boolean isFile(String path) throws IOException {
		boolean ret = false;
		FileAttributes attr = sftpClient.stat(path);
		if (attr.isFile()) {
			ret = true;
		}
		return ret;
	}

	public void symlink(String dir, String link) throws IOException {
		sftpClient.symlink(dir, link);
	}

	public int getConnectionID() {
		return ConnectionPluginManager.J2SSH_SFTP_PLUGIN;
	}

	public boolean hasConnectionOptions() {
		return true;
	}

	public PluginOptionPanel getConnectionOptionPanel() {
		if (optionPanel == null) {
			optionPanel = new SshOptionPanel();
		}
		return optionPanel;
	}

	private String getPassPhrase() {
		String passphrase = "";

		JFrame parent = null;
		if (SSHToolsPlugin.this.connectData != null) {
			parent = SSHToolsPlugin.this.connectData.getParentComponent();
		}

		if (passphraseDialog == null) {
			passphraseDialog = new SshPassphraseDialog(parent);
		}

		SshPassphraseGetter getter = new SshPassphraseGetter(passphraseDialog);

		try {
			SwingUtilities.invokeAndWait(getter);
		} catch (InterruptedException iex) {
			LOGGER.severe(iex.getMessage());
		} catch (InvocationTargetException itex) {
			LOGGER.severe(itex.getMessage());
		}

		passphrase = passphraseDialog.getPassword();
		if (passphrase == null) {
			passphrase = "";
		}

		return passphrase;
	}

	// Jawinton
	@Override
	public boolean requiresUsername() {
		return true;
	}

	public boolean requiresPassword() {
		if (optionPanel != null
				&& optionPanel
						.isOptionEnabled(SshOptionPanel.OPT_KEY_AUTHENTICATION)) {
			passwordRequired = false;
		} else {
			passwordRequired = true;
		}

		return passwordRequired;
	}

	public String getProtocolString() {
		return "sftp";
	}

	static class SshOptionPanel extends JPanel implements PluginOptionPanel,
			ActionListener {

		private static final long serialVersionUID = 2812722060870438819L;
		public final static int OPT_KEY_AUTHENTICATION = 1;
		public final static int OPT_SOCKS_PROXY = 2;

		private final JCheckBox pubKeyCheck = new JCheckBox();
		private final JTextField pubKeyFileField = new JTextField(20);

		private final SocksPanel socksPanel = new SocksPanel();

		public SshOptionPanel() {
			init();
		}

		private void init() {
			JPanel pubKeyPane = new JPanel();
			pubKeyPane.setLayout(new BoxLayout(pubKeyPane, BoxLayout.Y_AXIS));

			pubKeyCheck.setText(LanguageBundle.getInstance().getMessage(
					"sshplug.option.publickey.enabled"));

			final JLabel hintLabel = new JLabel(LanguageBundle.getInstance()
					.getMessage("sshplug.option.publickeyfile.hint"));

			pubKeyCheck.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			hintLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

			pubKeyPane.add(pubKeyCheck);
			pubKeyPane.add(hintLabel);

			JPanel pubKeyFilePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JButton chooseButton = new JButton("...");
			chooseButton.setActionCommand("choose");
			chooseButton.addActionListener(this);

			pubKeyFilePane.add(pubKeyFileField);
			pubKeyFilePane.add(chooseButton);
			pubKeyFilePane.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(),
					LanguageBundle.getInstance().getMessage(
							"sshplug.option.publickeyfile")));

			final JPanel optMainPanel = new JPanel();
			optMainPanel
					.setLayout(new BoxLayout(optMainPanel, BoxLayout.Y_AXIS));

			JPanel pubKeyPanel = new JPanel();
			pubKeyPanel.setLayout(new BoxLayout(pubKeyPanel, BoxLayout.Y_AXIS));

			pubKeyPane.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			pubKeyFilePane.setAlignmentX(JComponent.LEFT_ALIGNMENT);

			pubKeyPanel.add(pubKeyPane);
			pubKeyPanel.add(pubKeyFilePane);
			pubKeyPanel.setBorder(BorderFactory
					.createTitledBorder(LanguageBundle.getInstance()
							.getMessage("sshplug.option.publickey.title")));

			pubKeyPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			socksPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);

			optMainPanel.add(pubKeyPanel);
			optMainPanel.add(socksPanel);

			add(optMainPanel);
		}

		public JComponent getJComponent() {
			return this;
		}

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("choose")) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setFileHidingEnabled(false);
				int returnVal = fc.showOpenDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					pubKeyFileField.setText(fc.getSelectedFile()
							.getAbsolutePath());
				}
			}
		}

		public boolean isOptionEnabled(int option) {
			boolean retval = false;
			switch (option) {
			case OPT_KEY_AUTHENTICATION:
				retval = pubKeyCheck.isSelected();
				break;
			case OPT_SOCKS_PROXY:
				retval = socksPanel.isEnabled();
				break;
			}
			return retval;
		}

		public String getKeyFileName() {
			return pubKeyFileField.getText();
		}

		public SocksProxyData getSocksProxyData() {
			return new SocksProxyData(socksPanel.getType(),
					socksPanel.getHost(), socksPanel.getPort(),
					socksPanel.getUser(), socksPanel.getPassword());
		}

		public boolean hasValidData() {
			boolean valid = true;
			if (socksPanel.isEnabled()) {
				valid = socksPanel.hasValidData();
			}
			return valid;
		}

		public String getErrorMessage() {
			String error = "";
			if (!socksPanel.hasValidData()) {
				error = socksPanel.getErrorMessage();
			}
			return error;
		}

		public String getPanelName() {
			return "SSH plug-in";
		}

	}

	static class SocksPanel extends JPanel implements ItemListener {

		private static final long serialVersionUID = 1L;
		private JCheckBox socksCheck = new JCheckBox();
		private JRadioButton socks4Button = new JRadioButton("Socks4");
		private JRadioButton socks5Button = new JRadioButton("Socks5");
		private ButtonGroup socksGroup = new ButtonGroup();

		private JTextField hostField = new JTextField(20);
		private JTextField portField = new JTextField(20);
		private JTextField userField = new JTextField(20);
		private JPasswordField passwdField = new JPasswordField(20);

		private List<Component> compList = new ArrayList<Component>();

		public SocksPanel() {
			initUI();
			socksCheck.setSelected(false);
			socks5Button.setSelected(true);
			enableUI(false);
		}

		private void initUI() {

			socksCheck.setText(LanguageBundle.getInstance().getMessage(
					"sftp.socks.use"));
			socksCheck.addItemListener(this);

			socksGroup.add(socks4Button);
			socksGroup.add(socks5Button);

			compList.add(socks4Button);
			compList.add(socks5Button);
			compList.add(hostField);
			compList.add(portField);
			compList.add(userField);
			compList.add(passwdField);

			setBorder(BorderFactory.createTitledBorder("Socks proxy"));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			JLabel hostLabel = new JLabel(LanguageBundle.getInstance()
					.getMessage("label.host"));
			JLabel portLabel = new JLabel(LanguageBundle.getInstance()
					.getMessage("label.port"));
			JLabel userLabel = new JLabel(LanguageBundle.getInstance()
					.getMessage("label.user"));
			JLabel passwdLabel = new JLabel(LanguageBundle.getInstance()
					.getMessage("label.password"));

			JPanel socksCheckPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			socksCheckPanel.add(socksCheck);

			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			buttonPanel.add(socks4Button);
			buttonPanel.add(socks5Button);

			add(socksCheckPanel);
			add(buttonPanel);
			add(createElemPanel(hostLabel, hostField));
			add(createElemPanel(portLabel, portField));
			add(createElemPanel(userLabel, userField));
			add(createElemPanel(passwdLabel, passwdField));
		}

		// public Component add(Component c) {
		// if( c instanceof JComponent ) {
		// ((JComponent)c).setAlignmentX(JComponent.LEFT_ALIGNMENT);
		// }
		// return c;
		// }

		private JPanel createElemPanel(final JLabel lab,
				final JTextField textField) {
			JPanel subPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			// lab.setPreferredSize(new Dimension(50,25));
			subPane.add(lab);
			subPane.add(textField);
			return subPane;
		}

		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == socksCheck) {
				if (socksCheck.isSelected()) {
					enableUI(true);
				} else {
					enableUI(false);
				}
			}
		}

		private void enableUI(boolean enabled) {
			for (final Component c : compList) {
				c.setEnabled(enabled);
			}
		}

		public boolean isEnabled() {
			return socksCheck.isSelected();
		}

		public int getType() {
			int type = SocksProxyData.SOCKS5;
			if (socks4Button.isSelected()) {
				type = SocksProxyData.SOCKS4;
			}
			return type;
		}

		public String getHost() {
			return hostField.getText();
		}

		public int getPort() {
			return Integer.parseInt(portField.getText());
		}

		public String getUser() {
			return userField.getText();
		}

		public String getPassword() {
			return new String(passwdField.getPassword());
		}

		private boolean isPortValid() {
			boolean valid = true;
			try {
				Integer.parseInt(portField.getText());
			} catch (NumberFormatException e) {
				valid = false;
			}
			return valid;
		}

		private boolean isHostValid() {
			boolean valid = true;
			String host = getHost();
			if (host.equals("")) {
				valid = false;
			}
			return valid;
		}

		public boolean hasValidData() {
			boolean valid = true;
			valid &= isPortValid();
			valid &= isHostValid();
			return valid;
		}

		public String getErrorMessage() {
			String msg = "";
			if (!isPortValid()) {
				msg = LanguageBundle.getInstance().getMessage(
						"error.invalid_port");
			} else if (!isHostValid()) {
				msg = LanguageBundle.getInstance().getMessage(
						"error.invalid_host");
			}
			return msg;
		}

	}

	// static class SocksProxyData implements ISyncProjectSavable {
	static class SocksProxyData {
		public final static int SOCKS4 = 0;

		public final static int SOCKS5 = 1;
		private final int socksType;
		private final String host;
		private final int port;
		private final String user;
		private final String passwd;

		public SocksProxyData(final int socksType, final String host,
				final int port, final String user, final String passwd) {
			this.socksType = socksType;
			this.host = host;
			this.port = port;
			this.user = user;
			this.passwd = passwd;
		}

		public int getType() {
			return socksType;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		public String getUser() {
			return user;
		}

		public String getPassword() {
			return passwd;
		}

		public void load(Element parent) throws SyncProjectIOException {
			// TODO Auto-generated method stub

		}

		public Element save(Document document) throws SyncProjectIOException {

			// final Element socksElem =
			// document.createElement("SocksSettings");
			// TODO not implemented
			throw new SyncProjectIOException("socks settings cannot be saved");

			// return socksElem;
		}
	}

	static class SshPassphraseGetter implements Runnable {

		private final SshPassphraseDialog ppD;

		public SshPassphraseGetter(final SshPassphraseDialog ppD) {
			this.ppD = ppD;
		}

		public void run() {
			ppD.setVisible(true);
		}

	}

	class SshConnectionData extends TConnectionData {

		private SocksProxyData socksData;
		private String keyFileName;

		public SshConnectionData(JFrame parent, String host, int port,
				String user, String passwd) {
			super(parent, host, port, user, passwd,
					ConnectionPluginManager.J2SSH_SFTP_PLUGIN);
		}

		public void setSocksProxyData(SocksProxyData socksData) {
			this.socksData = socksData;
		}

		public SocksProxyData getSocksProxyData() {
			return socksData;
		}

		public void setKeyFileName(final String fileName) {
			this.keyFileName = fileName;
		}

		public boolean isSocksEnabled() {
			return (socksData != null);
		}

		public boolean isKeyFileEnabled() {
			return (keyFileName != null);
		}

		public String getKeyFileName() {
			return keyFileName;
		}

		// public Element save(Document document) throws SyncProjectIOException
		// {
		//
		// final Element conDataElem = super.save(document);
		//
		// if( keyFileName != null ) {
		// log.info("save keyFileName : " + keyFileName);
		// }
		//
		// if( socksData != null ) {
		// log.info("save socksData");
		// Element socksElem = socksData.save(document);
		// conDataElem.appendChild( socksElem );
		// }
		//
		// return conDataElem;
		// }

	}

	// ------------------------------------------------------------
	// KBIRequestHandler callback interface
	// ------------------------------------------------------------
	class TCapivaraKBIRequestHandler implements KBIRequestHandler {

		public void showPrompts(String name, String instructions,
				KBIPrompt[] prompts) {
			// blind confirmation ?
			boolean useDialog = true;

			try {
				useDialog = !MainWin.config.getProgramSettings()
						.getBooleanOption(
								TProgramSettings.OPTION_CONFIRM_PASSWORD);
			} catch (SettingsTypeException sex) {
				LOGGER.log(Level.WARNING, sex.getMessage());
			}

			TPasswordDialog passwdDiag = new TPasswordDialog(
					connectData.getParentComponent());

			String pwd = connectData.getPassword();
			// if blind confirmation but no password => show dialog
			if ((pwd == null) || (pwd.length() < 1)) {
				useDialog = true;
				pwd = "";
			}

			String response = pwd; // default dialog response is pwd

			passwdDiag.setDefaultPassword(pwd);

			if (prompts != null) {
				for (int i = 0; i < prompts.length; i++) {
					if (useDialog) // show Dialog ?
					{
						passwdDiag.setRemark(prompts[i].getPrompt()); // set
																		// message
						passwdDiag.setVisible(true);
						response = passwdDiag.getPassword();
						passwdDiag.dispose();
					}
					prompts[i].setResponse(response);
				}
			}
		}
	}

	public boolean requiresPort() {
		return true;
	}

	public int getDefaultPort() {
		return 22;
	}

	static class SSHToolsTransferProgress implements FileTransferProgress {

		private boolean cancelled;

		public synchronized void reset() {
			cancelled = false;
		}

		public synchronized void cancel() {
			cancelled = true;
		}

		public void completed() {
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public void progressed(long arg0) {
		}

		public void started(long arg0, String arg1) {
		}

	}

}
