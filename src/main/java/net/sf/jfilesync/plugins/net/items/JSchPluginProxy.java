/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2006 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.plugins.net.items;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.PluginConnectException;
import net.sf.jfilesync.plugins.net.TConnectionData;
import net.sf.jfilesync.util.TPathControlInterface;

public class JSchPluginProxy implements ConnectionPlugin {

	private JSchWorkerThread workerThread;
	private final JSchPlugin plugin = new JSchPlugin();
	private TConnectionData connectData;

	// private final static Logger LOGGER =
	// Logger.getLogger(JSchPluginProxy.class
	// .getName());

	public JSchPluginProxy() {
	}

	private void checkWorkerThread() {
		if (workerThread == null) {
			workerThread = new JSchWorkerThread(plugin);
			workerThread.startUp();
		}
	}

	private void shutdownWorkerThread() {
		if (workerThread != null) {
			workerThread.tearDown();
			workerThread = null;
		}
	}

	public void connect(TConnectionData connectData)
			throws PluginConnectException {
		this.connectData = connectData;
		checkWorkerThread();
		invokeWorkerThreadAndWait("connect",
				new Class[] { TConnectionData.class },
				new Object[] { connectData });
	}

	public TConnectionData getTConnectionData() {
		return connectData;
	}

	public void disconnect() {
		checkWorkerThread();
		invokeWorkerThreadAndWait("disconnect", null, null);
		shutdownWorkerThread();
	}

	public boolean isConnected() {
		checkWorkerThread();
		Boolean ret = (Boolean) invokeWorkerThreadAndWait("isConnected", null,
				null);
		return ret.booleanValue();
	}

	public boolean isLocalConnection() {
		return plugin.isLocalConnection();
	}

	public TPathControlInterface getPathControl() {
		return plugin.getPathControl();
	}

	public TFileData ls() throws IOException {
		checkWorkerThread();
		Object retVal = invokeWorkerThreadAndWait("ls", null, null);
		TFileData data = null;
		if (retVal != null) {
			data = (TFileData) retVal;
		}
		return data;
	}

	public TFileData ls(final String path) throws IOException {
		checkWorkerThread();
		Object retVal = invokeTaskThrowingIOException("ls",
				new Class[] { String.class }, new Object[] { path });

		TFileData data = null;
		if (retVal != null) {
			data = (TFileData) retVal;
		}

		return data;
	}

	public void chdir(String path) throws IOException {
		checkWorkerThread();
		invokeWorkerThreadAndWait("chdir", new Class[] { String.class },
				new Object[] { path });
	}

	public String getAbsolutePath() {
		checkWorkerThread();
		Object retVal = invokeWorkerThreadAndWait("getAbsolutePath", null, null);
		String path = null;
		if (retVal != null) {
			path = (String) retVal;
		}
		return path;
	}

	public boolean exists(String path) {
		checkWorkerThread();
		Boolean ret = (Boolean) invokeWorkerThreadAndWait("exists",
				new Class[] { String.class }, new Object[] { path });
		return ret.booleanValue();
	}

	public void put(InputStream instream, String remoteFileName)
			throws IOException {

		checkWorkerThread();
		invokeWorkerThreadAndWait("put", new Class[] { InputStream.class,
				String.class }, new Object[] { instream, remoteFileName });

	}

	public void get(String remoteFileName, OutputStream outstream)
			throws IOException {

		checkWorkerThread();
		invokeWorkerThreadAndWait("get", new Class[] { String.class,
				OutputStream.class },
				new Object[] { remoteFileName, outstream });

	}

	public void mkdir(String dir) throws IOException {

		checkWorkerThread();
		invokeWorkerThreadAndWait("mkdir", new Class[] { String.class },
				new Object[] { dir });

	}

	public void mkdirs(String dir) throws IOException {

		checkWorkerThread();
		invokeWorkerThreadAndWait("mkdirs", new Class[] { String.class },
				new Object[] { dir });

	}

	public String pwd() {

		checkWorkerThread();
		Object retVal = invokeWorkerThreadAndWait("pwd", null, null);
		String path = null;
		if (retVal != null) {
			path = (String) retVal;
		}
		return path;

	}

	public void remove(String file) throws IOException {

		checkWorkerThread();
		invokeWorkerThreadAndWait("remove", new Class[] { String.class },
				new Object[] { file });

	}

	public void rmdir(String dir) throws IOException {

		checkWorkerThread();
		invokeWorkerThreadAndWait("rmdir", new Class[] { String.class },
				new Object[] { dir });

	}

	public void abort() throws IOException {
		// checkWorkerThread();
		// invokeWorkerThreadAndWait("abort", null, null);
		plugin.abort();
	}

	public void rename(String oldpath, String newpath) throws IOException {

		checkWorkerThread();
		invokeWorkerThreadAndWait("rename", new Class[] { String.class,
				String.class }, new Object[] { oldpath, newpath });

	}

	public void setModificationTime(String file, long mtime) throws IOException {

		checkWorkerThread();
		invokeWorkerThreadAndWait("setModificationTime", new Class[] {
				String.class, Long.TYPE },
				new Object[] { file, new Long(mtime) });

	}

	public void setPermissions(String file, int permissions) throws IOException {

		checkWorkerThread();
		invokeWorkerThreadAndWait("setPermissions", new Class[] { String.class,
				Integer.TYPE }, new Object[] { file, new Integer(permissions) });

	}

	public boolean isLink(String path) throws IOException {
		checkWorkerThread();
		Boolean ret = (Boolean) invokeWorkerThreadAndWait("isLink",
				new Class[] { String.class }, new Object[] { path });
		return ret.booleanValue();
	}

	public boolean isFile(String path) throws IOException {
		checkWorkerThread();
		Boolean ret = (Boolean) invokeWorkerThreadAndWait("isFile",
				new Class[] { String.class }, new Object[] { path });
		return ret.booleanValue();
	}

	public int getConnectionID() {
		return plugin.getConnectionID();
	}

	public boolean hasConnectionOptions() {
		return plugin.hasConnectionOptions();
	}

	public PluginOptionPanel getConnectionOptionPanel() {
		return plugin.getConnectionOptionPanel();
	}

	public boolean requiresPassword() {
		return plugin.requiresPassword();
	}

	// Jawinton
	@Override
	public boolean requiresUsername() {
		return true;
	}

	public String getProtocolString() {
		return plugin.getProtocolString();
	}

	public boolean requiresPort() {
		return plugin.requiresPassword();
	}

	public int getDefaultPort() {
		return plugin.getDefaultPort();
	}

	public boolean isProvided(int feature) {
		return plugin.isProvided(feature);
	}

	public String getName() {
		return plugin.getName();
	}

	public String getDescription() {
		return plugin.getDescription();
	}

	public String getVersionString() {
		return plugin.getVersionString();
	}

	public int getMajorVersion() {
		return plugin.getMajorVersion();
	}

	public int getMinorVersion() {
		return plugin.getMinorVersion();
	}

	public String getLicense() {
		return plugin.getLicense();
	}

	protected Object invokeWorkerThreadAndWait(String methodName,
			Class<?>[] paramClasses, Object[] params) {
		JSchTask task = new JSchTask(methodName, paramClasses, params);
		workerThread.submitTask(task);
		task.waitFor();
		return task.getReturnValue();
	}

	protected Object invokeTaskThrowingIOException(String methodName,
			Class<?>[] paramClasses, Object[] params) throws IOException {

		JSchTask task = new JSchTask(methodName, paramClasses, params);
		workerThread.submitTask(task);
		task.waitFor();

		if (task.getIOException() != null) {
			throw task.getIOException();
		}

		return task.getReturnValue();
	}

	static class JSchTask {

		private final String methodName;
		private final Class<?>[] paramClasses;
		private final Object[] params;
		private Object retVal;
		private boolean done;
		private IOException ioexception;

		public JSchTask(String methodName, Class<?>[] paramClasses,
				Object[] params) {
			this.methodName = methodName;
			this.paramClasses = paramClasses;
			this.params = params;
		}

		public String getMethodName() {
			return methodName;
		}

		public Class<?>[] getParamClasses() {
			return paramClasses;
		}

		public Object[] getParams() {
			return params;
		}

		public Object getReturnValue() {
			return retVal;
		}

		public IOException getIOException() {
			return ioexception;
		}

		public void setIOException(IOException e) {
			ioexception = e;
		}

		public synchronized void setReturnValue(Object val) {
			retVal = val;
			done = true;
			notifyAll();
		}

		public synchronized void waitFor() {
			while (!done) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
		}

	}

	static class JSchWorkerThread extends Thread {

		private final JSchPlugin plugin;
		private boolean running;
		private Object queueMutex = new Object();
		private List<JSchTask> taskQueue = new ArrayList<JSchTask>();

		private final static Logger LOGGER = Logger
				.getLogger(JSchWorkerThread.class.getName());

		public JSchWorkerThread(JSchPlugin plugin) {
			this.plugin = plugin;
		}

		public synchronized void startUp() {
			if (!running) {
				running = true;
				start();
			}
		}

		public synchronized void tearDown() {
			running = false;
			interrupt();
		}

		public synchronized void submitTask(JSchTask task) {
			synchronized (queueMutex) {
				taskQueue.add(task);
				queueMutex.notify();
			}
		}

		protected void processTask(JSchTask task) {

			LOGGER.info("Call method '" + task.getMethodName() + "'");
			if (LOGGER.isLoggable(Level.INFO)) {
				for (int i = 0; task.getParams() != null
						&& i < task.getParams().length; i++) {
					LOGGER.info("params " + i + " : "
							+ task.getParamClasses()[i].getName());
				}
			}

			Object retVal = null;

			try {

				Method method = plugin.getClass().getMethod(
						task.getMethodName(), task.getParamClasses());
				retVal = method.invoke(plugin, task.getParams());
			} catch (InvocationTargetException e) {

				if (e.getTargetException().getClass().equals(IOException.class)) {
					task.setIOException((IOException) e.getTargetException());
				} else {
					LOGGER.severe(e.getMessage());
					e.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			} finally {
				task.setReturnValue(retVal);
			}

		}

		public void run() {

			LOGGER.info("Start worker thread");

			while (running) {
				synchronized (queueMutex) {
					if (taskQueue.size() > 0) {
						JSchTask task = (JSchTask) taskQueue.remove(0);
						processTask(task);
					} else {
						try {
							queueMutex.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}

			LOGGER.info("Worker thread stops");

		}

	}

	public void setHidden(String path) throws IOException {
		// TODO Auto-generated method stub

	}

}
