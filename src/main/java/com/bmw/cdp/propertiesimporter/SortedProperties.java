/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.16.0-SNAPSHOT
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


import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;


public class SortedProperties
   extends Properties
{
   /**
    * Overrides, called by the store method.
    */
   @Override
   @SuppressWarnings("unchecked")
   public synchronized Enumeration keys()
   {
      Enumeration keysEnum = super.keys();
      Vector keyList = new Vector();
      while (keysEnum.hasMoreElements()) {
         keyList.add(keysEnum.nextElement());
      }
      Collections.sort(keyList);
      return keyList.elements();
   }

}
