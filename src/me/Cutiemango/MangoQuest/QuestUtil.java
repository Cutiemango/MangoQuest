package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.conversation.QuestChoice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;

public class QuestUtil
{

	public static String translateColor(String s)
	{
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	{
		Main.instance.handler.sendTitle(p, fadeIn, stay, fadeOut, title, subtitle);
	}

	public static List<Quest> getGivenNPCQuests(NPC npc)
	{
		List<Quest> l = new ArrayList<>();
		for (Quest q : QuestStorage.Quests.values())
		{
			if (q.isCommandQuest())
				continue;
			else
				if (q.getQuestNPC().equals(npc))
					l.add(q);
				else
					continue;
		}
		return l;
	}

	public static <T> List<T> convert(Set<T> set)
	{
		return new ArrayList<T>(set);
	}

	public enum QuestTitleEnum
	{
		ACCEPT, FINISH, QUIT;
	}

	public static void sendQuestTitle(Player target, Quest quest, QuestTitleEnum e)
	{
		switch (e)
		{
			case ACCEPT:
				sendTitle(target, 1, 3, 1, "&b&l接受任務", quest.getQuestName());
				break;
			case FINISH:
				sendTitle(target, 1, 3, 1, "&6&l完成任務", quest.getQuestName());
				break;
			case QUIT:
				sendTitle(target, 1, 3, 1, "&c&l放棄任務", quest.getQuestName());
				break;
		}
	}

	public static QuestPlayerData getData(Player p)
	{
		return QuestStorage.Players.get(p.getName());
	}

	public static String getNPCMessage(int id, int fp)
	{
		if (!QuestStorage.NPCMap.containsKey(id))
			QuestStorage.NPCMap.put(id, new QuestNPC());
		return QuestStorage.NPCMap.get(id).getNPCMessage(fp);
	}

	public static List<QuestConversation> getConversations(int id, int fp)
	{
		if (!QuestStorage.NPCMap.containsKey(id))
			QuestStorage.NPCMap.put(id, new QuestNPC());
		return QuestStorage.NPCMap.get(id).getConversations(fp);
	}

	public static Quest getQuest(String s)
	{
		return QuestStorage.Quests.get(s);
	}

	public static void info(Player p, String s)
	{
		p.sendMessage(QuestStorage.prefix + " " + translateColor(s));
		return;
	}

	public static void error(Player p, String s)
	{
		p.sendMessage(translateColor("&c&lError> " + s));
		return;
	}

	public static void warnCmd(String s)
	{
		Bukkit.getLogger().warning("解析插件內容發生錯誤，請回報開發者。");
		Bukkit.getLogger().warning(s);
	}

	public static String convertTime(long l)
	{
		String s = "";

		long days = l / 86400000;
		long hours = (l % 86400000) / 3600000;
		long minutes = ((l % 86400000) % 3600000) / 60000;
		long seconds = (((l % 86400000) % 3600000) % 60000) / 1000;

		if (days > 0)
			s += days + " 天,";
		if (hours > 0)
			s += hours + " 小時,";
		if (minutes > 0)
			s += minutes + " 分鐘,";
		if (seconds > 0)
			s += seconds + " 秒";
		return s;
	}

	/**
	 * Will compare stacks by name, amount, data, display name/lore and
	 * enchantments
	 * 
	 * @param one
	 *            ItemStack to compare
	 * @param two
	 *            ItemStack to compare to
	 * @return true if equal, else return false
	 */
	public static boolean compareItem(ItemStack one, ItemStack two, boolean ignoreAmount)
	{
		if (one == null && two != null || one != null && two == null)
		{
			return false;
		}
		if (one == null && two == null)
		{
			return true;
		}
		if (one.getType().name() != two.getType().name())
		{
			return false;
		}
		else
			if ((one.getAmount() != two.getAmount()) && ignoreAmount == false)
			{
				return false;
			}
			else
				if (one.getData().equals(two.getData()) == false)
				{
					return false;
				}
		if (one.hasItemMeta() || two.hasItemMeta())
		{

			if (one.hasItemMeta() && two.hasItemMeta() == false)
			{
				return false;
			}
			else
				if (one.hasItemMeta() == false && two.hasItemMeta())
				{
					return false;
				}
				else
					if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName() == false)
					{
						return false;
					}
					else
						if (one.getItemMeta().hasDisplayName() == false && two.getItemMeta().hasDisplayName())
						{
							return false;
						}
						else
							if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore() == false)
							{
								return false;
							}
							else
								if (one.getItemMeta().hasLore() == false && two.getItemMeta().hasLore())
								{
									return false;
								}
								else
									if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName()
											&& ChatColor.stripColor(one.getItemMeta().getDisplayName())
													.equals(ChatColor.stripColor(two.getItemMeta().getDisplayName())) == false)
									{
										return false;
									}
									else
										if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore()
												&& one.getItemMeta().getLore().equals(two.getItemMeta().getLore()) == false)
										{
											return false;
										}

		}

		if (one.getEnchantments().equals(two.getEnchantments()) == false)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@SafeVarargs
	public static <T> List<T> createList(T... args)
	{
		List<T> list = new ArrayList<T>();
		for (T obj : args)
		{
			list.add(obj);
		}
		return list;
	}

	public static QuestConversation getConvByName(String s)
	{
		return QuestStorage.Conversations.get(s);
	}

	public static ConversationProgress getConvProgress(Player p)
	{
		return QuestStorage.ConvProgresses.get(p.getName());
	}

	public static QuestChoice getChoice(Player p)
	{
		return QuestStorage.ChoiceProgresses.get(p.getName());
	}

	public static QuestChoice getChoiceByName(String s)
	{
		return QuestStorage.Choices.get(s);
	}

	public static String translate(Material material, short data)
	{
		switch (material)
		{
			case STONE:
				switch (data)
				{
					case 1:
						return "花崗岩";
					case 2:
						return "平滑花崗岩";
					case 3:
						return "閃長岩";
					case 4:
						return "平滑閃長岩";
					case 5:
						return "安山岩";
					case 6:
						return "平滑安山岩";
				}
				return "石頭";
			case GRASS:
				return "草";
			case DIRT:
				switch (data)
				{
					case 1:
						return "粗泥";
					case 2:
						return "灰壤";
				}
				return "泥土";
			case COBBLESTONE:
				return "鵝卵石";
			case WOOD:
				switch (data)
				{
					case 1:
						return "杉木材";
					case 2:
						return "樺木材";
					case 3:
						return "叢木材";
					case 4:
						return "相思木材";
					case 5:
						return "黑像木材";
				}
				return "橡木材";
			case SAPLING:
				switch (data)
				{
					case 1:
						return "杉木樹苗";
					case 2:
						return "樺木樹苗";
					case 3:
						return "叢林木樹廟";
					case 4:
						return "相思木樹苗";
					case 5:
						return "黑像木樹苗";
				}
				return "橡木樹苗";
			case BEDROCK:
				return "基岩";
			case SAND:
				switch (data)
				{
					case 1:
						return "紅沙";
				}
				return "沙子";
			case GRAVEL:
				return "沙礫";
			case GOLD_ORE:
				return "金礦";
			case IRON_ORE:
				return "鐵礦";
			case COAL_ORE:
				return "煤礦";
			case LOG:
				switch (data)
				{
					case 1:
						return "杉木原木";
					case 2:
						return "樺木原木";
					case 3:
						return "叢林原木";
				}
				return "橡木原木";
			case LEAVES:
				switch (data)
				{
					case 1:
						return "杉木樹葉";
					case 2:
						return "樺木樹葉";
					case 3:
						return "叢林木樹葉";
				}
				return "橡木樹葉";
			case SPONGE:
				switch (data)
				{
					case 1:
						return "濕海綿";
				}
				return "海綿";
			case GLASS:
				return "玻璃";
			case LAPIS_ORE:
				return "青金石礦";
			case LAPIS_BLOCK:
				return "青金石磚";
			case DISPENSER:
				return "發射器";
			case SANDSTONE:
				switch (data)
				{
					case 1:
						return "浮雕砂岩";
					case 2:
						return "平滑砂岩";
				}
				return "砂岩";
			case NOTE_BLOCK:
				return "音諧盒";
			case POWERED_RAIL:
				return "動力鐵軌";
			case DETECTOR_RAIL:
				return "壓力鐵軌";
			case PISTON_STICKY_BASE:
				return "黏性活塞";
			case WEB:
				return "蜘蛛網";
			case LONG_GRASS:
				switch (data)
				{
					case 2:
						return "蕨";
				}
				return "草";
			case DEAD_BUSH:
				return "枯灌木";
			case PISTON_BASE:
				return "活塞";
			case WOOL:
				switch (data)
				{
					case 1:
						return "橘色羊毛";
					case 2:
						return "桃色羊毛";
					case 3:
						return "淺藍羊毛";
					case 4:
						return "黃色羊毛";
					case 5:
						return "淺綠羊毛";
					case 6:
						return "粉紅羊毛";
					case 7:
						return "灰色羊毛";
					case 8:
						return "淺灰色羊毛";
					case 9:
						return "青色羊毛";
					case 10:
						return "紫色羊毛";
					case 11:
						return "藍色羊毛";
					case 12:
						return "棕色羊毛";
					case 13:
						return "綠色羊毛";
					case 14:
						return "紅色羊毛";
					case 15:
						return "黑色羊毛";
				}
				return "羊毛";
			case YELLOW_FLOWER:
				return "蒲公英";
			case RED_ROSE:
				switch (data)
				{
					case 1:
						return "藍色蝴蝶蘭";
					case 2:
						return "紫紅球花";
					case 3:
						return "雛草";
					case 4:
						return "紅色鬱金香";
					case 5:
						return "橘色鬱金香";
					case 6:
						return "白色鬱金香";
					case 7:
						return "粉紅色鬱金香";
					case 8:
						return "雛菊";
				}
				return "玫瑰花";
			case BROWN_MUSHROOM:
				return "香菇";
			case RED_MUSHROOM:
				return "蘑菇";
			case GOLD_BLOCK:
				return "金磚";
			case IRON_BLOCK:
				return "鐵磚";
			case STEP:
				switch (data)
				{
					case 1:
						return "砂岩半磚";
					case 2:
						return "鵝卵石半磚";
					case 3:
						return "紅磚半磚";
					case 4:
						return "石磚半磚";
					case 5:
						return "地獄磚半磚";
					case 6:
						return "石英半磚";
				}
				return "石半磚";
			case BRICK:
				return "紅磚";
			case TNT:
				return "TNT";
			case BOOKSHELF:
				return "書櫃";
			case MOSSY_COBBLESTONE:
				return "青苔石";
			case OBSIDIAN:
				return "黑曜石";
			case TORCH:
				return "火把";
			case WOOD_STAIRS:
				return "橡木樓梯";
			case CHEST:
				return "儲物箱";
			case DIAMOND_ORE:
				return "鑽石礦";
			case DIAMOND_BLOCK:
				return "鑽石磚";
			case WORKBENCH:
				return "工作台";
			case FURNACE:
				return "熔爐";
			case LADDER:
				return "梯子";
			case RAILS:
				return "鐵軌";
			case COBBLESTONE_STAIRS:
				return "石樓梯";
			case LEVER:
				return "控制桿";
			case STONE_PLATE:
				return "石製壓力板";
			case WOOD_PLATE:
				return "木製壓力板";
			case REDSTONE_ORE:
				return "紅石礦";
			case REDSTONE_TORCH_ON:
				return "紅石火把";
			case STONE_BUTTON:
				return "石製按鈕";
			case SNOW:
				return "雪";
			case ICE:
				return "冰";
			case SNOW_BLOCK:
				return "雪";
			case CACTUS:
				return "仙人掌";
			case CLAY:
				return "黏土塊";
			case JUKEBOX:
				return "唱片機";
			case FENCE:
				return "柵欄";
			case PUMPKIN:
				return "南瓜";
			case NETHERRACK:
				return "地獄石";
			case SOUL_SAND:
				return "靈魂砂";
			case GLOWSTONE:
				return "螢光石";
			case JACK_O_LANTERN:
				return "南瓜燈";
			case STAINED_GLASS:
				switch (data)
				{
					case 1:
						return "橘色色玻璃";
					case 2:
						return "桃色色玻璃";
					case 3:
						return "淺藍色玻璃";
					case 4:
						return "黃色色玻璃";
					case 5:
						return "淺綠色玻璃";
					case 6:
						return "粉紅色玻璃";
					case 7:
						return "灰色色玻璃";
					case 8:
						return "淺灰色色玻璃";
					case 9:
						return "青色色玻璃";
					case 10:
						return "紫色色玻璃";
					case 11:
						return "藍色色玻璃";
					case 12:
						return "棕色色玻璃";
					case 13:
						return "綠色色玻璃";
					case 14:
						return "紅色色玻璃";
					case 15:
						return "黑色色玻璃";
				}
				return "白色玻璃";
			case TRAP_DOOR:
				return "地板門";
			case MONSTER_EGGS:
				switch (data)
				{
					case 1:
						return "鵝卵石怪物蛋";
					case 2:
						return "石磚怪物蛋";
					case 3:
						return "青苔石磚怪物蛋";
					case 4:
						return "裂石磚怪物蛋";
					case 5:
						return "浮雕石磚怪物蛋";
				}
				return "石頭怪物蛋";
			case SMOOTH_BRICK:
				switch (data)
				{
					case 1:
						return "青苔石磚";
					case 2:
						return "裂石磚";
					case 3:
						return "浮雕石磚";
				}
				return "石磚";
			case IRON_FENCE:
				return "鐵欄杆";
			case THIN_GLASS:
				return "玻璃片";
			case MELON_BLOCK:
				return "西瓜磚";
			case VINE:
				return "藤蔓";
			case FENCE_GATE:
				return "柵欄門";
			case BRICK_STAIRS:
				return "紅磚階梯";
			case SMOOTH_STAIRS:
				return "石磚階梯";
			case MYCEL:
				return "菌絲土";
			case WATER_LILY:
				return "荷葉";
			case NETHER_BRICK:
				return "地獄磚";
			case NETHER_FENCE:
				return "地獄柵欄";
			case NETHER_BRICK_STAIRS:
				return "地獄磚階梯";
			case ENCHANTMENT_TABLE:
				return "附魔台";
			case ENDER_PORTAL_FRAME:
				return "終界傳送門";
			case ENDER_STONE:
				return "終界石";
			case DRAGON_EGG:
				return "龍蛋";
			case REDSTONE_LAMP_OFF:
				return "紅石燈";
			case WOOD_STEP:
				switch (data)
				{
					case 1:
						return "杉木半磚";
					case 2:
						return "樺木半磚";
					case 3:
						return "叢林木半磚";
					case 4:
						return "相思木半磚";
					case 5:
						return "黑橡木半磚";
				}
				return "橡木半磚";
			case SANDSTONE_STAIRS:
				return "砂岩樓梯";
			case EMERALD_ORE:
				return "綠寶石礦";
			case ENDER_CHEST:
				return "終界箱";
			case TRIPWIRE_HOOK:
				return "絆線鉤";
			case EMERALD_BLOCK:
				return "綠寶石磚";
			case SPRUCE_WOOD_STAIRS:
				return "杉木樓梯";
			case BIRCH_WOOD_STAIRS:
				return "樺木樓梯";
			case JUNGLE_WOOD_STAIRS:
				return "叢林木樓梯";
			case BEACON:
				return "烽火台";
			case COBBLE_WALL:
				switch (data)
				{
					case 1:
						return "青苔石牆";
				}
				return "鵝卵石強";
			case WOOD_BUTTON:
				return "按鈕";
			case ANVIL:
				switch (data)
				{
					case 1:
						return "輕微損耗的鐵砧";
					case 2:
						return "嚴重損耗的鐵砧";
				}
				return "鐵砧";
			case TRAPPED_CHEST:
				return "陷阱儲物箱";
			case GOLD_PLATE:
				return "感重壓力板(輕)";
			case IRON_PLATE:
				return "感重壓力板(重)";
			case DAYLIGHT_DETECTOR:
				return "陽光感測器";
			case REDSTONE_BLOCK:
				return "紅石磚";
			case QUARTZ_ORE:
				return "地獄石英礦";
			case HOPPER:
				return "漏斗";
			case QUARTZ_BLOCK:
				switch (data)
				{
					case 1:
						return "浮雕石英磚";
					case 2:
						return "柱狀石英磚";
				}
				return "石英磚";
			case QUARTZ_STAIRS:
				return "石英階梯";
			case ACTIVATOR_RAIL:
				return "觸發鐵軌";
			case DROPPER:
				return "投擲器";
			case STAINED_CLAY:
				switch (data)
				{
					case 1:
						return "橘色黏土塊";
					case 2:
						return "洋紅黏土塊";
					case 3:
						return "淺藍黏土塊";
					case 4:
						return "黃色黏土塊";
					case 5:
						return "淺綠色黏土塊";
					case 6:
						return "粉紅黏土塊";
					case 7:
						return "灰色黏土塊";
					case 8:
						return "淺灰黏土塊";
					case 9:
						return "青色黏土塊";
					case 10:
						return "紫色黏土塊";
					case 11:
						return "藍色黏土塊";
					case 12:
						return "棕色黏土塊";
					case 13:
						return "綠色黏土塊";
					case 14:
						return "紅色黏土塊";
					case 15:
						return "黑色黏土塊";
				}
				return "白色黏土塊";
			case STAINED_GLASS_PANE:
				switch (data)
				{
					case 1:
						return "橘色玻璃片";
					case 2:
						return "洋紅玻璃片";
					case 3:
						return "淺藍玻璃片";
					case 4:
						return "黃色玻璃片";
					case 5:
						return "淺綠色玻璃片";
					case 6:
						return "粉紅玻璃片";
					case 7:
						return "灰色玻璃片";
					case 8:
						return "淺灰玻璃片";
					case 9:
						return "青色玻璃片";
					case 10:
						return "紫色玻璃片";
					case 11:
						return "藍色玻璃片";
					case 12:
						return "棕色玻璃片";
					case 13:
						return "綠色玻璃片";
					case 14:
						return "紅色玻璃片";
					case 15:
						return "黑色玻璃片";
				}
				return "白色玻璃片";
			case LEAVES_2:
				switch (data)
				{
					case 1:
						return "黑橡木樹葉";
				}
				return "相思木樹葉";
			case LOG_2:
				switch (data)
				{
					case 1:
						return "黑橡木原木";
				}
				return "相思木原木";
			case ACACIA_STAIRS:
				return "相思木樓梯";
			case DARK_OAK_STAIRS:
				return "黑橡木樓梯";
			case SLIME_BLOCK:
				return "史萊姆方塊";
			case IRON_TRAPDOOR:
				return "鐵製地板門";
			case PRISMARINE:
				switch (data)
				{
					case 1:
						return "海磷石磚";
					case 2:
						return "暗海磷石";
				}
				return "海磷石";
			case SEA_LANTERN:
				return "海燈籠";
			case HAY_BLOCK:
				return "乾草捆";
			case CARPET:
				switch (data)
				{
					case 1:
						return "橘色地毯";
					case 2:
						return "洋紅地毯";
					case 3:
						return "淺藍地毯";
					case 4:
						return "黃色地毯";
					case 5:
						return "淺綠色地毯";
					case 6:
						return "粉紅地毯";
					case 7:
						return "灰色地毯";
					case 8:
						return "淺灰地毯";
					case 9:
						return "青色地毯";
					case 10:
						return "紫色地毯";
					case 11:
						return "藍色地毯";
					case 12:
						return "棕色地毯";
					case 13:
						return "綠色地毯";
					case 14:
						return "紅色地毯";
					case 15:
						return "黑色地毯";
				}
				return "地毯";
			case HARD_CLAY:
				return "硬化黏土";
			case COAL_BLOCK:
				return "煤炭磚";
			case PACKED_ICE:
				return "冰磚";
			case DOUBLE_PLANT:
				switch (data)
				{
					case 1:
						return "紫丁香";
					case 2:
						return "芒草";
					case 3:
						return "大型蕨類";
					case 4:
						return "玫瑰叢";
					case 5:
						return "牡丹花";
				}
				return "向日葵";
			case RED_SANDSTONE:
				switch (data)
				{
					case 1:
						return "浮雕紅砂岩";
					case 2:
						return "平滑紅砂岩";
				}
				return "紅砂岩";
			case RED_SANDSTONE_STAIRS:
				return "紅砂岩樓梯";
			case STONE_SLAB2:
				return "紅砂岩半磚";
			case SPRUCE_FENCE_GATE:
				return "杉木柵欄門";
			case BIRCH_FENCE_GATE:
				return "樺木柵欄門";
			case JUNGLE_FENCE_GATE:
				return "叢林木柵欄門";
			case DARK_OAK_FENCE_GATE:
				return "黑橡木柵欄門";
			case ACACIA_FENCE_GATE:
				return "相思木柵欄門";
			case SPRUCE_FENCE:
				return "杉木柵欄";
			case BIRCH_FENCE:
				return "樺木柵欄";
			case JUNGLE_FENCE:
				return "叢林木柵欄";
			case DARK_OAK_FENCE:
				return "黑橡木柵欄";
			case ACACIA_FENCE:
				return "相思木柵欄";
			case END_ROD:
				return "終界燭";
			case CHORUS_PLANT:
				return "歌萊枝";
			case CHORUS_FLOWER:
				return "歌萊花";
			case PURPUR_BLOCK:
				return "紫珀磚";
			case PURPUR_PILLAR:
				return "柱狀紫珀磚";
			case PURPUR_STAIRS:
				return "紫珀磚階梯";
			case PURPUR_SLAB:
				return "紫珀磚半磚";
			case MAGMA:
				return "岩漿塊";
			case NETHER_WART_BLOCK:
				return "地獄疙瘩塊";
			case RED_NETHER_BRICK:
				return "紅地獄磚";
			case BONE_BLOCK:
				return "骨塊";
			case IRON_SPADE:
				return "鐵鏟";
			case IRON_PICKAXE:
				return "鐵鎬";
			case IRON_AXE:
				return "鐵斧";
			case FLINT_AND_STEEL:
				return "打火石";
			case APPLE:
				return "蘋果";
			case BOW:
				return "弓";
			case ARROW:
				return "箭矢";
			case COAL:
				switch (data)
				{
					case 1:
						return "木炭";
				}
				return "煤炭";
			case DIAMOND:
				return "鑽石";
			case IRON_INGOT:
				return "鐵錠";
			case GOLD_INGOT:
				return "金錠";
			case IRON_SWORD:
				return "鐵劍";
			case WOOD_SWORD:
				return "木劍";
			case WOOD_SPADE:
				return "木鏟";
			case WOOD_PICKAXE:
				return "木鎬";
			case WOOD_AXE:
				return "木斧";
			case STONE_SWORD:
				return "石劍";
			case STONE_SPADE:
				return "石鏟";
			case STONE_PICKAXE:
				return "石鎬";
			case STONE_AXE:
				return "石斧";
			case DIAMOND_SWORD:
				return "鑽石劍";
			case DIAMOND_SPADE:
				return "鑽石鏟";
			case DIAMOND_PICKAXE:
				return "鑽石鎬";
			case DIAMOND_AXE:
				return "鑽石斧";
			case STICK:
				return "木棒";
			case BOWL:
				return "碗";
			case MUSHROOM_SOUP:
				return "蘑菇湯";
			case GOLD_SWORD:
				return "金劍";
			case GOLD_SPADE:
				return "金鏟";
			case GOLD_PICKAXE:
				return "金鎬";
			case GOLD_AXE:
				return "金斧";
			case STRING:
				return "線";
			case FEATHER:
				return "羽毛";
			case SULPHUR:
				return "火藥";
			case WOOD_HOE:
				return "木鋤";
			case STONE_HOE:
				return "石鋤";
			case IRON_HOE:
				return "鐵製鋤頭";
			case DIAMOND_HOE:
				return "鑽石鋤";
			case GOLD_HOE:
				return "金鋤";
			case SEEDS:
				return "種子";
			case WHEAT:
				return "小麥";
			case BREAD:
				return "麵包";
			case LEATHER_HELMET:
				return "皮革帽子";
			case LEATHER_CHESTPLATE:
				return "皮革上衣";
			case LEATHER_LEGGINGS:
				return "皮革褲子";
			case LEATHER_BOOTS:
				return "皮革靴子";
			case CHAINMAIL_HELMET:
				return "鎖鍊頭盔";
			case CHAINMAIL_CHESTPLATE:
				return "鎖鍊胸甲";
			case CHAINMAIL_LEGGINGS:
				return "鎖鍊護腿";
			case CHAINMAIL_BOOTS:
				return "鎖鍊靴子";
			case IRON_HELMET:
				return "鐵製頭盔";
			case IRON_CHESTPLATE:
				return "鐵製胸甲";
			case IRON_LEGGINGS:
				return "鐵製護腿";
			case IRON_BOOTS:
				return "鐵製靴子";
			case DIAMOND_HELMET:
				return "鑽石頭盔";
			case DIAMOND_CHESTPLATE:
				return "鑽石胸甲";
			case DIAMOND_LEGGINGS:
				return "鑽石護腿";
			case DIAMOND_BOOTS:
				return "鑽石靴子";
			case GOLD_HELMET:
				return "黃金頭盔";
			case GOLD_CHESTPLATE:
				return "黃金胸甲";
			case GOLD_LEGGINGS:
				return "黃金護腿";
			case GOLD_BOOTS:
				return "黃金靴子";
			case FLINT:
				return "燧石";
			case PORK:
				return "生豬肉";
			case GRILLED_PORK:
				return "烤豬肉";
			case PAINTING:
				return "繪畫";
			case GOLDEN_APPLE:
				return "金蘋果";
			case SIGN:
				return "告示牌";
			case WOOD_DOOR:
				return "橡木門";
			case BUCKET:
				return "鐵桶";
			case WATER_BUCKET:
				return "水桶";
			case LAVA_BUCKET:
				return "岩漿桶";
			case MINECART:
				return "礦車";
			case SADDLE:
				return "鞍";
			case IRON_DOOR:
				return "鐵門";
			case REDSTONE:
				return "紅石";
			case SNOW_BALL:
				return "雪球";
			case BOAT:
				return "橡木船";
			case LEATHER:
				return "皮革";
			case MILK_BUCKET:
				return "牛奶";
			case CLAY_BRICK:
				return "紅磚頭";
			case CLAY_BALL:
				return "黏土";
			case SUGAR_CANE:
				return "甘蔗";
			case PAPER:
				return "紙";
			case BOOK:
				return "書";
			case SLIME_BALL:
				return "史萊姆球";
			case STORAGE_MINECART:
				return "運輸礦車";
			case POWERED_MINECART:
				return "動力礦車";
			case EGG:
				return "雞蛋";
			case COMPASS:
				return "羅盤";
			case FISHING_ROD:
				return "釣竿";
			case WATCH:
				return "時鐘";
			case GLOWSTONE_DUST:
				return "螢石粉";
			case RAW_FISH:
				switch (data)
				{
					case 1:
						return "生鮭魚";
					case 2:
						return "小丑魚";
					case 3:
						return "河豚";
				}
				return "生魚";
			case COOKED_FISH:
				switch (data)
				{
					case 1:
						return "熟鮭魚片";
				}
				return "烤魚";
			case INK_SACK:
				switch (data)
				{
					case 1:
						return "玫瑰紅";
					case 2:
						return "仙人掌綠";
					case 3:
						return "可可豆";
					case 4:
						return "青金石";
					case 5:
						return "紫色染料";
					case 6:
						return "青色染料";
					case 7:
						return "淺灰色染料";
					case 8:
						return "灰色染料";
					case 9:
						return "粉紅色染料";
					case 10:
						return "淺綠染料";
					case 11:
						return "蒲公英黃";
					case 12:
						return "淺藍色染料";
					case 13:
						return "洋紅色染料";
					case 14:
						return "橘色染料";
					case 15:
						return "骨粉";
				}
				return "墨囊";
			case BONE:
				return "骨頭";
			case SUGAR:
				return "糖";
			case CAKE:
				return "蛋糕";
			case BED:
				return "床";
			case DIODE:
				return "紅石中繼器";
			case COOKIE:
				return "餅乾";
			case SHEARS:
				return "剪刀";
			case MELON:
				return "西瓜";
			case PUMPKIN_SEEDS:
				return "南瓜種子";
			case MELON_SEEDS:
				return "西瓜種子";
			case RAW_BEEF:
				return "生牛肉";
			case COOKED_BEEF:
				return "牛排";
			case RAW_CHICKEN:
				return "生雞肉";
			case COOKED_CHICKEN:
				return "烤雞";
			case ROTTEN_FLESH:
				return "腐肉";
			case ENDER_PEARL:
				return "終界珍珠";
			case BLAZE_ROD:
				return "烈焰桿";
			case GHAST_TEAR:
				return "幽靈之淚";
			case GOLD_NUGGET:
				return "金粒";
			case NETHER_STALK:
				return "地獄疙瘩";
			case POTION:
				return "水瓶";
			case GLASS_BOTTLE:
				return "玻璃瓶";
			case SPIDER_EYE:
				return "蜘蛛眼";
			case FERMENTED_SPIDER_EYE:
				return "發酵蜘蛛眼";
			case BLAZE_POWDER:
				return "烈焰粉";
			case MAGMA_CREAM:
				return "熔岩球";
			case BREWING_STAND_ITEM:
				return "釀造台";
			case CAULDRON_ITEM:
				return "鍋釜";
			case EYE_OF_ENDER:
				return "終界之眼";
			case SPECKLED_MELON:
				return "鑲金西瓜";
			case MONSTER_EGG:
				return "生怪蛋";
			case EXP_BOTTLE:
				return "經驗瓶";
			case FIREBALL:
				return "火焰彈";
			case BOOK_AND_QUILL:
				return "書和羽毛筆";
			case WRITTEN_BOOK:
				return "成書";
			case EMERALD:
				return "綠寶石";
			case ITEM_FRAME:
				return "物品展示框";
			case FLOWER_POT_ITEM:
				return "花盆";
			case CARROT_ITEM:
				return "胡蘿蔔";
			case POTATO_ITEM:
				return "馬鈴薯";
			case BAKED_POTATO:
				return "烤馬鈴薯";
			case POISONOUS_POTATO:
				return "毒馬鈴薯";
			case EMPTY_MAP:
				return "空白地圖";
			case GOLDEN_CARROT:
				return "金胡蘿蔔";
			case SKULL_ITEM:
				switch (data)
				{
					case 1:
						return "凋零骷髏頭顱";
					case 2:
						return "殭屍頭顱";
					case 3:
						return "頭顱";
					case 4:
						return "苦力怕頭顱";
					case 5:
						return "龍首";
				}
				return "骷髏頭顱";
			case CARROT_STICK:
				return "胡蘿蔔釣竿";
			case NETHER_STAR:
				return "地獄之星";
			case PUMPKIN_PIE:
				return "南瓜派";
			case FIREWORK:
				return "煙花火箭";
			case FIREWORK_CHARGE:
				return "火藥球";
			case ENCHANTED_BOOK:
				return "附魔書";
			case REDSTONE_COMPARATOR:
				return "紅石比較器";
			case NETHER_BRICK_ITEM:
				return "地獄磚";
			case QUARTZ:
				return "地獄石英";
			case EXPLOSIVE_MINECART:
				return "TNT礦車";
			case HOPPER_MINECART:
				return "漏斗礦車";
			case PRISMARINE_SHARD:
				return "海磷碎片";
			case PRISMARINE_CRYSTALS:
				return "海磷晶體";
			case RABBIT:
				return "生兔肉";
			case COOKED_RABBIT:
				return "考兔肉";
			case RABBIT_STEW:
				return "兔肉湯";
			case RABBIT_FOOT:
				return "兔子腳";
			case RABBIT_HIDE:
				return "兔子皮";
			case ARMOR_STAND:
				return "盔甲座";
			case IRON_BARDING:
				return "鐵製馬鎧";
			case GOLD_BARDING:
				return "黃金製馬鎧";
			case DIAMOND_BARDING:
				return "鑽石製馬鎧";
			case LEASH:
				return "拴繩";
			case NAME_TAG:
				return "命名牌";
			case MUTTON:
				return "生羊肉";
			case COOKED_MUTTON:
				return "烤羊肉";
			case BANNER:
				switch (data)
				{
					case 1:
						return "紅色旗幟";
					case 2:
						return "綠色旗幟";
					case 3:
						return "棕色旗幟";
					case 4:
						return "藍色旗幟";
					case 5:
						return "紫色旗幟";
					case 6:
						return "青色旗幟";
					case 7:
						return "淺灰旗幟";
					case 8:
						return "灰色旗幟";
					case 9:
						return "粉紅旗幟";
					case 10:
						return "淺綠色旗幟";
					case 11:
						return "黃色旗幟";
					case 12:
						return "淺藍旗幟";
					case 13:
						return "洋紅旗幟";
					case 14:
						return "橘色旗幟";
					case 15:
						return "白色旗幟";
				}
				return "黑色旗幟";
			case END_CRYSTAL:
				return "終界水晶";
			case SPRUCE_DOOR_ITEM:
				return "杉木門";
			case BIRCH_DOOR_ITEM:
				return "樺木門";
			case JUNGLE_DOOR_ITEM:
				return "叢林木門";
			case ACACIA_DOOR_ITEM:
				return "相思木門";
			case DARK_OAK_DOOR_ITEM:
				return "黑橡木門";
			case CHORUS_FRUIT:
				return "歌萊果";
			case CHORUS_FRUIT_POPPED:
				return "爆開的歌萊果";
			case BEETROOT:
				return "甜菜根";
			case BEETROOT_SEEDS:
				return "甜菜種子";
			case BEETROOT_SOUP:
				return "甜菜湯";
			case DRAGONS_BREATH:
				return "龍之吐息";
			case SPLASH_POTION:
				return "藥水";
			case SPECTRAL_ARROW:
				return "追跡之箭";
			case TIPPED_ARROW:
				return "藥水箭";
			case LINGERING_POTION:
				return "藥水";
			case SHIELD:
				return "盾牌";
			case ELYTRA:
				return "鞘翅";
			case BOAT_SPRUCE:
				return "杉木船";
			case BOAT_BIRCH:
				return "樺木船";
			case BOAT_JUNGLE:
				return "叢林木船";
			case BOAT_ACACIA:
				return "相思木船";
			case BOAT_DARK_OAK:
				return "黑橡木船";
			case GOLD_RECORD:
				return "13唱片";
			case GREEN_RECORD:
				return "cat唱片";
			case RECORD_3:
				return "blocks唱片";
			case RECORD_4:
				return "chirp唱片";
			case RECORD_5:
				return "far唱片";
			case RECORD_6:
				return "mall唱片";
			case RECORD_7:
				return "mellohi唱片";
			case RECORD_8:
				return "stal唱片";
			case RECORD_9:
				return "strad唱片";
			case RECORD_10:
				return "ward唱片";
			case RECORD_11:
				return "11唱片";
			case RECORD_12:
				return "wait唱片";
			default:
				return "未知的物品";
		}
	}

	public static String translate(EntityType e)
	{
		if (!QuestStorage.EntityTypeMap.containsKey(e))
			return "未知";
		else
			return QuestStorage.EntityTypeMap.get(e);
	}
}
