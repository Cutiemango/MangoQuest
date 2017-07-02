package me.Cutiemango.MangoQuest.manager;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestGUIManager
{

	public static void openGUI(Player p, QuestProgress q)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&l任務名稱： ").add(q.getQuest().getQuestName()).changeLine();

		// NPC
		if (!q.getQuest().isCommandQuest())
		{
			NPC npc = q.getQuest().getQuestNPC();
			p1.add("&l任務NPC： ").add(new InteractiveText("").showNPCInfo(npc)).changeLine();
			p1.changeLine();
		}

		// Objects
		p1.add("&l任務內容：").changeLine();
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
			p2.add("&l任務提要：").changeLine();
			for (String out : q.getQuest().getQuestOutline())
			{
				p2.add(out).changeLine();
			}

			// Reward
			QuestBookPage p3 = new QuestBookPage();
			p3.add("&l任務獎勵：").changeLine();

			if (q.getQuest().getQuestReward().hasItem())
			{
				for (ItemStack is : q.getQuest().getQuestReward().getItems())
				{
					if (is != null)
					{
						p3.add(new InteractiveText("").showItem(is));
						p3.add(" &l" + is.getAmount() + " &0個").changeLine();
					}
				}
			}

			if (q.getQuest().getQuestReward().hasMoney())
				p3.add("&6金錢&0 " + q.getQuest().getQuestReward().getMoney() + " &6元").changeLine();

			if (q.getQuest().getQuestReward().hasExp())
				p3.add("&a經驗值&0 " + q.getQuest().getQuestReward().getExp() + " &a點").changeLine();

			if (q.getQuest().getQuestReward().hasFriendPoint())
			{
				for (Integer id : q.getQuest().getQuestReward().getFp().keySet())
				{
					NPC npc = CitizensAPI.getNPCRegistry().getById(id);
					p3.add(new InteractiveText("").showNPCInfo(npc)).endNormally();
					p3.add(" &c將會感激你").changeLine();
				}
			}

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
		page.add("       &0=》 &c&l選擇 &0《=").changeLine();
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
		page.add("&0&l[進行中的任務]").changeLine();
		for (QuestProgress qp : qd.getProgresses())
		{
			page.changeLine();
			page.add(new InteractiveText("").showQuest(qp.getQuest())).endNormally();
			page.add("：").endNormally();
			page.add(new InteractiveText("&c&l【放棄】").clickCommand("/mq quest quit " + qp.getQuest().getInternalID())).changeLine();
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
		page.add("&0&l[可進行的任務]").changeLine();

		for (Quest q : QuestStorage.Quests.values())
		{
			if (!qd.canTake(q, false))
				continue;
			else
			{
				QuestUtil.checkOutOfBounds(page, book);
				page = book.getLastEditingPage();
				page.add("- ");
				page.add(new InteractiveText("").showQuest(q));
				if (q.isCommandQuest())
					page.add(new InteractiveText("&2&l【接受】").clickCommand("/mq quest take " + q.getInternalID()));
				page.changeLine();
			}
		}
		
		
		book.newPage();
		page = book.getLastEditingPage();
		page.add("&0&l[已完成的任務]").changeLine();
		
		for (QuestFinishData qfd : qd.getFinishQuests())
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add("- ").endNormally();
			page.add(new InteractiveText("").showQuest(qfd.getQuest())).endNormally();
			page.add("： 已完成 " + qfd.getFinishedTimes() + " 次").changeLine();
		}

		QuestGUIManager.openBook(p, book.toSendableBook());
	}

	public static void openInfo(Player p, String msg)
	{
		QuestBookPage p1 = new QuestBookPage();
		p1.add(msg).changeLine();
		p1.add("&7(取消請輸入cancel。)").changeLine();
		openBook(p, p1);
	}

	public static void openBook(Player p, TextComponent... texts)
	{
		Main.instance.handler.openBook(p, texts);
	}

	public static void openNPCInfo(Player p, NPC npc, boolean trade)
	{
		QuestPlayerData qd = QuestUtil.getData(p);
		FlexiableBook book = new FlexiableBook();
		QuestBookPage page = book.getLastEditingPage();

//		// Title
//		p1.add("&5&lNPC &0&l| ");
//		p1.add(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getEntity().getLocation(), false));
//		p1.changeLine();

		List<Quest> holder = new ArrayList<>();

		// Message
		page.add("&0&l" + npc.getName() + "&0：「").add(QuestUtil.getNPCMessage(npc.getId(), qd.getNPCfp(npc.getId()))).add("」").changeLine();
		page.changeLine();

		// Interaction List
		page.add("&0&l[互動列表]").changeLine();
		if (trade)
			page.add(new InteractiveText("&0- &6&l＄&0【交易物品】").clickCommand("/mq quest trade " + npc.getId())).changeLine();
		for (QuestProgress q : qd.getNPCtoTalkWith(npc))
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			page.add("&0- &6&l？ &0").endNormally();
			page.add(TextComponentFactory.convertViewQuest(q.getQuest())).endNormally();
			page.add(new InteractiveText("&9&l【對話】").clickCommand("/mq conv npc " + npc.getId()).showText("&9點擊&f以開始對話")).endNormally();
			if (qd.isCurrentlyDoing(q.getQuest()) && !q.getQuest().isCommandQuest() && q.getQuest().getQuestNPC().equals(npc))
			{
				page.add(new InteractiveText("&c&l【放棄】").clickCommand("/mq quest quit " + q.getQuest().getInternalID())
						.showText("&c放棄任務 &f" + q.getQuest().getQuestName() + "\n&4所有的任務進度都會消失。")).endNormally();
				holder.add(q.getQuest());
			}
			page.changeLine();
		}
		for (Quest q : QuestUtil.getGivenNPCQuests(npc))
		{
			QuestUtil.checkOutOfBounds(page, book);
			page = book.getLastEditingPage();
			if (!q.isRedoable() && qd.hasFinished(q))
				continue;
			if (qd.canTake(q, false))
			{
				if (qd.hasFinished(q))
					page.add("&0- &8&l！ &0").endNormally();
				else
					page.add("&0- &6&l！ &0").endNormally();
				page.add(new InteractiveText("").showQuest(q)).endNormally();
				page.add(new InteractiveText("&2&l【接受】").clickCommand("/mq quest take " + q.getInternalID()).showText("&a接受任務 &f" + q.getQuestName()));
				page.changeLine();
				continue;
			}
			else
				if (qd.isCurrentlyDoing(q))
				{
					if (holder.contains(q))
						continue;
					page.add("&0- &8&l？ &0");
					page.add(new InteractiveText("").showQuest(q)).endNormally();
					page.add(new InteractiveText("&c&l【放棄】").clickCommand("/mq quest quit " + q.getInternalID())
							.showText("&c放棄任務 &f" + q.getQuestName() + "\n&4所有的任務進度都會消失。"));
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
			{
				page.add("&0- &7&o").endNormally();
				page.add(new InteractiveText(qc.getName() + " 〈&c&o♥&7&o〉").clickCommand("/mq conv opennew " + qc.getInternalID()));
			}
			else
			{
				page.add("&0- &6&l！ &0&l").endNormally();
				page.add(new InteractiveText(qc.getName() + " 〈&c♥&0&l〉").clickCommand("/mq conv opennew " + qc.getInternalID()));
			}
			page.changeLine();
		}
		openBook(p, book.toSendableBook());
	}

}
