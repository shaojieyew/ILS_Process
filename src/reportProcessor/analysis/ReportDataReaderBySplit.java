package reportProcessor.analysis;

import java.util.ArrayList;
import java.util.List;

import report.AttributeIndex;

//read specific data from the data to get the author name and ILS attributes

public class ReportDataReaderBySplit implements ReportDataReader {
	public final static String NEWLINE = "\\r?\\n";
	private String text;
	private List<AttributeIndex> attributeList = new ArrayList<AttributeIndex>();
	private String report_name="";
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getReport_name() {
		return report_name;
	}

	public void setReport_name(String report_name) {
		this.report_name = report_name;
	}

	public List<AttributeIndex> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<AttributeIndex> attributeList) {
		this.attributeList = attributeList;
	}

	
	public ReportDataReaderBySplit(String text){
		this.text = text;

		String name = text;
		name=name.replace(":", System.lineSeparator());
		if(name.indexOf(KEYWORD_QUESTIONNAIRE)>=0){
			name = name.split(KEYWORD_QUESTIONNAIRE)[1];
			name = name.split(NEWLINE)[0];
			if(name.indexOf(KEYWORD_RESULT)>=0){
				name = name.split(KEYWORD_RESULT)[1];
				name = name.split(NEWLINE)[0];
				String temp[] = name.trim().split(" ");
				if(temp!=null&&temp.length>0){
					name=name.replace(temp[0], "");
				}
				name=name.trim();
				
				if(name.indexOf(KEYWORD_ILS_ACTIVE)>=0){
					name = name.split(KEYWORD_ILS_ACTIVE)[0];
				}
				if(name.indexOf(KEYWORD_ILS_REFLECTIVE)>=0){
						name = name.split(KEYWORD_ILS_REFLECTIVE)[0];
				}
				report_name=name;
			}else{
				report_name="";
			}
		}else{
			report_name="";
		}

		text=text.replace(":", "");
		for(String attribute : ILS_ATTRIBUTES){
			String digits = "";
			if(text.indexOf(attribute)>=0){
				String attributeText = text;
				attributeText = attributeText.split(attribute)[1];
				//if(attribute.indexOf(NEWLINE)>=0){
					String arr[] = attributeText.split(NEWLINE);
					if(arr.length>0){
						attributeText = arr[0];
					}
				//}
				//if(attributeText.indexOf(".")>=0){
					 arr = attributeText.split(".");
						if(arr.length>0){
							attributeText = arr[0];
						}
				//}
				boolean digitExist = false;
				for(int i =0;i<attributeText.length();i++){
					if(Character.isDigit(attributeText.charAt(i))){
						digitExist = true;
						digits=digits+attributeText.charAt(i);
					}else{
						if(digitExist){
							break;
						}
					}
				}
			}

			int index = 0;
			if(digits.length()>0){
				index = Integer.parseInt(digits);
			}
			AttributeIndex ai = new AttributeIndex(attribute,index);
			attributeList.add(ai);
		}
	}
}
