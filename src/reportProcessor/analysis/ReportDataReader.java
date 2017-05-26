package reportProcessor.analysis;

import java.util.List;

import application.AttributeIndex;

//common interface for multiple different ways to implement ReportDataReader
public interface ReportDataReader {

	public final static String KEYWORD_RESULT = "Results";
	public final static String KEYWORD_QUESTIONNAIRE = "Questionnaire";
	public final static String KEYWORD_ILS_ACTIVE = AttributeIndex.KEYWORD_ILS_ACTIVE;
	public final static String KEYWORD_ILS_REFLECTIVE = AttributeIndex.KEYWORD_ILS_REFLECTIVE;
	public final static String KEYWORD_ILS_SENSING= AttributeIndex.KEYWORD_ILS_SENSING;
	public final static String KEYWORD_ILS_INTUITIVE= AttributeIndex.KEYWORD_ILS_INTUITIVE;
	public final static String KEYWORD_ILS_VISUAL= AttributeIndex.KEYWORD_ILS_VISUAL;
	public final static String KEYWORD_ILS_VERBAL = AttributeIndex.KEYWORD_ILS_VERBAL;
	public final static String KEYWORD_ILS_SEQUENTIAL = AttributeIndex.KEYWORD_ILS_SEQUENTIAL;
	public final static String KEYWORD_ILS_GLOBAL = AttributeIndex.KEYWORD_ILS_GLOBAL;
	
	public final static String[] ILS_ATTRIBUTES= {KEYWORD_ILS_ACTIVE,KEYWORD_ILS_REFLECTIVE,KEYWORD_ILS_SENSING,
			KEYWORD_ILS_INTUITIVE,KEYWORD_ILS_VISUAL,KEYWORD_ILS_VERBAL,
			KEYWORD_ILS_SEQUENTIAL,KEYWORD_ILS_GLOBAL};
	

	public String getReport_name();
	public List<AttributeIndex> getAttributeList();
}
