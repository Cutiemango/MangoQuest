package me.Cutiemango.MangoQuest;

import me.Cutiemango.MangoQuest.conversation.*;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class QuestStorage
{

	// Saved With InternalID
	public static LinkedHashMap<String, Quest> localQuests = new LinkedHashMap<>();

	// Saved With PlayerName
	public static Map<String, QuestPlayerData> playerData = new HashMap<>();
	public static Map<String, ConversationProgress> conversationProgress = new HashMap<>();
	public static Map<String, QuestChoice> choiceProgress = new HashMap<>();

	public static Map<String, QuestConversation> localConversations = new HashMap<>();
	public static Set<FriendConversation> friendConversations = new HashSet<>();
	public static Map<Quest, StartTriggerConversation> startTriggerConversations = new HashMap<>();

	public static Map<String, QuestChoice> localChoices = new HashMap<>();

	public static Map<Material, String> translationMap = new EnumMap<>(Material.class);
	public static Map<EntityType, String> entityTypeMap = new EnumMap<>(EntityType.class);

	public static String prefix = ChatColor.GOLD + "MangoQuest>";

	public static void clear()
	{
		localQuests = new LinkedHashMap<>();
		playerData = new HashMap<>();
		conversationProgress = new HashMap<>();
		friendConversations = new HashSet<>();
		localChoices = new HashMap<>();
		choiceProgress = new HashMap<>();
		localConversations = new HashMap<>();
	}

}
