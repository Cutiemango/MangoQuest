package me.Cutiemango.MangoQuest.conversation;

import java.util.List;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestChoice
{

	public static class Choice
	{
		private String s;
		private List<QuestBaseAction> act;

		public Choice(String c, List<QuestBaseAction> action)
		{
			s = c;
			act = action;
		}

		public String getContent()
		{
			return s;
		}

		public List<QuestBaseAction> getActions()
		{
			return act;
		}

	}

	public QuestChoice(TextComponent q, List<Choice> c)
	{
		question = q;
		choices = c;
	}

	private List<Choice> choices;
	private TextComponent question;

	public TextComponent getQuestion()
	{
		return question;
	}

	public List<Choice> getChoices()
	{
		return choices;
	}

	public void apply(ConversationProgress cp)
	{
		QuestStorage.ChoiceProgresses.put(cp.getOwner().getName(), this);
		cp.getCurrentPage().add(new InteractiveText(question.getText()).showText(I18n.locMsg("Conversation.ClickToAnswer"))
				.clickCommand("/mq conv openchoice")).endNormally();
		QuestBookGUIManager.openChoice(cp.getOwner(), question, choices);
	}

	public void choose(Player p, int i)
	{
		if (i > choices.size())
		{
			QuestChatManager.error(p, I18n.locMsg("Conversation.AnswerDenied"));
			return;
		}
		int count = 0;
		ConversationProgress cp = QuestStorage.ConvProgresses.get(p.getName());
		cp.retrieve();
		cp.getCurrentPage().add(question).changeLine();
		for (QuestBaseAction act : choices.get(i).getActions())
		{
			ConversationManager.getConvProgress(p).getActionQueue().add(count, act);
			count++;
		}
		ConversationManager.getConvProgress(p).nextAction();
	}

}
