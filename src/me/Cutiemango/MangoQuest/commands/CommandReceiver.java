package me.Cutiemango.MangoQuest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;

public class CommandReceiver implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (args.length == 0){
			sendHelp(p);
			return false;
		}
		switch(args[0]){
		case "conv":
		case "c":
			ConversationCommand.execute(p, args);
			break;
		case "quest":
		case "q":
			QuestCommand.execute(p, args);
			break;
		case "editor":
		case "e":
			QuestEditorCommand.execute(p, args);
			break;
		case "reload":
			Main.instance.reload();
			QuestUtil.info(p, "&6重新讀取資料成功。");
			break;
		default:
			break; 
		}
		return false;
	}
	
	public void sendHelp(Player p){
		QuestUtil.info(p, "&e&l指令幫助：");
		QuestUtil.info(p, "&f/mq quest (help) - 查詢關於&a&l任務&f的指令");
		QuestUtil.info(p, "&f/mq editor (help) - 查詢關於&9&l編輯器&f的指令");
		QuestUtil.info(p, "&f/mq reload - &c重新讀取設定檔案");
	}

}
