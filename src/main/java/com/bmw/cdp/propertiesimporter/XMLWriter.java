/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.6.0-SNAPSHOT
 * ____________________________________________________________________________
 *
 *         $Revision: 4532 $
 *    $LastChangedBy: qxa5203 $
 *  $LastChangedDate: 2012-01-30 14:32:48 +0100 (Mo, 30 Jan 2012) $
 *       Description: see Javadoc
 * ____________________________________________________________________________
 *
 *         Copyright: (c) BMW AG 2011, all rights reserved
 * ____________________________________________________________________________
 */
package com.bmw.cdp.propertiesimporter;


import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLWriter
{

   private final String        outputFile;
   private final String        rootDir;
   private final String[]      outputLocales;
   private final String        sidPre;
   private final String        filterMode;
   private static final String SPR = ";";
   private static final String LF  = "\n";


   public XMLWriter(PropertyUtils propertyUtils)
   {
      super();
      this.outputFile = propertyUtils.getOutputFile();
      this.rootDir = propertyUtils.getRootDir();
      this.outputLocales = propertyUtils.getOutputLocales();
      this.filterMode = propertyUtils.getFilterMode();
      this.sidPre = propertyUtils.getSidPre();
   }


   public String getXml(Map<String, PropertyForModule> modules)
      throws IOException
   {
      List<String> keys = new ArrayList<String>();
      for (Object key : modules.keySet()) {
         keys.add(key.toString());
      }
      StringBuffer buffer = new StringBuffer();
      buffer.append("<modules>");
      Collections.sort(keys);
      for (String key : keys) {
         PropertyForModule module = modules.get(key);
         buffer.append("<module>");
         buffer.append("<moduleName>");
         buffer.append(module.getModuleName());
         buffer.append("</moduleName>");
         buffer.append("<propertyFiles>");
         for (File propertyFile : module.getPropertyFiles()) {
            buffer.append("<propertyFile>");
            this.writeProperties(buffer, module, propertyFile);
            buffer.append("</propertyFile>");
         }
         buffer.append("</propertyFiles>");
         buffer.append("</module>");
      }
      buffer.append("</modules>");
      return buffer.toString();
   }


   private void writeHeaderLine(Writer writer)
      throws IOException
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append("\"Module\"" + SPR + "\"Dir\"" + SPR + "\"File\"" + SPR + "\"Key\"");
      for (String outputLocale : outputLocales) {
         buffer.append(SPR);
         buffer.append("\"" + outputLocale + "\"");
      }
      buffer.append(LF);
      writer.write(buffer.toString());
   }


   private void writeProperties(StringBuffer buffer, PropertyForModule module, File propertyFile)
      throws IOException
   {
      String basefileName = PropertyUtils.getBaseFileName(propertyFile, rootDir);
      Properties propertiesGlobal = module.getPropertiesGlobal().get(basefileName);

      if (propertiesGlobal == null || propertiesGlobal.isEmpty()) {
         return;
      }
      List<String> keys = new ArrayList<String>();
      for (Object key : propertiesGlobal.keySet()) {
         keys.add(key.toString());
      }
      Collections.sort(keys);
      buffer.append("<dirName>");
      buffer.append(cdata(PropertyUtils.getDirName(basefileName)));
      buffer.append("</dirName>");
      buffer.append("<fileName>");
      buffer.append(cdata(PropertyUtils.getFileName(basefileName)));
      buffer.append("</fileName>");
      buffer.append("<properties>");
      for (String key : keys) {
         buffer.append("<property>");
         buffer.append("<key>");
         buffer.append(cdata(key.toString()));
         buffer.append("</key>");
         buffer.append("<sid>");
         Map<String, Properties> localeMap = module.getProperties().get(PropertyUtils.SID_LOCALE);
         if (localeMap != null && localeMap.containsKey(basefileName)) {
            String value = localeMap.get(basefileName).getProperty(key, "");
            buffer.append(cdata(value));
         }
         buffer.append("</sid>");
         buffer.append("<tranlations>");
         for (String outputLocale : outputLocales)
            if (!PropertyUtils.SID_LOCALE.equals(outputLocale)) {
               localeMap = module.getProperties().get(outputLocale);
               buffer.append("<translation>");
               buffer.append("<outputLocale>");
               buffer.append(cdata(outputLocale));
               buffer.append("</outputLocale>");
               buffer.append("<value>");
               if (localeMap != null && localeMap.containsKey(basefileName)) {
                  String value = localeMap.get(basefileName).getProperty(key, "");
                  value = value.replaceAll("(\\r)?\\n", "<br />");
                  buffer.append(cdata(value));
               }
               buffer.append("</value>");
               buffer.append("</translation>");
            }
         buffer.append("</tranlations>");
         buffer.append("</property>");
      }
      buffer.append("</properties>");
   }


   public void evalXPathWrite(String xpathExpr, Document doc)
      throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
   {
      BufferedWriter writer = PropertyUtils.openReportFile(outputFile);
      writeHeaderLine(writer);
      BufferedWriter writerNaitive = null;
      if (PreferenceConstants.NATIVE.equals(filterMode)) {
         writerNaitive = PropertyUtils.openReportFile(outputFile.replace(".txt", ".csv"));
         writeHeaderLine(writerNaitive);
      }
      XPath xpath = XPathFactory.newInstance().newXPath();
      XPathExpression expr = xpath.compile(xpathExpr);
      NodeList nodeList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); ++i) {
         Node propertyNode = nodeList.item(i);
         if (propertyNode != null) {
            StringBuffer buffer = new StringBuffer();
            Node keyNode = propertyNode.getFirstChild();
            Node sidNode = keyNode.getNextSibling();
            Node propertyFileNode = propertyNode.getParentNode().getParentNode();
            Node dirNameNode = propertyFileNode.getFirstChild();
            Node fileNameNode = dirNameNode.getNextSibling();
            Node moduleNode = propertyFileNode.getParentNode().getParentNode();
            Node moduleNameNode = moduleNode.getFirstChild();
            buffer.append(getNodeValueQ(moduleNameNode));
            buffer.append(SPR);
            buffer.append(getNodeValueQ(dirNameNode));
            buffer.append(SPR);
            buffer.append(getNodeValueQ(fileNameNode));
            buffer.append(SPR);
            buffer.append(getNodeValueQ(keyNode));
            buffer.append(SPR);
            buffer.append(getNodeValueQ(sidNode));
            Node translation = sidNode.getNextSibling().getFirstChild();
            for (int j = 0; j < outputLocales.length - 1; ++j) {
               Node outputLocale = translation.getFirstChild();
               Node valueNode = outputLocale.getNextSibling();
               buffer.append(SPR);
               buffer.append(getNodeValueQ(valueNode));
               translation = translation.getNextSibling();
            }
            buffer.append(LF);
            writer.write(buffer.toString());
            if (PreferenceConstants.NATIVE.equals(filterMode)) {
               writerNaitive.write(buffer.toString().replaceAll("<br />", "\n"));
            }
         }
      }
      writer.flush();
      writer.close();
      if (PreferenceConstants.NATIVE.equals(filterMode)) {
         writerNaitive.flush();
         writerNaitive.close();
      }
   }


   public void evalXPathGenerateSIDs(String xpathExpr,
                                     Document doc,
                                     Map<String, PropertyForModule> modules)
      throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
   {
      BufferedWriter writer = PropertyUtils.openReportFile(outputFile);
      List<String> propertyFiles = new ArrayList<String>();
      writeHeaderLine(writer);
      XPath xpath = XPathFactory.newInstance().newXPath();
      XPathExpression expr = xpath.compile(xpathExpr);
      NodeList nodeList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); ++i) {
         Node propertyNode = nodeList.item(i);
         if (propertyNode != null) {
            StringBuffer buffer = new StringBuffer();
            Node keyNode = propertyNode.getFirstChild();
            Node sidNode = keyNode.getNextSibling();
            Node propertyFileNode = propertyNode.getParentNode().getParentNode();
            Node dirNameNode = propertyFileNode.getFirstChild();
            Node fileNameNode = dirNameNode.getNextSibling();
            Node moduleNode = propertyFileNode.getParentNode().getParentNode();
            Node moduleNameNode = moduleNode.getFirstChild();
            String path = rootDir + getNodeValue(dirNameNode) + getNodeValue(fileNameNode)
                          + "_" + PropertyUtils.SID_LOCALE + ".properties";
            File file = new File(path);
            if (!propertyFiles.contains(path)) {
               propertyFiles.add(path);
            }
            SortedProperties sortedProperties = PropertyUtils.readSortedProperties(file);
            if (sortedProperties == null) {
               sortedProperties = new SortedProperties();
               file.createNewFile();
            }
            String key = getNodeValue(keyNode);
            String sid = PropertyUtils.generateSID(getNodeValue(dirNameNode),
                                                   getNodeValue(fileNameNode), key,
                                                   getNodeValue(moduleNameNode), sidPre);
            sortedProperties.setProperty(key, sid);
            PropertyUtils.saveSortedProperties(sortedProperties, file);
            buffer.append(getNodeValueQ(moduleNameNode));
            buffer.append(SPR);
            buffer.append(getNodeValueQ(dirNameNode));
            buffer.append(SPR);
            buffer.append(getNodeValueQ(fileNameNode));
            buffer.append(SPR);
            buffer.append(getNodeValueQ(keyNode));
            buffer.append(SPR);
            buffer.append(sid);
            Node translation = sidNode.getNextSibling().getFirstChild();
            for (int j = 0; j < outputLocales.length - 1; ++j) {
               Node outputLocale = translation.getFirstChild();
               Node valueNode = outputLocale.getNextSibling();
               buffer.append(SPR);
               buffer.append(getNodeValueQ(valueNode));
               translation = translation.getNextSibling();
            }
            buffer.append(LF);
            writer.write(buffer.toString());
         }
      }
      writer.flush();
      writer.close();
      PropertyUtils.generateProperties(propertyFiles, rootDir);
   }


   private String getNodeValueQ(Node node)
   {
      String result = "";
      if (node.getFirstChild() != null) {
         result = node.getFirstChild().getNodeValue();
      }
      return "\"" + result.replaceAll("\"", "\"\"") + "\"";
   }


   private String getNodeValue(Node node)
   {
      String result = "";
      if (node.getFirstChild() != null) {
         result = node.getFirstChild().getNodeValue();
      }
      return result;
   }


   private String cdata(String value)
   {
      return "<![CDATA[" + value + "]]>";
   }


   public Collection<String> evalXPath(String xpathExpr, Document doc)
      throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
   {
      XPath xpath = XPathFactory.newInstance().newXPath();
      XPathExpression expr = xpath.compile(xpathExpr);
      Collection<String> result = new ArrayList<String>();
      NodeList nodeList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); ++i) {
         Node node = nodeList.item(i);
         if (node != null) {
            result.add(node.getNodeValue());
         }
      }
      return result;
   }


   public Document getDoc(String xml)
      throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
   {
      InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      docFactory.setNamespaceAware(false); // important!
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(in);
      return doc;
   }
}
