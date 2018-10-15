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


import java.util.List;


public class PropertiesForFile
{

   private List<String> resourceFiles;
   private List<String> xslFiles;
   private String       fileType;
   private List<String> keys;


   public PropertiesForFile(List<String> resourceFiles,
                            List<String> xslFiles,
                            String fileType,
                            List<String> keys)
   {
      super();
      this.resourceFiles = resourceFiles;
      this.xslFiles = xslFiles;
      this.fileType = fileType;
      this.keys = keys;
   }


   public List<String> getResourceFiles()
   {
      return resourceFiles;
   }


   public void setResourceFiles(List<String> resourceFiles)
   {
      this.resourceFiles = resourceFiles;
   }


   public List<String> getXslFiles()
   {
      return xslFiles;
   }


   public void setXslFiles(List<String> xslFiles)
   {
      this.xslFiles = xslFiles;
   }


   public String getFileType()
   {
      return fileType;
   }


   public void setFileType(String fileType)
   {
      this.fileType = fileType;
   }


   public List<String> getKeys()
   {
      return keys;
   }


   public void setKeys(List<String> keys)
   {
      this.keys = keys;
   }

}
