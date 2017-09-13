package me.Cutiemango.MangoQuest.model;

import me.Cutiemango.MangoQuest.I18n;

public class QuestSetting
{
	private boolean displayOnTake = true;
	private boolean displayOnProgress = true;
	private boolean displayOnFinish = true;
	
	protected boolean useCustomFailMessage = false;
	protected String failRequirementMessage = I18n.locMsg("Requirements.NotMeet.Default");
	
	protected boolean isRedoable = false;
	protected boolean isQuitable = true;
	protected String quitAcceptMsg = I18n.locMsg("QuestQuitMsg.DefaultQuit");
	protected String quitCancelMsg = I18n.locMsg("QuestQuitMsg.DefaultCancel");
	protected String quitDenyMsg = I18n.locMsg("QuestQuitMsg.Denied");
	protected long redoDelay;
	
	public boolean displayOnTake()
	{
		return displayOnTake;
	}
	
	public boolean displayOnProgress()
	{
		return displayOnProgress;
	}
	
	public boolean displayOnFinish()
	{
		return displayOnFinish;
	}
	
	public void toggle(boolean b1, boolean b2, boolean b3)
	{
		displayOnTake = b1;
		displayOnProgress = b2;
		displayOnFinish = b3;
	}
}
