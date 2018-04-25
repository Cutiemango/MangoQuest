package me.Cutiemango.MangoQuest.commands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.nisovin.shopkeepers.Shopkeeper;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestNPCManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.manager.reward.QuestRewardManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.PluginHooker;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.GUIOption;
import me.Cutiemango.MangoQuest.objects.reward.RewardCache;
import me.old.RPGshop.GUIManager;
import me.old.RPGshop.InventoryGUI.TradeGUI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import ru.nightexpress.unrealshop.shop.ShopManager;
import ru.nightexpress.unrealshop.shop.objects.UShop;
import ru.nightexpress.unrealshop.shop.types.OpenSource;

public class QuestCommand
{

	// Command: /mq quest args[1] args[2] args[3]
	public static void execute(Player sender, String args[])
	{
		if (args.length == 1)
		{
			sendHelp(sender);
			return;
		}
		else
			if (args.length >= 2)
			{
				switch (args[1])
				{
					case "list":
						sender.closeInventory();
						QuestBookGUIManager.openJourney(sender);
						return;
					case "help":
						sendHelp(sender);
						return;
					case "option":
						if (!QuestValidater.validateNPC(args[2]))
							return;
						if (QuestNPCManager.getOption(args[3]) != null)
						{
							GUIOption option = QuestNPCManager.getOption(args[3]);
							option.execute(sender);
						}
						return;
					case "trade":
						Player target = sender;
						if (args.length == 4)
							target = Bukkit.getPlayer(args[3]);
						PluginHooker hooker = Main.getHooker();
						if (hooker.hasCitizensEnabled())
						{
							NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
							if (npc == null || !QuestUtil.getData(target).isNearNPC(npc))
								return;
							if (hooker.hasShopkeepersEnabled())
							{
								Shopkeeper s = hooker.getShopkeepers().getShopkeeperByEntity(npc.getEntity());
								if (s != null)
								{
									target.closeInventory();
									s.openTradingWindow(target);
									return;
								}
							}
							if (hooker.hasRPGshopEnabled())
							{
								GUIManager.openGUI(new TradeGUI(Integer.toString(npc.getId()), target));
								return;
							}
							if (Bukkit.getPluginManager().isPluginEnabled("UnrealShop"))
							{
								for (UShop localUShop : ShopManager.getShops())
								{
									if (ArrayUtils.contains(localUShop.getNpcId(), npc.getId()))
									{
										ShopManager.openShop(target, localUShop, OpenSource.NPC);
										break;
									}
								}
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
						if (!QuestUtil.getData(sender).isCurrentlyDoing(quest))
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
						QuestBookGUIManager.openJourney(sender);
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
						return;
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
