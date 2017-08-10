package web;

public class Wrapper {

	Answer[][][] input;

	public Wrapper() {}
	
	public Wrapper(Answer[][][] input) {
		this.input = input;
	}

	public Answer[][][] getInput() {
		return input;
	}

	public void setInput(Answer[][][] input) {
		this.input = input;
	}

}
