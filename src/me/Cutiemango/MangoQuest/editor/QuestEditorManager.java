package me.Cutiemango.MangoQuest.editor;

import com.sucy.skill.SkillAPI;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.FlexibleBook;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.manager.TimeHandler;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestSetting;
import me.Cutiemango.MangoQuest.objects.RequirementType;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerObject.TriggerObjectType;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.interfaces.EditorObject;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class QuestEditorManager
{

	public static HashMap<String, Quest> isEditing = new HashMap<>();

	public static Quest getCurrentEditingQuest(Player p) {
		return isEditing.get(p.getName());
	}

	public static void edit(Player p, Quest q) {
		isEditing.put(p.getName(), q);
	}

	public static void exit(Player p) {
		isEditing.remove(p.getName());
		QuestChatManager.info(p, I18n.locMsg("EditorMessage.Exited"));
	}

	public static boolean checkEditorMode(Player p, boolean msg) {
		if (!isEditing.containsKey(p.getName()) && msg)
			QuestChatManager.error(p, I18n.locMsg("EditorMessage.NotInEditor"));
		return isEditing.containsKey(p.getName());
	}

	public static void mainGUI(Player p) {
		EditorListenerHandler.unregister(p);
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

		if (checkEditorMode(p, false) && QuestEditorManager.getCurrentEditingQuest(p).getInternalID() != null) {
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.ReturnToEditor")).clickCommand("/mq e gui")
					.showText(I18n.locMsg("QuestEditor.ReturnToEditor.ShowText"))).changeLine();
			p1.changeLine();
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.ExitEditor")).clickCommand("/mq e exit")
					.showText(I18n.locMsg("QuestEditor.ExitEditor.ShowText"))).changeLine();
		}
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void editGUI(Player p) {
		EditorListenerHandler.unregister(p);
		FlexibleBook book = new FlexibleBook();
		book.add(I18n.locMsg("QuestEditor.Title")).changeLine();
		book.add(I18n.locMsg("QuestEditor.ChooseEditQuest")).changeLine();
		for (Quest q : QuestStorage.localQuests.values()) {
			book.add(new InteractiveText("&0- &0&l" + q.getQuestName() + "&0(" + q.getInternalID() + ")")
					.clickCommand("/mq e select " + q.getInternalID())).changeLine();
		}
		book.changeLine();
		book.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e"));
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void removeGUI(Player p) {
		EditorListenerHandler.unregister(p);
		FlexibleBook book = new FlexibleBook();
		book.add(I18n.locMsg("QuestEditor.Title")).changeLine();
		book.add(I18n.locMsg("QuestEditor.ChooseRemoveQuest")).changeLine();
		for (Quest q : QuestStorage.localQuests.values()) {
			book.add(new InteractiveText("&0- &0&l" + q.getQuestName() + "&0(" + q.getInternalID() + ")")
					.clickCommand("/mq e remove confirm " + q.getInternalID()));
			book.changeLine();
		}
		book.changeLine();
		book.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e"));
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void removeConfirmGUI(Player p, Quest q) {
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.WarnRemoveQuest1")).changeLine();
		p1.add(I18n.locMsg("QuestEditor.WarnRemoveQuest2", q.getQuestName())).changeLine();
		p1.add(I18n.locMsg("QuestEditor.WarnRemoveQuest3")).changeLine();
		p1.changeLine();
		p1.changeLine();
		p1.add("  ");
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.WarnAccept")).clickCommand("/mq e remove quest " + q.getInternalID()));
		p1.add(" &8&l/ ");
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.WarnDeny")).clickCommand("/mq e remove"));
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void editQuest(Player p) {
		if (!checkEditorMode(p, true))
			return;
		Quest q = getCurrentEditingQuest(p);
		QuestSetting vs = q.getSettings();
		EditorListenerHandler.unregister(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.BasicInfo")).changeLine();
		p1.add(I18n.locMsg("QuestEditor.QuestInternalID", q.getInternalID())).changeLine();

		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.QuestName", q.getQuestName())).clickCommand("/mq e edit name")
				.showText(I18n.locMsg("QuestEditor.QuestName.ShowText"))).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.QuestNPC")).clickCommand("/mq e edit npc")
				.showText(I18n.locMsg("QuestEditor.QuestNPC.ShowText")));
		if (q.isCommandQuest())
			p1.add(I18n.locMsg("QuestEditor.false"));
		else
			p1.add(new InteractiveText("").showNPCInfo(q.getQuestNPC()));
		p1.changeLine();

		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.RedoSetting", q.getRedoSetting().getName())).clickCommand("/mq e edit redo")
				.showText(I18n.locMsg("QuestEditor.RedoSetting.ShowText"))).changeLine();

		switch (q.getRedoSetting()) {
			case COOLDOWN:
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.RedoDelay", TimeHandler.convertTime(q.getRedoDelay())))
						.clickCommand("/mq e edit redodelay").showText(I18n.locMsg("QuestEditor.RedoDelay.ShowText"))).changeLine();
				break;
			case WEEKLY:
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.ResetDay", I18n.locMsg("QuestEditor.ResetDay." + q.getResetDay())))
						.clickCommand("/mq e edit resetday").showText(I18n.locMsg("QuestEditor.ResetDay.ShowText"))).changeLine();
			case DAILY:
				p1.add(new InteractiveText(I18n.locMsg("QuestEditor.ResetHour", Integer.toString(q.getResetHour())))
						.clickCommand("/mq e edit resethour").showText(I18n.locMsg("QuestEditor.ResetHour.ShowText"))).changeLine();
				break;
		}
		p1.changeLine();

		QuestBookPage settings = new QuestBookPage();
		settings.add(I18n.locMsg("QuestEditor.QuestSettings")).changeLine();
		settings.add(new InteractiveText(I18n.locMsg("QuestEditor.IsQuitable")).clickCommand("/mq e edit quit " + !q.isQuitable())
				.showText(I18n.locMsg("QuestEditor.IsQuitable.ShowText." + !q.isQuitable())));
		settings.add(I18n.locMsg("QuestEditor." + q.isQuitable())).changeLine();
		settings.add(new InteractiveText(I18n.locMsg("QuestEditor.UsePermission")).clickCommand("/mq e edit perm " + !q.usePermission())
				.showText(I18n.locMsg("QuestEditor.UsePermission.ShowText." + !q.usePermission())));
		settings.add(I18n.locMsg("QuestEditor." + q.usePermission())).changeLine();
		settings.add(new InteractiveText((I18n.locMsg("QuestEditor.WorldLimit"))).clickCommand("/mq e edit world")
				.showText(I18n.locMsg("QuestEditor.WorldLimit.ShowText")));
		if (q.hasWorldLimit())
			settings.add(q.getWorldLimit().getName()).changeLine();
		else
			settings.add(I18n.locMsg("QuestEditor.NotSet")).changeLine();

		settings.changeLine();

		// Display settings
		settings.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnTake")).clickCommand("/mq e edit vis take " + !vs.displayOnTake())
				.showText(I18n.locMsg("QuestVisibility." + !vs.displayOnTake())));
		settings.add(I18n.locMsg("QuestEditor." + vs.displayOnTake())).changeLine();
		settings.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnProgress")).clickCommand("/mq e edit vis prog " + !vs.displayOnProgress())
				.showText(I18n.locMsg("QuestVisibility." + !vs.displayOnProgress())));
		settings.add(I18n.locMsg("QuestEditor." + vs.displayOnProgress())).changeLine();
		settings.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnFinish")).clickCommand("/mq e edit vis finish " + !vs.displayOnFinish())
				.showText(I18n.locMsg("QuestVisibility." + !vs.displayOnFinish())));
		settings.add(I18n.locMsg("QuestEditor." + vs.displayOnFinish())).changeLine();
		settings.add(new InteractiveText(I18n.locMsg("QuestVisibility.OnInteraction"))
				.clickCommand("/mq e edit vis interact " + !vs.displayOnInteraction())
				.showText(I18n.locMsg("QuestVisibility." + !vs.displayOnInteraction())));
		settings.add(I18n.locMsg("QuestEditor." + vs.displayOnInteraction())).changeLine();

		// Time Limit
		settings.add(new InteractiveText(I18n.locMsg("QuestEditor.IsTimeLimited")).clickCommand("/mq e edit limit " + !q.isTimeLimited())
				.showText(I18n.locMsg("QuestEditor.IsTimeLimited.ShowText." + !q.isTimeLimited())));
		settings.add(I18n.locMsg("QuestEditor." + q.isTimeLimited())).changeLine();
		if (q.isTimeLimited()) {
			settings.add(new InteractiveText(I18n.locMsg("QuestEditor.TimeLimit")).clickCommand("/mq e edit timelimit")
					.showText(I18n.locMsg("QuestEditor.TimeLimit.ShowText")));
			settings.add(TimeHandler.convertTime(q.getTimeLimit())).changeLine();
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
		for (String out : q.getQuestOutline()) {
			p3.add(out).changeLine();
		}
		p3.changeLine();
		p3.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit outline")).changeLine();

		QuestBookPage p4 = new QuestBookPage();
		p4.add(I18n.locMsg("QuestEditor.Reward")).changeLine();

		p4.add(I18n.locMsg("QuestEditor.RewardMoney", Double.toString(q.getQuestReward().getMoney())));
		p4.add(new InteractiveText(" " + I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward money")
				.showText(I18n.locMsg("QuestEditor.RewardMoney.ShowText"))).changeLine();

		p4.add(I18n.locMsg("QuestEditor.RewardExp", Integer.toString(q.getQuestReward().getExp())));
		p4.add(new InteractiveText(" " + I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward exp")
				.showText(I18n.locMsg("QuestEditor.RewardExp.ShowText"))).changeLine();

		if (Main.getHooker().hasSkillAPIEnabled()) {
			p4.add(I18n.locMsg("QuestEditor.RewardRPGExp", Integer.toString(q.getQuestReward().getSkillAPIExp())));
			p4.add(new InteractiveText(" " + I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward saexp")
					.showText(I18n.locMsg("QuestEditor.RewardRPGExp.ShowText"))).changeLine();
		}

		if (Main.getHooker().hasQuantumRPGEnabled()) {
			p4.add(I18n.locMsg("QuestEditor.RewardRPGExp", Integer.toString(q.getQuestReward().getQRPGExp())));
			p4.add(new InteractiveText(" " + I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward qrpgexp")
					.showText(I18n.locMsg("QuestEditor.RewardRPGExp.ShowText"))).changeLine();
		}

		p4.add(I18n.locMsg("QuestEditor.RewardFriendPoint"));
		p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward fp")
				.showText(I18n.locMsg("QuestEditor.RewardFriendPoint.ShowText"))).changeLine();

		if (q.getQuestReward().hasFriendPoint()) {
			for (Integer n : q.getQuestReward().getFriendPointMap().keySet()) {
				NPC npc = CitizensAPI.getNPCRegistry().getById(n);
				if (npc == null)
					continue;
				p4.add("- ");
				p4.add(new InteractiveText("").showNPCInfo(npc));
				p4.add("&0 " + q.getQuestReward().getFriendPointMap().get(n) + " " + I18n.locMsg("QuestEditor.Point"));
				p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove reward fp " + n));
				p4.changeLine();
			}
		}

		p4.add(I18n.locMsg("QuestEditor.RewardCommand"));
		p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew reward command")
				.showText(I18n.locMsg("QuestEditor.RewardCommand.ShowText"))).changeLine();
		if (q.getQuestReward().hasCommand()) {
			int counter = 1;
			for (String s : q.getQuestReward().getCommands()) {
				p4.add(new InteractiveText("- &0" + I18n.locMsg("QuestEditor.Command") + "(" + counter + ")").showText("&f/" + s));
				p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward command " + (counter - 1)));
				p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove reward command " + (counter - 1)));
				p4.changeLine();
				counter++;
			}
		}
		p4.add(I18n.locMsg("QuestEditor.RewardItem"));
		p4.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit reward item")
				.showText(I18n.locMsg("QuestEditor.RewardItem.ShowText"))).changeLine();

		QuestBookPage p5 = new QuestBookPage();
		p5.add(I18n.locMsg("QuestEditor.SaveAndExit")).changeLine();
		p5.add(new InteractiveText(I18n.locMsg("QuestEditor.SyncSevAndLoc")).clickCommand("/mq e sa").showText(I18n.locMsg("QuestEditor.WarnSave")))
				.changeLine();
		p5.add(new InteractiveText(I18n.locMsg("QuestEditor.SyncSev")).clickCommand("/mq e sl").showText(I18n.locMsg("QuestEditor.WarnSave")))
				.changeLine();
		p5.add(new InteractiveText(I18n.locMsg("QuestEditor.SyncLoc")).clickCommand("/mq e sc").showText(I18n.locMsg("QuestEditor.WarnSave")))
				.changeLine();
		p5.changeLine();
		p5.add(new InteractiveText(I18n.locMsg("QuestEditor.QuitEditor")).clickCommand("/mq e exit")
				.showText(I18n.locMsg("QuestEditor.ExitEditor.ShowText"))).changeLine();

		QuestBookGUIManager.openBook(p, p1, settings, p2, p3, p4, p5);
	}

	public static void editQuestTrigger(Player p, TriggerType type, int stage) {
		if (!checkEditorMode(p, true))
			return;
		FlexibleBook book = new FlexibleBook();
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		book.add(I18n.locMsg("QuestEditor.EditTrigger") + q.getQuestName()).changeLine();
		book.add(I18n.locMsg("QuestEditor.EditTriggerType") + type.toCustomString(stage)).changeLine();
		int index = 0;
		int realIndex = -1;

		if (q.hasTrigger(type)) {
			for (TriggerObject obj : q.getTriggerMap().get(type)) {
				realIndex++;
				if (obj.getStage() != stage)
					continue;
				book.add("- " + index + ".");
				book.add(new InteractiveText(obj.getObjType().toCustomString())
						.showText(QuestChatManager.toNormalDisplay(I18n.locMsg("QuestEditor.EditTriggerObjectType") + obj.getObject())));

				book.add(new InteractiveText(I18n.locMsg("QuestEditor.Add"))
						.clickCommand("/mq e addnew evt " + type.toString() + " " + stage + " " + (realIndex + 1) + " "));
				book.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit"))
						.clickCommand("/mq e edit evt " + type.toString() + " " + stage + " " + realIndex + " " + obj.getObjType().toString()));
				book.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove"))
						.clickCommand("/mq e remove evt " + type.toString() + " " + stage + " " + realIndex));

				book.changeLine();
				index++;
			}
		}

		if (index == 0)
			book.add(new InteractiveText(I18n.locMsg("QuestEditor.Add"))
					.clickCommand("/mq e addnew evt " + type.toString() + " " + stage + " " + 0 + " ")).changeLine();

		book.changeLine();
		book.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit evt"));
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void editQuestStages(Player p) {
		if (!checkEditorMode(p, true))
			return;
		FlexibleBook book = new FlexibleBook();
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		book.add(I18n.locMsg("QuestEditor.EditQuestStage")).changeLine();
		book.add(I18n.locMsg("QuestEditor.ChooseStage")).changeLine();
		for (int i = 1; i <= q.getStages().size(); i++) {
			book.add(new InteractiveText(I18n.locMsg("QuestEditor.Stage", Integer.toString(i))).clickCommand("/mq e edit stage " + i));
			book.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove stage " + i)).changeLine();
		}
		book.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew stage")).changeLine();
		book.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e gui")).changeLine();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void editQuestObjects(Player p, int stage) {
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.EditStage", Integer.toString(stage))).changeLine();
		p1.add(I18n.locMsg("QuestEditor.ChooseObject")).changeLine();
		for (int i = 1; i <= q.getStage(stage - 1).getObjects().size(); i++) {
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Object", Integer.toString(i))).clickCommand("/mq e edit object " + stage + " " + i));
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove object " + stage + " " + i)).changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew object " + stage)).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit stage")).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void editQuestObject(Player p, int stage, int obj) {
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		SimpleQuestObject o = q.getStage(stage - 1).getObject(obj - 1);
		if (!(o instanceof EditorObject)) {
			QuestEditorManager.editQuestObjects(p, stage);
			QuestChatManager.error(p, I18n.locMsg("CustomObject.NotEditable"));
			return;
		}
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.EditObject", Integer.toString(stage), Integer.toString(obj))).changeLine();
		p1.add(I18n.locMsg("QuestEditor.ObjectName") + o.getObjectName()).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " type")).changeLine();
		((EditorObject) o).formatEditorPage(p1, stage, obj);
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit stage " + stage)).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void editQuestRequirement(Player p) {
		if (!checkEditorMode(p, true))
			return;
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		QuestBookPage p1 = new QuestBookPage();
		QuestBookPage p2 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.EditRequirement")).changeLine();

		// Level Req
		p1.add(I18n.locMsg("QuestEditor.LevelReq") + q.getRequirements().get(RequirementType.LEVEL) + " ");
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req LEVEL")).changeLine();

		// Money Req
		p1.add(I18n.locMsg("QuestEditor.MoneyReq") + q.getRequirements().get(RequirementType.MONEY) + " ");
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req MONEY")).changeLine();

		// Item Req
		p1.add(I18n.locMsg("QuestEditor.ItemReq"));
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req ITEM")).changeLine();

		p1.changeLine();

		if (Main.getHooker().hasSkillAPIEnabled()) {
			p1.add(I18n.locMsg("QuestEditor.SkillAPIReq")).changeLine();

			String classID = q.getRequirements().get(RequirementType.SKILLAPI_CLASS).toString();
			String displayName = classID.equalsIgnoreCase("none") ? "null" : SkillAPI.getClass(classID).getName();
			p1.add(I18n.locMsg("QuestEditor.RPGClassReq", displayName, classID));
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req SKILLAPI_CLASS")).changeLine();

			p1.add(I18n.locMsg("QuestEditor.RPGLevelReq", q.getRequirements().get(RequirementType.SKILLAPI_LEVEL).toString()));
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req SKILLAPI_LEVEL")).changeLine();

			boolean allow = (Boolean) q.getRequirements().get(RequirementType.ALLOW_DESCENDANT);
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.AllowDescendant"))
					.showText(I18n.locMsg("QuestEditor.AllowDescendant.ShowText." + !allow))
					.clickCommand("/mq e edit req ALLOW_DESCENDANT " + !allow));
			p1.add(I18n.locMsg("QuestEditor." + allow)).changeLine();
		}

		if (Main.getHooker().hasQuantumRPGEnabled()) {
			p1.add(I18n.locMsg("QuestEditor.QRPGReq")).changeLine();

			String classID = q.getRequirements().get(RequirementType.QRPG_CLASS).toString();
			String displayName = classID.equalsIgnoreCase("none") ?
					"null" :
					Main.getHooker().getQuantumRPG().getModuleCache().getClassManager().getClassById(classID).getName();
			p1.add(I18n.locMsg("QuestEditor.RPGClassReq", displayName, classID));
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req QRPG_CLASS")).changeLine();

			p1.add(I18n.locMsg("QuestEditor.RPGLevelReq", q.getRequirements().get(RequirementType.QRPG_LEVEL).toString()));
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req QRPG_LEVEL")).changeLine();

			boolean allow = (Boolean) q.getRequirements().get(RequirementType.ALLOW_DESCENDANT);
			p1.add(new InteractiveText(I18n.locMsg("QuestEditor.AllowDescendant"))
					.showText(I18n.locMsg("QuestEditor.AllowDescendant.ShowText." + !allow))
					.clickCommand("/mq e edit req ALLOW_DESCENDANT " + !allow));
			p1.add(I18n.locMsg("QuestEditor." + allow)).changeLine();
		}

		// Page 2
		// Quest Req
		p2.add(I18n.locMsg("QuestEditor.QuestReq")).changeLine();
		int counter = 0;
		for (String s : (List<String>) q.getRequirements().get(RequirementType.QUEST)) {
			if (QuestUtil.getQuest(s) == null)
				continue;
			Quest quest = QuestUtil.getQuest(s);
			p2.add("&0&l- " + quest.getQuestName() + "&0(" + s + ")");
			p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove req QUEST " + counter)).changeLine();
			counter++;
		}
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew req QUEST")).changeLine();

		counter = 0;
		// Permission Req
		p2.add(I18n.locMsg("QuestEditor.PermissionReq")).changeLine();
		for (String s : (List<String>) q.getRequirements().get(RequirementType.PERMISSION)) {
			p2.add("&0&l- " + s);
			p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove req PERMISSION " + counter)).changeLine();
			counter++;
		}
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Add")).clickCommand("/mq e addnew req PERMISSION")).changeLine();

		// Friend Points Req
		p2.add(I18n.locMsg("QuestEditor.FriendPointReq"));
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit req FRIEND_POINT")).changeLine();
		for (Integer id : ((HashMap<Integer, Integer>) q.getRequirements().get(RequirementType.FRIEND_POINT)).keySet()) {
			if (!QuestValidater.validateNPC(Integer.toString(id)))
				continue;
			NPC npc = Main.getHooker().getNPC(id);
			p2.add("- ");
			p2.add(new InteractiveText("").showNPCInfo(npc));
			p2.add("&0 " + ((HashMap<Integer, Integer>) q.getRequirements().get(RequirementType.FRIEND_POINT)).get(npc.getId()) + " " + I18n
					.locMsg("QuestEditor.Point"));
			p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Remove")).clickCommand("/mq e remove req FRIEND_POINT " + npc.getId()));
			p2.changeLine();
		}

		p2.changeLine();
		p2.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e gui")).changeLine();
		QuestBookGUIManager.openBook(p, p1, p2);
	}

	public static void selectTriggerType(Player p, String mode) {
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.SelectTriggerType")).changeLine();
		for (TriggerType t : TriggerType.values()) {
			p1.add(new InteractiveText("- [" + t.toCustomString() + "]").clickCommand("/mq e " + mode + " evt " + t.toString()));
			p1.changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e gui")).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void selectTriggerStage(Player p, String mode, TriggerType t) {
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.SelectTriggerStage")).changeLine();
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		for (int s = 1; s <= q.getStages().size(); s++) {
			p1.add(new InteractiveText("- " + I18n.locMsg("QuestEditor.Stage", Integer.toString(s)))
					.clickCommand("/mq e " + mode + " evt " + t.toString() + " " + s));
			p1.changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e " + mode + " evt")).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void selectTriggerObjType(Player p, TriggerType t, int stage, int index) {
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.SelectTriggerObj")).changeLine();
		for (TriggerObjectType otype : TriggerObjectType.values()) {
			p1.add(new InteractiveText("- [" + otype.toCustomString() + "]")
					.clickCommand("/mq e addnew evt " + t.toString() + " " + stage + " " + index + " " + otype.toString()));
			p1.changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit evt " + t.toString())).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void selectObjectType(Player p, int stage, int obj) {
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.SelectObjectType")).changeLine();
		for (String s : SimpleQuestObject.ALL_OBJECTS.keySet()) {
			p1.add(new InteractiveText("- [" + SimpleQuestObject.ALL_OBJECTS.get(s) + "]")
					.clickCommand("/mq e edit object " + stage + " " + obj + " type " + s));
			p1.changeLine();
		}
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e edit object " + stage + " " + obj)).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void selectQuest(Player p, String cmd) {
		FlexibleBook book = new FlexibleBook();
		book.add(I18n.locMsg("QuestEditor.ChooseTargetQuest")).changeLine();
		for (Quest q : QuestStorage.localQuests.values()) {
			book.add(new InteractiveText("&0- &l" + q.getQuestName() + "&0(" + q.getInternalID() + ")").clickCommand(cmd + " " + q.getInternalID()));
			book.changeLine();
		}
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void createQuest(Player p) {
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.NewQuestTitle")).changeLine();
		p1.add(I18n.locMsg("QuestEditor.NewQuestDesc")).changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.NewQuestButton")).clickCommand("/mq e newquest create"));
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		p1.changeLine();
		String id = (q.getInternalID() != null) ? q.getInternalID() : I18n.locMsg("QuestEditor.NotSet");
		p1.add(I18n.locMsg("QuestEditor.NewQuestID"));
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e newquest id")).changeLine();
		p1.add(id).changeLine();
		String name = (q.getQuestName() != null) ? q.getQuestName() : I18n.locMsg("QuestEditor.NotSet");
		p1.add(I18n.locMsg("QuestEditor.NewQuestName"));
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e newquest name")).changeLine();
		p1.add(name).changeLine();
		p1.changeLine();
		p1.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq e")).changeLine();
		QuestBookGUIManager.openBook(p, p1);
	}

	public static void generateEditItemGUI(Player p, String type, List<ItemStack> list) {
		if (checkEditorMode(p, false)) {
			Quest q = QuestEditorManager.getCurrentEditingQuest(p);
			Inventory inv = Bukkit.createInventory(null, 27, "[" + type + "]" + q.getQuestName());
			inv.addItem(list.toArray(new ItemStack[0]));
			p.openInventory(inv);
		}
	}
}
