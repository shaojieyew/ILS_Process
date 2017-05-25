package reportSummary;

import java.io.File;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import util.FileUtility;

public class ImportFileVerificationFactory {
	
		public static ImportFileVerification getInstance(Object o){
			ImportFileVerification de = null;
	    	if(o instanceof XSSFSheet){
				de= new ImportFileVerificationExcel((XSSFSheet) o);
	    	}
	    	return de;
		}
	}
