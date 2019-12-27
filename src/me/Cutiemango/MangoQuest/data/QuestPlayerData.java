package me.Cutiemango.MangoQuest.data;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import me.Cutiemango.MangoQuest.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.event.QuestFinishEvent;
import me.Cutiemango.MangoQuest.event.QuestObjectProgressEvent;
import me.Cutiemango.MangoQuest.event.QuestTakeEvent;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.QuestChoice.Choice;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.manager.RequirementManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.questobject.CustomQuestObject;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.interfaces.NPCObject;
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

	private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

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
				Quest q = QuestUtil.getQuest(index);
				if (q == null)
				{
					QuestChatManager.error(p, I18n.locMsg("CommandInfo.TargetProgressNotFound", index));
					save.removeSection("QuestProgress." + index);
					continue;
				}
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
					QuestObjectProgress qp = new QuestObjectProgress(ob, save.getInt("QuestProgress." + index + ".QuestObjectProgress." + t));
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
					DebugHandler.log(3, "[Player] Quest id=" + s + " is not correctly loaded, causing some player's data leak.");
					// QuestChatManager.error(p,
					// I18n.locMsg("CommandInfo.TargetProgressNotFound", s));
					// save.removeSection("FinishedQuest." + s);
					continue;
				}

				QuestFinishData qd = new QuestFinishData(QuestUtil.getQuest(s), save.getInt("FinishedQuest." + s + ".FinishedTimes"),
						save.getLong("FinishedQuest." + s + ".LastFinishTime"), save.getBoolean("FinishedQuest." + s + ".RewardTaken"));
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
				if (qc != null)
					finishedConversations.add(qc);
			}
		}

		save.save();

		if (ConfigSettings.POP_LOGIN_MESSAGE)
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
		DebugHandler.log(3, "[Listener] Player " + p.getName() + "'s friend point of NPC id=" + id + " raised by " + value);
	}

	public void setNPCfp(int id, int value)
	{
		friendPointStorage.put(id, value);
	}

	public boolean meetFriendPointReq(Choice choice)
	{
		for (Integer npc : choice.getFriendPointReq().keySet())
		{
			if (!friendPointStorage.containsKey(npc))
				return false;
			if (!(friendPointStorage.get(npc) >= choice.getFriendPointReq().get(npc)))
				return false;
		}
		return true;
	}

	public void addFinishConversation(QuestConversation qc)
	{
		finishedConversations.add(qc);
	}

	public boolean checkStartConv(Quest q)
	{
		if (ConversationManager.getStartConversation(q) == null)
			return true;
		StartTriggerConversation conv = ConversationManager.getStartConversation(q);
		if (!hasFinished(conv))
		{
			if (ConversationManager.isInConvProgress(p, conv))
				ConversationManager.openConversation(p, ConversationManager.getConvProgress(p));
			else
				ConversationManager.startConversation(p, conv);
			return false;
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

	public void forceTake(Quest q, boolean msg)
	{
		if (!checkQuestSize(true) || !checkStartConv(q))
			return;
		q.trigger(p, TriggerType.TRIGGER_ON_TAKE, -1);
		currentQuests.add(new QuestProgress(q, p));
		if (msg)
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.ForceTakeQuest", q.getQuestName()));
		DebugHandler.log(3, "[Listener] Player " + p.getName() + " accepted a new quest " + q.getQuestName());
		Bukkit.getPluginManager().callEvent(new QuestTakeEvent(p, q));
	}

	public void forceNextStage(Quest q, boolean msg)
	{
		if (!isCurrentlyDoing(q))
			return;
		QuestProgress qp = getProgress(q);
		qp.nextStage();
		if (msg)
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.ForceNextStage", q.getQuestName()));
		DebugHandler.log(3, "[Listener] Player " + p.getName() + "'s quest stage of quest " + q.getQuestName() + " shifted.");
	}

	public void forceFinishObj(Quest q, int id, boolean msg)
	{
		if (!isCurrentlyDoing(q))
			return;
		QuestProgress qp = getProgress(q);
		QuestObjectProgress qop = qp.getCurrentObjects().get(id - 1);
		if (qop != null)
		{
			qop.finish();
			this.checkFinished(qp, qop);
			if (msg)
				QuestChatManager.info(p, I18n.locMsg("CommandInfo.ForceFinishObject", qop.getObject().toPlainText()));
			DebugHandler.log(3, "[Listener] Player " + p.getName() + "'s quest object of quest " + q.getQuestName() + " finished");
		}
	}

	public void forceFinish(Quest q, boolean msg)
	{
		if (!isCurrentlyDoing(q))
			return;
		QuestProgress qp = getProgress(q);
		qp.finish();
		if (msg)
			QuestChatManager.info(p, I18n.locMsg("CommandInfo.ForceFinishQuest", q.getQuestName()));
		DebugHandler.log(3, "[Listener] Player " + p.getName() + "'s quest " + q.getQuestName() + " finished");
		Bukkit.getPluginManager().callEvent(new QuestFinishEvent(p, q));
	}

	public void forceQuit(Quest q, boolean msg)
	{
		if (!isCurrentlyDoing(q))
			return;
		q.trigger(p, TriggerType.TRIGGER_ON_QUIT, -1);
		removeProgress(q);
		if (msg)
			QuestChatManager.error(p, I18n.locMsg("CommandInfo.ForceQuitQuest", q.getQuestName()));
		DebugHandler.log(3, "[Listener] Player " + p.getName() + " quitted quest " + q.getQuestName());
	}

	public void quitQuest(Quest q)
	{
		forceQuit(q, false);
	}

	public List<QuestProgress> getNPCtoTalkWith(NPC npc)
	{
		ArrayList<QuestProgress> all = new ArrayList<>();
		currentQuests.stream()
				.filter(qp -> qp.getCurrentObjects().stream().anyMatch(qop -> checkNPC(qop, npc)))
				.forEach(all::add);
		return all;
	}

	public boolean isNearNPC(NPC npc)
	{
		if (npc == null || npc.getStoredLocation() == null)
			return true;
		return npc.getStoredLocation().distance(p.getLocation()) < 20;
	}
	
	public boolean checkPlayerInWorld(Quest q)
	{
		return q.hasWorldLimit() ? p.getWorld().getName().equals(q.getWorldLimit().getName()) : true;
	}
	
	public void objectSuccess(QuestProgress qp, QuestObjectProgress qop)
	{
		qop.setProgress(qop.getProgress() + 1);
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				checkFinished(qp, qop);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}

	public void breakBlock(Material m)
	{
		currentQuests.stream()
			.filter(qp -> checkPlayerInWorld(qp.getQuest()))
			.forEach(qp ->
				qp.getCurrentObjects().stream()
					.filter(qop -> checkBlock(qop, m))
					.collect(Collectors.toList())
					.forEach(qop -> objectSuccess(qp, qop)));
	}

	private boolean checkBlock(QuestObjectProgress qop, Material m)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectBreakBlock))
			return false;
		return ((QuestObjectBreakBlock) qop.getObject()).getType() == m;
	}

	public void talkToNPC(NPC npc)
	{
		currentQuests.stream()
			.filter(qp -> checkPlayerInWorld(qp.getQuest()))
			.forEach(qp ->
				qp.getCurrentObjects().stream()
					.filter(qop -> checkNPC(qop, npc))
					.collect(Collectors.toList())
					.forEach(qop -> checkFinished(qp, qop)));
	}

	private boolean checkNPC(QuestObjectProgress qop, NPC npc)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectTalkToNPC))
			return false;
		return ((QuestObjectTalkToNPC) qop.getObject()).getTargetNPC().getId() == npc.getId();
	}
	
	// Checks whether the player has submitted a correct item
	// Returns true if submitted at least 1 item.
	private boolean checkItem(NPC npc, QuestObjectProgress qop)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectDeliverItem))
			return false;
		QuestObjectDeliverItem o = (QuestObjectDeliverItem) qop.getObject();
		ItemStack itemToDeliver = p.getInventory().getItemInMainHand();
		int amountNeeded = o.getAmount() - qop.getProgress();
		if (o.getTargetNPC().equals(npc))
		{
			if (o.getItem().isSimilar(itemToDeliver) || (ConfigSettings.USE_WEAK_ITEM_CHECK && QuestValidater.weakItemCheck(itemToDeliver, o.getItem())))
			{
				if (itemToDeliver.getAmount() > amountNeeded)
				{
					itemToDeliver.setAmount(itemToDeliver.getAmount() - amountNeeded);
					qop.setProgress(o.getAmount());
				}
				else
				{
					p.getInventory().setItemInMainHand(null);
					if (itemToDeliver.getAmount() == amountNeeded)
						qop.setProgress(o.getAmount());
					else
						qop.setProgress(qop.getProgress() + itemToDeliver.getAmount());
				}
				return true;
			}
			DebugHandler.log(5, "[Listener] The item submitted is not correct.");
		}
		DebugHandler.log(5, "[Listener] NPC not correct.");
		return false;
	}

	public boolean deliverItem(NPC npc)
	{
		AtomicReference<Pair<QuestProgress, QuestObjectProgress>> any = new AtomicReference<>();
		currentQuests.stream()
			.filter(qp -> checkPlayerInWorld(qp.getQuest()))
			.forEach(qp ->
			{
				Optional<QuestObjectProgress> obj = qp.getCurrentObjects().stream()
					.filter(qop -> checkItem(npc, qop))
					.findFirst();
				obj.ifPresent(qop -> any.set(new Pair<>(qp, qop)));
			});
		if (any.get() != null)
		{
			Pair<QuestProgress, QuestObjectProgress> pair = any.get();
			checkFinished(pair.getKey(), pair.getValue());
			DebugHandler.log(5, "[Listener] Player " + p.getName() + " handed in one or more quest-requiring item(s).");
			return true;
		}
		else
		{
			DebugHandler.log(5, "[Listener] Player " + p.getName() + " did not hand in any quest-requiring items.");
			return false;
		}
	}
	
	private boolean checkMob(QuestObjectProgress qop, Entity e)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectKillMob))
			return false;
		QuestObjectKillMob o = (QuestObjectKillMob)qop.getObject();
		if (!e.getType().equals(o.getType()))
			return false;
		if (o.hasCustomName())
			return e.getCustomName() != null && e.getCustomName().equals(o.getCustomName());
		return true;
	}

	public void killEntity(Entity e)
	{
		currentQuests.stream()
			.filter(qp -> checkPlayerInWorld(qp.getQuest()))
			.forEach(qp ->
				qp.getCurrentObjects().stream()
					.filter(qop -> checkMob(qop, e))
					.collect(Collectors.toList())
					.forEach(qop -> objectSuccess(qp, qop)));
	}

	public void killMythicMob(String mtmMob)
	{
		currentQuests.stream()
				.filter(qp -> checkPlayerInWorld(qp.getQuest()))
				.forEach(qp ->
						qp.getCurrentObjects().stream()
								.filter(qop -> checkMythicMob(qop, mtmMob))
								.collect(Collectors.toList())
								.forEach(qop -> objectSuccess(qp, qop)));
	}
	private boolean checkMythicMob(QuestObjectProgress qop, String mtmMob)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectKillMob))
			return false;
		QuestObjectKillMob o = (QuestObjectKillMob)qop.getObject();
		if (!o.isMythicObject())
			return false;
		return o.getMythicMob().getInternalName().equals(mtmMob);
	}

	public void consumeItem(ItemStack is)
	{
		currentQuests.stream()
				.filter(qp -> checkPlayerInWorld(qp.getQuest()))
				.forEach(qp ->
						qp.getCurrentObjects().stream()
								.filter(qop -> checkConsume(qop, is))
								.collect(Collectors.toList())
								.forEach(qop -> objectSuccess(qp, qop)));
	}

	private boolean checkConsume(QuestObjectProgress qop, ItemStack is)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectConsumeItem))
			return false;
		QuestObjectConsumeItem o = (QuestObjectConsumeItem) qop.getObject();
		return o.getItem().isSimilar(is) || (ConfigSettings.USE_WEAK_ITEM_CHECK && QuestValidater.weakItemCheck(is, o.getItem()));
	}

	public void reachLocation(Location l)
	{
		currentQuests.stream()
				.filter(qp -> checkPlayerInWorld(qp.getQuest()))
				.forEach(qp ->
						qp.getCurrentObjects().stream()
								.filter(qop -> checkLocation(qop, l))
								.collect(Collectors.toList())
								.forEach(qop -> objectSuccess(qp, qop)));
	}

	private boolean checkLocation(QuestObjectProgress qop, Location l)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectReachLocation))
			return false;
		QuestObjectReachLocation o = (QuestObjectReachLocation) qop.getObject();
		if (l.getX() < (o.getLocation().getX() + o.getRadius()) && l.getX() > (o.getLocation().getX() - o.getRadius()))
			if (l.getY() < (o.getLocation().getY() + o.getRadius()) && l.getY() > (o.getLocation().getY() - o.getRadius()))
				return l.getZ() < (o.getLocation().getZ() + o.getRadius()) && l.getZ() > (o.getLocation().getZ() - o.getRadius());
		return false;
	}

	public void removeProgress(Quest q)
	{
		currentQuests.stream()
				.filter(qp -> QuestValidater.weakValidate(q, qp.getQuest()))
				.findFirst().ifPresent(qp -> currentQuests.remove(qp));
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
		for (QuestFinishData data : finishedQuests)
		{
			if (!data.isRewardTaken())
			{
				Quest q = data.getQuest();
				if (!q.isCommandQuest())
					QuestChatManager.info(p, I18n.locMsg("QuestReward.RewardUnclaimed", q.getQuestNPC().getName()));
			}
		}
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
			if (!RequirementManager.meetRequirementWith(p, q.getRequirements()).succeed())
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
		currentQuests.stream()
				.filter(qp -> qp.getQuest().isTimeLimited() && System.currentTimeMillis() > qp.getQuest().getTimeLimit() + qp.getTakeTime())
				.forEach(qp ->
				{
					forceQuit(qp.getQuest(), false);
					QuestChatManager.info(p, I18n.locMsg("QuestJourney.QuestFailed", qp.getQuest().getQuestName()));
					DebugHandler.log(3, "[Listener] Player " + p.getName() + " failed quest " + qp.getQuest().getQuestName() + " because time is due.");
				});
	}

	public long getDelay(long last, long quest)
	{
		return quest - (System.currentTimeMillis() - last);
	}

	public Scoreboard getScoreboard()
	{
		return scoreboard;
	}

	public void checkFinished(QuestProgress qp, QuestObjectProgress qop)
	{
		SimpleQuestObject o = qop.getObject();
		qop.checkIfFinished();
		if (qop.isFinished())
		{
			QuestChatManager.info(p, I18n.locMsg("QuestJourney.ProgressText", qp.getQuest().getQuestName()) + o.toDisplayText() + " "
					+ I18n.locMsg("CommandInfo.Finished"));
			DebugHandler.log(2, "[Listener] Player " + p.getName() + "'s quest " + qp.getQuest().getQuestName() + " finished an object of "
					+ qop.getObject().getConfigString());
			qp.checkIfnextStage();
			Bukkit.getPluginManager().callEvent(new QuestObjectProgressEvent(this, qp.getQuest(), qop.getObject()));
		}
		else
		{
			if (o instanceof NumerableObject)
				QuestChatManager.info(p, I18n.locMsg("QuestJourney.ProgressText", qp.getQuest().getQuestName()) + o.toDisplayText() + " " + I18n
						.locMsg("CommandInfo.Progress", Integer.toString(qop.getProgress()), Integer.toString(((NumerableObject) o).getAmount())));
			else
				if (o instanceof QuestObjectReachLocation)
				{
					if (qop.getProgress() >= 1)
					{
						qop.finish();
						new BukkitRunnable()
						{
							@Override
							public void run()
							{
								checkFinished(qp, qop);
							}
						}.runTaskLater(Main.getInstance(), 1L);
					}
				}
				else if (o instanceof CustomQuestObject)
					QuestChatManager.info(p,
							I18n.locMsg("QuestJourney.ProgressText", qp.getQuest().getQuestName()) + ((CustomQuestObject) o).getProgressText(qop));
				else
					if (o instanceof QuestObjectTalkToNPC)
					{
						if (qop.getObject().hasConversation())
						{
							qop.openConversation(p);
							DebugHandler.log(2, "[Listener] Player " + p.getName() + "'s quest " + qp.getQuest().getQuestName()
									+ " has unfinished conversation.");
						}
						else
						{
							qop.finish();
							new BukkitRunnable()
							{
								@Override
								public void run()
								{
									checkFinished(qp, qop);
								}
							}.runTaskLater(Main.getInstance(), 1L);
						}
					}
		}
	}
}
