package me.Cutiemango.MangoQuest.manager.config;

import me.Cutiemango.MangoQuest.*;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.QuestStage;
import me.Cutiemango.MangoQuest.objects.RequirementType;
import me.Cutiemango.MangoQuest.objects.reward.QuestReward;
import me.Cutiemango.MangoQuest.objects.reward.RewardChoice;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class QuestConfigSaver
{
	public QuestConfigSaver(QuestConfigManager cm) {
		manager = cm;
	}

	private final QuestConfigManager manager;

	public void saveConversation(QuestConversation qc) {
		QuestIO conv = manager.getGlobalConversation();
		String cpath = "Conversations." + qc.getInternalID() + ".";
		conv.set(cpath + "NPC", qc.getNPC().getId());
		conv.set(cpath + "ConversationName", qc.getName());
		conv.set(cpath + "ConversationActions", saveConvAction(qc.getActions()));

		if (qc instanceof FriendConversation) {
			conv.set(cpath + "FriendConversation", true);
			conv.set(cpath + "FriendPoint", ((FriendConversation) qc).getReqPoint());
		} else if (qc instanceof StartTriggerConversation) {
			StartTriggerConversation sconv = (StartTriggerConversation) qc;
			conv.set(cpath + "StartTriggerConversation", true);
			conv.set(cpath + "StartQuest", sconv.getQuest().getInternalID());
			conv.set(cpath + "AcceptMessage", sconv.getAcceptMessage());
			conv.set(cpath + "DenyMessage", sconv.getDenyMessage());
			conv.set(cpath + "QuestFullMessage", sconv.getQuestFullMessage());
			conv.set(cpath + "AcceptActions", saveConvAction(sconv.getAcceptActions()));
			conv.set(cpath + "DenyActions", saveConvAction(sconv.getDenyActions()));
		}

		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.ConversationSaved", qc.getName(), qc.getInternalID()));
		DebugHandler.log(3, "[Config] Conversation of id=" + qc.getInternalID() + " is saved.");
		conv.save();
	}

	public void saveQuest(Quest q) {
		QuestIO quest = manager.getGlobalQuest();
		String qpath = "Quests." + q.getInternalID() + ".";
		quest.set(qpath + "QuestName", q.getQuestName());
		quest.set(qpath + "QuestOutline", q.getQuestOutline());

		quest.set(qpath + "QuestNPC", q.isCommandQuest() ? -1 : q.getQuestNPC().getId());

		if (q.getFailMessage() != null)
			quest.set(qpath + "MessageRequirementNotMeet", q.getFailMessage());

		quest.set(qpath + "RedoSetting", q.getRedoSetting().toString());
		quest.set(qpath + "RedoDelayMilliseconds", q.getRedoDelay());
		quest.set(qpath + "ResetHour", q.getResetHour());
		quest.set(qpath + "ResetDay", q.getResetDay());

		quest.set(qpath + "TimeLimited", q.isTimeLimited());
		quest.set(qpath + "TimeLimitMilliseconds", q.isTimeLimited() ? q.getTimeLimit() : null);

		saveRequirements(q);
		saveTrigger(q);
		saveStages(q);
		saveReward(q);

		if (q.hasWorldLimit())
			quest.set(qpath + "WorldLimit", q.getWorldLimit().getName());

		quest.set(qpath + "UsePermission", q.usePermission());

		quest.set(qpath + "Visibility.onTake", q.getSettings().displayOnTake());
		quest.set(qpath + "Visibility.onProgress", q.getSettings().displayOnProgress());
		quest.set(qpath + "Visibility.onFinish", q.getSettings().displayOnFinish());
		quest.set(qpath + "Visibility.onInteraction", q.getSettings().displayOnInteraction());

		quest.set(qpath + "QuitSettings.Quitable", q.isQuitable());
		quest.set(qpath + "QuitSettings.QuitAcceptMsg", q.getQuitAcceptMsg());
		quest.set(qpath + "QuitSettings.QuitCancelMsg", q.getQuitCancelMsg());

		if (!QuestValidater.detailedValidate(q, QuestUtil.getQuest(q.getInternalID()))) {
			DebugHandler.log(3, "[Config] Quest of id=" + q.getInternalID() + " does not fully equals to the former version, replacing...");
			quest.set(qpath + "Version", q.getVersion().getTimeStamp());
		}

		DebugHandler.log(3, "[Config] Quest of id=" + q.getInternalID() + " is saved.");
		QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.QuestSaved", q.getQuestName(), q.getInternalID()));
		quest.save();
	}

	@SuppressWarnings("unchecked")
	public void saveRequirements(Quest q) {
		QuestIO quest = manager.getGlobalQuest();
		String qpath = "Quests." + q.getInternalID() + ".";
		// clear all data
		quest.set(qpath + "Requirements", null);

		quest.set(qpath + "Requirements.Level", q.getRequirements().get(RequirementType.LEVEL));
		quest.set(qpath + "Requirements.Quest", q.getRequirements().get(RequirementType.QUEST));
		quest.set(qpath + "Requirements.Money", q.getRequirements().get(RequirementType.MONEY));
		quest.set(qpath + "Requirements.Permission", q.getRequirements().get(RequirementType.PERMISSION));
		int i = 0;
		quest.getConfig().set(qpath + "Requirements.Item", null);
		for (ItemStack is : (List<ItemStack>) q.getRequirements().get(RequirementType.ITEM)) {
			i++;
			quest.getConfig().set(qpath + "Requirements.Item." + i, is);
		}

		HashMap<Integer, Integer> fpMap = (HashMap<Integer, Integer>) q.getRequirements().get(RequirementType.FRIEND_POINT);
		quest.getConfig().set(qpath + "Requirements.FriendPoint", null);

		for (int id : fpMap.keySet())
			quest.set(qpath + "Requirements.FriendPoint." + id, fpMap.get(id));

		if (Main.getHooker().hasSkillAPIEnabled()) {
			quest.set(qpath + "Requirements.SkillAPIClass", q.getRequirements().get(RequirementType.SKILLAPI_CLASS));
			quest.set(qpath + "Requirements.SkillAPILevel", q.getRequirements().get(RequirementType.SKILLAPI_LEVEL));
		}

		if (Main.getHooker().hasQuantumRPGEnabled()) {
			quest.set(qpath + "Requirements.QRPGClass", q.getRequirements().get(RequirementType.QRPG_CLASS));
			quest.set(qpath + "Requirements.QRPGLevel", q.getRequirements().get(RequirementType.QRPG_LEVEL));
		}

		quest.set(qpath + "Requirements.AllowDescendant", q.getRequirements().get(RequirementType.ALLOW_DESCENDANT));
	}

	public void saveTrigger(Quest q) {
		QuestIO quest = manager.getGlobalQuest();
		String qpath = "Quests." + q.getInternalID() + ".";
		// clear all data 
		quest.set(qpath + "TriggerEvents", null);
		for (TriggerType type : q.getTriggerMap().keySet()) {
			List<String> list = new ArrayList<>();
			switch (type) {
				case TRIGGER_ON_FINISH:
				case TRIGGER_ON_QUIT:
				case TRIGGER_ON_TAKE:
					for (TriggerObject obj : q.getTriggerMap().get(type)) {
						list.add(obj.getObjType().toString() + " " + obj.getObject());
					}
					break;
				case TRIGGER_STAGE_FINISH:
				case TRIGGER_STAGE_START:
					for (TriggerObject obj : q.getTriggerMap().get(type)) {
						list.add(obj.getStage() + " " + obj.getObjType().toString() + " " + obj.getObject());
					}
					break;
				default:
					break;
			}
			quest.set(qpath + "TriggerEvents." + type.toString(), list);
		}
	}

	public void saveStages(Quest q) {
		QuestIO quest = manager.getGlobalQuest();
		String qpath = "Quests." + q.getInternalID() + ".";
		int stageCount = 0;
		int objCount = 0;
		quest.set(qpath + "Stages", "");
		for (QuestStage s : q.getStages()) {
			stageCount++;
			for (SimpleQuestObject obj : s.getObjects()) {
				objCount++;
				String objpath = qpath + "Stages." + stageCount + "." + objCount + ".";
				if (obj.hasConversation())
					quest.set(objpath + "ActivateConversation", obj.getConversation().getInternalID());
				quest.set(objpath + "ObjectType", obj.getConfigString());
				obj.save(quest, objpath);
			}
			objCount = 0;
		}
	}

	public void saveReward(Quest q) {
		QuestIO quest = manager.getGlobalQuest();

		String qpath = "Quests." + q.getInternalID() + ".";
		quest.set(qpath + "Rewards", null);

		QuestReward r = q.getQuestReward();
		quest.set(qpath + "Rewards.RewardAmount", r.getRewardAmount());
		quest.set(qpath + "Rewards.InstantGiveReward", r.instantGiveReward());
		quest.set(qpath + "Rewards.Choice", null);

		if (r.hasItem()) {
			int index = 0;
			for (RewardChoice choice : r.getChoices()) {
				index++;
				int itemIndex = 0;
				for (ItemStack item : choice.getItems()) {
					itemIndex++;
					quest.set(qpath + "Rewards.Choice." + index + "." + itemIndex, item);
				}
			}
		}
		if (r.hasMoney())
			quest.set(qpath + "Rewards.Money", r.getMoney());

		if (r.hasExp())
			quest.set(qpath + "Rewards.Experience", r.getExp());

		if (r.hasFriendPoint())
			for (Integer npc : r.getFriendPointMap().keySet())
				quest.set(qpath + "Rewards.FriendlyPoint." + npc, r.getFriendPointMap().get(npc));

		if (r.hasCommand())
			quest.set(qpath + "Rewards.Commands", r.getCommands());

		if (r.hasSkillAPIExp() && Main.getHooker().hasSkillAPIEnabled())
			quest.set(qpath + "Rewards.SkillAPIExp", r.getSkillAPIExp());

		if (r.hasQRPGExp() && Main.getHooker().hasQuantumRPGEnabled())
			quest.set(qpath + "Rewards.QRPGExp", r.getQRPGExp());

		if (!q.isCommandQuest())
			quest.set(qpath + "Rewards.RewardNPC", (r.hasRewardNPC() ? r.getRewardNPC() : q.getQuestNPC()).getId());
	}

	public void removeConversation(QuestConversation qc) {
		QuestIO conv = manager.getGlobalQuest();
		if (qc == null)
			return;
		conv.set("Conversations." + qc.getInternalID(), null);
		QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.ConversationDeleted", qc.getName(), qc.getInternalID()));
		DebugHandler.log(3, "[Config] Conversation of id=" + qc.getInternalID() + " is removed.");
		conv.save();
	}

	public void removeQuest(Quest q) {
		QuestIO quest = manager.getGlobalQuest();
		if (q == null)
			return;
		quest.set("Quests." + q.getInternalID(), null);
		QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.QuestDeleted", q.getQuestName(), q.getInternalID()));
		DebugHandler.log(3, "[Config] Quest of id=" + q.getInternalID() + " is removed.");
		quest.save();
	}

	public void clearPlayerData(Player p) {
		File f = new File(Main.getInstance().getDataFolder() + "/data/", p.getUniqueId() + ".yml");
		try {
			new YamlConfiguration().save(f);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.PlayerDataDeleted", p.getName()));
		DebugHandler.log(3, "[Config] Player data of " + p.getName() + " is removed.");
	}

	private List<String> saveConvAction(List<QuestBaseAction> clist) {
		List<String> list = new ArrayList<>();
		for (QuestBaseAction act : clist) {
			list.add(act.toConfigFormat());
		}
		return list;
	}
}
