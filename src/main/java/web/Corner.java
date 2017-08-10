package web;

import java.util.Arrays;

public class Corner {

	String value;
	String[][] tops;
	String[][] lefts;
	boolean sameChildren;
	
	public Corner(String value, String[][] tops, String[][] lefts) {
		this.value = value;
		this.tops = tops;
		this.lefts = lefts;
		sameChildren = true;
	}
	
	public Corner(String value, String[][] tops, String[][] lefts, boolean sameChildren) {
		this(value, tops, lefts);
		this.sameChildren = sameChildren;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String[][] getTops() {
		return tops;
	}

	public void setTops(String[][] tops) {
		this.tops = tops;
	}

	public String[][] getLefts() {
		return lefts;
	}

	public void setLefts(String[][] lefts) {
		this.lefts = lefts;
	}
	
	public boolean getSameChildren() {
		return sameChildren;
	}
	
	public void setSameChildren(boolean sameChildren) {
		this.sameChildren = sameChildren;
	}
	
	@Override
	public String toString() {
		String s = "\n";
		s += "\n" + value + ":";
		s += "\n lefts- " + Arrays.deepToString(lefts);
		s += "\n  tops- " + Arrays.deepToString(tops);
		if (!sameChildren)
			s += "\nNot same children";
		return s;
	}

}
