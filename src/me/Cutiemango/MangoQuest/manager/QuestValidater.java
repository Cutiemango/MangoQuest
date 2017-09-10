package me.Cutiemango.MangoQuest.manager;

import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.model.Quest;

public class QuestValidater
{
	public static boolean detailedValidate(Quest before, Quest after)
	{
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getQuestName().equals(after.getQuestName()))
			return false;
		if (!(before.isCommandQuest() == after.isCommandQuest()))
			return false;
		if (!before.isCommandQuest())
		{
			if (!(before.getQuestNPC().getId() == after.getQuestNPC().getId()))
				return false;
		}
		if (!before.getQuestOutline().equals(after.getQuestOutline()))
			return false;
		if (!before.getFailMessage().equals(after.getFailMessage()))
			return false;
		if (!before.getQuestReward().equals(after.getQuestReward()))
			return false;
		if (!(before.isRedoable() == after.isRedoable()))
			return false;
		if (before.isRedoable())
		{
			if (!(before.getRedoDelay() == after.getRedoDelay()))
				return false;
		}
		if (!before.getRequirements().equals(after.getRequirements()))
			return false;
		if (!before.getStages().equals(after.getStages()))
			return false;
		if (!before.getTriggerMap().equals(after.getTriggerMap()))
			return false;
		return true;
	}
	
	public static boolean detailedValidate(QuestConversation before, QuestConversation after)
	{
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getName().equals(after.getName()))
			return false;
		if (!(before.getNPC().getId() == after.getNPC().getId()))
			return false;
		if (!before.getActions().equals(after.getActions()))
			return false;
		if (!(before instanceof FriendConversation && after instanceof FriendConversation))
			return false;
		if (before instanceof FriendConversation && after instanceof FriendConversation)
			if (!(((FriendConversation)before).getReqPoint() == ((FriendConversation)after).getReqPoint()))
				return false;
		if (!(before instanceof StartTriggerConversation && after instanceof StartTriggerConversation))
			return false;
		if (before instanceof StartTriggerConversation && after instanceof StartTriggerConversation)
			if (!((StartTriggerConversation)before).equals(((StartTriggerConversation)after).getQuest()))
				return false;
		return true;
	}

	public static boolean weakValidate(Quest before, Quest after)
	{
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getQuestName().equals(after.getQuestName()))
			return false;
		return true;
	}
}
