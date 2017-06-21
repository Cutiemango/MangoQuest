package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.Location;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectReachLocation extends SimpleQuestObject
{

	public QuestObjectReachLocation(Location l, int i, String s)
	{
		loc = l;
		radius = i;
		name = QuestChatManager.translateColor(s);
		config = "REACH_LOCATION";
	}

	private Location loc;
	private int radius;
	private String name;

	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		return super.toTextComponent(Questi18n.localizeMessage("QuestObject.ReachLocation"), isFinished, name, loc);
	}

	@Override
	public String toPlainText()
	{
		return Questi18n.localizeMessage("QuestObject.ReachLocation", name);
	}

	@Override
	public String toDisplayText()
	{
		return Questi18n.localizeMessage("QuestObject.FinishMessage.ReachLocation", name);
	}
	
	public Location getLocation()
	{
		return loc;
	}

	public String getName()
	{
		return name;
	}

	public int getRadius()
	{
		return radius;
	}

	public void setLocation(Location l)
	{
		loc = l;
	}

	public void setName(String s)
	{
		name = s;
	}

	public void setRadius(int i)
	{
		radius = i;
	}

}
