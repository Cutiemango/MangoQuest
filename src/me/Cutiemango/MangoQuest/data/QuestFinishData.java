package me.Cutiemango.MangoQuest.data;

import me.Cutiemango.MangoQuest.model.Quest;

public class QuestFinishData
{

	private Quest q;
	private int times;
	private long lastFinish;

	public QuestFinishData(Quest quest, int time, long lastFinishTime)
	{
		q = quest;
		times = time;
		lastFinish = lastFinishTime;
	}

	public Quest getQuest()
	{
		return q;
	}

	public int getFinishedTimes()
	{
		return times;
	}

	public long getLastFinish()
	{
		return lastFinish;
	}

	public void finish()
	{
		lastFinish = System.currentTimeMillis();
		times++;
	}

	public void setLastFinish(long l)
	{
		lastFinish = l;
	}

}
