package reportSummary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import javafx.collections.ObservableList;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import report.AttributeIndex;
import report.Report;

public class ReportSummaryExcelXSSF implements ReportSummary {
	private XSSFSheet sheet;
	
	public ReportSummaryExcelXSSF(XSSFSheet o) {
		sheet=o;
	}

	private int studentColIndex = -1;
	private int recColIndex = -1;
	private int bandColIndex = -1;
	private int headerRow = -1;
	private boolean includeUnknownStudentInCounting = true;
	
	@Override
	public void process(ObservableList<Report> reports) {
		if(!verify()){
			//System.out.println("Invalid format");
			ReportSummaryExcelLayout.createNewLayout(sheet);	
		}
		System.out.println("Writing data to file");
		Iterator<Row> itr = sheet.iterator(); 
		List<String> StudentList = new ArrayList<String>();
		List<String> NewStudentList = new ArrayList<String>();
		List<Report> reportList = new ArrayList<Report>();

		int endOfstudentRow = -1;
		while (itr.hasNext()) { 
			Row row = itr.next(); 
			if(studentColIndex==-1||recColIndex==-1||bandColIndex==-1){
				 studentColIndex = -1;
				 recColIndex = -1;
				 bandColIndex = -1;
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell tempCell = cellIterator.next();
					if(tempCell.getCellType()==Cell.CELL_TYPE_STRING){
						if(tempCell.getStringCellValue().equals("Student")){
							studentColIndex = tempCell.getColumnIndex();
						}
						if(tempCell.getStringCellValue().equals("Recvd")){
							recColIndex = tempCell.getColumnIndex();
						}
						if(tempCell.getStringCellValue().equals("Band")){
							bandColIndex = tempCell.getColumnIndex();
						}
						if(studentColIndex!=-1&&recColIndex!=-1&&bandColIndex!=-1){
							headerRow = row.getRowNum();
							endOfstudentRow = headerRow+2;
							break;
						}
					}
				}
			}else{
				Cell cell = row.getCell(studentColIndex);
				if(cell!=null){
					if(cell.getCellType()==Cell.CELL_TYPE_STRING){
						if(cell.getStringCellValue().length()>0){
							StudentList.add(cell.getStringCellValue());
						}
					}
				}
			}

			//System.out.print(row.getRowNum());

			if(headerRow>-1){
				if(row.getRowNum()>headerRow+1){
					Cell cell = row.getCell(studentColIndex);
					if(cell==null||cell.getCellType()!=Cell.CELL_TYPE_STRING||cell.getStringCellValue().length()==0){
						endOfstudentRow=row.getRowNum();
						break;
					}
				}
			}
		}
		
		for(Report report : reports){
			if(report.getStatus().equals(Report.STATUS_COMPLETED)){
				NewStudentList.add(report.getAuthor_name());
				reportList.add(report);
			}
		}
		

		for(String x : StudentList){
			  List<ExtractedResult> f= FuzzySearch.extractSorted(x, NewStudentList,55);
			  for(ExtractedResult y : f){
				  if(x.equals(FuzzySearch.extractOne(y.getString(), StudentList).getString())){
					  //if(FuzzySearch.tokenSortRatio(x, y.getString())>80)
					  int score = FuzzySearch.tokenSortRatio(x, y.getString());
					  //int spaceCount1 =  x.split("\\s+").length -1;
					  //int spaceCount2 =  y.getString().split("\\s+").length -1;
					  float xlen = x.length();
					  float ylen = y.getString().length();
					  int thresholdScore = 100;
					  float temp = 1;
					  if(xlen<ylen){
						  temp = xlen/ylen;
					  }
					  if(xlen>ylen){
						  temp = ylen/xlen;
					  }
					  if(xlen==ylen){
						  thresholdScore = 85;
					  }else{
						  temp=temp-0.45f;
						  temp = temp*500;
						  temp = 55+temp/5;
						  thresholdScore = (int) temp;
					  }
					  if(thresholdScore>85){
						  thresholdScore=85;
					  }
					  if(thresholdScore<55){
						  thresholdScore=55;
					  }
					  if(thresholdScore<score){
					//	  System.out.println("Found: ["+score+"] "+x+" --> "+y.getString());
						  writeAttributeToList(x,y.getString(), reportList);
					  }
				  }
			 }
		}
		
		//System.out.println(endOfstudentRow);
		if(endOfstudentRow>-1){
			cleanSheet(endOfstudentRow,sheet.getLastRowNum(),0,bandColIndex+3);
			
			if(includeUnknownStudentInCounting){
				for(Report r: reportList){
					Row newRow = getRow( endOfstudentRow);
					Cell studentCell = newRow.createCell(studentColIndex);
					studentCell.setCellValue(r.getAuthor_name().toUpperCase());
					
					writeReportToRow(newRow, r);
					endOfstudentRow++;
				}
			}
			
			writeSummaryCount(endOfstudentRow);
			

			if(!includeUnknownStudentInCounting){
				int rowForLeftOver =endOfstudentRow+6;
				for(Report r: reportList){
					Row newRow = getRow( rowForLeftOver);
					Cell studentCell = newRow.createCell(studentColIndex);
					studentCell.setCellValue(r.getAuthor_name().toUpperCase());
					
					writeReportToRow(newRow, r);
					rowForLeftOver++;
				}
			}
			
			writeDetailSummaryCount(endOfstudentRow, headerRow, bandColIndex+7, false);
			writeDetailSummaryCount(endOfstudentRow, headerRow+11, bandColIndex+7, true);
	
		}
	}

	private void writeDetailSummaryCount(int endOfstudentRow, int startRow, int startCol, boolean percentage) {
		int studentStartRow = headerRow+3;
		int studentEndRow = endOfstudentRow;
		cleanSheet(startRow,sheet.getLastRowNum(),startCol,startCol+14);

		CellStyle formulaStyle = sheet.getWorkbook().createCellStyle();
		Font formulaFont = sheet.getWorkbook().createFont();
		formulaFont.setColor(IndexedColors.BLUE.getIndex());
		formulaStyle.setFont(formulaFont);
		
		CellStyle formulaStyle2 = sheet.getWorkbook().createCellStyle();
		Font formulaFont2 = sheet.getWorkbook().createFont();
		formulaFont2.setColor(IndexedColors.GREY_25_PERCENT.getIndex());
		formulaStyle2.setFont(formulaFont2);
		
		CellStyle alignCenterStyle = sheet.getWorkbook().createCellStyle();
		alignCenterStyle.setAlignment(CellStyle.ALIGN_CENTER);
		
		CellStyle perctStyle = sheet.getWorkbook().createCellStyle();
		perctStyle.setFont(formulaFont);
		perctStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("0.0%"));
		
		String colLetter1="";
		String colLetter2="";
		Row r = getRow(startRow);
		Cell cell = null;
		if(!percentage){
			cell = r.createCell(startCol);
			cell.setCellValue("Results (counting) for: XXXXX");
			
			cell = r.createCell(startCol+6);
			cell.setCellValue("sampling =");

			cell = r.createCell(startCol+7);
			cell.setCellType(Cell.CELL_TYPE_FORMULA);
			colLetter1 = CellReference.convertNumToColString(studentColIndex-1);
			colLetter2 = CellReference.convertNumToColString(bandColIndex-1);
			cell.setCellFormula("$"+colLetter2+"$"+(studentEndRow+3)+" / $"+colLetter1+"$"+(studentEndRow+3));
			cell.setCellStyle(perctStyle);

			cell = r.createCell(startCol+9);
			cell.setCellValue("error =");
			
			cell = r.createCell(startCol+10);
			cell.setCellType(Cell.CELL_TYPE_FORMULA);
			colLetter1 = CellReference.convertNumToColString(startCol+14);
			colLetter2 = CellReference.convertNumToColString(bandColIndex-1);
			cell.setCellFormula("IF( SUM("+colLetter1+""+(startRow+4)+":"+colLetter1+""+(startRow+7)+")=4*$"+colLetter2+"$"+(studentEndRow+3)+", \"nil\", \"YES\" )");
			cell.setCellStyle(formulaStyle);
		}else{
			cell = r.createCell(startCol);
			cell.setCellValue("Results (percentage) for: XXXXX");
		}
		
		
		int rowAttrIndexHeader =  startRow+2;
		r = getRow(rowAttrIndexHeader);
		int count = 1;
		for(int i=-11;i<=11;i=i+2){
			int x=i;
			if(x<0){
				x = (-1)*x;
			}
			cell = r.createCell(startCol+count);
			cell.setCellValue(x);
			cell.setCellStyle(alignCenterStyle);
			count++;
		}
		cell =r.createCell(startCol+14);
		cell.setCellValue("Chk");
		cell.setCellStyle(formulaStyle2);
		
		for(int z =1; z<=4;z++){
			r = getRow(rowAttrIndexHeader+z);
			if(z==1){
				cell =r.createCell(startCol);
				cell.setCellValue("ACT");
				cell =r.createCell(startCol+13);
				cell.setCellValue("REF");
			}

			if(z==2){
				cell =r.createCell(startCol);
				cell.setCellValue("SEN");
				cell =r.createCell(startCol+13);
				cell.setCellValue("INT");
			}

			if(z==3){
				cell =r.createCell(startCol);
				cell.setCellValue("VIS");
				cell =r.createCell(startCol+13);
				cell.setCellValue("VRB");
			}

			if(z==4){
				cell =r.createCell(startCol);
				cell.setCellValue("SEQ");
				cell =r.createCell(startCol+13);
				cell.setCellValue("GLO");
			}
			for(int i=-11;i<=11;i=i+2){
				int x=i;
				if(x<0){
					x = (-1)*x;
				}
				int index = i+11;
				index = index/2;

				cell = r.createCell(startCol+1+index);
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				String att = "";
				
				if(z==1){
					colLetter1 = CellReference.convertNumToColString(bandColIndex);
					att="A";
					if(i>0)
						att="R";
				}

				if(z==2){
					colLetter1 = CellReference.convertNumToColString(bandColIndex+1);
					att="S";
					if(i>0)
						att="I";
				}

				if(z==3){
					colLetter1 = CellReference.convertNumToColString(bandColIndex+2);
					att="V";
					if(i>0)
						att="B";
				}

				if(z==4){
					colLetter1 = CellReference.convertNumToColString(bandColIndex+3);
					att="Q";
					if(i>0)
						att="G";
				}
				if(percentage){
					String totalRecvColLetter = CellReference.convertNumToColString(bandColIndex-1);
					String totalRecvFormula = "$"+totalRecvColLetter+"$"+(studentEndRow+3);
					String indexCount = "COUNTIF("+colLetter1+(studentStartRow)+":"+colLetter1+(endOfstudentRow+1)+", \""+att+x+"\")";
					cell.setCellFormula("IF(("+indexCount+") = 0, \"\", 100 * ("+indexCount+") / ("+totalRecvFormula+"))");
				}else{
					cell.setCellFormula("COUNTIF("+colLetter1+(studentStartRow)+":"+colLetter1+(endOfstudentRow+1)+", \""+att+x+"\")");
				}
				CellStyle formulaStyle3 = formulaStyle;
				formulaStyle3.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("0"));
				cell.setCellStyle(formulaStyle3);
				

				cell =r.createCell(startCol+14);
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				colLetter1 = CellReference.convertNumToColString(startCol+1);
				colLetter2 = CellReference.convertNumToColString(startCol+12);
				cell.setCellFormula("SUM("+colLetter1+(r.getRowNum()+1)+":"+colLetter2+(r.getRowNum()+1)+")");
				cell.setCellStyle(formulaStyle2);
			}
			
			r = getRow(startRow+8);
			cell =r.createCell(startCol);
			cell.setCellValue("Chk");
			cell.setCellStyle(formulaStyle2);
			for(int i =1;i<=12;i++){
				colLetter1 = CellReference.convertNumToColString(startCol+i);
				cell =r.createCell(startCol+i);
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				cell.setCellFormula("SUM("+colLetter1+""+(startRow+4)+":"+colLetter1+""+(startRow+7)+")");
				cell.setCellStyle(formulaStyle2);
			}
		}
		
		if(percentage){
			for(int z =1; z<=4;z++){
				r = getRow(rowAttrIndexHeader+z);
				colLetter1 = CellReference.convertNumToColString(startCol+1);
				colLetter2 = CellReference.convertNumToColString(startCol+12);
				for(int x=1;x<=12;x++){
					String colLetter3 = CellReference.convertNumToColString(startCol+x);
			        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
			        ConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule("$"+colLetter3+"$"+(r.getRowNum()+1)+"=(MAX($"+colLetter1+"$"+(r.getRowNum()+1)+":$"+colLetter2+"$"+(r.getRowNum()+1)+"))");
			        PatternFormatting fill1 = rule1.createPatternFormatting();
			        fill1.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.index);
			        fill1.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
			        CellRangeAddress[] regions = {
			                CellRangeAddress.valueOf("$"+colLetter3+"$"+(r.getRowNum()+1))
			        };
			        sheetCF.addConditionalFormatting(regions, rule1);
				}
			}
		}
	}
	
	private void cleanSheet(int startRow, int endRow, int startCol, int endCol){
		for(int i =startRow;i<=endRow;i++){
			Row r = getRow(i);
			for(int x = startCol;x<=(endCol);x++){
				Cell c = r.getCell(x);
				if(c!=null){
					r.removeCell(c);
				}
			}
		}
	}

	private Row getRow(int index){
		Row row = sheet.getRow(index);
		if(row==null){
			row = sheet.createRow(index);
		}
		return row;
	}
	
	private void writeSummaryCount(int endOfstudentRow){	
		int rowIndex = endOfstudentRow+1;
		Row row = getRow( rowIndex);

		CellStyle style = sheet.getWorkbook().createCellStyle();
	    style.setAlignment(CellStyle.ALIGN_CENTER);

		CellStyle formulaStyle = sheet.getWorkbook().createCellStyle();
		Font formulaFont = sheet.getWorkbook().createFont();
		formulaFont.setColor(IndexedColors.BLUE.getIndex());
		formulaStyle.setFont(formulaFont);
	    
	    
		Cell cell = row.createCell(bandColIndex);
		cell.setCellValue("A/R");
		cell.setCellStyle(style);
		
		cell = row.createCell(bandColIndex+1);
		cell.setCellValue("S/I");
		cell.setCellStyle(style);
		
		cell = row.createCell(bandColIndex+2);
		cell.setCellValue("V/B");
		cell.setCellStyle(style);
		
		cell = row.createCell(bandColIndex+3);
		cell.setCellValue("Q/G");
		cell.setCellStyle(style);
		
		rowIndex=rowIndex+1;
		row = getRow( rowIndex);

		cell = row.createCell(studentColIndex-1);
		String colLetter = CellReference.convertNumToColString(studentColIndex);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTA("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+")");
		cell.setCellStyle(formulaStyle);

		cell = row.createCell(bandColIndex-2);
		cell.setCellValue("Totals");

		colLetter = CellReference.convertNumToColString(recColIndex);
		cell = row.createCell(bandColIndex-1);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTIF("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+", \"Y\")");
		cell.setCellStyle(formulaStyle);
		
		colLetter = CellReference.convertNumToColString(bandColIndex);
		cell = row.createCell(bandColIndex);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTA("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+")");
		cell.setCellStyle(formulaStyle);
		
		colLetter = CellReference.convertNumToColString(bandColIndex+1);
		cell = row.createCell(bandColIndex+1);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTA("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+")");
		cell.setCellStyle(formulaStyle);

		colLetter = CellReference.convertNumToColString(bandColIndex+2);
		cell = row.createCell(bandColIndex+2);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTA("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+")");
		cell.setCellStyle(formulaStyle);

		colLetter = CellReference.convertNumToColString(bandColIndex+3);
		cell = row.createCell(bandColIndex+3);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTA("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+")");
		cell.setCellStyle(formulaStyle);
		
		rowIndex=rowIndex+1;
		row = getRow( rowIndex);

		cell = row.createCell(bandColIndex-1);
		cell.setCellValue("left band =");

		colLetter = CellReference.convertNumToColString(bandColIndex);
		cell = row.createCell(bandColIndex);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTIF("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+", \"A*\")");
		cell.setCellStyle(formulaStyle);
		
		colLetter = CellReference.convertNumToColString(bandColIndex+1);
		cell = row.createCell(bandColIndex+1);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTIF("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+", \"S*\")");
		cell.setCellStyle(formulaStyle);
		
		colLetter = CellReference.convertNumToColString(bandColIndex+2);
		cell = row.createCell(bandColIndex+2);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTIF("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+", \"V*\")");
		cell.setCellStyle(formulaStyle);
		
		colLetter = CellReference.convertNumToColString(bandColIndex+3);
		cell = row.createCell(bandColIndex+3);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTIF("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+", \"Q*\")");
		cell.setCellStyle(formulaStyle);
		

		rowIndex=rowIndex+1;
		row = getRow( rowIndex);

		cell = row.createCell(bandColIndex-1);
		cell.setCellValue("right band =");

		colLetter = CellReference.convertNumToColString(bandColIndex);
		cell = row.createCell(bandColIndex);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTIF("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+", \"R*\")");
		cell.setCellStyle(formulaStyle);
		
		colLetter = CellReference.convertNumToColString(bandColIndex+1);
		cell = row.createCell(bandColIndex+1);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTIF("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+", \"I*\")");
		cell.setCellStyle(formulaStyle);
		
		colLetter = CellReference.convertNumToColString(bandColIndex+2);
		cell = row.createCell(bandColIndex+2);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTIF("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+", \"B*\")");
		cell.setCellStyle(formulaStyle);
		
		colLetter = CellReference.convertNumToColString(bandColIndex+3);
		cell = row.createCell(bandColIndex+3);
		cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("COUNTIF("+colLetter+"$"+(headerRow+3)+":"+colLetter+"$"+(endOfstudentRow+1)+", \"G*\")");
		cell.setCellStyle(formulaStyle);
		
	}
	
	private void writeReportToRow(Row newRow, Report r){
		Cell recCell = newRow.createCell(recColIndex);
		recCell.setCellValue("Y");
		
		Cell bandCell1=newRow.createCell(bandColIndex);
		bandCell1.setCellValue(getMaxIndex(r,AttributeIndex.KEYWORD_ILS_ACTIVE,AttributeIndex.KEYWORD_ILS_REFLECTIVE));

		Cell bandCell2=newRow.createCell(bandColIndex+1);
		bandCell2.setCellValue(getMaxIndex(r,AttributeIndex.KEYWORD_ILS_SENSING,AttributeIndex.KEYWORD_ILS_INTUITIVE));

		Cell bandCell3=newRow.createCell(bandColIndex+2);
		bandCell3.setCellValue(getMaxIndex(r,AttributeIndex.KEYWORD_ILS_VISUAL,AttributeIndex.KEYWORD_ILS_VERBAL));

		Cell bandCell4=newRow.createCell(bandColIndex+3);
		bandCell4.setCellValue(getMaxIndex(r,AttributeIndex.KEYWORD_ILS_SEQUENTIAL,AttributeIndex.KEYWORD_ILS_GLOBAL));
		
	}
	
	/*
	private void createSpaceforLeftOverStudent(int endOfstudentRow){
		int lastRow = sheet.getLastRowNum();
		int minCol = 0;
		int maxCol = bandColIndex+3;

		for(int i =endOfstudentRow; i <=lastRow;i++){
			int newRowIndex = i;
			Row newRow = getRow( newRowIndex);
			for(int cIndex = minCol; cIndex<=maxCol;cIndex++ ){
				Cell newCell = newRow.getCell(cIndex);
				if(newCell!=null)	
					newRow.removeCell(newCell);
			}
		}
	}
	*/
	/*
	private Cell copyCell(Cell newCell, Cell oldCell){
		newCell.setCellStyle(oldCell.getCellStyle());
		//newCell.setCellFormula(oldCell.getCellFormula());
		if(oldCell.getCellType()==Cell.CELL_TYPE_STRING){
			newCell.setCellValue(oldCell.getStringCellValue());
		}
		if(oldCell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			newCell.setCellValue(oldCell.getNumericCellValue());
		}
		return newCell;
	}
	*/
	private void writeAttributeToList(String x, String y, List<Report> reports ){
		Iterator<Row> itr = sheet.iterator(); 
		while (itr.hasNext()) { 
			Row row = itr.next(); 
			if(row.getRowNum()>headerRow+1){
				Cell cell=row.getCell(studentColIndex);
				if(cell==null){
					break;
				}else{
					if(cell.getCellType()==Cell.CELL_TYPE_STRING){
						if(cell.getStringCellValue().equals(x)){
							for(Report r: reports){
								if(y.equals(r.getAuthor_name())){

									writeReportToRow(row, r);
									reports.remove(r);
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private String getMaxIndex(Report r, String attr1, String attr2){
		AttributeIndex index1 = r.getAttributeIndexByAttribute(attr1);
		AttributeIndex index2 = r.getAttributeIndexByAttribute(attr2);
		if(index1.getIndex()>index2.getIndex()){
			return (index1.getInitial()+""+index1.getIndex());
		}else{
			return (index2.getInitial()+""+index2.getIndex());
		}
	}
	
	public boolean save(File destFile){
		boolean success = true;
		FileOutputStream out = null;
		try {
			try {
			out =  new FileOutputStream(destFile.getAbsolutePath());
			sheet.getWorkbook().write(out);
			}catch(FileNotFoundException ex){
				//AppDialog.alert("Cannot generate report to excel!",ex.getMessage());
				success=false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			success=false;
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	public boolean verify() {
		boolean valid = false;
		Iterator<Row> itr = sheet.iterator(); 
		int studentColIndex = -1;
		int recColIndex = -1;
		int bandColIndex = -1;
		while (itr.hasNext()) { 
			Row row = itr.next(); 
			if(studentColIndex==-1||recColIndex==-1||bandColIndex==-1){
				 studentColIndex = -1;
				 recColIndex = -1;
				 bandColIndex = -1;
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell tempCell = cellIterator.next();
					if(tempCell.getCellType()==Cell.CELL_TYPE_STRING){
						if(tempCell.getStringCellValue().equals("Student")){
							studentColIndex = tempCell.getColumnIndex();
						}
						if(tempCell.getStringCellValue().equals("Recvd")){
							recColIndex = tempCell.getColumnIndex();
						}
						if(tempCell.getStringCellValue().equals("Band")){
							bandColIndex = tempCell.getColumnIndex();
						}
						if(studentColIndex!=-1&&recColIndex!=-1&&bandColIndex!=-1){
							valid=true;
							break;
						}
					}
				}
			}
			
		}
		return valid;
	}
}
