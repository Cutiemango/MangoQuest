package me.Cutiemango.MangoQuest.objects.reward;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestReward
{
	private int experience;
	private double money;
	private List<RewardChoice> itemChoices = new ArrayList<>();
	private List<String> command = new ArrayList<>();
	private HashMap<Integer, Integer> friendPoints = new HashMap<>();

	private NPC rewardNPC;

	protected int rewardAmount = 1;
	protected boolean instantGiveReward = false;

	private int skillapiExp;
	private int qrpgExp;

	public void addFriendPoint(int id, int value) {
		friendPoints.put(id, value);
	}

	public void addCommand(String s) {
		command.add(s);
	}

	public boolean hasItem() {
		return !itemChoices.isEmpty();
	}

	public boolean hasMoney() {
		return !(money == 0.0D);
	}

	public boolean hasExp() {
		return !(experience == 0);
	}

	public boolean hasFriendPoint() {
		return !friendPoints.isEmpty();
	}

	public boolean hasCommand() {
		return !command.isEmpty();
	}

	public boolean hasSkillAPIExp() {
		return skillapiExp != 0;
	}

	public boolean hasQRPGExp() {
		return qrpgExp != 0;
	}

	public boolean hasMultipleChoices() {
		return itemChoices.size() > 1;
	}

	public List<RewardChoice> getChoices() {
		return itemChoices;
	}

	public double getMoney() {
		return money;
	}

	public int getExp() {
		return experience;
	}

	public int getSkillAPIExp() {
		return skillapiExp;
	}

	public int getQRPGExp() {
		return qrpgExp;
	}

	public HashMap<Integer, Integer> getFriendPointMap() {
		return friendPoints;
	}

	public List<String> getCommands() {
		return command;
	}

	public boolean isEmpty() {
		return itemChoices.isEmpty() && money == 0.0D && experience == 0.0D && command.isEmpty() && friendPoints.isEmpty();
	}

	public int getChoiceAmount() {
		return itemChoices.size();
	}

	public RewardChoice getDefaultChoice() {
		return itemChoices.get(0);
	}

	public RewardChoice getChoice(int index) {
		return itemChoices.get(index);
	}

	public NPC getRewardNPC() {
		return rewardNPC;
	}

	public boolean hasRewardNPC() {
		return rewardNPC != null;
	}

	public void setRewardNPC(NPC rewardNPC) {
		this.rewardNPC = rewardNPC;
	}

	public void setMoney(double m) {
		money = m;
	}

	public void setExp(int exp) {
		experience = exp;
	}

	public void setChoice(int i, RewardChoice c) {
		itemChoices.set(i, c);
	}

	public void setChoice(List<RewardChoice> l) {
		itemChoices = l;
	}

	public void setSkillAPIExp(int i) {
		skillapiExp = i;
	}

	public void setQRPGExp(int i) {
		qrpgExp = i;
	}

	public int getRewardAmount() {
		return rewardAmount;
	}

	public void setRewardAmount(int i) {
		if (i > itemChoices.size())
			return;
		rewardAmount = i;
	}

	public boolean instantGiveReward() {

		if (rewardAmount > 1)
			return false;
		return instantGiveReward;
	}

	public void setInstantGiveReward(boolean b) {
		instantGiveReward = b;
	}

	public void executeItemReward(Player p) {
		if (hasItem())
			for (RewardChoice choice : itemChoices)
				choice.executeReward(p);
	}

	public void executeReward(Player p) {
		if (hasMoney()) {
			if (Main.getHooker().hasEconomyEnabled()) {
				Main.getHooker().getEconomy().depositPlayer(p, money);
				QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveMoneyReward", Double.toString(money)));
			}
		}

		if (hasExp()) {
			p.giveExp(experience);
			QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveExpReward", Double.toString(experience)));
		}

		if (hasFriendPoint()) {
			QuestPlayerData qd = QuestUtil.getData(p);
			for (Integer id : friendPoints.keySet()) {
				qd.addNPCfp(id, friendPoints.get(id));
			}
		}

		if (hasCommand()) {
			for (String cmd : command) {
				cmd = cmd.replace("<player>", p.getName());
				QuestUtil.executeSyncConsoleCommand(cmd);
			}
		}

		if (hasSkillAPIExp()) {
			if (Main.getHooker().hasSkillAPIEnabled()) {
				SkillAPI.getPlayerData(p).giveExp(skillapiExp, ExpSource.COMMAND);
				QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveRPGExpReward", Integer.toString(skillapiExp)));
			}
		}

		if (hasQRPGExp()) {
			if (Main.getHooker().hasQuantumRPGEnabled()) {
				Main.getHooker().getQuantumRPG().getUserManager().getOrLoadUser(p).getActiveProfile().getClassData().addExp(qrpgExp);
				QuestChatManager.info(p, I18n.locMsg("QuestReward.GiveRPGExpReward", Integer.toString(qrpgExp)));
			}
		}
	}
}
