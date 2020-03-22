package me.Cutiemango.MangoQuest.manager;

import com.sucy.skill.SkillAPI;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.objects.requirement.RequirementFailResult;
import me.Cutiemango.MangoQuest.objects.requirement.RequirementType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class RequirementManager
{
	@SuppressWarnings("unchecked")
	public static RequirementFailResult meetRequirementWith(Player p, EnumMap<RequirementType, Object> requirements)
	{
		QuestPlayerData pd = QuestUtil.getData(p);
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
					if (Main.getHooker().hasEconomyEnabled())
					{
						if (!Main.getHooker().getEconomy().hasAccount(p))
							return new RequirementFailResult(RequirementType.MONEY, (Double) value);
						if (Main.getHooker().getEconomy().getBalance(p) < (Double) value)
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
				case SKILLAPI_CLASS:
					if (!Main.getHooker().hasSkillAPIEnabled())
						break;
					if (SkillAPI.hasPlayerData(p))
					{
						if (((String) value).equalsIgnoreCase("none"))
							break;
						if (!SkillAPI.isClassRegistered((String) value))
							return new RequirementFailResult(RequirementType.SKILLAPI_CLASS,
									I18n.locMsg("Requirements.NotMeet.NoClass", (String) value));
						if (!SkillAPI.getPlayerData(p).isExactClass(SkillAPI.getClass((String) value)))
							return new RequirementFailResult(RequirementType.SKILLAPI_CLASS, SkillAPI.getClass((String) value).getPrefix());
					}
					break;
				case SKILLAPI_LEVEL:
					if (!Main.getHooker().hasSkillAPIEnabled())
						break;
					if (SkillAPI.hasPlayerData(p) && SkillAPI.getPlayerData(p).getMainClass() != null)
					{
						if (!(SkillAPI.getPlayerData(p).getMainClass().getLevel() >= (Integer)value))
							return new RequirementFailResult(RequirementType.SKILLAPI_LEVEL, value);
					}
					break;
				case FRIEND_POINT:
					HashMap<Integer, Integer> map = (HashMap<Integer, Integer>) value;
					for (Integer id : ((HashMap<Integer, Integer>) value).keySet())
					{
						if (pd.getNPCfp(id) < map.get(id))
							return new RequirementFailResult(RequirementType.FRIEND_POINT, value);
					}
					break;
			}
		}
		return new RequirementFailResult(null, "");
	}
	
}
