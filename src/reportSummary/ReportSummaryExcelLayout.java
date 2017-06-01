package reportSummary;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ReportSummaryExcelLayout {

	
	public static void createNewLayout(XSSFSheet sheet){
		int studentListHeader_RowIndex=2;
		int studentSer_ColIndex=1;
		int studentName_ColIndex=2;
		int studentNat_ColIndex=3;
		int studentProg_ColIndex=4;
		int studentRecvdReport_colIndex=5;
		int studentBand_colIndex=6;
		
		studentListHeader_RowIndex= studentListHeader_RowIndex+sheet.getLastRowNum();
	    
		Row header1 = sheet.createRow(studentListHeader_RowIndex);
		Row header2 = sheet.createRow(studentListHeader_RowIndex+1);
		Cell cell = null;

		CellStyle style = sheet.getWorkbook().createCellStyle();
	    style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index );
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        
		
		for(int i =studentSer_ColIndex;i<=studentBand_colIndex+3;i++){
			cell = header1.createCell(i);
			cell.setCellStyle(style);
			cell = header2.createCell(i);
			cell.setCellStyle(style);

	        CellRangeAddress ca = new CellRangeAddress(studentListHeader_RowIndex,studentListHeader_RowIndex+1,i,i);
			RegionUtil.setBorderLeft(1, ca, sheet, sheet.getWorkbook());
			RegionUtil.setBorderRight(1, ca, sheet, sheet.getWorkbook());
			RegionUtil.setBorderBottom(1, ca, sheet, sheet.getWorkbook());
			RegionUtil.setBorderTop(1, ca, sheet, sheet.getWorkbook());
			if(i>=studentBand_colIndex){
		        ca = new CellRangeAddress(studentListHeader_RowIndex,studentListHeader_RowIndex,i,i);
				RegionUtil.setBorderBottom(1, ca, sheet, sheet.getWorkbook());
			}
		}
		
		cell = header1.getCell(studentSer_ColIndex);
		cell.setCellValue("Ser");
		
		cell = header1.getCell(studentName_ColIndex);
		cell.setCellValue("Student");
		
		cell = header1.getCell(studentNat_ColIndex);
		cell.setCellValue("Nat");
		
		cell = header1.getCell(studentProg_ColIndex);
		cell.setCellValue("Prog");
		cell = header2.getCell(studentProg_ColIndex);
		cell.setCellValue("Yr");
		
		cell = header1.getCell(studentRecvdReport_colIndex);
		cell.setCellValue("Recvd");
		
		cell = header1.getCell(studentBand_colIndex);
		cell.setCellValue("Band");
		sheet.addMergedRegion(new CellRangeAddress(studentListHeader_RowIndex,studentListHeader_RowIndex,studentBand_colIndex,studentBand_colIndex+3));

		cell = header2.getCell(studentBand_colIndex);
		cell.setCellValue("1");

		cell = header2.getCell(studentBand_colIndex+1);
		cell.setCellValue("2");

		cell = header2.getCell(studentBand_colIndex+2);
		cell.setCellValue("3");

		cell = header2.getCell(studentBand_colIndex+3);
		cell.setCellValue("4");

	}

}
