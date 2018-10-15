/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.6.0-SNAPSHOT
 * ____________________________________________________________________________
 *
 *         $Revision: 5647 $
 *    $LastChangedBy: qxa5203 $
 *  $LastChangedDate: 2012-04-25 07:35:29 +0200 (Mi, 25 Apr 2012) $
 *       Description: see Javadoc
 * ____________________________________________________________________________
 *
 *         Copyright: (c) BMW AG 2011, all rights reserved
 * ____________________________________________________________________________
 */
package com.bmw.cdp.propertiesimporter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PropertyFunction {

	public PropertyFunction() {
		super();
	}

	/**
	 * report missing TextSIDs into file in CSV Style
	 * 
	 * @param propertyUtils
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static void missingTextSIDs(PropertyUtils propertyUtils) throws IOException, XPathExpressionException,
			ParserConfigurationException, SAXException {
		Map<String, PropertyForModule> modules = PropertyForModule.getModules(propertyUtils);
		XMLWriter xmlWriter = new XMLWriter(propertyUtils);
		String xml = xmlWriter.getXml(modules);
		Document doc = xmlWriter.getDoc(xml);

		// write all translations without TextSID
		xmlWriter.evalXPathWrite("//property[sid='']", doc);
	}

	/**
	 * report missing TextSIDs into file
	 * 
	 * @param propertyUtils
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static void missingTextSIDsLong(PropertyUtils propertyUtils) throws IOException, XPathExpressionException,
			ParserConfigurationException, SAXException {
		Map<String, PropertyForModule> modules = PropertyForModule.getModules(propertyUtils);
		CSVWriter writer = new CSVWriter(propertyUtils);
		for (PropertyForModule module : modules.values()) {
			module.removePropertiesWithSID(propertyUtils);
		}
		writer.write(modules);
	}

	/**
	 * report available Properties into file in CSV Style
	 * 
	 * @param propertyUtils
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static void availableProperties(PropertyUtils propertyUtils) throws IOException, XPathExpressionException,
			ParserConfigurationException, SAXException {
		Map<String, PropertyForModule> modules = PropertyForModule.getModules(propertyUtils);
		XMLWriter xmlWriter = new XMLWriter(propertyUtils);
		String xml = xmlWriter.getXml(modules);
		Document doc = xmlWriter.getDoc(xml);

		// write all translations without TextSID
		xmlWriter.evalXPathWrite("//property", doc);
	}

	/**
	 * report available Properties into file
	 * 
	 * @param propertyUtils
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static void availablePropertiesLong(PropertyUtils propertyUtils) throws IOException,
			XPathExpressionException, ParserConfigurationException, SAXException {
		Map<String, PropertyForModule> modules = PropertyForModule.getModules(propertyUtils);
		CSVWriter writer = new CSVWriter(propertyUtils);
		writer.write(modules);
	}

	/**
	 * report available Properties into file
	 * 
	 * @param propertyUtils
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static void missingPropertyFiles(PropertyUtils propertyUtils) throws IOException, XPathExpressionException,
			ParserConfigurationException, SAXException {
		Map<String, PropertyForModule> modules = PropertyForModule.getModules(propertyUtils);
		CSVWriter writer = new CSVWriter(propertyUtils);
		writer.writeMissingFiles(modules);
	}

	/**
	 * report missing TextSIDs into file in CSV Style
	 * 
	 * @param propertyUtils
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static void generateMissingTextSIDs(PropertyUtils propertyUtils) throws IOException,
			XPathExpressionException, ParserConfigurationException, SAXException {
		Map<String, PropertyForModule> modules = PropertyForModule.getModules(propertyUtils);
		XMLWriter xmlWriter = new XMLWriter(propertyUtils);
		String xml = xmlWriter.getXml(modules);
		Document doc = xmlWriter.getDoc(xml);

		// write all translations without TextSID
		xmlWriter.evalXPathGenerateSIDs("//property[sid='']", doc, modules);
	}

	/**
	 * import TextWerk XML into given Project
	 * 
	 * @param propertyUtils
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @throws JAXBException
	 */
	public static void importXML(PropertyUtils propertyUtils) throws IOException, XPathExpressionException,
			ParserConfigurationException, SAXException, JAXBException {
		Map<String, PropertyForModule> modules = PropertyForModule.getModules(propertyUtils);
		Map<String, List<ModuleForSID>> sids = new HashMap<String, List<ModuleForSID>>();
		for (PropertyForModule module : modules.values()) {
			module.insertSIDs(propertyUtils, sids);
		}
		XMLReader xmlReader = new XMLReader(propertyUtils, sids);
		xmlReader.importPropertiesFromXML();
	}

	/**
	 * import TextWerk Properties Excel into given Project
	 *
	 * @param propertyUtils
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @throws JAXBException
	 * @throws InvalidFormatException
	 */
	public static void importExcel(PropertyUtils propertyUtils)
			throws IOException, XPathExpressionException, ParserConfigurationException, SAXException,
			JAXBException, InvalidFormatException
	{
		Map<String, PropertyForModule> modules = PropertyForModule.getModules(propertyUtils);
		Map<String, List<ModuleForSID>> sids = new HashMap<String, List<ModuleForSID>>();
		for (PropertyForModule module : modules.values()) {
			module.insertSIDsExcel(propertyUtils, sids);
		}
		ExcelReader excelReader = new ExcelReader(propertyUtils, sids);
		excelReader.importPropertiesFromExcel();
	}

	/**
	 * transformate XML into Labels with properties
	 * 
	 * @param propertyUtils
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @throws JAXBException
	 */
	public static void transformateXML(PropertyUtils propertyUtils) throws IOException, XPathExpressionException,
			ParserConfigurationException, SAXException, JAXBException {
		XMLReader xmlReader = new XMLReader(propertyUtils);
		xmlReader.transformXMLToProperties();
	}

}
