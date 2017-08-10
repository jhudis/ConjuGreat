package latin;

import supers.PartOfSpeech;

import java.io.IOException;
import java.net.MalformedURLException;

import english.EnglishPartOfSpeechInterface;
import exceptions.PartOfSpeechNotFoundException;

public abstract class LatinPartOfSpeech<G extends LatinPartOfSpeech<G, F, E>.LatinGenerator,
F extends LatinPartOfSpeech<G, F, E>.LatinForm, E extends EnglishPartOfSpeechInterface>
extends PartOfSpeech<G, F, E> implements LatinPartOfSpeechInterface {

	public LatinPartOfSpeech() {}
	
	public LatinPartOfSpeech(String s) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
		super(s);
	}
	
	public abstract class LatinGenerator extends PartOfSpeech<G, F, E>.Generator {
		
		@Override protected String getLangCode() {return LANG_CODE;}
		@Override protected String getLanguage() {return LANGUAGE;}
		
	}
	
	public abstract class LatinForm extends PartOfSpeech<G, F, E>.Form {
		
		public LatinForm(String value) {
			super(value);
		}
		
	}

}
