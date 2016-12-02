package me.Cutiemango.MangoQuest.commands.edtior;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.listeners.QuestEditorListener;
import me.Cutiemango.MangoQuest.manager.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;

public class CommandNewQuest {
	
	// /mq e newquest args[2] args[3]
	public static void execute(Player sender, String[] args){
		if (args.length == 2){
			QuestEditorManager.edit(sender, new Quest());
			QuestEditorManager.createQuest(sender);
			return;
		}
		else if (args.length > 2){
			switch (args[2]) {
			case "id":
				setInternalID(sender, args);
				break;
			case "name":
				setQuestName(sender, args);
				break;
			case "create":
				create(sender);
				break;
			}
		}
	}
	
	private static void setInternalID(Player p, String[] args){
		if (args.length == 3){
			QuestGUIManager.openInfo(p, "&9&l新建任務》\n&0請關閉視窗，\n&0並輸入要新建的&c&l內部ID&0。\n&0(建議使用英數混合)");
			QuestEditorListener.registerListeningObject(p, "mq e newquest id ");
			return;
		}
		if (args[3].equalsIgnoreCase("cancel")){
			QuestEditorManager.createQuest(p);
			return;
		}
		QuestEditorManager.getCurrentEditingQuest(p).setInternalID(args[3]);
		QuestUtil.info(p, "&a已經成功設定任務內部ID： " + args[3]);
		QuestEditorManager.createQuest(p);
	}
	
	private static void setQuestName(Player p, String[] args){
		if (args.length == 3){
			QuestGUIManager.openInfo(p, "&9&l新建任務》\n&0請關閉視窗，並輸入\n&0新建任務的&c&l顯示名稱&0。");
			QuestEditorListener.registerListeningObject(p, "mq e newquest name ");
			return;
		}
		if (args[3].equalsIgnoreCase("cancel")){
			QuestEditorManager.createQuest(p);
			return;
		}
		QuestEditorManager.getCurrentEditingQuest(p).setQuestName(args[3]);
		QuestUtil.info(p, "&a已經成功設定任務顯示名稱： " + args[3]);
		QuestEditorManager.createQuest(p);
	}
	
	private static void create(Player p){
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		if (q.getInternalID() != null && q.getQuestName() != null){
			QuestUtil.info(p, "&6新建任務： " + q.getQuestName() + " &a成功&6&l！");
			QuestEditorManager.editQuest(p);
		}else{
			QuestUtil.error(p, "新建任務所需的資料不足！請確認輸入後再創建！");
			QuestEditorManager.createQuest(p);
			return;
		}
	}

}
