package me.Cutiemango.MangoQuest;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import net.md_5.bungee.api.ChatColor;

public class QuestStorage {
	
	// Saved With InternalID
	public static Map<String, Quest> Quests = new HashMap<String, Quest>();

	// Saved With PlayerName
	public static Map<String, QuestPlayerData> Players = new HashMap<String, QuestPlayerData>();
	
	public static Map<Material, String> TranslateMap = new EnumMap<Material, String>(Material.class);
	public static Map<EntityType, String> EntityTypeMap = new EnumMap<EntityType, String>(EntityType.class);
	
	public static HashMap<Integer, QuestNPC> NPCMap = new HashMap<>();
	
	public static final String prefix = ChatColor.GOLD + "MangoQuest>";

}
