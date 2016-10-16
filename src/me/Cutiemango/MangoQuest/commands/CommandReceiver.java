package me.Cutiemango.MangoQuest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReceiver implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
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
		default:
			break;
		}
		return false;
	}
	
	

}
