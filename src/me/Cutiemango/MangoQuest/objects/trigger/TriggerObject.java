package me.Cutiemango.MangoQuest.objects.trigger;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TriggerObject
{
	TriggerObjectType type;
	String obj;
	int stage;
	
	public TriggerObject(TriggerObjectType t, String o, int i)
	{
		type = t;
		obj = o;
		stage = i;
	}
	
	public enum TriggerObjectType
	{
		COMMAND(I18n.locMsg("TriggerObject.Command")),
		COMMAND_PLAYER(I18n.locMsg("EnumAction.CommandPlayer")),
		COMMAND_PLAYER_OP(I18n.locMsg("EnumAction.CommandPlayerOP")),
		SEND_TITLE(I18n.locMsg("TriggerObject.SendTitle")),
		SEND_SUBTITLE(I18n.locMsg("TriggerObject.SendSubtitle")),
		SEND_TITLE_AND_SUBTITLE(I18n.locMsg("TriggerObject.SendTitleAndSubtitle")),
		SEND_MESSAGE(I18n.locMsg("TriggerObject.SendMessage")),
		OPEN_CONVERSATION(I18n.locMsg("TriggerObject.OpenConversation")),
		TELEPORT(I18n.locMsg("TriggerObject.Teleport")),
		WAIT(I18n.locMsg("TriggerObject.Wait"));
	
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
	
	public String getObject()
	{
		return obj;
	}
	
	public int getStage()
	{
		return stage;
	}
	
	public void trigger(Player p, TriggerTask task)
	{
		String object = obj.replace("<player>", p.getName());
		switch(type)
		{
			case WAIT:
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						task.next();
					}
				}.runTaskLater(Main.getInstance(), Long.parseLong(object) * 20);
				return;
			case COMMAND:
				QuestUtil.executeConsoleAsync(object);
				break;
			case COMMAND_PLAYER:
				QuestUtil.executeCommandAsync(p, object);
				break;
			case COMMAND_PLAYER_OP:
				QuestUtil.executeOPCommandAsync(p, object);
				break;
			case SEND_MESSAGE:
				p.sendMessage(QuestChatManager.translateColor(object));
				break;
			case SEND_SUBTITLE:
				QuestUtil.sendTitle(p, 5, 5, 5, "", object);
				break;
			case OPEN_CONVERSATION:
				if (ConversationManager.getConversation(object) != null)
					ConversationManager.startConversation(p, ConversationManager.getConversation(object));
				break;
			case SEND_TITLE:
				QuestUtil.sendTitle(p, 5, 5, 5, object, "");
				break;
			case SEND_TITLE_AND_SUBTITLE:
				String title = object.split("%")[0];
				String subtitle = "";
				if (object.split("%")[0].length() > 1)
					subtitle = object.split("%")[1];
				QuestUtil.sendTitle(p, 5, 5, 5, title, subtitle);
				break;
			case TELEPORT:
				String[] splited = obj.split(":");
				Location loc = new Location(Bukkit.getWorld(splited[0]), Double.parseDouble(splited[1]), Double.parseDouble(splited[2]),
						Double.parseDouble(splited[3]));
				p.teleport(loc);
				break;
			default:
				break;
		}
		task.next();
	}
}
