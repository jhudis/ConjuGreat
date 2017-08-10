package supers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import english.EnglishPartOfSpeechInterface;
import exceptions.NotPrimaryFormException;
import exceptions.PartOfSpeechNotFoundException;
import web.Corner;

//TODO Implement handleIrregulars() in Generator
//TODO Implement handleIrregulars() in Form
//TODO Go through a bunch of verbs and fix broken ones (add a blacklist?)
	//TODO Make sure exceptions actually work and are handled correctly
/**
 * In its most fundamental sense, a {@code PartOfSpeech} is a collection of {@link Form}s. It is also associated with
 * a {@link #basicTranslation} and a {@link #fullTranslation}.
 * @param <F> the subclass of {@link Form} the PartOfSpeech is composed of
 */
public abstract class PartOfSpeech<G extends PartOfSpeech<G, F, E>.Generator, F extends PartOfSpeech<G, F, E>.Form,
E extends EnglishPartOfSpeechInterface> implements PartOfSpeechInterface {
	
	protected List<F> forms;
	/**A basic English translation (e.g. for verbs, the English infinitive without "to")*/
	protected String basicTranslation;
	/**An expanded translation (e.g. for puella: "a girl, a lass, a maiden; a female child")*/
	protected String fullTranslation;
	/**The English object to be used for translating*/
	protected E e;
	protected Generator g;
	
	/*public static void main(String[] args) throws Exception {
		LatinVerb v = new LatinVerb("canto");
		System.out.println(v);
		System.out.println("\n\n");
		LatinNoun n = new LatinNoun("puella");
		System.out.println(n);
		System.out.println("\n\n");
		LatinAdjective a = new LatinAdjective("altus");
		System.out.println(a);
		System.out.println("\n\n");
		LatinPronoun p = new LatinPronoun("ipse");
		System.out.println(p);
	}/*
		
	/*public static void main(String[] args) throws Exception {
		LatinVerb v = new LatinVerb("canto");
		Corner[] corners = v.getCorners();
		System.out.println(Arrays.deepToString(corners));
	}*/
	
	public PartOfSpeech() {
		forms = new ArrayList<F>();
		g = generator();
	}
	
	public PartOfSpeech(String part) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
		this();
		g.generate(part);
	}

	/**@return a generator for the {@link PartOfSpeech}*/
	@SuppressWarnings("unchecked")
	protected Generator generator() {
		Type type = PartOfSpeech.this.getClass().getGenericSuperclass();
	    ParameterizedType paramType = (ParameterizedType) type;
	    Type[] a = paramType.getActualTypeArguments();
	    Class<G> c = (Class<G>) a[0];
		try {
			c.getDeclaredConstructors()[0].setAccessible(true);
			return (PartOfSpeech<G, F, E>.Generator) c.getDeclaredConstructors()[0].newInstance(PartOfSpeech.this);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException |
				InvocationTargetException e) {
			return null;
		}
	}
	
	public List<F> getAllForms() {
		return forms;
	}

	public void setAllForms(List<F> forms) {
		this.forms = forms;
	}

	/**@see supers.PartOfSpeech#fullTranslation fullTranslation*/
	public String getFullTranslation() {
		return fullTranslation;
	}

	/**@see supers.PartOfSpeech#fullTranslation fullTranslation*/
	public void setFullTranslation(String translation) {
		this.fullTranslation = translation;
	}
	
	/**@see supers.PartOfSpeech#basicTranslation basicTranslation*/
	public String getBasicTranslation() {
		return basicTranslation;
	}

	/**@see supers.PartOfSpeech#basicTranslation basicTranslation*/
	public void setBasicTranslation(String translation) {
		this.basicTranslation = translation;
	}
	
	/**@see Form#translate(EnglishPartOfSpeechInterface)*/
	public String[] translate(F f) {
		return f.translate();
	}
	
	private Field[] getNonStaticFields(Object t) {
		Field[] declaredFields = t.getClass().getFields();
		List<Field> nonStaticFieldsList = new ArrayList<Field>();
		for (Field field : declaredFields)
		    if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()))
		        nonStaticFieldsList.add(field);
		Field[] nonStaticFields = nonStaticFieldsList.toArray(new Field[] {});
		return nonStaticFields;
	}
	
	/**
	 * Returns a list of forms which have the given "traits" (Enum values).
	 * Follows the order of {@link Class#getFields()}.
	 * If a given value is null, any corresponding value will be accepted.
	 * @param traits desired traits of a form
	 * @return the forms which have those traits
	 */
	public List<F> getType(Enum<?>... traits) {
		List<F> matches = new ArrayList<F>();
		boolean match;
		for (F t : forms) {
			Field[] nonStaticFields = getNonStaticFields(t);
			if (traits.length == nonStaticFields.length) {
				match = true;
				for (int i = 0; i < traits.length; i++) {
					try {
						if (traits[i] != null && nonStaticFields[i].get(t) != traits[i])
							match = false;
					} catch (Exception e) {}
				}
				if (match) {
					matches.add(t);
				}
			}
		}
		return matches;
	}
	
	public List<F> getType(boolean sameSize, Enum<?>... traits) {
		if (sameSize)
			return getType(traits);
		List<F> matches = new ArrayList<F>();
		boolean match;
		for (F t : forms) {
			Field[] nonStaticFields = getNonStaticFields(t);
			match = true;
			for (int i = 0; i < traits.length; i++) {
				try {
					if (traits[i] != null && nonStaticFields[i].get(t) != traits[i])
						match = false;
				} catch (Exception e) {}
			}
			if (match) {
				matches.add(t);
			}
		}
		return matches;
	}
	
	/**
	 * Performs the same task as {@link PartOfSpeech#getType(Enum...)} but takes string representations of the traits
	 * @see PartOfSpeech#equals(Enum)
	 */
	public List<F> getType(String... traits) {
		List<F> matches = new ArrayList<F>();
		boolean match;
		for (F t : forms) {
			Field[] nonStaticFields = getNonStaticFields(t);
			if (traits.length == nonStaticFields.length) {
				match = true;
				for (int i = 0; i < traits.length; i++) {
					try {
						if (traits[i] != null && !equals((Enum<?>) nonStaticFields[i].get(t), traits[i]))
							match = false;
					} catch (Exception e) {}
				}
				if (match) {
					matches.add(t);
				}
			}
		}
		return matches;
	}
	
	/**
	 * Returns a single form having the given traits, null if no acceptable forms exist, or first form if more than one exist
	 * @param traits desired traits of a form
	 * @return the form which has those traits
	 * @see PartOfSpeech#getType(Enum...)
	 */
	public F getForm(Enum<?>... traits) {
		List<F> t = getType(traits);
		if (t == null || t.size() == 0)
			return null;
		return t.get(0);
	}
	
	public F getForm(boolean sameSize, Enum<?>... traits) {
		if (sameSize)
			return getForm(traits);
		List<F> t = getType(sameSize, traits);
		if (t == null || t.size() == 0)
			return null;
		return t.get(0);
	}
	
	/**
	 * Performs the same task as {@link PartOfSpeech#getForm(Enum...)} but takes string representations of the traits
	 * @see PartOfSpeech#equals(Enum)
	 */
	public F getForm(String... traits) {
		List<F> t = getType(traits);
		if (t == null || t.size() == 0)
			return null;
		return t.get(0);
	}
	
	protected String formsToString() {
		return String.join("\n", forms.stream().map(F::toString).collect(Collectors.toList()));
	}
	
	@Deprecated
	public String toStringHeader() { return null; }
	
	@Override
	public abstract String toString();
	
	/**
	 * Replacing the characters "-", " ", and "/" with underscores, determines whether the name of a given enum is
	 * equal to a given string
	 * @param e an enum
	 * @param s a String
	 * @return whether the two are equal
	 */
	public static boolean equals(Enum<?> e, String s) {
		return s.equals(toString(e));
	}
	
	/**
	 * Returns an enum of a given class which equals the given string, according to
	 * {@link PartOfSpeech#equals(Enum, String)}.
	 * If none are equal, will return null.
	 * <br>Example: {@code get(Tense.class, "present")} returns {@code Tense.PRESENT}
	 * @param e an enum class
	 * @param s a String
	 * @return the matching enum
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T get(Class<T> e, String s) {
		for (Field f : e.getFields())
			try {
				if (equals((Enum<T>) f.get(e), s))
					return (T) f.get(e);
			} catch (IllegalArgumentException | IllegalAccessException e1) {}
		return null;
	}
	
	public static Enum<?> get(Class<?> e, String s, int useless) {
		for (Field f : e.getFields())
			try {
				if (equals((Enum<?>) f.get(e), s))
					return (Enum<?>) f.get(e);
			} catch (IllegalArgumentException | IllegalAccessException e1) {}
		return null;
	}
	
	/**
	 * Returns a string representation of a given enum which is the lowercase name of the enum with underscores
	 * replaced by spaces
	 * @param e an enum
	 * @return the string representation
	 */
	public static String toString(Enum<?> e) {
		return e == null ? null : e.name().replaceAll("_S_", "/").replaceAll("_H_", "-").replaceAll("_", " ").toLowerCase();
	}
	
	/*public static <T> T[] subArray(T[] arr, int start, int end) {
		return Arrays.asList(arr).subList(start, end).toArray(arr);
	}*/
	
	public Corner[] getCorners() {
		Form d = forms.get(0);
		Field[] dnsf = getNonStaticFields(d); //default non-static fields
		if (dnsf.length < 4) { //no corners, just make one fake ("") one
			int cutoff = dnsf.length / 2; //start index of tops
			Field[] leftFields = Arrays.copyOfRange(dnsf, 0, cutoff);
			Field[]  topFields = Arrays.copyOfRange(dnsf, cutoff, dnsf.length);
			/*Field temp = leftFields[leftFields.length - 1];
			leftFields[leftFields.length - 1] = topFields[topFields.length - 1];
			topFields[topFields.length - 1] = temp;*/
			String[][] lefts = new String[leftFields.length][];
			String[][]  tops = new String[ topFields.length][];
			for (int i = 0; i < lefts.length; i++) {
				Enum<?> leftEnum = null;
				try { leftEnum = (Enum<?>) leftFields[i].get(d); } catch (Exception e) {}
				Enum<?>[] leftValues = leftEnum.getDeclaringClass().getEnumConstants();
				lefts[i] = new String[leftValues.length];
				for (int j = 0; j < leftValues.length; j++)
					lefts[i][j] = toString(leftValues[j]);
			}
			for (int i = 0; i < tops.length; i++) {
				Enum<?> topEnum = null;
				try { topEnum = (Enum<?>) topFields[i].get(d); } catch (Exception e) {}
				Enum<?>[] topValues = topEnum.getDeclaringClass().getEnumConstants();
				tops[i] = new String[topValues.length];
				for (int j = 0; j <  topValues.length; j++)
					tops[i][j] = toString( topValues[j]);
			}
			return new Corner[] {new Corner("", tops, lefts)};
		} else { //multiple corners
			Field cornerField = dnsf[0];
			Enum<?> cornerEnum = null;
			try { cornerEnum = (Enum<?>) cornerField.get(d); } catch (Exception e) {}
			Enum<?>[] cornerValues = cornerEnum.getDeclaringClass().getEnumConstants();
			Corner[] corners = new Corner[cornerValues.length];
			for (int i = 0; i < cornerValues.length; i++) {
				boolean sameChildren = true;
				Enum<?> c = cornerValues[i];
				Form f = getForm(false, c);
				Field[] nsf = getNonStaticFields(f); //non-static fields for this corner
				int cutoff = nsf.length > 3 ? ((nsf.length - 1) / 2 + 1) : 1;
				Field[] leftFields = Arrays.copyOfRange(nsf, 1, cutoff);
				Field[]  topFields = Arrays.copyOfRange(nsf, cutoff, nsf.length);
				String[][] lefts = new String[leftFields.length][];
				String[][]  tops = new String[ topFields.length][];
				for (int j = 0; j < lefts.length; j++) {
					Enum<?> leftEnum = null;
					try { leftEnum = (Enum<?>) leftFields[j].get(f); } catch (Exception ex) {}
					Enum<?>[] leftValues = leftEnum.getDeclaringClass().getEnumConstants();
					lefts[j] = new String[leftValues.length];
					List<String> leftsList = new ArrayList<String>();
					for (Enum<?> en : leftValues)
						if (getForm(false, nullButEnds(c, j + 2, en)) != null)
							leftsList.add(toString(en));
					lefts[j] = leftsList.toArray(new String[] {});
				}
				for (int j = 0; j < topFields.length; j++) {
					Enum<?> topEnum = null;
					try { topEnum = (Enum<?>) topFields[j].get(f); } catch (Exception ex) {}
					Enum<?>[] topValues = topEnum.getDeclaringClass().getEnumConstants();
					tops[j] = new String[topValues.length];
					if (nsf.length > 4 || j == 0) {
						for (int k = 0; k < topValues.length; k++)
							tops[j][k] = toString(topValues[k]);
					} else { //gross verbal-noun-ish stuff
						@SuppressWarnings("unchecked")
						ArrayList<String>[] topsList = (ArrayList<String>[]) new ArrayList[tops[j - 1].length];
						for (int k = 0; k < topsList.length; k++) topsList[k] = new ArrayList<String>();
						for (int k = 0; k < tops[j - 1].length; k++) {
							for (Enum<?> en : topValues) {
								Enum<?> prevTopEnum = null;
								try { prevTopEnum = (Enum<?>) topFields[j - 1].get(f); } catch (Exception ex) {}
								Class<?> prevTopClass = prevTopEnum.getDeclaringClass();
								if (getForm(false, nullButEnds(c,2+lefts.length+j,get(prevTopClass,tops[j-1][k],0),en)) != null)
									topsList[k].add(toString(en));
							}
						}
						for (List<String> topList : topsList) {
							boolean added = false;
							for (int k = 0; k < tops.length; k++)
								if (tops[k] == null || tops[k][0] == null) {
									tops[k] = topList.toArray(new String[] {});
									added = true;
								}
							if (!added) {
								List<String[]> topsAsList = new ArrayList<String[]>(Arrays.asList(tops));
								topsAsList.add(topList.toArray(new String[] {}));
								tops = topsAsList.toArray(new String[][] {});
							}
						}
						if (Arrays.equals(tops[tops.length - 1], tops[tops.length - 2]))
							tops = Arrays.copyOfRange(tops, 0, tops.length - 1);
						else
							sameChildren = false;
					}
				}
				corners[i] = new Corner(toString(c), tops, lefts, sameChildren);
			}
			return corners;
		}
	}
	
	protected static Enum<?>[] nullButEnds(Enum<?> en, int elements, Enum<?>... ens) {
		Enum<?>[] result = new Enum<?>[elements];
		result[0] = en;
		for (int i = 0; i < ens.length; i++)
			result[elements - ens.length + i] = ens[i];
		return result;
	}
	
	/**
	 * Does the behind-the-scenes work extracting data from Wiktionary and constructing a {@link PartOfSpeech}.
	 */
	public abstract class Generator {
		
		protected BufferedReader site;
		protected String line;
		protected int nulls;
		protected static final int MAX_NULLS = 10;
		/**An integer keeping track of the position of the current form within the table*/
		protected int index;
		
		public Generator() {
			index = 0;
		}
		
		/**
		 * Finds the data between the innermost tags, assuming there is only one piece of data
		 * @param htmlLine a line of HTML code
		 * @return the extracted data
		 */
		protected String extract(String htmlLine) {
			for (int i = 0; i < htmlLine.length() - 1; i++)
				if (htmlLine.charAt(i) == '>' && htmlLine.charAt(i + 1) != '<')
					return htmlLine.substring(i + 1, htmlLine.indexOf("<", i + 1));
			return null;
		}
		
		/**
		 * Finds nth instance of data between tags
		 * @param htmlLine a line of HTML code
		 * @param n the instance of data to seek
		 * @return the extracted data
		 */
		protected String extract(String htmlLine, int n) {
			String extracted = extract(htmlLine);
			int extractedIndex = htmlLine.indexOf(extracted + "<");
			for (int i = 0; i < n - 1; i++) {
				extracted = extract(htmlLine.substring(extractedIndex + extracted.length()));
				extractedIndex = htmlLine.indexOf(extracted + "<", extractedIndex);
				if (extractedIndex == -1) return null;
			}
			return extracted;
		}
		
		/**
		 * Finds data between tags after a given String
		 * @param htmlLine a line of HTML code
		 * @param after the String to look after for data
		 * @return the extracted data
		 */
		protected String extractAfter(String htmlLine, String after) {
			return extract(htmlLine.substring(htmlLine.indexOf(after)));
		}
		
		/**
		 * Finds data (a space doesn't count) between tags after a given String
		 * @param htmlLine a line of HTML code
		 * @param after the String to look after for data
		 * @return the extracted data
		 */
		protected String extractAfterNoSpace(String htmlLine, String after) {
			String result = " ";
			for (int n = 1; result.equals(" "); n++)
				result = extract(htmlLine.substring(htmlLine.indexOf(after)), n);
			return result;
		}
		
		/**@return the given string with parentheticals removed*/
		protected String removeParentheticals(String s) {
			while (s.contains("(") && s.contains(")"))
				s = s.substring(0, s.indexOf("(")) + s.substring(s.indexOf(")") + 1);
			return s.trim().replace("  ", " ");
		}
		
		/**
		 * Removes the accents in a given String
		 * @param s the String to remove accents from
		 * @return the String without accents
		 */
		protected String removeAccents(String s) {
			return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		}
		
		/**
		 * Returns a URL which points to the English Wiktionary page for a given part
		 * @param form a form of a part of speech
		 * @return the URL
		 * @throws MalformedURLException
		 */
		protected URL URL(String form) throws MalformedURLException {
			return URL(form.replaceAll(" ", "_"), "en");
		}
		
		/**
		 * Returns a URL which points to the Wiktionary in the language of a given code page for a given part
		 * @param form a form of a part of speech
		 * @param langCode the code Wiktionary associates with a language that goes at the beginning of the URL
		 * @return the URL
		 * @throws MalformedURLException
		 */
		protected URL URL(String form, String langCode) throws MalformedURLException {
			return new URL("https://" + langCode + ".wiktionary.org/wiki/" + form.replaceAll(" ", "_"));
		}
		
		/**
		 * Returns a BufferedReader which reads the English Wiktionary page for a given part
		 * @param form a form of a part of speech
		 * @return the BufferedReader
		 * @throws MalformedURLException
		 */
		protected BufferedReader site(String form) throws MalformedURLException, IOException {
			return new BufferedReader(new InputStreamReader((URL(form)).openStream()));
		}
		
		/**
		 * Returns a BufferedReader which reads the Wiktionary in the language of a given code page for a given part
		 * @param form a form of a part of speech
		 * @param langCode the code Wiktionary associates with a language that goes at the beginning of the URL
		 * @return the BufferedReader
		 * @throws MalformedURLException
		 */
		protected BufferedReader site(String form, String langCode) throws MalformedURLException, IOException {
			return new BufferedReader(new InputStreamReader((URL(form, langCode)).openStream()));
		}
		
		/**
		 * Returns a BufferedReader which reads from the given URL
		 * @param url the URL of the page to be read from
		 * @return the BufferedReader
		 * @throws MalformedURLException
		 */
		protected BufferedReader site(URL url) throws IOException {
			return new BufferedReader(new InputStreamReader(url.openStream()));
		}
		
		/**Returns the two letters used at the beginning of a Wiktionary URL for a given language (e.g. "en", "la")*/
		protected abstract String getLangCode();
		/**Returns the name of the language (e.g. "English", "Latin")*/
		protected abstract String getLanguage();
		/**Returns the name of the part of speech (e.g. "Noun", "Verb")*/
		protected abstract String getPartOfSpeech();
		
		/**Returns the string that indicates the start of the section on the Wiki page for this language*/
		protected String getLanguageStartIdentifier() {
			return "class=\"mw-headline\" id=\"" + getLanguage() + "\"";
		}
		/**Returns the string that indicates the start of the section (within the section on the Wiki page for this
		 * language) for this part of speech*/
		protected String getPartOfSpeechStartIdentifier() {
			return "><span class=\"mw-headline\" id=\"" + getPartOfSpeech();
		}
		/**Returns the string that indicates the start of the table containing the forms*/
		protected String getTableStartIdentifier() {return "<table ";};
		/**Returns the string that indicates the end of the table containing the forms*/
		protected String getTableEndIdentifier() {return "</table>";}
		
		/**
		 * Generates the PartOfSpeech if it is irregular
		 * @param s the primary form of the PartOfSpeech
		 * @return true if it got generated and therefore was irregular, false otherwise
		 */
		protected abstract boolean handleIrregulars(String s);
		
		/**
		 * Gets to the point in the Wiktionary page where the section for the language starts
		 * @throws IOException
		 * @throws PartOfSpeechNotFoundException
		 */
		protected void reachLangStart() throws IOException, PartOfSpeechNotFoundException {
			for (line = site.readLine(); (line == null || !line.contains(getLanguageStartIdentifier()));
					line = site.readLine()) {
				if (line != null) {
					nulls = 0;
					reachLangStartProcess();
				} else nulls++;
				if (nulls > MAX_NULLS)
					throw new PartOfSpeechNotFoundException();
			}
		}
		
		/**
		 * What happens while reaching the section where the language starts
		 * @see Generator#reachLangStart()
		 */
		protected abstract void reachLangStartProcess();
		
		/**
		 * Gets to the point in the Wiktionary page where the section for the part of speech starts
		 * @throws IOException
		 * @throws PartOfSpeechNotFoundException
		 * @throws NotPrimaryFormException 
		 */
		protected void reachPOSStart() throws IOException, PartOfSpeechNotFoundException, NotPrimaryFormException {
			for (line = site.readLine(); (line == null || !line.contains(getPartOfSpeechStartIdentifier()));
					line = site.readLine()) {
				if (line != null) {
					nulls = 0;
					reachPOSStartProcess();
				} else nulls++;
				if (nulls > MAX_NULLS) throw new PartOfSpeechNotFoundException();
			}
		}
		
		/**
		 * What happens while reaching the section where the part of speech starts
		 * @throws NotPrimaryFormException 
		 * @see Generator#reachPOSStart()
		 */
		protected abstract void reachPOSStartProcess() throws NotPrimaryFormException;
		
		/**
		 * Gets to the point in the Wiktionary page where the table containing the data for the part of speech is
		 * @throws IOException
		 * @throws PartOfSpeechNotFoundException
		 */
		protected void reachTableStart() throws NotPrimaryFormException, IOException, PartOfSpeechNotFoundException {
			for (line = line.substring(0); (line == null || !line.contains(getTableStartIdentifier()));
					line = site.readLine()) {
				if (line != null) {
					nulls = 0;
					reachTableStartProcess();
				} else nulls++;
				if (nulls > MAX_NULLS) throw new PartOfSpeechNotFoundException();
			}
		}
		
		/**
		 * What happens while reaching the section where the table containing the data for the part of speech is
		 * @see Generator#reachTableStart()
		 */
		protected abstract void reachTableStartProcess() throws NotPrimaryFormException;
		
		/**
		 * Returns whether the current line holds the translation ({@link PartOfSpeech#fullTranslation})
		 * @return whether the current line holds the translation
		 */
		protected abstract boolean isTranslation();
		
		/**Extracts the full translation from the current line*/
		protected String extractFullTranslation() {
			String translation = "";
			String l = line + "<>";
			for (int i = 1; extract(l, i) != null; i++) {
				translation += extract(l, i);
			}
			if (translation.charAt(translation.length() - 1) == '.')
				translation = translation.substring(0, translation.length() - 1);
			return removeParentheticals(translation);
		}
		
		/**Extracts a basic translation to be used as a backup when actually translating.*/
		protected abstract String extractBasicTranslation();
		
		/**
		 * Constructs a an English object given its first-person singular simple present form
		 * @param fspr the first-person singular simple present form
		 * @return the English object
		 */
		protected EnglishPartOfSpeechInterface constructEnglish(String fspr) throws PartOfSpeechNotFoundException {
			Type type = PartOfSpeech.this.getClass().getGenericSuperclass();
		    ParameterizedType paramType = (ParameterizedType) type;
		    Type[] a = paramType.getActualTypeArguments();
		    @SuppressWarnings("unchecked")
			Class<E> c = (Class<E>) a[2];
			try {
				return c.getDeclaredConstructor(String.class).newInstance(fspr);
			} catch (InstantiationException    | IllegalAccessException | IllegalArgumentException
				   | InvocationTargetException | NoSuchMethodException  | SecurityException e) {
				return null;
			}
		}
		
		/**@return an a corresponding {@link EnglishPartOfSpeechInterface}
		 * @throws PartOfSpeechNotFoundException*/
		protected EnglishPartOfSpeechInterface english() throws MalformedURLException, IOException,
		PartOfSpeechNotFoundException {
			try {
				site = site(removeAccents(forms.get(0).get()), getLangCode());
				for (line = site.readLine(); (line == null ||
						!(line.contains("<span lang=\"en\" class=\"lang-en\" dir=\"ltr\"")
						||(line.contains("<li>") && line.contains(": <span class=\"Latn\" lang=\"en\""))
						||line.contains("<span lang=\"en\" xml:lang=\"en\">")));
						line = site.readLine()) {
					if (line == null) nulls++; else nulls = 0;
					if (nulls > MAX_NULLS) throw new PartOfSpeechNotFoundException();
				}
				return constructEnglish(extractAfter(line, "href"));
			} catch (PartOfSpeechNotFoundException e) {
				return constructEnglish(basicTranslation);
			}
		}
		
		/**
		 * Gets to the point in the Wiktionary page where the table containing the data for the part of speech ends
		 * @throws IOException
		 * @throws PartOfSpeechNotFoundException
		 */
		protected void reachTableEnd() throws IOException, PartOfSpeechNotFoundException {
			for (line = site.readLine(); (line == null || !line.contains(getTableEndIdentifier()));
					line = site.readLine()) {
				if (line != null) {
					nulls = 0;
					reachTableEndProcess();
				} else nulls++;
				if (nulls > MAX_NULLS) throw new PartOfSpeechNotFoundException();
			}
		}
		
		/**
		 * Returns whether the current line holds a form
		 * @return whether the current line holds a form
		 */
		protected boolean isForm() {
			return line.contains("<td") && line.contains("><span class=\"Latn\" lang=\"" + getLangCode() + "\"");
		}
		
		/**
		 * Extracts from the current line a standard form
		 * @throws PartOfSpeechNotFoundException 
		 */
		protected abstract F extractForm() throws PartOfSpeechNotFoundException;
		
		/**
		 * What happens while reaching the section where the table containing the data for the part of speech ends
		 * @throws PartOfSpeechNotFoundException 
		 * @see Generator#reachTableEnd()
		 */
		protected void reachTableEndProcess() throws PartOfSpeechNotFoundException {
			if (isForm()) {
				forms.add(extractForm());
				index++;
			}
		}
		
		/**
		 * Constructs a {@link PartOfSpeech} given a form of it
		 * @param form a form of the part of speech
		 * @throws MalformedURLException
		 * @throws IOException
		 * @throws PartOfSpeechNotFoundException
		 */
		@SuppressWarnings("unchecked")
		public void generate(String form) throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
			if (handleIrregulars(form)) return;
			site = site(form);
			line = "";
			reachLangStart();
			try {
				reachPOSStart();
				reachTableStart();
				reachTableEnd();
				site.close();
				if (forms.size() < 1 || Arrays.asList(this.getClass().getFields()).contains(null))
					throw new PartOfSpeechNotFoundException();
			} catch (NotPrimaryFormException e) {
				generate(e.getMessage());
			}
			e = (E) english();
			if (e == null || Arrays.asList(e.getClass().getFields()).contains(null))
				throw new PartOfSpeechNotFoundException();
			forms.sort(null);
		}
		
		/**
		 * Generates a random part of speech from a list at a given URL which adheres to given specifications
		 * @param url the string URL of the list
		 * @param goodPres the first characters of the form must be one of these (ignored if null)
		 * @param badPres the first characters of the form cannot be one of these (ignored if null)
		 * @param goodSufs the last characters of the form must be one of these (ignored if null)
		 * @param badSufs the last characters of the form cannot be one of these (ignored if null)
		 * @param goodCtns the form must contain one of these (ignored if null)
		 * @param badCtns the form cannot contain one of these (ignored if null)
		 * @param allowCaps whether or not to include words which start with a capital letter
		 * @param maxLength the maximum length of the part of speech that gets generated
		 * @return the primary form of a random part of speech from the list at a given URL which adheres to the
		 * given specifications
		 * @throws MalformedURLException
		 * @throws IOException
		 * @throws PartOfSpeechNotFoundException
		 */
		public void random(String url, String[] goodPres, String[] badPres, String[] goodSufs, String[] badSufs,
				String[] goodCtns, String[] badCtns, boolean allowCaps, int maxLength)
				throws MalformedURLException, IOException, PartOfSpeechNotFoundException {
			List<String> forms = new ArrayList<String>();
			String pagefrom;
			boolean formsFound = false, formGenerated = false;
			while(!formGenerated) {
			try { while(!formsFound) {
			
			pagefrom = "";
			for (int i = 0; i < 3; i++)
				pagefrom += Character.toString("abcdefghijklmnopqrstuvwxyz".charAt((new Random()).nextInt(26)));
			site = site(new URL(url.replace("/wiki/", "/w/index.php?title=") + "&pagefrom=" + pagefrom));
			for (line = site.readLine(); (line == null || !line.contains("next page")); line = site.readLine()) {}
			boolean validPre, validSuf, validCtn;
			String form;
			for (line = site.readLine(); true; line = site.readLine()) {
				if (line != null) {
					form = extract(line);
					validPre = false;
					if (goodPres != null) {
						for (String goodPre : goodPres) {
							if (form.substring(0, goodPre.length()).equals(goodPre)) {
								validPre = true;
								break;
							}
						}
					} else {
						validPre = true;
					}
					if (badPres != null && validPre) {
						for (String badPre : badPres) {
							if (form.substring(0, badPre.length()).equals(badPre)) {
								validPre = false;
								break;
							}
						}
					}
					validSuf = false;
					if (goodSufs != null) {
						for (String goodSuf : goodSufs) {
							if (form.substring(form.length() - goodSuf.length()).equals(goodSuf)) {
								validSuf = true;
								break;
							}
						}
					} else {
						validSuf = true;
					}
					if (badSufs != null && validSuf) {
						for (String badSuf : badSufs) {
							if (form.substring(form.length() - badSuf.length()).equals(badSuf)) {
								validSuf = false;
								break;
							}
						}
					}
					validCtn = false;
					if (goodCtns != null) {
						for (String goodCtn : goodCtns) {
							if (form.contains(goodCtn)) {
								validCtn = true;
								break;
							}
						}
					} else {
						validCtn = true;
					}
					if (badCtns != null && validCtn) {
						for (String badCtn : badCtns) {
							if (form.contains(badCtn)) {
								validCtn = false;
								break;
							}
						}
					}
					if (validPre && validSuf && validCtn && (Character.isUpperCase(form.charAt(0)) == allowCaps)
							&& form.length() <= maxLength)
						forms.add(form);
				}
				if (line != null && line.contains("next page")) {
					if (forms.size() > 0)
						formsFound = true;
					break;	
				}
			}
			
			}} catch (Exception e) {}
			String form;
			int start = (new Random()).nextInt(forms.size());
			for (int i = start; i != start - 1; i = (i + 1) % forms.size()) {
				try {
					form = forms.get(i);
					PartOfSpeech.this.forms = new ArrayList<F>();
					fullTranslation = null;
					generator().generate(form);
					formGenerated = true;
					break;
				} catch (FileNotFoundException | PartOfSpeechNotFoundException e) {}
			}
			}
		}
		
	}
	
	/**Since Enums can't be created within inner classes, most <code>Form</code>s will implement a sub-interface
	 * of this*/
	public interface FormInterface {
		String[] NO_TRANSLATION = {};
	}

	/**
	 * Gramatically, a {@code Form} is a word with a particular inflectional ending or other modification
	 */
	public abstract class Form implements Comparable<Form>, FormInterface {
		
		/**The form itself, rather than any of its characteristics**/
		private String value;
		/**Index used for translating*/
		protected int n;
		
		public Form(String value) {
			this.value = value;
		}
		
		public Form() {
			this(null);
		}
		
		/**
		 * @return {@link Form#value}
		 */
		public String get() {
			return value;
		}
		
		/**
		 * @see {@link Form#value}
		 */
		public void set(String value) {
			this.value = value;
		}
		
		private boolean deepContains(String[] a, String o) {
			for (String s : a)
				if (s.contains(o))
					return true;
			return false;
		}
		
		private String firstContains(String[] a, String o) {
			for (String s : a)
				if (s.contains(o))
					return s;
			return null;
		}
		
		/**
		 * Returns a list of translations in which the given ones are expanded by slash-separated values.<br>
		 * E.g. ["he/she/it runs"] --> ["he runs", "she runs", "it runs"]<br>
		 * If necessary, multiple-word options should be underscore-separated.
		 * @param translations the original translations
		 * @return the processed translations
		 */
		protected String[] expand(String[] translations) {
			if (translations == null) return null;
			if (deepContains(translations, "/")) {
				List<String> transList = new ArrayList<String>();
				for (String translation : translations) {
					if (translation.contains("/")) {
						String variable = firstContains(translation.split(" "), "/");
						String[] options = variable.split("/");
						for (String option : options)
							transList.add(translation.replace(variable, option).replaceAll("_", " "));
					}
					else transList.add(translation);
				}
				translations = expand(transList.toArray(new String[] {}));
			}
			return translations;
		}
		
		/**
		 * Generates the Form if it is irregular
		 * @param s the primary form of the PartOfSpeech this Form belongs to
		 * @return the array of translations if it got translated and therefore was irregular, null otherwise
		 * @see #findTranslations()
		 */
		protected abstract String[] handleIrregulars(String s);
		
		/**@return all valid translations of this*/
		protected abstract String[] findTranslations();
		
		/**
		 * Returns an array of all possible English translations of this
		 * @return an array of all possible English translations of this
		 */
		public String[] translate() {
			String[] translations = handleIrregulars(PartOfSpeech.this.forms.get(0).get());
			if (translations != null) return expand(translations);
			return (expand(findTranslations()));
		}
		
		@SuppressWarnings({ "unchecked" })
		@Override
		public int compareTo(supers.PartOfSpeech<G, F, E>.Form o) {
			if (this.getClass() != o.getClass())
				return 0;
			for (Field f : this.getClass().getFields())
				try {
					if (f.get(this) != null && ((Comparable<Object>) f.get(this)).compareTo(f.get(o)) != 0)
						return ((Comparable<Object>) f.get(this)).compareTo(f.get(o));
				} catch (IllegalArgumentException | IllegalAccessException e) {}
			return 0;
		}
		
		@Override
		public String toString() {
			String s = "";
			Field[] fields = this.getClass().getFields();
			for (Field f : fields) {
				try {
				s += ", " + f.getName().replaceAll("_", "") + " - " + PartOfSpeech.toString((Enum<?>) f.get(this));
				} catch (Exception e) {}
			}
			return value + " " + Arrays.toString(translate()).replace("[", "(").replace("]", ")")
					+ ": " + s.substring(2);
		}
		
	}
	
}
