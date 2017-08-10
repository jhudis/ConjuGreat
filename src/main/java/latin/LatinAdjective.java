package latin;

import exceptions.NotPrimaryFormException;
import exceptions.PartOfSpeechNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;

import english.EnglishAdjective;
import supers.Adjective;
import latin.LatinAdjective.LatinAdjectiveGenerator;
import latin.LatinNoun.DeclensionInterface.Case;
import latin.LatinNoun.DeclensionInterface.Number;
import latin.LatinAdjective.Declension;
import latin.LatinAdjective.DeclensionInterface.Gender;

public class LatinAdjective extends LatinPartOfSpeech<LatinAdjectiveGenerator, Declension, EnglishAdjective>
		implements Adjective {
	
	public enum DeclensionNumber {FIRST_SECOND, THIRD}
	public DeclensionNumber declensionNumber;
	
	public LatinAdjective(String part) throws Exception {
		super(part);
	}
	
	public LatinAdjective(int maxLength) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
		super();
		g.random("https://en.wiktionary.org/wiki/Category:Latin_adjectives",
			null, null, null, null, null, new String[] {"-", ":"}, false, maxLength);
	}
	
	public LatinAdjective(DeclensionNumber declensionNumber, int maxLength) throws MalformedURLException,
	IOException, PartOfSpeechNotFoundException {
		super();
		switch (declensionNumber) {
		case FIRST_SECOND:
			g.random("https://en.wiktionary.org/wiki/Category:Latin_first_and_second_declension_adjectives",
					null, null, null, null, null, new String[] {"-", ":"}, false, maxLength);
			break;
		case THIRD:
			g.random("https://en.wiktionary.org/wiki/Category:Latin_third_declension_adjectives",
					null, null, null, null, null, new String[] {"-", ":"}, false, maxLength);
			break;
		}
	}

	@Override
	public String toString() {
		return getForm("nominative", "singular", "masculine").get() + ", " +
			   getForm("nominative", "singular", "feminine").get() + ", " +
			   getForm("nominative", "singular", "neuter").get() +
			   " (" + fullTranslation + ")";
	}
	
	public class LatinAdjectiveGenerator extends
	LatinPartOfSpeech<LatinAdjectiveGenerator, Declension, EnglishAdjective>.LatinGenerator {

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

		private boolean isDeclensionNumber() {
			return declensionNumber == null && line.contains("declension");
		}
		
		private DeclensionNumber extractDeclensionNumber() {
			return extractAfter(line, "declension").equals("first") ? DeclensionNumber.FIRST_SECOND :
				DeclensionNumber.THIRD;
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
		
		@Override
		protected void reachTableStartProcess() throws NotPrimaryFormException {
			if (isDeclensionNumber())
				declensionNumber = extractDeclensionNumber();
			else if (isTranslation()) {
				fullTranslation = extractFullTranslation();
				basicTranslation = extractBasicTranslation();
			}
		}

		int skip = -1;
		
		private boolean isRowspan2Form() {
			return line.contains("rowspan=\"2\"");
		}
		
		private void createRowspan2Forms() {
			forms.add(new Declension(Gender.values()[index % 3], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			forms.add(new Declension(Gender.values()[index % 3], Number.values()[(index % 6) / 3],
					Case.values()[index / 6 + 1], extract(line)));
			skip = index + 6;
		}
		
		private boolean isColspan3Form() {
			return line.contains("colspan=\"3\"");
		}
		
		private void createColspan3Forms() {
			forms.add(new Declension(Gender.values()[index % 3], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			forms.add(new Declension(Gender.values()[index % 3 + 1], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			forms.add(new Declension(Gender.values()[index % 3 + 2], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			index += 2;
		}
		
		private boolean isThirdMFForm() {
			return declensionNumber == DeclensionNumber.THIRD && Gender.values()[index % 3] == Gender.MASCULINE;
		}
		
		private void createThirdMFForms() {
			forms.add(new Declension(Gender.values()[index % 3], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			forms.add(new Declension(Gender.values()[index % 3 + 1], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			index++;
		}
		
		private boolean isThirdColspanForm() {
			return declensionNumber == DeclensionNumber.THIRD && line.contains("colspan=\"2\"");
		}
		
		private void createThirdColspanForms() {
			forms.add(new Declension(Gender.values()[index % 3], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			forms.add(new Declension(Gender.values()[index % 3 + 1], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			forms.add(new Declension(Gender.values()[index % 3 + 2], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line)));
			index += 2;
		}
		
		@Override
		protected Declension extractForm() throws PartOfSpeechNotFoundException {
			return new Declension(Gender.values()[index % 3], Number.values()[(index % 6) / 3],
					Case.values()[index / 6], extract(line));
		}

		@Override
		protected void reachTableEndProcess() throws PartOfSpeechNotFoundException {
			if (isForm()) {
				if (index == skip)
					index++;
				if (isThirdColspanForm())
					createThirdColspanForms();
				else if (isThirdMFForm())
					createThirdMFForms();
				else if (isRowspan2Form())
					createRowspan2Forms();
				else if (isColspan3Form())
					createColspan3Forms();
				else
					forms.add(extractForm());
				index++;
			}
		}
		
	}
	
	public interface DeclensionInterface extends supers.PartOfSpeech.FormInterface {
		public enum Gender {MASCULINE, FEMININE, NEUTER}
		public enum Number {SINGULAR, PLURAL}
		public enum Case {NOMINATIVE, GENITIVE, DATIVE, ACCUSATIVE, ABLATIVE, VOCATIVE}
	}
	
	public class Declension
	extends LatinPartOfSpeech<LatinAdjectiveGenerator, Declension, EnglishAdjective>.LatinForm {

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
			return new String[] {e.getFullTranslation()};
		}
		
	}
	
}
