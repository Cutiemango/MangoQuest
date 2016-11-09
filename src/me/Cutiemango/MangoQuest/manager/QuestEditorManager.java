package me.Cutiemango.MangoQuest.manager;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;
import me.Cutiemango.MangoQuest.model.RequirementType;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestEditorManager {

	public static HashMap<String, Quest> isEditing = new HashMap<>();
	
	public static boolean isInEditorMode(Player p){
		return isEditing.containsKey(p.getName());
	}
	
	public static Quest getCurrentEditingQuest(Player p){
		if (!isInEditorMode(p))
			return null;
		else return isEditing.get(p.getName());
	}
	
	public static void edit(Player p, Quest q){
		isEditing.put(p.getName(), q);
		editQuest(p);
	}
	
	public static void exit(Player p){
		if (!isInEditorMode(p))
			return;
		else
			isEditing.remove(p.getName());
	}
	
	public static void mainGUI(Player p){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&9&l任務線上編輯器系統 &0&l：\n"));
		QuestEditorManager.exit(p);
//		p1.addExtra(TextComponentFactory.registerHoverStringEvent(
//				TextComponentFactory.registerClickCommandEvent(QuestUtil.translateColor("&0&l- 《新建任務》"), "/mq e newquest"), "&f點擊進入新增任務介面"));
		p1.addExtra(TextComponentFactory.regHoverEvent(QuestUtil.translateColor("&0&l- 《新建任務》"), "&e功能將在未來開啟！"));
		p1.addExtra("\n\n");
		p1.addExtra(TextComponentFactory.regHoverEvent(
				TextComponentFactory.regClickCmdEvent(QuestUtil.translateColor("&0&l- 《編輯任務》"), "/mq e edit"), "&f點擊進入編輯任務介面"));
		p1.addExtra("\n\n");
		p1.addExtra(TextComponentFactory.regHoverEvent(
				TextComponentFactory.regClickCmdEvent(QuestUtil.translateColor("&0&l- 《移除任務》"), "/mq e remove"), "&f點擊進入移除任務介面"));
		p1.addExtra("\n");
		QuestGUIManager.openBook(p, p1);
	}
	
	public static void editGUI(Player p){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&9&l任務線上編輯器系統&0&l： 選擇要&2&l編輯&0&l的任務\n"));
		for (Quest q : QuestStorage.Quests.values()){
			p1.addExtra(TextComponentFactory.regClickCmdEvent(
					QuestUtil.translateColor("&0- &l" + q.getQuestName() + "&0(" + q.getInternalID() + ")"), "/mq e select " + q.getInternalID()));
			p1.addExtra("\n");
		}
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[返回編輯器選單]", "/mqe"));
		QuestGUIManager.openBook(p, p1);
	}
	
	public static void removeGUI(Player p){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&9&l任務線上編輯器系統&0&l： 選擇要&c&l移除&0&l的任務\n"));
		for (Quest q : QuestStorage.Quests.values()){
			p1.addExtra(TextComponentFactory.regClickCmdEvent(
					QuestUtil.translateColor("&0- &l" + q.getQuestName() + "&0(" + q.getInternalID() + ")"), "/mq e remove confirm " + q.getInternalID()));
			p1.addExtra("\n");
		}
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[返回編輯器選單]", "/mq e"));
		QuestGUIManager.openBook(p, p1);
	}
	
	public static void removeConfirmGUI(Player p, Quest q){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&4&l你確定要刪除任務 "));
		p1.addExtra(QuestUtil.translateColor("\n&0&l" + q.getQuestName() + " &4&l的所有資料嗎？"));
		p1.addExtra(QuestUtil.translateColor("\n&8&l(不可回復，請審慎考慮)"));
		p1.addExtra("\n\n  ");
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&2&l【確定】", "/mq e remove quest " + q.getInternalID()));
		p1.addExtra(QuestUtil.translateColor(" &8&l/ "));
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&c&l【取消】", "/mq e"));
		QuestGUIManager.openBook(p, p1);
	}
	
	public static void editQuest(Player p){
		if (!QuestEditorManager.isInEditorMode(p)){
			QuestUtil.error(p, "你不在編輯模式中！");
			return;
		}
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l編輯任務： " + q.getQuestName()));
		TextComponent p2 = new TextComponent(QuestUtil.translateColor("&0&l任務提要： \n"));
		for (String out : q.getQuestOutline()){
			p2.addExtra(QuestUtil.translateColor(out));
			p2.addExtra("\n");
		}
		
		p1.addExtra("\n");
		p1.addExtra(QuestUtil.translateColor("&0&l任務編碼： &0" + q.getInternalID()));
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regHoverEvent(
				TextComponentFactory.regClickCmdEvent("&6&l任務名稱", "/mq e edit name"), "&f點擊以編輯 任務名稱"));
		p1.addExtra(QuestUtil.translateColor("： &0" + q.getQuestName()));
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regHoverEvent(
				TextComponentFactory.regClickCmdEvent("&0&l任務NPC ", "/mq e edit npc"), "&f點擊以編輯 任務NPC"));
		p1.addExtra(QuestUtil.translateColor("&0&l： "));
		if (q.isCommandQuest())
			p1.addExtra(QuestUtil.translateColor("&c&l無"));
		else
			p1.addExtra(TextComponentFactory.convertLocHoverEvent(q.getQuestNPC().getName(), q.getQuestNPC().getEntity().getLocation(), false));
		p1.addExtra("\n");
		p1.addExtra(QuestUtil.translateColor("&0&l任務需求： "));
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit req"));
		p1.addExtra("\n");
		p1.addExtra(QuestUtil.translateColor("&0&l任務事件： "));
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit evt"));
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regHoverEvent(
				TextComponentFactory.regClickCmdEvent("&0&l重複執行", "/mq e edit redo"), "&f點擊以編輯 是否可重複執行"));
		if (q.isRedoable()){
			p1.addExtra(QuestUtil.translateColor("&0&l： &a是"));
			p1.addExtra("\n");
			p1.addExtra(TextComponentFactory.regHoverEvent(
					TextComponentFactory.regClickCmdEvent("&0&l等待時間", "/mq e edit redodelay"), "&f點擊以編輯 重複執行等待時間"));
			p1.addExtra(QuestUtil.translateColor("&0&l： " + QuestUtil.convertTime(q.getRedoDelay())));
			p1.addExtra("\n");
		}
		else
			p1.addExtra(QuestUtil.translateColor("&0&l： &c否"));
		
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[返回編輯器選單]", "/mq e"));
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regHoverEvent(
				TextComponentFactory.regClickCmdEvent("&2&l【同步伺服器與設定檔】", "/mq e sa"), "&c&l注意： 目前持有此任務的玩家都會遺失任務進度。"));
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regHoverEvent(
				TextComponentFactory.regClickCmdEvent("&5&l【同步伺服器】", "/mq e sl"), "&c&l注意： 目前持有此任務的玩家都會遺失任務進度。"));
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regHoverEvent(
				TextComponentFactory.regClickCmdEvent("&9&l【同步設定檔】", "/mq e sc"), "&c&l注意： 目前持有此任務的玩家都會遺失任務進度。"));
		
		p2.addExtra("\n");
		p2.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit outline"));
		QuestGUIManager.openBook(p, p1, p2);
	}
	
	public static void editQuestTrigger(Player p){
		if (!QuestEditorManager.isInEditorMode(p)){
			QuestUtil.error(p, "你不在編輯模式中！");
			return;
		}
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l編輯任務事件： " + q.getQuestName()));
		p1.addExtra("\n");
		int index = 0;
		for (QuestTrigger qt : q.getTriggers()){
			p1.addExtra("- " + index + ".");
			if (qt.getType().equals(TriggerType.TRIGGER_STAGE_START)
					|| qt.getType().equals(TriggerType.TRIGGER_STAGE_FINISH)) {
				p1.addExtra(TextComponentFactory.regHoverEvent(qt.getTriggerObject().toCustomString(),
						"觸發時機： " + qt.getType().toCustomString(qt.getCount()) + "\n觸發物件內容： "
								+ qt.getObject().toString()));
				p1.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit evt " + index + " "
						+ qt.getType().toString() + " " + qt.getCount() + " " + qt.getTriggerObject().toString()));
			} else {
				p1.addExtra(TextComponentFactory.regHoverEvent(qt.getTriggerObject().toCustomString(),
						"觸發時機： " + qt.getType().toCustomString() + "\n觸發物件內容： " + qt.getObject().toString()));
				p1.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit evt " + index + " "
						+ qt.getType().toString() + " " + qt.getTriggerObject().toString()));
			}
			p1.addExtra(TextComponentFactory.regClickCmdEvent("&7[移除]", "/mq e remove evt " + index));
			p1.addExtra("\n");
			index++;
		}
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[新增]", "/mq e addnew evt"));
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[返回任務選單]", "/mq e gui"));
		QuestGUIManager.openBook(p, p1);
	}
	
	@SuppressWarnings("unchecked")
	public static void editQuestRequirement(Player p){
		if (!QuestEditorManager.isInEditorMode(p)){
			QuestUtil.error(p, "你不在編輯模式中！");
			return;
		}
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l編輯任務需求： " + q.getQuestName()));
		TextComponent p2 = new TextComponent(QuestUtil.translateColor(""));
		p1.addExtra("\n");
		for (RequirementType t : RequirementType.values()){
			switch(t){
			case ITEM:
				p1.addExtra("物品需求：");
				p1.addExtra("\n");
				int i = 0;
				for (ItemStack item : (List<ItemStack>)q.getRequirements().get(t)){
					p1.addExtra("- ");
					p1.addExtra(TextComponentFactory.convertItemHoverEvent(item, false));
					p1.addExtra(QuestUtil.translateColor(" &0&l" + item.getAmount() + "&0 個"));
					p1.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit req ITEM " + i));
					p1.addExtra(TextComponentFactory.regClickCmdEvent("&7[移除]", "/mq e remove req ITEM " + i));
					p1.addExtra("\n");
					i++;
				}
				p1.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[新增]", "/mq e addnew req ITEM " + i));
				p1.addExtra("\n");
				break;
			case LEVEL:
				p1.addExtra("等級需求： " + q.getRequirements().get(t).toString() + " ");
				p1.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit req LEVEL"));
				p1.addExtra("\n");
				break;
			case MONEY:
				p1.addExtra("金錢需求： " + q.getRequirements().get(t).toString() + " ");
				p1.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit req MONEY"));
				p1.addExtra("\n");
				break;
				
			case NBTTAG:
				p2.addExtra("NBT標籤需求：");
				p2.addExtra("\n");
				i = 0;
				for (String s : (List<String>)q.getRequirements().get(t)){
					p2.addExtra("- ");
					p2.addExtra(s);
					p2.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit req NBTTAG " + i));
					p2.addExtra(TextComponentFactory.regClickCmdEvent("&7[移除]", "/mq e remove req NBTTAG " + i));
					p2.addExtra("\n");
					i++;
				}
				p2.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[新增]", "/mq e addnew req NBTTAG " + i));
				p2.addExtra("\n");
				break;
			case QUEST:
				p2.addExtra("任務需求：");
				p2.addExtra("\n");
				i = 0;
				for (String s : (List<String>)q.getRequirements().get(t)){
					p2.addExtra("- ");
					p2.addExtra(s);
					p2.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit req QUEST " + i));
					p2.addExtra(TextComponentFactory.regClickCmdEvent("&7[移除]", "/mq e remove req QUEST " + i));
					p2.addExtra("\n");
					i++;
				}
				p2.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[新增]", "/mq e addnew req QUEST " + i));
				p2.addExtra("\n");
				break;
			case SCOREBOARD:
				p2.addExtra("記分板需求：");
				p2.addExtra("\n");
				i = 0;
				for (String s : (List<String>)q.getRequirements().get(t)){
					p2.addExtra("- ");
					p2.addExtra(s);
					p2.addExtra(TextComponentFactory.regClickCmdEvent("&7[編輯]", "/mq e edit req SCOREBOARD " + i));
					p2.addExtra(TextComponentFactory.regClickCmdEvent("&7[移除]", "/mq e remove req SCOREBOARD " + i));
					p2.addExtra("\n");
					i++;
				}
				p2.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[新增]", "/mq e addnew req SCOREBOARD " + i));
				p2.addExtra("\n");
				break;
			}
		}
		p1.addExtra("\n");
		p1.addExtra(TextComponentFactory.regClickCmdEvent("&0&l[返回任務選單]", "/mq e gui"));
		QuestGUIManager.openBook(p, p1, p2);
	}
	
	public static void selectTriggerType(Player p){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l選擇觸發事件時機： \n"));
		for (TriggerType t : TriggerType.values()){
			p1.addExtra(TextComponentFactory.regClickCmdEvent("- [" + t.toCustomString() + "]", "/mq e addnew evt " + t.toString()));
			p1.addExtra("\n");
		}
		QuestGUIManager.openBook(p, p1);
	}
	
	public static void selectStage(Player p, TriggerType t){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l選擇觸發事件階段： \n"));
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		for (int s = 1; s <= q.getStages().size(); s++){
			p1.addExtra(TextComponentFactory.regClickCmdEvent("- [階段" + s + "]", "/mq e addnew evt " + t.toString() + " " + s));
			p1.addExtra("\n");
		}
		QuestGUIManager.openBook(p, p1);
	}
	
	public static void selectTriggerObject(Player p, TriggerType t, int s){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l選擇觸發物件： \n"));
		for (TriggerObject o : TriggerObject.values()){
			if (t.equals(TriggerType.TRIGGER_STAGE_START) || t.equals(TriggerType.TRIGGER_STAGE_FINISH)){
				p1.addExtra(TextComponentFactory.regClickCmdEvent("- [" + o.toCustomString() + "]", "/mq e addnew evt " + t.toString() + " " + s + " " + o.toString()));
				p1.addExtra("\n");
				continue;
			}
			p1.addExtra(TextComponentFactory.regClickCmdEvent("- [" + o.toCustomString() + "]", "/mq e addnew evt " + t.toString() + " " + o.toString()));
			p1.addExtra("\n");
		}
		QuestGUIManager.openBook(p, p1);
	}
	
	
}
