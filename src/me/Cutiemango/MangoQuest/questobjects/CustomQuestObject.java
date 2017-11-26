package me.Cutiemango.MangoQuest.questobjects;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestIO;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class CustomQuestObject extends SimpleQuestObject
{

	public CustomQuestObject(){}

	@Override
	public String getConfigString()
	{
		return "CUSTOM_OBJECT";
	}

	@Override
	public String getObjectName()
	{
		return I18n.locMsg("QuestObjectName.CustomObject");
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		return new TextComponent(I18n.locMsg("QuestObject.CustomObject"));
	}

	@Override
	public String toDisplayText()
	{
		return I18n.locMsg("QuestObject.CustomObject");
	}

	public abstract boolean load(QuestIO config, String qpath, int scount, int ocount);
	
	@Override
	public void save(QuestIO config, String objpath)
	{
		config.set(objpath + "ObjectClass", this.getClass().getName());
	}
	
}
