package me.Cutiemango.MangoQuest;

import java.util.HashMap;

public class QuestNPC {
	
	public QuestNPC(){
		msg.put(0, "...");
	}

	private HashMap<Integer, String> msg = new HashMap<>();
	
	public String getNPCMessage(int value){
		int r = 0;
		if (value == 0)
			return msg.get(0);
		for (int j : msg.keySet()){
			if (value < 0 && j < 0 && j > value && r > j)
					r = j;
			else if (value > 0 && value >= j && j > r)
					r = j;
			else continue;
		}
		
		return msg.get(r);
	}
	
	public void put(int i, String s){
		msg.put(i, s);
	}
}
