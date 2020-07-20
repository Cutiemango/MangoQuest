package me.Cutiemango.MangoQuest.manager;

import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.questobject.CustomQuestObject;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectDeliverItem;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardManager
{
	public static final int SCOREBOARD_TEXT_LIMIT = 40;

	public static Scoreboard update(QuestPlayerData pd)
	{
		Scoreboard s = pd.getScoreboard();
		Objective o = s.getObjective("quest");
		String title = I18n.locMsg("Scoreboard.Title");
		if (o != null)
			o.unregister();
		o = s.registerNewObjective("quest", "dummy");
		o.setDisplayName(title);
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		List<String> scoreList = new ArrayList<>();
		
		scoreList.add(I18n.locMsg("Scoreboard.CurrentQuests"));
		
		for (QuestProgress qp : pd.getProgresses())
		{
			if (!qp.getQuest().getSettings().displayOnProgress())
				continue;
			scoreList.add(pd.getQuestDisplayFormat(qp.getQuest()));
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (formatObjectDisplayText(qop).length() > 40)
				{
					if (qop.getObject() instanceof QuestObjectDeliverItem)
					{
						scoreList.add(formatObjectDeliverItem(qop)[0]);
						scoreList.add(formatObjectDeliverItem(qop)[1]);
						continue;
					}
				}
				scoreList.add(formatObjectDisplayText(qop));
			}
		}
		
		int count = 0;
		scoreList.add(I18n.locMsg("Scoreboard.AvailableQuests"));
		for (Quest q : QuestStorage.Quests.values())
		{
			if (!q.getSettings().displayOnTake())
				continue;
			count++;
			if (count > ConfigSettings.MAXIMUM_DISPLAY_QUEST_AMOUNT)
				break;
			if (pd.canTake(q, false))
			{
				scoreList.add(pd.getQuestDisplayFormat(q));
				if (!q.isCommandQuest())
					scoreList.add(QuestChatManager.trimColor(" &f - " + I18n.locMsg("Scoreboard.GoFindNPC", q.getQuestNPC().getName())));
				else
					scoreList.add(QuestChatManager.trimColor(" &f - " + I18n.locMsg("Scoreboard.TakeFromGUI")));
			}
		}
		
		formatScoreboard(o, scoreList);
		return s;
	}

	private static List<TextChunk> formatText(String uncolored)
	{
		final List<TextChunk> list = new ArrayList<>();
		String text = QuestChatManager.translateColor(uncolored);
		int index = -1;

		TextChunk saved = new TextChunk();

		for (int i = 0; i < text.toCharArray().length; i++)
		{
			// Color code
			if (text.charAt(i) == '§')
			{
				if (index != -1)
				{
					saved.setText(text.substring(index, i));
					list.add(saved);

					saved = new TextChunk();
					index = -1;
				}
				saved.addColor(text.charAt(++i));
			}
			// Normal text
			else
			{
				if (index == -1)
					index = i;
			}
		}
		if (index != -1)
		{
			saved.setText(text.substring(index));
			list.add(saved);
		}
		return list;
	}
	
	private static void formatScoreboard(Objective o, List<String> list)
	{
		int scoreIndex = 0;
		for (int i = 0; i < list.size(); i++)
		{
			String text = list.get(list.size() - (i+1));

			List<TextChunk> chunks = formatText(text);
			List<String> texts = new ArrayList<>();
			StringBuilder saved = new StringBuilder();
			for (TextChunk chunk : chunks)
			{
				if (saved.length() + chunk.length() >= SCOREBOARD_TEXT_LIMIT)
				{
					texts.add(saved.toString());
					saved = new StringBuilder("    ");
				}
				saved.append(chunk.toString());
			}
			texts.add(saved.toString());
			Collections.reverse(texts);

			for (String s : texts)
				o.getScore(s).setScore(scoreIndex++);
		}
	}


	
	private static String formatObjectDisplayText(QuestObjectProgress qop)
	{
		if (qop.isFinished())
			return QuestChatManager.trimColor(" &8&m&o - " + ChatColor.stripColor(qop.getObject().toDisplayText()));
		else
		{
			if (qop.getObject() instanceof NumerableObject)	
				return QuestChatManager.trimColor("&f  - " + qop.getObject().toDisplayText() + " " +
			I18n.locMsg("CommandInfo.Progress", Integer.toString(qop.getProgress()), Integer.toString(((NumerableObject) qop.getObject()).getAmount())));
			else if (qop.getObject() instanceof CustomQuestObject)
				return QuestChatManager.trimColor("&f  - " + ((CustomQuestObject)qop.getObject()).getProgressText(qop));
			else
				return QuestChatManager.trimColor("&f  - " + qop.getObject().toDisplayText());
		}
	}
	
	private static String[] formatObjectDeliverItem(QuestObjectProgress qop)
	{
		QuestObjectDeliverItem obj = (QuestObjectDeliverItem)qop.getObject();
		String[] array = new String[2];
		array[0] = QuestChatManager.trimColor("&f  - " + I18n.locMsg("Scoreboard.QuestObject.ItemToDeliver", Integer.toString(obj.getAmount()), QuestUtil.translate(obj.getItem())));
		array[1] = QuestChatManager.trimColor("    " + I18n.locMsg("Scoreboard.QuestObject.TargetNPC", obj.getTargetNPC().getName()) + " " +
				I18n.locMsg("CommandInfo.Progress", Integer.toString(qop.getProgress()), Integer.toString(((NumerableObject) qop.getObject()).getAmount())));
		if (qop.isFinished())
		{
			QuestChatManager.trimColor("&8&m&o  - " + ChatColor.stripColor(array[0]));
			QuestChatManager.trimColor("&8&m&o     " + ChatColor.stripColor(array[1]));
		}
		return array;
	}

	private static class TextChunk
	{
		private ChatColor colorCode;
		private ChatColor formatCode;
		private String realText;

		public void addColor(char c)
		{
			if (ChatColor.getByChar(c) != null)
			{
				ChatColor color = ChatColor.getByChar(c);
				if (color.isColor())
					colorCode = color;
				else
					if (color.isFormat())
						formatCode = color;
			}
		}

		public void setText(String text)
		{
			realText = text;
		}

		public int length()
		{
			return toString().length();
		}

		@Override
		public String toString()
		{
			return String.format("%s%s%s", colorCode == null ? "" : colorCode, formatCode == null ? "" : formatCode, realText);
		}
	}
}
