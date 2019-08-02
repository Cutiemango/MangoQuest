package me.Cutiemango.MangoQuest.model;

import org.bukkit.World;
import me.Cutiemango.MangoQuest.I18n;

public class QuestSetting
{
	// Visibility
	private boolean displayOnTake = true;
	private boolean displayOnProgress = true;
	private boolean displayOnFinish = true;
	private boolean displayOnInteraction = true;
	
	// FailMessage
	protected boolean useCustomFailMessage = false;
	protected String failRequirementMessage = I18n.locMsg("Requirements.NotMeet.Default");
	
	// Redo & Quit
	protected boolean isRedoable = false;
	protected boolean isQuitable = true;
	protected String quitAcceptMsg;
	protected String quitCancelMsg;
	protected String quitDenyMsg = I18n.locMsg("QuestQuitMsg.Denied");
	protected long redoDelay;
	
	// Limitations
	protected boolean isTimeLimited = false;
	protected long timeLimit = 60000L;
	protected boolean usePermission = false;
	protected World worldLimit = null;

	
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
	
	public boolean displayOnInteraction()
	{
		return displayOnInteraction;
	}
	
	public void toggle(boolean b1, boolean b2, boolean b3, boolean b4)
	{
		displayOnTake = b1;
		displayOnProgress = b2;
		displayOnFinish = b3;
		displayOnInteraction = b4;
	}
}
