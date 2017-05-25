package reportSummary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import application.Report;
import javafx.collections.ObservableList;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class ReportSummaryExcel implements ReportSummary {
	private XSSFSheet sheet;
	
	public ReportSummaryExcel(XSSFSheet o) {
		sheet=o;
	}

	 
	private int studentColIndex = -1;
	private int recColIndex = -1;
	private int bandColIndex = -1;
	private int headerRow = -1;
	
	@Override
	public void process(ObservableList<Report> reports) {
		if(!verify()){
			System.out.println("Invalid format");
			return;
		}
		System.out.println("Writing data to file");
		Iterator<Row> itr = sheet.iterator(); 
		List<String> StudentList = new ArrayList<String>();
		List<String> NewStudentList = new ArrayList<String>();
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
							break;
						}
					}
				}
			}else{
				Cell cell = row.getCell(studentColIndex);
				if(cell!=null){
					if(cell.getCellType()==Cell.CELL_TYPE_STRING){
						StudentList.add(cell.getStringCellValue());
					}
				}
			}
		}
		
		for(Report report : reports){
			NewStudentList.add(report.getAuthor_name());
		}
		

		for(String x : StudentList){
			  List<ExtractedResult> f= FuzzySearch.extractSorted(x, NewStudentList);
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
						  temp = 50+temp/5;
						  thresholdScore = (int) temp;
					  }
					  if(thresholdScore>85){
						  thresholdScore=85;
					  }
					  if(thresholdScore<50){
						  thresholdScore=50;
					  }
					  if(thresholdScore<score){
						  System.out.println("Found: ["+score+"] "+x+" --> "+y.getString());
					  }
				  }
			 }
		}
	}
	//[76] SHANNON NEO SI LIN [18] --> Shannon Neo [11]					[7]   0.61
	//[75] JAMIE YAP YI QI [15]--> Jamie Yap [9]						[6]	  0.6
	//[71] CHUA JIANBIN BRYAN [18]--> Bryan Chua [10]					[8]   0.55
	//[75] CHENG YANG ZHEN [15]--> Cheng Yang Zhen ( U1521618K) [30]    [15]  0.5
	//[64] ALLAGU REVATHI D/O SUBRAMANIAN[31] --> Allagu Revathi [14]	[17]  0.45
	
	@Override
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
