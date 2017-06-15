package me.Cutiemango.MangoQuest.book;

import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestBookPage
{

	public QuestBookPage()
	{
		page = new TextComponent("");
		textleft = new TextComponent("");
	}
	
	public QuestBookPage(TextComponent text)
	{
		page = text;
		textleft = new TextComponent("");
	}

	private TextComponent page;
	private TextComponent textleft;
	private int lineUsed = 1;
	
	public void endNormally()
	{
		if (textleft.toPlainText() != "")
		{
			page.addExtra(textleft);
			textleft = new TextComponent("");
		}
	}

	public void changeLine()
	{
		endNormally();
		page.addExtra("\n");
		lineUsed+=1;
	}

	public QuestBookPage add(String s)
	{
		if (s == "\n")
		{
			changeLine();
			return this;
		}
		s = textleft.toPlainText() + s;
		TextAlignment align = new TextAlignment(s, lineUsed);
		if (align.calculateCharSize(s) >= TextAlignment.MAXIUM_CHAR_PER_LINE)
			page.addExtra(align.getResult());
		textleft = new TextComponent(QuestChatManager.translateColor(align.getLeft()));
		lineUsed = align.lineUsed();
		return this;
	}

	public QuestBookPage add(TextComponent t)
	{		
		t.addExtra(textleft);
		String s = t.toPlainText();
		TextAlignment align = new TextAlignment(s, lineUsed);
		if (align.calculateCharSize(s) >= TextAlignment.MAXIUM_CHAR_PER_LINE)
			page.addExtra(align.getResult());
		textleft = new TextComponent(QuestChatManager.translateColor(align.getLeft()));
		lineUsed = align.lineUsed();
		return this;
	}

	public QuestBookPage add(InteractiveText it)
	{
		String s = it.get().toPlainText();
		s = textleft.toPlainText() + "@" + s + "#";
		TextAlignment align = new TextAlignment(s, lineUsed);
		if (align.calculateCharSize(s) >= TextAlignment.MAXIUM_CHAR_PER_LINE)
		{
			if (align.getResult().contains("@"))
			{
				String[] firstsplit = align.getResult().split("@");
				page.addExtra(firstsplit[0]);
				if (firstsplit[1].contains("#"))
				{
					String[] secondsplit = firstsplit[1].split("#");
					page.addExtra(it.toggleAlignText(secondsplit[0]));
					if (secondsplit.length > 1)
						page.addExtra(secondsplit[1]);
				}
				else
					page.addExtra(it.toggleAlignText(firstsplit[1]));
			}
			else
				page.addExtra(align.getResult());
		}
		if (align.getLeft().contains("@"))
		{
			if (align.getLeft().startsWith("@") && align.getLeft().contains("#"))
			{
				String[] split = align.getLeft().split("#");
				textleft.addExtra(it.toggleAlignText(split[0]));
				if (split.length > 1)
					textleft.addExtra(split[1]);
			}
			else
			{
				String[] firstsplit = align.getLeft().split("@");
				if (align.getLeft().contains("#"))
				{
					String[] secondsplit = firstsplit[1].split("#");
					textleft.addExtra(it.toggleAlignText(secondsplit[0]));
					if (secondsplit.length > 1)
						textleft.addExtra(secondsplit[1]);
				}
			}
		}
		lineUsed = align.lineUsed();
		return this;
	}

	public TextComponent getOriginalPage()
	{
		return page;
	}
	
	public QuestBookPage duplicate()
	{
		return new QuestBookPage((TextComponent)page.duplicate());
	}
	
	public TextComponent getTextleft()
	{
		return textleft;
	}
	
	public boolean pageOutOfBounds()
	{
		return lineUsed >= TextAlignment.MAXIUM_LINE_PER_PAGE;
	}
}
