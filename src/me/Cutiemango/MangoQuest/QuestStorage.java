package me.Cutiemango.MangoQuest;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.conversation.QuestChoice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import net.md_5.bungee.api.ChatColor;

public class QuestStorage
{

	// Saved With InternalID
	public static Map<String, Quest> Quests = new HashMap<>();

	// Saved With PlayerName
	public static Map<String, QuestPlayerData> Players = new HashMap<>();
	public static Map<String, ConversationProgress> ConvProgresses = new HashMap<>();
	public static Map<String, QuestChoice> ChoiceProgresses = new HashMap<>();

	public static Map<String, QuestConversation> Conversations = new HashMap<>();

	public static Map<String, QuestChoice> Choices = new HashMap<>();

	public static Map<Material, String> TranslateMap = new EnumMap<>(Material.class);
	public static Map<EntityType, String> EntityTypeMap = new EnumMap<>(EntityType.class);

	public static HashMap<Integer, QuestNPC> NPCMap = new HashMap<>();

	public static final String prefix = ChatColor.GOLD + "MangoQuest>";

	public static void clear()
	{
		Quests = new HashMap<>();
		Players = new HashMap<>();
		ConvProgresses = new HashMap<>();
		ChoiceProgresses = new HashMap<>();
		Conversations = new HashMap<>();
		Choices = new HashMap<>();
		NPCMap = new HashMap<>();
	}

}
