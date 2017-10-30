package me.Cutiemango.MangoQuest.manager;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.FlexiableBook;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.book.TextComponentFactory;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestChoice.Choice;
import me.Cutiemango.MangoQuest.data.QuestFinishData;
import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.RewardChoice;
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestGUIManager
{

	public static void openGUI(Player p, QuestProgress q)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(I18n.locMsg("QuestEditor.QuestName", q.getQuest().getQuestName())).changeLine();

		// NPC
		if (!q.getQuest().isCommandQuest())
		{
			NPC npc = q.getQuest().getQuestNPC();
			p1.add(I18n.locMsg("QuestEditor.QuestNPC")).add(new InteractiveText("").showNPCInfo(npc)).changeLine();
			p1.changeLine();
		}

		// Objects
		p1.add(I18n.locMsg("QuestEditor.QuestObjects")).changeLine();
		;
		for (int i = 0; i < q.getQuest().getStages().size(); i++)
		{
			if (q.getCurrentStage() > i)
			{
				for (SimpleQuestObject obj : q.getQuest().getStage(i).getObjects())
				{
					p1.add(obj.toTextComponent(true)).changeLine();
				}
			}
			else
				if (q.getCurrentStage() == i)
				{
					for (int k = 0; k < q.getCurrentObjects().size(); k++)
					{
						SimpleQuestObject obj = q.getQuest().getStage(i).getObjects().get(k);
						QuestObjectProgress ob = q.getCurrentObjects().get(k);
						if (ob.getObject().equals(obj) && ob.isFinished())
							p1.add(obj.toTextComponent(true)).changeLine();
						else
						{
							p1.add(obj.toTextComponent(false));
							if (obj instanceof NumerableObject)
								p1.add(" &8(" + ob.getProgress() + "/" + ((NumerableObject) obj).getAmount() + ")");
							p1.changeLine();
						}
					}
				}
				else
				{
					for (int j = 0; j < q.getQuest().getStage(i).getObjects().size(); j++)
					{
						p1.add("&8&l？？？").changeLine();
					}
				}

			// OutLine
			QuestBookPage p2 = new QuestBookPage();
			p2.add(I18n.locMsg("QuestEditor.Outline")).changeLine();
			for (String out : q.getQuest().getQuestOutline())
			{
				p2.add(out).changeLine();
			}

			// Reward
			QuestBookPage p3 = new QuestBookPage();
			p3.add(I18n.locMsg("QuestEditor.Reward")).changeLine();

			if (q.getQuest().getQuestReward().hasItem())
			{
				if (q.getQuest().getQuestReward().hasMultipleChoices())
				{
					p3.add(I18n.locMsg("QuestReward.SelectReward", Integer.toString(q.getQuest().getQuestReward().getChoiceAmount()), Integer.toString(q.getQuest().getQuestReward().getRewardAmount()))).changeLine();
					for (RewardChoice choice : q.getQuest().getQuestReward().getChoices())
					{
						p3.add("- ").endNormally();
						for (ItemStack item : choice.getItems())
						{
							p3.add(new InteractiveText("").showItem(item)).endNormally();
							p3.add(", ").endNormally();
						}
						p3.changeLine();
					}
				}
				else
				{
					for (ItemStack is : q.getQuest().getQuestReward().getDefaultChoice().getItems())
					{
						if (is != null)
						{
							p3.add(new InteractiveText("").showItem(is)).endNormally();
							p3.add(" ").endNormally();
							p3.add(I18n.locMsg("QuestEditor.RewardAmount", Integer.toString(is.getAmount()))).changeLine();
						}
					}
				}
			}

			if (q.getQuest().getQuestReward().hasMoney())
				p3.add(I18n.locMsg("QuestEditor.RewardMoney", Double.toString(QuestUtil.cut(q.getQuest().getQuestReward().getMoney())))).changeLine();

			if (q.getQuest().getQuestReward().hasExp())
				p3.add(I18n.locMsg("QuestEditor.RewardExp", Integer.toString(q.getQuest().getQuestReward().getExp()))).changeLine();

			// Removed due to prevention of awkward situation
//			if (q.getQuest().getQuestReward().hasFriendPoint())
//			{
//				for (Integer id : q.getQuest().getQuestReward().getFp().keySet())
//				{
//					NPC npc = CitizensAPI.getNPCRegistry().getById(id);
//					p3.add(new InteractiveText("").showNPCInfo(npc)).endNormally();
//					p3.add(" &c將會感激你").changeLine();
//				}
//			}

			openBook(p, p1, p2, p3);
		}
	}

	public static void openBook(Player p, QuestBookPage... qp)
	{
		List<TextComponent> list = new ArrayList<>();
		for (QuestBookPage page : qp)
		{
			list.add(page.getOriginalPage());
		}
		openBook(p, list.toArray(new TextComponent[list.size()]));
	}

	public static void openChoice(Player p, TextComponent q, List<Choice> c)
	{
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		page.add(I18n.locMsg("Conversation.ChoiceTitle")).changeLine();
		page.add(q).changeLine();
		for (int i = 0; i < c.size(); i++)
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add(new InteractiveText("- " + c.get(i).getContent()).clickCommand("/mq conv choose " + i)).changeLine();
		}
		openBook(p, book.toSendableBook());
	}

	public static void openJourney(Player p)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		
		// Page 1
		page.add(I18n.locMsg("QuestJourney.QuestProgress")).changeLine();
		for (QuestProgress qp : qd.getProgresses())
		{
			if (!qp.getQuest().getSettings().displayOnProgress())
				continue;
			page.changeLine();
			page.add(new InteractiveText("").showQuest(qp.getQuest())).endNormally();
			page.add("：").endNormally();
			if (qp.getQuest().isQuitable())
				page.add(new InteractiveText(I18n.locMsg("QuestJourney.QuitButton")).clickCommand("/mq quest quit " + qp.getQuest().getInternalID())).changeLine();
			if (qp.getQuest().isTimeLimited())
			{
				long timeleft = (qp.getTakeTime() + qp.getQuest().getTimeLimit()) - System.currentTimeMillis();
				page.add(new InteractiveText(I18n.locMsg("QuestJourney.TimeLeft", QuestUtil.convertTime(timeleft)))).changeLine();
			}
				for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				page.add("- ").endNormally();
				if (qop.isFinished())
					page.add(qop.getObject().toTextComponent(true)).changeLine();
				else
				{
					page.add(qop.getObject().toTextComponent(false));
					if (qop.getObject() instanceof NumerableObject)
						page.add(" &8(" + qop.getProgress() + "/" + ((NumerableObject) qop.getObject()).getAmount() + ")");
					page.changeLine();
				}
			}
		}
		
		book.newPage();
		page = book.getLastEditingPage();
		// Page 2
		page.add(I18n.locMsg("QuestJourney.QuestToTake")).changeLine();

		for (Quest q : QuestStorage.Quests.values())
		{
			if (!q.getSettings().displayOnTake())
				continue;
			if (!qd.canTake(q, false))
				continue;
			else
			{
				QuestUtil.checkOutOfBounds(page, book);
				page = book.getLastEditingPage();
				page.add("- ");
				page.add(new InteractiveText("").showQuest(q));
				if (q.isCommandQuest())
					page.add(new InteractiveText(I18n.locMsg("QuestJourney.TakeButton")).clickCommand("/mq quest take " + q.getInternalID()));
				page.changeLine();
			}
		}
		
		
		book.newPage();
		page = book.getLastEditingPage();
		page.add(I18n.locMsg("QuestJourney.QuestFinished")).changeLine();
		
		for (QuestFinishData qfd : qd.getFinishQuests())
		{
			if (!qfd.getQuest().getSettings().displayOnFinish())
				continue;
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add("- ").endNormally();
			page.add(new InteractiveText("").showQuest(qfd.getQuest())).endNormally();
			page.add("：").endNormally();
			page.add(I18n.locMsg("QuestJourney.FinishedTimes", Integer.toString(qfd.getFinishedTimes()))).changeLine();
		}

		QuestGUIManager.openBook(p, book.toSendableBook());
	}

	public static void openInfo(Player p, String msg)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(msg).changeLine();
		p1.add(I18n.locMsg("EditorMessage.EnterCancel")).changeLine();
		openBook(p, p1);
	}

	public static void openBook(Player p, TextComponent... texts)
	{
		Main.instance.handler.openBook(p, texts);
	}
	
	public static void openQuitGUI(Player p, Quest q)
	{
		QuestBookPage page = new QuestBookPage();
		page.add(I18n.locMsg("QuestQuitMsg.Title")).changeLine();
		page.add(I18n.locMsg("QuestQuitMsg.WarnAccept", q.getQuestName())).changeLine();
		page.add(I18n.locMsg("QuestQuitMsg.WarnAccept2")).changeLine();
		page.changeLine();
		page.add(new InteractiveText(q.getQuitAcceptMsg()).clickCommand("/mq q cquit " + q.getInternalID())).changeLine();
		page.changeLine();
		page.add(new InteractiveText(q.getQuitCancelMsg()).clickCommand("/mq q list")).changeLine();
		
		openBook(p, page);
	}

	public static void openNPCInfo(Player p, NPC npc, boolean trade)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();
		List<Quest> holder = new ArrayList<>();

		// Message
		page.add("&0&l" + npc.getName() + "&0：「").add(QuestUtil.getNPCMessage(npc.getId(), qd.getNPCfp(npc.getId()))).add("」").changeLine();
		page.changeLine();

		// Interaction List
		page.add(I18n.locMsg("QuestGUI.InteractionList")).changeLine();
		if (trade)
			page.add(new InteractiveText(I18n.locMsg("QuestGUI.Trade")).clickCommand("/mq quest trade " + npc.getId())).changeLine();
		for (QuestProgress q : qd.getNPCtoTalkWith(npc))
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add(I18n.locMsg("QuestGUI.QuestReturnSymbol")).endNormally();
			page.add(TextComponentFactory.convertViewQuest(q.getQuest())).endNormally();
			page.add(new InteractiveText(I18n.locMsg("QuestGUI.Conversation")).clickCommand("/mq conv npc " + npc.getId()).showText(I18n.locMsg("QuestGUI.Hover.ClickToChat"))).endNormally();
			if (q.getQuest().isQuitable())
				if (qd.isCurrentlyDoing(q.getQuest()) && !q.getQuest().isCommandQuest() && q.getQuest().getQuestNPC().equals(npc))
				{
					page.add(new InteractiveText(I18n.locMsg("QuestJourney.QuitButton")).clickCommand("/mq quest quit " + q.getQuest().getInternalID())
							.showText(I18n.locMsg("QuestGUI.Hover.QuitWarning", q.getQuest().getQuestName()))).endNormally();
					holder.add(q.getQuest());
				}
			page.changeLine();
		}
		for (Quest q : QuestUtil.getGivenNPCQuests(npc))
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			if (qd.hasFinished(q))
			{
				if (!qd.getFinishData(q).isRewardTaken())
				{
					page.add(I18n.locMsg("QuestGUI.NewQuestSymbol")).endNormally();
					page.add(new InteractiveText("").showQuest(q)).endNormally();
					page.add(new InteractiveText(I18n.locMsg("QuestJourney.RewardButton")).clickCommand("/mq q reward select " + q.getInternalID()).showText(I18n.locMsg("QuestGUI.Hover.ClaimReward"))).endNormally();
					page.changeLine();
				}
			}
			if (!q.isRedoable() && qd.hasFinished(q))
				continue;
			if (qd.canTake(q, false))
			{
				if (qd.hasFinished(q))
					page.add(I18n.locMsg("QuestGUI.RedoableQuestSymbol")).endNormally();
				else
					page.add(I18n.locMsg("QuestGUI.NewQuestSymbol")).endNormally();
				page.add(new InteractiveText("").showQuest(q)).endNormally();
				page.add(new InteractiveText(I18n.locMsg("QuestJourney.TakeButton")).clickCommand("/mq quest take " + q.getInternalID()).showText(I18n.locMsg("QuestGUI.Hover.TakeQuest", q.getQuestName()))).endNormally();
				page.changeLine();
				continue;
			}
			else
				if (qd.isCurrentlyDoing(q))
				{
					if (holder.contains(q))
						continue;
					page.add(I18n.locMsg("QuestGUI.QuestDoingSymbol"));
					page.add(new InteractiveText("").showQuest(q)).endNormally();
					if (q.isQuitable())
						page.add(new InteractiveText(I18n.locMsg("QuestJourney.QuitButton")).clickCommand("/mq quest quit " + q.getInternalID())
							.showText(I18n.locMsg("QuestGUI.Hover.QuitWarning", q.getQuestName()))).endNormally();
					page.changeLine();
					continue;
				}
				else
				{
					page.add("&0- ").endNormally();
					page.add(new InteractiveText("").showRequirement(qd, q));
					page.changeLine();
					continue;
				}
		}
		
		for (FriendConversation qc : QuestUtil.getConversations(npc, qd.getNPCfp(npc.getId())))
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			if (qd.hasFinished(qc))
				page.add(new InteractiveText(I18n.locMsg("QuestGUI.OldFriendConversation", qc.getName())).clickCommand("/mq conv opennew " + qc.getInternalID()));
			else
				page.add(new InteractiveText(I18n.locMsg("QuestGUI.NewFriendConversation", qc.getName())).clickCommand("/mq conv opennew " + qc.getInternalID()));
			page.changeLine();
		}
		openBook(p, book.toSendableBook());
	}
}
