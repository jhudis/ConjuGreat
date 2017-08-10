package web;

public class Setting {

	String id;
	String text;
	boolean checked;
	boolean prechecked;
	
	public Setting(String id, String text, boolean prechecked) {
		this.id = id;
		this.text = text;
		this.prechecked = prechecked;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean getChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		this.prechecked = checked;
	}
	
	public boolean getPrechecked() {
		return prechecked;
	}

	public void setPrechecked(boolean prechecked) {
		this.prechecked = prechecked;
	}

}
