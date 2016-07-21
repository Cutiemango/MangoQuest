package me.Cutiemango.MangoQuest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Cutiemango.MangoQuest.QuestUtil.QuestTitleEnum;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectItemDeliver;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.npc.NPC;

public class QuestPlayerData {

	public QuestPlayerData(Player p) {
		this.p = p;
	}
	
	public QuestPlayerData(Player p, FileConfiguration c){
		this.p = p;
		c.set("玩家資料." + p.getUniqueId() + ".玩家ID", p.getName());
		if (c.getConfigurationSection("玩家資料." + p.getUniqueId() + ".任務進度") != null){
			for (String index : c.getConfigurationSection("玩家資料." + p.getUniqueId() + ".任務進度").getKeys(false)){
				if (QuestStorage.Quests.get(index) == null){
					QuestUtil.error(p, "您的玩家資料有不存在或經被移除的任務，已經遺失資料！"
							+ "遺失的任務內部碼： " + index + "，若您覺得這不應該發生，請回報管理員。");
					continue;
				}
				Quest q = QuestStorage.Quests.get(index);
				int t = 0;
				int s = c.getInt("玩家資料." + p.getUniqueId() + ".任務進度." + index + ".QuestStage");
				List<QuestObjectProgress> qplist = new ArrayList<>();
				for (SimpleQuestObject ob : q.getStage(s).getObjects()){
					QuestObjectProgress qp = new QuestObjectProgress(ob, c.getInt("玩家資料." + p.getUniqueId() + ".任務進度." + index + ".QuestObjectProgress." + t));
					qp.checkIfFinished();
					qplist.add(qp);
					t++;
				}
				CurrentQuest.add(new QuestProgress(q, p, s, qplist));
			}
		}
		
		for (String s : c.getStringList("玩家資料." + p.getUniqueId() + ".已完成的任務")){
			if (QuestStorage.Quests.get(s) == null){
				QuestUtil.error(p, "您的玩家資料有不存在或經被移除的任務，已經遺失資料！"
						+ "遺失的任務內部碼： " + s + "，若您覺得這不應該發生，請回報管理員。");
				continue;
			}
			Quest q = QuestStorage.Quests.get(s);
			if (FinishedQuest.contains(q))
				continue;
			FinishedQuest.add(q);
		}
		QuestUtil.info(p, "&a玩家任務資料讀取完成！");
	}

	private Player p;
	private List<QuestProgress> CurrentQuest = new ArrayList<>();
	private List<Quest> FinishedQuest = new ArrayList<>();

	public void save() {
		QuestConfigLoad.pconfig.set("玩家資料." + p.getUniqueId() + ".玩家ID", p.getName());
		List<String> FinishedList = new ArrayList<>();
		for (Quest q : FinishedQuest) {
			if (FinishedList.contains(q.getInternalID()))
				continue;
			FinishedList.add(q.getInternalID());
		}
		QuestConfigLoad.pconfig.set("玩家資料." + p.getUniqueId() + ".已完成的任務", FinishedList);
		
		QuestConfigLoad.pconfig.set("玩家資料." + p.getUniqueId() + ".任務進度", "");
		
		if (!CurrentQuest.isEmpty()){
			for (QuestProgress qp : CurrentQuest) {
				qp.save(QuestConfigLoad.pconfig);
			}
		}
		try {
			QuestConfigLoad.pconfig.save(new File(Main.instance.getDataFolder(), "players.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Player getPlayer() {
		return p;
	}

	public boolean hasFinished(Quest q) {
		return FinishedQuest.contains(q);
	}
	
	public QuestProgress getProgress(Quest q){
		for (QuestProgress qp : CurrentQuest){
			if (q.getInternalID().equals(qp.getQuest().getInternalID())) {
				return qp;
			}
		}
		return null;
	}

	public List<QuestProgress> getProgresses() {
		return CurrentQuest;
	}
	
	public void takeQuest(Quest q){
		if (CurrentQuest.size() + 1 > 4){
			QuestUtil.info(p, "&c你的任務列表已滿，不能再接受任務了。");
			return;
		}
		for (QuestProgress qp : CurrentQuest){
			if (q.getInternalID().equals(qp.getQuest().getInternalID())) {
				QuestUtil.info(p, "&c你的任務列表已有此任務，不能再接受任務了。");
				return;
			}
		}
		CurrentQuest.add(new QuestProgress(q, p));
		QuestUtil.sendQuestTitle(p, q, QuestTitleEnum.ACCEPT);
	}
	
	public void quitQuest(Quest q){
		removeProgress(q);
		QuestUtil.sendQuestTitle(p, q, QuestTitleEnum.QUIT);
	}
	
	public void talkToNPC(NPC npc){
		for (QuestProgress qp : CurrentQuest){
			for (QuestObjectProgress qop : qp.getCurrentObjects()){
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectTalkToNPC){
					if (((QuestObjectTalkToNPC)qop.getObject()).getTargetNPC().equals(npc)){
						QuestObjectTalkToNPC o = (QuestObjectTalkToNPC)qop.getObject();
						qop.finish();
						qop.setProgress(1);
						QuestUtil.info(p, o.toPlainText() + " &a(已完成)");
						qp.checkIfnextStage();
						return;
					}
				}
			}
		}
	}
	
	public void deliverItem(NPC npc){
		for (QuestProgress qp : CurrentQuest){
			for (QuestObjectProgress qop : qp.getCurrentObjects()){
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectItemDeliver){
					QuestObjectItemDeliver o = (QuestObjectItemDeliver)qop.getObject();
					if (o.getTargetNPC().equals(npc) && o.getDeliverItem().isSimilar(p.getInventory().getItemInMainHand())){
						if (p.getInventory().getItemInMainHand().getAmount() > (o.getDeliverAmount() - qop.getProgress())){
							p.getInventory().getItemInMainHand().setAmount(
									p.getInventory().getItemInMainHand().getAmount() - (o.getDeliverAmount() - qop.getProgress()));
							qop.setProgress(o.getDeliverAmount());
							qop.finish();
							QuestUtil.info(p, o.toPlainText() + " &a(已完成)");
							qp.checkIfnextStage();
							return;
						}
						else if (p.getInventory().getItemInMainHand().getAmount() == (o.getDeliverAmount() - qop.getProgress())){
							p.getInventory().setItemInMainHand(null);
							qop.setProgress(o.getDeliverAmount());
							qop.finish();
							QuestUtil.info(p, o.toPlainText() + " &a(已完成)");
							qp.checkIfnextStage();
							return;
						}
						else{
							qop.setProgress(qop.getProgress() + p.getInventory().getItemInMainHand().getAmount());
							p.getInventory().setItemInMainHand(null);
							QuestUtil.info(p, o.toPlainText() + " &6進度： (" + qop.getProgress() + "/" + o.getDeliverAmount() + ")");
							qp.checkIfnextStage();
							return;
						}
					}
				}
			}
		}
	}

	public void removeProgress(Quest q) {
		for (QuestProgress qp : CurrentQuest){
			if (q.getInternalID().equals(qp.getQuest().getInternalID())) {
				CurrentQuest.remove(qp);
				break;
			}
		}
	}

	public void addFinishedQuest(Quest q) {
		if (FinishedQuest.contains(q))
			return;
		FinishedQuest.add(q);
	}
	
	public static boolean hasConfigData(Player p){
		return !(QuestConfigLoad.pconfig.getString("玩家資料." + p.getUniqueId() + ".玩家ID") == null);
	}

}
