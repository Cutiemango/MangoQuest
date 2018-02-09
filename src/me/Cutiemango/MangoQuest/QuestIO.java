package me.Cutiemango.MangoQuest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.google.common.base.Charsets;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

public class QuestIO
{

	private File file;
	private FileConfiguration config = new YamlConfiguration();

	public QuestIO(String name, boolean warn, boolean createFile)
	{
		file = new File(Main.getInstance().getDataFolder(), name);

		if (!file.exists())
		{
			if (createFile)
				Main.getInstance().saveResource(name, true);
			else
			{
				try
				{
					file.createNewFile();
					new YamlConfiguration().save(file);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (warn)
				QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.FileNotFound", name));
		}

		loadFrom(file);
	}
	
	public QuestIO(Player p)
	{
		File f = new File(Main.getInstance().getDataFolder() + "/data/" , p.getUniqueId() + ".yml");
		if (!f.exists())
		{
			f.getParentFile().mkdirs();
			try
			{
				f.createNewFile();
				new YamlConfiguration().save(f);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.NewRegister", p.getName()));
		}
		loadFrom(f);
		
		file = f;
	}

	public FileConfiguration getConfig()
	{
		return config;
	}

	public void save()
	{
		try
		{
			config.save(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void loadFrom(File f)
	{
		try
		{
			config.load(new InputStreamReader(new FileInputStream(f), Charsets.UTF_8));
		}
		catch (IOException | InvalidConfigurationException e)
		{
			QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.IOException"));
			e.printStackTrace();
		}
	}

	public Set<String> getSection(String path)
	{
		return config.getConfigurationSection(path).getKeys(false);
	}
	
	public Set<Integer> getIntegerSection(String path)
	{
		Set<Integer> set = new HashSet<>();
		for (String key : config.getConfigurationSection(path).getKeys(false))
		{
			set.add(Integer.parseInt(key));
		}
		return set;
	}

	public void set(String path, Object value)
	{
		config.set(path, value);
	}

	public int getInt(String path)
	{
		return config.getInt(path);
	}

	public double getDouble(String path)
	{
		return config.getDouble(path);
	}

	public long getLong(String path)
	{
		return config.getLong(path);
	}

	public boolean getBoolean(String path)
	{
		return config.getBoolean(path);
	}

	public String getString(String path)
	{
		return config.getString(path);
	}

	public List<String> getStringList(String path)
	{
		return config.getStringList(path);
	}
	
	public ItemStack getItemStack(String path)
	{
		return config.getItemStack(path);
	}

	public boolean contains(String path)
	{
		return config.contains(path);
	}

	public boolean isSection(String path)
	{
		return config.isConfigurationSection(path);
	}
	
	public void removeSection(String path){
		config.set(path, "");
		return;
	}

}
