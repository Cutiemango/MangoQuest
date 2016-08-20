package me.Cutiemango.MangoQuest.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestConfigLoad;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.QuestUtil.QuestTitleEnum;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectItemConsume;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectItemDeliver;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectReachLocation;
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
		
		if (c.isConfigurationSection("玩家資料." + p.getUniqueId() + ".已完成的任務")){
			for (String s : c.getConfigurationSection("玩家資料." + p.getUniqueId() + ".已完成的任務").getKeys(false)){
				if (QuestStorage.Quests.get(s) == null){
					QuestUtil.error(p, "您的玩家資料有不存在或經被移除的任務，已經遺失資料！"
							+ "遺失的任務內部碼： " + s + "，若您覺得這不應該發生，請回報管理員。");
					continue;
				}
				QuestFinishData qd = new QuestFinishData(QuestStorage.Quests.get(s) 
						,c.getInt("玩家資料." + p.getUniqueId() + ".已完成的任務." + s + ".FinishedTimes")
						,c.getLong("玩家資料." + p.getUniqueId() + ".已完成的任務." + s + ".LastFinishTime"));
				FinishedQuest.add(qd);
			}
		}
		QuestUtil.info(p, "&a玩家任務資料讀取完成！");
	}

	private Player p;
	private List<QuestProgress> CurrentQuest = new ArrayList<>();
	private List<QuestFinishData> FinishedQuest = new ArrayList<>();

	public void save() {
		QuestConfigLoad.pconfig.set("玩家資料." + p.getUniqueId() + ".玩家ID", p.getName());
		for (QuestFinishData q : FinishedQuest) {
			String id = q.getQuest().getInternalID();
			QuestConfigLoad.pconfig.set("玩家資料." + p.getUniqueId() + ".已完成的任務." + id + ".FinishedTimes", q.getFinishedTimes());
			QuestConfigLoad.pconfig.set("玩家資料." + p.getUniqueId() + ".已完成的任務." + id + ".LastFinishTime", q.getLastFinish());
		}
		
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
		for (QuestFinishData qd : FinishedQuest){
			if (qd.getQuest().getInternalID().equals(q.getInternalID()))
				return true;
		}
		return false;
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
		if (!canTake(q, true))
			return;
		if (q.hasTrigger()){
			for (QuestTrigger t : q.getTriggers()){
				if (t.getType().equals(TriggerType.TRIGGER_ON_TAKE)){
					t.trigger(p);
					continue;
				}
				else if (t.getType().equals(TriggerType.TRIGGER_STAGE_START) && t.getCount() == 1){
					t.trigger(p);
					continue;
				}
			}
		}
		CurrentQuest.add(new QuestProgress(q, p));
		QuestUtil.sendQuestTitle(p, q, QuestTitleEnum.ACCEPT);
	}
	
	public void forceQuit(Quest q){
		removeProgress(q);
		QuestUtil.error(p, "由於系統管理員重設了任務 " + q.getQuestName() + " 的任務內容，你被強制退出執行這個任務，所有任務紀錄將不會被保留。");
		return;
	}
	
	public void quitQuest(Quest q){
		for (QuestTrigger t : q.getTriggers()){
			if (t.getType().equals(TriggerType.TRIGGER_ON_QUIT)){
				t.trigger(p);
				continue;
			}
		}
		removeProgress(q);
		QuestUtil.sendQuestTitle(p, q, QuestTitleEnum.QUIT);
	}
	
	public void breakBlock(Material m){
		for (QuestProgress qp : CurrentQuest){
			for (QuestObjectProgress qop : qp.getCurrentObjects()){
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectBreakBlock){
					QuestObjectBreakBlock o = (QuestObjectBreakBlock)qop.getObject();
					if (o.getType().equals(m)){
						qop.setProgress(qop.getProgress() + 1);
						qop.checkIfFinished();
						if (qop.getProgress() == o.getAmount())
							QuestUtil.info(p, o.toPlainText() + " &a(已完成)");
						else
							QuestUtil.info(p, o.toPlainText() + " &6進度： (" + qop.getProgress() + "/" + o.getAmount() + ")");
						qp.checkIfnextStage();
						return;
					}
				}
			}
		}
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
						if (p.getInventory().getItemInMainHand().getAmount() > (o.getAmount() - qop.getProgress())){
							p.getInventory().getItemInMainHand().setAmount(
									p.getInventory().getItemInMainHand().getAmount() - (o.getAmount() - qop.getProgress()));
							qop.setProgress(o.getAmount());
							qop.checkIfFinished();
							QuestUtil.info(p, o.toPlainText() + " &a(已完成)");
							qp.checkIfnextStage();
							return;
						}
						else if (p.getInventory().getItemInMainHand().getAmount() == (o.getAmount() - qop.getProgress())){
							p.getInventory().setItemInMainHand(null);
							qop.setProgress(o.getAmount());
							qop.checkIfFinished();
							QuestUtil.info(p, o.toPlainText() + " &a(已完成)");
							qp.checkIfnextStage();
							return;
						}
						else{
							qop.checkIfFinished();
							qop.setProgress(qop.getProgress() + p.getInventory().getItemInMainHand().getAmount());
							p.getInventory().setItemInMainHand(null);
							QuestUtil.info(p, o.toPlainText() + " &6進度： (" + qop.getProgress() + "/" + o.getAmount() + ")");
							qp.checkIfnextStage();
							return;
						}
					}
				}
			}
		}
	}
	
	public void killEntity(Entity e){
		for (QuestProgress qp : CurrentQuest){
			for (QuestObjectProgress qop : qp.getCurrentObjects()){
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectKillMob){
					QuestObjectKillMob o = (QuestObjectKillMob)qop.getObject();
					if (o.hasCustomName()){
						if (e.getCustomName() == null || !e.getCustomName().equals(o.getCustomName()) || !e.getType().equals(o.getType()))
							return;
						else{
							qop.setProgress(qop.getProgress() + 1);
							qop.checkIfFinished();
							qp.checkIfnextStage();
							if (qop.getProgress() == o.getAmount())
								QuestUtil.info(p, o.toPlainText() + " &a(已完成)");
							else
								QuestUtil.info(p, o.toPlainText() + " &6進度： (" + qop.getProgress() + "/" + o.getAmount() + ")");
							return;
						}
					}
					else{
						if (e.getType().equals(o.getType())){
							qop.setProgress(qop.getProgress() + 1);
							qop.checkIfFinished();
							qp.checkIfnextStage();
							if (qop.getProgress() == o.getAmount())
								QuestUtil.info(p, o.toPlainText() + " &a(已完成)");
							else
								QuestUtil.info(p, o.toPlainText() + " &6進度： (" + qop.getProgress() + "/" + o.getAmount() + ")");
							return;
						}
					}
				}
			}
		}
	}
	
	public void consumeItem(ItemStack is){
		for (QuestProgress qp : CurrentQuest){
			for (QuestObjectProgress qop : qp.getCurrentObjects()){
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectItemConsume){
					QuestObjectItemConsume o = (QuestObjectItemConsume)qop.getObject();
					if (is.isSimilar(o.getItem())){
						qop.setProgress(qop.getProgress() + 1);
						qop.checkIfFinished();
						qp.checkIfnextStage();
						if (qop.getProgress() == o.getAmount())
							QuestUtil.info(p, o.toPlainText() + " &a(已完成)");
						else
							QuestUtil.info(p, o.toPlainText() + " &6進度： (" + qop.getProgress() + "/" + o.getAmount() + ")");
						return;
					}
				}
			}
		}
	}
	
	public void reachLocation(Location l){
		for (QuestProgress qp : CurrentQuest){
			for (QuestObjectProgress qop : qp.getCurrentObjects()){
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectReachLocation){
					QuestObjectReachLocation o = (QuestObjectReachLocation)qop.getObject();
					if (l.getX() < (o.getLocation().getX() + o.getRadius()) && l.getX() > (o.getLocation().getX() - o.getRadius())) {
						if (l.getY() < (o.getLocation().getY() + o.getRadius()) && l.getY() > (o.getLocation().getY() - o.getRadius())) {
							if (l.getZ() < (o.getLocation().getZ() + o.getRadius()) && l.getZ() > (o.getLocation().getZ() - o.getRadius())) {
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
	
	public List<QuestFinishData> getFinishQuests(){
		return FinishedQuest;
	}
	
	public QuestFinishData getFinishData(Quest q){
		if (!hasFinished(q))
			return null;
		for (QuestFinishData qd : FinishedQuest){
			if (qd.getQuest().getInternalID().equals(q.getInternalID())){
				return qd;
			}
		}
		return null;
	}

	public void addFinishedQuest(Quest q) {
		if (hasFinished(q)){
			getFinishData(q).finish();
			return;
		}
		FinishedQuest.add(new QuestFinishData(q, 1, System.currentTimeMillis()));
		return;
	}
	
	public boolean canTake(Quest q, boolean m){
		if (CurrentQuest.size() + 1 > 4){
			if (m)
				QuestUtil.info(p, "&c你的任務列表已滿，不能再接受任務了。");
			return false;
		}
		for (QuestProgress qp : CurrentQuest){
			if (q.getInternalID().equals(qp.getQuest().getInternalID())) {
				if (m)
					QuestUtil.info(p, "&c你的任務列表已有此任務，不能再接受任務了。");
				return false;
			}
		}
		if (!q.isRedoable() && hasFinished(q)){
			if (m)
				QuestUtil.info(p, "&c此為一次性任務。");
			return false;
		}
		if (q.hasRequirement()){
			if (!q.meetRequirementWith(p)){
				if (m)
					QuestUtil.info(p, q.getFailMessage());
				return false;
			}
		}
		if (hasFinished(q)){
			long d = getDelay(getFinishData(q).getLastFinish(), q.getRedoDelay());
			if (d > 0){
				if (m)
					QuestUtil.info(p, "&c你必須再等待 " + QuestUtil.convertTime(d) + " 才能再度接取這個任務。");
				return false;
			}
		}
		return true;
	}
	
	public static boolean hasConfigData(Player p){
		return !(QuestConfigLoad.pconfig.getString("玩家資料." + p.getUniqueId() + ".玩家ID") == null);
	}
	
	private long getDelay(long last, long quest){
		return quest - (System.currentTimeMillis() - last);
	}
}
