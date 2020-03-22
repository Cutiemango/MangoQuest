package me.Cutiemango.MangoQuest;

import me.Cutiemango.MangoQuest.manager.QuestChatManager;

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

public class I18n
{

	private static ResourceBundle bundle;

	public static void init(Locale local, boolean useCustom)
	{
		try
		{
			if (bundle != null)
				ResourceBundle.clearCache();
			
			String properties = "messages_" + ConfigSettings.LOCALE_USING.toString() + ".properties";
			String langPath = "lang" + File.separator + "original_" + ConfigSettings.LOCALE_USING.toString() + ".yml";
			
			if (useCustom)
			{
				QuestChatManager.logCmd(Level.INFO, "Using custom locale.");
				if (!new File(Main.getInstance().getDataFolder() + File.separator + langPath).exists())
					Main.getInstance().saveResource(langPath, true);
				if (!new File(Main.getInstance().getDataFolder() + File.separator + properties).exists())
					Main.getInstance().saveResource(properties, true);
			}
			else
			{
				QuestChatManager.logCmd(Level.INFO, "Using default locale.");
				Main.getInstance().saveResource(properties, true);
				Main.getInstance().saveResource(langPath, true);
			}
			bundle = ResourceBundle.getBundle("messages", local, new FileResClassLoader(I18n.class.getClassLoader(), Main.getInstance()));
		}
		catch (MissingResourceException e)
		{
			QuestChatManager.logCmd(Level.WARNING, "The plugin encountered an error during initializing the i18n file.");
			e.printStackTrace();
		}
	}

	public static String locMsg(String path)
	{
		String format = bundle.getString(path);
		format = QuestChatManager.translateColor(format);
		return format;
	}

	public static String locMsg(String path, String... args)
	{
		String format = bundle.getString(path);
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
