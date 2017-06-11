package me.Cutiemango.MangoQuest.conversation;

import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.QuestBookPage;

public class QuestConversationManager {
	
	/**
	 * Starts a conversation from the beginning.
	 * @param p The player to start a new conversation
	 * @param conv The conversation to start
	 * @return The progress of conversation
	 */
	public static ConversationProgress startConversation(Player p, QuestConversation conv){
		ConversationProgress cp = new ConversationProgress(p, conv);
		QuestStorage.ConvProgresses.put(p.getName(), cp);
		cp.newPage();
		openConversation(p, cp);
		return cp;
	}
	
	/**
	 * Update player's conversation with a specified progress.
	 * @param p The player to start a new conversation
	 * @param cp The progress to updtae
	 */
	public static void openConversation(Player p, ConversationProgress cp){
		QuestGUIManager.openBook(p, cp.getCurrentBook().toArray(new QuestBookPage[cp.getCurrentBook().size()]));
	}
	
	/**
	 * Gets a model conversation by internal id.
	 * @param s The id of the conversation.
	 */
	public static QuestConversation getConversation(String s){
		return QuestStorage.Conversations.get(s);
	}
	
	/**
	 * Gets a conversation progress by a specified player.
	 * @param p The player.
	 */
	public static ConversationProgress getConvProgress(Player p){
		return QuestStorage.ConvProgresses.get(p.getName());
	}
	
	/**
	 * Gets a model choice by internal id.
	 * @param p The player.
	 */
	public static QuestChoice getChoiceByName(String s){
		return QuestStorage.Choices.get(s);
	}
	
	/**
	 * Gets a choice data by a specified player.
	 * @param p The player.
	 */
	public static QuestChoice getChoiceProgress(Player p){
		return QuestStorage.ChoiceProgresses.get(p.getName());
	}
	
	public static QuestBookPage generateNewPage(QuestConversation conv)
	{
		QuestBookPage page = new QuestBookPage().add(QuestChatManager.translateColor("&0「" + conv.getName() + "」"));
		page.changeLine();
		return page;
	}

}
