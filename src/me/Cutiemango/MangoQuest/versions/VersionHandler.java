package me.Cutiemango.MangoQuest.versions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.md_5.bungee.api.chat.TextComponent;

public interface VersionHandler
{

	void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle);

	void openBook(Player p, TextComponent... texts);

	TextComponent textFactoryConvertLocation(String name, Location loc, boolean isFinished);

	TextComponent textFactoryConvertItem(ItemStack it, boolean f);

	boolean hasTag(Player p, String s);
	
	ItemStack addGUITag(ItemStack is);
	
	boolean hasGUITag(ItemStack is);
	
	void playNPCEffect(Player p, Location location);
}
