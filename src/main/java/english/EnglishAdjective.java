package english;

import exceptions.PartOfSpeechNotFoundException;
import supers.Adjective;
import english.EnglishAdjective.EnglishAdjectiveGenerator;

public class EnglishAdjective extends EnglishPartOfSpeech<EnglishAdjectiveGenerator> implements Adjective {
	
	public EnglishAdjective(String s) {
		fullTranslation = s;
	}
	
	@Override
	public String toString() {
		return fullTranslation;
	}
	
	public class EnglishAdjectiveGenerator extends EnglishPartOfSpeech<EnglishAdjectiveGenerator>.EnglishGenerator {

		@Override protected String getPartOfSpeech() {return PART_OF_SPEECH;}

		@Override
		protected boolean handleIrregulars(String s) {
			// FIXME Auto-generated method stub
			return false;
		}
		
		@Override
		protected void reachTableEndProcess() throws PartOfSpeechNotFoundException {}
		
	}

}