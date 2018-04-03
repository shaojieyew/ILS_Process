package reportProcessor.analysis;

import java.util.ArrayList;
import java.util.List;

import report.AttributeIndex;
import report.Report;

//read specific data from the data to get the author name and ILS attributes

public class ReportDataReaderBySplit extends ReportDataReader {
	public final static String NEWLINE = "\\r?\\n";
	private String text;
	
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	public ReportDataReaderBySplit(String text, Report report){
		setReport(report);
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
				report.setAuthor_name(name);
			}else{
				report.setAuthor_name("");
			}
		}else{
			report.setAuthor_name("");
		}

		text=text.replace(":", " ");
		for(String attribute : ILS_ATTRIBUTES){
			String digits = "";
			if(text.indexOf(attribute)>=0){
				String attributeText = text;
				attributeText = attributeText.split(attribute)[1];
				String arr[] = attributeText.split(NEWLINE);
				if(arr.length>0){
					attributeText = arr[0];
				}
				arr = attributeText.split(".");
				if(arr.length>0){
					attributeText = arr[0];
				}
				boolean digitExist = false;
				for(int i =0;i<attributeText.length();i++){
					if(Character.isDigit(attributeText.charAt(i))){
						digitExist = true;
						digits=digits+attributeText.charAt(i);
					}else{
						if(Character.isAlphabetic(attributeText.charAt(i))){
							break;
						}
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
			AttributeIndex ai = report.getAttributeIndexByAttribute(attribute);
			ai.setIndex(index);
		}
		if(report.getAuthor_name().length()==0){
			String filename = report.getFileName();
			if(filename.contains(".")){
				filename = filename.substring(0, filename.lastIndexOf('.'));
			}
			if(filename.startsWith("ILS ")){
				filename = filename.replaceAll("ILS ", "");
			}
			report.setAuthor_name(filename);
		}
	}
}
