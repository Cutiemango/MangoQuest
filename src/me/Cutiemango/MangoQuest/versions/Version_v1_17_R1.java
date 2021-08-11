package me.Cutiemango.MangoQuest.versions;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftMetaBook;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.PacketPlayOutOpenBook;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import net.minecraft.world.EnumHand;

public class Version_v1_17_R1 implements VersionHandler {

	@Override
	public void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		CraftPlayer cp = (CraftPlayer) p;
		ClientboundSetTitlesAnimationPacket times = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
		cp.getHandle().b.sendPacket(times);
		if (title != null) {
			ClientboundSetTitleTextPacket packetTitle = new ClientboundSetTitleTextPacket(
					CraftChatMessage.fromStringOrNull(QuestChatManager.translateColor(title)));
			(cp.getHandle()).b.sendPacket(packetTitle);
		}
		if (subtitle != null) {
			ClientboundSetSubtitleTextPacket packetSubtitle = new ClientboundSetSubtitleTextPacket(
					CraftChatMessage.fromStringOrNull(QuestChatManager.translateColor(subtitle)));
			(cp.getHandle()).b.sendPacket(packetSubtitle);
		}
	}

	@Override
	public void openBook(Player p, TextComponent... texts) {
		ArrayList<BaseComponent[]> list = new ArrayList<>();
		for (TextComponent t : texts)
			list.add(new BaseComponent[] { t });

		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		CraftMetaBook meta = (CraftMetaBook) book.getItemMeta();

		meta.spigot().setPages(list.toArray(new BaseComponent[][] {}));
		meta.setAuthor("MangoQuest");
		meta.setTitle("MangoQuest");
		book.setItemMeta(meta);

		int slot = p.getInventory().getHeldItemSlot();
		ItemStack old = p.getInventory().getItem(slot);
		p.getInventory().setItem(slot, book);
		((CraftPlayer) p).getHandle().b.sendPacket(new PacketPlayOutOpenBook(EnumHand.a));
		p.getInventory().setItem(slot, old);
	}

	@Override
	public TextComponent textFactoryConvertLocation(String name, Location loc, boolean isFinished) {
		if (loc == null)
			return new TextComponent("");

		ItemStack is = new ItemStack(Material.PAINTING);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);

		String displayMsg = I18n.locMsg("QuestJourney.NPCLocDisplay", loc.getWorld().getName(),
				Integer.toString(loc.getBlockX()), Integer.toString(loc.getBlockY()),
				Integer.toString(loc.getBlockZ()));

		im.setLore(QuestUtil.createList(displayMsg));

		is.setItemMeta(im);
		TextComponent text = new TextComponent(isFinished ? QuestChatManager.finishedObjectFormat(name) : name);

		ItemTag itemTag = ItemTag.ofNbt(CraftItemStack.asNMSCopy(is).getTag().asString());
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
				Arrays.asList(new net.md_5.bungee.api.chat.hover.content.Item(is.getType().getKey().toString(), is.getAmount(), itemTag))));
		return text;
	}

	/**
	 * displayText = the real text displayed hoverItem = the hover item
	 */
	@Override
	public TextComponent textFactoryConvertItem(final ItemStack item, boolean finished) {
		String displayText = QuestUtil.translate(item);

		if (finished)
			displayText = QuestChatManager.finishedObjectFormat(displayText);
		else
			displayText = ChatColor.BLACK + displayText;

		TextComponent text = new TextComponent(displayText);
		if (item != null) {
			NBTTagCompound tag = CraftItemStack.asNMSCopy(item).getTag();
			if (tag == null)
				return text;
			ItemTag itemTag = ItemTag.ofNbt(tag.asString());
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
					new net.md_5.bungee.api.chat.hover.content.Item(item.getType().getKey().toString(), item.getAmount(), itemTag)));
		}
		return text;
	}

	@Override
	public boolean hasTag(Player p, String s) {
		return ((CraftPlayer) p).getHandle().getScoreboardTags().contains(s);
	}

	@Override
	public ItemStack addGUITag(ItemStack item) {
		net.minecraft.world.item.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound stag = (nmscopy.hasTag()) ? nmscopy.getTag() : new NBTTagCompound();
		stag.setBoolean("GUIitem", true);
		nmscopy.setTag(stag);
		return CraftItemStack.asBukkitCopy(nmscopy);
	}

	@Override
	public boolean hasGUITag(ItemStack item) {
		net.minecraft.world.item.ItemStack  nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = (nmscopy.hasTag()) ? nmscopy.getTag() : new NBTTagCompound();
		return tag.hasKey("GUIitem");
	}

	@Override
	public void playNPCEffect(Player p, Location location) {
		location.setY(location.getY() + 2);
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(Particles.R, false,
				(float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 1, 1);
		((CraftPlayer) p).getHandle().b.sendPacket(packet);
	}
}
