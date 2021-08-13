package me.Cutiemango.MangoQuest.book;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ItemSafeInteractiveText extends InteractiveText{
	private TextComponent text;
	
	public ItemSafeInteractiveText(TextComponent t) {
		super(t);
		text = t;
	}

	public ItemSafeInteractiveText(String s) {
		this(new TextComponent(TextComponent.fromLegacyText(QuestChatManager.translateColor(s))));
	}

	// similar to showItem
	public ItemSafeInteractiveText(@NotNull ItemStack item) {
		super(item);
		int bytelen = 0;
		ItemStack itemclone = item.clone();
		ItemMeta meta = itemclone.getItemMeta();
		if (meta.hasDisplayName()) {
			try {
				bytelen += meta.getDisplayName().getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				QuestChatManager.logCmd(Level.WARNING, " Cannot get item display name byte length in utf 8");
				e.printStackTrace();
			}
		}
		if (meta.hasLore()) {
			List<String> temp = meta.getLore();
			int index = -1;
			for (String t : temp) {
				index++;
			
				try {
					int length = t.getBytes("UTF-8").length;
					if(bytelen+length > ConfigSettings.MAX_LENGTH) {
						index--;
						break;
					}
				    bytelen+=length;
				} catch (UnsupportedEncodingException e) {
    				QuestChatManager.logCmd(Level.WARNING, " Cannot get item lore byte length in utf 8");
					e.printStackTrace();
				}
			}
			temp = temp.subList(0, index);
			temp.add(ChatColor.translateAlternateColorCodes('&', "&2..................."));
			meta.setLore(temp);
		}
		itemclone.setItemMeta(meta);
		text = TextComponentFactory.convertItemHoverEvent(itemclone, false);
	}



	// "/" needed.
	public ItemSafeInteractiveText clickCommand(String cmd) {
		if (!cmd.startsWith("/"))
			cmd = "/" + cmd;
		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
		return this;
	}

	public ItemSafeInteractiveText showItem(@NotNull ItemStack item) {
		int bytelen = 0;
		ItemStack itemclone = item.clone();
		ItemMeta meta = itemclone.getItemMeta();
		if (meta.hasDisplayName()) {
			try {
				bytelen += meta.getDisplayName().getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				QuestChatManager.logCmd(Level.WARNING, " Cannot get item display name byte length in utf 8");
				e.printStackTrace();
			}
		}
		if (meta.hasLore()) {
			List<String> temp = meta.getLore();
			int index = -1;
			for (String t : temp) {
				index++;
			
				try {
					int length = t.getBytes("UTF-8").length;
					if(bytelen+length > ConfigSettings.MAX_LENGTH) {

						index--;
						break;
					}
				    bytelen+=length;
				} catch (UnsupportedEncodingException e) {
    				QuestChatManager.logCmd(Level.WARNING, " Cannot get item lore byte length in utf 8");
					e.printStackTrace();
				}
				

			}
			temp = temp.subList(0, index);
			meta.setLore(temp);
			
		}

		itemclone.setItemMeta(meta);
		text = TextComponentFactory.convertItemHoverEvent(itemclone, false);
		return this;
	}

	public ItemSafeInteractiveText showText(String s) {
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new BaseComponent[] { new TextComponent(QuestChatManager.translateColor(s)) }));
		return this;
	}

	public ItemSafeInteractiveText showNPCInfo(@NotNull NPC npc) {
		text.addExtra(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getStoredLocation(), false));
		return this;
	}

	// display: quest's displayName
	// hover: "click to view"
	public ItemSafeInteractiveText showQuest(Quest q) {
		text = TextComponentFactory.convertViewQuest(q);
		return this;
	}

	// display: quest's displayName
	// hover: requirement message
	public ItemSafeInteractiveText showRequirement(QuestPlayerData qd, Quest q) {
		text = TextComponentFactory.convertRequirement(qd, q);
		return this;
	}

	public TextComponent get() {
		return text;
	}
}
