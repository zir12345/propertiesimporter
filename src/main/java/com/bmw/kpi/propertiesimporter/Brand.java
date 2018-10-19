/**
 * ____________________________________________________________________________
 *
 *           Project: BMW ConnectDrive Platform PersonalizationService_ejb
 *           Version: 0.1.2-SNAPSHOT
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
package com.bmw.kpi.propertiesimporter;


public enum Brand {
   BMW(1, "BM", "BMW"), //
   MINI(2, "MI", "MINI"), //
   ROLLSROYCE(3, "RR", "ROLLSROYCE"), //
   BWM_MOTORRAD(4, "BD", "BMW Motorrad"), //
   M_GMB_H(5, "MH", "M GmbH"), //
   ZINORO(6, "ZI", "ZINORO"), //
   TOYOTA(7, "D1", "TOYOTA");

   /**
    * @param id
    * @param shortcut
    * @param name
    */
   private Brand(final int id, final String shortcut, final String name)
   {
      this.id = id;
      this.shortcut = shortcut;
      this.name = name;
   }

   private int    id;
   private String name;
   private String shortcut;


   /**
    * @return id
    */
   public int getId()
   {
      return id;
   }


   /**
    * @param id
    */
   public void setId(final int id)
   {
      this.id = id;
   }


   /**
    * @return name
    */
   public String getName()
   {
      return name;
   }


   /**
    * @param name
    */
   public void setName(final String name)
   {
      this.name = name;
   }


   /**
    * @return shortcut
    */
   public String getShortcut()
   {
      return shortcut;
   }


   /**
    * @param shortcut
    */
   public void setShortcut(final String shortcut)
   {
      this.shortcut = shortcut;
   }


   /**
    * Getting the brand for the given id.
    * 
    * @param id
    * @return Brand
    */
   public static Brand valueByID(final int id)
   {
      for (final Brand brand : Brand.values()) {
         if (id == brand.getId()) {
            return brand;
         }
      }
      return Brand.BMW;
   }


   /**
    * Getting brand for the given shortcut.
    * 
    * @param shortcut
    * @return Brand
    */
   public static Brand valueByShortcut(final String shortcut)
   {
      for (final Brand brand : Brand.values()) {
         if (brand.getShortcut().equals(shortcut)) {
            return brand;
         }
      }
      return Brand.BMW;
   }


   /**
    * Getting brand for the given name.
    * 
    * @param name
    * @return Brand
    */
   public static Brand valueByName(final String name)
   {
      for (final Brand brand : Brand.values()) {
         if (brand.getName().equals(name)) {
            return brand;
         }
      }
      return Brand.BMW;
   }


   /**
    * Getting brand for the given value.
    * 
    * @param value
    * @return Brand
    */
   public static Brand getBrand(final String value)
   {
      for (final Brand brand : Brand.values()) {
         if (brand.getName().equals(value)) {
            return brand;
         }
         else if (brand.getShortcut().equals(value)) {
            return brand;
         }
      }
      return Brand.BMW;
   }
}
