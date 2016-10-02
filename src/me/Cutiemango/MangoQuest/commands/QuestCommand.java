package me.Cutiemango.MangoQuest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;

public class QuestCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (cmd.getName().equals("mq")){
			if (args.length <= 1){
				if (args[0].equalsIgnoreCase("list")){
					QuestGUIManager.openJourney(p);
					return true;
				}
				else if (args[0].equalsIgnoreCase("nextconv")){
					if (QuestUtil.getConvProgress(p) != null){
						QuestUtil.getConvProgress(p).retrieve();
						QuestUtil.getConvProgress(p).nextAction();
					}
					return true;
				}
				sendHelp(p);
				return false;
			}
//			if (QuestStorage.Quests.get(args[1]) == null){
//				QuestUtil.error(p, "你所要找的任務不存在！");
//				return false;
//			}
//			Quest quest = QuestStorage.Quests.get(args[1]);
			QuestPlayerData qd = QuestUtil.getData(p);
			switch(args[0]){
				case "view":
					Quest quest = QuestStorage.Quests.get(args[1]);
					if (qd.getProgress(quest) == null){
						QuestGUIManager.openGUI(p, new QuestProgress(quest, p));
						return true;
					}
					QuestGUIManager.openGUI(p, qd.getProgress(quest));
					return true;
				case "take":
					quest = QuestStorage.Quests.get(args[1]);
					qd.takeQuest(quest);
					QuestGUIManager.openGUI(p, qd.getProgress(quest));
					return true;
				case "quit":
					quest = QuestStorage.Quests.get(args[1]);
					qd.quitQuest(quest);
					QuestGUIManager.openJourney(p);
					return true;
				case "choose":
					if (QuestUtil.getChoice(p) != null)
						QuestUtil.getChoice(p).choose(p, Integer.parseInt(args[1]));
					return true;
				case "openconv":
					if (QuestUtil.getConvByName(args[1]) != null){
						QuestUtil.getConvByName(args[1]).startNewConversation(p);
						return true;
					}
				case "callconv":
					if (QuestUtil.getConvProgress(p) != null){
						QuestGUIManager.updateConversation(p, QuestUtil.getConvProgress(p));
						return true;
					}
				}
		
		}
		return false;
	}
	
	private void sendHelp(Player p){
		QuestUtil.info(p, "指令幫助：");
		QuestUtil.info(p, "/mq list - 查看任務清單");
		QuestUtil.info(p, "/mq view [任務內部名稱] - 查看任務資料");
		QuestUtil.info(p, "/mq take [任務內部名稱] - 接取指定任務");
		QuestUtil.info(p, "/mq quit [任務內部名稱] - 放棄指定任務");
	}

}
