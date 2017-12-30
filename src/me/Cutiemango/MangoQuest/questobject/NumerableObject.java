package me.Cutiemango.MangoQuest.questobject;

import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.Syntax;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.questobject.interfaces.EditorObject;

public abstract class NumerableObject extends SimpleQuestObject implements EditorObject
{
	protected int amount = 1;

	public int getAmount()
	{
		return amount;
	}

	public void setAmount(int a)
	{
		amount = a;
	}
	
	@Override
	public void formatEditorPage(QuestBookPage page, int stage, int obj)
	{
		page.add(I18n.locMsg("QuestEditor.TargetAmount") + amount);
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " amount"));
		page.changeLine();
	}
	
	@Override
	public boolean receiveCommandInput(Player sender, String type, String obj)
	{
		switch (type)
		{
			case "amount":
				if (!QuestValidater.validateInteger(obj))
					return false;
				this.setAmount(Integer.parseInt(obj));
				break;
		}
		return true;
	}
	
	@Override
	public EditorListenerObject createCommandOutput(Player sender, String command, String type)
	{
		EditorListenerObject obj = null;
		switch (type)
		{
			case "amount":
				obj = new EditorListenerObject(ListeningType.STRING, command, Syntax.of("I", I18n.locMsg("Syntax.Number"), ""));
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterAmount"));
				break;
		}
		return obj;
	}
	
	@Override
	public boolean load(QuestIO config, String path)
	{
		amount = config.getInt(path + "Amount");
		return true;
	}
	
	@Override
	public void save(QuestIO config, String objpath)
	{
		config.set(objpath + "Amount", amount);
	}
}
