package me.Cutiemango.MangoQuest.manager;

import com.sucy.skill.SkillAPI;
import joptsimple.internal.Strings;
import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.objects.RequirementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.quantumrpg.data.api.UserProfile;
import su.nightexpress.quantumrpg.modules.list.classes.api.RPGClass;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RequirementManager
{
	public static Optional<String> meetRequirementWith(Player p, EnumMap<RequirementType, Object> requirements, boolean debug) {
		QuestPlayerData pd = QuestUtil.getData(p);

		List<String> failMsg = new ArrayList<>();

		for (RequirementType t : requirements.keySet()) {
			Object value = requirements.get(t);
			switch (t) {
				case PERMISSION:
					if (!(value instanceof List)) {
						DebugHandler.log(5, "[Requirements] Requirement type is PERMISSION, but the value is not a list.");
						break;
					}
					List<String> permissions = (List<String>) value;
					for (String perm : permissions) {
						if (!p.hasPermission(perm)) {
							failMsg.add(I18n.locMsg("Requirements.NotMeet.Permission"));
							break;
						}
					}
					break;
				case QUEST:
					for (String s : (List<String>) value) {
						Quest q = QuestUtil.getQuest(s);
						if (!pd.hasFinished(q))
							failMsg.add(I18n.locMsg("Requirements.NotMeet.Quest", q.getQuestName()));
					}
					break;
				case LEVEL:
					int level = (Integer) value;
					if (p.getLevel() < level)
						failMsg.add(I18n.locMsg("Requirements.NotMeet.Level", Integer.toString(level)));
					break;
				case MONEY:
					if (Main.getHooker().hasEconomyEnabled()) {
						double money = (Double) value;
						if (!Main.getHooker().getEconomy().hasAccount(p) || Main.getHooker().getEconomy().getBalance(p) < money)
							failMsg.add(I18n.locMsg("Requirements.NotMeet.Money", Double.toString(QuestUtil.cut(money))));
					}
					break;
				case ITEM:
					if (!(value instanceof List)) {
						DebugHandler.log(5, "[Requirements] Requirement type is ITEM, but the value is not a list.");
						break;
					}
					if (ConfigSettings.USE_WEAK_ITEM_CHECK) {
						HashMap<QuestValidater.WrappedWeakItem, Integer> reqItems = new HashMap<>();
						for (ItemStack i : (List<ItemStack>) value)
							if (i != null)
								reqItems.put(new QuestValidater.WrappedWeakItem(i), i.getAmount());

						for (ItemStack owned : p.getInventory().getContents()) {
							if (owned == null || owned.getType() == Material.AIR)
								continue;
							QuestValidater.WrappedWeakItem wrapped = new QuestValidater.WrappedWeakItem(owned);
							if (reqItems.containsKey(wrapped))
								reqItems.put(wrapped, Math.max(0, reqItems.get(wrapped) - owned.getAmount()));
						}

						for (QuestValidater.WrappedWeakItem wrapped : reqItems.keySet()) {
							if (reqItems.get(wrapped) != 0) {
								failMsg.add(I18n.locMsg("Requirements.NotMeet.Item", QuestUtil.getItemName(wrapped),
										Integer.toString(reqItems.get(wrapped))));
								if (debug) {
									DebugHandler.log(5, "[Requirements] User has failed requirement: " + t.toString());
									DebugHandler.log(5, "[Requirements] Did not found enough (or any) %s in user's inventory.",
											QuestUtil.getItemName(wrapped));
								}
							}
						}
					} else {
						for (ItemStack i : (List<ItemStack>) value) {
							if (!p.getInventory().containsAtLeast(i, i.getAmount())) {
								failMsg.add(I18n.locMsg("Requirements.NotMeet.Item", QuestUtil.getItemName(i), Integer.toString(i.getAmount())));
								if (debug) {
									DebugHandler.log(5, "[Requirements] User has failed requirement: " + t.toString());
									DebugHandler
											.log(5, "[Requirements] Did not found enough (or any) %s in user's inventory.", QuestUtil.getItemName(i));
								}
							}
						}
					}
					break;
				case SKILLAPI_CLASS:
					String classID = (String) value;
					if (classID.equalsIgnoreCase("none"))
						break;

					if (Main.getHooker().hasSkillAPIEnabled() && SkillAPI.hasPlayerData(p) && SkillAPI.getPlayerData(p).getMainClass() != null) {
						com.sucy.skill.api.classes.RPGClass userClass = SkillAPI.getPlayerData(p).getMainClass().getData(), reqClass = SkillAPI
								.getClass(classID);

						if (!SkillAPI.isClassRegistered(classID))
							failMsg.add(I18n.locMsg("Requirements.NotMeet.NoClass") + classID);
						else if (!SkillAPI.getPlayerData(p).isExactClass(reqClass)) {
							boolean found = false;
							if ((Boolean) requirements.get(RequirementType.ALLOW_DESCENDANT)) {
								// We recursively search if user's ascendant class matches the required class.
								com.sucy.skill.api.classes.RPGClass it = userClass;
								while (it.getParent() != null) {
									if (it.getParent().getName().equalsIgnoreCase(classID)) {
										found = true;
										if (debug)
											DebugHandler.log(5, "[Requirements] User has descendant class: %s", classID);
										break;
									}
									it = it.getParent();
								}
							}
							if (!found) {
								failMsg.add(I18n.locMsg("Requirements.NotMeet.RPGClass", reqClass.getPrefix()));
								if (debug) {
									DebugHandler.log(5, "[Requirements] User has failed requirement: " + t.toString());
									DebugHandler.log(5, "[Requirements] User has the following class: %s, while the required class is %s.", userClass,
											classID);
								}
							}
						}
					}
					break;
				case SKILLAPI_LEVEL:
					int lvl = (Integer) value;
					if (lvl == 0)
						break;

					else if (Main.getHooker().hasSkillAPIEnabled() && SkillAPI.hasPlayerData(p))
						if (SkillAPI.getPlayerData(p).getMainClass() == null || SkillAPI.getPlayerData(p).getMainClass().getLevel() < lvl)
							failMsg.add(I18n.locMsg("Requirements.NotMeet.RPGLevel", Integer.toString(lvl)));
					break;
				case QRPG_CLASS:
					classID = (String) value;
					if (classID.equalsIgnoreCase("none"))
						break;

					if (Main.getHooker().hasQuantumRPGEnabled()) {
						UserProfile user = Main.getHooker().getQuantumRPG().getUserManager().getOrLoadUser(p).getActiveProfile();
						String userClassID = user.getClassData().getClassId();

						if (Main.getHooker().getQuantumRPG().getModuleCache().getClassManager().getClassById(classID) == null)
							failMsg.add(I18n.locMsg("Requirements.NotMeet.NoClass") + classID);
						else if (!classID.equalsIgnoreCase(userClassID)) {
							RPGClass reqClass = Main.getHooker().getQuantumRPG().getModuleCache().getClassManager()
									.getClassById(classID), userClass = Main.getHooker().getQuantumRPG().getModuleCache().getClassManager()
									.getClassById(userClassID);

							boolean found = false;
							if ((Boolean) requirements.get(RequirementType.ALLOW_DESCENDANT)) {
								RPGClass it = userClass;
								while (it.getParent() != null) {
									if (it.getParent().getId().equalsIgnoreCase(classID)) {
										found = true;
										if (debug)
											DebugHandler.log(5, "[Requirements] User has descendant class: %s", classID);
										break;
									}
									it = it.getParent();
								}
							}
							if (!found) {
								failMsg.add(I18n.locMsg("Requirements.NotMeet.RPGClass", reqClass.getName()));
								if (debug) {
									DebugHandler.log(5, "[Requirements] User has failed requirement: " + t.toString());
									DebugHandler.log(5, "[Requirements] User has the following class: %s, while the required class is %s.",
											user.getClassData().getClassId(), classID);
								}
							}
						}
					}
					break;
				case QRPG_LEVEL:
					lvl = (Integer) value;
					if (lvl == 0)
						break;
					else if (Main.getHooker().hasQuantumRPGEnabled()) {
						UserProfile user = Main.getHooker().getQuantumRPG().getUserManager().getOrLoadUser(p).getActiveProfile();
						if (user.getClassData() == null || user.getClassData().getLevel() < lvl) {
							failMsg.add(I18n.locMsg("Requirements.NotMeet.RPGLevel", Integer.toString(lvl)));
							if (debug) {
								DebugHandler.log(5, "[Requirements] User has failed requirement: " + t.toString());
								DebugHandler
										.log(5, "[Requirements] User has level %d, while the required level is %d.", user.getClassData().getLevel(),
												lvl);
							}
						}
					}
					break;
				case FRIEND_POINT:
					HashMap<Integer, Integer> map = (HashMap<Integer, Integer>) value;

					for (Integer id : map.keySet()) {
						if (pd.getNPCfp(id) < map.get(id))
							failMsg.add(I18n.locMsg("Requirements.NotMeet.FriendPoint"));
					}
					break;
			}
		}

		if (failMsg.isEmpty())
			return Optional.empty();

		return Optional.of(Strings.join(failMsg, "\n"));
	}

}
