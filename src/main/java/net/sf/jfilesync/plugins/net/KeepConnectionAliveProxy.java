package net.sf.jfilesync.plugins.net;

import java.io.IOException;
import java.util.logging.Logger;

import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.event.types.TStandardMessage;

public class KeepConnectionAliveProxy extends AbstractConnectionProxyImpl {

  private final static Logger LOGGER = Logger.getLogger(ConnectionStatusProxy.class.getName());
  private int timeout_sec = 10;
  private ConnectionChecker conChecker;
  private final int ccID;

  public KeepConnectionAliveProxy(final AbstractConnectionProxy proxy, final int ccID) {
    super(proxy);
    this.ccID = ccID;
  }

  @Override
  public void connect() throws PluginConnectException {
    super.connect();
    conChecker = new ConnectionChecker(getAbstractConnectionProxy(), getTimeout(), ccID);
    conChecker.startUp();
  }

  @Override
  public void disconnect() {
    super.disconnect();
    if (conChecker != null) {
      conChecker.shutDown();
    }
  }

  public void setTimeOut(final int timeout_s) {
    this.timeout_sec = timeout_s;
  }

  public int getTimeout() {
    return timeout_sec;
  }

  static class ConnectionChecker implements Runnable {

    private final AbstractConnectionProxy proxy2Check;
    private Thread checkThread;
    private boolean running = true;
    private final int timeout_sec;
    private final int ccID;

    public ConnectionChecker(final AbstractConnectionProxy proxy2Check, final int timeout_sec, final int ccID) {
      if (proxy2Check == null) {
        throw new IllegalArgumentException("proxy2Check is null!");
      }
      this.proxy2Check = proxy2Check;
      this.timeout_sec = timeout_sec;
      this.ccID = ccID;
    }

    public void startUp() {
      if (checkThread == null) {
        checkThread = new Thread(this);
        checkThread.start();
      } else {
        LOGGER.warning("won't start another checking thread... called twice");
      }
    }

    public void shutDown() {
      if (checkThread != null) {
        running = false;
        checkThread.interrupt();
      } else {
        LOGGER.warning("there is no check thread to shut down");
      }
    }

    @Override
    public void run() {
      LOGGER.fine("starting check thread...");
      while (running) {
        if (proxy2Check == null) {
          LOGGER.warning("proxy2Check is null...exiting");
          running = false;
        } else {

          boolean connected = true;

          LOGGER.fine("keeping connection alive...");
          if (proxy2Check.isConnected()) {
            try {
              proxy2Check.pwd();
            } catch (final IOException e) {
              e.printStackTrace();
            }
          } else {
            connected = false;
          }

          if (!connected) {
            running = false;
            TEventMulticaster.getInstance().fireTEvent(this, ccID,
                new TStandardMessage(TMessage.ID.CONNECTION_LOST_MESSAGE));
          } else {
            try {
              Thread.sleep(timeout_sec * 1000);
            } catch (final InterruptedException e) {
            }
          }
        }
      }
      LOGGER.fine("check thread shutting down..");
    }

  }

}
