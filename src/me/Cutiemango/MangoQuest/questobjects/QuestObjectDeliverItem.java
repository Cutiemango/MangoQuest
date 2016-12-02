package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectDeliverItem extends ItemObject{
	
	public QuestObjectDeliverItem(NPC n, ItemStack is, int deliveramount) {
		npc = n;
		item = is;
		amount = deliveramount;
		config = "DELIVER_ITEM";
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
		return super.toTextComponent("提交 %0 個 %1 給 %2", isFinished, amount, item, npc);
	}

	@Override
	public String toPlainText() {
		if (item.getItemMeta().hasDisplayName())
			return QuestUtil.translateColor("&a提交 " + item.getItemMeta().getDisplayName() + " &f" + item + " &a個 給 " + npc.getName());
		else
			return QuestUtil.translateColor("&a提交 &f" + QuestUtil.translate(item.getType(), item.getDurability()) + " &f" + amount + " &a個 給 " + npc.getName());
	}
	
	

}
