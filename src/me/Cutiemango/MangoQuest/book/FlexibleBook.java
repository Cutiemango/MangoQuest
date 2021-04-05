package me.Cutiemango.MangoQuest.book;

import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FlexibleBook
{
	/*
	 * When using this, please remember to add:
	 *
	 * 	QuestUtil.checkOutOfBounds(page, book);
	 *	page = book.getLastEditingPage();
	 *
	 *	to make sure that it changes page smoothly.
	 */

	public FlexibleBook() {
		pages = new LinkedList<>();
		pages.add(new QuestBookPage());
	}

	private final LinkedList<QuestBookPage> pages;
	private int currentPage = 0;

	// Redirected book page operations
	public FlexibleBook add(String s) {
		getCurrentPage().add(s);
		return this;
	}

	public FlexibleBook add(TextComponent t) {
		getCurrentPage().add(t);
		return this;
	}

	public FlexibleBook add(InteractiveText it) {
		getCurrentPage().add(it);
		return this;
	}

	public void changeLine() {
		getCurrentPage().changeLine();
	}

	public QuestBookPage getPage(int index) {
		return pages.get(index);
	}

	public QuestBookPage getFirstPage() {
		return pages.getFirst();
	}

	public QuestBookPage getCurrentPage() {
		QuestBookPage page = pages.get(currentPage);
		if (page.isOutOfBounds()) {
			createNewPage();
			// add the remnants
			pages.getLast().add(page.getSaved());
			page = pages.get(currentPage);
		}
		return page;
	}

	public void createNewPage() {
		pages.add(new QuestBookPage());
		currentPage++;
	}

	public void setPage(int index, QuestBookPage page) {
		pages.set(index, page);
	}

	public void addPage(int index, QuestBookPage page) {
		pages.add(index, page);
	}

	public void removePage(int index) {
		if (pages.size() > index)
			pages.remove(index);
	}

	public void pushNewPage(QuestConversation conv) {
		pages.push(ConversationManager.generateNewPage(conv));
		currentPage++;
	}

	public int size() {
		return pages.size();
	}

	public TextComponent[] toSendableBook() {
		List<TextComponent> list = new ArrayList<>();
		for (QuestBookPage page : pages) {
			list.add(page.getOriginalPage());
		}
		return list.toArray(new TextComponent[0]);
	}
}
