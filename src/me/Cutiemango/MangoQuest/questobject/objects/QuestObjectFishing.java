package me.Cutiemango.MangoQuest.questobject.objects;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.interfaces.EditorObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class QuestObjectFishing extends NumerableObject implements EditorObject
{
	public QuestObjectFishing()
	{

	}

	public QuestObjectFishing(int fish)
	{
		amount = fish;
	}

	@Override
	public String toDisplayText()
	{
		return I18n.locMsg("QuestObject.Fishing", Integer.toString(amount));
	}

	@Override
	public String getConfigString()
	{
		return "FISHING";
	}

	@Override
	public String getObjectName()
	{
		return I18n.locMsg("QuestObjectName.Fishing");
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		return super.toTextComponent(ChatColor.stripColor(I18n.locMsg("QuestObject.Fishing")), isFinished, amount);
	}

	@Override
	public boolean load(QuestIO config, String path)
	{
		return super.load(config, path);
	}

	@Override
	public void save(QuestIO config, String path)
	{
		super.save(config, path);
	}

	@Override
	public boolean receiveCommandInput(Player sender, String type, String obj)
	{
		return super.receiveCommandInput(sender, type, obj);
	}

	@Override
	public EditorListenerObject createCommandOutput(Player sender, String command, String type)
	{
		return super.createCommandOutput(sender, command, type);
	}


}
