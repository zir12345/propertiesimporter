/**
 * ____________________________________________________________________________
 *
 *           Project: BMW.live
 *           Version: 2.6.0-SNAPSHOT
 * ____________________________________________________________________________
 *
 *         $Revision: 4532 $
 *    $LastChangedBy: qxa5203 $
 *  $LastChangedDate: 2012-01-30 14:32:48 +0100 (Mo, 30 Jan 2012) $
 *       Description: see Javadoc
 * ____________________________________________________________________________
 *
 *         Copyright: (c) BMW AG 2011, all rights reserved
 * ____________________________________________________________________________
 */
package com.bmw.kpi.propertiesimporter;


import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelReader
{

   private final String                          outputFile;
   private final String                          inputFile;
   private final String[]                        inputLocales;
   private final String                          rootDir;
   private final String                          brand;
   private final Map<String, List<ModuleForSID>> sids;
   private final String                          reportMode;
   private final String                          importMode;

   private static final String                   SPR         = "|";
   private static final String                   LF          = "\n";

   private static final String                   SIDTEXTWERK = "xy_XY";
   private static final String                   IDENTIFIER  = "Identifier";
   private static final String                   LOCALE      = "_";
   private static final Color                    COLORYELLOW = Color.YELLOW;


   public ExcelReader(PropertyUtils propertyUtils, Map<String, List<ModuleForSID>> sids)
   {
      super();
      this.outputFile = propertyUtils.getOutputFile();
      this.inputFile = propertyUtils.getInputFile();
      this.rootDir = propertyUtils.getRootDir();
      this.inputLocales = propertyUtils.getInputLocales();
      this.importMode = propertyUtils.getImportMode();
      this.brand = propertyUtils.getBrand();
      this.reportMode = propertyUtils.getReportMode();
      this.sids = sids;
   }


   private void writeHeaderLine(Writer writer, List<String> locales)
      throws IOException
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append("Module" + SPR + "Dir" + SPR + "File" + SPR + "Key" + SPR + "xy_XY");
      for (String locale : locales) {
         buffer.append(SPR);
         buffer.append(locale);
      }
      buffer.append(LF);
      writer.write(buffer.toString());
   }


   public void importPropertiesFromExcel()
      throws IOException, InvalidFormatException
   {
      BufferedWriter writer = PropertyUtils.openReportFile(outputFile);
      List<String> propertyFiles = new ArrayList<String>();
      List<String> importLocales = new ArrayList<String>();
      for (String locale : inputLocales) {
         importLocales.add(locale);
      }
      OPCPackage pkg = OPCPackage.open(inputFile, PackageAccess.READ);
      XSSFWorkbook wb = new XSSFWorkbook(pkg);
      XSSFSheet sheet = wb.getSheetAt(0);

      // get format information from excel first Cell
      int rowNumber = sheet.getLastRowNum() + 1;
      int sidCol = 0;
      int startCol = 0;
      int startRow = 0;

      XSSFRow row = sheet.getRow(startRow);
      int colNumber = row.getLastCellNum();
      for (int j = 0; j < colNumber; j++) {
         XSSFCell cell = sheet.getRow(startRow).getCell(j);
         if (cell != null
             && XSSFCell.CELL_TYPE_STRING == cell.getCellType()
             && (cell.getStringCellValue().indexOf(SIDTEXTWERK) > -1 || cell.getStringCellValue().indexOf(IDENTIFIER) > -1)) {
            sidCol = j;
            break;
         }
      }
      for (int j = sidCol + 1; j < colNumber; j++) {
         XSSFCell cell = sheet.getRow(startRow).getCell(j);
         if (cell != null && XSSFCell.CELL_TYPE_STRING == cell.getCellType()
             && cell.getStringCellValue().indexOf(LOCALE) > -1) {
            startCol = j;
            break;
         }
      }

      // get startCol for locales
      List<String> localesAll = new ArrayList<String>();
      for (int j = startCol; j < colNumber; j++) {
         XSSFCell cell = sheet.getRow(startRow).getCell(j);
         String locale = PropertyUtils.getLocale(cell.getStringCellValue(), importLocales, brand);
         if (!StringUtils.isEmpty(locale)) {
            localesAll.add(locale);
         }
      }
      writeHeaderLine(writer, localesAll);

      // convert to PropertyForModule
      Color importColor = null;
      if (PreferenceConstants.YELLOW.equals(importMode)) {
         importColor = COLORYELLOW;
      }
      for (int i = startRow + 1; i < rowNumber; i++) {
         ParentSID parentSid = PropertyUtils.getParentSID(getCellValue(sheet, i, sidCol));
         String ident = parentSid.getSid();
         if (!StringUtils.isEmpty(ident) && sids.containsKey(ident)) {
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
               for (int j = startCol; j < colNumber; j++) {
                  XSSFCell localeCell = sheet.getRow(startRow).getCell(j, Row.CREATE_NULL_AS_BLANK);
                  String locale = PropertyUtils.getLocale((localeCell.getStringCellValue()), importLocales, brand);
                  if (!StringUtils.isEmpty(locale)) {
                     String path = rootDir + sid.getDirName() + sid.getFileName() + "_" + locale + ".properties";
                     File file = new File(path);
                     String value = importCellValue(sheet, i, j, file, sid.getKey(), importColor, parentSid, locale);
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
            for (int j = startCol; j < colNumber; j++) {
               XSSFCell localeCell = sheet.getRow(startRow).getCell(j, Row.CREATE_NULL_AS_BLANK);
               String locale = PropertyUtils.getLocale((localeCell.getStringCellValue()), importLocales, brand);
               if (!StringUtils.isEmpty(locale)) {
                  String value = getCellValue(sheet, i, j, importColor);
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
      pkg.close();
      PropertyUtils.generateProperties(propertyFiles, rootDir);
   }


   private String getCellValue(XSSFSheet sheet, int i, int j)
   {
      XSSFCell cell;
      if (sheet.getRow(i) == null) {
         return "";
      }
      cell = sheet.getRow(i).getCell(j, Row.CREATE_NULL_AS_BLANK);
      String value = PropertyUtils.getStringValue(cell);
      return value;
   }


   private String getCellValue(XSSFSheet sheet, int i, int j, Color color)
   {
      XSSFCell cell;
      if (sheet.getRow(i) == null) {
         return "";
      }
      cell = sheet.getRow(i).getCell(j, Row.CREATE_NULL_AS_BLANK);
      boolean update = false;
      if (color == null) {
         update = true;
      }
      else if (cell.getCellStyle().getFillForegroundXSSFColor() != null) {
         byte[] rgb = cell.getCellStyle().getFillForegroundXSSFColor().getRgb();
         if (rgb != null && color.equals(new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF))) {
            update = true;
         }
      }
      String value = PropertyUtils.getStringValue(cell);
      update = update && (value != null && !"null".equals(value));
      if (update) {
         value = "#" + value + "#";
      }
      else
         value = "";
      return value;
   }


   private String importCellValue(XSSFSheet sheet,
                                  int i,
                                  int j,
                                  File file,
                                  String key,
                                  Color color,
                                  ParentSID parentSid,
                                  String locale)
      throws IOException
   {
      XSSFCell cell;
      if (sheet.getRow(i) == null) {
         return "";
      }
      cell = sheet.getRow(i).getCell(j, Row.CREATE_NULL_AS_BLANK);
      boolean update = false;
      if (color == null) {
         update = true;
      }
      else if (cell.getCellStyle().getFillForegroundXSSFColor() != null) {
         byte[] rgb = cell.getCellStyle().getFillForegroundXSSFColor().getRgb();
         if (rgb != null && color.equals(new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF))) {
            update = true;
         }
      }
      String value = PropertyUtils.getStringValue(cell, parentSid, locale);
      update = update && (value != null && !"null".equals(value));
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
            }
            else {
               String oldValue = sortedProperties.getProperty(key);
               if (oldValue != null) {
                  oldValue = sortedProperties.getProperty(key).replaceAll("(\\r)?\\n", "\n");
               }
               String proValue = value.replaceAll("<br +/?>|(\\r)?\\n", "\n");
               if (!sortedProperties.containsKey(key) || !oldValue.equals(proValue)) {
                  sortedProperties.setProperty(key, proValue);
                  PropertyUtils.saveSortedProperties(sortedProperties, file);
                  value = "#" + proValue + "#";
               }
            }
         }
      }
      else
         value = "";
      return value;
   }
}
