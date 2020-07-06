package me.Cutiemango.MangoQuest.conversation;

import me.Cutiemango.MangoQuest.*;
import me.Cutiemango.MangoQuest.book.FlexiableBook;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ConversationProgress
{
	public ConversationProgress(Player p, QuestConversation qc)
	{
		QuestStorage.ConvProgresses.put(p.getName(), this);
		owner = p;
		conv = qc;
		actQueue = new LinkedList<>(conv.getActions());
		
		// Init the first page
		currentBook.addPage(0, ConversationManager.generateNewPage(qc));
		currentBook.removePage(1);
	}

	public static final List<EnumAction> STOP_ACTIONS = Arrays.asList(EnumAction.BUTTON, EnumAction.WAIT, EnumAction.CHOICE, EnumAction.FINISH, EnumAction.TAKE_QUEST, EnumAction.EXIT);
	public static final List<EnumAction> DISABLE_RUSH = Arrays.asList(EnumAction.BUTTON, EnumAction.CHOICE, EnumAction.TAKE_QUEST, EnumAction.EXIT);
	
	protected Player owner;
	protected QuestConversation conv;
	protected LinkedList<QuestBaseAction> actQueue;
	protected FlexiableBook currentBook = new FlexiableBook();
	protected FlexiableBook history = new FlexiableBook();
	protected boolean isFinished;
	
	private int taskID = 0;

	protected boolean rushed = false;
	protected boolean disableRush = false;

	public void nextAction()
	{
		if (rushed)
		{
			while (actQueue.size() != 0)
			{
				QuestBaseAction act = actQueue.getFirst();
				if (DISABLE_RUSH.contains(act.getActionType()))
				{
					act.execute(this);
					disableRush = true;
					DebugHandler.log(5, "Rush disabled.");
					actQueue.removeFirst();
					break;
				}
				act.execute(this);
				actQueue.removeFirst();
				update();
			}
			if (actQueue.size() == 0 && !disableRush)
			{
				DebugHandler.log(5, "Finished due to no actions left and no pending actions to make by the player.");
				finish(true);
			}
			else
			{
				ConversationManager.openConversation(owner, this);
				rushed = false;
			}
			return;
		}
		
		if (actQueue.size() == 0)
		{
			DebugHandler.log(5, "Naturally finished due to no actions left.");
			finish(true);
			return;
		}
		actQueue.getFirst().execute(this);
		disableRush = false;
		DebugHandler.log(5, "Rush enabled.");
		
		if (actQueue.getFirst().getActionType() == EnumAction.EXIT)
			return;

		
		if (!(STOP_ACTIONS.contains(actQueue.getFirst().getActionType())))
		{
			cancelTask();
			registerTask();
		}

		ConversationManager.openConversation(owner, this);
		actQueue.removeFirst();
		
	}
	
	public void rush()
	{
		if (disableRush)
		{
			QuestChatManager.error(owner, I18n.locMsg("Conversation.SkipDisabled"));
			ConversationManager.openConversation(owner, this);
			return;
		}
		rushed = true;
		cancelTask();
		nextAction();
	}
	
	private void registerTask()
	{
		taskID = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				update();
				nextAction();
			}
		}.runTaskLater(Main.getInstance(), 25L).getTaskId();
	}
	
	private void cancelTask()
	{
		if (taskID != 0)
		{
			Bukkit.getScheduler().cancelTask(taskID);
			taskID = 0;
		}
	}

	public void finish(boolean questFinish)
	{
		if (isFinished)
			return;
		isFinished = true;
		getCurrentPage().changeLine();
		getCurrentPage().add(I18n.locMsg("Conversation.Finished")).changeLine();
		ConversationManager.openConversation(owner, this);
		if (conv.hasNPC() && questFinish)
		{
			QuestUtil.getData(owner).addFinishConversation(conv);
			if (conv instanceof StartTriggerConversation)
			{
				QuestUtil.executeCommandAsync(owner, "mq quest take " + ((StartTriggerConversation)conv).getQuest().getInternalID());
				ConversationManager.finishConversation(owner);
				return;
			}
			if (!(conv instanceof FriendConversation))
				QuestUtil.executeCommandAsync(owner, "mq conv npc " + conv.getNPC().getId());
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
		int page = 0;
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
