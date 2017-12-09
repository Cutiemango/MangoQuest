package me.Cutiemango.MangoQuest.questobject.interfaces;

import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;

public interface EditorObject
{
	public abstract void formatEditorPage(QuestBookPage page, int stage, int obj);
	public abstract boolean receiveCommandInput(Player sender, String type, String obj);
	public abstract EditorListenerObject createCommandOutput(Player sender, String command, String type);
}
