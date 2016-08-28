package me.Cutiemango.MangoQuest.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestGUIManager;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.editor.QuestEditorListener;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.model.RequirementType;
import net.citizensnpcs.api.CitizensAPI;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;

public class QuestEditorCommand implements CommandExecutor {
	
	private List<String> confirm = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (!p.isOp())
			return false;
		if (cmd.getName().equals("mqe")) {
			if (args.length == 1) {
				if (!QuestEditorManager.isInEditorMode(p)){
					QuestUtil.error(p, "你不在編輯模式中！");
					return false;
				}
				Quest q = QuestEditorManager.getCurrentEditingQuest(p);
				switch (args[0]) {
				case "exit":
					QuestEditorManager.exit(p);
					QuestUtil.info(p, "&a已經退出了編輯模式。");
					return false;
				case "gui":
					QuestEditorManager.editQuest(p);
					return false;
				case "sa":
				case "saveall":
					Main.instance.cfg.saveQuest(q);
					Quest.synchronizeLocal(q);
					QuestUtil.info(p, "&a任務 " + q.getQuestName() + " 已經成功儲存至設定檔案！");
					QuestUtil.info(p, "&b任務 " + q.getQuestName() + " 已經設定與伺服器資料中的任務同步！");
					break;
				case "sc":
				case "savecfg":
					Main.instance.cfg.saveQuest(q);
					QuestUtil.info(p, "&a任務 " + q.getQuestName() + " 已經成功儲存至設定檔案！");
					break;
				case "sl":
				case "savelocal":
					Quest.synchronizeLocal(q);
					QuestUtil.info(p, "&b任務 " + q.getQuestName() + " 已經設定與伺服器資料中的任務同步！");
					break;
				default:
					return false;
				}
				QuestEditorManager.exit(p);
			}
			Quest q = QuestEditorManager.getCurrentEditingQuest(p);
			if (args.length < 2)
				return false;
			switch (args[0]) {
			case "select":
				if (QuestUtil.getQuest(args[1]) == null) {
					QuestUtil.error(p, "找不到指定的任務。");
					return false;
				}
				if (QuestEditorManager.isInEditorMode(p)){
					if (!confirm.contains(p.getName())){
						QuestUtil.error(p, "目前發現您已經有正在編輯的任務，開始這個指定的任務並退出嗎？");
						QuestUtil.error(p, "若&a&l確定&c請再度輸入一次。");
						confirm.add(p.getName());
						return false;
					}
				}
				QuestEditorManager.edit(p, QuestUtil.getQuest(args[1]).clone());
				confirm.remove(p.getName());
				QuestUtil.info(p, "&c已經進入了編輯模式。在這個模式中將不能與NPC交談或交付物品。");
				return false;
			case "addnew":
				switch (args[1]) {
				case "req":
					RequirementType t = RequirementType.valueOf(args[2]);
					if (t.hasIndex() && args.length == 4) {
						switch (t) {
						case QUEST:
						case SCOREBOARD:
						case NBTTAG:
							QuestEditorListener.registerListeningObject(p,
									"mqe edit req " + t.toString() + " " + Integer.parseInt(args[3]) + " ");
							QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							((List<String>) q.getRequirements().get(t)).add("");
							break;
						case ITEM:
							QuestEditorListener.registerListeningObject(p,
									"mqe edit req " + t.toString() + " " + Integer.parseInt(args[3]));
							QuestGUIManager.openInfo(p, "&c並將物品拿在手上點擊右鍵，\n&c系統將會自動讀取。");
							((List<ItemStack>) q.getRequirements().get(t)).add(new ItemStack(Material.GRASS));
							break;
						default:
							break;
						}
					} else {
						switch (t) {
						case LEVEL:
							QuestEditorListener.registerListeningObject(p, "mqe edit req " + t.toString() + " ");
							QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							break;
						case MONEY:
							QuestEditorListener.registerListeningObject(p, "mqe edit req " + t.toString() + " ");
							QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							break;
						default:
							break;
						}
					}
					return false;
				case "evt":
					if (args.length == 2) {
						QuestEditorManager.selectTriggerType(p);
						return false;
					} else if (args.length == 3) {
						TriggerType type = TriggerType.valueOf(args[2]);
						if (type.equals(TriggerType.TRIGGER_STAGE_START)
								|| type.equals(TriggerType.TRIGGER_STAGE_FINISH)) {
							QuestEditorManager.selectStage(p, type);
							return false;
						}
						QuestEditorManager.selectTriggerObject(p, type, 0);
						return false;
					} else if (args.length == 4) {
						TriggerType type = TriggerType.valueOf(args[2]);
						if (type.equals(TriggerType.TRIGGER_STAGE_START)
								|| type.equals(TriggerType.TRIGGER_STAGE_FINISH)) {
							QuestEditorManager.selectTriggerObject(p, type, Integer.parseInt(args[3]));
							return false;
						}
						TriggerObject obj = TriggerObject.valueOf(args[3]);
						QuestEditorListener.registerListeningObject(p, "mqe edit evt " + q.getTriggers().size() + " "
								+ type.toString() + " " + obj.toString() + " ");
						QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
						return false;
					} else if (args.length == 5) {
						TriggerType type = TriggerType.valueOf(args[2]);
						TriggerObject obj = TriggerObject.valueOf(args[4]);
						if (type.equals(TriggerType.TRIGGER_STAGE_START)
								|| type.equals(TriggerType.TRIGGER_STAGE_FINISH)) {
							QuestEditorListener.registerListeningObject(p,
									"mqe edit evt " + q.getTriggers().size() + " " + type.toString() + " "
											+ Integer.parseInt(args[3]) + " " + obj.toString() + " ");
							QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							return false;
						}
					}
				}
				break;
			case "edit":
				switch (args[1]) {
				case "name":
					if (args.length == 2) {
						QuestEditorListener.registerListeningObject(p, "mqe edit name ");
						QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
						return false;
					} else if (args.length == 3) {
						q.setQuestName(args[2]);
						QuestEditorManager.editQuest(p);
						return false;
					}
				case "outline":
					if (args.length == 2) {
						QuestEditorListener.registerListeningObject(p, "mqe edit outline ");
						QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
						return false;
					} else if (args.length >= 3) {
						String s = "";
						for (int i = 2; i < args.length; i++) {
							s = s + args[i] + " ";
						}
						q.setQuestOutline(s);
						QuestEditorManager.editQuest(p);
						return false;
					}
				case "redo":
					if (args.length == 2) {
						QuestEditorListener.registerListeningObject(p, "mqe edit redo ");
						QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
						return false;
					} else if (args.length == 3) {
						q.setRedoable(Boolean.parseBoolean(args[2]));
						QuestEditorManager.editQuest(p);
						return false;
					}
				case "redodelay":
					if (args.length == 2) {
						QuestEditorListener.registerListeningObject(p, "mqe edit redodelay ");
						QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
						return false;
					} else if (args.length == 3) {
						q.setRedoDelay(Long.parseLong(args[2]));
						QuestEditorManager.editQuest(p);
						return false;
					}
				case "npc":
					if (args.length == 2) {
						QuestEditorListener.registerListeningObject(p, "mqe edit npc ");
						QuestGUIManager.openInfo(p, "&c並左鍵打擊指定的NPC。");
						return false;
					} else if (args.length == 3) {
						q.setQuestNPC(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2])));
						QuestEditorManager.editQuest(p);
						return false;
					}
				case "req":
					if (args.length == 2) {
						QuestEditorManager.editQuestRequirement(p);
						return false;
					}
					RequirementType t = RequirementType.valueOf(args[2]);
					if (args.length >= 5) {
						if (args[4].equalsIgnoreCase("cancel")) {
							QuestEditorManager.editQuestRequirement(p);
							return false;
						}
						switch (t) {
						case ITEM:
							List<ItemStack> itemlist = (List<ItemStack>) q.getRequirements().get(t);
							if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
								for (ItemStack is : itemlist){
									if (is.isSimilar(p.getInventory().getItemInMainHand())) {
										itemlist.remove(Integer.parseInt(args[3]));
										QuestUtil.error(p, "任務需求中已經有這個物件了！");
										QuestEditorManager.editQuestRequirement(p);
										return false;
									}
								}
								itemlist.remove(Integer.parseInt(args[3]));
								itemlist.add(p.getInventory().getItemInMainHand());
							} else {
								QuestUtil.error(p, "物品不可為空！");
								break;
							}
							break;
						case LEVEL:
						case MONEY:
							break;
						case NBTTAG:
							if (((List<String>) q.getRequirements().get(t)).contains(args[4])) {
								QuestUtil.error(p, "任務需求中已經有這個物件了！");
								((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
								break;
							}
							((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
							((List<String>) q.getRequirements().get(t)).add(args[4]);
							break;
						case QUEST:
							if (QuestUtil.getQuest(args[4]) != null) {
								if (((List<String>) q.getRequirements().get(t)).contains(args[4])) {
									QuestUtil.error(p, "任務需求中已經有這個物件了！");
									((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
									break;
								}
								((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
								((List<String>) q.getRequirements().get(t)).add(args[4]);
								break;
							} else {
								QuestUtil.error(p, "找不到指定的任務。");
								break;
							}
						case SCOREBOARD:
							if (((List<String>) q.getRequirements().get(t)).contains(args[4])) {
								QuestUtil.error(p, "任務需求中已經有這個物件了！");
								((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
								break;
							}
							((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
							((List<String>) q.getRequirements().get(t)).add(args[4]);
							break;
						default:
							break;

						}
						QuestEditorManager.editQuestRequirement(p);
						return false;
					} else if (args.length == 4) {
						switch (t) {
						case LEVEL:
							q.getRequirements().put(t, Integer.parseInt(args[3]));
							QuestEditorManager.editQuestRequirement(p);
							break;
						case MONEY:
							q.getRequirements().put(t, Double.parseDouble(args[3]));
							QuestEditorManager.editQuestRequirement(p);
							break;
						case QUEST:
						case SCOREBOARD:
						case NBTTAG:
							QuestEditorListener.registerListeningObject(p,
									"mqe edit req " + t.toString() + " " + Integer.parseInt(args[3]) + " ");
							QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							break;
						case ITEM:
							QuestEditorListener.registerListeningObject(p,
									"mqe edit req " + t.toString() + " " + Integer.parseInt(args[3]) + " ");
							QuestGUIManager.openInfo(p, "&c並將物品拿在手上點擊右鍵，\n&c系統將會自動讀取。");
							break;
						}
						return false;
					} else if (args.length == 3) {
						switch (t) {
						case LEVEL:
							QuestEditorListener.registerListeningObject(p, "mqe edit req " + t.toString() + " ");
							QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							break;
						case MONEY:
							QuestEditorListener.registerListeningObject(p, "mqe edit req " + t.toString() + " ");
							QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							break;
						default:
							break;
						}
						return false;
					}
				case "evt":
					if (args.length == 2) {
						QuestEditorManager.editQuestTrigger(p);
						return false;
					}
					int index = Integer.parseInt(args[2]);
					TriggerType type = TriggerType.valueOf(args[3]);
					if (args.length >= 7) {
						if (type.equals(TriggerType.TRIGGER_STAGE_START) || type.equals(TriggerType.TRIGGER_STAGE_FINISH)){
							int i = Integer.parseInt(args[4]);
							TriggerObject obj = TriggerObject.valueOf(args[5]);
							String s = "";
							for (int j = 6; j < args.length; j++) {
								s = s + args[j] + " ";
							}
							if (index == q.getTriggers().size())
								q.getTriggers().add(new QuestTrigger(type, obj, i, s));
							else
								q.getTriggers().set(index, new QuestTrigger(type, obj, i, s));
							QuestEditorManager.editQuestTrigger(p);
							return false;
						}
						TriggerObject obj = TriggerObject.valueOf(args[4]);
						String s = "";
						for (int j = 5; j < args.length; j++) {
							s = s + args[j] + " ";
						}
						if (index == q.getTriggers().size())
							q.getTriggers().add(new QuestTrigger(type, obj, s));
						else
							q.getTriggers().set(index, new QuestTrigger(type, obj, s));
						QuestEditorManager.editQuestTrigger(p);
						return false;
					} else if (args.length == 6) {
						switch (type) {
						case TRIGGER_STAGE_START:
						case TRIGGER_STAGE_FINISH:
							int i = Integer.parseInt(args[4]);
							TriggerObject obj = TriggerObject.valueOf(args[5]);
							QuestEditorListener.registerListeningObject(p, "mqe edit evt " + index + " "
									+ type.toString() + " " + i + " " + obj.toString() + " ");
							QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
							return false;
						default:
							obj = TriggerObject.valueOf(args[4]);
							String s = "";
							for (int j = 5; j < args.length; j++) {
								s = s + args[j] + " ";
							}
							q.getTriggers().set(index, new QuestTrigger(type, obj, s));
							QuestEditorManager.editQuestTrigger(p);
							return false;
						}
					} else if (args.length == 5) {
						TriggerObject obj = TriggerObject.valueOf(args[4]);
						QuestEditorListener.registerListeningObject(p,
								"mqe edit evt " + index + " " + type.toString() + " " + obj.toString() + " ");
						QuestGUIManager.openInfo(p, "&c並打開聊天窗輸入數值。\n&7(請依照對應的類別輸入內容)");
						return false;
					}
				}
				break;
			case "remove":
				switch (args[1]) {
				case "req":
					RequirementType t = RequirementType.valueOf(args[2]);
					if (args.length == 4) {
						switch (t) {
						case LEVEL:
						case MONEY:
							break;
						case QUEST:
						case SCOREBOARD:
						case NBTTAG:
							((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
							break;
						case ITEM:
							((List<ItemStack>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
							break;
						}
						QuestEditorManager.editQuestRequirement(p);
						QuestUtil.info(p, "指定的物件已經移除。");
						return false;
					}
				case "evt":
					int index = Integer.parseInt(args[2]);
					if (args.length == 3) {
						q.getTriggers().remove(index);
						QuestEditorManager.editQuestTrigger(p);
						QuestUtil.info(p, "指定的物件已經移除。");
						return false;
					}
				}
			case "sa":
			case "saveall":
				if (!QuestEditorManager.isInEditorMode(p)){
					QuestUtil.error(p, "你不在編輯模式中！");
					return false;
				}
				Main.instance.cfg.saveQuest(q);
				Quest.synchronizeLocal(q);
				QuestUtil.info(p, "&a任務 " + q.getQuestName() + " 已經成功儲存至設定檔案！");
				QuestUtil.info(p, "&b任務 " + q.getQuestName() + " 已經設定與伺服器資料中的任務同步！");
				QuestEditorManager.exit(p);
				break;
			case "sc":
			case "savecfg":
				if (!QuestEditorManager.isInEditorMode(p)){
					QuestUtil.error(p, "你不在編輯模式中！");
					return false;
				}
				Main.instance.cfg.saveQuest(q);
				QuestUtil.info(p, "&a任務 " + q.getQuestName() + " 已經成功儲存至設定檔案！");
				QuestEditorManager.exit(p);
				break;
			case "sl":
			case "savelocal":
				if (!QuestEditorManager.isInEditorMode(p)){
					QuestUtil.error(p, "你不在編輯模式中！");
					return false;
				}
				Quest.synchronizeLocal(q);
				QuestUtil.info(p, "&b任務 " + q.getQuestName() + " 已經設定與伺服器資料中的任務同步！");
				QuestEditorManager.exit(p);
				break;
			}
		}
		return false;
	}
}
