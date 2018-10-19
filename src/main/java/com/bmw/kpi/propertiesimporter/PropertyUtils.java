/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.6.0-SNAPSHOT
 * ____________________________________________________________________________
 *
 *         $Revision: 2061 $
 *    $LastChangedBy: qx93698 $
 *  $LastChangedDate: 2011-05-24 10:46:52 +0200 (Di, 24 Mai 2011) $
 *       Description: see Javadoc
 * ____________________________________________________________________________
 *
 *         Copyright: (c) BMW AG 2011, all rights reserved
 * ____________________________________________________________________________
 */
package com.bmw.kpi.propertiesimporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.bmw.kpi.propertiesimporter.bundle.BundleEntry;
import com.bmw.kpi.propertiesimporter.bundle.PropertiesGenerator;
import com.bmw.kpi.propertiesimporter.data.FAHRZEUGTEXTE;
import com.bmw.kpi.propertiesimporter.data.SPRACHVARIANTE;

public class PropertyUtils {

	private static final String ROOTDIR = "rootdir";
	private static final String INPUTLOCALES = "inputlocales";
	private static final String OUTPUTLOCALES = "outputlocales";
	private static final String OUTPUTFILE = "outputfile";
	private static final String INPUTFILE = "inputfile";
	private static final String RESOURCE = "resource";
	private static final String FUNCTION = "function";
	private static final String SIDPRE = "sidpre";
	private static final String IMPORTMODE = "importmode";
	private static final String FILTERMODE = "filtermode";
	private static final String BRAND = "brand";
	private static final String PORTAL = "portal";
	private static final String REPORTMODE = "reportmode";

	public static final String REPORT_MISSING_TEXTSIDS = "missingtextsids";
	public static final String REPORT_AVAILABLE_PROPERTIES = "availableproperties";
	public static final String REPORT_MISSING_TEXTSIDS_LONG = "missingtextsidslong";
	public static final String REPORT_AVAILABLE_PROPERTIES_LONG = "availablepropertieslong";
	public static final String REPORT_MISSING_PROPERTY_FILES = "missingpropertyfiles";
	public static final String GENERATE_MISSING_TEXTSIDS = "generatemissingtextsids";
	public static final String TRANSFORMATE_XML = "transformatexml";
	public static final String IMPORT_XML = "importxml";
	public static final String IMPORT_EXCEL = "importexcel";

	public static final String ROOT_DIR = "D:\\dev\\ws_live\\BMW.live\\portals\\nbt";
	public static final String BON_PRE = "SID_BON_";
	public static final String SID_LOCALE = "xy_XY";

	public static final String START = "start";
	public static final String APP = "app";
	public static final String APPSTORE = "appstore";
	public static final String COMMON = "common";
	public static final String MAIN = "main";
	public static final String RESOURCES = "resources";
	public static final String I18N = "i18n";
	public static final String EXLUDES_LOCALES_DE_ALL = "de_AT;de_BE;de_CH;de_DE;de_LU";
	public static final String EXLUDES_LOCALES_DE_OTHER_DE = "de_AT;de_BE;de_CH;de_LU";
	public static final String EXLUDES_LOCALES_DE_OTHER_AT = "de_BE;de_CH;de_DE;de_LU";
	public static final String[] INPUT_LOCALES = { "xy_XY", "de_AT", "de_DE", "en_AE", "en_CA", "en_KW", "en_GB",
			"en_US", "es_ES", "fr_CA", "fr_FR", "hu_HU", "it_IT", "nl_NL", "pl_PL", "pt_PT", "ru_RU", "sv_SE", "zh_CN" };
	public static final String[] OUTPUT_LOCALES = { "xy_XY", "de_AT", "de_DE", "en_AE", "en_CA", "en_KW", "en_GB",
			"en_US", "es_ES", "fr_CA", "fr_FR", "hu_HU", "it_IT", "nl_NL", "pl_PL", "pt_PT", "ru_RU", "sv_SE", "zh_CN" };

	public static final String LINKSTORE_DBS = "TST,INT";
	public static final String MESSAGE_DBS = "TST,INT";

	public static final String OUTPUT_FILE = "D:/tmp/portal_nbt_properties.txt";
	public static final String INPUT_FILE = "D:/tmp/nbt/nbt_texte_v2.15.0.txt";
	public static final String IMPORT_MODE = "yellow";
	public static final String FILTER_MODE = "filter";
	public static final String REPORT_MODE = "delta";

	public static final String BRAND_DEFAULT = "BM";

	private final static Pattern PATTERN_VARIATION_FIELD_DE = Pattern.compile("_VARFLDDE_.*");
	private final static Pattern PATTERN_VARIATION_FIELD_AT = Pattern.compile("_VARFLDAT_.*");
	private final static Pattern PATTERN_VARIATION_FIELD = Pattern.compile("_VARFLD_.*");

	private String rootDir = ROOT_DIR;
	private String[] inputLocales = {};
	private String[] outputLocales = {};
	private String outputFile = OUTPUT_FILE;
	private String inputFile = INPUT_FILE;
	private String resource = "";
	private String function = "";
	private String sidPre = BON_PRE;
	private String importMode = IMPORT_MODE;
	private String filterMode = FILTER_MODE;
	private String brand = BRAND_DEFAULT;
	private String portal = "";
	private String reportMode = REPORT_MODE;

	public static final Map<String, String[]> MODULES_PATH_MAP;

	public static final String CRLF = "\r\n";
	public static final String LF = "\n";
	public static final String BR = "<BR />";

	public final static String XML = ".xml";
	public final static String XSL = ".xsl";
	public final static String JAVA = ".java";

	public static CellStyle                   cs                               = null;
	public static CellStyle                   csBold                           = null;
	public static CellStyle                   csTop                            = null;
	public static CellStyle                   csRight                          = null;
	public static CellStyle                   csBottom                         = null;
	public static CellStyle                   csLeft                           = null;
	public static CellStyle                   csTopLeft                        = null;
	public static CellStyle                   csTopRight                       = null;
	public static CellStyle                   csBottomLeft                     = null;
	public static CellStyle                   csBottomRight                    = null;
	public static CellStyle                   csYellow                         = null;
	public static CellStyle                   csRed                            = null;
	public static CellStyle                   csOrange                         = null;
	public static CellStyle                   csGreen                          = null;
	public static CellStyle                   csBlue                           = null;

	public PropertyUtils(String[] args) {
		super();
		boolean fixedParam = true;
		if (args.length > 0) {
			for (String arg : args) {
				if (arg.startsWith("-") && arg.indexOf("=") > 1 && !arg.endsWith("=")) {
					String param = arg.substring(1, arg.indexOf("="));
					String value = arg.substring(arg.indexOf("=") + 1);
					if (FUNCTION.equals(param)) {
						this.function = value;
					} else if (ROOTDIR.equals(param)) {
						setRootDir(value);
					} else if (OUTPUTFILE.equals(param)) {
						setOutputFile(value);
					} else if (INPUTFILE.equals(param)) {
						setInputFile(value);
					} else if (PORTAL.equals(param)) {
						this.portal = value;
					} else if (INPUTLOCALES.equals(param)) {
						this.inputLocales = value.split(",");
					} else if (OUTPUTLOCALES.equals(param)) {
						this.outputLocales = value.split(",");
					} else if (BRAND.equals(param)) {
						this.brand = value;
					} else if (RESOURCE.equals(param)) {
						this.resource = value;
					} else if (FUNCTION.equals(param)) {
						this.function = value;
					} else if (SIDPRE.equals(param)) {
						this.sidPre = value;
					} else if (IMPORTMODE.equals(param)) {
						this.importMode = value;
					} else if (FILTERMODE.equals(param)) {
						this.filterMode = value;
					} else if (REPORTMODE.equals(param)) {
						this.reportMode = value;
					}
					fixedParam = false;
				}
			}

			if (fixedParam) {
				if (args == null || args.length < 5 || args.length > 6) {
					toolTip(0);
				}

				this.function = args[0];
				setRootDir(args[1]);
				setOutputFile(args[2]);
				setInputFile(args[3]);
				if (args[4].contains("_")) {
					this.inputLocales = args[4].split(",");
				} else {
					this.portal = args[4];
				}
				if (args.length == 6) {
					this.brand = args[5];
				}
			}
			if (portal.equals("NBT")) {
				this.inputLocales = PreferenceConstants.LOCALES_NBT.split(",");
			} else if (portal.equals("COOL")) {
				this.inputLocales = PreferenceConstants.LOCALES_COOL.split(",");
			} else if (portal.equals("INTERNET")) {
				this.inputLocales = PreferenceConstants.LOCALES_INTERNET.split(",");
			} else if (portal.equals("FUPO")) {
				this.inputLocales = PreferenceConstants.LOCALES_FUPO.split(",");
			} else if (portal.equals("ROUTES")) {
				this.inputLocales = PreferenceConstants.LOCALES_ROUTES.split(",");
			} else {
				if (inputLocales.length == 0) {
					this.inputLocales = INPUT_LOCALES;
				}
			}
			if (outputLocales.length == 0) {
				this.outputLocales = inputLocales;
			}
		} else {
			toolTip(0);
		}
	}

	public void toolTip(int mode) {
		if (mode == 0) {
			System.err.println("----------------------------------------------------------------------");
			System.err.println(" Usage: propertiesimporter.jar");
			System.err.println("   first argument, function");
			System.err.println("   second argument, rootDir");
			System.err.println("   third argument, outputFile");
			System.err.println("   fourth argument, inputFile");
			System.err.println("   fifth argument, inputLocales (de_DE,de_AT...) or portal (NBT or COOL)");
			System.err.println("   sixth argument optional, brand");
			System.err.println(" Examples propertiesimporter.jar from nbt/textwerk | portal_bmwlive/textwerk root");
			System.err
					.println("   java -jar propertiesimporter.jar importxml .. report_gen3_bmw.txt CD_BMWOnline_NBT_ALL-BMW.xml NBT BMW");
			System.err
					.println("   java -jar propertiesimporter.jar importxml .. report_gen2_bmw.txt CD_BMWOnline_COOL_ALL-BMW.xml COOL BMW");
			System.err.println("----------------------------------------------------------------------");
			System.exit(1);
		}
	}

	/**
	 * @return the rootDir
	 */
	public String getRootDir() {
		return rootDir;
	}

	/**
	 * @return the inputLocales
	 */
	public String[] getInputLocales() {
		return inputLocales;
	}

	/**
	 * @return the outputLocales
	 */
	public String[] getOutputLocales() {
		return outputLocales;
	}

	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * @param rootDir
	 *            the rootDir to set
	 */
	public void setRootDir(String filepath) {
		Path abs = Paths.get(filepath);
		this.rootDir = abs.toAbsolutePath().toString();
	}

	/**
	 * @param inputLocale
	 *            the inputLocale to set
	 */
	public void setInputLocales(String[] inputLocales) {
		this.inputLocales = inputLocales;
	}

	/**
	 * @param outputLocales
	 *            the outputLocales to set
	 */
	public void setOutputLocales(String[] outputLocales) {
		this.outputLocales = outputLocales;
	}

	/**
	 * @param outputFile
	 *            the outputFile to set
	 */
	public void setOutputFile(String filepath) {
		Path abs = Paths.get(filepath);
		this.outputFile = abs.toAbsolutePath().toString();
	}

	/**
	 * @return the inputFile
	 */
	public String getInputFile() {

		return inputFile;
	}

	/**
	 * @param inputFile
	 *            the inputFile as absolute File to set
	 */
	public void setInputFile(String filepath) {
		Path abs = Paths.get(filepath);
		this.inputFile = abs.toAbsolutePath().toString();
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @param function
	 *            the function to set
	 */
	public void setFunction(String function) {
		this.function = function;
	}

	/**
	 * @return the sidPre
	 */
	public String getSidPre() {
		return sidPre;
	}

	/**
	 * @param sidPre
	 *            the sidPre to set
	 */
	public void setSidPre(String sidPre) {
		this.sidPre = sidPre;
	}

	public String getImportMode() {
		return importMode;
	}

	public void setImportMode(String importMode) {
		this.importMode = importMode;
	}

	public String getFilterMode() {
		return filterMode;
	}

	public void setFilterMode(String filterMode) {
		this.filterMode = filterMode;
	}

	public String getReportMode() {
		return reportMode;
	}

	public void setReportMode(String reportMode) {
		this.reportMode = reportMode;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public static String getBaseFileName(File file, String rootDir) {
		String result = "";
		String value = file.getAbsolutePath().replace(rootDir, "");
		int idx = value.lastIndexOf("_");
		if (idx > -1) {
			String name = value.substring(0, idx - 3);
			int idx2 = value.lastIndexOf("_", idx - 4); // start_zh_ZN_ZI
			if (idx2 == idx - 6) {
				name = value.substring(0, idx2);
			}
			result = name;
		}
		return result;
	}

	public static String getLocaleName(File file, String rootDir) {
		String result = "";
		String value = file.getAbsolutePath().replace(rootDir, "");
		int idx = value.lastIndexOf("_");
		if (idx > -1) {
			String locale = value.substring(idx - 2, idx + 3);
			int idx2 = value.lastIndexOf("_", idx - 4); // start_zh_ZN_ZI
			if (idx2 == idx - 6) {
				locale = value.substring(idx2 + 1, idx2 + 9);
			}
			result = locale;
		}
		return result;
	}

	public static BufferedWriter openReportFile(String outputFile) throws IOException {
		File dir = new File(PropertyUtils.getDirName(outputFile));
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(new File(outputFile));
		// File is UTF8 Prefix
		fos.write(239);
		fos.write(187);
		fos.write(191);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
		return writer;
	}

	public static void writePropertiesFile(String content, String path) throws IOException {
		FileOutputStream fos = new FileOutputStream(new File(path));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
		writer.write(content);
		writer.flush();
		writer.close();
	}

	/**
	 * @param propertyFiles
	 * @throws IOException
	 */
	public static void generateProperties(List<String> propertyFiles, String dir) throws IOException {
		for (String propertyFile : propertyFiles) {
			File file = new File(propertyFile);
			FileReader fr = new FileReader(propertyFile);
			BufferedReader br = new BufferedReader(fr);

			StringBuffer buffer = new StringBuffer();
			buffer.append(PropertiesGenerator.GENERATED_BY);
			buffer.append(CRLF);
			String locale = PropertyUtils.getInputLocale(file, dir);
			Properties properties = PropertyUtils.readProperties(file);
			if (properties != null && !properties.isEmpty()) {
				PropertyBundle bundle = new PropertyBundle();
				bundle.setComment(buffer.toString());
				for (Map.Entry<Object, Object> entry : properties.entrySet()) {
					String key = entry.getKey().toString();
					String value = properties.get(key).toString();
					BundleEntry bundleEntry = new BundleEntry(key, value, "");
					bundle.addBundleEntry(bundleEntry, new Locale(locale));
				}
				String content = PropertiesGenerator.generate(bundle);
				PropertyUtils.writePropertiesFile(content, propertyFile);
			}
			br.close();
			fr.close();
		}
	}

	public static BufferedWriter openSqlFile(String outputFile) throws IOException {
		File dir = new File(PropertyUtils.getDirName(outputFile));
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		// File is UTF8 Prefix
		FileOutputStream fos = new FileOutputStream(new File(outputFile));
		fos.write(45);
		fos.write(45);
		fos.write(239);
		fos.write(187);
		fos.write(191);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
		return writer;
	}

	public static String getFileName(String basefileName) {
		int idx = basefileName.lastIndexOf("\\");
		return basefileName.substring(idx + 1);
	}

	public static String getResourceName(String resouceFile) {
		int idx = resouceFile.lastIndexOf("/");
		return resouceFile.substring(idx + 1);
	}

	public static String getModuleName(String rootDir) {
		int idx = rootDir.lastIndexOf("\\");
		return rootDir.substring(idx + 1);
	}

	public static String getDirName(String basefileName) {
		int idx = basefileName.lastIndexOf("\\");
		return basefileName.substring(0, idx + 1);
	}

	public static String getInputLocale(File file, String rootDir) {
		String result = "";
		String value = file.getAbsolutePath().replace(rootDir, "");
		int idx = value.lastIndexOf("_");
		if (idx > -1) {
			String locale = value.substring(idx - 2, idx + 3);
			int idx2 = value.lastIndexOf("_", idx - 4); // start_zh_ZN_ZI
			if (idx2 == idx - 6) {
				locale = value.substring(idx2 + 1, idx2 + 9);
			}
			result = locale;
		}
		return result;
	}

	public static Properties readProperties(File file) throws IOException {
		if (!file.exists()) {
			return null;
		}
		Properties properties = new Properties();
		FileInputStream in = new FileInputStream(file);
		properties.load(in);
		in.close();
		return properties;
	}

	public static SortedProperties readSortedProperties(File file) throws IOException {
		if (!file.exists()) {
			return null;
		}
		SortedProperties sortedProperties = new SortedProperties();
		FileInputStream in = new FileInputStream(file);
		sortedProperties.load(in);
		in.close();
		return sortedProperties;
	}

	public static File saveProperties(Properties properties, File file) throws FileNotFoundException, IOException

	{
		if (!file.exists()) {
			return null;
		}
		FileOutputStream out = new FileOutputStream(file);
		properties.store(out, "");
		out.close();
		return file;
	}

	public static File saveSortedProperties(SortedProperties sortedProperties, File file) throws FileNotFoundException,
			IOException {
		if (!file.exists()) {
			return null;
		}
		FileOutputStream out = new FileOutputStream(file);
		sortedProperties.store(out, "");
		out.close();
		return file;
	}

	public static String generateSID(String dirName, String fileName, String key, String modulename, String sidPre) {
		String sid = "SID";
		String[] dirs = dirName.split("\\\\");
		String[] labels = key.split("\\.");
		String lastDir = dirs[dirs.length - 1];
		sid = sidPre + modulename;
		// "nbt_appstore","\portal_nbt_appstore_war\src\main\webapp\static\resources\office\","start","Label.Error.NotSubscribed","SID_BON_NBT_APPSTORE_OFFICE_ERROR_NOTSUBSCRIBED"
		if (!(modulename.toLowerCase().indexOf(PropertyUtils.APPSTORE) == -1 && (PropertyUtils.APP.equals(lastDir
				.toLowerCase())
				|| PropertyUtils.COMMON.equals(lastDir.toLowerCase())
				|| PropertyUtils.RESOURCES.equals(lastDir.toLowerCase()) || PropertyUtils.I18N.equals(lastDir
				.toLowerCase())))) {
			sid += "_" + lastDir;
		}
		if (!PropertyUtils.START.equals(fileName) && modulename.toLowerCase().indexOf(fileName.toLowerCase()) == -1) {
			sid += "_" + fileName;
		}
		int start = 0;
		if (labels[0].equals("Label")) {
			start = 1;
		}
		for (int j = start; j < labels.length; j++) {
			sid += "_" + labels[j];
		}
		sid = StringUtils.upperCase(sid).replaceAll("-", "_");
		return sid;
	}

	public static String getLocale(String value) {
		return getLocale(value, PropertyUtils.BRAND_DEFAULT);
	}

	public static String getLocale(String value, String brand) {
		String result = "";
		int idx = value.lastIndexOf("_");
		if (idx > -1) {
			String locale = value.substring(idx - 2, idx + 3);
			int idx2 = value.lastIndexOf("_", idx - 1);
			if (idx2 > -1) {
				locale = value.substring(idx2 - 2, idx2 + 6);
			} else {
				locale = PropertyUtils.getBrandLocale(locale, brand);
			}
			result = locale;
		}
		return result;
	}

	public static String getLocale(String value, List<String> importLocales) {
		return getLocale(value, importLocales, PropertyUtils.BRAND_DEFAULT);
	}

	public static String getLocale(String value, List<String> importLocales, String brand) {
		String result = "";
		int idx = value.lastIndexOf("_");
		if (idx > -1) {
			String locale = value.substring(idx - 2, idx + 3);
			int idx2 = value.lastIndexOf("_", idx - 1);
			if (idx2 > -1) {
				locale = value.substring(idx2 - 2, idx2 + 6);
			} else {
				locale = PropertyUtils.getBrandLocale(locale, brand);
			}
			if (importLocales.contains(locale)) {
				result = locale;
			}
		}
		return result;
	}

	public static String getBrandLocale(String locale, String brand) {
		String result = locale;
		if (StringUtils.isNotBlank(brand)) {
			if (!Brand.getBrand(brand).equals(Brand.BMW)) {
				result = locale + "_" + Brand.getBrand(brand).getShortcut();
			}
		}
		return result;
	}

	public static String getShortBrand(String brand) {
		String result = Brand.BMW.getShortcut();
		if (StringUtils.isNotBlank(brand)) {
			result = Brand.getBrand(brand).getShortcut();
		}
		return result;
	}

	public static String getSqlBrand(String locale, String defaultBrand) {
		String result = Brand.BMW.getShortcut();
		if (locale.split("_").length == 3) {
			result = Brand.getBrand(locale.split("_")[2]).getShortcut();
		}
		return result;
	}

	public static String getXMLLocale(SPRACHVARIANTE sprachvariante, List<String> importLocales) {
		return getXMLLocale(sprachvariante, importLocales, PropertyUtils.BRAND_DEFAULT);
	}

	public static String getXMLLocale(SPRACHVARIANTE sprachvariante, List<String> importLocales, String brand) {
		String value = sprachvariante.getSPRACHE();
		if (sprachvariante.getISONAME() != null) {
			value = sprachvariante.getISONAME();
		}
		return getLocale(value, importLocales, brand);
	}

	public static String getStringValue(XSSFCell cell)
	{
		return getStringValue(cell, null, null);
	}


	public static String getStringValue(XSSFCell cell, ParentSID parentSid, String locale)
	{
		String value = "";
		if (parentSid != null && parentSid.getExcludeLocales().contains(locale)) {
			value = null;
		}
		else if (XSSFCell.CELL_TYPE_BLANK == cell.getCellType()) {
			value = "";
		}
		else if (XSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
			value = cell.getStringCellValue();
			if (value == null || "0".equals(value) || value.contains("n. t. a.") || value.contains("n.t.a.")) {
				if (parentSid != null && parentSid.getSid() != null) {
					value = null;
				}
				else {
					value = "";
				}
				value = "";
			}
			else if (value.contains("[leerer String]")) {
				if (parentSid == null || parentSid.isParent()) {
					value = " ";
				}
				else {
					value = null;
				}
			}
			else if (parentSid != null && parentSid.getSid() != null
					&& (value.contains("n. y. t.") || value.contains("n.y.t."))) {
				value = parentSid.getSid();
			}
		}
		else if (XSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
			if (cell.getNumericCellValue() == 0) {
				value = "";
			}
			else {
				value = new BigDecimal(Double.toString(cell.getNumericCellValue())).toPlainString();
				String[] numstr = value.split("\\.");
				if (numstr.length > 1 && "0".equals(numstr[1])) {
					value = numstr[0];
				}
			}
		}
		return value;
	}


	public static String getStringValue(String value) {
		return getStringValue(value, null, null);
	}

	public static ParentSID getParentSID(String subSid) {
		subSid = subSid.trim().replaceAll("(\\r)?\\n", "");
		String sid = subSid;
		boolean parent = true;
		String excludeLocales = "";
		Matcher matcher = PATTERN_VARIATION_FIELD.matcher(subSid);
		Matcher matcherDE = PATTERN_VARIATION_FIELD_DE.matcher(subSid);
		Matcher matcherAT = PATTERN_VARIATION_FIELD_AT.matcher(subSid);
		if (matcher.find()) {
			sid = subSid.substring(0, matcher.start());
			parent = false;
			excludeLocales = EXLUDES_LOCALES_DE_ALL;
		} else if (matcherDE.find()) {
			sid = subSid.substring(0, matcherDE.start());
			parent = false;
			excludeLocales = EXLUDES_LOCALES_DE_OTHER_DE;
		} else if (matcherAT.find()) {
			sid = subSid.substring(0, matcherAT.start());
			parent = false;
			excludeLocales = EXLUDES_LOCALES_DE_OTHER_AT;
		}
		return new ParentSID(sid, subSid, parent, excludeLocales);
	}

	public static String getStringValue(String value, ParentSID parentSid, String locale, String master) {
		if (parentSid != null && parentSid.getExcludeLocales().contains(locale)
				&& (StringUtils.isBlank(master) || master.equals(value))) {
			value = null;
		} else if (value == null || "0".equals(value) || value.contains("n. t. a.") || value.contains("n.t.a.")) {
			if (parentSid != null && parentSid.getSid() != null) {
				value = null;
			} else {
				value = "";
			}
		} else if (value.length() == 0) {
			if (parentSid == null || parentSid.isParent()) {
				value = " ";
			} else {
				value = null;
			}
		} else if (parentSid != null && parentSid.getSid() != null
				&& (value.contains("n. y. t.") || value.contains("n.y.t."))) {
			value = parentSid.getSid();
		}

		return value;
	}

	public static String getStringValue(String value, ParentSID parentSid, String locale) {
		return getStringValue(value, parentSid, locale, null);
	}

	public static SAXSource getSAXSource(String file) throws ParserConfigurationException, SAXNotRecognizedException,
			SAXNotSupportedException, FileNotFoundException, UnsupportedEncodingException, SAXException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://apache.org/xml/features/validation/schema", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		BOMInputStream bOMInputStream = new BOMInputStream(new FileInputStream(file));

		InputSource inputSource = new InputSource(new InputStreamReader(bOMInputStream, "UTF-8"));
		SAXSource source = new SAXSource(spf.newSAXParser().getXMLReader(), inputSource);
		return source;
	}

	/**
	 * @param xml
	 * @return
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static FAHRZEUGTEXTE unmarshalTextwerkXml(String file) throws JAXBException, ParserConfigurationException,
			SAXException, FileNotFoundException, UnsupportedEncodingException {
		FAHRZEUGTEXTE fahrzeugTexte = null;
		final JAXBContext jc = JAXBContext.newInstance(FAHRZEUGTEXTE.class);

		final SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		spf.setFeature("http://apache.org/xml/features/validation/schema", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		spf.setNamespaceAware(true);

		final org.xml.sax.XMLReader xmlReader = spf.newSAXParser().getXMLReader();

		final BOMInputStream bOMInputStream = new BOMInputStream(new FileInputStream(file));
		final InputSource inputSource = new InputSource(new InputStreamReader(bOMInputStream, "UTF-8"));
		final SAXSource source = new SAXSource(xmlReader, inputSource);

		final Unmarshaller unmarshaller = jc.createUnmarshaller();
		fahrzeugTexte = (FAHRZEUGTEXTE) unmarshaller.unmarshal(source);

		return fahrzeugTexte;

	}

	public static String getTemplatePart(Integer templateId) {
		String templatePart = String.valueOf(templateId);
		return templatePart.substring(templatePart.length() - 5, templatePart.length() - 1);
	}

	static {
		MODULES_PATH_MAP = new LinkedHashMap<String, String[]>();
		MODULES_PATH_MAP.put("portal_internet_war", new String[] { "Common", "menudefinition/MenuInternet",
				"\\src\\main\\webapp\\cdp_static\\", "Rebrush", "resources", "common", "config", "pia\\Pia" });

	}
}