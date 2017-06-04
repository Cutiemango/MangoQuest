package me.Cutiemango.MangoQuest.conversation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestChatManager;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import net.md_5.bungee.api.chat.TextComponent;

public class ConversationProgress
{

	public ConversationProgress(Player p, QuestConversation qc)
	{
		QuestStorage.ConvProgresses.put(p.getName(), this);
		owner = p;
		conv = qc;
		actQueue = new LinkedList<>(conv.getActions());
	}

	public static final List<EnumAction> STOP_ACTIONS = Arrays.asList(new EnumAction[]
	{ EnumAction.BUTTON, EnumAction.WAIT, EnumAction.CHOICE, EnumAction.FINISH });

	private Player owner;
	private QuestConversation conv;
	private LinkedList<QuestBaseAction> actQueue;
	private LinkedList<TextComponent> currentBook = new LinkedList<>();
	private LinkedList<TextComponent> history = new LinkedList<>();
	private boolean isFinished;
	// private boolean reset;

	// Book Page
	private int page;

	public void nextAction()
	{
		if (actQueue.size() == 0)
		{
			finish(true);
			return;
		}
		actQueue.getFirst().execute(this);
		QuestGUIManager.updateConversation(owner, this);
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
		getCurrentPage().addExtra("\n");
		getCurrentPage().addExtra(Questi18n.localizeMessage("Conversation.Finished"));
		QuestGUIManager.updateConversation(owner, this);
		if (conv.hasNPC() && questFinish)
		{
			QuestUtil.getData(owner).addFinishConversation(conv);
			isFinished = true;
			if (!conv.isFriendConv())
				owner.performCommand("mq conv npc " + conv.getNPC().getId());
		}
		// reset = !questFinish;
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

	public LinkedList<TextComponent> getCurrentBook()
	{
		return currentBook;
	}

	// public boolean needReset(){
	// return reset;
	// }

	public TextComponent getCurrentPage()
	{
		if (currentBook.get(page) == null)
			currentBook.set(page, new TextComponent(getDefaultTitleString()));
		return currentBook.get(page);
	}

	public void setCurrentBook(LinkedList<TextComponent> list)
	{
		currentBook = list;
		update();
	}

	public void retrieve()
	{
		currentBook.set(0, (TextComponent) history.getFirst().duplicate());
	}

	public void update()
	{
		for (int i = 0; i <= page; i++)
		{
			history.add(i, new TextComponent(""));
			history.get(i).addExtra(currentBook.get(i).duplicate());
		}
	}

	public void newPage()
	{
		currentBook.push(new TextComponent(getDefaultTitleString()));
		update();
	}

	public boolean isFinished()
	{
		return isFinished;
	}

	public final String getDefaultTitleString()
	{
		return QuestChatManager.translateColor("&0「" + conv.getName() + "」");
	}
}
