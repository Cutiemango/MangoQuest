package me.Cutiemango.MangoQuest.questobjects;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;

public abstract class NumerableObject extends SimpleQuestObject
{

	protected int amount;

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
	public boolean load(QuestIO config, String qpath, int scount, int ocount)
	{
		amount = config.getInt(qpath + "Stages." + scount + "." + ocount + ".Amount");
		return true;
	}
	
	@Override
	public void save(QuestIO config, String objpath)
	{
		config.set(objpath + "Amount", amount);
	}
}
