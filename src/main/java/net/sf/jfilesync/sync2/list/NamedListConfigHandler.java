/*
 * capivara - Java File Synchronization
 *
 * Created on 31-Dec-2005
 * Copyright (C) 2005 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 * $Id$
 */
package net.sf.jfilesync.sync2.list;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.jfilesync.settings.ConfigFileHandler;

public class NamedListConfigHandler {

  private ConfigFileHandler definitions = ConfigFileHandler.getInstance();
  
  static Logger log = Logger.getLogger(NamedListConfigHandler.class
      .getPackage().getName());
  
  private RegexConfig regexConfig;
  private static NamedListConfigHandler singleton;
  
  private NamedListConfigHandler() {
    loadConfig();
  }
  
  public static NamedListConfigHandler getInstance() {
    if( singleton == null ) {
      singleton = new NamedListConfigHandler();
    }
    return singleton;
  }
  
  private void loadConfig() {
    boolean fileExists = checkConfigFile();
    if( fileExists ) {
      RegexConfigReader reader = new RegexConfigReader();
      try {
        reader.readConfig();
        regexConfig = reader.getConfig();
      }
      catch(RegexConfigException e) {
        log.warning(e.getMessage());
        JOptionPane.showMessageDialog(null, 
            "Error while reading regular expression lists. Create new configuration.",
            "Regex config error",
            JOptionPane.ERROR_MESSAGE);
        regexConfig = new RegexConfig();
      }
    }
    else {
      regexConfig = new RegexConfig();
    }
  }
  
  public NExpListList getNamedLists() {
    NExpListList list = new NExpListList();
   
    if( regexConfig != null ) {
      list = regexConfig.getLists();
    }
    else {
      JOptionPane.showMessageDialog(null, 
          "Error while reading regular expressions. Send bug report!",
          "Regex error",
          JOptionPane.ERROR_MESSAGE);
    }
    
    return list;
  }
  
  public void saveNamedList(final NamedExpressionList list) 
  throws IOException {
    if( list != null ) {
      if( regexConfig != null ) {
        regexConfig.saveNamedList(list);
        //log.info("write to xml file");
        RegexConfigWriter writer = new RegexConfigWriter();
        writer.writeConfig(regexConfig);
      }
    }
  }
  
  public void saveLists(final NExpListList lists) 
  throws IOException {
    if( lists != null ) {
      regexConfig = new RegexConfig();
      for(int i=0; i<lists.size(); i++) {
        saveNamedList(lists.getElementAt(i));
      }
    }
  }
  
  private boolean checkConfigFile() {
    boolean ret = false;
    String path = definitions.getRegexFileLocation();
    if( new File(path).exists() ) {
      ret = true;
    }
    return ret;
  }
    
  class RegexConfigException extends Exception {
    public RegexConfigException(final String msg) {
      super(msg);
    }
  }
  
  class RegexConfigReader {

    private Document config;
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    
    public RegexConfigReader() {
      factory = DocumentBuilderFactory.newInstance();
    }
    
    public void readConfig() throws RegexConfigException {
      try {
        builder = factory.newDocumentBuilder();
        File f = new File(definitions.getRegexFileLocation());
        config = builder.parse(f);
      }
      catch( SAXException sxe ) {
        throw new RegexConfigException(sxe.getMessage());
      }
      catch( ParserConfigurationException pce ) {
        throw new RegexConfigException(pce.getMessage());
      }
      catch( IOException ioe ) {
        throw new RegexConfigException(ioe.getMessage());
      }
    }
    
    public RegexConfig getConfig() {
      return new RegexConfig(config);
    }

  }
  
  class RegexConfigWriter {
    
    public RegexConfigWriter() { }

    public void writeConfig(RegexConfig regexConfig) throws IOException {
      try {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        
        DOMSource source = new DOMSource(regexConfig.getDocument());
        File configFile = new File(definitions.getRegexFileLocation());
        StreamResult result = new StreamResult(configFile);
        
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
      }
      catch(TransformerConfigurationException e) {
        throw new IOException(e.getMessage());
      }
      catch(TransformerException e) {
        throw new IOException(e.getMessage());
      }      
    }

  }
  
  
  class RegexConfig {

    private Document config;
    private Element root = null;

    private final static String XML_ROOT = "capiregex";
    private final static String XML_NODE_LIST = "list";
    private final static String XML_ATTR_LIST_NAME = "name";
    private final static String XML_NODE_EXP = "expression";
    
    public RegexConfig() {
      try {
        config = DocumentBuilderFactory.newInstance().newDocumentBuilder().
            newDocument();
      }
      catch (ParserConfigurationException pce) {
        pce.printStackTrace();
      }
      root = config.createElement(XML_ROOT);
      config.appendChild(root);
    }
    
    public RegexConfig(final Document config) {
      this.config = config;
    }

    public Document getDocument() {
      return config;
    }
    
    public NExpListList getLists() {
      NExpListList ll = new NExpListList();

      NodeList listNodes = config.getElementsByTagName(XML_NODE_LIST);
      for (int i = 0; i<listNodes.getLength(); i++)  {
        Node listNode = listNodes.item(i);

        if( listNode.getNodeType() == Node.ELEMENT_NODE ) {
          
          Element listNodeElem = (Element)listNode;
          String listName = listNodeElem.getAttribute(XML_ATTR_LIST_NAME);
          //log.info("list : " + listName);
          
          if( listName != null && ! listName.equals("") ) {
            
            ExpressionList elist = new ExpressionList();

            NodeList expList = listNodeElem.getElementsByTagName(XML_NODE_EXP);
            for(int ei=0; ei<expList.getLength(); ei++) {
              String exp = readExpression(expList.item(ei));
              if( exp != null ) {
                //log.info("expression : " + exp);
                elist.addExpression(new Expression(exp));
              }
            }
            
            if( elist.size() > 0 ) {
              NamedExpressionList nlist = new NamedExpressionList(
                  listName, elist);
              ll.addNamedExpressionList(nlist);
            }
          }
          
        }
        
      }

      return ll;
    }
    
    private String readExpression(final Node expNode) {
      String exp = null;
      
      if( expNode.getNodeType() == Node.ELEMENT_NODE ) {
        Element expElem = (Element)expNode;
        NodeList expElemChilds = expElem.getChildNodes();
        for(int i=0; i<expElemChilds.getLength(); i++) {
          if( expElemChilds.item(i).getNodeType() == Node.CDATA_SECTION_NODE ) {
            CDATASection data = (CDATASection) expElemChilds.item(i);
            exp = data.getNodeValue();
          }
        }
      }
      
      return exp;
    }
    
    public void saveNamedList(final NamedExpressionList list) {
      final String newName = list.getName();
            
      NodeList listNodes = config.getElementsByTagName(XML_NODE_LIST);
      Element saveNode = null;  // remember node if list already exists

      for (int i = 0; i<listNodes.getLength(); i++)  {
        Node listNode = listNodes.item(i);
        if( listNode.getNodeType() == Node.ELEMENT_NODE ) {
          Element listNodeElem = (Element)listNode;
          String listName = listNodeElem.getAttribute(XML_ATTR_LIST_NAME);

          if( listName.equals(newName) ) {
            saveNode = listNodeElem;
            // delete old childs
            while( listNodeElem.getFirstChild() != null ) {
              listNodeElem.removeChild(listNodeElem.getFirstChild());
            }
          }
        }
      }
      
      if( saveNode == null ) {
        //log.info("create new node");
        saveNode = config.createElement(XML_NODE_LIST);
        saveNode.setAttribute(XML_ATTR_LIST_NAME, newName);
        
        Node rootNode = getRootNode();
        if( rootNode != null ) {
          log.info("append to root node");
          rootNode.appendChild(saveNode);
        }
      }
      
      addExpressionListsToElement(saveNode, list);
      
    }
    
    private Node getRootNode() {
      Node root = null;
      NodeList nodeList = config.getElementsByTagName(XML_ROOT);
      if( nodeList.getLength() > 0 ) {
        root = nodeList.item(0);
      }
      return root;
    }
    
    private void addExpressionListsToElement(final Element saveElem,
       final NamedExpressionList list) {
      
      for(int i=0; i<list.getList().size(); i++) {
        Expression exp = list.getList().get(i);
        Element expElem = config.createElement(XML_NODE_EXP);
        expElem.appendChild(config.createCDATASection(exp.toString()));
        saveElem.appendChild(expElem);
      }
      
    }
    
  }
  
}
