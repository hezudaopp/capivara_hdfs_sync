/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2009  Sascha Hunold <hunoldinho@users.sourceforge.net>
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

package net.sf.jfilesync.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerListener;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.ConnectionConfig;
import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.engine.GWorkerStore;
import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileDataSorter;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.engine.worker.BasicFileTreeBuilder;
import net.sf.jfilesync.engine.worker.BasicFileTreeEvent;
import net.sf.jfilesync.engine.worker.DeleteFilesWorker;
import net.sf.jfilesync.engine.worker.PermissionsWorker;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.TAbstractGUIElementListener;
import net.sf.jfilesync.engine.worker.TAbstractWorkerGUIElement;
import net.sf.jfilesync.engine.worker.TGetFileListWorker;
import net.sf.jfilesync.engine.worker.WorkerComponentStore;
import net.sf.jfilesync.engine.worker.events.TFileListEvent;
import net.sf.jfilesync.event.ControlCenterBusListener;
import net.sf.jfilesync.event.ControlCenterEvent;
import net.sf.jfilesync.event.QuickConnectListener;
import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventListener;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.event.types.ConnectRequestMessage;
import net.sf.jfilesync.event.types.ConnectionErrorMsg;
import net.sf.jfilesync.event.types.ConnectionStateMessage;
import net.sf.jfilesync.event.types.ControlActivationEvent;
import net.sf.jfilesync.event.types.ControlChangeEvent;
import net.sf.jfilesync.event.types.ErrorMessage;
import net.sf.jfilesync.event.types.LsCancelledMessage;
import net.sf.jfilesync.event.types.LsFailedMessage;
import net.sf.jfilesync.event.types.TChdirMessage;
import net.sf.jfilesync.event.types.TLsReplyMessage;
import net.sf.jfilesync.event.types.TSortFileMessage;
import net.sf.jfilesync.event.types.TStandardMessage;
import net.sf.jfilesync.gui.components.BookmarkActionListener;
import net.sf.jfilesync.gui.components.BookmarkPanel;
import net.sf.jfilesync.gui.components.BookmarkShowEvent;
import net.sf.jfilesync.gui.components.BookmarksListener;
import net.sf.jfilesync.gui.components.ConnectionContextComponentGroup;
import net.sf.jfilesync.gui.components.DeleteWorkerPanel;
import net.sf.jfilesync.gui.components.FileCollectWorkerPanel;
import net.sf.jfilesync.gui.components.SimpleFileProgressWorkerPanel;
import net.sf.jfilesync.gui.components.WorkerPanel;
import net.sf.jfilesync.gui.dialog.BookmarkPopupMenu;
import net.sf.jfilesync.gui.dialog.PermissionChangeDisplayDialog;
import net.sf.jfilesync.gui.dialog.PermissionsChooseDialog;
import net.sf.jfilesync.gui.dialog.TFileRenameDialog;
import net.sf.jfilesync.gui.keymap.KeyStrokeModel;
import net.sf.jfilesync.gui.viewmodel.TFileDataModel;
import net.sf.jfilesync.gui.viewmodel.TFileDataTable;
import net.sf.jfilesync.plugins.GeneralPlugin;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.plugins.net.CaseInsensitiveProxy;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.ConnectionProxy;
import net.sf.jfilesync.plugins.net.ConnectionStatusProxy;
import net.sf.jfilesync.plugins.net.EncodingProxy;
import net.sf.jfilesync.plugins.net.KeepConnectionAliveProxy;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.plugins.net.items.AbstractLocalPlugin;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.service.unify.DupController;
import net.sf.jfilesync.service.unify.DupExplorer;
import net.sf.jfilesync.service.unify.UnifyEfficientDuplicateSearchWorker;
import net.sf.jfilesync.service.unify.UnifyFileFactory;
import net.sf.jfilesync.service.unify.UnifyFileTree;
import net.sf.jfilesync.service.unify.DupController.DupControllerEvent;
import net.sf.jfilesync.service.unify.action.UnifyDeleteOption;
import net.sf.jfilesync.service.unify.action.UnifyMoveOption;
import net.sf.jfilesync.service.unify.action.UnifyOption;
import net.sf.jfilesync.service.unify.worker.UnifyDeletePerformer;
import net.sf.jfilesync.service.unify.worker.UnifyMovePerformer;
import net.sf.jfilesync.settings.ConfigDefinitions;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;
import net.sf.jfilesync.util.FontUtils;
import net.sf.jfilesync.util.TMiscTool;
import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;
import net.sf.jfilesync.util.TWindowPositioner;

public class TControlCenter extends JPanel implements ActionListener, TEventListener, TAbstractGUIElementListener,
    ControlCenterBusListener, GWorkerListener, BookmarksListener, BookmarkActionListener {

  private static final long serialVersionUID = 1L;
  private final JFrame parent;
  private final int ccID;

  private TFileDataTable fileDataTable;
  // private FileTable2 fileDataTable;
  private TFileDataModel tableDataModel;
  private TFileData fileData;
  private TFileDataSorter dataSorter;
  private AbstractConnectionProxy con = null;
  private TConnectionData conData = null;
  private String currentPath = null;
  private String lastPath = null; // last path that was listed

  private GWorkerObserver observer = new GWorkerObserver();
  // there should only be one active worker per ControlCenter
  // even though they can be spawned recursively
  private GWorker activeWorker;
  private TAbstractWorkerGUIElement abstractGuiElement;
  private WorkerComponentStore compStore = new WorkerComponentStore();
  private GWorkerStore workerStore = new GWorkerStore();

  // -------------------------------------------------------------------------
  // GUI stuff
  private TStatusLine statusLine = new TStatusLine();
  private StatusPanel statusPanel = new StatusPanel();
//  private JToggleButton connectButton;
  private JScrollPane scrollPane;
  private JComboBox pathBox;
  private final BookmarkPanel bookmarkPanel = new BookmarkPanel();

  // displays file system roots, e.g., c: or d:
  private TRootChooser rootBox;

  // action command String constants
  private final static String FS_ROOT_CHANGED = "fs_root_changed";
  private final static String CHANGE_PERMISSIONS = "change_rwx";
  private final static String ADD_BOOKMARK_ACTION = "bookmark";
  private final static String ACTION_EXECUTE = "execute_task_action";

  private TTablePopupMenu tablePopup = new TTablePopupMenu(this);
  private TComponentGroup controlGroup1 = new TComponentGroup();
  private ConnectionContextComponentGroup contextControl = new ConnectionContextComponentGroup();
  private final Dimension buttonDimension = new Dimension(34, 34);

  // combo box with actions that can be applied on one connection
  private JComboBox actionBox = new JComboBox();
  private final static int TASK_UNIFY = 0;
  private final static int[] actionTasks = { TASK_UNIFY };
  private final static HashMap<Integer, String> taskId2Name = new HashMap<Integer, String>();
  static {
    taskId2Name.put(TASK_UNIFY, LanguageBundle.getInstance().getMessage("label.unify"));
  }

  // -------------------------------------------------------------------------
  // helper variables

  private boolean showHiddenFiles = true; // show hidden files
  private boolean active;
  private boolean isReloadEvent;
  private String lastSelectedFile; // file name

  // initial start directory right after connect
  private String initialDirectory;
  // set home to starting directory if remote
  private String homeDirectory;

  private final static Logger LOGGER = Logger.getLogger(TControlCenter.class.getName());

  public TControlCenter(final JFrame parent, final int ccID) {

    this.parent = parent;
    this.ccID = ccID;
    fileData = new TFileData();
    dataSorter = new TFileDataSorter(fileData);
    tableDataModel = new TFileDataModel(fileData);
    fileDataTable = new TFileDataTable(ccID, tableDataModel);
    // fileDataTable = new FileTable2(ccID, tableDataModel);
    fileDataTable.addMouseListener(new ControlCenterFileTableMouseAdapter());

    registerListeners();
    registerKeyEvents();
    loadConfig();

    initUI();
    clearCenter();
  }

  public void setInitialDirectory(final String dir) {
    initialDirectory = dir;
  }

  private void registerListeners() {
    final TEventMulticaster emc = TEventMulticaster.getInstance();

    emc.addTEventListener(this, TMessage.ID.CHDIR_REQUEST_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.SORT_FILE_LIST_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.CONNECT_REQUEST_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.CONNECT_REPLY_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.CONNECT_FAILURE_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.DISCONNECT_REPLY_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.LS_REPLY_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.LS_FAILURE_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.LS_CANCELLED_MESSAGE);

    emc.addTEventListener(this, TMessage.ID.DELETE_OK_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.DELETE_CANCELLED_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.DELETE_FAILURE_MESSAGE);

    emc.addTEventListener(this, TMessage.ID.DESELECT_FILES_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.DELETE_FILES_MESSAGE);
    emc.addTEventListener(this, TMessage.ID.ERROR_MESSAGE);

    emc.addTEventListener(this, TMessage.ID.RELOAD_DIR_MESSAGE);

    observer.addWorkerListener(this);
    bookmarkPanel.addBookmarksListener(this);
  }

  private void registerKeyEvents() {
    KeyStrokeModel.registerAction("reload", this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new CCKeyHandler(
        "reload"));
    KeyStrokeModel.registerAction("rename", this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new CCKeyHandler(
        "rename"));
    KeyStrokeModel.registerAction("viewer", this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new CCKeyHandler(
        "viewer"));
    KeyStrokeModel.registerAction("mkdir", this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new CCKeyHandler(
        "mkdir"));

  }

  private void loadConfig() {
    try {
      showHiddenFiles = MainWin.config.getProgramSettings().getBooleanOption(TProgramSettings.OPTION_HIDDEN_FILES);
      LOGGER.info("hiddenFiles: " + showHiddenFiles);
      if (showHiddenFiles) {
        dataSorter.removeFilter(TFileDataSorter.FILTER_HIDDEN_FILES);
      } else {
        dataSorter.addFilter(TFileDataSorter.FILTER_HIDDEN_FILES);
      }
      updateTableView();
    } catch (SettingsTypeException ex) {
      LOGGER.log(Level.WARNING, ex.getMessage());
    }
  }

  private void initUI() {

    setLayout(new BorderLayout());

    scrollPane = new JScrollPane(fileDataTable);

    statusLine.addTAbstractGUIElementListener(this);

    /* Jawinton */
    // connectButton =
    // MainWin.themeFactory.createToggleButton("togglebutton_connection");
    /*
    connectButton = new JToggleButton();
    connectButton.setIcon(new ImageIcon(getClass()
        .getResource("/net/sf/jfilesync/gui/themes/default/disconnected2.png")));
    connectButton.setPressedIcon(new ImageIcon(getClass().getResource(
        "/net/sf/jfilesync/gui/themes/default/connecting2.png")));
    connectButton.setSelectedIcon(new ImageIcon(getClass().getResource(
        "/net/sf/jfilesync/gui/themes/default/connected2.png")));
    connectButton.setPreferredSize(buttonDimension);

    connectButton.addActionListener(this);
    connectButton.setActionCommand("connect");
    connectButton.setBorderPainted(true);
    connectButton.setFocusable(false);
    connectButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.connect"));
    connectButton.addMouseListener(new QuickConnectListener(ccID));
    */
    final String[] roots = { "/" };
    rootBox = new TRootChooser(this, roots);
    // rootBox.setPreferredSize(new Dimension(45,20));
    // rootBox.setMinimumSize(new Dimension(45,20));
    // rootBox.setMaximumSize(new Dimension(45,20));
    int fontSize = FontUtils.getFontSize(rootBox.getEditor().getEditorComponent(), rootBox.getEditor()
        .getEditorComponent().getFont(), "/");
    rootBox.setPreferredSize(new Dimension(60, fontSize + 5));
    rootBox.setMinimumSize(new Dimension(60, fontSize + 5));
    rootBox.setMaximumSize(new Dimension(60, fontSize + 5));
    rootBox.setEnabled(false);
    controlGroup1.add(rootBox);

    pathBox = new JComboBox();
    pathBox.setPreferredSize(new Dimension(300, fontSize + 5));
    pathBox.setMinimumSize(new Dimension(50, fontSize + 5));
    pathBox.setEditable(true);
    pathBox.addItem(new String(""));
    controlGroup1.add(pathBox);

    Component editor = pathBox.getEditor().getEditorComponent();
    editor.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        // get last path
        // get newpath from combobox
        // try sftp.chdir(newpath)
        // if it fails chdir(lastpath)
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          String newpath = pathBox.getEditor().getItem().toString();
          // normalize path here
          newpath = con.getPathControlInstance().normalize(newpath);
          newpath = con.getPathControlInstance().trimPathString(newpath);
          LOGGER.info("newpath: " + newpath);
          /*
           * since 0.3a check if path exists directly over existing con if so ->
           * save path and load directory
           */
          boolean valid = con.exists(newpath);
          LOGGER.info("newpath : " + newpath + " exists ? " + valid);
          if (valid) {
            // check if newpath is in already in list
            boolean inlist = false;
            for (int i = 0; i < pathBox.getItemCount(); i++) {
              if (pathBox.getItemAt(i).equals(newpath)) {
                inlist = true;
                break;
              }
            }
            if (!inlist) {
              ItemListener[] ils = pathBox.getItemListeners();
              pathBox.removeItemListener(ils[0]);
              pathBox.insertItemAt(newpath, 0);
              pathBox.setSelectedIndex(0);
              pathBox.addItemListener(ils[0]);
            }
            change_dir(newpath);
          } else {
            TErrorHandling.reportError(TErrorHandling.ERROR_IO_PATH_UNKNOWN);
          }
        }
      }
    });

    pathBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        String curpath = e.getItem().toString();
        String newpath = pathBox.getEditor().getItem().toString();
        LOGGER.info("newpath: " + newpath + " curpath " + curpath);
        if (con != null && con.isConnected() && con.exists(newpath) && !newpath.equals(curpath)) {
          change_dir(newpath);
        }
      }
    });

    add(createTopPanel(), BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);

    JPanel dummyPanel = new JPanel(true);
    dummyPanel.setLayout(new BoxLayout(dummyPanel, BoxLayout.Y_AXIS));
    dummyPanel.add(statusPanel);
    dummyPanel.add(statusLine);

    add(dummyPanel, BorderLayout.SOUTH);

    setBorder(null);

  }

  protected JPanel createTopPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//    panel.add(createButtonPanel());	// Jawinton
//    panel.add(createActionPanel());	// Jawinton
    panel.add(createPathPanel());
    return panel;
  }

  protected JPanel createButtonPanel() {
    JButton dirUpButton = new JButton(new ImageIcon(getClass().getResource(
        "/net/sf/jfilesync/gui/themes/default/1uparrow.png")));
    initButton(dirUpButton);
    dirUpButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.dirup"));
    dirUpButton.addActionListener(this);
    dirUpButton.setActionCommand("dirup");

    JButton homeButton = new JButton(new ImageIcon(getClass().getResource(
        "/net/sf/jfilesync/gui/themes/default/gohome.png")));
    initButton(homeButton);
    homeButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.home"));
    homeButton.addActionListener(this);
    homeButton.setActionCommand("go_home");

    JButton mkdirButton = new JButton(new ImageIcon(getClass().getResource(
        "/net/sf/jfilesync/gui/themes/default/folder_new.png")));
    initButton(mkdirButton);
    mkdirButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.mkdir"));
    mkdirButton.addActionListener(this);
    mkdirButton.setActionCommand("mkdir");

    JButton deleteButton = new JButton(new ImageIcon(getClass().getResource(
        "/net/sf/jfilesync/gui/themes/default/edittrash.png")));
    initButton(deleteButton);
    deleteButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.delete"));
    deleteButton.addActionListener(this);
    deleteButton.setActionCommand("delete");

    JButton reloadButton = new JButton(new ImageIcon(getClass().getResource(
        "/net/sf/jfilesync/gui/themes/default/reload.png")));
    initButton(reloadButton);
    reloadButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.refresh"));
    reloadButton.addActionListener(this);
    reloadButton.setActionCommand("reload");

    JButton permButton = new JButton("rwx");
    permButton.setFocusable(false);
    permButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.permissions"));
    permButton.addActionListener(this);
    permButton.setActionCommand(CHANGE_PERMISSIONS);
    permButton.setPreferredSize(new Dimension(60, 34));
    int[] plugins = ConnectionPluginManager.getSupportedPlugins();
    for (int i = 0; i < plugins.length; i++) {
      if (ConnectionPluginManager.getConnectionModelInstance(plugins[i]).isProvided(
          GeneralPlugin.PROVIDES_PERMISSION_HANDLING)) {
        contextControl.addComponent(permButton, plugins[i]);
      }
    }

    JButton bookmarkButton = new JButton(new ImageIcon(getClass().getResource(
        "/net/sf/jfilesync/gui/themes/default/stock-star2.png")));
    initButton(bookmarkButton);
    bookmarkButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.bookmark"));
    bookmarkButton.addActionListener(this);
    bookmarkButton.setActionCommand(ADD_BOOKMARK_ACTION);

    FlowLayout tightFlowLayout = new FlowLayout(FlowLayout.LEFT, 1, 1);
    JPanel buttonPanel = new JPanel(tightFlowLayout);

//    buttonPanel.add(connectButton);
    buttonPanel.add(dirUpButton);
    buttonPanel.add(homeButton);
    buttonPanel.add(reloadButton);
    buttonPanel.add(mkdirButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(bookmarkButton);
    buttonPanel.add(permButton);

    return buttonPanel;
  }

  protected JPanel createActionPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    for (int i = 0; i < actionTasks.length; i++) {
      actionBox.addItem(taskId2Name.get(actionTasks[i]));
    }

    JButton startButton = new JButton(LanguageBundle.getInstance().getMessage("label.start"));
    startButton.setFocusable(false);
    startButton.addActionListener(this);
    startButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.start_action"));
    startButton.setActionCommand(ACTION_EXECUTE);

    panel.add(actionBox);
    panel.add(startButton);

    return panel;
  }

  // ------------------------------------------------------------------------

  /*
   * Panel containing current location, current root (Windows), bookmark button
   */

  private JPanel createPathPanel() {
    JPanel pathPanel = new JPanel();

    JLabel pathLabel = new JLabel();
    pathLabel.setText(LanguageBundle.getInstance().getMessage("component.path.label"));

    SpringLayout layout = new SpringLayout();
    pathPanel.setLayout(layout);

    layout.putConstraint(SpringLayout.WEST, pathLabel, 2, SpringLayout.WEST, pathPanel);
    layout.putConstraint(SpringLayout.NORTH, pathLabel, 0, SpringLayout.NORTH, pathPanel);
    layout.putConstraint(SpringLayout.SOUTH, pathLabel, 0, SpringLayout.SOUTH, pathPanel);
    layout.putConstraint(SpringLayout.WEST, rootBox, 2, SpringLayout.EAST, pathLabel);
    layout.putConstraint(SpringLayout.WEST, pathBox, 0, SpringLayout.EAST, rootBox);
    layout.putConstraint(SpringLayout.WEST, bookmarkPanel, 0, SpringLayout.EAST, pathBox);
    layout.putConstraint(SpringLayout.EAST, pathPanel, 0, SpringLayout.EAST, bookmarkPanel);
    layout.putConstraint(SpringLayout.SOUTH, pathPanel, 0, SpringLayout.SOUTH, pathBox);

    pathPanel.add(pathLabel);
    pathPanel.add(rootBox);
    pathPanel.add(pathBox);
    pathPanel.add(bookmarkPanel);

    return pathPanel;
  }

  private void initButton(JButton button) {
    button.setPreferredSize(buttonDimension);
    button.setMinimumSize(buttonDimension);
    button.setMaximumSize(buttonDimension);
    button.setFocusable(false);
    controlGroup1.add(button);
  }

  // -------------------------------------------------------------------------

  /**
   * reset all data structures and gui objects
   */
  public void clearCenter() {
    // clear data structure
    fileData = new TFileData();
    tableDataModel.setFileData(fileData);
    dataSorter.setFileData(fileData);
    fileDataTable.setDataModel(tableDataModel);

    updateFileDataModel(fileData);
    fileDataTable.getTableHeader().updateUI();
    updateCurrentPath();

    fileDataTable.deselectAllFiles();

    // okay - generate a clean connection :-)
    con = null;

    // show connect button
    setConnectionState(false);
//    connectButton.setEnabled(true);

    rootBox.reset();
    rootBox.setEnabled(false);

    // intialDirectory not valid anymore
    initialDirectory = null;
  }

  // ------------------------------------------------------------------------

  protected void setConnectionState(boolean connected) {

    setConnectButtonState(connected);

    TEventMulticaster.getInstance().fireTEvent(this, getID(), new ConnectionStateMessage(getID(), connected));

    if (connected) {
      statusLine.setTextColor(new Color(0x065d00));
      statusLine.setText(LanguageBundle.getInstance().getMessage("status.msg.connected"));
      statusLine.setHostString(conData.getPlugin().getProtocolString() + "://" + conData.getUser() + "@"
          + conData.getHost());
      statusLine.setHostTextColor(new Color(0x000786));
    } else {
      statusLine.setTextColor(new Color(0xad0505));
      statusLine.setText(LanguageBundle.getInstance().getMessage("status.msg.disconnected"));
      statusLine.setHostString("");
    }

  }

  protected void checkConnection() {
    if (con == null || !con.isConnected()) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          JOptionPane.showMessageDialog(TControlCenter.this,
              LanguageBundle.getInstance().getMessage("dialog.message.disconnected"));
          clearCenter();
        }
      });
    }
  }

  // ------------------------------------------------------------------------

  /*
   * set the connect/disconnect button
   * 
   * @param connected
   */
  private void setConnectButtonState(boolean connected) {
    if (connected) {
    	// Jawinton
//      connectButton.setSelected(true);
//      connectButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.disconnect"));
      bookmarkPanel.setEnabled(true);
    } else {
      // make the disconnect button
    	// Jawinton
//      connectButton.setSelected(false);
//      connectButton.setToolTipText(LanguageBundle.getInstance().getMessage("button.tooltip.connect"));
      bookmarkPanel.setEnabled(false);
    }

    controlGroup1.setEnable(connected);
    if (con != null && con.getPlugin() != null) {
      contextControl.enableComponentsForPluginID(connected, con.getPlugin().getConnectionID());
    } else {
      contextControl.disableAllComponents();
    }
  }

  // ------------------------------------------------------------------------

  public void updateFileDataModel(TFileData fd) {
    tableDataModel.updateView(fd);

    if (con != null && con.getPlugin().isProvided(GeneralPlugin.PROVIDES_PERMISSION_HANDLING)) {
      ((TFileDataModel) fileDataTable.getModel()).setColumnCount(4);
    } else {
      ((TFileDataModel) fileDataTable.getModel()).setColumnCount(3);
    }
    fileDataTable.revalidateColumns();

    fileDataTable.deselectAllFiles();
    fileDataTable.updateUI(); // update look

    // if( isActive() && tableDataModel.getRowCount() > 0 ) {
    // fileDataTable.selectRow(0);
    // fileDataTable.requestFocus();
    // }

    if (scrollPane != null) {
      scrollPane.getVerticalScrollBar().setValue(0);
    }

    /*
     * // hack to avoid having selected rows // in both ControlCenters
     * fileDataTable.selectRow(0);
     * TControlCenterFactory.getControlCenterBus().fireControlCenterBusEvent(
     * new ControlActivationEvent(this, params.getCentrolCenterId()) );
     */
  }

  protected void resetTableScrollBars() {
    scrollPane.getViewport().setViewPosition(new Point(0, 0));
  }

  protected void setCurrentPath(final String path) {
    currentPath = path;

    ItemListener[] ils = pathBox.getItemListeners();
    LOGGER.info("path box listeners : " + ils.length);
    for (int i = 0; i < ils.length; i++) {
      pathBox.removeItemListener(ils[i]);
    }

    boolean found = false;
    for (int i = 0; i < pathBox.getItemCount(); i++) {
      String boxPath = (String) pathBox.getItemAt(i);
      if (boxPath.equals(path)) {
        found = true;
      }
    }

    if (found) {
      LOGGER.info("set path box editor : " + path);
      pathBox.getEditor().setItem(path);
    } else {
      pathBox.removeItemAt(0);
      pathBox.insertItemAt(path, 0);
      pathBox.setSelectedIndex(0);
    }

    // check if we need to change the fs root on Windows (c:,d:,..)
    final String root = con.getPathControlInstance().getRoot(path);
    if (root != null && !root.toUpperCase().equals(rootBox.getCurrentRoot().toUpperCase())) {
      rootBox.setSelectedItem(root.toUpperCase());
    }

    for (int i = 0; i < ils.length; i++) {
      pathBox.addItemListener(ils[i]);
    }

  }

  public boolean isConnected() {
    if (con == null) {
      return false;
    }

    if (con.isConnected()) {
      return true;
    }

    return false;
  }

  // ------------------------------------------------------------------------

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    if (command.equals("connect")) {
      if (!isConnected()) {
        connectAction();
      } else {
        disconnectAction();
      }
    } else if (command.equals("mkdir")) {
      mkdirEvent();
    } else if (command.equals("delete")) {
      deleteEvent();
    } else if (command.equals("reload")) {
      reloadDir();
    } else if (command.equals("go_home")) {
      go_home();
    } else if (command.equals("hidden")) {
      if (showHiddenFiles) {
        dataSorter.addFilter(TFileDataSorter.FILTER_HIDDEN_FILES);
        showHiddenFiles = false;
      } else {
        dataSorter.removeFilter(TFileDataSorter.FILTER_HIDDEN_FILES);
        showHiddenFiles = true;
      }
      updateFileDataModel(dataSorter.getFileDataCopy());
    } else if (command.equals("edit")) {
      startEditor();
    } else if (command.equals("select_all")) {
      fileDataTable.selectAllFiles();
    } else if (command.equals("rename")) {
      renameEvent();
    } else if (command.equals(FS_ROOT_CHANGED)) {
      tryChangingTheRoot();
    } else if (command.equals("dirup")) {
      change_dir("..");
    } else if (command.equals(CHANGE_PERMISSIONS)) {
      change_permissions();
    } else if (command.equals(ADD_BOOKMARK_ACTION)) {
      add_bookmark();
    } else if (command.equals(ACTION_EXECUTE)) {
      executeAction();
    } else {
      LOGGER.warning("unkown command : " + e.getActionCommand());
    }

  }

  // ------------------------------------------------------------------------

  private void tryChangingTheRoot() {
    if (con == null || !con.isConnected())
      return;
    TPathControlInterface pathControl = TPathControlFactory.getPathControlInstance(TMiscTool.getOSId());
    String savedRoot = pathControl.getRoot(this.currentPath);
    String savedPath = this.currentPath;

    if (savedRoot.equals(rootBox.getCurrentRoot())) {
      // in case the user selects the current root once again
      // just return
      return;
    }

    try {

      con.chdir(rootBox.getCurrentRoot());
      fileData = con.ls();
      dataSorter.setFileData(fileData);
      dataSorter.sortFileDataByLastPropertyUsed();
      updateFileDataModel(dataSorter.getFileDataCopy());
      fileDataTable.clearSelection();
      resetTableScrollBars();
      updateCurrentPath();

    } catch (IOException ex) {

      try {
        // ok something went wrong with changing
        // to a different root directory
        // rollback to saved state
        con.chdir(savedPath);
        rootBox.setSelectedItem(savedRoot);

      } catch (IOException ioex) {
        LOGGER.warning(ioex.getMessage());
      }

      LOGGER.warning(ex.getMessage());
    }
  }

  // ------------------------------------------------------------------------

  private void connectAction() { // Jawinton, private to public
    if (conData == null) {
      conData = new TConnectionData(parent);
    }

    if (!conData.promptForConnectionData(true)) {
      setConnectionState(false);
      return;
    }

    doConnect();
  }
  
  // ------------------------------------------------------------------------

  public void connect(TConnectionData data) {
    conData = data;
    doConnect();
//	Jawinton
//    if (!(conData.getPlugin() instanceof AbstractLocalPlugin)) {	// Jawinton
//      if (conData.promptForConnectionData(true)) {
//        doConnect();
//      } else {
//        setConnectionState(false);
//      }
//    } else {
//      // local plugin
//      doConnect();
//    }

  }

  // ------------------------------------------------------------------------

  private void doConnect() {

    con = new ConnectionProxy(conData);
    if (conData.getEncoding() != null) {
      LOGGER.fine("Using encoding proxy..");
      con = new EncodingProxy(con, conData.getEncoding());
    }
    if (conData.isCaseInsensitive()) {
      LOGGER.fine("Using case insensitivew proxy..");
      con = new CaseInsensitiveProxy(con);
    }

    if (conData.isKeepAliveSet()) {
      LOGGER.fine("Using keep alive proxy..");
      con = new KeepConnectionAliveProxy(con, getID());
    }

    con = new ConnectionStatusProxy(con, getID());

    if (con != null) {
      // disable all gui elements
      // setStateOfGUI(false);
      controlGroup1.setEnable(false);
//      connectButton.setEnabled(false);	// Jawinton

      ConnectionEstablisher establisher = new ConnectionEstablisher(con);
      establisher.start();
    }
  }

  /*
   * This inner class ConnectionEstablisher is only a bad hack to avoid a
   * hanging GUI while connecting. It is to be removed by messaging asap! TODO
   * revise this
   */
  class ConnectionEstablisher extends Thread {
    private AbstractConnectionProxy con;
    private TEventMulticaster emc = TEventMulticaster.getInstance();

    public ConnectionEstablisher(AbstractConnectionProxy con) {
      this.con = con;
    }

    public void run() {
      // spawn another thread
      // and wait at most timeout seconds
      ConnectorRunnable connector = new ConnectorRunnable(con);
      connector.connect();

      // inform TCC about connection status
      if (!connector.isTimedOut()) {
        if (con.isConnected()) {
          LOGGER.finer("send connected");
          emc.fireTEvent(this, ccID, new TStandardMessage(TMessage.ID.CONNECT_REPLY_MESSAGE));
        } else {
          // LOGGER.info("send not connected");
        	 // Jawinton
          //emc.fireTEvent(this, ccID, new ConnectionErrorMsg(connector.getErrorCode(), connector.getErrorMessage()));
        	emc.fireTEvent(this, ccID, new TStandardMessage(TMessage.ID.CONNECT_FAILURE_MESSAGE));
        }
      } else {
        LOGGER.finer("send timeout");
        connector.interrupt();
        emc.fireTEvent(this, ccID, new ConnectionErrorMsg(TErrorHandling.ERROR_CONNECTION_TIMEDOUT, "Timeout"));
      }

    }
  }

  // ------------------------------------------------------------------------

  class ConnectorRunnable extends Thread {
    private final AbstractConnectionProxy con;

    private boolean completed;
    private boolean done;
    private boolean timedout = false;

    private Boolean doneFlag = new Boolean(false);

    private PluginConnectException pce;

    public ConnectorRunnable(final AbstractConnectionProxy con) {
      this.con = con;
    }

    public void run() {

      try {
        con.connect();
        completed = true;
      } catch (PluginConnectException pce) {
        this.pce = pce;
      }

      synchronized (doneFlag) {
        done = true;
        // LOGGER.info("notify");
        doneFlag.notifyAll();
      }

      LOGGER.info("connector closing");

      if (isInterrupted()) {
        con.disconnect();
      }
    }

    public synchronized boolean connetionCompleted() {
      return completed;
    }

    public synchronized int getErrorCode() {
      if (pce != null) {
        return pce.getErrorCode();
      } else {
        return TErrorHandling.NO_ERROR;
      }
    }

    public synchronized String getErrorMessage() {
      if (pce != null) {
        return pce.getMessage();
      } else {
        return null;
      }
    }

    public void connect() {

      start();
      try {
        // wait(ConfigDefinitions.WAIT_FOR_CONNECTION_TIMEOUT_S * 1000L);
        synchronized (doneFlag) {
          if (!done) {
            // LOGGER.info("wait start");
            doneFlag.wait(ConfigDefinitions.WAIT_FOR_CONNECTION_TIMEOUT_S * 1000L);
            // LOGGER.info("wait end");
            if (!done) {
              // LOGGER.info("SET TIMEOUT");
              timedout = true;
            }
          }
        }
      } catch (InterruptedException e) {
        LOGGER.log(Level.WARNING, e.getMessage(), e);
      }

    }

    public synchronized boolean isTimedOut() {
      return timedout;
    }
  }

  // ------------------------------------------------------------------------

  public void disconnectAction() {	// Jawinton, private to public
    if (con != null && con.isConnected()) {
      con.disconnect();
    }
    TEventMulticaster.getInstance().fireTEvent(this, ccID, new TStandardMessage(TMessage.ID.DISCONNECT_REPLY_MESSAGE));
  }

  // ------------------------------------------------------------------------

  public void processEvent(TEvent e) {

    if (!messageForMe(e)) {
      return;
    }

    switch (e.getMessage().getMessageType()) {
    // -----
    case CHDIR_REQUEST_MESSAGE:
      change_dir(((TChdirMessage) e.getMessage()).getPath());
      break;
    // -----
    case SORT_FILE_LIST_MESSAGE:
      dataSorter.sortFileDataInverse(((TSortFileMessage) e.getMessage()).getFilePropertyToSort());
      updateFileDataModel(dataSorter.getFileDataCopy());
      break;

    // -----
    case CONNECT_REPLY_MESSAGE:
      if (isConnected()) {

        // change to home directory
        homeDirectory = null;
        String use_path = getHomeDirectory();
        if (initialDirectory != null && initialDirectory.length() > 0) {
          use_path = con.getPathControlInstance().trimPathString(initialDirectory);
        }

        setConnectionState(true);

        if (con.getPlugin().getConnectionID() == ConnectionPluginManager.LOCAL_PLUGIN
            && TMiscTool.getOSId() == TMiscTool.OS_WINDOWS) {
          final File rootFiles[] = File.listRoots();
          final String rootStrings[] = new String[rootFiles.length];
          for (int i = 0; i < rootFiles.length; i++) {
            rootStrings[i] = rootFiles[i].getAbsolutePath();
          }
          TPathControlInterface pci = TPathControlFactory.getPathControlInstance(TMiscTool.OS_WINDOWS);
          String rootPath = pci.getRoot(use_path);
          rootBox.setRoots(rootStrings);
          rootBox.setSelectedRoot(rootPath);
          rootBox.setListenable(true);
          rootBox.setEnabled(true);
        }

        // get contents of directory
        if (use_path != null) {
          change_dir(use_path);
        }
      } else // Connect Reply - but no connection detected !!!
      {
        clearCenter();
        TErrorHandling.reportError(TErrorHandling.ERROR_CONNECTION_FAILURE, "something is wrong !");
      }
//      connectButton.setEnabled(true);
      controlGroup1.setEnable(true);
      break;
    // -----
    case CONNECT_FAILURE_MESSAGE:
      TErrorHandling.reportError(TErrorHandling.ERROR_CONNECTION_FAILURE, "connection not established !");
      clearCenter();
      break;
    case CONNECTION_LOST_MESSAGE:
      TErrorHandling.reportError(TErrorHandling.ERROR_CONNECTION_LOST, "connection lost!");
      clearCenter();
      break;
    // -----
    case DISCONNECT_REPLY_MESSAGE:
      // System.out.println("disconnect msg") ;
      clearCenter(); // reset
      break;
    // -----
    case LS_REPLY_MESSAGE:
      fileData = ((TLsReplyMessage) e.getMessage()).getFileData();

      fileDataTable.deselectAllFiles();
      dataSorter.setFileData(fileData);
      dataSorter.sortFileDataByLastPropertyUsed();
      updateFileDataModel(dataSorter.getFileDataCopy());
      updateCurrentPath();
      enableCC(true);

      if (isActive()) {
        TControlCenterFactory.getControlCenterBus().fireControlCenterBusEvent(
            new ControlActivationEvent(TControlCenter.this, ccID));
      }

      break;
    // -----
    case LS_FAILURE_MESSAGE:
      TErrorHandling.reportError(TErrorHandling.ERROR_IO_LS);
      enableCC(true);
      // change_dir(currentPath);
      break;
    // -----
    case LS_CANCELLED_MESSAGE:
      enableCC(true);
      // change_dir(currentPath);
      break;
    // -----
    case DELETE_OK_MESSAGE:
    case DELETE_CANCELLED_MESSAGE:
    case DELETE_FAILURE_MESSAGE:
      enableCC(true);
      reloadDir();
      break;
    case DESELECT_FILES_MESSAGE:
      fileDataTable.deselectAllFiles();
      break;
    case DELETE_FILES_MESSAGE:
      deleteEvent();
      break;
    case ERROR_MESSAGE:
      handleErrorMsg((ErrorMessage) e.getMessage());
      break;
    case RELOAD_DIR_MESSAGE:
      reloadDir();
      break;
    case CONNECT_REQUEST_MESSAGE:
      handleConnectMessage((ConnectRequestMessage) e.getMessage());
      break;
    default:
      LOGGER.warning("unhandled event: " + e);
    }
  }

  // ------------------------------------------------------------------------

  private void handleConnectMessage(ConnectRequestMessage message) {

    if (isConnected()) {
      int opt = JOptionPane.showConfirmDialog(this,
          LanguageBundle.getInstance().getMessage("connect.quick.curent_tab_question"), LanguageBundle.getInstance()
              .getMessage("connect.quick.curent_tab"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (opt == JOptionPane.NO_OPTION) {
        return;
      } else {
        disconnectAction();
      }
    }

    ConnectionConfig config = message.getConnectionConfig();
    TConnectionData data = new TConnectionData(parent, config.getHostName(), config.getPort(), config.getUserName(),
        "", config.getProtocol());

    connect(data);
  }

  // ------------------------------------------------------------------------

  protected void handleErrorMsg(ErrorMessage msg) {

    TErrorHandling.reportError(msg.getCode());

    if (msg instanceof ConnectionErrorMsg) {
      clearCenter();
    }
  }

  // ------------------------------------------------------------------------

  public void busMessageReceived(ControlCenterEvent e) {
    if (e instanceof ControlActivationEvent) {
      if (((ControlActivationEvent) e).getActiveID() == ccID) {
        // when user clicks on another tab
        // restore the new tabs selection
        if (!e.getSource().equals(this)) {
          fileDataTable.restoreSelectedRow();
        } else if (isReloadEvent && lastPath != null && currentPath != null && lastPath.equals(currentPath)) {
          // case when user reloads dir
          // restore selection as well
          if (lastSelectedFile != null) {
            fileDataTable.restoreSelectedRow(lastSelectedFile);
          } else {
            fileDataTable.selectRow(0);
            scrollPane.getVerticalScrollBar().setValue(0);
          }

        } else {
          // check if there is a dir to select
          if (lastPath != null && con != null && !lastPath.equals(currentPath) && lastPath.startsWith(currentPath)) {
            LOGGER.info("lastPath: " + lastPath + " currentPath: " + currentPath);
            String toSelect = lastPath.substring(currentPath.length(), lastPath.length());
            TPathControlInterface pc = con.getPathControlInstance();
            if (toSelect.startsWith(pc.getFileSeparator())) {
              toSelect = toSelect.substring(pc.getFileSeparator().length(), toSelect.length());
              int idx = toSelect.indexOf(pc.getFileSeparator());
              if (idx != -1) {
                toSelect = toSelect.substring(0, idx);
              }
            }
            LOGGER.info("toSelect : " + toSelect);

            int row = fileDataTable.getRowOfFile(toSelect);
            if (row != -1) {
              fileDataTable.selectRow(row);
              Rectangle r = fileDataTable.getCellRect(row, 0, false);
              Rectangle r2 = scrollPane.getBounds();
              // 30 is vertical offset of the JTable
              // which is more or less the height of the header
              int visibleNum = (r2.height - 30) / r.height;
              // System.out.println("visible : " + visibleNum);
              int scrollY = r.y - (visibleNum / 2 * r.height);
              if (scrollY < 0) {
                scrollY = 0;
              }
              // System.out.println("scrollY : " + scrollY);
              scrollPane.getVerticalScrollBar().setValue(scrollY);
            } else {
              fileDataTable.selectRow(0);
              scrollPane.getVerticalScrollBar().setValue(0);
            }

            lastPath = currentPath;
          } else {
            int row = fileDataTable.getSelectedRow();
            if (row == -1) {
              fileDataTable.selectRow(0);
            }
          }

        }

        setActive(true);
        // at last get the focus back
        requestFocusForFileTable();
      } else {
        if (isActive()) {
          fileDataTable.saveSelectedRow();
          fileDataTable.deselectAllFiles();
          setActive(false);
        }
      }
    } else if (e instanceof ControlChangeEvent) {
      // logger.debug("cc change : " + ccID + " activated : " +
      // ((ControlChangeEvent)e).getActiveID() );

      if (((ControlChangeEvent) e).getActiveID() == ccID) {
        fileDataTable.restoreSelectedRow();
        setActive(true);
        requestFocusForFileTable();
      } else {
        if (isActive()) {
          fileDataTable.saveSelectedRow();
          fileDataTable.deselectAllFiles();
          setActive(false);
        }
      }
    }
  }

  protected synchronized void requestFocusForFileTable() {
    SwingUtilities.invokeLater(new FocusRequester(fileDataTable));
  }

  // ------------------------------------------------------------------------

  private synchronized void performListDirectory(String path) {
    LOGGER.info("performing ls on : " + path);
    /*
     * 
     * // take care here: // now the wait dialog is not used // but for future,
     * when this ControlCenter is not visible // because it's in a hidden tab,
     * the StandardWaitDialog // will crash because it cannot calculate its
     * location
     * 
     * abstractGuiElement = new TStandardWaitDialog(this,
     * TStandardWaitDialog.ACTION_COLLECT);
     * abstractGuiElement.addTAbstractGUIElementListener(this);
     */

    activeWorker = new TGetFileListWorker(con, path);
    activeWorker.addObserver(observer);

    enableCC(false);

    LOGGER.info("execute ls worker");
    observer.execute(activeWorker);
  }

  // ------------------------------------------------------------------------

  private boolean messageForMe(TEvent e) {
    if (e.getCCId() != TEvent.ANY_REGISTERED_RECEIPIENT && (e.getCCId() != -1 && e.getCCId() != ccID)) {
      return false;
    }
    return true;
  }

  // ------------------------------------------------------------------------

  private void go_home() {
    change_dir(getHomeDirectory());
  }

  private String getHomeDirectory() {
    if (homeDirectory == null) {
      if (conData == null) {
        homeDirectory = ".";
      } else if (conData.getPlugin().isLocalConnection()) {
        homeDirectory = System.getProperty("user.home");
      } else {
        if (con != null) {
          homeDirectory = con.getCurrentPath();
          if (homeDirectory == null) {
            homeDirectory = ".";
          }
        } else {
          homeDirectory = ".";
        }
      }
    }
    return homeDirectory;
  }

  private void change_dir(final String dir) {
    String chdir = dir;

    if (activeWorker != null && !activeWorker.isFinished()) {
      return;
    }

    if (dir == null) {
      return;
    }

    if (con != null && con.isConnected()) {
      String lastDir = con.getCurrentPath();
      if (chdir.equals("..")) {
        chdir = con.getPathControlInstance().getPathLevelUp(lastDir);
      }
      try {
        con.chdir(chdir);
        lastPath = currentPath;
        performListDirectory(chdir);
      } catch (IOException ex) {
        LOGGER.severe(ex.getMessage());
        TErrorHandling.reportError(TErrorHandling.ERROR_IO_LS, ex.getMessage());
        // rollback to last working directory
        try {
          con.chdir(lastDir);
          return;
        } catch (IOException ioex) {
          ioex.printStackTrace();
        }
      }
    } else {
      checkConnection();
    }

  }

  // ------------------------------------------------------------------------

  private void mkdirEvent() {

    if (con != null && con.isConnected()) {

      TMkdirDialog mkdirDialog = new TMkdirDialog(parent);
      mkdirDialog.setLocation(TWindowPositioner.getCenteredWindowPoint(mkdirDialog));
      mkdirDialog.setVisible(true);

      if (mkdirDialog.getClosingType() == TMkdirDialog.CANCEL) {
        return;
      }

      String dirName = mkdirDialog.getDirUserEntered();
      try {
        con.mkdir(dirName);
        reloadDir();
      } catch (IOException e) {
        LOGGER.warning("IOException while performing mkdir: " + e.getMessage());
        TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL, e.getMessage());
      }
    } else {
      checkConnection();
    }

  }

  // ------------------------------------------------------------------------

  private void deleteEvent() {

    if (con != null && con.isConnected()) {

      final TFileProperties[] selectedFiles = getSelectedFileData();

      if (selectedFiles == null || selectedFiles.length == 0) {
        return;
      } else {

        String key_start = "dialog.confirm.delete.start.plural";
        String key_end = "dialog.confirm.delete.end.plural";
        if (selectedFiles.length == 1) {
          key_start = "dialog.confirm.delete.start.singular";
          key_end = "dialog.confirm.delete.end.singular";
        }

        int userSelected = JOptionPane.showConfirmDialog(parent, LanguageBundle.getInstance().getMessage(key_start)
            + " " + selectedFiles.length + " " + LanguageBundle.getInstance().getMessage(key_end), LanguageBundle
            .getInstance().getMessage("label.delete"), JOptionPane.YES_NO_OPTION);

        if (userSelected != JOptionPane.YES_OPTION) {
          return;
        }

        LOGGER.info("delete files");

        activeWorker = new DeleteFilesWorker(con, selectedFiles, true);
        activeWorker.addObserver(observer);

        DeleteWorkerPanel deletePanel = new DeleteWorkerPanel();
        deletePanel.addTAbstractGUIElementListener(this);

        // connect panel to worker
        compStore.storeComponent(activeWorker, deletePanel);

        enableCC(false);
        statusPanel.addWorkerPanel(deletePanel);

        LOGGER.info("execute delete worker");
        observer.execute(activeWorker);
      }

    } else {
      checkConnection();
    }
  }

  private void executeAction() {
    int selIdx = actionBox.getSelectedIndex();

    int taskId = actionTasks[selIdx];
    if (taskId == TASK_UNIFY) {
      unify();
    } else {
      LOGGER.warning("Unkown task type : " + taskId);
    }
  }

  private void unify() {
    if (con == null || !con.isConnected()) {
      return;
    }

    LOGGER.info("unify()");
    GWorker worker = new BasicFileTreeBuilder(con, getCurrentPath(), new UnifyFileFactory(con.getCurrentPath(),
        con.getPathControlInstance()));
    executeWorker(worker, "unify", new FileCollectWorkerPanel());
  }

  private void executeWorker(GWorker worker, String taskTag, WorkerPanel guiPanel) {

    worker.addObserver(observer);

    workerStore.registerWorker(worker, taskTag);

    guiPanel.addTAbstractGUIElementListener(this);

    compStore.storeComponent(worker, guiPanel);

    enableCC(false);
    statusPanel.addWorkerPanel(guiPanel);

    LOGGER.info("worker " + worker);
    activeWorker = worker;
    observer.execute(worker);
  }

  // ------------------------------------------------------------------------

  protected void change_permissions() {

    if (con == null || !con.isConnected()) {
      return;
    }

    int rows = fileDataTable.getSelectedRowCount();

    // nothing selected
    if (rows <= 0) {
      return;
    }

    TFileProperties[] selectedFiles = getSelectedFileData();
    if (selectedFiles == null || selectedFiles.length < 1) {
      return;
    }

    int type = PermissionsChooseDialog.TYPE_FILES_AND_DIRS;
    boolean dirFound = false;
    boolean fileFound = false;
    int dirPerm = -1, filePerm = -1;
    for (int i = 0; i < selectedFiles.length; i++) {
      int perm = selectedFiles[i].getAttributes().getPermissions();

      if (selectedFiles[i].isDirectory() && !dirFound) {
        dirFound = true;
        dirPerm = perm;
      } else if (!fileFound) {
        fileFound = true;
        filePerm = perm;
      }

      if (dirFound && fileFound) {
        break;
      }
    }

    if (!dirFound) {
      type = PermissionsChooseDialog.TYPE_FILES_ONLY;
    }

    PermissionsChooseDialog pmd = new PermissionsChooseDialog(this, type);

    if (fileFound) {
      pmd.setFilePermissions(filePerm);
    }

    if (dirFound) {
      pmd.setDirPermissions(dirPerm);
    }

    pmd.setVisible(true);

    int option = pmd.getUserOption();
    if (option == PermissionsChooseDialog.OPTION_PROCEED) {

      abstractGuiElement = new PermissionChangeDisplayDialog(this);
      abstractGuiElement.addTAbstractGUIElementListener(this);

      boolean recursive = pmd.isRecursiveEnabled();

      activeWorker = new PermissionsWorker(this, con, getConnectionDetails(), selectedFiles, pmd.getDirPermissions(),
          pmd.getFilePermissions(), recursive);

      activeWorker.addObserver(observer);

      enableCC(false);

      LOGGER.info("execute permission worker");
      observer.execute(activeWorker);
      abstractGuiElement.enableGUIElement(true);
    }

  }

  // ------------------------------------------------------------------------

  private void renameEvent() {

    if (con != null && con.isConnected()) {

      int rows = fileDataTable.getSelectedRowCount();

      // nothing selected
      if (rows <= 0)
        return;

      if (rows > 1) { // rename more than one file, useful ???
        // TODO renaming more than one file?
        return;
      }

      TFileProperties[] selectedFiles = getSelectedFileData();
      if (selectedFiles == null || selectedFiles.length < 1) {
        return;
      }

      String oldpath = selectedFiles[0].getAbsoluteFileName();
      String oldname = selectedFiles[0].getFileName();
      TFileRenameDialog diag = new TFileRenameDialog(parent, oldname);
      int back = diag.showMe();
      if (back == TFileRenameDialog.CANCEL) {
        return;
      }
      String newname = diag.getFileName();

      if (newname.equals(oldname)) {
        return;
      }

      String basename = con.getPathControlInstance().getPathLevelUp(oldpath);
      String newpath = con.getPathControlInstance().appendDirectory(basename, newname);

      LOGGER.info("rename : " + oldpath + " to " + newpath);

      try {
        con.rename(oldpath, newpath);
        reloadDir();
      } catch (IOException ex) {
        TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL, ex.getMessage());
      }

    } else {
      checkConnection();
    }

  }

  // ------------------------------------------------------------------------

  private void reloadDir() {
    if (con != null && con.isConnected()) {

      String path = getCurrentPath();
      if (path == null) {
        TErrorHandling.reportError(TErrorHandling.ERROR_IO_PATH_UNKNOWN);
        return;
      }

      if (isActive() && fileDataTable != null) {
        isReloadEvent = true;
        if (fileDataTable.getFirstSelectedFileData() != null) {
          lastSelectedFile = fileDataTable.getFirstSelectedFileData().getFileName();
          fileDataTable.saveSelectedRow();
        }
      }

      // performListDirectory(path);
      change_dir(path);

    } else {
      checkConnection();
    }
  }

  // ------------------------------------------------------------------------

  // !TODO! strange code
  // need to be revised
  public void updateCurrentPath() {
    if (con == null) {
      return;
    }

    if (con != null && con.isConnected()) {
      currentPath = con.getCurrentPath();
    } else {
      currentPath = "";
    }
    LOGGER.info("set current path to " + currentPath);
    setCurrentPath(currentPath);
  }

  // ------------------------------------------------------------------------

  public void refreshDataView() {
    change_dir(getCurrentPath());
  }

  protected void updateTableView() {
    if (dataSorter != null && dataSorter.getFileDataCopy() != null) {
      updateFileDataModel(dataSorter.getFileDataCopy());
    }
  }

  // ------------------------------------------------------------------------

  private void startEditor() {
    TFileProperties eFile = fileDataTable.getFirstSelectedFileData();
    if (eFile == null) {
      return;
    }
    new TextViewManager(this).startViewerFor(con, eFile);
  }

  // ------------------------------------------------------------------------

  public AbstractConnectionProxy getConnection() {
    return con;
  }

  public TFileProperties[] getSelectedFileData() {
    return fileDataTable.getSelectedFileData();
  }

  public String getCurrentPath() {
    return currentPath;
  }

  public ConnectionDetails getConnectionDetails() {
    ConnectionDetails cd;
    if (conData != null) {
      cd = new ConnectionDetails(conData.getUser(), conData.getHost(), getCurrentPath());
    } else {
      // cd = new ConnectionDetails("","","");
      cd = null;
    }
    return cd;
  }

  // ------------------------------------------------------------------------

  public synchronized boolean isActive() {
    return active;
  }

  public synchronized void setActive(final boolean a) {
    active = a;
  }

  public int getID() {
    return ccID;
  }

  // -----------------------------------------------------------------------------

  public synchronized void workerDone(GWorkerEvent e) {

    GWorker worker = (GWorker) e.getSource();
    if (workerStore.contains(worker)) {
      final Component c = compStore.getComponent(worker);
      final String task = workerStore.getTaskTag(worker);
      workerStore.unregisterWorker(worker);

      if (c != null) {
        removePanel(worker);
        enableCC(true);
      }

      if (task != null && (task.equals("unify_move") || task.equals("unify_delete"))) {
        requestFocusForFileTable();
      }

      if (worker instanceof BasicFileTreeBuilder) {
        if (task.equals("unify")) {
          final UnifyFileTree tree = (UnifyFileTree) ((BasicFileTreeEvent) e).getBasicFileTree();
          GWorker subWorker = new UnifyEfficientDuplicateSearchWorker(con, tree);
          WorkerPanel panel = new SimpleFileProgressWorkerPanel(LanguageBundle.getInstance().getMessage(
              "worker.compare_hashes"));
          executeWorker(subWorker, "unify", panel);
        }
      }

      if (worker instanceof UnifyEfficientDuplicateSearchWorker) {
        UnifyFileTree tree = (UnifyFileTree) ((BasicFileTreeEvent) e).getBasicFileTree();
        DupExplorer explorer = new DupExplorer(parent, tree);
        DupController controller = new DupController(tree, explorer);
        explorer.setVisible(true);
        if (controller.getUserChoice() == DupControllerEvent.OKAY) {
          final UnifyOption option = controller.getSelectedOption();
          if (option == null) {
            LOGGER.warning("option must not be null");
          } else {
            if (option instanceof UnifyMoveOption) {
              final GWorker subWorker = new UnifyMovePerformer(getConnection(), (UnifyMoveOption) option);
              final WorkerPanel panel = new SimpleFileProgressWorkerPanel(LanguageBundle.getInstance().getMessage(
                  "unify.action.move"));
              executeWorker(subWorker, "unify_move", panel);
            } else if (option instanceof UnifyDeleteOption) {
              final GWorker subWorker = new UnifyDeletePerformer(getConnection(), (UnifyDeleteOption) option);
              final WorkerPanel panel = new SimpleFileProgressWorkerPanel(LanguageBundle.getInstance().getMessage(
                  "unify.action.remove"));
              executeWorker(subWorker, "unify_delete", panel);
            } else {
              LOGGER.warning("unhandled option " + option.getName());
            }
          }
        } else {
          requestFocusForFileTable();
        }
        // reloadDir();
      }

    } else if (e.getSource() instanceof TGetFileListWorker) {
      LOGGER.info("send LsReply");
      TFileData data = ((TFileListEvent) e).getFileData();
      TEventMulticaster.getInstance().fireTEvent(this, ccID, new TLsReplyMessage(data));
    } else if (e.getSource() instanceof DeleteFilesWorker) {
      removePanel((GWorker) e.getSource());
      enableCC(true);
      reloadDir();
    } else if (e.getSource() instanceof PermissionsWorker) {
      if (abstractGuiElement != null) {
        abstractGuiElement.enableGUIElement(false);
      }
      reloadDir();
    }
  }

  public synchronized void workerCancelled(GWorkerEvent e) {
    GWorker worker = (GWorker) e.getSource();
    if (workerStore.contains(worker)) {

      final String taskTag = workerStore.getTaskTag(worker);
      workerStore.unregisterWorker(worker);

      final Component c = compStore.getComponent(worker);
      if (c != null) {
        removePanel(worker);
        enableCC(true);
      }

      if (taskTag != null && (taskTag.startsWith("unify"))) {
        requestFocusForFileTable();
      }

    } else if (e.getSource() instanceof TGetFileListWorker) {
      LOGGER.info("send LsCancelled");
      TEventMulticaster.getInstance().fireTEvent(this, ccID, new LsCancelledMessage());

    } else if (e.getSource() instanceof PermissionsWorker) {
      if (abstractGuiElement != null) {
        abstractGuiElement.enableGUIElement(false);
      }
      reloadDir();

    } else if (e.getSource() instanceof DeleteFilesWorker) {
      removePanel((GWorker) e.getSource());
      enableCC(true);
      reloadDir();
    }

  }

  public synchronized void workerDied(GWorkerEvent e) {

    if (e == null) {
      TErrorHandling.reportError(TErrorHandling.ERROR_ADVICE_ABORT_PROGRAM, "fatal: worker died without error code");
      return;
    }

    Exception ex = ((GWorker) e.getSource()).getException();
    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);

    TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL, ex.getMessage());

    GWorker worker = (GWorker) e.getSource();
    if (workerStore.contains(worker)) {
      workerStore.unregisterWorker(worker);

      Component c = compStore.getComponent(worker);
      if (c != null) {
        removePanel(worker);
        enableCC(true);
      }
    } else if (e.getSource() instanceof TGetFileListWorker) {
      TEventMulticaster.getInstance().fireTEvent(this, ccID, new LsFailedMessage());
    } else if (e.getSource() instanceof DeleteFilesWorker) {
      removePanel((GWorker) e.getSource());
      enableCC(true);
      reloadDir();
    } else if (e.getSource() instanceof PermissionsWorker) {
      if (abstractGuiElement != null) {
        abstractGuiElement.enableGUIElement(false);
      }
      TErrorHandling.reportError(TErrorHandling.ERROR_IO_PERMISSIONS);
      enableCC(true);
    }

    checkConnection();
  }

  public synchronized void updateModel(GWorkerEvent e) {

    if (e.getSource() instanceof GWorker && workerStore.contains((GWorker) e.getSource())) {
      GWorker source = (GWorker) e.getSource();
      String taskTag = workerStore.getTaskTag(source);
      if (taskTag != null) {
        Component c = compStore.getComponent(source);
        if (c instanceof TAbstractWorkerGUIElement) {
          ((TAbstractWorkerGUIElement) c).displayWorkerData(e);
        }
      }
    } else if (e.getSource() instanceof DeleteFilesWorker) {
      TAbstractWorkerGUIElement panel = (TAbstractWorkerGUIElement) compStore.getComponent((GWorker) e.getSource());
      panel.displayWorkerData(e);
    } else {
      if (abstractGuiElement != null) {
        abstractGuiElement.displayWorkerData(e);
      }
    }
  }

  public void cancelClicked(TAbstractDialogEvent e) {
    LOGGER.info("cancel worker");
    if (activeWorker != null) {
      observer.cancel(activeWorker);
    }
  }

  protected void enableCC(boolean enable) {

    LOGGER.info("enable CC : " + enable);

    controlGroup1.setEnable(enable);
//    connectButton.setEnabled(enable);
    fileDataTable.setEnabled(enable);
    if (con != null && con.getPlugin() != null) {
      contextControl.enableComponentsForPluginID(enable, con.getPlugin().getConnectionID());
    } else {
      contextControl.disableAllComponents();
    }

    statusLine.setCancelEnabled(!enable);
  }

  // -----------------------------------------------------------------------------

  protected void removePanel(final GWorker worker) {
    WorkerPanel panel = (WorkerPanel) compStore.getComponent(worker);
    statusPanel.removeWorkerPanel(panel);
    // revalidate();
  }

  public void showBookmarks(BookmarkShowEvent e) {
    String[] bookmarkStrings = MainWin.config.getAllBookmarks();
    if (bookmarkStrings.length > 0) {
      BookmarkPopupMenu popup = new BookmarkPopupMenu(bookmarkStrings);
      popup.addBookmarkActionListener(this);
      popup.show(bookmarkPanel.getParent(), bookmarkPanel.getX(), bookmarkPanel.getY());
    }
  }

  public void bookmarkSelected(String bookmark) {
    if (bookmark == null) {
      return;
    }
    change_dir(bookmark);
  }

  public void bookmarkDelete(String bookmark) {
    if (bookmark != null) {
      String message = LanguageBundle.getInstance().getMessage("bookmark.delete_question");

      final String printable = con.getPathControlInstance().getPrintablePath(bookmark);

      message = message.replaceFirst("%s", printable);
      int opt = JOptionPane.showConfirmDialog(this, message,
          LanguageBundle.getInstance().getMessage("bookmark.delete_title"), JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE);
      if (opt == JOptionPane.YES_OPTION) {
        MainWin.config.removeBookmark(bookmark);
      }
    }
  }

  private void add_bookmark() {
    final String currentPath = getCurrentPath();
    if (currentPath != null) {

      String message = LanguageBundle.getInstance().getMessage("bookmark.add_question");
      final String printable = con.getPathControlInstance().getPrintablePath(currentPath);

      message = message.replaceFirst("%s", printable);
      int opt = JOptionPane.showConfirmDialog(this, message,
          LanguageBundle.getInstance().getMessage("bookmark.add_title"), JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE);
      if (opt == JOptionPane.YES_OPTION) {
        MainWin.config.addBookmark(currentPath);
      }
    }
  }

  // -----------------------------------------------------------------------------

  class CCKeyHandler extends AbstractAction {
    private static final long serialVersionUID = 4123098481188679733L;
    private final String shortcut;

    public CCKeyHandler(String shortcut) {
      this.shortcut = shortcut;
    }

    public void actionPerformed(ActionEvent e) {
      if (shortcut.equals("reload")) {
        reloadDir();
      } else if (shortcut.equals("rename")) {
        renameEvent();
      } else if (shortcut.equals("viewer")) {
        startEditor();
      } else if (shortcut.equals("mkdir")) {
        mkdirEvent();
      } else {
        LOGGER.warning("Unkown shortcut : " + shortcut);
      }
    }
  }

  // -----------------------------------------------------------------------------

  class TTablePopupMenu extends JPopupMenu {
    private static final long serialVersionUID = 1L;
    private JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem(LanguageBundle.getInstance().getMessage(
        "menu.option.show_hidden_files"), TControlCenter.this.showHiddenFiles);

    public TTablePopupMenu(ActionListener listener) {
      JMenuItem item;

      item = new JMenuItem(LanguageBundle.getInstance().getMessage("menu.option.select_all"));
      addMenuItem(item, "select_all", listener);

      item = new JMenuItem(LanguageBundle.getInstance().getMessage("menu.option.edit"));
      addMenuItem(item, "edit", listener);

      item = new JMenuItem(LanguageBundle.getInstance().getMessage("menu.option.rename"));
      addMenuItem(item, "rename", listener);

      addSeparator();

      addMenuItem(checkItem, "hidden", listener);
    }

    private void addMenuItem(JMenuItem item, String actionCommand, ActionListener listener) {
      item.setActionCommand(actionCommand);
      item.addActionListener(listener);
      add(item);
    }

    public void show(Component invoker, int x, int y) {
      if (checkItem != null) {
        checkItem.setSelected(TControlCenter.this.showHiddenFiles);
      }
      super.show(invoker, x, y);
    }

  }

  // -----------------------------------------------------------------------------
  // a small container class to manipulate group properties easily
  // implemented for fast manipulation of preselected object properties
  class TComponentGroup {

    private final java.util.List<JComponent> compList = new ArrayList<JComponent>();

    public TComponentGroup() {

    }

    public void add(final JComponent c) {
      compList.add(c);
    }

    public void setEnable(boolean state) {
      for (JComponent c : compList) {
        c.setEnabled(state);
      }
    }
  }

  // -----------------------------------------------------------------------------

  class TRootChooser extends JComboBox {
    private static final long serialVersionUID = 1L;
    private ActionListener listener;

    public TRootChooser(ActionListener listener, String[] roots) {
      setRoots(roots);
      this.listener = listener;
      this.setActionCommand(FS_ROOT_CHANGED);
    }

    public String getCurrentRoot() {
      return (String) this.getSelectedItem();
    }

    public void reset() {
      this.removeAllItems();
      this.addItem("/");
      this.removeActionListener(listener);
    }

    public void setRoots(String[] roots) {
      int elems = this.getItemCount();
      for (int i = 0; i < roots.length; i++)
        this.addItem(roots[i]);
      for (int i = 0; i < elems; i++)
        this.removeItemAt(0);
    }

    public void setSelectedRoot(String root) {
      this.setSelectedItem(root);
    };

    public void setListenable(boolean b) {
      if (b) {
        this.addActionListener(listener);
      } else {
        this.removeActionListener(listener);
      }
    }

  }

  // -----------------------------------------------------------------------------

  // some helper class
  // with this class the focus is properly set to the TFileDataTable
  class FocusRequester implements Runnable {
    private JTable table;

    FocusRequester(JTable table) {
      this.table = table;
    }

    public void run() {
      table.requestFocus();
    }
  }

  // -----------------------------------------------------------------------------

  class ControlCenterFileTableMouseAdapter extends MouseAdapter {

    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup(e);
      }
    }

    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup(e);
      } else {
        isReloadEvent = false;
        TControlCenterFactory.getControlCenterBus().fireControlCenterBusEvent(
            new ControlActivationEvent(TControlCenter.this, ccID));
      }
    }

    public void mouseClicked(MouseEvent e) {
    }

    private void popup(MouseEvent e) {
      if (!tablePopup.isVisible()) {
        tablePopup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  // -----------------------------------------------------------------------------

  static class StatusPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JPanel workerPanel = new JPanel();

    public StatusPanel() {
      super(true);
      initUI();
    }

    protected void initUI() {
      workerPanel.setLayout(new BoxLayout(workerPanel, BoxLayout.Y_AXIS));
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(workerPanel);
    }

    public synchronized void addWorkerPanel(WorkerPanel panel) {
      workerPanel.add(panel);
      revalidate();
    }

    public synchronized void removeWorkerPanel(WorkerPanel panel) {
      workerPanel.remove(panel);
      revalidate();
    }
  }

}