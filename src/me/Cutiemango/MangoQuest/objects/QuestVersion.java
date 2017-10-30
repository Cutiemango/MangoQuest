package me.Cutiemango.MangoQuest.objects;

public class QuestVersion
{

	public QuestVersion(long l)
	{
		questVersion = l;
	}

	private long questVersion;

	public long getVersion()
	{
		return questVersion;
	}

	public void update()
	{
		questVersion = System.currentTimeMillis();
	}

	public void retrieve(long version)
	{
		questVersion = version;
	}

	public static QuestVersion instantVersion()
	{
		return new QuestVersion(System.currentTimeMillis());
	}
}
