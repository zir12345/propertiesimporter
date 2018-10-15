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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PropertiesReferencesFinder
{

   private final static Pattern           PATTERN_RESOURCE_FILE1    = Pattern.compile(".*RESOURCE_FILE.*=.*\".*\";");
   private final static Pattern           PATTERN_RESOURCE_FILE2    = Pattern.compile(".*String getResourceFile\\(\\)");
   private final static Pattern           PATTERN_RESOURCE_FILE3    = Pattern.compile("<param-name>resourceFile</param-name>");
   private final static Pattern           PATTERN_COMMON_FILE       = Pattern.compile(".*COMMON_FILE\\).*;");
   private final static Pattern           PATTERN_RESOURCE_MENU     = Pattern.compile(".*RESOURCE_MENU.*=.*\".*\";");
   private final static Pattern           PATTERN_XSL_FILE1         = Pattern.compile(".*XSL_FILE.*=.*\".*\";");
   private final static Pattern           PATTERN_XSL_FILE2         = Pattern.compile(".*String getXslFile\\(\\)");
   private final static Pattern           PATTERN_XSL_FILE3         = Pattern.compile("<xsl:include href=\"\\.\\./[^\\.]*\\.xsl\" />");
   private final static Pattern           PATTERN_XSL_FILE4         = Pattern.compile("<param-name>xslFile</param-name>");
   private final static Pattern           PATTERN_KEY_JAVA1         = Pattern.compile("\\.getString\\(\"[^\"]*\"");
   private final static Pattern           PATTERN_KEY_JAVA2         = Pattern.compile("ERRORS[^=]*= new HashMap<Integer, String>\\(\\) \\{");
   private final static Pattern           PATTERN_KEY_JAVA3         = Pattern.compile("LIST\\(\"[^\"]*\"");                                   ;
   private final static Pattern           PATTERN_KEY_XSL1          = Pattern.compile("key\\('[^']*' *, *'[^']*'");
   private final static Pattern           PATTERN_KEY_XSL2          = Pattern.compile("/(link|label)\\[@id='[^']*'");
   private final static Pattern           PATTERN_KEY_XML1          = Pattern.compile("CDATA\\[ *Label\\.Text\\.[^]]*\\]");
   private final static Pattern           PATTERN_KEY_XML2          = Pattern.compile("> *Label\\.Text\\.[^<]*<");
   private final static Pattern           PATTERN_VALUE_JAVA        = Pattern.compile("\"[^\"]*\"");
   private final static Pattern           PATTERN_VALUE_XSL1        = Pattern.compile("'[^']*' *, *'[^']*'");
   private final static Pattern           PATTERN_VALUE_XSL2        = Pattern.compile("/.*\\[@id='[^']*'");
   private final static Pattern           PATTERN_VALUE_XSL3        = Pattern.compile("\"\\.\\./[^\\.]*\\.xsl\"");
   private final static Pattern           PATTERN_VALUE_XML1        = Pattern.compile("\\[[^]]*\\]");
   private final static Pattern           PATTERN_VALUE_XML2        = Pattern.compile(">[^<]*<");
   private final static Pattern           PATTERN_VALUE_XML3        = Pattern.compile("<param-value>[^<]*</param-value>");
   private final static Pattern           PATTERN_RESOURCE_FILE_XML = Pattern.compile("<resource>[^<]*</resource>");
   private final static Pattern           PATTERN_RESOURCE_MENU_XML = Pattern.compile("<menuComposition>");
   private final static String            ERRORSEND                 = "};";
   private final static String            MESSAGETEXT               = "Message.Text.";

   private final Pattern                  pattern;
   private Map<String, PropertiesForFile> propertiesReferences;
   private Map<String, String>            xslWebResource;
   private final String[]                 pathMap;


   PropertiesReferencesFinder(String module)
   {
      super();
      String regexp = ".*(.xsl|.xml|.java)";
      pattern = Pattern.compile(regexp);
      propertiesReferences = new HashMap<String, PropertiesForFile>();
      xslWebResource = new HashMap<String, String>();
      pathMap = PropertyUtils.MODULES_PATH_MAP.get(module);
   }


   public Map<String, PropertiesForFile> getPropertiesReferences()
   {
      return propertiesReferences;
   }


   public Map<String, String> getXslWebResource()
   {
      return xslWebResource;
   }


   public void findReferencedPropertyFiles(String start)
      throws IOException
   {
      File startDir = new File(start);
      String[] fileNames = startDir.list();
      if (fileNames != null) {
         for (String fileName : fileNames) {
            File f = new File(startDir.getPath(), fileName);
            if ("target".equals(fileName) || ".svn".equals(fileName)
                || ".settings".equals(fileName)) {
               continue;
            }
            if (f.isDirectory()) {
               findReferencedPropertyFiles(f.getPath());
            }
            Matcher match = pattern.matcher(fileName);
            if (match.matches()) {
               PropertiesForFile propertiesReference = searchPropertiesReference(f);
               if (propertiesReference != null) {
                  propertiesReferences.put(f.getAbsolutePath(), propertiesReference);
               }
            }

         }
      }
   }


   public PropertiesForFile searchPropertiesReference(File file)
      throws IOException
   {
      PropertiesForFile result = null;
      List<String> resourceFiles = new ArrayList<String>();
      List<String> xslFiles = new ArrayList<String>();
      List<String> keys = new ArrayList<String>();
      String fileType = null;
      Scanner fileScanner = new Scanner(file);

      if (file.getName().endsWith(PropertyUtils.JAVA)) {
         fileType = PropertyUtils.JAVA;
         while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (matcherFindJava1(resourceFiles, PATTERN_RESOURCE_FILE1.matcher(line))) {
            }
            else if (PATTERN_RESOURCE_FILE2.matcher(line).find()) {
               line = fileScanner.nextLine();
               line = fileScanner.nextLine();
               lineFind(resourceFiles, line);
            }

            else if (matcherFindJavaXsl(xslFiles, PATTERN_XSL_FILE1.matcher(line))) {
            }
            else if (PATTERN_XSL_FILE2.matcher(line).find()) {
               line = fileScanner.nextLine();
               line = fileScanner.nextLine();
               lineFindXsl(xslFiles, line);
            }
            else if (PATTERN_COMMON_FILE.matcher(line).find()) {
               if (!resourceFiles.contains(pathMap[0])) {
                  resourceFiles.add(pathMap[0]);
               }
            }
            else if (PATTERN_RESOURCE_MENU.matcher(line).find()) {
               if (!resourceFiles.contains(pathMap[1])) {
                  resourceFiles.add(pathMap[1]);
               }
            }
            else if (matcherFindJava1(keys, PATTERN_KEY_JAVA1.matcher(line))) {
            }
            else if (matcherFindJava2(keys, PATTERN_KEY_JAVA3.matcher(line))) {
            }
            else if (PATTERN_KEY_JAVA2.matcher(line).find()) {
               line = fileScanner.nextLine();
               while (!ERRORSEND.equals(line.trim())) {
                  lineFindErrors(keys, line);
                  line = fileScanner.nextLine();
               }
            }
         }
      }
      else if (file.getName().endsWith(PropertyUtils.XSL)) {
         fileType = PropertyUtils.XSL;
         if (file.getAbsolutePath().contains(pathMap[5])) {
            resourceFiles.add(pathMap[0]);
         }

         while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (matcherFindXsl1(keys, PATTERN_KEY_XSL1.matcher(line))) {
            }
            else if (matcherFindXsl2(keys, PATTERN_KEY_XSL2.matcher(line))) {
            }
            else if (matcherFindXsl3(xslFiles, PATTERN_XSL_FILE3.matcher(line))) {
            }
         }
      }
      else if (file.getName().endsWith(PropertyUtils.XML)) {
         fileType = PropertyUtils.XML;
         while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (matcherFindXml2(resourceFiles, PATTERN_RESOURCE_FILE_XML.matcher(line))) {
            }
            else if (PATTERN_RESOURCE_MENU_XML.matcher(line).find()) {
               if (!resourceFiles.contains(pathMap[1])) {
                  resourceFiles.add(pathMap[1]);
               }
            }
            else if (matcherFindXml1(keys, PATTERN_KEY_XML1.matcher(line))) {
            }
            else if (matcherFindXml2(keys, PATTERN_KEY_XML2.matcher(line))) {
            }
            else if (PATTERN_XSL_FILE4.matcher(line).find()) {
               line = fileScanner.nextLine();
               Matcher matcher1 = PATTERN_VALUE_XML3.matcher(line);
               if (matcher1.find()) {
                  String xslFile = matcher1.group(0).substring(13,
                                                               matcher1.group(0).length() - 14).trim();
                  line = fileScanner.nextLine();
                  line = fileScanner.nextLine();
                  line = fileScanner.nextLine();
                  Matcher matcher2 = PATTERN_RESOURCE_FILE3.matcher(line);
                  if (matcher2.find()) {
                     line = fileScanner.nextLine();
                     Matcher matcher3 = PATTERN_VALUE_XML3.matcher(line);
                     if (matcher3.find()) {
                        line = fileScanner.nextLine();
                        String resourceFile = matcher3.group(0).substring(13,
                                                                          matcher3.group(0).length() - 14).trim();
                        if (!xslWebResource.containsKey(xslFile)) {
                           xslWebResource.put(xslFile, resourceFile);
                        }
                     }
                  }
               }
            }
         }
      }
      result = new PropertiesForFile(resourceFiles, xslFiles, fileType, keys);
      return result;
   }


   private void lineFind(List<String> list, String line)
   {
      Matcher matcher1 = PATTERN_VALUE_JAVA.matcher(line);
      if (matcher1.find()) {
         String value = matcher1.group(0).substring(1, matcher1.group(0).length() - 1).trim();
         if (!list.contains(value)) {
            list.add(value);
         }
      }
   }


   private void lineFindXsl(List<String> list, String line)
   {
      Matcher matcher1 = PATTERN_VALUE_JAVA.matcher(line);
      if (matcher1.find()) {
         String value = matcher1.group(0).substring(1, matcher1.group(0).length() - 1).trim();
         if (!list.contains(value)) {
            list.add(value);
            list.add(value + pathMap[3]);
         }
      }
   }


   private void lineFindErrors(List<String> list, String line)
   {
      Matcher matcher1 = PATTERN_VALUE_JAVA.matcher(line);
      if (matcher1.find()) {
         String value = MESSAGETEXT
                        + matcher1.group(0).substring(1, matcher1.group(0).length() - 1).trim();
         if (!list.contains(value)) {
            list.add(value);
         }
      }
   }


   private boolean matcherFindJava1(List<String> list, Matcher matcher)
   {
      boolean find = false;
      while (matcher.find()) {
         String matchStr = matcher.group(0);
         Matcher matcher1 = PATTERN_VALUE_JAVA.matcher(matchStr);
         if (matcher1.find()) {
            String value = matcher1.group(0).substring(1, matcher1.group(0).length() - 1).trim();
            if (!list.contains(value)) {
               list.add(value);
            }
            find = true;
         }
      }
      return find;
   }


   private boolean matcherFindJava2(List<String> list, Matcher matcher)
   {
      boolean find = false;
      while (matcher.find()) {
         String matchStr = matcher.group(0);
         Matcher matcher1 = PATTERN_VALUE_JAVA.matcher(matchStr);
         if (matcher1.find()) {
            String value = matcher1.group(0).substring(1, matcher1.group(0).length() - 1).trim();
            if (!list.contains(value)) {
               list.add(value);
            }
            find = true;
         }
      }
      return find;
   }


   private boolean matcherFindJavaXsl(List<String> list, Matcher matcher)
   {
      boolean find = false;
      while (matcher.find()) {
         String matchStr = matcher.group(0);
         Matcher matcher1 = PATTERN_VALUE_JAVA.matcher(matchStr);
         if (matcher1.find()) {
            String value = matcher1.group(0).substring(1, matcher1.group(0).length() - 1).trim();
            if (!list.contains(value)) {
               list.add(value);
               list.add(value + pathMap[3]);
            }
            find = true;
         }
      }
      return find;
   }


   private boolean matcherFindXsl1(List<String> list, Matcher matcher)
   {
      boolean find = false;
      while (matcher.find()) {
         String matchStr = matcher.group(0);
         Matcher matcher1 = PATTERN_VALUE_XSL1.matcher(matchStr);
         if (matcher1.find()) {
            String value = matcher1.group(0).substring(1, matcher1.group(0).length() - 1).replaceAll("' *, *'",
                                                                                                     ".Text.").trim();
            if (!list.contains(value)) {
               list.add(value);
            }
            find = true;
         }
      }
      return find;
   }


   private boolean matcherFindXsl2(List<String> list, Matcher matcher)
   {
      boolean find = false;
      while (matcher.find()) {
         String matchStr = matcher.group(0);
         Matcher matcher1 = PATTERN_VALUE_XSL2.matcher(matchStr);
         if (matcher1.find()) {
            String value = matcher1.group(0).substring(1, matcher1.group(0).length() - 1).replaceAll("\\[@id= *'",
                                                                                                     ".Text.").trim();
            if (!list.contains(value)) {
               list.add(value);
               if (matchStr.contains("/link")) {
                  list.add(value.replace(".Text.", ".URL."));
                  list.add(value.replace(".Text.", ".Target."));
               }
            }
            find = true;
         }
      }
      return find;
   }


   private boolean matcherFindXsl3(List<String> list, Matcher matcher)
   {
      boolean find = false;
      while (matcher.find()) {
         String matchStr = matcher.group(0);
         Matcher matcher1 = PATTERN_VALUE_XSL3.matcher(matchStr);
         if (matcher1.find()) {
            String value = matcher1.group(0).substring(4, matcher1.group(0).length() - 5).trim();
            if (!list.contains(value)) {
               list.add(value);
            }
            find = true;
         }
      }
      return find;
   }


   private boolean matcherFindXml1(List<String> list, Matcher matcher)
   {
      boolean find = false;
      while (matcher.find()) {
         String matchStr = matcher.group(0);
         Matcher matcher1 = PATTERN_VALUE_XML1.matcher(matchStr);
         if (matcher1.find()) {
            String value = matcher1.group(0).substring(1, matcher1.group(0).length() - 1).trim();
            if (!list.contains(value)) {
               list.add(value);
            }
            find = true;
         }
      }
      return find;
   }


   private boolean matcherFindXml2(List<String> list, Matcher matcher)
   {
      boolean find = false;
      while (matcher.find()) {
         String matchStr = matcher.group(0);
         Matcher matcher1 = PATTERN_VALUE_XML2.matcher(matchStr);
         if (matcher1.find()) {
            String value = matcher1.group(0).substring(1, matcher1.group(0).length() - 1).trim();
            if (!list.contains(value)) {
               list.add(value);
            }
            find = true;
         }
      }
      return find;
   }
}
