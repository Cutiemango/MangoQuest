package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.sucy.skill.SkillAPI;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Syntax;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.manager.reward.QuestRewardManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.requirement.RequirementType;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject.TriggerObjectType;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.interfaces.EditorObject;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectConsumeItem;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectDeliverItem;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectTalkToNPC;
import net.citizensnpcs.api.CitizensAPI;

public class CommandEditQuest
{

	// Command: /mq e edit args[2] args[3]
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
			case "timelimit":
			case "vis":
			case "quit":
			case "redo":
			case "redodelay":
			case "world":
			case "perm":
				CommandEditSetting.execute(q, sender, args);
				break;
			case "name":
				editName(q, sender, args);
				break;
			case "outline":
				editOutline(q, sender, args);
				break;
			case "npc":
				editNPC(q, sender, args);
				break;
			case "req":
				editRequirements(q, sender, args);
				break;
			case "evt":
				editTrigger(q, sender, args);
				break;
			case "stage":
				editStage(q, sender, args);
				break;
			case "object":
				editObject(q, sender, args);
				break;
			case "reward":
				editReward(q, sender, args);
				break;
		}
	}

	private static void editName(Quest q, Player sender, String args[])
	{
		if (args.length == 3)
		{

			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit name", null));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				q.setQuestName(args[3]);
				QuestEditorManager.editQuest(sender);
				return;
			}
	}

	private static void editOutline(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit outline", null));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.Outline"));
			return;
		}
		else
			if (args.length >= 4)
			{
				int line = 0;
				try
				{
					line = Integer.parseInt(args[3]) - 1;
				}
				catch (NumberFormatException e)
				{
					QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
					QuestEditorManager.editQuest(sender);
					return;
				}
				if (line < 0)
				{
					QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
					QuestEditorManager.editQuest(sender);
					return;
				}
				String s = QuestUtil.convertArgsString(args, 4);
				if (q.getQuestOutline().size() - 1 < line)
					q.getQuestOutline().add(line, s);
				else
					q.getQuestOutline().set(line, s);
				QuestEditorManager.editQuest(sender);
				return;
			}
	}
	
	// /mq e edit req [type] ([index]) [obj]
	@SuppressWarnings("unchecked")
	private static void editRequirements(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			QuestEditorManager.editQuestRequirement(sender);
			return;
		}
		RequirementType t = RequirementType.valueOf(args[3]);
		if (args.length >= 6)
		{
			if (args[5].equalsIgnoreCase("cancel"))
			{
				QuestEditorManager.editQuestRequirement(sender);
				return;
			}
			switch (t)
			{
				case ITEM:
				case LEVEL:
				case MONEY:
					break;
				case QUEST:
					if (QuestUtil.getQuest(args[5]) != null)
					{
						if (((List<String>) q.getRequirements().get(t)).contains(args[5]))
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ObjectExist"));
							((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
							break;
						}
						((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
						((List<String>) q.getRequirements().get(t)).add(args[5]);
						break;
					}
					else
					{
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.QuestNotFound"));
						break;
					}
				case FRIEND_POINT:
					int id = Integer.parseInt(args[4]);
					int fp = Integer.parseInt(args[5]);
					if (!QuestValidater.validateNPC(Integer.toString(id)))
					{
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.NPCNotFound", Integer.toString(id)));
						return;
					}
					((HashMap<Integer, Integer>) q.getRequirements().get(t)).put(id, fp);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.FriendPointRegistered", Integer.toString(id), Integer.toString(fp)));
					break;
				default:
					break;

			}
			QuestEditorManager.editQuestRequirement(sender);
			return;
		}
		else
			if (args.length == 5)
			{
				switch (t)
				{
					case LEVEL:
						q.getRequirements().put(t, Integer.parseInt(args[4]));
						QuestEditorManager.editQuestRequirement(sender);
						break;
					case MONEY:
						q.getRequirements().put(t, Double.parseDouble(args[4]));
						QuestEditorManager.editQuestRequirement(sender);
						break;
					case QUEST:
						QuestEditorManager.selectQuest(sender, "mq e edit req " + t.toString() + " " + Integer.parseInt(args[4]));
						break;
					case FRIEND_POINT:
						int id = Integer.parseInt(args[4]);
						if (!QuestValidater.validateNPC(Integer.toString(id)))
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.NPCNotFound", Integer.toString(id)));
							return;
						}
						EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit req FRIEND_POINT " + args[4], null));
						QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.FriendPointReq"));
						break;
					case ITEM:
						break;
					case SKILLAPI_CLASS:
						if (SkillAPI.isClassRegistered(args[4]))
							q.getRequirements().put(t, args[4]);
						else
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.NotSkillAPIClass", args[4]));
						QuestEditorManager.editQuestRequirement(sender);
						break;
					case SKILLAPI_LEVEL:
						q.getRequirements().put(t, Integer.parseInt(args[4]));
						QuestEditorManager.editQuestRequirement(sender);
						break;
				}
				return;
			}
			else
				if (args.length == 4)
				{
					switch (t)
					{
						case LEVEL:
						case MONEY:
						case SKILLAPI_CLASS:
						case SKILLAPI_LEVEL:
						case FRIEND_POINT:
							EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit req " + t.toString(), null));
							QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
							break;
						case ITEM:
							EditorListenerHandler.registerGUI(sender, "requirement");
							break;
						default:
							break;
					}
					return;
				}
	}

	private static void editNPC(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.NPC_LEFT_CLICK, "mq e edit npc", Syntax.of("N", I18n.locMsg("Syntax.NPCID"), "")));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.ClickNPC"));
			return;
		}
		else
			if (args.length == 4)
			{
				if (args[3].equalsIgnoreCase("-1"))
				{
					q.setQuestNPC(null);
					QuestEditorManager.editQuest(sender);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.NPCRemoved"));
					return;
				}
				q.setQuestNPC(Main.getHooker().getNPC(args[3]));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}

	// Command: /mq e edit evt [triggertype] [stage] [index] [objtype] [obj]
	private static void editTrigger(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			QuestEditorManager.selectTriggerType(sender, "edit");
			return;
		}
		else if (args.length == 4)
		{
			TriggerType type = TriggerType.valueOf(args[3]);
			if (type == TriggerType.TRIGGER_STAGE_FINISH || type == TriggerType.TRIGGER_STAGE_START)
				QuestEditorManager.selectTriggerStage(sender, "edit", TriggerType.valueOf(args[3]));
			else
				QuestEditorManager.editQuestTrigger(sender, TriggerType.valueOf(args[3]), -1);
			return;
		}
		else if (args.length == 5)
		{
			QuestEditorManager.editQuestTrigger(sender, TriggerType.valueOf(args[3]), Integer.parseInt(args[4]));
			return;
		}
		TriggerType type = TriggerType.valueOf(args[3]);
		int stage = Integer.parseInt(args[4]);
		int index = Integer.parseInt(args[5]);
		TriggerObjectType objtype = TriggerObjectType.valueOf(args[6]);
		if (args.length >= 8)
		{
			String s = QuestUtil.convertArgsString(args, 7);
			if (s.equalsIgnoreCase("cancel"))
			{
				QuestEditorManager.editQuestTrigger(sender, type, stage);
				return;
			}
			List<TriggerObject> list = q.getTriggerMap().containsKey(type) ? q.getTriggerMap().get(type) : new ArrayList<>();
			if (index == list.size())
				list.add(new TriggerObject(objtype, s, stage));
			else
				list.set(index, new TriggerObject(objtype, s, stage));
			q.getTriggerMap().put(type, list);
			QuestEditorManager.editQuestTrigger(sender, type, stage);
			return;
		}
		else
			if (args.length == 7)
			{
				if (objtype == TriggerObjectType.SEND_TITLE_AND_SUBTITLE)
				{
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING,
							"mq e edit evt " + type.toString() + " " + stage + " " + index + " " + objtype.toString(), Syntax.of("S%S", I18n.locMsg("Syntax.TitleAndSubtitle"), "%")));
					QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.TitleAndSubtitle"));
					return;
				}
				else if (objtype == TriggerObjectType.TELEPORT)
				{
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING,
							"mq e edit evt " + type.toString() + " " + stage + " " + index + " " + objtype.toString(), Syntax.of("S:D:D:D", I18n.locMsg("Syntax.Teleport"), ":")));
					QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.Teleport"));
					return;
				}
				EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING,
						"mq e edit evt " + type.toString() + " " + stage + " " + index + " " + objtype.toString(), null));
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			}
	}

	private static void editStage(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			QuestEditorManager.editQuestStages(sender);
			return;
		}
		else
			if (args.length == 4)
			{
				int stage = 1;
				try
				{
					stage = Integer.parseInt(args[3]);
				}
				catch (NumberFormatException e)
				{
					QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
				}
				QuestEditorManager.editQuestObjects(sender, stage);
				return;
			}
	}

	// /mq e edit object [stage] [objcount] [obj] [content]...
	private static void editObject(Quest q, Player sender, String[] args)
	{
		if (args.length <= 4)
		{
			QuestEditorManager.editQuestStages(sender);
			return;
		}
		int stage = 1;
		int obj = 1;
		try
		{
			stage = Integer.parseInt(args[3]);
			obj = Integer.parseInt(args[4]);
		}
		catch (NumberFormatException e)
		{
			QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
			return;
		}
		switch (args.length)
		{
			case 5:
				QuestEditorManager.editQuestObject(sender, stage, obj);
				return;
			case 6:
				if (args[5].equalsIgnoreCase("type"))
				{
					QuestEditorManager.selectObjectType(sender, stage, obj);
					return;
				}
				SimpleQuestObject qobj = q.getStage(stage - 1).getObject(obj - 1);
				if (!(qobj instanceof EditorObject))
					return;
				EditorListenerObject eobj = ((EditorObject)qobj).createCommandOutput(sender, "mq e edit object " + stage + " " + obj + " " + args[5], args[5]);
				if (eobj == null)
					return;
				EditorListenerHandler.register(sender, eobj);
				return;
			case 7:
				SimpleQuestObject o = q.getStage(stage - 1).getObject(obj - 1);
				if (args[6].equalsIgnoreCase("cancel"))
					break;
				else if (args[5].equalsIgnoreCase("type"))
				{
					SimpleQuestObject ob = null;
					switch (args[6].toUpperCase())
					{
						case "BREAK_BLOCK":
							ob = new QuestObjectBreakBlock(Material.GRASS, (short) 0, 1);
							break;
						case "CONSUME_ITEM":
							ob = new QuestObjectConsumeItem(new ItemStack(Material.BREAD), 1);
							break;
						case "DELIVER_ITEM":
							ob = new QuestObjectDeliverItem(CitizensAPI.getNPCRegistry().getById(0), new ItemStack(Material.APPLE), 1);
							break;
						case "KILL_MOB":
							ob = new QuestObjectKillMob(EntityType.ZOMBIE, 1, null);
							break;
						case "REACH_LOCATION":
							ob = new QuestObjectReachLocation(new Location(Bukkit.getWorld("world"), 0, 0, 0), 0,
									I18n.locMsg("EditorMessage.DefaultLocation"));
							break;
						case "TALK_TO_NPC":
							ob = new QuestObjectTalkToNPC(Main.getHooker().getNPC(0));
							break;
						default:
							return;
					}
					if (ob != null)
					{
						q.getStage(stage - 1).getObjects().set(obj - 1, ob);
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ChangeObject"));
					}
				}
				if (!(o instanceof EditorObject))
					return;
				if (!((EditorObject)o).receiveCommandInput(sender, args[5], args[6]))
				{
					QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument", args[6]));
					break;
				}
				QuestEditorManager.editQuestObject(sender, stage, obj);
		}
	}

	// /mq e edit reward [type] [value]
	// /mq e edit command [counter] [value]...
	private static void editReward(Quest q, Player sender, String[] args)
	{
		if (args.length == 4)
		{
			switch (args[3].toLowerCase())
			{
				case "money":
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit reward " + args[3], null));
					QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.MoneyAmount"));
					break;
				case "choiceamount":
				case "exp":
				case "saexp":
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit reward " + args[3], Syntax.of("I", I18n.locMsg("Syntax.Number"), "")));
					QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.ExpAmount"));
					break;
				case "item":
					QuestRewardManager.openEditMainGUI(sender);
					return;
				default:
					QuestEditorManager.editQuest(sender);
					return;
			}
			return;
		}
		else
			if (args.length == 5)
			{
				switch (args[3].toLowerCase())
				{
					case "money":
						double money = q.getQuestReward().getMoney();
						try
						{
							money = Double.parseDouble(args[4]);
						}
						catch (NumberFormatException e)
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
							break;
						}
						q.getQuestReward().setMoney(money);
						break;
					case "exp":
						q.getQuestReward().setExp(Integer.parseInt(args[4]));
						break;
					case "saexp":
						q.getQuestReward().setSkillAPIExp(Integer.parseInt(args[4]));
						break;
					case "choiceamount":
						int i = Integer.parseInt(args[4]);
						if (i > q.getQuestReward().getChoiceAmount())
							QuestChatManager.error(sender, I18n.locMsg("QuestReward.TooManyChoices"));
						q.getQuestReward().setRewardAmount(i);
						QuestRewardManager.openEditMainGUI(sender);
						return;
					case "fp":
						QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.FriendPoint"));
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.STRING, "mq e edit reward fp " + Integer.parseInt(args[4]), Syntax.of("N:D", I18n.locMsg("Syntax.FriendPoint"), ":")));
						return;
					case "command":
						QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterCommand"));
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.STRING, "mq e edit reward command " + Integer.parseInt(args[4]), null));
						return;
				}
				QuestEditorManager.editQuest(sender);
			}
			else
				if (args.length >= 6)
				{
					switch (args[3].toLowerCase())
					{
						case "fp":
							int npc = 0;
							int fp = 0;
							try
							{
								npc = Integer.parseInt(args[4]);
								fp = Integer.parseInt(args[5]);
							}
							catch (NumberFormatException e)
							{
								QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
								QuestEditorManager.editQuest(sender);
								return;
							}
							q.getQuestReward().getFp().put(npc, fp);
							break;
						case "command":
							String cmd = QuestUtil.convertArgsString(args, 5);
							int index = Integer.parseInt(args[4]);
							if (!cmd.equals(""))
								q.getQuestReward().getCommands().set(index, cmd);
							break;
					}
					QuestEditorManager.editQuest(sender);
				}
	}

}
