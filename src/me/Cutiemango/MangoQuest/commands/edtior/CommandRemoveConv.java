package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.List;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;

public class CommandRemoveConv
{
	// /mq ce remove args[2] args[3]
	public static void execute(QuestConversation conv, Player sender, String[] args)
	{
		if (!QuestEditorManager.checkEditorMode(sender, true))
			return;
		switch (args[2])
		{
			case "act":
			case "acceptact":
			case "denyact":
				removeAction(conv, sender, args);
				break;
		}
		return;
	}
	
	
	// /mq ce remove [acceptact/denyact/act] [index]
	private static void removeAction(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length < 4)
			return;
		int index = Integer.parseInt(args[3]);
		switch (args[2])
		{
			case "act":
				List<QuestBaseAction> list = conv.getActions();
				list.remove(index);
				conv.setActions(list);
				break;
			case "acceptact":
				list = ((StartTriggerConversation)conv).getAcceptActions();
				list.remove(index);
				((StartTriggerConversation)conv).setAcceptActions(list);
				break;
			case "denyact":
				list = ((StartTriggerConversation)conv).getDenyActions();
				list.remove(index);
				((StartTriggerConversation)conv).setDenyActions(list);
				break;
		}
		ConversationEditorManager.editConversation(sender);
		return;
	}
}
