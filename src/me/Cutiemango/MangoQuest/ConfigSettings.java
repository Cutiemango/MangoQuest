package me.Cutiemango.MangoQuest;

import java.util.Locale;

public class ConfigSettings
{
	public static Locale LOCALE_USING;
	public static Locale DEFAULT_LOCALE = new Locale("zh", "TW");
	public static boolean USE_RIGHT_CLICK_MENU = true;
	public static int MAXIMUM_QUEST_AMOUNT = 4;
	public static int MAXIMUM_DISPLAY_QUEST_AMOUNT = 3;
	public static int PLAYER_DATA_SAVE_INTERVAL = 600;
	
	public static boolean POP_LOGIN_MESSAGE = true;
	public static boolean ENABLE_SCOREBOARD = true;
	public static boolean USE_PARTICLE_EFFECT = true;
	public static boolean ENABLE_SKIP = false;
	
	public static boolean USE_WEAK_ITEM_CHECK = false;

	public static boolean USE_DATABASE = false;
	public static String DATABASE_ADDRESS = "localhost";
	public static int DATABASE_PORT = 3306;
	public static String DATABASE_USER = "admin";
	public static String DATABASE_PASSWORD = "1234";
}
