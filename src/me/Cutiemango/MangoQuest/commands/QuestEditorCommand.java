package me.Cutiemango.MangoQuest.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.listeners.QuestEditorListener;
import me.Cutiemango.MangoQuest.manager.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.RequirementType;
import net.citizensnpcs.api.CitizensAPI;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;

public class QuestEditorCommand{
	
	private static List<String> confirm = new ArrayList<>();
	
	//Command: /mq editor args[1] args[2]
	@SuppressWarnings("unchecked")
	public static void execute(Player sender, String[] args){
		if (!sender.isOp())
			return;
		if (args.length == 1){
			QuestEditorManager.mainGUI(sender);
			return;
		}
		else if (args.length == 2){
			if (args[1].equalsIgnoreCase("edit")){
				QuestEditorManager.editGUI(sender);
				return;
			}
			else if (args[1].equalsIgnoreCase("remove")){
				QuestEditorManager.removeGUI(sender);
				return;
			}
			if (!QuestEditorManager.isInEditorMode(sender)){
				QuestUtil.error(sender, "你不在編輯模式中！");
				return;
			}
			Quest q = QuestEditorManager.getCurrentEditingQuest(sender);
			switch(args[1]){
				case "exit":
					QuestEditorManager.exit(sender);
					QuestUtil.info(sender, "&a已經退出了編輯模式。");
					return;
				case "gui":
					QuestEditorManager.editQuest(sender);
					return;
				case "sa":
				case "saveall":
					Main.instance.configManager.saveQuest(q);
					Quest.synchronizeLocal(q);
					QuestUtil.info(sender, "&a任務 " + q.getQuestName() + " 已經成功儲存至設定檔案！");
					QuestUtil.info(sender, "&b任務 " + q.getQuestName() + " 已經設定與伺服器資料中的任務同步！");
					break;
				case "sc":
				case "savecfg":
					Main.instance.configManager.saveQuest(q);
					QuestUtil.info(sender, "&a任務 " + q.getQuestName() + " 已經成功儲存至設定檔案！");
					break;
				case "sl":
				case "savelocal":
					Quest.synchronizeLocal(q);
					QuestUtil.info(sender, "&b任務 " + q.getQuestName() + " 已經設定與伺服器資料中的任務同步！");
					break;
				default:
					return;
			}
			QuestEditorManager.exit(sender);
		}
		else{
			Quest q = QuestEditorManager.getCurrentEditingQuest(sender);
			switch (args[1]) {
			case "select":
				if (QuestUtil.getQuest(args[2]) == null) {
					QuestUtil.error(sender, "找不到指定的任務。");
					return;
				}
				if (QuestEditorManager.isInEditorMode(sender)){
					if (!confirm.contains(sender.getName())){
						QuestUtil.error(sender, "目前發現您已經有正在編輯的任務，開始這個指定的任務並退出嗎？");
						QuestUtil.error(sender, "若&a&l確定&c請再度輸入一次。");
						confirm.add(sender.getName());
						return;
					}
				}
				QuestEditorManager.edit(sender, QuestUtil.getQuest(args[2]).clone());
				confirm.remove(sender.getName());
				QuestUtil.info(sender, "&c已經進入了編輯模式。在這個模式中將不能與NPC交談或交付物品。");
				return;
			case "addnew":
				switch (args[2]) {
					case "req":
						RequirementType t = RequirementType.valueOf(args[3]);
						if (t.hasIndex() && args.length == 5) {
							switch (t) {
							case QUEST:
							case SCOREBOARD:
							case NBTTAG:
								QuestEditorListener.registerListeningObject(sender,
										"mqe edit req " + t.toString() + " " + Integer.parseInt(args[4]) + " ");
								QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
								((List<String>) q.getRequirements().get(t)).add("");
								break;
							case ITEM:
								QuestEditorListener.registerListeningObject(sender,
										"mqe edit req " + t.toString() + " " + Integer.parseInt(args[4]));
								QuestGUIManager.openInfo(sender, "&c並將物品拿在手上點擊右鍵，\n&c系統將會自動讀取。");
								((List<ItemStack>) q.getRequirements().get(t)).add(new ItemStack(Material.GRASS));
								break;
							default:
								break;
							}
						} else {
							switch (t) {
							case LEVEL:
								QuestEditorListener.registerListeningObject(sender, "mqe edit req " + t.toString() + " ");
								QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
								break;
							case MONEY:
								QuestEditorListener.registerListeningObject(sender, "mqe edit req " + t.toString() + " ");
								QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
								break;
							default:
								break;
							}
						}
						return;
					case "evt":
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
							QuestEditorListener.registerListeningObject(sender, "mqe edit evt " + q.getTriggers().size() + " "
									+ type.toString() + " " + obj.toString() + " ");
							QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							return;
						} else if (args.length == 6) {
							TriggerType type = TriggerType.valueOf(args[3]);
							TriggerObject obj = TriggerObject.valueOf(args[5]);
							if (type.equals(TriggerType.TRIGGER_STAGE_START)
									|| type.equals(TriggerType.TRIGGER_STAGE_FINISH)) {
								QuestEditorListener.registerListeningObject(sender,
										"mqe edit evt " + q.getTriggers().size() + " " + type.toString() + " "
												+ Integer.parseInt(args[4]) + " " + obj.toString() + " ");
								QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
								return;
							}
						}
				}
				break;
			case "edit":
				switch (args[2]) {
					case "name":
						if (args.length == 3) {
							QuestEditorListener.registerListeningObject(sender, "mqe edit name ");
							QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							return;
						} else if (args.length == 4) {
							if (args[3].equals("cancel")){
								QuestEditorManager.editQuest(sender);
								return;
							}
							q.setQuestName(args[3]);
							QuestEditorManager.editQuest(sender);
							return;
						}
					case "outline":
						if (args.length == 3) {
							QuestEditorListener.registerListeningObject(sender, "mqe edit outline ");
							QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							return;
						} else if (args.length >= 4) {
							if (args[3].equalsIgnoreCase("cancel")){
								QuestEditorManager.editQuest(sender);
								return;
							}
							String s = "";
							for (int i = 2; i < args.length; i++) {
								s = s + args[i] + " ";
							}
							q.setQuestOutline(s);
							QuestEditorManager.editQuest(sender);
							return;
						}
					case "redo":
						if (args.length == 3) {
							QuestEditorListener.registerListeningObject(sender, "mqe edit redo ");
							QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							return;
						} else if (args.length == 4) {
							if (args[3].equalsIgnoreCase("cancel")){
								QuestEditorManager.editQuest(sender);
								return;
							}
							q.setRedoable(Boolean.parseBoolean(args[3]));
							QuestEditorManager.editQuest(sender);
							return;
						}
					case "redodelay":
						if (args.length == 3) {
							QuestEditorListener.registerListeningObject(sender, "mqe edit redodelay ");
							QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							return;
						} else if (args.length == 4) {
							if (args[3].equalsIgnoreCase("cancel")){
								QuestEditorManager.editQuest(sender);
								return;
							}
							q.setRedoDelay(Long.parseLong(args[3]));
							QuestEditorManager.editQuest(sender);
							return;
						}
					case "npc":
						if (args.length == 3) {
							QuestEditorListener.registerListeningObject(sender, "mqe edit npc ");
							QuestGUIManager.openInfo(sender, "&c並左鍵打擊指定的NPC。");
							return;
						} else if (args.length == 4) {
							if (args[3].equalsIgnoreCase("-1")){
								q.setQuestNPC(null);
								QuestEditorManager.editQuest(sender);
								QuestUtil.info(sender, "&c已經取消此任務的NPC。");
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
					case "req":
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
								List<ItemStack> itemlist = (List<ItemStack>) q.getRequirements().get(t);
								if (sender.getInventory().getItemInMainHand().getType() != Material.AIR) {
									for (ItemStack is : itemlist){
										if (is.isSimilar(sender.getInventory().getItemInMainHand())) {
											itemlist.remove(Integer.parseInt(args[4]));
											QuestUtil.error(sender, "任務需求中已經有這個物件了！");
											QuestEditorManager.editQuestRequirement(sender);
											return;
										}
									}
									itemlist.remove(Integer.parseInt(args[4]));
									itemlist.add(sender.getInventory().getItemInMainHand());
								} else {
									QuestUtil.error(sender, "物品不可為空！");
									break;
								}
								break;
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
										"mqe edit req " + t.toString() + " " + Integer.parseInt(args[4]) + " ");
								QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
								break;
							case ITEM:
								QuestEditorListener.registerListeningObject(sender,
										"mqe edit req " + t.toString() + " " + Integer.parseInt(args[4]) + " ");
								QuestGUIManager.openInfo(sender, "&c並將物品拿在手上點擊右鍵，\n&c系統將會自動讀取。");
								break;
							}
							return;
						} else if (args.length == 4) {
							switch (t) {
							case LEVEL:
								QuestEditorListener.registerListeningObject(sender, "mqe edit req " + t.toString() + " ");
								QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
								break;
							case MONEY:
								QuestEditorListener.registerListeningObject(sender, "mqe edit req " + t.toString() + " ");
								QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
								break;
							default:
								break;
							}
							return;
						}
					case "evt":
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
								QuestEditorListener.registerListeningObject(sender, "mqe edit evt " + index + " "
										+ type.toString() + " " + i + " " + obj.toString() + " ");
								QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
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
									"mqe edit evt " + index + " " + type.toString() + " " + obj.toString() + " ");
							QuestGUIManager.openInfo(sender, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							return;
						}
					}
			case "remove":
				switch (args[2]) {
					case "confirm":
						if (args.length == 4){
							if (QuestUtil.getQuest(args[3]) != null){
								Quest target = QuestUtil.getQuest(args[3]);
								QuestEditorManager.removeConfirmGUI(sender, target);
								return;
							}
						}
					case "req":
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
							QuestUtil.info(sender, "指定的物件已經移除。");
							return;
						}
					case "evt":
						int index = Integer.parseInt(args[3]);
						if (args.length == 4) {
							q.getTriggers().remove(index);
							QuestEditorManager.editQuestTrigger(sender);
							QuestUtil.info(sender, "指定的物件已經移除。");
							return;
						}
					case "quest":
						if (args.length == 4){
							if (QuestUtil.getQuest(args[3]) != null){
								Quest target = QuestUtil.getQuest(args[3]);
								for (Player pl : Bukkit.getOnlinePlayers()){
									Iterator<QuestProgress> it = QuestUtil.getData(pl).getProgresses().iterator();
									while (it.hasNext()) {
										QuestProgress qp = it.next();
										if (qp.getQuest().equals(target)){
											QuestUtil.getData(pl).forceQuit(target);
											break;
										}
										else continue;
									}
								}
								Main.instance.configManager.removeQuest(target);
								QuestUtil.info(sender, "&c任務 " + target.getQuestName() + " 已經移除。");
								QuestStorage.Quests.remove(args[3]);
								return;
							}
						}
					}
			case "sa":
			case "saveall":
				if (!QuestEditorManager.isInEditorMode(sender)){
					QuestUtil.error(sender, "你不在編輯模式中！");
					return;
				}
				Main.instance.configManager.saveQuest(q);
				Quest.synchronizeLocal(q);
				QuestUtil.info(sender, "&a任務 " + q.getQuestName() + " 已經成功儲存至設定檔案！");
				QuestUtil.info(sender, "&b任務 " + q.getQuestName() + " 已經設定與伺服器資料中的任務同步！");
				break;
			case "sc":
			case "savecfg":
				if (!QuestEditorManager.isInEditorMode(sender)){
					QuestUtil.error(sender, "你不在編輯模式中！");
					return;
				}
				Main.instance.configManager.saveQuest(q);
				QuestUtil.info(sender, "&a任務 " + q.getQuestName() + " 已經成功儲存至設定檔案！");
				break;
			case "sl":
			case "savelocal":
				if (!QuestEditorManager.isInEditorMode(sender)){
					QuestUtil.error(sender, "你不在編輯模式中！");
					return;
				}
				Quest.synchronizeLocal(q);
				QuestUtil.info(sender, "&b任務 " + q.getQuestName() + " 已經設定與伺服器資料中的任務同步！");
				break;
			}
			QuestEditorManager.mainGUI(sender);
		}
	}

}
