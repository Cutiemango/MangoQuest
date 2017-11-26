package me.Cutiemango.MangoQuest.questobjects;

import java.util.logging.Level;
import org.bukkit.ChatColor;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectTalkToNPC extends SimpleQuestObject implements NPCObject
{
	
	public QuestObjectTalkToNPC(){}

	public QuestObjectTalkToNPC(NPC n)
	{
		npc = n;
	}
	
	@Override
	public String getConfigString()
	{
		return "TALK_TO_NPC";
	}

	@Override
	public String getObjectName()
	{
		return I18n.locMsg("QuestObjectName.TalkToNPC");
	}

	private NPC npc;

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
		return super.toTextComponent(ChatColor.stripColor(I18n.locMsg("QuestObject.TalkToNPC")), isFinished, npc);
	}

	@Override
	public String toDisplayText()
	{
		return I18n.locMsg("QuestObject.TalkToNPC", getTargetNPC().getName());
	}

	@Override
	public void formatEditorPage(QuestBookPage page, int stage, int obj)
	{
		page.add(I18n.locMsg("QuestEditor.TalkNPC")).endNormally();
		if (npc == null)
			page.add(new InteractiveText(I18n.locMsg("QuestEditor.NotSet"))).endNormally();
		else
			page.add(new InteractiveText("").showNPCInfo(npc)).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " npc")).changeLine();
		page.add(I18n.locMsg("QuestEditor.ActivateConversation")).endNormally();
		if (conv == null)
			page.add(new InteractiveText(I18n.locMsg("QuestEditor.NotSet"))).endNormally();
		else
			page.add(conv.getName() + "(" + conv.getInternalID() + ")").endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " conv")).changeLine();
		page.changeLine();
	}

	@Override
	public boolean load(QuestIO config, String path)
	{
		String id = config.getString(path + "TargetNPC");
		if (!QuestValidater.validateNPC(id))
		{
			QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.NPCNotValid", id));
			return false;
		}
		npc = Main.getHooker().getNPC(id);
		return true;
	}

	@Override
	public void save(QuestIO config, String objpath)
	{
		config.set(objpath + "TargetNPC", npc.getId());
	}

}
