package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectItemDeliver extends SimpleQuestObject{
	
	public QuestObjectItemDeliver(NPC npc, ItemStack is, int deliveramount) {
		this.TargetNPC = npc;
		this.DeliverItem = is;
		this.DeliverAmount = deliveramount;
	}

	private NPC TargetNPC;
	private ItemStack DeliverItem;
	private int DeliverAmount;

	public NPC getTargetNPC() {
		return TargetNPC;
	}

	public void setTargetNPC(NPC targetNPC) {
		TargetNPC = targetNPC;
	}

	public ItemStack getDeliverItem() {
		return DeliverItem;
	}

	public void setDeliverItem(ItemStack deliverItem) {
		DeliverItem = deliverItem;
	}

	public int getDeliverAmount() {
		return this.DeliverAmount;
	}

	public void setDeliverAmount(int i) {
		this.DeliverAmount = i;
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished) {
		TextComponent text = new TextComponent();
		if (isFinished){
			text = new TextComponent(QuestUtil.translateColor("&8&m&o提交 "));
			text.addExtra(QuestUtil.translateColor("&8&m&o"));
			text.addExtra(QuestUtil.convertItemStacktoHoverEvent(true, DeliverItem));
			TextComponent suffix = new TextComponent(
					QuestUtil.translateColor(" &8&m&o" + DeliverAmount + " &8&m&o個 給 "));
			text.addExtra(suffix);
			text.addExtra(QuestUtil.convertNPCtoHoverEvent(isFinished, TargetNPC));
			return text;
		}
		else{
			text = new TextComponent(QuestUtil.translateColor("&0提交 "));
			text.addExtra(QuestUtil.convertItemStacktoHoverEvent(false, DeliverItem));
			TextComponent suffix = new TextComponent(
					QuestUtil.translateColor(" &0&l" + DeliverAmount + " &0個 給 "));
			text.addExtra(suffix);
			text.addExtra(QuestUtil.convertNPCtoHoverEvent(isFinished, TargetNPC));
			return text;
		}
	}

	@Override
	public String toPlainText() {
		if (DeliverItem.getItemMeta().hasDisplayName())
			return QuestUtil.translateColor("&a提交 " + DeliverItem.getItemMeta().getDisplayName() + " &f" + DeliverAmount + " &a個 給 " + TargetNPC.getName());
		else
			return QuestUtil.translateColor("&a提交 &f" + QuestUtil.translate(DeliverItem.getType()) + " &f" + DeliverAmount + " &a個 給 " + TargetNPC.getName());
	}
	
	

}
