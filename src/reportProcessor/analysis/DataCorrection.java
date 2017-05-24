package reportProcessor.analysis;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import util.LevenshteinDistance;

public class DataCorrection {

	
	private String text;
	private String[] correctText = {ReportDataReader.KEYWORD_QUESTIONNAIRE,ReportDataReader.KEYWORD_RESULT,
			ReportDataReader.KEYWORD_ILS_ACTIVE,ReportDataReader.KEYWORD_ILS_REFLECTIVE,ReportDataReader.KEYWORD_ILS_SENSING,ReportDataReader.KEYWORD_ILS_INTUITIVE,
			ReportDataReader.KEYWORD_ILS_VISUAL,ReportDataReader.KEYWORD_ILS_VERBAL,ReportDataReader.KEYWORD_ILS_SEQUENTIAL,ReportDataReader.KEYWORD_ILS_GLOBAL};

	public final static int STRICTNESS_VERY_STRICT =80;
	public final static int STRICTNESS_STRICT =70;
	public final static int STRICTNESS_LESS_STRICT =60;
	public final static int STRICTNESS_NOT_STRICT =40;
	
	
	//correct word in inText base on correctText array
	//use fuzzy search and check the match rate, if it matches the word replace it with the correct text.
	public DataCorrection(String inText){
		text = inText;
	}
	
	public String getCorrectedText(int search_strictness, int word_match_strictness){
		if(search_strictness<0)
			search_strictness=0;
		if(search_strictness>100)
			search_strictness=100;
		if(word_match_strictness<0)
			word_match_strictness=0;
		if(word_match_strictness>100)
			word_match_strictness=100;
		
		String newText="";
		if(text==null){
			text="";
			return "";
		}
		String lines[] =text.split("\\r?\\n");
		for(String line : lines){

			String arr[] = line.split("\\s+|[ ]+");
			List<String> wordList = Arrays.stream(arr).collect(Collectors.toList());

			for(String correction:correctText){
				  List<ExtractedResult> results = FuzzySearch.extractSorted(correction, wordList,search_strictness);
				  /*
				  for(ExtractedResult found: results){
					  if(LevenshteinDistance.matches(found.getString(), correction)>60)
						  text=text.replace(found.getString(), correction);
				  }
				  */
				  for(ExtractedResult found: results){
					  List<String> correctTextList = Arrays.stream(correctText).collect(Collectors.toList());
					  String closest = FuzzySearch.extractOne(found.getString(), correctTextList).getString();
					  if(closest.equals(correction)){
						  String str= found.getString();
						  String temp=str;
						  int score = LevenshteinDistance.matches(str, correction);
						  int originalScore = score;
						  int index =line.indexOf(str);
						  int failCount=0;
						  for(int i =index+str.length();i<line.length();i++){
							  temp = temp+line.charAt(i);
							  int nScore = LevenshteinDistance.matches(temp.replace(" ", "").replace(":", ""), correction);
							  if(nScore>score){
								  score = nScore;
								  str = temp;
								  failCount=0;
							  }
							  if(nScore<score){
								  failCount++;
							  }
							  //System.out.println(temp+"--> "+nScore+"---- failcount:"+failCount);
							  //System.out.println(str+"--> "+score);
							  if(failCount>5||(nScore<originalScore&&failCount>3)){
								  break;
							  }
						  }
						  
						  temp = str;
						  if(index>0){
							  for(int i =index-1;i>=0;i--){
								  temp = line.charAt(i)+temp;
								  int nScore = LevenshteinDistance.matches(temp.replace(" ", "").replace(":", ""), correction);
								  
								  if(nScore>score){
									  score = nScore;
									  str = temp;
									  failCount=0;
								  }
								  if(nScore<score){
									  failCount++;
								  }
								  //System.out.println(temp+"--> "+nScore+"---- failcount:"+failCount);
								  //System.out.println(str+"--> "+score);
								  if(failCount>5||(nScore<originalScore&&failCount>3)){
									  break;
								  }
							  }
						  }
						 if(score>word_match_strictness)
							 line=line.replace(str.trim(), correction);
					  }
				  }
			}
			line = correctAmbiguousAlphaNum(line);
			
			if(word_match_strictness>DataCorrection.STRICTNESS_LESS_STRICT||search_strictness>DataCorrection.STRICTNESS_LESS_STRICT){

				if((line.indexOf(ReportDataReader.KEYWORD_ILS_ACTIVE)>-1||
						line.indexOf(ReportDataReader.KEYWORD_ILS_REFLECTIVE)>-1||
						line.indexOf(ReportDataReader.KEYWORD_ILS_SENSING)>-1||
						line.indexOf(ReportDataReader.KEYWORD_ILS_INTUITIVE)>-1||
						line.indexOf(ReportDataReader.KEYWORD_ILS_VISUAL)>-1||
						line.indexOf(ReportDataReader.KEYWORD_ILS_VERBAL)>-1||
						line.indexOf(ReportDataReader.KEYWORD_ILS_SEQUENTIAL)>-1||
						line.indexOf(ReportDataReader.KEYWORD_ILS_GLOBAL)>-1)){
					
					DataCorrection dc = new DataCorrection(line);
					line=dc.getCorrectedText(DataCorrection.STRICTNESS_LESS_STRICT,DataCorrection.STRICTNESS_LESS_STRICT);
				}
			}
			newText = newText+line+System.getProperty("line.separator").toString();
		}
		return newText;
	}
	
	
	public  String correctAmbiguousAlphaNum(String input){
		String inputArr[] = input.split("\\s+|[ ]+");
		for(String word : inputArr){
			if(word.length()==1 || (word.length()==2 && word.charAt(0)==word.charAt(1))){
				if(word.charAt(0)=='l' || word.charAt(0)=='I' || word.charAt(0)=='J'){
					String newWord = word.replace(word.charAt(0)+"", "1");
					input=input.replace(word, newWord);
				}
			}
			if(word.length()==1){
				if(word.charAt(0)=='O'||word.charAt(0)=='D'||word.charAt(0)=='Q'){
					String newWord = word.replace(word.charAt(0)+"", "0");
					input=input.replace(word, newWord);
				}
				if(word.charAt(0)=='B'){
					String newWord = word.replace(word.charAt(0)+"", "8");
					input=input.replace(word, newWord);
				}
				if(word.charAt(0)=='S'){
					String newWord = word.replace(word.charAt(0)+"", "5");
					input=input.replace(word, newWord);
				}
				if(word.charAt(0)=='Z'){
					String newWord = word.replace(word.charAt(0)+"", "2");
					input=input.replace(word, newWord);
				}
			}
			
			int numberOfLetter =0;
			int nonLetter =0;
			for (char ch : word.toCharArray()) {
			  if (Character.isLetter(ch)) {
				  numberOfLetter ++;
			  }else{
				  nonLetter++;
			  }
			}
			if ((float)nonLetter/(float)numberOfLetter<=0.25f||(nonLetter==1&&word.length()>=2)) { 
				String newWord = word.replace("0", "o");
				newWord = newWord.replace("1","l");
				newWord = newWord.replace("5","S");
				input=input.replace(word, newWord);
			}
		}
		input=input.replaceAll(" 1 1 ", " 11 ");
		return input;
	}
	public String getCorrection(){
		return text;
	}
}
