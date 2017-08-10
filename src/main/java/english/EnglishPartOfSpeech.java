package english;

import java.io.IOException;
import java.net.MalformedURLException;
import exceptions.NotPrimaryFormException;
import exceptions.PartOfSpeechNotFoundException;
import supers.PartOfSpeech;

public abstract class EnglishPartOfSpeech<G extends EnglishPartOfSpeech<G>.EnglishGenerator>
extends PartOfSpeech<G, EnglishPartOfSpeech<G>.EnglishForm, EnglishPartOfSpeech<G>>
implements EnglishPartOfSpeechInterface {

	public EnglishPartOfSpeech() {}
	
	public EnglishPartOfSpeech(String s) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
		super(s);
	}
	
	public abstract class EnglishGenerator
	extends PartOfSpeech<G, EnglishPartOfSpeech<G>.EnglishForm, EnglishPartOfSpeech<G>>.Generator {
		
		@Override protected String getLangCode() {return LANG_CODE;}		
		@Override protected String getLanguage() {return LANGUAGE;}
		@Override protected String getTableStartIdentifier() {
			return "><span class=\"mw-headline\" id=\"" + getPartOfSpeech() + "\">";}
		@Override protected String getTableEndIdentifier() {return "<ol>";}
		
		@Override
		protected void reachLangStartProcess() {}
		
		@Override
		protected void reachPOSStartProcess() {}

		@Override
		/**
		 * Really just gets to the line with the parts, no actual table
		 */
		protected void reachTableStartProcess() throws NotPrimaryFormException {}

		@Deprecated
		@Override
		protected boolean isTranslation() {return false;}
		
		@Deprecated
		@Override
		protected String extractBasicTranslation() {return null;}
		
		@Deprecated
		@Override
		protected boolean isForm() {return false;}

		@Deprecated
		@Override
		protected EnglishPartOfSpeech<G>.EnglishForm extractForm() {return null;}

		/**Detects if on the line with the primary forms of the part of speech*/
		protected boolean isParts() {
			return line.contains("<p><strong class=\"") && line.contains(" headword\" lang=\"en\">");
		}
		
		@Override
		public void generate(String form) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
			site = site(form);
			line = "";
			reachLangStart();
			try {
				reachPOSStart();
				reachTableStart();
				reachTableEnd();
				site.close();
			} catch (NotPrimaryFormException e) {
				generate(e.getMessage());
			}
		}
		
	}
	
	public class EnglishForm
	extends PartOfSpeech<G, EnglishPartOfSpeech<G>.EnglishForm, EnglishPartOfSpeech<G>>.Form {
		
		@Deprecated
		@Override
		public String[] handleIrregulars(String s) {return null;}
		
		@Deprecated
		@Override
		protected String[] findTranslations() {return null;}
		
	}
	
}
