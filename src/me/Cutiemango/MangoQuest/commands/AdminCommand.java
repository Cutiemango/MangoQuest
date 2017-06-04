package me.Cutiemango.MangoQuest.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestChatManager;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;

public class AdminCommand implements CommandExecutor
{
	// Command: /mqa [args]
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (!p.isOp())
		{
			QuestChatManager.error(p, Questi18n.localizeMessage("CommandInfo.NoPermission"));
			;
			return false;
		}
		if (args.length > 1)
		{
			switch (args[0])
			{
				case "reload":
					Main.instance.reload();
					QuestChatManager.info(p, "&a" + Questi18n.localizeMessage("CommandInfo.ReloadSuccessful"));
					return true;
				// /mqa nextstage [玩家ID] [任務]
				// /mqa forcetake [玩家ID] [任務]
				// /mqa forcefinish [玩家ID] [任務]
				case "nextstage":
				case "forcetake":
				case "forcefinish":
					if (args.length < 3)
						break;
					Player target = Bukkit.getPlayer(args[1]);
					Quest q = QuestUtil.getQuest(args[2]);
					if (target == null || q == null)
					{
						QuestChatManager.error(p, Questi18n.localizeMessage("CommandInfo.InvalidArgument"));
						return false;
					}
					QuestPlayerData pd = QuestUtil.getData(target);
					if (args[0].equalsIgnoreCase("nextstage"))
						pd.forceNextStage(q, true);
					else
						if (args[0].equalsIgnoreCase("forcetake"))
							pd.forceTake(q, true);
						else
							pd.forceFinish(q, true);
					break;
					// /mqa finishobject [玩家ID] [任務] [物件編號]
				case "finishobject":
					if (args.length < 4)
						break;
					target = Bukkit.getPlayer(args[1]);
					q = QuestUtil.getQuest(args[2]);
					Integer obj = Integer.parseInt(args[3]);
					if (target == null || q == null)
					{
						QuestChatManager.error(p, Questi18n.localizeMessage("CommandInfo.InvalidArgument"));
						return false;
					}
					pd = QuestUtil.getData(target);
					pd.forceFinishObj(q, obj, true);
					break;
					// /mqa removedata [玩家ID]
				case "removedata":
					target = Bukkit.getPlayer(args[1]);
					QuestUtil.clearData(p);
					break;
			}
			QuestChatManager.info(p, Questi18n.localizeMessage("CommandInfo.CommandPerformed"));
			return true;
		}
		sendAdminHelp(p);
		return true;
	}

	public void sendAdminHelp(Player p)
	{
		QuestChatManager.info(p, Questi18n.localizeMessage("AdminCommandHelp.Title"));
		QuestChatManager.info(p, Questi18n.localizeMessage("AdminCommandHelp.Reload"));
		QuestChatManager.info(p, Questi18n.localizeMessage("AdminCommandHelp.NextStage"));
		QuestChatManager.info(p, Questi18n.localizeMessage("AdminCommandHelp.FinishObject"));
		QuestChatManager.info(p, Questi18n.localizeMessage("AdminCommandHelp.TakeQuest"));
		QuestChatManager.info(p, Questi18n.localizeMessage("AdminCommandHelp.FinishQuest"));
		QuestChatManager.info(p, Questi18n.localizeMessage("AdminCommandHelp.RemovePlayerData"));
	}

}
