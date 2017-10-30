package me.Cutiemango.MangoQuest.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;

public class QuestReward
{
	private int experience;
	private double money;
	private List<RewardChoice> itemChoices = new ArrayList<>();
	private List<String> command = new ArrayList<>();
	private HashMap<Integer, Integer> friendPoints = new HashMap<>();
	
	protected int rewardAmount = 1;
	protected boolean instantGiveReward = false;
	
	private int skillAPIexp;


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
		friendPoints.put(id, value);
	}

	public void addCommand(String s)
	{
		command.add(s);
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
		return !itemChoices.isEmpty();
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
		return !friendPoints.isEmpty();
	}

	public boolean hasCommand()
	{
		return !command.isEmpty();
	}
	
	public boolean hasSkillAPIExp()
	{
		return !(skillAPIexp == 0);
	}
	
	public boolean hasMultipleChoices()
	{
		return itemChoices.size() > 1;
	}

	public List<RewardChoice> getChoices()
	{
		return itemChoices;
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
		return friendPoints;
	}

	public List<String> getCommands()
	{
		return command;
	}

	public boolean isEmpty()
	{
		return itemChoices.isEmpty() && money == 0.0D && experience == 0.0D && command.isEmpty() && friendPoints.isEmpty();
	}
	
	public int getChoiceAmount()
	{
		return itemChoices.size();
	}
	
	public RewardChoice getDefaultChoice()
	{
		return itemChoices.get(0);
	}
	
	public RewardChoice getChoice(int index)
	{
		return itemChoices.get(index);
	}

	public void setMoney(double m)
	{
		money = m;
	}

	public void setExp(int exp)
	{
		experience = exp;
	}

	public void setChoice(int i, RewardChoice c)
	{
		itemChoices.set(i, c);
	}
	
	public void setChoice(List<RewardChoice> l)
	{
		itemChoices = l;
	}
	
	public void setSkillAPIExp(int i)
	{
		skillAPIexp = i;
	}
	
	public int getRewardAmount()
	{
		return rewardAmount;
	}
	
	public void setRewardAmount(int i)
	{
		rewardAmount = i;
	}
	
	public boolean instantGiveReward()
	{
		if (rewardAmount > 1)
			return false;
		return instantGiveReward;
	}
	
	public void setInstantGiveReward(boolean b)
	{
		instantGiveReward = b;
	}

	public void executeItemReward(Player p)
	{
		if (this.hasItem())
		{
			for (RewardChoice choice : itemChoices)
			{
				choice.executeReward(p);
			}
		}
	}
	
	public void executeReward(Player p)
	{
		if (this.hasMoney())
		{
			Main.instance.pluginHooker.getEconomy().depositPlayer(p, money);
			QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveMoneyReward", Double.toString(money)));
		}

		if (this.hasExp())
		{
			p.giveExp(experience);
			QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveExpReward", Double.toString(experience)));
		}

		if (this.hasFriendPoint())
		{
			QuestPlayerData qd = QuestUtil.getData(p);
			for (Integer id : friendPoints.keySet())
			{
				qd.addNPCfp(id, friendPoints.get(id));
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
			QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveSkillAPIExpReward", Integer.toString(skillAPIexp)));
		}
	}
}
