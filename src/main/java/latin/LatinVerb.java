package latin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import english.EnglishVerb;
import exceptions.NotPrimaryFormException;
import exceptions.PartOfSpeechNotFoundException;
import latin.LatinVerb.Conjugation;
import latin.LatinVerb.LatinVerbGenerator;
import latin.LatinVerb.ConjugationInterface.*;
import latin.LatinVerb.ConjugationInterface.Number;
import supers.Verb;

public class LatinVerb extends LatinPartOfSpeech<LatinVerbGenerator, Conjugation, EnglishVerb> implements Verb {
	
	public enum ConjugationNumber {FIRST, SECOND, THIRD, THIRD_IO, FOURTH}
	private ConjugationNumber conjugationNumber;
	
	public LatinVerb(String part) throws Exception {
		super(part);
	}
	
	public LatinVerb(ConjugationNumber number, int maxLength) throws MalformedURLException, IOException,
	PartOfSpeechNotFoundException {
		super();
		switch (number) {
			case THIRD:
				g.random("https://en.wiktionary.org/wiki/Category:Latin_third_conjugation_verbs",
						null, null, null, new String[] {"r", "io"}, null, new String[] {"-", ":"}, false, maxLength);
				break;
			case THIRD_IO:
				g.random("https://en.wiktionary.org/wiki/Category:Latin_third_conjugation_verbs",
						null, null, new String[] {"io"}, new String[] {"r"}, null, new String[] {"-", ":"}, false,
						maxLength);
				break;
			default:
				g.random("https://en.wiktionary.org/wiki/Category:Latin_" + toString(number) + "_conjugation_verbs",
						null, null, null, new String[] {"r"}, null, new String[] {"-", ":"}, false, maxLength);
		}
	}
	
	public LatinVerb(int maxLength) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
		super();
		g.random("https://en.wiktionary.org/wiki/Category:Latin_verbs",
				null, null, null, new String[] {"r"}, null, new String[] {"-", ":"}, false, maxLength);
	}
	
	public ConjugationNumber getConjugationNumber() {
		return conjugationNumber;
	}

	public void setConjugationNumber(ConjugationNumber conjugationNumber) {
		this.conjugationNumber = conjugationNumber;
	}
	
	public String getPrincipleParts() {
		String principleParts = "";
		try {
			principleParts += getForm("indicative", "active", "present", "singular", "first").get();
			principleParts += ", " + getForm("non-finite forms", "infinitives", "active", "present").get();
			principleParts += ", " + getForm("indicative", "active", "perfect", "singular", "first").get();
			try {
				principleParts += ", " + getForm("non-finite forms", "participles", "passive", "perfect").get();
			} catch (Exception e) {
				principleParts += ", " + getForm("non-finite forms", "participles", "active", "future").get();
			}
			principleParts = principleParts.substring(0, principleParts.length() - 1) + "m";
		} catch (Exception e) {}
		return principleParts;
	}

	@Override
	public String toString() {
		String s = "";
		//s += e.toString().replace("\n", "");
		s += "\n" + getPrincipleParts();
		s += " (" + toString(conjugationNumber) + " conjugation - " + fullTranslation + ")";
		return s;
	}
	
	public class LatinVerbGenerator
	extends LatinPartOfSpeech<LatinVerbGenerator, Conjugation, EnglishVerb>.LatinGenerator {
		
		@Override protected String getPartOfSpeech() {return PART_OF_SPEECH;}
		
		@Override
		protected boolean handleIrregulars(String s) {
			// FIXME Auto-generated method stub
			return false;
		}
		
		@Override
		protected void reachLangStartProcess() {}
		
		@Override
		protected void reachPOSStartProcess() throws NotPrimaryFormException {
			if (hasPrefix())
				throw new NotPrimaryFormException(extractBase());
		}

		@Override
		protected void reachTableStartProcess() throws NotPrimaryFormException {
			if (isNotFirstPrinciplePart())
				throw new NotPrimaryFormException(extractFirstPrinciplePart());
			if (isConjugationNumber())
				conjugationNumber = extractConjugationNumber();
			if (isTranslation()) {
				fullTranslation = extractFullTranslation();
				basicTranslation = extractBasicTranslation();
			}
		}
		
		private boolean hasPrefix() {
			return line.contains("<p>From <i class=\"Latn mention\" lang=\"la\" xml:lang=\"la\"><a href=\"/wiki/")
					&& extractAfter(line, "From").contains("-") && !extractBase().contains("-");
		}
		
		private String extractBase() {
			return removeAccents(extractAfter(line, "+"));
		}
		
		private boolean isNotFirstPrinciplePart() {
			return line.contains("<span class=\"form-of-definition-link\">");
		}
		
		private String extractFirstPrinciplePart() {
			return removeAccents(extractAfter(line, "<span class=\"form-of-definition-link\">"));
		}
		
		private boolean isConjugationNumber() {
			return line.contains("<i><a href=\"/wiki/Appendix:Latin_");
		}
		
		private ConjugationNumber extractConjugationNumber() {
			String conjugationNumber = extractAfter(line, "<i><a href=\"/wiki/Appendix:Latin_")
					.replace(" conjugation", "");
			if (line.contains("iō-variant"))
				conjugationNumber += " io";
			return get(ConjugationNumber.class, conjugationNumber);
		}

		@Override
		protected boolean isTranslation() {
			return fullTranslation == null && line.contains("<li>") && line.length() != 4 &&
					((!line.contains("<span") && line.charAt(4) != '<') ||
	line.contains("<span class=\"ib-brac\">(</span><span class=\"ib-content\"><a href=\"/wiki/Appendix:Glossary#"));
		}

		@Override
		protected String extractFullTranslation() {
			return super.extractFullTranslation().replaceAll("I", "to").replaceAll("am", "be");
		}
		
		private Mood mood;
		private Voice voice;
		private Tense tense;
		private Verbal verbal;
		
		private boolean isTableHeader() {
			return line.contains("<th");
		}
		
		private boolean isMood() {
			return line.contains("colspan=\"2\" rowspan=\"2\"") || line.contains("verbal nouns");
		}
		
		private Mood extractMood() {
			return get(Mood.class, extract(line));
		}
		
		private boolean isVoice() {
			return line.contains("active<") || line.contains("passive<");
		}
		
		private Voice extractVoice() {
			return get(Voice.class, extract(line));
		}
		
		private boolean isTense() {
			return line.contains("th style=\"background:#") && !line.contains(";width:");
		}
		
		private Tense extractTense() {
			return get(Tense.class, extract(line).replace("&#160;", " "));
		}
		
		private boolean isVerbal() {
			return line.contains("<th style=\"background:#e2e4c0\" colspan=\"2\">");
		}
		
		private Verbal extractVerbal() {
			return get(Verbal.class, extract(line));
		}
		
		/**
		 * @return whether the line contains a form which is "x + some form of sum"
		 */
		private boolean isSumTense() {
			return line.contains("colspan=\"6\"");
		}
		
		/**
		 * @see LatinVerbGenerator#isSumTense()
		 */
		private void createSumTenses() {
			String stem = extract(line);
			String ending = extract(line.substring(line.indexOf("</a>")));
			String[] endings = null;
			switch (ending) {
				case " + present active indicative of ":
					endings = new String[] {"sum", "es", "est", "sumus", "estis", "sunt"};
					break;
				case " + imperfect active indicative of ":
					endings = new String[] {"eram", "erās", "erat", "erāmus", "erātis", "erant"};
					break;
				case " + future active indicative of ":
					endings = new String[] {"erō", "eris", "erit", "erimus", "eritis", "erunt"};
					break;
				case " + present active subjunctive of ":
					endings = new String[] {"sim", "sīs", "sit", "sīmus", "sītis", "sint"};
					break;
				case " + imperfect active subjunctive of ":
					endings = new String[] {"essem", "essēs", "esset", "essēmus", "essētis", "essent"};
					break;
			}
			
			for (index = 0; index < 6; index++) {
				forms.add(new StandardConjugation(mood, voice, tense, Number.values()[index / 3],
						Person.values()[index % 3], stem + " " + endings[index]));
			}
			index = 0;
		}
		
		private boolean isEmptyConjugation() {
			return line.contains("—");
		}
		
		private NonFinite extractNonFiniteConjugation() {
			return new NonFinite(verbal, Voice.values()[index / 3], (new Tense[] {Tense.PRESENT, Tense.PERFECT,
				Tense.FUTURE})[index % 3], (extract(line) +
				(extract(line, 2) == null || extract(line, 2).equals(", ") ? "" : " " + extract(line, 3))));
		}
		
		private VerbalNoun extractVerbalNounConjugation() {
			return new VerbalNoun(VerbalNounType.values()[index / 4],
				VerbalNounCase.values()[index <= 3 ? index : index - 1],
				extract(line));
		}

		@Override
		protected StandardConjugation extractForm() {
			return new StandardConjugation(mood, voice, tense, Number.values()[index / 3],
					Person.values()[index % 3], extract(line));
		}
		
		@Override
		protected boolean isForm() {
			return line.contains("<td");
		}

		@Override
		protected void reachTableEndProcess() {
			if (isTableHeader()) {
				if (isVerbal())
					verbal = extractVerbal();
				else if (isMood())
					mood = extractMood();
				else if (isVoice())
					voice = extractVoice();
				else if (isTense())
					tense = extractTense();
				else if (isSumTense())
					createSumTenses();
			} else if (isForm()) {
				if (!isEmptyConjugation()) {
					if (mood == Mood.NON_H_FINITE_FORMS)
						forms.add(extractNonFiniteConjugation());
					else if (mood == Mood.VERBAL_NOUNS)
						forms.add(extractVerbalNounConjugation());
					else
						forms.add(extractForm());
				}
				index = (index + 1) % 6;
			}
		}
		
		@Override
		protected String extractBasicTranslation() {
			int start = fullTranslation.indexOf("to ") == 0 ? 3 : 0;
			int end = fullTranslation.contains(",") ? fullTranslation.indexOf(",") : fullTranslation.length();
			return fullTranslation.substring(start, end);
		}
		
	}
	
	public interface ConjugationInterface extends supers.PartOfSpeech.FormInterface {
		public enum Mood {INDICATIVE, SUBJUNCTIVE, IMPERATIVE, NON_H_FINITE_FORMS, VERBAL_NOUNS}
		public enum Voice {ACTIVE, PASSIVE}
		public enum Tense {PRESENT, IMPERFECT, FUTURE, PERFECT, PLUPERFECT, FUTURE_PERFECT}
		public enum Number {SINGULAR, PLURAL}
		public enum Person {FIRST, SECOND, THIRD}
		public enum Verbal {INFINITIVES, PARTICIPLES}
		public enum VerbalNounType {GERUND, SUPINE}
		public enum VerbalNounCase {NOMINATIVE, GENITIVE, DATIVE_S_ABLATIVE, ACCUSATIVE, ABLATIVE}
	}
	
	public abstract class Conjugation extends LatinPartOfSpeech<LatinVerbGenerator, Conjugation, EnglishVerb>.LatinForm
	implements ConjugationInterface {
		
		public Conjugation(String value) {
			super(value);
		}
		
		@Override
		public String[] handleIrregulars(String s) {
			// FIXME Auto-generated method stub
			return null;
		}
		
	}
	
	public class StandardConjugation extends Conjugation {
		
		public Mood mood;
		public Voice voice;
		public Tense tense;
		public Number number;
		public Person person;
		
		public StandardConjugation(Mood mood, Voice voice, Tense tense, Number number, Person person, String value) {
			super(value);
			this.mood = mood;
			this.voice = voice;
			this.tense = tense;
			this.number = number;
			this.person = person;
			n = 3 * Arrays.binarySearch(Number.values(), number) + Arrays.binarySearch(Person.values(), person);
		}
		
		@Override
		protected String[] findTranslations() {
			switch (mood) {
			case INDICATIVE:
				switch(voice) {
				case ACTIVE:
					switch(tense) {
					case PRESENT: return new String[] {
						EnglishVerb.SUBJECT[n] + " " + e.spr[n],
						EnglishVerb.SUBJECT[n] + " " + EnglishVerb.AM[n] + " " + e.prp,
						EnglishVerb.SUBJECT[n] + " " + EnglishVerb.DO[n] + " " + e.fspr};
					case IMPERFECT: return new String[] {
						EnglishVerb.SUBJECT[n] + " " + EnglishVerb.WAS[n] + " " + e.prp,
						EnglishVerb.SUBJECT[n] + " used to " + e.fspr};
					case FUTURE: return new String[] {
						EnglishVerb.SUBJECT[n] + " will " + e.fspr};
					case PERFECT: return new String[] {
						EnglishVerb.SUBJECT[n] + " " + e.spa,
					   	EnglishVerb.SUBJECT[n] + " " + EnglishVerb.HAVE[n] + " " + e.pap,
						EnglishVerb.SUBJECT[n] + " did " + e.fspr};
					case PLUPERFECT: return new String[] {
						EnglishVerb.SUBJECT[n] + " had " + e.pap};
					case FUTURE_PERFECT: return new String[] {
						EnglishVerb.SUBJECT[n] + " will have " + e.pap};
					}
				case PASSIVE:
					switch(tense) {
					case PRESENT: return new String[] {
						EnglishVerb.SUBJECT[n] + " " + EnglishVerb.AM[n] + " " + e.pap,
					   	EnglishVerb.SUBJECT[n] + " " + EnglishVerb.AM[n] + " being " + e.pap};
					case IMPERFECT: return new String[] {
						EnglishVerb.SUBJECT[n] + " " + EnglishVerb.WAS[n] + " being " + e.pap,
						EnglishVerb.SUBJECT[n] + " used to be " + e.pap};
					case FUTURE: return new String[] {
						EnglishVerb.SUBJECT[n] + " will be " + e.pap};
					case PERFECT: return new String[] {
						EnglishVerb.SUBJECT[n] + " " + EnglishVerb.WAS[n] + " " + e.pap,
					   	EnglishVerb.SUBJECT[n] + " " + EnglishVerb.HAVE[n] + " been " + e.pap};
					case PLUPERFECT: return new String[] {
						EnglishVerb.SUBJECT[n] + " had been " + e.pap};
					case FUTURE_PERFECT: return new String[] {
						EnglishVerb.SUBJECT[n] + " will have been " + e.pap};
					}
				}
			case SUBJUNCTIVE:
				switch(voice) {
				case ACTIVE:
					switch(tense) {
					case PRESENT: return new String[] {
							EnglishVerb.SUBJECT[n] + " may " + e.fspr};
					case IMPERFECT: return new String[] {
							EnglishVerb.SUBJECT[n] + " might " + e.fspr};
					case PERFECT: return new String[] {
							EnglishVerb.SUBJECT[n] + " might " + e.fspr};
					case PLUPERFECT: return new String[] {
							EnglishVerb.SUBJECT[n] + " would have " + e.pap};
					default:
						break;
					}
				case PASSIVE:
					switch(tense) {
					case PRESENT: return new String[] {
							EnglishVerb.SUBJECT[n] + " may be " + e.pap};
					case IMPERFECT: return new String[] {
							EnglishVerb.SUBJECT[n] + " might be " + e.pap};
					case PERFECT: return new String[] {
							EnglishVerb.SUBJECT[n] + " might be " + e.pap};
					case PLUPERFECT: return new String[] {
							EnglishVerb.SUBJECT[n] + " would have been " + e.pap};
					default:
						break;
					}
				}
			case IMPERATIVE:
				switch(voice) {
				case ACTIVE:
					switch(tense) {
					case PRESENT: return new String[] {
						e.fspr + "!"};
					case FUTURE: return new String[] {
						EnglishVerb.SUBJECT[n] + " shall " + e.fspr + "!"};
					default:
						break;
					}
				case PASSIVE:
					switch(tense) {
					case PRESENT: return new String[] {
						"be " + e.pap + "!"};
					case FUTURE: return new String[] {
						EnglishVerb.SUBJECT[n] + " shall be " + e.pap + "!"};
					default:
						break;
					}
				}
			default:
				break;
			}
			return NO_TRANSLATION;
		}

		
	}
	
	public class NonFinite extends Conjugation {
		
		public Mood mood;
		public Verbal verbal;
		public Voice voice;
		public Tense tense;
		
		public NonFinite(Verbal verbal, Voice voice, Tense tense, String value) {
			super(value);
			this.mood = Mood.NON_H_FINITE_FORMS;
			this.verbal = verbal;
			this.voice = voice;
			this.tense = tense;
		}

		@Override
		protected String[] findTranslations() {
			switch (verbal) {
			case INFINITIVES:
				switch (voice) {
				case ACTIVE:
					switch (tense) {
					case PRESENT: return new String[] {
							"to " + e.fspr};
					case PERFECT: return new String[] {
							"to have " + e.pap};
					case FUTURE: return new String[] {
							"to be about to " + e.fspr};
					default:
						break;
					}
				case PASSIVE:
					switch (tense) {
					case PRESENT: return new String[] {
							"to be " + e.pap};
					case PERFECT: return new String[] {
							"to have been " + e.pap};
					case FUTURE: return new String[] {
							"to be about to be " + e.pap};
					default:
						break;
					}
				}
			case PARTICIPLES:
				switch (voice) {
				case ACTIVE:
					switch (tense) {
					case PRESENT: return new String[] {
							e.prp};
					case FUTURE: return new String[] {
							"about to " + e.fspr};
					default:
						break;
					}
				case PASSIVE:
					switch (tense) {
					case PERFECT: return new String[] {
							"having been " + e.pap};
					case FUTURE: return new String[] {
							"about to be " + e.pap};
					default:
						break;
					}
				}
			}
			return NO_TRANSLATION;
		}
		
	}
	
	public class VerbalNoun extends Conjugation {
		
		public Mood mood;
		public VerbalNounType type;
		public VerbalNounCase case_;
		
		public VerbalNoun(VerbalNounType type, VerbalNounCase case_, String value) {
			super(value);
			this.mood = Mood.VERBAL_NOUNS;
			this.type = type;
			this.case_ = case_;
		}

		@Override
		protected String[] findTranslations() {
			switch(type) {
			case GERUND:
				switch(case_) {
				case NOMINATIVE: return new String[] {
						"to " + e.fspr};
				case GENITIVE: return new String[] {
						"of " + e.prp};
				case DATIVE_S_ABLATIVE: return new String[] {
						"for " + e.prp,
						"by " + e.prp};
				case ACCUSATIVE: return new String[] {
						"to " + e.fspr};
				default:
					break;
				}
			case SUPINE:
				switch(case_) {
				case ACCUSATIVE: return new String[] {
						"to " + e.fspr};
				case ABLATIVE: return new String[] {
						"by " + e.prp};
				default:
					break;
				}
			}
			return NO_TRANSLATION;
		}
		
	}
	
}