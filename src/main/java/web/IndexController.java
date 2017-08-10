package web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import latin.LatinAdjective;
import latin.LatinNoun;
import latin.LatinPronoun;
import latin.LatinVerb;
import supers.PartOfSpeech;
import supers.PartOfSpeech.Form;

//TODO *Get website hosted*
@Controller
public class IndexController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model, HttpSession session) {
		session.setAttribute("oneElement", new String[] {""});
		session.setAttribute("poses", new String[] {"Verb", "Noun", "Adjective", "Pronoun"});
		session.setAttribute("showTable", false);
		session.setAttribute("showCorrect", false);
		return "index";
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST, params = "search")
	public String search(String pos, String query, Model model, HttpSession session) {
		if (pos == null || query == null)
			return "index";
		model.addAttribute("pos", pos);
		model.addAttribute("query", query);
		@SuppressWarnings("rawtypes")
		PartOfSpeech word = null;
		try {
			     if (pos.equals("Verb"))      word = new LatinVerb(query);
			else if (pos.equals("Noun"))      word = new LatinNoun(query);
			else if (pos.equals("Adjective")) word = new LatinAdjective(query);
			else if (pos.equals("Pronoun"))   word = new LatinPronoun(query);
		} catch (Exception e) {}
		session.setAttribute("word", word);
		session.setAttribute("row", new Counter());
		session.setAttribute("col", new Counter());
		session.setAttribute("showTable", true);
		session.setAttribute("showCorrect", false);
		session.setAttribute("settings", new Setting[] {
				new Setting("ignoreAccents", "Ignore accents", true),
				new Setting("justOne", "Only require one translation (if unchecked, all possible translations must be "
						+ "provided, separated by \", \")", true)
		});
		model.addAttribute("checked", true);
		model.addAttribute("checkedJustOne", true);
		model.addAttribute("wrapper", new Wrapper());
		return "index";
	}
	
	private int multiplyLengthsUpTo(Object[][] arr, Object[] stop) { //stop inclusive
		int product = 1;
		for (int i = 0; i < arr.length; i++) {
			product *= arr[i].length;
			if (arr[i] == stop)
				break;
		}
		return product;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST, params = "grade")
	public String grade(Wrapper wrapper, String[] checkedSettings, BindingResult bindingResult, Model model,
			HttpSession session) {
		Setting[] settings = (Setting[]) session.getAttribute("settings");
		for (Setting s : settings) {
			if (checkedSettings != null && Arrays.asList(checkedSettings).contains(s.id))
				s.setChecked(true);
			else
				s.setChecked(false);
		}
		@SuppressWarnings("rawtypes")
		PartOfSpeech word = (PartOfSpeech) session.getAttribute("word");
		Corner[] corners = word.getCorners();
		Answer[][][] input = wrapper.input;
		for (int corNum = 0; corNum < input.length; corNum++)
			for (int row = 0; row < input[corNum].length; row++)
				for (int col = 0; col < input[corNum][row].length; col++) {
					List<String> traits = new ArrayList<String>();
					if (corners[corNum].value != "") traits.add(corners[corNum].value);
					for (String[] left : corners[corNum].lefts)
						traits.add(left[(row / (input[corNum].length / multiplyLengthsUpTo(corners[corNum].lefts, left)))
						                % left.length]);
					if (corners[corNum].sameChildren)
						for (String[] top : corners[corNum].tops)
							traits.add(top[(col / (input[corNum][row].length / multiplyLengthsUpTo(corners[corNum].tops, top)))
							               % top.length]);
					else
						outerloop:
						for (int level = 1, currentCol = 0; level < corners[corNum].tops.length; level++) {
							for (String value : corners[corNum].tops[level]) {
								if (currentCol == col) {
									traits.add(corners[corNum].tops[0][level - 1]);
									traits.add(value);
									break outerloop;
								}
								currentCol++;
							}
						}
					@SuppressWarnings("rawtypes")
					Form f = word.getForm(traits.toArray(new String[] {}));
					input[corNum][row][col].setIgnoreAccents(getSetting(settings, "ignoreAccents").checked);
					input[corNum][row][col].setExpected(f == null ? "" : f.get());
					input[corNum][row][col].setJustOne(getSetting(settings, "justOne").checked);
					input[corNum][row][col].setExpectedTranslations(f == null ? new String[] {} : f.translate());
				}
		session.setAttribute("showCorrect", true);
		return "index";
	}
	
	private Setting getSetting(Setting[] settings, String id) {
		for (Setting s : settings)
			if (s.id.equals(id))
				return s;
		return null;
	}

}
