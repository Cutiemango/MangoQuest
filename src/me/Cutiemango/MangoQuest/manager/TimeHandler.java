package me.Cutiemango.MangoQuest.manager;

import java.util.Calendar;

public class TimeHandler
{
	public static String convertTime(long l)
	{
		long days = l / 86400000, hours = (l % 86400000) / 3600000, minutes = (l % 3600000) / 60000, seconds = (l % 60000) / 1000;

		return String.format("%s%s%s%s%s", days > 0 ? days + " 天 " : "",
				hours > 0 ? hours + " 小時 " : "",
				minutes > 0 ? minutes + " 分鐘 " : "",
				seconds > 0 ? seconds + " 秒 " : "",
				l < 1000 ? "即將" : "").trim();
	}

	public static long getDailyCooldown(long lastTimestamp, int hour)
	{
		Calendar availableTime = Calendar.getInstance();
		availableTime.setTimeInMillis(lastTimestamp);
		availableTime.add(Calendar.DAY_OF_WEEK, 1);

		availableTime.set(Calendar.HOUR_OF_DAY, hour);
		availableTime.set(Calendar.MINUTE, 0);
		availableTime.set(Calendar.SECOND, 0);
		availableTime.set(Calendar.MILLISECOND, 0);

		return Math.max(availableTime.getTimeInMillis() - System.currentTimeMillis(), -1L);
	}

	public static long getWeeklyCooldown(long lastTimestamp, int day, int hour)
	{
		Calendar availableTime = Calendar.getInstance();
		availableTime.setTimeInMillis(lastTimestamp);

		if (availableTime.get(Calendar.DAY_OF_WEEK) >= 1 + day % 7)
			availableTime.add(Calendar.WEEK_OF_MONTH, 1);

		availableTime.set(Calendar.DAY_OF_WEEK, 1 + day % 7);
		availableTime.set(Calendar.HOUR_OF_DAY, hour);
		availableTime.set(Calendar.MINUTE, 0);
		availableTime.set(Calendar.SECOND, 0);
		availableTime.set(Calendar.MILLISECOND, 0);

		return Math.max(availableTime.getTimeInMillis() - System.currentTimeMillis(), -1L);
	}

	public static boolean canTakeDaily(long lastTimestamp, int hour)
	{
		return getDailyCooldown(lastTimestamp, hour) == -1L;
	}

	public static boolean canTakeWeekly(long lastTimestamp, int day, int hour)
	{
		return getWeeklyCooldown(lastTimestamp, day, hour) == -1L;
	}
}
