package me.Cutiemango.MangoQuest.data;

import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Pair;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.ConversationManager;
import me.Cutiemango.MangoQuest.conversation.QuestChoice.Choice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.event.QuestFinishEvent;
import me.Cutiemango.MangoQuest.event.QuestObjectProgressEvent;
import me.Cutiemango.MangoQuest.event.QuestTakeEvent;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.manager.RequirementManager;
import me.Cutiemango.MangoQuest.manager.TimeHandler;
import me.Cutiemango.MangoQuest.manager.database.DatabaseLoader;
import me.Cutiemango.MangoQuest.manager.database.DatabaseSaver;
import me.Cutiemango.MangoQuest.manager.mongodb.MongodbLoader;
import me.Cutiemango.MangoQuest.manager.mongodb.MongodbSaver;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.trigger.TriggerType;
import me.Cutiemango.MangoQuest.questobject.CustomQuestObject;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;
import me.Cutiemango.MangoQuest.questobject.objects.*;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class QuestPlayerData
{
	private int PDID;
	private Player owner;
	private QuestIO save;

	private Set<QuestProgress> currentQuests = new HashSet<>();
	private Set<QuestFinishData> finishedQuests = new HashSet<>();
	private Set<String> finishedConversations = new HashSet<>();

	private HashMap<Integer, Integer> friendPointStorage = new HashMap<>();

	private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

	public QuestPlayerData(Player p)
	{
		owner = p;
	}

	public void loadExistingData(Set<QuestProgress> q, Set<QuestFinishData> fd, Set<String> convs, HashMap<Integer, Integer> map, int id)
	{
		currentQuests = q;
		finishedQuests = fd;
		finishedConversations = convs;
		friendPointStorage = map;
		PDID = id;
	}

	public void load(ConfigSettings.SaveType saveType)
	{
		switch(saveType)
		{
			case YML:
				loadFromYml();
				break;
			case SQL:
				DatabaseLoader.loadPlayer(this);
				break;
			case MONGODB:
				MongodbLoader.loadPlayer(this);
				break;
		}
	}

	public void save()
	{
		switch(ConfigSettings.SAVE_TYPE)
		{
			case YML:
				saveToYml();
				break;
			case SQL:
				DatabaseSaver.savePlayerData(this);
				break;
			case MONGODB:
				MongodbSaver.savePlayerData(this);
				break;
		}
	}

	public void loadFromYml()
	{
		save = new QuestIO(owner);
		save.set("LastKnownID", owner.getName());

		if (save.isSection("QuestProgress"))
		{
			for (String index : save.getSection("QuestProgress"))
			{
				Quest q = QuestUtil.getQuest(index);
				if (q == null)
				{
					QuestChatManager.error(owner, I18n.locMsg("CommandInfo.TargetProgressNotFound", index));
					save.removeSection("QuestProgress." + index);
					continue;
				}

				if (q.getVersion().getTimeStamp() != save.getLong("QuestProgress." + q.getInternalID() + ".Version"))
				{
					QuestChatManager.error(owner, I18n.locMsg("CommandInfo.OutdatedQuestVersion", index));
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
				QuestProgress qp = new QuestProgress(q, owner, s, qplist, save.getLong("QuestProgress." + index + ".TakeStamp"));
				currentQuests.add(qp);
			}
		}

		if (save.isSection("FinishedQuest"))
		{
			for (String s : save.getSection("FinishedQuest"))
			{
				if (QuestUtil.getQuest(s) == null)
				{
					DebugHandler.log(5, "[Player] Quest id=%s is not correctly loaded, causing some player's data leak.", s);
					continue;
				}

				QuestFinishData qd = new QuestFinishData(QuestUtil.getQuest(s), save.getInt("FinishedQuest." + s + ".FinishedTimes"),
						save.getLong("FinishedQuest." + s + ".LastFinishTime"), save.getBoolean("FinishedQuest." + s + ".RewardTaken"));
				finishedQuests.add(qd);
			}
		}

		if (save.isSection("FriendPoint"))
			for (String s : save.getSection("FriendPoint"))
				friendPointStorage.put(Integer.parseInt(s), save.getInt("FriendPoint." + s));

		if (save.getStringList("FinishedConversation") != null)
		{
			for (String s : save.getStringList("FinishedConversation"))
			{
				QuestConversation qc = ConversationManager.getConversation(s);
				if (qc != null)
					finishedConversations.add(qc.getInternalID());
			}
		}

		save.save();

		if (ConfigSettings.POP_LOGIN_MESSAGE)
			QuestChatManager.info(owner, I18n.locMsg("CommandInfo.PlayerLoadComplete"));
	}

	public void saveToYml() {
		save.set("LastKnownID", owner.getName());
		for (QuestFinishData q : finishedQuests)
		{
			String id = q.getQuest().getInternalID();
			save.set("FinishedQuest." + id + ".FinishedTimes", q.getFinishedTimes());
			save.set("FinishedQuest." + id + ".LastFinishTime", q.getLastFinish());
			save.set("FinishedQuest." + id + ".RewardTaken", q.isRewardTaken());
		}

		save.set("QuestProgress", "");

		if (!currentQuests.isEmpty())
			for (QuestProgress qp : currentQuests)
				qp.save(save);

		for (int i : friendPointStorage.keySet())
			save.set("FriendPoint." + i, friendPointStorage.get(i));

		save.set("FinishedConversation", QuestUtil.convert(new HashSet<>(finishedConversations)));
		save.save();
	}

	public Player getPlayer()
	{
		return owner;
	}

	public int getPDID() {
		return PDID;
	}

	public HashMap<Integer, Integer> getFriendPointStorage() {
		return friendPointStorage;
	}

	public Set<String> getFinishedConversations() {
		return finishedConversations;
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
		return finishedConversations.contains(qc.getInternalID());
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
		DebugHandler.log(3, "[Listener] Player " + owner.getName() + "'s friend point of NPC id=" + id + " raised by " + value);
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
		finishedConversations.add(qc.getInternalID());
	}

	public boolean checkStartConv(Quest q)
	{
		if (ConversationManager.getStartConversation(q) == null)
			return true;
		StartTriggerConversation conv = ConversationManager.getStartConversation(q);
		if (!hasFinished(conv))
		{
			if (ConversationManager.isInConvProgress(owner, conv))
				ConversationManager.openConversation(owner, ConversationManager.getConvProgress(owner));
			else
				ConversationManager.startConversation(owner, conv);
			return false;
		}
		return true;
	}

	public boolean checkQuestSize(boolean msg)
	{
		if (currentQuests.size() + 1 > ConfigSettings.MAXIMUM_QUEST_AMOUNT)
		{
			if (msg)
				QuestChatManager.info(owner, I18n.locMsg("CommandInfo.QuestListFull"));
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
				QuestChatManager.error(owner, I18n.locMsg("CommandInfo.OutRanged"));
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
		q.trigger(owner, TriggerType.TRIGGER_ON_TAKE, -1);
		currentQuests.add(new QuestProgress(q, owner));
		if (msg)
			QuestChatManager.info(owner, I18n.locMsg("CommandInfo.ForceTakeQuest", q.getQuestName()));
		DebugHandler.log(3, "[Listener] Player " + owner.getName() + " accepted a new quest " + q.getQuestName());
		Bukkit.getPluginManager().callEvent(new QuestTakeEvent(owner, q));
	}

	public void forceNextStage(Quest q, boolean msg)
	{
		if (!isCurrentlyDoing(q))
			return;
		QuestProgress qp = getProgress(q);
		qp.nextStage();
		if (msg)
			QuestChatManager.info(owner, I18n.locMsg("CommandInfo.ForceNextStage", q.getQuestName()));
		DebugHandler.log(3, "[Listener] Player " + owner.getName() + "'s quest stage of quest " + q.getQuestName() + " shifted.");
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
				QuestChatManager.info(owner, I18n.locMsg("CommandInfo.ForceFinishObject", qop.getObject().toPlainText()));
			DebugHandler.log(3, "[Listener] Player " + owner.getName() + "'s quest object of quest " + q.getQuestName() + " finished");
		}
	}

	public void forceFinish(Quest q, boolean msg)
	{
		if (!isCurrentlyDoing(q))
			return;
		QuestProgress qp = getProgress(q);
		qp.finish();
		if (msg)
			QuestChatManager.info(owner, I18n.locMsg("CommandInfo.ForceFinishQuest", q.getQuestName()));
		DebugHandler.log(3, "[Listener] Player " + owner.getName() + "'s quest " + q.getQuestName() + " finished");
		Bukkit.getPluginManager().callEvent(new QuestFinishEvent(owner, q));
	}

	public void forceQuit(Quest q, boolean msg)
	{
		if (!isCurrentlyDoing(q))
			return;
		q.trigger(owner, TriggerType.TRIGGER_ON_QUIT, -1);
		removeProgress(q);
		if (msg)
			QuestChatManager.error(owner, I18n.locMsg("CommandInfo.ForceQuitQuest", q.getQuestName()));
		DebugHandler.log(3, "[Listener] Player " + owner.getName() + " quitted quest " + q.getQuestName());
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
		return npc.getStoredLocation().getWorld().getName().equals(owner.getWorld().getName()) && npc.getStoredLocation().distance(owner.getLocation()) < 20;
	}
	
	public boolean checkPlayerInWorld(Quest q)
	{
		return !q.hasWorldLimit() || owner.getWorld().getName().equals(q.getWorldLimit().getName());
	}
	
	public void objectSuccess(QuestProgress qp, QuestObjectProgress qop)
	{
		qop.setProgress(qop.getProgress() + 1);
		DebugHandler.log(5, "[Listener] Player %s's object succeeded: (%d/%d)", owner.getName(), qop.getProgress(), qop.getObject() instanceof NumerableObject ? ((NumerableObject) qop.getObject()).getAmount() : 1);
		checkFinished(qp, qop);
	}

	public void breakBlock(Material m)
	{
		HashSet<AtomicReference<Pair<QuestProgress, QuestObjectProgress>>> set = new HashSet<>();
		currentQuests.stream()
			.filter(qp -> checkPlayerInWorld(qp.getQuest()))
			.forEach(qp ->
				qp.getCurrentObjects().stream()
					.filter(qop -> checkBlock(qop, m))
					.collect(Collectors.toList())
					.forEach(qop ->
					{
						AtomicReference<Pair<QuestProgress, QuestObjectProgress>> ref = new AtomicReference<>();
						ref.set(new Pair<>(qp, qop));
						set.add(ref);
					}));
		if (set.isEmpty())
			return;
		for (AtomicReference<Pair<QuestProgress, QuestObjectProgress>> any : set)
		{
			if (any.get() != null)
			{
				Pair<QuestProgress, QuestObjectProgress> pair = any.get();
				objectSuccess(pair.getKey(), pair.getValue());
			}
		}
	}

	private boolean checkBlock(QuestObjectProgress qop, Material m)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectBreakBlock))
			return false;
		return ((QuestObjectBreakBlock) qop.getObject()).getType() == m;
	}

	public void talkToNPC(NPC npc)
	{
		AtomicReference<Pair<QuestProgress, QuestObjectProgress>> any = new AtomicReference<>();
		currentQuests.stream()
			.filter(qp -> checkPlayerInWorld(qp.getQuest()))
			.forEach(qp ->
			{
				Optional<QuestObjectProgress> obj = qp.getCurrentObjects().stream()
						.filter(qop -> checkNPC(qop, npc))
						.findFirst();
				obj.ifPresent(qop -> any.set(new Pair<>(qp, qop)));
			});
		if (any.get() != null)
		{
			Pair<QuestProgress, QuestObjectProgress> pair = any.get();
			checkFinished(pair.getKey(), pair.getValue());
			DebugHandler.log(5, "[Listener] Player " + owner.getName() + " talked to a npc.");
		}
	}

	private boolean checkNPC(QuestObjectProgress qop, NPC npc)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectTalkToNPC))
			return false;
		return ((QuestObjectTalkToNPC) qop.getObject()).getTargetNPC().getId() == npc.getId();
	}
	
	// Checks whether the player has submitted a correct item
	// Returns true if submitted at least 1 item.
	private boolean checkItem(QuestObjectProgress qop, NPC npc)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectDeliverItem))
			return false;
		QuestObjectDeliverItem o = (QuestObjectDeliverItem) qop.getObject();
		ItemStack itemToDeliver = owner.getInventory().getItemInMainHand();
		int amountNeeded = o.getAmount() - qop.getProgress();
		DebugHandler.log(5, "[Listener] Checking item submission...");
		if (o.getTargetNPC().equals(npc))
		{
			DebugHandler.log(5, "[Listener] NPC check PASSED.");
			DebugHandler.log(5, "[Listener] Bukkit similarity = " + o.getItem().isSimilar(itemToDeliver));
			DebugHandler.log(5, "[Listener] Weak itemCheck = " + QuestValidater.weakItemCheck(itemToDeliver, o.getItem()));
			if (o.getItem().isSimilar(itemToDeliver) || (ConfigSettings.USE_WEAK_ITEM_CHECK && QuestValidater.weakItemCheck(itemToDeliver, o.getItem())))
			{
				DebugHandler.log(5, "[Listener] Item similarity check PASSED.");
				if (itemToDeliver.getAmount() > amountNeeded)
				{
					itemToDeliver.setAmount(itemToDeliver.getAmount() - amountNeeded);
					qop.setProgress(o.getAmount());
				}
				else
				{
					owner.getInventory().setItemInMainHand(null);
					qop.setProgress(itemToDeliver.getAmount() == amountNeeded ? o.getAmount() : qop.getProgress() + itemToDeliver.getAmount());
				}
				return true;
			}
			DebugHandler.log(5, "[Listener] Failed due to the item submitted is not correct.");
			return false;
		}
		DebugHandler.log(5, "[Listener] NPC not correct. Required " + o.getTargetNPC().getId() + " but get " + npc.getId());
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
					.filter(qop -> checkItem(qop, npc))
					.findFirst();
				obj.ifPresent(qop -> any.set(new Pair<>(qp, qop)));
			});
		if (any.get() != null)
		{
			Pair<QuestProgress, QuestObjectProgress> pair = any.get();
			checkFinished(pair.getKey(), pair.getValue());
			DebugHandler.log(5, "[Listener] Player " + owner.getName() + " handed in one or more quest-requiring item(s).");
			return true;
		}
		else
		{
			DebugHandler.log(5, "[Listener] Player " + owner.getName() + " did not hand in any quest-requiring items.");
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

	public void killMob(Entity e)
	{
		HashSet<AtomicReference<Pair<QuestProgress, QuestObjectProgress>>> set = new HashSet<>();
		currentQuests.stream()
			.filter(qp -> checkPlayerInWorld(qp.getQuest()))
			.forEach(qp ->
					qp.getCurrentObjects().stream()
						.filter(qop -> checkMob(qop, e))
						.collect(Collectors.toList())
						.forEach(qop ->
						{
							AtomicReference<Pair<QuestProgress, QuestObjectProgress>> ref = new AtomicReference<>();
							ref.set(new Pair<>(qp, qop));
							set.add(ref);
						}));
		if (set.isEmpty())
			return;
		for (AtomicReference<Pair<QuestProgress, QuestObjectProgress>> any : set)
		{
			if (any.get() != null)
			{
				Pair<QuestProgress, QuestObjectProgress> pair = any.get();
				objectSuccess(pair.getKey(), pair.getValue());
			}
		}
	}

	public void catchFish()
	{
		HashSet<AtomicReference<Pair<QuestProgress, QuestObjectProgress>>> set = new HashSet<>();
		currentQuests.stream()
			.filter(qp -> checkPlayerInWorld(qp.getQuest()))
			.forEach(qp ->
					qp.getCurrentObjects().stream()
						.filter(qop -> qop.getObject() instanceof QuestObjectFishing && !qop.isFinished())
						.collect(Collectors.toList())
						.forEach(qop ->
						{
							AtomicReference<Pair<QuestProgress, QuestObjectProgress>> ref = new AtomicReference<>();
							ref.set(new Pair<>(qp, qop));
							set.add(ref);
						}));
		if (set.isEmpty())
			return;
		for (AtomicReference<Pair<QuestProgress, QuestObjectProgress>> any : set)
		{
			if (any.get() != null)
			{
				Pair<QuestProgress, QuestObjectProgress> pair = any.get();
				objectSuccess(pair.getKey(), pair.getValue());
			}
		}
	}

	public void killMythicMob(String mtmMob)
	{
		HashSet<AtomicReference<Pair<QuestProgress, QuestObjectProgress>>> set = new HashSet<>();
		currentQuests.stream()
				.filter(qp -> checkPlayerInWorld(qp.getQuest()))
				.forEach(qp ->
						qp.getCurrentObjects().stream()
								.filter(qop -> checkMythicMob(qop, mtmMob))
								.collect(Collectors.toList())
								.forEach(qop ->
								{
									AtomicReference<Pair<QuestProgress, QuestObjectProgress>> ref = new AtomicReference<>();
									ref.set(new Pair<>(qp, qop));
									set.add(ref);
								}));
		if (set.isEmpty())
			return;
		for (AtomicReference<Pair<QuestProgress, QuestObjectProgress>> any : set)
		{
			if (any.get() != null)
			{
				Pair<QuestProgress, QuestObjectProgress> pair = any.get();
				objectSuccess(pair.getKey(), pair.getValue());
			}
		}
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
		HashSet<AtomicReference<Pair<QuestProgress, QuestObjectProgress>>> set = new HashSet<>();
		currentQuests.stream()
				.filter(qp -> checkPlayerInWorld(qp.getQuest()))
				.forEach(qp ->
						qp.getCurrentObjects().stream()
								.filter(qop -> checkConsume(qop, is))
								.collect(Collectors.toList())
								.forEach(qop ->
								{
									AtomicReference<Pair<QuestProgress, QuestObjectProgress>> ref = new AtomicReference<>();
									ref.set(new Pair<>(qp, qop));
									set.add(ref);
								}));
		if (set.isEmpty())
			return;
		for (AtomicReference<Pair<QuestProgress, QuestObjectProgress>> any : set)
		{
			if (any.get() != null)
			{
				Pair<QuestProgress, QuestObjectProgress> pair = any.get();
				objectSuccess(pair.getKey(), pair.getValue());
			}
		}
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
		HashSet<AtomicReference<Pair<QuestProgress, QuestObjectProgress>>> set = new HashSet<>();
		currentQuests.stream()
				.filter(qp -> checkPlayerInWorld(qp.getQuest()))
				.forEach(qp ->
						qp.getCurrentObjects().stream()
								.filter(qop -> checkLocation(qop, l))
								.collect(Collectors.toList())
								.forEach(qop ->
								{
									AtomicReference<Pair<QuestProgress, QuestObjectProgress>> ref = new AtomicReference<>();
									ref.set(new Pair<>(qp, qop));
									set.add(ref);
								}));
		if (set.isEmpty())
			return;
		for (AtomicReference<Pair<QuestProgress, QuestObjectProgress>> any : set)
		{
			if (any.get() != null)
			{
				Pair<QuestProgress, QuestObjectProgress> pair = any.get();
				objectSuccess(pair.getKey(), pair.getValue());
			}
		}
	}

	private boolean checkLocation(QuestObjectProgress qop, Location l)
	{
		if (qop.isFinished() || !(qop.getObject() instanceof QuestObjectReachLocation))
			return false;
		QuestObjectReachLocation o = (QuestObjectReachLocation) qop.getObject();
		if (l.getWorld().getName().equals(o.getLocation().getWorld().getName()))
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
				if (op.getObject() instanceof QuestObjectTalkToNPC && !op.isFinished())
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
		return getFinishData(q).isRewardTaken();
	}

	public void claimReward(Quest q)
	{
		getFinishData(q).setRewardTaken(true);
		save();
	}

	public void checkUnclaimedReward()
	{
		for (QuestFinishData data : finishedQuests)
		{
			if (!data.isRewardTaken())
			{
				Quest q = data.getQuest();
				if (!q.isCommandQuest())
					QuestChatManager.info(owner, I18n.locMsg("QuestReward.RewardUnclaimed", q.getQuestNPC().getName()));
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

	public boolean canTake(Quest q, boolean sendMsg)
	{
		if (isCurrentlyDoing(q))
		{
			if (sendMsg)
				QuestChatManager.info(owner, I18n.locMsg("CommandInfo.AlreadyTaken"));
			return false;
		}
		if (q.usePermission() && !owner.hasPermission("MangoQuest.takeQuest." + q.getInternalID()))
		{
			if (sendMsg)
				QuestChatManager.info(owner, I18n.locMsg("CommandInfo.NoPermTakeQuest"));
			return false;
		}
		if (q.hasRequirement() && RequirementManager.meetRequirementWith(owner, q.getRequirements(), false).isPresent())
		{
			if (sendMsg)
				QuestChatManager.info(owner, q.getFailMessage());
			return false;
		}

		if (hasFinished(q))
		{
			long lastFinishTime = getFinishData(q).getLastFinish();
			switch (q.getRedoSetting())
			{
				case ONCE_ONLY:
					if (sendMsg)
						QuestChatManager.info(owner, I18n.locMsg("CommandInfo.NotRedoable"));
					return false;
				case COOLDOWN:
					long d = getDelay(lastFinishTime, q.getRedoDelay());
					if (d > 0)
					{
						if (sendMsg)
							QuestChatManager.info(owner, I18n.locMsg("CommandInfo.QuestCooldown", TimeHandler.convertTime(d)));
						return false;
					}
					break;
				case DAILY:
					if (!TimeHandler.canTakeDaily(lastFinishTime, q.getResetHour()))
					{
						if (sendMsg)
							QuestChatManager.info(owner, I18n.locMsg("CommandInfo.QuestCooldown", TimeHandler.convertTime(TimeHandler.getDailyCooldown(lastFinishTime, q.getResetHour()))));
						return false;
					}
					break;
				case WEEKLY:
					if (!TimeHandler.canTakeWeekly(lastFinishTime, q.getResetDay(), q.getResetHour()))
					{
						if (sendMsg)
							QuestChatManager.info(owner, I18n.locMsg("CommandInfo.QuestCooldown", TimeHandler.convertTime(TimeHandler.getWeeklyCooldown(lastFinishTime, q.getResetDay(), q.getResetHour()))));
						return false;
					}
					break;
			}
			if (!hasTakenReward(q))
			{
				if (sendMsg)
					QuestChatManager.info(owner, I18n.locMsg("QuestReward.RewardNotTaken"));
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
					QuestChatManager.info(owner, I18n.locMsg("QuestJourney.QuestFailed", qp.getQuest().getQuestName()));
					DebugHandler.log(3, "[Listener] Player " + owner.getName() + " failed quest " + qp.getQuest().getQuestName() + " because time is due.");
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
			QuestChatManager.info(owner, I18n.locMsg("QuestJourney.ProgressText", qp.getQuest().getQuestName()) + o.toDisplayText() + " "
					+ I18n.locMsg("CommandInfo.Finished"));
			DebugHandler.log(2, "[Listener] Player " + owner.getName() + "'s quest " + qp.getQuest().getQuestName() + " finished an object of "
					+ qop.getObject().getConfigString());
			qp.checkIfNextStage();
			Bukkit.getPluginManager().callEvent(new QuestObjectProgressEvent(this, qp.getQuest(), qop.getObject()));
		}
		else
		{
			if (o instanceof NumerableObject)
				QuestChatManager.info(owner, I18n.locMsg("QuestJourney.ProgressText", qp.getQuest().getQuestName()) + o.toDisplayText() + " " + I18n
						.locMsg("CommandInfo.Progress", Integer.toString(qop.getProgress()), Integer.toString(((NumerableObject) o).getAmount())));
			else
				if (o instanceof QuestObjectReachLocation)
				{
					if (qop.getProgress() >= 1)
					{
						qop.finish();
						checkFinished(qp, qop);
					}
				}
				else if (o instanceof CustomQuestObject)
					QuestChatManager.info(owner,
							I18n.locMsg("QuestJourney.ProgressText", qp.getQuest().getQuestName()) + ((CustomQuestObject) o).getProgressText(qop));
				else
					if (o instanceof QuestObjectTalkToNPC)
					{
						if (qop.getObject().hasConversation())
						{
							qop.openConversation(owner);
							DebugHandler.log(2, "[Listener] Player " + owner.getName() + "'s quest " + qp.getQuest().getQuestName()
									+ " has unfinished conversation.");
						}
						else
						{
							qop.setProgress(1);
							qop.finish();
							checkFinished(qp, qop);
						}
					}
		}
	}
}
