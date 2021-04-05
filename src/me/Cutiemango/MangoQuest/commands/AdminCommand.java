package me.Cutiemango.MangoQuest.commands;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.manager.config.QuestConfigManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AdminCommand implements CommandExecutor
{
	// Command: /mqa [args]
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("MangoQuest.AdminCommand")) {
			QuestChatManager.error(sender, I18n.locMsg("CommandInfo.NoPermission"));
			return false;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				Main.getInstance().reload();
				QuestChatManager.info(sender, "&a" + I18n.locMsg("CommandInfo.ReloadSuccessful"));
				return true;
			} else {
				sendAdminHelp(sender);
				return false;
			}
		} else if (args.length > 1) {
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
				return false;
			}
			QuestPlayerData pd = QuestUtil.getData(target);
			switch (args[0]) {
				// /mqa nextstage [ID] [quest]
				// /mqa forcetake [ID] [quest]
				// /mqa forcefinish [ID] [quest]
				case "nextstage":
					Optional<Quest> quest = getQuestArg(args, 2);
					quest.ifPresentOrElse(q -> pd.forceNextStage(q, true),
							() -> QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument")));
					break;
				case "forcetake":
					quest = getQuestArg(args, 2);
					quest.ifPresentOrElse(q -> pd.forceTake(q, true),
							() -> QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument")));
					break;
				case "forcefinish":
					quest = getQuestArg(args, 2);
					quest.ifPresentOrElse(q -> pd.forceFinish(q, true),
							() -> QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument")));
					break;
				case "forcequit":
					quest = getQuestArg(args, 2);
					quest.ifPresentOrElse(q -> pd.forceQuit(q, true),
							() -> QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument")));
					break;
				// /mqa finishobject [ID] [quest] [objindex]
				case "finishobject":
					if (args.length < 4)
						return false;
					quest = getQuestArg(args, 2);
					int obj = Integer.parseInt(args[3]);
					if (!quest.isPresent()) {
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
						return false;
					}
					pd.forceFinishObj(quest.get(), obj, true);
					break;
				// /mqa removedata [ID]
				case "removedata":
					target.kickPlayer(I18n.locMsg("CommandInfo.KickForDataClearing"));
					QuestConfigManager.getSaver().clearPlayerData(target);
					QuestChatManager.info(sender, I18n.locMsg("CommandInfo.PlayerDataRemoved"));
					break;
				// /mqa friendpoint [ID] add/set [NPC] [amount]
				case "friendpoint":
					if (args.length < 5)
						return false;
					if (!QuestValidater.validateNPC(args[3])) {
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
						return false;
					}
					if (!QuestValidater.validateInteger(args[4])) {
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
						return false;
					}
					NPC npc = Main.getHooker().getNPC(args[3]);
					int amount = Integer.parseInt(args[4]);
					switch (args[2]) {
						case "add":
							pd.addNPCfp(npc.getId(), amount);
							QuestChatManager.info(target,
									I18n.locMsg("CommandInfo.FriendPointAdded", target.getName(), npc.getName(), Integer.toString(amount)));
							return false;
						case "set":
							pd.setNPCfp(npc.getId(), amount);
							QuestChatManager.info(target,
									I18n.locMsg("CommandInfo.FriendPointSet", target.getName(), npc.getName(), Integer.toString(amount)));
							return false;
					}
					break;
				// /mqa opennpc [ID] [NPCID] [trade]
				case "opennpc":
					if (args.length < 4)
						return false;
					if (!QuestValidater.validateNPC(args[2])) {
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.InvalidArgument"));
						return false;
					}
					npc = Main.getHooker().getNPC(args[2]);
					boolean trade = Boolean.parseBoolean(args[3]);
					QuestBookGUIManager.openNPCInfo(target, npc, trade);
					break;
			}
			QuestChatManager.info(sender, I18n.locMsg("CommandInfo.CommandExecuted"));
			return true;
		}
		sendAdminHelp(sender);
		return true;
	}

	private Optional<Quest> getQuestArg(String[] args, int index) {
		return index < args.length ? Optional.ofNullable(QuestUtil.getQuest(args[index])) : Optional.empty();
	}

	public void sendAdminHelp(CommandSender p) {
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
