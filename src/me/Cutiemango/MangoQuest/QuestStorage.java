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
	public static final LinkedHashMap<String, Quest> localQuests = new LinkedHashMap<>();

	// Saved With PlayerName
	public static final HashMap<String, QuestPlayerData> playerData = new HashMap<>();
	public static final HashMap<String, ConversationProgress> conversationProgress = new HashMap<>();
	public static final HashMap<String, QuestChoice> choiceProgress = new HashMap<>();

	public static final HashMap<String, QuestConversation> localConversations = new HashMap<>();
	public static final HashSet<FriendConversation> friendConversations = new HashSet<>();
	public static final HashMap<Quest, StartTriggerConversation> startTriggerConversations = new HashMap<>();

	public static final HashMap<String, QuestChoice> localChoices = new HashMap<>();

	public static final EnumMap<Material, String> translationMap = new EnumMap<>(Material.class);
	public static final EnumMap<EntityType, String> entityTypeMap = new EnumMap<>(EntityType.class);

	public static String prefix = ChatColor.GOLD + "MangoQuest>";

	public static void clear()
	{
		localQuests.clear();
		playerData.clear();
		conversationProgress.clear();
		friendConversations.clear();
		localChoices.clear();
		choiceProgress.clear();
		localConversations.clear();
	}

}
