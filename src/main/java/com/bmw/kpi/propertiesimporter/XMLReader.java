/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.6.0-SNAPSHOT
 * ____________________________________________________________________________
 *
 *         $Revision: 5647 $
 *    $LastChangedBy: qxa5203 $
 *  $LastChangedDate: 2012-04-12 07:35:29 +0200 (Do, 12 Apr 2012) $
 *       Description: see Javadoc
 * ____________________________________________________________________________
 *
 *         Copyright: (c) BMW AG 2011, all rights reserved
 * ____________________________________________________________________________
 */
package com.bmw.kpi.propertiesimporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.bmw.kpi.propertiesimporter.data.FAHRZEUGTEXTE;
import com.bmw.kpi.propertiesimporter.data.SPRACHVARIANTE;
import com.bmw.kpi.propertiesimporter.data.TEXT;
import com.bmw.kpi.propertiesimporter.datatransform.Content;
import com.bmw.kpi.propertiesimporter.bundle.Bundle;

public class XMLReader extends Bundle {
	private final String outputFile;
	private final String inputFile;
	private final String resource;
	private final String[] inputLocales;
	private final String rootDir;
	private final String brand;
	private final String reportMode;
	private final Map<String, List<ModuleForSID>> sids;

	private static final String SPR = "|";
	private static final String LF = "\n";
	private static final String LABEL = "Label.Text.";

	public XMLReader(PropertyUtils propertyUtils, Map<String, List<ModuleForSID>> sids) {
		super();
		this.outputFile = propertyUtils.getOutputFile();
		this.inputFile = propertyUtils.getInputFile();
		this.resource = propertyUtils.getResource();
		this.rootDir = propertyUtils.getRootDir();
		this.inputLocales = propertyUtils.getInputLocales();
		this.brand = propertyUtils.getBrand();
		this.reportMode = propertyUtils.getReportMode();
		this.sids = sids;
	}

	public XMLReader(PropertyUtils propertyUtils) {
		super();
		this.outputFile = propertyUtils.getOutputFile();
		this.inputFile = propertyUtils.getInputFile();
		this.resource = propertyUtils.getResource();
		this.rootDir = propertyUtils.getRootDir();
		this.inputLocales = propertyUtils.getInputLocales();
		this.brand = propertyUtils.getBrand();
		this.reportMode = propertyUtils.getReportMode();
		this.sids = new HashMap<String, List<ModuleForSID>>();
	}

	private void writeHeaderLine(Writer writer, List<TEXT> texte, List<String> importLocales) throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Module" + SPR + "Dir" + SPR + "File" + SPR + "Key" + SPR + "xy_XY");
		TEXT text = texte.get(0);
		List<SPRACHVARIANTE> sprachvarianten = text.getSPRACHVARIANTE();
		for (SPRACHVARIANTE sprachvariante : sprachvarianten) {
			String locale = PropertyUtils.getXMLLocale(sprachvariante, importLocales, brand);
			if (!StringUtils.isEmpty(locale)) {
				buffer.append(SPR);
				buffer.append(locale);
			}
		}
		buffer.append(LF);
		writer.write(buffer.toString());
	}

	public void importPropertiesFromXML() throws IOException, JAXBException, SAXNotRecognizedException,
			SAXNotSupportedException, ParserConfigurationException, SAXException {
		BufferedWriter writer = PropertyUtils.openReportFile(outputFile);
		List<String> propertyFiles = new ArrayList<String>();
		List<String> importLocales = new ArrayList<String>();
		for (String locale : inputLocales) {
			importLocales.add(locale);
		}

		// unmarshal from inputFile
		final FAHRZEUGTEXTE fahrzeugTexte;
		fahrzeugTexte = PropertyUtils.unmarshalTextwerkXml(inputFile);

		// convert to PropertyForModule
		List<TEXT> texte = fahrzeugTexte.getTEXTE().getTEXT();
		writeHeaderLine(writer, texte, importLocales);
		for (TEXT text : texte) {
			List<SPRACHVARIANTE> sprachvarianten = text.getSPRACHVARIANTE();
			String master = sprachvarianten.get(0).getContent();
			ParentSID parentSid = PropertyUtils.getParentSID(text.getIDENTIFIER());
			String ident = parentSid.getSid();
			if (sids.containsKey(ident)) {
				List<ModuleForSID> sidList = sids.get(ident);
				for (ModuleForSID sid : sidList) {
					boolean updated = false;
					StringBuffer buffer = new StringBuffer();
					buffer.append(sid.getModuleName());
					buffer.append(SPR);
					buffer.append(sid.getDirName());
					buffer.append(SPR);
					buffer.append(sid.getFileName());
					buffer.append(SPR);
					buffer.append(sid.getKey());
					buffer.append(SPR);
					buffer.append(ident);
					buffer.append(SPR);
					for (SPRACHVARIANTE sprachvariante : sprachvarianten) {
						String locale = PropertyUtils.getXMLLocale(sprachvariante, importLocales, brand);
						if (!StringUtils.isEmpty(locale)) {
							String path = rootDir + sid.getDirName() + sid.getFileName() + "_" + locale + ".properties";
							File file = new File(path);
							String value = importXmlValue(parentSid, sid.getKey(), sprachvariante, file, locale, master);
							if (!"#".equals(value) && value.startsWith("#") && value.endsWith("#")) {
								if (!propertyFiles.contains(path)) {
									propertyFiles.add(path);
								}
								updated = true;
							}
							buffer.append(value.replaceAll("(\\r)?\\n", " "));
							buffer.append(SPR);
						}
					}
					buffer.append(LF);
					if (updated) {
						writer.write(buffer.toString());
					}
				}
			} else if (reportMode.equals("all") && !StringUtils.isEmpty(ident)) {
				boolean updated = false;
				StringBuffer buffer = new StringBuffer();
				buffer.append("");
				buffer.append(SPR);
				buffer.append("");
				buffer.append(SPR);
				buffer.append("");
				buffer.append(SPR);
				buffer.append("");
				buffer.append(SPR);
				buffer.append(ident);
				buffer.append(SPR);
				for (SPRACHVARIANTE sprachvariante : sprachvarianten) {
					String locale = PropertyUtils.getXMLLocale(sprachvariante, importLocales, brand);
					if (!StringUtils.isEmpty(locale)) {
						String value = getXmlValue(sprachvariante);
						if (!"#".equals(value) && value.startsWith("#") && value.endsWith("#")) {
							updated = true;
						}
						buffer.append(value.replaceAll("(\\r)?\\n", " "));
						buffer.append(SPR);
					}
				}
				buffer.append(LF);
				if (updated) {
					writer.write(buffer.toString());
				}
			}
		}
		writer.flush();
		writer.close();
		PropertyUtils.generateProperties(propertyFiles, rootDir);
	}

	private String importXmlValue(ParentSID parentSid, String key, SPRACHVARIANTE sprachvariante, File file,
			String locale, String master) throws IOException, FileNotFoundException {
		String value = PropertyUtils.getStringValue(sprachvariante.getContent(), parentSid, locale, master);
		boolean update = (value != null && !"null".equals(value));

		if (update) {
			SortedProperties sortedProperties = PropertyUtils.readSortedProperties(file);

			if (sortedProperties == null && !StringUtils.isEmpty(value)) {
				sortedProperties = new SortedProperties();
				file.createNewFile();
			}
			if (sortedProperties != null) {
				if (StringUtils.isEmpty(value)) {
					if (sortedProperties.containsKey(key)) {
						sortedProperties.remove(key);
						PropertyUtils.saveSortedProperties(sortedProperties, file);
						value = "#" + value + "#";
					}
				} else {
					String oldValue = sortedProperties.getProperty(key);
					if (oldValue != null) {
						oldValue = sortedProperties.getProperty(key).replaceAll("(\\r)?\\n", "\n");
					}
					String proValue = value.replaceAll("<br +/?>|(\\r)?\\n|\\\\n", "\n");
					if (!sortedProperties.containsKey(key) || !oldValue.equals(proValue)) {
						sortedProperties.setProperty(key, proValue);
						PropertyUtils.saveSortedProperties(sortedProperties, file);
						value = "#" + proValue + "#";
					}
				}
			}
		} else
			value = "";
		return value;
	}

	private String getXmlValue(SPRACHVARIANTE sprachvariante) {
		String value = PropertyUtils.getStringValue(sprachvariante.getContent());
		boolean update = (value != null && !"null".equals(value));
		if (update) {
			value = "#" + value + "#";
		} else
			value = "";
		return value;
	}

	public void transformXMLToProperties() throws IOException, JAXBException, SAXNotRecognizedException,
			SAXNotSupportedException, ParserConfigurationException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(Content.class);

		File propertiesFile = new File(outputFile);
		String key = null;
		SortedProperties sortedPropertiesOld = PropertyUtils.readSortedProperties(propertiesFile);
		SortedProperties sortedProperties = new SortedProperties();
		if (sortedPropertiesOld == null) {
			propertiesFile.createNewFile();
		}
		// unmarshal from inputFile
		Unmarshaller u = jc.createUnmarshaller();
		File file = new File(inputFile);
		Content content = (Content) u.unmarshal(PropertyUtils.getSAXSource(inputFile));

		// One column
		if (content.getDualColumn() == null) {

			// Content header
			key = transform(content.getTitle(), "Title", sortedProperties);
			if (key != null) {
				content.setTitle(key);
			}
			key = transform(content.getHeading(), "Heading", sortedProperties);
			if (key != null) {
				content.setHeading(key);
			}
			key = transform(content.getP(), "P", sortedProperties);
			if (key != null) {
				content.setP(key);
			}
			if (content.getImage() != null) {
				Content.Image image = content.getImage();
				key = transform(image.getTitle(), "Image.Title", sortedProperties);
				if (key != null) {
					image.setTitle(key);
				}
				key = transform(image.getAlt(), "Image.Alt", sortedProperties);
				if (key != null) {
					image.setAlt(key);
				}
			}

			// Section1
			if (content.getSection1() != null) {
				// Section1 Header
				Content.Section1 section1 = content.getSection1();
				key = transform(section1.getHeading(), "Section1.Heading", sortedProperties);
				if (key != null) {
					section1.setHeading(key);
				}
				key = transform(section1.getP(), "Section1.P", sortedProperties);
				if (key != null) {
					section1.setP(key);
				}
			}
			// Sections
			if (content.getSection() != null) {
				List<Content.Section> sections = content.getSection();
				for (int i = 0; i < sections.size(); i++) {
					// Section header
					Content.Section section = sections.get(i);
					key = transform(section.getHeading(), "Section." + (i + 1) + ".Heading", sortedProperties);
					if (key != null) {
						section.setHeading(key);
					}
					if (section.getP() != null) {
						List<String> ps = section.getP();
						for (int j = 0; j < ps.size(); j++) {
							key = transform(ps.get(j), "Section." + (i + 1) + ".P." + (j + 1), sortedProperties);
							if (key != null) {
								ps.set(j, key);
							}
						}
						sections.set(i, section);
					}
					if (section.getText() != null) {
						List<String> texts = section.getText();
						for (int j = 0; j < texts.size(); j++) {
							key = transform(texts.get(j), "Section." + (i + 1) + ".Text." + (j + 1), sortedProperties);
							if (key != null) {
								texts.set(j, key);
							}
						}
						sections.set(i, section);
					}
					if (section.getLink() != null) {
						Content.Section.Link link = section.getLink();
						key = transform(link.getLabel(), "Section." + (i + 1) + ".Link.Label", sortedProperties);
						if (key != null) {
							link.setLabel(key);
						}
						key = transform(link.getInfo(), "Section." + (i + 1) + ".Link.Info", sortedProperties);
						if (key != null) {
							link.setInfo(key);
						}
						key = transform(link.getUrl(), "Section." + (i + 1) + ".Link.Url", sortedProperties);
						if (key != null) {
							link.setUrl(key);
						}
						key = transform(link.getImgLabel(), "Section." + (i + 1) + ".Link.ImgLabel", sortedProperties);
						if (key != null) {
							link.setImgLabel(key);
						}
						sections.set(i, section);
					}
				}
			}

			// Teasers
			if (content.getTeasers() != null) {
				Content.Teasers teasersHead = content.getTeasers();
				key = transform(teasersHead.getHeading(), "Teasers.Heading", sortedProperties);
				if (key != null) {
					teasersHead.setHeading(key);
				}
				if (teasersHead.getTeaser() != null) {
					List<Content.Teasers.Teaser> teasers = teasersHead.getTeaser();
					for (int i = 0; i < teasers.size(); i++) {
						// Section header
						Content.Teasers.Teaser teaser = teasers.get(i);
						Content.Teasers.Teaser.Link link = teaser.getLink();
						key = transform(link.getHref(), "Teaser." + (i + 1) + ".Link.Href", sortedProperties);
						if (key != null) {
							link.setHref(key);
						}
						key = transform(link.getText(), "Teaser." + (i + 1) + ".Link.Text", sortedProperties);
						if (key != null) {
							link.setText(key);
						}
						teaser.setLink(link);
						key = transform(teaser.getText(), "Teaser." + (i + 1) + ".Text", sortedProperties);
						if (key != null) {
							teaser.setText(key);
						}
						teasers.set(i, teaser);
					}
				}
			}

			// Dual column
		} else {
			Content.DualColumn dualColumn = content.getDualColumn();

			// DualColumn header
			key = transform(dualColumn.getTitle(), "DualColumn.Title", sortedProperties);
			if (key != null) {
				dualColumn.setTitle(key);
			}
			key = transform(dualColumn.getHeading(), "DualColumn.Heading", sortedProperties);
			if (key != null) {
				dualColumn.setHeading(key);
			}
			key = transform(dualColumn.getP(), "DualColumn.P", sortedProperties);
			if (key != null) {
				dualColumn.setP(key);
			}

			// Section1s
			if (dualColumn.getSection1() != null) {
				List<Content.DualColumn.Section1> section1s = dualColumn.getSection1();
				for (int i = 0; i < section1s.size(); i++) {
					// Section header
					Content.DualColumn.Section1 section1 = section1s.get(i);
					key = transform(section1.getHeading(), "DualColumn.Section1." + (i + 1) + ".Heading",
							sortedProperties);
					if (key != null) {
						section1.setHeading(key);
					}
					key = transform(section1.getP(), "DualColumn.Section1." + (i + 1) + ".P", sortedProperties);
					if (key != null) {
						section1.setP(key);
					}
				}
			}

			// Section2s
			if (dualColumn.getSection2() != null) {
				List<Content.DualColumn.Section2> section2s = dualColumn.getSection2();
				for (int i = 0; i < section2s.size(); i++) {
					// Section header
					Content.DualColumn.Section2 section2 = section2s.get(i);
					key = transform(section2.getHeading(), "DualColumn.Section2." + (i + 1) + ".Heading",
							sortedProperties);
					if (key != null) {
						section2.setHeading(key);
					}
					key = transform(section2.getP(), "DualColumn.Section2." + (i + 1) + ".P", sortedProperties);
					if (key != null) {
						section2.setP(key);
					}
				}
			}

			// DualColumn link
			if (dualColumn.getLink() != null) {
				Content.DualColumn.Link link = dualColumn.getLink();
				key = transform(link.getLabel(), "DualColumn.Link.Label", sortedProperties);
				if (key != null) {
					link.setLabel(key);
				}
				key = transform(link.getImgLabel(), "DualColumn.Link.ImgLabel", sortedProperties);
				if (key != null) {
					link.setImgLabel(key);
				}
				key = transform(link.getValue(), "DualColumn.Link.Value", sortedProperties);
				if (key != null) {
					link.setValue(key);
				}
			}
		}
		content.setResource(resource);
		// marshal to inputFile with Labels
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));

		m.marshal(content, writer);
		writer.flush();
		writer.close();
		if (sortedPropertiesOld != null) {
			Enumeration e = sortedPropertiesOld.propertyNames();
			while (e.hasMoreElements()) {
				String pkey = (String) e.nextElement();
				if (sortedProperties.containsKey(pkey) && "".equals(sortedProperties.getProperty(pkey))) {
					sortedProperties.setProperty(pkey, sortedPropertiesOld.getProperty(pkey));
				}
			}
		}
		PropertyUtils.saveSortedProperties(sortedProperties, propertiesFile);
	}

	private String transform(String value, String extend, SortedProperties sortedProperties) {
		String result = null;
		if (value != null && value.indexOf(LABEL) == -1) {
			String key = LABEL + extend;
			value = value.replaceAll("\\t", "");
			value = value.trim();
			value = value.replaceAll("  *", " ");
			value = value.replaceAll("\\n ", "\n");
			value = value.replaceAll(" \\n", "\n");
			value = value.trim();
			if (!value.equals("")) {
				sortedProperties.setProperty(key, value);
				result = key;
			}
		} else if (value != null) {
			String key = value.trim();
			sortedProperties.setProperty(key, "");
		}
		return result;
	}
}
