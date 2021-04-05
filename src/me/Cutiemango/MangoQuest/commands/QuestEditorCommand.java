package me.Cutiemango.MangoQuest.commands;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.commands.edtior.CommandEditQuest;
import me.Cutiemango.MangoQuest.commands.edtior.CommandNewObject;
import me.Cutiemango.MangoQuest.commands.edtior.CommandNewQuest;
import me.Cutiemango.MangoQuest.commands.edtior.CommandRemoveQuest;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.config.QuestConfigManager;
import me.Cutiemango.MangoQuest.model.Quest;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuestEditorCommand
{
	private static final List<String> confirm = new ArrayList<>();

	// Command: /mq editor args[1] args[2]
	public static void execute(Player sender, String[] args) {
		if (!sender.hasPermission("MangoQuest.AdminCommand")) {
			QuestChatManager.error(sender, I18n.locMsg("CommandInfo.NoPermission"));
			return;
		}
		Quest q = QuestEditorManager.getCurrentEditingQuest(sender);
		if (args.length == 1) {
			QuestEditorManager.mainGUI(sender);
		} else if (args.length == 2) {
			switch (args[1]) {
				case "edit":
					QuestEditorManager.editGUI(sender);
					return;
				case "remove":
					QuestEditorManager.removeGUI(sender);
					return;
				case "help":
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.UseCommand"));
					return;
				case "exit":
					QuestEditorManager.exit(sender);
					QuestEditorManager.mainGUI(sender);
					return;
				case "gui":
					QuestEditorManager.editQuest(sender);
					return;
				case "newquest":
					CommandNewQuest.execute(sender, args);
					return;
				case "sa":
				case "saveall":
					if (!QuestEditorManager.checkEditorMode(sender, true))
						return;
					if (q.getStages().isEmpty() || q.getStage(0).getObjects().isEmpty()) {
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.QuestEmpty"));
						return;
					}
					QuestConfigManager.getSaver().saveQuest(q);
					Quest.synchronizeLocal(q);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.QuestSaveCfgSuccess", q.getQuestName()));
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.QuestSaveSevSuccess", q.getQuestName()));
					break;
				case "sc":
				case "savecfg":
					if (!QuestEditorManager.checkEditorMode(sender, true))
						return;
					if (q.getStages().isEmpty()) {
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.QuestEmpty"));
						return;
					}
					QuestConfigManager.getSaver().saveQuest(q);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.QuestSaveSevSuccess", q.getQuestName()));
					break;
				case "sl":
				case "savelocal":
					if (!QuestEditorManager.checkEditorMode(sender, true))
						return;
					if (q.getStages().isEmpty()) {
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.QuestEmpty"));
						return;
					}
					Quest.synchronizeLocal(q);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.QuestSaveCfgSuccess", q.getQuestName()));
					break;
			}
			QuestEditorManager.exit(sender);
		} else {
			switch (args[1]) {
				case "select":
					if (QuestUtil.getQuest(args[2]) == null) {
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.QuestNotFound"));
						return;
					}
					if (QuestEditorManager.checkEditorMode(sender, false)) {
						if (!confirm.contains(sender.getName())) {
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.QuitEditing"));
							confirm.add(sender.getName());
							return;
						}
					}
					QuestEditorManager.edit(sender, QuestUtil.getQuest(args[2]).clone());
					QuestEditorManager.editQuest(sender);
					confirm.remove(sender.getName());
					QuestChatManager.info(sender, "&c" + I18n.locMsg("EditorMessage.Entered"));
					return;
				case "addnew":
					CommandNewObject.execute(q, sender, args);
					return;
				case "edit":
					CommandEditQuest.execute(q, sender, args);
					return;
				case "newquest":
					CommandNewQuest.execute(sender, args);
					return;
				case "remove":
					CommandRemoveQuest.execute(q, sender, args);
					return;
			}
			QuestEditorManager.mainGUI(sender);
		}
	}

}
