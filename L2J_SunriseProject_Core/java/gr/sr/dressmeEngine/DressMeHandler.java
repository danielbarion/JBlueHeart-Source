package gr.sr.dressmeEngine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import l2r.Config;
import l2r.L2DatabaseFactory;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;

import gr.sr.dressmeEngine.data.DressMeArmorData;
import gr.sr.dressmeEngine.data.DressMeHatData;
import gr.sr.dressmeEngine.data.DressMeWeaponData;
import gr.sr.dressmeEngine.util.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DressMeHandler
{
	private static final Logger _log = LoggerFactory.getLogger(DressMeHandler.class);
	
	public static void visuality(L2PcInstance player, L2ItemInstance item, int visual)
	{
		item.setVisualItemId(visual);
		updateVisualInDb(item, visual);
		
		if (visual > 0)
		{
			player.sendMessage(item.getName() + " visual change to " + Util.getItemName(visual));
		}
		else
		{
			player.sendMessage("Visual removed from " + item.getName() + ".");
		}
		
		player.broadcastUserInfo();
	}
	
	public static void updateVisualInDb(L2ItemInstance item, int visual)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE items SET visual_item_id=? " + "WHERE object_id = ?"))
		{
			ps.setInt(1, visual);
			ps.setInt(2, item.getObjectId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			if (Config.DEBUG)
			{
				_log.error("Could not update dress me item in DB: Reason: " + e.getMessage(), e);
			}
		}
	}
	
	public static Map<Integer, DressMeWeaponData> initWeaponMap(String type, Map<Integer, DressMeWeaponData> map, L2ItemInstance slot)
	{
		if (type.equals("SWORD") && (slot.getItem().getBodyPart() != L2Item.SLOT_LR_HAND))
		{
			return map = DressMeLoader.SWORD;
		}
		else if (type.equals("BLUNT") && (slot.getItem().getBodyPart() != L2Item.SLOT_LR_HAND))
		{
			return map = DressMeLoader.BLUNT;
		}
		else if (type.equals("SWORD") && (slot.getItem().getBodyPart() == L2Item.SLOT_LR_HAND))
		{
			return map = DressMeLoader.BIGSWORD;
		}
		else if (type.equals("BLUNT") && (slot.getItem().getBodyPart() == L2Item.SLOT_LR_HAND))
		{
			return map = DressMeLoader.BIGBLUNT;
		}
		else if (type.equals("DAGGER"))
		{
			return map = DressMeLoader.DAGGER;
		}
		else if (type.equals("BOW"))
		{
			return map = DressMeLoader.BOW;
		}
		else if (type.equals("POLE"))
		{
			return map = DressMeLoader.POLE;
		}
		else if (type.equals("FIST"))
		{
			return map = DressMeLoader.FIST;
		}
		else if (type.equals("DUAL"))
		{
			return map = DressMeLoader.DUAL;
		}
		else if (type.equals("DUALFIST"))
		{
			return map = DressMeLoader.DUALFIST;
		}
		else if (type.equals("FISHINGROD"))
		{
			return map = DressMeLoader.ROD;
		}
		else if (type.equals("CROSSBOW"))
		{
			return map = DressMeLoader.CROSSBOW;
		}
		else if (type.equals("RAPIER"))
		{
			return map = DressMeLoader.RAPIER;
		}
		else if (type.equals("ANCIENTSWORD"))
		{
			return map = DressMeLoader.ANCIENTSWORD;
		}
		else if (type.equals("DUALDAGGER"))
		{
			return map = DressMeLoader.DUALDAGGER;
		}
		else
		{
			_log.error("Dress me system: Unknown weapon type: " + type);
			return null;
		}
	}
	
	public static Map<Integer, DressMeArmorData> initArmorMap(String type, Map<Integer, DressMeArmorData> map, L2ItemInstance slot)
	{
		if (type.equals("LIGHT"))
		{
			return map = DressMeLoader.LIGHT;
		}
		else if (type.equals("HEAVY"))
		{
			return map = DressMeLoader.HEAVY;
		}
		else if (type.equals("ROBE"))
		{
			return map = DressMeLoader.ROBE;
		}
		else
		{
			_log.error("Dress me system: Unknown armor type: " + type);
			return null;
		}
	}
	
	public static Map<Integer, DressMeHatData> initHatMap(Map<Integer, DressMeHatData> map, L2ItemInstance slot)
	{
		if ((slot.getLocationSlot() == 2) && (slot.getItem().getBodyPart() != L2Item.SLOT_HAIRALL))
		{
			return map = DressMeLoader.HAIR;
		}
		else if ((slot.getLocationSlot() == 2) && (slot.getItem().getBodyPart() == L2Item.SLOT_HAIRALL))
		{
			return map = DressMeLoader.HAIR_FULL;
		}
		else if (slot.getLocationSlot() == 3)
		{
			return map = DressMeLoader.HAIR2;
		}
		else
		{
			_log.error("Dress me system: Unknown hat slot: " + slot.getLocationSlot());
			return null;
		}
	}
}
