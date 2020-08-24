package me.Cutiemango.MangoQuest.commands;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestNPCManager;
import me.Cutiemango.MangoQuest.manager.QuestRewardManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.GUIOption;
import me.Cutiemango.MangoQuest.objects.reward.RewardCache;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QuestCommand
{

	// Command: /mq quest args[1] args[2] args[3]
	public static void execute(Player sender, String[] args)
	{
		if (args.length == 1)
		{
			sendHelp(sender);
		}
		else
			if (args.length >= 2)
			{
				switch (args[1])
				{
					case "list":
						if (args.length == 2)
						{
							sender.closeInventory();
							QuestBookGUIManager.openJourneyMenu(sender);
							return;
						}
						else
						{
							switch (args[2])
							{
								case "progress":
									sender.closeInventory();
									QuestBookGUIManager.openProgressJourney(sender);
									return;
								case "doable":
									sender.closeInventory();
									QuestBookGUIManager.openDoableJourney(sender);
									return;
								case "finished":
									sender.closeInventory();
									QuestBookGUIManager.openFinishedJourney(sender);
									return;
							}
						}
						return;
					case "help":
						sendHelp(sender);
						return;
					case "option":
						Player target = sender;
						if (QuestNPCManager.getOption(args[3]) == null || !QuestValidater.validateNPC(args[2]) || !QuestUtil.getData(target).isNearNPC(Main.getHooker().getNPC(args[2])))
							return;
						GUIOption option = QuestNPCManager.getOption(args[3]);
						option.execute(sender);
						return;
					case "trade":
						target = sender;
						if (args.length == 4)
							target = Bukkit.getPlayer(args[3]);

						NPC npc = Main.getHooker().getNPC(args[2]);
						if (npc == null || !QuestUtil.getData(target).isNearNPC(npc))
							return;
						if (Main.getHooker().hasShopkeepersEnabled())
						{
							Shopkeeper s = ShopkeepersAPI.getShopkeeperRegistry().getShopkeeperByEntity(npc.getEntity());
							if (s != null)
							{
								target.closeInventory();
								s.openTradingWindow(target);
								return;
							}
						}
						return;
					case "reward":
						if (args.length >= 3)
						{
							switch (args[2])
							{
								case "select":
									if (QuestRewardManager.hasRewardCache(sender))
									{
										QuestRewardManager.getRewardCache(sender).openGUI();
										return;
									}
									if (args.length == 4)
									{
										Quest q = QuestUtil.getQuest(args[3]);
										if (q == null)
										{
											QuestChatManager.error(sender, I18n.locMsg("CommandInfo.QuestNotFound"));
											return;
										}
										QuestRewardManager.registerCache(sender, q);
									}
									return;
								case "add":
									if (!QuestRewardManager.hasRewardCache(sender))
										return;
									if (args.length == 4)
									{
										RewardCache cache = QuestRewardManager.getRewardCache(sender);
										cache.addChoice(Integer.parseInt(args[3]));
									}
									return;
								case "remove":
									if (!QuestRewardManager.hasRewardCache(sender))
										return;
									if (args.length == 4)
									{
										RewardCache cache = QuestRewardManager.getRewardCache(sender);
										cache.removeChoice(Integer.parseInt(args[3]));
									}
									return;
							}
						}
						return;
				}
				
				if (args.length < 3 || QuestStorage.Quests.get(args[2]) == null)
				{
					QuestChatManager.error(sender, I18n.locMsg("CommandInfo.QuestNotFound"));
					return;
				}
				Quest quest = QuestStorage.Quests.get(args[2]);
				QuestPlayerData qd = QuestUtil.getData(sender);
				switch (args[1])
				{
					case "view":
						if (!qd.isCurrentlyDoing(quest))
							QuestBookGUIManager.openGUIwithProgress(sender, new QuestProgress(quest, sender));
						else
							QuestBookGUIManager.openGUIwithProgress(sender, qd.getProgress(quest));
						return;
					case "take":
						qd.takeQuest(quest, true);
						return;
					case "cquit":
						if (!quest.isQuitable())
						{
							QuestChatManager.error(sender, I18n.locMsg("QuestQuitMsg.Denied"));
							return;
						}
						qd.quitQuest(quest);
						QuestBookGUIManager.openJourneyMenu(sender);
						return;
					case "quit":
						if (!quest.isQuitable())
						{
							QuestChatManager.error(sender, I18n.locMsg("QuestQuitMsg.Denied"));
							return;
						}
						QuestBookGUIManager.openQuitGUI(sender, quest);
						return;
					default:
						sendHelp(sender);
				}
			}
	}

	private static void sendHelp(Player p)
	{
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.Title"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.List"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.View"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.Take"));
		QuestChatManager.info(p, I18n.locMsg("CommandHelp.Quit"));
	}

}
