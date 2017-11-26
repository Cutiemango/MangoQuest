package me.Cutiemango.MangoQuest.questobjects;

import java.util.logging.Level;
import org.bukkit.Material;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectBreakBlock extends NumerableObject
{
	// Reserved for initializing with load()
	public QuestObjectBreakBlock(){}
	
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
	}

	@Override
	public String getConfigString()
	{
		return "BREAK_BLOCK";
	}

	@Override
	public String getObjectName()
	{
		return I18n.locMsg("QuestObjectName.BreakBlock");
	}

	private Material block;
	private short subID = 0;

	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		return super.toTextComponent(ChatColor.stripColor(I18n.locMsg("QuestObject.BreakBlock")), isFinished, amount, block, subID);
	}
	
	@Override
	public String toDisplayText()
	{
		return I18n.locMsg("QuestObject.BreakBlock", Integer.toString(amount), QuestUtil.translate(block, subID), "");
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

	@Override
	public void formatEditorPage(QuestBookPage page, int stage, int obj)
	{
		page.add(I18n.locMsg("QuestEditor.BreakBlock") + QuestUtil.translate(block, subID)).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " block")).changeLine();
		super.formatEditorPage(page, stage, obj);
	}

	@Override
	public boolean load(QuestIO config, String qpath, int scount, int ocount)
	{
		block = Material.getMaterial(config.getString(qpath + "Stages." + scount + "." + ocount + ".BlockType"));
		subID = Short.parseShort(Integer.toString(config.getInt(qpath + "Stages." + scount + "." + ocount + ".SubID")));
		super.load(config, qpath, scount, ocount);
		return true;
	}

	@Override
	public void save(QuestIO config, String objpath)
	{
		config.set(objpath + "BlockType", block.toString());
		config.set(objpath + "SubID", subID);
		super.save(config, objpath);
	}
}
