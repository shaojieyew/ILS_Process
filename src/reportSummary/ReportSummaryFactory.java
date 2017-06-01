package reportSummary;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ReportSummaryFactory {
		public static ReportSummary instance=null;
		public static ReportSummary createInstance(Object o){
			ReportSummary rs = null;
	    	if(o instanceof XSSFSheet){
	    		rs= new ReportSummaryExcelXSSF((XSSFSheet) o);
	    	}
	    	instance=rs;
	    	return rs;
		}
		public static ReportSummary getInstance(){
			return instance;
		}
		public static ReportSummary deleteInstance(){
			return instance=null;
		}
	}
