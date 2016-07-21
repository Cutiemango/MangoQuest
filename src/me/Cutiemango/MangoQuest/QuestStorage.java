package me.Cutiemango.MangoQuest;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

public class QuestStorage {
	
	// Saved With InternalID
	public static Map<String, Quest> Quests = new HashMap<String, Quest>();

	// Saved With PlayerName
	public static Map<String, QuestPlayerData> Players = new HashMap<String, QuestPlayerData>();
	
	public static final String prefix = ChatColor.GOLD + "MangoQuest>";

}
