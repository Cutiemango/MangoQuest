package me.Cutiemango.MangoQuest.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestConfigManager;
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
			QuestChatManager.error(p, I18n.locMsg("CommandInfo.NoPermission"));
			return false;
		}
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("reload"))
			{
				Main.instance.reload();
				QuestChatManager.info(p, "&a" + I18n.locMsg("CommandInfo.ReloadSuccessful"));
				return true;
			}
			else
			{
				sendAdminHelp(p);
				return false;
			}
		}
		else if (args.length > 1)
		{
			switch (args[0])
			{
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
						QuestChatManager.error(p, I18n.locMsg("CommandInfo.InvalidArgument"));
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
						QuestChatManager.error(p, I18n.locMsg("CommandInfo.InvalidArgument"));
						return false;
					}
					pd = QuestUtil.getData(target);
					pd.forceFinishObj(q, obj, true);
					break;
					// /mqa removedata [玩家ID]
				case "removedata":
					target = Bukkit.getPlayer(args[1]);
					p.kickPlayer(I18n.locMsg("CommandInfo.KickForDataClearing"));
					QuestConfigManager.getSaver().clearPlayerData(p);
					QuestChatManager.info(p, I18n.locMsg("CommandInfo.PlayerDataRemoved"));
					break;
			}
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.CommandPerformed"));
			return true;
		}
		sendAdminHelp(p);
		return true;
	}

	public void sendAdminHelp(Player p)
	{
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.Title"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.Reload"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.NextStage"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.FinishObject"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.TakeQuest"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.FinishQuest"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.RemovePlayerData"));
	}

}
