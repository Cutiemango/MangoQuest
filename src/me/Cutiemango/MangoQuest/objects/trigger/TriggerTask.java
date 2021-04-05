package me.Cutiemango.MangoQuest.objects.trigger;

import me.Cutiemango.MangoQuest.DebugHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class TriggerTask
{
	private Player target;
	private List<TriggerObject> list;
	private int stage = -1;
	private int index;

	public TriggerTask(Player p, List<TriggerObject> objs) {
		list = objs;
		target = p;
	}

	public void withStage(int i) {
		stage = i;
	}

	public void start() {
		index = 0;
		next();
	}

	public void next() {
		if (list.size() < index + 1)
			return;
		if (list.get(index).getStage() != stage) {
			DebugHandler.log(5, "[Triggers] Triggertask skipped with stage " + stage + ", because the target stage was " + list.get(index).getStage());
			index++;
			next();
			return;
		}
		index++;
		list.get(index - 1).trigger(target, this);
		DebugHandler.log(5, "[Triggers] Triggertask started with stage " + stage + ".");
	}
}
