package me.Cutiemango.MangoQuest.data;

import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.questobject.CustomQuestObject;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;

public class QuestObjectProgress
{

	private boolean isFinished = false;
	private SimpleQuestObject obj;
	private ConversationProgress cp;
	private int i;

	public QuestObjectProgress(SimpleQuestObject object, int amount)
	{
		obj = object;
		i = amount;
	}

	public void checkIfFinished()
	{
		if (obj instanceof CustomQuestObject)
			return;
		if (obj instanceof QuestObjectTalkToNPC)
		{
			if (i == 1 || (cp != null && cp.isFinished()))
				isFinished = true;
			return;
		}
		if (obj instanceof NumerableObject)
		{
			if (((NumerableObject) obj).getAmount() == i)
				isFinished = true;
		}
		else
			if (obj instanceof SimpleQuestObject)
			{
				if (i == 1)
					isFinished = true;
			}
	}

	public void newConversation(Player p)
	{
		if (obj.hasConversation())
		{
			cp = ConversationManager.startConversation(p, obj.getConversation());
			cp.nextAction();
			return;
		}
	}

	public void openConversation(Player p)
	{
		if (cp == null)
		{
			if (obj.hasConversation())
				newConversation(p);
			return;
		}
		else
		{
			if (cp.isFinished())
				finish();
			else
				ConversationManager.openConversation(p, cp);
		}
		return;
	}

	public SimpleQuestObject getObject()
	{
		return obj;
	}

	public int getProgress()
	{
		return i;
	}

	public boolean isFinished()
	{
		return isFinished;
	}

	public void finish()
	{
		isFinished = true;
	}
	
	public void addProgress(int p)
	{
		i+=p;
	}

	public void setProgress(int p)
	{
		i = p;
	}

}
