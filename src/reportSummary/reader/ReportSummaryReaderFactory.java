package reportSummary.reader;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ReportSummaryReaderFactory {
		public static ReportSummaryReader instance=null;
		public static ReportSummaryReader createInstance(Object o){
			ReportSummaryReader rs = null;
	    	if(o instanceof XSSFSheet){
	    		rs= new ReportSummaryExcelXSSFReader((XSSFSheet) o);
	    	}
	    	instance=rs;
	    	return rs;
		}
		public static ReportSummaryReader getInstance(){
			return instance;
		}
		public static ReportSummaryReader deleteInstance(){
			return instance=null;
		}
	}
