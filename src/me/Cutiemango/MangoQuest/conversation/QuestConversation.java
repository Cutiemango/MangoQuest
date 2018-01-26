package me.Cutiemango.MangoQuest.conversation;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;
import net.citizensnpcs.api.npc.NPC;

public class QuestConversation
{
	// Only init with command
	public QuestConversation()
	{
		action = new ArrayList<>();
		name = I18n.locMsg("QuestEditor.NotSet");
		id = I18n.locMsg("QuestEditor.NotSet");
		npc = null;
	}
	
	public QuestConversation(String s, String internal, NPC n, List<QuestBaseAction> list)
	{
		name = s;
		action = list;
		id = internal;
		npc = n;
	}

	protected List<QuestBaseAction> action;
	protected String name;
	protected String id;
	protected NPC npc;

	public List<QuestBaseAction> getActions()
	{
		return action;
	}

	public String getName()
	{
		return name;
	}

	public String getInternalID()
	{
		return id;
	}

	public NPC getNPC()
	{
		return npc;
	}

	public boolean hasNPC()
	{
		return npc != null;
	}
	
	public void setInternalID(String s)
	{
		id = s;
	}
	
	public void setName(String s)
	{
		name = s;
	}
	
	public void setNPC(NPC n)
	{
		npc = n;
	}
	
	public void setActions(List<QuestBaseAction> l)
	{
		action = l;
	}
	
	@Override
	public QuestConversation clone()
	{
		return new QuestConversation(name, id, npc, action);
	}
	
	public static void synchronizeLocal(QuestConversation qc)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			ConversationManager.forceQuit(p, qc);
		}
		QuestStorage.Conversations.put(qc.getInternalID(), qc);
	}

}
