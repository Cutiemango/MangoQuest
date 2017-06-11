package me.Cutiemango.MangoQuest.model;

import me.Cutiemango.MangoQuest.TextAlignment;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestBookPage
{

	public QuestBookPage()
	{

	}
	
	public QuestBookPage(TextComponent text)
	{
		page = text;
	}

	private TextComponent page = new TextComponent("");
	private TextComponent textleft = new TextComponent("");

	public void changeLine()
	{
		if (textleft != null)
		{
			page.addExtra(textleft);
			textleft = new TextComponent("");
		}
		page.addExtra("\n");
	}

	public QuestBookPage add(String s)
	{
		if (s == "\n")
		{
			changeLine();
			return this;
		}
		s = textleft.toPlainText() + s;
		TextAlignment align = new TextAlignment(s);
		if (align.calculateCharSize(s) >= TextAlignment.MAXIUM_CHAR_PER_LINE){
//			System.out.println("Result:" + align.getResult());
//			System.out.println("Left:" + align.getLeft());
			page.addExtra(align.getResult());
		}
		textleft = new TextComponent(QuestChatManager.translateColor(align.getLeft()));
		return this;
	}

	public QuestBookPage add(TextComponent t)
	{
		page.addExtra(t);
		return this;
	}

	public QuestBookPage add(InteractiveText it)
	{
		String s = it.get().toPlainText();
		s = textleft.toPlainText() + "@" + s + "#";
		TextAlignment align = new TextAlignment(s);
//		System.out.println(align.getResult());
//		System.out.println(align.getLeft());
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
}
