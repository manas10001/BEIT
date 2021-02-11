/*
 The MIT License

 Copyright (c) 2013 - 2013
   1. High Performance Computing Group, 
   School of Electrical Engineering and Computer Science (SEECS), 
   National University of Sciences and Technology (NUST)
   2. Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter (2013 - 2013)
   

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be included
 in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * File         : MPJUtil.java 
 * Author       : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter
 * Created      : April 03, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 */
package runtime.common;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MPJXml {
  /**
   * @uml.property  name="xmlString"
   */
  private String xmlString;
  /**
   * @uml.property  name="xmlNode"
   * @uml.associationEnd  multiplicity="(1 1)"
   */
  Node xmlNode = null;

  private Element elem() {
    if (xmlNode instanceof Element)
      return (Element) xmlNode;
    return null;
  }

  public MPJXml(String xml) {

    if (xml == "")
      throw new NullPointerException();
    this.xmlString = stripInValidXMLCharacters(xml).trim();

    parseXML();
  }

  public String stripInValidXMLCharacters(String in) {
    StringBuffer out = new StringBuffer(); // Used to hold the output.
    char current; // Used to reference the current character.

    if (in == null || ("".equals(in)))
      return ""; // vacancy test.
    for (int i = 0; i < in.length(); i++) {
      current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught
			      // here; it should not happen.
      if ((current == 0x9) || (current == 0xA) || (current == 0xD)
	  || ((current >= 0x20) && (current <= 0xD7FF))
	  || ((current >= 0xE000) && (current <= 0xFFFD))
	  || ((current >= 0x10000) && (current <= 0x10FFFF)))
	out.append(current);
    }
    return out.toString();
  }

  private MPJXml(Element xmlNode) {
    this.xmlNode = xmlNode;
  }

  public Node getNode() {
    return xmlNode;
  }

  private String localName() {
    if (elem() != null)
      return elem().getTagName();
    return "";
  }

  public String getTagName() {
    return localName();
  }

  public String getAttrib(String arg) {
    if (elem() != null) {
      if (elem().hasAttribute(arg)) {
	return elem().getAttribute(arg);
      }
    }
    return "";
  }

  public void setAttrib(String attribName, Boolean attribVal) {
    setAttrib(attribName, attribVal.toString());
  }

  public void setAttrib(String attribName, double attribVal) {
    setAttrib(attribName, ((Double) attribVal).toString());
  }

  public void setAttrib(String attribName, int attribVal) {
    setAttrib(attribName, Integer.toString(attribVal));
  }

  public void setAttrib(String attribName, String attribVal) {
    if (elem() != null)
      elem().setAttribute(attribName, attribVal);
  }

  public MPJXml getChild(String tagName) {
    ArrayList<MPJXml> xl = getChildren(tagName);
    if (xl.size() > 0) {
      return xl.get(0);
    }
    return null;
  }

  public ArrayList<MPJXml> getChildren() {
    ArrayList<MPJXml> xl = new ArrayList<MPJXml>();
    NodeList nodes = xmlNode.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      if (nodes.item(i) instanceof Element) {
	xl.add(new MPJXml((Element) nodes.item(i)));
      }
    }
    return xl;
  }

  public ArrayList<MPJXml> getChildren(String tagName) {
    ArrayList<MPJXml> taggedChildren = new ArrayList<MPJXml>();
    ArrayList<MPJXml> xl = getChildren();
    for (int i = 0; i < xl.size(); i++) {
      if (xl.get(i).getTagName().equals(tagName)) {
	taggedChildren.add(xl.get(i));
      }
    }
    return taggedChildren;
  }

  public MPJXml appendChild(MPJXml xml) {
    try {
      org.w3c.dom.Document domYouAreAddingTheNodeTo = xmlNode
	  .getOwnerDocument();
      Node tempNode = domYouAreAddingTheNodeTo.importNode(xml.xmlNode, true);
      this.xmlNode.appendChild(tempNode);
    }
    catch (Exception ex) {
    }
    return this;
  }

  public String getText() {
    return xmlNode.getTextContent();
  }

  public void setText(String text) {
    xmlNode.setTextContent(text);
  }

  private Boolean parseXML() {
    DocumentBuilder documentBuilder;
    org.w3c.dom.Document dom = null;
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
	.newInstance();
    docBuilderFactory.setIgnoringElementContentWhitespace(true);
    try {
      documentBuilder = docBuilderFactory.newDocumentBuilder();
    }
    catch (ParserConfigurationException e) {

      System.out.println("Wrong parser configuration: " + e.getMessage());
      return null;
    }
    try {
      dom = documentBuilder.newDocument(); // initialize
      xmlNode = dom.getDocumentElement(); // initialize
      InputSource in = new InputSource(new StringReader(xmlString));
      in.setEncoding("UTF-8");
      dom = documentBuilder.parse(in);
      xmlNode = dom.getDocumentElement();
      return true;
    }
    catch (SAXException e) {
      System.out.println("Wrong MPJXml file structure: " + e.getMessage());
      return null;
    }
    catch (IOException e) {
      System.out.println("Could not read source file: " + e.getMessage());
    }

    return false;
  }

  public String toXmlString() {
    // set up a transformer
    TransformerFactory transfac = TransformerFactory.newInstance();
    Transformer trans;
    try {
      trans = transfac.newTransformer();
    }
    catch (TransformerConfigurationException e1) {
      e1.printStackTrace();
      return "";
    }
    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    trans.setOutputProperty(OutputKeys.INDENT, "yes");

    // create string from xml tree
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(xmlNode.getOwnerDocument());
    try {
      trans.transform(source, result);
    }
    catch (TransformerException e) {
      e.printStackTrace();
    }
    return sw.toString();
  }
}
