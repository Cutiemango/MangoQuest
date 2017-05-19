package me.Cutiemango.MangoQuest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;

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
			if (p.isOp()){
				Main.instance.reload();
				QuestUtil.info(p, "&a" + Questi18n.localizeMessage("CommandInfo.ReloadSuccessful"));
			}
			break;
		default:
			break; 
		}
		return false;
	}
	
	public void sendHelp(Player p){
		QuestUtil.info(p, "&e&l" + Questi18n.localizeMessage("CommandHelp.Title"));
		QuestUtil.info(p, "&f" + Questi18n.localizeMessage("CommandHelp.Quest"));
		QuestUtil.info(p, "&f" + Questi18n.localizeMessage("CommandHelp.Editor"));
		QuestUtil.info(p, "&f" + Questi18n.localizeMessage("CommandHelp.Reload"));
		QuestUtil.info(p, "&e&l" + Questi18n.localizeMessage("CommandHelp.Title"));
	}

}
