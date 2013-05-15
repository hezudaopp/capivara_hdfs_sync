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
package net.sf.jfilesync.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.jfilesync.GlobalDataStore;
import net.sf.jfilesync.engine.ConnectionDetails;
import net.sf.jfilesync.gui.components.ConnectionDetailsComponent2;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.ConfigDefinitions;
import net.sf.jfilesync.sync2.SyncMethod;
import net.sf.jfilesync.sync2.list.Expression;
import net.sf.jfilesync.sync2.list.ExpressionList;
import net.sf.jfilesync.sync2.list.ExpressionListDialog;
import net.sf.jfilesync.sync2.list.NExpListList;
import net.sf.jfilesync.sync2.list.NamedListConfigHandler;
import net.sf.jfilesync.sync2.syncer.SyncSettings;

public class SyncOptionDialog extends JDialog implements ActionListener, SyncMethodChangeListener {

  private static final long serialVersionUID = 3907209373041046835L;
  public final static int OPTION_OK = 0, OPTION_CANCEL = 1;
  private int userChoice = OPTION_CANCEL;

  private MethodChooserPanel methodPane = null;
  private ConnectionOptionsPanel leftConPane;
  private ConnectionOptionsPanel rightConPane;

  private final static String OK_CMD = "OK";
  private final static String CANCEL_CMD = "CANCEL";

  private final static String REGEX_ACTIVE = ExpressionRegexPanel.class.getName() + "ACTIVE";

  private ExpressionList includeList;
  private ExpressionList excludeList;

  /* for MAC stuff etc. */
  private ExpressionList addOnExpressionList = new ExpressionList();
  private SyncSettingsPanel settingsPanel = new SyncSettingsPanel();

  // private final static Logger LOGGER =
  // Logger.getLogger(SyncOptionDialog.class
  // .getPackage().getName());

  public SyncOptionDialog(JFrame parent, ConnectionDetails detailsLeft, ConnectionDetails detailsRight,
      SyncMethod[] syncMethods, boolean[] methodsEnabled) {

    super(parent, LanguageBundle.getInstance().getMessage("sync.method.label"), true);

    leftConPane = new ConnectionOptionsPanel(detailsLeft);
    rightConPane = new ConnectionOptionsPanel(detailsRight);

    leftConPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), LanguageBundle
        .getInstance().getMessage("sync.label.left_side")));

    rightConPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), LanguageBundle
        .getInstance().getMessage("sync.label.right_side")));

    methodPane = new MethodChooserPanel(syncMethods, methodsEnabled);
    methodPane.addSyncMethodChangeListener(this);
    methodPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), LanguageBundle
        .getInstance().getMessage("sync.method.label")));

    final JPanel main = new JPanel();
    main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

    final JPanel connectionInfoPanel = new JPanel();
    connectionInfoPanel.setLayout(new BoxLayout(connectionInfoPanel, BoxLayout.X_AXIS));
    connectionInfoPanel.add(leftConPane);
    connectionInfoPanel.add(rightConPane);

    main.add(connectionInfoPanel);
    main.add(methodPane);
    main.add(settingsPanel);

    ExpressionRegexPanel expPanel = new ExpressionRegexPanel();
    main.add(expPanel);

    JButton okButton = new JButton(LanguageBundle.getInstance().getMessage("label.start"));
    okButton.setActionCommand(OK_CMD);
    okButton.addActionListener(this);
    if (syncMethods == null || syncMethods.length == 0) {
      okButton.setEnabled(false);
    }

    JButton cancelButton = new JButton(LanguageBundle.getInstance().getMessage("label.abort"));
    cancelButton.setActionCommand(CANCEL_CMD);
    cancelButton.addActionListener(this);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    getContentPane().add(main, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(parent);
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals(OK_CMD)) {
      userChoice = OPTION_OK;
      setVisible(false);
    } else if (cmd.equals(CANCEL_CMD)) {
      userChoice = OPTION_CANCEL;
      setVisible(false);
    }
  }

  public SyncMethod getChosenMethod() {
    if (methodPane != null) {
      return methodPane.getSelectedMethod();
    } else {
      return null;
    }
  }

  public int getUserChoice() {
    return userChoice;
  }

  public void syncMethodChosen(SyncMethod m) {
    if (m == null) {
      return;
    }
    if (leftConPane != null) {
      leftConPane.setFatModeEnabled(m.requiresTimeStamps());
    }
    if (rightConPane != null) {
      rightConPane.setFatModeEnabled(m.requiresTimeStamps());
    }
  }

  public SyncSettings getSyncSettings() {
    final SyncSettings settings = new SyncSettings();
    if (leftConPane != null) {
      settings.setLeftFatModeEnabled(leftConPane.isFatModeEnabled());
    }
    if (rightConPane != null) {
      settings.setRightFatModeEnabled(rightConPane.isFatModeEnabled());
    }
    settings.setCaseSensitive(!settingsPanel.isCaseInsensitive());
    if (includeList != null) {
      settings.setIncludeList(includeList);
    }

    if (settingsPanel.isDsStoreIgnored()) {
      if (excludeList == null) {
        excludeList = new ExpressionList();
      }
    }

    ExpressionList eList = null;
    if (excludeList != null) {
      eList = new ExpressionList();
      eList.merge(excludeList);
    }

    if (addOnExpressionList.size() > 0) {
      if (eList == null) {
        eList = new ExpressionList();
      }
      eList.merge(addOnExpressionList);
    }

    if (eList != null) {
      settings.setExcludeList(eList);
    }

    settings.setModifyWindow(settingsPanel.getModifyWindow());

    return settings;
  }

  private void enableMacExpressions(boolean enabled) {
//    System.out.println("enabled? " + enabled);	// Jawinton
    for (final String expString : ConfigDefinitions.MAC_EXCLUDE_LIST) {
      if (enabled) {
        addOnExpressionList.addExpression(new Expression(expString));
      } else {
        addOnExpressionList.deleteExpression(new Expression(expString));
      }
    }
  }

  class ExpressionRegexPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JButton button = new JButton(LanguageBundle.getInstance().getMessage("label.edit"));

    private JLabel isActiveLabel = new JLabel();

    public ExpressionRegexPanel() {
      initUI();
      if (GlobalDataStore.getInstance().getValue(REGEX_ACTIVE) != null) {
        enableRegexFitering((Boolean) GlobalDataStore.getInstance().getValue(REGEX_ACTIVE));
      }
    }

    private void initUI() {
      button.addActionListener(this);
      button.setActionCommand("open");

      isActiveLabel.setForeground(Color.RED);
      isActiveLabel.setPreferredSize(new Dimension(150, 25));

      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(button);
      add(isActiveLabel);

      setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), LanguageBundle.getInstance()
          .getMessage("label.file_filter")));
    }

    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("open")) {
        openExpressionDialog();
      }
    }

    protected void openExpressionDialog() {

      NExpListList lists = NamedListConfigHandler.getInstance().getNamedLists();

      ExpressionListDialog eld = new ExpressionListDialog(SyncOptionDialog.this, lists);

      eld.setVisible(true);

      int userOpt = eld.getUserOption();

      if (userOpt == ExpressionListDialog.OPTION_APPLY) {
        applyRegex();
      } else {
        includeList = null;
        excludeList = null;
        enableRegexFitering(false);
      }
    }

    private void applyRegex() {
      boolean enableRegex = false;
      includeList = GlobalDataStore.getInstance().getIncludeList();
      excludeList = GlobalDataStore.getInstance().getExcludeList();
      if (includeList.size() < 1 && excludeList.size() < 1) {
        includeList = null;
        excludeList = null;
      } else {
        enableRegex = true;
      }
      enableRegexFitering(enableRegex);
      GlobalDataStore.getInstance().storeValue(REGEX_ACTIVE, enableRegex);
    }

    protected void enableRegexFitering(final boolean enabled) {
      if (enabled) {
        isActiveLabel.setText(LanguageBundle.getInstance().getMessage("filter.isactive"));
      } else {
        isActiveLabel.setText("");
      }
    }
  }

  class ConnectionOptionsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final ConnectionDetails details;

    private JCheckBox fatTimeCheckBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "sync.settings.use_fat_time"));

    public ConnectionOptionsPanel(final ConnectionDetails details) {
      this.details = details;
      initUI();
    }

    private void initUI() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      JPanel optPane1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
      optPane1.add(fatTimeCheckBox);

      add(new ConnectionDetailsComponent2(details));
      add(optPane1);
    }

    public boolean isFatModeEnabled() {
      return fatTimeCheckBox.isSelected();
    }

    public void setFatModeEnabled(final boolean enabled) {
      fatTimeCheckBox.setEnabled(enabled);
    }

  }

  class SyncSettingsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JCheckBox caseInsensitive = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "sync.settings.case_insensitive"));

    private JCheckBox dsStoreCheckBox = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "sync.settings.ignore_dsstore"), false);

    private JCheckBox modifyLabel = new JCheckBox(LanguageBundle.getInstance().getMessage(
        "sync.settings.time_res_window"), false);

    private JSpinner modifySpinner = new JSpinner();

    public SyncSettingsPanel() {
      initUI();
      dsStoreCheckBox.setSelected(true);
    }

    public int getModifyWindow() {
      int window = 0;
      if (modifyLabel.isSelected()) {
        window = ((Integer) modifySpinner.getValue()).intValue();
      }
      return window;
    }

    private void initUI() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      addOption(caseInsensitive);
      addOption(dsStoreCheckBox);
      add(createModifyWindowPanel());

      dsStoreCheckBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          enableMacExpressions(dsStoreCheckBox.isSelected());
        }
      });

    }

    private JPanel createModifyWindowPanel() {
      final JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));

      SpinnerModel model = new SpinnerNumberModel(1, // initial value
          0, // min
          5, // max
          1); // step

      modifySpinner.setModel(model);
      modifySpinner.setFocusable(false);
      modifySpinner.setEnabled(false);

      modifyLabel.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          modifySpinner.setEnabled(modifyLabel.isSelected());
        }
      });

      pane.add(modifyLabel);
      pane.add(modifySpinner);

      return pane;
    }

    private void addOption(JCheckBox checkbox) {
      JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
      pane.add(checkbox);
      add(pane);
    }

    public boolean isCaseInsensitive() {
      return caseInsensitive.isSelected();
    }

    public boolean isDsStoreIgnored() {
      return dsStoreCheckBox.isSelected();
    }

  }

  private static class MethodChooserPanel extends JPanel implements ItemListener {

    private static final long serialVersionUID = 3834030264224723768L;
    private final SyncMethod[] methods;
    private final boolean[] methodEnabled;
    private int userMethod = -1;
    private JComboBox methodBox = new JComboBox();
    private final java.util.List<SyncMethodChangeListener> listeners = new ArrayList<SyncMethodChangeListener>();

    public MethodChooserPanel(final SyncMethod[] methods, final boolean[] methodEnabled) {
      this.methods = methods;
      this.methodEnabled = methodEnabled;

      methodBox.addItemListener(this);
      for (int i = 0; i < methods.length; i++) {
        if (this.methodEnabled[i] == true) {
          methodBox.addItem(methods[i].getMethodName());
        }
      }

      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(methodBox);
    }

    public SyncMethod getSelectedMethod() {
      return methods[userMethod];
    }

    public synchronized void addSyncMethodChangeListener(SyncMethodChangeListener l) {
      if (l == null) {
        throw new NullPointerException("MethodChangeListener is null");
      }
      listeners.add(l);
    }

    public synchronized void fireSyncMethodChangedEvent(SyncMethod m) {
      if (m == null) {
        return;
      }
      for (SyncMethodChangeListener l : listeners) {
        l.syncMethodChosen(m);
      }
    }

    public void itemStateChanged(ItemEvent e) {
      if (e.getSource() == methodBox) {
        String methodName = (String) methodBox.getSelectedItem();
        for (int i = 0; i < methods.length; i++) {
          if (methodName.equals(methods[i].getMethodName())) {
            userMethod = i;
            fireSyncMethodChangedEvent(methods[i]);
          }
        }
      }
    }
  }

}
