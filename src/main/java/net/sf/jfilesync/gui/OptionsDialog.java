/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed 2004 by Sascha Hunold <hunoldinho@users.sourceforge.net>
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
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.types.TSettingsChangedMessage;
import net.sf.jfilesync.gui.components.PermissionPanel;
import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.ConfigDefinitions;
import net.sf.jfilesync.settings.ConfigFileHandler;
import net.sf.jfilesync.settings.ConfigWriter;
import net.sf.jfilesync.settings.MasterPasswordHandler;
import net.sf.jfilesync.settings.PasswordStoreException;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;

public class OptionsDialog extends JDialog implements TreeSelectionListener, ActionListener {

  private static final long serialVersionUID = 1L;

  private final JFrame parent;
  private DefaultMutableTreeNode topNode;
  private CardLayout mainLayout;
  private JTree optionTree;

  // size of configuration tree on left hand side
  private final Dimension treeDim = new Dimension(160, 250);

  protected java.util.List<OptionPanel> optPanelList = new ArrayList<OptionPanel>();
  protected JPanel mainPanel;

  private final static Logger LOGGER = Logger.getLogger(OptionsDialog.class.getName());

  public OptionsDialog(final JFrame parent) {
    super(parent, "Capivara settings", true);
    this.parent = parent;
    initUI();
    loadConfig();
  }

  protected void initUI() {
    mainPanel = new JPanel();
    mainLayout = new CardLayout();

    final JButton applyButton = new JButton(LanguageBundle.getInstance().getMessage("label.apply"));
    final JButton cancelButton = new JButton(LanguageBundle.getInstance().getMessage("label.cancel"));

    // generate Tree View
    // create the nodes
    topNode = new DefaultMutableTreeNode(new NodeInfo("main", "0"));
    createNodes(topNode);

    // create a tree that allows one selection at a time.
    optionTree = new JTree(topNode);
    optionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    optionTree.addTreeSelectionListener(this);
    optionTree.setCellRenderer(new OptionsTreeRenderer());
    optionTree.setVisibleRowCount(1);
    optionTree.setShowsRootHandles(true);
    optionTree.setRootVisible(false);
    

    // create the scroll pane and add the tree to it.
    final JScrollPane treeView = new JScrollPane(optionTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    treeView.setSize(treeDim);
    treeView.setPreferredSize(treeDim);
    treeView.setMinimumSize(treeDim);

    ToolTipManager.sharedInstance().registerComponent(optionTree);

    // button panel
    applyButton.setActionCommand("apply");
    applyButton.addActionListener(this);
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(this);

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(applyButton);
    buttonPanel.add(cancelButton);
    buttonPanel.setEnabled(true);

    // the main Panel
    mainPanel.setLayout(mainLayout);
    mainPanel.setBorder(new EmptyBorder(0, 5, 0, 0));

    // setting up the Option Panels
    // emptyPanel !!!!
    final OptionPanel p1 = new OptionPanelAdapter();
    /*
     * p1.add(new JLabel( LanguageBundle.getInstance().getMessage(
     * "dialog.options.no_settings_available")), BorderLayout.CENTER) ;
     */
    mainPanel.add(p1, "0");
    optPanelList.add(p1);

    // Language panel
    final OptionPanel p2 = new LanguagePanel();
    mainPanel.add(p2, "lang");
    optPanelList.add(p2);

    // File Editor Panel
    final OptionPanel p3 = new EditorPanel();
    mainPanel.add(p3, "editor");
    optPanelList.add(p3);

    // Look&Feel Panel
    final OptionPanel p4 = new LFSuperPanel();
    mainPanel.add(p4, "look");
    optPanelList.add(p4);

    // FileTable Panel
    // final OptionPanel p5 = new FileTablePanel();
    final OptionPanel p5 = new FileTableSuperPanel();
    mainPanel.add(p5, "ftable");
    optPanelList.add(p5);

    final OptionPanel fileAttrPanel = new FileAttributesPanel();
    mainPanel.add(fileAttrPanel, "fileattr");
    optPanelList.add(fileAttrPanel);

    final OptionPanel keyPanel = new KeyStrokeOptionPanel();
    mainPanel.add(keyPanel, "keylayout");
    optPanelList.add(keyPanel);

    final ConnectionPluginPanel p6 = new ConnectionPluginPanel();
    mainPanel.add(p6, "conplugins");
    // mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    final GeneralOptionsPanel generalPanel = new GeneralOptionsPanel();
    mainPanel.add(generalPanel, "general");
    optPanelList.add(generalPanel);

    final JPanel content = new JPanel(new BorderLayout(12, 12));
    content.setBorder(new EmptyBorder(12, 12, 0, 12));
    setContentPane(content);

    final JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, mainPanel);
    splitter.setBorder(new EmptyBorder(0, 0, 0, 0));

    content.add(buttonPanel, BorderLayout.SOUTH);
    content.add(splitter, BorderLayout.CENTER);

    // select the first node by default
    // looks better than showing an empty panel
    if (optionTree.getRowCount() > 0) {
      optionTree.setSelectionRow(0);
    }

    pack();
    setResizable(false);
    setLocationRelativeTo(getParent());
  }

  private void loadConfig() {
    for (final OptionPanel panel : optPanelList) {
      panel.loadConfig();
    }
  }

  private void createNodes(final DefaultMutableTreeNode top) {
    DefaultMutableTreeNode category = null;
    DefaultMutableTreeNode lNode = null;

    category = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance()
        .getMessage("dialog.section.general"), "general", TImageIconProvider.OPTION_GUI));
    top.add(category);

    category = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance().getMessage(
        "dialog.section.explorer"), "ftable", TImageIconProvider.OPTION_FILETABLE));
    top.add(category);

    lNode = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance().getMessage("dialog.section.fileattr"),
        "fileattr", TImageIconProvider.OPTION_FILETABLE));
    top.add(lNode);

    lNode = new DefaultMutableTreeNode(new NodeInfo(
        LanguageBundle.getInstance().getMessage("dialog.section.keylayout"), "keylayout",
        TImageIconProvider.OPTION_KEYBOARD));
    top.add(lNode);

    lNode = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance().getMessage("dialog.section.editor"),
        "editor", TImageIconProvider.OPTION_FILEEDIT));
    top.add(lNode);

//    category = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance().getMessage("dialog.section.gui"),
//        "0", TImageIconProvider.OPTION_GUI));
//    top.add(category);

    lNode = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance().getMessage("dialog.section.lookfeel"),
        "look", TImageIconProvider.OPTION_LOOKFEEL));
    top.add(lNode);

//    lNode = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance().getMessage("dialog.section.themes"),
//        "theme", TImageIconProvider.OPTION_LOOKFEEL));
//    category.add(lNode);

    // --------------------------------
    category = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance().getMessage(
        "dialog.section.language"), "lang", TImageIconProvider.OPTION_LANGUAGE));
    top.add(category);

//    // --------------------------------
//    category = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance()
//        .getMessage("dialog.section.plugins"), "0", TImageIconProvider.OPTION_PLUGINS));
//    top.add(category);

    // dynamic generated list ???
    lNode = new DefaultMutableTreeNode(new NodeInfo(LanguageBundle.getInstance().getMessage("option.conplugins"),
        "conplugins", TImageIconProvider.OPTION_CON_PLUGINS));
    top.add(lNode);

  }

  // --------------- TreeSelectionListener -------------------------------------
  public void valueChanged(final TreeSelectionEvent e) {
    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) optionTree.getLastSelectedPathComponent();

    if (node == null) {
      return;
    }

    final NodeInfo lNode = (NodeInfo) node.getUserObject();
    mainLayout.show(mainPanel, lNode.tag);
  }

  // ---------------------------------------------------------------------------

  public void actionPerformed(final ActionEvent e) {
    final String command = e.getActionCommand();
    if (command.equals("apply")) {
      final int opt = JOptionPane.showConfirmDialog(this,
          LanguageBundle.getInstance().getMessage("dialog.confirm.settings.apply"), "label.apply",
          JOptionPane.YES_NO_OPTION);

      if (opt == JOptionPane.NO_OPTION) {
        return;
      }
      if (opt == JOptionPane.YES_OPTION) {
        applySettings();
      }

      setVisible(false);
      dispose();
    } else if (command.equals("cancel")) {
      setVisible(false);
      dispose();
    }
  }

  public void applySettings() {
    // check all option panels for changes
    // right now only optPanel[1] works (languages)
    boolean needToSend = false;

    for (final OptionPanel panel : optPanelList) {
      if (panel.hasChanged()) {
        panel.applyChanges();
        // in case something has changed
        // send a notification to registered listeners
        needToSend = true;
      }
    }

    if (needToSend) {
      final TSettingsChangedMessage msg = new TSettingsChangedMessage();
      final TEventMulticaster emc = TEventMulticaster.getInstance();
      emc.fireTEvent(this, -1, msg);
      // let's save settings
      new ConfigWriter().writeConfig(MainWin.config, ConfigFileHandler.getInstance().getConfigFileLocation());
    }
  }

  // ---------------------------------------------------------------------------
  private class NodeInfo {
    public String name;

    public String tag; // the shortcut of the optPanel

    public int iconNumb;

    public NodeInfo(final String nodeName, final String nodeTag) {
      name = nodeName;
      tag = nodeTag;
      iconNumb = -1;
    }

    public NodeInfo(final String nodeName, final String nodeTag, final int iNum) {
      name = nodeName;
      tag = nodeTag;
      iconNumb = iNum;
    }

    public String toString() {
      return name;
    }
  }

  // ---------------------------------------------------------------------------

  class OptionsTreeRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    public OptionsTreeRenderer() {
    }

    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel,
        final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

      if (node.getUserObject() instanceof NodeInfo) {
        final NodeInfo nodeInfo = (NodeInfo) (node.getUserObject());

        if (nodeInfo.iconNumb != -1) {
          final ImageIcon ic = TImageIconProvider.getInstance().getImageIcon(nodeInfo.iconNumb);
          if (ic != null) {
            setIcon(ic);
          }
        }
      } else {
        LOGGER.severe("tree object is not a NodeInfo object");
      }

      return this;
    }
  }

  // ---------------------------------------------------------------------------

  private abstract class OptionPanel extends JPanel {

    public OptionPanel() {
      final String borderTitle = getPanelBorderTitle();
      setBorder(BorderFactory.createTitledBorder(borderTitle));
    }

    /**
     * panel title
     */
    public abstract String getPanelBorderTitle();

    /**
     * update global application properties
     */
    public abstract void applyChanges();

    /**
     * did properties change in this panel?
     */
    public abstract boolean hasChanged();

    /**
     * loads values from config
     */
    public abstract void loadConfig();
  }

  class OptionPanelAdapter extends OptionPanel {

    private static final long serialVersionUID = -1023283161426010157L;

    public String getPanelBorderTitle() {
      return "";
    }

    public void applyChanges() {
    }

    public boolean hasChanged() {
      return true;
    }

    public void loadConfig() {
    }
  }

  class LanguagePanel extends OptionPanel {

    private static final long serialVersionUID = 1L;
    private final java.util.List<String> languages = LanguageBundle.getInstance().getAvailableLanguages();
    String userLang = null;
    private final JRadioButton[] buttons;
    private final HashMap<String, JRadioButton> buttonHash = new HashMap<String, JRadioButton>();

    public LanguagePanel() {
      super();
      final ButtonGroup buttonGroup = new ButtonGroup();

      final JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

      buttons = new JRadioButton[languages.size()];
      for (int t = 0; t < languages.size(); t++) {
        final String langkey = (String) languages.get(t);
        buttons[t] = new JRadioButton(LanguageBundle.getInstance().getLanguageDescription(langkey));
        buttonHash.put(langkey, buttons[t]);
        buttonPanel.add(buttons[t]);
        buttonGroup.add(buttons[t]);
      }

      setLayout(new FlowLayout(FlowLayout.LEFT));
      // JScrollPane mainPane = new JScrollPane(buttonPanel);
      // add(mainPane);
      add(buttonPanel);
    }

    public void loadConfig() {
      userLang = LanguageBundle.getDefaultLanguageKey();
      try {
        userLang = MainWin.config.getProgramSettings().getStringOption(TProgramSettings.OPTION_USER_LANG);
      } catch (final SettingsTypeException sex) {
        LOGGER.log(Level.WARNING, sex.getMessage());
      }

      // // for compat reasons
      // if (!LanguageBundle.getInstance().getAvailableLanguages().contains(
      // userLang)) {
      // userLang = LanguageBundle.getDefaultLanguageKey();
      // }

      LOGGER.info("userLang : " + userLang);

      final JRadioButton radioButton = (JRadioButton) buttonHash.get(userLang);
      if (radioButton != null) {
        radioButton.setSelected(true);
      }
    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.options.languagepanel.title");
    }

    public void applyChanges() {
      if (hasChanged()) {
        final String selLangKey = getSelectedLangKey();
        if (selLangKey != null) {
          try {
            MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_USER_LANG, selLangKey);
          } catch (final SettingsTypeException sex) {
            LOGGER.log(Level.WARNING, sex.getMessage());
          }
        } else {
          LOGGER.severe("select language key is null");
        }
      }
    }

    public boolean hasChanged() {
      boolean changed = false;
      final String selectedKey = getSelectedLangKey();
      if (selectedKey != null && userLang != selectedKey) {
        changed = true;
      }
      return changed;
    }

    private String getSelectedLangKey() {
      String retLangKey = null;
      for (int i = 0; buttons != null && i < buttons.length; i++) {
        if (buttons[i].isSelected()) {
          for (final String currentLangKey : buttonHash.keySet()) {
            if (buttonHash.get(currentLangKey).isSelected()) {
              retLangKey = currentLangKey;
              break;
            }
          }
        }
      }
      return retLangKey;
    }

  }

  // ---------------------------------------------------------------------------
  class EditorPanel extends OptionPanel implements ChangeListener {

    private static final long serialVersionUID = 8179159857194246219L;
    private final JCheckBox askLargerBox;
    private final JTextField sizeField;
    private final JLabel label1;

    public EditorPanel() {
      super();
      askLargerBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
          "dialog.options.editorpanel.ask_if_remote_larger_then"));
      askLargerBox.addChangeListener(this);

      sizeField = new JTextField(6);
      label1 = new JLabel("kB");

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      final JPanel pane1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pane1.add(askLargerBox);
      pane1.add(sizeField);
      pane1.add(label1);
      pane1.setBorder(BorderFactory.createEtchedBorder());

      this.add(pane1);
    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.options.editorpanel.title");
    }

    public void loadConfig() {
      try {
        askLargerBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_CONFIRM_EDITOR_OPEN));
        sizeField.setText(new Integer(MainWin.config.getProgramSettings().getIntegerOption(
            TProgramSettings.OPTION_CONFIRM_EDITOR_SIZE)).toString());
      } catch (final SettingsTypeException ste) {
        LOGGER.log(Level.WARNING, ste.getMessage());
      }
    }

    public void applyChanges() {
      try {
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_CONFIRM_EDITOR_OPEN,
            new Boolean(askLargerBox.isSelected()).toString());
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_CONFIRM_EDITOR_SIZE,
            sizeField.getText());
      } catch (final SettingsTypeException ste) {
        LOGGER.log(Level.WARNING, ste.getMessage());
      }
    }

    public boolean hasChanged() {
      boolean back = false;
      try {
        final int savedSize = MainWin.config.getProgramSettings().getIntegerOption(
            TProgramSettings.OPTION_CONFIRM_EDITOR_SIZE);
        try {
          final int newSize = Integer.parseInt(sizeField.getText());
          if (newSize != savedSize) {
            back = true;
          }

          if (askLargerBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
              TProgramSettings.OPTION_CONFIRM_EDITOR_OPEN)) {
            back = true;
          }
        } catch (final NumberFormatException nfe) {
          LOGGER.log(Level.WARNING, nfe.getMessage());
        }
      } catch (final SettingsTypeException ste) {
        LOGGER.log(Level.WARNING, ste.getMessage());
      }
      return back;
    }

    public void stateChanged(final ChangeEvent e) {
      final Object obj = e.getSource();

      if (obj == askLargerBox) {
        final boolean state = askLargerBox.isSelected();
        sizeField.setEnabled(state);
        label1.setEnabled(state);
      }
    }
  }

  /*
   * @author sascha
   * 
   * Panel contains 2 the other OptionPanels LookAndFeelPanel and
   * FileTableStylePanel
   */
  class LFSuperPanel extends OptionPanel {

    private static final long serialVersionUID = 814562226188527601L;
    private final LookFeelPanel p1 = new LookFeelPanel();
    private final FileTableStylePanel p2 = new FileTableStylePanel();

    public LFSuperPanel() {

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      final JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      p1.setAlignmentX(Component.LEFT_ALIGNMENT);
      p2.setAlignmentX(Component.LEFT_ALIGNMENT);
      mainPanel.add(p2);
      mainPanel.add(p1);
      mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      add(mainPanel);
    }

    public void applyChanges() {
      if (p1.hasChanged()) {
        p1.applyChanges();
      }

      if (p2.hasChanged()) {
        p2.applyChanges();
      }
    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.section.lookfeel");
    }

    public boolean hasChanged() {
      return (p1.hasChanged() || p2.hasChanged());
    }

    public void loadConfig() {
      p1.loadConfig();
      p2.loadConfig();
    }
  }

  class FileTableSuperPanel extends OptionPanel {

    private static final long serialVersionUID = 1L;
    private final FileTablePanel p1 = new FileTablePanel();
    private final FileTableInitPanel p2 = new FileTableInitPanel();

    public FileTableSuperPanel() {

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      final JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      p1.setAlignmentX(Component.LEFT_ALIGNMENT);
      p2.setAlignmentX(Component.LEFT_ALIGNMENT);
      mainPanel.add(p1);
      mainPanel.add(p2);
      mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      add(mainPanel);
    }

    public void applyChanges() {
      if (p1.hasChanged()) {
        p1.applyChanges();
      }

      if (p2.hasChanged()) {
        p2.applyChanges();
      }
    }

    public String getPanelBorderTitle() {
      // return "@todo@ file table";
      return null;
    }

    public boolean hasChanged() {
      return (p1.hasChanged() || p2.hasChanged());
    }

    public void loadConfig() {
      p1.loadConfig();
      p2.loadConfig();
    }
  }

  // ---------------------------------------------------------------------------

  class FileTableStylePanel extends OptionPanel {

    private static final long serialVersionUID = 6791947890109730351L;
    private final JRadioButton but1;
    private final JRadioButton but2;

    public FileTableStylePanel() {
      setLayout(new FlowLayout(FlowLayout.LEFT));

      final ButtonGroup group = new ButtonGroup();
      but1 = new JRadioButton("KDE");
      but2 = new JRadioButton("Windows");
      group.add(but1);
      group.add(but2);

      final JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
      buttonPanel.add(but1);
      buttonPanel.add(but2);

      add(buttonPanel);
    }

    public void applyChanges() {
      try {
        int style = ConfigDefinitions.DEFAULT_STYLE;

        if (but1.isSelected()) {
          style = ConfigDefinitions.STYLE_KDE;
        } else if (but2.isSelected()) {
          style = ConfigDefinitions.STYLE_WINDOWS;
        }

        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_TABLE_STYLE,
            new Integer(style).toString());
      } catch (final SettingsTypeException sex) {
        LOGGER.log(Level.WARNING, sex.getMessage());
      }
    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.option.explorer.title");
    }

    public boolean hasChanged() {
      try {
        final int style = MainWin.config.getProgramSettings().getIntegerOption(TProgramSettings.OPTION_TABLE_STYLE);
        switch (style) {
        case ConfigDefinitions.STYLE_KDE:
          if (!but1.isSelected()) {
            return true;
          }
          break;
        case ConfigDefinitions.STYLE_WINDOWS:
          if (!but2.isSelected()) {
            return true;
          }
          break;
        }
      } catch (final SettingsTypeException sex) {
        LOGGER.log(Level.WARNING, sex.getMessage());
      }
      return false;
    }

    public void loadConfig() {
      try {
        final int style = MainWin.config.getProgramSettings().getIntegerOption(TProgramSettings.OPTION_TABLE_STYLE);
        switch (style) {
        case ConfigDefinitions.STYLE_KDE:
          but1.setSelected(true);
          break;
        case ConfigDefinitions.STYLE_WINDOWS:
          but2.setSelected(true);
          break;
        }
      } catch (final SettingsTypeException sex) {
        LOGGER.log(Level.WARNING, sex.getMessage());
      }
    }
  }

  // ---------------------------------------------------------------------------
  class LookFeelPanel extends OptionPanel {

    private static final long serialVersionUID = 7974899107785684033L;
    private final ButtonGroup group;
    private JRadioButton[] radios;
    private final LookAndFeelInfo[] lookAndFeels;
    private String currentLookAndFeelClassName;

    public LookFeelPanel() {
      setLayout(new FlowLayout(FlowLayout.LEFT));

      group = new ButtonGroup();

      currentLookAndFeelClassName = UIManager.getLookAndFeel().getClass().getName();

      lookAndFeels = UIManager.getInstalledLookAndFeels();
      if (lookAndFeels != null) {
        radios = new JRadioButton[lookAndFeels.length];
        for (int i = 0; i < lookAndFeels.length; i++) {
          radios[i] = new JRadioButton(lookAndFeels[i].getName());
          if (currentLookAndFeelClassName.equals(lookAndFeels[i].getClassName())) {
            radios[i].setSelected(true);
          }
          group.add(radios[i]);
        }
      }

      final JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
      for (int i = 0; radios != null && i < radios.length; i++) {
        buttonPanel.add(radios[i]);
      }

      add(buttonPanel);

    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.options.lookfeelpanel.title");
    }

    private final LookAndFeelInfo getSelectedLookAndFeelInfo() {
      LookAndFeelInfo selected = null;

      for (int i = 0; lookAndFeels != null && i < lookAndFeels.length; i++) {
        if (radios[i].isSelected()) {
          selected = lookAndFeels[i];
          break;
        }
      }
      return selected;
    }

    public void loadConfig() {

      String lfClassName = null;
      try {
        lfClassName = MainWin.config.getProgramSettings().getStringOption(TProgramSettings.OPTION_LOOK_AND_FEEL_CLASS);
      } catch (final SettingsTypeException e) {
        LOGGER.warning(e.getMessage());
      }

      if (lfClassName != null) {
        currentLookAndFeelClassName = lfClassName;
      }

    }

    public void applyChanges() {
      final LookAndFeelInfo selected = getSelectedLookAndFeelInfo();
      if (selected != null) {
        try {
          final String newLookAndFeelClassName = selected.getClassName();
          UIManager.setLookAndFeel(newLookAndFeelClassName);
          SwingUtilities.updateComponentTreeUI(parent);
          // if that works we save settings

          MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_LOOK_AND_FEEL_CLASS,
              newLookAndFeelClassName);

        } catch (final UnsupportedLookAndFeelException e) {
          LOGGER.warning(e.getMessage());
        } catch (final IllegalAccessException e) {
          LOGGER.warning(e.getMessage());
        } catch (final ClassNotFoundException e) {
          LOGGER.warning(e.getMessage());
        } catch (final InstantiationException e) {
          LOGGER.warning(e.getMessage());
        } catch (final SettingsTypeException e) {
          LOGGER.warning(e.getMessage());
        }
      }
    }

    public boolean hasChanged() {
      boolean changed = false;

      final LookAndFeelInfo selected = getSelectedLookAndFeelInfo();
      if (!selected.getClassName().equals(currentLookAndFeelClassName.getClass().getName())) {
        changed = true;
      }

      return changed;
    }

  } // of LookFeelPanel

  // ---------------------------------------------------------------------------

  class FileTablePanel extends OptionPanel {

    private static final long serialVersionUID = 4848374519424536489L;

    private final JCheckBox doubleClickBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.options.filetablepanel.double_click_opens_directory"));;

    private final JCheckBox hideFileBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "menu.option.show_hidden_files"));

    private final JCheckBox copyDialogAutocloseBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "menu.option.copy_autoclose"));

    private final JCheckBox usePassWDConfirmDialog = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "menu.option.use_pwdConfirm"));
    private final JCheckBox guruCheckBox = new JCheckBox(LanguageBundle.getInstance().getMessage("dialog.options.guru"));
    private final JCheckBox fileSizeBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.options.filesize_in_bytes"));
    private final JCheckBox followSymLinksCopyBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "copy.option.follow_symlinks"));
    private final JCheckBox followSymLinksSyncBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "sync.copy.follow_symlinks"));
    private final JCheckBox caseInsensitiveBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.options.filetablepanel.case_insensitive"));
    private final JTextField tabLengthField = new JTextField(3);
    private final JTextField tabFontSizeField = new JTextField(3);

    public FileTablePanel() {
      super();

      final JLabel tabLengthLabel = new JLabel(LanguageBundle.getInstance().getMessage(
          "dialog.options.tab_title_length"));
      final JPanel tabLengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      tabLengthPanel.add(tabLengthLabel);
      tabLengthPanel.add(tabLengthField);

      final JLabel tabFontSizeLabel = new JLabel(LanguageBundle.getInstance().getMessage(
          "dialog.options.filetablepanel.fontsize"));
      // final JPanel tabFontSizePanel = new JPanel(
      // new FlowLayout(FlowLayout.LEFT));
      tabLengthPanel.add(tabFontSizeLabel);
      tabLengthPanel.add(tabFontSizeField);

      final BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
      setLayout(layout);

      addComponent(doubleClickBox);
      addComponent(hideFileBox);
      addComponent(copyDialogAutocloseBox);
      addComponent(usePassWDConfirmDialog);
      addComponent(guruCheckBox);
      addComponent(fileSizeBox);
      addComponent(caseInsensitiveBox);
      // addComponent(fatBox);
      addComponent(followSymLinksCopyBox);
      addComponent(followSymLinksSyncBox);
      addComponent(tabLengthPanel);
      // addComponent(tabFontSizePanel);
    }

    private void addComponent(final JComponent c) {
      c.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(c);
    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.options.filetablepanel.title");
    }

    public void loadConfig() {
      try {
        doubleClickBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_DIR_OPEN_DOUBLE_CLICK));

        hideFileBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_HIDDEN_FILES));

        copyDialogAutocloseBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_CLOSE_COPY_DIALOG));

        usePassWDConfirmDialog.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_CONFIRM_PASSWORD));

        guruCheckBox.setSelected(MainWin.config.getProgramSettings()
            .getBooleanOption(TProgramSettings.OPTION_GURU_MODE));

        fileSizeBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_FILE_SIZE_BYTES));

        caseInsensitiveBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_FILE_TABLE_CASE_INSENSITIVE));

        followSymLinksCopyBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_FOLLOW_SYMLINKS_COPY));

        followSymLinksSyncBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_FOLLOW_SYMLINKS_SYNC));

        final int tab_title_length = MainWin.config.getProgramSettings().getIntegerOption(
            TProgramSettings.OPTION_TAB_TITLE_LENGTH);
        tabLengthField.setText(Integer.toString(tab_title_length));

        final int tab_font_size = MainWin.config.getProgramSettings().getIntegerOption(
            TProgramSettings.OPTION_TAB_FONT_SIZE);
        tabFontSizeField.setText(Integer.toString(tab_font_size));

      } catch (final SettingsTypeException sex) {
        LOGGER.log(Level.WARNING, sex.getMessage());
      }
    }

    public void applyChanges() {
      try {
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_DIR_OPEN_DOUBLE_CLICK,
            Boolean.toString(doubleClickBox.isSelected()));
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_HIDDEN_FILES,
            Boolean.toString(hideFileBox.isSelected()));
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_CLOSE_COPY_DIALOG,
            Boolean.toString(copyDialogAutocloseBox.isSelected()));
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_CONFIRM_PASSWORD,
            Boolean.toString(usePassWDConfirmDialog.isSelected()));
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_GURU_MODE,
            Boolean.toString(guruCheckBox.isSelected()));
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_FILE_SIZE_BYTES,
            Boolean.toString(fileSizeBox.isSelected()));
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_FILE_TABLE_CASE_INSENSITIVE,
            Boolean.toString(caseInsensitiveBox.isSelected()));
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_FOLLOW_SYMLINKS_COPY,
            Boolean.toString(followSymLinksCopyBox.isSelected()));
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_FOLLOW_SYMLINKS_SYNC,
            Boolean.toString(followSymLinksSyncBox.isSelected()));

        final String tab_titleStr = tabLengthField.getText();
        if (tab_titleStr != null) {
          final int tab_title_length = Integer.parseInt(tab_titleStr);
          MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_TAB_TITLE_LENGTH,
              Integer.toString(tab_title_length));
        }

        final String tab_fontStr = tabFontSizeField.getText();
        if (tab_fontStr != null) {
          final int tab_font_size = Integer.parseInt(tab_fontStr);
          MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_TAB_FONT_SIZE,
              Integer.toString(tab_font_size));
        }

      } catch (final SettingsTypeException ste) {
        LOGGER.warning(ste.getMessage());
      } catch (final NumberFormatException e) {
        LOGGER.warning(e.getMessage());
      }

    }

    public boolean hasChanged() {
      boolean changed = false;

      try {

        if (hideFileBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_HIDDEN_FILES)
            || copyDialogAutocloseBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_CLOSE_COPY_DIALOG)
            || usePassWDConfirmDialog.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_CONFIRM_PASSWORD)
            || doubleClickBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_DIR_OPEN_DOUBLE_CLICK)
            || guruCheckBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_GURU_MODE)
            || fileSizeBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_FILE_SIZE_BYTES)
            || caseInsensitiveBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_FILE_TABLE_CASE_INSENSITIVE)
            || followSymLinksCopyBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_FOLLOW_SYMLINKS_COPY)
            || followSymLinksSyncBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_FOLLOW_SYMLINKS_SYNC)) {
          changed = true;
        }

        if (!changed) {
          // check even more

          try {

            final int tab_title_length = Integer.parseInt(tabLengthField.getText());
            if (tab_title_length != MainWin.config.getProgramSettings().getIntegerOption(
                TProgramSettings.OPTION_TAB_TITLE_LENGTH)) {
              changed = true;
            }

            final int tab_font_size = Integer.parseInt(tabFontSizeField.getText());
            if (tab_font_size != MainWin.config.getProgramSettings().getIntegerOption(
                TProgramSettings.OPTION_TAB_FONT_SIZE)) {
              changed = true;
            }

          } catch (final NumberFormatException e) {
            LOGGER.warning(e.getMessage());
          }

        }

      } catch (final SettingsTypeException sex) {
        LOGGER.log(Level.WARNING, sex.getMessage());
      }

      return changed;
    }
  }

  class FileTableInitPanel extends OptionPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final JCheckBox connectLeftPanelToLocalBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.options.initdir.connect_local"));
    private final JCheckBox useGnuBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.options.initdir.usegnu"));
    private final JCheckBox useCustomPathBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.options.initdir.custompath"));
    private final JTextField useCustomPathField = new JTextField(20);
    private final JButton useCustomPathButton = new JButton("..");

    public FileTableInitPanel() {

      final BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
      setLayout(layout);

      final JPanel useCustomPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      useCustomPathPanel.add(useCustomPathField);
      useCustomPathPanel.add(useCustomPathButton);

      connectLeftPanelToLocalBox.addActionListener(this);
      connectLeftPanelToLocalBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          checkConnectToLocal();
        }
      });

      // useGnuBox.addActionListener(this);

      useCustomPathBox.addActionListener(this);
      useCustomPathBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          checkCustomPath();
        }
      });

      useCustomPathButton.addActionListener(this);

      addComponent(connectLeftPanelToLocalBox);
      addComponent(useGnuBox);
      addComponent(useCustomPathBox);
      addComponent(useCustomPathPanel);

      checkConnectToLocal();
      checkCustomPath();

    }

    private void addComponent(final JComponent c) {
      c.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(c);
    }

    @Override
    public void applyChanges() {
      try {
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_CONNECT_TO_LOCAL,
            Boolean.toString(connectLeftPanelToLocalBox.isSelected()));

        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_USE_GNU_FOR_LOCAL,
            Boolean.toString(useGnuBox.isSelected()));

        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_USE_CUSTOM_PATH,
            Boolean.toString(useCustomPathBox.isSelected()));

        if (useCustomPathBox.isSelected()) {
          final String customPath = useCustomPathField.getText();
          MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_CUSTOM_PATH, customPath);
        }

      } catch (SettingsTypeException e) {
        LOGGER.severe(e.getMessage());
      }
    }

    @Override
    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.options.initdir.title");
    }

    @Override
    public boolean hasChanged() {

      boolean changed = false;

      try {
        if (connectLeftPanelToLocalBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_CONNECT_TO_LOCAL)
            || useGnuBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_USE_GNU_FOR_LOCAL)
            || useCustomPathBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_USE_CUSTOM_PATH)) {
          changed = true;
        }

        if (useCustomPathBox.isSelected()) {
          String oldPath = MainWin.config.getProgramSettings().getStringOption(TProgramSettings.OPTION_CUSTOM_PATH);
          String newPath = useCustomPathField.getText();
          if (oldPath != newPath) {
            changed = true;
          }
        }

      } catch (SettingsTypeException e) {
        LOGGER.severe(e.getMessage());
      }

      return changed;
    }

    @Override
    public void loadConfig() {

      try {

        connectLeftPanelToLocalBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_CONNECT_TO_LOCAL));

        useGnuBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_USE_GNU_FOR_LOCAL));

        useCustomPathBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_USE_CUSTOM_PATH));

        if (MainWin.config.getProgramSettings().getBooleanOption(TProgramSettings.OPTION_USE_CUSTOM_PATH)) {

          useCustomPathField.setText(MainWin.config.getProgramSettings().getStringOption(
              TProgramSettings.OPTION_CUSTOM_PATH));
        }

      } catch (SettingsTypeException e) {
        LOGGER.severe(e.getMessage());
      }

    }

    public void actionPerformed(ActionEvent e) {

      if (e.getSource().equals(connectLeftPanelToLocalBox)) {
        checkConnectToLocal();

      } else if (e.getSource().equals(useCustomPathBox)) {
        checkCustomPath();

      } else if (e.getSource().equals(useCustomPathButton)) {

        JFileChooser fC = new JFileChooser();
        fC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int choice = fC.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
          final String dir = fC.getSelectedFile().getAbsolutePath();
          useCustomPathField.setText(dir);
        }

      } else {
        LOGGER.warning("unhandled event " + e);
      }
    }

    private void checkCustomPath() {

      if (useCustomPathBox.isEnabled() && useCustomPathBox.isSelected()) {
        useCustomPathField.setEnabled(true);
        useCustomPathButton.setEnabled(true);
      } else {
        useCustomPathField.setEnabled(false);
        useCustomPathButton.setEnabled(false);
      }
    }

    private void checkConnectToLocal() {

      LOGGER.fine("checkConnectToLocal()");

      if (connectLeftPanelToLocalBox.isEnabled() && connectLeftPanelToLocalBox.isSelected()) {
        useGnuBox.setEnabled(true);
        useCustomPathBox.setEnabled(true);
      } else {
        useGnuBox.setEnabled(false);
        useCustomPathBox.setEnabled(false);
      }
    }

  }

  // ---------------------------------------------------------------------------

  class FileAttributesPanel extends OptionPanel {

    private static final long serialVersionUID = 3544392487352610870L;

    private final JCheckBox mtimeBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.options.preserve_mtime"));

    private final JCheckBox permBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.options.preserve_perm"));

    private final PermissionPanel dirPermPanel = new PermissionPanel(PermissionPanel.TYPE_DIR, false);

    private final PermissionPanel filePermPanel = new PermissionPanel(PermissionPanel.TYPE_FILE, false);

    private int permissions_dir = -1;
    private int permissions_file = -1;
    private boolean preserve_mtime = false;
    private boolean preserve_perm = false;

    public FileAttributesPanel() {

      final JPanel mtimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      mtimePanel.add(mtimeBox);
      final JPanel permPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      permPanel.add(permBox);

      dirPermPanel.setBorder(BorderFactory.createTitledBorder(LanguageBundle.getInstance().getMessage(
          "dialog.options.perm.dir")));
      filePermPanel.setBorder(BorderFactory.createTitledBorder(LanguageBundle.getInstance().getMessage(
          "dialog.options.perm.file")));

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(mtimePanel);
      add(permPanel);
      add(dirPermPanel);
      add(filePermPanel);
    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.options.filetattributes.title");
    }

    public boolean hasChanged() {
      boolean ret = false;
      if (preserve_mtime != mtimeBox.isSelected() || preserve_perm != permBox.isSelected()
          || permissions_dir != dirPermPanel.getPermissions() || permissions_file != filePermPanel.getPermissions()) {
        ret = true;
      }
      return ret;
    }

    public void loadConfig() {
      try {
        preserve_mtime = MainWin.config.getProgramSettings().getBooleanOption(TProgramSettings.OPTION_PRESERVE_MTIME);
        mtimeBox.setSelected(preserve_mtime);

        preserve_perm = MainWin.config.getProgramSettings().getBooleanOption(TProgramSettings.OPTION_PRESERVE_PERM);
        permBox.setSelected(preserve_perm);

        permissions_dir = MainWin.config.getProgramSettings().getIntegerOption(TProgramSettings.OPTION_PERMISSIONS_DIR);
        if (permissions_dir != -1) {
          dirPermPanel.setPermissions(permissions_dir);
        }

        permissions_file = MainWin.config.getProgramSettings().getIntegerOption(
            TProgramSettings.OPTION_PERMISSIONS_FILE);

        if (permissions_file != -1) {
          filePermPanel.setPermissions(permissions_file);
        }
      } catch (final SettingsTypeException sex) {
        LOGGER.log(Level.WARNING, sex.getMessage());
      }
    }

    public void applyChanges() {
      try {
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_PRESERVE_MTIME,
            new Boolean(mtimeBox.isSelected()).toString());
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_PRESERVE_PERM,
            new Boolean(permBox.isSelected()).toString());
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_PERMISSIONS_DIR,
            new Integer(dirPermPanel.getPermissions()).toString());
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_PERMISSIONS_FILE,
            new Integer(filePermPanel.getPermissions()).toString());
      } catch (final SettingsTypeException ste) {
        LOGGER.log(Level.WARNING, ste.getMessage());
      }
    }

  } // FileAttributesPanel

  // ---------------------------------------------------------------------------

  class GeneralOptionsPanel extends OptionPanel {

    private static final long serialVersionUID = -1541535493434451169L;

    private final JCheckBox promptForExitCheckBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.confirm.exit_always"));
    private final JCheckBox expertCheckBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "dialog.options.general.expert"));

    private final JButton setMasterPasswordBtn = new JButton(LanguageBundle.getInstance().getMessage(
        "dialog.options.set_master_password"));

    public GeneralOptionsPanel() {
      final JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      addComponent(mainPanel, promptForExitCheckBox);
      addComponent(mainPanel, expertCheckBox);
      addComponent(mainPanel, setMasterPasswordBtn);

      setMasterPasswordBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            MasterPasswordHandler.getInstance().setOrChangePassword(OptionsDialog.this);
          } catch (PasswordStoreException e1) {
            e1.printStackTrace();
          }
        }
      });

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(mainPanel);
    }

    private void addComponent(final JPanel mainPane, final JComponent comp) {
      comp.setAlignmentX(Component.LEFT_ALIGNMENT);
      mainPane.add(comp);
    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.options.general.title");
    }

    public void applyChanges() {
      try {
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_DO_NOT_PROMPT_FOR_EXIT,
            new Boolean(promptForExitCheckBox.isSelected()).toString());
        MainWin.config.getProgramSettings().setProgramOption(TProgramSettings.OPTION_EXPERT_MODE,
            new Boolean(expertCheckBox.isSelected()).toString());
      } catch (final SettingsTypeException ste) {
        LOGGER.log(Level.WARNING, ste.getMessage());
      }
    }

    public boolean hasChanged() {
      boolean ret = false;
      try {
        if (promptForExitCheckBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_DO_NOT_PROMPT_FOR_EXIT)
            || expertCheckBox.isSelected() != MainWin.config.getProgramSettings().getBooleanOption(
                TProgramSettings.OPTION_EXPERT_MODE)) {
          ret = true;
        }
      } catch (final SettingsTypeException ste) {
        LOGGER.warning(ste.getMessage());
      }
      return ret;
    }

    public void loadConfig() {
      try {
        promptForExitCheckBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_DO_NOT_PROMPT_FOR_EXIT));
        expertCheckBox.setSelected(MainWin.config.getProgramSettings().getBooleanOption(
            TProgramSettings.OPTION_EXPERT_MODE));
      } catch (final SettingsTypeException sex) {
        LOGGER.log(Level.WARNING, sex.getMessage());
      }
    }

  }

  // ---------------------------------------------------------------------------

  class ConnectionPluginPanel extends OptionPanel {

    private static final long serialVersionUID = 3977303200640611638L;
    private final JList pluginList = new JList();
    // maps plugin names to plugin IDs for fast search
    private final HashMap<String, Integer> pluginHash = new HashMap<String, Integer>();
    private final ConnectionPluginInfoPanel infoPanel = new ConnectionPluginInfoPanel();

    public ConnectionPluginPanel() {

      final int[] pluginIDs = ConnectionPluginManager.getSupportedPlugins();
      final JScrollPane scrollPane = new JScrollPane(pluginList);

      // fill list with plug-in names
      final String[] pluginNames = new String[pluginIDs.length];
      for (int i = 0; i < pluginIDs.length; i++) {
        pluginNames[i] = ConnectionPluginManager.getConnectionModelInstance(pluginIDs[i]).getName();
        pluginHash.put(pluginNames[i], new Integer(pluginIDs[i]));
      }
      pluginList.setListData(pluginNames);

      pluginList.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(final ListSelectionEvent lse) {
          final String pluginName = (String) pluginList.getSelectedValue();

          if (pluginName == null) {
            infoPanel.showPluginInfo(null);
            return;
          } else {
            final int pluginID = ((Integer) pluginHash.get(pluginName)).intValue();
            final ConnectionPlugin plugin = ConnectionPluginManager.getConnectionModelInstance(pluginID);
            infoPanel.showPluginInfo(plugin);
          }
        }
      });

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(scrollPane);
      add(Box.createVerticalStrut(5));
      add(infoPanel);
    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.options.connection_plugins.title");
    }

    public void applyChanges() {
    }

    public boolean hasChanged() {
      return false;
    }

    public void loadConfig() {
    }
  } // end ConnectionPluginPanel

  // ---------------------------------------------------------------------------

  class ConnectionPluginInfoPanel extends JPanel {

    private static final long serialVersionUID = 3257007670086351158L;
    private final JTextField nameField = new JTextField();
    private final JTextField descrField = new JTextField();
    private final JTextField versionField = new JTextField();
    private final JTextField licenseField = new JTextField();

    public ConnectionPluginInfoPanel() {

      final JPanel bp = new JPanel();
      bp.setLayout(new BoxLayout(bp, BoxLayout.Y_AXIS));

      bp.add(fieldSetup(nameField, LanguageBundle.getInstance().getMessage("theme.name")));
      bp.add(Box.createVerticalStrut(3));
      bp.add(fieldSetup(descrField, LanguageBundle.getInstance().getMessage("theme.description")));
      bp.add(Box.createVerticalStrut(3));
      bp.add(fieldSetup(versionField, LanguageBundle.getInstance().getMessage("theme.version")));
      bp.add(Box.createVerticalStrut(3));
      bp.add(fieldSetup(licenseField, LanguageBundle.getInstance().getMessage("theme.license")));

      setLayout(new BorderLayout());
      // setBorder(BorderFactory.createEtchedBorder());
      add(bp, BorderLayout.CENTER);
    }

    public void showPluginInfo(final ConnectionPlugin plugin) {
      if (plugin != null) {
        nameField.setText(plugin.getName());
        descrField.setText(plugin.getDescription());
        versionField.setText(plugin.getMajorVersion() + "." + plugin.getMinorVersion());
        licenseField.setText(plugin.getLicense());
      } else {
        nameField.setText("");
        descrField.setText("");
        versionField.setText("");
        licenseField.setText("");
      }
    }

    protected JComponent fieldSetup(final JTextField label, final String title) {
      final JPanel fieldPane = new JPanel(new GridLayout(1, 1));

      label.setEditable(false);
      fieldPane.add(label);
      fieldPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), title));

      return fieldPane;
    }

  } // ConnectionPluginInfoPanel

  class KeyStrokeOptionPanel extends OptionPanel {

    private static final long serialVersionUID = 7492299373957261632L;
    private final KeyStrokePanel keyStrokePanel;

    public KeyStrokeOptionPanel() {

      final JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      keyStrokePanel = new KeyStrokePanel(new KeyStrokeConfig());
      mainPanel.add(keyStrokePanel);

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(mainPanel);
    }

    public String getPanelBorderTitle() {
      return LanguageBundle.getInstance().getMessage("dialog.options.keystroke.title");
    }

    public void applyChanges() {
    }

    public boolean hasChanged() {
      return false;
    }

    public void loadConfig() {

      final InputStream instream = OptionsDialog.class
          .getResourceAsStream("/net/sf/jfilesync/gui/keymap/default_keymap.props");
      final InputStream instream2 = OptionsDialog.class
          .getResourceAsStream("/net/sf/jfilesync/gui/keymap/shortcut2localekey.properties");

      final KeyStrokeConfigLoader loader = new KeyStrokeConfigLoader(instream, instream2);

      try {
        loader.load();
        keyStrokePanel.setKeyStrokeConfig(loader.getKeyStrokeConfig());
      } catch (final IOException e) {
        LOGGER.warning("cannot read key properties " + e.getMessage());
      }

    }

  }

}
