package me.Cutiemango.MangoQuest.commands;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReceiver implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (args.length == 0)
		{
			sendHelp(p);
			return false;
		}
		switch (args[0])
		{
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
			case "conveditor":
			case "ce":
				ConversationEditorCommand.execute(p, args);
				break;
			default:
				sendHelp(p);
				break;
		}
		return false;
	}

	public void sendHelp(Player p)
	{
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.Title"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.Quest"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.Editor"));
	}

}
