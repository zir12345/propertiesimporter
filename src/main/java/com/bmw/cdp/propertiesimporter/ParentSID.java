/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.26.0-SNAPSHOT
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
package com.bmw.cdp.propertiesimporter;


public class ParentSID
{

   private String  sid;
   private String  subSid;
   private boolean parent;
   private String  excludeLocales;


   public ParentSID(String sid, String subSid, boolean parent, String excludeLocales)
   {
      super();
      this.sid = sid;
      this.subSid = subSid;
      this.parent = parent;
      this.excludeLocales = excludeLocales;
   }


   public String getSid()
   {
      return sid;
   }


   public void setSid(String sid)
   {
      this.sid = sid;
   }


   public String getSubSid()
   {
      return subSid;
   }


   public boolean isParent()
   {
      return parent;
   }


   public void setParent(boolean parent)
   {
      this.parent = parent;
   }


   public void setSubSid(String subSid)
   {
      this.subSid = subSid;
   }


   public String getExcludeLocales()
   {
      return excludeLocales;
   }


   public void setExcludeLocale(String excludeLocales)
   {
      this.excludeLocales = excludeLocales;
   }

}
