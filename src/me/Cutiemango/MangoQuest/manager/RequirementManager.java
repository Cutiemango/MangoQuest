package me.Cutiemango.MangoQuest.manager;

import com.sucy.skill.SkillAPI;
import joptsimple.internal.Strings;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.RequirementType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RequirementManager
{
	public static Optional<String> meetRequirementWith(Player p, EnumMap<RequirementType, Object> requirements)
	{
		QuestPlayerData pd = QuestUtil.getData(p);

		List<String> failMsg = new ArrayList<>();

		for (RequirementType t : requirements.keySet())
		{
			Object value = requirements.get(t);
			switch (t)
			{
				case QUEST:
					for (String s : (List<String>) value)
					{
						Quest q = QuestUtil.getQuest(s);
						if (!pd.hasFinished(q))
							failMsg.add(I18n.locMsg("Requirements.NotMeet.Quest", q.getQuestName()));
					}
					break;
				case LEVEL:
					int level = (Integer) value;
					if (p.getLevel() < level)
						failMsg.add(I18n.locMsg("Requirements.NotMeet.Level", Integer.toString(level)));
					break;
				case MONEY:
					if (Main.getHooker().hasEconomyEnabled())
					{
						double money = (Double) value;
						if (!Main.getHooker().getEconomy().hasAccount(p) || Main.getHooker().getEconomy().getBalance(p) < money)
							failMsg.add(I18n.locMsg("Requirements.NotMeet.Money", Double.toString(QuestUtil.cut(money))));
					}
					break;
				case ITEM:
					for (ItemStack i : (List<ItemStack>) value)
					{
						if (i == null)
							continue;
						if (!p.getInventory().containsAtLeast(i, i.getAmount()))
							failMsg.add(I18n.locMsg("Requirements.NotMeet.Item", QuestUtil.getItemName(i), Integer.toString(i.getAmount())));
					}
					break;
				case SKILLAPI_CLASS:
					if (Main.getHooker().hasSkillAPIEnabled() && SkillAPI.hasPlayerData(p))
					{
						String className = (String) value;
						if (className.equalsIgnoreCase("none"))
							break;
						else if (!SkillAPI.isClassRegistered(className))
							failMsg.add(I18n.locMsg("Requirements.NotMeet.NoClass") + className);
						else if (!SkillAPI.getPlayerData(p).isExactClass(SkillAPI.getClass(className)))
							failMsg.add(I18n.locMsg("Requirements.NotMeet.SkillAPIClass", SkillAPI.getClass(className).getPrefix()));
					}
					break;
				case SKILLAPI_LEVEL:
					if (Main.getHooker().hasSkillAPIEnabled() && SkillAPI.hasPlayerData(p))
						if (SkillAPI.getPlayerData(p).getMainClass() == null || SkillAPI.getPlayerData(p).getMainClass().getLevel() < (Integer)value)
							failMsg.add(I18n.locMsg("Requirements.NotMeet.SkillAPILevel", Integer.toString((Integer) value)));
					break;
				case FRIEND_POINT:
					HashMap<Integer, Integer> map = (HashMap<Integer, Integer>) value;

					for (Integer id : ((HashMap<Integer, Integer>) value).keySet())
					{
						if (pd.getNPCfp(id) < map.get(id))
							failMsg.add(I18n.locMsg("Requirements.NotMeet.FriendPoint"));
					}
					break;
			}
		}

		if (failMsg.isEmpty())
			return Optional.ofNullable(null);

		return Optional.of(Strings.join(failMsg, "\n"));
	}
	
}
