package net.sf.jfilesync.settings;

public interface IPasswordStore {

    public String encodePassword(final String plainPassword,
            final String plainMasterPasswd) throws PasswordStoreException;

    public String decodePassword(final String encodedPassword,
            final String plainMasterPasswd) throws PasswordStoreException;

}
