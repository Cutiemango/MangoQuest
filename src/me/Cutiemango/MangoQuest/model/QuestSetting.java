package me.Cutiemango.MangoQuest.model;

import me.Cutiemango.MangoQuest.I18n;
import org.bukkit.World;

public class QuestSetting
{
	// Visibility
	private boolean displayOnTake = true;
	private boolean displayOnProgress = true;
	private boolean displayOnFinish = true;
	private boolean displayOnInteraction = true;

	// FailMessage
	protected String failRequirementMessage = I18n.locMsg("Requirements.NotMeet.Default");

	// Redo & Quit
	//	protected boolean isRedoable = false;
	protected boolean isQuitable = true;
	protected String quitAcceptMsg = I18n.locMsg("QuestQuitMsg.DefaultQuit");
	protected String quitCancelMsg = I18n.locMsg("QuestQuitMsg.DefaultCancel");
	protected String quitDenyMsg = I18n.locMsg("QuestQuitMsg.Denied");

	protected RedoSetting redoSetting = RedoSetting.ONCE_ONLY;
	protected long redoDelay = 0L;
	protected int resetDay = 1;
	protected int resetHour = 0;

	// Limitations
	protected boolean isTimeLimited = false;
	protected long timeLimit = 60000L;
	protected boolean usePermission = false;
	protected World worldLimit = null;

	public boolean displayOnTake() {
		return displayOnTake;
	}

	public boolean displayOnProgress() {
		return displayOnProgress;
	}

	public boolean displayOnFinish() {
		return displayOnFinish;
	}

	public boolean displayOnInteraction() {
		return displayOnInteraction;
	}

	public void toggle(boolean b1, boolean b2, boolean b3, boolean b4) {
		displayOnTake = b1;
		displayOnProgress = b2;
		displayOnFinish = b3;
		displayOnInteraction = b4;
	}

	public enum RedoSetting
	{
		DAILY(I18n.locMsg("QuestEditor.RedoSetting.Daily")),
		WEEKLY(I18n.locMsg("QuestEditor.RedoSetting.Weekly")),
		COOLDOWN(I18n.locMsg("QuestEditor.RedoSetting.Cooldown")),
		ONCE_ONLY(I18n.locMsg("QuestEditor.RedoSetting.OnceOnly"));

		RedoSetting(String s) {
			name = s;
		}

		private String name;

		public String getName() {
			return name;
		}
	}
}
