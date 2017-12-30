package me.Cutiemango.MangoQuest.conversation;

import java.util.Arrays;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.advancements.QuestAdvancementManager;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
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
		CHANGE_PAGE(I18n.locMsg("EnumAction.ChangePage")),
		CHANGE_LINE(I18n.locMsg("EnumAction.ChangeLine")),
		SENTENCE(I18n.locMsg("EnumAction.Sentence")),
		NPC_TALK(I18n.locMsg("EnumAction.NPCTalk")),
		CHOICE(I18n.locMsg("EnumAction.Choice")),
		BUTTON(I18n.locMsg("EnumAction.Button")),
		COMMAND(I18n.locMsg("EnumAction.Command")),
		WAIT(I18n.locMsg("EnumAction.Wait")),
		FINISH(I18n.locMsg("EnumAction.Finish")),
		TAKE_QUEST(I18n.locMsg("EnumAction.TakeQuest")),
		GIVE_ADVANCEMENT(I18n.locMsg("EnumAction.GiveAdvancement"));
		
		EnumAction(String s)
		{
			name = s;
		}

		public static final List<EnumAction> NO_OBJ_ACTIONS = Arrays.asList(EnumAction.CHANGE_LINE, EnumAction.CHANGE_PAGE, EnumAction.BUTTON, EnumAction.TAKE_QUEST);
		private String name;
		
		public String toCustomString()
		{
			return name;
		}
	}

	public void execute(final ConversationProgress cp)
	{
		if (obj != null && obj instanceof String)
			obj = obj.replace("<player>", cp.getOwner().getName());
		switch (action)
		{
			case BUTTON:
				cp.getCurrentPage().add(new InteractiveText(I18n.locMsg("Conversation.Button")).clickCommand("/mq conv next")).changeLine();
				break;
			case CHANGE_LINE:
				cp.getCurrentPage().changeLine();
				break;
			case CHANGE_PAGE:
				cp.newPage();
				break;
			case CHOICE:
				QuestChoice c = ConversationManager.getChoiceByName(obj);
				if (c == null)
					break;
				c.apply(cp);
				break;
			case COMMAND:
				QuestUtil.executeConsoleAsync(obj);
				break;
			case SENTENCE:
				cp.getCurrentPage().add(QuestChatManager.translateColor(obj)).changeLine();
				break;
			case NPC_TALK:
				String[] split = obj.split("@");
				NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(split[1]));
				if (npc != null)
					cp.getCurrentPage().add(I18n.locMsg("QuestJourney.NPCFriendMessage", npc.getName(), split[0])).changeLine();
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
				break;
			case FINISH:
				cp.finish(Boolean.valueOf(obj));
				break;
			case GIVE_ADVANCEMENT:
				if (Main.isUsingUpdatedVersion())
				{
					QuestAdvancementManager.getAdvancement(obj).grant(cp.getOwner());
					break;
				}
			case TAKE_QUEST:
				if (!(cp.getConversation() instanceof StartTriggerConversation))
					break;
				StartTriggerConversation conv = (StartTriggerConversation)cp.getConversation();
				QuestPlayerData data = QuestUtil.getData(cp.getOwner());
				if (!data.checkQuestSize(false))
				{
					cp.getCurrentPage().add(conv.getQuestFullMessage()).changeLine();
					cp.getActionQueue().add(new QuestBaseAction(EnumAction.FINISH, "false"));
					break;
				}
				cp.newPage();
				cp.getCurrentPage().add(I18n.locMsg("Conversation.ChooseAnOption")).changeLine();
				
				cp.getCurrentPage().add(new InteractiveText(I18n.locMsg("Conversation.DefaultQuestAcceptMessage") + conv.getAcceptMessage()).clickCommand("/mq conv takequest")).changeLine();
				cp.getCurrentPage().add(new InteractiveText(I18n.locMsg("Conversation.DefaultQuestDenyMessage") + conv.getDenyMessage()).clickCommand("/mq conv denyquest")).changeLine();
				break;
			default:
				break;
		}
		
	}

	public EnumAction getActionType()
	{
		return action;
	}
	
	public String getObject()
	{
		return obj;
	}
	
	public String toConfigFormat()
	{
		return action.toString() + "#" + obj;
	}

}