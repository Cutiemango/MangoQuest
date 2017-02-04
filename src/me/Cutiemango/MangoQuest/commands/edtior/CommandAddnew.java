package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.listeners.QuestEditorListener;
import me.Cutiemango.MangoQuest.manager.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestStage;
import me.Cutiemango.MangoQuest.model.RequirementType;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;

public class CommandAddnew {
	
	public static void execute(Quest q, Player sender, String[] args){
		if (!QuestEditorManager.isInEditorMode(sender)){
			QuestUtil.error(sender, "你不在編輯模式中！");
			return;
		}
		switch (args[2]) {
			case "req":
				addRequirements(q, sender, args);
				break;
			case "evt":
				addEvent(q, sender, args);
				break;
			case "stage":
				addStage(q, sender, args);
				break;
			case "object":
				addObject(q, sender, args);
				break;
			case "reward":
				addReward(q, sender, args);
				break;
		}
	}
	
	private static void addEvent(Quest q, Player sender, String[] args){
		if (args.length == 3) {
			QuestEditorManager.selectTriggerType(sender);
			return;
		} else if (args.length == 4) {
			TriggerType type = TriggerType.valueOf(args[3]);
			if (type.equals(TriggerType.TRIGGER_STAGE_START)
					|| type.equals(TriggerType.TRIGGER_STAGE_FINISH)) {
				QuestEditorManager.selectStage(sender, type);
				return;
			}
			QuestEditorManager.selectTriggerObject(sender, type, 0);
			return;
		} else if (args.length == 5) {
			TriggerType type = TriggerType.valueOf(args[3]);
			if (type.equals(TriggerType.TRIGGER_STAGE_START)
					|| type.equals(TriggerType.TRIGGER_STAGE_FINISH)) {
				QuestEditorManager.selectTriggerObject(sender, type, Integer.parseInt(args[4]));
				return;
			}
			TriggerObject obj = TriggerObject.valueOf(args[4]);
			QuestEditorListener.registerListeningObject(sender, "mq e edit evt " + q.getTriggers().size() + " "
					+ type.toString() + " " + obj.toString() + " ");
			QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
			return;
		} else if (args.length == 6) {
			TriggerType type = TriggerType.valueOf(args[3]);
			TriggerObject obj = TriggerObject.valueOf(args[5]);
			if (type.equals(TriggerType.TRIGGER_STAGE_START)
					|| type.equals(TriggerType.TRIGGER_STAGE_FINISH)) {
				QuestEditorListener.registerListeningObject(sender,
						"mq e edit evt " + q.getTriggers().size() + " " + type.toString() + " "
								+ Integer.parseInt(args[4]) + " " + obj.toString() + " ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
				return;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void addRequirements(Quest q, Player sender, String[] args){
		RequirementType t = RequirementType.valueOf(args[3]);
		if (t.hasIndex() && args.length == 5) {
			switch (t) {
			case QUEST:
			case SCOREBOARD:
			case NBTTAG:
				QuestEditorListener.registerListeningObject(sender,
						"mq e edit req " + t.toString() + " " + Integer.parseInt(args[4]) + " ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
				((List<String>) q.getRequirements().get(t)).add("");
				break;
			case ITEM:
				QuestEditorListener.registerListeningObject(sender,
						"mq e edit req " + t.toString() + " " + Integer.parseInt(args[4]) + " ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並將物品拿在手上點擊右鍵，\n&c系統將會自動讀取。");
				((List<ItemStack>) q.getRequirements().get(t)).add(new ItemStack(Material.GRASS));
				break;
			default:
				break;
			}
		} else {
			switch (t) {
			case LEVEL:
				QuestEditorListener.registerListeningObject(sender, "mq e edit req " + t.toString() + " ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
				break;
			case MONEY:
				QuestEditorListener.registerListeningObject(sender, "mq e edit req " + t.toString() + " ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
				break;
			default:
				break;
			}
		}
	}
	
	// /mq e addnew stage
	private static void addStage(Quest q, Player sender, String[] args){
		List<SimpleQuestObject> l = new ArrayList<>();
		l.add(new QuestObjectBreakBlock(Material.GRASS, (short)0, 1));
		q.getStages().add(q.getStages().size(), new QuestStage(null, null, l));
		QuestUtil.info(sender, "&e已經成功創建新的任務階段。");
		QuestUtil.info(sender, "&e由於每個任務階段至少需含有&c&l一個&e任務目標，已自動&a新增預設目標&e。");
		QuestUtil.info(sender, "&e預設任務目標皆可依自身需求更改。");
		QuestEditorManager.editQuestStages(sender);
	}
	
	// /mq e addnew object [stage]
	private static void addObject(Quest q, Player sender, String[] args){
		if (args.length == 4){
			int stage = 1;
			try{
				stage = Integer.parseInt(args[3]);
			}catch(NumberFormatException e){
				QuestUtil.error(sender, "請輸入正確的數字！");
				return;
			}
			q.getStage(stage - 1).getObjects().add(q.getStage(stage - 1).getObjects().size(), new QuestObjectBreakBlock(Material.GRASS, (short)0, 1));
			QuestEditorManager.selectObjectType(sender, stage, q.getStage(stage - 1).getObjects().size());
		}
	}
	
	// /mq e addnew reward item
	// /mq e addnew reward fp [npc] [value]
	private static void addReward(Quest q, Player sender, String[] args){
		if (args.length == 4){
			switch(args[3].toLowerCase()){
			case "item":
				QuestEditorListener.registerListeningObject(sender,
						"mq e edit reward item " + q.getQuestReward().getItems().size() + " ");
				q.getQuestReward().getItems().add(new ItemStack(Material.DIRT));
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並將物品拿在手上點擊右鍵，\n&c系統將會自動讀取。");
				return;
			case "fp":
				QuestEditorListener.registerListeningObject(sender, "mq e addnew reward fp ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並按照此格式輸入：\n&0[NPC代碼]:[好感度]。");
				return;
			}
		}
		else if (args.length == 5){
			String[] sp = args[4].split(":");
			try{
				q.getQuestReward().getFp().put(Integer.parseInt(sp[0]), Integer.parseInt(sp[1]));
			}catch(NumberFormatException e){
				QuestUtil.error(sender, "未按照指定格式輸入！");
				QuestEditorManager.editQuest(sender);
				return;
			}
			QuestEditorManager.editQuest(sender);
			return;
		}
	}

}
