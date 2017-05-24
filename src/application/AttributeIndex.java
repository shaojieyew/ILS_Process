
package application;

/*
 * Entity Class for Learning Style 
 */

public class AttributeIndex{
	String attribute;
	int index;
	public AttributeIndex(String attribute, int index) {
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
}