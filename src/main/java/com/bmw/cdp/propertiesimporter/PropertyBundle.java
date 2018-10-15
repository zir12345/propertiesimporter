/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.24.0-SNAPSHOT
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


import java.util.Locale;

import com.bmw.cdp.propertiesimporter.bundle.Bundle;
import com.bmw.cdp.propertiesimporter.bundle.BundleEntry;


public class PropertyBundle
   extends Bundle
{

   public void addBundleEntry(BundleEntry entry, Locale locale)
   {
      super.addEntry(entry);
      super.setLocale(locale);
   }

}
