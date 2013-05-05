package net.sf.jfilesync.settings;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.ConnectionConfig;

public class MasterPasswordHandler {

    private final static Logger LOGGER = Logger
            .getLogger(MasterPasswordHandler.class.getName());

    private String plainMasterPassword;

    private static MasterPasswordHandler singleton;

    private MasterPasswordHandler() {

    }

    public static synchronized MasterPasswordHandler getInstance() {
        if (singleton == null) {
            singleton = new MasterPasswordHandler();
        }
        return singleton;
    }

    public boolean setOrChangePassword(Component parent)
            throws PasswordStoreException {

        boolean changedCorrectly = false;

        String oldEnryptedPassword = getMasterPassword();
        String enteredPlainPassword = null;

        boolean passwdOk = false;
        boolean canceled = false;
        boolean noOldPassword = false;

        if( oldEnryptedPassword == null || oldEnryptedPassword.equals("") ) {
            // no password set
            passwdOk = true;
            noOldPassword = true;
        } else {
            do {
                // ask for old passwd
                enteredPlainPassword = showMasterPasswordDialog(parent,
                        "Enter old master password");
                if( enteredPlainPassword == null ) {
                    canceled = true;
                } else {
                    String enteredEncrypedPasswd = CapivaraPasswordStore
                            .getInstance().encryptMasterPassword(
                                    enteredPlainPassword);

                    LOGGER.fine("old enc master: " + oldEnryptedPassword
                            + " enc master: " + enteredEncrypedPasswd);

                    if( enteredEncrypedPasswd.equals(oldEnryptedPassword) ) {
                        passwdOk = true;
                    } else {
                        JOptionPane.showMessageDialog(parent, "password incorrect");
                    }
                }
            }
           while( !passwdOk && !canceled);
        }

        if( canceled ) {
            return changedCorrectly;
        }

        if( ! passwdOk ) {
            LOGGER.severe("sanity check : bug");
            return changedCorrectly;
        }

        boolean newPasswdOk = false;
        canceled = false;
        String newPlainPasswd = null;

        do {
            // ask for new passwd
            newPlainPasswd = showPasswordRepeatDialog(parent, "Enter new master password");
            if( newPlainPasswd == null ) {
                canceled = true;
            } else {
                newPasswdOk = checkPassword(newPlainPasswd);
                if( ! newPasswdOk ) {
                    JOptionPane.showMessageDialog(parent,
                            "password must be at least 8 chars long");
                }
            }

        } while( ! newPasswdOk && ! canceled );

        if( canceled ) {
            return changedCorrectly;
        }

        if( ! passwdOk ) {
            return changedCorrectly;
        }

        final String newEncrypedMasterPasswd = CapivaraPasswordStore
                .getInstance().encryptMasterPassword(newPlainPasswd);

        try {
            changeMasterPassword(newEncrypedMasterPasswd);
            if( noOldPassword == false ) {
                updateConnectionPasswords(enteredPlainPassword, newPlainPasswd);
            }
            plainMasterPassword = newPlainPasswd;
            changedCorrectly = true;

        } catch(SettingsTypeException e) {
            e.printStackTrace();
        }


        return changedCorrectly;
    }

    private void updateConnectionPasswords(String oldPlainPassword,
            String newPlainPassword) throws PasswordStoreException {

        // and recode all other passwords
        final ConnectionConfig[] cons = MainWin.config
                .getSavedConnections();
        if (cons == null) {
            LOGGER.warning("cons is null. please report bug");
        } else {

            for (final ConnectionConfig con : cons) {

                final String oldEncConPasswd = con.getPassword();
                if( oldEncConPasswd != null ) {
                    final String oldDecConPasswd = CapivaraPasswordStore
                            .getInstance().decodePassword(oldEncConPasswd,
                                    oldPlainPassword);
                    final String newEncConPasswd = CapivaraPasswordStore
                            .getInstance().encodePassword(oldDecConPasswd,
                                    newPlainPassword);

                    con.setPassword(newEncConPasswd);
                    MainWin.config.changeConnectionData(con);
                }
            }

        }

    }

    private void changeMasterPassword(String encodedNewPassword)
            throws PasswordStoreException, SettingsTypeException {

        MainWin.config.getProgramSettings().setProgramOption(
                TProgramSettings.OPTION_MASTER_PASSWORD,
                encodedNewPassword);
    }


    private String showPasswordRepeatDialog(Component parent, String string) {
        String password = null;

        JPanel panel2 = new JPanel();
        JPasswordField passwdFld1 = new JPasswordField(10);
        JPasswordField passwdFld2 = new JPasswordField(10);
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel2.add(passwdFld1);
        panel2.add(passwdFld2);


        while (true) {

            int option = JOptionPane.showConfirmDialog(parent, panel2, string,
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.CANCEL_OPTION) {
                password = null;
                break;
            }

            final String passwd1 = new String(passwdFld1.getPassword(), 0,
                    passwdFld1.getPassword().length);
            final String passwd2 = new String(passwdFld2.getPassword(), 0,
                    passwdFld2.getPassword().length);

            LOGGER.info("passwd1 : " + passwd1 + " , passwd2: " + passwd2);

            if (!passwd1.equals(passwd2)) {
                JOptionPane
                        .showMessageDialog(parent, "passwords do not match");
            } else {
                password = passwd1;
                break;
            }
        }


        return password;
    }

    private boolean checkPassword(String newPlainPasswd) {
        boolean okay = true;

        if( newPlainPasswd == null ) {
            okay = false;
        } else if( newPlainPasswd.length() < 8 ) {
            okay = false;
        }
        return okay;
    }

    private String getMasterPassword() {
        String passwd = null;
        try {
            passwd = MainWin.config.getProgramSettings().getStringOption(
                    TProgramSettings.OPTION_MASTER_PASSWORD);
        } catch (SettingsTypeException e) {
            e.printStackTrace();
        }
        return passwd;
    }


    private String showMasterPasswordDialog(Component parent, String message) {
        String passwd = null;

        JPasswordField passwordField = new JPasswordField(10);
        final int option = JOptionPane.showConfirmDialog(parent, passwordField, message,
                JOptionPane.OK_CANCEL_OPTION);

        if( option == JOptionPane.OK_OPTION ) {
            passwd = new String(passwordField.getPassword(), 0, passwordField
                    .getPassword().length);
        } else {
            passwd = null;
        }

        return passwd;
    }


    public String requestMasterPassword(Component parent)
            throws PasswordStoreException {

        if (plainMasterPassword != null) {
            return plainMasterPassword;
        }

        boolean canceled = false;
        boolean passwdOk = false;

        final String storedMasterPasswd = getMasterPassword();
        if (storedMasterPasswd == null) {
            LOGGER.warning("no master password");
        } else {

            do {

                final JPasswordField passwordField = new JPasswordField(10);
                int opt = JOptionPane.showConfirmDialog(parent, passwordField,
                        "Enter master password", JOptionPane.OK_CANCEL_OPTION);

                if (opt == JOptionPane.CANCEL_OPTION) {
                    canceled = true;
                } else {
                    plainMasterPassword = new String(passwordField
                            .getPassword(), 0,
                            passwordField.getPassword().length);

                    final String encodedPasswd = CapivaraPasswordStore
                            .getInstance().encryptMasterPassword(
                                    plainMasterPassword);

                    if (encodedPasswd.equals(storedMasterPasswd)) {
                        passwdOk = true;
                    } else {
                        JOptionPane.showMessageDialog(parent,
                                "password incorrect");
                    }

                }

            } while (!canceled && !passwdOk);

            if (canceled) {
                plainMasterPassword = null;
            }

        }

        return plainMasterPassword;
    }

}
