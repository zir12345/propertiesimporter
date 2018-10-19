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




public class ModuleForSID
{

   private String moduleName;
   private String key;
   private String dirName;
   private String fileName;


   public ModuleForSID(String moduleName, String key, String dirName, String fileName)
   {
      super();
      this.moduleName = moduleName;
      this.key = key;
      this.dirName = dirName;
      this.fileName = fileName;
   }


   /**
    * @return the moduleName
    */
   public String getModuleName()
   {
      return moduleName;
   }


   /**
    * @return the key
    */
   public String getKey()
   {
      return key;
   }


   /**
    * @return the dirName
    */
   public String getDirName()
   {
      return dirName;
   }


   /**
    * @return the fileName
    */
   public String getFileName()
   {
      return fileName;
   }


   /**
    * @param moduleName
    *        the moduleName to set
    */
   public void setModuleName(String moduleName)
   {
      this.moduleName = moduleName;
   }


   /**
    * @param key
    *        the key to set
    */
   public void setKey(String key)
   {
      this.key = key;
   }


   /**
    * @param dirName
    *        the dirName to set
    */
   public void setDirName(String dirName)
   {
      this.dirName = dirName;
   }


   /**
    * @param fileName
    *        the fileName to set
    */
   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }

}
