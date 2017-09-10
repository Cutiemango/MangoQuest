package me.Cutiemango.MangoQuest.model;

import me.Cutiemango.MangoQuest.I18n;

public enum TriggerType
{
	TRIGGER_ON_TAKE(I18n.locMsg("TriggerType.OnTake"), false),
	TRIGGER_ON_QUIT(I18n.locMsg("TriggerType.OnQuit"), false),
	TRIGGER_ON_FINISH(I18n.locMsg("TriggerType.OnFinish"), false),
	TRIGGER_STAGE_START(I18n.locMsg("TriggerType.StageStart"), true),
	TRIGGER_STAGE_FINISH(I18n.locMsg("TriggerType.StageFinish"), true);

	private String name;
	private boolean hasStage;

	TriggerType(String s, boolean b)
	{
		name = s;
		hasStage = b;
	}

	public String toCustomString()
	{
		return name;
	}

	public boolean hasStage()
	{
		return hasStage;
	}

	public String toCustomString(int i)
	{
		return name.replace("N", Integer.toString(i));
	}
}