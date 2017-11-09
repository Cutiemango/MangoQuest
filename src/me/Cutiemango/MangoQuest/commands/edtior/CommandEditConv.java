package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.List;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Syntax;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.CitizensAPI;

public class CommandEditConv
{
	// /mq ce edit args[2] args[3]
	public static void execute(QuestConversation conv, Player sender, String[] args)
	{
		if (!ConversationEditorManager.checkEditorMode(sender, true))
			return;
		if (args.length == 4 && args[3].equals("cancel"))
		{
			ConversationEditorManager.editConversation(sender);
			return;
		}
		switch (args[2])
		{
			case "name":
				editName(conv, sender, args);
				break;
			case "npc":
				editNPC(conv, sender, args);
				break;
			case "convtype":
				editConvType(conv, sender, args);
				break;
			case "fconvp":
				editFriendConvPoint(conv, sender, args);
				break;
			case "act":
				editAction(conv, sender, args, "act");
				break;
			case "accpetact":
				editAction(conv, sender, args, "acceptact");
				break;
			case "denyact":
				editAction(conv, sender, args, "denyact");
				break;
			case "acceptmsg":
			case "denymsg":
			case "fullmsg":
				editMessage((StartTriggerConversation) conv, sender, args);
				break;
			case "quest":
				editQuest((StartTriggerConversation) conv, sender, args);
				break;
		}
		return;

	}

	// /mq ce edit convtype [type]
	private static void editConvType(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			ConversationEditorManager.selectConvType(sender);
			return;
		}
		else
			if (args.length == 4)
			{
				switch (args[3])
				{
					case "normal":
						if (conv instanceof FriendConversation)
							ConversationEditorManager.edit(sender, ((FriendConversation)conv).simpleClone());
						else if (conv instanceof StartTriggerConversation)
							ConversationEditorManager.edit(sender, ((StartTriggerConversation)conv).simpleClone());
						else
							ConversationEditorManager.edit(sender, conv.clone());
						break;
					case "friend":
						ConversationEditorManager.edit(sender, new FriendConversation(conv, 0));
						break;
					case "start":
						ConversationEditorManager.edit(sender, new StartTriggerConversation(conv, null));
						break;
				}
				ConversationEditorManager.editConversation(sender);
				return;
			}
	}

	private static void editName(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq ce edit name", null));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				conv.setName(args[3]);
				ConversationEditorManager.editConversation(sender);
				return;
			}
	}

	private static void editNPC(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.NPC_LEFT_CLICK, "mq ce edit npc", Syntax.of("I", "[NPCID]", "")));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.ClickNPC"));
			return;
		}
		else
			if (args.length == 4)
			{
				conv.setNPC(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3])));
				ConversationEditorManager.editConversation(sender);
				return;
			}
	}

	private static void editFriendConvPoint(QuestConversation conv, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq ce edit fconvp", Syntax.of("I", I18n.locMsg("Syntax.Number"), "")));
			QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				if (conv instanceof FriendConversation)
					((FriendConversation) conv).setReqPoint(Integer.parseInt(args[3]));

				ConversationEditorManager.editConversation(sender);
				return;
			}
	}

	// /mq ce edit [acceptmsg/denymsg/fullmsg] [msg]
	private static void editMessage(StartTriggerConversation conv, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			String type = args[2];
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq ce edit " + type, null));
			return;
		}
		else
			if (args.length >= 4)
			{
				String type = args[2];
				String msg = QuestUtil.convertArgsString(args, 4);
				switch (type)
				{
					case "acceptmsg":
						conv.setAcceptMessage(msg);
						break;
					case "denymsg":
						conv.setDenyMessage(msg);
						break;
					case "fullmsg":
						conv.setQuestFullMessage(msg);
						break;
				}
			}
		ConversationEditorManager.editConversation(sender);
	}

	// /mq ce edit quest [questname]
	private static void editQuest(StartTriggerConversation conv, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			QuestEditorManager.selectQuest(sender, "/mq ce edit quest");
			return;
		}
		else
			if (args.length == 4)
			{
				Quest q = QuestUtil.getQuest(args[3]);
				conv.setQuest(q);
				QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered", q.getQuestName()));
			}
	}

	// /mq ce edit act [index] [acttype] [actobj]
	private static void editAction(QuestConversation conv, Player sender, String[] args, String type)
	{
		if (args.length == 4)
		{
			int index = Integer.parseInt(args[3]);
			ConversationEditorManager.selectActionType(sender, type, "edit", index);
			return;
		}
		else
			if (args.length == 5)
			{
				int index = Integer.parseInt(args[3]);
				EnumAction act = EnumAction.valueOf(args[4]);
				if (EnumAction.NO_OBJ_ACTIONS.contains(act))
				{
					switch (type)
					{
						case "act":
							List<QuestBaseAction> list = conv.getActions();
							if (list.size() - 1 >= index)
							{
								list.set(index, new QuestBaseAction(act, null));
								conv.setActions(list);
							}
							break;
						case "acceptact":
							if (conv instanceof StartTriggerConversation)
							{
								list = ((StartTriggerConversation) conv).getAcceptActions();
								if (list.size() - 1 >= index)
								{
									list.set(index, new QuestBaseAction(act, null));
									((StartTriggerConversation) conv).setAcceptActions(list);
								}
							}
							break;
						case "denyact":
							if (conv instanceof StartTriggerConversation)
							{
								list = ((StartTriggerConversation) conv).getDenyActions();
								if (list.size() - 1 >= index)
								{
									list.set(index, new QuestBaseAction(act, null));
									((StartTriggerConversation) conv).setDenyActions(list);
								}
							}
							break;
					}
					ConversationEditorManager.editConversation(sender);
					return;
				}
				if (act == EnumAction.NPC_TALK)
				{
					EditorListenerHandler.register(sender,
							new EditorListenerObject(ListeningType.STRING, "/mq ce edit " + type + " " + index + " " + act.toString(), Syntax.of("S@I", I18n.locMsg("Syntax.NPCTalk"), "@")));
					QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.NPCTalk"));
					return;
				}
				EditorListenerHandler.register(sender,
						new EditorListenerObject(ListeningType.STRING, "/mq ce edit " + type + " " + index + " " + act.toString(), null));
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			}
			else
				if (args.length >= 6)
				{
					int index = Integer.parseInt(args[3]);
					EnumAction act = EnumAction.valueOf(args[4]);
					String s = QuestUtil.convertArgsString(args, 5);
					if (s.equalsIgnoreCase("cancel"))
					{
						ConversationEditorManager.editConversation(sender);
						return;
					}
					switch (type)
					{
						case "act":
							List<QuestBaseAction> list = conv.getActions();
							if (list.size() - 1 >= index)
							{
								list.set(index, new QuestBaseAction(act, s));
								conv.setActions(list);
							}
							break;
						case "acceptact":
							if (conv instanceof StartTriggerConversation)
							{
								list = ((StartTriggerConversation) conv).getAcceptActions();
								if (list.size() - 1 >= index)
								{
									list.set(index, new QuestBaseAction(act, s));
									((StartTriggerConversation) conv).setAcceptActions(list);
								}
							}
							break;
						case "denyact":
							if (conv instanceof StartTriggerConversation)
							{
								list = ((StartTriggerConversation) conv).getDenyActions();
								if (list.size() - 1 >= index)
								{
									list.set(index, new QuestBaseAction(act, s));
									((StartTriggerConversation) conv).setDenyActions(list);
								}
							}
							break;
					}
					ConversationEditorManager.editConversation(sender);
					return;
				}
	}
}
