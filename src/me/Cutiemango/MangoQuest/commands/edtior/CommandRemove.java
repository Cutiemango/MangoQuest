package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestVersion;
import me.Cutiemango.MangoQuest.model.RequirementType;

public class CommandRemove {
	
	public static void execute(Quest q, Player sender, String[] args){
		if (args.length >= 3){
			switch(args[2]){
				case "quest":
					removeQuest(q, sender, args);
					return;
				case "confirm":
					removeConfirm(q, sender, args);
					return;
			}
		}
		if (!QuestEditorManager.isInEditorMode(sender)){
			QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.NotInEditor"));
			return;
		}
		switch (args[2]) {
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
	
	private static void removeReward(Quest q, Player sender, String[] args) {
		if (args.length == 5){
			switch(args[3].toLowerCase()){
				case "item":
					q.getQuestReward().getItems().remove(Integer.parseInt(args[4]));
					break;
				case "fp":
					q.getQuestReward().getFp().remove(Integer.parseInt(args[4]));
					break;
				case "command":
					q.getQuestReward().getCommands().remove(Integer.parseInt(args[4]));
					break;
			}
			QuestEditorManager.editQuest(sender);
			return;
		}
		
	}

	private static void removeConfirm(Quest q, Player sender, String[] args){
		if (args.length == 4){
			if (QuestUtil.getQuest(args[3]) != null){
				Quest target = QuestUtil.getQuest(args[3]);
				QuestEditorManager.removeConfirmGUI(sender, target);
				return;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void removeRequirements(Quest q, Player sender, String[] args){
		RequirementType t = RequirementType.valueOf(args[3]);
		if (args.length == 5) {
			switch (t) {
			case LEVEL:
			case MONEY:
				break;
			case QUEST:
			case SCOREBOARD:
			case NBTTAG:
				((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
				break;
			case ITEM:
				((List<ItemStack>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
				break;
			}
			QuestEditorManager.editQuestRequirement(sender);
			QuestChatManager.info(sender, Questi18n.localizeMessage("EditorMessage.ObjectRemoved"));
			return;
		}
	}
	
	private static void removeEvent(Quest q, Player sender, String[] args){
		int index = Integer.parseInt(args[3]);
		if (args.length == 4) {
			q.getTriggers().remove(index);
			QuestEditorManager.editQuestTrigger(sender);
			QuestChatManager.info(sender, Questi18n.localizeMessage("EditorMessage.ObjectRemoved"));
			return;
		}
	}
	
	private static void removeQuest(Quest q, Player sender, String[] args){
		if (args.length == 4){
			if (QuestUtil.getQuest(args[3]) != null){
				Quest target = QuestUtil.getQuest(args[3]);
				for (Player pl : Bukkit.getOnlinePlayers()){
					Iterator<QuestProgress> it = QuestUtil.getData(pl).getProgresses().iterator();
					while (it.hasNext()) {
						QuestProgress qp = it.next();
						if (QuestVersion.weakValidate(target, qp.getQuest())){
							QuestUtil.getData(pl).forceQuit(target, true);
							break;
						}
						else continue;
					}
				}
				Main.instance.configManager.removeQuest(target);
				QuestChatManager.info(sender, Questi18n.localizeMessage("EditorMessage.QuestRemoved", target.getQuestName()));
				QuestStorage.Quests.remove(args[3]);
				QuestEditorManager.removeGUI(sender);
				return;
			}
		}
	}
	
	// /mq e remove stage [stage]
	private static void removeStage(Quest q, Player sender, String[] args){
		if (args.length == 4){
			int stage = 1;
			try{
				stage = Integer.parseInt(args[3]);
			}catch(NumberFormatException e){
				QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.WrongFormat"));
				QuestEditorManager.editQuestStages(sender);
				return;
			}
			q.getStages().remove(stage - 1);
			QuestChatManager.info(sender, Questi18n.localizeMessage("EditorMessage.ObjectRemoved"));
			QuestEditorManager.editQuestStages(sender);
			return;
		}
	}
	
	// /mq e remove object [stage] [object]
	private static void removeObject(Quest q, Player sender, String[] args){
		if (args.length == 5){
			int stage = 1;
			int obj = 1;
			try{
				stage = Integer.parseInt(args[3]);
				obj = Integer.parseInt(args[4]);
			}catch(NumberFormatException e){
				QuestChatManager.error(sender, Questi18n.localizeMessage("EditorMessage.WrongFormat"));
				QuestEditorManager.editQuestStages(sender);
				return;
			}
			q.getStage(stage - 1).getObjects().remove(obj - 1);
			QuestChatManager.info(sender, Questi18n.localizeMessage("EditorMessage.ObjectRemoved"));
			QuestEditorManager.editQuestObjects(sender, stage);
			return;
		}
	}
}
