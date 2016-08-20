package me.Cutiemango.MangoQuest.questobjects;

import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;
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
		TextComponent text = new TextComponent();
		if (isFinished) {
			text = new TextComponent(QuestUtil.translateColor("&8&m&o與 "));
			text.addExtra(TextComponentFactory.convertLocationtoHoverEvent(npc.getName(), npc.getEntity().getLocation(), isFinished));
			text.addExtra(QuestUtil.translateColor(" &8&m&o對話"));
		} else {
			text = new TextComponent(QuestUtil.translateColor("&0與 "));
			text.addExtra(TextComponentFactory.convertLocationtoHoverEvent(npc.getName(), npc.getEntity().getLocation(), isFinished));
			text.addExtra(QuestUtil.translateColor(" &0對話"));
		}
		return text;
	}

	@Override
	public String toPlainText() {
		return QuestUtil.translateColor("&a與 " + getTargetNPC().getName() + " &a對話");
	}

}
