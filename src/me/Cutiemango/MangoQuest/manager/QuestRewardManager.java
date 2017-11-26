package me.Cutiemango.MangoQuest.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.QuestReward;
import me.Cutiemango.MangoQuest.objects.RewardCache;
import me.Cutiemango.MangoQuest.objects.RewardChoice;
import net.md_5.bungee.api.ChatColor;

public class QuestRewardManager implements Listener
{
	public static final int MAXMIUM_CHOICES = 9;
	private static HashMap<String, RewardCache> rewardCache = new HashMap<>();
	
	public static void openEditMainGUI(Player p)
	{
		if (!QuestEditorManager.checkEditorMode(p, false))
			return;
		QuestReward reward = QuestEditorManager.getCurrentEditingQuest(p).getQuestReward();
		Inventory inv = Bukkit.createInventory(null, 27, I18n.locMsg("QuestReward.RewardEditTitle"));
		int a = reward.getChoiceAmount();
		
		for (int i = 0; i < a + 1; i++)
		{
			if (i == a)
				inv.setItem(getRewardSlot(a+1, i), newRewardChoice(i));
			else
				inv.setItem(getRewardSlot(a+1, i), itemButton(reward.getChoice(i), i));
		}
		
		inv.setItem(26, editRewardAmount(reward.getRewardAmount()));

		for (int i = 0; i < 27; i++)
		{
			if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR))
				inv.setItem(i, glassPane(reward.getRewardAmount()));
		}
		p.openInventory(inv);
	}
	
	public static void openEditRewardGUI(Player p, int index)
	{
		if (!QuestEditorManager.checkEditorMode(p, false))
			return;
		String title = I18n.locMsg("QuestReward.ChoiceEditTitle");
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		title += q.getQuestName() + ":" + Integer.toString(index);
		Inventory inv = Bukkit.createInventory(null, 27, title);
		
		if (q.getQuestReward().getChoices().size() <= index)
			q.getQuestReward().getChoices().add(index, new RewardChoice(new ArrayList<ItemStack>()));
		
		for (ItemStack item : q.getQuestReward().getChoice(index).getItems())
		{
			if (item == null || item.getType().equals(Material.AIR))
				continue;
			inv.addItem(item);
		}
		p.openInventory(inv);
	}
	
	private static ItemStack editRewardAmount(int amount)
	{
		QuestGUIItem anvil = new QuestGUIItem(Material.ANVIL, 1, (short)0);
		anvil.setName(I18n.locMsg("QuestReward.EditRewardAmount"));
		anvil.setLore(QuestUtil.createList(I18n.locMsg("QuestReward.CurrentRewardAmount", Integer.toString(amount), I18n.locMsg("QuestReward.ClickToEdit"))));
		return anvil.get();
	}
	
	private static ItemStack newRewardChoice(int index)
	{
		QuestGUIItem chest = new QuestGUIItem(Material.ENDER_CHEST, 1, (short)0);
		chest.setName(I18n.locMsg("QuestReward.NewRewardChoice"));
		chest.setLore(QuestUtil.createList(I18n.locMsg("QuestReward.NewRewardChoiceLore"), QuestChatManager.translateColor("&0" + index)));
		chest.glowEffect();
		return chest.get();
	}
	
	private static ItemStack glassPane(int amount)
	{
		QuestGUIItem glassPane = new QuestGUIItem(Material.STAINED_GLASS_PANE, 1, (short)QuestUtil.randomInteger(0, 15));
		glassPane.setName(I18n.locMsg("QuestReward.EditGlassPane", Integer.toString(amount)));
		return glassPane.get();
	}
	
	private static ItemStack itemButton(RewardChoice rc, int index)
	{
		ItemStack firstItem = new ItemStack(Material.BARRIER);
		QuestGUIItem button;
		if (rc.getItems().size() == 0)
			button = new QuestGUIItem(Material.BARRIER, 1, (short)0);
		else
		{
			firstItem = rc.getItems().get(0);
			button = new QuestGUIItem(firstItem.getType(), firstItem.getAmount(), (short)firstItem.getDurability());
		}
		List<String> lore = new ArrayList<>();
		for (ItemStack item : rc.getItems())
		{
			if (item == null || item.getType() == Material.AIR)
				 continue;
			lore.add(QuestChatManager.translateColor("&f- " + getItemName(item)));
		}
		lore.add(I18n.locMsg("QuestReward.ClickToEdit"));
		lore.add(ChatColor.BLACK + "" + index);
		button.setName("&f" + getItemName(firstItem));
		button.setLore(lore);
		return button.get();
	}
	
	public static void saveItemChoice(Player p, List<ItemStack> items, int index)
	{
		Quest q = QuestEditorManager.getCurrentEditingQuest(p);
		q.getQuestReward().setChoice(index, new RewardChoice(items));
	}
	
	public static String getItemName(ItemStack item)
	{
		String name = "";
		if (item == null)
			return "Unknown Item";
		if (item.getType() == Material.BARRIER)
			name = I18n.locMsg("QuestReward.DefaultRewardIcon");
		else
			name = QuestChatManager.translateColor(QuestUtil.translate(item) + " &f" + ChatColor.stripColor(I18n.locMsg("QuestEditor.Amount", Integer.toString(item.getAmount()))));
		return name;
	}
	
	/**
	* @param amount
	* The amount of slots you need.
	* @param x
	* The index.
	* @return
	* The exact slot you need with given index.
	*/
	public static int getRewardSlot(int amount, int x)
	{
		if (amount % 2 == 0)
		{
			int[] arySlot = new int[]
			{ 3, 5, 1, 7, 2, 6, 0, 8 };
			return 9 + arySlot[x];
		}
		else
		{
			int[] arySlot = new int[]
			{ 4, 2, 6, 3, 5, 1, 7, 0, 8 };
			return 9 + arySlot[x];
		}
	}
	
	public static void giveRewardItem(Player p, ItemStack is)
	{
		if (p.getInventory().firstEmpty() == -1)
		{
			QuestChatManager.info(p, I18n.locMsg("QuestReward.RewardDropped"));
			p.getWorld().dropItem(p.getLocation(), is);
		}
		else
		{
			p.getInventory().addItem(is);
			if (is.hasItemMeta() && is.getItemMeta().hasDisplayName())
				QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveItemReward", is.getItemMeta().getDisplayName(),
						Integer.toString(is.getAmount())));
			else
				QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveItemReward",
						QuestUtil.translate(is.getType(), is.getDurability()), Integer.toString(is.getAmount())));
		}
		return;
	}
	
	public static RewardCache getRewardCache(Player p)
	{
		return rewardCache.get(p.getName());
	}
	
	public static boolean hasRewardCache(Player p)
	{
		return rewardCache.containsKey(p.getName());
	}
	
	public static void removeCache(Player p)
	{
		rewardCache.remove(p.getName());
	}
	
	public static void registerCache(Player p, Quest q)
	{
		RewardCache cache = new RewardCache(p, q);
		rewardCache.put(p.getName(), cache);
		cache.openGUI();
	}
}
