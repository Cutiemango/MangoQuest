package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectReachLocation extends SimpleQuestObject
{
	public QuestObjectReachLocation(){}
	
	public QuestObjectReachLocation(Location l, int i, String s)
	{
		loc = l;
		radius = i;
		name = QuestChatManager.translateColor(s);
	}
	
	@Override
	public String getConfigString()
	{
		return "REACH_LOCATION";
	}

	@Override
	public String getObjectName()
	{
		return I18n.locMsg("QuestObjectName.ReachLocation");
	}

	private Location loc;
	private int radius;
	private String name;

	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		return super.toTextComponent(ChatColor.stripColor(I18n.locMsg("QuestObject.ReachLocation")), isFinished, name, loc);
	}

	@Override
	public String toDisplayText()
	{
		return I18n.locMsg("QuestObject.ReachLocation", name, "");
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

	@Override
	public void formatEditorPage(QuestBookPage page, int stage, int obj)
	{
		page.add(I18n.locMsg("QuestEditor.ReachLocation")).endNormally();
		page.add("(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")").endNormally();
		page.changeLine();
		page.add(I18n.locMsg("QuestEditor.ReachRadius") + radius + I18n.locMsg("QuestEditor.WithinBlocks")).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " loc")).endNormally();
		page.changeLine();
		page.add(I18n.locMsg("QuestEditor.LocationName") + name).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " locname")).endNormally();
		page.changeLine();
	}

	@Override
	public boolean load(QuestIO config, String qpath, int scount, int ocount)
	{
		String[] splited = config.getString(qpath + "Stages." + scount + "." + ocount + ".Location").split(":");
		loc = new Location(Bukkit.getWorld(splited[0]), Double.parseDouble(splited[1]), Double.parseDouble(splited[2]),
				Double.parseDouble(splited[3]));
		radius = config.getInt(qpath + "Stages." + scount + "." + ocount + ".Range");
		name = config.getString(qpath + "Stages." + scount + "." + ocount + ".LocationName");
		return true;
	}

	@Override
	public void save(QuestIO config, String objpath)
	{
		String location = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ();
		config.set(objpath + "Location", location);
		config.set(objpath + "LocationName", name);
		config.set(objpath + "Range", radius);
	}

}
