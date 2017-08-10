package web;

import java.text.Normalizer;
import java.util.Arrays;

public class Answer {

	private String actual;
	private String expected;
	private String correct;
	private boolean ignoreAccents;
	private String actualTranslations;
	private String[] expectedTranslations;
	private String correctTranslation;
	private boolean justOne;
	
	public Answer() {
		correct = "unknown";
		ignoreAccents = false;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
		updateCorrect();
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected == null ? "" : expected;
		updateCorrect();
	}

	public String getCorrect() {
		return correct;
	}

	public void setCorrect(String correct) {
		this.correct = correct;
	}
	
	public boolean getIgnoreAccents() {
		return ignoreAccents;
	}
	
	public void setIgnoreAccents(boolean ignoreAccents) {
		this.ignoreAccents = ignoreAccents;
		updateCorrect();
	}
	
	private void updateCorrect() {
		if (ignoreAccents)
			correct = removeAccents(actual).equals(removeAccents(expected)) ? "correct" : "incorrect";
		else
			correct = actual.equals(expected) ? "correct" : "incorrect";
	}
	
	/**
	 * Removes the accents in a given String
	 * @param s the String to remove accents from
	 * @return the String without accents
	 */
	private String removeAccents(String s) {
		if (s == null) return null;
		return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public String getActualTranslations() {
		return actualTranslations;
	}

	public void setActualTranslations(String actualTranslations) {
		this.actualTranslations = actualTranslations;
		updateCorrectTranslation();
	}

	public String[] getExpectedTranslations() {
		return expectedTranslations;
	}

	public void setExpectedTranslations(String[] expectedTranslations) {
		this.expectedTranslations = expectedTranslations;
		updateCorrectTranslation();
	}

	public String getCorrectTranslation() {
		return correctTranslation;
	}

	public void setCorrectTranslation(String correctTranslation) {
		this.correctTranslation = correctTranslation;
	}
	
	public boolean getJustOne() {
		return justOne;
	}
	
	public void setJustOne(boolean justOne) {
		this.justOne = justOne;
		updateCorrectTranslation();
	}
	
	private void updateCorrectTranslation() {
		if (expectedTranslations == null) return;
		if (justOne)
			correctTranslation = Arrays.asList(expectedTranslations).contains(actualTranslations) ? "correct" : "incorrect";
		else {
			Arrays.sort(expectedTranslations);
			String[] sortedAT = actualTranslations.split(", ");
			Arrays.sort(sortedAT);
			correctTranslation = Arrays.equals(expectedTranslations, sortedAT) ? "correct" : "incorrect";
		}
	}

}
