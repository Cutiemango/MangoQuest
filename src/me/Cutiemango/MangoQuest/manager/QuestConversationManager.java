package me.Cutiemango.MangoQuest.manager;

import java.util.LinkedList;
import java.util.Optional;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.conversation.QuestChoice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import net.md_5.bungee.api.chat.TextComponent;

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
		TextComponent p1 = new TextComponent(QuestUtil.translateColor(cp.getDefaultTitleString()));
		p1.addExtra("\n");
		cp.setCurrentBook(new LinkedList<>(QuestUtil.createList(p1)));
		openConversation(p, cp);
		return cp;
	}
	
	/**
	 * Update player's conversation with a specified progress.
	 * @param p The player to start a new conversation
	 * @param cp The progress to updtae
	 */
	public static void openConversation(Player p, ConversationProgress cp){
		QuestGUIManager.openBook(p, cp.getCurrentBook().toArray(new TextComponent[cp.getCurrentBook().size()]));
	}
	
	/**
	 * Gets a model conversation by internal id.
	 * @param s The id of the conversation.
	 */
	public static Optional<QuestConversation> getConversation(String s){
		return Optional.ofNullable(QuestStorage.Conversations.get(s));
	}
	
	/**
	 * Gets a conversation progress by a specified player.
	 * @param p The player.
	 */
	public static Optional<ConversationProgress> getConvProgress(Player p){
		return Optional.ofNullable(QuestStorage.ConvProgresses.get(p.getName()));
	}
	
	/**
	 * Gets a model choice by internal id.
	 * @param p The player.
	 */
	public static Optional<QuestChoice> getChoiceByName(String s){
		return Optional.ofNullable(QuestStorage.Choices.get(s));
	}
	
	/**
	 * Gets a choice data by a specified player.
	 * @param p The player.
	 */
	public static Optional<QuestChoice> getChoice(Player p){
		return Optional.ofNullable(QuestStorage.ChoiceProgresses.get(p.getName()));
	}
	
	

}
