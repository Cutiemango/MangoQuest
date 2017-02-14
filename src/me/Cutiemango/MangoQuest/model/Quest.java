package me.Cutiemango.MangoQuest.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;

public class Quest {
	
	// Only Initialize with Command
	public Quest(){
		for (RequirementType t : RequirementType.values()){
			switch(t){
			case ITEM:
				Requirements.put(t, new ArrayList<ItemStack>());
				break;
			case LEVEL:
				Requirements.put(t, 0);
				break;
			case MONEY:
				Requirements.put(t, 0.0D);
				break;
			case NBTTAG:
				Requirements.put(t, new ArrayList<String>());
				break;
			case QUEST:
				Requirements.put(t, new ArrayList<String>());
				break;
			case SCOREBOARD:
				Requirements.put(t, new ArrayList<String>());
				break;
			}
		}
	}
	
	public Quest(String InternalID, String name, List<String> QuestOutline, QuestReward reward, List<QuestStage> stages, NPC npc){
		this.InternalID = InternalID;
		this.QuestName = QuestUtil.translateColor(name);
		this.QuestOutline = QuestOutline;
		this.reward = reward;
		this.AllStages = stages;
		this.QuestNPC = npc;
		
		
		for (RequirementType t : RequirementType.values()){
			switch(t){
			case ITEM:
				Requirements.put(t, new ArrayList<ItemStack>());
				break;
			case LEVEL:
				Requirements.put(t, 0);
				break;
			case MONEY:
				Requirements.put(t, 0.0D);
				break;
			case NBTTAG:
				Requirements.put(t, new ArrayList<String>());
				break;
			case QUEST:
				Requirements.put(t, new ArrayList<String>());
				break;
			case SCOREBOARD:
				Requirements.put(t, new ArrayList<String>());
				break;
			}
		}
	}
	
	private NPC QuestNPC;
	private String InternalID;
	private String QuestName;
	
	private List<String> QuestOutline = new ArrayList<>();
	
	private boolean useCustomFailMessage = false;
	private String FailRequirementMessage = "&c你並沒有達到指定的任務條件。";
	private List<QuestStage> AllStages = new ArrayList<>();
	private QuestReward reward = new QuestReward();
	
	private EnumMap<RequirementType, Object> Requirements = new EnumMap<>(RequirementType.class);
	private List<QuestTrigger> Triggers = new ArrayList<>();
	
	private boolean isRedoable = false;
	private long RedoDelay;
	
	private QuestCache cache;

	public String getInternalID() {
		return InternalID;
	}

	public void setInternalID(String internalID) {
		InternalID = internalID;
	}
	
	public String getQuestName() {
		return QuestName;
	}
	
	public void setQuestName(String s){
		QuestName = s;
	}

	public List<String> getQuestOutline() {
		return QuestOutline;
	}
	
	public void setQuestOutline(List<String> s){
		QuestOutline = s;
	}
	
	public QuestReward getQuestReward(){
		return this.reward;
	}

	public NPC getQuestNPC() {
		return QuestNPC;
	}
	
	public QuestCache getCache(){
		return cache;
	}
	
	public void registerVersion(QuestCache qc){
		cache = qc;
	}
	
	public void setQuestNPC(NPC npc){
		QuestNPC = npc;
	}
	
	public boolean isCommandQuest(){
		return QuestNPC == null;
	}
	
	public List<QuestStage> getStages(){
		return AllStages;
	}
	
	public List<SimpleQuestObject> getAllObjects(){
		List<SimpleQuestObject> list = new ArrayList<>();
		for (QuestStage qs : AllStages){
			list.addAll(qs.getObjects());
		}
		return list;
	}
	
	
	public QuestStage getStage(int index){
		return AllStages.get(index);
	}

	public EnumMap<RequirementType, Object> getRequirements() {
		return Requirements;
	}

	public List<QuestTrigger> getTriggers() {
		return Triggers;
	}

	public void setTriggers(List<QuestTrigger> triggers) {
		Triggers = triggers;
	}
	
	public boolean hasTrigger(){
		return !Triggers.isEmpty();
	}
	
	public boolean hasRequirement(){
		return !Requirements.isEmpty();
	}
	
	public String getFailMessage(){
		return FailRequirementMessage;
	}
	
	public void setFailMessage(String s){
		FailRequirementMessage = s;
	}
	
	public boolean useCustomFailMessage(){
		return useCustomFailMessage;
	}
	
	public void setUseCustomFailMessage(boolean b){
		useCustomFailMessage = b;
	}
	
	public boolean isRedoable(){
		return isRedoable;
	}
	
	public void setRedoable(boolean b){
		isRedoable = b;
	}
	
	public long getRedoDelay(){
		return RedoDelay;
	}
	
	public void setRedoDelay(long delay){
		RedoDelay = delay;
	}
	
	public void setRequirements(EnumMap<RequirementType, Object> m){
		Requirements = m;
	}
	
	@SuppressWarnings("unchecked")
	public FailResult meetRequirementWith(Player p){
		QuestPlayerData pd = QuestUtil.getData(p);
		for (RequirementType t : Requirements.keySet()){
			Object value = Requirements.get(t);
			switch (t){
			case QUEST:
				for (String q : (List<String>)value){
					if (!pd.hasFinished(QuestUtil.getQuest(q)))
						return new FailResult(RequirementType.QUEST, QuestUtil.getQuest(q));
				}
				break;
			case LEVEL:
				if (!(p.getLevel() >= (Integer)value))
					return new FailResult(RequirementType.LEVEL, (Integer)value);
				break;
			case MONEY:
				if (Main.instance.initManager.hasEconomyEnabled()){
					if (!(Main.instance.initManager.getEconomy().getBalance(p) >= (Double)value))
						return new FailResult(RequirementType.MONEY, (Double)value);
				}
				break;
			case ITEM:
				for (ItemStack i : (List<ItemStack>)value){
					if (!p.getInventory().containsAtLeast(i, i.getAmount()))
						return new FailResult(RequirementType.ITEM, i);
				}
				break;
			case SCOREBOARD:
				for (String s : (List<String>)value){
					s = s.replace(" ", "");
					String[] split;
					if (s.contains(">=")) {
						split = s.split(">=");
						if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]) == null){
							QuestUtil.warnCmd("任務 " + InternalID + " 的記分板內容有錯誤，找不到伺服器上名為 " + split[0] + " 的記分板物件資料！");
							return new FailResult(RequirementType.SCOREBOARD, "");
						}
						if (!(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0])
								.getScore(p.getName()).getScore() >= Integer.parseInt(split[1])))
							return new FailResult(RequirementType.SCOREBOARD, "");
					} else if (s.contains("<=")) {
						split = s.split("<=");
						if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]) == null){
							QuestUtil.warnCmd("任務 " + InternalID + " 的記分板內容有錯誤，找不到伺服器上名為 " + split[0] + " 的記分板物件資料！");
							return new FailResult(RequirementType.SCOREBOARD, "");
						}
						if (!(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0])
								.getScore(p.getName()).getScore() <= Integer.parseInt(split[1])))
							return new FailResult(RequirementType.SCOREBOARD, "");
					} else if (s.contains("==")) {
						split = s.split("==");
						if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0]) == null){
							QuestUtil.warnCmd("任務 " + InternalID + " 的記分板內容有錯誤，找不到伺服器上名為 " + split[0] + " 的記分板物件資料！");
							return new FailResult(RequirementType.SCOREBOARD, "");
						}
						if (!(Bukkit.getScoreboardManager().getMainScoreboard().getObjective(split[0])
								.getScore(p.getName()).getScore() == Integer.parseInt(split[1])))
							return new FailResult(RequirementType.SCOREBOARD, "");
					}
				}
				break;
			case NBTTAG:
				for (String n : (List<String>)value){
					if (!Main.instance.handler.hasTag(p, n))
						return new FailResult(RequirementType.NBTTAG, "");
				}
				break;
			}
		}
		return new FailResult(null, "");
	}
	
	@Override
	public Quest clone(){
		Quest q = new Quest(InternalID, QuestName, QuestOutline, reward, AllStages, QuestNPC);
		q.setRequirements(Requirements);
		q.setRedoable(isRedoable);
		q.setRedoDelay(RedoDelay);
		q.setFailMessage(FailRequirementMessage);
		q.useCustomFailMessage = useCustomFailMessage;
		return q;
	}
	
	public static void synchronizeLocal(Quest q){
		for (Player p : Bukkit.getOnlinePlayers()){
			QuestPlayerData pd = QuestUtil.getData(p);
			Iterator<QuestProgress> it = pd.getProgresses().iterator();
			while (it.hasNext()) {
				QuestProgress qp = it.next();
				if (QuestCache.detailedValidate(q, qp.getQuest())){
					pd.forceQuit(q);
					break;
				}
				else continue;
			}
		}
		QuestStorage.Quests.put(q.getInternalID(), q);
	}
	
	public class FailResult{
		Object obj;
		RequirementType type;
		
		public FailResult(RequirementType t, Object o){
			type = t;
			obj = o;
		}
		
		public boolean succeed(){
			return type == null;
		}
		
		public RequirementType getFailType(){
			return type;
		}
		
		public String getMessage(){
			String s = "";
			if (type == null)
				return s;
			switch (type){
			case ITEM:
				ItemStack item = (ItemStack)obj;
				if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
					s = ChatColor.RED + "沒有指定的物品： " + item.getItemMeta().getDisplayName();
				else
					s = ChatColor.RED + "沒有指定的物品： " + QuestUtil.translate(item.getType(), item.getDurability());
				break;
			case LEVEL:
				s = ChatColor.RED + "等級不足： " + (Integer)obj;
				break;
			case MONEY:
				s = ChatColor.RED + "金錢不足： " + (Double)obj;
				break;
			case SCOREBOARD:
			case NBTTAG:
				s = ChatColor.RED + "你並沒有達到指定的任務條件。";
				break;
			case QUEST:
				s = ChatColor.RED + "沒有完成任務： " + ((Quest)obj).getQuestName();
				break;
			}
			return s;
		}
		
	}
	
}
