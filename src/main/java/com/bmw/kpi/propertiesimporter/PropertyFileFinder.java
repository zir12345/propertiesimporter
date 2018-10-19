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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PropertyFileFinder
{

   private final Pattern    pattern;
   private final List<File> propertyFiles;


   PropertyFileFinder(String[] inputLocales)
   {
      super();
      String regexp = ".*(";
      for (String inputLocale : inputLocales) {
         regexp += inputLocale + "|";
      }
      regexp = regexp.substring(0, regexp.length() - 1) + ").properties";
      pattern = Pattern.compile(regexp);
      propertyFiles = new ArrayList<File>();
   }


   public List<File> getPropertyFiles()
   {
      return propertyFiles;
   }


   public void findProperties(String start)
      throws IOException
   {
      File startDir = new File(start);
      String[] fileNames = startDir.list();
      String lastFilePat = "";
      if (fileNames != null) {
         for (String fileName : fileNames) {
            File f = new File(startDir.getPath(), fileName);
            if ("target".equals(fileName) || ".svn".equals(fileName)) {
               continue;
            }
            if (f.isDirectory()) {
               findProperties(f.getPath());
            }
            Matcher match = pattern.matcher(fileName);
            if (match.matches()) {
               int idx = fileName.indexOf("_");
               String filePat = fileName.substring(0, idx);
               if (!filePat.equals(lastFilePat)) {
                  propertyFiles.add(new File(startDir.getPath(), fileName));
                  lastFilePat = filePat;
               }
            }
         }
      }
   }
}
