package me.Cutiemango.MangoQuest.objects;

import me.Cutiemango.MangoQuest.I18n;

public enum RequirementType
{
	LEVEL(I18n.locMsg("Requirements.Level"), false),
	MONEY(I18n.locMsg("Requirements.Money"), false),
	QUEST(I18n.locMsg("Requirements.Quest"), true),
	ITEM(I18n.locMsg("Requirements.Item"), true),
	FRIEND_POINT(I18n.locMsg("Requirements.FriendPoint"), true),
	SKILLAPI_CLASS(I18n.locMsg("Requirements.SkillAPIClass"), true),
	SKILLAPI_LEVEL(I18n.locMsg("Requirements.SkillAPILevel"), true);

	private String name;
	private boolean index;

	RequirementType(String s, boolean b)
	{
		name = s;
		index = b;
	}

	public boolean hasIndex()
	{
		return index;
	}

}
