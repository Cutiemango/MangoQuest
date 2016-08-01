package me.Cutiemango.MangoQuest.commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (cmd.getName().equals("mqe")) {
			Quest q = QuestEditorManager.getCurrentEditingQuest(p);
			if (args.length < 2)
				return false;
			switch (args[0]) {
			case "select":
				if (QuestUtil.getQuest(args[1]) == null) {
					QuestUtil.error(p, "你所要找的任務不存在！");
					return false;
				}
				QuestEditorManager.edit(p, QuestUtil.getQuest(args[1]));
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
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
							((List<String>) q.getRequirements().get(t)).add("");
							break;
						case ITEM:
							QuestEditorListener.registerListeningObject(p,
									"mqe edit req " + t.toString() + " " + Integer.parseInt(args[3]));
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並開啟物品欄，將需求物品拿在主手上點擊右鍵，\n&c系統將會自動讀取物品內容。");
							((List<ItemStack>) q.getRequirements().get(t)).add(new ItemStack(Material.GRASS));
							break;
						default:
							break;
						}
					} else {
						switch (t) {
						case LEVEL:
							QuestEditorListener.registerListeningObject(p, "mqe edit req " + t.toString() + " ");
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
							break;
						case MONEY:
							QuestEditorListener.registerListeningObject(p, "mqe edit req " + t.toString() + " ");
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
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
						QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
						return false;
					} else if (args.length == 5) {
						TriggerType type = TriggerType.valueOf(args[2]);
						TriggerObject obj = TriggerObject.valueOf(args[4]);
						if (type.equals(TriggerType.TRIGGER_STAGE_START)
								|| type.equals(TriggerType.TRIGGER_STAGE_FINISH)) {
							QuestEditorListener.registerListeningObject(p,
									"mqe edit evt " + q.getTriggers().size() + " " + type.toString() + " "
											+ Integer.parseInt(args[3]) + " " + obj.toString() + " ");
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
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
						QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
						return false;
					} else if (args.length == 3) {
						q.setQuestName(args[2]);
						QuestEditorManager.editQuest(p);
						return false;
					}
				case "outline":
					if (args.length == 2) {
						QuestEditorListener.registerListeningObject(p, "mqe edit outline ");
						QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
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
						QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
						return false;
					} else if (args.length == 3) {
						q.setRedoable(Boolean.parseBoolean(args[2]));
						QuestEditorManager.editQuest(p);
						return false;
					}
				case "redodelay":
					if (args.length == 2) {
						QuestEditorListener.registerListeningObject(p, "mqe edit redodelay ");
						QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
						return false;
					} else if (args.length == 3) {
						q.setRedoDelay(Long.parseLong(args[2]));
						QuestEditorManager.editQuest(p);
						return false;
					}
				case "npc":
					if (args.length == 2) {
						QuestEditorListener.registerListeningObject(p, "mqe edit npc ");
						QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並右鍵點擊目標NPC。");
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
								if (((List<ItemStack>) q.getRequirements().get(t))
										.contains(p.getInventory().getItemInMainHand())) {
									QuestUtil.error(p, "任務需求中已經有這個物件了！");
									break;
								}
								((List<ItemStack>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
								itemlist.add(p.getInventory().getItemInMainHand());
							} else {
								QuestUtil.error(p, "物品不可為空！");
								break;
							}
							break;
						case LEVEL:
							q.getRequirements().put(t, Integer.parseInt(args[4]));
							break;
						case MONEY:
							q.getRequirements().put(t, Double.parseDouble(args[4]));
							break;
						case NBTTAG:
							if (((List<String>) q.getRequirements().get(t)).contains(args[4])) {
								QuestUtil.error(p, "任務需求中已經有這個物件了！");
								break;
							}
							((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[3]));
							((List<String>) q.getRequirements().get(t)).add(args[4]);
							break;
						case QUEST:
							if (QuestUtil.getQuest(args[4]) != null) {
								if (((List<String>) q.getRequirements().get(t)).contains(args[4])) {
									QuestUtil.error(p, "任務需求中已經有這個物件了！");
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
							break;
						case MONEY:
							q.getRequirements().put(t, Double.parseDouble(args[3]));
							break;
						case QUEST:
						case SCOREBOARD:
						case NBTTAG:
							QuestEditorListener.registerListeningObject(p,
									"mqe edit req " + t.toString() + " " + Integer.parseInt(args[3]) + " ");
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
							break;
						case ITEM:
							QuestEditorListener.registerListeningObject(p,
									"mqe edit req " + t.toString() + " " + Integer.parseInt(args[3]) + " ");
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，並開啟物品欄，\n&c將需求物品拿在主手上點擊右鍵，\n&c系統將會自動讀取物品內容。");
							break;
						}
						return false;
					} else if (args.length == 3) {
						switch (t) {
						case LEVEL:
							QuestEditorListener.registerListeningObject(p, "mqe edit req " + t.toString() + " ");
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
							break;
						case MONEY:
							QuestEditorListener.registerListeningObject(p, "mqe edit req " + t.toString() + " ");
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
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
							QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
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
						QuestGUIManager.openInfo(p, "&c請關閉書本視窗，\n&c並打開聊天窗輸入需求數值\n&7(請依照對應的類別輸入內容)");
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
				break;
			}
		}
		return false;
	}
}
