package me.Cutiemango.MangoQuest.questobjects;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectTalkToNPC extends SimpleQuestObject{
	
	public QuestObjectTalkToNPC(NPC npc) {
		this.TargetNPC = npc;
	}

	private NPC TargetNPC;
	
	public NPC getTargetNPC() {
		return TargetNPC;
	}

	public void setTargetNPC(NPC targetNPC) {
		TargetNPC = targetNPC;
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		TextComponent text = new TextComponent();
		if (isFinished) {
			text = new TextComponent(QuestUtil.translateColor("&8&m&o與 "));
			text.addExtra(QuestUtil.convertNPCtoHoverEvent(true, getTargetNPC()));
			text.addExtra(ChatColor.translateAlternateColorCodes('&', " &8&m&o對話"));
		} else {
			text = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&0與 "));
			text.addExtra(QuestUtil.convertNPCtoHoverEvent(false, getTargetNPC()));
			text.addExtra(ChatColor.translateAlternateColorCodes('&', " &0對話"));
		}
		return text;
	}

	@Override
	public String toPlainText() {
		return ChatColor.translateAlternateColorCodes('&', "&a與 " + getTargetNPC().getName() + " &a對話");
	}

}
