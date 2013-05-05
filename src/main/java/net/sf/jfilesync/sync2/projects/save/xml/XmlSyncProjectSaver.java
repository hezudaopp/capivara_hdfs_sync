/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003-2007 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
package net.sf.jfilesync.sync2.projects.save.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.jfilesync.sync2.projects.ISyncProjectNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlSyncProjectSaver {

  public XmlSyncProjectSaver() {

  }

  public void save(ISyncProjectNode projectRoot, String projectFileName)
      throws IOException, ParserConfigurationException,
      TransformerFactoryConfigurationError, TransformerException {


    final Document doc = DocumentBuilderFactory.newInstance()
      .newDocumentBuilder().newDocument();

    writeDocument(projectRoot, doc);

     final TransformerFactory tF = TransformerFactory.newInstance();
     tF.setAttribute("indent-number", new Integer(2));

     final Transformer transformer = tF.newTransformer();

    final DOMSource source = new DOMSource(doc);
    //final StreamResult result = new StreamResult(new File(projectFileName));
    final StreamResult result = new StreamResult(new BufferedWriter(
        new FileWriter(new File(projectFileName))));

    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(source, result);

  }

  private void writeDocument(ISyncProjectNode node, Document doc) {
    final Element rootElement = doc.createElement(node.getId());
    doc.appendChild(rootElement);

    writeValues(doc, rootElement, node);
    writeValueLists(doc, rootElement, node);
    writeChilds(doc, rootElement, node);
  }

  private void writeChilds(Document doc, Element parent, ISyncProjectNode node) {
    for(String key : node.getChildKeys()) {
      final ISyncProjectNode child = node.getChild(key);
      writeChild(doc, parent, child);
    }
  }

  private void writeChild(Document doc, Element parent, ISyncProjectNode child) {
    final Element childElement = doc.createElement(child.getId());
    writeValues(doc, childElement, child);
    writeValueLists(doc, childElement, child);
    writeChilds(doc, childElement, child);
    parent.appendChild(childElement);
  }

  private void writeValueLists(Document doc, Element parent,
      ISyncProjectNode node) {

    for(String key : node.getValueListKeys()) {
      final List<String> valueList = node.getValueList(key);
      final Element listElement = doc.createElement("list");
      listElement.setAttribute("id", key);
      for(String value: valueList) {
        final Element item = doc.createElement("item");
        item.appendChild(doc.createCDATASection(value));
        listElement.appendChild(item);
      }
      parent.appendChild(listElement);
    }

  }

  private void writeValues(Document doc, Element parent, ISyncProjectNode node) {

    for(String key : node.getValueKeys() ) {
      final String value = node.getValue(key);
      final Element propElement = doc.createElement("property");
      propElement.setAttribute("key", key);
      propElement.setAttribute("value", value);
      parent.appendChild(propElement);
    }

  }

}
