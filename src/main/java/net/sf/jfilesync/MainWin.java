/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
 * changed by: Raik Nagel <raik.nagel@uni-bayreuth.de>
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
 * $Id: MainWin.java,v 1.141 2006/09/06 09:04:02 hunold Exp $
 */

package net.sf.jfilesync;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerListener;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.engine.CapivaraService;
import net.sf.jfilesync.engine.CapivaraSystemTracker;
import net.sf.jfilesync.engine.ConParams;
import net.sf.jfilesync.engine.ConnectionConfig;
import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.engine.CopySettings;
import net.sf.jfilesync.engine.FileTransfer2;
import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.engine.worker.DataCollectWorker;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.TAbstractGUIElementListener;
import net.sf.jfilesync.engine.worker.events.TFileListEvent;
import net.sf.jfilesync.event.QuickConnectListener;
import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventListener;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.event.types.ConnectRequestMessage;
import net.sf.jfilesync.event.types.ControlCenterSwitchMessage;
import net.sf.jfilesync.event.types.ControlChangeEvent;
import net.sf.jfilesync.event.types.QuickConnectMessage;
import net.sf.jfilesync.gui.AboutDialog;
import net.sf.jfilesync.gui.FileCopyProgressMonitor;
import net.sf.jfilesync.gui.InfoDialog;
import net.sf.jfilesync.gui.KeyStrokeConfigLoader;
import net.sf.jfilesync.gui.OptionsDialog;
import net.sf.jfilesync.gui.TConnectDialog;
import net.sf.jfilesync.gui.TControlCenter;
import net.sf.jfilesync.gui.TControlCenterFactory;
import net.sf.jfilesync.gui.TextViewFrame;
import net.sf.jfilesync.gui.UserHostsDialog;
import net.sf.jfilesync.gui.components.CapivaraStatsPanel;
import net.sf.jfilesync.gui.components.PrefixComboBox;
import net.sf.jfilesync.gui.components.PrefixComboBoxListener;
import net.sf.jfilesync.gui.components.PrefixItemEvent;
import net.sf.jfilesync.gui.components.TabbedBrowser;
import net.sf.jfilesync.gui.dialog.AbstractWorkerDialog;
import net.sf.jfilesync.gui.dialog.CapivaraCloseDialog;
import net.sf.jfilesync.gui.dialog.CollectFilesDialog;
import net.sf.jfilesync.gui.dialog.CopyConfirmDialog;
import net.sf.jfilesync.gui.dialog.SyncOptionDialog;
import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.gui.keymap.KeyStrokeModel;
import net.sf.jfilesync.gui.swing.ComponentVisibleRunner;
import net.sf.jfilesync.gui.utils.TStartup;
import net.sf.jfilesync.plugins.GeneralPlugin;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.BuildInfoPropertiesReader;
import net.sf.jfilesync.settings.CapivaraRuntimeConfiguration;
import net.sf.jfilesync.settings.ConfigDefinitions;
import net.sf.jfilesync.settings.ConfigFileHandler;
import net.sf.jfilesync.settings.ConfigReader;
import net.sf.jfilesync.settings.ConfigWriter;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TConfig;
import net.sf.jfilesync.settings.TProgramSettings;
import net.sf.jfilesync.settings.locale.LanguageDialog;
import net.sf.jfilesync.sync2.ConcurrentSyncTreeBuilder;
import net.sf.jfilesync.sync2.SyncComparator;
import net.sf.jfilesync.sync2.SyncMethod;
import net.sf.jfilesync.sync2.SyncSettingsStore;
import net.sf.jfilesync.sync2.SyncTree;
import net.sf.jfilesync.sync2.diffs.SyncDiffTree;
import net.sf.jfilesync.sync2.event.SyncDiffEvent;
import net.sf.jfilesync.sync2.event.SyncTreesMessage;
import net.sf.jfilesync.sync2.gui.SyncCompareFilesDialog;
import net.sf.jfilesync.sync2.gui.SyncTreeExplorerFrame;
import net.sf.jfilesync.sync2.syncer.Md5SyncMethod;
import net.sf.jfilesync.sync2.syncer.Sha1SyncMethod;
import net.sf.jfilesync.sync2.syncer.SizeSyncMethod;
import net.sf.jfilesync.sync2.syncer.SyncSettings;
import net.sf.jfilesync.sync2.syncer.TimeSyncMethod;
import net.sf.jfilesync.util.CmdLineOptions;
import net.sf.jfilesync.util.TMiscTool;
import net.sf.jfilesync.util.TWindowPositioner;
import net.sf.jfilesync.util.TextFileReader;

public class MainWin extends JFrame implements ActionListener,
		ComponentListener, TEventListener, GWorkerListener,
		TAbstractGUIElementListener, KeyListener, FocusListener,
		PrefixComboBoxListener {

	private static final long serialVersionUID = 1L;
	private final Dimension buttonDimension = new Dimension(34, 34);
	public static TConfig config = null;
	public static KeyStrokeModel keyMap = null;

	private TabbedBrowser leftBrowser = new TabbedBrowser(this);
	private TabbedBrowser rightBrowser = new TabbedBrowser(this);

	/* Jawinton */
	private ConnectionPlugin activePlugin;
	protected ConnectionConfig[] conConf;
	private volatile int supProtos[] = ConnectionPluginManager
			.getSupportedPlugins();
	private final HashMap<Integer, ConnectionPlugin> pluginHash = new HashMap<Integer, ConnectionPlugin>();
	private JToolBar toolBar;
	private JLabel hostLabel, portLabel, userLabel;
	private JTextField userField;
	private PrefixComboBox hostCombo;
	private JTextField portTextField = new JTextField(4);
	private JButton loginButton;
	private Font boldFont = new java.awt.Font("Dialog", Font.BOLD, 12);
	private Font normFont = new java.awt.Font("Dialog", Font.PLAIN, 12);
	private TConnectionData conData;
	/* Jawinton */

	// GUI which has to be accessed in this class
	private JSplitPane mainPane;
	private SyncMethod[] methods = { new TimeSyncMethod(),
			new Sha1SyncMethod(), new Md5SyncMethod(), new SizeSyncMethod() };
	private SyncMethod chosenMethod; // currently chosen sync method

	// worker variables
	private GWorkerObserver observer = new GWorkerObserver();
	private GWorker activeWorker;
	private AbstractWorkerDialog abstractGuiElement;

	// storing trees temporarily
	private SyncTree tree1, tree2;

	private final static Logger LOGGER = Logger.getLogger(MainWin.class
			.getName());

	private CopySettings copyStruct;
	private static CmdLineOptions opts;
	private boolean guru = false;

	// services
	private final List<CapivaraService> capiServices = new ArrayList<CapivaraService>();

	public MainWin() {
		loadSettings();
		registerListeners();
		initUI();
		startServices();

		// move focus to leftBrowser
		TControlCenterFactory.getControlCenterBus().fireControlCenterBusEvent(
				new ControlChangeEvent(this, 0));
	}

	private void initUI() {

		toolBar = new JToolBar();
		BorderLayout borderLayout1 = new BorderLayout();
		mainPane = new JSplitPane(); // the main pain ;-)
		JPanel mainContentPane = new JPanel(borderLayout1);

		JMenuBar jMenuBar1 = new JMenuBar();
		setupMenuBar(jMenuBar1);

		// --------- Main Panel -----------
		mainPane.setPreferredSize(new Dimension(1000, 600));
		mainPane.setOneTouchExpandable(true);
		mainPane.setDividerLocation(mainPane.getPreferredSize().width / 2);

		mainPane.setLeftComponent(leftBrowser);
		mainPane.setRightComponent(rightBrowser);

		setLocale(java.util.Locale.getDefault());
		setJMenuBar(jMenuBar1);
		setContentPane(mainContentPane);

		mainContentPane.setLayout(borderLayout1);
		mainContentPane.setBorder(BorderFactory.createEtchedBorder());

		mainContentPane.add(toolBar, BorderLayout.NORTH);
		mainContentPane.add(mainPane, BorderLayout.CENTER);
		mainContentPane.add(new CapivaraStatsPanel(), BorderLayout.SOUTH);

		// JButton copyButton = themeFactory
		// .createButton("button_copy",
		// LanguageBundle.getInstance().getMessage("label.copy"));
		/* Jawinton */
		this.hostLabel = new JLabel(LanguageBundle.getInstance().getMessage(
				"label.host")
				+ " ");
		this.hostLabel.setFont(normFont);
		conConf = config.getSavedConnections();
		String[] hostData = new String[conConf.length];
		for (int i = 0; i < conConf.length; i++) {
			hostData[i] = conConf[i].getHostName();
		}
		this.hostCombo = new PrefixComboBox(hostData);
		this.hostCombo.setMinimumSize(new Dimension(200, 20));
		this.hostCombo.setMaximumSize(new Dimension(200, 20));
		this.hostCombo.setPreferredSize(new Dimension(200, 20));
		this.hostCombo.setSize(new Dimension(200, 20));
		this.hostCombo.setPrefixComboBoxListener(this);
		this.hostCombo.addCustomFocusListener(this);

		this.userLabel = new JLabel(LanguageBundle.getInstance().getMessage(
				"label.user")
				+ " ");
		this.userLabel.setFont(normFont);
		this.userField = new JTextField(30);
		this.userField.setMinimumSize(new Dimension(200, 20));
		this.userField.setMaximumSize(new Dimension(200, 20));
		this.userField.setPreferredSize(new Dimension(200, 20));
		this.userField.setSize(new Dimension(200, 20));
		this.userField.addKeyListener(this);
		this.userField.addFocusListener(this);
		this.portLabel = new JLabel(LanguageBundle.getInstance().getMessage(
				"label.port")
				+ " ");
		this.portLabel.setFont(normFont);
		this.portTextField.setMinimumSize(new Dimension(100, 20));
		this.portTextField.setMaximumSize(new Dimension(100, 20));
		this.portTextField.setPreferredSize(new Dimension(100, 20));
		this.portTextField.setSize(new Dimension(100, 20));
		this.portTextField.addFocusListener(this);

		ConnectionPlugin[] plugins = new ConnectionPlugin[supProtos.length];

		// fill protoCombo
		for (int i = 0; i < supProtos.length; i++) {
			ConnectionPlugin pluginToAdd = ConnectionPluginManager
					.getConnectionModelInstance(supProtos[i]);

			plugins[i] = ConnectionPluginManager
					.getConnectionModelInstance(supProtos[i]);

			pluginHash.put(new Integer(supProtos[i]), plugins[i]);

			if (supProtos[i] == ConnectionPluginManager.HDFS_PLUGIN) { // Jawinton
				activePlugin = plugins[i];
			}
		}

		// JButton loginButton = new
		// JButton(LanguageBundle.getInstance().getMessage("label.login"), new
		// ImageIcon(getClass()
		// .getResource("/net/sf/jfilesync/gui/themes/default/disconnected.png")));
		// loginButton.setActionCommand("login_command");
		// loginButton.addActionListener(this);
		// loginButton.setToolTipText(LanguageBundle.getInstance().getMessage("label.login"));
		// loginButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		// loginButton.setHorizontalTextPosition(SwingConstants.CENTER);
		// loginButton.setFocusable(false);
		this.loginButton = new JButton(LanguageBundle.getInstance()
				.getMessage("label.login"), new ImageIcon(getClass()
				.getResource("/net/sf/jfilesync/gui/themes/default/disconnected2.png")));

		loginButton.addActionListener(this);
		loginButton.setActionCommand("login_command");
		loginButton.setBorderPainted(true);
		loginButton.setFocusable(false);
		loginButton.setToolTipText(LanguageBundle.getInstance().getMessage(
				"label.login"));
		loginButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		loginButton.setHorizontalTextPosition(SwingConstants.CENTER);
		/* Jawinton */

		// JButton syncButton = themeFactory
		// .createButton("button_sync",
		// LanguageBundle.getInstance().getMessage("label.sync"));
		JButton syncButton = new JButton(LanguageBundle.getInstance()
				.getMessage("label.sync"), new ImageIcon(getClass()
				.getResource(
						"/net/sf/jfilesync/gui/themes/default/view_right.png")));
		syncButton.addActionListener(this);
		syncButton.setToolTipText(LanguageBundle.getInstance().getMessage(
				"label.sync"));
		syncButton.setActionCommand("sync");
		syncButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		syncButton.setHorizontalTextPosition(SwingConstants.CENTER);
		syncButton.setFocusable(false);

		/* Jawinton */
		JButton chooseHostButton = new JButton(LanguageBundle.getInstance()
	            .getMessage("label.favourites"), new ImageIcon(getClass()
	    				.getResource(
	    						"/net/sf/jfilesync/gui/themes/default/stock-star2.png")));
		chooseHostButton.setActionCommand("choose");
        chooseHostButton.addActionListener(this);
        chooseHostButton.setToolTipText(LanguageBundle.getInstance().getMessage(
				"label.favourites"));
        chooseHostButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        chooseHostButton.setHorizontalTextPosition(SwingConstants.CENTER);
		
		toolBar.add(hostLabel);
		toolBar.add(hostCombo);
		toolBar.add(portLabel);
		toolBar.add(portTextField);
		toolBar.add(userLabel);
		toolBar.add(userField);
		toolBar.add(loginButton);
		toolBar.addSeparator();
		toolBar.add(syncButton);
		toolBar.add(chooseHostButton);
		/* Jawinton */

		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// !TODO! fire WindowClosingEvent to children
				closingEvent();
			}
		});

		addComponentListener(this);

		final String title = ConfigDefinitions.PROGRAM_NAME + "@"
				+ TMiscTool.getLocalHostName() + " "
				+ ConfigDefinitions.PROGRAM_VERSION;

		setTitle(title);

		setIconImage(TImageIconProvider.getInstance().getImage(
				TImageIconProvider.FRAME_ICON));

		/*
		 * - a way to avoid redundant use of ActionListeners and KeyListeners on
		 * JButton - without this change of the InputMap hitting Enter will not
		 * fire an ActionEvent even though the JButton has the focus
		 */

		UIManager.put("Button.focusInputMap",
				new UIDefaults.LazyInputMap(new Object[] { "ENTER", "pressed",
						"released ENTER", "released" }));

		pack();
		mainPane.setDividerLocation(0.5);
		setLocation(TWindowPositioner.getCenteredWindowPoint(this));
	}

	private void setupMenuBar(JMenuBar menubar) {

		final JMenu toolMenu = new JMenu();
		final JMenu helpMenu = new JMenu();
		final JMenuItem optionItem = new JMenuItem();

		// file menu
		final JMenu fileMenu = new JMenu();
		fileMenu.setText(LanguageBundle.getInstance().getMessage("menu.file"));
		JMenuItem jMenuItem1 = new JMenuItem();
		jMenuItem1
				.setText(LanguageBundle.getInstance().getMessage("menu.exit"));
		jMenuItem1.setActionCommand("exit");
		jMenuItem1.addActionListener(this);
		fileMenu.add(jMenuItem1);

		// tool menu
		// Jawinton
//		toolMenu.setText(LanguageBundle.getInstance().getMessage("menu.tools"));
//		optionItem.setText(LanguageBundle.getInstance().getMessage(
//				"menu.options"));
//		optionItem.setActionCommand("options");
//		optionItem.addActionListener(this);
//		toolMenu.add(optionItem);

		// help menu
		JMenuItem aboutItem = new JMenuItem();
		aboutItem
				.setText(LanguageBundle.getInstance().getMessage("menu.about"));
		aboutItem.setActionCommand("aboutdiag");
		aboutItem.addActionListener(this);
		JMenuItem changelogItem = new JMenuItem();
		changelogItem.setText(LanguageBundle.getInstance().getMessage(
				"menu.changelog"));
		changelogItem.setActionCommand("changelog");
		changelogItem.addActionListener(this);

		helpMenu.setText(LanguageBundle.getInstance().getMessage("menu.help"));
		helpMenu.add(aboutItem);
		helpMenu.add(changelogItem);

		menubar.add(fileMenu);
		menubar.add(toolMenu);
		menubar.add(helpMenu);
	}

	// --------------------------------------------------------------------------

	private void registerListeners() {
		TEventMulticaster emc = TEventMulticaster.getInstance();
		emc.addTEventListener(this, TMessage.ID.SETTINGS_CHANGED_MESSAGE);
		emc.addTEventListener(this, TMessage.ID.COPY_FILES_MESSAGE);
		emc.addTEventListener(this, TMessage.ID.CONTROLCENTER_CHANGE_MESSAGE);
		emc.addTEventListener(this, TMessage.ID.QUICK_CONNECT_MESSAGE);
		emc.addTEventListener(this, TMessage.ID.SAVE_CONFIG_MESSAGE);
		observer.addWorkerListener(this);
	}

	// --------------------------------------------------------------------------

	protected void loadSettings() {
		try {
			final String userLang = config.getProgramSettings()
					.getStringOption(TProgramSettings.OPTION_USER_LANG);

			LanguageBundle.setLanguage(userLang);

			guru = config.getProgramSettings().getBooleanOption(
					TProgramSettings.OPTION_GURU_MODE);
		} catch (SettingsTypeException ste) {
			LOGGER.severe(ste.getMessage());
		}
	}

	// --------------------------------------------------------------------------

	public void componentResized(ComponentEvent e) {
		adjustDivider();
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	/*
	 * --------------------------------------------------------------------------
	 */
	public void actionPerformed(ActionEvent e) {
		// Object obj = e.getSource();
		String command = e.getActionCommand();

		if (command.equals("exit")) {
			closingEvent();
		} else if (command.equals("options")) {
			options_actionPerformed(e);
		} else if (command.equals("aboutdiag")) {
			new AboutDialog(this);
		} else if (command.equals("changelog")) {
			showChangeLog();
		} else if (command.equals("sync")) {
			try {
				sync();
			} catch (IOException ioex) {
				ioex.printStackTrace();
			}
		} else if (command.equals("copy_command")) {
			copy_event();
		} else if (command.equals("select_all")) {

		} else if (command.equals("login_command")) {
			/* Jawinton */
			TControlCenter rc = rightBrowser.getFocusedControlCenter();
			if (!rc.isConnected()) {
				if (this.setupConnectionData()) rc.connect(conData);
			} else {
				rightBrowser.addControlCenter(TControlCenterFactory.getInstance(this).getNextControlCenter());
			    rightBrowser.setSelectedIndex(rightBrowser.getSelectedIndex()+1);
			    rc = rightBrowser.getFocusedControlCenter();
			    if (!rc.isConnected()) {
					if (this.setupConnectionData()) rc.connect(conData);
			    }
//				rc.disconnectAction();
			}
		} else if (command.equals("choose")){
			ChooseHostButton_actionPerformed(e);
			/* Jawinton */
		} else {
			LOGGER.warning("bug: action " + e.getActionCommand() + " unknown");
		}
	}

	private void options_actionPerformed(ActionEvent e) {
		OptionsDialog dialog = new OptionsDialog(this);
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	private void showChangeLog() {
		try {
			final String changesText = TextFileReader
					.readFileContentsFromResource("/net/sf/jfilesync/Changes.txt");
			new TextViewFrame(changesText, LanguageBundle.getInstance()
					.getMessage("menu.changelog")).setVisible(true);
		} catch (IOException e) {
			LOGGER.warning(e.getMessage());
		}
	}

	private void adjustDivider() {
		// does not work with WindowStateEvent because it is fired before
		// the window is resized
		mainPane.setDividerLocation(0.5);
	}

	private void cleanup() {
		// TODO complete
		// disconnect all current connections
		// save user settings

		// writing config on normal exit
		saveConfiguration();
		stopServices();
	}

	private void closingEvent() {
		// debug output - if service thread hangs, the program
		// will never exit....

		boolean showPrompt = true;
		try {
			showPrompt = !config.getProgramSettings().getBooleanOption(
					TProgramSettings.OPTION_DO_NOT_PROMPT_FOR_EXIT);
		} catch (SettingsTypeException ste) {
			LOGGER.warning(ste.getMessage());
		}

		if (showPrompt) {
			CapivaraCloseDialog ccd = new CapivaraCloseDialog(this);
			ccd.setVisible(true);
			int option = ccd.getUserOption();
			if (option == CapivaraCloseDialog.OK_OPTION) {
				if (ccd.isNeverPromptSelected()) {
					try {
						config.getProgramSettings().setProgramOption(
								TProgramSettings.OPTION_DO_NOT_PROMPT_FOR_EXIT,
								new Boolean(true).toString());
					} catch (SettingsTypeException ste) {
						LOGGER.warning(ste.getMessage());
					}
				}
				doClose();
			}
		} else {
			doClose();
		}

	}

	private void doClose() {
		setVisible(false);
		TEventMulticaster.getInstance().shutdown();
		cleanup();
		dispose();
		System.exit(0);
	}

	// ---------------------------------------------------------------------------

	private void sync() throws IOException {

		TControlCenter lc = leftBrowser.getFocusedControlCenter();
		TControlCenter rc = rightBrowser.getFocusedControlCenter();

		AbstractConnectionProxy con1 = lc.getConnection();
		AbstractConnectionProxy con2 = rc.getConnection();

		if (con1 == null || con2 == null || !con1.isConnected()
				|| !con2.isConnected()) {
			TErrorHandling
					.reportError(TErrorHandling.ERROR_CONNECTION_NOT_ACTIVE);
			return;
		}

		final List<SyncMethod> syncMethodsAvailable = new ArrayList<SyncMethod>();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i] != null
					&& methods[i].isApplicable(con1.getPlugin(),
							con2.getPlugin())) {
				syncMethodsAvailable.add(methods[i]);
			}
		}
		if (syncMethodsAvailable.size() > 0) {

			SyncMethod[] methodsToUse = (SyncMethod[]) syncMethodsAvailable
					.toArray(new SyncMethod[syncMethodsAvailable.size()]);

			boolean[] methodsEnabled = new boolean[methodsToUse.length];
			for (int i = 0; i < methodsEnabled.length; i++) {
				methodsEnabled[i] = true;
			}
			if (!inExpertMode()
					&& (!con1.isLocalConnection() || !con2.isLocalConnection())) {
				for (int i = 0; i < methodsToUse.length; i++) {
					if (methodsToUse[i].getMethodID() != SyncMethod.METHOD_TIME) {
						methodsEnabled[i] = false;
					}
				}
			}

			SyncOptionDialog sod = new SyncOptionDialog(this,
					lc.getConnectionDetails(), rc.getConnectionDetails(),
					methodsToUse, methodsEnabled);
			sod.setVisible(true);

			int opt = sod.getUserChoice();
			if (opt != SyncOptionDialog.OPTION_OK) {
				return;
			}

			final SyncSettings syncSettings = sod.getSyncSettings();
			chosenMethod = sod.getChosenMethod();
			chosenMethod.applySyncSyncSettings(syncSettings);
			SyncSettingsStore.getInstance().addSyncSettings(syncSettings);

			LOGGER.info("collecting sync data");
			ConcurrentSyncTreeBuilder myWorker = new ConcurrentSyncTreeBuilder(
					this, syncSettings.getSyncID(), con1, lc.getCurrentPath(),
					lc.getConnectionDetails(), con2, rc.getCurrentPath(),
					rc.getConnectionDetails());

			activeWorker = myWorker;
			activeWorker.addObserver(observer);
			observer.execute(activeWorker);
		} else {
			LOGGER.warning("no sync method applicable");
		}

	}

	private boolean inExpertMode() {
		boolean ret = false;
		try {
			ret = config.getProgramSettings().getBooleanOption(
					TProgramSettings.OPTION_EXPERT_MODE);
		} catch (SettingsTypeException ste) {
			LOGGER.warning(ste.getMessage());
		}
		return ret;
	}

	// ---------------------------------------------------------------------------

	private void copy_event() {

		copyStruct = new CopySettings();

		TControlCenter lc = leftBrowser.getFocusedControlCenter();
		TControlCenter rc = rightBrowser.getFocusedControlCenter();

		if (lc.isActive()) {
			copyStruct.setSource(lc);
			copyStruct.setTarget(rc);
		} else {
			copyStruct.setSource(rc);
			copyStruct.setTarget(lc);
		}
		copyData(copyStruct);
	}

	// ---------------------------------------------------------------------------

	private void copyData(final CopySettings cs) {

		LOGGER.fine("copy data");

		final AbstractConnectionProxy connection1 = cs.getSource()
				.getConnection();
		final AbstractConnectionProxy connection2 = cs.getTarget()
				.getConnection();

		if (connection1 == null || connection2 == null
				|| !connection1.isConnected() || !connection2.isConnected()) {
			TErrorHandling
					.reportError(TErrorHandling.ERROR_CONNECTION_NOT_ACTIVE);
			return;
		} else {
			LOGGER.fine("all connections active");
		}

		final ConnectionDetails cd1 = cs.getSource().getConnectionDetails();
		final ConnectionDetails cd2 = cs.getTarget().getConnectionDetails();

		TFileProperties[] data = cs.getSource().getSelectedFileData();

		CopyConfirmDialog ccd = null;
		if (data.length <= 0) {
			return;
		} else if (data.length == 1) {
			ccd = new CopyConfirmDialog(this, cd1, cd2, data[0].getFileName(),
					data[0].isDirectory());
		} else {
			boolean onlyDirs = true;
			boolean onlyFiles = true;
			for (int i = 0; i < data.length; i++) {
				if (data[i].isDirectory()) {
					onlyFiles = false;
				} else {
					onlyDirs = false;
				}
			}
			int type = CopyConfirmDialog.TYPE_FILES;
			if (onlyDirs) {
				type = CopyConfirmDialog.TYPE_DIRECTORIES;
			} else if (onlyFiles) {
				type = CopyConfirmDialog.TYPE_FILES;
			} else {
				type = CopyConfirmDialog.TYPE_FILES_AND_DIRECTORIES;
			}
			ccd = new CopyConfirmDialog(this, cd1, cd2, data.length, type);
		}

		if (ccd != null) {
			// target and source support permissions
			if (isPermissionHandlingSupported(copyStruct)) {
				ccd.setEnablePreservePermissions(true);
			}
			// target supports permissions
			if (copyStruct.getTarget().getConnection().getPlugin()
					.isProvided(GeneralPlugin.PROVIDES_PERMISSION_HANDLING)) {
				ccd.setEnableTargetPermissions(true);
			}
		}

		boolean preserve_mtime = false;
		boolean preserve_perm = false;
		boolean follow_symlinks = false;

		try {
			preserve_mtime = MainWin.config.getProgramSettings()
					.getBooleanOption(TProgramSettings.OPTION_PRESERVE_MTIME);
			follow_symlinks = MainWin.config.getProgramSettings()
					.getBooleanOption(
							TProgramSettings.OPTION_FOLLOW_SYMLINKS_COPY);

			if (isPermissionHandlingSupported(copyStruct)) {
				preserve_perm = MainWin.config
						.getProgramSettings()
						.getBooleanOption(TProgramSettings.OPTION_PRESERVE_PERM);
			} else {
				preserve_perm = false;
			}
		} catch (SettingsTypeException ste) {
			LOGGER.warning(ste.getMessage());
		}

		if (!guru) {

			ccd.setPreserveMtime(preserve_mtime);
			ccd.setFollowSymLinks(follow_symlinks);

			if (isPermissionHandlingSupported(copyStruct)) {
				ccd.setPreservePermissions(preserve_perm);
			} else {
				ccd.setPreservePermissions(false);
			}

			ccd.setVisible(true);

			int userChoice = ccd.getUserChoice();
			if (userChoice == CopyConfirmDialog.OPTION_CANCEL) {
				return;
			} else {

				copyStruct.setPreserveMtime(ccd.getPreserveModificationTime());
				follow_symlinks = ccd.getFollowSymcLinks();

				if (ccd.getPreservePermissions()) {
					copyStruct.setPreservePerm(true);
				} else if (ccd.getUseCustomTargetPermissions()) {
					copyStruct.setDirPerm(ccd.getDirectoryPermissions());
					copyStruct.setFilePerm(ccd.getFilePermissions());
				}

			}
		} else {

			// GURU mode
			copyStruct.setPreserveMtime(preserve_mtime);
			copyStruct.setPreservePerm(preserve_perm);

			try {
				int dir_perm = MainWin.config.getProgramSettings()
						.getIntegerOption(
								TProgramSettings.OPTION_PERMISSIONS_DIR);
				int file_perm = MainWin.config.getProgramSettings()
						.getIntegerOption(
								TProgramSettings.OPTION_PERMISSIONS_FILE);
				copyStruct.setDirPerm(dir_perm);
				copyStruct.setFilePerm(file_perm);
			} catch (SettingsTypeException ste) {
				LOGGER.warning(ste.getMessage());
			}

		}

		abstractGuiElement = new CollectFilesDialog(this, cs.getSource()
				.getConnectionDetails());
		abstractGuiElement.addTAbstractGUIElementListener(this);

		activeWorker = new DataCollectWorker(connection1, data, true,
				follow_symlinks);
		activeWorker.addObserver(observer);

		abstractGuiElement.enableGUIElement(true);
		observer.execute(activeWorker);

	}

	private boolean isPermissionHandlingSupported(
			final CopySettings copySettings) {
		boolean ret = false;

		if (copySettings.getSource().getConnection().getPlugin()
				.isProvided(GeneralPlugin.PROVIDES_PERMISSION_HANDLING)
				&& copySettings.getTarget().getConnection().getPlugin()
						.isProvided(GeneralPlugin.PROVIDES_PERMISSION_HANDLING)) {
			ret = true;
		}
		return ret;
	}

	// ----------------------------------------------------------------------------

	private static void loadConfiguration() {

		if (opts.hasConfigDir()) {
			CapivaraRuntimeConfiguration.getInstance().setUserConfigDir(
					opts.getConfigDir());
		}

		final ConfigFileHandler configHandler = ConfigFileHandler.getInstance();

		if (configHandler.configDirExists()) {

			if (configHandler.configFileExists()) {
				ConfigReader reader = new ConfigReader();
				try {
					reader.readConfig(configHandler.getConfigFileLocation());
					config = reader.getConfig();
				} catch (IOException e) {
					int opt = JOptionPane
							.showConfirmDialog(
									null,
									"Error while reading configuration file.\nCreate new configuration?",
									"Configuration read error",
									JOptionPane.ERROR_MESSAGE,
									JOptionPane.YES_NO_OPTION);

					if (opt == JOptionPane.YES_OPTION) {
						config = new TConfig();
					} else {
						System.exit(-1);
					}
				}
			} else {
				config = new TConfig();
			}

		} else {

			boolean createNewConfig = false;

			if (!opts.hasConfigDir()) {
				createNewConfig = true;
			} else {

				final String userDir = CapivaraRuntimeConfiguration
						.getInstance().getUserConfigDir();

				final File userDirFile = new File(userDir);
				if (!userDirFile.exists()) {
					TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL,
							userDir + " does not exists");
					System.exit(-1);
				}

				String question = "Create new configuration directory in %s?";
				question = question.replace("%s", CapivaraRuntimeConfiguration
						.getInstance().getUserConfigDir());

				final int opt = JOptionPane.showConfirmDialog(null, question,
						"Confirmation", JOptionPane.ERROR_MESSAGE,
						JOptionPane.YES_NO_OPTION);

				if (opt == JOptionPane.YES_OPTION) {
					createNewConfig = true;
				}
			}

			if (createNewConfig) {
				try {
					configHandler.createConfigDir();
					config = new TConfig();
				} catch (IOException e) {
					TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL,
							e.getMessage());
					System.exit(-1);
				}
			} else {
				TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL,
						"No configuration! Exiting...");
				System.exit(-1);
			}

		}

		if (config == null) {
			TErrorHandling
					.reportError(TErrorHandling.ERROR_IO_GENERAL,
							"[BUG] no configuration loaded. Send email to author! Exiting...");
		}

	}

	// ----------------------------------------------------------------------------

	private static void saveConfiguration() {
		ConfigWriter writer = new ConfigWriter();
		if (config != null) {
			writer.writeConfig(config, ConfigFileHandler.getInstance()
					.getConfigFileLocation());
		}
	}

	// ----------------------------------------------------------------------------

	// initialized all objects with saved or default properties
	private static void setupConfiguration() {
		String lang = null;
		try {
			lang = config.getProgramSettings().getStringOption(
					TProgramSettings.OPTION_USER_LANG);
		} catch (SettingsTypeException ste) {
			LOGGER.log(Level.WARNING, ste.getMessage());
		}

		// okay a valid Language ID was loaded
		if (lang.equals("undefined")) {
			setupLanguage();
		} else {
			LanguageBundle.setLanguage(lang);
		}

		LOGGER.info("set language : " + LanguageBundle.getLanguage());
	}

	private static void loadKeyBindings() {
		LOGGER.info("read keymap");
		try {
			final InputStream instream = MainWin.class
					.getResourceAsStream("/net/sf/jfilesync/gui/keymap/default_keymap.props");
			final InputStream instream2 = MainWin.class
					.getResourceAsStream("/net/sf/jfilesync/gui/keymap/shortcut2localekey.properties");

			KeyStrokeConfigLoader loader = new KeyStrokeConfigLoader(instream,
					instream2);

			loader.load();
			keyMap = loader.getKeyStrokeModel();

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,
					"Error while loading keymap file!\n" + e.getMessage());
			JOptionPane.showMessageDialog(null,
					"Keybindings not loaded. exiting");
			System.exit(-1);
		}
	}

	// ----------------------------------------------------------------------------

	public void processEvent(TEvent e) {
		switch (e.getMessage().getMessageType()) {
		case SETTINGS_CHANGED_MESSAGE:
			loadSettings();
			break;
		case COPY_FILES_MESSAGE:
			copy_event();
			break;
		case CONTROLCENTER_CHANGE_MESSAGE:
			// change msg sent by some table
			// find out in which browser this table is located
			// and active the top tab in the other browser

			ControlCenterSwitchMessage ccsm = (ControlCenterSwitchMessage) e
					.getMessage();
			int lastActiveCC = ccsm.getOrigin();
			int idToActivate = -1;

			if (leftBrowser.containsControlCenterById(lastActiveCC)) {
				// get top layered pane of rightBrowser
				// and active this ControlCenter
				idToActivate = rightBrowser.getFocusedControlCenter().getID();
			} else {
				idToActivate = leftBrowser.getFocusedControlCenter().getID();
			}

			LOGGER.info("origin : " + lastActiveCC);
			LOGGER.info("active : " + idToActivate);

			if (idToActivate > -1) {
				TControlCenterFactory.getControlCenterBus()
						.fireControlCenterBusEvent(
								new ControlChangeEvent(this, idToActivate));
			} else {
				LOGGER.log(Level.SEVERE, "ControlCenter could not be activated");
			}
			break; // end TMessageType.CONTROLCENTER_CHANGE_MESSAGE
		case QUICK_CONNECT_MESSAGE:
			handleQuickConnectMessage((QuickConnectMessage) e.getMessage());
			break;
		case SAVE_CONFIG_MESSAGE:
			LOGGER.fine("saving current configuration...");
			saveConfiguration();
			break;
		default:
			LOGGER.warning("Unhandled message type: "
					+ e.getMessage().getMessageType());
			break;
		}

	}

	// ----------------------------------------------------------------------------

	public void workerDone(GWorkerEvent e) {
		// if( e.getSource() instanceof GetSyncTreesWorker ) {
		if (e.getSource() instanceof ConcurrentSyncTreeBuilder) {
			SyncTreesMessage msg = (SyncTreesMessage) ((GWorker) e.getSource())
					.construct();

			tree1 = msg.getTree1();
			tree2 = msg.getTree2();

			abstractGuiElement = new SyncCompareFilesDialog(this);
			abstractGuiElement.addTAbstractGUIElementListener(this);

			int syncID = ((ConcurrentSyncTreeBuilder) e.getSource())
					.getSyncID();

			activeWorker = new SyncComparator(syncID, leftBrowser
					.getFocusedControlCenter().getConnection(), tree1,
					rightBrowser.getFocusedControlCenter().getConnection(),
					tree2, chosenMethod);
			activeWorker.addObserver(observer);

			LOGGER.info("start comparing..");
			observer.execute(activeWorker);
			abstractGuiElement.enableGUIElement(true);
		} else if (e.getSource() instanceof SyncComparator) {
			abstractGuiElement.enableGUIElement(false);
			SyncDiffEvent sde = (SyncDiffEvent) ((GWorker) e.getSource())
					.construct();

			SyncDiffTree diffTree1 = sde.getLeftSyncDiffTree();
			SyncDiffTree diffTree2 = sde.getRightSyncDiffTree();

			assert (diffTree1 != null && diffTree2 != null);

			TControlCenter lc = leftBrowser.getFocusedControlCenter();
			TControlCenter rc = rightBrowser.getFocusedControlCenter();

			SyncTreeExplorerFrame syncFrame = new SyncTreeExplorerFrame(this,
					((SyncComparator) e.getSource()).getSyncID(), tree1,
					diffTree1, tree2, diffTree2, lc.getConnection(),
					lc.getConnectionDetails(), rc.getConnection(),
					rc.getConnectionDetails(), chosenMethod.getSyncOptions());

			syncFrame.setVisible(true);
			// if( syncFrame.hasSyncBeenExecuted() ) {
			lc.refreshDataView();
			rc.refreshDataView();
			// }

			SyncComparator comparator = (SyncComparator) e.getSource();
			SyncSettingsStore.getInstance().removeSyncSettings(
					comparator.getSyncID());
		} else if (e.getSource() instanceof DataCollectWorker) {

			abstractGuiElement.enableGUIElement(false);

			TFileData collection1;
			if (activeWorker != null
					&& activeWorker.getWorkerState() == GWorker.STATE_DONE) {
				collection1 = ((TFileListEvent) activeWorker.construct())
						.getFileData();
			} else {
				return;
			}

			if (collection1 == null) {
				return;
			}

			FileTransfer2 transfer = new FileTransfer2(this, copyStruct
					.getSource().getConnection(), copyStruct.getSource()
					.getCurrentPath(), copyStruct.getTarget().getConnection(),
					copyStruct.getTarget().getCurrentPath(),
					collection1.getFilePropertiesArray());

			if (copyStruct.getPreserveMtime()) {
				transfer.addOption(FileTransfer2.OPTION_PRESERVE_MTIME);
			}

			if (copyStruct.getPreservePerm()) {
				transfer.addOption(FileTransfer2.OPTION_PRESERVE_PERMISSIONS);
			} else {
				int dir_perm = copyStruct.getDirPerm();
				int file_perm = copyStruct.getFilePerm();
				if (dir_perm != -1 && file_perm != -1) {
					transfer.setTargetPermissions(dir_perm, file_perm);
				}
			}

			FileCopyProgressMonitor monitor = new FileCopyProgressMonitor(this);

			transfer.setMonitor(monitor);
			// since 0.6.3 is startCopying blocking
			transfer.startCopying();

			// ---------------------------------------------------------------------

			boolean targetStateOK = true;

			final class ControlCenterClearer implements Runnable {
				private final TControlCenter cc;

				public ControlCenterClearer(TControlCenter cc) {
					this.cc = cc;
				}

				public void run() {
					cc.clearCenter();
				}
			}

			if (transfer.wasCancelled()) {
				LOGGER.info("transfer cancelled");
				if (!copyStruct.getSource().getConnection().isConnected()) {
					TErrorHandling.reportError(
							TErrorHandling.ERROR_CONNECTION_LOST, copyStruct
									.getSource().getConnectionDetails()
									.toString());
					// copyStruct.getSource().clearCenter();
					SwingUtilities.invokeLater(new ControlCenterClearer(
							copyStruct.getSource()));
				}
				if (!copyStruct.getTarget().getConnection().isConnected()) {
					targetStateOK = false;
					TErrorHandling.reportError(
							TErrorHandling.ERROR_CONNECTION_LOST, copyStruct
									.getTarget().getConnectionDetails()
									.toString());
					// copyStruct.getTarget().clearCenter();
					SwingUtilities.invokeLater(new ControlCenterClearer(
							copyStruct.getTarget()));
				}
			}

			if (targetStateOK) {
				copyStruct.getTarget().refreshDataView();
			}

		} // end of workerDone for DataCollectWorker
		else {
			TErrorHandling
					.reportError(TErrorHandling.ERROR_UNKNOWN_WORKER_ERROR);
		}

	}

	public void workerCancelled(GWorkerEvent e) {
		LOGGER.warning(e.getSource().getClass() + " cancelled");

		if (e.getSource() instanceof SyncComparator
				|| e.getSource() instanceof ConcurrentSyncTreeBuilder) {
			if (abstractGuiElement != null) {
				abstractGuiElement.enableGUIElement(false);
			}
			leftBrowser.getFocusedControlCenter().refreshDataView();
			rightBrowser.getFocusedControlCenter().refreshDataView();
		} else if (e.getSource() instanceof DataCollectWorker) {
			abstractGuiElement.enableGUIElement(false);
		} else {
			LOGGER.warning("cancel event unhandled! not too good!");
		}
	}

	public void workerDied(GWorkerEvent e) {

		Exception ex = ((GWorker) e.getSource()).getException();
		LOGGER.severe(ex.getMessage());
		LOGGER.info("worker died: " + e.getSource().getClass().toString());

		ex.printStackTrace();

		TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL,
				ex.getMessage());

		// if( reportErrors ) {
		// reportErrors = TErrorHandling.reportErrorSubsequently(
		// TErrorHandling.ERROR_IO_GENERAL, ex.getMessage());
		// }

		if (e.getSource() instanceof SyncComparator) {
			abstractGuiElement.enableGUIElement(false);
		} else if (e.getSource() instanceof DataCollectWorker) {
			abstractGuiElement.enableGUIElement(false);
		}
	}

	public void updateModel(GWorkerEvent e) {
		if (abstractGuiElement != null) {
			abstractGuiElement.displayWorkerData(e);
		}
	}

	public void cancelClicked(TAbstractDialogEvent e) {
		if (activeWorker != null) {
			observer.cancel(activeWorker);
		}
	}

	// ----------------------------------------------------------------------------

	public static void printJvmConfig() {
		final String[] properties = { "java.version", "java.vm.version",
				"java.vm.name", "file.separator", "os.name", "os.arch",
				"os.version", "user.dir", "user.home", "user.name",
				"file.encoding" };

		for (int i = 0; i < properties.length; i++) {
			LOGGER.info(properties[i] + " : "
					+ System.getProperty(properties[i]));
		}

		final File roots[] = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			LOGGER.info("root " + roots[i].getAbsolutePath());
		}
	}

	// --------------------------------------------------------------------------

	private void initConnections() {
		if (opts.hasLeftTarget()) {
			TControlCenter cc = leftBrowser.getFirstControlCenter();
			int port = ConnectionPluginManager.getConnectionModelInstance(
					opts.getLeftProtocol()).getDefaultPort();
			TConnectionData data = new TConnectionData(this,
					opts.getLeftHost(), port, opts.getLeftUser(), "",
					opts.getLeftProtocol());
			cc.setInitialDirectory(opts.getLeftPath());
			cc.connect(data);
		} else {
			try {
				if (config.getProgramSettings().getBooleanOption(
						TProgramSettings.OPTION_CONNECT_TO_LOCAL)) {

					final TControlCenter cc = leftBrowser
							.getFirstControlCenter();

					final TConnectionData data = new TConnectionData(this);

					if (config.getProgramSettings().getBooleanOption(
							TProgramSettings.OPTION_USE_GNU_FOR_LOCAL)) {

						data.setProtocol(ConnectionPluginManager.GNU_PLUGIN);
					}

					if (config.getProgramSettings().getBooleanOption(
							TProgramSettings.OPTION_USE_CUSTOM_PATH)) {

						final String customPath = config.getProgramSettings()
								.getStringOption(
										TProgramSettings.OPTION_CUSTOM_PATH);

						if (customPath != null) {
							cc.setInitialDirectory(customPath);
						}

					}
					cc.connect(data);
				}
			} catch (SettingsTypeException e) {
				LOGGER.severe(e.getMessage());
			}

		}

		if (opts.hasRightTarget()) {
			TControlCenter cc = rightBrowser.getFirstControlCenter();
			int port = ConnectionPluginManager.getConnectionModelInstance(
					opts.getRightProtocol()).getDefaultPort();
			TConnectionData data = new TConnectionData(this,
					opts.getRightHost(), port, opts.getRightUser(), "",
					opts.getRightProtocol());
			cc.setInitialDirectory(opts.getRightPath());
			cc.connect(data);
		}
	}

	// --------------------------------------------------------------------------

	private void handleQuickConnectMessage(QuickConnectMessage message) {
		if (message == null) {
			LOGGER.warning("quick connect msg is null");
			return;
		}

		TControlCenter targetCC = null;
		if (message.getNewTab()) {
			targetCC = TControlCenterFactory.getInstance(this)
					.getNextControlCenter();
			if (leftBrowser.containsControlCenterById(message.getSourceCCId())) {
				// create new tab on left side
				leftBrowser.addControlCenter(targetCC);
				leftBrowser.showControlCenter(targetCC.getID());
			} else {
				rightBrowser.addControlCenter(targetCC);
				rightBrowser.showControlCenter(targetCC.getID());
			}
		} else {
			targetCC = leftBrowser.getControlCenter(message.getSourceCCId());
			if (targetCC == null) {
				targetCC = rightBrowser.getControlCenter(message
						.getSourceCCId());
			}
		}

		if (targetCC == null) {
			LOGGER.warning("target control center is null");
			return;
		}

		TEventMulticaster.getInstance().fireTEvent(this, targetCC.getID(),
				new ConnectRequestMessage(message.getConnectionConfig()));
	}

	// --------------------------------------------------------------------------

	private static void initLogging() {
		// Jawinton
		final String logPropResource = "/net/sf/jfilesync/capivara_logger.properties";

		try {
			LogManager.getLogManager().readConfiguration(
					MainWin.class.getResourceAsStream(logPropResource));
		} catch (IOException e) {

			System.err
					.println("Cannot initialize logging. Error while reading "
							+ logPropResource);

			Logger.getLogger("").setLevel(Level.WARNING);
		}
	}

	private static void setupLookAndFeel() {
		String lookAndFeelClass = UIManager.getSystemLookAndFeelClassName();
		try {
			lookAndFeelClass = config.getProgramSettings().getStringOption(
					TProgramSettings.OPTION_LOOK_AND_FEEL_CLASS);
		} catch (SettingsTypeException e) {
			LOGGER.warning(e.getMessage());
		}

		try {
			UIManager.setLookAndFeel(lookAndFeelClass);
		} catch (Exception ex) {
			LOGGER.warning(ex.getMessage());
		}
	}

	/*
	 * just iterate over capiService list and start 'services'
	 */
	private void startServices() {

		capiServices.add(new CapivaraSystemTracker());

		for (CapivaraService service : capiServices) {
			service.startService();
		}

	}

	private void stopServices() {
		for (CapivaraService service : capiServices) {
			service.stopService();
		}
	}

	private static void setupLanguage() {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					LanguageDialog langDia = new LanguageDialog(TStartup
							.getSplashScreenFrame());
					langDia.setVisible(true);
					String userLang = langDia.getSelectedLanguage();
					try {
						config.getProgramSettings().setProgramOption(
								TProgramSettings.OPTION_USER_LANG, userLang);
					} catch (SettingsTypeException e) {
						LOGGER.warning("Cannot store language settings"
								+ e.getMessage());
						e.printStackTrace();
					}
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	// --------------------------------------------------------------------------

	private static void showNoticeDialog(final JFrame w) {
		try {
			boolean showNote = config.getProgramSettings().getBooleanOption(
					TProgramSettings.OPTION_SHOW_NOTE);
			if (showNote) {
				SwingUtilities.invokeLater(new ComponentVisibleRunner(
						new InfoDialog(w), true));
			}
		} catch (IOException e) {
			LOGGER.warning("cannot open info dialog: " + e.getMessage());
		} catch (SettingsTypeException e) {
			LOGGER.warning(e.getMessage());
		}
	}

	// --------------------------------------------------------------------------
	public static void main(String args[]) {

		initLogging();

		// this is to see various options for different OS
		// when testing
		if (LOGGER.isLoggable(Level.INFO)) {
			printJvmConfig();
		}

		opts = new CmdLineOptions(args);

		if (opts.showHelp()) {
			System.out.println("options : " + opts.getUsage());
		} else {

			BuildInfoPropertiesReader.readBuildVersionData();
			BuildInfoPropertiesReader.readCapivaraProperties();

			if (!opts.hideSplashScreen()) {
				TStartup.showSplashScreen();
			}

			TStartup.advanceSplashProgress("loading configuration..");
			loadConfiguration();

			TStartup.advanceSplashProgress("setting up look and feel..");
			setupLookAndFeel();

			TStartup.advanceSplashProgress("loading key bindings..");
			loadKeyBindings();

			TStartup.advanceSplashProgress("initializing components..");
			setupConfiguration();

			MainWin w = new MainWin();
			TStartup.hideSplashScreen();
			TStartup.disposeSplashScreen();
			SwingUtilities.invokeLater(new ComponentVisibleRunner(w, true));
			w.initConnections();
			// showNoticeDialog(w);
		}

	}

	/* Jawinton */
	public void focusGained(FocusEvent e) {
		Object obj = e.getSource();

		if (obj == hostCombo
				|| obj == hostCombo.getEditor().getEditorComponent()) {
			hostLabel.setFont(boldFont);
		} else if (obj == userField) {
			userLabel.setFont(boldFont);
		} else if (obj == portTextField) {
			portLabel.setFont(boldFont);
		}
	}

	public void focusLost(FocusEvent e) {
		Object obj = e.getSource();

		if (obj == hostCombo
				|| obj == hostCombo.getEditor().getEditorComponent()) {
			hostLabel.setFont(normFont);
		} else if (obj == userField) {
			userLabel.setFont(normFont);
		} else if (obj == portTextField) {
			portLabel.setFont(normFont);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			Object obj = e.getSource();
			if (obj == this.hostCombo) {
				dispatchEvent(new FocusEvent(this.portTextField,
						FocusEvent.FOCUS_GAINED));
			} else if (obj == this.portTextField) {
				// this.dispatchEvent( new FocusEvent(OkayButton,
				// FocusEvent.FOCUS_GAINED) );
				// simulate the button click
				// clean code ??? - to create an ActionEvent has no effect, why
				// ?
				dispatchEvent(new FocusEvent(this.userField,
						FocusEvent.FOCUS_GAINED));
			} else if (obj == this.userField) {
				// clean code ?
				dispatchEvent(new FocusEvent(this.loginButton,
						FocusEvent.FOCUS_GAINED));
			} else if (obj == this.loginButton) {
				actionPerformed(new ActionEvent(this.loginButton,
						ActionEvent.ACTION_PERFORMED, "login_command"));
			}
		} // end VK_ENTER

	}

	@Override
	public void prefixItemChosen(PrefixItemEvent event) {
		String host = event.getStringData();
		assert (host != null);
		searchForMatchingHostEntries(host);
	}

	protected void searchForMatchingHostEntries(String host) {
		// try to find matching entry "host" in config
		for (int i = 0; i < conConf.length; i++) {
			if (conConf[i].getHostName().compareTo(host) == 0) {
				String username = conConf[i].getUserName();
				int port = conConf[i].getPort();
				this.userField.setText(username);
				this.portTextField.setText(String.valueOf(port));
				dispatchEvent(new FocusEvent(this.loginButton,
						FocusEvent.FOCUS_GAINED));
				return;
			}
		}
		this.dispatchEvent(new FocusEvent(this.loginButton,
				FocusEvent.FOCUS_GAINED));
	}

	public String getHost() {
		return ((String) hostCombo.getEditor().getItem()).trim();
	}

	public void setHost(String host) {
		hostCombo.getEditor().setItem(host);
	}

	public String getUser() {
		return userField.getText().trim();
	}

	public void setUser(String user) {
		userField.setText(user);
	}

	public int getPort() {
		String portText = portTextField.getText();
		int port = -1;
		if (portText.equals("") || portText.equals("-1")) {
			port = activePlugin.getDefaultPort();
		} else {
			try {
				port = Integer.parseInt(portText);
			} catch (NumberFormatException e) {
				LOGGER.warning("Not a number : " + e.getMessage());
				port = activePlugin.getDefaultPort();
			}
		}
		return port;
	}

	public void setPort(int port) {
		if (port == -1) {
			if (activePlugin.isLocalConnection()) {
				portTextField.setText("");
			} else {
				portTextField.setText(Integer.toString(activePlugin
						.getDefaultPort()));
			}
		} else {
			portTextField.setText(Integer.toString(port));
		}
	}

	public synchronized boolean setupConnectionData() {
		if ("".equals(this.getHost()) || "".equals(this.getUser())) return false;
		this.conData = new TConnectionData(this, this.getHost(), this.getPort(), this.getUser(), "",
		        ConnectionPluginManager.HDFS_PLUGIN);
		return true; // successful data update
	}
	
	private void ChooseHostButton_actionPerformed(ActionEvent e) {
        ConParams params = new ConParams();
        UserHostsDialog hostDialog = new UserHostsDialog(this,
                params);
        hostDialog.setVisible(true);

        if (!params.hostname.equals("")) {
            setHost(params.hostname);
            setUser(params.username);
            setPort(params.port);
            // let's give the password field the focus since
            // only the password needs to be entered
            loginButton.requestFocus();
        }
    }
	/* Jawinton */
}
