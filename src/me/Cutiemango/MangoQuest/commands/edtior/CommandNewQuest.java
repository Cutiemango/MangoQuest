package me.Cutiemango.MangoQuest.commands.edtior;

import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;

public class CommandNewQuest
{

	// /mq e newquest args[2] args[3]
	public static void execute(Player sender, String[] args)
	{
		if (args.length == 2)
		{
			QuestEditorManager.edit(sender, new Quest());
			QuestEditorManager.createQuest(sender);
		}
		else
			if (args.length > 2)
			{
				switch (args[2])
				{
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

	private static void setInternalID(Player p, String[] args)
	{
		if (args.length == 3)
		{
			QuestBookGUIManager.openInfo(p, I18n.locMsg("EditorMessage.NewQuest.EnterID"));
			EditorListenerHandler.register(p, new EditorListenerObject(ListeningType.STRING, "mq e newquest id", null));
			return;
		}
		if (args[3].equalsIgnoreCase("cancel"))
		{
			QuestEditorManager.createQuest(p);
			return;
		}
		QuestEditorManager.getCurrentEditingQuest(p).setInternalID(args[3]);
		QuestChatManager.info(p, I18n.locMsg("EditorMessage.IDRegistered", args[3]));
		QuestEditorManager.createQuest(p);
	}

	private static void setQuestName(Player p, String[] args)
	{
		if (args.length == 3)
		{
			QuestBookGUIManager.openInfo(p, I18n.locMsg("EditorMessage.NewQuest.EnterName"));
			EditorListenerHandler.register(p, new EditorListenerObject(ListeningType.STRING, "mq e newquest name", null));
			return;
		}
		if (args[3].equalsIgnoreCase("cancel"))
		{
			QuestEditorManager.createQuest(p);
			return;
		}
		QuestEditorManager.getCurrentEditingQuest(p).setQuestName(args[3]);
		QuestChatManager.info(p, I18n.locMsg("EditorMessage.NameRegistered", args[3]));
		QuestEditorManager.createQuest(p);
	}

	private static void create(Player p)
	{
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		if (q.getInternalID() != null && q.getQuestName() != null)
		{
			QuestChatManager.info(p, I18n.locMsg("EditorMessage.NewQuest.Successful", q.getQuestName()));
			QuestEditorManager.editQuest(p);
		}
		else
		{
			QuestChatManager.error(p, I18n.locMsg("EditorMessage.NewQuest.Failed"));
			QuestEditorManager.createQuest(p);
		}
	}

}
