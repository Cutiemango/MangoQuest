package me.Cutiemango.MangoQuest.editor;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.QuestGUIManager;
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
	
	public static void editQuest(Player p){
		if (!QuestEditorManager.isInEditorMode(p)){
			QuestUtil.error(p, "你不在編輯模式中！");
			return;
		}
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l編輯任務： " + q.getQuestName()));
		TextComponent p2 = new TextComponent(QuestUtil.translateColor("&0任務提要： \n" + q.getQuestOutline()));
		p1.addExtra("\n");
		p1.addExtra(QuestUtil.translateColor("&0任務內部碼： " + q.getInternalID()));
		p1.addExtra("\n");
		p1.addExtra(QuestUtil.translateColor("&0任務名稱： " + q.getQuestName()));
		p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit name"));
		p1.addExtra("\n");
		p1.addExtra(QuestUtil.translateColor("&0任務NPC： "));
		p1.addExtra(TextComponentFactory.convertLocationtoHoverEvent(q.getQuestNPC().getName(), q.getQuestNPC().getEntity().getLocation(), false));
		p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit npc"));
		p1.addExtra("\n");
		p1.addExtra(QuestUtil.translateColor("&0任務需求： "));
		p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit req"));
		p1.addExtra("\n");
		p1.addExtra(QuestUtil.translateColor("&0任務事件： "));
		p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit evt"));
		p1.addExtra("\n");
		p1.addExtra(QuestUtil.translateColor("&0是否可重複執行： " + Boolean.toString(q.isRedoable())));
		p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit redo"));
		p1.addExtra("\n");
		if (q.isRedoable()){
			p1.addExtra(QuestUtil.translateColor("&0重復執行CD時間： \n" + QuestUtil.convertTime(q.getRedoDelay())));
			p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit redodelay"));
			p1.addExtra("\n");
		}
		
		
		p2.addExtra("\n");
		p2.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit outline"));
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
				p1.addExtra(TextComponentFactory.registerHoverStringEvent(qt.getTriggerObject().toCustomString(),
						"觸發時機： " + qt.getType().toCustomString(qt.getCount()) + "\n觸發物件內容： "
								+ qt.getObject().toString()));
				p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit evt " + index + " "
						+ qt.getType().toString() + " " + qt.getCount() + " " + qt.getTriggerObject().toString()));
			} else {
				p1.addExtra(TextComponentFactory.registerHoverStringEvent(qt.getTriggerObject().toCustomString(),
						"觸發時機： " + qt.getType().toCustomString() + "\n觸發物件內容： " + qt.getObject().toString()));
				p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit evt " + index + " "
						+ qt.getType().toString() + " " + qt.getTriggerObject().toString()));
			}
			p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[移除]", "/mqe remove evt " + index));
			p1.addExtra("\n");
			index++;
		}
		p1.addExtra(TextComponentFactory.registerClickCommandEvent("&0&l[新增]", "/mqe addnew evt"));
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
					p1.addExtra(TextComponentFactory.convertItemStacktoHoverEvent(false, item));
					p1.addExtra(QuestUtil.translateColor(" &0&l" + item.getAmount() + "&0 個"));
					p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit req ITEM " + i));
					p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[移除]", "/mqe remove req ITEM " + i));
					p1.addExtra("\n");
					i++;
				}
				p1.addExtra(TextComponentFactory.registerClickCommandEvent("&0&l[新增]", "/mqe addnew req ITEM " + i));
				p1.addExtra("\n");
				break;
			case LEVEL:
				p1.addExtra("等級需求： " + q.getRequirements().get(t).toString() + " ");
				p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit req LEVEL"));
				p1.addExtra("\n");
				break;
			case MONEY:
				p1.addExtra("金錢需求： " + q.getRequirements().get(t).toString() + " ");
				p1.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit req MONEY"));
				p1.addExtra("\n");
				break;
				
			case NBTTAG:
				p2.addExtra("NBT標籤需求：");
				p2.addExtra("\n");
				i = 0;
				for (String s : (List<String>)q.getRequirements().get(t)){
					p2.addExtra("- ");
					p2.addExtra(s);
					p2.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit req NBTTAG " + i));
					p2.addExtra(TextComponentFactory.registerClickCommandEvent("&7[移除]", "/mqe remove req NBTTAG " + i));
					p2.addExtra("\n");
					i++;
				}
				p2.addExtra(TextComponentFactory.registerClickCommandEvent("&0&l[新增]", "/mqe addnew req NBTTAG " + i));
				p2.addExtra("\n");
				break;
			case QUEST:
				p2.addExtra("任務需求：");
				p2.addExtra("\n");
				i = 0;
				for (String s : (List<String>)q.getRequirements().get(t)){
					p2.addExtra("- ");
					p2.addExtra(s);
					p2.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit req QUEST " + i));
					p2.addExtra(TextComponentFactory.registerClickCommandEvent("&7[移除]", "/mqe remove req QUEST " + i));
					p2.addExtra("\n");
					i++;
				}
				p2.addExtra(TextComponentFactory.registerClickCommandEvent("&0&l[新增]", "/mqe addnew req QUEST " + i));
				p2.addExtra("\n");
				break;
			case SCOREBOARD:
				p2.addExtra("記分板需求：");
				p2.addExtra("\n");
				i = 0;
				for (String s : (List<String>)q.getRequirements().get(t)){
					p2.addExtra("- ");
					p2.addExtra(s);
					p2.addExtra(TextComponentFactory.registerClickCommandEvent("&7[編輯]", "/mqe edit req SCOREBOARD " + i));
					p2.addExtra(TextComponentFactory.registerClickCommandEvent("&7[移除]", "/mqe remove req SCOREBOARD " + i));
					p2.addExtra("\n");
					i++;
				}
				p2.addExtra(TextComponentFactory.registerClickCommandEvent("&0&l[新增]", "/mqe addnew req SCOREBOARD " + i));
				p2.addExtra("\n");
				break;
			}
		}
		
		QuestGUIManager.openBook(p, p1, p2);
	}
	
	public static void selectTriggerType(Player p){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l選擇觸發事件時機： \n"));
		for (TriggerType t : TriggerType.values()){
			p1.addExtra(TextComponentFactory.registerClickCommandEvent("- [" + t.toCustomString() + "]", "/mqe addnew evt " + t.toString()));
			p1.addExtra("\n");
		}
		QuestGUIManager.openBook(p, p1);
	}
	
	public static void selectStage(Player p, TriggerType t){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l選擇觸發事件階段： \n"));
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		for (int s = 1; s <= q.getStages().size(); s++){
			p1.addExtra(TextComponentFactory.registerClickCommandEvent("- [階段" + s + "]", "/mqe addnew evt " + t.toString() + " " + s));
			p1.addExtra("\n");
		}
		QuestGUIManager.openBook(p, p1);
	}
	
	public static void selectTriggerObject(Player p, TriggerType t, int s){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l選擇觸發物件： \n"));
		for (TriggerObject o : TriggerObject.values()){
			if (t.equals(TriggerType.TRIGGER_STAGE_START) || t.equals(TriggerType.TRIGGER_STAGE_FINISH)){
				p1.addExtra(TextComponentFactory.registerClickCommandEvent("- [" + o.toCustomString() + "]", "/mqe addnew evt " + t.toString() + " " + s + " " + o.toString()));
				p1.addExtra("\n");
				continue;
			}
			p1.addExtra(TextComponentFactory.registerClickCommandEvent("- [" + o.toCustomString() + "]", "/mqe addnew evt " + t.toString() + " " + o.toString()));
			p1.addExtra("\n");
		}
		QuestGUIManager.openBook(p, p1);
	}
	
	
}
