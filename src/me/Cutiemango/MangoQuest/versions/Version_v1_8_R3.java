package me.Cutiemango.MangoQuest.versions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import io.netty.buffer.Unpooled;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class Version_v1_8_R3 implements VersionHandler
{

	@Override
	public void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	{
		if (title != null)
		{
			PacketPlayOutTitle ppot = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + QuestChatManager.translateColor(title) + "\"}"), fadeIn, stay, fadeOut);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppot);
		}
		
		if (subtitle != null)
		{
			PacketPlayOutTitle ppot = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + QuestChatManager.translateColor(subtitle) + "\"}"), fadeIn, stay, fadeOut);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppot);
		}
	}


	@Override
	public void openBook(Player p, TextComponent... texts)
	{
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		net.minecraft.server.v1_8_R3.ItemStack nmsbook = CraftItemStack.asNMSCopy(book);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList taglist = new NBTTagList();

		for (TextComponent t : texts)
		{
			taglist.add(new NBTTagString(ComponentSerializer.toString(t)));
		}
		tag.set("pages", taglist);
		nmsbook.setTag(tag);

		book = CraftItemStack.asBukkitCopy(nmsbook);

		int slot = p.getInventory().getHeldItemSlot();
		ItemStack old = p.getInventory().getItem(slot);
		p.getInventory().setItem(slot, book);
		PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(Unpooled.buffer()));
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		p.getInventory().setItem(slot, old);
	}

	@Override
	public TextComponent textFactoryConvertLocation(String name, Location loc, boolean isFinished)
	{
		TextComponent t = new TextComponent();
		ItemStack is = new ItemStack(Material.SIGN);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		if (loc != null)
			im.setLore(QuestUtil.createList(I18n.locMsg("QuestJourney.NPCLocDisplay", loc.getWorld().getName(), Double.toString(Math.floor(loc.getX())), Double.toString(Math.floor(loc.getY())), Double.toString(Math.floor(loc.getZ())))));
		is.setItemMeta(im);
		if (isFinished)
			t = new TextComponent(QuestChatManager.translateColor("&8&m&o") + ChatColor.stripColor(name));
		else
			t = new TextComponent(name);

		net.minecraft.server.v1_8_R3.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();

		BaseComponent[] hoverEventComponents = new BaseComponent[]
		{ new TextComponent(itemJson) };
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));

		return t;
	}

	@Override
	public TextComponent textFactoryConvertItem(ItemStack it, boolean f)
	{
		TextComponent itemname = new TextComponent();
		ItemStack is = it.clone();
		if (!is.getItemMeta().hasDisplayName())
		{
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + QuestUtil.translate(is.getType(), it.getDurability()));
			is.setItemMeta(im);
			if (f)
				itemname = new TextComponent(QuestChatManager.translateColor("&8&m&o") + QuestUtil.translate(is.getType(), is.getDurability()));
			else
				itemname = new TextComponent(ChatColor.BLACK + QuestUtil.translate(is.getType(), is.getDurability()));
		}
		else
		{
			if (f)
				itemname = new TextComponent(QuestChatManager.translateColor("&8&m&o") + QuestUtil.translate(is.getType(), is.getDurability()));
			else
				itemname = new TextComponent(is.getItemMeta().getDisplayName());
		}

		net.minecraft.server.v1_8_R3.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();

		BaseComponent[] hoverEventComponents = new BaseComponent[]
		{ new TextComponent(itemJson) };
		itemname.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, hoverEventComponents));

		return itemname;
	}

	// P() is not visible
	// return ((CraftPlayer)p).getHandle().P().contains(s);
	@Override
	public boolean hasTag(Player p, String s)
	{
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItemInMainHand(Player p)
	{
		return p.getItemInHand();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void setItemInMainHand(Player p, ItemStack is)
	{
		p.setItemInHand(is);
	}
	
	@Override
	public ItemStack addGUITag(ItemStack item)
	{
		net.minecraft.server.v1_8_R3.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound stag = (nmscopy.hasTag()) ? nmscopy.getTag() : new NBTTagCompound();
		stag.setBoolean("GUIitem", true);
		nmscopy.setTag(stag);
		return CraftItemStack.asBukkitCopy(nmscopy);
	}

	@Override
	public boolean hasGUITag(ItemStack item)
	{
		net.minecraft.server.v1_8_R3.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = (nmscopy.hasTag()) ? nmscopy.getTag() : new NBTTagCompound();
		return tag.hasKey("GUIitem");
	}
	
	@Override
	public void playNPCEffect(Player p, Location location)
	{
		location.setY(location.getY() + 2);
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.NOTE, false, (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 1, 1, null);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

}
