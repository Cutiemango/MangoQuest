package me.Cutiemango.MangoQuest.versions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.TextComponent;

public abstract interface QuestVersionHandler {
	
	public abstract void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut,
			String title, String subtitle);
	
	public abstract void openBook(Player p, TextComponent... texts);
	
	public abstract TextComponent textFactoryConvertLocation(String name, Location loc, boolean isFinished);
	
	public abstract TextComponent textFactoryConvertItem(ItemStack it, boolean f);
	
	public abstract boolean hasTag(Player p, String s);
	
	public abstract ItemStack getItemInMainHand(Player p);
}
