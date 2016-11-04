package me.Cutiemango.MangoQuest.questobjects;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectTalkToNPC extends SimpleQuestObject{
	
	public QuestObjectTalkToNPC(NPC n) {
		npc = n;
		config = "TALK_TO_NPC";
	}

	private NPC npc;
	
	public NPC getTargetNPC() {
		return npc;
	}

	public void setTargetNPC(NPC targetNPC) {
		npc = targetNPC;
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		return super.toTextComponent("與 %0 對話", isFinished, npc);
	}

	@Override
	public String toPlainText() {
		return QuestUtil.translateColor("&a與 " + getTargetNPC().getName() + " &a對話");
	}

}
