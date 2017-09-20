package reportSummary.reader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import javafx.collections.ObservableList;
import report.AttributeIndex;
import report.Report;
import reportSummary.ReportSummaryExcelLayout;
import reportSummary.ReportSummaryExcelXSSF;
import reportSummary.ReportSummaryFactory;

public class ReportSummaryExcelXSSFReader implements ReportSummaryReader{

	private XSSFSheet sheet;
	
	public ReportSummaryExcelXSSFReader(XSSFSheet o) {
		sheet=o;
	}
	private int studentColIndex = -1;
	private int recColIndex = -1;
	private int bandColIndex = -1;
	private int headerRow = -1;

	@Override
	public ArrayList<Report> read() {
		Iterator<Row> itr = sheet.iterator(); 
		ArrayList<String> StudentList = new ArrayList<String>();
		ArrayList<Report> reportList = new ArrayList<Report>();

		int endOfstudentRow = -1;
		while (itr.hasNext()) { 
			Row row = itr.next(); 
			//header
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
			}
			//student list
			else{
				Cell cell = row.getCell(studentColIndex);
				if(cell!=null){
					if(cell.getCellType()==Cell.CELL_TYPE_STRING){
						if(cell.getStringCellValue().length()>0){
							//StudentList.add(cell.getStringCellValue());
							ArrayList<AttributeIndex> atts = new ArrayList<AttributeIndex>();
							for(int i =0;i<4;i++){
								Cell attributeCell = row.getCell(bandColIndex+i);
								if(attributeCell!=null&&attributeCell.getCellType()==Cell.CELL_TYPE_STRING){
									atts.add(getAttributeFromString(attributeCell.getStringCellValue()));
								}
							}
							Report r = new Report(cell.getStringCellValue(),atts,Report.STATUS_COMPLETED);
							reportList.add(r);
						}
					}
				}
			}

		}
		return reportList;
	}
	
	
	private AttributeIndex getAttributeFromString(String text){
		char c = text.charAt(0);
		int index = Integer.parseInt(text.substring(1));
		return AttributeIndex.getAttributeFromInitial(c,index);
	}

}
