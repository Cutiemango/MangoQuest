package me.Cutiemango.MangoQuest.objects.trigger;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.Main;

public class TriggerTask
{
	private Player target;
	private List<TriggerObject> list = new ArrayList<>();
	private int stage = -1;
	private int index;
	
	public TriggerTask(Player p, List<TriggerObject> objs)
	{
		list = objs;
		target = p;
	}
	
	public void withStage(int i)
	{
		stage = i;
	}
	
	public void start()
	{
		index = 0;
		next();
	}
	
	public void next()
	{
		if (list.size() < index+1)
			return;
		if (list.get(index).getStage() != stage)
		{
			Main.debug("Task skipped with stage " + stage + ", and target stage was " + list.get(index).getStage());
			index++;
			next();
			return;
		}
		index++;
		list.get(index-1).trigger(target, this);
		Main.debug("Task started with stage " + stage + ".");
	}
}
