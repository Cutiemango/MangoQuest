package me.Cutiemango.MangoQuest.model;

public class QuestCache {
	
	public QuestCache(long l){
		questVersion = l;
	}

	private long questVersion;

	public long getVersion(){
		return questVersion;
	}
	
	public void update(){
		questVersion = System.currentTimeMillis();
	}
	
	public void retrieve(long version){
		questVersion = version;
	}
	
	public static boolean detailedValidate(Quest before, Quest after){
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getQuestName().equals(after.getQuestName()))
			return false;
		if (!(before.isCommandQuest() == after.isCommandQuest()))
			return false;
		if (!before.isCommandQuest()){
			if (!(before.getQuestNPC().getId() == after.getQuestNPC().getId()))
				return false;
		}
		if (!before.getQuestOutline().equals(after.getQuestOutline()))
			return false;
		if (!before.getFailMessage().equals(after.getFailMessage()))
			return false;
		if (!before.getQuestReward().equals(after.getQuestReward()))
			return false;
		if (!(before.isRedoable() == after.isRedoable()))
			return false;
		if (before.isRedoable()){
			if (!(before.getRedoDelay() == after.getRedoDelay()))
				return false;
		}
		if (!before.getRequirements().equals(after.getRequirements()))
			return false;
		if (!before.getStages().equals(after.getStages()))
			return false;
		if (!before.getTriggers().equals(after.getTriggers()))
			return false;
		return true;
	}
	
	public static boolean weakValidate(Quest before, Quest after){
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getQuestName().equals(after.getQuestName()))
			return false;
		return true;
	}
}
