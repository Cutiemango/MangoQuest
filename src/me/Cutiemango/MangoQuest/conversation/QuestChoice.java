package me.Cutiemango.MangoQuest.conversation;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class QuestChoice
{
	public static class Choice
	{
		private final String content;
		private final List<QuestBaseAction> actions;
		private final HashMap<Integer, Integer> fpReq = new HashMap<>();

		public Choice(String c, List<QuestBaseAction> action) {
			content = c;
			actions = action;
		}

		public String getContent() {
			return content;
		}

		public List<QuestBaseAction> getActions() {
			return actions;
		}

		public HashMap<Integer, Integer> getFriendPointReq() {
			return fpReq;
		}

		public void setFriendPointReq(int npcid, int value) {
			fpReq.put(npcid, value);
		}

	}

	public QuestChoice(TextComponent q, List<Choice> c) {
		question = q;
		choices = c;
	}

	private List<Choice> choices;
	private TextComponent question;

	public TextComponent getQuestion() {
		return question;
	}

	public List<Choice> getChoices() {
		return choices;
	}

	public void apply(ConversationProgress cp) {
		QuestStorage.choiceProgress.put(cp.getOwner().getName(), this);
		cp.getCurrentPage()
				.add(new InteractiveText(question.getText()).showText(I18n.locMsg("Conversation.ClickToAnswer")).clickCommand("/mq conv openchoice"));
	}

	public void choose(Player p, int i) {
		if (i > choices.size()) {
			QuestChatManager.error(p, I18n.locMsg("Conversation.AnswerDenied"));
			return;
		}
		int count = 0;
		ConversationProgress cp = QuestStorage.conversationProgress.get(p.getName());
		if (cp != null) {
			cp.retrieve();
			cp.getCurrentPage().add(question).changeLine();
			for (QuestBaseAction act : choices.get(i).getActions()) {
				ConversationManager.getConvProgress(p).getActionQueue().add(count, act);
				count++;
			}
			ConversationManager.getConvProgress(p).nextAction();
		}
	}

}
