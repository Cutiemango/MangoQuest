package me.Cutiemango.MangoQuest.objects.requirement;

import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.model.Quest;

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
				s = I18n.locMsg("Requirements.NotMeet.Item") + QuestUtil.getItemName(item);
				break;
			case LEVEL:
				s = I18n.locMsg("Requirements.NotMeet.Level") + obj;
				break;
			case MONEY:
				s = I18n.locMsg("Requirements.NotMeet.Money") + obj;
				break;
			case FRIEND_POINT:
				s = I18n.locMsg("Requirements.NotMeet.FriendPoint");
				break;
			case QUEST:
				if (obj == null)
					s = I18n.locMsg("Requirements.NotMeet.Special");
				else
					s = I18n.locMsg("Requirements.NotMeet.Quest") + ((Quest) obj).getQuestName();
				break;
			case SKILLAPI_CLASS:
				s = I18n.locMsg("Requirements.NotMeet.SkillAPIClass") + obj.toString();
				break;
			case SKILLAPI_LEVEL:
				s = I18n.locMsg("Requirements.NotMeet.SkillAPILevel") + obj;
				break;

		}
		return s;
	}

}
