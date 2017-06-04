package me.Cutiemango.MangoQuest.commands.edtior;

import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.QuestChatManager;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.editor.QuestEditorListener;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;

public class CommandNewQuest {
	
	// /mq e newquest args[2] args[3]
	public static void execute(Player sender, String[] args){
		if (args.length == 2){
			QuestEditorManager.edit(sender, new Quest());
			QuestEditorManager.createQuest(sender);
			return;
		}
		else if (args.length > 2){
			switch (args[2]) {
			case "id":
				setInternalID(sender, args);
				break;
			case "name":
				setQuestName(sender, args);
				break;
			case "create":
				create(sender);
				break;
			}
		}
	}
	
	private static void setInternalID(Player p, String[] args){
		if (args.length == 3){
			QuestGUIManager.openInfo(p, Questi18n.localizeMessage("EditorMessage.NewQuest.EnterID"));
			QuestEditorListener.registerListeningObject(p, "mq e newquest id ");
			return;
		}
		if (args[3].equalsIgnoreCase("cancel")){
			QuestEditorManager.createQuest(p);
			return;
		}
		QuestEditorManager.getCurrentEditingQuest(p).setInternalID(args[3]);
		QuestChatManager.info(p, Questi18n.localizeMessage("EditorMessage.NewQuest.IDRegistered", args[3]));
		QuestEditorManager.createQuest(p);
	}
	
	private static void setQuestName(Player p, String[] args){
		if (args.length == 3){
			QuestGUIManager.openInfo(p, Questi18n.localizeMessage("EditorMessage.NewQuest.EnterName"));
			QuestEditorListener.registerListeningObject(p, "mq e newquest name ");
			return;
		}
		if (args[3].equalsIgnoreCase("cancel")){
			QuestEditorManager.createQuest(p);
			return;
		}
		QuestEditorManager.getCurrentEditingQuest(p).setQuestName(args[3]);
		QuestChatManager.info(p, Questi18n.localizeMessage("EditorMessage.NewQuest.NameRegistered", args[3]));
		QuestEditorManager.createQuest(p);
	}
	
	private static void create(Player p){
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		if (q.getInternalID() != null && q.getQuestName() != null){
			QuestChatManager.info(p, Questi18n.localizeMessage("EditorMessage.NewQuest.Successful", q.getQuestName()));
			QuestEditorManager.editQuest(p);
		}else{
			QuestChatManager.error(p, Questi18n.localizeMessage("EditorMessage.NewQuest.Failed"));
			QuestEditorManager.createQuest(p);
			return;
		}
	}

}
