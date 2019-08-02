package me.Cutiemango.MangoQuest.manager;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;

public class QuestChatManager
{
	public static String translateColor(String s)
	{
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static String toNormalDisplay(String s)
	{
		return QuestChatManager.translateColor(s.replaceAll("&0", "&f").replaceAll("§0", "§f"));
	}
	
	public static String toBookDisplay(String s)
	{
		return QuestChatManager.translateColor(s.replaceAll("&f", "&0").replaceAll("§f", "§0"));
	}
	
	public static String finishedObjectFormat(String s)
	{
		return translateColor("&8&m&o") + ChatColor.stripColor(s);
	}
	
	public static String trimColor(String s)
	{	
		String targetText = translateColor(s);
		boolean escape = false;
		boolean nextTextSplit = false;
		int index = 0;
		String savedText = "";
		for (int i = 0; i < targetText.toCharArray().length; i++)
		{
			if (escape)
			{
				escape = false;
				continue;
			}
			if (targetText.charAt(i) == '§')
			{
				if (nextTextSplit)
				{
					String split = targetText.substring(index, i);
					savedText += ChatColor.getLastColors(split) + ChatColor.stripColor(split);
					index = i;
					nextTextSplit = false;
				}
				escape = true;
				continue;
			}
			nextTextSplit = true;
		}
		String split = targetText.substring(index, targetText.toCharArray().length);
		savedText += ChatColor.getLastColors(split) + ChatColor.stripColor(split);
		return savedText;
	}
	
	public static void info(CommandSender p, String s)
	{
		p.sendMessage(QuestStorage.prefix + " " + translateColor(s));
		return;
	}

	public static void error(CommandSender p, String s)
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
