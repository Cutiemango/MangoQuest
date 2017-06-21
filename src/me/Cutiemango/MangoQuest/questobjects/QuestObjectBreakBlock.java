package me.Cutiemango.MangoQuest.questobjects;

import java.util.logging.Level;
import org.bukkit.Material;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectBreakBlock extends NumerableObject
{

	public QuestObjectBreakBlock(Material m, short s, int i)
	{
		if (!m.isBlock())
		{
			QuestChatManager.logCmd(Level.WARNING, "破壞方塊 物件提供的參數並非能夠破壞的方塊。");
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
		return Questi18n.localizeMessage("QuestObject.BreakBlock", Integer.toString(amount), QuestUtil.translate(block, subID), "");
	}
	
	@Override
	public String toDisplayText()
	{
		return Questi18n.localizeMessage("QuestObject.FinishMessage.BreakBlock", Integer.toString(amount), QuestUtil.translate(block, subID), "");
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
