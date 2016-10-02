package me.Cutiemango.MangoQuest.conversation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	private List<TextComponent> currentBook = new ArrayList<>();
	private List<TextComponent> history = new ArrayList<>();
	
	// Book Page
	private int page;
	
	public void nextAction(){
		if (actQueue.size() == 0){
			finish();
			return;
		}
		try{
			actQueue.getFirst().execute(this);
			QuestGUIManager.updateConversation(owner, this);
			System.out.println(history.get(page).toPlainText());
			if (!(actQueue.getFirst().getActionType() == EnumAction.BUTTON ||
					actQueue.getFirst().getActionType() == EnumAction.WAIT ||
					actQueue.getFirst().getActionType() == EnumAction.CHOICE)){
				new BukkitRunnable(){
					@Override
					public void run() {
						update(getCurrentPage());
						nextAction();
						return;
					}
				}.runTaskLater(Main.instance, 10L);
			}
			actQueue.removeFirst();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
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
	
	public List<TextComponent> getCurrentBook(){
		return currentBook;
	}
	
	public TextComponent getCurrentPage(){
		if (currentBook.get(page) == null)
			currentBook.set(page, new TextComponent(getDefaultTitleString()));
		return currentBook.get(page);
	}
	
	public void setCurrentBook(List<TextComponent> list){
		currentBook = list;
		update(currentBook.get(page));
	}
	
	public void retrieve(){
		currentBook.set(page, (TextComponent)history.get(page).duplicate());
	}
	
	public void update(TextComponent text){
		history.add(page, new TextComponent(""));
		history.get(page).addExtra(text.duplicate());
	}
	
	public int newPage(){
		page++;
		currentBook.add(page, new TextComponent(getDefaultTitleString()));
		return page;
	}
	
	public final String getDefaultTitleString(){
		return QuestUtil.translateColor("&0「" + conv.getName() + "」");
	}
}
