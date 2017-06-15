package me.Cutiemango.MangoQuest.book;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.TextComponent;

public class FlexiableBook
{
	public FlexiableBook()
	{
		pages = new ArrayList<>();
		pages.add(new QuestBookPage());
	}
	
	List<QuestBookPage> pages;

	public QuestBookPage getPage(int index)
	{
		return pages.get(index);
	}
	
	public QuestBookPage getLastEditingPage()
	{
		return pages.get(pages.size() - 1);
	}
	
	public void newPage()
	{
		pages.add(new QuestBookPage());
	}
	
	public int size()
	{
		return pages.size();
	}
	
	public TextComponent[] toSendableBook()
	{
		List<TextComponent> list = new ArrayList<>();
		for (QuestBookPage page : pages)
		{
			list.add(page.getOriginalPage());
		}
		return list.toArray(new TextComponent[list.size()]);
	}
}
