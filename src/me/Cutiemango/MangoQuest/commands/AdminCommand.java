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
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.manager.config.QuestConfigManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class AdminCommand implements CommandExecutor
{
	// Command: /mqa [args]
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!sender.isOp())
		{
			QuestChatManager.error(sender, I18n.locMsg("CommandInfo.NoPermission"));
			return false;
		}
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("reload"))
			{
				Main.instance.reload();
				QuestChatManager.info(sender, "&a" + I18n.locMsg("CommandInfo.ReloadSuccessful"));
				return true;
			}
			else
			{
				sendAdminHelp(sender);
				return false;
			}
		}
		else if (args.length > 1)
		{
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null)
			{
				QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
				return false;
			}
			switch (args[0])
			{
				// /mqa nextstage [玩家ID] [任務]
				// /mqa forcetake [玩家ID] [任務]
				// /mqa forcefinish [玩家ID] [任務]
				case "nextstage":
				case "forcetake":
				case "forcefinish":
					if (args.length < 3)
						return false;
					Quest q = QuestUtil.getQuest(args[2]);
					if (q == null)
					{
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
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
						return false;
					q = QuestUtil.getQuest(args[2]);
					Integer obj = Integer.parseInt(args[3]);
					if (q == null)
					{
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
						return false;
					}
					pd = QuestUtil.getData(target);
					pd.forceFinishObj(q, obj, true);
					break;
					// /mqa removedata [玩家ID]
				case "removedata":
					target.kickPlayer(I18n.locMsg("CommandInfo.KickForDataClearing"));
					QuestConfigManager.getSaver().clearPlayerData(target);
					QuestChatManager.info(sender, I18n.locMsg("CommandInfo.PlayerDataRemoved"));
					break;
				// /mqa friendpoint add/set [ID] [NPC] [amount]
				case "friendpoint":
					if (args.length < 5)
						return false;
					if (QuestValidater.validateNPC(sender, args[3], true) && QuestValidater.validateInteger(sender, args[4], true))
					{
						NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3]));
						int amount = Integer.parseInt(args[4]);
						pd = QuestUtil.getData(target);
						switch (args[1])
						{
							case "add":
								pd.addNPCfp(npc.getId(), amount);
								QuestChatManager.info(target, I18n.locMsg("CommandInfo.FriendPointAdded", target.getName(), npc.getName(), Integer.toString(amount)));
								return false;
							case "set":
								pd.setNPCfp(npc.getId(), amount);
								QuestChatManager.info(target, I18n.locMsg("CommandInfo.FriendPointSet", target.getName(), npc.getName(), Integer.toString(amount)));
								return false;
						}
					}
					
			}
			QuestChatManager.info(sender, I18n.locMsg("CommandInfo.CommandExecuted"));
			return true;
		}
		sendAdminHelp(sender);
		return true;
	}

	public void sendAdminHelp(CommandSender p)
	{
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.Title"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.Reload"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.NextStage"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.FinishObject"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.TakeQuest"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.FinishQuest"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.RemovePlayerData"));
		QuestChatManager.info(p, I18n.locMsg("AdminCommandHelp.FriendPoint"));
	}

}
