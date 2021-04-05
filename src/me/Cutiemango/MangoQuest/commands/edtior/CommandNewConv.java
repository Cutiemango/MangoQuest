package me.Cutiemango.MangoQuest.commands.edtior;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Syntax;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.editor.ConversationEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

public class CommandNewConv
{
	// /mq ce newconv args[2] args[3]
	public static void execute(Player sender, String[] args) {
		if (args.length == 2) {
			ConversationEditorManager.edit(sender, new QuestConversation());
			ConversationEditorManager.createConversation(sender);
		} else if (args.length > 2) {
			switch (args[2]) {
				case "id":
					setInternalID(sender, args);
					break;
				case "name":
					setConvName(sender, args);
					break;
				case "npc":
					setConvNPC(sender, args);
					break;
				case "create":
					create(sender);
					break;
			}
		}
	}

	private static void setInternalID(Player p, String[] args) {
		if (args.length == 3) {
			QuestBookGUIManager.openInfo(p, I18n.locMsg("EditorMessage.NewConv.EnterID"));
			EditorListenerHandler.register(p, new EditorListenerObject(ListeningType.STRING, "mq ce newconv id", null));
			return;
		}
		if (args[3].equalsIgnoreCase("cancel")) {
			ConversationEditorManager.createConversation(p);
			return;
		}
		ConversationEditorManager.getEditingConversation(p).setInternalID(args[3]);
		QuestChatManager.info(p, I18n.locMsg("EditorMessage.NameRegistered", args[3]));
		ConversationEditorManager.createConversation(p);
	}

	private static void setConvName(Player p, String[] args) {
		if (args.length == 3) {
			QuestBookGUIManager.openInfo(p, I18n.locMsg("EditorMessage.NewConv.EnterName"));
			EditorListenerHandler.register(p, new EditorListenerObject(ListeningType.STRING, "mq ce newconv name", null));
			return;
		}
		if (args[3].equalsIgnoreCase("cancel")) {
			ConversationEditorManager.createConversation(p);
			return;
		}
		ConversationEditorManager.getEditingConversation(p).setName(args[3]);
		QuestChatManager.info(p, I18n.locMsg("EditorMessage.NameRegistered", args[3]));
		ConversationEditorManager.createConversation(p);
	}

	private static void setConvNPC(Player p, String[] args) {
		if (args.length == 3) {
			QuestBookGUIManager.openInfo(p, I18n.locMsg("EditorMessage.NewConv.EnterNPC"));
			EditorListenerHandler.register(p,
					new EditorListenerObject(ListeningType.NPC_LEFT_CLICK, "mq ce newconv npc", Syntax.of("N", I18n.locMsg("Syntax.NPCID"), "")));
			return;
		}
		if (args[3].equalsIgnoreCase("cancel")) {
			ConversationEditorManager.createConversation(p);
			return;
		}
		NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3]));
		if (npc == null)
			return;
		ConversationEditorManager.getEditingConversation(p).setNPC(npc);
		QuestChatManager.info(p, I18n.locMsg("EditorMessage.NPCRegistered", npc.getName()));
		ConversationEditorManager.createConversation(p);
	}

	private static void create(Player p) {
		QuestConversation conv = ConversationEditorManager.getEditingConversation(p);
		if (conv.getInternalID() != null && conv.getName() != null && conv.getNPC() != null) {
			QuestChatManager.info(p, I18n.locMsg("EditorMessage.NewConv.Successful", conv.getName()));
			ConversationEditorManager.editConversation(p);
		} else {
			QuestChatManager.info(p, I18n.locMsg("EditorMessage.NewConv.Failed"));
			ConversationEditorManager.createConversation(p);
		}
	}
}
