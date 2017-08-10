package latin;

import exceptions.NotPrimaryFormException;
import exceptions.PartOfSpeechNotFoundException;
import supers.Noun;
import latin.LatinNoun.Declension;
import latin.LatinNoun.DeclensionInterface.Number;
import latin.LatinNoun.DeclensionInterface.Case;
import latin.LatinNoun.LatinNounGenerator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import english.EnglishNoun;

public class LatinNoun extends LatinPartOfSpeech<LatinNounGenerator, Declension, EnglishNoun> implements Noun {

	public enum DeclensionNumber {FIRST, SECOND, THIRD, THIRD_I_STEM, FOURTH, FIFTH}
	private DeclensionNumber declensionNumber;
	public enum Gender {MASCULINE, FEMININE, NEUTER}
	private Gender gender;
	
	public LatinNoun(String part) throws Exception {
		super(part);
	}
	
	public LatinNoun(DeclensionNumber number, int maxLength) throws MalformedURLException, IOException,
	PartOfSpeechNotFoundException {
		super();
		if (number == DeclensionNumber.THIRD || number == DeclensionNumber.THIRD_I_STEM)
			do {
				forms = new ArrayList<Declension>();
				g.random("https://en.wiktionary.org/wiki/Category:Latin_third_declension_nouns",
						null, null, null, null, null, new String[] {"-", ":"}, false, maxLength);
			} while (declensionNumber != number);
		else
			g.random("https://en.wiktionary.org/wiki/Category:Latin_" + toString(number)
				+ "_declension_nouns", null, null, null, null, null, new String[] {"-", ":"}, false, maxLength);
	}
	
	public LatinNoun(DeclensionNumber number, Gender gender, int maxLength) throws MalformedURLException, IOException,
	PartOfSpeechNotFoundException {
		super();
		if (number == DeclensionNumber.THIRD || number == DeclensionNumber.THIRD_I_STEM)
			do {
				forms = new ArrayList<Declension>();
				g.random("https://en.wiktionary.org/wiki/Category:Latin_" + toString(gender) +
					"_nouns_in_the_third_declension", null, null, null, null, null, new String[] {"-", ":"}, false,
					maxLength);
			} while (declensionNumber != number);
		else
			g.random("https://en.wiktionary.org/wiki/Category:Latin_" + toString(gender) +
				"_nouns_in_the_" + toString(number) + "_declension", null, null, null, null, null,
				new String[] {"-", ":"}, false, maxLength);
	}
	
	public LatinNoun(int maxLength) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
		super();
		g.random("https://en.wiktionary.org/wiki/Category:Latin_nouns",
			null, null, null, null, null, new String[] {"-", ":"}, false, maxLength);
	}
	
	public DeclensionNumber getDeclensionNumber() {
		return declensionNumber;
	}

	public void setDeclensionNumber(DeclensionNumber declensionNumber) {
		this.declensionNumber = declensionNumber;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return getForm("nominative", "singular").get() + ", " + getForm("genitive", "singular").get() + ", "
			+ gender.toString().substring(0, 1).toLowerCase() + "." + " (" + toString(declensionNumber) + " declension - "
			+ fullTranslation + ")";
	}
	
	public class LatinNounGenerator
	extends LatinPartOfSpeech<LatinNounGenerator, Declension, EnglishNoun>.LatinGenerator {
		
		@Override protected String getPartOfSpeech() {return PART_OF_SPEECH;}
		
		@Override
		protected boolean handleIrregulars(String s) {
			// FIXME Auto-generated method stub
			return false;
		}
		
		@Override
		protected void reachLangStartProcess() {}
		
		@Override
		protected void reachPOSStartProcess() {}

		@Override
		protected void reachTableStartProcess() throws NotPrimaryFormException {
			if (isGender())
				gender = extractGender();
			else if (isTranslation()) {
				fullTranslation = extractFullTranslation();
				basicTranslation = extractBasicTranslation();
			}
			else if (isDeclensionNumber())
				declensionNumber = extractDeclensionNumber();
		}

		private boolean isGender() {
			return line.contains("gender");
		}
		
		private Gender extractGender() {
			return get(Gender.class, line.substring(line.indexOf("abbr title=\"") + "abbr title=\"".length(),
					line.indexOf(" ", line.indexOf("abbr title=\"") + "abbr title=\"".length())));
		}
		
		@Override
		protected boolean isTranslation() {
			return fullTranslation == null && (line.contains("<li><a href=\"/wiki/")
					|| line.contains("<li>a <a href=\"/wiki/")
					|| line.contains("<li><span class=\"ib-brac\">(</span><span class=\"ib-content\">"));
		}
		
		@Override
		protected String extractBasicTranslation() {
			int start = fullTranslation.indexOf("a ") == 0 ? 2 : 0;
			int end = fullTranslation.contains(",") ? fullTranslation.indexOf(",") : fullTranslation.length();
			return fullTranslation.substring(start, end);
		}
		
		private boolean isDeclensionNumber() {
			return line.contains("declension\">");
		}
		
		private DeclensionNumber extractDeclensionNumber() {
			return get(DeclensionNumber.class, extractAfter(line, "declension\">").replace(" declension", "")
				.toLowerCase() + ((line.contains("i-stem") || line.contains("<i>-ī</i>")) ? " i stem" : ""));
		}

		@Override
		protected Declension extractForm() throws PartOfSpeechNotFoundException {
			try {
				return new Declension(Number.values()[index % 2], Case.values()[index / 2], extract(line));
			} catch (ArrayIndexOutOfBoundsException e) {throw new PartOfSpeechNotFoundException();}
		}
		
	}
		
	public interface DeclensionInterface extends supers.PartOfSpeech.FormInterface {
		public enum Number {SINGULAR, PLURAL}
		public enum Case {NOMINATIVE, GENITIVE, DATIVE, ACCUSATIVE, ABLATIVE, VOCATIVE}
	}
	
	public class Declension extends LatinPartOfSpeech<LatinNounGenerator, Declension, EnglishNoun>.LatinForm
	implements DeclensionInterface {

		public Case case_;
		public Number number;
		
		public Declension(Number number, Case case_, String value) {
			super(value);
			this.number = number;
			this.case_ = case_;
		}
		
		@Override
		public String[] handleIrregulars(String s) {
			// FIXME Auto-generated method stub
			return null;
		}
		
		@Override
		protected String[] findTranslations() {
			switch (number) {
			case SINGULAR:
				switch (case_) {
				case NOMINATIVE: return new String[] {
						"a/the " + e.singular};
				case GENITIVE: return new String[] {
						"of the " + e.singular};
				case DATIVE: return new String[] {
						"to/for the " + e.singular};
				case ACCUSATIVE: return new String[] {
						"a/the " + e.singular};
				case ABLATIVE: return new String[] {
						"by/with/in/from the " + e.singular};
				case VOCATIVE: return new String[] {
						e.singular + "!"};
				}
			case PLURAL:
				switch (case_) {
				case NOMINATIVE: return new String[] {
						e.plural,
						"the " + e.plural};
				case GENITIVE: return new String[] {
						"of the " + e.plural};
				case DATIVE: return new String[] {
						"to/for the " + e.plural};
				case ACCUSATIVE: return new String[] {
						e.plural,
						"the " + e.plural};
				case ABLATIVE: return new String[] {
						"by/with/in/from the " + e.plural};
				case VOCATIVE: return new String[] {
						e.plural + "!"};
				}
			}
			return NO_TRANSLATION;
		}
		
	}
		
}
