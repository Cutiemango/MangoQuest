package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.List;

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
	private List<QuestStage> AllStages = new ArrayList<>();
	private QuestReward reward;

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
}
