package me.Cutiemango.MangoQuest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

public class Questi18n
{

	private static ResourceBundle bundle;

	public static void init(Locale local)
	{
		try
		{
			Main.instance.saveResource("messages_" + QuestConfigSettings.LOCALE_USING.toString() + ".properties", true);
			Main.instance.saveResource("original_" + QuestConfigSettings.LOCALE_USING.toString() + ".yml", 
					!new File("original_" + QuestConfigSettings.LOCALE_USING.toString() + ".yml").exists());
			bundle = ResourceBundle.getBundle("messages", local);
		}
		catch (MissingResourceException e)
		{
			bundle = ResourceBundle.getBundle("messages", local, new FileResClassLoader(Questi18n.class.getClassLoader(), Main.instance));
		}
	}

	public static String localizeMessage(String path)
	{
		String format = bundle.getString(path);
		format = QuestChatManager.translateColor(format);
		if (format == null)
			return path;
		else
			return format;
	}

	public static String localizeMessage(String path, String... args)
	{
		String format = bundle.getString(path);
		if (format == null)
			return path;
		format = QuestChatManager.translateColor(format);
		if (format.contains("%"))
		{
			try
			{
				for (int arg = 0; arg < args.length; arg++)
				{
					format = format.replace("[%" + arg + "]", args[arg]);
				}
				return format;
			}
			catch (Exception e)
			{
				QuestChatManager.logCmd(Level.WARNING, "An error occured whilst localizing " + path + " .");
				e.printStackTrace();
			}
		}
		return format;
	}

	private static class FileResClassLoader extends ClassLoader
	{
		private final File dataFolder;

		FileResClassLoader(final ClassLoader classLoader, final Main plugin)
		{
			super(classLoader);
			this.dataFolder = plugin.getDataFolder();
		}

		@Override
		public URL getResource(final String string)
		{
			final File file = new File(dataFolder, string);
			if (file.exists())
			{
				try
				{
					return file.toURI().toURL();
				}
				catch (MalformedURLException ex)
				{
				}
			}
			return null;
		}

		@Override
		public InputStream getResourceAsStream(final String string)
		{
			final File file = new File(dataFolder, string);
			if (file.exists())
			{
				try
				{
					return new FileInputStream(file);
				}
				catch (FileNotFoundException ex)
				{
				}
			}
			return null;
		}
	}

}
