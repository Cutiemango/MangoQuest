package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.inventory.ItemStack;

public abstract class ItemObject extends NumerableObject{

	protected ItemStack item;
	
	public ItemStack getItem(){
		return item;
	}
	
	public void setItem(ItemStack is){
		item = is;
	}

}
