package me.Cutiemango.MangoQuest.commands.edtior;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Syntax;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestSetting;

public class CommandEditSetting
{
	public static void execute(Quest q, Player sender, String[] args)
	{
		if (!QuestEditorManager.checkEditorMode(sender, true))
			return;
		if (args.length == 4 && args[3].equals("cancel"))
		{
			QuestEditorManager.editQuest(sender);
			return;
		}
		switch (args[2])
		{
			case "limit":
				editLimit(q, sender, args);
				break;
			case "timelimit":
				editTimeLimit(q, sender, args);
				break;
			case "vis":
				editVisibility(q, sender, args);
				break;
			case "quit":
				editQuit(q, sender, args);
				break;
			case "redo":
				editRedo(q, sender, args);
				break;
			case "redodelay":
				editRedoDelay(q, sender, args);
				break;
			case "world":
				editWorld(q, sender, args);
				break;
			case "perm":
				editPermission(q, sender, args);
				break;
		}
	}
	
	// /mq e edit perm 
	private static void editPermission(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit perm", null));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				q.setUsePermission(Boolean.parseBoolean(args[3]));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}

	private static void editWorld(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit world", Syntax.of("W", I18n.locMsg("Syntax.World"), "")));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				q.setWorldLimit(Bukkit.getWorld(args[3]));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}

	private static void editLimit(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit limit", null));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				q.setTimeLimited(Boolean.parseBoolean(args[3]));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}
	
	private static void editTimeLimit(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit timelimit", Syntax.of("I", I18n.locMsg("Syntax.Number"), "")));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				String mili = Integer.toString(Integer.parseInt(args[3]) * 1000);
				q.setTimeLimit(Long.parseLong(mili));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}
	
	// /mq e edit vis [take/prog/finish] [true/false]
	private static void editVisibility(Quest q, Player sender, String[] args)
	{
		QuestSetting s = q.getSettings();
		switch (args[3])
		{
			case "take":
				s.toggle(Boolean.parseBoolean(args[4]), s.displayOnProgress(), s.displayOnFinish());
				break;
			case "prog":
				s.toggle(s.displayOnTake(), Boolean.parseBoolean(args[4]), s.displayOnFinish());
				break;
			case "finish":
				s.toggle(s.displayOnTake(), s.displayOnProgress(), Boolean.parseBoolean(args[4]));
				break;
		}
		QuestEditorManager.editQuest(sender);
	}
	
	private static void editQuit(Quest q, Player sender, String[] args)
	{
		q.setQuitable(Boolean.parseBoolean(args[3]));
		QuestEditorManager.editQuest(sender);
		return;
	}
	
	private static void editRedo(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit redo", null));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				q.setRedoable(Boolean.parseBoolean(args[3]));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}
	

	private static void editRedoDelay(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit redodelay", Syntax.of("I", I18n.locMsg("Syntax.Number"), "")));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				String mili = Integer.toString(Integer.parseInt(args[3]) * 1000);
				q.setRedoDelay(Long.parseLong(mili));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}
}
