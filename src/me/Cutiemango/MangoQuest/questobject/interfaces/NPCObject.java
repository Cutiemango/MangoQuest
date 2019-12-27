package me.Cutiemango.MangoQuest.questobject.interfaces;

import net.citizensnpcs.api.npc.NPC;

public interface NPCObject
{
	NPC getTargetNPC();
	void setTargetNPC(NPC targetNPC);
}
