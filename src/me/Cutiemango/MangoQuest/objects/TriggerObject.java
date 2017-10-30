package me.Cutiemango.MangoQuest.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.advancements.QuestAdvancementManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.TriggerType;

public class TriggerObject
{
	TriggerObjectType type;
	Object obj;
	int s;
	
	public TriggerObject(TriggerObjectType t, Object o, int i)
	{
		type = t;
		obj = o;
		s = i;
	}
	
	public enum TriggerObjectType
	{
		COMMAND(I18n.locMsg("TriggerObject.Command")),
		SEND_TITLE(I18n.locMsg("TriggerObject.SendTitle")),
		SEND_SUBTITLE(I18n.locMsg("TriggerObject.SendSubtitle")),
		SEND_TITLE_AND_SUBTITLE(I18n.locMsg("TriggerObject.SendTitleAndSubtitle")),
		SEND_MESSAGE(I18n.locMsg("TriggerObject.SendMessage")),
		TELEPORT(I18n.locMsg("TriggerObject.Teleport")),
		WAIT(I18n.locMsg("TriggerObject.Wait")),
		GIVE_ADVANCEMENT(I18n.locMsg("TriggerObject.GiveAdvancement"));
	
		private String name;
	
		TriggerObjectType(String s)
		{
			name = s;
		}
	
		public String toCustomString()
		{
			return name;
		}
	}
	
	public TriggerObjectType getObjType()
	{
		return type;
	}
	
	public Object getObject()
	{
		return obj;
	}
	
	public int getStage()
	{
		return s;
	}
	
	public void trigger(Player p, int index, TriggerType t, int stage, Quest q)
	{
		if (s != stage)
		{
			q.trigger(p, index+1, t, stage);
			return;
		}
		String object = ((String) obj).replace("<player>", p.getName());
		switch(type)
		{
			case WAIT:
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						q.trigger(p, index+1, t, stage);
					}
				}.runTaskLater(Main.instance, Long.parseLong(object) * 20);
				return;
			case COMMAND:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), object);
				break;
			case GIVE_ADVANCEMENT:
				if (Main.isUsingUpdatedVersion())
				{
					QuestAdvancementManager.getAdvancement(object).grant(p);
				}
				break;
			case SEND_MESSAGE:
				p.sendMessage(QuestChatManager.translateColor(object));
				break;
			case SEND_SUBTITLE:
				QuestUtil.sendTitle(p, 5, 5, 5, null, object);
				break;
			case SEND_TITLE:
				QuestUtil.sendTitle(p, 5, 5, 5, object, null);
				break;
			case SEND_TITLE_AND_SUBTITLE:
				String title = object.split("%")[0];
				String subtitle = "";
				if (object.split("%")[0].length() > 1)
					subtitle = object.split("%")[1];
				QuestUtil.sendTitle(p, 5, 5, 5, title, subtitle);
				break;
			case TELEPORT:
				String[] splited = ((String) obj).split(":");
				Location loc = new Location(Bukkit.getWorld(splited[0]), Double.parseDouble(splited[1]), Double.parseDouble(splited[2]),
						Double.parseDouble(splited[3]));
				p.teleport(loc);
				break;
			default:
				break;
		}
		q.trigger(p, index+1, t, stage);
	}
}
