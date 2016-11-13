package me.Cutiemango.MangoQuest.conversation;

import java.util.List;

import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import net.citizensnpcs.api.npc.NPC;

public class QuestConversation {
	
	private List<QuestBaseAction> action;
	private String name;
	private String id;
	private NPC npc;
	
	private boolean isFriendConv;
	
	public QuestConversation(String s, String internal, NPC n, List<QuestBaseAction> list, boolean f){
		name = s;
		action = list;
		id = internal;
		npc = n;
		isFriendConv = f;
	}
	
	public ConversationProgress startNewConversation(Player p){
		ConversationProgress cp = new ConversationProgress(p, this);
		QuestStorage.ConvProgresses.put(p.getName(), cp);
		QuestGUIManager.openConversation(p);
		return cp;
	}
	
	public List<QuestBaseAction> getActions(){
		return action;
	}
	
	public String getName(){
		return name;
	}
	
	public String getInternalID(){
		return id;
	}
	
	public NPC getNPC(){
		return npc;
	}
	
	public boolean hasNPC(){
		return npc != null;
	}
	
	public boolean isFriendConv(){
		return isFriendConv;
	}

}
