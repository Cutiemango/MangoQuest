package me.Cutiemango.MangoQuest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class QuestIO {
	
	private File file;
	private FileConfiguration config;
	
	public QuestIO(String name){
		File f = new File(Main.instance.getDataFolder(), name);
		
		if (!f.exists()){
			Main.instance.saveResource(name, true);
			Bukkit.getLogger().log(Level.SEVERE, "[MangoQuest] 找不到" + name + "，建立新檔案！");
		}
		
		loadFrom(f);
	}
	
	public FileConfiguration getConfig(){
		return config;
	}

	public void save(){
		try{
			config.save(file);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void loadFrom(File f){
		file = f;
		config = YamlConfiguration.loadConfiguration(f);
	}
	
	public Set<String> getSection(String path){
		return config.getConfigurationSection(path).getKeys(false);
	}
	
	public void set(String path, Object value){
		config.set(path, value);
	}
	
	public int getInt(String path){
		return config.getInt(path);
	}
	
	public double getDouble(String path){
		return config.getDouble(path);
	}
	
	public long getLong(String path){
		return config.getLong(path);
	}
	
	public boolean getBoolean(String path){
		return config.getBoolean(path);
	}
	
	public String getString(String path){
		return config.getString(path);
	}
	
	public List<String> getStringList(String path){
		return config.getStringList(path);
	}

	public boolean contains(String path){
		return config.contains(path);
	}
	
	public boolean isSection(String path){
		return config.isConfigurationSection(path);
	}
}
