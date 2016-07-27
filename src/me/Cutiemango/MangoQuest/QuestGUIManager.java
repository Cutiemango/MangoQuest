package me.Cutiemango.MangoQuest;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import io.netty.buffer.Unpooled;
import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectItemDeliver;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_10_R1.EnumHand;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketDataSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutCustomPayload;

public class QuestGUIManager {
	
	@SuppressWarnings("unchecked")
	public static void openGUI(QuestProgress q, Player p){
		List<IChatBaseComponent> pages = null;
		
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		try {
			pages = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(bookMeta);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TextComponent QuestInfo = new TextComponent(ChatColor.BOLD + "任務名稱： ");
		QuestInfo.addExtra(q.getQuest().getQuestName() + "\n\n");
		QuestInfo.addExtra(ChatColor.BOLD + "任務提要： \n");
		QuestInfo.addExtra(q.getQuest().getQuestOutline() + "\n\n");
		if (q.getQuest().getQuestNPC() != null){
			QuestInfo.addExtra(ChatColor.BOLD + "任務NPC： \n");
			QuestInfo.addExtra(QuestUtil.convertNPCtoHoverEvent(false, q.getQuest().getQuestNPC()));
			QuestInfo.addExtra("\n\n");
		}else{
			p.sendMessage(QuestStorage.prefix + ChatColor.DARK_RED + "NPC讀取錯誤！");
		}
		
		
		QuestInfo.addExtra(ChatColor.BOLD + "任務內容： \n");
		for (int i = 0; i < q.getQuest().getStages().size(); i++){
			if (q.getCurrentStage() > i){
				for (SimpleQuestObject obj : q.getQuest().getStage(i).getObjects()){
					QuestInfo.addExtra(obj.toTextComponent(true));
					QuestInfo.addExtra("\n");
				}
			}
			else if (q.getCurrentStage() == i){
				for (int k = 0; k < q.getCurrentObjects().size(); k++){
					SimpleQuestObject obj = q.getQuest().getStage(i).getObjects().get(k);
					QuestObjectProgress ob = q.getCurrentObjects().get(k);
					if (ob.getObject().equals(obj) && ob.isFinished()){
						QuestInfo.addExtra(obj.toTextComponent(true));
						QuestInfo.addExtra("\n");
					}
					else{
						QuestInfo.addExtra(obj.toTextComponent(false));
						if (obj instanceof QuestObjectItemDeliver)
							QuestInfo.addExtra(new TextComponent(QuestUtil.translateColor(
									" &8(" + ob.getProgress() + "/" + ((QuestObjectItemDeliver)obj).getDeliverAmount() + ")")));
						else if (obj instanceof QuestObjectBreakBlock)
							QuestInfo.addExtra(new TextComponent(QuestUtil.translateColor(
									" &8(" + ob.getProgress() + "/" + ((QuestObjectBreakBlock)obj).getAmount() + ")")));
						else if (obj instanceof QuestObjectKillMob)
							QuestInfo.addExtra(new TextComponent(QuestUtil.translateColor(
									" &8(" + ob.getProgress() + "/" + ((QuestObjectKillMob)obj).getAmount() + ")")));
						QuestInfo.addExtra("\n");
					}
				}
			}
			else{
				for (int j = 0; j < q.getQuest().getStage(i).getObjects().size(); j++){
					QuestInfo.addExtra(new TextComponent(QuestUtil.translateColor("&8&l？？？")));
					QuestInfo.addExtra("\n");
				}
			}
		}
		
		IChatBaseComponent IQuestInfo = ChatSerializer.a(ComponentSerializer.toString(QuestInfo));
		pages.add(IQuestInfo);
		
		TextComponent QuestReward = new TextComponent(ChatColor.BOLD + "任務獎勵： ");
		
		if (q.getQuest().getQuestReward().hasItem()){
			for (ItemStack is : q.getQuest().getQuestReward().getItems()){
				QuestReward.addExtra("\n");
				QuestReward.addExtra(QuestUtil.convertItemStacktoHoverEvent(false, is));
				TextComponent suffix = new TextComponent(ChatColor.translateAlternateColorCodes('&' , " &l" + is.getAmount() + " &0個"));
				QuestReward.addExtra(suffix);
			}
		}
		
		if (q.getQuest().getQuestReward().hasMoney()){
			QuestReward.addExtra(ChatColor.GOLD + "金錢 " + ChatColor.BLACK + q.getQuest().getQuestReward().getMoney() + ChatColor.GOLD + " 元");
		}

		IChatBaseComponent IQuestReward = ChatSerializer.a(ComponentSerializer.toString(QuestReward));
		pages.add(IQuestReward);
		
		book.setItemMeta(bookMeta);
		
		openBook(book, p);
	}
	
	@SuppressWarnings("unchecked")
	public static void openViewGUI(Quest quest, Player p){
		List<IChatBaseComponent> pages = null;
		
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		try {
			pages = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(bookMeta);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TextComponent QuestInfo = new TextComponent(ChatColor.BOLD + "任務名稱： ");
		QuestInfo.addExtra(ChatColor.stripColor(quest.getQuestName()) + "\n\n");
		QuestInfo.addExtra(ChatColor.BOLD + "任務提要： \n");
		QuestInfo.addExtra(ChatColor.stripColor(quest.getQuestOutline()) + "\n\n");
		if (quest.getQuestNPC() != null){
			QuestInfo.addExtra(ChatColor.BOLD + "任務NPC： \n");
			QuestInfo.addExtra(QuestUtil.convertNPCtoHoverEvent(false, quest.getQuestNPC()));
			QuestInfo.addExtra("\n\n");
		}else{
			p.sendMessage(QuestStorage.prefix + ChatColor.DARK_RED + "NPC讀取錯誤！");
		}
		
		
		QuestInfo.addExtra(ChatColor.BOLD + "任務內容： \n");
		for (SimpleQuestObject obj : quest.getStage(0).getObjects()){
			QuestInfo.addExtra(obj.toTextComponent(false));
			QuestInfo.addExtra("\n");
		}
		
		IChatBaseComponent IQuestInfo = ChatSerializer.a(ComponentSerializer.toString(QuestInfo));
		pages.add(IQuestInfo);
		
		TextComponent QuestReward = new TextComponent(ChatColor.BOLD + "任務獎勵： ");
		
		if (quest.getQuestReward().hasItem()){
			for (ItemStack is : quest.getQuestReward().getItems()){
				QuestReward.addExtra("\n");
				QuestReward.addExtra(QuestUtil.convertItemStacktoHoverEvent(false, is));
				TextComponent suffix = new TextComponent(ChatColor.translateAlternateColorCodes('&' , " &l" + is.getAmount() + " &0個"));
				QuestReward.addExtra(suffix);
			}
		}
		
		if (quest.getQuestReward().hasMoney()){
			QuestReward.addExtra(ChatColor.GOLD + "金錢 " + ChatColor.BLACK + quest.getQuestReward().getMoney() + ChatColor.GOLD + " 元");
		}

		IChatBaseComponent IQuestReward = ChatSerializer.a(ComponentSerializer.toString(QuestReward));
		pages.add(IQuestReward);
		
		book.setItemMeta(bookMeta);
		
		openBook(book, p);
	}
	
	private static void openBook(ItemStack book, Player p){
		int slot = p.getInventory().getHeldItemSlot();
		ItemStack old = p.getInventory().getItem(slot);
		p.getInventory().setItem(slot, book);
		PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());
		packetdataserializer.a(EnumHand.MAIN_HAND);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", packetdataserializer));
		p.getInventory().setItem(slot, old);
	}

}
