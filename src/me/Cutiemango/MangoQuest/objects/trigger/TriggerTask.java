package me.Cutiemango.MangoQuest.objects.trigger;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

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
		list.get(index).trigger(target, this);
	}
	
	public void next()
	{
		if (list.size() <= index+1)
			return;
		index++;
		if (list.get(index).getStage() != stage)
		{
			next();
			return;
		}
		list.get(index).trigger(target, this);
	}
}
