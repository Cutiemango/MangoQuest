package me.Cutiemango.MangoQuest.questobject.objects;

import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.interfaces.EditorObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectBreakBlock extends NumerableObject implements EditorObject
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

	public short getSubID()
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
	public boolean load(QuestIO config, String path)
	{
		block = Material.getMaterial(config.getString(path + "BlockType"));
		subID = Short.parseShort(Integer.toString(config.getInt(path + "SubID")));
		return super.load(config, path);
	}

	@Override
	public void save(QuestIO config, String objpath)
	{
		config.set(objpath + "BlockType", block.toString());
		config.set(objpath + "SubID", subID);
		super.save(config, objpath);
	}

	@Override
	public boolean receiveCommandInput(Player sender, String type, String obj)
	{
		switch (type)
		{
			case "block":
				String[] split = obj.split(":");
				if (!QuestValidater.validateInteger(split[1]) || Material.getMaterial(split[0]) == null)
					return false;
				setType(Material.getMaterial(split[0]));
				setSubID(Short.parseShort(split[1]));
				break;
			default:
				return super.receiveCommandInput(sender, type, obj);
		}
		return true;
	}

	@Override
	public EditorListenerObject createCommandOutput(Player sender, String command, String type)
	{
		EditorListenerObject obj = null;
		switch (type)
		{
			case "block":
				obj = new EditorListenerObject(ListeningType.BLOCK, command, null);
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.BreakBlock"));
				break;
			default:
				return super.createCommandOutput(sender, command, type);
		}
		return obj;
	}
}
