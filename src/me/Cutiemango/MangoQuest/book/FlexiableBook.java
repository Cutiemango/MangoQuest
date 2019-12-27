package me.Cutiemango.MangoQuest.book;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import net.md_5.bungee.api.chat.TextComponent;

public class FlexiableBook
{
	/*
	 * When using this, please remember to add:
	 * 
	 * 	QuestUtil.checkOutOfBounds(page, book);
	 *	page = book.getLastEditingPage();
	 *	
	 *	to make sure that it changes page smoothly. 
	 */
	
	public FlexiableBook()
	{
		pages = new LinkedList<>();
		pages.add(new QuestBookPage());
	}
	
	private LinkedList<QuestBookPage> pages;

	public QuestBookPage getPage(int index)
	{
		return pages.get(index);
	}
	
	public QuestBookPage getFirstPage()
	{
		return pages.getFirst();
	}
	
	public QuestBookPage getLastEditingPage()
	{
		return pages.get(pages.size() - 1);
	}
	
	public void newPage()
	{
		pages.add(new QuestBookPage());
	}
	
	public void setPage(int index, QuestBookPage page)
	{
		pages.set(index, page);
	}
	
	public void addPage(int index, QuestBookPage page)
	{
		pages.add(index, page);
	}
	
	public void removePage(int index)
	{
		if (pages.size() > index)
			pages.remove(index);
	}
	
	public void pushNewPage(QuestConversation conv)
	{
		pages.push(ConversationManager.generateNewPage(conv));
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
