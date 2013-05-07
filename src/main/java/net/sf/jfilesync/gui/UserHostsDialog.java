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
 */

package net.sf.jfilesync.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.ConParams;
import net.sf.jfilesync.engine.ConnectionConfig;
import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.event.types.TStandardMessage;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.items.THdfs_plugin;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.CapivaraPasswordStore;
import net.sf.jfilesync.settings.MasterPasswordHandler;
import net.sf.jfilesync.settings.PasswordStoreException;

public class UserHostsDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 3618420419112088371L;
    private JList savedList;
    private final JTextField userField = new JTextField(20);;
    private final JTextField hostField = new JTextField(20);
//    private final JPasswordField passwordField = new JPasswordField(20);	// Jawinton
    private final JComboBox protoCombo = new JComboBox();
    private final ConParams params;

    private final JLabel userLabel = new JLabel(LanguageBundle.getInstance()
            .getMessage("label.user"));
    private final JLabel hostLabel = new JLabel(LanguageBundle.getInstance()
            .getMessage("label.host"));
    private final JLabel protoLabel = new JLabel(LanguageBundle.getInstance()
            .getMessage("label.protocol"));
//    private final JLabel passwdLabel = new JLabel(LanguageBundle.getInstance()
//            .getMessage("label.password"));	// Jawinton
    
    private final JTextArea passwdHintArea = new JTextArea(LanguageBundle
            .getInstance().getMessage(
                    "window.dialog.user_host_settings.password_hint"));

    private final JButton okbutton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.ok"));
    private final JButton cancelbutton = new JButton(LanguageBundle
            .getInstance().getMessage("label.cancel"));
    private final JButton newbutton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.new"));
    private final JButton savebutton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.save"));
    private final JButton deletebutton = new JButton(LanguageBundle
            .getInstance().getMessage("label.delete"));

    /* Jawinton */
    private static final int hdfsPluginId = ConnectionPluginManager.HDFS_PLUGIN;
    private final ConnectionPlugin hdfsPlugin = ConnectionPluginManager
            .getConnectionModelInstance(hdfsPluginId);
    private DefaultListModel listModel;
    private final JCheckBox portCheckBox = new JCheckBox(LanguageBundle
            .getInstance().getMessage("label.default"), true);
    private final JTextField portTextField = new JTextField(4);

    private ConnectionPlugin activePlugin = hdfsPlugin;
    /* Jawinton */
    private final java.util.List<ConnectionPlugin> pluginList = new ArrayList<ConnectionPlugin>();

    private final static Logger LOGGER = Logger.getLogger(UserHostsDialog.class
            .getPackage().getName());

    public UserHostsDialog(final JDialog parent, final ConParams params) {
        super(parent, LanguageBundle.getInstance().getMessage(
                "window.dialog.user_host_settings.label"), true);
        this.params = params;
        initDialog();
    }

    public UserHostsDialog(final JFrame parent, final ConParams params) {
        super(parent, LanguageBundle.getInstance().getMessage(
                "window.dialog.user_host_settings.label"), true);
        this.params = params;
        initDialog();
    }

    private void initDialog() {

        final JPanel pane = new JPanel(new BorderLayout());
        final JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        listModel = new DefaultListModel();
        updateListModel();
        savedList = new JList(listModel);
        savedList
                .setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        // savedList.setMinimumSize(new Dimension(150, 300));
        // savedList.setPreferredSize(new Dimension(200, 300));

        savedList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                setListEditState();
            }
        });

        savedList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() < 2) {
                    return;
                }
                if (savedList.isSelectionEmpty()) {
                    return;
                }
                setListEditState();
                getUserData();
                dispose();
            }
        });

        // fill protoCombo
        final int supProtos[] = ConnectionPluginManager.getSupportedPlugins();
        for (int i = 0; i < supProtos.length; i++) {
            final ConnectionPlugin plugin = ConnectionPluginManager
                    .getConnectionModelInstance(supProtos[i]);
            if (!plugin.isLocalConnection()) {
                pluginList.add(plugin);
                protoCombo.addItem(plugin.getDescription());
            }
        }

        portCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(final ItemEvent e) {
                if (portCheckBox.isSelected()) {
                    setDefaultPortState(true);
                } else {
                    portTextField.setEnabled(true);
                }
            }
        });

        protoCombo.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final int idx = protoCombo.getSelectedIndex();
                if (idx < pluginList.size()) {
                    activePlugin = pluginList.get(idx);
                    setDefaultPortState(true);
                }
            }
        });

        final JLabel portLabel = new JLabel(LanguageBundle.getInstance()
                .getMessage("label.port"));
        final JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portPanel.add(portCheckBox);
        portPanel.add(portTextField);

        final JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        addToOptionPanel(optionPanel, hostLabel);
        addToOptionPanel(optionPanel, hostField);
        optionPanel.add(Box.createVerticalStrut(7));
        addToOptionPanel(optionPanel, portLabel);
        addToOptionPanel(optionPanel, portPanel);
        optionPanel.add(Box.createVerticalStrut(7));
        addToOptionPanel(optionPanel, userLabel);
        addToOptionPanel(optionPanel, userField);
        optionPanel.add(Box.createVerticalStrut(7));
        addToOptionPanel(optionPanel, protoLabel);
        addToOptionPanel(optionPanel, protoCombo);
        // Jawinton
//        optionPanel.add(Box.createVerticalStrut(7));
//        addToOptionPanel(optionPanel, passwdLabel);
//        addToOptionPanel(optionPanel, passwordField);
        
        passwdHintArea.setEditable(false);
        passwdHintArea.setBorder(BorderFactory.createEtchedBorder());
        addToOptionPanel(optionPanel, passwdHintArea);
        
        final JPanel rightSplitPane = new JPanel();
        rightSplitPane.add(optionPanel, BorderLayout.NORTH);

        final JScrollPane favoritesScroller = new JScrollPane(savedList);
        favoritesScroller.setPreferredSize(new Dimension(250, 300));

        splitpane.setLeftComponent(favoritesScroller);
        splitpane.setRightComponent(rightSplitPane);

        setupButton(okbutton, this, "ok");
        setupButton(savebutton, this, "save");
        setupButton(newbutton, this, "new");
        setupButton(deletebutton, this, "delete");
        setupButton(cancelbutton, this, "cancel");

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(savebutton);
        buttonPanel.add(newbutton);
        buttonPanel.add(deletebutton);

        final JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel2.add(okbutton);
        buttonPanel2.add(cancelbutton);

        final JPanel allButtonPanel = new JPanel();
        allButtonPanel
                .setLayout(new BoxLayout(allButtonPanel, BoxLayout.Y_AXIS));
        allButtonPanel.add(buttonPanel);
        allButtonPanel.add(buttonPanel2);

        pane.add(splitpane, BorderLayout.CENTER);
        pane.add(allButtonPanel, BorderLayout.SOUTH);

        setInitState();
        setContentPane(pane);
        pack();
        setLocationRelativeTo(getParent());
    }

    private void addToOptionPanel(final JPanel optionPanel,
            final JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionPanel.add(component);
    }

    private void setDefaultPortState(final boolean enabled, final int port) {
        portCheckBox.setSelected(enabled);
        portCheckBox.setEnabled(enabled);
        portTextField.setEnabled(!enabled);

        if (port == -1) {
            portTextField.setText("");
        } else {
            portTextField.setText(Integer.toString(port));
        }
    }

    private void setDefaultPortState(final boolean enabled) {
        setDefaultPortState(enabled, activePlugin.getDefaultPort());
    }

    private void updateListModel() {
        if (listModel == null) {
            return;
        }
        final ConnectionConfig[] conConf = MainWin.config.getSavedConnections();
        for (int i = 0; i < conConf.length; i++) {
            if (!listModel.contains(conConf[i].getDescription())) {
                listModel.addElement(conConf[i].getDescription());
            }
        }
    }

    private void setupButton(final JButton button,
            final ActionListener listener, final String command) {
        button.addActionListener(listener);
        button.setActionCommand(command);
    }

    public void actionPerformed(final ActionEvent e) {
        final String command = e.getActionCommand();
        if (command.equals("ok")) {
            final boolean portOK = checkPort();
            if (portOK) {
                getUserData();
                setVisible(false);
            }
        } else if (command.equals("save")) {

            if (hostField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, LanguageBundle
                        .getInstance()
                        .getMessage("favdialog.hostname_required"));
                return;
            }

            if (userField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, LanguageBundle
                        .getInstance()
                        .getMessage("favdialog.username_required"));
                return;
            }

            final boolean portOK = checkPort();
            if (!portOK) {
                return;
            }

            boolean insert = false;
            final int listSelection = savedList.getSelectedIndex();
            if (listSelection == -1) {
                insert = true;
            }

            final String host = hostField.getText().trim();
            final String user = userField.getText().trim();
            final int port = getSelectedPort();

            String descr = "";

            if (insert) {
                boolean nameOK = false;

                // ask for a name
                do {

                    do {
                        final String value = user + "@" + host;
                        descr = JOptionPane.showInputDialog(
                                UserHostsDialog.this,
                                LanguageBundle.getInstance().getMessage(
                                        "dialog.save.connection_name.request"),
                                value);

                        if (descr == null) {
                            // canceled
                            return;
                        }
                    } while (descr.equals(""));

                    // check if description exists
                    // if yes -> deny saving
                    boolean exists = false;
                    for (int i = 0; i < listModel.size(); i++) {
                        if (listModel.elementAt(i).equals(descr)) {
                            exists = true;
                            break;
                        }
                    }

                    if (exists) {
                        JOptionPane.showMessageDialog(UserHostsDialog.this,
                                LanguageBundle.getInstance().getMessage(
                                        "dialog.save.connection_name.exists")
                                        + " : " + descr);
                    } else {
                        nameOK = true;
                    }
                } while (!nameOK);
            } else {
                descr = (String) listModel.getElementAt(listSelection);
            }

            final int protocol = getSelectedProtocolId();

            final ConnectionConfig conf = new ConnectionConfig(descr, protocol,
                    hostField.getText().trim(), port, userField.getText()
                            .trim());

            // Jawinton
//            final char[] passwdAr = passwordField.getPassword();
//            if (passwdAr != null && passwdAr.length > 0) {
//                final String passwdStr = new String(passwdAr);
//                try {
//
//                    String masterPassword = MasterPasswordHandler.getInstance()
//                            .requestMasterPassword(this);
//
//                    if (masterPassword == null) {
//                        if (MasterPasswordHandler.getInstance()
//                                .setOrChangePassword(this)) {
//                            masterPassword = MasterPasswordHandler
//                                    .getInstance().requestMasterPassword(this);
//                        }
//                    }
//
//                    if (masterPassword != null) {
//                        final String encodedPasswd = CapivaraPasswordStore
//                                .getInstance().encodePassword(passwdStr,
//                                        masterPassword);
//
//                        LOGGER.fine("encoded password: " + encodedPasswd);
//
//                        if (encodedPasswd != null) {
//                            conf.setPassword(encodedPasswd);
//                        }
//                    }
//
//                } catch (final PasswordStoreException pe) {
//                    LOGGER.log(Level.WARNING, pe.getMessage(), pe);
//                }
//            }

            if (insert) {
                // add data
                MainWin.config.addConnectionData(conf);
                updateListModel();
            } else {
                // update/change DOM data
                final int opt = JOptionPane.showConfirmDialog(
                        UserHostsDialog.this, LanguageBundle.getInstance()
                                .getMessage("dialog.confirm.override_favorite")
                                + " " + descr,
                        LanguageBundle.getInstance().getMessage(
                                "dialog.confirm.override_favorite.title"),
                        JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.OK_OPTION) {
                    MainWin.config.changeConnectionData(conf);
                }
            }
            saveCurrentConfiguration();

        } else if (command.equals("cancel")) {
            setVisible(false);
        } else if (command.equals("new")) {
            setNewItemState();
        } else if (command.equals("delete")) {
            final int selected = savedList.getSelectedIndex();
            if (selected == -1) {
                return;
            }

            final String descr = (String) savedList.getSelectedValue();
            final int userOpt = JOptionPane.showConfirmDialog(this,
                    LanguageBundle.getInstance().getMessage(
                            "dialog.confirm.delete_entry")
                            + " : " + descr + "?", LanguageBundle.getInstance()
                            .getMessage("label.delete"),
                    JOptionPane.YES_NO_OPTION);
            if (userOpt == JOptionPane.YES_OPTION) {
                // we delete only by descr name (that's the only thing
                // the user cannot change in this dialog)
                MainWin.config.removeConnectionData(descr);
                listModel.removeElement(descr);
                setInitState();
                saveCurrentConfiguration();
            }
        }
    }

    private final void saveCurrentConfiguration() {
        // send configuration save message
        TEventMulticaster.getInstance().fireTEvent(this,
                TEvent.ANY_REGISTERED_RECEIPIENT,
                new TStandardMessage(TMessage.ID.SAVE_CONFIG_MESSAGE));
    }

    private boolean checkPort() {
        boolean ret = false;
        final String portStr = portTextField.getText().trim();
        try {
            Integer.parseInt(portStr);
            ret = true;
        } catch (final NumberFormatException e) {
            JOptionPane.showMessageDialog(UserHostsDialog.this, LanguageBundle
                    .getInstance().getMessage("error.config.port")
                    + " : " + portStr);
            ret = false;
        }
        return ret;
    }

    private int getSelectedPort() {
        int port = -1;

        if (portCheckBox.isSelected()) {
            port = activePlugin.getDefaultPort();
        } else {
            final String portStr = portTextField.getText().trim();
            port = Integer.parseInt(portStr);
        }

        return port;
    }

    private void setListEditState() {

        deletebutton.setEnabled(true);
        savebutton.setEnabled(false);
        hostField.setEnabled(false);
        userField.setEnabled(false);
//        passwordField.setEnabled(false);
        protoCombo.setEnabled(false);
        
        final String desc = (String) savedList.getSelectedValue();
        final ConnectionConfig[] conConf = MainWin.config.getSavedConnections();
        for (int i = 0; i < conConf.length; i++) {
            if (conConf[i].getDescription().equals(desc)) {
                final ConnectionPlugin plugin = ConnectionPluginManager
                        .getConnectionModelInstance(conConf[i].getProtocol());
                hostField.setText(conConf[i].getHostName());
                userField.setText(conConf[i].getUserName());
                protoCombo.setSelectedItem(plugin.getDescription());

                if (conConf[i].getPort() != -1
                        && conConf[i].getPort() != plugin.getDefaultPort()) {
                    portCheckBox.setSelected(false);
                    portTextField.setText(Integer
                            .toString(conConf[i].getPort()));
                } else {
                    portCheckBox.setSelected(true);
                    portTextField.setText(Integer.toString(plugin
                            .getDefaultPort()));
                }

                // Jawinton
//                if (conConf[i].getPassword() != null) {
//                    try {
//                        final String masterPassword = MasterPasswordHandler
//                                .getInstance().requestMasterPassword(this);
//                        if (masterPassword != null) {
//                            final String conPasswd = CapivaraPasswordStore
//                                    .getInstance().decodePassword(
//                                            conConf[i].getPassword(),
//                                            masterPassword);
//                            passwordField.setText(conPasswd);
//                        }
//                    } catch (final PasswordStoreException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    passwordField.setText("");
//                }

                break;
            }
        }

    }

    private void setNewItemState() {
        userField.setText("");
        hostField.setText("");
        for (int i = 0; i < pluginList.size(); i++) {
            if ((pluginList.get(i)).getConnectionID() == hdfsPlugin
                    .getConnectionID()) {
                protoCombo.setSelectedIndex(i);
                setDefaultPortState(true);
                break;
            }
        }
        savedList.clearSelection();
        
        deletebutton.setEnabled(false);
        savebutton.setEnabled(true);
        hostField.setEnabled(true);
        userField.setEnabled(true);
        protoCombo.setEnabled(true);
//        passwordField.setEnabled(true);   // Jawinton     
    }

    private void setInitState() {
        setNewItemState();
    }

    private int getSelectedProtocolId() {
        int protocol = -1;

        final int idx = protoCombo.getSelectedIndex();
        final ConnectionPlugin plugin = pluginList.get(idx);
        protocol = plugin.getConnectionID();

        return protocol;
    }

    private void getUserData() {
        params.hostname = hostField.getText().trim();
        params.username = userField.getText().trim();
        params.protocol = getSelectedProtocolId();
        params.port = getSelectedPort();
//        params.password = String.valueOf(passwordField.getPassword()); Jawinton
    }

}
