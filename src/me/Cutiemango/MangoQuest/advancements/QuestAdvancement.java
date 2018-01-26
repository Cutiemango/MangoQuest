package me.Cutiemango.MangoQuest.advancements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.minecraft.server.v1_12_R1.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;
import java.util.logging.Level;

/**
 * @author charliej - the very API
 * @author DiscowZombie - adopting for Builder-Pattern
 * @author 2008Choco - NamespacedKey support
 * @author GiansCode - small but useful changes
 * @author Ste3et_C0st - add/take advancement logic
 * @author PROgrammer_JARvis - rework and combining
 * @author ysl3000 - useful advice and bug-tracking at PullRequests
 */
public class QuestAdvancement
{
	private NamespacedKey id;
	private short iconsubID = 0;
	private String title = "Untitled", parent, icon = "minecraft:golden_apple", description = "no description",
			background = "minecraft:textures/gui/advancements/backgrounds/stone.png";
	private boolean announce = false, toast = true;
	private FrameType frame = FrameType.TASK;
	private Set<Trigger> triggers;

	public QuestAdvancement(NamespacedKey id)
	{
		this.id = id;
		triggers = new HashSet<>();

	}

	public static QuestAdvancement build(NamespacedKey id)
	{
		QuestAdvancement advancement = new QuestAdvancement(id);
		QuestAdvancementManager.advancements.put(id, advancement);
		return advancement;
	}

	public static QuestAdvancement build(JavaPlugin plugin, String id)
	{
		return build(new NamespacedKey(plugin, id));
	}

	public QuestAdvancement build()
	{
		QuestAdvancementManager.advancements.put(id, this);
		return this;
	}

	public QuestAdvancement unbuild()
	{
		remove();
		QuestAdvancementManager.advancements.remove(id);
		return this;
	}

	public String getID()
	{
		return id.toString();
	}
	
	public NamespacedKey getKey()
	{
		return id;
	}

	public String getIcon()
	{
		return icon;
	}

	public QuestAdvancement icon(String icon)
	{
		this.icon = icon;
		return this;
	}

	public QuestAdvancement icon(Material icon)
	{
		String minecraftKey = Item.REGISTRY.b(CraftMagicNumbers.getItem(icon)).toString();
		return this.icon(minecraftKey);
	}
	
	public QuestAdvancement iconSubID(short data)
	{
		iconsubID = data;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public QuestAdvancement description(String description)
	{
		this.description = description;
		return this;
	}

	public String getBackground()
	{
		return background;
	}

	// TODO Array/Enum of all known backgrounds for ease of access. Integrate
	// with material enum.
	public QuestAdvancement background(String url)
	{
		this.background = url;
		return this;
	}

	public String getTitle()
	{
		return title;
	}

	public QuestAdvancement title(String title)
	{
		this.title = title;
		return this;
	}

	public String getParent()
	{
		return parent;
	}

	public QuestAdvancement parent(String parent)
	{
		this.parent = parent;
		return this;
	}

	public Set<Trigger> getTriggers()
	{
		return triggers;
	}

	public QuestAdvancement addTrigger(Trigger trigger)
	{
		triggers.add(trigger);
		return this;
	}

	public FrameType getFrame()
	{
		return frame;
	}
	
	public short getIconData()
	{
		return iconsubID;
	}

	public QuestAdvancement frame(FrameType frame)
	{
		this.frame = frame;
		return this;
	}

	public QuestAdvancement frame(String frame)
	{
		if (frame.equalsIgnoreCase("random"))
			return frame(FrameType.RANDOM());
		else
			try
			{
				frame(FrameType.valueOf(frame));
			}
			catch (EnumConstantNotPresentException e)
			{
				Bukkit.getLogger().info("[src.QuestAdvancement] Unknown FrameType given. Using default (TASK)");
				frame(FrameType.TASK);
			}
		return this;
	}

	public boolean getAnnouncement()
	{
		return announce;
	}

	public QuestAdvancement announcement(boolean announce)
	{
		this.announce = announce;
		return this;
	}

	public boolean getToast()
	{
		return toast;
	}

	public QuestAdvancement toast(boolean toast)
	{
		this.toast = toast;
		return this;
	}

	@SuppressWarnings("unchecked")
	public String getJSON()
	{
		JSONObject json = new JSONObject();

		JSONObject icon = new JSONObject();
		icon.put("item", getIcon());
		icon.put("data", getIconData());

		JSONObject display = new JSONObject();
		display.put("icon", icon);
		display.put("title", getTitle());
		display.put("description", getDescription());
		display.put("background", getBackground());
		display.put("frame", getFrame().toString());
		display.put("announce_to_chat", getAnnouncement());
		display.put("show_toast", getToast());

		json.put("parent", getParent());

		JSONObject criteria = new JSONObject();

		// Changed to normal comment as JavaDocs are not displayed here
		// @PROgrm_JARvis
		/*
		 * Define each criteria, for each criteria in list, add items, trigger
		 * and conditions
		 */

		if (getTriggers().isEmpty())
			triggers.add(new Trigger(TriggerType.IMPOSSIBLE, "default"));
		for (Trigger trigger : getTriggers())
		{
			JSONObject triggerObj = new JSONObject();
			final JSONObject advConditions = new JSONObject();

			triggerObj.put("trigger", "minecraft:" + trigger.type.toString().toLowerCase());
			trigger.conditions.forEach(condition -> advConditions.put(condition.name, condition.set));
			if (!trigger.conditions.isEmpty())
				triggerObj.put("conditions", advConditions);
			criteria.put(trigger.name, triggerObj);
		}

		json.put("criteria", criteria);
		json.put("display", display);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		return gson.toJson(json);
	}

	public boolean counterUp(Player player)
	{
		String criteriaString = null;
		for (String criteria : getAdvancement().getCriteria())
		{
			if (player.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria) != null)
				criteriaString = criteria;
			else
				break;
		}
		if (criteriaString == null)
			return false;
		player.getAdvancementProgress(getAdvancement()).awardCriteria(criteriaString);
		return true;
	}

	public boolean counterDown(Player player)
	{
		String criteriaString = null;
		for (String criteria : getAdvancement().getCriteria())
		{
			if (player.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria) != null)
				criteriaString = criteria;
			else
				break;
		}
		if (criteriaString == null)
			return false;
		player.getAdvancementProgress(getAdvancement()).revokeCriteria(criteriaString);
		return true;
	}

	public void counterReset(Player player)
	{
		for (String criteria : getAdvancement().getCriteria())
		{
			if (player.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria) != null)
				player.getAdvancementProgress(getAdvancement()).revokeCriteria(criteria);
		}
	}

	@SuppressWarnings("deprecation")
	public QuestAdvancement add()
	{
		try
		{
			Bukkit.getUnsafe().loadAdvancement(id, getJSON());
			QuestChatManager.logCmd(Level.INFO, I18n.locMsg("Cmdlog.AdvancementLoading", id.getKey()));
		}
		catch (IllegalArgumentException e){}
		return this;
	}

	@SuppressWarnings("deprecation")
	public QuestAdvancement remove()
	{
		Bukkit.getUnsafe().removeAdvancement(id);
		return this;
	}

	public QuestAdvancement show(JavaPlugin plugin, final Player... players)
	{
		add();
		grant(players);
        Bukkit.getScheduler().runTaskLater(plugin, () ->
        {
            revoke(players);
            remove();
        }, 20L);
		return this;
	}

	public QuestAdvancement grant(Player... players)
	{
		Advancement advancement = getAdvancement();
		for (Player player : players)
		{
			if (!player.getAdvancementProgress(advancement).isDone())
			{
				Collection<String> remainingCriteria = player.getAdvancementProgress(advancement).getRemainingCriteria();
				for (String remainingCriterion : remainingCriteria)
					player.getAdvancementProgress(getAdvancement()).awardCriteria(remainingCriterion);
			}
		}
		return this;
	}

	public QuestAdvancement revoke(Player... players)
	{
		Advancement advancement = getAdvancement();
		for (Player player : players)
		{
			if (player.getAdvancementProgress(advancement).isDone())
			{
				Collection<String> awardedCriteria = player.getAdvancementProgress(advancement).getAwardedCriteria();
				for (String awardedCriterion : awardedCriteria)
					player.getAdvancementProgress(getAdvancement()).revokeCriteria(awardedCriterion);
			}
		}
		return this;
	}

	public Advancement getAdvancement()
	{
		return Bukkit.getAdvancement(id);
	}

	public enum FrameType
	{
		TASK("task"),
		GOAL("goal"),
		CHALLENGE("challenge");

		private String name = "task";

		FrameType(String name)
		{
			this.name = name;
		}

		public static FrameType RANDOM()
		{
			FrameType[] frameTypes = FrameType.values();
			return frameTypes[(int) (Math.random() * (frameTypes.length - 1))];
		}

		public String toString()
		{
			return name;
		}
	}

	@Override
	public String toString()
	{
		return "Advancement(" + id + "|" + this.title + ")";
	}

	// BEGIN UTIL
	@SuppressWarnings(
	{ "unchecked", "deprecation" })
	protected static JSONObject convertItemToJSON(ItemStack item, JSONObject itemJSON)
	{
		itemJSON.put("item", "minecraft:" + item.getType().name().toLowerCase());
		itemJSON.put("amount", item.getAmount());
		itemJSON.put("data", item.getData().getData());
		return itemJSON;
	}

	// BEGIN CLASSES
	public static class Condition
	{
		protected String name;
		protected Object set;

		public Condition(String name, JSONObject set)
		{
			this.name = name;
			this.set = set;
		}

		public Condition(String name, ItemStack item)
		{
			this(name, convertItemToJSON(item, new JSONObject()));

		}

		public Condition(String name, String set)
		{
			this.name = name;
			this.set = set;
		}
	}

	public static class Trigger
	{
		protected TriggerType type;
		protected String name;
		protected Set<Condition> conditions;

		public Trigger(TriggerType type, String name)
		{
			this.name = name;
			this.type = type;
			conditions = new HashSet<>();
		}

		public Trigger addCondition(Condition condition)
		{
			conditions.add(condition);

			return this;
		}
	}

	public static enum TriggerType
	{
		ARBITRARY_PLAYER_TICK,
		BRED_ANIMALS,
		BREWED_POTION,
		CHANGED_DIMENSION,
		CONSTRUCT_BEACON,
		CONSUME_ITEM,
		CURED_ZOMBIE_VILLAGER,
		ENCHANTED_ITEM,
		ENTER_BLOCK,
		ENTITY_HURT_PLAYER,
		ENTITY_KILLED_PLAYER,
		IMPOSSIBLE,
		INVENTORY_CHANGED,
		ITEM_DURABILITY_CHANGED,
		LEVITATION,
		LOCATION,
		PLACED_BLOCK,
		PLAYER_HURT_ENTITY,
		PLAYER_KILLED_ENTITY,
		RECIPE_UNLOCKED,
		SLEPT_IN_BED,
		SUMMONED_ENTITY,
		TAME_ANIMAL,
		TICK,
		USED_ENDER_EYE,
		VILLAGER_TRADE

	}
}