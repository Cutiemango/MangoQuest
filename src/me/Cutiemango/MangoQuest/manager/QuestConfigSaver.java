package me.Cutiemango.MangoQuest.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestReward;
import me.Cutiemango.MangoQuest.model.QuestStage;
import me.Cutiemango.MangoQuest.model.RequirementType;
import me.Cutiemango.MangoQuest.model.TriggerObject;
import me.Cutiemango.MangoQuest.model.TriggerType;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectConsumeItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectDeliverItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;

public class QuestConfigSaver
{
	public QuestConfigSaver(QuestConfigManager cm)
	{
		manager = cm;
	}
	
	private QuestConfigManager manager;
	
	private QuestIO quest;
	private QuestIO conv;
	
	public void init()
	{
		quest = manager.QuestsIO;
		conv = manager.ConversationIO;
	}
	
	public void saveConversation(QuestConversation qc)
	{
		String cpath = "Conversations." + qc.getInternalID() + ".";
		conv.set(cpath + "NPC", qc.getNPC().getId());
		conv.set(cpath + "ConversationName", qc.getName());
		conv.set(cpath + "ConversationActions", saveConvAction(qc.getActions()));
		
		if (qc instanceof FriendConversation)
		{
			conv.set(cpath + "FriendConversation", true);
			conv.set(cpath + "FriendPoint", ((FriendConversation)qc).getReqPoint());
		}
		else if (qc instanceof StartTriggerConversation)
		{
			StartTriggerConversation sconv = (StartTriggerConversation)qc;
			conv.set(cpath + "StartQuest", sconv.getQuest().getInternalID());
			conv.set(cpath + "AcceptMessage", sconv.getAcceptMessage());
			conv.set(cpath + "DenyMessage", sconv.getDenyMessage());
			conv.set(cpath + "QuestFullMessage", sconv.getQuestFullMessage());
			conv.set(cpath + "AcceptActions", saveConvAction(sconv.getAcceptActions()));
			conv.set(cpath + "DenyActions", saveConvAction(sconv.getDenyActions()));
		}
		
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.ConversationSaved", qc.getName(), qc.getInternalID()));
		conv.save();
	}

	public void saveQuest(Quest q)
	{
		String qpath = "Quests." + q.getInternalID() + ".";
		quest.set(qpath + "QuestName", q.getQuestName());
		quest.set(qpath + "QuestOutline", q.getQuestOutline());
		if (q.isCommandQuest())
			quest.set(qpath + "QuestNPC", -1);
		else
			quest.set(qpath + "QuestNPC", q.getQuestNPC().getId());
		if (q.getFailMessage() != null)
			quest.set(qpath + "MessageRequirementNotMeet", q.getFailMessage());
		quest.set(qpath + "Redoable", q.isRedoable());
		if (q.isRedoable())
			quest.set(qpath + "RedoDelayMilliseconds", q.getRedoDelay());
		
		quest.set(qpath + "TimeLimited", q.isTimeLimited());
		if (q.isTimeLimited())
			quest.set(qpath + "TimeLimitMilliseconds", q.getTimeLimit());
		
		saveRequirements(q);
		saveTrigger(q);
		saveStages(q);
		saveReward(q);

		quest.set(qpath + "Visibility.onTake", q.getSettings().displayOnTake());
		quest.set(qpath + "Visibility.onProgress", q.getSettings().displayOnProgress());
		quest.set(qpath + "Visibility.onFinish", q.getSettings().displayOnFinish());
		quest.set(qpath + "QuitSettings.Quitable", q.isQuitable());
		quest.set(qpath + "QuitSettings.QuitAcceptMsg", q.getQuitAcceptMsg());
		quest.set(qpath + "QuitSettings.QuitCancelMsg", q.getQuitCancelMsg());
		if (!QuestValidater.detailedValidate(q, QuestUtil.getQuest(q.getInternalID())))
			quest.set(qpath + "Version", q.getVersion().getVersion());
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.QuestSaved", q.getQuestName(), q.getInternalID()));
		quest.save();
	}
	
	@SuppressWarnings("unchecked")
	public void saveRequirements(Quest q)
	{
		String qpath = "Quests." + q.getInternalID() + ".";
		quest.set(qpath + "Requirements.Level", q.getRequirements().get(RequirementType.LEVEL));
		quest.set(qpath + "Requirements.Quest", q.getRequirements().get(RequirementType.QUEST));
		int i = 0;
		for (ItemStack is : (List<ItemStack>) q.getRequirements().get(RequirementType.ITEM))
		{
			i++;
			quest.getConfig().set(qpath + "Requirements.Item." + i, is);
		}
		quest.set(qpath + "Requirements.Scoreboard", q.getRequirements().get(RequirementType.SCOREBOARD));
		quest.set(qpath + "Requirements.NBTTag", q.getRequirements().get(RequirementType.NBTTAG));
		if (Main.instance.pluginHooker.hasSkillAPIEnabled())
		{
			quest.set(qpath + "Requirements.SkillAPIClass", q.getRequirements().get(RequirementType.SKILLAPI_CLASS));
			quest.set(qpath + "Requirements.SkillAPILevel", q.getRequirements().get(RequirementType.SKILLAPI_LEVEL));
		}
	}
	
	public void saveTrigger(Quest q)
	{
		String qpath = "Quests." + q.getInternalID() + ".";
		for (TriggerType type : q.getTriggerMap().keySet())
		{
			List<String> list = new ArrayList<>();
			switch(type)
			{
				case TRIGGER_ON_FINISH:
				case TRIGGER_ON_QUIT:
				case TRIGGER_ON_TAKE:
					for (TriggerObject obj : q.getTriggerMap().get(type))
					{
						list.add(obj.getObjType().toString() + " " + obj.getObject().toString());
					}
					break;
				case TRIGGER_STAGE_FINISH:
				case TRIGGER_STAGE_START:
					for (TriggerObject obj : q.getTriggerMap().get(type))
					{
						list.add(obj.getStage() + " " + obj.getObjType().toString() + " " + obj.getObject().toString());
					}
					break;
				default:
					break;
			}
			quest.set(qpath + "TriggerEvents." + type.toString(), list);
		}
	}
	
	public void saveStages(Quest q)
	{
		String qpath = "Quests." + q.getInternalID() + ".";
		int stageCount = 0;
		int objCount = 0;
		quest.set(qpath + "Stages", "");
		for (QuestStage s : q.getStages())
		{
			stageCount++;
			for (SimpleQuestObject obj : s.getObjects())
			{
				objCount++;
				String objpath = qpath + "Stages." + stageCount + "." + objCount + ".";
				if (obj.hasConversation())
					quest.set(objpath + "ActivateConversation", obj.getConversation().getInternalID());
				quest.set(objpath + "ObjectType", obj.getConfigString());
				switch (obj.getConfigString())
				{
					case "DELIVER_ITEM":
						QuestObjectDeliverItem o = (QuestObjectDeliverItem) obj;
						quest.set(objpath + "TargetNPC", o.getTargetNPC().getId());
						quest.set(objpath + "Item", o.getItem());
						break;
					case "TALK_TO_NPC":
						QuestObjectTalkToNPC on = (QuestObjectTalkToNPC) obj;
						quest.set(objpath + "TargetNPC", on.getTargetNPC().getId());
						break;
					case "KILL_MOB":
						QuestObjectKillMob om = (QuestObjectKillMob) obj;
						quest.set(objpath + "Amount", om.getAmount());
						if (om.isMythicObject())
						{
							quest.set(objpath + "MythicMob", om.getMythicMob().getInternalName());
							break;
						}
						quest.set(objpath + "MobType", om.getType().toString());
						if (om.hasCustomName())
							quest.set(objpath + "MobName", om.getCustomName());
						break;
					case "BREAK_BLOCK":
						QuestObjectBreakBlock ob = (QuestObjectBreakBlock) obj;
						quest.set(objpath + "BlockType", ob.getType().toString());
						quest.set(objpath + "SubID", ob.getShort());
						quest.set(objpath + "Amount", ob.getAmount());
						break;
					case "CONSUME_ITEM":
						QuestObjectConsumeItem oi = (QuestObjectConsumeItem) obj;
						quest.set(objpath + "Item", oi.getItem());
						break;
					case "REACH_LOCATION":
						QuestObjectReachLocation or = (QuestObjectReachLocation) obj;
						String loc = or.getLocation().getWorld().getName() + ":" + or.getLocation().getX() + ":" + or.getLocation().getY() + ":"
								+ or.getLocation().getZ();
						quest.set(objpath + "Location", loc);
						quest.set(objpath + "LocationName", or.getName());
						quest.set(objpath + "Range", or.getRadius());
						break;
				}
				continue;
			}
			objCount = 0;
		}
	}
	
	public void saveReward(Quest q)
	{
		String qpath = "Quests." + q.getInternalID() + ".";
		QuestReward r = q.getQuestReward();
		if (r.hasItem())
		{
			int c = 0;
			for (ItemStack is : q.getQuestReward().getItems())
			{
				c++;
				quest.set(qpath + "Rewards.Item." + c, is);
			}
		}
		if (r.hasMoney())
			quest.set(qpath + "Rewards.Money", q.getQuestReward().getMoney());
		if (r.hasExp())
			quest.set(qpath + "Rewards.Experience", q.getQuestReward().getExp());
		if (r.hasFriendPoint())
		{
			for (Integer npc : q.getQuestReward().getFp().keySet())
			{
				quest.set(qpath + "Rewards.FriendlyPoint." + npc, q.getQuestReward().getFp().get(npc));
			}
		}
		if (r.hasCommand())
			quest.set(qpath + "Rewards.Commands", q.getQuestReward().getCommands());
		if (r.hasSkillAPIExp() && Main.instance.pluginHooker.hasSkillAPIEnabled())
			quest.set(qpath + "Rewards.SkillAPIExp", q.getQuestReward().getSkillAPIExp());
	}
	
	public void removeConversation(QuestConversation qc)
	{
		conv.set("Conversations." + qc.getInternalID(), null);
		QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.ConversationDeleted", qc.getName(), qc.getInternalID()));
		conv.save();
	}
	
	public void removeQuest(Quest q)
	{
		quest.set("Quests." + q.getInternalID(), null);
		QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.QuestDeleted", q.getQuestName(), q.getInternalID()));
		quest.save();
	}
	
	public void clearPlayerData(Player p)
	{
		File f = new File(Main.instance.getDataFolder() + "/data/" , p.getUniqueId() + ".yml");
		try
		{
			new YamlConfiguration().save(f);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.PlayerDataDeleted", p.getName()));
	}
	
	private List<String> saveConvAction(List<QuestBaseAction> clist)
	{
		List<String> list = new ArrayList<>();
		for (QuestBaseAction act : clist)
		{
			list.add(act.toConfigFormat());
		}
		return list;
	}
}
