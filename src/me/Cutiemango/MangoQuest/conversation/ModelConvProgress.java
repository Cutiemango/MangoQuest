package me.Cutiemango.MangoQuest.conversation;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import org.bukkit.entity.Player;

public class ModelConvProgress extends ConversationProgress
{

	public ModelConvProgress(Player p, QuestConversation qc)
	{
		super(p, qc);
		QuestBookPage page = new QuestBookPage();
		page.add(new InteractiveText(I18n.locMsg("Conversation.Title", conv.getName())).showText(I18n.locMsg("ConversationEditor.EndOfSimulation.ShowText")).clickCommand("/mq ce gui"));
		page.changeLine();
		currentBook.setPage(0, page);
	}
	
	@Override
	public void finish(boolean questFinish)
	{
		getCurrentPage().changeLine();
		getCurrentPage().add(I18n.locMsg("Conversation.Finished")).changeLine();
		getCurrentPage().add(new InteractiveText(I18n.locMsg("ConversationEditor.EndOfSimulation")).clickCommand("/mq ce gui").showText(I18n.locMsg("ConversationEditor.EndOfSimulation.ShowText"))).changeLine();
		ConversationManager.openConversation(owner, this);
	}

}
