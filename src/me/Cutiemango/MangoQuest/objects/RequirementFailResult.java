package me.Cutiemango.MangoQuest.objects;

import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.RequirementType;
import net.md_5.bungee.api.ChatColor;

public class RequirementFailResult
{
	Object obj;
	RequirementType type;

	public RequirementFailResult(RequirementType t, Object o)
	{
		type = t;
		obj = o;
	}

	public boolean succeed()
	{
		return type == null;
	}

	public RequirementType getFailType()
	{
		return type;
	}

	public String getMessage()
	{
		String s = "";
		if (type == null)
			return s;
		switch (type)
		{
			case ITEM:
				ItemStack item = (ItemStack) obj;
				if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
					s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Item") + item.getItemMeta().getDisplayName();
				else
					s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Item")
							+ QuestUtil.translate(item.getType(), item.getDurability());
				break;
			case LEVEL:
				s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Level") + (Integer) obj;
				break;
			case MONEY:
				s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Money") + (Double) obj;
				break;
			case SCOREBOARD:
			case NBTTAG:
				s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Special");
				break;
			case QUEST:
				if (obj == null)
					s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Special");
				else
					s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.Quest") + ((Quest) obj).getQuestName();
				break;
			case SKILLAPI_CLASS:
				s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.SkillAPIClass") + obj.toString();
				break;
			case SKILLAPI_LEVEL:
				s = ChatColor.RED + I18n.locMsg("Requirements.NotMeet.SkillAPILevel") + (Integer) obj;
				break;

		}
		return s;
	}

}
