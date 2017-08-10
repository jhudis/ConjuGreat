package web;

public class Counter {

	int value;
	
	public Counter() {
		value = -1;
	}
	
	public int get() {
		return value;
	}
	
	public void increment() {
		value++;
	}
	
	public void reset() {
		value = -1;
	}

}
