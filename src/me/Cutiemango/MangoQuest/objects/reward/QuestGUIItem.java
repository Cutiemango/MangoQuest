package me.Cutiemango.MangoQuest.objects.reward;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class QuestGUIItem
{
	public QuestGUIItem(Material type, int amount)
	{
		item = new ItemStack(type, amount);
		meta = item.getItemMeta();
	}
	
	public QuestGUIItem(ItemStack is)
	{
		item = is.clone();
		meta = is.getItemMeta();
	}
	
	ItemStack item;
	ItemMeta meta;
	boolean glow, unbreakable;
	
	public ItemStack get()
	{
		if (unbreakable)
			meta.setUnbreakable(true);
		item.setItemMeta(meta);
		if (glow)
			item.addUnsafeEnchantment(Enchantment.LURE, 87);
		return setGUIItem(item);
	}
	
	public void setName(String s)
	{
		if (s == null)
			s = "";
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
	
	public void setUnbreakable()
	{
		unbreakable = true;
	}
	
	public static ItemStack setGUIItem(ItemStack item)
	{
		if (!isGUIItem(item))
			return Main.getInstance().handler.addGUITag(item);
		else return item;
	}
	
	public static boolean isGUIItem(ItemStack item)
	{
		if (item == null || item.getType() == Material.AIR) return false;
		return Main.getInstance().handler.hasGUITag(item);
	}
}
