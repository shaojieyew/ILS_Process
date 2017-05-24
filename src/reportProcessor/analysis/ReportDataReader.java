package reportProcessor.analysis;

import java.util.List;

import application.AttributeIndex;

//common interface for multiple different ways to implement ReportDataReader
public interface ReportDataReader {

	public final static String KEYWORD_RESULT = "Results";
	public final static String KEYWORD_QUESTIONNAIRE = "Questionnaire";
	public final static String KEYWORD_ILS_ACTIVE = "Active";
	public final static String KEYWORD_ILS_REFLECTIVE = "Reflective";
	public final static String KEYWORD_ILS_SENSING = "Sensing";
	public final static String KEYWORD_ILS_INTUITIVE = "Intuitive";
	public final static String KEYWORD_ILS_VISUAL = "Visual";
	public final static String KEYWORD_ILS_VERBAL = "Verbal";
	public final static String KEYWORD_ILS_SEQUENTIAL = "Sequential";
	public final static String KEYWORD_ILS_GLOBAL = "Global";
	
	public final static String[] ILS_ATTRIBUTES= {KEYWORD_ILS_ACTIVE,KEYWORD_ILS_REFLECTIVE,KEYWORD_ILS_SENSING,
			KEYWORD_ILS_INTUITIVE,KEYWORD_ILS_VISUAL,KEYWORD_ILS_VERBAL,
			KEYWORD_ILS_SEQUENTIAL,KEYWORD_ILS_GLOBAL};
	

	public String getReport_name();
	public List<AttributeIndex> getAttributeList();
}
