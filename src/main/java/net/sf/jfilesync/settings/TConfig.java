/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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

package net.sf.jfilesync.settings;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.jfilesync.engine.ConnectionConfig;
import net.sf.jfilesync.plugins.net.ConnectionPlugin;
import net.sf.jfilesync.plugins.net.ConnectionPluginManager;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TConfig {

    private static final String
      XML_ROOT             = "jfilesync",
      XML_PARAM_ROOT       = "paramconf",
      XML_CONNECTION_CONF  = "connectionconf",
      XML_CON              = "connection",
      XML_CON_NAME         = "name",
      XML_CON_USER         = "user",
      XML_CON_HOST         = "host",
      XML_CON_PORT         = "port",
      XML_CON_PROTOCOL     = "protocol",
      XML_CON_PASSWORD     = "password",
      XML_BOOKMARKS_TAG    = "bookmarks",
      XML_BOOMARK_TAG      = "bookmark";


    private Document config;
    private TProgramSettings progSettings = null; // user settings

    private final static Logger LOGGER = Logger.getLogger(TConfig.class.getName());

    public TConfig() {
        try {
            config = createFreshConfig();
            // ask for locale
            // show dialog and set user language
            getProgramSettings();
        } catch (final ParserConfigurationException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private Document createFreshConfig() throws ParserConfigurationException {
        final Document conf = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        validateConfig(conf);
        return conf;
    }

    public TConfig(final Document config) {
        if (config == null) {
            throw new NullPointerException("config");
        }
        validateConfig(config);
        this.config = config;
        progSettings = new TProgramSettings(config);
    }

    private void validateConfig(final Document document) {
        Element root = null;
        if (document.getElementsByTagName(XML_ROOT).getLength() < 1) {
            root = document.createElement(XML_ROOT);
            document.appendChild(root);
        } else {
            root = (Element) document.getElementsByTagName(XML_ROOT).item(0);
        }
        if (root != null) {
            if (document.getElementsByTagName(XML_PARAM_ROOT).getLength() < 1) {
                root.appendChild(document.createElement(XML_PARAM_ROOT));
            }
            if (document.getElementsByTagName(XML_CONNECTION_CONF).getLength() < 1) {
                root.appendChild(document.createElement(XML_CONNECTION_CONF));
            }
            if (document.getElementsByTagName(XML_BOOKMARKS_TAG).getLength() < 1) {
                root.appendChild(document.createElement(XML_BOOKMARKS_TAG));
            }
        }
    }

    public org.w3c.dom.Document getDOMDocument() {
        return config;
    }

    public ConnectionConfig[] getSavedConnections() {
        final NodeList connectionList = config.getElementsByTagName(XML_CON);

        final ConnectionConfig[] configs = new ConnectionConfig[connectionList
                .getLength()];

        for (int i = 0; i < connectionList.getLength(); i++) {
            final Node conNode = connectionList.item(i);
            String conName = "", conUser = "", conHost = "";
            int conPort = -1;
            int conProtocol = -1;
            final NodeList conChilds = conNode.getChildNodes();
            String passwd = null;

            for (int j = 0; j < conChilds.getLength(); j++) {
                final Node conChildNode = conChilds.item(j);

                if (conChildNode.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) conChildNode;

                    if (elem.getTagName().equals(XML_CON_NAME)) {
                        conName = extractString(elem);
                    } else if (elem.getNodeName().equals(XML_CON_USER)) {
                        conUser = extractString(elem);
                    } else if (elem.getNodeName().equals(XML_CON_HOST)) {
                        conHost = extractString(elem);
                    } else if (elem.getNodeName().equals(XML_CON_PORT)) {
                        try {
                            conPort = extractInt(elem);
                        } catch (final TParseException e) {
                            LOGGER.warning(e.getMessage());
                        }
                    } else if (elem.getNodeName().equals(XML_CON_PROTOCOL)) {
                        try {
                            conProtocol = extractInt(elem);
                            ;
                        } catch (final TParseException tpe) {
                            tpe.printStackTrace();
                        }
                    } else if (elem.getNodeName().equals(XML_CON_PASSWORD)) {
                        passwd = extractString(elem);
                    }
                }
            }
            configs[i] = new ConnectionConfig(conName, conProtocol, conHost,
                    conPort, conUser);
            if( passwd != null ) {
                configs[i].setPassword(passwd);
            }
        }

        return configs;
    }

    private String extractString(final Node node) {
        if (node.getChildNodes().getLength() > 0) {
            return node.getChildNodes().item(0).getNodeValue();
        }
        return "";
    }

    private int extractInt(final Node node) throws TParseException {
        int v = 0;

        if (node != null) {
            String value = "";
            if (node.getChildNodes().getLength() > 0) {
                value = node.getChildNodes().item(0).getNodeValue();
                try {
                    v = Integer.parseInt(value);
                } catch (final NumberFormatException nfe) {
                    throw new TParseException(nfe);
                }
            }
        }

        return v;
    }

    public void addConnectionData(final ConnectionConfig conConfig) {
        // there is only one element in this list
        // connectionconf
        final NodeList confList = config.getElementsByTagName(XML_CONNECTION_CONF);
        // so we have a node to insert new settings
        final Node confNode = confList.item(0);

        final Element conElem = config.createElement(XML_CON);

        final Element conNameElem = config.createElement(XML_CON_NAME);
        conNameElem.appendChild(config.createTextNode(conConfig
                .getDescription()));
        conElem.appendChild(conNameElem);

        final Element conProtElem = config.createElement(XML_CON_PROTOCOL);
        conProtElem.appendChild(config.createTextNode(Integer
                .toString(conConfig.getProtocol())));
        conElem.appendChild(conProtElem);

        final Element conHostElem = config.createElement(XML_CON_HOST);
        conHostElem.appendChild(config.createTextNode(conConfig.getHostName()));
        conElem.appendChild(conHostElem);

        final int port = conConfig.getPort();
        addPortToConnectionElement(conElem, port, conConfig.getProtocol());

        final Element conUserElem = config.createElement(XML_CON_USER);
        conUserElem.appendChild(config.createTextNode(conConfig.getUserName()));
        conElem.appendChild(conUserElem);

        final String passwd = conConfig.getPassword();
        if( passwd != null && passwd.length() > 0 ) {
            final Element conPasswdElem = config.createElement(XML_CON_PASSWORD);
            conPasswdElem.appendChild(config.createTextNode(passwd));
            conElem.appendChild(conPasswdElem);
        }

        confNode.appendChild(conElem);
    }

    public void removeConnectionData(final String description) {
        // connection configuration node
        final NodeList confList = config.getElementsByTagName(XML_CONNECTION_CONF);
        final Node confNode = confList.item(0);

        final Node conNode = findConnectionNode(description);
        if (conNode != null) {
            confNode.removeChild(conNode);
        }
    }

    private void addPortToConnectionElement(final Element conElem,
            final int port, final int pluginID) {

        final ConnectionPlugin plugin = ConnectionPluginManager
                .getConnectionModelInstance(pluginID);

        if (port != -1 && port != plugin.getDefaultPort()) {
            // safe ports which are not default ports
            final Element conPortElem = config.createElement(XML_CON_PORT);
            conPortElem.appendChild(config.createTextNode(Integer
                    .toString(port)));
            conElem.appendChild(conPortElem);
        }
    }

    public void changeConnectionData(final ConnectionConfig conConfig) {
        // connection configuration node

        final Node conNode = findConnectionNode(conConfig.getDescription());

        if( conNode == null ) {
            return;
        }

        final NodeList conchilds = conNode.getChildNodes();

        boolean hadPort = false;
        boolean hadPasswd = false;
        Node toDelete = null;

        for (int i = 0; i < conchilds.getLength(); i++) {
            if (conchilds.item(i).getNodeType() == Node.ELEMENT_NODE) {

                final Element conchildElem = (Element) conchilds.item(i);
                final String tagname = conchildElem.getTagName();
                if (tagname.equals(TConfig.XML_CON_USER)) {
                    deleteChildsOf(conchildElem);
                    conchildElem.appendChild(config.createTextNode(conConfig
                            .getUserName()));
                } else if (tagname.equals(TConfig.XML_CON_HOST)) {
                    deleteChildsOf(conchildElem);
                    conchildElem.appendChild(config.createTextNode(conConfig
                            .getHostName()));
                } else if (tagname.equals(TConfig.XML_CON_PROTOCOL)) {
                    deleteChildsOf(conchildElem);
                    conchildElem.appendChild(config.createTextNode(Integer
                            .toString(conConfig.getProtocol())));
                } else if (tagname.equals(TConfig.XML_CON_PASSWORD)) {
                    hadPasswd = true;
                    deleteChildsOf(conchildElem);
                    if( conConfig.getPassword() != null ) {
                        conchildElem.appendChild(config
                                .createTextNode(conConfig.getPassword()));
                    }
                } else if (tagname.equals(TConfig.XML_CON_PORT)) {
                    hadPort = true;

                    final ConnectionPlugin plugin = ConnectionPluginManager
                            .getConnectionModelInstance(conConfig.getProtocol());
                    final int newPort = conConfig.getPort();
                    if (newPort != -1 && newPort != plugin.getDefaultPort()) {
                        // update port
                        deleteChildsOf(conchildElem);
                        conchildElem.appendChild(config.createTextNode(Integer
                                .toString(conConfig.getPort())));
                    } else {
                        toDelete = conchilds.item(i);
                    }
                }

            }
        }

        if (!hadPort) {
            // so, there was no port saved
            // save it now
            LOGGER.info("add port");
            addPortToConnectionElement((Element) conNode, conConfig.getPort(),
                    conConfig.getProtocol());
        } else if (toDelete != null) {
            conNode.removeChild(toDelete);
        }

        if (!hadPasswd) {
            if (conConfig.getPassword() != null) {
                final Element conPasswdElem = config
                        .createElement(XML_CON_PASSWORD);
                conPasswdElem.appendChild(config.createTextNode(conConfig
                        .getPassword()));
                conNode.appendChild(conPasswdElem);
            }
        }

    }

    // ----------------------------------------------------------------------------

    private Node findConnectionNode(final String descr) {
        final Node snode = null;

        // this list contains the element
        final NodeList conList = config.getElementsByTagName(XML_CON);
        for (int i = 0; i < conList.getLength(); i++) {
            final Node conNode = conList.item(i);
            final NodeList conChilds = conNode.getChildNodes();
            for (int j = 0; j < conChilds.getLength(); j++) {
                final Node childNode = conChilds.item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    final Element childElem = (Element) childNode;
                    if (childElem.getTagName().equals(XML_CON_NAME)) {
                        // descr node found
                        if (childElem.getFirstChild().getNodeValue().equals(
                                descr)) {
                            return conNode;
                        }
                    }
                }
            }
        }

        return snode;
    }

    // -----------------------------------------------------------------------------

    private void deleteChildsOf(final Node node) {
        final NodeList delNodeList = node.getChildNodes();
        for (int k = 0; k < delNodeList.getLength(); k++) {
            node.removeChild(delNodeList.item(k));
        }
    }

    // -----------------------------------------------------------------------------

    public synchronized void addBookmark(final String bookmark) {
        if (bookmark == null) {
            return;
        }
        final NodeList bookmarksNodeList = config
                .getElementsByTagName(XML_BOOKMARKS_TAG);
        if (bookmarksNodeList != null && bookmarksNodeList.getLength() > 0) {
            final Node bookmarksNode = bookmarksNodeList.item(0);
            final Element bookmarkElement = config.createElement(XML_BOOMARK_TAG);
            bookmarkElement.appendChild(config.createCDATASection(bookmark));
            bookmarksNode.appendChild(bookmarkElement);
        }
    }

    public synchronized void removeBookmark(final String bookmark) {
        if (bookmark == null) {
            return;
        }
        final NodeList bookmarksNodeList = config
                .getElementsByTagName(XML_BOOKMARKS_TAG);
        if (bookmarksNodeList != null && bookmarksNodeList.getLength() > 0) {
            final NodeList bookmarkList = ((Element) bookmarksNodeList.item(0))
                    .getElementsByTagName(XML_BOOMARK_TAG);
            for (int i = 0; bookmarkList != null
                    && i < bookmarkList.getLength(); i++) {
                final Element bookmarkElem = (Element) bookmarkList.item(i);
                final boolean contains = containsBookmark(bookmarkElem, bookmark);
                if (contains) {
                    ((Element) bookmarksNodeList.item(0))
                            .removeChild(bookmarkElem);
                    removeEmptyTextNodes((Element) bookmarksNodeList.item(0));
                    break;
                }
            }
        }
    }

    private void removeEmptyTextNodes(final Element element) {
        final NodeList list = element.getChildNodes();
        for (int i = 0; list != null && i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.TEXT_NODE) {
                element.removeChild(list.item(i));
            }
        }
    }

    private boolean containsBookmark(final Element bookmarkElement,
            final String bookmark) {
        boolean ret = false;
        final NodeList childs = bookmarkElement.getChildNodes();
        if (childs != null) {
            for (int i = 0; i < childs.getLength(); i++) {
                if (childs.item(i).getNodeType() == Node.CDATA_SECTION_NODE) {
                    if (((CDATASection) childs.item(i)).getData().equals(
                            bookmark)) {
                        ret = true;
                        break;
                    }
                }
            }
        }
        return ret;
    }

    public synchronized String[] getAllBookmarks() {
        final List<String> bookmarkList = new LinkedList<String>();
        final NodeList bookmarksNodes = config
                .getElementsByTagName(XML_BOOKMARKS_TAG);
        if (bookmarksNodes != null && bookmarksNodes.getLength() > 0) {
            final NodeList bookmarkNodeList = ((Element) bookmarksNodes.item(0))
                    .getElementsByTagName(XML_BOOMARK_TAG);
            for (int i = 0; bookmarkNodeList != null
                    && i < bookmarkNodeList.getLength(); i++) {
                addBookmarkToList(bookmarkList, (Element) bookmarkNodeList
                        .item(i));
            }
        }
        return bookmarkList.toArray(new String[bookmarkList.size()]);
    }

    private void addBookmarkToList(final List<String> bookmarkList,
            final Element bookmarkElement) {

        final NodeList childNodes = bookmarkElement.getChildNodes();
        for (int i = 0; childNodes != null && i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeType() == Node.CDATA_SECTION_NODE) {
                final CDATASection data = (CDATASection) childNodes.item(i);
                final String bookmark = data.getData();
                if (bookmark != null) {
                    bookmarkList.add(bookmark);
                }
                break;
            }
        }

    }

    //-----------------------------------------------------------------------------

    public TProgramSettings getProgramSettings() {
        if (progSettings == null) {
            // read settings from xml tree
            progSettings = new TProgramSettings(config);
        }
        return progSettings;
    }

}
