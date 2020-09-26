package me.Cutiemango.MangoQuest.conversation;

import me.Cutiemango.MangoQuest.*;
import me.Cutiemango.MangoQuest.book.FlexibleBook;
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
		QuestStorage.conversationProgress.put(p.getName(), this);
		owner = p;
		conv = qc;
		actQueue = new LinkedList<>(conv.getActions());
		
		// Init the first page
		currentBook.addPage(0, ConversationManager.generateNewPage(qc));
		currentBook.removePage(1);
	}

	// actions that needs player interaction or wait
	public static final List<EnumAction> STOP_ACTIONS = Arrays.asList(EnumAction.BUTTON, EnumAction.WAIT, EnumAction.CHOICE, EnumAction.FINISH, EnumAction.TAKE_QUEST, EnumAction.EXIT);

	// actions that will cause the rush function to be disabled
	public static final List<EnumAction> DISABLE_RUSH = Arrays.asList(EnumAction.BUTTON, EnumAction.CHOICE, EnumAction.TAKE_QUEST, EnumAction.EXIT);

	// actions that need to be scheduled a task
	public static final List<EnumAction> DELAYED_ACTIONS = Arrays.asList(EnumAction.BUTTON, EnumAction.CHANGE_PAGE, EnumAction.NPC_TALK, EnumAction.SENTENCE, EnumAction.TAKE_QUEST, EnumAction.CHOICE);
	
	protected Player owner;
	protected QuestConversation conv;
	protected LinkedList<QuestBaseAction> actQueue;
	protected FlexibleBook currentBook = new FlexibleBook();
	protected FlexibleBook history = new FlexibleBook();
	protected boolean isFinished;
	
	private int taskID = 0;

	protected boolean rushed = false;
	protected boolean rushDisabled = false;

	public void nextAction()
	{
		// need to handle if the player disconnects
		if (!owner.isOnline())
			return;

		if (rushed)
		{
			while (!actQueue.isEmpty())
			{
				QuestBaseAction act = actQueue.pollFirst();
				act.execute(this);

				// encountered an action that requires player to interact
				if (DISABLE_RUSH.contains(act.getActionType()))
				{
					rushDisabled = true;
					rushed = false;
					DebugHandler.log(5, "[Conversation] Rush disabled.");

					ConversationManager.openConversation(owner, this);
					return;
				}
				update();
			}
			// the action queue is cleared
			DebugHandler.log(5, "[Conversation] Finished due to no actions left and no pending actions to be made by the player.");
			finish(true);
		}
		else
		{
			if (actQueue.isEmpty())
			{
				DebugHandler.log(5, "[Conversation] Conversation finished due to no actions left.");
				finish(true);
				return;
			}
			else
			{
				QuestBaseAction act = actQueue.getFirst();
				act.execute(this);
				actQueue.pop();
				rushDisabled = false;
				DebugHandler.log(5, "[Conversation] Action type %s with param %s executed.", act.getActionType().toString(), act.getObject());
				DebugHandler.log(5, "[Conversation] Rush enabled.");

				if (act.getActionType() == EnumAction.EXIT)
					return;

				// still more actions

				// if the action genre is not displaying text, execute immediately
				if (!actQueue.isEmpty() && !DELAYED_ACTIONS.contains(actQueue.getFirst().getActionType()))
				{
					nextAction();
					return;
				}
				else
				{
					// otherwise, schedule a task with specified delay (check if the action requires player interaction first)
					if (!(STOP_ACTIONS.contains(act.getActionType())))
					{
						cancelTask();
						registerTask();

						// history needs to be written immediately
						update();
					}
					ConversationManager.openConversation(owner, this);
				}
			}
		}
	}
	
	public void rush()
	{
		if (rushDisabled)
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
				nextAction();
			}
		}.runTaskLater(Main.getInstance(), ConfigSettings.CONVERSATION_ACTION_INTERVAL_IN_TICKS).getTaskId();
	}
	
	public void cancelTask()
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
				QuestUtil.executeSyncCommand(owner, "mq quest take " + ((StartTriggerConversation)conv).getQuest().getInternalID());
				ConversationManager.finishConversation(owner);
				return;
			}
			if (!(conv instanceof FriendConversation))
				QuestUtil.executeSyncCommand(owner, "mq conv npc " + conv.getNPC().getId());
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

	public FlexibleBook getCurrentBook()
	{
		return currentBook;
	}

	public QuestBookPage getCurrentPage()
	{
		if (currentBook.getFirstPage().isOutOfBounds())
		{
			currentBook.pushNewPage(conv);
			currentBook.getFirstPage().add(currentBook.getPage(1).getSaved());
		}
		return currentBook.getFirstPage();
	}
	
	public void retrieve()
	{
		currentBook.setPage(0, history.getFirstPage().duplicate());
		DebugHandler.log(5, "[Conversation] History retrieved.");
	}

	// write current book data into history
	public void update()
	{
		for (int i = 0; i < currentBook.size(); i++)
		{
			if (history.size() <= i)
				history.addPage(i, new QuestBookPage());
			history.setPage(i, currentBook.getPage(i).duplicate());
		}
		DebugHandler.log(5, "[Conversation] History written.");
	}

	public void newPage()
	{
		currentBook.pushNewPage(conv);
		currentBook.getFirstPage().add(currentBook.getPage(1).getSaved());
		update();
	}

	public boolean isFinished()
	{
		return isFinished;
	}
}
