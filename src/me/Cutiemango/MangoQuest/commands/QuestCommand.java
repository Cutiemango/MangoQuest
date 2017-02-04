package me.Cutiemango.MangoQuest.commands;

import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.Shopkeeper;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestInitializer;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class QuestCommand{
	
	//Command: /mq quest args[1] args[2]
	public static void execute(Player sender, String args[]){
		if (args.length == 1){
			sendHelp(sender);
			return;
		}
		else if (args.length >= 2){
			if (args[1].equalsIgnoreCase("list")){
				QuestGUIManager.openJourney(sender);
				return;
			}
			else if (args[1].equalsIgnoreCase("help")){
				sendHelp(sender);
				return;
			}
			else if (args[1].equalsIgnoreCase("trade")){
				QuestInitializer im = Main.instance.initManager;
				if (im.hasCitizensEnabled()){
					NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
					if (npc == null || npc.getEntity().getLocation().distance(sender.getLocation()) > 20)
						return;
					Shopkeeper s = Main.instance.initManager.getShopkeepers().getShopkeeperByEntity(npc.getEntity());
					if (s == null)
						return;
					else{
						sender.closeInventory();
						s.openTradingWindow(sender);
						return;
					}
				}
				return;
			}
			if (QuestStorage.Quests.get(args[2]) == null){
				QuestUtil.error(sender, "你所要找的任務不存在！");
				return;
			}
			Quest quest = QuestStorage.Quests.get(args[2]);
			QuestPlayerData qd = QuestUtil.getData(sender);
			switch(args[1]){
			case "view":
				if (qd.getProgress(quest) == null)
					QuestGUIManager.openGUI(sender, new QuestProgress(quest, sender));
				else
					QuestGUIManager.openGUI(sender, qd.getProgress(quest));
				return;
			case "take":
				qd.takeQuest(quest);
				QuestGUIManager.openGUI(sender, qd.getProgress(quest));
				return;
			case "quit":
				qd.quitQuest(quest);
				QuestGUIManager.openJourney(sender);
				return;
			default:
				sendHelp(sender);
				return;
			}
		}
	}
	
	private static void sendHelp(Player p){
		QuestUtil.info(p, "指令幫助：");
		QuestUtil.info(p, "/mq quest list - 查看任務清單");
		QuestUtil.info(p, "/mq quest view [任務內部名稱] - 查看任務資料");
		QuestUtil.info(p, "/mq quest take [任務內部名稱] - 接取指定任務");
		QuestUtil.info(p, "/mq quest quit [任務內部名稱] - 放棄指定任務");
	}

}
