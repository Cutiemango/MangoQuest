package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.List;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;

public class CommandNewAction
{
	// /mq ce new [acceptact/denyact/act] [type] [obj]
	public static void execute(QuestConversation conv, Player sender, String[] args)
	{
		if (!ConversationEditorManager.checkEditorMode(sender, true))
			return;
		if (args.length == 3)
		{
			ConversationEditorManager.selectActionType(sender, args[2], -1);
			return;
		}
		else
			if (args.length == 4)
			{
				EnumAction act = EnumAction.valueOf(args[3]);
				if (EnumAction.NO_OBJ_ACTIONS.contains(act))
				{
					switch (args[2])
					{
						case "act":
							List<QuestBaseAction> list = conv.getActions();
							list.add(new QuestBaseAction(act, null));
							conv.setActions(list);
							break;
						case "acceptact":
							list = ((StartTriggerConversation)conv).getAcceptActions();
							list.add(new QuestBaseAction(act, null));
							((StartTriggerConversation)conv).setAcceptActions(list);
							break;
						case "denyact":
							list = ((StartTriggerConversation)conv).getDenyActions();
							list.add(new QuestBaseAction(act, null));
							((StartTriggerConversation)conv).setDenyActions(list);
							break;
					}
					ConversationEditorManager.editConversation(sender);
					return;
				}
				EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq ce new " + args[2] + " " + act.toString()));
				QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
				return;
			}
			else if (args.length >= 5)
			{
				String s = "";
				for (int j = 4; j < args.length; j++)
				{
					s = s + args[j];
					if (j + 1 == args.length)
						break;
					else
						s += " ";
				}
				EnumAction act = EnumAction.valueOf(args[3]);
				switch (args[2])
				{
					case "act":
						List<QuestBaseAction> list = conv.getActions();
						list.add(new QuestBaseAction(act, s));
						conv.setActions(list);
						break;
					case "acceptact":
						list = ((StartTriggerConversation)conv).getAcceptActions();
						list.add(new QuestBaseAction(act, s));
						((StartTriggerConversation)conv).setAcceptActions(list);
						break;
					case "denyact":
						list = ((StartTriggerConversation)conv).getDenyActions();
						list.add(new QuestBaseAction(act, s));
						((StartTriggerConversation)conv).setDenyActions(list);
						break;
				}
				ConversationEditorManager.editConversation(sender);
				return;
			}
	}
}
