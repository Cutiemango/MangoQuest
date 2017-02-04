package me.Cutiemango.MangoQuest.versions;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.netty.buffer.Unpooled;
import me.Cutiemango.MangoQuest.QuestUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_11_R1.EnumHand;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.NBTTagString;
import net.minecraft.server.v1_11_R1.PacketDataSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction;

public class Version_v1_11_R1 implements QuestVersionHandler{

	@Override
	public void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		if (title != null) {
			title = ChatColor.translateAlternateColorCodes('&', title);
			PacketPlayOutTitle ppot = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + title + "\"}"), fadeIn, stay, fadeOut);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppot);
		}
		if (subtitle != null) {
			subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
			PacketPlayOutTitle ppot = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"), fadeIn, stay, fadeOut);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppot);
		}
	}

	@Override
	public void openBook(Player p, TextComponent... texts) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		net.minecraft.server.v1_11_R1.ItemStack nmsbook = CraftItemStack.asNMSCopy(book);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList taglist = new NBTTagList();

		for (TextComponent t : texts){
			taglist.add(new NBTTagString(ComponentSerializer.toString(t)));
		}
		
		tag.set("pages", taglist);
		nmsbook.setTag(tag);
		
		book = CraftItemStack.asBukkitCopy(nmsbook);
		
		int slot = p.getInventory().getHeldItemSlot();
		ItemStack old = p.getInventory().getItem(slot);
		p.getInventory().setItem(slot, book);
		PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());
		packetdataserializer.a(EnumHand.MAIN_HAND);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", packetdataserializer));
		p.getInventory().setItem(slot, old);
	}

	@Override
	public TextComponent textFactoryConvertLocation(String name, Location loc, boolean isFinished) {
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
		
		net.minecraft.server.v1_11_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();

		BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(itemJson) };
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));

		return t;
	}

	@Override
	public TextComponent textFactoryConvertItem(ItemStack it, boolean f) {
		TextComponent itemname = new TextComponent();
		ItemStack is = it.clone();
		if (!is.getItemMeta().hasDisplayName()) {
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + QuestUtil.translate(is.getType() , it.getDurability()));
			is.setItemMeta(im);
			if (f)
				itemname = new TextComponent(QuestUtil.translateColor("&8&m&o") + QuestUtil.translate(is.getType(), is.getDurability()));
			else
				itemname = new TextComponent(ChatColor.BLACK + QuestUtil.translate(is.getType(), is.getDurability()));
		} else {
			if (f)
				itemname = new TextComponent(QuestUtil.translateColor("&8&m&o") + QuestUtil.translate(is.getType(), is.getDurability()));
			else
				itemname = new TextComponent(is.getItemMeta().getDisplayName());
		}
		
		net.minecraft.server.v1_11_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();
		
		BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(itemJson) };
		itemname.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, hoverEventComponents));
		
		return itemname;
	}
	
	@Override
	public boolean hasTag(Player p, String s){
		return ((CraftPlayer)p).getHandle().P().contains(s);
	}
	
	@Override
	public ItemStack getItemInMainHand(Player p) {
		return p.getInventory().getItemInMainHand();
	}

}