package me.Cutiemango.MangoQuest.questobjects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectBreakBlock extends NumerableObject
{

	public QuestObjectBreakBlock(Material m, short s, int i)
	{
		if (!m.isBlock())
		{
			Bukkit.getLogger().warning("ERROR: argument of QuestObjectBreakBlock does not have a matched Material Object.");
			return;
		}
		subID = s;
		block = m;
		amount = i;
		config = "BREAK_BLOCK";
	}

	private Material block;
	private short subID = 0;

	// Use raw
	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		return super.toTextComponent(Questi18n.localizeMessage("QuestObject.BreakBlock"), isFinished, amount, block, subID);
	}

	@Override
	public String toPlainText()
	{
		return ChatColor.GREEN + Questi18n.localizeMessage("QuestObject.BreakBlock", Integer.toString(amount), QuestUtil.translate(block, subID), "");
	}

	public Material getType()
	{
		return block;
	}

	public short getShort()
	{
		return subID;
	}

	public void setType(Material m)
	{
		block = m;
	}

	public void setSubID(short s)
	{
		subID = s;
	}
}
