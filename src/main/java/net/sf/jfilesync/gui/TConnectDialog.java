/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Raik Nagel <raik.nagel@uni-bayreuth.de>
 * changed 2003-2005 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id: TConnectDialog.java,v 1.31 2006/08/29 19:58:19 hunold Exp $
 */

package net.sf.jfilesync.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.*;

import net.sf.jfilesync.engine.ConParams;
import net.sf.jfilesync.engine.ConnectionConfig;
import net.sf.jfilesync.gui.components.*;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.items.PluginOptionPanel;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;
import net.sf.jfilesync.util.*;


import net.sf.jfilesync.MainWin;

public class TConnectDialog extends JDialog implements ActionListener,
        KeyListener, FocusListener, PrefixComboBoxListener {

    private static final long serialVersionUID = 1L;

    // return values of dialog
    public static final int
      CD_RESULT_NOTHING = -1,
      CD_RESULT_CANCEL  =  1,
      CD_RESULT_OKAY    =  2;

    protected ConnectionConfig[] conConf;

    private JPanel mainPanel, buttonPanel;
    private JLabel hostLabel, userLabel, passwdLabel, protoLabel;
    private JTextField userField;
    private PrefixComboBox hostCombo;
    private JComboBox protoCombo;
    private JPasswordField passwdField;
    private JCheckBox portCheckBox = new JCheckBox("Default");
    private JTextField portTextField = new JTextField(4);

    private JButton okayButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.ok"));
    private JButton cancelButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.cancel"));
    private JButton chooseHostButton = new JButton(LanguageBundle.getInstance()
            .getMessage("label.favourites"));
    private JButton optionButton = new JButton(LanguageBundle.getInstance()
            .getMessage("dialog.connect.options"));
    private JCheckBox encodingCheckBox = new JCheckBox(LanguageBundle
            .getInstance().getMessage("dialog.connect.encoding"));
    private JComboBox encodingCombo = new JComboBox();
    private JCheckBox caseInsensitiveCheckBox = new JCheckBox(LanguageBundle
            .getInstance().getMessage("dialog.connect.case_insensitive"));

    private JCheckBox keepAliveCheckbox = new JCheckBox(LanguageBundle
            .getInstance().getMessage("dialog.connect.keep_alive"), true);

    private Component focusElement; // element to focus when showing dialog

    // currently chosen plugin instance
    // to retrieve information and/or options
    private ConnectionPlugin activePlugin;
    // references to all plugins
    private ConnectionPlugin[] plugins;
    // map pluginID -> plugin
    private final HashMap<Integer, ConnectionPlugin> pluginHash = new HashMap<Integer, ConnectionPlugin>();
    private int result;

    private Font boldFont = new java.awt.Font("Dialog", Font.BOLD, 12);
    private Font normFont = new java.awt.Font("Dialog", Font.PLAIN, 12);

    private volatile int supProtos[] = ConnectionPluginManager
            .getSupportedPlugins();

    private final static Logger LOGGER = Logger.getLogger(TConnectDialog.class
            .getPackage().getName());

    public TConnectDialog(JFrame parent) {
        super(parent, LanguageBundle.getInstance().getMessage(
                "window.dialog.con_settings.label"));
        result = CD_RESULT_NOTHING;

        mainPanel = new JPanel();

        hostLabel = new JLabel(LanguageBundle.getInstance().getMessage(
                "label.host"));
        hostLabel.setFont(normFont);

        conConf = MainWin.config.getSavedConnections();
        String[] hostData = new String[conConf.length];
        for (int i = 0; i < conConf.length; i++) {
            hostData[i] = conConf[i].getHostName();
        }

        hostCombo = new PrefixComboBox(hostData);
        hostCombo.setMinimumSize(new Dimension(300, 30));
        hostCombo.setPreferredSize(new Dimension(300, 30));
        hostCombo.setSize(new Dimension(300, 30));
        hostCombo.setPrefixComboBoxListener(this);
        hostCombo.addCustomFocusListener(this);

        protoLabel = new JLabel(LanguageBundle.getInstance().getMessage(
                "label.protocol"));
        protoLabel.setFont(normFont);

        protoCombo = new JComboBox();
        protoCombo.addKeyListener(this);
        protoCombo.addFocusListener(this);
        protoCombo.setMinimumSize(new Dimension(300, 30));
        protoCombo.setPreferredSize(new Dimension(300, 30));
        protoCombo.setSize(new Dimension(300, 30));
        protoCombo.setEditable(false);

        plugins = new ConnectionPlugin[supProtos.length];

        // fill protoCombo
        for (int i = 0; i < supProtos.length; i++) {
            ConnectionPlugin pluginToAdd = ConnectionPluginManager
                    .getConnectionModelInstance(supProtos[i]);

            // disable GNU plugin for non experts
            try {
                boolean expertMode = MainWin.config.getProgramSettings()
                        .getBooleanOption(TProgramSettings.OPTION_EXPERT_MODE);
                if (pluginToAdd.getConnectionID() == ConnectionPluginManager.GNU_PLUGIN) {
                    if (expertMode) {
                        protoCombo.addItem(pluginToAdd.getDescription());
                    }
                } else {
                    protoCombo.addItem(pluginToAdd.getDescription());
                }
            } catch (SettingsTypeException ste) {
                LOGGER.warning(ste.getMessage());
            }

            plugins[i] = ConnectionPluginManager
                    .getConnectionModelInstance(supProtos[i]);

            pluginHash.put(new Integer(supProtos[i]), plugins[i]);

            if (supProtos[i] == ConnectionPluginManager.LOCAL_PLUGIN) {
                protoCombo.setSelectedIndex(i);
                resetPluginInfo(plugins[i]);
            }
        }

        protoCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pluginDescr = (String) protoCombo.getSelectedItem();
                for (int i = 0; i < plugins.length; i++) {
                    if (plugins[i].getDescription().equals(pluginDescr)) {
                        activePlugin = plugins[i];
                        setProtocolDialogElements();
                        break;
                    }
                }
            }
        });

        userLabel = new JLabel(LanguageBundle.getInstance().getMessage(
                "label.user"));
        userLabel.setFont(normFont);
        userField = new JTextField(30);
        userField.addKeyListener(this);
        userField.addFocusListener(this);

        passwdLabel = new JLabel(LanguageBundle.getInstance().getMessage(
                "label.password"));
        passwdLabel.setFont(boldFont);
        passwdField = new JPasswordField(30);
        passwdField.addKeyListener(this);
        passwdField.addFocusListener(this);

        chooseHostButton.setActionCommand("choose");
        chooseHostButton.addActionListener(this);

        optionButton.setActionCommand("options");
        optionButton.addActionListener(this);

        // panel layout
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        mainPanel.setLayout(gb);

        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.fill = GridBagConstraints.BOTH;

        int nextLine = GridBagConstraints.REMAINDER;

        insert(gb, gbc, hostLabel, 1, 1);
        insert(gb, gbc, hostCombo, 1, 1);
        insert(gb, gbc, chooseHostButton, 1, nextLine);

        JLabel portLabel = new JLabel("Port");
        portLabel.setFont(normFont);
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portPanel.setBorder(BorderFactory.createEtchedBorder());
        portPanel.add(portCheckBox);
        portPanel.add(portTextField);

        portCheckBox.setSelected(true);
        portTextField.setEnabled(false);
        portCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (portCheckBox.isSelected()) {
                    portTextField.setEnabled(false);
                } else {
                    portTextField.setEnabled(true);
                }
            }
        });

        encodingCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (encodingCheckBox.isSelected()) {
                    encodingCombo.setEnabled(true);
                } else {
                    encodingCombo.setEnabled(false);
                }
            }
        });
        encodingCheckBox.setSelected(false);
        encodingCombo.setEnabled(false);

        JPanel encodingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        encodingPanel.add(encodingCheckBox);
        encodingPanel.add(encodingCombo);
        encodingPanel.add(GuiHelper.createExperimentalLabel());

        String[] encodings = EncodingUtils.getDefaultEncodings();
        for (int i = 0; i < encodings.length; i++) {
            encodingCombo.addItem(encodings[i]);
        }

        final JPanel caseInsensitivePanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT));
        caseInsensitivePanel.add(caseInsensitiveCheckBox);
        caseInsensitivePanel.add(GuiHelper.createExperimentalLabel());

        final JPanel keepAlivePanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT));
        keepAlivePanel.add(keepAliveCheckbox);

        JPanel advPanel = new JPanel();
        advPanel.setLayout(new BoxLayout(advPanel, BoxLayout.Y_AXIS));
        advPanel.add(encodingPanel);
        advPanel.add(caseInsensitivePanel);
        advPanel.add(keepAlivePanel);

        insert(gb, gbc, portLabel, 1, 1);
        insert(gb, gbc, portPanel, 1, 1);
        insert(gb, gbc, new JPanel(), 1, nextLine);

        insert(gb, gbc, protoLabel, 1, 1);
        insert(gb, gbc, protoCombo, 1, 1);
        insert(gb, gbc, optionButton, 1, nextLine);

        insert(gb, gbc, userLabel, 1, 1);
        insert(gb, gbc, userField, 1, nextLine);

        insert(gb, gbc, passwdLabel, 1, 1);
        insert(gb, gbc, passwdField, 1, nextLine);

        // insert(gb, gbc, encodingPanel, 1, nextLine);
        // insert(gb, gbc, caseInsensitiveCheckBox, 1, nextLine);
        insert(gb, gbc, advPanel, 1, nextLine);

        /* ButtonPanel settings ----------------------------------------------- */
        buttonPanel = new JPanel();

        okayButton.setActionCommand("okay");
        okayButton.addActionListener(this);
        okayButton.addKeyListener(this);

        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        cancelButton.addKeyListener(this);

        buttonPanel.add(okayButton);
        buttonPanel.add(cancelButton);

        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        setModal(true);
        setResizable(false);

        pack();

        // to prevent layout-manager work, buh
        hostLabel.setMinimumSize(hostLabel.getSize());
        userLabel.setMinimumSize(userLabel.getSize());
        passwdLabel.setMinimumSize(passwdLabel.getSize());

        // it's not a trick - it's to prohibit all layout manager actions
        passwdLabel.setPreferredSize(new Dimension(passwdLabel.getWidth(), passwdLabel
                .getHeight()));

        hostLabel.setMaximumSize(hostLabel.getSize());
        userLabel.setMaximumSize(userLabel.getSize());
        passwdLabel.setMaximumSize(passwdLabel.getSize());

        hostCombo.setMinimumSize(hostCombo.getSize());
        userField.setMinimumSize(userField.getSize());
        passwdField.setMinimumSize(passwdField.getSize());

        hostCombo.setMaximumSize(hostCombo.getSize());
        userField.setMaximumSize(userField.getSize());
        passwdField.setMaximumSize(passwdField.getSize());

        // switch to the normal view
        passwdLabel.setFont(normFont);

        // enable default protocol view
        setProtocolDialogElements();

        // Deprecated. As of 1.4, replaced by FocusTraversalPolicy
        // hostField.setNextFocusableComponent(userField);
        // use :
        TFocusManager fm = new TFocusManager(6);

        // first focus element
        fm.setNext(hostCombo.getEditorTextField());
        fm.setNext(protoCombo);
        fm.setNext(userField);
        // last focus element
        fm.setNext(passwdField);
        fm.setNext(okayButton);
        fm.setNext(cancelButton);
        setFocusTraversalPolicy(fm);

    }

    private void insert(GridBagLayout gb, GridBagConstraints gbc,
            Component obj, int height, int width) {
        gbc.gridheight = height;
        gbc.gridwidth = width;
        gb.setConstraints(obj, gbc);
        mainPanel.add(obj);
    }

    private void resetPluginInfo(ConnectionPlugin plugin) {

        activePlugin = plugin;

        if (plugin.hasConnectionOptions()) {
            optionButton.setEnabled(true);
        } else {
            optionButton.setEnabled(false);
        }
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

    public String getPasswd() {
        return String.valueOf(passwdField.getPassword());
    }

    public void setPasswd(String passwd) {
        passwdField.setText(passwd);
    }

    public int getPort() {
        String portText = portTextField.getText();
        int port = -1;
        if (portText.equals("") || portCheckBox.isSelected()) {
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
            portCheckBox.setSelected(true);
            if (activePlugin.isLocalConnection()) {
                portTextField.setText("");
            } else {
                portTextField.setText(Integer.toString(activePlugin
                        .getDefaultPort()));
            }
        } else {
            portTextField.setText(Integer.toString(port));
            if (port == activePlugin.getDefaultPort()) {
                portCheckBox.setSelected(true);
            } else {
                portCheckBox.setSelected(false);
            }
        }
    }

    public boolean hasUserEncoding() {
        return encodingCheckBox.isSelected();
    }

    public boolean isCaseInsensitive() {
        return caseInsensitiveCheckBox.isSelected();
    }

    public boolean isKeepAliveSet() {
        return keepAliveCheckbox.isSelected();
    }

    public String getUserEncoding() {
        return (String) encodingCombo.getSelectedItem();
    }

    public ConnectionPlugin getPlugin() {
        return activePlugin;
    }

    public void setPlugin(ConnectionPlugin plugin) {
        for (int i = 0; i < protoCombo.getItemCount(); i++) {
            if (protoCombo.getItemAt(i).equals(plugin.getDescription())) {
                protoCombo.setSelectedIndex(i);
                break;
            }
        }
        setProtocolDialogElements();
        resetPluginInfo(plugin);
    }

    // activates or disables some dialog input fields
    protected void setProtocolDialogElements() {
        if (activePlugin.isLocalConnection()) {
            hostCombo.setEnabled(false);
            userField.setEnabled(false);
            passwdField.setEnabled(false);

            hostCombo.setEditorText("localhost");
            userField.setText(TMiscTool.getUserName());
            passwdField.setText("");

            portCheckBox.setEnabled(false);
            portCheckBox.setSelected(true);
            portTextField.setText("");
        } else {
            hostCombo.setEnabled(true);
            // Jawinton
            if (activePlugin != null) {
            	userField.setEnabled(activePlugin.requiresUsername());
            } else {
            	userField.setEnabled(true);
            }

            LOGGER.info("password required " + activePlugin.requiresPassword());

            if (activePlugin != null && !activePlugin.requiresPassword()) {
                passwdField.setEnabled(false);
            } else {
                passwdField.setEnabled(true);
            }

            if (activePlugin.requiresPort()) {
                portCheckBox.setEnabled(true);
                portCheckBox.setSelected(true);
                portTextField.setText(Integer.toString(activePlugin
                        .getDefaultPort()));
            }

        }

        resetPluginInfo(activePlugin);
    }

    public int showDialog() {
        result = CD_RESULT_NOTHING; // no action
        setLocation(TWindowPositioner.center(this.getOwner(), this));
        setFocusElement();
        setVisible(true);
        return result; // the result is set by action methods for buttons
    }

    private void setFocusElement() {
        if (focusElement == null) {
            // focus ok button to quickly connect to local file system
            okayButton.requestFocus();
        } else {
            // LOGGER.info("focus to " + focusElement);
            focusElement.requestFocus();
        }
    }

    public void setFocusElement(final int dialogElement) {
        focusElement = passwdField;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("okay")) {
            okayButtonActionPerformed(e);
        } else if (cmd.equals("cancel")) {
            cancelButtonActionPerformed(e);
        } else if (cmd.equals("choose")) {
            ChooseHostButton_actionPerformed(e);
        } else if (cmd.equals("options")) {
            PluginOptionPanel optPanel = activePlugin
                    .getConnectionOptionPanel();
            if (optPanel != null) {
                PluginOptionDialog optionDiag = new PluginOptionDialog(this,
                        optPanel);
                optionDiag.setVisible(true);
                // setProtocolDialogElements();
            }

        }

    }

    private void okayButtonActionPerformed(ActionEvent e) {
        result = CD_RESULT_OKAY;
        this.dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        result = CD_RESULT_CANCEL;
        this.dispose();
    }

    private void ChooseHostButton_actionPerformed(ActionEvent e) {
        ConParams params = new ConParams();
        UserHostsDialog hostDialog = new UserHostsDialog(TConnectDialog.this,
                params);
        hostDialog.setVisible(true);

        if (!params.hostname.equals("")) {
            setHost(params.hostname);
            setUser(params.username);
            setPlugin(ConnectionPluginManager
                    .getConnectionModelInstance(params.protocol));
            String password = "";
            if( params.password != null ) {
                password = params.password;
            }
            passwdField.setText(password);
            setPort(params.port);
            // let's give the password field the focus since
            // only the password needs to be entered
            passwdField.requestFocus();
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            Object obj = e.getSource();
            if (obj == userField) {
                dispatchEvent(new FocusEvent(passwdField,
                        FocusEvent.FOCUS_GAINED));
            } else if (obj == passwdField) {
                // this.dispatchEvent( new FocusEvent(OkayButton,
                // FocusEvent.FOCUS_GAINED) );
                // simulate the button click
                // clean code ??? - to create an ActionEvent has no effect, why
                // ?
                actionPerformed(new ActionEvent(okayButton,
                        ActionEvent.ACTION_PERFORMED, "okay"));
            } else if (obj == okayButton) {
                // clean code ?
                actionPerformed(new ActionEvent(okayButton,
                        ActionEvent.ACTION_PERFORMED, "okay"));
            } else if (obj == cancelButton) {
                actionPerformed(new ActionEvent(cancelButton,
                        ActionEvent.ACTION_PERFORMED, "cancel"));
            }
        } // end VK_ENTER

    }

    // -----------------------------------------------------------------------------

    public void focusGained(FocusEvent e) {
        Object obj = e.getSource();

        if (obj == hostCombo
                || obj == hostCombo.getEditor().getEditorComponent()) {
            hostLabel.setFont(boldFont);
        } else if (obj == protoCombo) {
            protoLabel.setFont(boldFont);
        } else if (obj == userField) {
            userLabel.setFont(boldFont);
        } else if (obj == passwdField) {
            passwdLabel.setFont(boldFont);
        }
    }

    public void focusLost(FocusEvent e) {
        Object obj = e.getSource();

        if (obj == hostCombo
                || obj == hostCombo.getEditor().getEditorComponent()) {
            hostLabel.setFont(normFont);
        } else if (obj == protoCombo) {
            protoLabel.setFont(normFont);
        } else if (obj == userField) {
            userLabel.setFont(normFont);
        } else if (obj == passwdField) {
            passwdLabel.setFont(normFont);
        }
    }

    protected void searchForMatchingHostEntries(String host) {
        // try to find matching entry "host" in config
        for (int i = 0; i < conConf.length; i++) {
            if (conConf[i].getHostName().compareTo(host) == 0) {
                String username = conConf[i].getUserName();
                int protocol = conConf[i].getProtocol();
                userField.setText(username);
                protoCombo.setSelectedItem(ConnectionPluginManager
                        .getConnectionModelInstance(protocol).getDescription());
                dispatchEvent(new FocusEvent(passwdField,
                        FocusEvent.FOCUS_GAINED));
                return;
            }
        }
        this.dispatchEvent(new FocusEvent(userField, FocusEvent.FOCUS_GAINED));
    }

    public void prefixItemChosen(PrefixItemEvent event) {
        String host = event.getStringData();
        assert (host != null);
        searchForMatchingHostEntries(host);
    }

    // -----------------------------------------------------------------------------

    /*
     * a focus manager without error handling !!! required since java 1.4
     */

    class TFocusManager extends FocusTraversalPolicy {

        private int compCount; // how many components ?
        private int maxCompCount; // max components in array
        private Component[] components; // all handled components

        public TFocusManager(int maxFocusObjects) {
            compCount = 0;
            maxCompCount = maxFocusObjects;
            components = new Component[maxFocusObjects];
        }

        public Component getComponentAfter(Container focusCycleRoot,
                Component aComponent) {
            Component back = null;
            int in = findComponent(aComponent);

            if (in == compCount - 1) {
                back = components[0]; /* last entry */
            } else if ((in < compCount) && (in > -1)) {
                back = components[in + 1];
            }
            return back;
        }

        public Component getLastComponent(Container focusCycleRoot) {
            return components[compCount];
        }

        public Component getFirstComponent(Container focusCycleRoot) {
            return components[0];
        }

        public Component getDefaultComponent(Container focusCycleRoot) {
            return components[0];
        }

        public Component getComponentBefore(Container focusCycleRoot,
                Component aComponent) {
            Component back = null;
            int in = findComponent(aComponent);

            if (in > 0) {
                back = components[in - 1];
            } else if (in == 0) {
                back = components[compCount - 1]; // first entry /
            }
            return back;
        }

        // own
        private final int findComponent(Component aComponent) {
            int t = 0;
            int back = -1;

            // enumerate the component array
            while (t < compCount) {
                // found ?
                if (aComponent == components[t]) {
                    back = t;
                    t = compCount;
                } else {
                    t++;
                }
            }
            return back;
        }

        // own
        public final void setNext(Component aComponent) {
            if (compCount < maxCompCount) {
                components[compCount] = aComponent;
                compCount++;
            }
        }
    }

    static class PluginOptionDialog extends JDialog implements ActionListener {

        private static final long serialVersionUID = 1L;
        private final JDialog owner;
        private final PluginOptionPanel optPanel;

        private JButton okButton = new JButton(LanguageBundle.getInstance()
                .getMessage("label.ok"));

        public PluginOptionDialog(final JDialog owner,
          final PluginOptionPanel optPanel) {
            super(owner, true);
            this.owner = owner;
            this.optPanel = optPanel;
            init();
        }

        protected void init() {

            if( optPanel.getPanelName() != null ) {
              setTitle(optPanel.getPanelName());
            }
          
            okButton.setActionCommand("ok");
            okButton.addActionListener(this);
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(okButton);

            JPanel mainPane = new JPanel();
            mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
            mainPane.add(optPanel.getJComponent());
            mainPane.add(buttonPanel);

            getContentPane().add(mainPane);

            pack();

            setLocationRelativeTo(owner);
        }

        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.equals("ok")) {
                boolean dataOK = optPanel.hasValidData();
                if (dataOK) {
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, optPanel
                            .getErrorMessage(), LanguageBundle.getInstance()
                            .getMessage("error.invalid_data"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
