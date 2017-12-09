package me.Cutiemango.MangoQuest.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.manager.RequirementManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.TriggerType;
import me.Cutiemango.MangoQuest.questobject.CustomQuestObject;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectConsumeItem;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectDeliverItem;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectTalkToNPC;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;

public class QuestPlayerData
{
	private Player p;
	private Set<QuestProgress> currentQuests = new HashSet<>();
	private Set<QuestFinishData> finishedQuests = new HashSet<>();
	private Set<QuestConversation> finishedConversations = new HashSet<>();
	
	private QuestIO save;

	private HashMap<Integer, Integer> friendPointStorage = new HashMap<>();

	public QuestPlayerData(Player p)
	{
		this.p = p;
		save = new QuestIO(p);
		load();
		save();
	}
	
	public void load()
	{
		save.set("LastKnownID", p.getName());

		if (save.isSection("QuestProgress"))
		{
			for (String index : save.getSection("QuestProgress"))
			{
				if (QuestUtil.getQuest(index) == null)
				{
					QuestChatManager.error(p, I18n.locMsg("CommandInfo.TargetProgressNotFound", index));
					save.removeSection("QuestProgress." + index);
					continue;
				}
				Quest q = QuestUtil.getQuest(index);
				if (!(q.getVersion().getVersion() == save.getLong("QuestProgress." + q.getInternalID() + ".Version")))
				{
					QuestChatManager.error(p, I18n.locMsg("CommandInfo.OutdatedQuestVersion", index));
					save.removeSection("QuestProgress." + index);
					continue;
				}

				int t = 0;
				int s = save.getInt("QuestProgress." + index + ".QuestStage");
				List<QuestObjectProgress> qplist = new ArrayList<>();
				for (SimpleQuestObject ob : q.getStage(s).getObjects())
				{
					QuestObjectProgress qp = new QuestObjectProgress(ob,
							save.getInt("QuestProgress." + index + ".QuestObjectProgress." + t));
					qp.checkIfFinished();
					qplist.add(qp);
					t++;
				}
				QuestProgress qp = new QuestProgress(q, p, s, qplist);
				if (save.getLong("QuestProgress." + index + ".TakeStamp") != 0)
					qp.setTakeTime(save.getLong("QuestProgress." + index + ".TakeStamp"));
				currentQuests.add(qp);
			}
		}

		if (save.isSection("FinishedQuest"))
		{
			for (String s : save.getSection("FinishedQuest"))
			{
				if (QuestUtil.getQuest(s) == null)
				{
					QuestChatManager.error(p, I18n.locMsg("CommandInfo.TargetProgressNotFound", s));
					save.removeSection("FinishedQuest." + s);
					continue;
				}

				boolean rewardTaken = save.getBoolean("FinishedQuest." + s + ".RewardTaken");
				
				QuestFinishData qd = new QuestFinishData(QuestUtil.getQuest(s),
						save.getInt("FinishedQuest." + s + ".FinishedTimes"),
						save.getLong("FinishedQuest." + s + ".LastFinishTime"),
						rewardTaken);
				finishedQuests.add(qd);
			}
		}

		if (save.isSection("FriendPoint"))
		{
			for (String s : save.getSection("FriendPoint"))
			{
				friendPointStorage.put(Integer.parseInt(s), save.getInt("FriendPoint." + s));
			}
		}

		if (save.getStringList("FinishedConversation") != null)
		{
			for (String s : save.getStringList("FinishedConversation"))
			{
				QuestConversation qc = ConversationManager.getConversation(s);
				if (qc != null && !finishedConversations.contains(s))
					finishedConversations.add(qc);
			}
		}

		save.save();

		QuestChatManager.info(p, I18n.locMsg("CommandInfo.PlayerLoadComplete"));
	}

	public void save()
	{
		save.set("LastKnownID", p.getName());
		for (QuestFinishData q : finishedQuests)
		{
			String id = q.getQuest().getInternalID();
			save.set("FinishedQuest." + id + ".FinishedTimes", q.getFinishedTimes());
			save.set("FinishedQuest." + id + ".LastFinishTime", q.getLastFinish());
			save.set("FinishedQuest." + id + ".RewardTaken", q.isRewardTaken());
		}

		save.set("QuestProgress", "");

		if (!currentQuests.isEmpty())
		{
			for (QuestProgress qp : currentQuests)
			{
				qp.save(save);
			}
		}

		for (int i : friendPointStorage.keySet())
		{
			save.set("FriendPoint." + i, friendPointStorage.get(i));
		}

		Set<String> s = new HashSet<>();
		for (QuestConversation conv : finishedConversations)
		{
			s.add(conv.getInternalID());
		}
		save.set("FinishedConversation", QuestUtil.convert(s));

		save.save();
	}

	public Player getPlayer()
	{
		return p;
	}

	public boolean hasFinished(Quest q)
	{
		if (q == null)
			 return false;
		for (QuestFinishData qd : finishedQuests)
		{
			if (qd.getQuest() == null)
				continue;
			if (qd.getQuest().getInternalID().equals(q.getInternalID()))
				return true;
		}
		return false;
	}

	public boolean hasFinished(QuestConversation qc)
	{
		return finishedConversations.contains(qc);
	}

	public QuestProgress getProgress(Quest q)
	{
		for (QuestProgress qp : currentQuests)
		{
			if (q.getInternalID().equals(qp.getQuest().getInternalID()))
				return qp;
		}
		return null;
	}

	public Set<QuestProgress> getProgresses()
	{
		return currentQuests;
	}

	public int getNPCfp(int id)
	{
		if (!friendPointStorage.containsKey(id))
			friendPointStorage.put(id, 0);
		return friendPointStorage.get(id);
	}

	public void addNPCfp(int id, int value)
	{
		if (!friendPointStorage.containsKey(id))
			friendPointStorage.put(id, 0);
		friendPointStorage.put(id, friendPointStorage.get(id) + value);
	}
	
	public void setNPCfp(int id, int value)
	{
		friendPointStorage.put(id, value);
	}

	public void addFinishConversation(QuestConversation qc)
	{
		finishedConversations.add(qc);
	}
	
	public boolean checkStartConv(Quest q)
	{
		if (ConversationManager.getStartConversation(q) != null)
		{
			StartTriggerConversation conv = ConversationManager.getStartConversation(q);
			if (!hasFinished(conv))
			{
				if (ConversationManager.isInConvProgress(p, conv))
					ConversationManager.openConversation(p, ConversationManager.getConvProgress(p));
				else
					ConversationManager.startConversation(p, conv);
				return false;
			}
		}
		return true;
	}

	public boolean checkQuestSize(boolean msg)
	{
		if (currentQuests.size() + 1 > ConfigSettings.MAXIUM_QUEST_AMOUNT)
		{
			if (msg)
				QuestChatManager.info(p, I18n.locMsg("CommandInfo.QuestListFull"));
			return false;
		}
		return true;
	}

	public void takeQuest(Quest q, boolean checkConv)
	{
		if (!canTake(q, true))
			return;
		if (!q.isCommandQuest())
		{
			if (!isNearNPC(q.getQuestNPC()))
			{
				QuestChatManager.error(p, I18n.locMsg("CommandInfo.OutRanged"));
				return;
			}
		}
		if (!checkQuestSize(true))
			return;
		if (checkConv && !checkStartConv(q))
			return;
		forceTake(q, false);
	}
	
	public void forceTake(Quest q, boolean msg){
		if (!checkQuestSize(true))
			return;
		q.trigger(p, 0, TriggerType.TRIGGER_ON_TAKE, -1);
		currentQuests.add(new QuestProgress(q, p));
		if (msg)
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.ForceTakeQuest", q.getQuestName()));
		return;
	}
	
	public void forceNextStage(Quest q, boolean msg)
	{
		if (!isCurrentlyDoing(q))
			return;
		QuestProgress qp = getProgress(q);
		qp.nextStage();
		if (msg)
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.ForceNextStage", q.getQuestName()));
		return;
	}
	
	public void forceFinishObj(Quest q, int id, boolean msg){
		if (!isCurrentlyDoing(q))
			return;
		QuestProgress qp = getProgress(q);
		QuestObjectProgress qop = qp.getCurrentObjects().get(id-1);
		if (qop != null)
		{
			qop.finish();
			this.checkFinished(qp, qop);
			if (msg)
				QuestChatManager.info(p, I18n.locMsg("CommandInfo.ForceFinishObject", qop.getObject().toPlainText()));
			return;
		}
	}
	
	public void forceFinish(Quest q, boolean msg){
		if (!isCurrentlyDoing(q))
			return;
		QuestProgress qp = getProgress(q);
		qp.finish();
		if (msg)
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.ForceFinishQuest", q.getQuestName()));
		return;
	}
	
	public void forceQuit(Quest q, boolean msg)
	{
		if (!isCurrentlyDoing(q))
			return;
		q.trigger(p, 0, TriggerType.TRIGGER_ON_QUIT, -1);
		removeProgress(q);
		if (msg)
			QuestChatManager.error(p, I18n.locMsg("CommandInfo.ForceQuitQuest", q.getQuestName()));
		return;
	}

	public void quitQuest(Quest q)
	{
		forceQuit(q, false);
	}

	public List<QuestProgress> getNPCtoTalkWith(NPC npc)
	{
		List<QuestProgress> l = new ArrayList<>();
		for (QuestProgress qp : currentQuests)
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

	public void breakBlock(Material m, short subID)
	{
		for (QuestProgress qp : currentQuests)
		{
			if (qp.getQuest().hasWorldLimit() && !p.getWorld().getName().equals(qp.getQuest().getWorldLimit().getName()))
				continue;
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectBreakBlock)
				{
					QuestObjectBreakBlock o = (QuestObjectBreakBlock) qop.getObject();
					if (o.getType().equals(m) && o.getSubID() == subID)
					{
						qop.setProgress(qop.getProgress() + 1);
						this.checkFinished(qp, qop);
						return;
					}
				}
			}
		}
	}

	public void talkToNPC(NPC npc)
	{
		for (QuestProgress qp : currentQuests)
		{
			if (qp.getQuest().hasWorldLimit() && !p.getWorld().getName().equals(qp.getQuest().getWorldLimit().getName()))
				continue;
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
							QuestChatManager.error(p, I18n.locMsg("CommandInfo.OutRanged"));
							return;
						}
						this.checkFinished(qp, qop);
						return;
					}
				}
			}
		}
	}

	public boolean deliverItem(NPC npc)
	{
		for (QuestProgress qp : currentQuests)
		{
			if (qp.getQuest().hasWorldLimit() && !p.getWorld().getName().equals(qp.getQuest().getWorldLimit().getName()))
				continue;
			for (QuestObjectProgress qop : qp.getCurrentObjects())
			{
				if (qop.isFinished())
					continue;
				if (qop.getObject() instanceof QuestObjectDeliverItem)
				{
					QuestObjectDeliverItem o = (QuestObjectDeliverItem) qop.getObject();
					ItemStack itemtoDeliver = Main.instance.handler.getItemInMainHand(p);
					int amountNeeded = o.getAmount() - qop.getProgress();
					if (o.getTargetNPC().equals(npc) && QuestValidater.compareItem(o.getItem(), itemtoDeliver, true))
					{
						if (itemtoDeliver.getAmount() > amountNeeded)
						{
							itemtoDeliver.setAmount(itemtoDeliver.getAmount() - amountNeeded);
							qop.setProgress(o.getAmount());
						}
						else
						{
							 Main.instance.handler.setItemInMainHand(p, null);
							if (itemtoDeliver.getAmount() == amountNeeded)
								qop.setProgress(o.getAmount());
							else
								qop.setProgress(qop.getProgress() + itemtoDeliver.getAmount());
						}
						this.checkFinished(qp, qop);
						return true;
					}
				}
			}
		}
		return false;
	}

	public void killEntity(Entity e)
	{
		for (QuestProgress qp : currentQuests)
		{
			if (qp.getQuest().hasWorldLimit() && !p.getWorld().getName().equals(qp.getQuest().getWorldLimit().getName()))
				continue;
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
							this.checkFinished(qp, qop);
							return;
						}
					}
					else
					{
						if (e.getType().equals(o.getType()))
						{
							qop.setProgress(qop.getProgress() + 1);
							this.checkFinished(qp, qop);
							return;
						}
					}
				}
			}
		}
	}

	public void killMythicMob(MythicMob m)
	{
		for (QuestProgress qp : currentQuests)
		{
			if (qp.getQuest().hasWorldLimit() && !p.getWorld().getName().equals(qp.getQuest().getWorldLimit().getName()))
				continue;
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
							this.checkFinished(qp, qop);
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
		for (QuestProgress qp : currentQuests)
		{
			if (qp.getQuest().hasWorldLimit() && !p.getWorld().getName().equals(qp.getQuest().getWorldLimit().getName()))
				continue;
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
						this.checkFinished(qp, qop);
						return;
					}
				}
			}
		}
	}

	public void reachLocation(Location l)
	{
		for (QuestProgress qp : currentQuests)
		{
			if (qp.getQuest().hasWorldLimit() && !p.getWorld().getName().equals(qp.getQuest().getWorldLimit().getName()))
				continue;
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
								this.checkFinished(qp, qop);
								return;
							}
				}
			}
		}
	}

	public void removeProgress(Quest q)
	{
		Iterator<QuestProgress> it = currentQuests.iterator();
		while (it.hasNext())
		{
			QuestProgress qp = it.next();
			if (q.getInternalID().equals(qp.getQuest().getInternalID()))
			{
				currentQuests.remove(qp);
				return;
			}
		}
	}

	public Set<QuestFinishData> getFinishQuests()
	{
		return finishedQuests;
	}
	
	public QuestIO getSaveFile()
	{
		return save;
	}
	
	public String getQuestDisplayFormat(Quest q)
	{
		if (canTake(q, false))
		{
			if (hasFinished(q))
				return I18n.locMsg("QuestGUI.RedoableQuestSymbol").replaceAll("§0", "§f") + ChatColor.BOLD + q.getQuestName();
			else
				return I18n.locMsg("QuestGUI.NewQuestSymbol").replaceAll("§0", "§f") + ChatColor.BOLD + q.getQuestName();
		}
		else
		{
			for (QuestObjectProgress op : getProgress(q).getCurrentObjects())
			{
				if (op.getObject() instanceof QuestObjectTalkToNPC)
					return I18n.locMsg("QuestGUI.QuestReturnSymbol").replaceAll("§0", "§f") + ChatColor.BOLD + q.getQuestName();
			}
			return I18n.locMsg("QuestGUI.QuestDoingSymbol").replaceAll("§0", "§f") + ChatColor.BOLD + q.getQuestName();
		}
	}

	public QuestFinishData getFinishData(Quest q)
	{
		if (!hasFinished(q))
			return null;
		for (QuestFinishData qd : finishedQuests)
		{
			if (qd.getQuest().getInternalID().equals(q.getInternalID()))
				return qd;
		}
		return null;
	}

	public void addFinishedQuest(Quest q, boolean reward)
	{
		if (hasFinished(q))
		{
			getFinishData(q).finish();
			return;
		}
		finishedQuests.add(new QuestFinishData(q, 1, System.currentTimeMillis(), reward));
		return;
	}
	
	public boolean hasTakenReward(Quest q)
	{
		if (!hasFinished(q))
			return false;
		QuestFinishData data = getFinishData(q);
		return data.isRewardTaken();
	}
	
	public void rewardClaimed(Quest q)
	{
		getFinishData(q).setRewardTaken(true);
		save();
	}
	
	public void checkRewardUnclaimed()
	{
		Set<NPC> set = new HashSet<>();
		for (QuestFinishData data : finishedQuests)
		{
			if (!data.isRewardTaken())
			{
				Quest q = data.getQuest();
				if (!q.isCommandQuest())
				{
					QuestChatManager.info(p, I18n.locMsg("QuestReward.RewardUnclaimed", q.getQuestNPC().getName()));
					set.add(q.getQuestNPC());
				}
			}
		}
	}
	
	public Set<Quest> getUnclaimedReward()
	{
		Set<Quest> set = new HashSet<>();
		for (QuestFinishData data : finishedQuests)
		{
			if (!data.isRewardTaken())
				set.add(data.getQuest());
		}
		return set;
	}

	public boolean isCurrentlyDoing(Quest q)
	{
		for (QuestProgress qp : currentQuests)
		{
			if (QuestValidater.weakValidate(qp.getQuest(), q))
				return true;
		}
		return false;
	}

	public boolean canTake(Quest q, boolean sendmsg)
	{
		if (isCurrentlyDoing(q))
		{
			if (sendmsg)
				QuestChatManager.info(p, I18n.locMsg("CommandInfo.AlreadyTaken"));
			return false;
		}
		if (!q.isRedoable() && hasFinished(q))
		{
			if (sendmsg)
				QuestChatManager.info(p, I18n.locMsg("CommandInfo.NotRedoable"));
			return false;
		}
		if (q.usePermission() && !p.hasPermission("MangoQuest.takeQuest." + q.getInternalID()))
		{
			if (sendmsg)
				QuestChatManager.info(p, I18n.locMsg("CommandInfo.CommandInfo.NoPermTakeQuest"));
			return false;
		}
		if (q.hasRequirement())
		{
			if (!RequirementManager.meetRequirementWith(p, q).succeed())
			{
				if (sendmsg)
					QuestChatManager.info(p, q.getFailMessage());
				return false;
			}
		}
		if (hasFinished(q))
		{
			long d = getDelay(getFinishData(q).getLastFinish(), q.getRedoDelay());
			if (d > 0)
			{
				if (sendmsg)
					QuestChatManager.info(p, I18n.locMsg("CommandInfo.QuestCooldown", QuestUtil.convertTime(d)));
				return false;
			}
		}
		return true;
	}
	
	public void checkQuestFail()
	{
		Iterator<QuestProgress> it = currentQuests.iterator();
		while(it.hasNext())
		{
			QuestProgress qp = it.next();
			if (!qp.getQuest().isTimeLimited())
				continue;
			if (System.currentTimeMillis() > qp.getQuest().getTimeLimit() + qp.getTakeTime())
			{
				forceQuit(qp.getQuest(), false);
				QuestChatManager.info(p, "&c由於時間已到，任務 &r" + qp.getQuest().getQuestName() + " &c已經失敗！");
			}
			else continue;
		}
		return;
	}

	public long getDelay(long last, long quest)
	{
		return quest - (System.currentTimeMillis() - last);
	}

	public boolean checkFinished(QuestProgress qp, QuestObjectProgress qop)
	{
		SimpleQuestObject o = qop.getObject();
		qop.checkIfFinished();
		if (qop.isFinished())
		{
			if (!(o instanceof QuestObjectTalkToNPC))
				qop.newConversation(p);
			QuestChatManager.info(p,"&f「" + qp.getQuest().getQuestName() + "&f」" + o.toDisplayText() + " " + I18n.locMsg("CommandInfo.Finished"));
			qp.checkIfnextStage();
			return true;
		}
		else
		{
			if (o instanceof NumerableObject)
				QuestChatManager.info(p, "&f「" + qp.getQuest().getQuestName() + "&f」" + o.toDisplayText() + " " + I18n.locMsg("CommandInfo.Progress", Integer.toString(qop.getProgress()),
						Integer.toString(((NumerableObject) o).getAmount())));
			else if (o instanceof CustomQuestObject)
				QuestChatManager.info(p, "&f「" + qp.getQuest().getQuestName() + "&f」" + ((CustomQuestObject)o).getProgressText(qop));
			else
				if (o instanceof QuestObjectTalkToNPC)
				{
					if (qop.getObject().hasConversation())
					{
						qop.openConversation(p);
						return false;
					}
					else
					{
						qop.finish();
						checkFinished(qp, qop);
						return true;
					}
				}
			return false;
		}
	}
}
