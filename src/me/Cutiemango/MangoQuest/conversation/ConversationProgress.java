package me.Cutiemango.MangoQuest.conversation;

import java.util.LinkedList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.QuestBaseAction.EnumAction;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import net.md_5.bungee.api.chat.TextComponent;

public class ConversationProgress {
	
	public ConversationProgress(Player p, QuestConversation qc){
		QuestStorage.ConvProgresses.put(p.getName(), this);
		owner = p;
		conv = qc;
		actQueue = new LinkedList<>(conv.getActions());
	}
	
	private Player owner;
	private QuestConversation conv;
	private LinkedList<QuestBaseAction> actQueue;
	private LinkedList<TextComponent> currentBook = new LinkedList<>();
	private LinkedList<TextComponent> history = new LinkedList<>();
	
	// Book Page
	private int page;
	
	public void nextAction(){
		if (actQueue.size() == 0){
			finish();
			return;
		}
		actQueue.getFirst().execute(this);
		QuestGUIManager.updateConversation(owner, this);
		if (!(actQueue.getFirst().getActionType() == EnumAction.BUTTON ||
				actQueue.getFirst().getActionType() == EnumAction.WAIT ||
				actQueue.getFirst().getActionType() == EnumAction.CHOICE)){
			new BukkitRunnable(){
				@Override
				public void run() {
					update();
					nextAction();
					return;
				}
			}.runTaskLater(Main.instance, 10L);
		}
		actQueue.removeFirst();
	}
	
	public void finish(){
		getCurrentPage().addExtra("\n");
		getCurrentPage().addExtra("對話結束。");
		QuestGUIManager.updateConversation(owner, this);
	}


	public Player getOwner() {
		return owner;
	}
	
	public LinkedList<QuestBaseAction> getActionQueue(){
		return actQueue;
	}

	public QuestConversation getConvseration() {
		return conv;
	}
	
	public LinkedList<TextComponent> getCurrentBook(){
		return currentBook;
	}
	
	public TextComponent getCurrentPage(){
		if (currentBook.get(page) == null)
			currentBook.set(page, new TextComponent(getDefaultTitleString()));
		return currentBook.get(page);
	}
	
	public void setCurrentBook(LinkedList<TextComponent> list){
		currentBook = list;
		update();
	}
	
	public void retrieve(){
		currentBook.set(0, (TextComponent)history.getFirst().duplicate());
	}
	
	public void update(){
		for (int i = 0; i <= page; i++){
			history.add(i, new TextComponent(""));
			history.get(i).addExtra(currentBook.get(i).duplicate());
		}
	}
	
	public void newPage(){
		currentBook.push(new TextComponent(getDefaultTitleString()));
		update();
	}
	
	public final String getDefaultTitleString(){
		return QuestUtil.translateColor("&0「" + conv.getName() + "」");
	}
}
