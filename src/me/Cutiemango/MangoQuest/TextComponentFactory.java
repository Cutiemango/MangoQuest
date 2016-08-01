package me.Cutiemango.MangoQuest;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Cutiemango.MangoQuest.model.Quest;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.minecraft.server.v1_10_R1.NBTTagCompound;

public class TextComponentFactory {
	
	public static TextComponent registerClickCommandEvent(String text, String command){
		TextComponent t = new TextComponent(QuestUtil.translateColor(text));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return t;
	}
	
	public static TextComponent registerClickCommandEvent(TextComponent t, String command){
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		return t;
	}
	
	public static TextComponent registerHoverStringEvent(String text, String s){
		TextComponent t = new TextComponent(QuestUtil.translateColor(text));
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  new BaseComponent[]{new TextComponent(
				QuestUtil.translateColor(s))}));
		return t;
	}
	
	public static TextComponent registerHoverStringEvent(TextComponent t, String s){
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  new BaseComponent[]{new TextComponent(
				QuestUtil.translateColor(s))}));
		return t;
	}
	
	public static TextComponent convertItemStacktoHoverEvent(boolean f, ItemStack it) {
		TextComponent itemname = new TextComponent();
		ItemStack is = it.clone();
		if (!is.getItemMeta().hasDisplayName()) {
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + QuestUtil.translate(is.getType()));
			is.setItemMeta(im);
			if (f)
				itemname = new TextComponent(QuestUtil.translateColor("&8&m&o") + QuestUtil.translate(is.getType()));
			else
				itemname = new TextComponent(ChatColor.BLACK + QuestUtil.translate(is.getType()));
		} else {
			if (f)
				itemname = new TextComponent(QuestUtil.translateColor("&8&m&o") + QuestUtil.translate(is.getType()));
			else
				itemname = new TextComponent(is.getItemMeta().getDisplayName());
		}
		
		net.minecraft.server.v1_10_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();
		
		BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(itemJson) };
		itemname.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, hoverEventComponents));

		return itemname;
	}
	
	public static TextComponent convertLocationtoHoverEvent(String name, Location loc, boolean isFinished){
		TextComponent t = new TextComponent();
		ItemStack is = new ItemStack(Material.SIGN);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(new String[] { ChatColor.GOLD + "地點位於：",
				ChatColor.WHITE + "- 世界： " + loc.getWorld().getName(), ChatColor.WHITE + "- 座標： ("
						+ Math.floor(loc.getX()) + " , " + loc.getY() + " , " + Math.floor(loc.getZ()) + ")" }));
		is.setItemMeta(im);
		if (isFinished)
			t = new TextComponent(QuestUtil.translateColor("&8&m&o") + ChatColor.stripColor(name));
		else
			t = new TextComponent(name);
		
		net.minecraft.server.v1_10_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();

		BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(itemJson) };
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));

		return t;
	}
	
	public static TextComponent convertViewQuest(Quest q){
		TextComponent t = new TextComponent(ChatColor.BOLD + q.getQuestName());
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  new BaseComponent[]{new TextComponent(QuestUtil.translateColor("&7&l點擊以查看 " + q.getQuestName() + " &7&l的詳細資料"))}));
		t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mq view " + q.getInternalID()));
		return t;
	}

}
