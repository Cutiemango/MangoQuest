package me.Cutiemango.MangoQuest.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestBookPage;
import me.Cutiemango.MangoQuest.QuestStorage;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.TextComponentFactory;
import me.Cutiemango.MangoQuest.conversation.ConversationProgress;
import me.Cutiemango.MangoQuest.conversation.QuestChoice.Choice;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.data.QuestFinishData;
import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.TextComponent;

public class QuestGUIManager {
	
	public static void openGUI(Player p, QuestProgress q){
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&l任務名稱：").add(q.getQuest().getQuestName()).changeLine();
		
		// NPC
		if (!q.getQuest().isCommandQuest()){
			NPC npc = q.getQuest().getQuestNPC();
			p1.add("&l任務NPC：").add(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getEntity().getLocation(), false)).changeLine();
			p1.changeLine();
		}
		
		// Objects
		p1.add("&l任務內容：").changeLine();;
		for (int i = 0; i < q.getQuest().getStages().size(); i++){
			if (q.getCurrentStage() > i){
				for (SimpleQuestObject obj : q.getQuest().getStage(i).getObjects()){
					p1.add(obj.toTextComponent(true)).changeLine();
				}
			}
			else if (q.getCurrentStage() == i){
				for (int k = 0; k < q.getCurrentObjects().size(); k++){
					SimpleQuestObject obj = q.getQuest().getStage(i).getObjects().get(k);
					QuestObjectProgress ob = q.getCurrentObjects().get(k);
					if (ob.getObject().equals(obj) && ob.isFinished())
						p1.add(obj.toTextComponent(true)).changeLine();
					else{
						p1.add(obj.toTextComponent(false));
						if (obj instanceof NumerableObject)
							p1.add(" &8(" + ob.getProgress() + "/" + ((NumerableObject)obj).getAmount() + ")");
						p1.changeLine();
					}
				}
			}
			else{
				for (int j = 0; j < q.getQuest().getStage(i).getObjects().size(); j++){
					p1.add("&8&l？？？").changeLine();
				}
			}
			
			// OutLine
			QuestBookPage p2 = new QuestBookPage();
			p2.add("&l任務提要：").changeLine();
			for (String out : q.getQuest().getQuestOutline()){
				p2.add(out).changeLine();
			}
			
			// Reward
			QuestBookPage p3 = new QuestBookPage();
			p3.add("&l任務獎勵：").changeLine();
			
			if (q.getQuest().getQuestReward().hasItem()){
				for (ItemStack is : q.getQuest().getQuestReward().getItems()){
					p3.add(TextComponentFactory.convertItemHoverEvent(is, false));
					p3.add(" &l" + is.getAmount() + " &0個").changeLine();
				}
			}
			
			if (q.getQuest().getQuestReward().hasMoney())
				p3.add("&6金錢&0 " + q.getQuest().getQuestReward().getMoney() + " &6元").changeLine();
			
			if (q.getQuest().getQuestReward().hasExp())
				p3.add("&a經驗值&0 " + q.getQuest().getQuestReward().getExp() + " &a點").changeLine();
			
			if (q.getQuest().getQuestReward().hasFriendPoint()){
				for (Integer id : q.getQuest().getQuestReward().getFp().keySet()){
					NPC npc = CitizensAPI.getNPCRegistry().getById(id);
					p3.add(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getEntity().getLocation(), false));
					p3.add(" &c將會感激你").changeLine();
				}
			}
			
			openBook(p, p1, p2, p3);
		}
	}
	
	public static void openBook(Player p, QuestBookPage... qp){
		List<TextComponent> list = new ArrayList<>();
		for (QuestBookPage page : qp){
			list.add(page.getOriginalPage());
		}
		openBook(p, list.toArray(new TextComponent[list.size()]));
	}
	
	public static void openConversation(Player p){
		ConversationProgress cp = QuestUtil.getConvProgress(p);
		TextComponent p1 = new TextComponent(QuestUtil.translateColor(cp.getDefaultTitleString()));
		p1.addExtra("\n");
		cp.setCurrentBook(new LinkedList<>(QuestUtil.createList(p1)));
		updateConversation(p, cp);
	}
	
	public static void updateConversation(Player p, ConversationProgress cp){
		openBook(p, cp.getCurrentBook().toArray(new TextComponent[cp.getCurrentBook().size()]));
	}
	
	public static void openChoice(Player p, TextComponent q, List<Choice> c){
		QuestBookPage p1 = new QuestBookPage();
		p1.add("       &0=》 &c&l選擇 &0《=").changeLine();
		p1.add(q).changeLine();
		for (int i = 0; i < c.size(); i++){
			p1.changeLine();
			p1.add(TextComponentFactory.regClickCmdEvent("- " + c.get(i).getContent(), "/mq conv choose " + i));
			p1.changeLine();
		}
		openBook(p, p1);
	}
	
	public static void openJourney(Player p){
		QuestPlayerData qd = QuestUtil.getData(p);
		QuestBookPage p1 = new QuestBookPage();
		p1.add("&0&l[進行中的任務]").changeLine();
		for (QuestProgress qp : qd.getProgresses()){
			p1.changeLine();
			p1.add(TextComponentFactory.convertViewQuest(qp.getQuest())).add("：");
			p1.add(TextComponentFactory.regClickCmdEvent("&c&l【放棄】", "/mq quest quit " + qp.getQuest().getInternalID()));
			p1.changeLine();
			for (QuestObjectProgress qop : qp.getCurrentObjects()){
				p1.add("- ");
				if (qop.isFinished())
					p1.add(qop.getObject().toTextComponent(true)).changeLine();
				else{
					p1.add(qop.getObject().toTextComponent(false));
					if (qop.getObject() instanceof NumerableObject)
						p1.add(" &8(" + qop.getProgress() + "/" + ((NumerableObject)qop.getObject()).getAmount() + ")");
					p1.changeLine();
				}
			}
		}
		
		QuestBookPage p2 = new QuestBookPage();
		p2.add("&0&l[可進行的任務]").changeLine();

		for (Quest q : QuestStorage.Quests.values()){
			if (!qd.canTake(q, false))
				continue;
			else{
				p2.add("- ");
				p2.add(TextComponentFactory.convertViewQuest(q));
				if (q.isCommandQuest())
					p2.add(TextComponentFactory.regClickCmdEvent("&2&l【接受】", "/mq quest take " + q.getInternalID()));
				p2.changeLine();
			}
		}
		
		QuestBookPage p3 = new QuestBookPage();
		
		p3.add("&0&l[已完成的任務]").changeLine();;

		for (QuestFinishData qfd : qd.getFinishQuests()){
			p3.add("- ");
			p3.add(TextComponentFactory.convertViewQuest(qfd.getQuest()));
			p3.add("： 已完成 " + qfd.getFinishedTimes() + " 次").changeLine();
		}

		openBook(p, p1, p2, p3);
	}
	
	public static void openInfo(Player p, String msg){
		QuestBookPage p1 = new QuestBookPage();
		p1.add(msg).add("&7(取消請輸入cancel。)");
		openBook(p, p1);
	}

	public static void openBook(Player p, TextComponent... texts){
		Main.instance.handler.openBook(p, texts);
	}
	
	public static void openNPCInfo(Player p, NPC npc, boolean trade){
		QuestPlayerData qd = QuestUtil.getData(p);
		QuestBookPage p1 = new QuestBookPage();
		
		//Title
		p1.add("&5&lNPC介面 &0&l| ");
		p1.add(TextComponentFactory.convertLocHoverEvent(npc.getName(), npc.getEntity().getLocation(), false));
		p1.changeLine();
		
		List<Quest> holder = new ArrayList<>();
		
		//Message
		p1.changeLine();
		p1.add("&0&l" + npc.getName() + "&0：「").add(QuestUtil.getNPCMessage(npc.getId(), qd.getNPCfp(npc.getId()))).add("」").changeLine();
		p1.changeLine();
		
		//Interaction List
		p1.add("&0&l[互動列表]").changeLine();
		if (trade)
			p1.add(TextComponentFactory.regClickCmdEvent("&0- &6&l＄&0【交易物品】", "/mq quest trade " + npc.getId())).changeLine();
		for (QuestProgress q : qd.getNPCtoTalkWith(npc)){
			p1.add("&0- &6&l？ &0");
			p1.add(TextComponentFactory.convertViewQuest(q.getQuest()));
			p1.add(TextComponentFactory.regHoverEvent(
					TextComponentFactory.regClickCmdEvent("&9&l【對話】", "/mq conv npc " + npc.getId()),
					"&9點擊&f以開始對話"));
			if (qd.isCurrentlyDoing(q.getQuest()) && !q.getQuest().isCommandQuest() && q.getQuest().getQuestNPC().equals(npc)){
				p1.add(TextComponentFactory.regHoverEvent(
						TextComponentFactory.regClickCmdEvent("&c&l【放棄】", "/mq quest quit " + q.getQuest().getInternalID()),
						"&c放棄任務 &f" + q.getQuest().getQuestName() + "\n&4所有的任務進度都會消失。"));
				holder.add(q.getQuest());
			}
			p1.add("\n");
		}
		for (Quest q : QuestUtil.getGivenNPCQuests(npc)){
			if (qd.canTake(q, false)){
				if (qd.hasFinished(q))
					p1.add("&0- &8&l！ &0");
				else
					p1.add("&0- &6&l！ &0");
				p1.add(TextComponentFactory.convertViewQuest(q));
				p1.add(TextComponentFactory.regHoverEvent(
						TextComponentFactory.regClickCmdEvent("&2&l【接受】", "/mq quest take " + q.getInternalID()),
						"&a接受任務 &f" + q.getQuestName()));
				p1.changeLine();
				continue;
			}
			else if (qd.isCurrentlyDoing(q)){
				if (holder.contains(q))
					continue;
				p1.add("&0- &8&l？ &0");
				p1.add(TextComponentFactory.convertViewQuest(q));
				p1.add(TextComponentFactory.regHoverEvent(
						TextComponentFactory.regClickCmdEvent("&c&l【放棄】", "/mq quest quit " + q.getInternalID()),
						"&c放棄任務 &f" + q.getQuestName() + "\n&4所有的任務進度都會消失。"));
				p1.changeLine();
				continue;
			}
			else{
				p1.add("&0- ");
				p1.add(TextComponentFactory.convertRequirement(qd, q));
				p1.changeLine();
			}
		}
		p1.changeLine();
		for (QuestConversation qc : QuestUtil.getConversations(npc.getId(), qd.getNPCfp(npc.getId()))){
			if (qd.hasFinished(qc)){
				p1.add("&0- &7&o");
				p1.add(TextComponentFactory.regClickCmdEvent(qc.getName() + " 〈&c&o♥&7&o〉", "/mq conv opennew " + qc.getInternalID()));
			}
			else{
				p1.add("&0- &6&l！ &0&l");
				p1.add(TextComponentFactory.regClickCmdEvent(qc.getName() + " 〈&c♥&0&l〉", "/mq conv opennew " + qc.getInternalID()));
			}
			p1.changeLine();
		}
		openBook(p, p1);
	}

}
