package me.Cutiemango.MangoQuest.versions;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.*;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Version_v1_14_R1 implements VersionHandler
{

	@Override
	public void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	{
		if (title == null)
			title = "";
		if (subtitle == null)
			subtitle = "";
		PacketPlayOutTitle ppot = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + QuestChatManager.translateColor(title) + "\"}"), fadeIn, stay, fadeOut);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppot);
		PacketPlayOutTitle subppot = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + QuestChatManager.translateColor(subtitle) + "\"}"), fadeIn, stay, fadeOut);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(subppot);
	}

	@Override
	public void openBook(Player p, TextComponent... texts)
	{
		
		ArrayList<BaseComponent[]> list = new ArrayList<>();
		for (TextComponent t : texts)
		{
			list.add(new BaseComponent[] {t});
		}

		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		CraftMetaBook meta = (CraftMetaBook)book.getItemMeta();

		// Finally to use written API.
		meta.spigot().setPages(list.toArray(new BaseComponent[][] {}));
		meta.setAuthor("MsngoQuest");
		meta.setTitle("MsngoQuest");
		book.setItemMeta(meta);
		
		int slot = p.getInventory().getHeldItemSlot();
		ItemStack old = p.getInventory().getItem(slot);
		p.getInventory().setItem(slot, book);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenBook(EnumHand.MAIN_HAND));
		p.getInventory().setItem(slot, old);
	}

	@Override
	public TextComponent textFactoryConvertLocation(String name, Location loc, boolean isFinished)
	{
		TextComponent t = new TextComponent("");
		ItemStack is = new ItemStack(Material.PAINTING);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		if (loc != null)
			im.setLore(QuestUtil.createList(I18n.locMsg("QuestJourney.NPCLocDisplay", loc.getWorld().getName(), Double.toString(Math.floor(loc.getX())), Double.toString(Math.floor(loc.getY())), Double.toString(Math.floor(loc.getZ())))));
		is.setItemMeta(im);
		if (isFinished)
			t = new TextComponent(QuestChatManager.finishedObjectFormat(name));
		else
			t = new TextComponent(name);

		net.minecraft.server.v1_14_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();

		BaseComponent[] hoverEventComponents = new BaseComponent[]
		{ new TextComponent(itemJson) };
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));

		return t;
	}


	@Override
	public TextComponent textFactoryConvertItem(ItemStack it, boolean finished)
	{
		String base = "";
		ItemStack is = it.clone();
		
		if (is.hasItemMeta() && is.getItemMeta().hasDisplayName())
		{
			if (finished)
				base = QuestChatManager.finishedObjectFormat(QuestUtil.translate(is.getType()));
			else
				base = is.getItemMeta().getDisplayName();
		}
		else
		{
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + QuestUtil.translate(is.getType()));
			is.setItemMeta(im);
			if (finished)
				base = QuestChatManager.finishedObjectFormat(QuestUtil.translate(is.getType()));
			else
				base = ChatColor.BLACK + QuestUtil.translate(is.getType());
		}
		
		TextComponent text = new TextComponent(base);
		net.minecraft.server.v1_14_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();

		BaseComponent[] hoverEventComponents = new BaseComponent[]{ new TextComponent(itemJson) };
		text.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, hoverEventComponents));

		return text;
	}

	@Override
	public boolean hasTag(Player p, String s)
	{
		return ((CraftPlayer) p).getHandle().getScoreboardTags().contains(s);
	}
	
	@Override
	public ItemStack addGUITag(ItemStack item)
	{
		net.minecraft.server.v1_14_R1.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound stag = (nmscopy.hasTag()) ? nmscopy.getTag() : new NBTTagCompound();
		stag.setBoolean("GUIitem", true);
		nmscopy.setTag(stag);
		return CraftItemStack.asBukkitCopy(nmscopy);
	}

	@Override
	public boolean hasGUITag(ItemStack item)
	{
		net.minecraft.server.v1_14_R1.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = (nmscopy.hasTag()) ? nmscopy.getTag() : new NBTTagCompound();
		return tag.hasKey("GUIitem");
	}
	
	@Override
	public void playNPCEffect(Player p, Location location)
	{
		location.setY(location.getY() + 2);
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(Particles.NOTE, false, (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 1, 1);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

}
