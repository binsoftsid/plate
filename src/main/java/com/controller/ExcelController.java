package com.controller;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

//ako se kreira xlsx 
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.jasperreports.engine.export.oasis.CellStyle;

@Component
public class ExcelController {

	public void buildExcelDocument() throws Exception {

		
	    //kreiranje xlsx fajla
		//XSSFWorkbook workbook = new XSSFWorkbook();
	    //XSSFSheet sheet = workbook.createSheet("Datatypes in Java");

	    Workbook workbook = new HSSFWorkbook();
	    //Workbook wb = new XSSFWorkbook();
	    CreationHelper createHelper = workbook.getCreationHelper();
	    
        Object[][] datatypes = {
                {"Datatype", "Type", "Size(in bytes)"},
                {"int", "Primitive", 2},
                {"float", "Primitive", 4},
                {"double", "Primitive", 8},
                {"char", "Primitive", 1},
                {"String", "Non-Primitive", "No fixed size"}
        };
        int rowNum = 0;
        Sheet sheet = workbook.createSheet("new sheet");
        
        for (Object[] datatype : datatypes) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 2;
            for (Object field : datatype) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }

	    
	    
	    
	    //Sheet sheet = workbook.createSheet("new sheet");
	
	    // Create a row and put some cells in it. Rows are 0 based.
	    /*
	    Row row = sheet.createRow(0);
	    Cell cell = row.createCell(0);
	    cell.setCellValue(1);
	    Cell cell1 = row.createCell(2);
	    cell1.setCellValue("PROBA Excel");
		
	    row.createCell(1).setCellValue(1.2);
	    row.createCell(2).setCellValue(
	    		createHelper.createRichTextString("This is a string"));
	    row.createCell(3).setCellValue(true);
	    */
	 // Create a Sheet
       // Sheet sheet = workbook.createSheet("Employee");

 
        // Create a Row
        //Row headerRow = sheet.createRow(0);

	
	    // Write the output to a file
	    //OutputStreamWriter izlaz = new OutputStreamWriter(new FileOutputStream("workbook.xls"),"UTF-8");
	    OutputStream fileOut = new FileOutputStream("workbook.xls")  ; //FileOutputStream("workbook.xls")
	    workbook.write(fileOut);
	    
	}
}