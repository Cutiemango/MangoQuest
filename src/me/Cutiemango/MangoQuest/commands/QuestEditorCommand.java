package me.Cutiemango.MangoQuest.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.commands.edtior.CommandAddnew;
import me.Cutiemango.MangoQuest.commands.edtior.CommandEdit;
import me.Cutiemango.MangoQuest.commands.edtior.CommandNewQuest;
import me.Cutiemango.MangoQuest.commands.edtior.CommandRemove;
import me.Cutiemango.MangoQuest.manager.QuestEditorManager;
import me.Cutiemango.MangoQuest.model.Quest;

public class QuestEditorCommand{
	
	private static List<String> confirm = new ArrayList<>();
	
	//Command: /mq editor args[1] args[2]
	public static void execute(Player sender, String[] args){
		if (!sender.isOp())
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(sender);
		if (args.length == 1){
			QuestEditorManager.mainGUI(sender);
			return;
		}
		else if (args.length == 2){
			switch(args[1]){
				case "edit":
					QuestEditorManager.editGUI(sender);
					return;
				case "remove":
					QuestEditorManager.removeGUI(sender);
					return;
				case "help":
					QuestUtil.info(sender, "請使用 /mq e 來開啟介面！");
					return;
				case "exit":
					QuestEditorManager.exit(sender);
					QuestEditorManager.mainGUI(sender);
					return;
				case "gui":
					QuestEditorManager.editQuest(sender);
					return;
				case "newquest":
					CommandNewQuest.execute(sender, args);
					return;
				case "sa":
				case "saveall":
					if (!QuestEditorManager.isInEditorMode(sender)){
						QuestUtil.error(sender, "你不在編輯模式中！");
						return;
					}
					if (q.getStages().isEmpty()){
						QuestUtil.error(sender, "此任務沒有任何任務目標，無法被儲存！");
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
					if (q.getStages().isEmpty()){
						QuestUtil.error(sender, "此任務沒有任何任務目標，無法被儲存！");
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
					if (q.getStages().isEmpty()){
						QuestUtil.error(sender, "此任務沒有任何任務目標，無法被儲存！");
						return;
					}
					Quest.synchronizeLocal(q);
					QuestUtil.info(sender, "&b任務 " + q.getQuestName() + " 已經設定與伺服器資料中的任務同步！");
					break;
			}
		}
		else{
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
				CommandAddnew.execute(q, sender, args);
				return;
			case "edit":
				CommandEdit.execute(q, sender, args);
				return;
			case "newquest":
				CommandNewQuest.execute(sender, args);
				return;
			case "remove":
				CommandRemove.execute(q, sender, args);
				return;
			}
			QuestEditorManager.mainGUI(sender);
		}
	}

}
