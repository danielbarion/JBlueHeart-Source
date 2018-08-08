package gr.sr.dressmeEngine.handler;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.xml.impl.ArmorSetsData;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.L2ArmorSet;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.ItemType;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import gr.sr.dressmeEngine.DressMeHandler;
import gr.sr.dressmeEngine.data.DressMeArmorData;
import gr.sr.dressmeEngine.data.DressMeCloakData;
import gr.sr.dressmeEngine.data.DressMeHatData;
import gr.sr.dressmeEngine.data.DressMeShieldData;
import gr.sr.dressmeEngine.data.DressMeWeaponData;
import gr.sr.dressmeEngine.util.Util;
import gr.sr.dressmeEngine.xml.dataHolder.DressMeArmorHolder;
import gr.sr.dressmeEngine.xml.dataHolder.DressMeCloakHolder;
import gr.sr.dressmeEngine.xml.dataHolder.DressMeHatHolder;
import gr.sr.dressmeEngine.xml.dataHolder.DressMeShieldHolder;
import gr.sr.dressmeEngine.xml.dataHolder.DressMeWeaponHolder;
import gr.sr.main.Conditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DressMeVCmd implements IVoicedCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(DressMeVCmd.class);
	
	String index_path = "data/html/sunrise/dressme/index.htm";
	String info_path = "data/html/sunrise/dressme/info.htm";
	String undressme_path = "data/html/sunrise/dressme/undressme.htm";
	
	String index_armor_path = "data/html/sunrise/dressme/index-armor.htm";
	String template_armor_path = "data/html/sunrise/dressme/template-armor.htm";
	
	String index_cloak = "data/html/sunrise/dressme/index-cloak.htm";
	String template_cloak_path = "data/html/sunrise/dressme/template-cloak.htm";
	
	String index_shield_path = "data/html/sunrise/dressme/index-shield.htm";
	String template_shield_path = "data/html/sunrise/dressme/template-shield.htm";
	
	String index_weapon_path = "data/html/sunrise/dressme/index-weapon.htm";
	String template_weapon_path = "data/html/sunrise/dressme/template-weapon.htm";
	
	String index_hat_path = "data/html/sunrise/dressme/index-hat.htm";
	String template_hat_path = "data/html/sunrise/dressme/template-hat.htm";
	
	String dress_cloak_path = "data/html/sunrise/dressme/dress-cloak.htm";
	String dress_shield_path = "data/html/sunrise/dressme/dress-shield.htm";
	String dress_armor_path = "data/html/sunrise/dressme/dress-armor.htm";
	String dress_weapon_path = "data/html/sunrise/dressme/dress-weapon.htm";
	String dress_hat_path = "data/html/sunrise/dressme/dress-hat.htm";
	
	private final String[] _commandList = new String[]
	{
		"dressme",
		"undressme",
		
		"dressinfo",
		
		"showdress",
		"hidedress",
		
		"dressme-armor",
		"dress-armor",
		"dress-armorpage",
		"undressme-armor",
		
		"dressme-cloak",
		"dress-cloak",
		"dress-cloakpage",
		"undressme-cloak",
		
		"dressme-shield",
		"dress-shield",
		"dress-shieldpage",
		"undressme-shield",
		
		"dressme-weapon",
		"dress-weapon",
		"dress-weaponpage",
		"undressme-weapon",
		
		"dressme-hat",
		"dress-hat",
		"dress-hatpage",
		"undressme-hat"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance player, String args)
	{
		if (command.equals("dressme"))
		{
			String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_path);
			html = html.replace("<?show_hide?>", !player.getVarB("showVisualChange") ? "Show visual equip on other player!" : "Hide visual equip on other player!");
			html = html.replace("<?show_hide_b?>", !player.getVarB("showVisualChange") ? "showdress" : "hidedress");
			
			sendHtml(player, html);
			return true;
		}
		else if (command.equals("dressme-armor"))
		{
			L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			if ((slot == null) || !slot.isArmor())
			{
				player.sendMessage("Error: Armor chest must be equiped!");
				return false;
			}
			
			String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_armor_path);
			String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_armor_path);
			String block = "";
			String list = "";
			
			if (args == null)
			{
				args = "1";
			}
			
			String[] param = args.split(" ");
			
			final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
			final int perpage = 5;
			int counter = 0;
			
			String type = slot.getArmorItem().getItemType().getDescription();
			Map<Integer, DressMeArmorData> map = DressMeHandler.initArmorMap(type, new HashMap<>(), slot);
			if (map == null)
			{
				_log.error("Dress me system: Armor Map is null.");
				return false;
			}
			
			for (int i = (page - 1) * perpage; i < map.size(); i++)
			{
				DressMeArmorData dress = map.get(i + 1);
				if (dress != null)
				{
					block = template;
					
					String dress_name = dress.getName();
					
					if (dress_name.length() > 29)
					{
						dress_name = dress_name.substring(0, 29) + "...";
					}
					
					block = block.replace("{bypass}", "bypass -h voice .dress-armorpage " + dress.getId());
					block = block.replace("{name}", dress_name);
					block = block.replace("{price}", Util.formatPay(player, dress.getPriceCount(), dress.getPriceId()));
					block = block.replace("{icon}", Util.getItemIcon(dress.getChest()));
					list += block;
				}
				
				counter++;
				
				if (counter >= perpage)
				{
					break;
				}
			}
			
			double count = Math.ceil((double) map.size() / perpage);
			int inline = 1;
			String navigation = "";
			
			for (int i = 1; i <= count; i++)
			{
				if (i == page)
				{
					navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-armor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-armor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				
				if (inline == 7)
				{
					navigation += "</tr><tr>";
					inline = 0;
				}
				inline++;
			}
			
			if (navigation.equals(""))
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}
			
			html = html.replace("{list}", list);
			html = html.replace("{navigation}", navigation);
			
			NpcHtmlMessage msg = new NpcHtmlMessage();
			msg.setHtml(html);
			player.sendPacket(msg);
			return true;
		}
		else if (command.equals("dressme-cloak"))
		{
			String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_cloak);
			String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_cloak_path);
			String block = "";
			String list = "";
			
			if (args == null)
			{
				args = "1";
			}
			
			String[] param = args.split(" ");
			
			final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
			final int perpage = 5;
			int counter = 0;
			
			for (int i = (page - 1) * perpage; i < DressMeCloakHolder.getInstance().size(); i++)
			{
				DressMeCloakData cloak = DressMeCloakHolder.getInstance().getCloak(i + 1);
				if (cloak != null)
				{
					block = template;
					
					String cloak_name = cloak.getName();
					
					if (cloak_name.length() > 29)
					{
						cloak_name = cloak_name.substring(0, 29) + "...";
					}
					
					block = block.replace("{bypass}", "bypass -h voice .dress-cloakpage " + (i + 1));
					block = block.replace("{name}", cloak_name);
					block = block.replace("{price}", Util.formatPay(player, cloak.getPriceCount(), cloak.getPriceId()));
					block = block.replace("{icon}", Util.getItemIcon(cloak.getCloakId()));
					list += block;
				}
				
				counter++;
				
				if (counter >= perpage)
				{
					break;
				}
			}
			
			double count = Math.ceil((double) DressMeCloakHolder.getInstance().size() / perpage);
			int inline = 1;
			String navigation = "";
			
			for (int i = 1; i <= count; i++)
			{
				if (i == page)
				{
					navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-cloak " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-cloak " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				
				if (inline == 7)
				{
					navigation += "</tr><tr>";
					inline = 0;
				}
				inline++;
			}
			
			if (navigation.equals(""))
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}
			
			html = html.replace("{list}", list);
			html = html.replace("{navigation}", navigation);
			
			sendHtml(player, html);
			return true;
		}
		else if (command.equals("dressme-shield"))
		{
			String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_shield_path);
			String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_shield_path);
			String block = "";
			String list = "";
			
			if (args == null)
			{
				args = "1";
			}
			
			String[] param = args.split(" ");
			
			final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
			final int perpage = 5;
			int counter = 0;
			
			for (int i = (page - 1) * perpage; i < DressMeShieldHolder.getInstance().size(); i++)
			{
				DressMeShieldData shield = DressMeShieldHolder.getInstance().getShield(i + 1);
				if (shield != null)
				{
					block = template;
					
					String shield_name = shield.getName();
					
					if (shield_name.length() > 29)
					{
						shield_name = shield_name.substring(0, 29) + "...";
					}
					
					block = block.replace("{bypass}", "bypass -h voice .dress-shieldpage " + (i + 1));
					block = block.replace("{name}", shield_name);
					block = block.replace("{price}", Util.formatPay(player, shield.getPriceCount(), shield.getPriceId()));
					block = block.replace("{icon}", Util.getItemIcon(shield.getShieldId()));
					list += block;
				}
				
				counter++;
				
				if (counter >= perpage)
				{
					break;
				}
			}
			
			double count = Math.ceil((double) DressMeShieldHolder.getInstance().size() / perpage);
			int inline = 1;
			String navigation = "";
			
			for (int i = 1; i <= count; i++)
			{
				if (i == page)
				{
					navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-shield " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-shield " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				
				if (inline == 7)
				{
					navigation += "</tr><tr>";
					inline = 0;
				}
				inline++;
			}
			
			if (navigation.equals(""))
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}
			
			html = html.replace("{list}", list);
			html = html.replace("{navigation}", navigation);
			
			sendHtml(player, html);
			return true;
		}
		else if (command.equals("dressme-weapon"))
		{
			L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			if (slot == null)
			{
				player.sendMessage("Error: Weapon must be equiped!");
				return false;
			}
			
			String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_weapon_path);
			String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_weapon_path);
			String block = "";
			String list = "";
			
			if (args == null)
			{
				args = "1";
			}
			
			String[] param = args.split(" ");
			
			final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
			final int perpage = 5;
			int counter = 0;
			
			ItemType type = slot.getItemType();
			Map<Integer, DressMeWeaponData> map = DressMeHandler.initWeaponMap(type.toString(), new HashMap<>(), slot);
			if (map == null)
			{
				_log.error("Dress me system: Weapon Map is null.");
				return false;
			}
			
			for (int i = (page - 1) * perpage; i < map.size(); i++)
			{
				DressMeWeaponData weapon = map.get(i + 1);
				if (weapon != null)
				{
					block = template;
					
					String cloak_name = weapon.getName();
					
					if (cloak_name.length() > 29)
					{
						cloak_name = cloak_name.substring(0, 29) + "...";
					}
					
					block = block.replace("{bypass}", "bypass -h voice .dress-weaponpage " + weapon.getId());
					block = block.replace("{name}", cloak_name);
					block = block.replace("{price}", Util.formatPay(player, weapon.getPriceCount(), weapon.getPriceId()));
					block = block.replace("{icon}", Util.getItemIcon(weapon.getId()));
					list += block;
				}
				
				counter++;
				
				if (counter >= perpage)
				{
					break;
				}
			}
			
			double count = Math.ceil((double) map.size() / perpage);
			int inline = 1;
			String navigation = "";
			
			for (int i = 1; i <= count; i++)
			{
				if (i == page)
				{
					navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-weapon " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-weapon " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				
				if (inline == 7)
				{
					navigation += "</tr><tr>";
					inline = 0;
				}
				inline++;
			}
			
			if (navigation.equals(""))
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}
			
			html = html.replace("{list}", list);
			html = html.replace("{navigation}", navigation);
			
			sendHtml(player, html);
			return true;
		}
		else if (command.equals("dressme-hat"))
		{
			L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR);
			if (slot == null)
			{
				slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR2);
			}
			
			if (slot == null)
			{
				player.sendMessage("Error: Hat must be equiped!");
				return false;
			}
			
			String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_hat_path);
			String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_hat_path);
			String block = "";
			String list = "";
			
			if (args == null)
			{
				args = "1";
			}
			
			String[] param = args.split(" ");
			
			final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
			final int perpage = 5;
			int counter = 0;
			
			Map<Integer, DressMeHatData> map = DressMeHandler.initHatMap(new HashMap<>(), slot);
			if (map == null)
			{
				_log.error("Dress me system: Hat Map is null.");
				return false;
			}
			
			for (int i = (page - 1) * perpage; i < map.size(); i++)
			{
				DressMeHatData hat = map.get(i + 1);
				if (hat != null)
				{
					block = template;
					
					String hat_name = hat.getName();
					
					if (hat_name.length() > 29)
					{
						hat_name = hat_name.substring(0, 29) + "...";
					}
					
					block = block.replace("{bypass}", "bypass -h voice .dress-hatpage " + hat.getId());
					block = block.replace("{name}", hat_name);
					block = block.replace("{price}", Util.formatPay(player, hat.getPriceCount(), hat.getPriceId()));
					block = block.replace("{icon}", Util.getItemIcon(hat.getHatId()));
					list += block;
				}
				
				counter++;
				
				if (counter >= perpage)
				{
					break;
				}
			}
			
			double count = Math.ceil((double) map.size() / perpage);
			int inline = 1;
			String navigation = "";
			
			for (int i = 1; i <= count; i++)
			{
				if (i == page)
				{
					navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-hat " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-hat " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				
				if (inline == 7)
				{
					navigation += "</tr><tr>";
					inline = 0;
				}
				inline++;
			}
			
			if (navigation.equals(""))
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}
			
			html = html.replace("{list}", list);
			html = html.replace("{navigation}", navigation);
			
			sendHtml(player, html);
			return true;
		}
		else if (command.equals("dress-armorpage"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			DressMeArmorData dress = DressMeArmorHolder.getInstance().getArmor(set);
			if (dress != null)
			{
				String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_armor_path);
				
				Inventory inv = player.getInventory();
				
				L2ItemInstance my_chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
				html = html.replace("{my_chest_icon}", my_chest == null ? "icon.NOIMAGE" : my_chest.getItem().getIcon());
				L2ItemInstance my_legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
				html = html.replace("{my_legs_icon}", my_legs == null ? "icon.NOIMAGE" : my_legs.getItem().getIcon());
				L2ItemInstance my_gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
				html = html.replace("{my_gloves_icon}", my_gloves == null ? "icon.NOIMAGE" : my_gloves.getItem().getIcon());
				L2ItemInstance my_feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);
				html = html.replace("{my_feet_icon}", my_feet == null ? "icon.NOIMAGE" : my_feet.getItem().getIcon());
				
				html = html.replace("{bypass}", "bypass -h voice .dress-armor " + set);
				html = html.replace("{name}", dress.getName());
				html = html.replace("{price}", Util.formatPay(player, dress.getPriceCount(), dress.getPriceId()));
				
				L2Item chest = ItemData.getInstance().getTemplate(dress.getChest());
				html = html.replace("{chest_icon}", chest.getIcon());
				html = html.replace("{chest_name}", chest.getName());
				html = html.replace("{chest_grade}", chest.getItemGrade().name());
				
				if (dress.getLegs() != -1)
				{
					L2Item legs = ItemData.getInstance().getTemplate(dress.getLegs());
					html = html.replace("{legs_icon}", legs.getIcon());
					html = html.replace("{legs_name}", legs.getName());
					html = html.replace("{legs_grade}", legs.getItemGrade().name());
				}
				else
				{
					html = html.replace("{legs_icon}", "icon.NOIMAGE");
					html = html.replace("{legs_name}", "<font color=FF0000>...</font>");
					html = html.replace("{legs_grade}", "NO");
				}
				
				L2Item gloves = ItemData.getInstance().getTemplate(dress.getGloves());
				html = html.replace("{gloves_icon}", gloves.getIcon());
				html = html.replace("{gloves_name}", gloves.getName());
				html = html.replace("{gloves_grade}", gloves.getItemGrade().name());
				
				L2Item feet = ItemData.getInstance().getTemplate(dress.getFeet());
				html = html.replace("{feet_icon}", feet.getIcon());
				html = html.replace("{feet_name}", feet.getName());
				html = html.replace("{feet_grade}", feet.getItemGrade().name());
				
				sendHtml(player, html);
				return true;
			}
			return false;
		}
		else if (command.equals("dress-cloakpage"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			DressMeCloakData cloak = DressMeCloakHolder.getInstance().getCloak(set);
			if (cloak != null)
			{
				String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_cloak_path);
				
				Inventory inv = player.getInventory();
				L2ItemInstance my_cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_CLOAK);
				html = html.replace("{my_cloak_icon}", my_cloak == null ? "icon.NOIMAGE" : my_cloak.getItem().getIcon());
				
				html = html.replace("{bypass}", "bypass -h voice .dress-cloak " + cloak.getId());
				html = html.replace("{name}", cloak.getName());
				html = html.replace("{price}", Util.formatPay(player, cloak.getPriceCount(), cloak.getPriceId()));
				
				L2Item item = ItemData.getInstance().getTemplate(cloak.getCloakId());
				html = html.replace("{item_icon}", item.getIcon());
				html = html.replace("{item_name}", item.getName());
				html = html.replace("{item_grade}", item.getItemGrade().name());
				
				sendHtml(player, html);
				return true;
			}
			return false;
		}
		else if (command.equals("dress-shieldpage"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			DressMeShieldData shield = DressMeShieldHolder.getInstance().getShield(set);
			if (shield != null)
			{
				String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_shield_path);
				
				Inventory inv = player.getInventory();
				L2ItemInstance my_shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
				html = html.replace("{my_shield_icon}", my_shield == null ? "icon.NOIMAGE" : my_shield.getItem().getIcon());
				
				html = html.replace("{bypass}", "bypass -h voice .dress-shield " + shield.getId());
				html = html.replace("{name}", shield.getName());
				html = html.replace("{price}", Util.formatPay(player, shield.getPriceCount(), shield.getPriceId()));
				
				L2Item item = ItemData.getInstance().getTemplate(shield.getShieldId());
				html = html.replace("{item_icon}", item.getIcon());
				html = html.replace("{item_name}", item.getName());
				html = html.replace("{item_grade}", item.getItemGrade().name());
				
				sendHtml(player, html);
				return true;
			}
			return false;
		}
		else if (command.equals("dress-weaponpage"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			DressMeWeaponData weapon = DressMeWeaponHolder.getInstance().getWeapon(set);
			if (weapon != null)
			{
				String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_weapon_path);
				
				Inventory inv = player.getInventory();
				L2ItemInstance my_weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);
				html = html.replace("{my_weapon_icon}", my_weapon == null ? "icon.NOIMAGE" : my_weapon.getItem().getIcon());
				
				html = html.replace("{bypass}", "bypass -h voice .dress-weapon " + weapon.getId());
				html = html.replace("{name}", weapon.getName());
				html = html.replace("{price}", Util.formatPay(player, weapon.getPriceCount(), weapon.getPriceId()));
				
				L2Item item = ItemData.getInstance().getTemplate(weapon.getId());
				html = html.replace("{item_icon}", item.getIcon());
				html = html.replace("{item_name}", item.getName());
				html = html.replace("{item_grade}", item.getItemGrade().name());
				
				sendHtml(player, html);
				return true;
			}
			return false;
		}
		else if (command.equals("dress-hatpage"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			DressMeHatData hat = DressMeHatHolder.getInstance().getHat(set);
			if (hat != null)
			{
				String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_hat_path);
				
				Inventory inv = player.getInventory();
				
				L2ItemInstance my_hat = null;
				switch (hat.getSlot())
				{
					case 1: // HAIR
					case 3: // FULL HAIR
						my_hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR);
						break;
					case 2: // HAIR2
						my_hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR2);
						break;
				}
				
				html = html.replace("{my_hat_icon}", my_hat == null ? "icon.NOIMAGE" : my_hat.getItem().getIcon());
				
				html = html.replace("{bypass}", "bypass -h voice .dress-hat " + hat.getId());
				html = html.replace("{name}", hat.getName());
				html = html.replace("{price}", Util.formatPay(player, hat.getPriceCount(), hat.getPriceId()));
				
				L2Item item = ItemData.getInstance().getTemplate(hat.getHatId());
				html = html.replace("{item_icon}", item.getIcon());
				html = html.replace("{item_name}", item.getName());
				html = html.replace("{item_grade}", item.getItemGrade().name());
				
				sendHtml(player, html);
				return true;
			}
			return false;
		}
		else if (command.equals("dressinfo"))
		{
			String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), info_path);
			sendHtml(player, html);
			return true;
		}
		else if (command.equals("dress-armor"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			
			DressMeArmorData dress = DressMeArmorHolder.getInstance().getArmor(set);
			Inventory inv = player.getInventory();
			
			L2ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			L2ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
			L2ItemInstance gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
			L2ItemInstance feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);
			
			if (chest == null)
			{
				player.sendMessage("Error: Chest must be equiped.");
				useVoicedCommand("dress-armorpage", player, args);
				return false;
			}
			
			if (chest.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR)
			{
				L2Item visual = ItemData.getInstance().getTemplate(dress.getChest());
				if (chest.getItem().getBodyPart() != visual.getBodyPart())
				{
					player.sendMessage("Error: You can't change visual in full armor type not full armors.");
					useVoicedCommand("dress-armorpage", player, args);
					return false;
				}
			}
			
			// Checks for armor set for the equipped chest.
			if (!ArmorSetsData.getInstance().isArmorSet(chest.getId()))
			{
				player.sendMessage("Error: You can't visualize current set.");
				useVoicedCommand("dress-armorpage", player, args);
				return false;
			}
			
			L2ArmorSet armoSet = ArmorSetsData.getInstance().getSet(chest.getId());
			if ((armoSet == null) || !armoSet.containAll(player))
			{
				player.sendMessage("Error: You can't visualize, set is not complete.");
				useVoicedCommand("dress-armorpage", player, args);
				return false;
			}
			
			if (!chest.getArmorItem().getItemType().getDescription().equals(dress.getType()))
			{
				player.sendMessage("Error: You can't visualize current set.");
				useVoicedCommand("dress-armorpage", player, args);
				return false;
			}
			
			if (Conditions.checkPlayerItemCount(player, dress.getPriceId(), dress.getPriceCount()))
			{
				player.destroyItemByItemId("VisualChange", dress.getPriceId(), dress.getPriceCount(), player, true);
				DressMeHandler.visuality(player, chest, dress.getChest());
				
				if (dress.getLegs() != -1)
				{
					DressMeHandler.visuality(player, legs, dress.getLegs());
				}
				else if ((dress.getLegs() == -1) && (chest.getItem().getBodyPart() != L2Item.SLOT_FULL_ARMOR))
				{
					DressMeHandler.visuality(player, legs, dress.getChest());
				}
				
				DressMeHandler.visuality(player, gloves, dress.getGloves());
				DressMeHandler.visuality(player, feet, dress.getFeet());
				player.broadcastUserInfo();
			}
			useVoicedCommand("dressme", player, null);
			return true;
		}
		else if (command.equals("dress-cloak"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			
			DressMeCloakData cloak_data = DressMeCloakHolder.getInstance().getCloak(set);
			Inventory inv = player.getInventory();
			
			L2ItemInstance cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_CLOAK);
			
			if (cloak == null)
			{
				player.sendMessage("Error: Cloak must be equiped.");
				useVoicedCommand("dress-cloakpage", player, args);
				return false;
			}
			
			if (Conditions.checkPlayerItemCount(player, cloak_data.getPriceId(), cloak_data.getPriceCount()))
			{
				player.destroyItemByItemId("VisualChange", cloak_data.getPriceId(), cloak_data.getPriceCount(), player, true);
				DressMeHandler.visuality(player, cloak, cloak_data.getCloakId());
			}
			player.broadcastUserInfo();
			useVoicedCommand("dressme", player, null);
			return true;
		}
		else if (command.equals("dress-shield"))
		{
			final int shield_id = Integer.parseInt(args.split(" ")[0]);
			
			DressMeShieldData shield_data = DressMeShieldHolder.getInstance().getShield(shield_id);
			Inventory inv = player.getInventory();
			
			L2ItemInstance shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			
			if (shield == null)
			{
				player.sendMessage("Error: Shield must be equiped.");
				useVoicedCommand("dress-shieldpage", player, args);
				return false;
			}
			
			if (Conditions.checkPlayerItemCount(player, shield_data.getPriceId(), shield_data.getPriceCount()))
			{
				player.destroyItemByItemId("VisualChange", shield_data.getPriceId(), shield_data.getPriceCount(), player, true);
				DressMeHandler.visuality(player, shield, shield_data.getShieldId());
			}
			player.broadcastUserInfo();
			useVoicedCommand("dressme", player, null);
			return true;
		}
		else if (command.equals("dress-weapon"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			
			DressMeWeaponData weapon_data = DressMeWeaponHolder.getInstance().getWeapon(set);
			Inventory inv = player.getInventory();
			
			L2ItemInstance weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			
			if (weapon == null)
			{
				player.sendMessage("Error: Weapon must be equiped.");
				useVoicedCommand("dress-weaponpage", player, args);
				return false;
			}
			
			if (!weapon.getItemType().toString().equals(weapon_data.getType()))
			{
				player.sendMessage("Error: Weapon must be equals type.");
				useVoicedCommand("dressme-weapon", player, null);
				return false;
			}
			
			if (Conditions.checkPlayerItemCount(player, weapon_data.getPriceId(), weapon_data.getPriceCount()))
			{
				player.destroyItemByItemId("VisualChange", weapon_data.getPriceId(), weapon_data.getPriceCount(), player, true);
				DressMeHandler.visuality(player, weapon, weapon_data.getId());
			}
			player.broadcastUserInfo();
			useVoicedCommand("dressme", player, null);
			return true;
		}
		else if (command.equals("dress-hat"))
		{
			final int set = Integer.parseInt(args.split(" ")[0]);
			
			DressMeHatData hat_data = DressMeHatHolder.getInstance().getHat(set);
			Inventory inv = player.getInventory();
			
			L2ItemInstance hat = null;
			switch (hat_data.getSlot())
			{
				case 1: // HAIR
				case 3: // FULL HAIR
					hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR);
					break;
				case 2: // HAIR2
					hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR2);
					break;
			}
			
			if (hat == null)
			{
				player.sendMessage("Error: Hat must be equiped.");
				useVoicedCommand("dress-hatpage", player, args);
				return false;
			}
			
			L2Item visual = ItemData.getInstance().getTemplate(hat_data.getHatId());
			if (hat.getItem().getBodyPart() != visual.getBodyPart())
			{
				player.sendMessage("Error: You can't change visual on different hat types!");
				useVoicedCommand("dress-hatpage", player, args);
				return false;
			}
			
			if (Conditions.checkPlayerItemCount(player, hat_data.getPriceId(), hat_data.getPriceCount()))
			{
				player.destroyItemByItemId("VisualChange", hat_data.getPriceId(), hat_data.getPriceCount(), player, true);
				DressMeHandler.visuality(player, hat, hat_data.getHatId());
			}
			player.broadcastUserInfo();
			useVoicedCommand("dressme", player, null);
			return true;
		}
		else if (command.equals("undressme"))
		{
			String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), undressme_path);
			html = html.replace("<?show_hide?>", !player.getVarB("showVisualChange") ? "Show visual equip on other player!" : "Hide visual equip on other player!");
			html = html.replace("<?show_hide_b?>", !player.getVarB("showVisualChange") ? "showdress" : "hidedress");
			
			sendHtml(player, html);
			return true;
		}
		else if (command.equals("undressme-armor"))
		{
			Inventory inv = player.getInventory();
			L2ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
			L2ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
			L2ItemInstance gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
			L2ItemInstance feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);
			
			if (chest != null)
			{
				DressMeHandler.visuality(player, chest, 0);
			}
			if (legs != null)
			{
				DressMeHandler.visuality(player, legs, 0);
			}
			if (gloves != null)
			{
				DressMeHandler.visuality(player, gloves, 0);
			}
			if (feet != null)
			{
				DressMeHandler.visuality(player, feet, 0);
			}
			
			player.broadcastUserInfo();
			useVoicedCommand("undressme", player, null);
			return true;
		}
		else if (command.equals("undressme-cloak"))
		{
			L2ItemInstance cloak = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CLOAK);
			if (cloak != null)
			{
				DressMeHandler.visuality(player, cloak, 0);
			}
			player.broadcastUserInfo();
			useVoicedCommand("undressme", player, null);
			return true;
		}
		else if (command.equals("undressme-shield"))
		{
			L2ItemInstance shield = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			if (shield != null)
			{
				DressMeHandler.visuality(player, shield, 0);
			}
			player.broadcastUserInfo();
			useVoicedCommand("undressme", player, null);
			return true;
		}
		else if (command.equals("undressme-weapon"))
		{
			L2ItemInstance weapon = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			if (weapon != null)
			{
				DressMeHandler.visuality(player, weapon, 0);
			}
			player.broadcastUserInfo();
			useVoicedCommand("undressme", player, null);
			return true;
		}
		else if (command.equals("undressme-hat"))
		{
			L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR);
			if (slot == null)
			{
				slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR2);
			}
			
			if (slot != null)
			{
				DressMeHandler.visuality(player, slot, 0);
			}
			player.broadcastUserInfo();
			useVoicedCommand("undressme", player, null);
			return true;
		}
		else if (command.equals("showdress"))
		{
			player.setVar("showVisualChange", "true");
			player.broadcastUserInfo();
			useVoicedCommand("dressme", player, null);
			return true;
		}
		else if (command.equals("hidedress"))
		{
			player.setVar("showVisualChange", "false");
			player.broadcastUserInfo();
			useVoicedCommand("dressme", player, null);
			return true;
		}
		
		return false;
	}
	
	private void sendHtml(L2PcInstance player, String html)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage();
		msg.setHtml(html);
		player.sendPacket(msg);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}
