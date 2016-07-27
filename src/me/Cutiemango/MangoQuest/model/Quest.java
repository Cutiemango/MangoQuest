package me.Cutiemango.MangoQuest.model;

import java.util.ArrayList;
import java.util.List;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.npc.NPC;

public class Quest {
	
	public Quest(String InternalID, String name, String QuestOutline, QuestReward reward, List<QuestStage> stages, NPC npc){
		this.InternalID = InternalID;
		this.QuestName = QuestUtil.translateColor(name);
		this.QuestOutline = QuestUtil.translateColor(QuestOutline);
		this.reward = reward;
		this.AllStages = stages;
		this.QuestNPC = npc;
	}
	
	private NPC QuestNPC;
	private String InternalID;

	private String QuestName;
	private String QuestOutline;
	private String FailRequirementMessage;
	private List<QuestStage> AllStages = new ArrayList<>();
	private QuestReward reward;
	
	private List<QuestRequirement> Requirements = new ArrayList<>();
	private List<QuestTrigger> Triggers = new ArrayList<>();
	
	private boolean isRedoable = false;
	private long RedoDelay;

	public String getInternalID() {
		return InternalID;
	}

	public void setInternalID(String internalID) {
		InternalID = internalID;
	}
	
	public String getQuestName() {
		return QuestName;
	}

	public String getQuestOutline() {
		return QuestOutline;
	}
	
	public QuestReward getQuestReward(){
		return this.reward;
	}

	public NPC getQuestNPC() {
		return QuestNPC;
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

	public List<QuestRequirement> getRequirements() {
		return Requirements;
	}

	public List<QuestTrigger> getTriggers() {
		return Triggers;
	}

	public void setRequirements(List<QuestRequirement> requirements) {
		Requirements = requirements;
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
}
