/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.6.0-SNAPSHOT
 * ____________________________________________________________________________
 *
 *         $Revision: 5647 $
 *    $LastChangedBy: qxa0556 $
 *  $LastChangedDate: 2012-04-25 07:35:29 +0200 (Mi, 25 Apr 2012) $
 *       Description: see Javadoc
 * ____________________________________________________________________________
 *
 *         Copyright: (c) BMW AG 2011, all rights reserved
 * ____________________________________________________________________________
 */
package com.bmw.cdp.propertiesimporter;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.xml.sax.SAXException;

public class PropertiesImporter {

	/**
	 * @param args
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws JAXBException
	 * @throws InvalidFormatException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static void main(String[] args)
            throws XPathExpressionException, IOException, ParserConfigurationException, SAXException, JAXBException, InvalidFormatException {
		System.out.println("Start");
		PropertyUtils utils = new PropertyUtils(args);
		String function = utils.getFunction();
		if (StringUtils.isEmpty(function)) {
			System.out.println("No report specified");
		} else if (PropertyUtils.REPORT_MISSING_TEXTSIDS.equals(function)) {
			PropertyFunction.missingTextSIDs(utils);
		} else if (PropertyUtils.REPORT_AVAILABLE_PROPERTIES.equals(function)) {
			PropertyFunction.availableProperties(utils);
		} else if (PropertyUtils.REPORT_MISSING_TEXTSIDS_LONG.equals(function)) {
			PropertyFunction.missingTextSIDsLong(utils);
		} else if (PropertyUtils.REPORT_AVAILABLE_PROPERTIES_LONG.equals(function)) {
			PropertyFunction.availablePropertiesLong(utils);
		} else if (PropertyUtils.REPORT_MISSING_PROPERTY_FILES.equals(function)) {
			PropertyFunction.missingPropertyFiles(utils);
		} else if (PropertyUtils.GENERATE_MISSING_TEXTSIDS.equals(function)) {
			PropertyFunction.generateMissingTextSIDs(utils);
		} else if (PropertyUtils.IMPORT_XML.equals(function)) {
			PropertyFunction.importXML(utils);
		} else if (PropertyUtils.IMPORT_EXCEL.equals(function)) {
			PropertyFunction.importExcel(utils);
		} else if (PropertyUtils.TRANSFORMATE_XML.equals(function)) {
			PropertyFunction.transformateXML(utils);
		}
		System.out.println("Output file: " + utils.getOutputFile());

	}
}
