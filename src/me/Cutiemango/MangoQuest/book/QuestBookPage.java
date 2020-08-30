package me.Cutiemango.MangoQuest.book;

import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.Pair;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;

public class QuestBookPage
{
	private static final double MAXIMUM_CHAR_PER_LINE = 19D;
	private static final double MAXIMUM_LINE_PER_PAGE = 14D;

	public static HashMap<Character.UnicodeBlock, Pair<Double, Double>> CHARACTER_SIZEMAP = new HashMap<>();

	static
	{
		CHARACTER_SIZEMAP.put(Character.UnicodeBlock.BASIC_LATIN, new Pair<>(1D, 1.2D));
		CHARACTER_SIZEMAP.put(Character.UnicodeBlock.DINGBATS, new Pair<>(1.5D, 1.55D));
		CHARACTER_SIZEMAP.put(Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION, new Pair<>(1.5D, 1.55D));
		CHARACTER_SIZEMAP.put(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS, new Pair<>(1.5D, 1.55D));
		CHARACTER_SIZEMAP.put(Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS, new Pair<>(1.5D, 1.55D));
	}

	public QuestBookPage()
	{
		page = new TextComponent();
		saved = new TextComponent();
	}

	public QuestBookPage(TextComponent text)
	{
		this();
		add(text);
	}

	// used for duplicating
	public QuestBookPage(TextComponent p, TextComponent s, double d, int i)
	{
		page = p;
		saved = s;
		size = d;
		lineUsed = i;
	}

	// the final page shown to the player
	private final TextComponent page;

	// currently saved text, will be added when:
	// - changeLine
	// - the page is going to be shown to the player
	private TextComponent saved;

	private double size = 0d;
	private int lineUsed = 1;

	public QuestBookPage add(String s)
	{
		add(new TextComponent(s));
		return this;
	}

	public QuestBookPage add(TextComponent t)
	{
		if (t.getText().contains("\n"))
		{
			String[] split = t.getText().split("\n");
			for (String s : split)
			{
				add(new TextComponent(s));
				changeLine();
			}
			return this;
		}

//		DebugHandler.log(5, "Adding: %s", t.getText());
		TextComponent sanitized = TextComponentFactory.formatSanitize(t);
//		DebugHandler.log(5, "Sanitized: %s", sanitized.getText());

		StringBuilder builder = new StringBuilder();
		for (char c : sanitized.getText().toCharArray())
		{
			Pair<Double, Double> pair = CHARACTER_SIZEMAP.getOrDefault(Character.UnicodeBlock.of(c), new Pair<>(1d, 1d));
			double charSize = sanitized.isBold() ? pair.getValue() : pair.getKey();
//			DebugHandler.log(5, "Size: %2.2f, charSize: %2.2f", size, charSize);
			if (size + charSize > MAXIMUM_CHAR_PER_LINE)
			{
				TextComponent left = new TextComponent();
				left.setText(builder.toString());
				left.copyFormatting(sanitized);
				saved.addExtra(left);
//				DebugHandler.log(5, "Saved: %s", saved.toLegacyText());

				changeLine();
				saved.copyFormatting(sanitized);
//				DebugHandler.log(5, "Page: %s", page.toLegacyText());
				builder = new StringBuilder();
			}
			builder.append(c);
			size += charSize;
		}

		if (builder.length() != 0)
		{
			if (saved.toPlainText().length() == 0)
			{
				saved.copyFormatting(sanitized);
				saved.setText(builder.toString());
			}
			else
			{
				TextComponent left = new TextComponent();
				left.setText(builder.toString());
				left.copyFormatting(sanitized);
				saved.addExtra(left);
			}
		}

		if (sanitized.getExtra() != null && !sanitized.getExtra().isEmpty())
			for (BaseComponent comp : sanitized.getExtra())
			{
				if (!(comp instanceof TextComponent))
					continue;
				// we need to cancel out parent's formatting
				if (comp.isBoldRaw() == null)
					comp.setBold(false);
				if (comp.isItalicRaw() == null)
					comp.setItalic(false);
				if (comp.isUnderlinedRaw() == null)
					comp.setUnderlined(false);
				if (comp.isStrikethroughRaw() == null)
					comp.setStrikethrough(false);
				comp.copyFormatting(sanitized, ComponentBuilder.FormatRetention.EVENTS, true);
				add((TextComponent)comp);
			}
		return this;
	}

	public QuestBookPage add(InteractiveText it)
	{
		add(it.get());
		return this;
	}

	public void changeLine()
	{
		if (saved.toPlainText().length() != 0)
		{
			page.addExtra(saved);
			saved = new TextComponent();
		}
		size = 0d;
		page.addExtra("\n");
		lineUsed+=1;
//		DebugHandler.log(5, "Line changed.");
	}

	public TextComponent getOriginalPage()
	{
		if (saved.toPlainText().length() != 0)
			page.addExtra(saved);
		return page;
	}
	
	public QuestBookPage duplicate()
	{
		// to support lower versions, duplicate is necessary
		return new QuestBookPage((TextComponent)page.duplicate(), (TextComponent)saved.duplicate(), size, lineUsed);
	}

	public TextComponent getSaved()
	{
		return saved;
	}
	
	public boolean isOutOfBounds()
	{
		return lineUsed >= MAXIMUM_LINE_PER_PAGE;
	}
}