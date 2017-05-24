package util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class LevenshteinDistance {
 
  public static int computeDistance(String s1, String s2) {
    s1 = s1.toLowerCase();
    s2 = s2.toLowerCase();
 
    int[] costs = new int[s2.length() + 1];
    for (int i = 0; i <= s1.length(); i++) {
      int lastValue = i;
      for (int j = 0; j <= s2.length(); j++) {
        if (i == 0)
          costs[j] = j;
        else {
          if (j > 0) {
            int newValue = costs[j - 1];
            if (s1.charAt(i - 1) != s2.charAt(j - 1))
              newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
            costs[j - 1] = lastValue;
            lastValue = newValue;
          }
        }
      }
      if (i > 0)
        costs[s2.length()] = lastValue;
    }
    return costs[s2.length()];
  }
 
  public static void printDistance(String s1, String s2) {
    System.out.println(s1 + "-->" + s2 + ": " + computeDistance(s1, s2));
  }
 
  public static int matches(String a, String b){
	  String x=a.toLowerCase();
	  String y=b.toLowerCase();
	  return FuzzySearch.ratio(a, b);
  }
  
  /*
  public static void main(String[] args) {
	  
	  String a = "Question";
	  String b = "Questionnaire";
	  a=a.toLowerCase();
	  b=b.toLowerCase();
	  System.out.println(FuzzySearch.ratio(a, b));
	  System.out.println(FuzzySearch.partialRatio(a, b));
	  System.out.println(FuzzySearch.tokenSortRatio(a, b));
	  System.out.println(FuzzySearch.tokenSetRatio(a, b));
	  
	  System.out.println(FuzzySearch.tokenSortPartialRatio(a, b));
	  System.out.println(FuzzySearch.tokenSetPartialRatio(a, b));
	  System.out.println(FuzzySearch.tokenSetPartialRatio(a, b));
	  printDistance(a, b);
	  
	  String correction= "Visual";
	  String input = "Reflective: 1 Intuitive: 1 Visual: 7 Sequential: 9";
	  String input1 = "Reflective 5 > Intuitive 5 > Visual 9 > Sequential 5";
	  List<String> list = Arrays.stream(input.split("[ | ]")).collect(Collectors.toList());
	  System.out.println(list);
	  char ch1 = ' ';
	  char ch2 = ' ';
	  char ch3 = ' ';
	  String hex1 = String.format("%04x", (int) ch1);
	  String hex2 = String.format("%04x", (int) ch2);
	  String hex3 = String.format("%04x", (int) ch3);
	  System.out.println("hex 1 :"+hex1);
	  System.out.println("hex 2 :"+hex2);
	  List<ExtractedResult> results =FuzzySearch.extractSorted(correction, list,70);

	  System.out.println(results);
	  for(ExtractedResult found: results){
		  String str= found.getString();
		  String temp=str;
		  int score = LevenshteinDistance.matches(str, correction);
		  int originalScore = score;
		  int index =input.indexOf(str);
		  int failCount=0;
		  for(int i =index+str.length();i<input.length();i++){
			  temp = temp+input.charAt(i);
			  int nScore = LevenshteinDistance.matches(temp.replace(" ", ""),correction);
			  if(nScore>score){
				  score = nScore;
				  str = temp;
				  failCount=0;
			  }
			  if(nScore<score){
				  failCount++;
			  }
			  System.out.println(temp+"--> "+nScore+"---- failcount:"+failCount);
			  System.out.println(str+"--> "+score);
			  if(failCount>10||(nScore<originalScore&&failCount>2)){
				  break;
			  }
		  }
		  
		  temp = str;
		  if(index>0){
			  for(int i =index-1;i>=0;i--){
				  temp = input.charAt(i)+temp;
				  int nScore = LevenshteinDistance.matches(temp.replace(" ", ""), correction);
				  
				  if(nScore>score){
					  score = nScore;
					  str = temp;
					  failCount=0;
				  }
				  if(nScore<score){
					  failCount++;
				  }
				  System.out.println(temp+"--> "+nScore+"---- failcount:"+failCount);
				  System.out.println(str+"--> "+score);
				  if(failCount>10||(nScore<originalScore&&failCount>2)){
					  break;
				  }
			  }
		  }
		 if(score>70)
			 input=input.replace(str, correction);
	  }
	  
	  System.out.println(input);
  }
  
  */
}