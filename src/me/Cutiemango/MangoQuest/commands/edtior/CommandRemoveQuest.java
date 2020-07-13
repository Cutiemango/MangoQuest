package me.Cutiemango.MangoQuest.commands.edtior;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.manager.config.QuestConfigManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.RequirementType;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CommandRemoveQuest
{

	public static void execute(Quest q, Player sender, String[] args)
	{
		if (args.length >= 3)
		{
			switch (args[2])
			{
				case "quest":
					removeQuest(sender, args);
					return;
				case "confirm":
					removeConfirm(sender, args);
					return;
			}
		}
		if (!QuestEditorManager.checkEditorMode(sender, true))
			return;
		switch (args[2])
		{
			case "req":
				removeRequirements(q, sender, args);
				break;
			case "evt":
				removeEvent(q, sender, args);
				break;
			case "stage":
				removeStage(q, sender, args);
				break;
			case "object":
				removeObject(q, sender, args);
				break;
			case "reward":
				removeReward(q, sender, args);
				break;
		}
	}

	private static void removeReward(Quest q, Player sender, String[] args)
	{
		if (args.length == 5)
		{
			switch (args[3].toLowerCase())
			{
				case "item":
					q.getQuestReward().getChoices().remove(Integer.parseInt(args[4]));
					break;
				case "fp":
					q.getQuestReward().getFp().remove(Integer.parseInt(args[4]));
					break;
				case "command":
					q.getQuestReward().getCommands().remove(Integer.parseInt(args[4]));
					break;
			}
			QuestEditorManager.editQuest(sender);
		}

	}

	private static void removeConfirm(Player sender, String[] args)
	{
		if (args.length == 4)
		{
			if (QuestUtil.getQuest(args[3]) != null)
			{
				Quest target = QuestUtil.getQuest(args[3]);
				QuestEditorManager.removeConfirmGUI(sender, target);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void removeRequirements(Quest q, Player sender, String[] args)
	{
		RequirementType t = RequirementType.valueOf(args[3]);
		if (args.length == 5)
		{
			switch (t)
			{
				case LEVEL:
				case MONEY:
				case ITEM:
				case SKILLAPI_CLASS:
				case SKILLAPI_LEVEL:
					break;
				case QUEST:
					((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
					break;
				case FRIEND_POINT:
					((HashMap<Integer, Integer>)q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
					break;
			}
			QuestEditorManager.editQuestRequirement(sender);
			QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRemoved"));
		}
	}
	
	// /mq e remove evt [triggertype] [stage] [index]
	private static void removeEvent(Quest q, Player sender, String[] args)
	{
		if (args.length == 6)
		{
			TriggerType type = TriggerType.valueOf(args[3]);
			int stage = Integer.parseInt(args[4]);
			int index = Integer.parseInt(args[5]);
			List<TriggerObject> list = q.getTriggerMap().get(type);
			if (list.get(index).getStage() == stage)
			{
				list.remove(index);
				q.getTriggerMap().put(type, list);
				QuestEditorManager.editQuestTrigger(sender, type, stage);
				QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRemoved"));
			}
		}
	}

	private static void removeQuest(Player sender, String[] args)
	{
		if (args.length == 4)
		{
			if (QuestUtil.getQuest(args[3]) != null)
			{
				Quest target = QuestUtil.getQuest(args[3]);
				for (Player pl : Bukkit.getOnlinePlayers())
				{
					Iterator<QuestProgress> it = QuestUtil.getData(pl).getProgresses().iterator();
					while (it.hasNext())
					{
						QuestProgress qp = it.next();
						if (QuestValidater.weakValidate(target, qp.getQuest()))
						{
							QuestUtil.getData(pl).forceQuit(target, true);
							break;
						}
					}
				}
				QuestConfigManager.getSaver().removeQuest(target);
				QuestChatManager.info(sender, I18n.locMsg("EditorMessage.QuestRemoved", target.getQuestName()));
				QuestStorage.Quests.remove(args[3]);
				QuestEditorManager.removeGUI(sender);
			}
		}
	}

	// /mq e remove stage [stage]
	private static void removeStage(Quest q, Player sender, String[] args)
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
				QuestEditorManager.editQuestStages(sender);
				return;
			}
			q.getStages().remove(stage - 1);
			QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRemoved"));
			QuestEditorManager.editQuestStages(sender);
		}
	}

	// /mq e remove object [stage] [object]
	private static void removeObject(Quest q, Player sender, String[] args)
	{
		if (args.length == 5)
		{
			int stage;
			int obj;
			try
			{
				stage = Integer.parseInt(args[3]);
				obj = Integer.parseInt(args[4]);
			}
			catch (NumberFormatException e)
			{
				QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
				QuestEditorManager.editQuestStages(sender);
				return;
			}
			q.getStage(stage - 1).getObjects().remove(obj - 1);
			QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRemoved"));
			QuestEditorManager.editQuestObjects(sender, stage);
		}
	}
}
