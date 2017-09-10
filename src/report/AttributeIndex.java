
package report;

/*
 * Entity Class for Learning Style 
 */

public class AttributeIndex{
	String attribute;
	int index;

	public final static String KEYWORD_ILS_ACTIVE = "Active";
	public final static String KEYWORD_ILS_REFLECTIVE = "Reflective";
	public final static String KEYWORD_ILS_SENSING = "Sensing";
	public final static String KEYWORD_ILS_INTUITIVE = "Intuitive";
	public final static String KEYWORD_ILS_VISUAL = "Visual";
	public final static String KEYWORD_ILS_VERBAL = "Verbal";//B
	public final static String KEYWORD_ILS_SEQUENTIAL = "Sequential";//Q
	public final static String KEYWORD_ILS_GLOBAL = "Global";
	
	public final static String[]LEFT_ILS_INDEX = {KEYWORD_ILS_ACTIVE,KEYWORD_ILS_SENSING,KEYWORD_ILS_VISUAL,KEYWORD_ILS_SEQUENTIAL};
	public final static String[]RIGHT_ILS_INDEX = {KEYWORD_ILS_REFLECTIVE,KEYWORD_ILS_INTUITIVE,KEYWORD_ILS_VERBAL,KEYWORD_ILS_GLOBAL};
	
	protected AttributeIndex(String attribute, int index) {
		super();
		this.attribute = attribute;
		this.index = index;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getInitial(){
		switch (attribute){
		case KEYWORD_ILS_VERBAL:
			return "B";
		case KEYWORD_ILS_SEQUENTIAL:
			return "Q";
		default:
			if(attribute==null||attribute.length()==0){
				return "";
			}else{
				return attribute.substring(0, 1);
			}
		}
	}
	
	public static AttributeIndex getAttributeFromInitial(char c,int index){
		String attribute = null;
		switch (c){
		case 'A':
			attribute = KEYWORD_ILS_ACTIVE;
			break;
		case 'R':
			attribute = KEYWORD_ILS_REFLECTIVE;
			break;
		case 'S':
			attribute = KEYWORD_ILS_SENSING;
			break;
		case 'I':
			attribute = KEYWORD_ILS_INTUITIVE;
			break;
		case 'V':
			attribute = KEYWORD_ILS_VISUAL;
			break;
		case 'B':
			attribute = KEYWORD_ILS_VERBAL;
			break;
		case 'Q':
			attribute = KEYWORD_ILS_SEQUENTIAL;
			break;
		case 'G':
			attribute = KEYWORD_ILS_GLOBAL;
			break;
		}
		
		if(attribute==null)
			return null;
		
		return  new AttributeIndex(attribute,index);
	}
}