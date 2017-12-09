package me.Cutiemango.MangoQuest.objects;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

public class QuestGUIItem
{
	public QuestGUIItem(Material type, int amount, short damage)
	{
		item = new ItemStack(type, amount, damage);
		meta = item.getItemMeta();
	}
	
	public QuestGUIItem(ItemStack is)
	{
		item = is.clone();
		meta = is.getItemMeta();
	}
	
	ItemStack item;
	ItemMeta meta;
	boolean glow;
	
	public ItemStack get()
	{
		item.setItemMeta(meta);
		if (glow)
			item.addUnsafeEnchantment(Enchantment.LURE, 87);
		return setGUIItem(item);
	}
	
	public void setName(String s)
	{
		meta.setDisplayName(QuestChatManager.translateColor(s));
	}
	
	public void setLore(List<String> lore)
	{
		meta.setLore(lore);
	}
	
	public void glowEffect()
	{
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		glow = true;
	}
	
	public static ItemStack setGUIItem(ItemStack item)
	{
		if (!isGUIItem(item))
			return Main.instance.handler.addGUITag(item);
		else return item;
	}
	
	public static boolean isGUIItem(ItemStack item)
	{
		return Main.instance.handler.hasGUITag(item);
	}
}
