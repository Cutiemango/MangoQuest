package me.Cutiemango.MangoQuest.questobject.objects;

import java.util.logging.Level;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.book.InteractiveText;
import me.Cutiemango.MangoQuest.book.QuestBookPage;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestIO;
import me.Cutiemango.MangoQuest.manager.QuestBookGUIManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.interfaces.EditorObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestObjectKillMob extends NumerableObject implements EditorObject
{
	public QuestObjectKillMob(){}

	public QuestObjectKillMob(EntityType t, int i, String customname)
	{
		type = t;
		amount = i;
		customName = customname;
	}

	public QuestObjectKillMob(MythicMob mmMob, int i)
	{
		mtmMob = mmMob;
		amount = i;
		type = EntityType.valueOf(mmMob.getEntityType().toUpperCase());
		customName = mmMob.getDisplayName();
	}
	
	@Override
	public String getConfigString()
	{
		return "KILL_MOB";
	}

	@Override
	public String getObjectName()
	{
		return I18n.locMsg("QuestObjectName.KillMob");
	}

	private EntityType type;
	private String customName;
	private MythicMob mtmMob;

	public EntityType getType()
	{
		return type;
	}

	public boolean hasCustomName()
	{
		return !(customName == null);
	}

	public String getCustomName()
	{
		return customName;
	}

	public MythicMob getMythicMob()
	{
		return mtmMob;
	}

	public void setCustomName(String s)
	{
		customName = s;
	}

	public void setMythicMob(MythicMob m)
	{
		mtmMob = m;
		customName = m.getDisplayName();
		type = EntityType.valueOf(m.getEntityType().toUpperCase());
	}

	public void setType(EntityType t)
	{
		type = t;
	}

	public boolean isMythicObject()
	{
		return mtmMob != null;
	}

	@Override
	public TextComponent toTextComponent(boolean isFinished)
	{
		if (hasCustomName())
			return super.toTextComponent(ChatColor.stripColor(I18n.locMsg("QuestObject.KillMob")), isFinished, amount, customName);
		else
			return super.toTextComponent(ChatColor.stripColor(I18n.locMsg("QuestObject.KillMob")), isFinished, amount, type);
	}
	
	@Override
	public String toDisplayText()
	{
		if (hasCustomName())
			return I18n.locMsg("QuestObject.KillMob", Integer.toString(amount), QuestChatManager.translateColor(customName));
		else
			return I18n.locMsg("QuestObject.KillMob", Integer.toString(amount), QuestUtil.translate(type));
	}

	@Override
	public void formatEditorPage(QuestBookPage page, int stage, int obj)
	{
		if (Main.instance.pluginHooker.hasMythicMobEnabled())
		{
			page.add(I18n.locMsg("QuestEditor.MythicMobs"));
			if (isMythicObject())
				page.add(mtmMob.getDisplayName() + "(" + mtmMob.getInternalName() + ")").endNormally();
			else
				page.add(I18n.locMsg("QuestEditor.NotSet")).endNormally();
			page.changeLine();
			page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " mtmmob")).changeLine();
		}
		page.add(I18n.locMsg("QuestEditor.MobName"));
		if (hasCustomName())
			page.add(customName).endNormally();
		else
			page.add(I18n.locMsg("QuestEditor.NotSet"));
		page.changeLine();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " mobname")).changeLine();
		page.changeLine();

		page.add(I18n.locMsg("QuestEditor.MobType") + QuestUtil.translate(type)).endNormally();
		page.add(new InteractiveText(I18n.locMsg("QuestEditor.Edit")).clickCommand("/mq e edit object " + stage + " " + obj + " mobtype")).changeLine();
		page.changeLine();
		super.formatEditorPage(page, stage, obj);
	}
	
	@Override
	public boolean load(QuestIO config, String path)
	{
		if (config.getString(path + "MythicMob") != null)
		{
			if (!Main.instance.pluginHooker.hasMythicMobEnabled())
			{
				QuestChatManager.logCmd(Level.SEVERE, I18n.locMsg("Cmdlog.MTMNotInstalled"));
				return false;
			}
			String id = config.getString(path + "MythicMob");
			if (!QuestValidater.validateMythicMob(id))
			{
				QuestChatManager.logCmd(Level.WARNING, I18n.locMsg("Cmdlog.MTMMobNotFound", id));
				return false;
			}
			mtmMob = Main.getHooker().getMythicMob(id);
			customName = mtmMob.getDisplayName();
			type = EntityType.valueOf(mtmMob.getEntityType().toUpperCase());
		}
		else
			if (config.getString(path + "MobName") != null)
			{
				customName = config.getString(path + "MobName");
				type = EntityType.valueOf(config.getString(path + "MobType"));
			}
			else
				if (config.getString(path + "MobType") != null)
					type = EntityType.valueOf(config.getString(path + "MobType"));
				else return false;
		return super.load(config, path);
	}

	@Override
	public void save(QuestIO config, String objpath)
	{
		if (isMythicObject())
			config.set(objpath + "MythicMob", mtmMob.getInternalName());
		else
		{
			config.set(objpath + "MobType", type.toString());
			if (hasCustomName())
				config.set(objpath + "MobName", customName);
		}
		super.save(config, objpath);
	}

	@Override
	public boolean receiveCommandInput(Player sender, String type, String obj)
	{
		switch (type)
		{
			case "mobname":
				setCustomName(obj);
				break;
			case "mobtype":
				if (EntityType.valueOf(obj) == null)
					return false;
				setType(EntityType.valueOf(obj));
				break;
			case "mtmmob":
				if (!QuestValidater.validateMythicMob(obj))
				{
					QuestChatManager.error(sender, I18n.locMsg("Cmdlog.MTMMobNotFound", obj));
					return false;
				}
				setCustomName(obj);
				setType(EntityType.valueOf(obj));
				setMythicMob(Main.getHooker().getMythicMob(obj));
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
			case "mobtype":
				obj = new EditorListenerObject(ListeningType.MOB_LEFT_CLICK, command, null);
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.HitMob"));
				break;
			case "mtmmob":
				obj = new EditorListenerObject(ListeningType.MTMMOB_LEFT_CLICK, command, null);
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterMobID"));
				break;
			case "mobname":
				obj = new EditorListenerObject(ListeningType.STRING, command, null);
				QuestBookGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterMobName"));
				break;
			default:
				return super.createCommandOutput(sender, command, type);
		}
		return obj;
	}

}
