package me.Cutiemango.MangoQuest;

import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Syntax
{
	// S - Normal String
	// I - Positive Integer (excluding 0)
	// N - NPC's ID
	// W - World
	// D - Positive and negative integer (including 0)
	// B - Boolean

	// The order of customRegex
	private String order;
	// The regex that will be used in matcher
	private String regex;
	private String desc;
	// Dividers that used in the customRegex
	private Set<String> dividers = new HashSet<>();

	private boolean caseInsensitive;

	private Syntax(String s, String d, String... divider) {
		order = s;
		for (String div : divider) {
			if (!div.equals("")) {
				order = order.replaceAll(div, "");
				dividers.add(div);
			}
		}
		s = s.replaceAll("S", "\\\\S+");
		s = s.replaceAll("I", "\\\\d+");
		s = s.replaceAll("N", "-1|\\\\d+");
		s = s.replaceAll("W", "\\\\S+");
		s = s.replaceAll("D", "-?[0-9]\\\\d*");

		if (s.contains("B")) {
			s = s.replaceAll("B", "(true|false)");
			caseInsensitive = true;
		}

		regex = s;
		desc = d;
	}

	public static Syntax of(String customRegex, String description, String... divider) {
		return new Syntax(customRegex, description, divider);
	}

	public boolean matches(Player p, String originalInput) {
		if (originalInput.contains("cancel"))
			return false;
		Pattern pattern = caseInsensitive ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		if (!pattern.matcher(originalInput).matches()) {
			QuestChatManager.syntaxError(p, desc, originalInput);
			return false;
		}
		// The array of User's Input
		ArrayList<String> inputArray = new ArrayList<>();
		String userInput = originalInput;
		if (!dividers.isEmpty()) {
			for (String div : dividers) {
				String[] objs = userInput.split(div);
				for (int i = 0; i < objs.length - 1; i++) {
					inputArray.add(objs[i]);
				}
				// If there are more than one dividers
				userInput = objs[objs.length - 1];
			}
		}

		inputArray.add(userInput);

		if (inputArray.isEmpty() || order.toCharArray().length == 0) {
			QuestChatManager.error(p, "Error");
			return false;
		}

		// Check Object Types (Check NPC)
		for (int i = 0; i < order.toCharArray().length; i++) {
			if (order.toCharArray()[i] == 'N') {
				if (Integer.parseInt(inputArray.get(i)) == -1)
					return true;
				if (Main.getHooker().getNPC(inputArray.get(i)) == null) {
					QuestChatManager.error(p, I18n.locMsg("SyntaxError.NPCNotValid"));
					return false;
				}
			} else if (order.toCharArray()[i] == 'W') {
				if (!QuestValidater.isWorld(inputArray.get(i))) {
					QuestChatManager.error(p, I18n.locMsg("SyntaxError.WorldNotValid"));
					return false;
				}
			}
		}
		return true;
	}
}