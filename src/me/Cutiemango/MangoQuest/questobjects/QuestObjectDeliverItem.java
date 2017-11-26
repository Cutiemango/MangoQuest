package me.Cutiemango.MangoQuest.questobjects;

import java.util.logging.Level;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectDeliverItem extends ItemObject implements NPCObject
{
	
	// Reserved for initializing with load()
	public QuestObjectDeliverItem(){}
	
	public QuestObjectDeliverItem(NPC n, ItemStack is, int deliveramount)
	{
		npc = n;
		item = is;
		amount = deliveramount;
	}
	
	@Override
	public String getConfigString()
	{
		return "DELIVER_ITEM";
	}

	@Override
	public String getObjectName()
	{
		return I18n.locMsg("QuestObjectName.DeliverItem");
	}

	private NPC npc;
	
	@Override
	public void setAmount(int i)
	{
		item.setAmount(i);
		amount = i;
	}

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
		return super.toTextComponent(ChatColor.stripColor(I18n.locMsg("QuestObject.DeliverItem")), isFinished, amount, item, npc);
	}
	
	@Override
	public String toDisplayText()
	{
		if (item.getItemMeta().hasDisplayName())
			return I18n.locMsg("QuestObject.DeliverItem", Integer.toString(amount),
					item.getItemMeta().getDisplayName(), npc.getName());
		else
			return I18n.locMsg("QuestObject.DeliverItem", Integer.toString(amount),
					QuestUtil.translate(item.getType(), item.getDurability()), npc.getName());
	}

	@Override
	public void formatEditorPage(QuestBookPage page, int stage, int obj)
	{
		page.add(I18n.locMsg("QuestEditor.DeliverItem"));
		page.add(new InteractiveText("").showItem(item)).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " item")).changeLine();
		page.add(I18n.locMsg("QuestEditor.DeliverNPC"));
		if (npc == null)
			page.add(new InteractiveText(I18n.locMsg("QuestEditor.NotSet"))).endNormally();
		else
			page.add(new InteractiveText("").showNPCInfo(npc)).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " itemnpc")).changeLine();
		super.formatEditorPage(page, stage, obj);
	}
	
	@Override
	public boolean load(QuestIO config, String qpath, int scount, int ocount)
	{
		String s = config.getString(qpath + "Stages." + scount + "." + ocount + ".TargetNPC");
		if (!QuestValidater.validateNPC(s))
		{
			QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.NPCNotValid", s));
			return false;
		}
		npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(s));
		item = config.getItemStack(qpath + "Stages." + scount + "." + ocount + ".Item");
		amount = item.getAmount();
		super.load(config, qpath, scount, ocount);
		return true;
	}

	@Override
	public void save(QuestIO config, String objpath)
	{
		config.set(objpath + "TargetNPC", npc.getId());
		config.set(objpath + "Item", item);
		super.save(config, objpath);
	}

}
