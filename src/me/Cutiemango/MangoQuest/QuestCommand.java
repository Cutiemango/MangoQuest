package me.Cutiemango.MangoQuest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (cmd.getName().equals("mq")){
			if (args.length >= 2){
				if (args[0].equalsIgnoreCase("view")){
					if (QuestStorage.Quests.get(args[1]) == null)
						p.sendMessage("你所要找的任務不存在！");
					else{
						Quest quest = QuestStorage.Quests.get(args[1]);
						QuestPlayerData qd = QuestUtil.getData(p);
						if (qd.getProgress(quest) == null){
							QuestGUIManager.openViewGUI(quest, p);
							return false;
						}
						QuestGUIManager.openGUI(qd.getProgress(quest), p);
					}
				}
				else if (args[0].equalsIgnoreCase("take")){
					if (QuestStorage.Quests.get(args[1]) == null)
						p.sendMessage("你所要找的任務不存在！");
					else{
						Quest quest = QuestStorage.Quests.get(args[1]);
						QuestPlayerData qd = QuestUtil.getData(p);
						qd.takeQuest(quest);
					}
				}
				else if (args[0].equalsIgnoreCase("quit")){
					if (QuestStorage.Quests.get(args[1]) == null)
						p.sendMessage("你所要找的任務不存在！");
					else{
						Quest quest = QuestStorage.Quests.get(args[1]);
						QuestPlayerData qd = QuestUtil.getData(p);
						qd.quitQuest(quest);
					}
				}
			}
		}
		return false;
	}
	

}
