package me.Cutiemango.MangoQuest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.citizensnpcs.api.CitizensAPI;

public class QuestReward
{

	private double money;
	private List<ItemStack> items = new ArrayList<>();
	private int experience;
	private List<String> command = new ArrayList<>();
	private HashMap<Integer, Integer> npcfp = new HashMap<>();
	
	private int skillAPIexp;

	public void addItem(ItemStack is)
	{
		items.add(is);
	}

	public void addMoney(double d)
	{
		money += d;
	}

	public void addExp(int i)
	{
		experience += i;
	}
	
	public void addSkillAPIExp(int i)
	{
		skillAPIexp += i;
	}

	public void addFriendPoint(int id, int value)
	{
		npcfp.put(id, value);
	}

	public void addCommand(String s)
	{
		command.add(s);
	}

	public void removeItem(ItemStack is)
	{
		if (items.contains(is))
			items.remove(is);
		else
			return;
	}

	public void removeMoney(double d)
	{
		if (money < d)
			money = 0;
		else
			money -= d;
	}

	public void removeExp(int i)
	{
		if (experience < i)
			experience = 0;
		else
			experience -= i;
	}

	public boolean hasItem()
	{
		return !items.isEmpty();
	}

	public boolean hasMoney()
	{
		return !(money == 0.0D);
	}

	public boolean hasExp()
	{
		return !(experience == 0);
	}

	public boolean hasFriendPoint()
	{
		return !npcfp.isEmpty();
	}

	public boolean hasCommand()
	{
		return !command.isEmpty();
	}
	
	public boolean hasSkillAPIExp()
	{
		return !(skillAPIexp == 0);
	}

	public List<ItemStack> getItems()
	{
		return items;
	}

	public double getMoney()
	{
		return money;
	}

	public int getExp()
	{
		return experience;
	}
	
	public int getSkillAPIExp()
	{
		return skillAPIexp;
	}

	public HashMap<Integer, Integer> getFp()
	{
		return npcfp;
	}

	public List<String> getCommands()
	{
		return command;
	}

	public boolean isEmpty()
	{
		return items.isEmpty() && money == 0.0D;
	}

	public void setMoney(double m)
	{
		money = m;
	}

	public void setExp(int exp)
	{
		experience = exp;
	}

	public void setItemReward(List<ItemStack> l)
	{
		items = l;
	}
	
	public void setSkillAPIExp(int i)
	{
		skillAPIexp = i;
	}

	public void giveRewardTo(Player p)
	{
		if (this.hasItem())
		{
			for (ItemStack is : items)
			{
				if (p.getInventory().firstEmpty() == -1)
				{
					QuestChatManager.info(p, I18n.locMsg("CommandInfo.RewardDropped"));
					p.getWorld().dropItem(p.getLocation(), is);
					return;
				}
				else
				{
					p.getInventory().addItem(is);
					if (is.hasItemMeta() && is.getItemMeta().hasDisplayName())
						QuestChatManager.info(p, I18n.locMsg("CommandInfo.GiveItemReward", is.getItemMeta().getDisplayName(),
								Integer.toString(is.getAmount())));
					else
						QuestChatManager.info(p, I18n.locMsg("CommandInfo.GiveItemReward",
								QuestUtil.translate(is.getType(), is.getDurability()), Integer.toString(is.getAmount())));
				}
			}
		}

		if (this.hasMoney())
		{
			Main.instance.pluginHooker.getEconomy().depositPlayer(p, money);
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.GiveMoneyReward", Double.toString(money)));
		}

		if (this.hasExp())
		{
			p.giveExp(experience);
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.GiveExpReward", Double.toString(experience)));
		}

		if (this.hasFriendPoint())
		{
			QuestPlayerData qd = QuestUtil.getData(p);
			for (Integer id : npcfp.keySet())
			{
				qd.addNPCfp(id, npcfp.get(id));
				QuestChatManager.info(p, I18n.locMsg("CommandInfo.GiveFriendPoint", CitizensAPI.getNPCRegistry().getById(id).getName()));
			}
		}

		if (this.hasCommand())
		{
			for (String cmd : command)
			{
				if (!Bukkit.isPrimaryThread())
					Bukkit.getScheduler().runTask(Main.instance, new Runnable()
					{
						@Override
						public void run()
						{
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("<player>", p.getName()));
						}
					});
			}
		}
		
		if (this.hasSkillAPIExp())
		{
			SkillAPI.getPlayerData(p).giveExp(skillAPIexp, ExpSource.COMMAND);
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.GiveSkillAPIExpReward", Integer.toString(skillAPIexp)));
		}
	}
}
