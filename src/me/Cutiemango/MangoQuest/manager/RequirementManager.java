package me.Cutiemango.MangoQuest.manager;

import java.util.EnumMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.sucy.skill.SkillAPI;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.RequirementType;
import me.Cutiemango.MangoQuest.objects.RequirementFailResult;

public class RequirementManager
{
	@SuppressWarnings("unchecked")
	public static RequirementFailResult meetRequirementWith(Player p, Quest q)
	{
		QuestPlayerData pd = QuestUtil.getData(p);
		EnumMap<RequirementType, Object> requirements = q.getRequirements();
		for (RequirementType t : requirements.keySet())
		{
			Object value = requirements.get(t);
			switch (t)
			{
				case QUEST:
					for (String s : (List<String>) value)
					{
						if (!pd.hasFinished(QuestUtil.getQuest(s)))
							return new RequirementFailResult(RequirementType.QUEST, QuestUtil.getQuest(s));
					}
					break;
				case LEVEL:
					if (!(p.getLevel() >= (Integer) value))
						return new RequirementFailResult(RequirementType.LEVEL, (Integer) value);
					break;
				case MONEY:
					if (Main.instance.pluginHooker.hasEconomyEnabled())
					{
						if (!(Main.instance.pluginHooker.getEconomy().getBalance(p) >= (Double) value))
							return new RequirementFailResult(RequirementType.MONEY, (Double) value);
					}
					break;
				case ITEM:
					for (ItemStack i : (List<ItemStack>) value)
					{
						if (i == null)
							continue;
						if (!p.getInventory().containsAtLeast(i, i.getAmount()))
							return new RequirementFailResult(RequirementType.ITEM, i);
					}
					break;
				case SCOREBOARD:
					for (String s : (List<String>) value)
					{
						s = s.replace(" ", "");
						String[] split;
						if (s.contains(">="))
						{
							split = s.split(">=");
							if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]) == null)
							{
								QuestChatManager.logCmd(Level.WARNING, "任務 " + q.getInternalID() + " 的記分板內容有錯誤，找不到伺服器上名為 " + split[0] + " 的記分板物件資料！");
								return new RequirementFailResult(RequirementType.SCOREBOARD, "");
							}
							if (!(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]).getScore(p.getName()).getScore() >= Integer
									.parseInt(split[1])))
								return new RequirementFailResult(RequirementType.SCOREBOARD, "");
						}
						else
							if (s.contains("<="))
							{
								split = s.split("<=");
								if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]) == null)
								{
									QuestChatManager.logCmd(Level.WARNING, "任務 " + q.getInternalID() + " 的記分板內容有錯誤，找不到伺服器上名為 " + split[0] + " 的記分板物件資料！");
									return new RequirementFailResult(RequirementType.SCOREBOARD, "");
								}
								if (!(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]).getScore(p.getName())
										.getScore() <= Integer.parseInt(split[1])))
									return new RequirementFailResult(RequirementType.SCOREBOARD, "");
							}
							else
								if (s.contains("=="))
								{
									split = s.split("==");
									if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]) == null)
									{
										QuestChatManager.logCmd(Level.WARNING, "任務 " + q.getInternalID() + " 的記分板內容有錯誤，找不到伺服器上名為 " + split[0] + " 的記分板物件資料！");
										return new RequirementFailResult(RequirementType.SCOREBOARD, "");
									}
									if (!(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]).getScore(p.getName())
											.getScore() == Integer.parseInt(split[1])))
										return new RequirementFailResult(RequirementType.SCOREBOARD, "");
								}
					}
					break;
				case NBTTAG:
					for (String n : (List<String>) value)
					{
						if (!Main.instance.handler.hasTag(p, n))
							return new RequirementFailResult(RequirementType.NBTTAG, "");
					}
					break;
				case SKILLAPI_CLASS:
					if (!Main.instance.pluginHooker.hasSkillAPIEnabled())
						break;
					if (SkillAPI.hasPlayerData(p))
					{
						if (((String)value).equalsIgnoreCase("none"))
							break;
						if (SkillAPI.getClass((String)value) == null)
							return new RequirementFailResult(RequirementType.SKILLAPI_CLASS, I18n.locMsg("Requirements.NotMeet.BadConfig") + "沒有名為 " + value + "的職業。");
						if (!SkillAPI.getPlayerData(p).isClass(SkillAPI.getClass((String)value)))
								return new RequirementFailResult(RequirementType.SKILLAPI_CLASS, SkillAPI.getClass((String)value).getName());
					}
					break;
				case SKILLAPI_LEVEL:
					if (!Main.instance.pluginHooker.hasSkillAPIEnabled())
						break;
					if (SkillAPI.hasPlayerData(p) && SkillAPI.getPlayerData(p).getMainClass() != null)
					{
						if (!(SkillAPI.getPlayerData(p).getMainClass().getLevel() >= (Integer)value))
							return new RequirementFailResult(RequirementType.SKILLAPI_LEVEL, value);
					}
					break;
			}
		}
		return new RequirementFailResult(null, "");
	}
	
}
