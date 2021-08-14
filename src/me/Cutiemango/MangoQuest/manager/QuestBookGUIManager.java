package me.Cutiemango.MangoQuest.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.FlexibleBook;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.ItemSafeInteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestChoice.Choice;
import me.Cutiemango.MangoQuest.data.QuestFinishData;
import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.GUIOption;
import me.Cutiemango.MangoQuest.objects.QuestStage;
import me.Cutiemango.MangoQuest.objects.reward.RewardChoice;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestBookGUIManager
{

	public static void openGUIWithProgress(Player p, QuestProgress qp) {
		FlexibleBook book = new FlexibleBook();
		book.add(I18n.locMsg("QuestEditor.QuestName", qp.getQuest().getQuestName()));
		book.changeLine();

		// NPC
		if (!qp.getQuest().isCommandQuest()) {
			NPC npc = qp.getQuest().getQuestNPC();
			book.add(I18n.locMsg("QuestEditor.QuestNPC"));
			book.add(new InteractiveText("").showNPCInfo(npc)).changeLine();
			book.changeLine();
		}

		// Objects
		book.add(I18n.locMsg("QuestEditor.QuestObjects")).changeLine();
		for (int i = 0; i < qp.getQuest().getStages().size(); i++) {
			QuestStage stage = qp.getQuest().getStage(i);
			for (int k = 0; k < stage.getObjects().size(); k++) {
				SimpleQuestObject obj = stage.getObjects().get(k);

				if (qp.getCurrentStage() == i && !qp.getCurrentObjects().get(k).isFinished()) {
					QuestObjectProgress qop = qp.getCurrentObjects().get(k);
					book.add(obj.toTextComponent(false));
					if (obj instanceof NumerableObject)
						book.add(" &8(" + qop.getProgress() + "/" + ((NumerableObject) obj).getAmount() + ")");
					book.changeLine();
				} else if (qp.getCurrentStage() < i)
					for (int j = 0; j < qp.getQuest().getStage(i).getObjects().size(); j++)
						book.add(I18n.locMsg("QuestJourney.NotRevealed")).changeLine();
				else
					book.add(obj.toTextComponent(true)).changeLine();
			}
		}
		if (QuestUtil.getData(p).canTake(qp.getQuest(), false))
			book.add(new InteractiveText(I18n.locMsg("QuestJourney.TakeButton"))
					.showText(I18n.locMsg("QuestGUI.Hover.TakeQuest", qp.getQuest().getQuestName()))
					.clickCommand("/mq q take " + qp.getQuest().getInternalID()));
		book.changeLine();

		// OutLine
		book.createNewPage();
		book.add(I18n.locMsg("QuestEditor.Outline")).changeLine();
		for (String out : qp.getQuest().getQuestOutline())
			book.add(out).changeLine();

		// Reward
		book.createNewPage();
		book.add(I18n.locMsg("QuestEditor.Reward")).changeLine();

		if (qp.getQuest().getQuestReward().hasItem()) {
			if (qp.getQuest().getQuestReward().hasMultipleChoices()) {
				book.add(I18n.locMsg("QuestReward.SelectReward", Integer.toString(qp.getQuest().getQuestReward().getChoiceAmount()),
						Integer.toString(qp.getQuest().getQuestReward().getRewardAmount()))).changeLine();
				for (RewardChoice choice : qp.getQuest().getQuestReward().getChoices()) {
					book.add("- ");
					List<ItemStack> items = choice.getItems();
					for (int j = 0; j < items.size(); j++)
						book.add(new ItemSafeInteractiveText(items.get(j)).get()).add(j == items.size() - 1 ? "" : ", ");
					book.changeLine();
				}
			} else {
				for (ItemStack is : qp.getQuest().getQuestReward().getDefaultChoice().getItems()) {
					if (is != null) {
						book.add(new ItemSafeInteractiveText(is).get());
						book.add(" ");
						book.add(I18n.locMsg("QuestReward.RewardAmount", Integer.toString(is.getAmount()))).changeLine();
					}
				}
			}
		}

		if (qp.getQuest().getQuestReward().hasMoney())
			book.add(I18n.locMsg("QuestEditor.RewardMoney", Double.toString(QuestUtil.cut(qp.getQuest().getQuestReward().getMoney())))).changeLine();

		if (qp.getQuest().getQuestReward().hasExp())
			book.add(I18n.locMsg("QuestEditor.RewardExp", Integer.toString(qp.getQuest().getQuestReward().getExp()))).changeLine();
		openBook(p, book.toSendableBook());
	}

	public static void openBook(Player p, QuestBookPage... qp) {
		List<TextComponent> list = new ArrayList<>();
		for (QuestBookPage page : qp)
			list.add(page.getOriginalPage());

		openBook(p, list.toArray(new TextComponent[0]));
	}

	public static void openChoice(Player p, TextComponent q, List<Choice> c) {
		FlexibleBook book = new FlexibleBook();
		book.add(I18n.locMsg("Conversation.ChoiceTitle")).changeLine();
		book.add(q).changeLine();
		for (int i = 0; i < c.size(); i++) {
			if (!QuestUtil.getData(p).meetFriendPointReq(c.get(i)))
				continue;
			book.add(new InteractiveText((i + 1) + ". " + c.get(i).getContent()).clickCommand("/mq conv choose " + i)).changeLine();
		}
		openBook(p, book.toSendableBook());
	}

	public static void openJourneyMenu(Player p) {
		FlexibleBook book = new FlexibleBook();
		book.add(new InteractiveText(I18n.locMsg("QuestJourney.QuestList")));
		book.changeLine();
		book.changeLine();
		book.changeLine();
		book.add(new InteractiveText("     " + I18n.locMsg("QuestJourney.QuestProgress")).clickCommand("/mq quest list progress"));
		book.changeLine();
		book.changeLine();
		book.changeLine();
		book.changeLine();
		book.add(new InteractiveText("     " + I18n.locMsg("QuestJourney.QuestToTake")).clickCommand("/mq quest list doable"));
		book.changeLine();
		book.changeLine();
		book.changeLine();
		book.changeLine();
		book.add(new InteractiveText("     " + I18n.locMsg("QuestJourney.QuestFinished")).clickCommand("/mq quest list finished"));
		book.changeLine();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void openProgressJourney(Player p) {
		QuestPlayerData qd = QuestUtil.getData(p);
		FlexibleBook book = new FlexibleBook();
		book.add(I18n.locMsg("QuestJourney.QuestProgress")).changeLine();
		for (QuestProgress qp : qd.getProgresses()) {
			if (!qp.getQuest().getSettings().displayOnProgress())
				continue;
			book.changeLine();
			if (qp.getQuest().isQuitable())
				book.add(new InteractiveText(I18n.locMsg("QuestJourney.QuitButton")).clickCommand("/mq quest quit " + qp.getQuest().getInternalID()));
			book.add(new InteractiveText("").showQuest(qp.getQuest()));
			book.add(":");
			book.changeLine();
			if (qp.getQuest().isTimeLimited()) {
				long timeleft = (qp.getTakeTime() + qp.getQuest().getTimeLimit()) - System.currentTimeMillis();
				book.add(new InteractiveText(I18n.locMsg("QuestJourney.TimeLeft", TimeHandler.convertTime(timeleft)))).changeLine();
			}
			for (QuestObjectProgress qop : qp.getCurrentObjects()) {
				book.add("- ");
				if (qop.isFinished())
					book.add(qop.getObject().toTextComponent(true)).changeLine();
				else {
					book.add(qop.getObject().toTextComponent(false));
					if (qop.getObject() instanceof NumerableObject)
						book.add(" &8(" + qop.getProgress() + "/" + ((NumerableObject) qop.getObject()).getAmount() + ")");
					book.changeLine();
				}
			}
		}
		book.changeLine();
		book.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq q list")).changeLine();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void openDoableJourney(Player p) {
		QuestPlayerData qd = QuestUtil.getData(p);
		FlexibleBook book = new FlexibleBook();
		book.add(I18n.locMsg("QuestJourney.QuestToTake")).changeLine();
		for (Quest q : QuestStorage.localQuests.values()) {
			if (q.getSettings().displayOnTake() && qd.canTake(q, false)) {
				book.add("- ");
				book.add(new InteractiveText("").showQuest(q));
				if (q.isCommandQuest())
					book.add(new InteractiveText(I18n.locMsg("QuestJourney.TakeButton")).clickCommand("/mq quest take " + q.getInternalID()));
				book.changeLine();
			}
		}
		book.changeLine();
		book.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq q list")).changeLine();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void openFinishedJourney(Player p) {
		QuestPlayerData qd = QuestUtil.getData(p);
		FlexibleBook book = new FlexibleBook();
		book.add(I18n.locMsg("QuestJourney.QuestFinished")).changeLine();
		for (QuestFinishData qfd : qd.getFinishQuests()) {
			if (!qfd.getQuest().getSettings().displayOnFinish())
				continue;
			if (!qfd.isRewardTaken() && qfd.getQuest().isCommandQuest()) {
				book.add(I18n.locMsg("QuestGUI.NewQuestSymbol"));
				book.add(new InteractiveText("").showQuest(qfd.getQuest()));
				book.add(new InteractiveText(I18n.locMsg("QuestJourney.RewardButton"))
						.clickCommand("/mq q reward select " + qfd.getQuest().getInternalID()).showText(I18n.locMsg("QuestGUI.Hover.ClaimReward")));
				book.changeLine();
			} else {
				book.add("- ");
				book.add(new InteractiveText("").showQuest(qfd.getQuest()));
				book.add(": ");
				book.add(I18n.locMsg("QuestJourney.FinishedTimes", Integer.toString(qfd.getFinishedTimes()))).changeLine();
			}
		}
		book.changeLine();
		book.add(new InteractiveText(I18n.locMsg("QuestEditor.Return")).clickCommand("/mq q list")).changeLine();
		QuestBookGUIManager.openBook(p, book.toSendableBook());
	}

	public static void openInfo(Player p, String msg) {
		QuestBookPage p1 = new QuestBookPage();
		p1.add(msg).changeLine();
		p1.add(I18n.locMsg("EditorMessage.EnterCancel")).changeLine();
		openBook(p, p1);
	}

	public static void openBook(Player p, TextComponent... texts) {
		Main.getInstance().handler.openBook(p, texts);
	}

	public static void openQuitGUI(Player p, Quest q) {
		QuestBookPage page = new QuestBookPage();
		page.add(I18n.locMsg("QuestQuitMsg.Title")).changeLine();
		page.add(I18n.locMsg("QuestQuitMsg.WarnAccept", q.getQuestName())).changeLine();
		page.add(I18n.locMsg("QuestQuitMsg.WarnAccept2")).changeLine();
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestQuitMsg.QuitQuest") + q.getQuitAcceptMsg()).clickCommand("/mq q cquit " + q.getInternalID()))
				.changeLine();
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestQuitMsg.Cancel") + q.getQuitCancelMsg()).clickCommand("/mq q list")).changeLine();

		openBook(p, page);
	}

	public static void openNPCInfo(Player p, NPC npc, boolean trade) {
		QuestPlayerData qd = QuestUtil.getData(p);
		FlexibleBook book = new FlexibleBook();
		List<String> holder = new ArrayList<>();

		// Message
		book.add(I18n.locMsg("QuestJourney.NPCFriendMessage", npc.getName(), QuestNPCManager.getNPCMessage(npc.getId(), qd.getNPCfp(npc.getId()))))
				.changeLine();
		book.changeLine();

		// Interaction List
		book.add(I18n.locMsg("QuestGUI.InteractionList")).changeLine();

		// Show GUIOptions
		for (GUIOption option : QuestNPCManager.getNPCData(npc.getId()).getOptions()) {
			if (!option.meetRequirementWith(p))
				continue;
			book.add(option.toInteractiveText(npc)).changeLine();
		}

		// Trade Options
		if (trade)
			book.add(new InteractiveText(I18n.locMsg("QuestGUI.Trade")).clickCommand("/mq quest trade " + npc.getId())).changeLine();

		for (QuestProgress q : qd.getNPCtoTalkWith(npc)) {
			book.add(I18n.locMsg("QuestGUI.QuestReturnSymbol"));
			book.add(new InteractiveText("").showQuest(q.getQuest()));
			book.add(new InteractiveText(I18n.locMsg("QuestGUI.Conversation")).clickCommand("/mq conv npc " + npc.getId())
					.showText(I18n.locMsg("QuestGUI.Hover.ClickToChat")));
			if (q.getQuest().isQuitable())
				if (qd.isCurrentlyDoing(q.getQuest()) && !q.getQuest().isCommandQuest() && q.getQuest().getQuestNPC().equals(npc)) {
					book.add(
							new InteractiveText(I18n.locMsg("QuestJourney.QuitButton")).clickCommand("/mq quest quit " + q.getQuest().getInternalID())
									.showText(I18n.locMsg("QuestGUI.Hover.QuitWarning", q.getQuest().getQuestName())));
					holder.add(q.getQuest().getInternalID());
				}
			book.changeLine();
		}

		if (QuestNPCManager.hasData(npc.getId())) {
			for (Quest q : QuestNPCManager.getNPCData(npc.getId()).getRewardQuests()) {
				if (qd.hasFinished(q) && q.getQuestReward().hasMultipleChoices() && !qd.getFinishData(q).isRewardTaken() && q.getQuestReward()
						.getRewardNPC().getId() == npc.getId()) {
					book.add(I18n.locMsg("QuestGUI.NewQuestSymbol"));
					book.add(new InteractiveText("").showQuest(q));
					book.add(new InteractiveText(I18n.locMsg("QuestJourney.RewardButton")).clickCommand("/mq q reward select " + q.getInternalID())
							.showText(I18n.locMsg("QuestGUI.Hover.ClaimReward"))).changeLine();
				}
			}
			for (Quest q : QuestNPCManager.getNPCData(npc.getId()).getGivenQuests()) {
				if (holder.contains(q.getInternalID()) || (!q.isRedoable() && qd.hasFinished(q)))
					continue;
				if (qd.canTake(q, false)) {
					if (qd.hasFinished(q))
						book.add(I18n.locMsg("QuestGUI.RedoableQuestSymbol"));
					else
						book.add(I18n.locMsg("QuestGUI.NewQuestSymbol"));
					book.add(new InteractiveText("").showQuest(q));
					book.add(new InteractiveText(I18n.locMsg("QuestJourney.TakeButton")).clickCommand("/mq quest take " + q.getInternalID())
							.showText(I18n.locMsg("QuestGUI.Hover.TakeQuest", q.getQuestName())));
					book.changeLine();
				} else if (qd.isCurrentlyDoing(q)) {
					book.add(I18n.locMsg("QuestGUI.QuestDoingSymbol"));
					book.add(new InteractiveText("").showQuest(q));
					if (q.isQuitable())
						book.add(new InteractiveText(I18n.locMsg("QuestJourney.QuitButton")).clickCommand("/mq quest quit " + q.getInternalID())
								.showText(I18n.locMsg("QuestGUI.Hover.QuitWarning", q.getQuestName())));
					book.changeLine();
				} else {
					book.add("&0- ");
					book.add(new InteractiveText("").showRequirement(qd, q));
					book.changeLine();
				}
			}
		}

		for (FriendConversation qc : QuestUtil.getConversations(npc, qd.getNPCfp(npc.getId()))) {
			if (qd.hasFinished(qc))
				book.add(new InteractiveText(I18n.locMsg("QuestGUI.OldFriendConversation", qc.getName()))
						.clickCommand("/mq conv opennew " + qc.getInternalID()));
			else
				book.add(new InteractiveText(I18n.locMsg("QuestGUI.NewFriendConversation", qc.getName()))
						.clickCommand("/mq conv opennew " + qc.getInternalID()));
			book.changeLine();
		}
		openBook(p, book.toSendableBook());
	}
}
