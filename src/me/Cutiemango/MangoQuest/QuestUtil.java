package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;

public class QuestUtil {
	
	public static String translateColor(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
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
	
	public static void warnCmd(Class<?> clazz, String s){
		Bukkit.getLogger().warning("解析 " + clazz.getClass().getName() + ".class 時發生錯誤，請檢查設定檔。");
		Bukkit.getLogger().warning("若您確認這是個BUG，請回報開發者。");
		Bukkit.getLogger().warning(s);
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
