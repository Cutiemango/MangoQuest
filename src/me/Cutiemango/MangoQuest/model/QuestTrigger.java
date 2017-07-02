package me.Cutiemango.MangoQuest.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.advancements.QuestAdvancementManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

public class QuestTrigger
{

	TriggerType t;
	TriggerObject o;
	Object value;
	int count;

	public QuestTrigger(TriggerType type, TriggerObject obj, Object arg)
	{
		t = type;
		o = obj;
		value = arg;
		if (value instanceof String)
			value = QuestChatManager.translateColor((String) value);
	}

	public QuestTrigger(TriggerType type, TriggerObject obj, int i, Object arg)
	{
		t = type;
		count = i;
		o = obj;
		value = arg;
		if (value instanceof String)
			value = QuestChatManager.translateColor((String) value);
	}

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

	public enum TriggerObject
	{
		COMMAND(I18n.locMsg("TriggerObject.Command")),
		SEND_TITLE(I18n.locMsg("TriggerObject.SendTitle")),
		SEND_SUBTITLE(I18n.locMsg("TriggerObject.SendSubtitle")),
		SEND_MESSAGE(I18n.locMsg("TriggerObject.SendMessage")),
		TELEPORT(I18n.locMsg("TriggerObject.Teleport")),
		GIVE_ADVANCEMENT(I18n.locMsg("TriggerObject.GiveAdvancement"));

		private String name;

		TriggerObject(String s)
		{
			name = s;
		}

		public String toCustomString()
		{
			return name;
		}
	}

	public void trigger(Player p)
	{
		String replaced = "";
		Location loc = p.getLocation();
		replaced = ((String) value).replace("<player>", p.getName());
		replaced = QuestChatManager.translateColor(replaced);
		switch (o)
		{
			case COMMAND:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replaced);
				break;
			case SEND_TITLE:
				QuestUtil.sendTitle(p, 5, 5, 5, replaced, null);
				break;
			case SEND_SUBTITLE:
				QuestUtil.sendTitle(p, 5, 5, 5, null, replaced);
				break;
			case SEND_MESSAGE:
				p.sendMessage(replaced);
				break;
			case TELEPORT:
				String[] splited = ((String) value).split(":");
				loc = new Location(Bukkit.getWorld(splited[0]), Double.parseDouble(splited[1]), Double.parseDouble(splited[2]),
						Double.parseDouble(splited[3]));
				p.teleport(loc);
				break;
			case GIVE_ADVANCEMENT:
				if (Main.isUsingUpdatedVersion())
				{
					QuestAdvancementManager.getAdvancement(replaced).grant(p);
				}
				break;
			default:
				break;
		}

	}

	public TriggerType getType()
	{
		return t;
	}

	public int getCount()
	{
		return count;
	}

	public TriggerObject getTriggerObject()
	{
		return o;
	}

	public Object getObject()
	{
		return value;
	}

}
