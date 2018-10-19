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
package com.bmw.kpi.propertiesimporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class CSVWriter {

	private final String outputFile;
	private final String[] inputLocales;
	private final String rootDir;
	private final String[] outputLocales;

	private static final String SPR = "|";
	private static final String LF = "\n";

	public CSVWriter(PropertyUtils propertyUtils) {
		super();
		this.outputFile = propertyUtils.getOutputFile();
		this.rootDir = propertyUtils.getRootDir();
		this.inputLocales = propertyUtils.getInputLocales();
		this.outputLocales = propertyUtils.getOutputLocales();
	}

	public void write(Map<String, PropertyForModule> modules)
			throws IOException {
		BufferedWriter writer = PropertyUtils.openReportFile(outputFile);
		List<String> keys = new ArrayList<String>();
		for (Object key : modules.keySet()) {
			keys.add(key.toString());
		}
		Collections.sort(keys);
		for (String key : keys) {
			PropertyForModule module = modules.get(key);
			StringBuffer buffer = new StringBuffer();
			buffer.append("Module:" + SPR);
			buffer.append(module.getModuleName());
			buffer.append(LF);
			writer.write(buffer.toString());
			for (File propertyFile : module.getPropertyFiles()) {
				this.writeProperties(writer, module, propertyFile);
				writer.write(LF);
			}
			writer.write(LF);
			writer.write(LF);
		}
		writer.flush();
		writer.close();
	}

	public void writeMissingFiles(Map<String, PropertyForModule> modules)
			throws IOException {
		BufferedWriter writer = PropertyUtils.openReportFile(outputFile);
		List<String> keys = new ArrayList<String>();
		for (Object key : modules.keySet()) {
			keys.add(key.toString());
		}
		Collections.sort(keys);
		for (String key : keys) {
			PropertyForModule module = modules.get(key);
			StringBuffer buffer = new StringBuffer();
			if (module.getMissingFiles().size() > 0) {
				buffer.append("Module:" + SPR);
				buffer.append(module.getModuleName());
				String lastBasefileName = "";
				for (File missingFile : module.getMissingFiles()) {
					String basefileName = PropertyUtils.getBaseFileName(
							missingFile, rootDir);
					if (!lastBasefileName.equals(basefileName)) {
						buffer.append(LF);
						buffer.append(PropertyUtils.getDirName(basefileName));
						buffer.append(SPR);
						buffer.append(PropertyUtils.getFileName(basefileName));
						lastBasefileName = basefileName;
					}
					String localeName = PropertyUtils.getLocaleName(
							missingFile, rootDir);
					buffer.append(SPR);
					buffer.append(localeName);
				}
				buffer.append(LF);
				writer.write(buffer.toString());
				writer.write(LF);
			}
		}
		writer.flush();
		writer.close();
	}

	private void writeHeaderLine(Writer writer) throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Dir" + SPR + "File" + SPR + "Key");
		for (String outputLocale : outputLocales) {
			buffer.append(SPR);
			buffer.append(outputLocale);
		}
		buffer.append(LF);
		writer.write(buffer.toString());
	}

	private void writeProperties(Writer writer, PropertyForModule module,
			File propertyFile) throws IOException {
		String basefileName = PropertyUtils.getBaseFileName(propertyFile,
				rootDir);
		Properties propertiesGlobal = module.getPropertiesGlobal().get(
				basefileName);

		if (propertiesGlobal == null || propertiesGlobal.isEmpty()) {
			return;
		}
		this.writeHeaderLine(writer);
		List<String> keys = new ArrayList<String>();
		for (Object key : propertiesGlobal.keySet()) {
			keys.add(key.toString());
		}
		Collections.sort(keys);
		for (String key : keys) {
			boolean print = false;
			for (String outputLocale : outputLocales) {
				Map<String, Properties> localeMap = module.getProperties().get(
						outputLocale);
				if (localeMap != null && localeMap.containsKey(basefileName)) {
					if (StringUtils.isNotEmpty(localeMap.get(basefileName)
							.getProperty(key, ""))) {
						print = true;
						break;
					}
				}
			}
			// print label only if is at least in one language defined
			if (print) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(PropertyUtils.getDirName(basefileName));
				buffer.append(SPR);
				buffer.append(PropertyUtils.getFileName(basefileName));
				buffer.append(SPR);
				buffer.append(key.toString());
				buffer.append(SPR);
				for (String outputLocale : outputLocales) {
					Map<String, Properties> localeMap = module.getProperties()
							.get(outputLocale);
					if (localeMap != null
							&& localeMap.containsKey(basefileName)) {
						String value = localeMap.get(basefileName).getProperty(
								key, "");
						value = value.replaceAll("\\r\\n", "<br/>");
						value = value.replaceAll("\\rn", "<br/>");
						value = value.replaceAll("\\n", "<br/>");
						value = StringEscapeUtils.unescapeHtml(value);
						buffer.append(value);
						buffer.append(SPR);
					} else {
						buffer.append(SPR);
					}
				}
				buffer.append(LF);
				writer.write(buffer.toString());
			}
		}
	}

}
