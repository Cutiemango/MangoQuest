package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class SimpleQuestObject {
	
    public abstract TextComponent toTextComponent(boolean isFinished);
	
	protected TextComponent toTextComponent(String s, boolean isFinished, Object... args){
		TextComponent text = new TextComponent("");
		String left = s;
		String color = QuestUtil.translateColor("&0");
		if (isFinished)
			color = QuestUtil.translateColor("&8&m&o");
		for (int i = 0; i < args.length; i++){
			String[] split = left.split("%" + i);
			if (split[0].equals(left)){
				text.addExtra(color + left);
				return text;
			}
			if (split.length >= 2)
				left = split[1];
			else
				left = "";
			text.addExtra(color + split[0]);
			
			if (args[i] instanceof ItemStack)
				text.addExtra(TextComponentFactory.convertItemHoverEvent((ItemStack)args[i], isFinished));
			else if (args[i] instanceof Integer)
				text.addExtra(color + Integer.toString((Integer)args[i]));
			else if (args[i] instanceof NPC){
				NPC npc = (NPC)args[i];
				text.addExtra(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getEntity().getLocation(), isFinished));
			}
			else if (args[1] instanceof Material)
				text.addExtra(color + QuestUtil.translate((Material)args[i], (byte)0));
			
			// QuestObjectReachLocation
			else if (args[i] instanceof String){
				if (args.length - 1 > i && args[i+1] instanceof Location){
					text.addExtra(TextComponentFactory.convertLocHoverEvent((String)args[i], (Location)args[i+1], isFinished));
					left.replace("%" + Integer.toString(i+1), "");
				}
				else{
					if (isFinished)
						text.addExtra(color + (String)args[i]);
					else
						text.addExtra(color + QuestUtil.translateColor((String)args[i]));
				}
			}
			
			// QuestObjectKillMob
			else if (args[i] instanceof EntityType)
				text.addExtra(QuestUtil.translate((EntityType)args[i]));
		}
		text.addExtra(color + left);
		return text;
	}

	public abstract String toPlainText();
	
	protected String config;
	protected QuestConversation conv = null;
	
	public String getConfigString(){
		return config;
	}
	
	public void setConfigString(String s){
		config = s;
	}
	
	public QuestConversation getConversation(){
		return conv;
	}
	
	public boolean hasConversation(){
		return !(conv == null);
	}
	
	public void setConversation(QuestConversation qc){
		conv = qc;
	}

}
