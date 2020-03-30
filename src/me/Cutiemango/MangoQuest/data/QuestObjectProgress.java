package me.Cutiemango.MangoQuest.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.questobject.CustomQuestObject;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectTalkToNPC;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

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
			if (i == 1 || (cp != null && QuestUtil.getData(cp.getOwner()).hasFinished(cp.getConversation())))
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
