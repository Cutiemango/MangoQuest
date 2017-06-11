package me.Cutiemango.MangoQuest.conversation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.model.QuestBookPage;

public class ConversationProgress
{

	public ConversationProgress(Player p, QuestConversation qc)
	{
		QuestStorage.ConvProgresses.put(p.getName(), this);
		owner = p;
		conv = qc;
		actQueue = new LinkedList<>(conv.getActions());
	}

	public static final List<EnumAction> STOP_ACTIONS = Arrays.asList(EnumAction.BUTTON, EnumAction.WAIT, EnumAction.CHOICE, EnumAction.FINISH);

	private Player owner;
	private QuestConversation conv;
	private LinkedList<QuestBaseAction> actQueue;
	private LinkedList<QuestBookPage> currentBook = new LinkedList<>();
	private LinkedList<QuestBookPage> history = new LinkedList<>();
	private boolean isFinished;

	private int page = 0;

	public void nextAction()
	{
		if (actQueue.size() == 0)
		{
			finish(true);
			return;
		}
		actQueue.getFirst().execute(this);
		QuestConversationManager.openConversation(owner, this);
		if (!(STOP_ACTIONS.contains(actQueue.getFirst().getActionType())))
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					update();
					nextAction();
					return;
				}
			}.runTaskLater(Main.instance, 15L);
		}
		actQueue.removeFirst();
	}

	public void finish(boolean questFinish)
	{
		getCurrentPage().changeLine();
		getCurrentPage().add(Questi18n.localizeMessage("Conversation.Finished")).changeLine();
		QuestConversationManager.openConversation(owner, this);
		if (conv.hasNPC() && questFinish)
		{
			QuestUtil.getData(owner).addFinishConversation(conv);
			isFinished = true;
			if (!conv.isFriendConv())
				owner.performCommand("mq conv npc " + conv.getNPC().getId());
		}
	}

	public Player getOwner()
	{
		return owner;
	}

	public LinkedList<QuestBaseAction> getActionQueue()
	{
		return actQueue;
	}

	public QuestConversation getConvseration()
	{
		return conv;
	}

	public LinkedList<QuestBookPage> getCurrentBook()
	{
		return currentBook;
	}


	public QuestBookPage getCurrentPage()
	{
		if (currentBook.get(page) == null)
			currentBook.add(page, QuestConversationManager.generateNewPage(conv));
		return currentBook.get(page);
	}

	public void setCurrentBook(LinkedList<QuestBookPage> list)
	{
		currentBook = list;
		update();
	}

	public void retrieve()
	{
		currentBook.set(0, history.getFirst().duplicate());
	}

	public void update()
	{
		for (int i = 0; i <= page; i++)
		{
			if (page > history.size() - 1)
				history.add(i, QuestConversationManager.generateNewPage(conv));
			history.set(i, currentBook.get(i).duplicate());
		}
	}

	public void newPage()
	{
		currentBook.push(QuestConversationManager.generateNewPage(conv));
		update();
	}

	public boolean isFinished()
	{
		return isFinished;
	}
}
