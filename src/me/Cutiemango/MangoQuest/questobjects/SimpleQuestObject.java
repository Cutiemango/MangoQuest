package me.Cutiemango.MangoQuest.questobjects;

import net.md_5.bungee.api.chat.TextComponent;

public abstract class SimpleQuestObject {
	
	public abstract TextComponent toTextComponent(boolean isFinished);

	public abstract String toPlainText();

}
