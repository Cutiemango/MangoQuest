package me.Cutiemango.MangoQuest.questobjects;

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
}
