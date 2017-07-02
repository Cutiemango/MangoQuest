package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectDeliverItem extends ItemObject implements NPCObject
{

	public QuestObjectDeliverItem(NPC n, ItemStack is, int deliveramount)
	{
		npc = n;
		item = is;
		amount = deliveramount;
		config = "DELIVER_ITEM";
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
		return super.toTextComponent(I18n.locMsg("QuestObject.DeliverItem"), isFinished, amount, item, npc);
	}

	@Override
	public String toPlainText()
	{
		if (item.getItemMeta().hasDisplayName())
			return I18n.locMsg("QuestObject.DeliverItem", Integer.toString(amount),
					item.getItemMeta().getDisplayName(), npc.getName());
		else
			return I18n.locMsg("QuestObject.DeliverItem", Integer.toString(amount),
					QuestUtil.translate(item.getType(), item.getDurability()), npc.getName());
	}
	
	@Override
	public String toDisplayText()
	{
		if (item.getItemMeta().hasDisplayName())
			return I18n.locMsg("QuestObject.FinishMessage.DeliverItem", Integer.toString(amount),
					item.getItemMeta().getDisplayName(), npc.getName());
		else
			return I18n.locMsg("QuestObject.FinishMessage.DeliverItem", Integer.toString(amount),
					QuestUtil.translate(item.getType(), item.getDurability()), npc.getName());
	}

}
