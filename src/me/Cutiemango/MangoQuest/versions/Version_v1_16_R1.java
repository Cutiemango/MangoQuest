package me.Cutiemango.MangoQuest.versions;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R1.Particles;
import net.minecraft.server.v1_16_R1.EnumHand;
import net.minecraft.server.v1_16_R1.PacketPlayOutOpenBook;
import net.minecraft.server.v1_16_R1.IChatBaseComponent;
import net.minecraft.server.v1_16_R1.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftMetaBook;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Version_v1_16_R1 implements VersionHandler
{

	@Override
	public void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
	{
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + QuestChatManager
				.translateColor(title == null ? "" : title) + "\"}"), fadeIn, stay, fadeOut));
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + QuestChatManager.translateColor(subtitle == null ? "" : subtitle) + "\"}"), fadeIn, stay, fadeOut));
	}

	@Override
	public void openBook(Player p, TextComponent... texts)
	{
		ArrayList<BaseComponent[]> list = new ArrayList<>();
		for (TextComponent t : texts)
			list.add(new BaseComponent[] {t});

		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		CraftMetaBook meta = (CraftMetaBook)book.getItemMeta();

		meta.spigot().setPages(list.toArray(new BaseComponent[][]{}));
		meta.setAuthor("MangoQuest");
		meta.setTitle("MangoQuest");
		book.setItemMeta(meta);

		int slot = p.getInventory().getHeldItemSlot();
		ItemStack old = p.getInventory().getItem(slot);
		p.getInventory().setItem(slot, book);
		((org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenBook(EnumHand.MAIN_HAND));
		p.getInventory().setItem(slot, old);
	}

	@Override
	public TextComponent textFactoryConvertLocation(String name, Location loc, boolean isFinished)
	{
		if (loc == null)
			return new TextComponent("");

		ItemStack is = new ItemStack(Material.PAINTING);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);

		String displayMsg = I18n.locMsg("QuestJourney.NPCLocDisplay",
				loc.getWorld().getName(),
				Integer.toString(loc.getBlockX()),
				Integer.toString(loc.getBlockY()),
				Integer.toString(loc.getBlockZ()));

		im.setLore(QuestUtil.createList(displayMsg));

		is.setItemMeta(im);
		TextComponent text = new TextComponent(isFinished ? QuestChatManager.finishedObjectFormat(name) : name);

		net.minecraft.server.v1_16_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());

		BaseComponent[] hoverEventComponents = new BaseComponent[]{ new TextComponent(tag.toString()) };
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));
		return text;
	}

	/**
	 * displayText = the real text displayed
	 * hoverItem = the hover item
	 */
	@Override
	public TextComponent textFactoryConvertItem(final ItemStack is, boolean finished)
	{
		String displayText = is.hasItemMeta() && is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : QuestUtil.translate(is.getType());

		if (finished)
			displayText = QuestChatManager.finishedObjectFormat(displayText);
		else
			displayText = ChatColor.BLACK + displayText;

		TextComponent text = new TextComponent(displayText);

		net.minecraft.server.v1_16_R1.ItemStack i = CraftItemStack.asNMSCopy(is);
		NBTTagCompound tag = i.save(new NBTTagCompound());
		String itemJson = tag.toString();

		BaseComponent[] hoverEventComponents = new BaseComponent[]{ new TextComponent(itemJson) };
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));

		return text;
	}

	@Override
	public boolean hasTag(Player p, String s)
	{
		return ((org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer) p).getHandle().getScoreboardTags().contains(s);
	}

	@Override
	public ItemStack addGUITag(ItemStack item)
	{
		net.minecraft.server.v1_16_R1.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound stag = (nmscopy.hasTag()) ? nmscopy.getTag() : new NBTTagCompound();
		stag.setBoolean("GUIitem", true);
		nmscopy.setTag(stag);
		return CraftItemStack.asBukkitCopy(nmscopy);
	}

	@Override
	public boolean hasGUITag(ItemStack item)
	{
		net.minecraft.server.v1_16_R1.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = (nmscopy.hasTag()) ? nmscopy.getTag() : new NBTTagCompound();
		return tag.hasKey("GUIitem");
	}

	@Override
	public void playNPCEffect(Player p, Location location)
	{
		location.setY(location.getY() + 2);
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(Particles.NOTE, false, (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 1, 1);
		((org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
}
