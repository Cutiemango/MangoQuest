package me.Cutiemango.MangoQuest.editor;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.book.FlexiableBook;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestSetting;
import me.Cutiemango.MangoQuest.model.RequirementType;
import me.Cutiemango.MangoQuest.model.TriggerType;
import me.Cutiemango.MangoQuest.objects.TriggerObject;
import me.Cutiemango.MangoQuest.objects.TriggerObject.TriggerObjectType;
import me.Cutiemango.MangoQuest.questobjects.CustomQuestObject;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class QuestEditorManager
{

	public static HashMap<String, Quest> isEditing = new HashMap<>();

	public static Quest getCurrentEditingQuest(Player p)
	{
		return isEditing.get(p.getName());
	}

	public static void edit(Player p, Quest q)
	{
		isEditing.put(p.getName(), q);
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
		EditorListenerHandler.unreigster(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.Title")).changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.NewQuest")).clickCommand("/mq e newquest")
				.showText(I18n.locMsg("QuestEditor.NewQuest.ShowText"))).changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.EditQuest")).clickCommand("/mq e edit")
				.showText(I18n.locMsg("QuestEditor.EditQuest.ShowText"))).changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.RemoveQuest")).clickCommand("/mq e remove")
				.showText(I18n.locMsg("QuestEditor.RemoveQuest.ShowText"))).changeLine();
		p1.changeLine();

		if (checkEditorMode(p, false) && QuestEditorManager.getCurrentEditingQuest(p).getInternalID() != null)
		{
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.ReturnToEditor")).clickCommand("/mq e gui")
					.showText(I18n.locMsg("QuestEditor.ReturnToEditor.ShowText"))).changeLine();
			p1.changeLine();
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.ExitEditor")).clickCommand("/mq e exit")
					.showText(I18n.locMsg("QuestEditor.ExitEditor.ShowText"))).changeLine();
		}
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void editGUI(Player p)
	{
		EditorListenerHandler.unreigster(p);
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		page.add(I18n.locMsg("QuestEditor.Title")).changeLine();
		page.add(I18n.locMsg("QuestEditor.ChooseEditQuest")).changeLine();
		for (Quest q : QuestStorage.Quests.values())
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add(new InteractiveText("&0- &l" + q.getQuestName() + "&0(" + q.getInternalID() + ")")
					.clickCommand("/mq e select " + q.getInternalID()));
			page.changeLine();
		}
		QuestUtil.checkOutOfBounds(page, book);
		page = book.getLastEditingPage();
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e"));
		page.endNormally();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void removeGUI(Player p)
	{
		EditorListenerHandler.unreigster(p);
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		page.add(I18n.locMsg("QuestEditor.Title")).changeLine();
		page.add(I18n.locMsg("QuestEditor.ChooseRemoveQuest")).changeLine();
		for (Quest q : QuestStorage.Quests.values())
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add(new InteractiveText("&0- &l" + q.getQuestName() + "&0(" + q.getInternalID() + ")")
					.clickCommand("/mq e remove confirm " + q.getInternalID()));
			page.changeLine();
		}
		QuestUtil.checkOutOfBounds(page, book);
		page = book.getLastEditingPage();
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e"));
		page.endNormally();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void removeConfirmGUI(Player p, Quest q)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.WarnRemoveQuest1")).changeLine();
		p1.add(I18n.locMsg("QuestEditor.WarnRemoveQuest2", q.getQuestName())).changeLine();
		p1.add(I18n.locMsg("QuestEditor.WarnRemoveQuest3")).changeLine();
		p1.changeLine();
		p1.changeLine();
		p1.add("  ");
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.WarnAccept")).clickCommand("/mq e remove quest " + q.getInternalID()));
		p1.endNormally();
		p1.add(" &8&l/ ");
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.WarnDeny")).clickCommand("/mq e remove"));
		p1.endNormally();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void editQuest(Player p)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		EditorListenerHandler.unreigster(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.BasicInfo")).changeLine();
		p1.add(I18n.locMsg("QuestEditor.QuestInternalID", q.getInternalID())).changeLine();

		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.QuestName", q.getQuestName())).clickCommand("/mq e edit name")
				.showText(I18n.locMsg("QuestEditor.QuestName.ShowText"))).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.QuestNPC")).clickCommand("/mq e edit npc")
				.showText(I18n.locMsg("QuestEditor.QuestNPC.ShowText"))).endNormally();
		if (q.isCommandQuest())
			p1.add(I18n.locMsg("QuestEditor.CommandQuest")).endNormally();
		else
			p1.add(new InteractiveText("").showNPCInfo(q.getQuestNPC())).endNormally();
		p1.changeLine();
		
		
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.IsRedoable")).clickCommand("/mq e edit redo " + !q.isRedoable())
				.showText(I18n.locMsg("QuestEditor.IsRedoable.ShowText." + !q.isRedoable()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + q.isRedoable())).changeLine();
		if (q.isRedoable())
		{
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.RedoDelay")).clickCommand("/mq e edit redodelay")
					.showText(I18n.locMsg("QuestEditor.RedoDelay.ShowText"))).endNormally();
			p1.add(QuestUtil.convertTime(q.getRedoDelay())).changeLine();
		}
		QuestSetting vs = q.getSettings();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.IsQuitable")).clickCommand("/mq e edit quit " + !q.isQuitable())
			.showText(I18n.locMsg("QuestEditor.IsQuitable.ShowText." + !q.isQuitable()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + q.isQuitable())).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.UsePermission")).clickCommand("/mq e edit perm " + !q.usePermission())
				.showText(I18n.locMsg("QuestEditor.UsePermission.ShowText." + !q.usePermission()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + q.usePermission())).changeLine();
		p1.add(new InteractiveText((I18n.locMsg("QuestEditor.WorldLimit"))).clickCommand("/mq e edit world")
				.showText(I18n.locMsg("QuestEditor.WorldLimit.ShowText"))).endNormally();
		if (q.hasWorldLimit())
			p1.add(q.getWorldLimit().getName()).changeLine();
		else
			p1.add(I18n.locMsg("QuestEditor.NotSet")).changeLine();
		
		p1.changeLine();

		p1.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnTake")).clickCommand("/mq e edit vis take " + !vs.displayOnTake()).showText(I18n.locMsg("QuestVisibility." + !vs.displayOnTake()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + vs.displayOnTake())).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnProgress")).clickCommand("/mq e edit vis prog " + !vs.displayOnProgress()).showText(I18n.locMsg("QuestVisibility." + !vs.displayOnProgress()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + vs.displayOnProgress())).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnFinish")).clickCommand("/mq e edit vis finish " + !vs.displayOnFinish()).showText(I18n.locMsg("QuestVisibility." + !vs.displayOnFinish()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + vs.displayOnFinish())).changeLine();
		
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.IsTimeLimited")).clickCommand("/mq e edit limit " + !q.isTimeLimited()).showText(I18n.locMsg("QuestEditor.IsTimeLimited.ShowText." + !q.isTimeLimited()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + q.isTimeLimited())).changeLine();
		if (q.isTimeLimited())
		{
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.TimeLimit")).clickCommand("/mq e edit timelimit")
					.showText(I18n.locMsg("QuestEditor.TimeLimit.ShowText"))).endNormally();
			p1.add(QuestUtil.convertTime(q.getTimeLimit())).changeLine();
		}
		
		QuestBookPage p2 = new QuestBookPage();
		p2.add(I18n.locMsg("QuestEditor.ReqEventStageInfo")).changeLine();
		p2.changeLine();
		p2.add(I18n.locMsg("QuestEditor.Requirement")).changeLine();
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.GoToEditPage")).clickCommand("/mq e edit req")
				.showText(I18n.locMsg("QuestEditor.Requirement.ShowText"))).changeLine();
		p2.changeLine();
		p2.add(I18n.locMsg("QuestEditor.QuestEvent")).changeLine();
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.GoToEditPage")).clickCommand("/mq e edit evt")
				.showText(I18n.locMsg("QuestEditor.Event.ShowText"))).changeLine();
		p2.changeLine();
		p2.add(I18n.locMsg("QuestEditor.QuestStage")).changeLine();
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.GoToEditPage")).clickCommand("/mq e edit stage")
				.showText(I18n.locMsg("QuestEditor.QuestStage.ShowText"))).changeLine();

		QuestBookPage p3 = new QuestBookPage();
		p3.add(I18n.locMsg("QuestEditor.Outline")).changeLine();
		for (String out : q.getQuestOutline())
		{
			p3.add(out).changeLine();
		}
		p3.changeLine();
		p3.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit outline")).changeLine();;

		QuestBookPage p4 = new QuestBookPage();
		p4.add(I18n.locMsg("QuestEditor.Reward")).changeLine();

		p4.add(I18n.locMsg("QuestEditor.RewardMoney", Double.toString(q.getQuestReward().getMoney()))).endNormally();
		p4.add(new InteractiveText(" " + I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward money").showText(I18n.locMsg("QuestEditor.RewardMoney.ShowText"))).changeLine();

		p4.add(I18n.locMsg("QuestEditor.RewardExp", Integer.toString(q.getQuestReward().getExp()))).endNormally();
		p4.add(new InteractiveText(" " + I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward exp").showText(I18n.locMsg("QuestEditor.RewardExp.ShowText"))).changeLine();
		
		if (Main.instance.pluginHooker.hasSkillAPIEnabled())
		{
			p4.add(I18n.locMsg("QuestEditor.RewardSkillAPIExp", Integer.toString(q.getQuestReward().getSkillAPIExp()))).endNormally();
			p4.add(new InteractiveText(" " + I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward saexp").showText(I18n.locMsg("QuestEditor.RewardSkillAPIExp.ShowText"))).changeLine();
		}
	
		p4.add(I18n.locMsg("QuestEditor.RewardFriendPoint")).endNormally();
		p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew reward fp").showText(I18n.locMsg("QuestEditor.RewardFriendPoint.ShowText"))).changeLine();
		
		if (q.getQuestReward().hasFriendPoint())
		{
			for (Integer n : q.getQuestReward().getFp().keySet())
			{
				NPC npc = CitizensAPI.getNPCRegistry().getById(n);
				if (npc == null)
					continue;
				p4.add("- ").endNormally();
				p4.add(new InteractiveText("").showNPCInfo(npc)).endNormally();
				p4.add("&0 " + q.getQuestReward().getFp().get(n) + " " + I18n.locMsg("QuestEditor.Point")).endNormally();
				p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward fp " + n)).endNormally();
				p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove reward fp " + n)).endNormally();
				p4.changeLine();
			}
		}
		
		p4.add(I18n.locMsg("QuestEditor.RewardCommand")).endNormally();
		p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew reward command").showText(I18n.locMsg("QuestEditor.RewardCommand.ShowText"))).changeLine();
		if (q.getQuestReward().hasCommand())
		{
			int counter = 1;
			for (String s : q.getQuestReward().getCommands())
			{
				p4.add(new InteractiveText("- &0" + I18n.locMsg("QuestEditor.Command") + "(" + counter + ")").showText("&f/" + s));
				p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward command " + (counter - 1))).endNormally();
				p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove reward command " + (counter - 1))).endNormally();
				p4.changeLine();
				counter++;
			}
		}
		p4.add(I18n.locMsg("QuestEditor.RewardItem")).endNormally();
		p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward item").showText(I18n.locMsg("QuestEditor.RewardItem.ShowText"))).changeLine();

		QuestBookPage p5 = new QuestBookPage();
		p5.add(I18n.locMsg("QuestEditor.SaveAndExit")).changeLine();
		p5.add(new InteractiveText(I18n.locMsg("QuestEditor.SyncSevAndLoc")).clickCommand("/mq e sa").showText(I18n.locMsg("QuestEditor.WarnSave"))).changeLine();
		p5.add(new InteractiveText(I18n.locMsg("QuestEditor.SyncSev")).clickCommand("/mq e sl").showText(I18n.locMsg("QuestEditor.WarnSave"))).changeLine();
		p5.add(new InteractiveText(I18n.locMsg("QuestEditor.SyncLoc")).clickCommand("/mq e sc").showText(I18n.locMsg("QuestEditor.WarnSave"))).changeLine();
		p5.changeLine();
		p5.add(new InteractiveText(I18n.locMsg("QuestEditor.QuitEditor")).clickCommand("/mq e exit").showText(I18n.locMsg("QuestEditor.ExitEditor.ShowText"))).changeLine();

		QuestBookGUIManager.openBook(p, p1, p2, p3, p4, p5);
	}

	public static void editQuestTrigger(Player p, TriggerType type, int stage)
	{
		if (!checkEditorMode(p, true))
			return;
		FlexiableBook book = new FlexiableBook();
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage page = book.getLastEditingPage();
		page.add(I18n.locMsg("QuestEditor.EditTrigger") + q.getQuestName()).changeLine();
		page.add(I18n.locMsg("QuestEditor.EditTriggerType") + type.toCustomString(stage)).changeLine();
		int index = 0;
		int realIndex = -1;
		
		if (q.hasTrigger(type))
		{
			for (TriggerObject obj : q.getTriggerMap().get(type))
			{
				realIndex++;
				if (obj.getStage() != stage)
					continue;
				QuestUtil.checkOutOfBounds(page, book);
				page = book.getLastEditingPage();
				page.add("- " + index + ".");
				
				page.add(new InteractiveText(obj.getObjType().toCustomString())
						.showText(I18n.locMsg("QuestEditor.EditTriggerObjectType") + obj.getObject().toString())).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew evt " + type.toString() + " " + stage + " " + (realIndex+1) + " ")).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit evt " + type.toString() + " " + stage + " " + realIndex + " " + obj.getObjType().toString())).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove evt " + type.toString() + " " + stage + " " + realIndex)).endNormally();
				page.changeLine();
				index++;
			}
		}
		
		if (q.getTriggerMap().get(type) == null || q.getTriggerMap().get(type).size() == 0)
			page.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew evt " + type.toString() + " " + stage + " " + 0 + " ")).changeLine();
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e gui")).endNormally();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void editQuestStages(Player p)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.EditQuestStage")).changeLine();
		p1.add(I18n.locMsg("QuestEditor.ChooseStage")).changeLine();
		for (int i = 1; i <= q.getStages().size(); i++)
		{
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Stage", Integer.toString(i))).clickCommand("/mq e edit stage " + i));
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove stage " + i)).changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew stage")).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e gui")).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void editQuestObjects(Player p, int stage)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.EditStage", Integer.toString(stage))).changeLine();
		p1.add(I18n.locMsg("QuestEditor.ChooseObject")).changeLine();
		for (int i = 1; i <= q.getStage(stage - 1).getObjects().size(); i++)
		{
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Object", Integer.toString(stage))).clickCommand("/mq e edit object " + stage + " " + i));
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove object " + stage + " " + i)).changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew object " + stage)).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit stage")).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void editQuestObject(Player p, int stage, int obj)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		SimpleQuestObject o = q.getStage(stage - 1).getObject(obj - 1);
		if (o instanceof CustomQuestObject)
		{
			QuestEditorManager.editQuestObjects(p, stage);
			QuestChatManager.error(p, I18n.locMsg("CustomObject.NotEditable"));
			return;
		}
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.EditObject", Integer.toString(stage), Integer.toString(obj))).changeLine();
		p1.add(I18n.locMsg("QuestEditor.ObjectName") + o.getObjectName()).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " type")).changeLine();
		o.formatEditorPage(p1, stage, obj);
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit stage " + stage)).changeLine();
		QuestBookGUIManager.openBook(p, p1);
		return;
	}

	@SuppressWarnings("unchecked")
	public static void editQuestRequirement(Player p)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage p1 = new QuestBookPage();
		QuestBookPage p2 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.EditRequirement")).changeLine();
		for (RequirementType t : RequirementType.values())
		{
			int i = 0;
			switch (t)
			{
				case ITEM:
					p1.add(I18n.locMsg("QuestEditor.ItemReq")).endNormally();
					p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req ITEM"));
					p1.changeLine();
					break;
				case LEVEL:
					p1.add(I18n.locMsg("QuestEditor.LevelReq") + q.getRequirements().get(t).toString() + " ").endNormally();
					p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req LEVEL"));
					p1.changeLine();
					break;
				case MONEY:
					p1.add(I18n.locMsg("QuestEditor.MoneyReq") + q.getRequirements().get(t).toString() + " ").endNormally();
					p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req MONEY"));
					p1.changeLine();
					break;
				case SKILLAPI_CLASS:
					if (Main.instance.pluginHooker.hasSkillAPIEnabled())
					{
						p1.add(I18n.locMsg("QuestEditor.SkillAPIClassReq") + q.getRequirements().get(t).toString() + " ").endNormally();
						p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req SKILLAPI_CLASS"));
						p1.changeLine();
					}
					break;
				case SKILLAPI_LEVEL:
					if (Main.instance.pluginHooker.hasSkillAPIEnabled())
					{
						p1.add(I18n.locMsg("QuestEditor.SkillAPILevelReq") + q.getRequirements().get(t).toString() + " ").endNormally();
						p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req SKILLAPI_LEVEL"));
						p1.changeLine();
					}
					break;
				case NBTTAG:
					p2.add(I18n.locMsg("QuestEditor.NBTReq")).changeLine();
					i = 0;
					for (String s : (List<String>) q.getRequirements().get(t))
					{
						p2.add("- ").add(s).endNormally();
						p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req NBTTAG " + i)).endNormally();
						p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove req NBTTAG " + i)).endNormally();
						p2.changeLine();
						i++;
					}
					p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew req NBTTAG " + i)).endNormally();
					p2.changeLine();
					break;
				case QUEST:
					p2.add(I18n.locMsg("QuestEditor.QuestReq"));
					p2.changeLine();
					i = 0;
					for (String s : (List<String>) q.getRequirements().get(t))
					{
						p2.add("- ").add(s).endNormally();
						p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req QUEST " + i)).endNormally();
						p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove req QUEST " + i)).endNormally();
						p2.changeLine();
						i++;
					}
					p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew req QUEST " + i)).endNormally();
					p2.changeLine();
					break;
				case SCOREBOARD:
					p2.add(I18n.locMsg("QuestEditor.ScoreboardReq"));
					p2.changeLine();
					i = 0;
					for (String s : (List<String>) q.getRequirements().get(t))
					{
						p2.add("- ").add(s).endNormally();
						p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req SCOREBOARD " + i)).endNormally();
						p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove req SCOREBOARD " + i)).endNormally();
						p2.changeLine();
						i++;
					}
					p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew req SCOREBOARD " + i)).endNormally();
					p2.changeLine();
					break;
			}
		}
		p2.changeLine();
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e gui")).changeLine();
		QuestBookGUIManager.openBook(p, p1, p2);
	}

	public static void selectTriggerType(Player p, String mode)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.SelectTriggerType")).changeLine();
		for (TriggerType t : TriggerType.values())
		{
			p1.add(new InteractiveText("- [" + t.toCustomString() + "]").clickCommand("/mq e " + mode + " evt " + t.toString())).endNormally();
			p1.changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e gui")).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void selectTriggerStage(Player p, String mode, TriggerType t)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.SelectTriggerStage")).changeLine();
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		for (int s = 1; s <= q.getStages().size(); s++)
		{
			p1.add(new InteractiveText("- " + I18n.locMsg("QuestEditor.Stage", Integer.toString(s))).clickCommand("/mq e " + mode + " evt " + t.toString() + " " + s)).endNormally();
			p1.changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e gui")).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void selectTriggerObjType(Player p, TriggerType t, int stage, int index)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.SelectTriggerObj")).changeLine();
		for (TriggerObjectType otype : TriggerObjectType.values())
		{
			p1.add(new InteractiveText("- [" + otype.toCustomString() + "]").clickCommand("/mq e addnew evt " + t.toString() + " " + stage + " " + index + " " + otype.toString())).endNormally();
			p1.changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e addnew evt " + t.toString())).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void selectObjectType(Player p, int stage, int obj)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.SelectObjectType")).changeLine();
		for (String s : SimpleQuestObject.ALL_OBJECTS.keySet())
		{
			p1.add(new InteractiveText("- [" + SimpleQuestObject.ALL_OBJECTS.get(s) + "]").clickCommand("/mq e edit object " + stage + " " + obj + " type " + s)).endNormally();
			p1.changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit object " + stage + " " + obj)).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}
	
	public static void selectQuest(Player p, String cmd)
	{
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		page.add(I18n.locMsg("QuestEditor.ChooseTargetQuest")).changeLine();
		for (Quest q : QuestStorage.Quests.values())
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add(new InteractiveText("&0- &l" + q.getQuestName() + "&0(" + q.getInternalID() + ")")
					.clickCommand(cmd + " " + q.getInternalID())).endNormally();
			page.changeLine();
		}
		QuestUtil.checkOutOfBounds(page, book);
		page = book.getLastEditingPage();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void createQuest(Player p)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.NewQuestTitle")).changeLine();
		p1.add(I18n.locMsg("QuestEditor.NewQuestDesc")).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.NewQuestButton")).clickCommand("/mq e newquest create")).endNormally();
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		p1.changeLine();
		String id = (q.getInternalID() != null) ? q.getInternalID() : I18n.locMsg("QuestEditor.NotSet");
		p1.add(I18n.locMsg("QuestEditor.NewQuestID")).endNormally();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e newquest id")).changeLine();
		p1.add(id).changeLine();
		String name = (q.getQuestName() != null) ? q.getQuestName() : I18n.locMsg("QuestEditor.NotSet");
		p1.add(I18n.locMsg("QuestEditor.NewQuestName")).endNormally();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e newquest name")).changeLine();
		p1.add(name).changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e")).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void generateEditItemGUI(Player p, String type, List<ItemStack> list)
	{
		if (checkEditorMode(p, false))
		{
			Quest q = QuestEditorManager.getCurrentEditingQuest(p);
			Inventory inv = Bukkit.createInventory(null, 27, "〈" + type + "〉" + q.getQuestName());
			inv.addItem(list.toArray(new ItemStack[list.size()]));
			p.openInventory(inv);
		}
		else
			return;
	}
}
