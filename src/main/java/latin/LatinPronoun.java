package latin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import english.EnglishPartOfSpeechInterface;
import english.EnglishPronoun;
import exceptions.NotPrimaryFormException;
import exceptions.PartOfSpeechNotFoundException;
import supers.Pronoun;
import supers.PartOfSpeech.FormInterface;
import latin.LatinPronoun.DeclensionInterface.Gender;
import latin.LatinPronoun.DeclensionInterface.Number;
import latin.LatinPronoun.DeclensionInterface.Case;
import latin.LatinPronoun.Declension;
import latin.LatinPronoun.LatinPronounGenerator;


public class LatinPronoun extends LatinPartOfSpeech<LatinPronounGenerator, Declension, EnglishPronoun>
		implements Pronoun {
	
	public LatinPronoun(String part) throws Exception {
		super(part);
	}
	
	@Override
	public String toString() {
		return getForm("nominative", "singular", "masculine").get() + ", " +
			   getForm("nominative", "singular", "feminine").get() + ", " +
			   getForm("nominative", "singular", "neuter").get() +
			   " (" + fullTranslation + ")";
	}

	public class LatinPronounGenerator
	extends LatinPartOfSpeech<LatinPronounGenerator, Declension, EnglishPronoun>.LatinGenerator {

		@Override
		protected String getPartOfSpeech() {return PART_OF_SPEECH;}

		@Override
		protected boolean handleIrregulars(String s) {
			// FIXME Auto-generated method stub
			return false;
		}

		@Override
		protected void reachLangStartProcess() {}

		@Override
		protected void reachPOSStartProcess() throws NotPrimaryFormException {}

		@Override
		protected void reachTableStartProcess() throws NotPrimaryFormException {
			if (isTranslation()) {
				fullTranslation = extractFullTranslation();
				basicTranslation = extractBasicTranslation();
			}
		}

		@Override
		protected boolean isTranslation() {
			return fullTranslation == null && line.contains("<li><a href=\"/wiki/");
		}

		@Override
		protected String extractBasicTranslation() {
			int start = 0;
			int end = fullTranslation.contains(",") ? fullTranslation.indexOf(",") : fullTranslation.length();
			return fullTranslation.substring(start, end);
		}
		
		private boolean isColspanForm() {
			return line.contains("colspan=\"");
		}
		
		private void createColspanForms() {
			int colspan = Integer.valueOf(Character.toString(line.charAt(line.indexOf("colspan=\"")
					+ "colspan=\"".length())));
			for (int i = 0; i < colspan; i++)
				forms.add(new Declension(Gender.values()[index % 3 + i], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			index += colspan - 1;
		}

		@Override
		protected Declension extractForm() throws PartOfSpeechNotFoundException {
			return new Declension(Gender.values()[index % 3], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line));
		}
		
		@Override
		protected void reachTableEndProcess() throws PartOfSpeechNotFoundException {
			if (isForm()) {
				if (isColspanForm())
					createColspanForms();
				else
					forms.add(extractForm());
				index++;
			}
		}

		@Override
		protected EnglishPartOfSpeechInterface english()
				throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
			@SuppressWarnings("serial")
			Map<String, String[]> english = new HashMap<String, String[]>(){{
				put("ipse", new String[] {"himself", "herself", "itself", "themselves"});
			}};
			if (english.containsKey(forms.get(0).get()))
				return new EnglishPronoun(english.get(forms.get(0).get()));
			return null;
		}
		
		
		
	}
	
	public interface DeclensionInterface extends FormInterface {
		public enum Gender {MASCULINE, FEMININE, NEUTER}
		public enum Number {SINGULAR, PLURAL}
		public enum Case {NOMINATIVE, GENITIVE, DATIVE, ACCUSATIVE, ABLATIVE}
	}
	
	public class Declension extends LatinPartOfSpeech<LatinPronounGenerator, Declension, EnglishPronoun>.LatinForm
	implements DeclensionInterface {

		public Case case_;
		public Number number;
		public Gender gender;
		
		public Declension(Gender gender, Number number, Case case_, String value) {
			super(value);
			this.gender = gender;
			this.number = number;
			this.case_ = case_;
		}

		@Override
		protected String[] handleIrregulars(String s) {
			// FIXME Auto-generated method stub
			return null;
		}

		@Override
		protected String[] findTranslations() {
			if (e.forms.length == 1) {
				n = 1;
			} else if (e.forms.length == 2) {
				n = Arrays.binarySearch(Number.values(), number);
			} else if (e.forms.length == 4) {
				n = number == Number.SINGULAR ? Arrays.binarySearch(Gender.values(), gender) : 3;
			} else if(e.forms.length == 6) {
				n = Arrays.binarySearch(Number.values(), number) * 3
				  + Arrays.binarySearch(Gender.values(), gender);
			} else {
				return NO_TRANSLATION;
			}
			
			switch (number) {
			case SINGULAR:
				switch (case_) {
				case NOMINATIVE: return new String[] {
						e.forms[n]};
				case GENITIVE: return new String[] {
						"of " + e.forms[n]};
				case DATIVE: return new String[] {
						"to/for " + e.forms[n]};
				case ACCUSATIVE: return new String[] {
						e.forms[n]};
				case ABLATIVE: return new String[] {
						"by/with/in/from " + e.forms[n]};
				}
			case PLURAL:
				switch (case_) {
				case NOMINATIVE: return new String[] {
						e.forms[n]};
				case GENITIVE: return new String[] {
						"of " + e.forms[n]};
				case DATIVE: return new String[] {
						"to/for " + e.forms[n]};
				case ACCUSATIVE: return new String[] {
						e.forms[n]};
				case ABLATIVE: return new String[] {
						"by/with/in/from " + e.forms[n]};
				}
			}
			
			return NO_TRANSLATION;
		}
		
	}
	
}
