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
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestSetting;
import me.Cutiemango.MangoQuest.model.RequirementType;
import me.Cutiemango.MangoQuest.model.TriggerObject;
import me.Cutiemango.MangoQuest.model.TriggerType;
import me.Cutiemango.MangoQuest.model.TriggerObject.TriggerObjectType;
import me.Cutiemango.MangoQuest.questobjects.ItemObject;
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectDeliverItem;
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
		QuestGUIManager.openBook(p, p1);
	}

	public static void editGUI(Player p)
	{
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
		QuestGUIManager.openBook(p, book.toSendableBook());
	}

	public static void removeGUI(Player p)
	{
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
		QuestGUIManager.openBook(p, book.toSendableBook());
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
		QuestGUIManager.openBook(p, p1);
	}

	public static void editQuest(Player p)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);

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
		
		p1.changeLine();

		p1.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnTake")).clickCommand("/mq e edit vis take " + !vs.displayOnTake()).showText(I18n.locMsg("QuestVisibility." + !vs.displayOnTake()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + vs.displayOnTake())).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnProgress")).clickCommand("/mq e edit vis prog " + !vs.displayOnProgress()).showText(I18n.locMsg("QuestVisibility." + !vs.displayOnProgress()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + vs.displayOnProgress())).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnFinish")).clickCommand("/mq e edit vis finish " + !vs.displayOnFinish()).showText(I18n.locMsg("QuestVisibility." + !vs.displayOnFinish()))).endNormally();
		p1.add(I18n.locMsg("QuestEditor." + vs.displayOnFinish())).changeLine();

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

		QuestGUIManager.openBook(p, p1, p2, p3, p4, p5);
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
		
		if (q.hasTrigger(type))
		{
			for (TriggerObject obj : q.getTriggerMap().get(type))
			{
				if (obj.getStage() != stage)
					continue;
				QuestUtil.checkOutOfBounds(page, book);
				page = book.getLastEditingPage();
				page.add("- " + index + ".");
				
				page.add(new InteractiveText(obj.getObjType().toCustomString())
						.showText(I18n.locMsg("QuestEditor.EditTriggerObjectType") + obj.getObject().toString())).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit evt " + type.toString() + " " + stage + " " + index + " " + obj.toString())).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove evt " + type.toString() + " " + stage + " " + index)).endNormally();
				page.changeLine();
				index++;
			}
		}
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew evt " + type.toString() + " " + stage)).changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e gui")).endNormally();
		QuestGUIManager.openBook(p, book.toSendableBook());
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
		QuestGUIManager.openBook(p, p1);
	}

	public static void editQuestObjects(Player p, int stage)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.EditStage")).changeLine();
		p1.add(I18n.locMsg("QuestEditor.ChooseObject")).changeLine();
		for (int i = 1; i <= q.getStage(stage - 1).getObjects().size(); i++)
		{
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Object", Integer.toString(stage))).clickCommand("/mq e edit object " + stage + " " + i));
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove object " + stage + " " + i)).changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew object " + stage)).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit stage")).changeLine();
		QuestGUIManager.openBook(p, p1);
	}

	public static void editQuestObject(Player p, int stage, int obj)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		SimpleQuestObject o = q.getStage(stage - 1).getObject(obj - 1);
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.EditObject", Integer.toString(stage), Integer.toString(obj))).changeLine();
		p1.add(I18n.locMsg("QuestEditor.ObjectName") + o.getObjectName()).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " type")).changeLine();
		switch (o.getConfigString())
		{
			case "BREAK_BLOCK":
				p1.add(I18n.locMsg("QuestEditor.BreakBlock") + QuestUtil.translate(((QuestObjectBreakBlock) o).getType(), ((QuestObjectBreakBlock) o).getShort())).endNormally();
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " block")).changeLine();
				break;
			case "CONSUME_ITEM":
				p1.add(I18n.locMsg("QuestEditor.ConsumeItem"));
				p1.add(new InteractiveText("").showItem(((ItemObject) o).getItem())).endNormally();
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " item")).changeLine();
				break;
			case "DELIVER_ITEM":
				p1.add(I18n.locMsg("QuestEditor.DeliverItem"));
				p1.add(new InteractiveText("").showItem(((ItemObject) o).getItem())).endNormally();
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " item")).changeLine();

				p1.add(I18n.locMsg("QuestEditor.DeliverNPC"));
				NPC npc = ((QuestObjectDeliverItem) o).getTargetNPC();
				if (npc == null)
					p1.add(new InteractiveText(I18n.locMsg("QuestEditor.NotSet"))).endNormally();
				else
					p1.add(new InteractiveText("").showNPCInfo(npc)).endNormally();
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " itemnpc")).changeLine();
				break;
			case "KILL_MOB":
				if (Main.instance.initManager.hasMythicMobEnabled())
				{
					p1.add(I18n.locMsg("QuestEditor.MythicMobs"));
					if (((QuestObjectKillMob) o).isMythicObject())
						p1.add(((QuestObjectKillMob) o).getMythicMob().getDisplayName() + "("
								+ ((QuestObjectKillMob) o).getMythicMob().getInternalName() + ")").endNormally();
					else
						p1.add(I18n.locMsg("QuestEditor.NotSet")).endNormally();
					p1.changeLine();
					p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " mtmmob")).changeLine();
				}
				p1.add(I18n.locMsg("QuestEditor.MobName"));
				if (((QuestObjectKillMob) o).hasCustomName())
					p1.add(((QuestObjectKillMob) o).getCustomName()).endNormally();
				else
					p1.add(I18n.locMsg("QuestEditor.NotSet"));
				p1.changeLine();
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " mobname")).changeLine();
				p1.changeLine();

				p1.add(I18n.locMsg("QuestEditor.MobType") + QuestUtil.translate(((QuestObjectKillMob) o).getType())).endNormally();
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " mobtype")).changeLine();
				p1.changeLine();
				break;
			case "REACH_LOCATION":
				p1.add(I18n.locMsg("QuestEditor.ReachLocation")).endNormally();
				p1.add("(" + ((QuestObjectReachLocation) o).getLocation().getBlockX() + ", "
						+ ((QuestObjectReachLocation) o).getLocation().getBlockY() + ", " + ((QuestObjectReachLocation) o).getLocation().getBlockZ()
						+ ")").endNormally();
				p1.changeLine();
				p1.add(I18n.locMsg("QuestEditor.ReachRadius") + ((QuestObjectReachLocation) o).getRadius() + I18n.locMsg("QuestEditor.WithinBlocks")).endNormally();
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " loc")).endNormally();
				p1.changeLine();
				p1.add(I18n.locMsg("QuestEditor.LocationName") + ((QuestObjectReachLocation) o).getName()).endNormally();
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " locname")).endNormally();
				p1.changeLine();
				break;
			case "TALK_TO_NPC":
				p1.add(I18n.locMsg("QuestEditor.TalkNPC")).endNormally();
				npc = ((QuestObjectTalkToNPC) o).getTargetNPC();
				if (npc == null)
					p1.add(new InteractiveText(I18n.locMsg("QuestEditor.NotSet"))).endNormally();
				else
					p1.add(new InteractiveText("").showNPCInfo(npc)).endNormally();
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " npc")).changeLine();
				p1.changeLine();
				break;

		}
		if (o instanceof NumerableObject)
		{
			p1.add(I18n.locMsg("QuestEditor.TargetAmount") + ((NumerableObject) o).getAmount());
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " amount"));
			p1.changeLine();
		}
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit stage " + stage)).changeLine();
		QuestGUIManager.openBook(p, p1);
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
		QuestGUIManager.openBook(p, p1, p2);
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
		QuestGUIManager.openBook(p, p1);
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
		QuestGUIManager.openBook(p, p1);
	}

	public static void selectTriggerObjType(Player p, TriggerType t, int stage)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.SelectTriggerObj")).changeLine();
		for (TriggerObjectType otype : TriggerObjectType.values())
		{
			p1.add(new InteractiveText("- [" + otype.toCustomString() + "]").clickCommand("/mq e addnew evt " + t.toString() + " " + stage + " " + otype.toString())).endNormally();
			p1.changeLine();
		}
		QuestGUIManager.openBook(p, p1);
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
		QuestGUIManager.openBook(p, p1);
	}
	
	public static void selectQuest(Player p, String cmd)
	{
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		page.add(I18n.locMsg("QuestEditor.ChooseEditQuest")).changeLine();
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
		page.add("&8&l(關閉並輸入cancel來取消輸入)").changeLine();
		QuestGUIManager.openBook(p, book.toSendableBook());
	}

	public static void createQuest(Player p)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&9&l新建任務》").changeLine();
		p1.add("&0若要新建任務，").changeLine();
		p1.add("確認以下兩個&c&l必須&0的參數輸入完成後，").changeLine();
		p1.add("&0點擊&a&l創建&0按鈕，").changeLine();
		p1.add("即可創建新任務。").changeLine();
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		p1.changeLine();
		if (q.getInternalID() == null)
			p1.add("&0新建任務內部ID： &c未設定");
		else
			p1.add("&0新建任務內部ID： " + q.getInternalID());
		p1.changeLine();
		p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e newquest id"));
		p1.changeLine();
		if (q.getQuestName() == null)
			p1.add("&0新建任務顯示名稱： &c未設定");
		else
			p1.add("&0新建任務顯示名稱： " + q.getQuestName());
		p1.changeLine();
		p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e newquest name"));
		p1.changeLine();
		p1.changeLine();
		p1.add(new InteractiveText("&2       &l【創建任務】").clickCommand("/mq e newquest create")).endNormally();
		QuestGUIManager.openBook(p, p1);
	}

	public static void generateEditItemGUI(Player p, String type, List<ItemStack> list)
	{
		if (checkEditorMode(p, false))
		{
			Quest q = QuestEditorManager.getCurrentEditingQuest(p);
			Inventory inv = Bukkit.createInventory(null, 27, "《" + type + "》" + q.getQuestName());
			inv.addItem(list.toArray(new ItemStack[list.size()]));
			p.openInventory(inv);
		}
		else
			return;
	}
}
