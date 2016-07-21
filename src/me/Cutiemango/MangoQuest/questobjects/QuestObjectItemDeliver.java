package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.QuestUtil;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
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
			text = new TextComponent(QuestUtil.translateColor("&8&m&o提交物品 "));
			text.addExtra(QuestUtil.translateColor("&8&m&o"));
			text.addExtra(QuestUtil.convertItemStacktoHoverEvent(true, DeliverItem));
			TextComponent suffix = new TextComponent(
					QuestUtil.translateColor(" &8&m&o" + DeliverItem.getAmount() + " &8&m&o個"));
			text.addExtra(suffix);
			return text;
		}
		else{
			text = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&0提交物品 "));
			text.addExtra(QuestUtil.convertItemStacktoHoverEvent(false, DeliverItem));
			TextComponent suffix = new TextComponent(
					ChatColor.translateAlternateColorCodes('&', " &0&l" + DeliverItem.getAmount() + " &0個"));
			text.addExtra(suffix);
			return text;
		}
	}

	@Override
	public String toPlainText() {
		if (DeliverItem.getItemMeta().hasDisplayName())
			return ChatColor.translateAlternateColorCodes('&',
				"&a提交物品 " + DeliverItem.getItemMeta().getDisplayName() + " &f" + DeliverAmount + " &a個");
		else
			return ChatColor.translateAlternateColorCodes('&',
					"&a提交物品 &f" + QuestUtil.translateItemStackToChinese(DeliverItem) + " &f" + DeliverAmount + " &a個");
	}
	
	

}
