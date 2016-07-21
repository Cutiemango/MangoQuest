package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class QuestReward {

	private double REWARD_MONEY;
	private List<ItemStack> REWARD_ITEMS = new ArrayList<>();

	public QuestReward(ItemStack is) {
		this.REWARD_ITEMS.add(is);
	}

	public QuestReward(double amount) {
		this.REWARD_MONEY = amount;
	}

	public void add(ItemStack is) {
		this.REWARD_ITEMS.add(is);
	}

	public void add(double money) {
		this.REWARD_MONEY += money;
	}

	public void remove(ItemStack is) {
		if (REWARD_ITEMS.contains(is))
			this.REWARD_ITEMS.remove(is);
		else
			return;
	}

	public void remove(double money) {
		if (this.REWARD_MONEY < money)
			this.REWARD_MONEY = 0;
		else
			this.REWARD_MONEY -= money;
	}

	public boolean hasItem() {
		return !(this.REWARD_ITEMS.isEmpty());
	}

	public boolean hasMoney() {
		return !(this.REWARD_MONEY == 0.0D);
	}

	public List<ItemStack> getItems() {
		return this.REWARD_ITEMS;
	}

	public double getMoney() {
		return this.REWARD_MONEY;
	}

	public void giveRewardTo(Player p) {
		if (this.hasItem()) {
			for (ItemStack is : this.REWARD_ITEMS) {
				if (p.getInventory().firstEmpty() == -1) {
					p.sendMessage(QuestStorage.prefix + ChatColor.RED + "背包物品過多，你的任務獎勵 "
							+ is.getItemMeta().getDisplayName() + ChatColor.RED + " 掉落地面！");
					p.getWorld().dropItem(p.getLocation(), is);
					return;
				} else {
					p.getInventory().addItem(is);
					if (is.getItemMeta().hasDisplayName())
						QuestUtil.info(p, "&e&l你得到了任務獎勵 " + is.getItemMeta().getDisplayName() + " &f" + is.getAmount() + " &e&l個");
					else
						QuestUtil.info(p, "&e&l你得到了任務獎勵 " + QuestUtil.translateItemStackToChinese(is) + " &f" + is.getAmount() + " &e&l個");
				}
			}
		}

		if (this.hasMoney()) {
			Main.economy.depositPlayer(p, this.REWARD_MONEY);
			QuestUtil.info(p, "&e&l你得到了任務獎勵  &f" + REWARD_MONEY + " &e&l元");
		}
	}
}
