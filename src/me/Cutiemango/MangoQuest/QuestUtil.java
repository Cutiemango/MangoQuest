package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;

public class QuestUtil {
	
	public static String translateColor(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static TextComponent convertItemStacktoHoverEvent(boolean f, ItemStack it) {
		TextComponent itemname = new TextComponent();
		ItemStack is = it.clone();
		if (!is.getItemMeta().hasDisplayName()) {
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + translate(is.getType()));
			is.setItemMeta(im);
			if (f)
				itemname = new TextComponent(QuestUtil.translateColor("&8&m&o") + translate(is.getType()));
			else
				itemname = new TextComponent(ChatColor.BLACK + translate(is.getType()));
		} else {
			if (f)
				itemname = new TextComponent(QuestUtil.translateColor("&8&m&o") + translate(is.getType()));
			else
				itemname = new TextComponent(is.getItemMeta().getDisplayName());
		}
		
		net.minecraft.server.v1_10_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();
		
		BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(itemJson) };
		itemname.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, hoverEventComponents));

		return itemname;
	}
	
	public static ItemStack getItemStack(FileConfiguration config, String path) {
		Material m = Material.getMaterial(config.getString(path + ".類別"));
		int amount = config.getInt(path + ".數量");
		ItemStack is = new ItemStack(m, amount);
		if (config.getString(path + ".名稱") != null) {
			String name = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".名稱"));
			List<String> lore = new ArrayList<>();
			for (String s : config.getStringList(path + ".註解")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(name);
			im.setLore(lore);
			is.setItemMeta(im);
		}
		return is;
	}
	
	public static TextComponent convertNPCtoHoverEvent(boolean f, NPC npc) {
		TextComponent name = new TextComponent();
		if (npc == null)
			return name;
		ItemStack is = new ItemStack(Material.SIGN);
		ItemMeta im = is.getItemMeta();
		Location loc = npc.getEntity().getLocation();
		im.setDisplayName(npc.getName());
		im.setLore(Arrays.asList(new String[] { ChatColor.GOLD + "目前位於：",
				ChatColor.WHITE + "- 世界： " + loc.getWorld().getName(), ChatColor.WHITE + "- 座標： ("
						+ Math.floor(loc.getX()) + " , " + loc.getY() + " , " + Math.floor(loc.getZ()) + ")" }));
		is.setItemMeta(im);
		
		if (f)
			name = new TextComponent(QuestUtil.translateColor("&8&m&o") + ChatColor.stripColor(npc.getName()));
		else
			name = new TextComponent(npc.getName());

		net.minecraft.server.v1_10_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();

		BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(itemJson) };
		name.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, hoverEventComponents));

		return name;
	}
	
	public static void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title,
			String subtitle) {
		if (title != null) {
			title = ChatColor.translateAlternateColorCodes('&', title);
			PacketPlayOutTitle ppot = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + title + "\"}"), fadeIn, stay, fadeOut);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppot);
		}
		if (subtitle != null) {
			subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
			PacketPlayOutTitle ppot = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"), fadeIn, stay, fadeOut);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppot);
		}
	}

	public enum QuestTitleEnum{
		ACCEPT, FINISH, QUIT;
	}
	
	public static void sendQuestTitle(Player target, Quest quest, QuestTitleEnum e) {
		switch(e){
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
	
	public static QuestPlayerData getData(Player p){
		return QuestStorage.Players.get(p.getName());
	}
	
	public static Quest getQuest(String s){
		return QuestStorage.Quests.get(s);
	}
	
	public static void info(Player p, String s){
		p.sendMessage(QuestStorage.prefix + " " + translateColor(s));
		return;
	}
	
	public static void error(Player p, String s){
		p.sendMessage(translateColor("&cError> " + s));
		return;
	}
	
	public static String convertTime(long l){
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
	
	public static String translate(Material m){
		if (!QuestStorage.TranslateMap.containsKey(m))
			return "未知";
		else return QuestStorage.TranslateMap.get(m);
	}
	
	public static String translate(EntityType e){
		if (!QuestStorage.EntityTypeMap.containsKey(e))
			return "未知";
		else return QuestStorage.EntityTypeMap.get(e);
	}
}
