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
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;
import me.Cutiemango.MangoQuest.model.RequirementType;
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
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.ReturnToMenu")).clickCommand("/mq e"));
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
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.ReturnToMenu")).clickCommand("/mq e"));
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
		p1.add("&d&l基本資料編輯》 ").changeLine();
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

		if (q.isRedoable())
		{
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.IsRedoable")).clickCommand("/mq e edit redo false")
					.showText(I18n.locMsg("QuestEditor.IsRedoable.ShowText.False"))).endNormally();
			p1.add(I18n.locMsg("QuestEditor.True")).changeLine();
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.RedoDelay")).clickCommand("/mq e edit redodelay")
					.showText(I18n.locMsg("QuestEditor.RedoDelay.ShowText"))).endNormally();
			p1.add(QuestUtil.convertTime(q.getRedoDelay())).changeLine();
		}
		else
		{
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.IsRedoable")).clickCommand("/mq e edit redo true")
					.showText(I18n.locMsg("QuestEditor.IsRedoable.ShowText.True"))).endNormally();
			p1.add(I18n.locMsg("QuestEditor.False")).changeLine();
		}

		QuestBookPage p2 = new QuestBookPage();
		p2.add(I18n.locMsg("QuestEditor.ReqEventStageInfo")).changeLine();

		p2.add(I18n.locMsg("QuestEditor.Requirement")).changeLine();
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.GoToEditPage")).clickCommand("/mq e edit req")
				.showText(I18n.locMsg("QuestEditor.Requirement.ShowText"))).changeLine();
		p2.add(I18n.locMsg("QuestEditor.Event")).changeLine();
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.GoToEditPage")).clickCommand("/mq e edit evt")
				.showText(I18n.locMsg("QuestEditor.Event.ShowText"))).changeLine();
		p2.add(I18n.locMsg("QuestEditor.Stage")).changeLine();
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.GoToEditPage")).clickCommand("/mq e edit stage")
				.showText(I18n.locMsg("QuestEditor.Stage.ShowText"))).changeLine();

		QuestBookPage p3 = new QuestBookPage();
		p3.add("&8&l任務提要》").changeLine();
		for (String out : q.getQuestOutline())
		{
			p3.add(out).changeLine();
		}
		p3.changeLine();
		p3.add(new InteractiveText("&0&l[編輯提要]").clickCommand("/mq e edit outline")).changeLine();;

		QuestBookPage p4 = new QuestBookPage();
		p4.add("&6&l任務獎勵》").changeLine();

		p4.add("金錢： " + q.getQuestReward().getMoney() + " 元").endNormally();
		p4.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit reward money").showText("&f點擊以輸入 &6金錢獎勵")).changeLine();

		p4.add("經驗值： " + q.getQuestReward().getExp() + " 點").endNormally();
		p4.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit reward exp").showText("&f點擊以輸入 &b經驗值獎勵")).changeLine();

		p4.add(new InteractiveText("好感度： ").clickCommand("/mq e addnew reward fp").showText("&f點擊以&c新增&f好感度設定")).changeLine();
		if (q.getQuestReward().hasFriendPoint())
		{
			for (Integer n : q.getQuestReward().getFp().keySet())
			{
				NPC npc = CitizensAPI.getNPCRegistry().getById(n);
				if (npc == null)
					continue;
				p4.add("- ").endNormally();
				p4.add(new InteractiveText("").showNPCInfo(npc)).endNormally();
				p4.add("&0 " + q.getQuestReward().getFp().get(n) + " 點").endNormally();
				p4.add(new InteractiveText("&0&l[編輯]").clickCommand("/mq e edit reward fp " + n)).endNormally();
				p4.add(new InteractiveText("&0&l[移除]").clickCommand("/mq e remove reward fp " + n)).endNormally();
				p4.changeLine();
			}
		}
		p4.add(new InteractiveText("指令： ").clickCommand("/mq e addnew reward command").showText("&f點擊以&c新增&f指令")).changeLine();
		if (q.getQuestReward().hasCommand())
		{
			int counter = 1;
			for (String s : q.getQuestReward().getCommands())
			{
				p4.add(new InteractiveText("- &0指令(" + counter + ")").showText("&f/" + s));
				p4.add(new InteractiveText("&0&l[編輯]").clickCommand("/mq e edit reward command " + (counter - 1))).endNormally();
				p4.add(new InteractiveText("&0&l[移除]").clickCommand("/mq e remove reward command " + (counter - 1))).endNormally();
				p4.changeLine();
				counter++;
			}
		}
		p4.add("物品： ").changeLine();
		p4.add(new InteractiveText("&7[點擊開啟GUI]").clickCommand("/mq e edit reward item").showText("&f請將物品丟入物品欄，系統將會自動讀取。")).changeLine();

		QuestBookPage p5 = new QuestBookPage();
		p5.add("&c&l儲存/退出》").changeLine();
		p5.add(new InteractiveText("&2&l【同步伺服器與設定檔】").clickCommand("/mq e sa").showText("&c&l注意： 目前持有此任務的玩家都會遺失任務進度。")).changeLine();
		p5.add(new InteractiveText("&5&l【同步伺服器】").clickCommand("/mq e sl").showText("&c&l注意： 目前持有此任務的玩家都會遺失任務進度。")).changeLine();
		p5.add(new InteractiveText("&9&l【同步設定檔】").clickCommand("/mq e sc").showText("&c&l注意： 目前持有此任務的玩家都會遺失任務進度。")).changeLine();
		p5.changeLine();
		p5.add(new InteractiveText("&0&l[退出編輯器]").clickCommand("/mq e exit").showText("&c&l所做的任何變更都不會儲存。")).changeLine();

		QuestGUIManager.openBook(p, p1, p2, p3, p4, p5);
	}

	public static void editQuestTrigger(Player p)
	{
		if (!checkEditorMode(p, true))
			return;
		FlexiableBook book = new FlexiableBook();
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage page = book.getLastEditingPage();
		page.add("&0&l編輯任務事件： " + q.getQuestName()).changeLine();
		int index = 0;
		if (q.hasTrigger())
		{
			for (QuestTrigger qt : q.getTriggers())
			{
				QuestUtil.checkOutOfBounds(page, book);
				page = book.getLastEditingPage();
				page.add("- " + index + ".");
				if (qt.getType().equals(TriggerType.TRIGGER_STAGE_START) || qt.getType().equals(TriggerType.TRIGGER_STAGE_FINISH))
				{
					page.add(new InteractiveText(qt.getTriggerObject().toCustomString())
							.showText("觸發時機： " + qt.getType().toCustomString(qt.getCount()) + "\n觸發物件內容： " + qt.getObject().toString())).endNormally();
					page.add(new InteractiveText("&7[編輯]").clickCommand("/mq e edit evt " + index + " " + qt.getType().toString() + " " + qt.getCount()
							+ " " + qt.getTriggerObject().toString())).endNormally();
				}
				else
				{
					page.add(new InteractiveText(qt.getTriggerObject().toCustomString())
							.showText("觸發時機： " + qt.getType().toCustomString() + "\n觸發物件內容： " + qt.getObject().toString())).endNormally();
					page.add(new InteractiveText("&7[編輯]")
							.clickCommand("/mq e edit evt " + index + " " + qt.getType().toString() + " " + qt.getTriggerObject().toString())).endNormally();
				}
				page.add(new InteractiveText("&7[移除]").clickCommand("/mq e remove evt " + index)).endNormally();
				page.changeLine();
				index++;
			}
		}
		page.add(new InteractiveText("&0&l[新增]").clickCommand("/mq e addnew evt")).endNormally();
		page.changeLine();
		page.add(new InteractiveText("&0&l[返回任務選單]").clickCommand("/mq e gui")).endNormally();
		QuestGUIManager.openBook(p, book.toSendableBook());
	}

	public static void editQuestStages(Player p)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&0&l編輯任務內容： ").changeLine();
		p1.add(QuestChatManager.translateColor("&c(選擇要編輯的任務階段。)")).changeLine();
		for (int i = 1; i <= q.getStages().size(); i++)
		{
			p1.add(new InteractiveText("[編輯階段(" + i + ")]").clickCommand("/mq e edit stage " + i));
			p1.add(new InteractiveText("&c [移除]").clickCommand("/mq e remove stage " + i)).changeLine();
		}
		p1.add(new InteractiveText("&c&l[新增]").clickCommand("/mq e addnew stage")).changeLine();
		p1.add(new InteractiveText("&0&l[返回任務選單]").clickCommand("/mq e gui")).changeLine();
		QuestGUIManager.openBook(p, p1);
	}

	public static void editQuestObjects(Player p, int stage)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&0&l編輯：任務階段(" + stage + ")").changeLine();
		p1.add("&c(選擇要編輯的任務目標。)").changeLine();
		for (int i = 1; i <= q.getStage(stage - 1).getObjects().size(); i++)
		{
			p1.add(new InteractiveText("[編輯目標(" + i + ")]").clickCommand("/mq e edit object " + stage + " " + i));
			p1.add(new InteractiveText("&c [移除]").clickCommand("/mq e remove object " + stage + " " + i)).changeLine();
		}
		p1.add(new InteractiveText("&c&l[新增]").clickCommand("/mq e addnew object " + stage)).changeLine();
		p1.add(new InteractiveText("&0&l[返回選擇任務階段]").clickCommand("/mq e edit stage")).changeLine();
		QuestGUIManager.openBook(p, p1);
	}

	public static void editQuestObject(Player p, int stage, int obj)
	{
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		SimpleQuestObject o = q.getStage(stage - 1).getObject(obj - 1);
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&0&l編輯：任務目標(" + stage + "-" + obj + ")").changeLine();
		p1.add("&c&l目標類別：&0 " + o.getObjectName()).changeLine();
		p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " type")).changeLine();
		switch (o.getConfigString())
		{
			case "BREAK_BLOCK":
				p1.add("&0破壞方塊： " + QuestUtil.translate(((QuestObjectBreakBlock) o).getType(), ((QuestObjectBreakBlock) o).getShort())).endNormally();
				p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " block")).changeLine();
				break;
			case "CONSUME_ITEM":
				p1.add("&0消耗物品： ");
				p1.add(new InteractiveText("").showItem(((ItemObject) o).getItem())).endNormally();
				p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " item")).changeLine();
				break;
			case "DELIVER_ITEM":
				p1.add("&0提交物品： ");
				p1.add(new InteractiveText("").showItem(((ItemObject) o).getItem())).endNormally();
				p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " item")).changeLine();

				p1.add("&0提交NPC： ");
				NPC npc = ((QuestObjectDeliverItem) o).getTargetNPC();
				if (npc == null)
					p1.add(new InteractiveText("&0【未設定的NPC】").showText("&c請盡快更換此任務NPC，因為不存在！")).endNormally();
				else
					p1.add(new InteractiveText("").showNPCInfo(npc)).endNormally();
				p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " itemnpc")).changeLine();
				break;
			case "KILL_MOB":
				if (Main.instance.initManager.hasMythicMobEnabled())
				{
					p1.add("&0自定義MTM怪物名稱： ");
					if (((QuestObjectKillMob) o).isMythicObject())
						p1.add(((QuestObjectKillMob) o).getMythicMob().getDisplayName() + "("
								+ ((QuestObjectKillMob) o).getMythicMob().getInternalName() + ")").endNormally();
					else
						p1.add("&c未設定").endNormally();
					p1.changeLine();
					p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " mtmmob")).changeLine();
				}
				p1.add("&0自定義怪物名稱： ");
				if (((QuestObjectKillMob) o).hasCustomName())
					p1.add(((QuestObjectKillMob) o).getCustomName()).endNormally();
				else
					p1.add("&c未設定");
				p1.changeLine();
				p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " mobname"));
				p1.changeLine();

				p1.add("&0怪物類別： " + QuestUtil.translate(((QuestObjectKillMob) o).getType()));
				p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " mobtype"));
				p1.changeLine();
				break;
			case "REACH_LOCATION":
				p1.add("&0到達地點： ").endNormally();
				p1.add("(" + ((QuestObjectReachLocation) o).getLocation().getBlockX() + ", "
						+ ((QuestObjectReachLocation) o).getLocation().getBlockY() + ", " + ((QuestObjectReachLocation) o).getLocation().getBlockZ()
						+ ")").endNormally();
				p1.changeLine();
				p1.add("&0到達範圍： " + ((QuestObjectReachLocation) o).getRadius() + " 格").endNormally();
				p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " loc")).endNormally();
				p1.changeLine();
				p1.add("&0到達地點名稱： " + ((QuestObjectReachLocation) o).getName()).endNormally();
				p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " locname")).endNormally();
				p1.changeLine();
				break;
			case "TALK_TO_NPC":
				p1.add("&0NPC： ");
				npc = ((QuestObjectTalkToNPC) o).getTargetNPC();
				if (npc == null)
					p1.add(new InteractiveText("&0【未設定的NPC】").showText("&c請盡快更換此任務NPC，因為不存在！"));
				else
					p1.add(new InteractiveText("").showNPCInfo(npc));
				p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " npc"));
				p1.changeLine();
				break;

		}
		if (o instanceof NumerableObject)
		{
			p1.add("&0目標數量： " + ((NumerableObject) o).getAmount());
			p1.add(new InteractiveText("&0 &l[編輯]").clickCommand("/mq e edit object " + stage + " " + obj + " amount"));
			p1.changeLine();
		}
		p1.changeLine();
		p1.add(new InteractiveText("&0&l[返回選擇任務目標]").clickCommand("/mq e edit stage " + stage)).changeLine();
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
		p1.add("&0&l編輯任務需求： " + q.getQuestName()).changeLine();
		for (RequirementType t : RequirementType.values())
		{
			int i = 0;
			switch (t)
			{
				
				case ITEM:
					p1.add("物品需求：").changeLine();
					p1.add(new InteractiveText("&7[點擊開啟GUI]").clickCommand("/mq e edit req ITEM"));
					p1.changeLine();
					break;
				case LEVEL:
					p1.add("等級需求： " + q.getRequirements().get(t).toString() + " ").endNormally();
					p1.add(new InteractiveText("&7[編輯]").clickCommand("/mq e edit req LEVEL"));
					p1.changeLine();
					break;
				case MONEY:
					p1.add("金錢需求： " + q.getRequirements().get(t).toString() + " ").endNormally();
					p1.add(new InteractiveText("&7[編輯]").clickCommand("/mq e edit req MONEY"));
					p1.changeLine();
					break;

				case NBTTAG:
					p2.add("NBT標籤需求：").changeLine();
					i = 0;
					for (String s : (List<String>) q.getRequirements().get(t))
					{
						p2.add("- ").add(s).endNormally();
						p2.add(new InteractiveText("&7[編輯]").clickCommand("/mq e edit req NBTTAG " + i)).endNormally();
						p2.add(new InteractiveText("&7[移除]").clickCommand("/mq e remove req NBTTAG " + i)).endNormally();
						p2.changeLine();
						i++;
					}
					p2.add(new InteractiveText("&0&l[新增]").clickCommand("/mq e addnew req NBTTAG " + i)).endNormally();
					p2.changeLine();
					break;
				case QUEST:
					p2.add("任務需求：");
					p2.changeLine();
					i = 0;
					for (String s : (List<String>) q.getRequirements().get(t))
					{
						p2.add("- ").add(s).endNormally();
						p2.add(new InteractiveText("&7[編輯]").clickCommand("/mq e edit req QUEST " + i)).endNormally();
						p2.add(new InteractiveText("&7[移除]").clickCommand("/mq e remove req QUEST " + i)).endNormally();
						p2.changeLine();
						i++;
					}
					p2.add(new InteractiveText("&0&l[新增]").clickCommand("/mq e addnew req QUEST " + i)).endNormally();
					p2.changeLine();
					break;
				case SCOREBOARD:
					p2.add("記分板需求：");
					p2.changeLine();
					i = 0;
					for (String s : (List<String>) q.getRequirements().get(t))
					{
						p2.add("- ").add(s).endNormally();
						p2.add(new InteractiveText("&7[編輯]").clickCommand("/mq e edit req SCOREBOARD " + i)).endNormally();
						p2.add(new InteractiveText("&7[移除]").clickCommand("/mq e remove req SCOREBOARD " + i)).endNormally();
						p2.changeLine();
						i++;
					}
					p2.add(new InteractiveText("&0&l[新增]").clickCommand("/mq e addnew req SCOREBOARD " + i)).endNormally();
					p2.changeLine();
					break;
			}
		}
		p2.changeLine();
		p2.add(new InteractiveText("&0&l[返回任務選單]").clickCommand("/mq e gui")).changeLine();
		QuestGUIManager.openBook(p, p1, p2);
	}

	public static void selectTriggerType(Player p)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&0&l選擇觸發事件時機：").changeLine();
		for (TriggerType t : TriggerType.values())
		{
			p1.add(new InteractiveText("- [" + t.toCustomString() + "]").clickCommand("/mq e addnew evt " + t.toString()));
			p1.changeLine();
		}
		QuestGUIManager.openBook(p, p1);
	}

	public static void selectStage(Player p, TriggerType t)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&0&l選擇觸發事件階段：").changeLine();
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		for (int s = 1; s <= q.getStages().size(); s++)
		{
			p1.add(new InteractiveText("- [階段" + s + "]").clickCommand("/mq e addnew evt " + t.toString() + " " + s));
			p1.changeLine();
		}
		QuestGUIManager.openBook(p, p1);
	}

	public static void selectTriggerObject(Player p, TriggerType t, int s)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&0&l選擇觸發物件：").changeLine();
		for (TriggerObject o : TriggerObject.values())
		{
			if (t.equals(TriggerType.TRIGGER_STAGE_START) || t.equals(TriggerType.TRIGGER_STAGE_FINISH))
			{
				p1.add(new InteractiveText("- [" + o.toCustomString() + "]")
						.clickCommand("/mq e addnew evt " + t.toString() + " " + s + " " + o.toString()));
				p1.changeLine();
				continue;
			}
			p1.add(new InteractiveText("- [" + o.toCustomString() + "]").clickCommand("/mq e addnew evt " + t.toString() + " " + o.toString()));
			p1.changeLine();
		}
		QuestGUIManager.openBook(p, p1);
	}

	public static void selectObjectType(Player p, int stage, int obj)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&0&l選擇任務目標類別：").changeLine();
		for (String s : SimpleQuestObject.ALL_OBJECTS.keySet())
		{
			p1.add(new InteractiveText("- [" + SimpleQuestObject.ALL_OBJECTS.get(s) + "]").clickCommand("/mq e edit object " + stage + " " + obj + " type " + s));
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
					.clickCommand(cmd + " " + q.getInternalID()));
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
