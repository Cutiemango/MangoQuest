package me.Cutiemango.MangoQuest.versions;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.minecraft.server.v1_16_R3.EnumHand;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenBook;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R3.Particles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Version_v1_16_R3 implements VersionHandler
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
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenBook(EnumHand.MAIN_HAND));
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

		ItemTag itemTag = ItemTag.ofNbt(CraftItemStack.asNMSCopy(is).getTag().asString());
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(is.getType().getKey().toString(), is.getAmount(), itemTag)));
		return text;
	}

	/**
	 * displayText = the real text displayed
	 * hoverItem = the hover item
	 */
	@Override
	public TextComponent textFactoryConvertItem(final ItemStack item, boolean finished)
	{
		String displayText = QuestUtil.translate(item);

		if (finished)
			displayText = QuestChatManager.finishedObjectFormat(displayText);
		else
			displayText = ChatColor.BLACK + displayText;

		TextComponent text = new TextComponent(displayText);
		if (item != null)
		{
			NBTTagCompound tag = CraftItemStack.asNMSCopy(item).getTag();
			if (tag == null)
				return text;
			ItemTag itemTag = ItemTag.ofNbt(tag.asString());
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().toString(), item.getAmount(), itemTag)));
		}
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
		net.minecraft.server.v1_16_R3.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound stag = (nmscopy.hasTag()) ? nmscopy.getTag() : new NBTTagCompound();
		stag.setBoolean("GUIitem", true);
		nmscopy.setTag(stag);
		return CraftItemStack.asBukkitCopy(nmscopy);
	}

	@Override
	public boolean hasGUITag(ItemStack item)
	{
		net.minecraft.server.v1_16_R3.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
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
