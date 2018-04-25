package me.Cutiemango.MangoQuest.conversation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.book.FlexiableBook;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;

public class ConversationProgress
{

	public ConversationProgress(Player p, QuestConversation qc)
	{
		QuestStorage.ConvProgresses.put(p.getName(), this);
		owner = p;
		conv = qc;
		actQueue = new LinkedList<>(conv.getActions());
	}

	public static final List<EnumAction> STOP_ACTIONS = Arrays.asList(EnumAction.BUTTON, EnumAction.WAIT, EnumAction.CHOICE, EnumAction.FINISH, EnumAction.TAKE_QUEST, EnumAction.EXIT);
	
	protected Player owner;
	protected QuestConversation conv;
	protected LinkedList<QuestBaseAction> actQueue;
	protected FlexiableBook currentBook = new FlexiableBook();
	protected FlexiableBook history = new FlexiableBook();
	protected boolean isFinished;
	private BukkitTask currentTask;

	private int page = 0;

	public void nextAction()
	{
		if (actQueue.size() == 0)
		{
			finish(true);
			return;
		}
		actQueue.getFirst().execute(this);
		if (actQueue.getFirst().getActionType() == EnumAction.EXIT)
			return;
		ConversationManager.openConversation(owner, this);
		if (!(STOP_ACTIONS.contains(actQueue.getFirst().getActionType())))
		{
			cancelTask();
			registerTask();
		}
		actQueue.removeFirst();
	}
	
	private void registerTask()
	{
		currentTask = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				update();
				nextAction();
				currentTask = null;
				return;
			}
		}.runTaskLater(Main.getInstance(), 25L);
	}
	
	private void cancelTask()
	{
		if (currentTask != null)
		{
			currentTask.cancel();
			currentTask = null;
		}
	}

	public void finish(boolean questFinish)
	{
		getCurrentPage().changeLine();
		getCurrentPage().add(I18n.locMsg("Conversation.Finished")).changeLine();
		ConversationManager.openConversation(owner, this);
		if (conv.hasNPC() && questFinish)
		{
			QuestUtil.getData(owner).addFinishConversation(conv);
			isFinished = true;
			if (!(conv instanceof FriendConversation))
				QuestUtil.executeCommandAsync(owner, "mq conv npc " + conv.getNPC().getId());
			if (conv instanceof StartTriggerConversation)
				QuestUtil.executeCommandAsync(owner, "mq quest take " + ((StartTriggerConversation)conv).getQuest().getInternalID());
		}
		ConversationManager.finishConversation(owner);
	}

	public Player getOwner()
	{
		return owner;
	}

	public LinkedList<QuestBaseAction> getActionQueue()
	{
		return actQueue;
	}

	public QuestConversation getConversation()
	{
		return conv;
	}

	public FlexiableBook getCurrentBook()
	{
		return currentBook;
	}


	public QuestBookPage getCurrentPage()
	{
		if (currentBook.getFirstPage().pageOutOfBounds())
		{
			currentBook.pushNewPage(conv);
			currentBook.getFirstPage().add(currentBook.getPage(1).getTextleft()).endNormally();
		}
		return currentBook.getFirstPage();
	}

	public void setCurrentBook(FlexiableBook book)
	{
		currentBook = book;
		update();
	}
	
	public void retrieve()
	{
		currentBook.setPage(0, history.getFirstPage().duplicate());
	}

	public void update()
	{
		for (int i = 0; i <= page; i++)
		{
			if (page > history.size() - 1)
				history.addPage(i, ConversationManager.generateNewPage(conv));
			history.setPage(i, currentBook.getPage(i).duplicate());
		}
	}

	public void newPage()
	{
		currentBook.pushNewPage(conv);
		currentBook.getFirstPage().add(currentBook.getPage(1).getTextleft()).endNormally();
		update();
	}

	public boolean isFinished()
	{
		return isFinished;
	}
}
