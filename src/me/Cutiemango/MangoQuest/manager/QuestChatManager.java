package me.Cutiemango.MangoQuest.manager;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;

public class QuestChatManager
{
	public static String translateColor(String s)
	{
		return ChatColor.translateAlternateColorCodes('&', s);
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
	
	public static void logCmd(Level lv, String msg)
	{
		Bukkit.getLogger().log(lv, "[MangoQuest] " + msg);
	}
	
	public static void syntaxError(Player p, String req, String entry)
	{
		info(p, I18n.locMsg("SyntaxError.IllegalArgument"));
		info(p, I18n.locMsg("SyntaxError.ReqEntry") + req);
		info(p, I18n.locMsg("SyntaxError.YourEntry") + entry);
		return;
	}
	
}
