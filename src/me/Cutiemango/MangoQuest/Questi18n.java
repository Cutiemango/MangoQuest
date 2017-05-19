package me.Cutiemango.MangoQuest;

import java.util.Locale;
import java.util.ResourceBundle;

public class Questi18n {
	
	private static ResourceBundle bundle = null;
	
	public static void init(String locale){
		String[] s = locale.split("_");
		bundle = ResourceBundle.getBundle("messages" , new Locale(s[0], s[1]));
	}
	
	public static String localizeMessage(String path){
		String format = bundle.getString(path);
		format = QuestUtil.translateColor(format);
		if (format == null)
			return path;
		else
			return format;
	}
	
	public static String localizeMessage(String path, String... args){
		String format = bundle.getString(path);
		if (format == null)
			return path;
		format = QuestUtil.translateColor(format);
		if (format.contains("%")){
			try{
				for (int arg = 0; arg < args.length; arg++){
					format = format.replace("[%" + arg + "]", args[arg]);
				}
				return format;
			}catch(Exception e){
				QuestUtil.warnCmd("An error occured whilest localizing " + path + " .");
				e.printStackTrace();
			}
		}
		return format;
	}

}
