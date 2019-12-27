package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.List;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Syntax;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;

public class CommandNewAction
{
	// /mq ce new [acceptact/denyact/act] [index] [type] [obj]
	public static void execute(QuestConversation conv, Player sender, String[] args)
	{
		if (!ConversationEditorManager.checkEditorMode(sender, true))
			return;
		if (args.length < 4)
			return;
		String acttype = args[2];
		int index = Integer.parseInt(args[3]);
		if (args.length == 4)
			ConversationEditorManager.selectActionType(sender, acttype, "new", index);
		else
			if (args.length == 5)
			{
				EnumAction act = EnumAction.valueOf(args[4]);
				if (EnumAction.NO_OBJ_ACTIONS.contains(act))
				{
					switch (acttype)
					{
						case "act":
							List<QuestBaseAction> list = conv.getActions();
							list.add(index, new QuestBaseAction(act, null));
							conv.setActions(list);
							break;
						case "acceptact":
							list = ((StartTriggerConversation)conv).getAcceptActions();
							list.add(index, new QuestBaseAction(act, null));
							((StartTriggerConversation)conv).setAcceptActions(list);
							break;
						case "denyact":
							list = ((StartTriggerConversation)conv).getDenyActions();
							list.add(index, new QuestBaseAction(act, null));
							((StartTriggerConversation)conv).setDenyActions(list);
							break;
					}
					ConversationEditorManager.editConversation(sender);
					return;
				}
				if (act == EnumAction.NPC_TALK)
				{
					EditorListenerHandler.register(sender,
							new EditorListenerObject(ListeningType.STRING, "mq ce new " + acttype + " " + index + " " + act.toString(), Syntax.of("S@I", I18n.locMsg("Syntax.NPCTalk"), "@")));
					QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.NPCTalk"));
					return;
				}
				EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq ce new " + acttype + " " + index + " " + act.toString(), null));
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			}
			else
			{
				String s = "";
				for (int j = 5; j < args.length; j++)
				{
					s = s + args[j];
					if (j + 1 == args.length)
						break;
					else
						s += " ";
				}
				if (s.equalsIgnoreCase("cancel"))
				{
					ConversationEditorManager.editConversation(sender);
					return;
				}
				EnumAction act = EnumAction.valueOf(args[4]);
				switch (acttype)
				{
					case "act":
						List<QuestBaseAction> list = conv.getActions();
						list.add(index, new QuestBaseAction(act, s));
						conv.setActions(list);
						break;
					case "acceptact":
						list = ((StartTriggerConversation)conv).getAcceptActions();
						list.add(index, new QuestBaseAction(act, s));
						((StartTriggerConversation)conv).setAcceptActions(list);
						break;
					case "denyact":
						list = ((StartTriggerConversation)conv).getDenyActions();
						list.add(index, new QuestBaseAction(act, s));
						((StartTriggerConversation)conv).setDenyActions(list);
						break;
				}
				ConversationEditorManager.editConversation(sender);
			}
	}
}
