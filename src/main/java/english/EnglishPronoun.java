package english;

import java.util.Arrays;

import exceptions.PartOfSpeechNotFoundException;
import supers.Pronoun;

public class EnglishPronoun extends EnglishPartOfSpeech<EnglishPronoun.EnglishPronounGenerator> implements Pronoun {

	public String[] forms;
	
	public EnglishPronoun(String... forms) {
		this.forms = forms;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(forms).replace("[", "").replace("]", "");
	}
	
	public class EnglishPronounGenerator extends EnglishPartOfSpeech<EnglishPronounGenerator>.EnglishGenerator {
		
		@Override protected String getPartOfSpeech() {return PART_OF_SPEECH;}

		@Override
		protected boolean handleIrregulars(String s) {return false;}
		
		@Override
		protected void reachTableEndProcess() throws PartOfSpeechNotFoundException {}
		
	}
	
}