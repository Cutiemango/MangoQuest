package me.Cutiemango.MangoQuest.objects;

public class QuestVersion
{
	public QuestVersion(long l)
	{
		timeStamp = l;
	}

	private long timeStamp;

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public void update()
	{
		timeStamp = System.currentTimeMillis();
	}

	public void retrieve(long version)
	{
		timeStamp = version;
	}

	public static QuestVersion instantVersion()
	{
		return new QuestVersion(System.currentTimeMillis());
	}
}
