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
package com.bmw.cdp.propertiesimporter;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;


public class PropertyForModule
{

   private static Pattern                             PATTERN_COOL = Pattern.compile("portal_bmwlive_.*war");
   private static Pattern                             PATTERN_NBT  = Pattern.compile("portal_nbt_.*war");
   private static Pattern                             PATTERN_BON  = Pattern.compile("portal_.*war");
   private static Pattern                             PATTERN_GEN  = Pattern.compile("test_.*war");
   private static Pattern                             PATTERN_ALL  = Pattern.compile(".*src");
   private static Pattern                             PATTERN_MVN  = Pattern.compile(".*i18n");
   private final static String                        UNKNOWN      = "unknown";
   private final static String                        MAIN         = "main";
   private final static String                        COOL         = "cool_";
   private final static String                        NBT          = "nbt_";

   private final String                               moduleName;
   private final List<File>                           missingFiles;
   private final List<File>                           propertyFiles;
   private final Map<String, Map<String, Properties>> properties;
   private final Map<String, Properties>              propertiesGlobal;


   public PropertyForModule(String moduleName)
   {
      super();
      this.moduleName = moduleName;
      propertyFiles = new ArrayList<File>();
      missingFiles = new ArrayList<File>();
      properties = new HashMap<String, Map<String, Properties>>();
      propertiesGlobal = new HashMap<String, Properties>();
   }


   public void addFile(File file)
   {
      propertyFiles.add(file);
   }


   public List<File> getPropertyFiles()
   {
      return propertyFiles;
   }


   public void addMissingFile(File file)
   {
      missingFiles.add(file);
   }


   public List<File> getMissingFiles()
   {
      return missingFiles;
   }


   public String getModuleName()
   {
      return moduleName;
   }


   public Map<String, Properties> addToMap(String baseName)
   {
      Map<String, Properties> propertyMap = new HashMap<String, Properties>();
      this.properties.put(baseName, propertyMap);
      return propertyMap;
   }


   public Map<String, Map<String, Properties>> getProperties()
   {
      return properties;
   }


   public Map<String, Properties> getPropertiesGlobal()
   {
      return propertiesGlobal;
   }


   public void readingExistingProperty(PropertyUtils propertyUtils)
      throws IOException
   {
      for (File f : this.propertyFiles) {
         for (String outputLocale : propertyUtils.getOutputLocales()) {
            String locale = PropertyUtils.getInputLocale(f, propertyUtils.getRootDir());
            String name = f.getName().replace(locale, outputLocale);
            String dir = f.getAbsolutePath().replace(f.getName(), "");
            File file = new File(dir + "/" + name);
            Properties properties = PropertyUtils.readProperties(file);
            if (properties != null) {
               String baseFileName = PropertyUtils.getBaseFileName(file,
                                                                   propertyUtils.getRootDir());
               Map<String, Properties> localeMap = this.properties.get(outputLocale);
               if (localeMap == null) {
                  localeMap = new HashMap<String, Properties>();
                  this.properties.put(outputLocale, localeMap);

               }
               localeMap.put(baseFileName, properties);
               for (String inputLocale : propertyUtils.getInputLocales()) {
                  if (inputLocale.equals(outputLocale)) {
                     Properties propertiesTmp = this.propertiesGlobal.get(baseFileName);
                     if (propertiesTmp == null) {
                        propertiesTmp = new Properties();
                     }
                     propertiesTmp.putAll(properties);
                     this.propertiesGlobal.put(baseFileName, propertiesTmp);
                  }
               }
            }
            else {
               addMissingFile(file);
            }
         }
      }

   }


   public void removePropertiesWithSID(PropertyUtils propertyUtils)
      throws IOException
   {
      for (File propertyFile : this.getPropertyFiles()) {
         String basefileName = PropertyUtils.getBaseFileName(propertyFile,
                                                             propertyUtils.getRootDir());
         Properties properties = this.propertiesGlobal.get(basefileName);
         if (properties.isEmpty()) {
            continue;
         }
         List<String> keys = new ArrayList<String>();
         for (Object key : properties.keySet()) {
            keys.add(key.toString());
         }
         Collections.sort(keys);
         for (String key : keys) {
            String value = properties.getProperty(key);
            boolean hasSID = false;
            for (String outputLocale : propertyUtils.getOutputLocales()) {
               Map<String, Properties> localeMap = this.getProperties().get(outputLocale);
               if (localeMap != null && localeMap.containsKey(basefileName)) {
                  if (PropertyUtils.SID_LOCALE.equals(outputLocale)
                      && StringUtils.isNotEmpty(value)) {
                     if (localeMap.get(basefileName).containsKey(key)) {
                        hasSID = true;
                        localeMap.get(basefileName).remove(key);
                     }
                  }
                  else if (hasSID) {
                     localeMap.get(basefileName).remove(key);
                  }
               }
            }
         }
      }
   }


   public void insertSIDs(PropertyUtils propertyUtils, Map<String, List<ModuleForSID>> sids)
      throws IOException
   {
      for (File propertyFile : this.getPropertyFiles()) {
         String basefileName = PropertyUtils.getBaseFileName(propertyFile,
                                                             propertyUtils.getRootDir());
         Properties properties = this.propertiesGlobal.get(basefileName);
         if (properties.isEmpty()) {
            continue;
         }
         List<String> keys = new ArrayList<String>();
         for (Object key : properties.keySet()) {
            keys.add(key.toString());
         }
         Collections.sort(keys);
         for (String key : keys) {
            String value = properties.getProperty(key);
            String outputLocale = PropertyUtils.SID_LOCALE;
            Map<String, Properties> localeMap = this.getProperties().get(outputLocale);
            if (localeMap != null && localeMap.containsKey(basefileName)) {
               if (StringUtils.isNotEmpty(value)) {
                  if (localeMap.get(basefileName).containsKey(key)) {
                     String dirName = PropertyUtils.getDirName(basefileName);
                     String fileName = PropertyUtils.getFileName(basefileName);
                     String sidKey = localeMap.get(basefileName).getProperty(key).trim().replaceAll("(\\r)?\\n",
                                                                                                    "");
                     ModuleForSID sid = new ModuleForSID(moduleName, key, dirName, fileName);
                     List<ModuleForSID> sidList = new ArrayList<ModuleForSID>();
                     if (sids.containsKey(sidKey)) {
                        sidList = sids.get(sidKey);
                     }
                     sidList.add(sid);
                     sids.put(sidKey, sidList);
                  }
               }
            }
         }
      }
   }


   public void insertSIDsExcel(PropertyUtils propertyUtils, Map<String, List<ModuleForSID>> sids)
      throws IOException
   {
      for (File propertyFile : this.getPropertyFiles()) {
         String basefileName = PropertyUtils.getBaseFileName(propertyFile,
                                                             propertyUtils.getRootDir());
         Properties properties = this.propertiesGlobal.get(basefileName);
         if (properties.isEmpty()) {
            continue;
         }
         List<String> keys = new ArrayList<String>();
         for (Object key : properties.keySet()) {
            keys.add(key.toString());
         }
         Collections.sort(keys);
         for (String key : keys) {
            String value = properties.getProperty(key);
            String outputLocale = PropertyUtils.SID_LOCALE;
            Map<String, Properties> localeMap = this.getProperties().get(outputLocale);
            if (localeMap != null && localeMap.containsKey(basefileName)) {
               if (StringUtils.isNotEmpty(value)) {
                  if (localeMap.get(basefileName).containsKey(key)) {
                     String dirName = PropertyUtils.getDirName(basefileName);
                     String fileName = PropertyUtils.getFileName(basefileName);
                     String sidKey = localeMap.get(basefileName).getProperty(key).trim().replaceAll("(\\r)?\\n",
                                                                                                    "");
                     ModuleForSID sid = new ModuleForSID(moduleName, key, dirName, fileName);
                     List<ModuleForSID> sidList = new ArrayList<ModuleForSID>();
                     if (sids.containsKey(sidKey)) {
                        sidList = sids.get(sidKey);
                     }
                     sidList.add(sid);
                     sids.put(sidKey, sidList);
                  }
               }
            }
         }
      }
   }


   public static String getModuleName(File file)
      throws IOException
   {
      Matcher match = PATTERN_COOL.matcher(file.getCanonicalPath());
      Matcher match2 = PATTERN_NBT.matcher(file.getCanonicalPath());
      Matcher match3 = PATTERN_BON.matcher(file.getCanonicalPath());
      Matcher match4 = PATTERN_GEN.matcher(file.getCanonicalPath());
      Matcher match5 = PATTERN_ALL.matcher(file.getCanonicalPath());
      Matcher match6 = PATTERN_MVN.matcher(file.getCanonicalPath());

      if (match.find()) {
         if (match.start() + 15 >= match.end() - 4) {
            return COOL + MAIN;
         }
         String moduleName = file.getCanonicalPath().substring(match.start() + 15,
                                                               match.end() - 4);
         return COOL + moduleName;
      }
      else if (match2.find()) {
         String moduleName = file.getCanonicalPath().substring(match2.start() + 11,
                                                               match2.end() - 4);
         return NBT + moduleName;
      }
      else if (match3.find()) {
         String moduleName = file.getCanonicalPath().substring(match3.start() + 7,
                                                               match3.end() - 4);
         return moduleName;
      }
      else if (match4.find()) {
         String moduleName = file.getCanonicalPath().substring(match4.start() + 5,
                                                               match4.end() - 4);
         return moduleName;
      }
      else if (match5.find()) {
         String moduleName = file.getCanonicalPath().substring(match5.start(), match5.end() - 4);
         int idx = moduleName.lastIndexOf("\\");
         return moduleName.substring(idx + 1);
      }
      else if (match6.find()) {
         String moduleName = file.getCanonicalPath().substring(match6.start(), match6.end() - 5);
         int idx = moduleName.lastIndexOf("\\");
         return moduleName.substring(idx + 1);
      }
      else {
         System.out.println("Unknown Module");
         return UNKNOWN;
      }

   }


   public static Map<String, PropertyForModule> getModules(PropertyUtils propertyUtils)
      throws IOException, XPathExpressionException, ParserConfigurationException, SAXException
   {
      PropertyFileFinder propertyFinder = new PropertyFileFinder(
                                                                 propertyUtils.getInputLocales());
      propertyFinder.findProperties(propertyUtils.getRootDir());
      Map<String, PropertyForModule> modules = new HashMap<String, PropertyForModule>();
      for (File f : propertyFinder.getPropertyFiles()) {
         String moduleName = PropertyForModule.getModuleName(f);
         PropertyForModule propertyModule = modules.get(moduleName);
         if (propertyModule == null) {
            propertyModule = new PropertyForModule(moduleName);
            modules.put(moduleName, propertyModule);
         }
         propertyModule.addFile(f);
      }
      for (PropertyForModule module : modules.values()) {
         module.readingExistingProperty(propertyUtils);
      }
      return modules;
   }
}
