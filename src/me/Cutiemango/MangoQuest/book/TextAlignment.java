package me.Cutiemango.MangoQuest.book;

import static java.lang.Character.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

public class TextAlignment
{

	public static final double MAXIUM_CHAR_PER_LINE = 28D;
	public static final double MAXIUM_BOLD_CHAR_PER_LINE = 26D;
	public static final double MAXIUM_LINE_PER_PAGE = 14D;
	
	public static HashMap<UnicodeBlock, Double> CHARACTER_SIZEMAP = new HashMap<>();

	public static final List<Character> ESCAPE_COLOR_CODES = Arrays.asList('k', 'l', 'm', 'n', 'o', 'r');
	public static final List<String> IGNORE_CHARS = Arrays.asList("@", "\\", "#", "§", "&");

	public TextAlignment(String s, int line)
	{
		textToAlign = QuestChatManager.translateColor(s);
		lineUsed = line;
		align();
	}
	
	static
	{
		CHARACTER_SIZEMAP.put(UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS, 1.0D);
		CHARACTER_SIZEMAP.put(UnicodeBlock.BASIC_LATIN, 1.0D);
		CHARACTER_SIZEMAP.put(UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION, 2.25D);
		CHARACTER_SIZEMAP.put(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS, 2.25D);
	}

	private String textToAlign = "";
	private String aligned = "";
	private String left = "";
	private int lineUsed;

	public void align()
	{
		boolean usedup = false;

		if (textToAlign.split("\n").length > 1)
		{
			String[] split = textToAlign.split("\n");
			for (int i = 0; i < split.length - 1; i++)
			{
				if (lineUsed > MAXIUM_LINE_PER_PAGE && !usedup)
					usedup = true;
				if (usedup)
				{
					left += split[i];
					left += "\n";
					continue;
				}
				aligned += split[i];
				aligned += "\n";
				lineUsed+=1;
			}
			if (usedup) return;
			textToAlign = split[split.length - 1];
		}
		
		for (int i = 0; i <= MAXIUM_LINE_PER_PAGE - lineUsed; i++)
		{
			if (usedup)
			{
				left = textToAlign;
				return;
			}

			if (calculateCharSize(textToAlign) > getStandard(textToAlign))
			{
				aligned += textToAlign.substring(0, getSingleLineIndex(textToAlign));
				if (getSingleLineIndex(textToAlign) + 1 <= textToAlign.length())
					textToAlign = textToAlign.substring(getSingleLineIndex(textToAlign));
				else
					continue;
				aligned += "\n";
				lineUsed+=1;
				if (lineUsed > MAXIUM_LINE_PER_PAGE && !usedup)
					usedup = true;
			}
			else
			{
				if (aligned.lastIndexOf("\n") == -1)
					left = getLastAppliedColor(aligned) + textToAlign;
				else
					left = getLastAppliedColor(aligned) + aligned.substring(aligned.lastIndexOf("\n") + 1) + textToAlign;
				return;
			}
		}
		return;
	}

	public double calculateCharSize(String s)
	{
		int stringIndex = -1;
		double size = 0D;
		boolean skipnext = false;
		
		for (int i = 0; i < s.length();)
		{
			stringIndex += 1;
			int codepoint = s.codePointAt(i);
			i += Character.charCount(codepoint);
			if (IGNORE_CHARS.contains(Character.toString(s.charAt(stringIndex))))
			{
				skipnext = true;
				continue;
			}
			if (skipnext)
			{
				skipnext = false;
				continue;
			}
			size += getSize(UnicodeBlock.of(codepoint));
			skipnext = false;
			continue;
		}
		return size;
	}

	private int getSingleLineIndex(String s)
	{
		double size = 0D;
		int index = -1;
		boolean skipnext = false;

		for (int i = 0; i < s.length();)
		{
			index += 1;
			int codepoint = s.codePointAt(i);
			i += Character.charCount(codepoint);
			if (IGNORE_CHARS.contains(Character.toString(s.charAt(index))))
			{
				skipnext = true;
				continue;
			}
			if (skipnext)
			{
				skipnext = false;
				continue;
			}
			size += getSize(UnicodeBlock.of(codepoint));
			if (size >= getStandard(s))
				return index;
			continue;
		}
		return index;
	}
	
	private double getSize(UnicodeBlock block)
	{
		if (CHARACTER_SIZEMAP.containsKey(block))
			return CHARACTER_SIZEMAP.get(block);
		else return 1.0D;
	}

	private String getLastAppliedColor(String s)
	{
		String color = "§0";
		if (s.lastIndexOf("§") == -1)
			return "§0";
		if (ESCAPE_COLOR_CODES.contains(s.charAt(s.lastIndexOf("§") + 1)))
			if (s.lastIndexOf("§") - 1 > 0)
				color = "§" + s.charAt(s.lastIndexOf("§") - 1) + "§" + s.charAt(s.lastIndexOf("§") + 1);
			else
				color = "§" + s.charAt(s.lastIndexOf("§") + 1);
		else
			color = "§" + s.charAt(s.lastIndexOf("§") + 1);
		return color;
	}
	
	public int lineUsed()
	{
		return lineUsed;
	}

	public String getResult()
	{
		return aligned;
	}

	public String getLeft()
	{
		return left;
	}
	
	public static double getStandard(String s)
	{
		if (s.contains("§l"))
			return MAXIUM_BOLD_CHAR_PER_LINE;
		else
			return MAXIUM_CHAR_PER_LINE;
	}
}
