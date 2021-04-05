package me.Cutiemango.MangoQuest.questobject;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.TextComponentFactory;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public abstract class SimpleQuestObject
{
	public static HashMap<String, String> ALL_OBJECTS = new HashMap<>();

	public static void initObjectNames() {
		ALL_OBJECTS.put("BREAK_BLOCK", I18n.locMsg("QuestObjectName.BreakBlock"));
		ALL_OBJECTS.put("CONSUME_ITEM", I18n.locMsg("QuestObjectName.ConsumeItem"));
		ALL_OBJECTS.put("DELIVER_ITEM", I18n.locMsg("QuestObjectName.DeliverItem"));
		ALL_OBJECTS.put("KILL_MOB", I18n.locMsg("QuestObjectName.KillMob"));
		ALL_OBJECTS.put("REACH_LOCATION", I18n.locMsg("QuestObjectName.ReachLocation"));
		ALL_OBJECTS.put("TALK_TO_NPC", I18n.locMsg("QuestObjectName.TalkToNPC"));
		ALL_OBJECTS.put("FISHING", I18n.locMsg("QuestObjectName.Fishing"));
	}

	/**
	 * This is used for a TextComponent version of toDisplayText.
	 * Mainly displayed on book.
	 */
	protected TextComponent toTextComponent(String s, boolean isFinished, Object... args) {
		TextComponent text = new TextComponent("");
		s = s.replace("[", "").replace("]", "");
		String left = s;
		String color = QuestChatManager.translateColor("&0");

		Material block = null;
		if (isFinished)
			color = QuestChatManager.translateColor("&8&m&o");
		for (int i = 0; i < args.length; i++) {
			String[] split = left.split("%" + i);
			if (split.length != 0) {
				if (split[0].equals(left)) {
					text.addExtra(color + left);
					return text;
				}
				if (split.length >= 2)
					left = split[1];
				else
					left = "";
				text.addExtra(color + split[0]);
			} else
				left = "";

			if (args[i] instanceof ItemStack)
				text.addExtra(TextComponentFactory.convertItemHoverEvent((ItemStack) args[i], isFinished));
			else if (args[i] instanceof Integer)
				text.addExtra(color + args[i]);
			else if (args[i] instanceof NPC) {
				NPC npc = (NPC) args[i];
				if (npc == null || npc.getEntity() == null)
					text.addExtra(I18n.locMsg("Translation.UnknownNPC"));
				else
					text.addExtra(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getEntity().getLocation(), isFinished));
			} else if (args[i] instanceof Material)
				block = (Material) args[i];
			else
				// QuestObjectReachLocation
				if (args[i] instanceof String) {
					if (args.length - 1 > i && args[i + 1] instanceof Location) {
						text.addExtra(TextComponentFactory.convertLocHoverEvent((String) args[i], (Location) args[i + 1], isFinished));
						left = left.replace("%" + (i + 1), "");
					} else {
						if (isFinished)
							text.addExtra(color + args[i]);
						else
							text.addExtra(color + QuestChatManager.translateColor((String) args[i]));
					}
				}
				// QuestObjectKillMob
				else if (args[i] instanceof EntityType)
					text.addExtra(QuestUtil.translate((EntityType) args[i]));
		}
		if (block != null)
			text.addExtra(color + QuestUtil.translate(block));
		text.addExtra(color + left);
		return text;
	}

	/**
	 * This is used for converting objects into text in order to display in chat.
	 *
	 * @return e.g. Talk to [%0] ([%0] represents the NPC's name)
	 */
	public abstract String toDisplayText();

	public abstract boolean load(QuestIO config, String path);

	public abstract void save(QuestIO config, String objpath);

	public abstract String getConfigString();

	public abstract String getObjectName();

	public abstract TextComponent toTextComponent(boolean isFinished);

	protected String activateConversation = null;

	public String toPlainText() {
		return ChatColor.stripColor(toDisplayText());
	}

	public QuestConversation getConversation() {
		return ConversationManager.getConversation(activateConversation);
	}

	public boolean hasConversation() {
		return ConversationManager.getConversation(activateConversation) != null;
	}

	public void setConversation(String s) {
		activateConversation = s;
	}

}
