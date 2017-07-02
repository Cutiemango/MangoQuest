package me.Cutiemango.MangoQuest.questobjects;

import me.Cutiemango.MangoQuest.I18n;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectTalkToNPC extends SimpleQuestObject implements NPCObject
{

	public QuestObjectTalkToNPC(NPC n)
	{
		npc = n;
		config = "TALK_TO_NPC";
	}

	private NPC npc;

	public NPC getTargetNPC()
	{
		return npc;
	}

	public void setTargetNPC(NPC targetNPC)
	{
		npc = targetNPC;
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		return super.toTextComponent(I18n.locMsg("QuestObject.TalkToNPC"), isFinished, npc);
	}

	@Override
	public String toPlainText()
	{
		return I18n.locMsg("QuestObject.TalkToNPC", getTargetNPC().getName());
	}

	@Override
	public String toDisplayText()
	{
		return I18n.locMsg("QuestObject.FinishMessage.TalkToNPC", getTargetNPC().getName());
	}

}
