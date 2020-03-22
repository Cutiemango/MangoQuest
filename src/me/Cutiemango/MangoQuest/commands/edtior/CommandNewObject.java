package me.Cutiemango.MangoQuest.commands.edtior;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Syntax;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.QuestStage;
import me.Cutiemango.MangoQuest.objects.requirement.RequirementType;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject.TriggerObjectType;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectBreakBlock;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandNewObject
{
	
	public static void execute(Quest q, Player sender, String[] args)
	{
		if (!QuestEditorManager.checkEditorMode(sender, true))
			return;
		switch (args[2])
		{
			case "req":
				addRequirements(q, sender, args);
				break;
			case "evt":
				addEvent(q, sender, args);
				break;
			case "stage":
				addStage(q, sender);
				break;
			case "object":
				addObject(q, sender, args);
				break;
			case "reward":
				addReward(q, sender, args);
				break;
		}
	}

	// /mq e addnew evt [triggertype] [stage] [index] [triggerobject]
	private static void addEvent(Quest q, Player sender, String[] args)
	{
		if (args.length < 6)
			return;
		TriggerType type = TriggerType.valueOf(args[3]);
		int stage = Integer.parseInt(args[4]);
		int index = Integer.parseInt(args[5]);
		if (args.length == 6)
		{
			QuestEditorManager.selectTriggerObjType(sender, type, stage, index);
			return;
		}
		TriggerObjectType obj = TriggerObjectType.valueOf(args[6]);
		if (args.length == 7)
		{
			String cmd = "mq e addnew evt " + type.toString() + " " + stage + " " + index + " " + obj.toString();
			if (obj == TriggerObjectType.SEND_TITLE_AND_SUBTITLE)
			{
				EditorListenerHandler.register(sender,
						new EditorListenerObject(ListeningType.STRING, cmd, Syntax.of("S%S", I18n.locMsg("Syntax.TitleAndSubtitle"), "%")));
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.TitleAndSubtitle"));
				return;
			}
			else
				if (obj == TriggerObjectType.TELEPORT)
				{
					EditorListenerHandler.register(sender,
							new EditorListenerObject(ListeningType.STRING, cmd, Syntax.of("S:D:D:D", I18n.locMsg("Syntax.Teleport"), ":")));
					QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.Teleport"));
					return;
				}
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, cmd, null));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
		}
		else
		{
			String s = QuestUtil.convertArgsString(args, 7);
			if (s.equalsIgnoreCase("cancel"))
			{
				QuestEditorManager.editQuestTrigger(sender, type, stage);
				return;
			}
			List<TriggerObject> list = q.getTriggerMap().containsKey(type) ? q.getTriggerMap().get(type) : new ArrayList<>();
			list.add(index, new TriggerObject(obj, s, stage));
			q.getTriggerMap().put(type, list);
			QuestEditorManager.editQuestTrigger(sender, type, stage);
		}
	}
	
	// /mq e addnew req [type]
	@SuppressWarnings("unchecked")
	private static void addRequirements(Quest q, Player sender, String[] args)
	{
		RequirementType t = RequirementType.valueOf(args[3]);
		if (t.hasIndex() && t == RequirementType.QUEST)
		{
			if (args.length == 6)
			{
				if (QuestUtil.getQuest(args[5]) != null)
				{
					Quest quest = QuestUtil.getQuest(args[5]);
					((List<String>) q.getRequirements().get(t)).add(args[5]);
					QuestEditorManager.editQuestRequirement(sender);
					return;
				}
			}
			else if (args.length == 5)
			{
				int index = Integer.parseInt(args[4]);
				QuestEditorManager.selectQuest(sender, "/mq e addnew req QUEST " + index);
				return;
			}
		}
		else if (t == RequirementType.FRIEND_POINT)
		{
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.FriendPoint"));
			EditorListenerHandler.register(sender,
					new EditorListenerObject(ListeningType.STRING, "mq e edit req FRIEND_POINT", Syntax.of("N:D", I18n.locMsg("Syntax.FriendPoint"), ":")));
		}
	}

	// /mq e addnew stage
	private static void addStage(Quest q, Player sender)
	{
		List<SimpleQuestObject> l = new ArrayList<>();
		l.add(new QuestObjectBreakBlock(Material.GRASS, 1));
		q.getStages().add(q.getStages().size(), new QuestStage(l));
		QuestChatManager.info(sender, I18n.locMsg("EditorMessage.StageCreated"));
		QuestChatManager.info(sender, I18n.locMsg("EditorMessage.StageCreated2"));
		QuestChatManager.info(sender, I18n.locMsg("EditorMessage.StageCreated3"));
		QuestEditorManager.editQuestStages(sender);
	}

	// /mq e addnew object [stage]
	private static void addObject(Quest q, Player sender, String[] args)
	{
		if (args.length == 4)
		{
			int stage;
			try
			{
				stage = Integer.parseInt(args[3]);
			}
			catch (NumberFormatException e)
			{
				QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
				return;
			}
			q.getStage(stage - 1).getObjects().add(q.getStage(stage - 1).getObjects().size(),
					new QuestObjectBreakBlock(Material.GRASS, 1));
			QuestEditorManager.selectObjectType(sender, stage, q.getStage(stage - 1).getObjects().size());
		}
	}

	// /mq e addnew reward item
	// /mq e addnew reward fp [npc] [value]
	// /mq e addnew reward command [value] ...
	private static void addReward(Quest q, Player sender, String[] args)
	{
		if (args.length == 4)
		{
			switch (args[3].toLowerCase())
			{
				case "fp":
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e addnew reward fp", Syntax.of("N:D", I18n.locMsg("Syntax.FriendPoint"), ":")));
					QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.FriendPoint"));
					return;
				case "command":
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e addnew reward command", null));
					QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterCommand"));
					return;
			}
		}
		else
			if (args.length >= 5)
			{
				switch (args[3])
				{
					case "fp":
						String[] sp = args[4].split(":");
						try
						{
							q.getQuestReward().getFp().put(Integer.parseInt(sp[0]), Integer.parseInt(sp[1]));
						}
						catch (NumberFormatException e)
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
							QuestEditorManager.editQuest(sender);
							return;
						}
						QuestEditorManager.editQuest(sender);
						return;
					case "command":
						String s = QuestUtil.convertArgsString(args, 4);
						if (!s.equals(""))
							q.getQuestReward().addCommand(s);
						QuestEditorManager.editQuest(sender);
						return;
				}
			}
	}

}
