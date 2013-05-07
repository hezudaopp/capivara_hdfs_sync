/*
 * Jawinton
 */

package net.sf.jfilesync.plugins.net.items;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;

import net.sf.jfilesync.engine.FilePermissions;
import net.sf.jfilesync.engine.TErrorHandling;
import net.sf.jfilesync.engine.TFileAttributes;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.util.TPathControlFactory;
import net.sf.jfilesync.util.TPathControlInterface;

public class THdfs_plugin implements ConnectionPlugin{
	
	private TConnectionData conData;
	private final static Logger LOGGER = Logger.getLogger(THdfs_plugin.class.getName());
	private FileSystem fs;
	private Configuration conf;
	
	// for aborting put/get
	private OutputStream outstream;
	private InputStream instream;
	private boolean abort = false;

	public THdfs_plugin () {
	}
	
	@Override
	public boolean isProvided(int feature) {
		boolean ret = false;

	    switch (feature) {
	    case PROVIDES_PERMISSION_HANDLING:
	    case PROVIDES_FILETRANSFER:
	      ret = true;
	      break;
	    }

	    return ret;
	}

	@Override
	public String getName() {
		return "Hadoop Distributed FileSystem HDFS plugin";
	}

	@Override
	public String getDescription() {
		return "HDFS - Hadoop Distributed FileSystem";
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public int getMinorVersion() {
		return 1;
	}

	@Override
	public String getLicense() {
		return "Apache License 2.0";
	}

	/**
	 * There is a bug here. When user disconnect and then connect again, user will login as the user last time (the conData changed for the second time).
	 * Even though I have changed my login user name for the second time. But If I restart the app, it works well.
	 * FIXED! http://stackoverflow.com/questions/15941108/hdfs-access-from-remote-host-through-java-api-user-authentication
	 */
	@Override
	public void connect(TConnectionData connectData)
			throws PluginConnectException {
		this.conData = connectData;
	    final String hostname = conData.getHost();
	    final int port        = conData.getPort();
	    final String uri = "hdfs://" + hostname + ":" +port;
	    final String username = conData.getUser();
//	    System.setProperty("HADOOP_USER_NAME", username);
//	    conf = new Configuration();
	    
	    LOGGER.info("trying to connect to :" + hostname);
	    UserGroupInformation ugi
        = UserGroupInformation.createRemoteUser(username);
	    try {
			ugi.doAs(new PrivilegedExceptionAction<Void>() {

				@Override
				public Void run() throws Exception {
					conf = new Configuration();
					conf.set("hadoop.job.ugi", username);
					fs = FileSystem.get(URI.create(uri), conf);
					return null;
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new PluginConnectException(TErrorHandling.ERROR_CONNECTION_FAILURE, 
					e.getMessage());
		}
	    LOGGER.info("hdfs connect done");
	}

	@Override
	public TConnectionData getTConnectionData() {
		return conData;
	}

	@Override
	public void disconnect() {
		System.clearProperty("HADOOP_USER_NAME");
		try {
			if (fs!= null) {
				fs.close();
				fs = null;
			}
//			FileSystem.closeAllForUGI(UserGroupInformation.getLoginUser());
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
	}

	@Override
	public boolean isConnected() {
		boolean isConnected = false;
		if (fs != null) {
			isConnected = true;
			try {
				ls();
			} catch (Exception e) {
				isConnected = false;
			}
		}
		return isConnected;
	}

	@Override
	public boolean isLocalConnection() {
		return false;
	}

	@Override
	public TPathControlInterface getPathControl() {
		return TPathControlFactory.getPathControlInstance("net");
	}

	@Override
	public TFileData ls() throws IOException {
		return ls(fs.getWorkingDirectory().toUri().getPath());
	}

	@Override
	public TFileData ls(String path) throws IOException {
		TFileData fileData = new TFileData();
		FileStatus[] status = null;

		if (path == null) {
			throw new NullPointerException("path is null");
		}

		chdir(path);
		status = fs.listStatus(new Path("."));

		if (status != null) {
			for (int i = 0; i < status.length; i++) {
				if (status[i] == null) {
					LOGGER.warning("Problem while listing directory. Possibly missing files");
					continue;
				} else if (status[i].getPath().getName().compareTo("..") == 0
						|| status[i].getPath().getName().compareTo(".") == 0) {
					continue; // filter . and ..
				} else {
					TFileProperties tfile = extractFileProperties(status[i],
							status);
					fileData.addFileProperties(tfile);
				}
			}
		}
		return fileData;
	}
	
	protected TFileProperties extractFileProperties(FileStatus file,
			FileStatus[] filesInDir) throws IOException {
		TFileProperties prop = new TFileProperties();
		String filename = file.getPath().getName();
		prop.setFileName(filename);

		final String cwd = fs.getWorkingDirectory().toUri().getPath();
		String fname = null;
		if (cwd.endsWith("/")) {
			fname = cwd + filename;
		} else {
			fname = cwd + "/" + filename;
		}
		prop.setAbsoluteFileName(fname);

		if (filename.startsWith(".")) {
			prop.setHiddenFlag(true);
		}

		// There is a little problem with ftp.getSize(), because it's sometimes
		// 0
		prop.setFileSize(new BigInteger(Long.toString(file.getLen())));
		// System.out.println(file.getName() + " , " + file.getTimestamp());
		prop.setFileModTime(file.getModificationTime());
		// System.out.println("file: " + fname);
		// System.out.println("isDirectory: " + file.isDirectory());
		prop.setDirectoryFlag(file.isDirectory());
		prop.setLinkFlag(file.isSymlink());

		int permissions = 0;

		permissions |= file.isDirectory() ? FilePermissions.S_IFDIR : 0;
		permissions |= file.isSymlink() ? FilePermissions.S_IFLNK : 0;
		permissions |= file.getPermission().toShort();

		final TFileAttributes attr = new TFileAttributes();
		attr.setPermissions(permissions);
		prop.setAttributes(attr);

		/*
		 * what needs to be done is implement caching of directories which have
		 * to be listed for link detection implement recursive link detection
		 * for links to links SaHu July 2006
		 */

		/*
		 * if( file.isSymbolicLink() ) { System.out.println("link target : " +
		 * file.getLink()); }
		 */

		// if( file.isSymbolicLink() ) {
		// // check if link points to dir
		// final String linkTarget = file.getLink();
		// final String linkTargetBaseName =
		// getPathControl().basename(linkTarget);
		// //System.out.println("link target basename: " + linkTargetBaseName);
		// if( linkTarget != null ) {
		// String linkContaingPath =
		// getPathControl().getPathLevelUp(linkTarget);
		// FTPFile[] targetFiles = null;
		// if( linkContaingPath.equals("") || linkContaingPath.equals(cwd) ) {
		// targetFiles = filesInDir;
		// } else {
		// //System.out.println("check dir : " + linkContaingPath);
		// targetFiles = ftpClient.listFiles(linkContaingPath);
		// }
		//
		//
		// if( targetFiles != null ) {
		// for(int i=0; i<targetFiles.length; i++) {
		// //System.out.println("> " + targetFiles[i].getName());
		// if( targetFiles[i].getName().equals(linkTargetBaseName) ) {
		// if( targetFiles[i].isDirectory() ) {
		// prop.setDirectoryFlag(true);
		// }
		// break;
		// }
		// }
		// }
		// }
		// }

		if (file.isSymlink()) {
			Path path = file.getSymlink();
			if (fs.isDirectory(path))
				prop.setDirectoryFlag(true);
		}

		return prop;
	}


	@Override
	public void chdir(String path) throws IOException {
		if (!path.equals(".")) {
			fs.setWorkingDirectory(new Path(path));
		}
	}

	@Override
	public String getAbsolutePath() {
		String result = "";
		Path path = fs.getWorkingDirectory();
    	String pwd = path.toUri().getPath();
    	if (pwd != null) result = pwd;
	    return result;
	}

	@Override
	public boolean exists(String path) {
		try {
			return fs.exists(new Path(path));
		} catch (IOException e) {
			LOGGER.warning(e.getMessage());
			return false;
		}
	}

	@Override
	public void put(InputStream instream, String remoteFileName)
			throws IOException {
		synchronized (this) {
			abort = false;
			this.instream = instream;
		}
		try {
			OutputStream out = fs.create(new Path(remoteFileName));
			IOUtils.copyBytes(instream, out, 4096, true);
		} catch (IOException e) {
			if (!abort) {
				throw e;
			} else {
				checkConnectionState();
			}
		}

	}

	@Override
	public void get(String remoteFileName, OutputStream outstream)
			throws IOException {
		synchronized (this) {
			abort = false;
			this.outstream = outstream;
		}
		InputStream in = null;
		try {
			in = fs.open(new Path(remoteFileName));
			IOUtils.copyBytes(in, outstream, 4096, false);
		} catch (IOException e) {
			if (!abort) {
				throw e;
			} else {
				checkConnectionState();
			}
		}

	}
	
	private void checkConnectionState() throws IOException {
		if (fs == null) {
			throw new IOException(LanguageBundle.getInstance().getMessage(
					"plugin.connection.lost"));
		}
	}

	@Override
	public void mkdir(String dir) throws IOException {
		boolean commandOK = FileSystem.mkdirs(fs, new Path(dir), FsPermission.getDefault());
		if (!commandOK) {
			LOGGER.warning("Failed to make directory " + dir);
		}
	}

	@Override
	public void mkdirs(String dir) throws IOException {
		boolean commandOK = fs.mkdirs(new Path(dir));
		if (!commandOK) {
			LOGGER.warning("Failed to make directories " + dir);
		}
	}

	@Override
	public String pwd() throws IOException {
		return getAbsolutePath();
	}

	@Override
	public void remove(String file) throws IOException {
		boolean commandOK = fs.delete(new Path(file), false);
		if (!commandOK) {
			LOGGER.warning("Failed to delete file " + file);
		}
	}

	@Override
	public void rmdir(String dir) throws IOException {
		boolean commandOK = fs.delete(new Path(dir), true);
		if (!commandOK) {
			LOGGER.warning("Failed to remove directory " + dir);
		}
	}

	@Override
	public void abort() throws IOException {
		// abort() does not work as expected
	    // ftpClient.abort();

	    abort = true;
	    if (instream != null) {
	      instream.close();
	    }
	    if (outstream != null) {
	      outstream.close();
	    }
		
	}

	@Override
	public void rename(String oldpath, String newpath) throws IOException {
		boolean commandOK = fs.rename(new Path(oldpath), new Path(newpath));
		if (!commandOK) {
	      LOGGER.warning("Failed to do rename operation on " + oldpath);
	    }
	}

	@Override
	public void setModificationTime(String file, long mtime) throws IOException {
		fs.setTimes(new Path(file), mtime, -1);
	}

	@Override
	public void setPermissions(String file, int permissions) throws IOException {
		FsPermission fp = FsPermission.createImmutable((short)permissions);
		fs.setPermission(new Path(file), fp);
	}

	@Override
	public boolean isLink(String path) throws IOException {
		Path file = new Path(path);
		FileStatus stat = fs.getFileStatus(file);
		return stat.isSymlink();
	}

	@Override
	public boolean isFile(String path) throws IOException {
		Path file = new Path(path);
		FileStatus stat = fs.getFileStatus(file);
		return stat.isFile();
	}

	@Override
	public int getConnectionID() {
		return ConnectionPluginManager.HDFS_PLUGIN;
	}

	@Override
	public boolean hasConnectionOptions() {
		return false;
	}

	@Override
	public PluginOptionPanel getConnectionOptionPanel() {
		return null;
	}

	@Override
	public boolean requiresPassword() {
		return false;
	}
	
	@Override
	public boolean requiresUsername() {
		return true;
	}

	@Override
	public String getProtocolString() {
		return "hdfs";
	}

	@Override
	public boolean requiresPort() {
		return true;
	}

	@Override
	public int getDefaultPort() {
		return 8020;
	}

	@Override
	public void setHidden(String path) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
