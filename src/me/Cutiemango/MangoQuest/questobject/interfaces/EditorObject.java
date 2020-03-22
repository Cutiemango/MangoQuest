package me.Cutiemango.MangoQuest.questobject.interfaces;

import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import org.bukkit.entity.Player;

public interface EditorObject
{
	void formatEditorPage(QuestBookPage page, int stage, int obj);
	boolean receiveCommandInput(Player sender, String type, String obj);
	EditorListenerObject createCommandOutput(Player sender, String command, String type);
}
