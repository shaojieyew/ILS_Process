package reportSummary;

import java.io.File;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ImportFileVerificationExcel implements ImportFileVerification {
	
	private XSSFSheet sheet;
	public ImportFileVerificationExcel(XSSFSheet sheet){
		this.sheet = sheet;
	}
	
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
