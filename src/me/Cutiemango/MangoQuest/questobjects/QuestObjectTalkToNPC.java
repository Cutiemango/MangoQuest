package me.Cutiemango.MangoQuest.questobjects;

import me.Cutiemango.MangoQuest.Questi18n;
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
		return super.toTextComponent(Questi18n.localizeMessage("QuestObject.TalkToNPC"), isFinished, npc);
	}

	@Override
	public String toPlainText()
	{
		return Questi18n.localizeMessage("QuestObject.TalkToNPC", getTargetNPC().getName());
	}

}
