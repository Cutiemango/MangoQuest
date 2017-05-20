package me.Cutiemango.MangoQuest.commands;

import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.Shopkeeper;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestInitializer;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
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
			if (args.length < 3 || QuestStorage.Quests.get(args[2]) == null){
				QuestUtil.error(sender, Questi18n.localizeMessage("CommandInfo.QuestNotFound"));
				return;
			}
			Quest quest = QuestStorage.Quests.get(args[2]);
			QuestPlayerData qd = QuestUtil.getData(sender);
			switch(args[1]){
			case "view":
				if (!QuestUtil.getData(sender).isCurrentlyDoing(quest))
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
		QuestUtil.info(p, "&e&l" + Questi18n.localizeMessage("CommandHelp.Title"));
		QuestUtil.info(p, Questi18n.localizeMessage("CommandQuestHelp.List"));
		QuestUtil.info(p, Questi18n.localizeMessage("CommandQuestHelp.View"));
		QuestUtil.info(p, Questi18n.localizeMessage("CommandQuestHelp.Take"));
		QuestUtil.info(p, Questi18n.localizeMessage("CommandQuestHelp.Quit"));
	}

}
