package english;

import supers.Noun;
import english.EnglishNoun.EnglishNounGenerator;
import exceptions.PartOfSpeechNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;

public class EnglishNoun extends EnglishPartOfSpeech<EnglishNounGenerator> implements Noun {

	public String singular, plural;
	public String[] sp;
	
	public EnglishNoun() {}
	
	public EnglishNoun(String s) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
		super(s);
		sp = new String[] {this.singular, this.plural};
	}
	
	@Override
	public String toString() {
		return singular + ", " + plural;
	}
	
	public class EnglishNounGenerator extends EnglishPartOfSpeech<EnglishNounGenerator>.EnglishGenerator {
		
		@Override protected String getPartOfSpeech() {return PART_OF_SPEECH;}

		@Override
		protected boolean handleIrregulars(String s) {
			// FIXME Auto-generated method stub
			return false;
		}
		
		private String extractSingular() {
			return extract(line);
		}
		
		private String extractPlural() {
			return extractAfterNoSpace(line, "plural");
		}

		@Override
		protected void reachTableEndProcess() {
			if (isParts()) {
				singular = extractSingular();
				plural = extractPlural();
			}
		}
		
	}
	
}
