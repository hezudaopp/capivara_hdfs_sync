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
 * $Id: SyncTreeExplorerFrame.java,v 1.35 2006/08/09 22:18:39 hunold Exp $
 */

package net.sf.jfilesync.sync2.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import net.sf.gnocchi.GWorker;
import net.sf.gnocchi.GWorkerEvent;
import net.sf.gnocchi.GWorkerListener;
import net.sf.gnocchi.GWorkerObserver;
import net.sf.jfilesync.develop.DevelopmentMode;
import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.engine.worker.TAbstractDialogEvent;
import net.sf.jfilesync.engine.worker.TAbstractGUIElementListener;
import net.sf.jfilesync.engine.worker.TAbstractWorkerGUIElement;
import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventListener;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.plugins.net.AbstractConnectionProxy;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.sync2.SyncAction;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncFile;
import net.sf.jfilesync.sync2.SyncOption;
import net.sf.jfilesync.sync2.SyncOptions;
import net.sf.jfilesync.sync2.SyncPerformer2;
import net.sf.jfilesync.sync2.SyncSettingsStore;
import net.sf.jfilesync.sync2.SyncTree;
import net.sf.jfilesync.sync2.SyncOption.Direction;
import net.sf.jfilesync.sync2.diffs.SyncDiffTree;
import net.sf.jfilesync.sync2.event.SyncIgnoreFileMessage;
import net.sf.jfilesync.sync2.event.SyncPluginMessage;
import net.sf.jfilesync.sync2.event.SyncShowOnlyFilesMessage;
import net.sf.jfilesync.sync2.event.SyncTreeViewUpdateMessage;
import net.sf.jfilesync.sync2.plugin.DiffPlugin;
import net.sf.jfilesync.sync2.plugin.SyncPlugin;
import net.sf.jfilesync.sync2.plugin.SyncPluginException;
import net.sf.jfilesync.sync2.plugin.SyncPluginProvider;
import net.sf.jfilesync.sync2.projects.SyncProjectSaver;
import net.sf.jfilesync.sync2.projects.SyncProjectSettings;
import net.sf.jfilesync.sync2.syncer.SyncSettings;
import net.sf.jfilesync.util.TPathControlInterface;

public class SyncTreeExplorerFrame extends JDialog implements ActionListener,
    GWorkerListener, TAbstractGUIElementListener, TEventListener, ItemListener,
    SyncDiffAttributeListener {

  private static final long serialVersionUID = 1L;
  private final JFrame parent;
  // private final int syncID;
  private final JSplitPane splitPane = new JSplitPane();
  private SyncTreeExplorer leftExplorer, rightExplorer;
  private final SyncTree leftTree, rightTree;
  private final SyncDiffTree leftDiffTree, rightDiffTree;
  private final JComboBox syncChoice = new JComboBox();
  private final JComboBox syncViewOptions = new JComboBox();
  private final JComboBox syncDirectionOptions = new JComboBox();

  private final JCheckBox showExcludedFilesBox = new JCheckBox(LanguageBundle
      .getInstance().getMessage("sync.view.showexcluded"));

  private final SyncOptions syncOptions;
  private final AbstractConnectionProxy con1, con2;
  private final ConnectionDetails conDetails1, conDetails2;

  private final SyncSettings settings;

  private final DiffExplanationPanel diffPanel = new DiffExplanationPanel();
  private SyncFile lastShownFile;

  private TAbstractWorkerGUIElement dialog;
  private GWorker currentWorker;
  private final GWorkerObserver observer = new GWorkerObserver();

  private static final String SYNC_COMMAND = "sync";
  private static final String CLOSE_COMMAND = "close";
  private static final String MAXIMIZE_COMMAND = "maximize";

  protected static final String SAVE_PROJECT_CMD = "save_project";

  /* just a flag to know whether a dir reload is necessary */
  //private boolean syncPerformed = false;

  private final static Logger LOGGER = Logger
      .getLogger(SyncTreeExplorerFrame.class.getPackage().getName());

  public SyncTreeExplorerFrame(final JFrame parent, final int syncID,
      final SyncTree leftTree, final SyncDiffTree leftDiffTree,
      final SyncTree rightTree, final SyncDiffTree rightDiffTree,
      final AbstractConnectionProxy con1, final ConnectionDetails conDetails1,
      final AbstractConnectionProxy con2, final ConnectionDetails conDetails2,
      final SyncOptions syncOptions) {
    super(parent, "SyncExplorer", true);
    this.parent = parent;
    // this.syncID = syncID;
    this.leftTree = leftTree;
    this.rightTree = rightTree;
    this.leftDiffTree = leftDiffTree;
    this.rightDiffTree = rightDiffTree;
    this.con1 = con1;
    this.conDetails1 = conDetails1;
    this.con2 = con2;
    this.conDetails2 = conDetails2;
    this.syncOptions = syncOptions;

    settings = SyncSettingsStore.getInstance().getSyncSettings(syncID);
    if (settings == null) {
      TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL,
          "sync settings not found @todo@");
      throw new IllegalArgumentException("sync settings not found");
    }

    registerListeners();
    initUI();
  }

  protected void initUI() {
    leftExplorer = new SyncTreeExplorer(leftTree, leftDiffTree, conDetails1);
    rightExplorer = new SyncTreeExplorer(rightTree, rightDiffTree, conDetails2);

    leftExplorer.addSyncDiffAttributeListener(this);
    rightExplorer.addSyncDiffAttributeListener(this);

    final JPanel mainPane = new JPanel();
    splitPane.setLeftComponent(leftExplorer);
    splitPane.setRightComponent(rightExplorer);
    splitPane.setOneTouchExpandable(true);
    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
    mainPane.add(splitPane);

    final JButton startButton = new JButton(LanguageBundle.getInstance()
        .getMessage("sync.dialog.label.preview"));
    startButton.setActionCommand(SYNC_COMMAND);
    startButton.addActionListener(this);

    final JButton closeButton = new JButton(LanguageBundle.getInstance()
        .getMessage("label.abort"));
    closeButton.setActionCommand(CLOSE_COMMAND);
    closeButton.addActionListener(this);

    final JButton maximizeButton = new JButton(LanguageBundle.getInstance()
        .getMessage("label.maximize"));
    maximizeButton.setActionCommand(MAXIMIZE_COMMAND);
    maximizeButton.addActionListener(this);

    // sync.view_options

    syncChoice.setEditable(false);

    final SyncOption[] opts = syncOptions.getSyncOptions();
    for (int i = 0; i < opts.length; i++) {
      syncChoice.addItem(opts[i].getDescription());
    }
    if (opts.length > 0) {
      updateDirections(opts[0]);
    }
    syncChoice.addItemListener(this);

    syncViewOptions.setEditable(false);
    // syncViewOptions.addActionListener(this);
    syncViewOptions.addItemListener(this);
    syncViewOptions.addItem(LanguageBundle.getInstance().getMessage(
        "sync.explorer.view.all"));
    syncViewOptions.addItem(LanguageBundle.getInstance().getMessage(
        "sync.explorer.view.diff"));

    final JPanel syncChoicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    syncChoicePanel.add(new JLabel(LanguageBundle.getInstance().getMessage(
        "sync.option.choice")));
    syncChoicePanel.add(syncChoice);

    syncChoicePanel.add(new JLabel(LanguageBundle.getInstance().getMessage(
        "sync.option.direction.label")));
    syncChoicePanel.add(syncDirectionOptions);

    syncDirectionOptions.setRenderer(new DirectionListRenderer());

    syncChoicePanel.add(new JLabel(LanguageBundle.getInstance().getMessage(
        "sync.view_options")));
    syncChoicePanel.add(syncViewOptions);
    // syncChoicePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

    final JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
    buttonPane.add(startButton);
    buttonPane.add(maximizeButton);
    buttonPane.add(closeButton);
    // buttonPane.setBorder(BorderFactory.createRaisedBevelBorder());
    buttonPane
        .setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

    final JPanel optionPanel = createOptionButtonPanel();
    optionPanel.setBorder(BorderFactory
        .createEtchedBorder(EtchedBorder.LOWERED));

    final BoxLayout blayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
    getContentPane().setLayout(blayout);
    getContentPane().add(mainPane);
    getContentPane().add(diffPanel);
    getContentPane().add(optionPanel);
    getContentPane().add(syncChoicePanel);
    getContentPane().add(buttonPane);

    /*
     * setIconImage(TImageIconProvider.getImageIconProvider().getImage(
     * TImageIconProvider.FRAME_ICON));
     */

    if (DevelopmentMode.BETA_FEATURES_ON) {
      final SyncExplorerMenuBar menuBar = new SyncExplorerMenuBar();
      menuBar.addActionListener(this);
      setJMenuBar(menuBar);
    }

    pack();
    setLocationRelativeTo(parent);
    splitPane.setDividerLocation(Component.CENTER_ALIGNMENT);
  }

  protected void registerListeners() {
    TEventMulticaster.getInstance().addTEventListener(this,
        TMessage.ID.SYNC_IGNORE_FILE_MESSAGE);

    TEventMulticaster.getInstance().addTEventListener(this,
        TMessage.ID.SYNC_TREE_VIEW_KEEP_FILES_MESSAGE);

    TEventMulticaster.getInstance().addTEventListener(this,
        TMessage.ID.SYNC_UNHIDE_FILE_MESSAGE);

    TEventMulticaster.getInstance().addTEventListener(this,
        TMessage.ID.SYNC_PLUGIN_FILE_MESSAGE);
  }

  public void processEvent(final TEvent e) {
    final TMessage msg = e.getMessage();
    switch (msg.getMessageType()) {
    case SYNC_IGNORE_FILE_MESSAGE:
      final SyncIgnoreFileMessage sim = (SyncIgnoreFileMessage) msg;
      handleIngoreFileMessage(sim);
      break;
    case SYNC_UNHIDE_FILE_MESSAGE:
      handleUnhideFileMessage();
      break;
    case SYNC_TREE_VIEW_KEEP_FILES_MESSAGE:
      final SyncShowOnlyFilesMessage som = (SyncShowOnlyFilesMessage) msg;
      handleShowOnlyMessage(som);
      break;
    case SYNC_PLUGIN_FILE_MESSAGE:
      handleSyncPluginMessage((SyncPluginMessage) msg);
      break;
    default:
      LOGGER.warning("unhandled event " + e.getMessage());
    }
  }

  public void actionPerformed(final ActionEvent e) {
    final String cmd = e.getActionCommand();
    if (cmd.equals(SYNC_COMMAND)) {
      doSync();
    } else if (cmd.equals(CLOSE_COMMAND)) {
      setVisibilitySafely(false);
    } else if (cmd.equals(SAVE_PROJECT_CMD)) {
      saveSyncProject();
    } else if (cmd.equals(MAXIMIZE_COMMAND)) {
      maximizeView();
    } else {
      LOGGER.warning("Unknown command : " + e.getActionCommand());
    }
  }

  public void itemStateChanged(final ItemEvent e) {
    if (e.getSource() == showExcludedFilesBox) {
      LOGGER.info("show excluded ? : " + showExcludedFilesBox.isSelected());
      leftExplorer.showExcludedFiles(showExcludedFilesBox.isSelected());
      rightExplorer.showExcludedFiles(showExcludedFilesBox.isSelected());

    } else if (e.getSource() == syncViewOptions) {
      final int viewOptIdx = syncViewOptions.getSelectedIndex();
      if (viewOptIdx == 0) {
        LOGGER.info("show entire tree");
        leftExplorer.showOnlyDifferences(false);
        rightExplorer.showOnlyDifferences(false);
      } else if (viewOptIdx == 1) {
        LOGGER.info("show diff tree");
        leftExplorer.showOnlyDifferences(true);
        rightExplorer.showOnlyDifferences(true);
      }
    } else if (e.getSource() == syncChoice) {

      final SyncOption syncOption = getSelectedSyncOption();
      if (syncOption == null) {
        LOGGER.severe("BUG: no sync option selected");
      } else {
        updateDirections(syncOption);
      }

    } else {
      LOGGER.warning("unhandled item event source " + e.getSource());
    }
  }

  private JPanel createOptionButtonPanel() {
    final JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));

    final boolean showExcluded = true;
    showExcludedFilesBox.setSelected(showExcluded);
    showExcludedFilesBox.addItemListener(this);
    pane.add(showExcludedFilesBox);
    leftExplorer.showExcludedFiles(showExcluded);
    rightExplorer.showExcludedFiles(showExcluded);

    return pane;
  }

  private void doSync() {

    final SyncOption syncOption = getSelectedSyncOption();

    if (syncOption == null) {

      LOGGER.severe("BUG: sync option could not be determined");

    } else {

      final Direction direction = getSelectedDirection();
      if (!isValidDirection(direction)) {
        return;
      }

      final SyncAction[] l1 = syncOption.getLeftActionList(leftDiffTree,
          direction);
      final SyncAction[] l2 = syncOption.getRightActionList(rightDiffTree,
          direction);

      final SyncActionDialog2 sad = new SyncActionDialog2(this, l1,
          conDetails1, l2, conDetails2);
      sad.setVisible(true);

      final int userOpt = sad.getUserChoice();
      if (userOpt == SyncActionDialog2.ACTION_SYNC) {

        dialog = new SyncProgressDialog2(this);
        dialog.addTAbstractGUIElementListener(this);

        final SyncPerformer2 performer = new SyncPerformer2(con1, l1, leftTree
            .getRootPath(), con2, l2, rightTree.getRootPath());
        performer.setCopyProgressListener((SyncProgressDialog2) dialog);

        currentWorker = performer;
        currentWorker.addObserver(observer);
        observer.addWorkerListener(this);
        observer.execute(currentWorker);
        dialog.enableGUIElement(true);

      }
    }

  }

  private boolean isValidDirection(final Direction direction) {
    boolean valid = true;

    if (direction == Direction.NOOP) {
      valid = false;
      JOptionPane.showMessageDialog(this, LanguageBundle.getInstance()
          .getMessage("sync.message.nodirection"));
    }

    return valid;
  }

  private Direction getSelectedDirection() {
    final DirectionOption dirOption = (DirectionOption) syncDirectionOptions
        .getSelectedItem();
    return dirOption.getDirection();
  }

  private SyncOption getSelectedSyncOption() {
    final String description = (String) syncChoice.getSelectedItem();
    final SyncOption syncOption = syncOptions.getSyncOption(description);
    return syncOption;
  }

  private void updateDirections(final SyncOption syncOption) {
    if (syncOption == null) {
      return;
    }
    syncDirectionOptions.removeAllItems();

    // noop is always first choice
    syncDirectionOptions.addItem(new DirectionOption(Direction.NOOP,
        TImageIconProvider.getInstance().getImageIcon(
            TImageIconProvider.SYNC_DIRECTION_NOOP, 15, 15)));

    for (final Direction direction : syncOption.getSupportedDirections()) {

      int iconID = TImageIconProvider.SYNC_DIRECTION_LEFT;
      if (direction == Direction.BIDIRECTIONAL) {
        iconID = TImageIconProvider.SYNC_DIRECTION_BI;
      } else if (direction == Direction.LEFT_RIGHT) {
        iconID = TImageIconProvider.SYNC_DIRECTION_RIGHT;
      } else if (direction == Direction.RIGHT_LEFT) {
        iconID = TImageIconProvider.SYNC_DIRECTION_LEFT;
      } else {
        LOGGER.warning("unknown direction: " + direction);
      }

      final ImageIcon icon = TImageIconProvider.getInstance().getImageIcon(
          iconID, 40, 15);

      syncDirectionOptions.addItem(new DirectionOption(direction, icon));

      // syncDirectionOptions.addItem(syncOption
      // .getDirectionDescription(direction));
    }
  }

  public void workerDone(final GWorkerEvent e) {
    if (dialog != null) {
      dialog.enableGUIElement(false);
    }
    setVisibilitySafely(false);
  }

  public void workerCancelled(final GWorkerEvent e) {
    if (dialog != null) {
      dialog.enableGUIElement(false);
    }
    setVisibilitySafely(false);
  }

  public void workerDied(final GWorkerEvent e) {
    TErrorHandling.reportError(TErrorHandling.ERROR_SYNC_PERFORMER);
    if (dialog != null) {
      dialog.enableGUIElement(false);
    }
    setVisibilitySafely(false);
  }

  public void updateModel(final GWorkerEvent e) {
    if (dialog != null) {
      dialog.displayWorkerData(e);
    }
  }

  public void cancelClicked(final TAbstractDialogEvent e) {
    if (currentWorker != null) {
      observer.cancel(currentWorker);
    }
  }

  protected void setVisibilitySafely(final boolean visible) {
    if (visible) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setVisible(true);
        }
      });
    } else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setVisible(false);
          dispose();
        }
      });
    }
  }

  private void maximizeView() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setLocation(0, 0);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        splitPane.setDividerLocation(getSize().width / 2);
      }
    });
  }

  protected void handleIngoreFileMessage(final SyncIgnoreFileMessage msg) {
    final String[] fileNames = msg.getPaths();

    if (fileNames == null) {
      return;
    }

    final SyncTree origin = msg.getSyncTree();

    final SyncTree source = (origin == leftTree) ? leftTree : rightTree;
    final SyncTree target = (source == leftTree) ? rightTree : leftTree;

    final String source_root = source.getRootPath();
    final String target_root = target.getRootPath();
    final TPathControlInterface spc = source.getPathControl();
    final TPathControlInterface tpc = target.getPathControl();

    for (int i = 0; i < fileNames.length; i++) {
      final String source_path = fileNames[i];
      final String relPath = spc.plainpath(source_root, source_path);
      final String target_path = tpc.appendDirectory(target_root, relPath);

      LOGGER.info("ignore - source: " + source_path + " , target: "
          + target_path);
      source.setIgnoredFile(source_path);
      target.setIgnoredFile(target_path);
    }

    TEventMulticaster.getInstance().fireTEvent(this, -1,
        new SyncTreeViewUpdateMessage());
  }

  protected void handleUnhideFileMessage() {
    LOGGER.info("unhide trees");
    leftTree.unhide();
    rightTree.unhide();
    TEventMulticaster.getInstance().fireTEvent(this, -1,
        new SyncTreeViewUpdateMessage());
  }

  protected void handleShowOnlyMessage(final SyncShowOnlyFilesMessage som) {
    final String[] fileNames = som.getFilesToKeep();

    if (fileNames == null) {
      return;
    }

    final SyncTree origin = som.getSyncTree();

    final SyncTree source = (origin == leftTree) ? leftTree : rightTree;
    final SyncTree target = (source == leftTree) ? rightTree : leftTree;

    final String source_root = source.getRootPath();
    final String target_root = target.getRootPath();
    final TPathControlInterface spc = source.getPathControl();
    final TPathControlInterface tpc = target.getPathControl();

    final String[] targetFileNames = new String[fileNames.length];
    for (int i = 0; i < fileNames.length; i++) {
      final String source_path = fileNames[i];
      final String relPath = spc.plainpath(source_root, source_path);
      final String target_path = tpc.appendDirectory(target_root, relPath);
      targetFileNames[i] = target_path;
    }

    source.showOnly(fileNames);
    target.showOnly(targetFileNames);

    TEventMulticaster.getInstance().fireTEvent(this, -1,
        new SyncTreeViewUpdateMessage());
  }

  private void handleSyncPluginMessage(final SyncPluginMessage msg) {

    SyncPlugin plugin = null;

    final SyncPluginProvider provider = new SyncPluginProvider();
    int sourceConId;
    int targetConId;

    final SyncTree origin = msg.getSyncTree();

    SyncTree targetTree = null;
    AbstractConnectionProxy targetCon = null;

    if (origin == leftTree) {
      targetTree = rightTree;
      targetCon = con2;
      sourceConId = provider.registerConnection(con1);
      targetConId = provider.registerConnection(con2);
    } else {
      targetTree = leftTree;
      targetCon = con1;
      sourceConId = provider.registerConnection(con2);
      targetConId = provider.registerConnection(con1);
    }

    /*
     * try to locate source file name in other tree if there is no such file
     * then we cannot compare anything
     *
     */
    final String file2Path = targetCon.getPathControlInstance()
        .appendDirectory(targetTree.getRootPath(),
            msg.getFile().getRelativePath());

    final SyncFile file1 = msg.getFile();
    final SyncFile file2 = targetTree.findSyncFile(file2Path);
    
    if (file2 == null) {
      LOGGER.warning("no corresponing file : " + file2Path);
      return;
    }

    LOGGER.info("sync file1: " + file1.getFile().getAbsoluteFileName());
    LOGGER.info("sync file2: " + file2.getFile().getAbsoluteFileName());

    if (msg.getPluginId().equals("diff")) {

      plugin = new DiffPlugin(provider);

    } else {
      LOGGER.warning("unhandled plugin message " + msg.getPluginId());
    }

    if (plugin != null) {
      try {
        plugin.compare(sourceConId, file1, targetConId, file2);
      } catch (final SyncPluginException e) {
        TErrorHandling.reportError(TErrorHandling.ERROR_IO_GENERAL, e
            .getMessage());
        e.printStackTrace();
      } 
    }

  }

  public void showSyncAttribute(final SyncDiffAttributeEvent e) {

    if (e == null) {
      return;
    }

    final SyncFile file = e.getSyncFile();
    if (file == lastShownFile) {
      return;
    } else {

      lastShownFile = file;

      final SyncDiff leftDiff = leftDiffTree.getSyncDiffCollection()
          .getSyncDiff(file);
      final SyncDiff rightDiff = rightDiffTree.getSyncDiffCollection()
          .getSyncDiff(file);

      if (leftDiff == null && rightDiff == null) {
        diffPanel.clear();
      } else if (leftDiff == null && rightDiff != null) {
        diffPanel.showMessage(rightDiff.getLongDescription(), rightDiff
            .getForeground());
      } else if (rightDiff == null && leftDiff != null) {
        diffPanel.showMessage(leftDiff.getLongDescription(), leftDiff
            .getForeground());
      } else {
        LOGGER.warning("both files cannot have been hovered at the same time");
      }

    }

  }

  protected void saveSyncProject() {
    LOGGER.info("Saving project..");

    final SyncProjectSettings projectSettings = new SyncProjectSettings(
        settings, con1, leftTree.getRootPath(), con2, rightTree.getRootPath());

    final SyncProjectSaver saver = new SyncProjectSaver(projectSettings);

    try {
      saver.saveProject();
    } catch (final IOException e) {
      // @TODO@ error handling
      e.printStackTrace();
    }

  }

  static class SyncExplorerMenuBar extends JMenuBar {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final java.util.List<JMenuItem> menuEntries = new ArrayList<JMenuItem>();

    public SyncExplorerMenuBar() {

      final JMenu projectMenu = new JMenu(LanguageBundle.getInstance()
          .getMessage("label.sync_project"));

      final JMenuItem saveProjectMenuItem = new JMenuItem(LanguageBundle
          .getInstance().getMessage("label.save"));
      projectMenu.add(saveProjectMenuItem);
      addMenuItem(saveProjectMenuItem, SyncTreeExplorerFrame.SAVE_PROJECT_CMD);

      final JMenu mainMenu = new JMenu("@Main@");
      final JMenuItem ignoredMenuItem = new JMenuItem("@ignored");
      mainMenu.add(ignoredMenuItem);

      add(mainMenu);
      add(projectMenu);

    }

    private void addMenuItem(final JMenuItem menuItem, final String command) {
      menuEntries.add(menuItem);
      menuItem.setActionCommand(command);
    }

    public void addActionListener(final ActionListener l) {
      for (final JMenuItem mI : menuEntries) {
        mI.addActionListener(l);
      }
    }

  }

  static class DiffExplanationPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public JTextField textField = new JTextField(80);

    public DiffExplanationPanel() {
      initUI();
    }

    private void initUI() {
      textField.setEditable(false);
      textField.setFocusable(false);

      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(textField);
    }

    public void showMessage(final String message, final Color color) {
      textField.setForeground(color);
      textField.setText(message);
    }

    public void clear() {
      textField.setText("");
    }

  }

  static class DirectionOption {

    private final Direction direction;
    private final ImageIcon icon;

    public DirectionOption(final Direction direction, final ImageIcon icon) {
      super();
      this.direction = direction;
      this.icon = icon;
    }

    public Direction getDirection() {
      return direction;
    }

    public ImageIcon getIcon() {
      return icon;
    }

  }

  static class DirectionListRenderer extends DefaultListCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(final JList list,
        final Object value, final int index, final boolean isSelected,
        final boolean cellHasFocus) {

      final DirectionOption option = (DirectionOption) value;

      return super.getListCellRendererComponent(list, option.getIcon(), index,
          isSelected, cellHasFocus);
    }

  }

}
