package me.Cutiemango.MangoQuest.editor;

import java.util.HashMap;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.book.FlexiableBook;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class ConversationEditorManager
{
	public static HashMap<String, QuestConversation> isEditing = new HashMap<>();
	
	public static QuestConversation getEditingConversation(Player p)
	{
		return isEditing.get(p.getName());
	}
	
	public static void edit(Player p, QuestConversation conv)
	{
		isEditing.put(p.getName(), conv);
	}

	public static void exit(Player p)
	{
		isEditing.remove(p.getName());
		QuestChatManager.info(p, I18n.locMsg("EditorMessage.Exited"));
	}
	
	public static boolean checkEditorMode(Player p, boolean msg)
	{
		if (!isEditing.containsKey(p.getName()) && msg)
			QuestChatManager.error(p, I18n.locMsg("EditorMessage.NotInEditor"));
		return isEditing.containsKey(p.getName());
	}
	
	public static void mainGUI(Player p)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.Title")).changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("ConversationEditor.NewConversation")).clickCommand("/mq ce newconv")
				.showText(I18n.locMsg("ConversationEditor.NewConversation.ShowText"))).changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("ConversationEditor.EditConversation")).clickCommand("/mq ce edit")
				.showText(I18n.locMsg("ConversationEditor.EditConversation.ShowText"))).changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("ConversationEditor.RemoveConversation")).clickCommand("/mq ce remove")
				.showText(I18n.locMsg("ConversationEditor.RemoveConversation.ShowText"))).changeLine();
		p1.changeLine();
		
		if (checkEditorMode(p, false) && ConversationEditorManager.getEditingConversation(p).getInternalID() != null)
		{
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.ReturnToEditor")).clickCommand("/mq ce gui")
					.showText(I18n.locMsg("QuestEditor.ReturnToEditor.ShowText"))).changeLine();
			p1.changeLine();
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.ExitEditor")).clickCommand("/mq ce exit")
					.showText(I18n.locMsg("QuestEditor.ExitEditor.ShowText"))).changeLine();
		}
		QuestBookGUIManager.openBook(p, p1);
	}
	
	public static void editGUI(Player p)
	{
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		page.add(I18n.locMsg("ConversationEditor.Title")).changeLine();
		page.add(I18n.locMsg("ConversationEditor.ChooseEditConv")).changeLine();
		for (QuestConversation conv : QuestStorage.Conversations.values())
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add(new InteractiveText("&0- &l" + conv.getName() + "&0(" + conv.getInternalID() + ")")
					.clickCommand("/mq ce select " + conv.getInternalID()));
			page.changeLine();
		}
		QuestUtil.checkOutOfBounds(page, book);
		page = book.getLastEditingPage();
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq ce"));
		page.endNormally();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}
	
	public static void removeGUI(Player p)
	{
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		page.add(I18n.locMsg("ConversationEditor.Title")).changeLine();
		page.add(I18n.locMsg("ConversationEditor.ChooseRemoveConv")).changeLine();
		for (QuestConversation conv : QuestStorage.Conversations.values())
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add(new InteractiveText("&0- &l" + conv.getName() + "&0(" + conv.getInternalID() + ")")
					.clickCommand("/mq ce remove confirm " + conv.getInternalID()));
			page.changeLine();
		}
		QuestUtil.checkOutOfBounds(page, book);
		page = book.getLastEditingPage();
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq ce"));
		page.endNormally();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}
	
	public static void editConversation(Player p)
	{
		if (!checkEditorMode(p, true))
			return;
		QuestConversation conv = getEditingConversation(p);
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		// Basic Info
		page.add(I18n.locMsg("QuestEditor.BasicInfo")).changeLine();
		page.add(new InteractiveText(I18n.locMsg("ConversationEditor.ConvInternalID", conv.getInternalID()))
				.showText(I18n.locMsg("ConversationEditor.ConvInternalID.ShowText"))).changeLine();
		page.add(new InteractiveText(I18n.locMsg("ConversationEditor.ConvName", conv.getName()))
				.showText(I18n.locMsg("ConversationEditor.ConvName.ShowText")).clickCommand("/mq ce edit name")).changeLine();
		page.add(new InteractiveText(I18n.locMsg("ConversationEditor.ConvNPC"))
				.showText(I18n.locMsg("ConversationEditor.ConvNPC.ShowText")).clickCommand("/mq ce edit npc")).endNormally();
		
		if (conv.getNPC() != null)
			page.add(new InteractiveText("").showNPCInfo(conv.getNPC())).changeLine();
		else
			page.add(I18n.locMsg("QuestEditor.NotSet")).changeLine();
		
		page.changeLine();
		page.changeLine();
		
		page.add(new InteractiveText(I18n.locMsg("ConversationEditor.StartSimulation")).clickCommand("/mq ce modconv")).changeLine();
		
		page.changeLine();
		page.changeLine();
		page.changeLine();
		page.changeLine();
		
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq ce edit")).changeLine();

		// Speical conv
		book.newPage();
		page = book.getLastEditingPage();
		page.add(new InteractiveText(I18n.locMsg("ConversationEditor.ConvType"))
				.showText(I18n.locMsg("ConversationEditor.ConvType.ShowText")).clickCommand("/mq ce edit convtype")).endNormally();
		
		int counter = 0;
		if (conv instanceof FriendConversation)
		{
			page.add(I18n.locMsg("ConversationEditor.FriendConvText")).changeLine();
			
			page.add(new InteractiveText(I18n.locMsg("ConversationEditor.FriendConvReqPoint"))
				.showText(I18n.locMsg("ConversationEditor.FriendConvReqPoint.ShowText")).clickCommand("/mq ce edit fconvp")).endNormally();
			page.add(Integer.toString(((FriendConversation)conv).getReqPoint())).changeLine();
		}
		else if (conv instanceof StartTriggerConversation)
		{
			StartTriggerConversation sconv = (StartTriggerConversation)conv;
			page.add(I18n.locMsg("ConversationEditor.StartTriggerConvText")).changeLine();
			page.add(new InteractiveText(I18n.locMsg("ConversationEditor.QuestTrigger")).clickCommand("/mq ce edit quest")).endNormally();
			page.add(new InteractiveText("").showQuest(sconv.getQuest())).changeLine();
			
			page.add(new InteractiveText(I18n.locMsg("ConversationEditor.QuestFullMessage")).clickCommand("/mq ce edit fullmsg").showText(I18n.locMsg("ConversationEditor.QuestFullMessage.ShowText"))).changeLine();
			page.add(sconv.getQuestFullMessage()).changeLine();
			
			page.add(new InteractiveText(I18n.locMsg("ConversationEditor.QuestAcceptMessage")).clickCommand("/mq ce edit acceptmsg").showText(I18n.locMsg("ConversationEditor.QuestAcceptMessage.ShowText"))).endNormally();
			page.add(sconv.getAcceptMessage()).changeLine();
			
			page.add(new InteractiveText(I18n.locMsg("ConversationEditor.QuestDenyMessage")).clickCommand("/mq ce edit denymsg").showText(I18n.locMsg("ConversationEditor.QuestDenyMessage.ShowText"))).endNormally();
			page.add(sconv.getDenyMessage()).changeLine();
			
			book.newPage();
			page = book.getLastEditingPage();
			
			page.add(I18n.locMsg("ConversationEditor.QuestAcceptActions")).changeLine();
			
			for (QuestBaseAction act : ((StartTriggerConversation) conv).getAcceptActions())
			{
				page = book.getLastEditingPage();
				QuestUtil.checkOutOfBounds(page, book);
				page.add("- " + counter + ".");
				if (EnumAction.NO_OBJ_ACTIONS.contains(act.getActionType()))
					page.add(new InteractiveText(act.getActionType().toCustomString())
						.showText(I18n.locMsg("ConversationEditor.ActionObject") + I18n.locMsg("ConversationEditor.NoObjectAction"))
							.clickCommand("/mq ce edit acceptact" + counter)).endNormally();
				else
					if (act.getActionType().equals(EnumAction.NPC_TALK))
					{
						String[] split = act.getObject().split("@");
						NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(split[1]));
						page.add(new InteractiveText(act.getActionType().toCustomString())
								.showText(I18n.locMsg("ConversationEditor.ActionObject") + QuestChatManager.toNormalDisplay(I18n.locMsg("QuestJourney.NPCFriendMessage", npc.getName(), split[0])))).endNormally();
					}
					else
						page.add(new InteractiveText(act.getActionType().toCustomString()).showText(I18n.locMsg("ConversationEditor.ActionObject") + act.getObject())).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq ce new acceptact " + (counter + 1))).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq ce edit acceptact " + counter)).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq ce remove acceptact " + counter)).endNormally();
				page.changeLine();
				counter++;
			}
			if (((StartTriggerConversation) conv).getAcceptActions().size() == 0)
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq ce new acceptact " + 0)).changeLine();

			page.changeLine();
			
			book.newPage();
			page = book.getLastEditingPage();
			
			page.add(I18n.locMsg("ConversationEditor.QuestDenyActions")).changeLine();
			
			counter = 0;
			for (QuestBaseAction act : ((StartTriggerConversation) conv).getDenyActions())
			{
				page = book.getLastEditingPage();
				QuestUtil.checkOutOfBounds(page, book);
				page.add("- " + counter + ".");
				if (EnumAction.NO_OBJ_ACTIONS.contains(act.getActionType()))
					page.add(new InteractiveText(act.getActionType().toCustomString())
						.showText(I18n.locMsg("ConversationEditor.ActionObject") + I18n.locMsg("ConversationEditor.NoObjectAction"))).endNormally();
				else
					if (act.getActionType().equals(EnumAction.NPC_TALK))
					{
						String[] split = act.getObject().split("@");
						NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(split[1]));
						page.add(new InteractiveText(act.getActionType().toCustomString()).showText(I18n.locMsg("ConversationEditor.ActionObject") + QuestChatManager.toNormalDisplay(I18n.locMsg("QuestJourney.NPCFriendMessage", npc.getName(), split[0])))).endNormally();
					}
					else
						page.add(new InteractiveText(act.getActionType().toCustomString()).showText(I18n.locMsg("ConversationEditor.ActionObject") + act.getObject())).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq ce new denyact " + (counter + 1))).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq ce edit denyact " + counter)).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq ce remove denyact " + counter)).endNormally();
				page.changeLine();
				counter++;
			}
			
			if (((StartTriggerConversation) conv).getDenyActions().size() == 0)
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq ce new denyact " + counter)).endNormally();

			page.changeLine();
		}
		else
		{
			page.add(I18n.locMsg("ConversationEditor.QuestConvText")).changeLine();
			page.changeLine();
			page.add(I18n.locMsg("ConversationEditor.Empty")).changeLine();
			page.changeLine();
		}
		
		book.newPage();
		page = book.getLastEditingPage();
		
		page.add(I18n.locMsg("ConversationEditor.ConvActionInfo")).changeLine();
		counter = 0;
		for (QuestBaseAction act : conv.getActions())
		{
			page = book.getLastEditingPage();
			QuestUtil.checkOutOfBounds(page, book);
			page.add("- " + counter + ".");
			if (EnumAction.NO_OBJ_ACTIONS.contains(act.getActionType()))
				page.add(new InteractiveText(act.getActionType().toCustomString()).showText(I18n.locMsg("ConversationEditor.ActionObject") + I18n.locMsg("ConversationEditor.NoObjectAction"))).endNormally();
			else
				if (act.getActionType().equals(EnumAction.NPC_TALK))
				{
					String[] split = act.getObject().split("@");
					NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(split[1]));
					page.add(new InteractiveText(act.getActionType().toCustomString()).showText(I18n.locMsg("ConversationEditor.ActionObject") + QuestChatManager.toNormalDisplay(I18n.locMsg("QuestJourney.NPCFriendMessage", npc.getName(), split[0])))).endNormally();
				}
				else
					page.add(new InteractiveText(act.getActionType().toCustomString()).showText(I18n.locMsg("ConversationEditor.ActionObject") + act.getObject())).endNormally();
			page.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq ce new act " + (counter + 1))).endNormally();
			page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq ce edit act " + counter)).endNormally();
			page.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq ce remove act " + counter)).endNormally();
			page.changeLine();
			counter++;
		}
		
		if (conv.getActions().size() == 0)
			page.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq ce new act " + 0)).changeLine();
		
		page = book.getLastEditingPage();
		QuestUtil.checkOutOfBounds(page, book);
		page.changeLine();
		
		// Saving Page
		book.newPage();
		page = book.getLastEditingPage();
		page.add(I18n.locMsg("QuestEditor.SaveAndExit")).changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.SyncSevAndLoc")).clickCommand("/mq ce sa").showText(I18n.locMsg("QuestEditor.WarnSave"))).changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.SyncSev")).clickCommand("/mq ce sl").showText(I18n.locMsg("QuestEditor.WarnSave"))).changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.SyncLoc")).clickCommand("/mq ce sc").showText(I18n.locMsg("QuestEditor.WarnSave"))).changeLine();
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.QuitEditor")).clickCommand("/mq ce exit").showText(I18n.locMsg("QuestEditor.ExitEditor.ShowText"))).changeLine();
		
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}
	
	public static void createConversation(Player p)
	{
		QuestBookPage page = new QuestBookPage();
		page.add(I18n.locMsg("ConversationEditor.CreateConv")).changeLine();
		page.add(I18n.locMsg("ConversationEditor.CreateConvDesc")).changeLine();
		QuestConversation qc = ConversationEditorManager.getEditingConversation(p);
		page.add(new InteractiveText(I18n.locMsg("ConversationEditor.CreateConvButton")).clickCommand("/mq ce newconv create")).changeLine();
		
		page.changeLine();
		
		String id = (qc.getInternalID() != null) ? qc.getInternalID() : I18n.locMsg("QuestEditor.NotSet");
		page.add(I18n.locMsg("ConversationEditor.NewConvID")).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq ce newconv id")).changeLine();
		page.add(id).changeLine();
		
		String name = (qc.getName() != null) ? qc.getName() : I18n.locMsg("QuestEditor.NotSet");
		page.add(I18n.locMsg("ConversationEditor.NewConvName")).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq ce newconv name")).changeLine();
		page.add(name).changeLine();
		
		InteractiveText npc = (qc.getNPC() != null) ? new InteractiveText("").showNPCInfo(qc.getNPC()) : new InteractiveText(I18n.locMsg("QuestEditor.NotSet"));
		page.add(I18n.locMsg("ConversationEditor.NewConvNPC")).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq ce newconv npc")).changeLine();
		page.add(npc).changeLine();
		
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq ce")).changeLine();
		QuestBookGUIManager.openBook(p, page);
	}
	
	
	public static void removeConfirmGUI(Player p, QuestConversation conv)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.WarnRemoveConv")).changeLine();
		p1.add(I18n.locMsg("QuestEditor.WarnRemoveQuest2", conv.getName())).changeLine();
		p1.add(I18n.locMsg("QuestEditor.WarnRemoveQuest3")).changeLine();
		p1.changeLine();
		p1.changeLine();
		p1.add("  ");
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.WarnAccept")).clickCommand("/mq ce remove conv " + conv.getInternalID()));
		p1.endNormally();
		p1.add(" &8&l/ ");
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.WarnDeny")).clickCommand("/mq ce remove"));
		p1.endNormally();
		QuestBookGUIManager.openBook(p, p1);
	}
	
	
	public static void selectActionType(Player p, String type, String mode, int index)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("ConversationEditor.SelectActionType")).changeLine();
		for (EnumAction act : EnumAction.values())
		{
			p1.add(new InteractiveText("- [" + act.toCustomString() + "]").clickCommand("/mq ce " + mode + " " + type + " " + index + " " + act.toString()));
			p1.changeLine();
		}
		QuestBookGUIManager.openBook(p, p1);
	}
	
	public static void selectConvType(Player p)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("ConversationEditor.SelectConvType")).changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("ConversationEditor.QuestConv")).clickCommand("/mq ce edit convtype normal"));
		p1.changeLine();
		p1.changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("ConversationEditor.FriendConv")).clickCommand("/mq ce edit convtype friend"));
		p1.changeLine();
		p1.changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("ConversationEditor.StartTriggerConv")).clickCommand("/mq ce edit convtype start"));
		p1.changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}
	
	public static void selectConversation(Player p, String cmd)
	{
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		page.add(I18n.locMsg("ConversationEditor.ChooseTargetConv")).changeLine();
		for (QuestConversation conv : QuestStorage.Conversations.values())
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add(new InteractiveText("&0- &l" + conv.getName() + "&0(" + conv.getInternalID() + ")")
					.clickCommand("/" + cmd + " " + conv.getInternalID())).endNormally();
			page.changeLine();
		}
		QuestUtil.checkOutOfBounds(page, book);
		page = book.getLastEditingPage();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

}
