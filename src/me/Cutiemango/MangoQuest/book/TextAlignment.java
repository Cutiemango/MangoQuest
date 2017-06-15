package me.Cutiemango.MangoQuest.book;

import static java.lang.Character.*;
import java.util.Arrays;
import java.util.List;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

public class TextAlignment
{

	public static final int MAXIUM_CHAR_PER_LINE = 29;
	public static final int MAXIUM_LINE_PER_PAGE = 14;

	public static final double CHARACTER_ALPHABET = 1D;
	public static final double CHARACTER_CHINESE = 2.4D;

	public static final List<UnicodeBlock> CHINESE_UNICODEBLOCK = Arrays.asList(
			UnicodeBlock.GENERAL_PUNCTUATION,
			UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION,
			UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS,
			UnicodeBlock.CJK_COMPATIBILITY_FORMS,
			UnicodeBlock.VERTICAL_FORMS);
	public static final List<Character> ESCAPE_COLOR_CODES = Arrays.asList('k', 'l', 'm', 'n', 'o', 'r');
	public static final List<String> IGNORE_CHARS = Arrays.asList("@", "\\", "#", "§", "&");

	public TextAlignment(String s, int line)
	{
		textToAlign = QuestChatManager.translateColor(s);
		lineUsed = line;
		align();
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
			if (calculateCharSize(textToAlign) > MAXIUM_CHAR_PER_LINE)
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
			if (UnicodeScript.of(codepoint) == UnicodeScript.HAN || CHINESE_UNICODEBLOCK.contains(UnicodeBlock.of(codepoint)))
				size += CHARACTER_CHINESE;
			else
				size += CHARACTER_ALPHABET;
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
			if (UnicodeScript.of(codepoint) == UnicodeScript.HAN || CHINESE_UNICODEBLOCK.contains(UnicodeBlock.of(codepoint)))
				size += CHARACTER_CHINESE;
			else
				size += CHARACTER_ALPHABET;
			if (size >= MAXIUM_CHAR_PER_LINE)
				return index;
			continue;
		}
		return index;
	}

	private String getLastAppliedColor(String s)
	{
		String color = "§";
		if (s.lastIndexOf("§") == -1)
			return "§0";
		if (ESCAPE_COLOR_CODES.contains(s.charAt(s.lastIndexOf("§") + 1)))
			if (s.lastIndexOf("§") - 1 > 0)
				color = "§" + s.charAt(s.lastIndexOf("§") - 1) + "§" + s.charAt(s.lastIndexOf("§") + 1);
			else
				color += s.charAt(s.lastIndexOf("§") + 1);
		else
			color += s.charAt(s.lastIndexOf("§") + 1);
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
}
