package me.Cutiemango.MangoQuest.questobjects;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.book.TextComponentFactory;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class SimpleQuestObject
{

	public abstract TextComponent toTextComponent(boolean isFinished);

	public static HashMap<String, String> ALL_OBJECTS = new HashMap<>();

	public static void initObjectNames()
	{
		ALL_OBJECTS.put("BREAK_BLOCK", Questi18n.localizeMessage("QuestObjectName.BreakBlock"));
		ALL_OBJECTS.put("CONSUME_ITEM", Questi18n.localizeMessage("QuestObjectName.ConsumeItem"));
		ALL_OBJECTS.put("DELIVER_ITEM", Questi18n.localizeMessage("QuestObjectName.DeliverItem"));
		ALL_OBJECTS.put("KILL_MOB", Questi18n.localizeMessage("QuestObjectName.KillMob"));
		ALL_OBJECTS.put("REACH_LOCATION", Questi18n.localizeMessage("QuestObjectName.ReachLocation"));
		ALL_OBJECTS.put("TALK_TO_NPC", Questi18n.localizeMessage("QuestObjectName.TalkToNPC"));
	}

	protected TextComponent toTextComponent(String s, boolean isFinished, Object... args)
	{
		TextComponent text = new TextComponent("");
		s = s.replace("[", "").replace("]", "");
		String left = s;
		String color = QuestChatManager.translateColor("&0");

		Material block = null;
		Short subID = 0;
		if (isFinished)
			color = QuestChatManager.translateColor("&8&m&o");
		for (int i = 0; i < args.length; i++)
		{
			String[] split = left.split("%" + i);
			if (split.length != 0)
			{
				if (split[0].equals(left))
				{
					text.addExtra(color + left);
					return text;
				}
				if (split.length >= 2)
					left = split[1];
				else
					left = "";
				text.addExtra(color + split[0]);
			}
			else
				left = "";

			if (args[i] instanceof ItemStack)
				text.addExtra(TextComponentFactory.convertItemHoverEvent((ItemStack) args[i], isFinished));
			else
				if (args[i] instanceof Integer)
					text.addExtra(color + Integer.toString((Integer) args[i]));
				else
					if (args[i] instanceof NPC)
					{
						NPC npc = (NPC) args[i];
						text.addExtra(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getEntity().getLocation(), isFinished));
					}
					else
						if (args[i] instanceof Material)
							block = (Material) args[i];
						else
							if (args[i] instanceof Short)
								subID = (short) args[i];
							// QuestObjectReachLocation
							else
								if (args[i] instanceof String)
								{
									if (args.length - 1 > i && args[i + 1] instanceof Location)
									{
										text.addExtra(
												TextComponentFactory.convertLocHoverEvent((String) args[i], (Location) args[i + 1], isFinished));
										left.replace("%" + Integer.toString(i + 1), "");
									}
									else
									{
										if (isFinished)
											text.addExtra(color + (String) args[i]);
										else
											text.addExtra(color + QuestChatManager.translateColor((String) args[i]));
									}
								}

								// QuestObjectKillMob
								else
									if (args[i] instanceof EntityType)
										text.addExtra(QuestUtil.translate((EntityType) args[i]));
		}
		if (block != null)
			text.addExtra(color + QuestUtil.translate(block, subID));
		text.addExtra(color + left);
		return text;
	}

	public abstract String toPlainText();

	protected String config;
	protected QuestConversation conv = null;

	public String getConfigString()
	{
		return config;
	}

	public String getObjectName()
	{
		return ALL_OBJECTS.get(config);
	}

	public QuestConversation getConversation()
	{
		return conv;
	}

	public boolean hasConversation()
	{
		return !(conv == null);
	}

	public void setConversation(QuestConversation qc)
	{
		conv = qc;
	}

}
