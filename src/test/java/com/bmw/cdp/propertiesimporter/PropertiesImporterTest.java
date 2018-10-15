/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.16.0-SNAPSHOT
 * ____________________________________________________________________________
 *
 *         $Revision:  $
 *    $LastChangedBy:  $
 *  $LastChangedDate:  $
 *       Description: see Javadoc
 * ____________________________________________________________________________
 *
 *         Copyright: (c) BMW AG 2011, all rights reserved
 * ____________________________________________________________________________
 */
/**
 * 
 */
package com.bmw.cdp.propertiesimporter;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author ZIR
 */
public class PropertiesImporterTest {

	private String userDir;
	private String rootDir;
	private String outputDir;
	private String outputFile;
	private String inputFile;
	private String importDir;
	private char SEP = File.separatorChar;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.userDir = System.getProperty("user.dir");
		this.rootDir = userDir + SEP + "src" + SEP + "test" + SEP + "projects";
		this.outputDir = userDir + SEP + "src" + SEP + "test" + SEP + "reports";
		this.importDir = userDir + SEP + "src" + SEP + "test" + SEP + "textwerk";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAvailableProperties()
			throws XPathExpressionException, IOException, ParserConfigurationException, SAXException, JAXBException, InvalidFormatException {
		this.outputFile = outputDir + SEP + "portal_test_availableproperties.txt";
		String[] args = { "-function=availableproperties", "-rootdir=" + rootDir + SEP + "test",
				"-outputfile=" + outputFile };
		PropertiesImporter.main(args);
		File file = new File(outputFile);
		assertTrue("Filesize is " + file.length() + " expected size 12851", (file.length() == 12851));
	}

	@Test
	public void testMissingTextSIDs()
			throws XPathExpressionException, IOException, ParserConfigurationException, SAXException, JAXBException, InvalidFormatException {
		this.outputFile = outputDir + SEP + "portal_test_missingtextsids.txt";
		String[] args = { "-function=missingtextsids", "-rootdir=" + rootDir + SEP + "test",
				"-outputfile=" + outputFile };
		PropertiesImporter.main(args);
		File file = new File(outputFile);
		assertTrue("Filesize is " + file.length() + " expected size 183", (file.length() == 183));
	}

	@Test
	public void testNBTImportXML()
			throws XPathExpressionException, IOException, ParserConfigurationException, SAXException, JAXBException, InvalidFormatException {
		this.outputFile = outputDir + SEP + "nbt_importxml.txt";
		this.inputFile = importDir + SEP + "CD_BMWOnline_NBT_ALL-BMW.xml";
		String[] args = { "-function=importxml", "-rootdir=" + rootDir + SEP + "nbt", "-inputfile=" + inputFile,
				"-outputfile=" + outputFile, "-outputlocales=" + PreferenceConstants.LOCALES_NBT,
				"-inputlocales=" + PreferenceConstants.LOCALES_NBT };
		PropertiesImporter.main(args);
		File file = new File(outputFile);
		assertTrue("Filesize is " + file.length() + " expected size 4674", (file.length() == 4674));
	}

	@Test
	public void testNBTZinoroImportXML()
			throws XPathExpressionException, IOException, ParserConfigurationException, SAXException, JAXBException, InvalidFormatException {
		this.outputFile = outputDir + SEP + "nbt_ZINORO_importxml.txt";
		this.inputFile = importDir + SEP + "CD_BMWOnline_NBT_ALL-ZINORO.xml";
		String[] args = { "-function=importxml", "-rootdir=" + rootDir + SEP + "nbt", "-inputfile=" + inputFile,
				"-brand=ZINORO", "-outputfile=" + outputFile, "-portal=NBT" };
		PropertiesImporter.main(args);
		File file = new File(outputFile);
		assertTrue("Filesize is " + file.length() + " expected size 47", (file.length() == 47));
	}

	@Test
	public void testCOOLImportXML()
			throws XPathExpressionException, IOException, ParserConfigurationException, SAXException, JAXBException, InvalidFormatException {
		this.outputFile = outputDir + SEP + "cool_importxml.txt";
		this.inputFile = importDir + SEP + "CD_BMWOnline_COOL_ALL-BMW.xml";
		String[] args = { "-function=importxml", "-rootdir=" + rootDir + SEP + "portal_bmwlive",
				"-inputfile=" + inputFile, "-brand=BMW", "-outputfile=" + outputFile,
				"-outputlocales=" + PreferenceConstants.LOCALES_COOL,
				"-inputlocales=" + PreferenceConstants.LOCALES_COOL };
		PropertiesImporter.main(args);
		File file = new File(outputFile);
		assertTrue("Filesize is " + file.length() + " expected size 1259", (file.length() == 1259));
	}

	@Test
	public void testNBTImportExcel()
			throws XPathExpressionException, IOException, ParserConfigurationException, SAXException, JAXBException, InvalidFormatException {
		this.outputFile = outputDir + SEP + "nbt_importexcel.txt";
		this.inputFile = importDir + SEP + "CD_BMWOnline_NBT_ALL-BMW.xlsx";
		String[] args = { "-function=importexcel", "-rootdir=" + rootDir + SEP + "nbt", "-inputfile=" + inputFile,
				"-outputfile=" + outputFile, "-outputlocales=" + PreferenceConstants.LOCALES_NBT,
				"-inputlocales=" + PreferenceConstants.LOCALES_NBT };
		PropertiesImporter.main(args);
		File file = new File(outputFile);
		assertTrue("Filesize is " + file.length() + " expected size 437", (file.length() == 437));
	}

}
