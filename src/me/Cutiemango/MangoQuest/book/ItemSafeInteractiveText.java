package me.Cutiemango.MangoQuest.book;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ItemSafeInteractiveText extends InteractiveText{
	public static final int MAX_LENGTH = 65535;
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
		ItemMeta meta = item.getItemMeta();
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
			List<Integer> indextoremove = new ArrayList<Integer>();
			for (String t : temp) {
				index++;
				try {
					int length = t.getBytes("UTF-8").length;
					if(bytelen+length > MAX_LENGTH) {
						indextoremove.add(length);
						break;
					}
				    bytelen+=length;
				} catch (UnsupportedEncodingException e) {
                    indextoremove.add(index);
    				QuestChatManager.logCmd(Level.WARNING, " Cannot get item lore byte length in utf 8");
					e.printStackTrace();
				}

			}
			meta.setLore(temp);
		}
		item.setItemMeta(meta);
		text = TextComponentFactory.convertItemHoverEvent(item, false);
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
		ItemMeta meta = item.getItemMeta();
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
			List<Integer> indextoremove = new ArrayList<Integer>();
			for (String t : temp) {
				index++;
				try {
					int length = t.getBytes("UTF-8").length;
					if(bytelen+length > MAX_LENGTH) {
						indextoremove.add(length);
						break;
					}
				    bytelen+=length;
				} catch (UnsupportedEncodingException e) {
                    indextoremove.add(index);
    				QuestChatManager.logCmd(Level.WARNING, " Cannot get item lore byte length in utf 8");
					e.printStackTrace();
				}

			}
			meta.setLore(temp);
		}
		item.setItemMeta(meta);
		text.addExtra(TextComponentFactory.convertItemHoverEvent(item, false));
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
