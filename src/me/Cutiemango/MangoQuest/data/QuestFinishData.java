package me.Cutiemango.MangoQuest.data;

import me.Cutiemango.MangoQuest.model.Quest;

public class QuestFinishData
{
	private Quest q;
	private int times;
	private long lastFinish;
	
	private boolean rewardTaken;

	public QuestFinishData(Quest quest, int time, long lastFinishTime, boolean reward)
	{
		q = quest;
		times = time;
		lastFinish = lastFinishTime;
		rewardTaken = reward;
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
	
	public boolean isRewardTaken()
	{
		return rewardTaken;
	}
	
	public void setRewardTaken(boolean b)
	{
		rewardTaken = b;
	}

}
