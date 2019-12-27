package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.config.QuestConfigManager;

public class CommandRemoveConv
{
	// /mq ce remove args[2] args[3]
	public static void execute(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length >= 3)
		{
			switch (args[2])
			{
				case "conv":
					removeConv(sender, args);
					return;
				case "confirm":
					removeConfirm(sender, args);
					return;
			}
		}
		if (!ConversationEditorManager.checkEditorMode(sender, true))
			return;
		switch (args[2])
		{
			case "act":
			case "acceptact":
			case "denyact":
				removeAction(conv, sender, args);
				break;
		}
	}

	private static void removeConfirm(Player sender, String[] args)
	{
		if (args.length == 4)
		{
			if (ConversationManager.getConversation(args[3]) != null)
			{
				QuestConversation target = ConversationManager.getConversation(args[3]);
				ConversationEditorManager.removeConfirmGUI(sender, target);
			}
		}
	}

	private static void removeConv(Player sender, String[] args)
	{
		if (args.length == 4)
		{
			if (ConversationManager.getConversation(args[3]) != null)
			{
				QuestConversation target = ConversationManager.getConversation(args[3]);
				for (Player pl : Bukkit.getOnlinePlayers())
				{
					if (ConversationManager.isInConvProgress(pl, target))
						ConversationManager.forceQuit(pl, target);
				}
				QuestConfigManager.getSaver().removeConversation(target);
				QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ConversationRemoved", target.getName()));
				QuestStorage.Conversations.remove(args[3]);
				ConversationEditorManager.removeGUI(sender);
			}
		}
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
				list = ((StartTriggerConversation) conv).getAcceptActions();
				list.remove(index);
				((StartTriggerConversation) conv).setAcceptActions(list);
				break;
			case "denyact":
				list = ((StartTriggerConversation) conv).getDenyActions();
				list.remove(index);
				((StartTriggerConversation) conv).setDenyActions(list);
				break;
		}
		ConversationEditorManager.editConversation(sender);
	}
}
