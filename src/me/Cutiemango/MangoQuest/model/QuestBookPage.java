package me.Cutiemango.MangoQuest.model;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestBookPage {
	
	public QuestBookPage(){
		page = new TextComponent();
	}
	
	private TextComponent page;
	
	public void changeLine(){
		page.addExtra("\n");
	}
	
	public QuestBookPage add(String s){
		page.addExtra(QuestUtil.translateColor(s));
		return this;
	}
	
	public QuestBookPage add(TextComponent t){
		page.addExtra(t);
		return this;
	}
	
	public QuestBookPage add(InteractiveText it){
		page.addExtra(it.get());
		return this;
	}
	
	public TextComponent getOriginalPage(){
		return page;
	}
}
