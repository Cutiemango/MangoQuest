package me.Cutiemango.MangoQuest;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import io.netty.buffer.Unpooled;
import me.Cutiemango.MangoQuest.data.QuestFinishData;
import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_10_R1.EnumHand;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketDataSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutCustomPayload;

public class QuestGUIManager {

	public static void openGUI(Player p, QuestProgress q){
		TextComponent p1 = new TextComponent(ChatColor.BOLD + "任務名稱： ");
		p1.addExtra(q.getQuest().getQuestName() + "\n");
		if (q.getQuest().getQuestNPC() != null){
			p1.addExtra(ChatColor.BOLD + "任務NPC： ");
			NPC npc = q.getQuest().getQuestNPC();
			p1.addExtra(TextComponentFactory.convertLocationtoHoverEvent(npc.getName(), npc.getEntity().getLocation(), false));
			p1.addExtra("\n\n");
		}
		
		p1.addExtra(ChatColor.BOLD + "任務內容： \n");
		for (int i = 0; i < q.getQuest().getStages().size(); i++){
			if (q.getCurrentStage() > i){
				for (SimpleQuestObject obj : q.getQuest().getStage(i).getObjects()){
					p1.addExtra(obj.toTextComponent(true));
					p1.addExtra("\n");
				}
			}
			else if (q.getCurrentStage() == i){
				for (int k = 0; k < q.getCurrentObjects().size(); k++){
					SimpleQuestObject obj = q.getQuest().getStage(i).getObjects().get(k);
					QuestObjectProgress ob = q.getCurrentObjects().get(k);
					if (ob.getObject().equals(obj) && ob.isFinished()){
						p1.addExtra(obj.toTextComponent(true));
						p1.addExtra("\n");
					}
					else{
						p1.addExtra(obj.toTextComponent(false));
						if (obj instanceof NumerableObject)
							p1.addExtra(new TextComponent(QuestUtil.translateColor(
									" &8(" + ob.getProgress() + "/" + ((NumerableObject)obj).getAmount() + ")")));
						p1.addExtra("\n");
					}
				}
			}
			else{
				for (int j = 0; j < q.getQuest().getStage(i).getObjects().size(); j++){
					p1.addExtra(new TextComponent(QuestUtil.translateColor("&8&l？？？")));
					p1.addExtra("\n");
				}
			}
		}

		TextComponent p2 = new TextComponent(ChatColor.BOLD + "任務提要： \n");
		p2.addExtra(q.getQuest().getQuestOutline());

		TextComponent p3 = new TextComponent(ChatColor.BOLD + "任務獎勵：");

		if (q.getQuest().getQuestReward().hasItem()){
			for (ItemStack is : q.getQuest().getQuestReward().getItems()){
				p3.addExtra("\n");
				p3.addExtra(TextComponentFactory.convertItemStacktoHoverEvent(false, is));
				TextComponent suffix = new TextComponent(ChatColor.translateAlternateColorCodes('&' , " &l" + is.getAmount() + " &0個"));
				p3.addExtra(suffix);
			}
		}
		
		if (q.getQuest().getQuestReward().hasMoney()){
			p3.addExtra(ChatColor.GOLD + "金錢 " + ChatColor.BLACK + q.getQuest().getQuestReward().getMoney() + ChatColor.GOLD + " 元");
		}

		openBook(p, p1, p2, p3);
	}
	
	public static void openJourney(Player p){
		QuestPlayerData qd = QuestUtil.getData(p);
		TextComponent p1 = new TextComponent(QuestUtil.translateColor("&0&l[進行中的任務]"));
		p1.addExtra("\n");
		for (QuestProgress qp : qd.getProgresses()){
			p1.addExtra("\n");
			p1.addExtra(TextComponentFactory.convertViewQuest(qp.getQuest()));
			p1.addExtra("：\n");
			for (QuestObjectProgress qop : qp.getCurrentObjects()){
				p1.addExtra("- ");
				if (qop.isFinished()){
					p1.addExtra(qop.getObject().toTextComponent(true));
					p1.addExtra("\n");
				}
				else{
					p1.addExtra(qop.getObject().toTextComponent(false));
					if (qop.getObject() instanceof NumerableObject)
						p1.addExtra(new TextComponent(QuestUtil.translateColor(
								" &8(" + qop.getProgress() + "/" + ((NumerableObject)qop.getObject()).getAmount() + ")")));
					p1.addExtra("\n");
				}
			}
		}

		TextComponent p2 = new TextComponent(QuestUtil.translateColor("&0&l[可進行的任務]"));
		p2.addExtra("\n");
		for (Quest q : QuestStorage.Quests.values()){
			if (!qd.canTake(q, false))
				continue;
			else{
				p2.addExtra("- ");
				p2.addExtra(TextComponentFactory.convertViewQuest(q));
				p2.addExtra("\n");
			}
		}

		TextComponent p3 = new TextComponent(QuestUtil.translateColor("&0&l[已完成的任務]"));
		p3.addExtra("\n");
		for (QuestFinishData qfd : qd.getFinishQuests()){
			p3.addExtra("- ");
			p3.addExtra(TextComponentFactory.convertViewQuest(qfd.getQuest()));
			p3.addExtra("： 已完成 " + qfd.getFinishedTimes() + " 次\n");
		}

		openBook(p, p1, p2, p3);
	}
	
	public static void openInfo(Player p, String msg){
		TextComponent p1 = new TextComponent(QuestUtil.translateColor(msg));
		openBook(p, p1);
	}
	
	@SuppressWarnings("unchecked")
	public static void openBook(Player p, TextComponent... texts){
		List<IChatBaseComponent> pages = null;
		
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		try {
			pages = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(bookMeta);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (TextComponent t : texts){
			pages.add(ChatSerializer.a(ComponentSerializer.toString(t)));
		}
		
		book.setItemMeta(bookMeta);
		
		int slot = p.getInventory().getHeldItemSlot();
		ItemStack old = p.getInventory().getItem(slot);
		p.getInventory().setItem(slot, book);
		PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());
		packetdataserializer.a(EnumHand.MAIN_HAND);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", packetdataserializer));
		p.getInventory().setItem(slot, old);
	}

}
