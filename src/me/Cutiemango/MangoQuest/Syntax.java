package me.Cutiemango.MangoQuest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.citizensnpcs.api.CitizensAPI;

public class Syntax
{
	private String order = "";
	private String regex = "";
	private String desc = "";
	private Set<String> dividers = new HashSet<>();

	private Syntax(String s, String d, String... divider)
	{
		order = s;
		for (String div : divider)
		{
			order = order.replaceAll(div, "");
			dividers.add(div);
		}
		s = s.replaceAll("S", "\\\\S\\+");
		s = s.replaceAll("I", "\\\\d\\+");
		s = s.replaceAll("N", "\\\\d\\+");
		regex = s;
		desc = d;
	}

	public static Syntax of(String customDivider, String description, String... divider)
	{
		return new Syntax(customDivider, description, divider);
	}

	public boolean matches(Player p, String s)
	{
		if (!s.matches(regex))
		{
			QuestChatManager.syntaxError(p, desc, s);
			return false;
		}
		// Create Object list
		ArrayList<Object> objs = new ArrayList<>();
		String target = s;
		for (String st : dividers)
		{
			String[] sp = target.split(st);
			for (int i = 0; i < sp.length - 1; i++)
			{
				objs.add(sp[i]);
			}
			target = sp[sp.length - 1];
		}

		// Check Object Types (Check NPC)
		for (int i = 0; i < objs.size(); i++)
		{
//			if (order.toCharArray()[i] == 'I')
//			{
//				try
//				{
//					objs.set(i, Integer.parseInt((String) objs.get(i)));
//				}
//				catch (NumberFormatException e)
//				{
//					QuestChatManager.syntaxError(p, I18n.locMsg("Syntax.Number"), objs.get(i).toString());
//					return false;
//				}
//			}
//			else
				if (order.toCharArray()[i] == 'N')
				{
					int id = 0;
//					try
//					{
						id = Integer.parseInt((String) objs.get(i));
//					}
//					catch (NumberFormatException e)
//					{
//						QuestChatManager.syntaxError(p, I18n.locMsg("Syntax.NPCID"), objs.get(i).toString());
//						return false;
//					}

					objs.set(i, Integer.parseInt((String) objs.get(i)));

					if (CitizensAPI.getNPCRegistry().getById(id) == null)
					{
						QuestChatManager.error(p, I18n.locMsg("SyntaxError.NPCNotValid"));
						return false;
					}
				}
		}
		return true;
	}

	public String getDescription()
	{
		return desc;
	}
}