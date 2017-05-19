package me.Cutiemango.MangoQuest.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.QuestUtil.QuestTitleEnum;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;
import me.Cutiemango.MangoQuest.model.QuestVersion;
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectConsumeItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectDeliverItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.npc.NPC;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;

public class QuestPlayerData
{
	private Player p;
	private Set<QuestProgress> CurrentQuest = new HashSet<>();
	private Set<QuestFinishData> FinishedQuest = new HashSet<>();

	private Set<QuestConversation> FinishedConversation = new HashSet<>();

	private HashMap<Integer, Integer> NPCfp = new HashMap<>();

	public QuestPlayerData(Player p)
	{
		this.p = p;
	}

	public QuestPlayerData(Player p, QuestIO io)
	{
		this.p = p;
		io.set("玩家資料." + p.getUniqueId() + ".玩家ID", p.getName());

		List<String> removeProgress = new ArrayList<>();

		if (io.isSection("玩家資料." + p.getUniqueId() + ".任務進度"))
		{
			for (String index : io.getSection("玩家資料." + p.getUniqueId() + ".任務進度"))
			{
				if (QuestStorage.Quests.get(index) == null)
				{
					QuestUtil.error(p, Questi18n.localizeMessage("CommandInfo.TargetProgressNotFound", index));
					continue;
				}
				Quest q = QuestStorage.Quests.get(index);
				if (!(q.getVersion().getVersion() == io.getLong("玩家資料." + p.getUniqueId() + ".任務進度." + q.getInternalID() + ".Version")))
				{
					QuestUtil.error(p, Questi18n.localizeMessage("CommandInfo.OutdatedQuestVersion", index));
					removeProgress.add(q.getInternalID());
					continue;
				}

				int t = 0;
				int s = io.getInt("玩家資料." + p.getUniqueId() + ".任務進度." + index + ".QuestStage");
				List<QuestObjectProgress> qplist = new ArrayList<>();
				for (SimpleQuestObject ob : q.getStage(s).getObjects())
				{
					QuestObjectProgress qp = new QuestObjectProgress(ob,
							io.getInt("玩家資料." + p.getUniqueId() + ".任務進度." + index + ".QuestObjectProgress." + t));
					qp.checkIfFinished();

					qplist.add(qp);
					t++;
				}
				CurrentQuest.add(new QuestProgress(q, p, s, qplist));
			}
		}

		for (String s : removeProgress)
		{
			io.set("玩家資料." + p.getUniqueId() + ".任務進度." + s, "");
		}

		if (io.isSection("玩家資料." + p.getUniqueId() + ".已完成的任務"))
		{
			for (String s : io.getSection("玩家資料." + p.getUniqueId() + ".已完成的任務"))
			{
				if (QuestStorage.Quests.get(s) == null)
				{
					QuestUtil.error(p, Questi18n.localizeMessage("CommandInfo.TargetProgressNotFound", s));
					continue;
				}
				QuestFinishData qd = new QuestFinishData(QuestStorage.Quests.get(s),
						io.getInt("玩家資料." + p.getUniqueId() + ".已完成的任務." + s + ".FinishedTimes"),
						io.getLong("玩家資料." + p.getUniqueId() + ".已完成的任務." + s + ".LastFinishTime"));
				FinishedQuest.add(qd);
			}
		}

		if (io.isSection("玩家資料." + p.getUniqueId() + ".NPC友好度"))
		{
			for (String s : io.getSection("玩家資料." + p.getUniqueId() + ".NPC友好度"))
			{
				NPCfp.put(Integer.parseInt(s), io.getInt("玩家資料." + p.getUniqueId() + ".NPC友好度." + s));
			}
		}

		if (io.getStringList("玩家資料." + p.getUniqueId() + ".已完成的對話") != null)
		{
			for (String s : io.getStringList("玩家資料." + p.getUniqueId() + ".已完成的對話"))
			{
				QuestConversation qc = QuestUtil.getConvByName(s);
				if (qc != null && !FinishedConversation.contains(s))
					FinishedConversation.add(qc);
			}
		}

		QuestUtil.info(p, Questi18n.localizeMessage("CommandInfo.PlayerLoadComplete"));
	}

	public void save()
	{
		Main.instance.configManager.getPlayerIO().set("玩家資料." + p.getUniqueId() + ".玩家ID", p.getName());
		for (QuestFinishData q : FinishedQuest)
		{
			String id = q.getQuest().getInternalID();
			Main.instance.configManager.getPlayerIO().set("玩家資料." + p.getUniqueId() + ".已完成的任務." + id + ".FinishedTimes", q.getFinishedTimes());
			Main.instance.configManager.getPlayerIO().set("玩家資料." + p.getUniqueId() + ".已完成的任務." + id + ".LastFinishTime", q.getLastFinish());
		}

		Main.instance.configManager.getPlayerIO().set("玩家資料." + p.getUniqueId() + ".任務進度", "");

		if (!CurrentQuest.isEmpty())
		{
			for (QuestProgress qp : CurrentQuest)
			{
				qp.save(Main.instance.configManager.getPlayerIO());
			}
		}

		for (int i : NPCfp.keySet())
		{
			Main.instance.configManager.getPlayerIO().set("玩家資料." + p.getUniqueId() + ".NPC友好度." + i, NPCfp.get(i));
		}

		Set<String> s = new HashSet<>();
		for (QuestConversation conv : FinishedConversation)
		{
			s.add(conv.getInternalID());
		}
		Main.instance.configManager.getPlayerIO().set("玩家資料." + p.getUniqueId() + ".已完成的對話", QuestUtil.convert(s));

		Main.instance.configManager.getPlayerIO().save();
	}

	public Player getPlayer()
	{
		return p;
	}

	public boolean hasFinished(Quest q)
	{
		for (QuestFinishData qd : FinishedQuest)
		{
			if (qd.getQuest().getInternalID().equals(q.getInternalID()))
				return true;
		}
		return false;
	}

	public boolean hasFinished(QuestConversation qc)
	{
		return FinishedConversation.contains(qc);
	}

	public QuestProgress getProgress(Quest q)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			if (q.getInternalID().equals(qp.getQuest().getInternalID()))
			{
				return qp;
			}
		}
		return null;
	}

	public Set<QuestProgress> getProgresses()
	{
		return CurrentQuest;
	}

	public int getNPCfp(int id)
	{
		if (!NPCfp.containsKey(id))
			NPCfp.put(id, 0);
		return NPCfp.get(id);
	}

	public void addNPCfp(int id, int value)
	{
		NPCfp.put(id, NPCfp.get(id) + value);
	}

	public void addFinishConversation(QuestConversation qc)
	{
		FinishedConversation.add(qc);
	}

	public void takeQuest(Quest q)
	{
		if (!canTake(q, true))
			return;
		if (!q.isCommandQuest())
		{
			if (!isNearNPC(q.getQuestNPC()))
			{
				QuestUtil.error(p, Questi18n.localizeMessage("CommandInfo.OutRanged"));
				return;
			}
		}
		if (CurrentQuest.size() + 1 > 4)
		{
			QuestUtil.info(p, Questi18n.localizeMessage("CommandInfo.QuestListFull"));
			return;
		}
		if (q.hasTrigger())
		{
			for (QuestTrigger t : q.getTriggers())
			{
				if (t.getType().equals(TriggerType.TRIGGER_ON_TAKE))
				{
					t.trigger(p);
					continue;
				}
				else
					if (t.getType().equals(TriggerType.TRIGGER_STAGE_START) && t.getCount() == 1)
					{
						t.trigger(p);
						continue;
					}
			}
		}
		CurrentQuest.add(new QuestProgress(q, p));
		QuestUtil.sendQuestTitle(p, q, QuestTitleEnum.ACCEPT);
	}

	public void forceQuit(Quest q)
	{
		removeProgress(q);
		QuestUtil.error(p, Questi18n.localizeMessage("CommandInfo.ForceQuit", q.getQuestName()));
		QuestUtil.error(p, Questi18n.localizeMessage("CommandInfo.ForceQuit2"));
		return;
	}

	public void quitQuest(Quest q)
	{
		for (QuestTrigger t : q.getTriggers())
		{
			if (t.getType().equals(TriggerType.TRIGGER_ON_QUIT))
			{
				t.trigger(p);
				continue;
			}
		}
		removeProgress(q);
		QuestUtil.sendQuestTitle(p, q, QuestTitleEnum.QUIT);
	}

	public List<QuestProgress> getNPCtoTalkWith(NPC npc)
	{
		List<QuestProgress> l = new ArrayList<>();
		for (QuestProgress qp : CurrentQuest)
		{
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.getObject() instanceof QuestObjectTalkToNPC && ((QuestObjectTalkToNPC) qop.getObject()).getTargetNPC().equals(npc)
						&& !qop.isFinished())
					l.add(qp);
			}
		}
		return l;
	}

	public boolean isNearNPC(NPC npc)
	{
		if (npc.getEntity().getLocation().distance(p.getLocation()) > 20)
			return false;
		else
			return true;
	}

	public void breakBlock(Material m)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectBreakBlock)
				{
					QuestObjectBreakBlock o = (QuestObjectBreakBlock) qop.getObject();
					if (o.getType().equals(m))
					{
						qop.setProgress(qop.getProgress() + 1);
						this.checkFinished(p, qp, qop, o);
						return;
					}
				}
			}
		}
	}

	public void talkToNPC(NPC npc)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectTalkToNPC)
				{
					if (((QuestObjectTalkToNPC) qop.getObject()).getTargetNPC().equals(npc))
					{
						if (!isNearNPC(npc))
						{
							QuestUtil.error(p, Questi18n.localizeMessage("CommandInfo.OutRanged"));
							return;
						}
						QuestObjectTalkToNPC o = (QuestObjectTalkToNPC) qop.getObject();
						this.checkFinished(p, qp, qop, o);
						return;
					}
				}
			}
		}
	}

	public void deliverItem(NPC npc)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectDeliverItem)
				{
					QuestObjectDeliverItem o = (QuestObjectDeliverItem) qop.getObject();
					ItemStack itemtoDeliver = Main.instance.handler.getItemInMainHand(p);
					int amountNeeded = o.getAmount() - qop.getProgress();
					if (o.getTargetNPC().equals(npc) && QuestUtil.compareItem(o.getItem(), itemtoDeliver, true))
					{
						if (itemtoDeliver.getAmount() > amountNeeded)
						{
							itemtoDeliver.setAmount(itemtoDeliver.getAmount() - amountNeeded);
							qop.setProgress(o.getAmount());
						}
						else
						{
							if (itemtoDeliver.getAmount() == amountNeeded)
								qop.setProgress(o.getAmount());
							else
								qop.setProgress(qop.getProgress() + itemtoDeliver.getAmount());
							// TODO Version Compatibility
							p.getInventory().setItemInMainHand(null);
						}
						this.checkFinished(p, qp, qop, o);
						return;
					}
				}
			}
		}
	}

	public void killEntity(Entity e)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectKillMob)
				{
					QuestObjectKillMob o = (QuestObjectKillMob) qop.getObject();
					if (o.hasCustomName())
					{
						if (e.getCustomName() == null || !e.getCustomName().equals(o.getCustomName()) || !e.getType().equals(o.getType()))
							return;
						else
						{
							qop.setProgress(qop.getProgress() + 1);
							this.checkFinished(p, qp, qop, o);
							return;
						}
					}
					else
					{
						if (e.getType().equals(o.getType()))
						{
							qop.setProgress(qop.getProgress() + 1);
							this.checkFinished(p, qp, qop, o);
							return;
						}
					}
				}
			}
		}
	}

	public void killMythicMob(MythicMob m)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectKillMob)
				{
					QuestObjectKillMob o = (QuestObjectKillMob) qop.getObject();
					if (o.isMythicObject())
					{
						if (o.getMythicMob().equals(m))
						{
							qop.setProgress(qop.getProgress() + 1);
							this.checkFinished(p, qp, qop, o);
							return;
						}
					}
					else
						continue;
				}
			}
		}
	}

	public void consumeItem(ItemStack is)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectConsumeItem)
				{
					QuestObjectConsumeItem o = (QuestObjectConsumeItem) qop.getObject();
					if (is.isSimilar(o.getItem()))
					{
						qop.setProgress(qop.getProgress() + 1);
						this.checkFinished(p, qp, qop, o);
						return;
					}
				}
			}
		}
	}

	public void reachLocation(Location l)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectReachLocation)
				{
					QuestObjectReachLocation o = (QuestObjectReachLocation) qop.getObject();
					if (l.getX() < (o.getLocation().getX() + o.getRadius()) && l.getX() > (o.getLocation().getX() - o.getRadius()))
						if (l.getY() < (o.getLocation().getY() + o.getRadius()) && l.getY() > (o.getLocation().getY() - o.getRadius()))
							if (l.getZ() < (o.getLocation().getZ() + o.getRadius()) && l.getZ() > (o.getLocation().getZ() - o.getRadius()))
							{
								qop.finish();
								this.checkFinished(p, qp, qop, o);
								return;
							}
				}
			}
		}
	}

	public void removeProgress(Quest q)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			if (q.getInternalID().equals(qp.getQuest().getInternalID()))
			{
				CurrentQuest.remove(qp);
				return;
			}
		}
	}

	public Set<QuestFinishData> getFinishQuests()
	{
		return FinishedQuest;
	}

	public QuestFinishData getFinishData(Quest q)
	{
		if (!hasFinished(q))
			return null;
		for (QuestFinishData qd : FinishedQuest)
		{
			if (qd.getQuest().getInternalID().equals(q.getInternalID()))
				return qd;
		}
		return null;
	}

	public void addFinishedQuest(Quest q)
	{
		if (hasFinished(q))
		{
			getFinishData(q).finish();
			return;
		}
		FinishedQuest.add(new QuestFinishData(q, 1, System.currentTimeMillis()));
		return;
	}

	public boolean isCurrentlyDoing(Quest q)
	{
		for (QuestProgress qp : CurrentQuest)
		{
			if (QuestVersion.weakValidate(qp.getQuest(), q))
				return true;
		}
		return false;
	}

	public boolean canTake(Quest q, boolean sendmsg)
	{
		if (isCurrentlyDoing(q))
		{
			if (sendmsg)
				QuestUtil.info(p, Questi18n.localizeMessage("CommandInfo.AlreadyTaken"));
			return false;
		}
		if (!q.isRedoable() && hasFinished(q))
		{
			if (sendmsg)
				QuestUtil.info(p, Questi18n.localizeMessage("CommandInfo.NotRedoable"));
			return false;
		}
		if (q.hasRequirement())
		{
			if (!q.meetRequirementWith(p).succeed())
			{
				if (sendmsg)
					QuestUtil.info(p, q.getFailMessage());
				return false;
			}
		}
		if (hasFinished(q))
		{
			long d = getDelay(getFinishData(q).getLastFinish(), q.getRedoDelay());
			if (d > 0)
			{
				if (sendmsg)
					QuestUtil.info(p, Questi18n.localizeMessage("CommandInfo.QuestCooldown", QuestUtil.convertTime(d)));
				return false;
			}
		}
		return true;
	}

	public static boolean hasConfigData(Player p)
	{
		return Main.instance.configManager.getPlayerIO().contains("玩家資料." + p.getUniqueId() + ".玩家ID");
	}

	public long getDelay(long last, long quest)
	{
		return quest - (System.currentTimeMillis() - last);
	}

	private boolean checkFinished(Player p, QuestProgress qp, QuestObjectProgress qop, SimpleQuestObject o)
	{
		qop.checkIfFinished();
		if (qop.isFinished())
		{
			qop.newConversation(p);
			QuestUtil.info(p, o.toPlainText() + " " + Questi18n.localizeMessage("CommandInfo.Finished"));
			qp.checkIfnextStage();
			return true;
		}
		else
		{
			if (o instanceof NumerableObject)
				QuestUtil.info(p, o.toPlainText() + " " + Questi18n.localizeMessage("CommandInfo.Progress", Integer.toString(qop.getProgress()),
						Integer.toString(((NumerableObject) o).getAmount())));
			else if (o instanceof QuestObjectTalkToNPC)
			{
				if (!qop.isFinished())
				{
					qop.openConversation(p);
					return false;
				}
			}
			return false;
		}
	}
}
