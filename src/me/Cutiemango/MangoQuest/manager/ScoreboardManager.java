package me.Cutiemango.MangoQuest.manager;

import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.TextAlignment;
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
import java.util.List;


public class ScoreboardManager
{
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
	
	private static String getLastAppliedColor(String s)
	{
		String color = "§f";
		String text = QuestChatManager.trimColor(s);
		if (text.lastIndexOf("§") == -1)
			return "§f";
		if (TextAlignment.ESCAPE_COLOR_CODES.contains(s.charAt(text.lastIndexOf("§") + 1)))
			if (s.lastIndexOf("§") - 1 > 0)
				color = "§" + text.charAt(text.lastIndexOf("§") - 1) + "§" + text.charAt(text.lastIndexOf("§") + 1);
			else
				color = "§" + text.charAt(text.lastIndexOf("§") + 1);
		else
			color = "§" + text.charAt(text.lastIndexOf("§") + 1);
		return color;
	}
	
	private static void formatScoreboard(Objective o, List<String> list)
	{
		int scoreIndex = 0;
		for (int i = 0; i < list.size(); i++)
		{
			String text = list.get(list.size() - (i+1));
			if (text.length() > 40)
			{
				text = QuestChatManager.trimColor(text);
				String t1 = text.substring(0, 40);
				String t2 = text.substring(40, text.length()-1);
				
				if (t1.charAt(t1.length()-1) == '§')
				{
					t1 = text.substring(0, 39);
					t2 = text.substring(39, text.length()-1);
				}
				
				o.getScore(t1).setScore(scoreIndex+1);
				o.getScore(QuestChatManager.trimColor("    " + getLastAppliedColor(t1) + t2)).setScore(scoreIndex);
				scoreIndex+=2;
				continue;
			}
			o.getScore(text).setScore(scoreIndex);
			scoreIndex++;
		}
		return;
	}
	
	private static String formatObjectDisplayText(QuestObjectProgress qop)
	{
		if (qop.isFinished())
			return QuestChatManager.trimColor(" &8&m&o - " + ChatColor.stripColor(qop.getObject().toDisplayText()));
		else
		{
			if (qop.getObject() instanceof NumerableObject)	
				return QuestChatManager.trimColor(" &f - " + qop.getObject().toDisplayText() + " " +
			I18n.locMsg("CommandInfo.Progress", Integer.toString(qop.getProgress()), Integer.toString(((NumerableObject) qop.getObject()).getAmount())));
			else if (qop.getObject() instanceof CustomQuestObject)
				return QuestChatManager.trimColor(" &f - " + ((CustomQuestObject)qop.getObject()).getProgressText(qop));
			else
				return QuestChatManager.trimColor(" &f - " + qop.getObject().toDisplayText());
		}
	}
	
	private static String[] formatObjectDeliverItem(QuestObjectProgress qop)
	{
		QuestObjectDeliverItem obj = (QuestObjectDeliverItem)qop.getObject();
		String[] array = new String[2];
		array[0] = QuestChatManager.trimColor(" &f - " + I18n.locMsg("Scoreboard.QuestObject.ItemToDeliver", Integer.toString(obj.getAmount()), QuestUtil.translate(obj.getItem())));
		array[1] = QuestChatManager.trimColor("    " + I18n.locMsg("Scoreboard.QuestObject.TargetNPC", obj.getTargetNPC().getName()) + " " +
				I18n.locMsg("CommandInfo.Progress", Integer.toString(qop.getProgress()), Integer.toString(((NumerableObject) qop.getObject()).getAmount())));
		if (qop.isFinished())
		{
			QuestChatManager.trimColor(" &8&m&o - " + ChatColor.stripColor(array[0]));
			QuestChatManager.trimColor("&8&m&o     " + ChatColor.stripColor(array[1]));
		}
		return array;
	}
}
