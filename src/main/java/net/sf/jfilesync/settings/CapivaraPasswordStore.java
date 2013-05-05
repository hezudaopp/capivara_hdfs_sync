package net.sf.jfilesync.settings;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.sshtools.j2ssh.util.Base64;

public class CapivaraPasswordStore implements IPasswordStore {

    private final static Logger LOGGER = Logger
            .getLogger(CapivaraPasswordStore.class.getName());

    private static CapivaraPasswordStore passwdStore;
    private Cipher cipher;

    private MessageDigest digest;

    public static synchronized CapivaraPasswordStore getInstance()
            throws PasswordStoreException {

        try {
            if (passwdStore == null) {
                passwdStore = new CapivaraPasswordStore();
            }
        } catch (NoSuchPaddingException e) {
            throw new PasswordStoreException(e);
        } catch (NoSuchProviderException e) {
            throw new PasswordStoreException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new PasswordStoreException(e);
        }
        return passwdStore;
    }

    private CapivaraPasswordStore() throws NoSuchPaddingException,
            NoSuchProviderException, NoSuchAlgorithmException {

        //cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        digest = MessageDigest.getInstance("MD5");

    }

    private SecretKey createKey(final String password) throws PasswordStoreException {
        SecretKey pKey = null;
        try {
            byte[] bytes = password.getBytes();
            // final DESKeySpec pass = new DESKeySpec(bytes);

            byte[] correctkey = null;
            if( bytes.length < 24 ) {
                correctkey = new byte[24];
                for(int i=0; i<bytes.length; i++) {
                    correctkey[i] = bytes[i];
                }
                for(int i=bytes.length; i<correctkey.length; i++) {
                    correctkey[i] = 0x0;
                }
            } else {
                correctkey = bytes;
            }

            //final DESedeKeySpec pass = new DESedeKeySpec(bytes);
            final DESedeKeySpec pass = new DESedeKeySpec(correctkey);
            // SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
            pKey = skf.generateSecret(pass);
        } catch (InvalidKeyException e) {
            throw new PasswordStoreException(e);
        } catch (InvalidKeySpecException e) {
            throw new PasswordStoreException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new PasswordStoreException(e);
        }
        return pKey;
    }

    @Override
    public String decodePassword(final String encodedPassword,
            final String masterPassword) throws PasswordStoreException {

        String decodedPassword = null;

        final SecretKey k = createKey(masterPassword);

        decodedPassword = decrypt(encodedPassword, k);

        return decodedPassword;
    }

    @Override
    public String encodePassword(final String plainPassword,
            final String plainMasterPasswd) throws PasswordStoreException {

        String encodedPasswd = null;

        LOGGER.fine("plain passwd : " + plainPassword
                + " plain master passwd : " + plainMasterPasswd);

        final SecretKey k = createKey(plainMasterPasswd);

        encodedPasswd = encrypt(plainPassword, k);

        return encodedPasswd;
    }

    private String encrypt(String word, SecretKey pKey) throws PasswordStoreException {

        String encryptedWord = null;

        final byte[] wordBytes = word.getBytes();

        try {

            cipher.init(Cipher.ENCRYPT_MODE, pKey);
            byte[] cipherText = cipher.doFinal(wordBytes);
            encryptedWord = Base64.encodeBytes(cipherText, true);

        } catch (InvalidKeyException e) {
            throw new PasswordStoreException(e);
        } catch (IllegalBlockSizeException e) {
            throw new PasswordStoreException(e);
        } catch (BadPaddingException e) {
            throw new PasswordStoreException(e);
        }

        return encryptedWord;
    }

    private String decrypt(String encodedString, SecretKey pKey) throws PasswordStoreException {
        String password = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, pKey);
            byte[] decodedBytes = Base64.decode(encodedString);
            byte[] text = cipher.doFinal(decodedBytes);
            password = new String(text);

        } catch (IllegalBlockSizeException e) {
            throw new PasswordStoreException(e);
        } catch (BadPaddingException e) {
            throw new PasswordStoreException(e);
        } catch (InvalidKeyException e) {
            throw new PasswordStoreException(e);
        } 

        return password;
    }

    public String encryptMasterPassword(String plainMasterPassword) {
        String encryptedPassword = null;

        if (plainMasterPassword != null) {
            byte[] digestedPasswd = digest.digest(plainMasterPassword
                    .getBytes());
            encryptedPassword = Base64.encodeBytes(digestedPasswd, true);
        }

        return encryptedPassword;
    }


}
