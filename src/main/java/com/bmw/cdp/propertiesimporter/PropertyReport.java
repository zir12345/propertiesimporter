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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyReport {

	private final String outputFile;
	private final String rootDir;
	private final String[] outputLocales;
	private final Map<String, List<ModuleForSID>> sids;
	private Map<String, String> cdpProperties;
	private Map<String, List<ModuleForSID>> cdpPropertyFiles;
	private Map<String, List<ModuleForSID>> codePropertyFiles;

	private static final String SPR = "|";
	private static final String LF = "\n";

	private static final int sidColumn = 4;

	public PropertyReport(PropertyUtils propertyUtils, Map<String, List<ModuleForSID>> sids) {
		super();
		this.outputFile = propertyUtils.getOutputFile();
		this.rootDir = propertyUtils.getRootDir();
		this.outputLocales = propertyUtils.getOutputLocales();
		this.sids = sids;
		this.cdpProperties = new HashMap<String, String>();
		this.cdpPropertyFiles = new HashMap<String, List<ModuleForSID>>();
		this.codePropertyFiles = new HashMap<String, List<ModuleForSID>>();
	}

	private void writeHeaderLine(Writer writer) throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Module" + SPR + "Dir" + SPR + "File" + SPR + "Key" + SPR + "xy_XY");
		for (int j = 1; j < outputLocales.length; j++) {
			buffer.append(SPR);
			buffer.append(outputLocales[j]);
		}
		buffer.append(LF);
		writer.write(buffer.toString());
	}

	private void getCdpProperties() throws IOException {
		String[] pathMap = PropertyUtils.MODULES_PATH_MAP.get(PropertyUtils.getModuleName(rootDir));
		for (Map.Entry<String, List<ModuleForSID>> entry : sids.entrySet()) {
			String ident = entry.getKey();
			List<ModuleForSID> sidList = sids.get(ident);
			for (ModuleForSID sid : sidList) {
				String keyl = sid.getKey().toLowerCase();
				List<ModuleForSID> keyList = new ArrayList<ModuleForSID>();
				if (cdpPropertyFiles.containsKey(keyl)) {
					keyList = cdpPropertyFiles.get(keyl);
				}
				keyList.add(sid);
				cdpPropertyFiles.put(keyl, keyList);
				String filePath = sid.getDirName() + sid.getFileName();
				String[] copyResources = pathMap[7].split(";");
				for (String copyResource : copyResources) {
					if (filePath.contains(copyResource)) {
						codePropertyFiles.put(keyl, keyList);
					}
				}
				for (int j = 1; j < outputLocales.length; j++) {
					String path = rootDir + sid.getDirName() + sid.getFileName() + "_" + outputLocales[j]
							+ ".properties";
					File file = new File(path);
					SortedProperties sortedProperties = PropertyUtils.readSortedProperties(file);
					if (sortedProperties != null) {
						if (sortedProperties.containsKey(sid.getKey())) {
							String value = sortedProperties.getProperty(sid.getKey());
							cdpProperties.put(ident + outputLocales[j], value);
						}
					}
				}
			}
		}
	}

	private void getCodeProperties() throws IOException {
		PropertiesReferencesFinder prf = new PropertiesReferencesFinder(PropertyUtils.getModuleName(rootDir));
		prf.findReferencedPropertyFiles(rootDir);
		String[] pathMap = PropertyUtils.MODULES_PATH_MAP.get(PropertyUtils.getModuleName(rootDir));
		Map<String, PropertiesForFile> propertiesReferences = prf.getPropertiesReferences();
		Map<String, String> xslWebResource = prf.getXslWebResource();
		for (Map.Entry<String, PropertiesForFile> entry : propertiesReferences.entrySet()) {
			PropertiesForFile propertiesForFile = entry.getValue();
			if (PropertyUtils.JAVA.equals(propertiesForFile.getFileType())) {
				for (String xslFile : propertiesForFile.getXslFiles()) {
					String key = rootDir + pathMap[2] + "xsl\\" + xslFile + PropertyUtils.XSL;
					key = key.replace("/", "\\");
					if (propertiesReferences.containsKey(key)) {
						PropertiesForFile propertiesForFileXslFile = propertiesReferences.get(key);
						propertiesForFileXslFile.setResourceFiles(propertiesForFile.getResourceFiles());
						propertiesReferences.put(key, propertiesForFileXslFile);
					}
				}
			} else if (PropertyUtils.XSL.equals(propertiesForFile.getFileType())) {
				String fileKey = entry.getKey().replace(rootDir + pathMap[2] + "xsl\\", "").replace("\\", "/")
						.replace(".xsl", "").replace(pathMap[3], "");
				if (xslWebResource.containsKey(fileKey)) {
					List<String> resourceFiles = propertiesForFile.getResourceFiles();
					String resourceFile = xslWebResource.get(fileKey);
					if (!resourceFiles.contains(resourceFile)) {
						resourceFiles.add(resourceFile);
						propertiesForFile.setResourceFiles(resourceFiles);
						propertiesReferences.put(entry.getKey(), propertiesForFile);
					}
				}
				for (String xslFile : propertiesForFile.getXslFiles()) {
					String key = rootDir + pathMap[2] + "xsl\\" + xslFile;
					key = key.replace("/", "\\");
					if (propertiesReferences.containsKey(key)) {
						PropertiesForFile propertiesForFileXslFile = propertiesReferences.get(key);
						List<String> resourceFiles = propertiesForFileXslFile.getResourceFiles();
						if (!resourceFiles.contains(pathMap[5])) {
							resourceFiles.add(pathMap[0]);
						}
						propertiesForFileXslFile.setResourceFiles(resourceFiles);
						propertiesReferences.put(key, propertiesForFileXslFile);
					}
				}
			}
		}
		for (Map.Entry<String, PropertiesForFile> entry : propertiesReferences.entrySet()) {
			PropertiesForFile propertiesForFile = entry.getValue();
			String pathKey = entry.getKey();
			if (propertiesForFile.getResourceFiles().size() == 0 && propertiesForFile.getKeys().size() > 0) {
				findResourceFiles(propertiesReferences, propertiesForFile, pathKey);
			}
		}
		for (Map.Entry<String, PropertiesForFile> entry : propertiesReferences.entrySet()) {
			PropertiesForFile propertiesForFile = entry.getValue();
			for (String key : propertiesForFile.getKeys()) {
				String keyl = key.toLowerCase();
				List<ModuleForSID> keyList = new ArrayList<ModuleForSID>();
				for (String resourceFile : propertiesForFile.getResourceFiles()) {
					if (codePropertyFiles.containsKey(keyl)) {
						keyList = codePropertyFiles.get(keyl);
					}
					String basefileName = pathMap[2] + pathMap[4] + "\\" + resourceFile.replace("/", "\\");
					ModuleForSID sid = new ModuleForSID(PropertyUtils.getModuleName(rootDir), key,
							PropertyUtils.getDirName(basefileName), PropertyUtils.getFileName(basefileName));
					keyList.add(sid);
				}
				codePropertyFiles.put(keyl, keyList);
			}
		}
	}

	/**
	 * @param pathMap
	 * @param propertiesReferences
	 * @param propertiesForFile
	 * @param pathKey
	 */
	private void findResourceFiles(Map<String, PropertiesForFile> propertiesReferences,
			PropertiesForFile propertiesForFile, String pathKey) {
		String[] pathMap = PropertyUtils.MODULES_PATH_MAP.get(PropertyUtils.getModuleName(rootDir));
		Map<String, Integer> resourceMap = new HashMap<String, Integer>();
		for (String key : propertiesForFile.getKeys()) {
			String keyl = key.toLowerCase();
			if (!key.endsWith(".")) {
				if (cdpPropertyFiles.containsKey(keyl)) {
					List<ModuleForSID> keyList = cdpPropertyFiles.get(keyl);
					for (ModuleForSID sid : keyList) {
						String fileKey = sid.getDirName() + sid.getFileName();
						fileKey = fileKey.replace(pathMap[2], "").replace("\\", "/").replace(pathMap[5] + "/", "")
								.replace(pathMap[6], "../" + pathMap[6]);
						int cnt = 1;
						if (resourceMap.containsKey(fileKey)) {
							cnt = resourceMap.get(fileKey) + 1;
						}
						resourceMap.put(fileKey, cnt);
					}
				}
			}
		}
		List<String> resourceFiles = new ArrayList<String>();
		if (PropertyUtils.XSL.equals(propertiesForFile.getFileType())) {
			int max = 0;
			String resourceFile = "";
			for (Map.Entry<String, Integer> entry1 : resourceMap.entrySet()) {
				if (entry1.getValue() > max) {
					resourceFile = entry1.getKey().replace(pathMap[4] + "/", "");
					max = entry1.getValue();
				}
			}
			resourceFiles.add(resourceFile);
		} else {
			for (Map.Entry<String, Integer> entry1 : resourceMap.entrySet()) {
				resourceFiles.add(entry1.getKey().replace(pathMap[4] + "/", ""));
			}
		}
		propertiesForFile.setResourceFiles(resourceFiles);
		propertiesReferences.put(pathKey, propertiesForFile);
	}

	private String getCdpValue(String locale, String sid) throws IOException {
		String searchKey = sid + locale;
		String value = "";
		if (cdpProperties.containsKey(searchKey)) {
			value = "#" + cdpProperties.get(searchKey) + "#";
		}
		return value;
	}

}
