package me.Cutiemango.MangoQuest.conversation;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class QuestBaseAction
{

	private EnumAction action;
	private String obj;

	public QuestBaseAction(EnumAction act, String st)
	{
		action = act;
		obj = st;
	}

	public enum EnumAction
	{
		CHANGE_PAGE,
		CHANGE_LINE,
		CHANGE_CONVERSATION,
		SENTENCE,
		NPC_TALK,
		CHOICE,
		BUTTON,
		COMMAND,
		WAIT,
		FINISH
	}

	public void execute(final ConversationProgress cp)
	{
		switch (action)
		{
			case BUTTON:
				cp.getCurrentPage().add(new InteractiveText("&0[▼]").clickCommand("/mq conv next")).changeLine();
				return;
			case CHANGE_CONVERSATION:
				// TODO Complicated
				break;
			case CHANGE_LINE:
				cp.getCurrentPage().changeLine();
				break;
			case CHANGE_PAGE:
				cp.newPage();
				break;
			case CHOICE:
				QuestChoice c = QuestConversationManager.getChoiceByName(obj);
				if (c == null)
					return;
				c.apply(cp);
				break;
			case COMMAND:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), obj);
				break;
			case SENTENCE:
				cp.getCurrentPage().add(QuestChatManager.translateColor(obj)).changeLine();
				break;
			case NPC_TALK:
				String[] split = obj.split("@");
				NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(split[1]));
				if (npc != null)
					cp.getCurrentPage().add(QuestChatManager.translateColor(npc.getName() + "&0：「" + split[0] + "」")).changeLine();
				break;
			case WAIT:
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						cp.nextAction();
						return;
					}
				}.runTaskLater(Main.instance, Long.parseLong(obj.toString()));
				return;
			case FINISH:
				cp.finish(Boolean.valueOf(obj));
				return;
			default:
				return;
		}
	}

	public EnumAction getActionType()
	{
		return action;
	}

}