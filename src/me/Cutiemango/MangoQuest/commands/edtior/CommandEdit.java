package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.listeners.QuestEditorListener;
import me.Cutiemango.MangoQuest.manager.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.RequirementType;
import me.Cutiemango.MangoQuest.questobjects.ItemObject;
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectConsumeItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectDeliverItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;
import net.citizensnpcs.api.CitizensAPI;

public class CommandEdit {
	
	// Command: /mq e edit args[2] args[3]
	public static void execute(Quest q, Player sender, String[] args){
		if (!QuestEditorManager.isInEditorMode(sender)){
			QuestUtil.error(sender, "你不在編輯模式中！");
			return;
		}
		switch (args[2]) {
			case "name":
				editName(q, sender, args);
				break;
			case "outline":
				editOutline(q, sender, args);
				break;
			case "redo":
				editRedo(q, sender, args);
				break;
			case "redodelay":
				editRedoDelay(q, sender, args);
				break;
			case "npc":
				editNPC(q, sender, args);
				break;
			case "req":
				editRequirements(q, sender, args);
				break;
			case "evt":
				editEvent(q, sender, args);
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
	
	private static void editName(Quest q, Player sender, String args[]){
		if (args.length == 3) {
			QuestEditorListener.registerListeningObject(sender, "mq e edit name ");
			QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
			return;
		}
		else if (args.length == 4) {
			if (args[3].equals("cancel")){
				QuestEditorManager.editQuest(sender);
				return;
			}
			q.setQuestName(args[3]);
			QuestEditorManager.editQuest(sender);
			return;
		}
	}
	
	private static void editOutline(Quest q, Player sender, String[] args){
		if (args.length == 3) {
			QuestEditorListener.registerListeningObject(sender, "mq e edit outline ");
			QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並在聊天窗輸入任務提要。\n&7(請輸入[行數][空1格][內容])\n&7(例如：1 這是第一行)");
			return;
		}
		else if (args.length >= 4) {
			if (args[3].equalsIgnoreCase("cancel")){
				QuestEditorManager.editQuest(sender);
				return;
			}
			int line = 0;
			try{
				line = Integer.parseInt(args[3]) - 1;
			}catch(NumberFormatException e){
				QuestUtil.error(sender, "請輸入行數！");
				QuestEditorManager.editQuest(sender);
				return;
			}
			if (line < 0){
				QuestUtil.error(sender, "行數輸入錯誤！請重新輸入！");
				QuestEditorManager.editQuest(sender);
				return;
			}
			String s = "";
			for (int i = 4; i < args.length; i++) {
				s = s + args[i] + " ";
			}
			s = s.trim();
			if (q.getQuestOutline().size() - 1 < line)
				q.getQuestOutline().add(line, s);
			else
				q.getQuestOutline().set(line, s);
			QuestEditorManager.editQuest(sender);
			return;
		}
	}
	
	private static void editRedo(Quest q, Player sender, String[] args){
		if (args.length == 3) {
			QuestEditorListener.registerListeningObject(sender, "mq e edit redo ");
			QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
			return;
		}
		else if (args.length == 4) {
			if (args[3].equalsIgnoreCase("cancel")){
				QuestEditorManager.editQuest(sender);
				return;
			}
			q.setRedoable(Boolean.parseBoolean(args[3]));
			QuestEditorManager.editQuest(sender);
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void editRequirements(Quest q, Player sender, String[] args){
		if (args.length == 3) {
			QuestEditorManager.editQuestRequirement(sender);
			return;
		}
		RequirementType t = RequirementType.valueOf(args[3]);
		if (args.length >= 6) {
			if (args[5].equalsIgnoreCase("cancel")) {
				QuestEditorManager.editQuestRequirement(sender);
				return;
			}
			switch (t) {
			case ITEM:
			case LEVEL:
			case MONEY:
				break;
			case NBTTAG:
				if (((List<String>) q.getRequirements().get(t)).contains(args[5])) {
					QuestUtil.error(sender, "任務需求中已經有這個物件了！");
					((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
					break;
				}
				((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
				((List<String>) q.getRequirements().get(t)).add(args[5]);
				break;
			case QUEST:
				if (QuestUtil.getQuest(args[5]) != null) {
					if (((List<String>) q.getRequirements().get(t)).contains(args[5])) {
						QuestUtil.error(sender, "任務需求中已經有這個物件了！");
						((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
						break;
					}
					((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
					((List<String>) q.getRequirements().get(t)).add(args[5]);
					break;
				} else {
					QuestUtil.error(sender, "找不到指定的任務。");
					break;
				}
			case SCOREBOARD:
				if (((List<String>) q.getRequirements().get(t)).contains(args[5])) {
					QuestUtil.error(sender, "任務需求中已經有這個物件了！");
					((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
					break;
				}
				((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
				((List<String>) q.getRequirements().get(t)).add(args[5]);
				break;
			default:
				break;

			}
			QuestEditorManager.editQuestRequirement(sender);
			return;
		} else if (args.length == 5) {
			switch (t) {
			case LEVEL:
				q.getRequirements().put(t, Integer.parseInt(args[4]));
				QuestEditorManager.editQuestRequirement(sender);
				break;
			case MONEY:
				q.getRequirements().put(t, Double.parseDouble(args[4]));
				QuestEditorManager.editQuestRequirement(sender);
				break;
			case QUEST:
			case SCOREBOARD:
			case NBTTAG:
				QuestEditorListener.registerListeningObject(sender,
						"mq e edit req " + t.toString() + " " + Integer.parseInt(args[4]) + " ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
				break;
			case ITEM:
				break;
			}
			return;
		} else if (args.length == 4) {
			switch (t) {
			case LEVEL:
				QuestEditorListener.registerListeningObject(sender, "mq e edit req " + t.toString() + " ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
				break;
			case MONEY:
				QuestEditorListener.registerListeningObject(sender, "mq e edit req " + t.toString() + " ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
				break;
			case ITEM:
				QuestEditorListener.registerGUI(sender, "requirement");
				break;
			default:
				break;
			}
			return;
		}
	}
	
	private static void editNPC(Quest q, Player sender, String[] args){
		if (args.length == 3) {
			QuestEditorListener.registerListeningObject(sender, "mq e edit npc ");
			QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並左鍵打擊指定的NPC。");
			return;
		} 
		else if (args.length == 4) {
			if (args[3].equalsIgnoreCase("-1")){
				q.setQuestNPC(null);
				QuestEditorManager.editQuest(sender);
				QuestUtil.info(sender, "&c請關閉書本視窗，\n&c已經取消此任務的NPC。");
				return;
			}
			else if (args[3].equalsIgnoreCase("cancel")){
				QuestEditorManager.editQuest(sender);
				return;
			}
			q.setQuestNPC(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3])));
			QuestEditorManager.editQuest(sender);
			return;
		}
	}
	
	private static void editRedoDelay(Quest q, Player sender, String[] args){
		if (args.length == 3) {
			QuestEditorListener.registerListeningObject(sender, "mq e edit redodelay ");
			QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
			return;
		} 
		else if (args.length == 4) {
			if (args[3].equalsIgnoreCase("cancel")){
				QuestEditorManager.editQuest(sender);
				return;
			}
			q.setRedoDelay(Long.parseLong(args[3]));
			QuestEditorManager.editQuest(sender);
			return;
		}
	}
	
	private static void editEvent(Quest q, Player sender, String[] args){
		if (args.length == 3) {
			QuestEditorManager.editQuestTrigger(sender);
			return;
		}
		int index = Integer.parseInt(args[3]);
		TriggerType type = TriggerType.valueOf(args[4]);
		if (args.length >= 8) {
			if (type.equals(TriggerType.TRIGGER_STAGE_START) || type.equals(TriggerType.TRIGGER_STAGE_FINISH)){
				int i = Integer.parseInt(args[5]);
				TriggerObject obj = TriggerObject.valueOf(args[6]);
				String s = "";
				for (int j = 7; j < args.length; j++) {
					s = s + args[j] + " ";
				}
				if (index == q.getTriggers().size())
					q.getTriggers().add(new QuestTrigger(type, obj, i, s));
				else
					q.getTriggers().set(index, new QuestTrigger(type, obj, i, s));
				QuestEditorManager.editQuestTrigger(sender);
				return;
			}
			TriggerObject obj = TriggerObject.valueOf(args[5]);
			String s = "";
			for (int j = 6; j < args.length; j++) {
				s = s + args[j] + " ";
			}
			if (index == q.getTriggers().size())
				q.getTriggers().add(new QuestTrigger(type, obj, s));
			else
				q.getTriggers().set(index, new QuestTrigger(type, obj, s));
			QuestEditorManager.editQuestTrigger(sender);
			return;
		} else if (args.length == 7) {
			switch (type) {
			case TRIGGER_STAGE_START:
			case TRIGGER_STAGE_FINISH:
				int i = Integer.parseInt(args[5]);
				TriggerObject obj = TriggerObject.valueOf(args[6]);
				QuestEditorListener.registerListeningObject(sender, "mq e edit evt " + index + " "
						+ type.toString() + " " + i + " " + obj.toString() + " ");
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
				return;
			default:
				obj = TriggerObject.valueOf(args[5]);
				String s = "";
				for (int j = 6; j < args.length; j++) {
					s = s + args[j] + " ";
				}
				if (index == q.getTriggers().size())
					q.getTriggers().add(new QuestTrigger(type, obj, s));
				else
					q.getTriggers().set(index, new QuestTrigger(type, obj, s));
				QuestEditorManager.editQuestTrigger(sender);
				return;
			}
		} else if (args.length == 6) {
			TriggerObject obj = TriggerObject.valueOf(args[5]);
			QuestEditorListener.registerListeningObject(sender,
					"mq e edit evt " + index + " " + type.toString() + " " + obj.toString() + " ");
			QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
			return;
		}
	}
	
	private static void editStage(Quest q, Player sender, String[] args){
		if (args.length == 3){
			QuestEditorManager.editQuestStages(sender);
			return;
		}
		else if (args.length == 4){
			int stage = 1;
			try{
				stage = Integer.parseInt(args[3]);
			}catch(NumberFormatException e){
				QuestUtil.error(sender, "請輸入正確的數字！");
			}
			QuestEditorManager.editQuestObjects(sender, stage);
			return;
		}
	}
	
	// /mq e edit object [stage] [objcount] [obj] [內容]...
	private static void editObject(Quest q, Player sender, String[] args){
		if (args.length <= 4){
			QuestEditorManager.editQuestStages(sender);
			return;
		}
		int stage = 1;
		int obj = 1;
		try{
			stage = Integer.parseInt(args[3]);
			obj = Integer.parseInt(args[4]);
		}catch(NumberFormatException e){
			QuestUtil.error(sender, "請輸入正確的數字！");
			return;
		}
		switch(args.length){
			case 5:
				QuestEditorManager.editQuestObject(sender, stage, obj);
				return;
			case 6:
				switch(args[5].toLowerCase()){
					case "block":
						QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並破壞方塊，\n&c系統會自動讀取。");
						break;
					case "amount":
						QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並打開聊天窗輸入數量。");
						break;
					case "item":
						QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並將物品拿在主手上，\n&c點擊右鍵，系統將自動讀取。");
						break;
					case "itemnpc":
					case "npc":
						QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並左鍵打擊目標NPC，\n&c系統將自動讀取。");
						break;
					case "mtmmob":
						QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並輸入自定義怪物的ID。\n&c(或是左鍵打擊怪物)");
						break;
					case "mobname":
						QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並輸入自定義怪物名稱。");
						break;
					case "mobtype":
						QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並左鍵打擊怪物，\n&c系統將自動讀取。");
						break;
					case "loc":
						QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並到達目標位置，\n&c輸入限定範圍後，\n&c系統將自動讀取。");
						break;
					case "locname":
						QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並輸入目標位置名稱。");
						break;
					case "type":
						QuestEditorManager.selectObjectType(sender, stage, obj);
						return;
					default:
						return;
				}
				QuestEditorListener.registerListeningObject(sender, "mq e edit object " + stage + " " + obj + " " + args[5] + " ");
				return;
			case 7:
				SimpleQuestObject o = q.getStage(stage - 1).getObject(obj - 1);
				if (args[6].equalsIgnoreCase("cancel"))
					break;
				switch(args[5].toLowerCase()){
				case "block":
					try{
						String[] split = args[6].split(":");
						((QuestObjectBreakBlock)o).setType(Material.getMaterial(split[0]));
						((QuestObjectBreakBlock)o).setSubID(Short.parseShort(split[1]));
						QuestUtil.info(sender, "方塊成功登錄： " + QuestUtil.translate(Material.getMaterial(split[0]), Short.parseShort(split[1])));
					}catch(Exception e){
						QuestUtil.error(sender, "輸入了錯誤的方塊名稱！");
					}
					break;
				case "amount":
					try{
						((NumerableObject)o).setAmount(Integer.parseInt(args[6]));
						QuestUtil.info(sender, "數量更改成功： " + args[6]);
					}catch(NumberFormatException e){
						QuestUtil.error(sender, "輸入了錯誤的數字！");
					}
					break;
				case "item":
					try{
						((ItemObject)o).setItem(Main.instance.handler.getItemInMainHand(sender));
						QuestUtil.info(sender, "物品更改成功。");
					}catch(NullPointerException e){
						QuestUtil.error(sender, "請拿著物品在主手以利系統讀取！");
					}
					break;
				case "itemnpc":
					try{
						((QuestObjectDeliverItem)o).setTargetNPC(CitizensAPI.getNPCRegistry().getById(Integer.valueOf(args[6])));
						QuestUtil.info(sender, "NPC更改成功： " + args[6]);
					}catch(Exception e){
						QuestUtil.error(sender, "請輸入正確以及存在的NPC代號！");
					}
					break;
				case "npc":
					try{
						((QuestObjectTalkToNPC)o).setTargetNPC(CitizensAPI.getNPCRegistry().getById(Integer.valueOf(args[6])));
						QuestUtil.info(sender, "NPC更改成功： " + args[6]);
					}catch(Exception e){
						QuestUtil.error(sender, "請輸入正確以及存在的NPC代號！");
					}
					break;
				case "mtmmob":
					try{
						((QuestObjectKillMob)o).setMythicMob(Main.instance.initManager.getMythicMobsAPI().getMythicMob(args[6]));
						QuestUtil.info(sender, "MythicMob更改成功： " + args[6]);
					}catch(Exception e){
						e.printStackTrace();
						QuestUtil.error(sender, "請輸入正確以及存在的MythicMob代號！");
					}
					break;
				case "mobname":
					((QuestObjectKillMob)o).setCustomName(QuestUtil.translateColor(args[6]));
					QuestUtil.info(sender, "怪物名稱更改成功： " + args[6]);
					break;
				case "mobtype":
					try{
						((QuestObjectKillMob)o).setType(EntityType.valueOf(args[6]));
						QuestUtil.info(sender, "怪物更改成功： " + QuestUtil.translate(EntityType.valueOf(args[6])));
					}catch(Exception e){
						QuestUtil.error(sender, "請輸入正確的怪物名稱！");
					}
					break;
				case "loc":
					try{
						((QuestObjectReachLocation)o).setRadius(Integer.parseInt(args[6]));
						Location l = sender.getLocation();
						((QuestObjectReachLocation)o).setLocation(new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ()));
						QuestUtil.info(sender, "座標更改成功： (" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + ")");
						QuestUtil.info(sender, "距離更改成功： " + args[6]);
					}catch(NumberFormatException e){
						QuestUtil.error(sender, "輸入了錯誤的數字！");
					}
					break;
				case "locname":
					((QuestObjectReachLocation)o).setName(QuestUtil.translateColor(args[6]));
					QuestUtil.info(sender, "怪物名稱更改成功： " + args[6]);
					break;
				case "type":
					SimpleQuestObject ob = null;
					switch(args[6].toUpperCase()){
					case "BREAK_BLOCK":
						ob = new QuestObjectBreakBlock(Material.GRASS, (short)0, 1);
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
						ob = new QuestObjectReachLocation(new Location(Bukkit.getWorld("world"), 0, 0, 0), 0, "預設地點");
						break;
					case "TALK_TO_NPC":
						ob = new QuestObjectTalkToNPC(CitizensAPI.getNPCRegistry().getById(0));
						break;
					default:
						return;
					}
					if (ob != null){
						q.getStage(stage - 1).getObjects().set(obj - 1, ob);
						QuestUtil.info(sender, "&a更改任務目標類別時將會重設為預設值，請自行依據需求調整目標內容。");
					}
					break;
				}
				QuestEditorManager.editQuestObject(sender, stage, obj);
		}
	}
	
	// /mq e edit reward [type] [value]
	private static void editReward(Quest q, Player sender, String[] args){
		if (args.length == 4){
			switch(args[3].toLowerCase()){
			case "money":
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並輸入金錢獎勵金額。");
				break;
			case "exp":
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並輸入經驗值量。");
				break;
			case "item":
				QuestEditorListener.registerGUI(sender, "reward");
				return;
			}
			QuestEditorListener.registerListeningObject(sender, "mq e edit reward " + args[3] + " ");
			return;
		}else if (args.length == 5){
			switch(args[3].toLowerCase()){
			case "money":
				double money = q.getQuestReward().getMoney();
				try{
					money = Integer.parseInt(args[4]);
				}catch(NumberFormatException e){
					QuestUtil.error(sender, "請輸入正確的數字！");
					break;
				}
				q.getQuestReward().setMoney(money);
				break;
			case "exp":
				int exp = q.getQuestReward().getExp();
				try{
					exp = Integer.parseInt(args[4]);
				}catch(NumberFormatException e){
					QuestUtil.error(sender, "請輸入正確的數字！");
					break;
				}
				q.getQuestReward().setExp(exp);
				break;
			case "fp":
				QuestGUIManager.openInfo(sender, "&c請關閉書本視窗，\n&c並輸入要調整的好感度。");
				QuestEditorListener.registerListeningObject(sender, "mq e edit reward fp " + Integer.parseInt(args[4]) + " ");
				return;
			}
			QuestEditorManager.editQuest(sender);
		}
		else if (args.length == 6){
			switch(args[3].toLowerCase()){
			case "fp":
				int npc = 0;
				int fp = 0;
				try{
					npc = Integer.parseInt(args[4]);
					fp = Integer.parseInt(args[5]);
				}catch(NumberFormatException e){
					QuestUtil.error(sender, "請輸入正確的數字！");
					QuestEditorManager.editQuest(sender);
					return;
				}
				q.getQuestReward().getFp().put(npc, fp);
				break;
			}
			QuestEditorManager.editQuest(sender);
		}
	}

}
