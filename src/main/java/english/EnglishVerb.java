package english;

import supers.Verb;

import java.io.IOException;
import java.net.MalformedURLException;

import english.EnglishVerb.EnglishVerbGenerator;
import exceptions.PartOfSpeechNotFoundException;

public class EnglishVerb extends EnglishPartOfSpeech<EnglishVerbGenerator> implements Verb {

	public String fspr, tspr, prp, spa, pap; //firstPersonSingularSimplePresent, thirdPersonSingularSimplePresent,
									  		 //presentParticiple, simplePast, pastParticiple
	public static final String[]
			SUBJECT = {"I", "you", "he/she/it", "we", "you all", "they"},
			AM = {"am", "are", "is", "are", "are", "are"},
			DO = {"do", "do", "does", "do", "do", "do"},
			WAS = {"was", "were", "was", "were", "were", "were"},
			HAVE = {"have", "have", "has", "have", "have", "have"};
	public String[] spr;
	
	public EnglishVerb() {}
	
	public EnglishVerb(String fspr) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
		super(fspr);
		spr = new String[] {this.fspr, this.fspr, this.tspr, this.fspr, this.fspr, this.fspr};
	}
	
	@Override
	public String toString() {
		return fspr + ", " + tspr + ", " + prp + ", " + spa + ", " + pap;
	}

	public class EnglishVerbGenerator extends EnglishPartOfSpeech<EnglishVerbGenerator>.EnglishGenerator {
		
		@Override protected String getPartOfSpeech() {return PART_OF_SPEECH;}

		@Override
		protected boolean handleIrregulars(String s) {
			// FIXME Auto-generated method stub
			return false;
		}
		
		private String extractFspr() {
			return extract(line);
		}
		
		private String extractTspr() {
			return extractAfterNoSpace(line, "third-person singular simple present");
		}

		private String extractPrp() {
			return extractAfterNoSpace(line, "present participle");
		}
		
		private String extractSpa() {
			return extractAfterNoSpace(line, "simple past");
		}
		
		private String extractPap() {
			return extractAfterNoSpace(line, "past participle");
		}
		
		@Override
		protected void reachTableEndProcess() {
			if (isParts()) {
				fspr = extractFspr();
				tspr = extractTspr();
				prp  = extractPrp ();
				spa  = extractSpa ();
				pap  = extractPap ();
			}
		}
		
	}

}
