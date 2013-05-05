package net.sf.jfilesync.plugins.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.io.CapivaraFileInputStream;
import net.sf.jfilesync.io.CapivaraFileOutputStream;
import net.sf.jfilesync.util.TPathControlInterface;

public class AbstractConnectionProxyImpl implements AbstractConnectionProxy {

    private AbstractConnectionProxy proxy;

    public AbstractConnectionProxyImpl(AbstractConnectionProxy proxy) {
        this.proxy = proxy;
    }

    protected AbstractConnectionProxy getAbstractConnectionProxy() {
        return proxy;
    }

    @Override
    public void abort() throws IOException {
        proxy.abort();
    }

    @Override
    public void chdir(String path) throws IOException {
        proxy.chdir(path);
    }

    @Override
    public void connect() throws PluginConnectException {
        proxy.connect();
    }

    @Override
    public void disconnect() {
        proxy.disconnect();
    }

    @Override
    public boolean exists(String path) {
        return proxy.exists(path);
    }

    @Override
    public void get(String remoteFileName, OutputStream outstream)
            throws IOException {
        proxy.get(remoteFileName, outstream);
    }

    @Override
    public String getCurrentPath() {
        return proxy.getCurrentPath();
    }

    @Override
    public String getEncodedFileName(String fileName) throws IOException {
        return proxy.getEncodedFileName(fileName);
    }

    @Override
    public CapivaraFileInputStream getFileInputStream(String fileName)
            throws IOException {
        return proxy.getFileInputStream(fileName);
    }

    @Override
    public CapivaraFileOutputStream getFileOutputStream(String fileName)
            throws IOException {
        return proxy.getFileOutputStream(fileName);
    }

    @Override
    public TPathControlInterface getPathControlInstance() {
        return proxy.getPathControlInstance();
    }

    @Override
    public ConnectionPlugin getPlugin() {
        return proxy.getPlugin();
    }

    @Override
    public boolean isConnected() {
        return proxy.isConnected();
    }

    @Override
    public boolean isFile(String path) throws IOException {
        return proxy.isFile(path);
    }

    @Override
    public boolean isLink(String path) throws IOException {
        return proxy.isLink(path);
    }

    @Override
    public boolean isLocalConnection() {
        return proxy.isLocalConnection();
    }

    @Override
    public TFileData ls() throws IOException {
        return proxy.ls();
    }

    @Override
    public TFileData ls(String path) throws IOException {
        return proxy.ls(path);
    }

    @Override
    public void mkdir(String dirName) throws IOException {
        proxy.mkdir(dirName);
    }

    @Override
    public void mkdirs(String dirName) throws IOException {
        proxy.mkdirs(dirName);
    }

    @Override
    public void put(InputStream instream, String remoteFileName)
            throws IOException {
        proxy.put(instream, remoteFileName);
    }

    @Override
    public String pwd() throws IOException {
        return proxy.pwd();
    }

    @Override
    public void remove(String fileName) throws IOException {
        proxy.remove(fileName);
    }

    @Override
    public void rename(String oldAbsoluteFileName, String newName)
            throws IOException {
        proxy.rename(oldAbsoluteFileName, newName);
    }

    @Override
    public void rmdir(String dirName) throws IOException {
        proxy.rmdir(dirName);
    }

    @Override
    public void setHidden(String path) throws IOException {
        proxy.setHidden(path);
    }

    @Override
    public void setModificationTime(String fileName, long mtime)
            throws IOException {
        proxy.setModificationTime(fileName, mtime);
    }

    @Override
    public void setPermissions(String fileName, int permissions)
            throws IOException {
        proxy.setPermissions(fileName, permissions);
    }

}
