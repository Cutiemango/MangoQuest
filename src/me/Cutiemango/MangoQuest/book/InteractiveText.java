package me.Cutiemango.MangoQuest.book;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class InteractiveText
{
	public InteractiveText(TextComponent t) {
		text = t;
	}

	public InteractiveText(String s) {
		this(new TextComponent(TextComponent.fromLegacyText(QuestChatManager.translateColor(s))));
	}

	// similar to showItem
	public InteractiveText(@NotNull ItemStack item) {
		if(!this.getClass().isAssignableFrom(ItemSafeInteractiveText.class)) {
		text = TextComponentFactory.convertItemHoverEvent(item, false);
		}
	}

	private TextComponent text;

	// "/" needed.
	public InteractiveText clickCommand(String cmd) {
		if (!cmd.startsWith("/"))
			cmd = "/" + cmd;
		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
		return this;
	}

	public InteractiveText showItem(@NotNull ItemStack item) {
		text.addExtra(TextComponentFactory.convertItemHoverEvent(item, false));
		return this;
	}

	public InteractiveText showText(String s) {
		text.setHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent(QuestChatManager.translateColor(s)) }));
		return this;
	}

	public InteractiveText showNPCInfo(@NotNull NPC npc) {
		text.addExtra(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getStoredLocation(), false));
		return this;
	}

	// display: quest's displayName
	// hover: "click to view"
	public InteractiveText showQuest(Quest q) {
		text = TextComponentFactory.convertViewQuest(q);
		return this;
	}

	// display: quest's displayName
	// hover: requirement message
	public InteractiveText showRequirement(QuestPlayerData qd, Quest q) {
		text = TextComponentFactory.convertRequirement(qd, q);
		return this;
	}

	public TextComponent get() {
		return text;
	}
}
