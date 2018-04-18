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

		text=text.replace(":", " ");

		if(name.indexOf(KEYWORD_QUESTIONNAIRE)>=0){
			String temp[] = text.split(KEYWORD_QUESTIONNAIRE);
			if(temp.length>1){
				text = temp[1];
			}
		}
		
		boolean done=false;
		String textlines[] = text.split(NEWLINE);
		for(String textline: textlines){
			for(String attribute : ILS_ATTRIBUTES){
				String digits = "";
				if(textline.indexOf(attribute)>=0){
					//attributeText = 
					String []arrsOfCandidate = textline.split(attribute);
					int count=0;
					for(String c :arrsOfCandidate){
						if(count!=0){
							String attributeText =c;
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
							if(digits.length()>0){
								break;
							}
						}
						count++;
					}
				}
				int index = 0;
				if(digits.length()>0){
					index = Integer.parseInt(digits);
				}
				AttributeIndex ai = report.getAttributeIndexByAttribute(attribute);
				if(ai.getIndex()==0){
					ai.setIndex(index);
				}
				if(report.validateFile()){
					done=true;
				}
			}
			if(done){
				break;
				}
		}
		
	}
}
