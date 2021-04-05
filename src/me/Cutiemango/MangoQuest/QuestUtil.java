package me.Cutiemango.MangoQuest;

import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestValidater;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

public class QuestUtil
{

	public static void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		Main.getInstance().handler.sendTitle(p, fadeIn, stay, fadeOut, title, subtitle);
	}

	public static int randomInteger(int min, int max) {
		return new Random().nextInt(max - min + 1) + min;
	}

	public static <T> List<T> convert(Set<T> set) {
		return new ArrayList<T>(set);
	}

	public static QuestPlayerData getData(Player p) {
		return QuestStorage.playerData.get(p.getName());
	}

	public static double cut(double d) {
		return Math.floor((d * 100)) / 100;
	}

	public static void executeSyncCommand(Player p, String command) {
		if (p == null || command == null)
			return;
		Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> p.performCommand(command));
	}

	public static void executeSyncOPCommand(Player p, String command) {
		if (p == null || command == null)
			return;
		boolean op = p.isOp();
		Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
			try {
				if (!op)
					p.setOp(true);
				Bukkit.dispatchCommand(p, command);
			}
			catch (Exception e) {
				QuestChatManager.logCmd(Level.SEVERE, "The server encountered an unchecked exception when making player execute OP commands.");
				QuestChatManager.logCmd(Level.SEVERE, "Please report this to the plugin author.");
				e.printStackTrace();
			}
			finally {
				if (!op)
					p.setOp(false);
			}
		});
	}

	public static void executeSyncConsoleCommand(String command) {
		Optional.ofNullable(command).ifPresent(
				cmd -> Bukkit.getScheduler().callSyncMethod(Main.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)));
	}

	public static String convertArgsString(String[] array, int startIndex) {
		return String.join(" ", Arrays.copyOfRange(array, startIndex, array.length));
	}

	public static Set<FriendConversation> getConversations(NPC npc, int fp) {
		Set<FriendConversation> set = new HashSet<>();
		for (FriendConversation conv : QuestStorage.friendConversations) {
			if (conv.getNPC().equals(npc) && (fp >= conv.getReqPoint()))
				set.add(conv);
		}
		return set;
	}

	public static Quest getQuest(String s) {
		return QuestStorage.localQuests.get(s);
	}

	public static String getItemName(ItemStack is) {
		if (is.hasItemMeta() && is.getItemMeta().hasDisplayName())
			return is.getItemMeta().getDisplayName();
		else
			return translate(is.getType());
	}

	public static String getItemName(QuestValidater.WrappedWeakItem wrapper) {
		if (wrapper.hasItemMeta() && wrapper.hasDisplayName())
			return wrapper.getDisplayName();
		else
			return translate(wrapper.getType());
	}

	@SafeVarargs
	public static <T> List<T> createList(T... args) {
		List<T> list = new ArrayList<>();
		Collections.addAll(list, args);
		return list;
	}

	public static String translate(Material mat) {
		return Optional.ofNullable(QuestStorage.translationMap.get(mat)).orElseGet(() -> I18n.locMsg("Translation.UnknownItem"));
	}

	public static String translate(ItemStack item) {
		if (item == null)
			return I18n.locMsg("Translation.UnknownItem");
		return getItemName(item);
	}

	public static String translate(EntityType e) {
		return Optional.ofNullable(QuestStorage.entityTypeMap.get(e)).orElseGet(() -> I18n.locMsg("Translation.UnknownEntity"));
	}
}
